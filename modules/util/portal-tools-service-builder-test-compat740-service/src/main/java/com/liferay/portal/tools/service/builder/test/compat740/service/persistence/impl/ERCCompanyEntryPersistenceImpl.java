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
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.tools.service.builder.test.compat740.exception.DuplicateERCCompanyEntryExternalReferenceCodeException;
import com.liferay.portal.tools.service.builder.test.compat740.exception.NoSuchERCCompanyEntryException;
import com.liferay.portal.tools.service.builder.test.compat740.model.ERCCompanyEntry;
import com.liferay.portal.tools.service.builder.test.compat740.model.ERCCompanyEntryTable;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.ERCCompanyEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.ERCCompanyEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.ERCCompanyEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.ERCCompanyEntryUtil;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl.constants.SBCompat740PersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the erc company entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = ERCCompanyEntryPersistence.class)
public class ERCCompanyEntryPersistenceImpl
	extends BasePersistenceImpl<ERCCompanyEntry, NoSuchERCCompanyEntryException>
	implements ERCCompanyEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ERCCompanyEntryUtil</code> to access the erc company entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ERCCompanyEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ERCCompanyEntry, NoSuchERCCompanyEntryException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the erc company entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCCompanyEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of erc company entries
	 * @param end the upper bound of the range of erc company entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching erc company entries
	 */
	@Override
	public List<ERCCompanyEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ERCCompanyEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first erc company entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc company entry
	 * @throws NoSuchERCCompanyEntryException if a matching erc company entry could not be found
	 */
	@Override
	public ERCCompanyEntry findByUuid_First(
			String uuid, OrderByComparator<ERCCompanyEntry> orderByComparator)
		throws NoSuchERCCompanyEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first erc company entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc company entry, or <code>null</code> if a matching erc company entry could not be found
	 */
	@Override
	public ERCCompanyEntry fetchByUuid_First(
		String uuid, OrderByComparator<ERCCompanyEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the erc company entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of erc company entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching erc company entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<ERCCompanyEntry, NoSuchERCCompanyEntryException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the erc company entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCCompanyEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of erc company entries
	 * @param end the upper bound of the range of erc company entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching erc company entries
	 */
	@Override
	public List<ERCCompanyEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ERCCompanyEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first erc company entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc company entry
	 * @throws NoSuchERCCompanyEntryException if a matching erc company entry could not be found
	 */
	@Override
	public ERCCompanyEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ERCCompanyEntry> orderByComparator)
		throws NoSuchERCCompanyEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first erc company entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc company entry, or <code>null</code> if a matching erc company entry could not be found
	 */
	@Override
	public ERCCompanyEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ERCCompanyEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the erc company entries where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of erc company entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching erc company entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private UniquePersistenceFinder
		<ERCCompanyEntry, NoSuchERCCompanyEntryException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the erc company entry where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchERCCompanyEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching erc company entry
	 * @throws NoSuchERCCompanyEntryException if a matching erc company entry could not be found
	 */
	@Override
	public ERCCompanyEntry findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchERCCompanyEntryException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the erc company entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching erc company entry, or <code>null</code> if a matching erc company entry could not be found
	 */
	@Override
	public ERCCompanyEntry fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the erc company entry where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the erc company entry that was removed
	 */
	@Override
	public ERCCompanyEntry removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchERCCompanyEntryException {

		ERCCompanyEntry ercCompanyEntry = findByERC_C(
			externalReferenceCode, companyId);

		return remove(ercCompanyEntry);
	}

	/**
	 * Returns the number of erc company entries where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching erc company entries
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public ERCCompanyEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ERCCompanyEntry.class);

		setModelImplClass(ERCCompanyEntryImpl.class);
		setModelPKClass(long.class);

		setTable(ERCCompanyEntryTable.INSTANCE);
	}

	/**
	 * Creates a new erc company entry with the primary key. Does not add the erc company entry to the database.
	 *
	 * @param ercCompanyEntryId the primary key for the new erc company entry
	 * @return the new erc company entry
	 */
	@Override
	public ERCCompanyEntry create(long ercCompanyEntryId) {
		ERCCompanyEntry ercCompanyEntry = new ERCCompanyEntryImpl();

		ercCompanyEntry.setNew(true);
		ercCompanyEntry.setPrimaryKey(ercCompanyEntryId);

		String uuid = PortalUUIDUtil.generate();

		ercCompanyEntry.setUuid(uuid);

		ercCompanyEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ercCompanyEntry;
	}

	/**
	 * Removes the erc company entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ercCompanyEntryId the primary key of the erc company entry
	 * @return the erc company entry that was removed
	 * @throws NoSuchERCCompanyEntryException if a erc company entry with the primary key could not be found
	 */
	@Override
	public ERCCompanyEntry remove(long ercCompanyEntryId)
		throws NoSuchERCCompanyEntryException {

		return remove((Serializable)ercCompanyEntryId);
	}

	@Override
	protected ERCCompanyEntry removeImpl(ERCCompanyEntry ercCompanyEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ercCompanyEntry)) {
				ercCompanyEntry = (ERCCompanyEntry)session.get(
					ERCCompanyEntryImpl.class,
					ercCompanyEntry.getPrimaryKeyObj());
			}

			if (ercCompanyEntry != null) {
				session.delete(ercCompanyEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ercCompanyEntry != null) {
			clearCache(ercCompanyEntry);
		}

		return ercCompanyEntry;
	}

	@Override
	public ERCCompanyEntry updateImpl(ERCCompanyEntry ercCompanyEntry) {
		boolean isNew = ercCompanyEntry.isNew();

		if (!(ercCompanyEntry instanceof ERCCompanyEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ercCompanyEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ercCompanyEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ercCompanyEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ERCCompanyEntry implementation " +
					ercCompanyEntry.getClass());
		}

		ERCCompanyEntryModelImpl ercCompanyEntryModelImpl =
			(ERCCompanyEntryModelImpl)ercCompanyEntry;

		if (Validator.isNull(ercCompanyEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			ercCompanyEntry.setUuid(uuid);
		}

		if (Validator.isNull(ercCompanyEntry.getExternalReferenceCode())) {
			ercCompanyEntry.setExternalReferenceCode(ercCompanyEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					ercCompanyEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					ercCompanyEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = ercCompanyEntry.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = ercCompanyEntry.getPrimaryKey();
					}

					try {
						ercCompanyEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								ERCCompanyEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								ercCompanyEntry.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			ERCCompanyEntry ercERCCompanyEntry = fetchByERC_C(
				ercCompanyEntry.getExternalReferenceCode(),
				ercCompanyEntry.getCompanyId());

			if (isNew) {
				if (ercERCCompanyEntry != null) {
					throw new DuplicateERCCompanyEntryExternalReferenceCodeException(
						"Duplicate erc company entry with external reference code " +
							ercCompanyEntry.getExternalReferenceCode() +
								" and company " +
									ercCompanyEntry.getCompanyId());
				}
			}
			else {
				if ((ercERCCompanyEntry != null) &&
					(ercCompanyEntry.getErcCompanyEntryId() !=
						ercERCCompanyEntry.getErcCompanyEntryId())) {

					throw new DuplicateERCCompanyEntryExternalReferenceCodeException(
						"Duplicate erc company entry with external reference code " +
							ercCompanyEntry.getExternalReferenceCode() +
								" and company " +
									ercCompanyEntry.getCompanyId());
				}
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(ercCompanyEntry);
			}
			else {
				ercCompanyEntry = (ERCCompanyEntry)session.merge(
					ercCompanyEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ercCompanyEntry, false);

		if (isNew) {
			ercCompanyEntry.setNew(false);
		}

		ercCompanyEntry.resetOriginalValues();

		return ercCompanyEntry;
	}

	/**
	 * Returns the erc company entry with the primary key or throws a <code>NoSuchERCCompanyEntryException</code> if it could not be found.
	 *
	 * @param ercCompanyEntryId the primary key of the erc company entry
	 * @return the erc company entry
	 * @throws NoSuchERCCompanyEntryException if a erc company entry with the primary key could not be found
	 */
	@Override
	public ERCCompanyEntry findByPrimaryKey(long ercCompanyEntryId)
		throws NoSuchERCCompanyEntryException {

		return findByPrimaryKey((Serializable)ercCompanyEntryId);
	}

	/**
	 * Returns the erc company entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ercCompanyEntryId the primary key of the erc company entry
	 * @return the erc company entry, or <code>null</code> if a erc company entry with the primary key could not be found
	 */
	@Override
	public ERCCompanyEntry fetchByPrimaryKey(long ercCompanyEntryId) {
		return fetchByPrimaryKey((Serializable)ercCompanyEntryId);
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
		return "ercCompanyEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ERCCOMPANYENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ERCCompanyEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the erc company entry persistence.
	 */
	@Activate
	public void activate() {
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
			_SQL_SELECT_ERCCOMPANYENTRY_WHERE, _SQL_COUNT_ERCCOMPANYENTRY_WHERE,
			ERCCompanyEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"ercCompanyEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, ERCCompanyEntry::getUuid));

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
				_SQL_SELECT_ERCCOMPANYENTRY_WHERE,
				_SQL_COUNT_ERCCOMPANYENTRY_WHERE,
				ERCCompanyEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"ercCompanyEntry.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					ERCCompanyEntry::getUuid),
				new FinderColumn<>(
					"ercCompanyEntry.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, ERCCompanyEntry::getCompanyId));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(ERCCompanyEntry::getExternalReferenceCode),
				ERCCompanyEntry::getCompanyId),
			_SQL_SELECT_ERCCOMPANYENTRY_WHERE, "",
			new FinderColumn<>(
				"ercCompanyEntry.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				ERCCompanyEntry::getExternalReferenceCode),
			new FinderColumn<>(
				"ercCompanyEntry.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, ERCCompanyEntry::getCompanyId));

		ERCCompanyEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ERCCompanyEntryUtil.setPersistence(null);

		entityCache.removeCache(ERCCompanyEntryImpl.class.getName());
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
		ERCCompanyEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ERCCOMPANYENTRY =
		"SELECT ercCompanyEntry FROM ERCCompanyEntry ercCompanyEntry";

	private static final String _SQL_SELECT_ERCCOMPANYENTRY_WHERE =
		"SELECT ercCompanyEntry FROM ERCCompanyEntry ercCompanyEntry WHERE ";

	private static final String _SQL_COUNT_ERCCOMPANYENTRY_WHERE =
		"SELECT COUNT(ercCompanyEntry) FROM ERCCompanyEntry ercCompanyEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ERCCompanyEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ERCCompanyEntryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1761861915