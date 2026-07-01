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
import com.liferay.portal.tools.service.builder.test.compat740.exception.DuplicateERCGroupEntryExternalReferenceCodeException;
import com.liferay.portal.tools.service.builder.test.compat740.exception.NoSuchERCGroupEntryException;
import com.liferay.portal.tools.service.builder.test.compat740.model.ERCGroupEntry;
import com.liferay.portal.tools.service.builder.test.compat740.model.ERCGroupEntryTable;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.ERCGroupEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.ERCGroupEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.ERCGroupEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.ERCGroupEntryUtil;
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
 * The persistence implementation for the erc group entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = ERCGroupEntryPersistence.class)
public class ERCGroupEntryPersistenceImpl
	extends BasePersistenceImpl<ERCGroupEntry, NoSuchERCGroupEntryException>
	implements ERCGroupEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ERCGroupEntryUtil</code> to access the erc group entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ERCGroupEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ERCGroupEntry, NoSuchERCGroupEntryException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the erc group entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCGroupEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of erc group entries
	 * @param end the upper bound of the range of erc group entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching erc group entries
	 */
	@Override
	public List<ERCGroupEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<ERCGroupEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first erc group entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc group entry
	 * @throws NoSuchERCGroupEntryException if a matching erc group entry could not be found
	 */
	@Override
	public ERCGroupEntry findByUuid_First(
			String uuid, OrderByComparator<ERCGroupEntry> orderByComparator)
		throws NoSuchERCGroupEntryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first erc group entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc group entry, or <code>null</code> if a matching erc group entry could not be found
	 */
	@Override
	public ERCGroupEntry fetchByUuid_First(
		String uuid, OrderByComparator<ERCGroupEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the erc group entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of erc group entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching erc group entries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<ERCGroupEntry, NoSuchERCGroupEntryException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the erc group entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchERCGroupEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching erc group entry
	 * @throws NoSuchERCGroupEntryException if a matching erc group entry could not be found
	 */
	@Override
	public ERCGroupEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchERCGroupEntryException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the erc group entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching erc group entry, or <code>null</code> if a matching erc group entry could not be found
	 */
	@Override
	public ERCGroupEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the erc group entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the erc group entry that was removed
	 */
	@Override
	public ERCGroupEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchERCGroupEntryException {

		ERCGroupEntry ercGroupEntry = findByUUID_G(uuid, groupId);

		return remove(ercGroupEntry);
	}

	/**
	 * Returns the number of erc group entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching erc group entries
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<ERCGroupEntry, NoSuchERCGroupEntryException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the erc group entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ERCGroupEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of erc group entries
	 * @param end the upper bound of the range of erc group entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching erc group entries
	 */
	@Override
	public List<ERCGroupEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ERCGroupEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first erc group entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc group entry
	 * @throws NoSuchERCGroupEntryException if a matching erc group entry could not be found
	 */
	@Override
	public ERCGroupEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<ERCGroupEntry> orderByComparator)
		throws NoSuchERCGroupEntryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first erc group entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching erc group entry, or <code>null</code> if a matching erc group entry could not be found
	 */
	@Override
	public ERCGroupEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<ERCGroupEntry> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the erc group entries where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of erc group entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching erc group entries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private UniquePersistenceFinder<ERCGroupEntry, NoSuchERCGroupEntryException>
		_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the erc group entry where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchERCGroupEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching erc group entry
	 * @throws NoSuchERCGroupEntryException if a matching erc group entry could not be found
	 */
	@Override
	public ERCGroupEntry findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchERCGroupEntryException {

		return _uniquePersistenceFinderByERC_G.find(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the erc group entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching erc group entry, or <code>null</code> if a matching erc group entry could not be found
	 */
	@Override
	public ERCGroupEntry fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			finderCache, new Object[] {externalReferenceCode, groupId},
			useFinderCache);
	}

	/**
	 * Removes the erc group entry where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the erc group entry that was removed
	 */
	@Override
	public ERCGroupEntry removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchERCGroupEntryException {

		ERCGroupEntry ercGroupEntry = findByERC_G(
			externalReferenceCode, groupId);

		return remove(ercGroupEntry);
	}

	/**
	 * Returns the number of erc group entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching erc group entries
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	public ERCGroupEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ERCGroupEntry.class);

		setModelImplClass(ERCGroupEntryImpl.class);
		setModelPKClass(long.class);

		setTable(ERCGroupEntryTable.INSTANCE);
	}

	/**
	 * Creates a new erc group entry with the primary key. Does not add the erc group entry to the database.
	 *
	 * @param ercGroupEntryId the primary key for the new erc group entry
	 * @return the new erc group entry
	 */
	@Override
	public ERCGroupEntry create(long ercGroupEntryId) {
		ERCGroupEntry ercGroupEntry = new ERCGroupEntryImpl();

		ercGroupEntry.setNew(true);
		ercGroupEntry.setPrimaryKey(ercGroupEntryId);

		String uuid = PortalUUIDUtil.generate();

		ercGroupEntry.setUuid(uuid);

		ercGroupEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ercGroupEntry;
	}

	/**
	 * Removes the erc group entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ercGroupEntryId the primary key of the erc group entry
	 * @return the erc group entry that was removed
	 * @throws NoSuchERCGroupEntryException if a erc group entry with the primary key could not be found
	 */
	@Override
	public ERCGroupEntry remove(long ercGroupEntryId)
		throws NoSuchERCGroupEntryException {

		return remove((Serializable)ercGroupEntryId);
	}

	@Override
	protected ERCGroupEntry removeImpl(ERCGroupEntry ercGroupEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ercGroupEntry)) {
				ercGroupEntry = (ERCGroupEntry)session.get(
					ERCGroupEntryImpl.class, ercGroupEntry.getPrimaryKeyObj());
			}

			if (ercGroupEntry != null) {
				session.delete(ercGroupEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ercGroupEntry != null) {
			clearCache(ercGroupEntry);
		}

		return ercGroupEntry;
	}

	@Override
	public ERCGroupEntry updateImpl(ERCGroupEntry ercGroupEntry) {
		boolean isNew = ercGroupEntry.isNew();

		if (!(ercGroupEntry instanceof ERCGroupEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ercGroupEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ercGroupEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ercGroupEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ERCGroupEntry implementation " +
					ercGroupEntry.getClass());
		}

		ERCGroupEntryModelImpl ercGroupEntryModelImpl =
			(ERCGroupEntryModelImpl)ercGroupEntry;

		if (Validator.isNull(ercGroupEntry.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			ercGroupEntry.setUuid(uuid);
		}

		if (Validator.isNull(ercGroupEntry.getExternalReferenceCode())) {
			ercGroupEntry.setExternalReferenceCode(ercGroupEntry.getUuid());
		}
		else {
			if (!Objects.equals(
					ercGroupEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					ercGroupEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = ercGroupEntry.getCompanyId();

					long groupId = ercGroupEntry.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = ercGroupEntry.getPrimaryKey();
					}

					try {
						ercGroupEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								ERCGroupEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								ercGroupEntry.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			ERCGroupEntry ercERCGroupEntry = fetchByERC_G(
				ercGroupEntry.getExternalReferenceCode(),
				ercGroupEntry.getGroupId());

			if (isNew) {
				if (ercERCGroupEntry != null) {
					throw new DuplicateERCGroupEntryExternalReferenceCodeException(
						"Duplicate erc group entry with external reference code " +
							ercGroupEntry.getExternalReferenceCode() +
								" and group " + ercGroupEntry.getGroupId());
				}
			}
			else {
				if ((ercERCGroupEntry != null) &&
					(ercGroupEntry.getErcGroupEntryId() !=
						ercERCGroupEntry.getErcGroupEntryId())) {

					throw new DuplicateERCGroupEntryExternalReferenceCodeException(
						"Duplicate erc group entry with external reference code " +
							ercGroupEntry.getExternalReferenceCode() +
								" and group " + ercGroupEntry.getGroupId());
				}
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(ercGroupEntry);
			}
			else {
				ercGroupEntry = (ERCGroupEntry)session.merge(ercGroupEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ercGroupEntry, false);

		if (isNew) {
			ercGroupEntry.setNew(false);
		}

		ercGroupEntry.resetOriginalValues();

		return ercGroupEntry;
	}

	/**
	 * Returns the erc group entry with the primary key or throws a <code>NoSuchERCGroupEntryException</code> if it could not be found.
	 *
	 * @param ercGroupEntryId the primary key of the erc group entry
	 * @return the erc group entry
	 * @throws NoSuchERCGroupEntryException if a erc group entry with the primary key could not be found
	 */
	@Override
	public ERCGroupEntry findByPrimaryKey(long ercGroupEntryId)
		throws NoSuchERCGroupEntryException {

		return findByPrimaryKey((Serializable)ercGroupEntryId);
	}

	/**
	 * Returns the erc group entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ercGroupEntryId the primary key of the erc group entry
	 * @return the erc group entry, or <code>null</code> if a erc group entry with the primary key could not be found
	 */
	@Override
	public ERCGroupEntry fetchByPrimaryKey(long ercGroupEntryId) {
		return fetchByPrimaryKey((Serializable)ercGroupEntryId);
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
		return "ercGroupEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ERCGROUPENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ERCGroupEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the erc group entry persistence.
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
			_SQL_SELECT_ERCGROUPENTRY_WHERE, _SQL_COUNT_ERCGROUPENTRY_WHERE,
			ERCGroupEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"ercGroupEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, ERCGroupEntry::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(ERCGroupEntry::getUuid),
				ERCGroupEntry::getGroupId),
			_SQL_SELECT_ERCGROUPENTRY_WHERE, "",
			new FinderColumn<>(
				"ercGroupEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, ERCGroupEntry::getUuid),
			new FinderColumn<>(
				"ercGroupEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, ERCGroupEntry::getGroupId));

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
				_SQL_SELECT_ERCGROUPENTRY_WHERE, _SQL_COUNT_ERCGROUPENTRY_WHERE,
				ERCGroupEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"ercGroupEntry.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, ERCGroupEntry::getUuid),
				new FinderColumn<>(
					"ercGroupEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, ERCGroupEntry::getCompanyId));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(ERCGroupEntry::getExternalReferenceCode),
				ERCGroupEntry::getGroupId),
			_SQL_SELECT_ERCGROUPENTRY_WHERE, "",
			new FinderColumn<>(
				"ercGroupEntry.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				ERCGroupEntry::getExternalReferenceCode),
			new FinderColumn<>(
				"ercGroupEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, ERCGroupEntry::getGroupId));

		ERCGroupEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ERCGroupEntryUtil.setPersistence(null);

		entityCache.removeCache(ERCGroupEntryImpl.class.getName());
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
		ERCGroupEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ERCGROUPENTRY =
		"SELECT ercGroupEntry FROM ERCGroupEntry ercGroupEntry";

	private static final String _SQL_SELECT_ERCGROUPENTRY_WHERE =
		"SELECT ercGroupEntry FROM ERCGroupEntry ercGroupEntry WHERE ";

	private static final String _SQL_COUNT_ERCGROUPENTRY_WHERE =
		"SELECT COUNT(ercGroupEntry) FROM ERCGroupEntry ercGroupEntry WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1425953795