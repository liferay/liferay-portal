/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.rest.oauth2.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.sharepoint.rest.oauth2.exception.NoSuch2TokenEntryException;
import com.liferay.sharepoint.rest.oauth2.model.SharepointOAuth2TokenEntry;
import com.liferay.sharepoint.rest.oauth2.model.SharepointOAuth2TokenEntryTable;
import com.liferay.sharepoint.rest.oauth2.model.impl.SharepointOAuth2TokenEntryImpl;
import com.liferay.sharepoint.rest.oauth2.model.impl.SharepointOAuth2TokenEntryModelImpl;
import com.liferay.sharepoint.rest.oauth2.service.persistence.SharepointOAuth2TokenEntryPersistence;
import com.liferay.sharepoint.rest.oauth2.service.persistence.SharepointOAuth2TokenEntryUtil;
import com.liferay.sharepoint.rest.oauth2.service.persistence.impl.constants.SharepointOAuthPersistenceConstants;

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
 * The persistence implementation for the sharepoint o auth2 token entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Adolfo Pérez
 * @generated
 */
@Component(service = SharepointOAuth2TokenEntryPersistence.class)
public class SharepointOAuth2TokenEntryPersistenceImpl
	extends BasePersistenceImpl
		<SharepointOAuth2TokenEntry, NoSuch2TokenEntryException>
	implements SharepointOAuth2TokenEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SharepointOAuth2TokenEntryUtil</code> to access the sharepoint o auth2 token entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SharepointOAuth2TokenEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<SharepointOAuth2TokenEntry, NoSuch2TokenEntryException>
			_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the sharepoint o auth2 token entries where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SharepointOAuth2TokenEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of sharepoint o auth2 token entries
	 * @param end the upper bound of the range of sharepoint o auth2 token entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching sharepoint o auth2 token entries
	 */
	@Override
	public List<SharepointOAuth2TokenEntry> findByUserId(
		long userId, int start, int end,
		OrderByComparator<SharepointOAuth2TokenEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			finderCache, new Object[] {userId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first sharepoint o auth2 token entry in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sharepoint o auth2 token entry
	 * @throws NoSuch2TokenEntryException if a matching sharepoint o auth2 token entry could not be found
	 */
	@Override
	public SharepointOAuth2TokenEntry findByUserId_First(
			long userId,
			OrderByComparator<SharepointOAuth2TokenEntry> orderByComparator)
		throws NoSuch2TokenEntryException {

		return _collectionPersistenceFinderByUserId.findFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Returns the first sharepoint o auth2 token entry in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching sharepoint o auth2 token entry, or <code>null</code> if a matching sharepoint o auth2 token entry could not be found
	 */
	@Override
	public SharepointOAuth2TokenEntry fetchByUserId_First(
		long userId,
		OrderByComparator<SharepointOAuth2TokenEntry> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Removes all the sharepoint o auth2 token entries where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			finderCache, new Object[] {userId});
	}

	/**
	 * Returns the number of sharepoint o auth2 token entries where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching sharepoint o auth2 token entries
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			finderCache, new Object[] {userId});
	}

	private UniquePersistenceFinder
		<SharepointOAuth2TokenEntry, NoSuch2TokenEntryException>
			_uniquePersistenceFinderByU_C;

	/**
	 * Returns the sharepoint o auth2 token entry where userId = &#63; and configurationPid = &#63; or throws a <code>NoSuch2TokenEntryException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param configurationPid the configuration pid
	 * @return the matching sharepoint o auth2 token entry
	 * @throws NoSuch2TokenEntryException if a matching sharepoint o auth2 token entry could not be found
	 */
	@Override
	public SharepointOAuth2TokenEntry findByU_C(
			long userId, String configurationPid)
		throws NoSuch2TokenEntryException {

		return _uniquePersistenceFinderByU_C.find(
			finderCache, new Object[] {userId, configurationPid});
	}

	/**
	 * Returns the sharepoint o auth2 token entry where userId = &#63; and configurationPid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param configurationPid the configuration pid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching sharepoint o auth2 token entry, or <code>null</code> if a matching sharepoint o auth2 token entry could not be found
	 */
	@Override
	public SharepointOAuth2TokenEntry fetchByU_C(
		long userId, String configurationPid, boolean useFinderCache) {

		return _uniquePersistenceFinderByU_C.fetch(
			finderCache, new Object[] {userId, configurationPid},
			useFinderCache);
	}

	/**
	 * Removes the sharepoint o auth2 token entry where userId = &#63; and configurationPid = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param configurationPid the configuration pid
	 * @return the sharepoint o auth2 token entry that was removed
	 */
	@Override
	public SharepointOAuth2TokenEntry removeByU_C(
			long userId, String configurationPid)
		throws NoSuch2TokenEntryException {

		SharepointOAuth2TokenEntry sharepointOAuth2TokenEntry = findByU_C(
			userId, configurationPid);

		return remove(sharepointOAuth2TokenEntry);
	}

	/**
	 * Returns the number of sharepoint o auth2 token entries where userId = &#63; and configurationPid = &#63;.
	 *
	 * @param userId the user ID
	 * @param configurationPid the configuration pid
	 * @return the number of matching sharepoint o auth2 token entries
	 */
	@Override
	public int countByU_C(long userId, String configurationPid) {
		return _uniquePersistenceFinderByU_C.count(
			finderCache, new Object[] {userId, configurationPid});
	}

	public SharepointOAuth2TokenEntryPersistenceImpl() {
		setModelClass(SharepointOAuth2TokenEntry.class);

		setModelImplClass(SharepointOAuth2TokenEntryImpl.class);
		setModelPKClass(long.class);

		setTable(SharepointOAuth2TokenEntryTable.INSTANCE);
	}

	/**
	 * Creates a new sharepoint o auth2 token entry with the primary key. Does not add the sharepoint o auth2 token entry to the database.
	 *
	 * @param sharepointOAuth2TokenEntryId the primary key for the new sharepoint o auth2 token entry
	 * @return the new sharepoint o auth2 token entry
	 */
	@Override
	public SharepointOAuth2TokenEntry create(
		long sharepointOAuth2TokenEntryId) {

		SharepointOAuth2TokenEntry sharepointOAuth2TokenEntry =
			new SharepointOAuth2TokenEntryImpl();

		sharepointOAuth2TokenEntry.setNew(true);
		sharepointOAuth2TokenEntry.setPrimaryKey(sharepointOAuth2TokenEntryId);

		sharepointOAuth2TokenEntry.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return sharepointOAuth2TokenEntry;
	}

	/**
	 * Removes the sharepoint o auth2 token entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param sharepointOAuth2TokenEntryId the primary key of the sharepoint o auth2 token entry
	 * @return the sharepoint o auth2 token entry that was removed
	 * @throws NoSuch2TokenEntryException if a sharepoint o auth2 token entry with the primary key could not be found
	 */
	@Override
	public SharepointOAuth2TokenEntry remove(long sharepointOAuth2TokenEntryId)
		throws NoSuch2TokenEntryException {

		return remove((Serializable)sharepointOAuth2TokenEntryId);
	}

	@Override
	protected SharepointOAuth2TokenEntry removeImpl(
		SharepointOAuth2TokenEntry sharepointOAuth2TokenEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(sharepointOAuth2TokenEntry)) {
				sharepointOAuth2TokenEntry =
					(SharepointOAuth2TokenEntry)session.get(
						SharepointOAuth2TokenEntryImpl.class,
						sharepointOAuth2TokenEntry.getPrimaryKeyObj());
			}

			if (sharepointOAuth2TokenEntry != null) {
				session.delete(sharepointOAuth2TokenEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (sharepointOAuth2TokenEntry != null) {
			clearCache(sharepointOAuth2TokenEntry);
		}

		return sharepointOAuth2TokenEntry;
	}

	@Override
	public SharepointOAuth2TokenEntry updateImpl(
		SharepointOAuth2TokenEntry sharepointOAuth2TokenEntry) {

		boolean isNew = sharepointOAuth2TokenEntry.isNew();

		if (!(sharepointOAuth2TokenEntry instanceof
				SharepointOAuth2TokenEntryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(sharepointOAuth2TokenEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					sharepointOAuth2TokenEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in sharepointOAuth2TokenEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SharepointOAuth2TokenEntry implementation " +
					sharepointOAuth2TokenEntry.getClass());
		}

		SharepointOAuth2TokenEntryModelImpl
			sharepointOAuth2TokenEntryModelImpl =
				(SharepointOAuth2TokenEntryModelImpl)sharepointOAuth2TokenEntry;

		if (isNew && (sharepointOAuth2TokenEntry.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				sharepointOAuth2TokenEntry.setCreateDate(date);
			}
			else {
				sharepointOAuth2TokenEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(sharepointOAuth2TokenEntry);
			}
			else {
				sharepointOAuth2TokenEntry =
					(SharepointOAuth2TokenEntry)session.merge(
						sharepointOAuth2TokenEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(sharepointOAuth2TokenEntry, false);

		if (isNew) {
			sharepointOAuth2TokenEntry.setNew(false);
		}

		sharepointOAuth2TokenEntry.resetOriginalValues();

		return sharepointOAuth2TokenEntry;
	}

	/**
	 * Returns the sharepoint o auth2 token entry with the primary key or throws a <code>NoSuch2TokenEntryException</code> if it could not be found.
	 *
	 * @param sharepointOAuth2TokenEntryId the primary key of the sharepoint o auth2 token entry
	 * @return the sharepoint o auth2 token entry
	 * @throws NoSuch2TokenEntryException if a sharepoint o auth2 token entry with the primary key could not be found
	 */
	@Override
	public SharepointOAuth2TokenEntry findByPrimaryKey(
			long sharepointOAuth2TokenEntryId)
		throws NoSuch2TokenEntryException {

		return findByPrimaryKey((Serializable)sharepointOAuth2TokenEntryId);
	}

	/**
	 * Returns the sharepoint o auth2 token entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param sharepointOAuth2TokenEntryId the primary key of the sharepoint o auth2 token entry
	 * @return the sharepoint o auth2 token entry, or <code>null</code> if a sharepoint o auth2 token entry with the primary key could not be found
	 */
	@Override
	public SharepointOAuth2TokenEntry fetchByPrimaryKey(
		long sharepointOAuth2TokenEntryId) {

		return fetchByPrimaryKey((Serializable)sharepointOAuth2TokenEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "sharepointOAuth2TokenEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SHAREPOINTOAUTH2TOKENENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return SharepointOAuth2TokenEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the sharepoint o auth2 token entry persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByUserId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUserId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUserId",
					new String[] {Long.class.getName()},
					new String[] {"userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUserId",
					new String[] {Long.class.getName()},
					new String[] {"userId"}, false),
				_SQL_SELECT_SHAREPOINTOAUTH2TOKENENTRY_WHERE,
				_SQL_COUNT_SHAREPOINTOAUTH2TOKENENTRY_WHERE,
				SharepointOAuth2TokenEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"sharepointOAuth2TokenEntry.", "userId",
					FinderColumn.Type.LONG, "=", true, true,
					SharepointOAuth2TokenEntry::getUserId));

		_uniquePersistenceFinderByU_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByU_C",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"userId", "configurationPid"}, 0, 2, false,
				SharepointOAuth2TokenEntry::getUserId,
				convertNullFunction(
					SharepointOAuth2TokenEntry::getConfigurationPid)),
			_SQL_SELECT_SHAREPOINTOAUTH2TOKENENTRY_WHERE, "",
			new FinderColumn<>(
				"sharepointOAuth2TokenEntry.", "userId", FinderColumn.Type.LONG,
				"=", true, true, SharepointOAuth2TokenEntry::getUserId),
			new FinderColumn<>(
				"sharepointOAuth2TokenEntry.", "configurationPid",
				FinderColumn.Type.STRING, "=", true, true,
				SharepointOAuth2TokenEntry::getConfigurationPid));

		SharepointOAuth2TokenEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		SharepointOAuth2TokenEntryUtil.setPersistence(null);

		entityCache.removeCache(SharepointOAuth2TokenEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = SharepointOAuthPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = SharepointOAuthPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = SharepointOAuthPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		SharepointOAuth2TokenEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SHAREPOINTOAUTH2TOKENENTRY =
		"SELECT sharepointOAuth2TokenEntry FROM SharepointOAuth2TokenEntry sharepointOAuth2TokenEntry";

	private static final String _SQL_SELECT_SHAREPOINTOAUTH2TOKENENTRY_WHERE =
		"SELECT sharepointOAuth2TokenEntry FROM SharepointOAuth2TokenEntry sharepointOAuth2TokenEntry WHERE ";

	private static final String _SQL_COUNT_SHAREPOINTOAUTH2TOKENENTRY_WHERE =
		"SELECT COUNT(sharepointOAuth2TokenEntry) FROM SharepointOAuth2TokenEntry sharepointOAuth2TokenEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No SharepointOAuth2TokenEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		SharepointOAuth2TokenEntryPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1976381295