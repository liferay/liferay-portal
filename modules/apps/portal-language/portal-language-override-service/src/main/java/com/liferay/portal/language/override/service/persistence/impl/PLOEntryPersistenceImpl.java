/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.service.persistence.impl;

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
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.language.override.exception.DuplicatePLOEntryExternalReferenceCodeException;
import com.liferay.portal.language.override.exception.NoSuchPLOEntryException;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.model.PLOEntryTable;
import com.liferay.portal.language.override.model.impl.PLOEntryImpl;
import com.liferay.portal.language.override.model.impl.PLOEntryModelImpl;
import com.liferay.portal.language.override.service.persistence.PLOEntryPersistence;
import com.liferay.portal.language.override.service.persistence.PLOEntryUtil;
import com.liferay.portal.language.override.service.persistence.impl.constants.PLOPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
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
 * The persistence implementation for the plo entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Drew Brokke
 * @generated
 */
@Component(service = PLOEntryPersistence.class)
public class PLOEntryPersistenceImpl
	extends BasePersistenceImpl<PLOEntry, NoSuchPLOEntryException>
	implements PLOEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PLOEntryUtil</code> to access the plo entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PLOEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<PLOEntry, NoSuchPLOEntryException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the plo entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PLOEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of plo entries
	 * @param end the upper bound of the range of plo entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching plo entries
	 */
	@Override
	public List<PLOEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<PLOEntry> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first plo entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching plo entry
	 * @throws NoSuchPLOEntryException if a matching plo entry could not be found
	 */
	@Override
	public PLOEntry findByCompanyId_First(
			long companyId, OrderByComparator<PLOEntry> orderByComparator)
		throws NoSuchPLOEntryException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first plo entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching plo entry, or <code>null</code> if a matching plo entry could not be found
	 */
	@Override
	public PLOEntry fetchByCompanyId_First(
		long companyId, OrderByComparator<PLOEntry> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the plo entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of plo entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching plo entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder<PLOEntry, NoSuchPLOEntryException>
		_collectionPersistenceFinderByC_K;

	/**
	 * Returns an ordered range of all the plo entries where companyId = &#63; and key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PLOEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @param start the lower bound of the range of plo entries
	 * @param end the upper bound of the range of plo entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching plo entries
	 */
	@Override
	public List<PLOEntry> findByC_K(
		long companyId, String key, int start, int end,
		OrderByComparator<PLOEntry> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_K.find(
			finderCache, new Object[] {companyId, key}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first plo entry in the ordered set where companyId = &#63; and key = &#63;.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching plo entry
	 * @throws NoSuchPLOEntryException if a matching plo entry could not be found
	 */
	@Override
	public PLOEntry findByC_K_First(
			long companyId, String key,
			OrderByComparator<PLOEntry> orderByComparator)
		throws NoSuchPLOEntryException {

		return _collectionPersistenceFinderByC_K.findFirst(
			finderCache, new Object[] {companyId, key}, orderByComparator);
	}

	/**
	 * Returns the first plo entry in the ordered set where companyId = &#63; and key = &#63;.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching plo entry, or <code>null</code> if a matching plo entry could not be found
	 */
	@Override
	public PLOEntry fetchByC_K_First(
		long companyId, String key,
		OrderByComparator<PLOEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_K.fetchFirst(
			finderCache, new Object[] {companyId, key}, orderByComparator);
	}

	/**
	 * Removes all the plo entries where companyId = &#63; and key = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 */
	@Override
	public void removeByC_K(long companyId, String key) {
		_collectionPersistenceFinderByC_K.remove(
			finderCache, new Object[] {companyId, key});
	}

	/**
	 * Returns the number of plo entries where companyId = &#63; and key = &#63;.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @return the number of matching plo entries
	 */
	@Override
	public int countByC_K(long companyId, String key) {
		return _collectionPersistenceFinderByC_K.count(
			finderCache, new Object[] {companyId, key});
	}

	private CollectionPersistenceFinder<PLOEntry, NoSuchPLOEntryException>
		_collectionPersistenceFinderByC_L;

	/**
	 * Returns an ordered range of all the plo entries where companyId = &#63; and languageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PLOEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param languageId the language ID
	 * @param start the lower bound of the range of plo entries
	 * @param end the upper bound of the range of plo entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching plo entries
	 */
	@Override
	public List<PLOEntry> findByC_L(
		long companyId, String languageId, int start, int end,
		OrderByComparator<PLOEntry> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_L.find(
			finderCache, new Object[] {companyId, languageId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first plo entry in the ordered set where companyId = &#63; and languageId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param languageId the language ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching plo entry
	 * @throws NoSuchPLOEntryException if a matching plo entry could not be found
	 */
	@Override
	public PLOEntry findByC_L_First(
			long companyId, String languageId,
			OrderByComparator<PLOEntry> orderByComparator)
		throws NoSuchPLOEntryException {

		return _collectionPersistenceFinderByC_L.findFirst(
			finderCache, new Object[] {companyId, languageId},
			orderByComparator);
	}

	/**
	 * Returns the first plo entry in the ordered set where companyId = &#63; and languageId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param languageId the language ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching plo entry, or <code>null</code> if a matching plo entry could not be found
	 */
	@Override
	public PLOEntry fetchByC_L_First(
		long companyId, String languageId,
		OrderByComparator<PLOEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_L.fetchFirst(
			finderCache, new Object[] {companyId, languageId},
			orderByComparator);
	}

	/**
	 * Removes all the plo entries where companyId = &#63; and languageId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param languageId the language ID
	 */
	@Override
	public void removeByC_L(long companyId, String languageId) {
		_collectionPersistenceFinderByC_L.remove(
			finderCache, new Object[] {companyId, languageId});
	}

	/**
	 * Returns the number of plo entries where companyId = &#63; and languageId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param languageId the language ID
	 * @return the number of matching plo entries
	 */
	@Override
	public int countByC_L(long companyId, String languageId) {
		return _collectionPersistenceFinderByC_L.count(
			finderCache, new Object[] {companyId, languageId});
	}

	private UniquePersistenceFinder<PLOEntry, NoSuchPLOEntryException>
		_uniquePersistenceFinderByC_K_L;

	/**
	 * Returns the plo entry where companyId = &#63; and key = &#63; and languageId = &#63; or throws a <code>NoSuchPLOEntryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @param languageId the language ID
	 * @return the matching plo entry
	 * @throws NoSuchPLOEntryException if a matching plo entry could not be found
	 */
	@Override
	public PLOEntry findByC_K_L(long companyId, String key, String languageId)
		throws NoSuchPLOEntryException {

		return _uniquePersistenceFinderByC_K_L.find(
			finderCache, new Object[] {companyId, key, languageId});
	}

	/**
	 * Returns the plo entry where companyId = &#63; and key = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @param languageId the language ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching plo entry, or <code>null</code> if a matching plo entry could not be found
	 */
	@Override
	public PLOEntry fetchByC_K_L(
		long companyId, String key, String languageId, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_K_L.fetch(
			finderCache, new Object[] {companyId, key, languageId},
			useFinderCache);
	}

	/**
	 * Removes the plo entry where companyId = &#63; and key = &#63; and languageId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @param languageId the language ID
	 * @return the plo entry that was removed
	 */
	@Override
	public PLOEntry removeByC_K_L(long companyId, String key, String languageId)
		throws NoSuchPLOEntryException {

		PLOEntry ploEntry = findByC_K_L(companyId, key, languageId);

		return remove(ploEntry);
	}

	/**
	 * Returns the number of plo entries where companyId = &#63; and key = &#63; and languageId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param key the key
	 * @param languageId the language ID
	 * @return the number of matching plo entries
	 */
	@Override
	public int countByC_K_L(long companyId, String key, String languageId) {
		return _uniquePersistenceFinderByC_K_L.count(
			finderCache, new Object[] {companyId, key, languageId});
	}

	private UniquePersistenceFinder<PLOEntry, NoSuchPLOEntryException>
		_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the plo entry where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchPLOEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching plo entry
	 * @throws NoSuchPLOEntryException if a matching plo entry could not be found
	 */
	@Override
	public PLOEntry findByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchPLOEntryException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the plo entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching plo entry, or <code>null</code> if a matching plo entry could not be found
	 */
	@Override
	public PLOEntry fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the plo entry where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the plo entry that was removed
	 */
	@Override
	public PLOEntry removeByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchPLOEntryException {

		PLOEntry ploEntry = findByERC_C(externalReferenceCode, companyId);

		return remove(ploEntry);
	}

	/**
	 * Returns the number of plo entries where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching plo entries
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public PLOEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("key", "key_");

		setDBColumnNames(dbColumnNames);

		setModelClass(PLOEntry.class);

		setModelImplClass(PLOEntryImpl.class);
		setModelPKClass(long.class);

		setTable(PLOEntryTable.INSTANCE);
	}

	/**
	 * Creates a new plo entry with the primary key. Does not add the plo entry to the database.
	 *
	 * @param ploEntryId the primary key for the new plo entry
	 * @return the new plo entry
	 */
	@Override
	public PLOEntry create(long ploEntryId) {
		PLOEntry ploEntry = new PLOEntryImpl();

		ploEntry.setNew(true);
		ploEntry.setPrimaryKey(ploEntryId);

		ploEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ploEntry;
	}

	/**
	 * Removes the plo entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ploEntryId the primary key of the plo entry
	 * @return the plo entry that was removed
	 * @throws NoSuchPLOEntryException if a plo entry with the primary key could not be found
	 */
	@Override
	public PLOEntry remove(long ploEntryId) throws NoSuchPLOEntryException {
		return remove((Serializable)ploEntryId);
	}

	@Override
	protected PLOEntry removeImpl(PLOEntry ploEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ploEntry)) {
				ploEntry = (PLOEntry)session.get(
					PLOEntryImpl.class, ploEntry.getPrimaryKeyObj());
			}

			if (ploEntry != null) {
				session.delete(ploEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ploEntry != null) {
			clearCache(ploEntry);
		}

		return ploEntry;
	}

	@Override
	public PLOEntry updateImpl(PLOEntry ploEntry) {
		boolean isNew = ploEntry.isNew();

		if (!(ploEntry instanceof PLOEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ploEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(ploEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ploEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PLOEntry implementation " +
					ploEntry.getClass());
		}

		PLOEntryModelImpl ploEntryModelImpl = (PLOEntryModelImpl)ploEntry;

		if (Validator.isNull(ploEntry.getExternalReferenceCode())) {
			ploEntry.setExternalReferenceCode(
				String.valueOf(ploEntry.getPrimaryKey()));
		}
		else {
			if (!Objects.equals(
					ploEntryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					ploEntry.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = ploEntry.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = ploEntry.getPrimaryKey();
					}

					try {
						ploEntry.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								PLOEntry.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								ploEntry.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			PLOEntry ercPLOEntry = fetchByERC_C(
				ploEntry.getExternalReferenceCode(), ploEntry.getCompanyId());

			if (isNew) {
				if (ercPLOEntry != null) {
					throw new DuplicatePLOEntryExternalReferenceCodeException(
						"Duplicate plo entry with external reference code " +
							ploEntry.getExternalReferenceCode() +
								" and company " + ploEntry.getCompanyId());
				}
			}
			else {
				if ((ercPLOEntry != null) &&
					(ploEntry.getPloEntryId() != ercPLOEntry.getPloEntryId())) {

					throw new DuplicatePLOEntryExternalReferenceCodeException(
						"Duplicate plo entry with external reference code " +
							ploEntry.getExternalReferenceCode() +
								" and company " + ploEntry.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (ploEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				ploEntry.setCreateDate(date);
			}
			else {
				ploEntry.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!ploEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				ploEntry.setModifiedDate(date);
			}
			else {
				ploEntry.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		long userId = GetterUtil.getLong(PrincipalThreadLocal.getName());

		if (userId > 0) {
			long companyId = ploEntry.getCompanyId();

			long groupId = 0;

			long ploEntryId = 0;

			if (!isNew) {
				ploEntryId = ploEntry.getPrimaryKey();
			}

			try {
				ploEntry.setValue(
					SanitizerUtil.sanitize(
						companyId, groupId, userId, PLOEntry.class.getName(),
						ploEntryId, ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
						ploEntry.getValue(), null));
			}
			catch (SanitizerException sanitizerException) {
				throw new SystemException(sanitizerException);
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(ploEntry);
			}
			else {
				ploEntry = (PLOEntry)session.merge(ploEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ploEntry, false);

		if (isNew) {
			ploEntry.setNew(false);
		}

		ploEntry.resetOriginalValues();

		return ploEntry;
	}

	/**
	 * Returns the plo entry with the primary key or throws a <code>NoSuchPLOEntryException</code> if it could not be found.
	 *
	 * @param ploEntryId the primary key of the plo entry
	 * @return the plo entry
	 * @throws NoSuchPLOEntryException if a plo entry with the primary key could not be found
	 */
	@Override
	public PLOEntry findByPrimaryKey(long ploEntryId)
		throws NoSuchPLOEntryException {

		return findByPrimaryKey((Serializable)ploEntryId);
	}

	/**
	 * Returns the plo entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ploEntryId the primary key of the plo entry
	 * @return the plo entry, or <code>null</code> if a plo entry with the primary key could not be found
	 */
	@Override
	public PLOEntry fetchByPrimaryKey(long ploEntryId) {
		return fetchByPrimaryKey((Serializable)ploEntryId);
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
		return "ploEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PLOENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PLOEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the plo entry persistence.
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
				_SQL_SELECT_PLOENTRY_WHERE, _SQL_COUNT_PLOENTRY_WHERE,
				PLOEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"ploEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, PLOEntry::getCompanyId));

		_collectionPersistenceFinderByC_K = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_K",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "key_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_K",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "key_"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_K",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "key_"}, 0, 2, false, null),
			_SQL_SELECT_PLOENTRY_WHERE, _SQL_COUNT_PLOENTRY_WHERE,
			PLOEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"ploEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, PLOEntry::getCompanyId),
			new FinderColumn<>(
				"ploEntry.", "key", "key_", FinderColumn.Type.STRING, "=", true,
				true, PLOEntry::getKey));

		_collectionPersistenceFinderByC_L = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_L",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "languageId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_L",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "languageId"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_L",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "languageId"}, 0, 2, false, null),
			_SQL_SELECT_PLOENTRY_WHERE, _SQL_COUNT_PLOENTRY_WHERE,
			PLOEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"ploEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, PLOEntry::getCompanyId),
			new FinderColumn<>(
				"ploEntry.", "languageId", FinderColumn.Type.STRING, "=", true,
				true, PLOEntry::getLanguageId));

		_uniquePersistenceFinderByC_K_L = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_K_L",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "key_", "languageId"}, 0, 6, false,
				PLOEntry::getCompanyId, convertNullFunction(PLOEntry::getKey),
				convertNullFunction(PLOEntry::getLanguageId)),
			_SQL_SELECT_PLOENTRY_WHERE, "",
			new FinderColumn<>(
				"ploEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, PLOEntry::getCompanyId),
			new FinderColumn<>(
				"ploEntry.", "key", "key_", FinderColumn.Type.STRING, "=", true,
				true, PLOEntry::getKey),
			new FinderColumn<>(
				"ploEntry.", "languageId", FinderColumn.Type.STRING, "=", true,
				true, PLOEntry::getLanguageId));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false, convertNullFunction(PLOEntry::getExternalReferenceCode),
				PLOEntry::getCompanyId),
			_SQL_SELECT_PLOENTRY_WHERE, "",
			new FinderColumn<>(
				"ploEntry.", "externalReferenceCode", FinderColumn.Type.STRING,
				"=", true, true, PLOEntry::getExternalReferenceCode),
			new FinderColumn<>(
				"ploEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, PLOEntry::getCompanyId));

		PLOEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		PLOEntryUtil.setPersistence(null);

		entityCache.removeCache(PLOEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = PLOPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = PLOPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = PLOPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		PLOEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_PLOENTRY =
		"SELECT ploEntry FROM PLOEntry ploEntry";

	private static final String _SQL_SELECT_PLOENTRY_WHERE =
		"SELECT ploEntry FROM PLOEntry ploEntry WHERE ";

	private static final String _SQL_COUNT_PLOENTRY_WHERE =
		"SELECT COUNT(ploEntry) FROM PLOEntry ploEntry WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"key"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-189835021