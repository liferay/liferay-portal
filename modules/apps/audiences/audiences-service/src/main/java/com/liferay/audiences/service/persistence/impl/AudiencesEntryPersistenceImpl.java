/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.service.persistence.impl;

import com.liferay.audiences.exception.DuplicateAudiencesEntryExternalReferenceCodeException;
import com.liferay.audiences.exception.NoSuchAudiencesEntryException;
import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.audiences.model.AudiencesEntryTable;
import com.liferay.audiences.model.impl.AudiencesEntryImpl;
import com.liferay.audiences.model.impl.AudiencesEntryModelImpl;
import com.liferay.audiences.service.persistence.AudiencesEntryPersistence;
import com.liferay.audiences.service.persistence.AudiencesEntryUtil;
import com.liferay.audiences.service.persistence.impl.constants.AudiencesPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
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
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the audiences entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = AudiencesEntryPersistence.class)
public class AudiencesEntryPersistenceImpl
	extends BasePersistenceImpl<AudiencesEntry, NoSuchAudiencesEntryException>
	implements AudiencesEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AudiencesEntryUtil</code> to access the audiences entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AudiencesEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<AudiencesEntry, NoSuchAudiencesEntryException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the audiences entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AudiencesEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of audiences entries
	 * @param end the upper bound of the range of audiences entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching audiences entries
	 */
	@Override
	public List<AudiencesEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<AudiencesEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first audiences entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching audiences entry
	 * @throws NoSuchAudiencesEntryException if a matching audiences entry could not be found
	 */
	@Override
	public AudiencesEntry findByCompanyId_First(
			long companyId, OrderByComparator<AudiencesEntry> orderByComparator)
		throws NoSuchAudiencesEntryException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first audiences entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching audiences entry, or <code>null</code> if a matching audiences entry could not be found
	 */
	@Override
	public AudiencesEntry fetchByCompanyId_First(
		long companyId, OrderByComparator<AudiencesEntry> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the audiences entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of audiences entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching audiences entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<AudiencesEntry, NoSuchAudiencesEntryException>
			_collectionPersistenceFinderByC_LikeN;

	/**
	 * Returns all the audiences entries where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching audiences entries
	 */
	@Override
	public List<AudiencesEntry> findByC_LikeN(long companyId, String name) {
		return findByC_LikeN(
			companyId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the audiences entries where companyId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AudiencesEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of audiences entries
	 * @param end the upper bound of the range of audiences entries (not inclusive)
	 * @return the range of matching audiences entries
	 */
	@Override
	public List<AudiencesEntry> findByC_LikeN(
		long companyId, String name, int start, int end) {

		return findByC_LikeN(companyId, name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the audiences entries where companyId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AudiencesEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of audiences entries
	 * @param end the upper bound of the range of audiences entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching audiences entries
	 */
	@Override
	public List<AudiencesEntry> findByC_LikeN(
		long companyId, String name, int start, int end,
		OrderByComparator<AudiencesEntry> orderByComparator) {

		return findByC_LikeN(
			companyId, name, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the audiences entries where companyId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AudiencesEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of audiences entries
	 * @param end the upper bound of the range of audiences entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching audiences entries
	 */
	@Override
	public List<AudiencesEntry> findByC_LikeN(
		long companyId, String name, int start, int end,
		OrderByComparator<AudiencesEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_LikeN.find(
			finderCache, new Object[] {companyId, name}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first audiences entry in the ordered set where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching audiences entry
	 * @throws NoSuchAudiencesEntryException if a matching audiences entry could not be found
	 */
	@Override
	public AudiencesEntry findByC_LikeN_First(
			long companyId, String name,
			OrderByComparator<AudiencesEntry> orderByComparator)
		throws NoSuchAudiencesEntryException {

		return _collectionPersistenceFinderByC_LikeN.findFirst(
			finderCache, new Object[] {companyId, name}, orderByComparator);
	}

	/**
	 * Returns the first audiences entry in the ordered set where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching audiences entry, or <code>null</code> if a matching audiences entry could not be found
	 */
	@Override
	public AudiencesEntry fetchByC_LikeN_First(
		long companyId, String name,
		OrderByComparator<AudiencesEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_LikeN.fetchFirst(
			finderCache, new Object[] {companyId, name}, orderByComparator);
	}

	/**
	 * Removes all the audiences entries where companyId = &#63; and name LIKE &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 */
	@Override
	public void removeByC_LikeN(long companyId, String name) {
		_collectionPersistenceFinderByC_LikeN.remove(
			finderCache, new Object[] {companyId, name});
	}

	/**
	 * Returns the number of audiences entries where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching audiences entries
	 */
	@Override
	public int countByC_LikeN(long companyId, String name) {
		return _collectionPersistenceFinderByC_LikeN.count(
			finderCache, new Object[] {companyId, name});
	}

	private UniquePersistenceFinder
		<AudiencesEntry, NoSuchAudiencesEntryException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the audiences entry where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchAudiencesEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching audiences entry
	 * @throws NoSuchAudiencesEntryException if a matching audiences entry could not be found
	 */
	@Override
	public AudiencesEntry findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchAudiencesEntryException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the audiences entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching audiences entry, or <code>null</code> if a matching audiences entry could not be found
	 */
	@Override
	public AudiencesEntry fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the audiences entry where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the audiences entry that was removed
	 */
	@Override
	public AudiencesEntry removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchAudiencesEntryException {

		AudiencesEntry audiencesEntry = findByERC_C(
			externalReferenceCode, companyId);

		return remove(audiencesEntry);
	}

	/**
	 * Returns the number of audiences entries where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching audiences entries
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public AudiencesEntryPersistenceImpl() {
		setModelClass(AudiencesEntry.class);

		setModelImplClass(AudiencesEntryImpl.class);
		setModelPKClass(long.class);

		setTable(AudiencesEntryTable.INSTANCE);
	}

	/**
	 * Creates a new audiences entry with the primary key. Does not add the audiences entry to the database.
	 *
	 * @param audiencesEntryId the primary key for the new audiences entry
	 * @return the new audiences entry
	 */
	@Override
	public AudiencesEntry create(long audiencesEntryId) {
		AudiencesEntry audiencesEntry = new AudiencesEntryImpl();

		audiencesEntry.setNew(true);
		audiencesEntry.setPrimaryKey(audiencesEntryId);

		audiencesEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return audiencesEntry;
	}

	/**
	 * Removes the audiences entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param audiencesEntryId the primary key of the audiences entry
	 * @return the audiences entry that was removed
	 * @throws NoSuchAudiencesEntryException if a audiences entry with the primary key could not be found
	 */
	@Override
	public AudiencesEntry remove(long audiencesEntryId)
		throws NoSuchAudiencesEntryException {

		return remove((Serializable)audiencesEntryId);
	}

	@Override
	protected AudiencesEntry removeImpl(AudiencesEntry audiencesEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(audiencesEntry)) {
				audiencesEntry = (AudiencesEntry)session.get(
					AudiencesEntryImpl.class,
					audiencesEntry.getPrimaryKeyObj());
			}

			if (audiencesEntry != null) {
				session.delete(audiencesEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (audiencesEntry != null) {
			clearCache(audiencesEntry);
		}

		return audiencesEntry;
	}

	@Override
	public AudiencesEntry updateImpl(AudiencesEntry audiencesEntry) {
		boolean isNew = audiencesEntry.isNew();

		if (!(audiencesEntry instanceof AudiencesEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(audiencesEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					audiencesEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in audiencesEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AudiencesEntry implementation " +
					audiencesEntry.getClass());
		}

		AudiencesEntryModelImpl audiencesEntryModelImpl =
			(AudiencesEntryModelImpl)audiencesEntry;

		if (Validator.isNull(audiencesEntry.getExternalReferenceCode())) {
			audiencesEntry.setExternalReferenceCode(
				String.valueOf(audiencesEntry.getPrimaryKey()));
		}
		else {
			if (!Objects.equals(
					audiencesEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					audiencesEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = audiencesEntry.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = audiencesEntry.getPrimaryKey();
					}

					try {
						audiencesEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								AudiencesEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								audiencesEntry.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			AudiencesEntry ercAudiencesEntry = fetchByERC_C(
				audiencesEntry.getExternalReferenceCode(),
				audiencesEntry.getCompanyId());

			if (isNew) {
				if (ercAudiencesEntry != null) {
					throw new DuplicateAudiencesEntryExternalReferenceCodeException(
						"Duplicate audiences entry with external reference code " +
							audiencesEntry.getExternalReferenceCode() +
								" and company " +
									audiencesEntry.getCompanyId());
				}
			}
			else {
				if ((ercAudiencesEntry != null) &&
					(audiencesEntry.getAudiencesEntryId() !=
						ercAudiencesEntry.getAudiencesEntryId())) {

					throw new DuplicateAudiencesEntryExternalReferenceCodeException(
						"Duplicate audiences entry with external reference code " +
							audiencesEntry.getExternalReferenceCode() +
								" and company " +
									audiencesEntry.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (audiencesEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				audiencesEntry.setCreateDate(date);
			}
			else {
				audiencesEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!audiencesEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				audiencesEntry.setModifiedDate(date);
			}
			else {
				audiencesEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(audiencesEntry);
			}
			else {
				audiencesEntry = (AudiencesEntry)session.merge(audiencesEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(audiencesEntry, false);

		if (isNew) {
			audiencesEntry.setNew(false);
		}

		audiencesEntry.resetOriginalValues();

		return audiencesEntry;
	}

	/**
	 * Returns the audiences entry with the primary key or throws a <code>NoSuchAudiencesEntryException</code> if it could not be found.
	 *
	 * @param audiencesEntryId the primary key of the audiences entry
	 * @return the audiences entry
	 * @throws NoSuchAudiencesEntryException if a audiences entry with the primary key could not be found
	 */
	@Override
	public AudiencesEntry findByPrimaryKey(long audiencesEntryId)
		throws NoSuchAudiencesEntryException {

		return findByPrimaryKey((Serializable)audiencesEntryId);
	}

	/**
	 * Returns the audiences entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param audiencesEntryId the primary key of the audiences entry
	 * @return the audiences entry, or <code>null</code> if a audiences entry with the primary key could not be found
	 */
	@Override
	public AudiencesEntry fetchByPrimaryKey(long audiencesEntryId) {
		return fetchByPrimaryKey((Serializable)audiencesEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "audiencesEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_AUDIENCESENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return AudiencesEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the audiences entry persistence.
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
				_SQL_SELECT_AUDIENCESENTRY_WHERE,
				_SQL_COUNT_AUDIENCESENTRY_WHERE,
				AudiencesEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"audiencesEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, AudiencesEntry::getCompanyId));

		_collectionPersistenceFinderByC_LikeN =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_LikeN",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "name"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_LikeN",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"companyId", "name"}, false),
				_SQL_SELECT_AUDIENCESENTRY_WHERE,
				_SQL_COUNT_AUDIENCESENTRY_WHERE,
				AudiencesEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"audiencesEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, AudiencesEntry::getCompanyId),
				new FinderColumn<>(
					"audiencesEntry.", "name", FinderColumn.Type.STRING, "LIKE",
					false, true, AudiencesEntry::getName));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(AudiencesEntry::getExternalReferenceCode),
				AudiencesEntry::getCompanyId),
			_SQL_SELECT_AUDIENCESENTRY_WHERE, "",
			new FinderColumn<>(
				"audiencesEntry.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				AudiencesEntry::getExternalReferenceCode),
			new FinderColumn<>(
				"audiencesEntry.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, AudiencesEntry::getCompanyId));

		AudiencesEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		AudiencesEntryUtil.setPersistence(null);

		entityCache.removeCache(AudiencesEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = AudiencesPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = AudiencesPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = AudiencesPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		AudiencesEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_AUDIENCESENTRY =
		"SELECT audiencesEntry FROM AudiencesEntry audiencesEntry";

	private static final String _SQL_SELECT_AUDIENCESENTRY_WHERE =
		"SELECT audiencesEntry FROM AudiencesEntry audiencesEntry WHERE ";

	private static final String _SQL_COUNT_AUDIENCESENTRY_WHERE =
		"SELECT COUNT(audiencesEntry) FROM AudiencesEntry audiencesEntry WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-968481709