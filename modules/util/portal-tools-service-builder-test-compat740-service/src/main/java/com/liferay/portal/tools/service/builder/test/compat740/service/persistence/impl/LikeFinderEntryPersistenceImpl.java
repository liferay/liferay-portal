/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.tools.service.builder.test.compat740.exception.NoSuchLikeFinderEntryException;
import com.liferay.portal.tools.service.builder.test.compat740.model.LikeFinderEntry;
import com.liferay.portal.tools.service.builder.test.compat740.model.LikeFinderEntryTable;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.LikeFinderEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.LikeFinderEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.LikeFinderEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.LikeFinderEntryUtil;
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
 * The persistence implementation for the like finder entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = LikeFinderEntryPersistence.class)
public class LikeFinderEntryPersistenceImpl
	extends BasePersistenceImpl<LikeFinderEntry, NoSuchLikeFinderEntryException>
	implements LikeFinderEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LikeFinderEntryUtil</code> to access the like finder entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LikeFinderEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder
		<LikeFinderEntry, NoSuchLikeFinderEntryException>
			_uniquePersistenceFinderByO_O_P;

	/**
	 * Returns the like finder entry where ownerId = &#63; and ownerType = &#63; and portletId = &#63; or throws a <code>NoSuchLikeFinderEntryException</code> if it could not be found.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @return the matching like finder entry
	 * @throws NoSuchLikeFinderEntryException if a matching like finder entry could not be found
	 */
	@Override
	public LikeFinderEntry findByO_O_P(
			long ownerId, int ownerType, String portletId)
		throws NoSuchLikeFinderEntryException {

		return _uniquePersistenceFinderByO_O_P.find(
			finderCache, new Object[] {ownerId, ownerType, portletId});
	}

	/**
	 * Returns the like finder entry where ownerId = &#63; and ownerType = &#63; and portletId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching like finder entry, or <code>null</code> if a matching like finder entry could not be found
	 */
	@Override
	public LikeFinderEntry fetchByO_O_P(
		long ownerId, int ownerType, String portletId, boolean useFinderCache) {

		return _uniquePersistenceFinderByO_O_P.fetch(
			finderCache, new Object[] {ownerId, ownerType, portletId},
			useFinderCache);
	}

	/**
	 * Removes the like finder entry where ownerId = &#63; and ownerType = &#63; and portletId = &#63; from the database.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @return the like finder entry that was removed
	 */
	@Override
	public LikeFinderEntry removeByO_O_P(
			long ownerId, int ownerType, String portletId)
		throws NoSuchLikeFinderEntryException {

		LikeFinderEntry likeFinderEntry = findByO_O_P(
			ownerId, ownerType, portletId);

		return remove(likeFinderEntry);
	}

	/**
	 * Returns the number of like finder entries where ownerId = &#63; and ownerType = &#63; and portletId = &#63;.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @return the number of matching like finder entries
	 */
	@Override
	public int countByO_O_P(long ownerId, int ownerType, String portletId) {
		return _uniquePersistenceFinderByO_O_P.count(
			finderCache, new Object[] {ownerId, ownerType, portletId});
	}

	private CollectionPersistenceFinder
		<LikeFinderEntry, NoSuchLikeFinderEntryException>
			_collectionPersistenceFinderByC_O_O_LikeP;

	/**
	 * Returns all the like finder entries where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @return the matching like finder entries
	 */
	@Override
	public List<LikeFinderEntry> findByC_O_O_LikeP(
		long companyId, long ownerId, int ownerType, String portletId) {

		return findByC_O_O_LikeP(
			companyId, ownerId, ownerType, portletId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the like finder entries where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LikeFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param start the lower bound of the range of like finder entries
	 * @param end the upper bound of the range of like finder entries (not inclusive)
	 * @return the range of matching like finder entries
	 */
	@Override
	public List<LikeFinderEntry> findByC_O_O_LikeP(
		long companyId, long ownerId, int ownerType, String portletId,
		int start, int end) {

		return findByC_O_O_LikeP(
			companyId, ownerId, ownerType, portletId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the like finder entries where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LikeFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param start the lower bound of the range of like finder entries
	 * @param end the upper bound of the range of like finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching like finder entries
	 */
	@Override
	public List<LikeFinderEntry> findByC_O_O_LikeP(
		long companyId, long ownerId, int ownerType, String portletId,
		int start, int end,
		OrderByComparator<LikeFinderEntry> orderByComparator) {

		return findByC_O_O_LikeP(
			companyId, ownerId, ownerType, portletId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the like finder entries where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LikeFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param start the lower bound of the range of like finder entries
	 * @param end the upper bound of the range of like finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching like finder entries
	 */
	@Override
	public List<LikeFinderEntry> findByC_O_O_LikeP(
		long companyId, long ownerId, int ownerType, String portletId,
		int start, int end,
		OrderByComparator<LikeFinderEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_O_O_LikeP.find(
			finderCache,
			new Object[] {companyId, ownerId, ownerType, portletId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first like finder entry in the ordered set where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching like finder entry
	 * @throws NoSuchLikeFinderEntryException if a matching like finder entry could not be found
	 */
	@Override
	public LikeFinderEntry findByC_O_O_LikeP_First(
			long companyId, long ownerId, int ownerType, String portletId,
			OrderByComparator<LikeFinderEntry> orderByComparator)
		throws NoSuchLikeFinderEntryException {

		return _collectionPersistenceFinderByC_O_O_LikeP.findFirst(
			finderCache,
			new Object[] {companyId, ownerId, ownerType, portletId},
			orderByComparator);
	}

	/**
	 * Returns the first like finder entry in the ordered set where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching like finder entry, or <code>null</code> if a matching like finder entry could not be found
	 */
	@Override
	public LikeFinderEntry fetchByC_O_O_LikeP_First(
		long companyId, long ownerId, int ownerType, String portletId,
		OrderByComparator<LikeFinderEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_O_O_LikeP.fetchFirst(
			finderCache,
			new Object[] {companyId, ownerId, ownerType, portletId},
			orderByComparator);
	}

	/**
	 * Removes all the like finder entries where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 */
	@Override
	public void removeByC_O_O_LikeP(
		long companyId, long ownerId, int ownerType, String portletId) {

		_collectionPersistenceFinderByC_O_O_LikeP.remove(
			finderCache,
			new Object[] {companyId, ownerId, ownerType, portletId});
	}

	/**
	 * Returns the number of like finder entries where companyId = &#63; and ownerId = &#63; and ownerType = &#63; and portletId LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param portletId the portlet ID
	 * @return the number of matching like finder entries
	 */
	@Override
	public int countByC_O_O_LikeP(
		long companyId, long ownerId, int ownerType, String portletId) {

		return _collectionPersistenceFinderByC_O_O_LikeP.count(
			finderCache,
			new Object[] {companyId, ownerId, ownerType, portletId});
	}

	public LikeFinderEntryPersistenceImpl() {
		setModelClass(LikeFinderEntry.class);

		setModelImplClass(LikeFinderEntryImpl.class);
		setModelPKClass(long.class);

		setTable(LikeFinderEntryTable.INSTANCE);
	}

	/**
	 * Creates a new like finder entry with the primary key. Does not add the like finder entry to the database.
	 *
	 * @param likeFinderEntryId the primary key for the new like finder entry
	 * @return the new like finder entry
	 */
	@Override
	public LikeFinderEntry create(long likeFinderEntryId) {
		LikeFinderEntry likeFinderEntry = new LikeFinderEntryImpl();

		likeFinderEntry.setNew(true);
		likeFinderEntry.setPrimaryKey(likeFinderEntryId);

		likeFinderEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return likeFinderEntry;
	}

	/**
	 * Removes the like finder entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param likeFinderEntryId the primary key of the like finder entry
	 * @return the like finder entry that was removed
	 * @throws NoSuchLikeFinderEntryException if a like finder entry with the primary key could not be found
	 */
	@Override
	public LikeFinderEntry remove(long likeFinderEntryId)
		throws NoSuchLikeFinderEntryException {

		return remove((Serializable)likeFinderEntryId);
	}

	@Override
	protected LikeFinderEntry removeImpl(LikeFinderEntry likeFinderEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(likeFinderEntry)) {
				likeFinderEntry = (LikeFinderEntry)session.get(
					LikeFinderEntryImpl.class,
					likeFinderEntry.getPrimaryKeyObj());
			}

			if (likeFinderEntry != null) {
				session.delete(likeFinderEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (likeFinderEntry != null) {
			clearCache(likeFinderEntry);
		}

		return likeFinderEntry;
	}

	@Override
	public LikeFinderEntry updateImpl(LikeFinderEntry likeFinderEntry) {
		boolean isNew = likeFinderEntry.isNew();

		if (!(likeFinderEntry instanceof LikeFinderEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(likeFinderEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					likeFinderEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in likeFinderEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LikeFinderEntry implementation " +
					likeFinderEntry.getClass());
		}

		LikeFinderEntryModelImpl likeFinderEntryModelImpl =
			(LikeFinderEntryModelImpl)likeFinderEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(likeFinderEntry);
			}
			else {
				likeFinderEntry = (LikeFinderEntry)session.merge(
					likeFinderEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(likeFinderEntry, false);

		if (isNew) {
			likeFinderEntry.setNew(false);
		}

		likeFinderEntry.resetOriginalValues();

		return likeFinderEntry;
	}

	/**
	 * Returns the like finder entry with the primary key or throws a <code>NoSuchLikeFinderEntryException</code> if it could not be found.
	 *
	 * @param likeFinderEntryId the primary key of the like finder entry
	 * @return the like finder entry
	 * @throws NoSuchLikeFinderEntryException if a like finder entry with the primary key could not be found
	 */
	@Override
	public LikeFinderEntry findByPrimaryKey(long likeFinderEntryId)
		throws NoSuchLikeFinderEntryException {

		return findByPrimaryKey((Serializable)likeFinderEntryId);
	}

	/**
	 * Returns the like finder entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param likeFinderEntryId the primary key of the like finder entry
	 * @return the like finder entry, or <code>null</code> if a like finder entry with the primary key could not be found
	 */
	@Override
	public LikeFinderEntry fetchByPrimaryKey(long likeFinderEntryId) {
		return fetchByPrimaryKey((Serializable)likeFinderEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "likeFinderEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LIKEFINDERENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return LikeFinderEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the like finder entry persistence.
	 */
	@Activate
	public void activate() {
		_uniquePersistenceFinderByO_O_P = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByO_O_P",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					String.class.getName()
				},
				new String[] {"ownerId", "ownerType", "portletId"}, 0, 4, false,
				LikeFinderEntry::getOwnerId, LikeFinderEntry::getOwnerType,
				convertNullFunction(LikeFinderEntry::getPortletId)),
			_SQL_SELECT_LIKEFINDERENTRY_WHERE, "",
			new FinderColumn<>(
				"likeFinderEntry.", "ownerId", FinderColumn.Type.LONG, "=",
				true, true, LikeFinderEntry::getOwnerId),
			new FinderColumn<>(
				"likeFinderEntry.", "ownerType", FinderColumn.Type.INTEGER, "=",
				true, true, LikeFinderEntry::getOwnerType),
			new FinderColumn<>(
				"likeFinderEntry.", "portletId", FinderColumn.Type.STRING, "=",
				true, true, LikeFinderEntry::getPortletId));

		_collectionPersistenceFinderByC_O_O_LikeP =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_O_O_LikeP",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "ownerId", "ownerType", "portletId"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByC_O_O_LikeP",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), String.class.getName()
					},
					new String[] {
						"companyId", "ownerId", "ownerType", "portletId"
					},
					false),
				_SQL_SELECT_LIKEFINDERENTRY_WHERE,
				_SQL_COUNT_LIKEFINDERENTRY_WHERE,
				LikeFinderEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"likeFinderEntry.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, LikeFinderEntry::getCompanyId),
				new FinderColumn<>(
					"likeFinderEntry.", "ownerId", FinderColumn.Type.LONG, "=",
					true, true, LikeFinderEntry::getOwnerId),
				new FinderColumn<>(
					"likeFinderEntry.", "ownerType", FinderColumn.Type.INTEGER,
					"=", true, true, LikeFinderEntry::getOwnerType),
				new FinderColumn<>(
					"likeFinderEntry.", "portletId", FinderColumn.Type.STRING,
					"LIKE", true, true, LikeFinderEntry::getPortletId));

		LikeFinderEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		LikeFinderEntryUtil.setPersistence(null);

		entityCache.removeCache(LikeFinderEntryImpl.class.getName());
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
		LikeFinderEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LIKEFINDERENTRY =
		"SELECT likeFinderEntry FROM LikeFinderEntry likeFinderEntry";

	private static final String _SQL_SELECT_LIKEFINDERENTRY_WHERE =
		"SELECT likeFinderEntry FROM LikeFinderEntry likeFinderEntry WHERE ";

	private static final String _SQL_COUNT_LIKEFINDERENTRY_WHERE =
		"SELECT COUNT(likeFinderEntry) FROM LikeFinderEntry likeFinderEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No LikeFinderEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		LikeFinderEntryPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1017047786