/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.microblogs.service.persistence.impl;

import com.liferay.microblogs.exception.NoSuchEntryException;
import com.liferay.microblogs.model.MicroblogsEntry;
import com.liferay.microblogs.model.MicroblogsEntryTable;
import com.liferay.microblogs.model.impl.MicroblogsEntryImpl;
import com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl;
import com.liferay.microblogs.service.persistence.MicroblogsEntryPersistence;
import com.liferay.microblogs.service.persistence.MicroblogsEntryUtil;
import com.liferay.microblogs.service.persistence.impl.constants.MicroblogsPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the microblogs entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = MicroblogsEntryPersistence.class)
public class MicroblogsEntryPersistenceImpl
	extends BasePersistenceImpl<MicroblogsEntry, NoSuchEntryException>
	implements MicroblogsEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>MicroblogsEntryUtil</code> to access the microblogs entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		MicroblogsEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<MicroblogsEntry, NoSuchEntryException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the microblogs entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching microblogs entries
	 */
	@Override
	public List<MicroblogsEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<MicroblogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first microblogs entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry
	 * @throws NoSuchEntryException if a matching microblogs entry could not be found
	 */
	@Override
	public MicroblogsEntry findByCompanyId_First(
			long companyId,
			OrderByComparator<MicroblogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first microblogs entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry, or <code>null</code> if a matching microblogs entry could not be found
	 */
	@Override
	public MicroblogsEntry fetchByCompanyId_First(
		long companyId, OrderByComparator<MicroblogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the microblogs entries that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries that the user has permission to view
	 */
	@Override
	public List<MicroblogsEntry> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<MicroblogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.filterFind(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the microblogs entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of microblogs entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching microblogs entries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of microblogs entries that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching microblogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.filterCount(
			finderCache, new Object[] {companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<MicroblogsEntry, NoSuchEntryException>
			_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the microblogs entries where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching microblogs entries
	 */
	@Override
	public List<MicroblogsEntry> findByUserId(
		long userId, int start, int end,
		OrderByComparator<MicroblogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			finderCache, new Object[] {userId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first microblogs entry in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry
	 * @throws NoSuchEntryException if a matching microblogs entry could not be found
	 */
	@Override
	public MicroblogsEntry findByUserId_First(
			long userId, OrderByComparator<MicroblogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByUserId.findFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Returns the first microblogs entry in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry, or <code>null</code> if a matching microblogs entry could not be found
	 */
	@Override
	public MicroblogsEntry fetchByUserId_First(
		long userId, OrderByComparator<MicroblogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the microblogs entries that the user has permissions to view where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries that the user has permission to view
	 */
	@Override
	public List<MicroblogsEntry> filterFindByUserId(
		long userId, int start, int end,
		OrderByComparator<MicroblogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByUserId.filterFind(
			finderCache, new Object[] {userId}, start, end, orderByComparator);
	}

	/**
	 * Removes all the microblogs entries where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			finderCache, new Object[] {userId});
	}

	/**
	 * Returns the number of microblogs entries where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching microblogs entries
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			finderCache, new Object[] {userId});
	}

	/**
	 * Returns the number of microblogs entries that the user has permission to view where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching microblogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.filterCount(
			finderCache, new Object[] {userId});
	}

	private FilterCollectionPersistenceFinder
		<MicroblogsEntry, NoSuchEntryException>
			_collectionPersistenceFinderByU_T;

	/**
	 * Returns an ordered range of all the microblogs entries where userId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching microblogs entries
	 */
	@Override
	public List<MicroblogsEntry> findByU_T(
		long userId, int type, int start, int end,
		OrderByComparator<MicroblogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_T.find(
			finderCache, new Object[] {userId, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first microblogs entry in the ordered set where userId = &#63; and type = &#63;.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry
	 * @throws NoSuchEntryException if a matching microblogs entry could not be found
	 */
	@Override
	public MicroblogsEntry findByU_T_First(
			long userId, int type,
			OrderByComparator<MicroblogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByU_T.findFirst(
			finderCache, new Object[] {userId, type}, orderByComparator);
	}

	/**
	 * Returns the first microblogs entry in the ordered set where userId = &#63; and type = &#63;.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry, or <code>null</code> if a matching microblogs entry could not be found
	 */
	@Override
	public MicroblogsEntry fetchByU_T_First(
		long userId, int type,
		OrderByComparator<MicroblogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByU_T.fetchFirst(
			finderCache, new Object[] {userId, type}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the microblogs entries that the user has permissions to view where userId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries that the user has permission to view
	 */
	@Override
	public List<MicroblogsEntry> filterFindByU_T(
		long userId, int type, int start, int end,
		OrderByComparator<MicroblogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByU_T.filterFind(
			finderCache, new Object[] {userId, type}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the microblogs entries where userId = &#63; and type = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param type the type
	 */
	@Override
	public void removeByU_T(long userId, int type) {
		_collectionPersistenceFinderByU_T.remove(
			finderCache, new Object[] {userId, type});
	}

	/**
	 * Returns the number of microblogs entries where userId = &#63; and type = &#63;.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @return the number of matching microblogs entries
	 */
	@Override
	public int countByU_T(long userId, int type) {
		return _collectionPersistenceFinderByU_T.count(
			finderCache, new Object[] {userId, type});
	}

	/**
	 * Returns the number of microblogs entries that the user has permission to view where userId = &#63; and type = &#63;.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @return the number of matching microblogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByU_T(long userId, int type) {
		return _collectionPersistenceFinderByU_T.filterCount(
			finderCache, new Object[] {userId, type});
	}

	private FilterCollectionPersistenceFinder
		<MicroblogsEntry, NoSuchEntryException>
			_collectionPersistenceFinderByCCNI_CCPK;

	/**
	 * Returns an ordered range of all the microblogs entries where creatorClassNameId = &#63; and creatorClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching microblogs entries
	 */
	@Override
	public List<MicroblogsEntry> findByCCNI_CCPK(
		long creatorClassNameId, long creatorClassPK, int start, int end,
		OrderByComparator<MicroblogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCCNI_CCPK.find(
			finderCache,
			new Object[] {creatorClassNameId, new long[] {creatorClassPK}},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first microblogs entry in the ordered set where creatorClassNameId = &#63; and creatorClassPK = &#63;.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry
	 * @throws NoSuchEntryException if a matching microblogs entry could not be found
	 */
	@Override
	public MicroblogsEntry findByCCNI_CCPK_First(
			long creatorClassNameId, long creatorClassPK,
			OrderByComparator<MicroblogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByCCNI_CCPK.findFirst(
			finderCache,
			new Object[] {creatorClassNameId, new long[] {creatorClassPK}},
			orderByComparator);
	}

	/**
	 * Returns the first microblogs entry in the ordered set where creatorClassNameId = &#63; and creatorClassPK = &#63;.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry, or <code>null</code> if a matching microblogs entry could not be found
	 */
	@Override
	public MicroblogsEntry fetchByCCNI_CCPK_First(
		long creatorClassNameId, long creatorClassPK,
		OrderByComparator<MicroblogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByCCNI_CCPK.fetchFirst(
			finderCache,
			new Object[] {creatorClassNameId, new long[] {creatorClassPK}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the microblogs entries that the user has permissions to view where creatorClassNameId = &#63; and creatorClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries that the user has permission to view
	 */
	@Override
	public List<MicroblogsEntry> filterFindByCCNI_CCPK(
		long creatorClassNameId, long creatorClassPK, int start, int end,
		OrderByComparator<MicroblogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByCCNI_CCPK.filterFind(
			finderCache,
			new Object[] {creatorClassNameId, new long[] {creatorClassPK}},
			start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the microblogs entries that the user has permission to view where creatorClassNameId = &#63; and creatorClassPK = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPKs the creator class pks
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries that the user has permission to view
	 */
	@Override
	public List<MicroblogsEntry> filterFindByCCNI_CCPK(
		long creatorClassNameId, long[] creatorClassPKs, int start, int end,
		OrderByComparator<MicroblogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByCCNI_CCPK.filterFind(
			finderCache,
			new Object[] {
				creatorClassNameId, ArrayUtil.sortedUnique(creatorClassPKs)
			},
			start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the microblogs entries where creatorClassNameId = &#63; and creatorClassPK = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPKs the creator class pks
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching microblogs entries
	 */
	@Override
	public List<MicroblogsEntry> findByCCNI_CCPK(
		long creatorClassNameId, long[] creatorClassPKs, int start, int end,
		OrderByComparator<MicroblogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCCNI_CCPK.find(
			finderCache,
			new Object[] {
				creatorClassNameId, ArrayUtil.sortedUnique(creatorClassPKs)
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the microblogs entries where creatorClassNameId = &#63; and creatorClassPK = &#63; from the database.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 */
	@Override
	public void removeByCCNI_CCPK(
		long creatorClassNameId, long creatorClassPK) {

		_collectionPersistenceFinderByCCNI_CCPK.remove(
			finderCache,
			new Object[] {creatorClassNameId, new long[] {creatorClassPK}});
	}

	/**
	 * Returns the number of microblogs entries where creatorClassNameId = &#63; and creatorClassPK = &#63;.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @return the number of matching microblogs entries
	 */
	@Override
	public int countByCCNI_CCPK(long creatorClassNameId, long creatorClassPK) {
		return _collectionPersistenceFinderByCCNI_CCPK.count(
			finderCache,
			new Object[] {creatorClassNameId, new long[] {creatorClassPK}});
	}

	/**
	 * Returns the number of microblogs entries where creatorClassNameId = &#63; and creatorClassPK = any &#63;.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPKs the creator class pks
	 * @return the number of matching microblogs entries
	 */
	@Override
	public int countByCCNI_CCPK(
		long creatorClassNameId, long[] creatorClassPKs) {

		return _collectionPersistenceFinderByCCNI_CCPK.count(
			finderCache,
			new Object[] {
				creatorClassNameId, ArrayUtil.sortedUnique(creatorClassPKs)
			});
	}

	/**
	 * Returns the number of microblogs entries that the user has permission to view where creatorClassNameId = &#63; and creatorClassPK = &#63;.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @return the number of matching microblogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByCCNI_CCPK(
		long creatorClassNameId, long creatorClassPK) {

		return _collectionPersistenceFinderByCCNI_CCPK.filterCount(
			finderCache,
			new Object[] {creatorClassNameId, new long[] {creatorClassPK}});
	}

	/**
	 * Returns the number of microblogs entries that the user has permission to view where creatorClassNameId = &#63; and creatorClassPK = any &#63;.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPKs the creator class pks
	 * @return the number of matching microblogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByCCNI_CCPK(
		long creatorClassNameId, long[] creatorClassPKs) {

		return _collectionPersistenceFinderByCCNI_CCPK.filterCount(
			finderCache,
			new Object[] {
				creatorClassNameId, ArrayUtil.sortedUnique(creatorClassPKs)
			});
	}

	private FilterCollectionPersistenceFinder
		<MicroblogsEntry, NoSuchEntryException>
			_collectionPersistenceFinderByCCNI_T;

	/**
	 * Returns an ordered range of all the microblogs entries where creatorClassNameId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching microblogs entries
	 */
	@Override
	public List<MicroblogsEntry> findByCCNI_T(
		long creatorClassNameId, int type, int start, int end,
		OrderByComparator<MicroblogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCCNI_T.find(
			finderCache, new Object[] {creatorClassNameId, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first microblogs entry in the ordered set where creatorClassNameId = &#63; and type = &#63;.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry
	 * @throws NoSuchEntryException if a matching microblogs entry could not be found
	 */
	@Override
	public MicroblogsEntry findByCCNI_T_First(
			long creatorClassNameId, int type,
			OrderByComparator<MicroblogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByCCNI_T.findFirst(
			finderCache, new Object[] {creatorClassNameId, type},
			orderByComparator);
	}

	/**
	 * Returns the first microblogs entry in the ordered set where creatorClassNameId = &#63; and type = &#63;.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry, or <code>null</code> if a matching microblogs entry could not be found
	 */
	@Override
	public MicroblogsEntry fetchByCCNI_T_First(
		long creatorClassNameId, int type,
		OrderByComparator<MicroblogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByCCNI_T.fetchFirst(
			finderCache, new Object[] {creatorClassNameId, type},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the microblogs entries that the user has permissions to view where creatorClassNameId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries that the user has permission to view
	 */
	@Override
	public List<MicroblogsEntry> filterFindByCCNI_T(
		long creatorClassNameId, int type, int start, int end,
		OrderByComparator<MicroblogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByCCNI_T.filterFind(
			finderCache, new Object[] {creatorClassNameId, type}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the microblogs entries where creatorClassNameId = &#63; and type = &#63; from the database.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param type the type
	 */
	@Override
	public void removeByCCNI_T(long creatorClassNameId, int type) {
		_collectionPersistenceFinderByCCNI_T.remove(
			finderCache, new Object[] {creatorClassNameId, type});
	}

	/**
	 * Returns the number of microblogs entries where creatorClassNameId = &#63; and type = &#63;.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param type the type
	 * @return the number of matching microblogs entries
	 */
	@Override
	public int countByCCNI_T(long creatorClassNameId, int type) {
		return _collectionPersistenceFinderByCCNI_T.count(
			finderCache, new Object[] {creatorClassNameId, type});
	}

	/**
	 * Returns the number of microblogs entries that the user has permission to view where creatorClassNameId = &#63; and type = &#63;.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param type the type
	 * @return the number of matching microblogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByCCNI_T(long creatorClassNameId, int type) {
		return _collectionPersistenceFinderByCCNI_T.filterCount(
			finderCache, new Object[] {creatorClassNameId, type});
	}

	private FilterCollectionPersistenceFinder
		<MicroblogsEntry, NoSuchEntryException>
			_collectionPersistenceFinderByT_P;

	/**
	 * Returns an ordered range of all the microblogs entries where type = &#63; and parentMicroblogsEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param parentMicroblogsEntryId the parent microblogs entry ID
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching microblogs entries
	 */
	@Override
	public List<MicroblogsEntry> findByT_P(
		int type, long parentMicroblogsEntryId, int start, int end,
		OrderByComparator<MicroblogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByT_P.find(
			finderCache, new Object[] {type, parentMicroblogsEntryId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first microblogs entry in the ordered set where type = &#63; and parentMicroblogsEntryId = &#63;.
	 *
	 * @param type the type
	 * @param parentMicroblogsEntryId the parent microblogs entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry
	 * @throws NoSuchEntryException if a matching microblogs entry could not be found
	 */
	@Override
	public MicroblogsEntry findByT_P_First(
			int type, long parentMicroblogsEntryId,
			OrderByComparator<MicroblogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByT_P.findFirst(
			finderCache, new Object[] {type, parentMicroblogsEntryId},
			orderByComparator);
	}

	/**
	 * Returns the first microblogs entry in the ordered set where type = &#63; and parentMicroblogsEntryId = &#63;.
	 *
	 * @param type the type
	 * @param parentMicroblogsEntryId the parent microblogs entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry, or <code>null</code> if a matching microblogs entry could not be found
	 */
	@Override
	public MicroblogsEntry fetchByT_P_First(
		int type, long parentMicroblogsEntryId,
		OrderByComparator<MicroblogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByT_P.fetchFirst(
			finderCache, new Object[] {type, parentMicroblogsEntryId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the microblogs entries that the user has permissions to view where type = &#63; and parentMicroblogsEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param parentMicroblogsEntryId the parent microblogs entry ID
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries that the user has permission to view
	 */
	@Override
	public List<MicroblogsEntry> filterFindByT_P(
		int type, long parentMicroblogsEntryId, int start, int end,
		OrderByComparator<MicroblogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByT_P.filterFind(
			finderCache, new Object[] {type, parentMicroblogsEntryId}, start,
			end, orderByComparator);
	}

	/**
	 * Removes all the microblogs entries where type = &#63; and parentMicroblogsEntryId = &#63; from the database.
	 *
	 * @param type the type
	 * @param parentMicroblogsEntryId the parent microblogs entry ID
	 */
	@Override
	public void removeByT_P(int type, long parentMicroblogsEntryId) {
		_collectionPersistenceFinderByT_P.remove(
			finderCache, new Object[] {type, parentMicroblogsEntryId});
	}

	/**
	 * Returns the number of microblogs entries where type = &#63; and parentMicroblogsEntryId = &#63;.
	 *
	 * @param type the type
	 * @param parentMicroblogsEntryId the parent microblogs entry ID
	 * @return the number of matching microblogs entries
	 */
	@Override
	public int countByT_P(int type, long parentMicroblogsEntryId) {
		return _collectionPersistenceFinderByT_P.count(
			finderCache, new Object[] {type, parentMicroblogsEntryId});
	}

	/**
	 * Returns the number of microblogs entries that the user has permission to view where type = &#63; and parentMicroblogsEntryId = &#63;.
	 *
	 * @param type the type
	 * @param parentMicroblogsEntryId the parent microblogs entry ID
	 * @return the number of matching microblogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByT_P(int type, long parentMicroblogsEntryId) {
		return _collectionPersistenceFinderByT_P.filterCount(
			finderCache, new Object[] {type, parentMicroblogsEntryId});
	}

	private FilterCollectionPersistenceFinder
		<MicroblogsEntry, NoSuchEntryException>
			_collectionPersistenceFinderByC_CCNI_CCPK;

	/**
	 * Returns an ordered range of all the microblogs entries where companyId = &#63; and creatorClassNameId = &#63; and creatorClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching microblogs entries
	 */
	@Override
	public List<MicroblogsEntry> findByC_CCNI_CCPK(
		long companyId, long creatorClassNameId, long creatorClassPK, int start,
		int end, OrderByComparator<MicroblogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_CCNI_CCPK.find(
			finderCache,
			new Object[] {
				companyId, creatorClassNameId, new long[] {creatorClassPK}
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first microblogs entry in the ordered set where companyId = &#63; and creatorClassNameId = &#63; and creatorClassPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry
	 * @throws NoSuchEntryException if a matching microblogs entry could not be found
	 */
	@Override
	public MicroblogsEntry findByC_CCNI_CCPK_First(
			long companyId, long creatorClassNameId, long creatorClassPK,
			OrderByComparator<MicroblogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByC_CCNI_CCPK.findFirst(
			finderCache,
			new Object[] {
				companyId, creatorClassNameId, new long[] {creatorClassPK}
			},
			orderByComparator);
	}

	/**
	 * Returns the first microblogs entry in the ordered set where companyId = &#63; and creatorClassNameId = &#63; and creatorClassPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry, or <code>null</code> if a matching microblogs entry could not be found
	 */
	@Override
	public MicroblogsEntry fetchByC_CCNI_CCPK_First(
		long companyId, long creatorClassNameId, long creatorClassPK,
		OrderByComparator<MicroblogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_CCNI_CCPK.fetchFirst(
			finderCache,
			new Object[] {
				companyId, creatorClassNameId, new long[] {creatorClassPK}
			},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the microblogs entries that the user has permissions to view where companyId = &#63; and creatorClassNameId = &#63; and creatorClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries that the user has permission to view
	 */
	@Override
	public List<MicroblogsEntry> filterFindByC_CCNI_CCPK(
		long companyId, long creatorClassNameId, long creatorClassPK, int start,
		int end, OrderByComparator<MicroblogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_CCNI_CCPK.filterFind(
			finderCache,
			new Object[] {
				companyId, creatorClassNameId, new long[] {creatorClassPK}
			},
			start, end, orderByComparator, companyId, 0);
	}

	/**
	 * Returns an ordered range of all the microblogs entries that the user has permission to view where companyId = &#63; and creatorClassNameId = &#63; and creatorClassPK = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPKs the creator class pks
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries that the user has permission to view
	 */
	@Override
	public List<MicroblogsEntry> filterFindByC_CCNI_CCPK(
		long companyId, long creatorClassNameId, long[] creatorClassPKs,
		int start, int end,
		OrderByComparator<MicroblogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_CCNI_CCPK.filterFind(
			finderCache,
			new Object[] {
				companyId, creatorClassNameId,
				ArrayUtil.sortedUnique(creatorClassPKs)
			},
			start, end, orderByComparator, companyId, 0);
	}

	/**
	 * Returns an ordered range of all the microblogs entries where companyId = &#63; and creatorClassNameId = &#63; and creatorClassPK = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPKs the creator class pks
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching microblogs entries
	 */
	@Override
	public List<MicroblogsEntry> findByC_CCNI_CCPK(
		long companyId, long creatorClassNameId, long[] creatorClassPKs,
		int start, int end,
		OrderByComparator<MicroblogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_CCNI_CCPK.find(
			finderCache,
			new Object[] {
				companyId, creatorClassNameId,
				ArrayUtil.sortedUnique(creatorClassPKs)
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the microblogs entries where companyId = &#63; and creatorClassNameId = &#63; and creatorClassPK = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 */
	@Override
	public void removeByC_CCNI_CCPK(
		long companyId, long creatorClassNameId, long creatorClassPK) {

		_collectionPersistenceFinderByC_CCNI_CCPK.remove(
			finderCache,
			new Object[] {
				companyId, creatorClassNameId, new long[] {creatorClassPK}
			});
	}

	/**
	 * Returns the number of microblogs entries where companyId = &#63; and creatorClassNameId = &#63; and creatorClassPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @return the number of matching microblogs entries
	 */
	@Override
	public int countByC_CCNI_CCPK(
		long companyId, long creatorClassNameId, long creatorClassPK) {

		return _collectionPersistenceFinderByC_CCNI_CCPK.count(
			finderCache,
			new Object[] {
				companyId, creatorClassNameId, new long[] {creatorClassPK}
			});
	}

	/**
	 * Returns the number of microblogs entries where companyId = &#63; and creatorClassNameId = &#63; and creatorClassPK = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPKs the creator class pks
	 * @return the number of matching microblogs entries
	 */
	@Override
	public int countByC_CCNI_CCPK(
		long companyId, long creatorClassNameId, long[] creatorClassPKs) {

		return _collectionPersistenceFinderByC_CCNI_CCPK.count(
			finderCache,
			new Object[] {
				companyId, creatorClassNameId,
				ArrayUtil.sortedUnique(creatorClassPKs)
			});
	}

	/**
	 * Returns the number of microblogs entries that the user has permission to view where companyId = &#63; and creatorClassNameId = &#63; and creatorClassPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @return the number of matching microblogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByC_CCNI_CCPK(
		long companyId, long creatorClassNameId, long creatorClassPK) {

		return _collectionPersistenceFinderByC_CCNI_CCPK.filterCount(
			finderCache,
			new Object[] {
				companyId, creatorClassNameId, new long[] {creatorClassPK}
			},
			companyId, 0);
	}

	/**
	 * Returns the number of microblogs entries that the user has permission to view where companyId = &#63; and creatorClassNameId = &#63; and creatorClassPK = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPKs the creator class pks
	 * @return the number of matching microblogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByC_CCNI_CCPK(
		long companyId, long creatorClassNameId, long[] creatorClassPKs) {

		return _collectionPersistenceFinderByC_CCNI_CCPK.filterCount(
			finderCache,
			new Object[] {
				companyId, creatorClassNameId,
				ArrayUtil.sortedUnique(creatorClassPKs)
			},
			companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<MicroblogsEntry, NoSuchEntryException>
			_collectionPersistenceFinderByC_CCNI_T;

	/**
	 * Returns an ordered range of all the microblogs entries where companyId = &#63; and creatorClassNameId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching microblogs entries
	 */
	@Override
	public List<MicroblogsEntry> findByC_CCNI_T(
		long companyId, long creatorClassNameId, int type, int start, int end,
		OrderByComparator<MicroblogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_CCNI_T.find(
			finderCache, new Object[] {companyId, creatorClassNameId, type},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first microblogs entry in the ordered set where companyId = &#63; and creatorClassNameId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry
	 * @throws NoSuchEntryException if a matching microblogs entry could not be found
	 */
	@Override
	public MicroblogsEntry findByC_CCNI_T_First(
			long companyId, long creatorClassNameId, int type,
			OrderByComparator<MicroblogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByC_CCNI_T.findFirst(
			finderCache, new Object[] {companyId, creatorClassNameId, type},
			orderByComparator);
	}

	/**
	 * Returns the first microblogs entry in the ordered set where companyId = &#63; and creatorClassNameId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry, or <code>null</code> if a matching microblogs entry could not be found
	 */
	@Override
	public MicroblogsEntry fetchByC_CCNI_T_First(
		long companyId, long creatorClassNameId, int type,
		OrderByComparator<MicroblogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_CCNI_T.fetchFirst(
			finderCache, new Object[] {companyId, creatorClassNameId, type},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the microblogs entries that the user has permissions to view where companyId = &#63; and creatorClassNameId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries that the user has permission to view
	 */
	@Override
	public List<MicroblogsEntry> filterFindByC_CCNI_T(
		long companyId, long creatorClassNameId, int type, int start, int end,
		OrderByComparator<MicroblogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_CCNI_T.filterFind(
			finderCache, new Object[] {companyId, creatorClassNameId, type},
			start, end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the microblogs entries where companyId = &#63; and creatorClassNameId = &#63; and type = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param type the type
	 */
	@Override
	public void removeByC_CCNI_T(
		long companyId, long creatorClassNameId, int type) {

		_collectionPersistenceFinderByC_CCNI_T.remove(
			finderCache, new Object[] {companyId, creatorClassNameId, type});
	}

	/**
	 * Returns the number of microblogs entries where companyId = &#63; and creatorClassNameId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param type the type
	 * @return the number of matching microblogs entries
	 */
	@Override
	public int countByC_CCNI_T(
		long companyId, long creatorClassNameId, int type) {

		return _collectionPersistenceFinderByC_CCNI_T.count(
			finderCache, new Object[] {companyId, creatorClassNameId, type});
	}

	/**
	 * Returns the number of microblogs entries that the user has permission to view where companyId = &#63; and creatorClassNameId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param type the type
	 * @return the number of matching microblogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByC_CCNI_T(
		long companyId, long creatorClassNameId, int type) {

		return _collectionPersistenceFinderByC_CCNI_T.filterCount(
			finderCache, new Object[] {companyId, creatorClassNameId, type},
			companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<MicroblogsEntry, NoSuchEntryException>
			_collectionPersistenceFinderByCCNI_CCPK_T;

	/**
	 * Returns an ordered range of all the microblogs entries where creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching microblogs entries
	 */
	@Override
	public List<MicroblogsEntry> findByCCNI_CCPK_T(
		long creatorClassNameId, long creatorClassPK, int type, int start,
		int end, OrderByComparator<MicroblogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCCNI_CCPK_T.find(
			finderCache,
			new Object[] {
				creatorClassNameId, new long[] {creatorClassPK}, type
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first microblogs entry in the ordered set where creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63;.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry
	 * @throws NoSuchEntryException if a matching microblogs entry could not be found
	 */
	@Override
	public MicroblogsEntry findByCCNI_CCPK_T_First(
			long creatorClassNameId, long creatorClassPK, int type,
			OrderByComparator<MicroblogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByCCNI_CCPK_T.findFirst(
			finderCache,
			new Object[] {
				creatorClassNameId, new long[] {creatorClassPK}, type
			},
			orderByComparator);
	}

	/**
	 * Returns the first microblogs entry in the ordered set where creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63;.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry, or <code>null</code> if a matching microblogs entry could not be found
	 */
	@Override
	public MicroblogsEntry fetchByCCNI_CCPK_T_First(
		long creatorClassNameId, long creatorClassPK, int type,
		OrderByComparator<MicroblogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByCCNI_CCPK_T.fetchFirst(
			finderCache,
			new Object[] {
				creatorClassNameId, new long[] {creatorClassPK}, type
			},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the microblogs entries that the user has permissions to view where creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries that the user has permission to view
	 */
	@Override
	public List<MicroblogsEntry> filterFindByCCNI_CCPK_T(
		long creatorClassNameId, long creatorClassPK, int type, int start,
		int end, OrderByComparator<MicroblogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByCCNI_CCPK_T.filterFind(
			finderCache,
			new Object[] {
				creatorClassNameId, new long[] {creatorClassPK}, type
			},
			start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the microblogs entries that the user has permission to view where creatorClassNameId = &#63; and creatorClassPK = any &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPKs the creator class pks
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries that the user has permission to view
	 */
	@Override
	public List<MicroblogsEntry> filterFindByCCNI_CCPK_T(
		long creatorClassNameId, long[] creatorClassPKs, int type, int start,
		int end, OrderByComparator<MicroblogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByCCNI_CCPK_T.filterFind(
			finderCache,
			new Object[] {
				creatorClassNameId, ArrayUtil.sortedUnique(creatorClassPKs),
				type
			},
			start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the microblogs entries where creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPKs the creator class pks
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching microblogs entries
	 */
	@Override
	public List<MicroblogsEntry> findByCCNI_CCPK_T(
		long creatorClassNameId, long[] creatorClassPKs, int type, int start,
		int end, OrderByComparator<MicroblogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCCNI_CCPK_T.find(
			finderCache,
			new Object[] {
				creatorClassNameId, ArrayUtil.sortedUnique(creatorClassPKs),
				type
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the microblogs entries where creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63; from the database.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param type the type
	 */
	@Override
	public void removeByCCNI_CCPK_T(
		long creatorClassNameId, long creatorClassPK, int type) {

		_collectionPersistenceFinderByCCNI_CCPK_T.remove(
			finderCache,
			new Object[] {
				creatorClassNameId, new long[] {creatorClassPK}, type
			});
	}

	/**
	 * Returns the number of microblogs entries where creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63;.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param type the type
	 * @return the number of matching microblogs entries
	 */
	@Override
	public int countByCCNI_CCPK_T(
		long creatorClassNameId, long creatorClassPK, int type) {

		return _collectionPersistenceFinderByCCNI_CCPK_T.count(
			finderCache,
			new Object[] {
				creatorClassNameId, new long[] {creatorClassPK}, type
			});
	}

	/**
	 * Returns the number of microblogs entries where creatorClassNameId = &#63; and creatorClassPK = any &#63; and type = &#63;.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPKs the creator class pks
	 * @param type the type
	 * @return the number of matching microblogs entries
	 */
	@Override
	public int countByCCNI_CCPK_T(
		long creatorClassNameId, long[] creatorClassPKs, int type) {

		return _collectionPersistenceFinderByCCNI_CCPK_T.count(
			finderCache,
			new Object[] {
				creatorClassNameId, ArrayUtil.sortedUnique(creatorClassPKs),
				type
			});
	}

	/**
	 * Returns the number of microblogs entries that the user has permission to view where creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63;.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param type the type
	 * @return the number of matching microblogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByCCNI_CCPK_T(
		long creatorClassNameId, long creatorClassPK, int type) {

		return _collectionPersistenceFinderByCCNI_CCPK_T.filterCount(
			finderCache,
			new Object[] {
				creatorClassNameId, new long[] {creatorClassPK}, type
			});
	}

	/**
	 * Returns the number of microblogs entries that the user has permission to view where creatorClassNameId = &#63; and creatorClassPK = any &#63; and type = &#63;.
	 *
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPKs the creator class pks
	 * @param type the type
	 * @return the number of matching microblogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByCCNI_CCPK_T(
		long creatorClassNameId, long[] creatorClassPKs, int type) {

		return _collectionPersistenceFinderByCCNI_CCPK_T.filterCount(
			finderCache,
			new Object[] {
				creatorClassNameId, ArrayUtil.sortedUnique(creatorClassPKs),
				type
			});
	}

	private FilterCollectionPersistenceFinder
		<MicroblogsEntry, NoSuchEntryException>
			_collectionPersistenceFinderByC_CCNI_CCPK_T;

	/**
	 * Returns an ordered range of all the microblogs entries where companyId = &#63; and creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching microblogs entries
	 */
	@Override
	public List<MicroblogsEntry> findByC_CCNI_CCPK_T(
		long companyId, long creatorClassNameId, long creatorClassPK, int type,
		int start, int end,
		OrderByComparator<MicroblogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_CCNI_CCPK_T.find(
			finderCache,
			new Object[] {
				companyId, creatorClassNameId, new long[] {creatorClassPK}, type
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first microblogs entry in the ordered set where companyId = &#63; and creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry
	 * @throws NoSuchEntryException if a matching microblogs entry could not be found
	 */
	@Override
	public MicroblogsEntry findByC_CCNI_CCPK_T_First(
			long companyId, long creatorClassNameId, long creatorClassPK,
			int type, OrderByComparator<MicroblogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByC_CCNI_CCPK_T.findFirst(
			finderCache,
			new Object[] {
				companyId, creatorClassNameId, new long[] {creatorClassPK}, type
			},
			orderByComparator);
	}

	/**
	 * Returns the first microblogs entry in the ordered set where companyId = &#63; and creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry, or <code>null</code> if a matching microblogs entry could not be found
	 */
	@Override
	public MicroblogsEntry fetchByC_CCNI_CCPK_T_First(
		long companyId, long creatorClassNameId, long creatorClassPK, int type,
		OrderByComparator<MicroblogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_CCNI_CCPK_T.fetchFirst(
			finderCache,
			new Object[] {
				companyId, creatorClassNameId, new long[] {creatorClassPK}, type
			},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the microblogs entries that the user has permissions to view where companyId = &#63; and creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries that the user has permission to view
	 */
	@Override
	public List<MicroblogsEntry> filterFindByC_CCNI_CCPK_T(
		long companyId, long creatorClassNameId, long creatorClassPK, int type,
		int start, int end,
		OrderByComparator<MicroblogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_CCNI_CCPK_T.filterFind(
			finderCache,
			new Object[] {
				companyId, creatorClassNameId, new long[] {creatorClassPK}, type
			},
			start, end, orderByComparator, companyId, 0);
	}

	/**
	 * Returns an ordered range of all the microblogs entries that the user has permission to view where companyId = &#63; and creatorClassNameId = &#63; and creatorClassPK = any &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPKs the creator class pks
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries that the user has permission to view
	 */
	@Override
	public List<MicroblogsEntry> filterFindByC_CCNI_CCPK_T(
		long companyId, long creatorClassNameId, long[] creatorClassPKs,
		int type, int start, int end,
		OrderByComparator<MicroblogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByC_CCNI_CCPK_T.filterFind(
			finderCache,
			new Object[] {
				companyId, creatorClassNameId,
				ArrayUtil.sortedUnique(creatorClassPKs), type
			},
			start, end, orderByComparator, companyId, 0);
	}

	/**
	 * Returns an ordered range of all the microblogs entries where companyId = &#63; and creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPKs the creator class pks
	 * @param type the type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching microblogs entries
	 */
	@Override
	public List<MicroblogsEntry> findByC_CCNI_CCPK_T(
		long companyId, long creatorClassNameId, long[] creatorClassPKs,
		int type, int start, int end,
		OrderByComparator<MicroblogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_CCNI_CCPK_T.find(
			finderCache,
			new Object[] {
				companyId, creatorClassNameId,
				ArrayUtil.sortedUnique(creatorClassPKs), type
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the microblogs entries where companyId = &#63; and creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param type the type
	 */
	@Override
	public void removeByC_CCNI_CCPK_T(
		long companyId, long creatorClassNameId, long creatorClassPK,
		int type) {

		_collectionPersistenceFinderByC_CCNI_CCPK_T.remove(
			finderCache,
			new Object[] {
				companyId, creatorClassNameId, new long[] {creatorClassPK}, type
			});
	}

	/**
	 * Returns the number of microblogs entries where companyId = &#63; and creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param type the type
	 * @return the number of matching microblogs entries
	 */
	@Override
	public int countByC_CCNI_CCPK_T(
		long companyId, long creatorClassNameId, long creatorClassPK,
		int type) {

		return _collectionPersistenceFinderByC_CCNI_CCPK_T.count(
			finderCache,
			new Object[] {
				companyId, creatorClassNameId, new long[] {creatorClassPK}, type
			});
	}

	/**
	 * Returns the number of microblogs entries where companyId = &#63; and creatorClassNameId = &#63; and creatorClassPK = any &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPKs the creator class pks
	 * @param type the type
	 * @return the number of matching microblogs entries
	 */
	@Override
	public int countByC_CCNI_CCPK_T(
		long companyId, long creatorClassNameId, long[] creatorClassPKs,
		int type) {

		return _collectionPersistenceFinderByC_CCNI_CCPK_T.count(
			finderCache,
			new Object[] {
				companyId, creatorClassNameId,
				ArrayUtil.sortedUnique(creatorClassPKs), type
			});
	}

	/**
	 * Returns the number of microblogs entries that the user has permission to view where companyId = &#63; and creatorClassNameId = &#63; and creatorClassPK = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPK the creator class pk
	 * @param type the type
	 * @return the number of matching microblogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByC_CCNI_CCPK_T(
		long companyId, long creatorClassNameId, long creatorClassPK,
		int type) {

		return _collectionPersistenceFinderByC_CCNI_CCPK_T.filterCount(
			finderCache,
			new Object[] {
				companyId, creatorClassNameId, new long[] {creatorClassPK}, type
			},
			companyId, 0);
	}

	/**
	 * Returns the number of microblogs entries that the user has permission to view where companyId = &#63; and creatorClassNameId = &#63; and creatorClassPK = any &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param creatorClassNameId the creator class name ID
	 * @param creatorClassPKs the creator class pks
	 * @param type the type
	 * @return the number of matching microblogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByC_CCNI_CCPK_T(
		long companyId, long creatorClassNameId, long[] creatorClassPKs,
		int type) {

		return _collectionPersistenceFinderByC_CCNI_CCPK_T.filterCount(
			finderCache,
			new Object[] {
				companyId, creatorClassNameId,
				ArrayUtil.sortedUnique(creatorClassPKs), type
			},
			companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<MicroblogsEntry, NoSuchEntryException>
			_collectionPersistenceFinderByU_C_T_S;

	/**
	 * Returns an ordered range of all the microblogs entries where userId = &#63; and createDate = &#63; and type = &#63; and socialRelationType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param type the type
	 * @param socialRelationType the social relation type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching microblogs entries
	 */
	@Override
	public List<MicroblogsEntry> findByU_C_T_S(
		long userId, Date createDate, int type, int socialRelationType,
		int start, int end,
		OrderByComparator<MicroblogsEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_C_T_S.find(
			finderCache,
			new Object[] {userId, createDate, type, socialRelationType}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first microblogs entry in the ordered set where userId = &#63; and createDate = &#63; and type = &#63; and socialRelationType = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param type the type
	 * @param socialRelationType the social relation type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry
	 * @throws NoSuchEntryException if a matching microblogs entry could not be found
	 */
	@Override
	public MicroblogsEntry findByU_C_T_S_First(
			long userId, Date createDate, int type, int socialRelationType,
			OrderByComparator<MicroblogsEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByU_C_T_S.findFirst(
			finderCache,
			new Object[] {userId, createDate, type, socialRelationType},
			orderByComparator);
	}

	/**
	 * Returns the first microblogs entry in the ordered set where userId = &#63; and createDate = &#63; and type = &#63; and socialRelationType = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param type the type
	 * @param socialRelationType the social relation type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching microblogs entry, or <code>null</code> if a matching microblogs entry could not be found
	 */
	@Override
	public MicroblogsEntry fetchByU_C_T_S_First(
		long userId, Date createDate, int type, int socialRelationType,
		OrderByComparator<MicroblogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByU_C_T_S.fetchFirst(
			finderCache,
			new Object[] {userId, createDate, type, socialRelationType},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the microblogs entries that the user has permissions to view where userId = &#63; and createDate = &#63; and type = &#63; and socialRelationType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param type the type
	 * @param socialRelationType the social relation type
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching microblogs entries that the user has permission to view
	 */
	@Override
	public List<MicroblogsEntry> filterFindByU_C_T_S(
		long userId, Date createDate, int type, int socialRelationType,
		int start, int end,
		OrderByComparator<MicroblogsEntry> orderByComparator) {

		return _collectionPersistenceFinderByU_C_T_S.filterFind(
			finderCache,
			new Object[] {userId, createDate, type, socialRelationType}, start,
			end, orderByComparator);
	}

	/**
	 * Removes all the microblogs entries where userId = &#63; and createDate = &#63; and type = &#63; and socialRelationType = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param type the type
	 * @param socialRelationType the social relation type
	 */
	@Override
	public void removeByU_C_T_S(
		long userId, Date createDate, int type, int socialRelationType) {

		_collectionPersistenceFinderByU_C_T_S.remove(
			finderCache,
			new Object[] {userId, createDate, type, socialRelationType});
	}

	/**
	 * Returns the number of microblogs entries where userId = &#63; and createDate = &#63; and type = &#63; and socialRelationType = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param type the type
	 * @param socialRelationType the social relation type
	 * @return the number of matching microblogs entries
	 */
	@Override
	public int countByU_C_T_S(
		long userId, Date createDate, int type, int socialRelationType) {

		return _collectionPersistenceFinderByU_C_T_S.count(
			finderCache,
			new Object[] {userId, createDate, type, socialRelationType});
	}

	/**
	 * Returns the number of microblogs entries that the user has permission to view where userId = &#63; and createDate = &#63; and type = &#63; and socialRelationType = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param type the type
	 * @param socialRelationType the social relation type
	 * @return the number of matching microblogs entries that the user has permission to view
	 */
	@Override
	public int filterCountByU_C_T_S(
		long userId, Date createDate, int type, int socialRelationType) {

		return _collectionPersistenceFinderByU_C_T_S.filterCount(
			finderCache,
			new Object[] {userId, createDate, type, socialRelationType});
	}

	public MicroblogsEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(MicroblogsEntry.class);

		setModelImplClass(MicroblogsEntryImpl.class);
		setModelPKClass(long.class);

		setTable(MicroblogsEntryTable.INSTANCE);
	}

	/**
	 * Creates a new microblogs entry with the primary key. Does not add the microblogs entry to the database.
	 *
	 * @param microblogsEntryId the primary key for the new microblogs entry
	 * @return the new microblogs entry
	 */
	@Override
	public MicroblogsEntry create(long microblogsEntryId) {
		MicroblogsEntry microblogsEntry = new MicroblogsEntryImpl();

		microblogsEntry.setNew(true);
		microblogsEntry.setPrimaryKey(microblogsEntryId);

		microblogsEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return microblogsEntry;
	}

	/**
	 * Removes the microblogs entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param microblogsEntryId the primary key of the microblogs entry
	 * @return the microblogs entry that was removed
	 * @throws NoSuchEntryException if a microblogs entry with the primary key could not be found
	 */
	@Override
	public MicroblogsEntry remove(long microblogsEntryId)
		throws NoSuchEntryException {

		return remove((Serializable)microblogsEntryId);
	}

	@Override
	protected MicroblogsEntry removeImpl(MicroblogsEntry microblogsEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(microblogsEntry)) {
				microblogsEntry = (MicroblogsEntry)session.get(
					MicroblogsEntryImpl.class,
					microblogsEntry.getPrimaryKeyObj());
			}

			if (microblogsEntry != null) {
				session.delete(microblogsEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (microblogsEntry != null) {
			clearCache(microblogsEntry);
		}

		return microblogsEntry;
	}

	@Override
	public MicroblogsEntry updateImpl(MicroblogsEntry microblogsEntry) {
		boolean isNew = microblogsEntry.isNew();

		if (!(microblogsEntry instanceof MicroblogsEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(microblogsEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					microblogsEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in microblogsEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom MicroblogsEntry implementation " +
					microblogsEntry.getClass());
		}

		MicroblogsEntryModelImpl microblogsEntryModelImpl =
			(MicroblogsEntryModelImpl)microblogsEntry;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (microblogsEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				microblogsEntry.setCreateDate(date);
			}
			else {
				microblogsEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!microblogsEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				microblogsEntry.setModifiedDate(date);
			}
			else {
				microblogsEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(microblogsEntry);
			}
			else {
				microblogsEntry = (MicroblogsEntry)session.merge(
					microblogsEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(microblogsEntry, false);

		if (isNew) {
			microblogsEntry.setNew(false);
		}

		microblogsEntry.resetOriginalValues();

		return microblogsEntry;
	}

	/**
	 * Returns the microblogs entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param microblogsEntryId the primary key of the microblogs entry
	 * @return the microblogs entry
	 * @throws NoSuchEntryException if a microblogs entry with the primary key could not be found
	 */
	@Override
	public MicroblogsEntry findByPrimaryKey(long microblogsEntryId)
		throws NoSuchEntryException {

		return findByPrimaryKey((Serializable)microblogsEntryId);
	}

	/**
	 * Returns the microblogs entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param microblogsEntryId the primary key of the microblogs entry
	 * @return the microblogs entry, or <code>null</code> if a microblogs entry with the primary key could not be found
	 */
	@Override
	public MicroblogsEntry fetchByPrimaryKey(long microblogsEntryId) {
		return fetchByPrimaryKey((Serializable)microblogsEntryId);
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
		return "microblogsEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_MICROBLOGSENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return MicroblogsEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the microblogs entry persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCompanyId =
			new FilterCollectionPersistenceFinder<>(
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
				_SQL_SELECT_MICROBLOGSENTRY_WHERE,
				_SQL_COUNT_MICROBLOGSENTRY_WHERE,
				MicroblogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"microblogsEntry.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, MicroblogsEntry::getCompanyId));

		_collectionPersistenceFinderByUserId =
			new FilterCollectionPersistenceFinder<>(
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
				_SQL_SELECT_MICROBLOGSENTRY_WHERE,
				_SQL_COUNT_MICROBLOGSENTRY_WHERE,
				MicroblogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"microblogsEntry.", "userId", FinderColumn.Type.LONG, "=",
					true, true, MicroblogsEntry::getUserId));

		_collectionPersistenceFinderByU_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_T",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"userId", "type_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_T",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"userId", "type_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_T",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"userId", "type_"}, false),
				_SQL_SELECT_MICROBLOGSENTRY_WHERE,
				_SQL_COUNT_MICROBLOGSENTRY_WHERE,
				MicroblogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"microblogsEntry.", "userId", FinderColumn.Type.LONG, "=",
					true, true, MicroblogsEntry::getUserId),
				new FinderColumn<>(
					"microblogsEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					MicroblogsEntry::getType));

		_collectionPersistenceFinderByCCNI_CCPK =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCCNI_CCPK",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"creatorClassNameId", "creatorClassPK"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCCNI_CCPK",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"creatorClassNameId", "creatorClassPK"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByCCNI_CCPK",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"creatorClassNameId", "creatorClassPK"},
					false),
				_SQL_SELECT_MICROBLOGSENTRY_WHERE,
				_SQL_COUNT_MICROBLOGSENTRY_WHERE,
				MicroblogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"microblogsEntry.", "creatorClassNameId",
					FinderColumn.Type.LONG, "=", true, true,
					MicroblogsEntry::getCreatorClassNameId),
				new ArrayableFinderColumn<>(
					"microblogsEntry.", "creatorClassPK",
					FinderColumn.Type.LONG, "=", false, true, true,
					MicroblogsEntry::getCreatorClassPK));

		_collectionPersistenceFinderByCCNI_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCCNI_T",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"creatorClassNameId", "type_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCCNI_T",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"creatorClassNameId", "type_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCCNI_T",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"creatorClassNameId", "type_"}, false),
				_SQL_SELECT_MICROBLOGSENTRY_WHERE,
				_SQL_COUNT_MICROBLOGSENTRY_WHERE,
				MicroblogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"microblogsEntry.", "creatorClassNameId",
					FinderColumn.Type.LONG, "=", true, true,
					MicroblogsEntry::getCreatorClassNameId),
				new FinderColumn<>(
					"microblogsEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					MicroblogsEntry::getType));

		_collectionPersistenceFinderByT_P =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByT_P",
					new String[] {
						Integer.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"type_", "parentMicroblogsEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByT_P",
					new String[] {
						Integer.class.getName(), Long.class.getName()
					},
					new String[] {"type_", "parentMicroblogsEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByT_P",
					new String[] {
						Integer.class.getName(), Long.class.getName()
					},
					new String[] {"type_", "parentMicroblogsEntryId"}, false),
				_SQL_SELECT_MICROBLOGSENTRY_WHERE,
				_SQL_COUNT_MICROBLOGSENTRY_WHERE,
				MicroblogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"microblogsEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					MicroblogsEntry::getType),
				new FinderColumn<>(
					"microblogsEntry.", "parentMicroblogsEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					MicroblogsEntry::getParentMicroblogsEntryId));

		_collectionPersistenceFinderByC_CCNI_CCPK =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_CCNI_CCPK",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "creatorClassNameId", "creatorClassPK"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByC_CCNI_CCPK",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"companyId", "creatorClassNameId", "creatorClassPK"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByC_CCNI_CCPK",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"companyId", "creatorClassNameId", "creatorClassPK"
					},
					false),
				_SQL_SELECT_MICROBLOGSENTRY_WHERE,
				_SQL_COUNT_MICROBLOGSENTRY_WHERE,
				MicroblogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"microblogsEntry.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, MicroblogsEntry::getCompanyId),
				new FinderColumn<>(
					"microblogsEntry.", "creatorClassNameId",
					FinderColumn.Type.LONG, "=", true, true,
					MicroblogsEntry::getCreatorClassNameId),
				new ArrayableFinderColumn<>(
					"microblogsEntry.", "creatorClassPK",
					FinderColumn.Type.LONG, "=", false, true, true,
					MicroblogsEntry::getCreatorClassPK));

		_collectionPersistenceFinderByC_CCNI_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_CCNI_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "creatorClassNameId", "type_"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_CCNI_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"companyId", "creatorClassNameId", "type_"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByC_CCNI_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {"companyId", "creatorClassNameId", "type_"},
					false),
				_SQL_SELECT_MICROBLOGSENTRY_WHERE,
				_SQL_COUNT_MICROBLOGSENTRY_WHERE,
				MicroblogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"microblogsEntry.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, MicroblogsEntry::getCompanyId),
				new FinderColumn<>(
					"microblogsEntry.", "creatorClassNameId",
					FinderColumn.Type.LONG, "=", true, true,
					MicroblogsEntry::getCreatorClassNameId),
				new FinderColumn<>(
					"microblogsEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					MicroblogsEntry::getType));

		_collectionPersistenceFinderByCCNI_CCPK_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCCNI_CCPK_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"creatorClassNameId", "creatorClassPK", "type_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCCNI_CCPK_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"creatorClassNameId", "creatorClassPK", "type_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByCCNI_CCPK_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"creatorClassNameId", "creatorClassPK", "type_"
					},
					false),
				_SQL_SELECT_MICROBLOGSENTRY_WHERE,
				_SQL_COUNT_MICROBLOGSENTRY_WHERE,
				MicroblogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"microblogsEntry.", "creatorClassNameId",
					FinderColumn.Type.LONG, "=", true, true,
					MicroblogsEntry::getCreatorClassNameId),
				new ArrayableFinderColumn<>(
					"microblogsEntry.", "creatorClassPK",
					FinderColumn.Type.LONG, "=", false, true, true,
					MicroblogsEntry::getCreatorClassPK),
				new FinderColumn<>(
					"microblogsEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					MicroblogsEntry::getType));

		_collectionPersistenceFinderByC_CCNI_CCPK_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByC_CCNI_CCPK_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "creatorClassNameId", "creatorClassPK",
						"type_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByC_CCNI_CCPK_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {
						"companyId", "creatorClassNameId", "creatorClassPK",
						"type_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByC_CCNI_CCPK_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {
						"companyId", "creatorClassNameId", "creatorClassPK",
						"type_"
					},
					false),
				_SQL_SELECT_MICROBLOGSENTRY_WHERE,
				_SQL_COUNT_MICROBLOGSENTRY_WHERE,
				MicroblogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"microblogsEntry.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, MicroblogsEntry::getCompanyId),
				new FinderColumn<>(
					"microblogsEntry.", "creatorClassNameId",
					FinderColumn.Type.LONG, "=", true, true,
					MicroblogsEntry::getCreatorClassNameId),
				new ArrayableFinderColumn<>(
					"microblogsEntry.", "creatorClassPK",
					FinderColumn.Type.LONG, "=", false, true, true,
					MicroblogsEntry::getCreatorClassPK),
				new FinderColumn<>(
					"microblogsEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					MicroblogsEntry::getType));

		_collectionPersistenceFinderByU_C_T_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_C_T_S",
					new String[] {
						Long.class.getName(), Date.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"userId", "createDate", "type_", "socialRelationType"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_C_T_S",
					new String[] {
						Long.class.getName(), Date.class.getName(),
						Integer.class.getName(), Integer.class.getName()
					},
					new String[] {
						"userId", "createDate", "type_", "socialRelationType"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_C_T_S",
					new String[] {
						Long.class.getName(), Date.class.getName(),
						Integer.class.getName(), Integer.class.getName()
					},
					new String[] {
						"userId", "createDate", "type_", "socialRelationType"
					},
					false),
				_SQL_SELECT_MICROBLOGSENTRY_WHERE,
				_SQL_COUNT_MICROBLOGSENTRY_WHERE,
				MicroblogsEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"microblogsEntry.", "userId", FinderColumn.Type.LONG, "=",
					true, true, MicroblogsEntry::getUserId),
				new FinderColumn<>(
					"microblogsEntry.", "createDate", FinderColumn.Type.DATE,
					"=", true, true, MicroblogsEntry::getCreateDate),
				new FinderColumn<>(
					"microblogsEntry.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					MicroblogsEntry::getType),
				new FinderColumn<>(
					"microblogsEntry.", "socialRelationType",
					FinderColumn.Type.INTEGER, "=", true, true,
					MicroblogsEntry::getSocialRelationType));

		MicroblogsEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		MicroblogsEntryUtil.setPersistence(null);

		entityCache.removeCache(MicroblogsEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = MicroblogsPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = MicroblogsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = MicroblogsPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		MicroblogsEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_MICROBLOGSENTRY =
		"SELECT microblogsEntry FROM MicroblogsEntry microblogsEntry";

	private static final String _SQL_SELECT_MICROBLOGSENTRY_WHERE =
		"SELECT microblogsEntry FROM MicroblogsEntry microblogsEntry WHERE ";

	private static final String _SQL_COUNT_MICROBLOGSENTRY_WHERE =
		"SELECT COUNT(microblogsEntry) FROM MicroblogsEntry microblogsEntry WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"type"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1405982586