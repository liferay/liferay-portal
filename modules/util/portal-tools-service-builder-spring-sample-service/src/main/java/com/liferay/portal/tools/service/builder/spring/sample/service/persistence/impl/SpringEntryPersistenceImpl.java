/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.spring.sample.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.spring.sample.exception.NoSuchSpringEntryException;
import com.liferay.portal.tools.service.builder.spring.sample.model.SpringEntry;
import com.liferay.portal.tools.service.builder.spring.sample.model.SpringEntryTable;
import com.liferay.portal.tools.service.builder.spring.sample.model.impl.SpringEntryImpl;
import com.liferay.portal.tools.service.builder.spring.sample.model.impl.SpringEntryModelImpl;
import com.liferay.portal.tools.service.builder.spring.sample.service.persistence.SpringEntryPersistence;
import com.liferay.portal.tools.service.builder.spring.sample.service.persistence.SpringEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the spring entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class SpringEntryPersistenceImpl
	extends BasePersistenceImpl<SpringEntry, NoSuchSpringEntryException>
	implements SpringEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SpringEntryUtil</code> to access the spring entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SpringEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<SpringEntry, NoSuchSpringEntryException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the spring entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SpringEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of spring entries
	 * @param end the upper bound of the range of spring entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching spring entries
	 */
	@Override
	public List<SpringEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<SpringEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first spring entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching spring entry
	 * @throws NoSuchSpringEntryException if a matching spring entry could not be found
	 */
	@Override
	public SpringEntry findByUuid_First(
			String uuid, OrderByComparator<SpringEntry> orderByComparator)
		throws NoSuchSpringEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first spring entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching spring entry, or <code>null</code> if a matching spring entry could not be found
	 */
	@Override
	public SpringEntry fetchByUuid_First(
		String uuid, OrderByComparator<SpringEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the spring entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of spring entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching spring entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder<SpringEntry, NoSuchSpringEntryException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the spring entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SpringEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of spring entries
	 * @param end the upper bound of the range of spring entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching spring entries
	 */
	@Override
	public List<SpringEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SpringEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first spring entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching spring entry
	 * @throws NoSuchSpringEntryException if a matching spring entry could not be found
	 */
	@Override
	public SpringEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<SpringEntry> orderByComparator)
		throws NoSuchSpringEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first spring entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching spring entry, or <code>null</code> if a matching spring entry could not be found
	 */
	@Override
	public SpringEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<SpringEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the spring entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of spring entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching spring entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder<SpringEntry, NoSuchSpringEntryException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the spring entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SpringEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of spring entries
	 * @param end the upper bound of the range of spring entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching spring entries
	 */
	@Override
	public List<SpringEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<SpringEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first spring entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching spring entry
	 * @throws NoSuchSpringEntryException if a matching spring entry could not be found
	 */
	@Override
	public SpringEntry findByCompanyId_First(
			long companyId, OrderByComparator<SpringEntry> orderByComparator)
		throws NoSuchSpringEntryException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first spring entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching spring entry, or <code>null</code> if a matching spring entry could not be found
	 */
	@Override
	public SpringEntry fetchByCompanyId_First(
		long companyId, OrderByComparator<SpringEntry> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the spring entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of spring entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching spring entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	public SpringEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(SpringEntry.class);

		setModelImplClass(SpringEntryImpl.class);
		setModelPKClass(long.class);

		setTable(SpringEntryTable.INSTANCE);
	}

	/**
	 * Creates a new spring entry with the primary key. Does not add the spring entry to the database.
	 *
	 * @param springEntryId the primary key for the new spring entry
	 * @return the new spring entry
	 */
	@Override
	public SpringEntry create(long springEntryId) {
		SpringEntry springEntry = new SpringEntryImpl();

		springEntry.setNew(true);
		springEntry.setPrimaryKey(springEntryId);

		String uuid = PortalUUIDUtil.generate();

		springEntry.setUuid(uuid);

		springEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return springEntry;
	}

	/**
	 * Removes the spring entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param springEntryId the primary key of the spring entry
	 * @return the spring entry that was removed
	 * @throws NoSuchSpringEntryException if a spring entry with the primary key could not be found
	 */
	@Override
	public SpringEntry remove(long springEntryId)
		throws NoSuchSpringEntryException {

		return remove((Serializable)springEntryId);
	}

	@Override
	protected SpringEntry removeImpl(SpringEntry springEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(springEntry)) {
				springEntry = (SpringEntry)session.get(
					SpringEntryImpl.class, springEntry.getPrimaryKeyObj());
			}

			if (springEntry != null) {
				session.delete(springEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (springEntry != null) {
			clearCache(springEntry);
		}

		return springEntry;
	}

	@Override
	public SpringEntry updateImpl(SpringEntry springEntry) {
		boolean isNew = springEntry.isNew();

		if (!(springEntry instanceof SpringEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(springEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(springEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in springEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SpringEntry implementation " +
					springEntry.getClass());
		}

		SpringEntryModelImpl springEntryModelImpl =
			(SpringEntryModelImpl)springEntry;

		if (Validator.isNull(springEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			springEntry.setUuid(uuid);
		}

		if (isNew && (springEntry.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				springEntry.setCreateDate(date);
			}
			else {
				springEntry.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(springEntry);
			}
			else {
				springEntry = (SpringEntry)session.merge(springEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(springEntry, false);

		if (isNew) {
			springEntry.setNew(false);
		}

		springEntry.resetOriginalValues();

		return springEntry;
	}

	/**
	 * Returns the spring entry with the primary key or throws a <code>NoSuchSpringEntryException</code> if it could not be found.
	 *
	 * @param springEntryId the primary key of the spring entry
	 * @return the spring entry
	 * @throws NoSuchSpringEntryException if a spring entry with the primary key could not be found
	 */
	@Override
	public SpringEntry findByPrimaryKey(long springEntryId)
		throws NoSuchSpringEntryException {

		return findByPrimaryKey((Serializable)springEntryId);
	}

	/**
	 * Returns the spring entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param springEntryId the primary key of the spring entry
	 * @return the spring entry, or <code>null</code> if a spring entry with the primary key could not be found
	 */
	@Override
	public SpringEntry fetchByPrimaryKey(long springEntryId) {
		return fetchByPrimaryKey((Serializable)springEntryId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "springEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SPRINGENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return SpringEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the spring entry persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByUuid = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"uuid_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, false, null),
			_SQL_SELECT_SPRINGENTRY_WHERE, _SQL_COUNT_SPRINGENTRY_WHERE,
			SpringEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"springEntry.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, SpringEntry::getUuid));

		_collectionPersistenceFinderByUuid_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, false, null),
				_SQL_SELECT_SPRINGENTRY_WHERE, _SQL_COUNT_SPRINGENTRY_WHERE,
				SpringEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"springEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, SpringEntry::getUuid),
				new FinderColumn<>(
					"springEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, SpringEntry::getCompanyId));

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
				_SQL_SELECT_SPRINGENTRY_WHERE, _SQL_COUNT_SPRINGENTRY_WHERE,
				SpringEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"springEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, SpringEntry::getCompanyId));

		SpringEntryUtil.setPersistence(this);
	}

	public void destroy() {
		SpringEntryUtil.setPersistence(null);

		entityCache.removeCache(SpringEntryImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		SpringEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SPRINGENTRY =
		"SELECT springEntry FROM SpringEntry springEntry";

	private static final String _SQL_SELECT_SPRINGENTRY_WHERE =
		"SELECT springEntry FROM SpringEntry springEntry WHERE ";

	private static final String _SQL_COUNT_SPRINGENTRY_WHERE =
		"SELECT COUNT(springEntry) FROM SpringEntry springEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No SpringEntry exists with the key {";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:733895332