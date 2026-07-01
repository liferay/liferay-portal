/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.changeset.service.persistence.impl;

import com.liferay.changeset.exception.NoSuchEntryException;
import com.liferay.changeset.model.ChangesetEntry;
import com.liferay.changeset.model.ChangesetEntryTable;
import com.liferay.changeset.model.impl.ChangesetEntryImpl;
import com.liferay.changeset.model.impl.ChangesetEntryModelImpl;
import com.liferay.changeset.service.persistence.ChangesetEntryPersistence;
import com.liferay.changeset.service.persistence.ChangesetEntryUtil;
import com.liferay.changeset.service.persistence.impl.constants.ChangesetPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

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
 * The persistence implementation for the changeset entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = ChangesetEntryPersistence.class)
public class ChangesetEntryPersistenceImpl
	extends BasePersistenceImpl<ChangesetEntry, NoSuchEntryException>
	implements ChangesetEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ChangesetEntryUtil</code> to access the changeset entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ChangesetEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<ChangesetEntry, NoSuchEntryException>
		_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the changeset entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ChangesetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of changeset entries
	 * @param end the upper bound of the range of changeset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching changeset entries
	 */
	@Override
	public List<ChangesetEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<ChangesetEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first changeset entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching changeset entry
	 * @throws NoSuchEntryException if a matching changeset entry could not be found
	 */
	@Override
	public ChangesetEntry findByGroupId_First(
			long groupId, OrderByComparator<ChangesetEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first changeset entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching changeset entry, or <code>null</code> if a matching changeset entry could not be found
	 */
	@Override
	public ChangesetEntry fetchByGroupId_First(
		long groupId, OrderByComparator<ChangesetEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the changeset entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of changeset entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching changeset entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private CollectionPersistenceFinder<ChangesetEntry, NoSuchEntryException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the changeset entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ChangesetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of changeset entries
	 * @param end the upper bound of the range of changeset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching changeset entries
	 */
	@Override
	public List<ChangesetEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<ChangesetEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first changeset entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching changeset entry
	 * @throws NoSuchEntryException if a matching changeset entry could not be found
	 */
	@Override
	public ChangesetEntry findByCompanyId_First(
			long companyId, OrderByComparator<ChangesetEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first changeset entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching changeset entry, or <code>null</code> if a matching changeset entry could not be found
	 */
	@Override
	public ChangesetEntry fetchByCompanyId_First(
		long companyId, OrderByComparator<ChangesetEntry> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the changeset entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of changeset entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching changeset entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder<ChangesetEntry, NoSuchEntryException>
		_collectionPersistenceFinderByChangesetCollectionId;

	/**
	 * Returns an ordered range of all the changeset entries where changesetCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ChangesetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param start the lower bound of the range of changeset entries
	 * @param end the upper bound of the range of changeset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching changeset entries
	 */
	@Override
	public List<ChangesetEntry> findByChangesetCollectionId(
		long changesetCollectionId, int start, int end,
		OrderByComparator<ChangesetEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByChangesetCollectionId.find(
			finderCache, new Object[] {changesetCollectionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first changeset entry in the ordered set where changesetCollectionId = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching changeset entry
	 * @throws NoSuchEntryException if a matching changeset entry could not be found
	 */
	@Override
	public ChangesetEntry findByChangesetCollectionId_First(
			long changesetCollectionId,
			OrderByComparator<ChangesetEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByChangesetCollectionId.findFirst(
			finderCache, new Object[] {changesetCollectionId},
			orderByComparator);
	}

	/**
	 * Returns the first changeset entry in the ordered set where changesetCollectionId = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching changeset entry, or <code>null</code> if a matching changeset entry could not be found
	 */
	@Override
	public ChangesetEntry fetchByChangesetCollectionId_First(
		long changesetCollectionId,
		OrderByComparator<ChangesetEntry> orderByComparator) {

		return _collectionPersistenceFinderByChangesetCollectionId.fetchFirst(
			finderCache, new Object[] {changesetCollectionId},
			orderByComparator);
	}

	/**
	 * Removes all the changeset entries where changesetCollectionId = &#63; from the database.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 */
	@Override
	public void removeByChangesetCollectionId(long changesetCollectionId) {
		_collectionPersistenceFinderByChangesetCollectionId.remove(
			finderCache, new Object[] {changesetCollectionId});
	}

	/**
	 * Returns the number of changeset entries where changesetCollectionId = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @return the number of matching changeset entries
	 */
	@Override
	public int countByChangesetCollectionId(long changesetCollectionId) {
		return _collectionPersistenceFinderByChangesetCollectionId.count(
			finderCache, new Object[] {changesetCollectionId});
	}

	private CollectionPersistenceFinder<ChangesetEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_C;

	/**
	 * Returns an ordered range of all the changeset entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ChangesetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of changeset entries
	 * @param end the upper bound of the range of changeset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching changeset entries
	 */
	@Override
	public List<ChangesetEntry> findByG_C(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<ChangesetEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C.find(
			finderCache, new Object[] {groupId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first changeset entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching changeset entry
	 * @throws NoSuchEntryException if a matching changeset entry could not be found
	 */
	@Override
	public ChangesetEntry findByG_C_First(
			long groupId, long classNameId,
			OrderByComparator<ChangesetEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_C.findFirst(
			finderCache, new Object[] {groupId, classNameId},
			orderByComparator);
	}

	/**
	 * Returns the first changeset entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching changeset entry, or <code>null</code> if a matching changeset entry could not be found
	 */
	@Override
	public ChangesetEntry fetchByG_C_First(
		long groupId, long classNameId,
		OrderByComparator<ChangesetEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_C.fetchFirst(
			finderCache, new Object[] {groupId, classNameId},
			orderByComparator);
	}

	/**
	 * Removes all the changeset entries where groupId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByG_C(long groupId, long classNameId) {
		_collectionPersistenceFinderByG_C.remove(
			finderCache, new Object[] {groupId, classNameId});
	}

	/**
	 * Returns the number of changeset entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching changeset entries
	 */
	@Override
	public int countByG_C(long groupId, long classNameId) {
		return _collectionPersistenceFinderByG_C.count(
			finderCache, new Object[] {groupId, classNameId});
	}

	private CollectionPersistenceFinder<ChangesetEntry, NoSuchEntryException>
		_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the changeset entries where changesetCollectionId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ChangesetEntryModelImpl</code>.
	 * </p>
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of changeset entries
	 * @param end the upper bound of the range of changeset entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching changeset entries
	 */
	@Override
	public List<ChangesetEntry> findByC_C(
		long changesetCollectionId, long classNameId, int start, int end,
		OrderByComparator<ChangesetEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache, new Object[] {changesetCollectionId, classNameId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first changeset entry in the ordered set where changesetCollectionId = &#63; and classNameId = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching changeset entry
	 * @throws NoSuchEntryException if a matching changeset entry could not be found
	 */
	@Override
	public ChangesetEntry findByC_C_First(
			long changesetCollectionId, long classNameId,
			OrderByComparator<ChangesetEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache, new Object[] {changesetCollectionId, classNameId},
			orderByComparator);
	}

	/**
	 * Returns the first changeset entry in the ordered set where changesetCollectionId = &#63; and classNameId = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching changeset entry, or <code>null</code> if a matching changeset entry could not be found
	 */
	@Override
	public ChangesetEntry fetchByC_C_First(
		long changesetCollectionId, long classNameId,
		OrderByComparator<ChangesetEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache, new Object[] {changesetCollectionId, classNameId},
			orderByComparator);
	}

	/**
	 * Removes all the changeset entries where changesetCollectionId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByC_C(long changesetCollectionId, long classNameId) {
		_collectionPersistenceFinderByC_C.remove(
			finderCache, new Object[] {changesetCollectionId, classNameId});
	}

	/**
	 * Returns the number of changeset entries where changesetCollectionId = &#63; and classNameId = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @return the number of matching changeset entries
	 */
	@Override
	public int countByC_C(long changesetCollectionId, long classNameId) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache, new Object[] {changesetCollectionId, classNameId});
	}

	private UniquePersistenceFinder<ChangesetEntry, NoSuchEntryException>
		_uniquePersistenceFinderByC_CERC_C;

	/**
	 * Returns the changeset entry where changesetCollectionId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @return the matching changeset entry
	 * @throws NoSuchEntryException if a matching changeset entry could not be found
	 */
	@Override
	public ChangesetEntry findByC_CERC_C(
			long changesetCollectionId, String classExternalReferenceCode,
			long classNameId)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByC_CERC_C.find(
			finderCache,
			new Object[] {
				changesetCollectionId, classExternalReferenceCode, classNameId
			});
	}

	/**
	 * Returns the changeset entry where changesetCollectionId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching changeset entry, or <code>null</code> if a matching changeset entry could not be found
	 */
	@Override
	public ChangesetEntry fetchByC_CERC_C(
		long changesetCollectionId, String classExternalReferenceCode,
		long classNameId, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_CERC_C.fetch(
			finderCache,
			new Object[] {
				changesetCollectionId, classExternalReferenceCode, classNameId
			},
			useFinderCache);
	}

	/**
	 * Removes the changeset entry where changesetCollectionId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63; from the database.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @return the changeset entry that was removed
	 */
	@Override
	public ChangesetEntry removeByC_CERC_C(
			long changesetCollectionId, String classExternalReferenceCode,
			long classNameId)
		throws NoSuchEntryException {

		ChangesetEntry changesetEntry = findByC_CERC_C(
			changesetCollectionId, classExternalReferenceCode, classNameId);

		return remove(changesetEntry);
	}

	/**
	 * Returns the number of changeset entries where changesetCollectionId = &#63; and classExternalReferenceCode = &#63; and classNameId = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classExternalReferenceCode the class external reference code
	 * @param classNameId the class name ID
	 * @return the number of matching changeset entries
	 */
	@Override
	public int countByC_CERC_C(
		long changesetCollectionId, String classExternalReferenceCode,
		long classNameId) {

		return _uniquePersistenceFinderByC_CERC_C.count(
			finderCache,
			new Object[] {
				changesetCollectionId, classExternalReferenceCode, classNameId
			});
	}

	private UniquePersistenceFinder<ChangesetEntry, NoSuchEntryException>
		_uniquePersistenceFinderByC_C_C;

	/**
	 * Returns the changeset entry where changesetCollectionId = &#63; and classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching changeset entry
	 * @throws NoSuchEntryException if a matching changeset entry could not be found
	 */
	@Override
	public ChangesetEntry findByC_C_C(
			long changesetCollectionId, long classNameId, long classPK)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByC_C_C.find(
			finderCache,
			new Object[] {changesetCollectionId, classNameId, classPK});
	}

	/**
	 * Returns the changeset entry where changesetCollectionId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching changeset entry, or <code>null</code> if a matching changeset entry could not be found
	 */
	@Override
	public ChangesetEntry fetchByC_C_C(
		long changesetCollectionId, long classNameId, long classPK,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C_C.fetch(
			finderCache,
			new Object[] {changesetCollectionId, classNameId, classPK},
			useFinderCache);
	}

	/**
	 * Removes the changeset entry where changesetCollectionId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the changeset entry that was removed
	 */
	@Override
	public ChangesetEntry removeByC_C_C(
			long changesetCollectionId, long classNameId, long classPK)
		throws NoSuchEntryException {

		ChangesetEntry changesetEntry = findByC_C_C(
			changesetCollectionId, classNameId, classPK);

		return remove(changesetEntry);
	}

	/**
	 * Returns the number of changeset entries where changesetCollectionId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param changesetCollectionId the changeset collection ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching changeset entries
	 */
	@Override
	public int countByC_C_C(
		long changesetCollectionId, long classNameId, long classPK) {

		return _uniquePersistenceFinderByC_C_C.count(
			finderCache,
			new Object[] {changesetCollectionId, classNameId, classPK});
	}

	public ChangesetEntryPersistenceImpl() {
		setModelClass(ChangesetEntry.class);

		setModelImplClass(ChangesetEntryImpl.class);
		setModelPKClass(long.class);

		setTable(ChangesetEntryTable.INSTANCE);
	}

	/**
	 * Creates a new changeset entry with the primary key. Does not add the changeset entry to the database.
	 *
	 * @param changesetEntryId the primary key for the new changeset entry
	 * @return the new changeset entry
	 */
	@Override
	public ChangesetEntry create(long changesetEntryId) {
		ChangesetEntry changesetEntry = new ChangesetEntryImpl();

		changesetEntry.setNew(true);
		changesetEntry.setPrimaryKey(changesetEntryId);

		changesetEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return changesetEntry;
	}

	/**
	 * Removes the changeset entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param changesetEntryId the primary key of the changeset entry
	 * @return the changeset entry that was removed
	 * @throws NoSuchEntryException if a changeset entry with the primary key could not be found
	 */
	@Override
	public ChangesetEntry remove(long changesetEntryId)
		throws NoSuchEntryException {

		return remove((Serializable)changesetEntryId);
	}

	@Override
	protected ChangesetEntry removeImpl(ChangesetEntry changesetEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(changesetEntry)) {
				changesetEntry = (ChangesetEntry)session.get(
					ChangesetEntryImpl.class,
					changesetEntry.getPrimaryKeyObj());
			}

			if (changesetEntry != null) {
				session.delete(changesetEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (changesetEntry != null) {
			clearCache(changesetEntry);
		}

		return changesetEntry;
	}

	@Override
	public ChangesetEntry updateImpl(ChangesetEntry changesetEntry) {
		boolean isNew = changesetEntry.isNew();

		if (!(changesetEntry instanceof ChangesetEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(changesetEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					changesetEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in changesetEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ChangesetEntry implementation " +
					changesetEntry.getClass());
		}

		ChangesetEntryModelImpl changesetEntryModelImpl =
			(ChangesetEntryModelImpl)changesetEntry;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (changesetEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				changesetEntry.setCreateDate(date);
			}
			else {
				changesetEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!changesetEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				changesetEntry.setModifiedDate(date);
			}
			else {
				changesetEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(changesetEntry);
			}
			else {
				changesetEntry = (ChangesetEntry)session.merge(changesetEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(changesetEntry, false);

		if (isNew) {
			changesetEntry.setNew(false);
		}

		changesetEntry.resetOriginalValues();

		return changesetEntry;
	}

	/**
	 * Returns the changeset entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param changesetEntryId the primary key of the changeset entry
	 * @return the changeset entry
	 * @throws NoSuchEntryException if a changeset entry with the primary key could not be found
	 */
	@Override
	public ChangesetEntry findByPrimaryKey(long changesetEntryId)
		throws NoSuchEntryException {

		return findByPrimaryKey((Serializable)changesetEntryId);
	}

	/**
	 * Returns the changeset entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param changesetEntryId the primary key of the changeset entry
	 * @return the changeset entry, or <code>null</code> if a changeset entry with the primary key could not be found
	 */
	@Override
	public ChangesetEntry fetchByPrimaryKey(long changesetEntryId) {
		return fetchByPrimaryKey((Serializable)changesetEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "changesetEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CHANGESETENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ChangesetEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the changeset entry persistence.
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
				_SQL_SELECT_CHANGESETENTRY_WHERE,
				_SQL_COUNT_CHANGESETENTRY_WHERE,
				ChangesetEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"changesetEntry.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, ChangesetEntry::getGroupId));

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
				_SQL_SELECT_CHANGESETENTRY_WHERE,
				_SQL_COUNT_CHANGESETENTRY_WHERE,
				ChangesetEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"changesetEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, ChangesetEntry::getCompanyId));

		_collectionPersistenceFinderByChangesetCollectionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByChangesetCollectionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"changesetCollectionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByChangesetCollectionId",
					new String[] {Long.class.getName()},
					new String[] {"changesetCollectionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByChangesetCollectionId",
					new String[] {Long.class.getName()},
					new String[] {"changesetCollectionId"}, false),
				_SQL_SELECT_CHANGESETENTRY_WHERE,
				_SQL_COUNT_CHANGESETENTRY_WHERE,
				ChangesetEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"changesetEntry.", "changesetCollectionId",
					FinderColumn.Type.LONG, "=", true, true,
					ChangesetEntry::getChangesetCollectionId));

		_collectionPersistenceFinderByG_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "classNameId"}, false),
			_SQL_SELECT_CHANGESETENTRY_WHERE, _SQL_COUNT_CHANGESETENTRY_WHERE,
			ChangesetEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"changesetEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, ChangesetEntry::getGroupId),
			new FinderColumn<>(
				"changesetEntry.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, ChangesetEntry::getClassNameId));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"changesetCollectionId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"changesetCollectionId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"changesetCollectionId", "classNameId"}, false),
			_SQL_SELECT_CHANGESETENTRY_WHERE, _SQL_COUNT_CHANGESETENTRY_WHERE,
			ChangesetEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"changesetEntry.", "changesetCollectionId",
				FinderColumn.Type.LONG, "=", true, true,
				ChangesetEntry::getChangesetCollectionId),
			new FinderColumn<>(
				"changesetEntry.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, ChangesetEntry::getClassNameId));

		_uniquePersistenceFinderByC_CERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_CERC_C",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Long.class.getName()
				},
				new String[] {
					"changesetCollectionId", "classExternalReferenceCode",
					"classNameId"
				},
				0, 2, false, ChangesetEntry::getChangesetCollectionId,
				convertNullFunction(
					ChangesetEntry::getClassExternalReferenceCode),
				ChangesetEntry::getClassNameId),
			_SQL_SELECT_CHANGESETENTRY_WHERE, "",
			new FinderColumn<>(
				"changesetEntry.", "changesetCollectionId",
				FinderColumn.Type.LONG, "=", true, true,
				ChangesetEntry::getChangesetCollectionId),
			new FinderColumn<>(
				"changesetEntry.", "classExternalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				ChangesetEntry::getClassExternalReferenceCode),
			new FinderColumn<>(
				"changesetEntry.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, ChangesetEntry::getClassNameId));

		_uniquePersistenceFinderByC_C_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {
					"changesetCollectionId", "classNameId", "classPK"
				},
				0, 0, false, ChangesetEntry::getChangesetCollectionId,
				ChangesetEntry::getClassNameId, ChangesetEntry::getClassPK),
			_SQL_SELECT_CHANGESETENTRY_WHERE, "",
			new FinderColumn<>(
				"changesetEntry.", "changesetCollectionId",
				FinderColumn.Type.LONG, "=", true, true,
				ChangesetEntry::getChangesetCollectionId),
			new FinderColumn<>(
				"changesetEntry.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, ChangesetEntry::getClassNameId),
			new FinderColumn<>(
				"changesetEntry.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, ChangesetEntry::getClassPK));

		ChangesetEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		ChangesetEntryUtil.setPersistence(null);

		entityCache.removeCache(ChangesetEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = ChangesetPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = ChangesetPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = ChangesetPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		ChangesetEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CHANGESETENTRY =
		"SELECT changesetEntry FROM ChangesetEntry changesetEntry";

	private static final String _SQL_SELECT_CHANGESETENTRY_WHERE =
		"SELECT changesetEntry FROM ChangesetEntry changesetEntry WHERE ";

	private static final String _SQL_COUNT_CHANGESETENTRY_WHERE =
		"SELECT COUNT(changesetEntry) FROM ChangesetEntry changesetEntry WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1869906317