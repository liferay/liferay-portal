/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.service.persistence.impl;

import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
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
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.trash.exception.NoSuchEntryException;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.model.TrashEntryTable;
import com.liferay.trash.model.impl.TrashEntryImpl;
import com.liferay.trash.model.impl.TrashEntryModelImpl;
import com.liferay.trash.service.persistence.TrashEntryPersistence;
import com.liferay.trash.service.persistence.TrashEntryUtil;
import com.liferay.trash.service.persistence.impl.constants.TrashPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the trash entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = TrashEntryPersistence.class)
public class TrashEntryPersistenceImpl
	extends BasePersistenceImpl<TrashEntry, NoSuchEntryException>
	implements TrashEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>TrashEntryUtil</code> to access the trash entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		TrashEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<TrashEntry, NoSuchEntryException>
		_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the trash entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching trash entries
	 */
	@Override
	public List<TrashEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first trash entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry findByGroupId_First(
			long groupId, OrderByComparator<TrashEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first trash entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry fetchByGroupId_First(
		long groupId, OrderByComparator<TrashEntry> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the trash entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of trash entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching trash entries
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private CollectionPersistenceFinder<TrashEntry, NoSuchEntryException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the trash entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching trash entries
	 */
	@Override
	public List<TrashEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first trash entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry findByCompanyId_First(
			long companyId, OrderByComparator<TrashEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first trash entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry fetchByCompanyId_First(
		long companyId, OrderByComparator<TrashEntry> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the trash entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of trash entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching trash entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder<TrashEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_LtCD;

	/**
	 * Returns all the trash entries where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @return the matching trash entries
	 */
	@Override
	public List<TrashEntry> findByG_LtCD(long groupId, Date createDate) {
		return findByG_LtCD(
			groupId, createDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the trash entries where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @return the range of matching trash entries
	 */
	@Override
	public List<TrashEntry> findByG_LtCD(
		long groupId, Date createDate, int start, int end) {

		return findByG_LtCD(groupId, createDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the trash entries where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching trash entries
	 */
	@Override
	public List<TrashEntry> findByG_LtCD(
		long groupId, Date createDate, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator) {

		return findByG_LtCD(
			groupId, createDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the trash entries where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching trash entries
	 */
	@Override
	public List<TrashEntry> findByG_LtCD(
		long groupId, Date createDate, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_LtCD.find(
			finderCache, new Object[] {groupId, createDate}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first trash entry in the ordered set where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry findByG_LtCD_First(
			long groupId, Date createDate,
			OrderByComparator<TrashEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_LtCD.findFirst(
			finderCache, new Object[] {groupId, createDate}, orderByComparator);
	}

	/**
	 * Returns the first trash entry in the ordered set where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry fetchByG_LtCD_First(
		long groupId, Date createDate,
		OrderByComparator<TrashEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_LtCD.fetchFirst(
			finderCache, new Object[] {groupId, createDate}, orderByComparator);
	}

	/**
	 * Removes all the trash entries where groupId = &#63; and createDate &lt; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 */
	@Override
	public void removeByG_LtCD(long groupId, Date createDate) {
		_collectionPersistenceFinderByG_LtCD.remove(
			finderCache, new Object[] {groupId, createDate});
	}

	/**
	 * Returns the number of trash entries where groupId = &#63; and createDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param createDate the create date
	 * @return the number of matching trash entries
	 */
	@Override
	public int countByG_LtCD(long groupId, Date createDate) {
		return _collectionPersistenceFinderByG_LtCD.count(
			finderCache, new Object[] {groupId, createDate});
	}

	private CollectionPersistenceFinder<TrashEntry, NoSuchEntryException>
		_collectionPersistenceFinderByG_CN;

	/**
	 * Returns an ordered range of all the trash entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching trash entries
	 */
	@Override
	public List<TrashEntry> findByG_CN(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_CN.find(
			finderCache, new Object[] {groupId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first trash entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry findByG_CN_First(
			long groupId, long classNameId,
			OrderByComparator<TrashEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByG_CN.findFirst(
			finderCache, new Object[] {groupId, classNameId},
			orderByComparator);
	}

	/**
	 * Returns the first trash entry in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry fetchByG_CN_First(
		long groupId, long classNameId,
		OrderByComparator<TrashEntry> orderByComparator) {

		return _collectionPersistenceFinderByG_CN.fetchFirst(
			finderCache, new Object[] {groupId, classNameId},
			orderByComparator);
	}

	/**
	 * Removes all the trash entries where groupId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByG_CN(long groupId, long classNameId) {
		_collectionPersistenceFinderByG_CN.remove(
			finderCache, new Object[] {groupId, classNameId});
	}

	/**
	 * Returns the number of trash entries where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching trash entries
	 */
	@Override
	public int countByG_CN(long groupId, long classNameId) {
		return _collectionPersistenceFinderByG_CN.count(
			finderCache, new Object[] {groupId, classNameId});
	}

	private CollectionPersistenceFinder<TrashEntry, NoSuchEntryException>
		_collectionPersistenceFinderByC_CN;

	/**
	 * Returns an ordered range of all the trash entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching trash entries
	 */
	@Override
	public List<TrashEntry> findByC_CN(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_CN.find(
			finderCache, new Object[] {companyId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first trash entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry findByC_CN_First(
			long companyId, long classNameId,
			OrderByComparator<TrashEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByC_CN.findFirst(
			finderCache, new Object[] {companyId, classNameId},
			orderByComparator);
	}

	/**
	 * Returns the first trash entry in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry fetchByC_CN_First(
		long companyId, long classNameId,
		OrderByComparator<TrashEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_CN.fetchFirst(
			finderCache, new Object[] {companyId, classNameId},
			orderByComparator);
	}

	/**
	 * Removes all the trash entries where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByC_CN(long companyId, long classNameId) {
		_collectionPersistenceFinderByC_CN.remove(
			finderCache, new Object[] {companyId, classNameId});
	}

	/**
	 * Returns the number of trash entries where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching trash entries
	 */
	@Override
	public int countByC_CN(long companyId, long classNameId) {
		return _collectionPersistenceFinderByC_CN.count(
			finderCache, new Object[] {companyId, classNameId});
	}

	private UniquePersistenceFinder<TrashEntry, NoSuchEntryException>
		_uniquePersistenceFinderByCN_CPK;

	/**
	 * Returns the trash entry where classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching trash entry
	 * @throws NoSuchEntryException if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry findByCN_CPK(long classNameId, long classPK)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByCN_CPK.find(
			finderCache, new Object[] {classNameId, classPK});
	}

	/**
	 * Returns the trash entry where classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching trash entry, or <code>null</code> if a matching trash entry could not be found
	 */
	@Override
	public TrashEntry fetchByCN_CPK(
		long classNameId, long classPK, boolean useFinderCache) {

		return _uniquePersistenceFinderByCN_CPK.fetch(
			finderCache, new Object[] {classNameId, classPK}, useFinderCache);
	}

	/**
	 * Removes the trash entry where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the trash entry that was removed
	 */
	@Override
	public TrashEntry removeByCN_CPK(long classNameId, long classPK)
		throws NoSuchEntryException {

		TrashEntry trashEntry = findByCN_CPK(classNameId, classPK);

		return remove(trashEntry);
	}

	/**
	 * Returns the number of trash entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching trash entries
	 */
	@Override
	public int countByCN_CPK(long classNameId, long classPK) {
		return _uniquePersistenceFinderByCN_CPK.count(
			finderCache, new Object[] {classNameId, classPK});
	}

	public TrashEntryPersistenceImpl() {
		setModelClass(TrashEntry.class);

		setModelImplClass(TrashEntryImpl.class);
		setModelPKClass(long.class);

		setTable(TrashEntryTable.INSTANCE);
	}

	/**
	 * Creates a new trash entry with the primary key. Does not add the trash entry to the database.
	 *
	 * @param entryId the primary key for the new trash entry
	 * @return the new trash entry
	 */
	@Override
	public TrashEntry create(long entryId) {
		TrashEntry trashEntry = new TrashEntryImpl();

		trashEntry.setNew(true);
		trashEntry.setPrimaryKey(entryId);

		trashEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return trashEntry;
	}

	/**
	 * Removes the trash entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param entryId the primary key of the trash entry
	 * @return the trash entry that was removed
	 * @throws NoSuchEntryException if a trash entry with the primary key could not be found
	 */
	@Override
	public TrashEntry remove(long entryId) throws NoSuchEntryException {
		return remove((Serializable)entryId);
	}

	@Override
	protected TrashEntry removeImpl(TrashEntry trashEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(trashEntry)) {
				trashEntry = (TrashEntry)session.get(
					TrashEntryImpl.class, trashEntry.getPrimaryKeyObj());
			}

			if ((trashEntry != null) &&
				ctPersistenceHelper.isRemove(trashEntry)) {

				session.delete(trashEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (trashEntry != null) {
			clearCache(trashEntry);
		}

		return trashEntry;
	}

	@Override
	public TrashEntry updateImpl(TrashEntry trashEntry) {
		boolean isNew = trashEntry.isNew();

		if (!(trashEntry instanceof TrashEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(trashEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(trashEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in trashEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom TrashEntry implementation " +
					trashEntry.getClass());
		}

		TrashEntryModelImpl trashEntryModelImpl =
			(TrashEntryModelImpl)trashEntry;

		if (isNew && (trashEntry.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				trashEntry.setCreateDate(date);
			}
			else {
				trashEntry.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(trashEntry)) {
				if (!isNew) {
					session.evict(
						TrashEntryImpl.class, trashEntry.getPrimaryKeyObj());
				}

				session.save(trashEntry);
			}
			else {
				trashEntry = (TrashEntry)session.merge(trashEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(trashEntry, false);

		if (isNew) {
			trashEntry.setNew(false);
		}

		trashEntry.resetOriginalValues();

		return trashEntry;
	}

	/**
	 * Returns the trash entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param entryId the primary key of the trash entry
	 * @return the trash entry
	 * @throws NoSuchEntryException if a trash entry with the primary key could not be found
	 */
	@Override
	public TrashEntry findByPrimaryKey(long entryId)
		throws NoSuchEntryException {

		return findByPrimaryKey((Serializable)entryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the trash entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param entryId the primary key of the trash entry
	 * @return the trash entry, or <code>null</code> if a trash entry with the primary key could not be found
	 */
	@Override
	public TrashEntry fetchByPrimaryKey(long entryId) {
		return fetchByPrimaryKey((Serializable)entryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "entryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_TRASHENTRY;
	}

	@Override
	public Set<String> getCTColumnNames(
		CTColumnResolutionType ctColumnResolutionType) {

		return _ctColumnNamesMap.getOrDefault(
			ctColumnResolutionType, Collections.emptySet());
	}

	@Override
	public List<String> getMappingTableNames() {
		return _mappingTableNames;
	}

	@Override
	public Map<String, Integer> getTableColumnsMap() {
		return TrashEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "TrashEntry";
	}

	@Override
	public List<String[]> getUniqueIndexColumnNames() {
		return _uniqueIndexColumnNames;
	}

	private static final Map<CTColumnResolutionType, Set<String>>
		_ctColumnNamesMap = new EnumMap<CTColumnResolutionType, Set<String>>(
			CTColumnResolutionType.class);
	private static final List<String> _mappingTableNames =
		new ArrayList<String>();
	private static final List<String[]> _uniqueIndexColumnNames =
		new ArrayList<String[]>();

	static {
		Set<String> ctControlColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("systemEventSetKey");
		ctMergeColumnNames.add("typeSettings");
		ctMergeColumnNames.add("status");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("entryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"classNameId", "classPK"});
	}

	/**
	 * Initializes the trash entry persistence.
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
				_SQL_SELECT_TRASHENTRY_WHERE, _SQL_COUNT_TRASHENTRY_WHERE,
				TrashEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"trashEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, TrashEntry::getGroupId));

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
				_SQL_SELECT_TRASHENTRY_WHERE, _SQL_COUNT_TRASHENTRY_WHERE,
				TrashEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"trashEntry.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, TrashEntry::getCompanyId));

		_collectionPersistenceFinderByG_LtCD =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_LtCD",
					new String[] {
						Long.class.getName(), Date.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "createDate"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_LtCD",
					new String[] {Long.class.getName(), Date.class.getName()},
					new String[] {"groupId", "createDate"}, false),
				_SQL_SELECT_TRASHENTRY_WHERE, _SQL_COUNT_TRASHENTRY_WHERE,
				TrashEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"trashEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, TrashEntry::getGroupId),
				new FinderColumn<>(
					"trashEntry.", "createDate", FinderColumn.Type.DATE, "<",
					true, true, TrashEntry::getCreateDate));

		_collectionPersistenceFinderByG_CN = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_CN",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_CN",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_CN",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "classNameId"}, false),
			_SQL_SELECT_TRASHENTRY_WHERE, _SQL_COUNT_TRASHENTRY_WHERE,
			TrashEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"trashEntry.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, TrashEntry::getGroupId),
			new FinderColumn<>(
				"trashEntry.", "classNameId", FinderColumn.Type.LONG, "=", true,
				true, TrashEntry::getClassNameId));

		_collectionPersistenceFinderByC_CN = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_CN",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_CN",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_CN",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "classNameId"}, false),
			_SQL_SELECT_TRASHENTRY_WHERE, _SQL_COUNT_TRASHENTRY_WHERE,
			TrashEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"trashEntry.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, TrashEntry::getCompanyId),
			new FinderColumn<>(
				"trashEntry.", "classNameId", FinderColumn.Type.LONG, "=", true,
				true, TrashEntry::getClassNameId));

		_uniquePersistenceFinderByCN_CPK = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByCN_CPK",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, 0, 0, false,
				TrashEntry::getClassNameId, TrashEntry::getClassPK),
			_SQL_SELECT_TRASHENTRY_WHERE, "",
			new FinderColumn<>(
				"trashEntry.", "classNameId", FinderColumn.Type.LONG, "=", true,
				true, TrashEntry::getClassNameId),
			new FinderColumn<>(
				"trashEntry.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, TrashEntry::getClassPK));

		TrashEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		TrashEntryUtil.setPersistence(null);

		entityCache.removeCache(TrashEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = TrashPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = TrashPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = TrashPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		TrashEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_TRASHENTRY =
		"SELECT trashEntry FROM TrashEntry trashEntry";

	private static final String _SQL_SELECT_TRASHENTRY_WHERE =
		"SELECT trashEntry FROM TrashEntry trashEntry WHERE ";

	private static final String _SQL_COUNT_TRASHENTRY_WHERE =
		"SELECT COUNT(trashEntry) FROM TrashEntry trashEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No TrashEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		TrashEntryPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1140305217