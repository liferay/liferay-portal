/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.qualifier.service.persistence.impl;

import com.liferay.commerce.qualifier.exception.NoSuchCommerceQualifierEntryException;
import com.liferay.commerce.qualifier.model.CommerceQualifierEntry;
import com.liferay.commerce.qualifier.model.CommerceQualifierEntryTable;
import com.liferay.commerce.qualifier.model.impl.CommerceQualifierEntryImpl;
import com.liferay.commerce.qualifier.model.impl.CommerceQualifierEntryModelImpl;
import com.liferay.commerce.qualifier.service.persistence.CommerceQualifierEntryPersistence;
import com.liferay.commerce.qualifier.service.persistence.CommerceQualifierEntryUtil;
import com.liferay.commerce.qualifier.service.persistence.impl.constants.CommercePersistenceConstants;
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
 * The persistence implementation for the commerce qualifier entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Riccardo Alberti
 * @generated
 */
@Component(service = CommerceQualifierEntryPersistence.class)
public class CommerceQualifierEntryPersistenceImpl
	extends BasePersistenceImpl
		<CommerceQualifierEntry, NoSuchCommerceQualifierEntryException>
	implements CommerceQualifierEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceQualifierEntryUtil</code> to access the commerce qualifier entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceQualifierEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceQualifierEntry, NoSuchCommerceQualifierEntryException>
			_collectionPersistenceFinderByS_S;

	/**
	 * Returns an ordered range of all the commerce qualifier entries where sourceClassNameId = &#63; and sourceClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceQualifierEntryModelImpl</code>.
	 * </p>
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param start the lower bound of the range of commerce qualifier entries
	 * @param end the upper bound of the range of commerce qualifier entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce qualifier entries
	 */
	@Override
	public List<CommerceQualifierEntry> findByS_S(
		long sourceClassNameId, long sourceClassPK, int start, int end,
		OrderByComparator<CommerceQualifierEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByS_S.find(
			finderCache, new Object[] {sourceClassNameId, sourceClassPK}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce qualifier entry in the ordered set where sourceClassNameId = &#63; and sourceClassPK = &#63;.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce qualifier entry
	 * @throws NoSuchCommerceQualifierEntryException if a matching commerce qualifier entry could not be found
	 */
	@Override
	public CommerceQualifierEntry findByS_S_First(
			long sourceClassNameId, long sourceClassPK,
			OrderByComparator<CommerceQualifierEntry> orderByComparator)
		throws NoSuchCommerceQualifierEntryException {

		return _collectionPersistenceFinderByS_S.findFirst(
			finderCache, new Object[] {sourceClassNameId, sourceClassPK},
			orderByComparator);
	}

	/**
	 * Returns the first commerce qualifier entry in the ordered set where sourceClassNameId = &#63; and sourceClassPK = &#63;.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce qualifier entry, or <code>null</code> if a matching commerce qualifier entry could not be found
	 */
	@Override
	public CommerceQualifierEntry fetchByS_S_First(
		long sourceClassNameId, long sourceClassPK,
		OrderByComparator<CommerceQualifierEntry> orderByComparator) {

		return _collectionPersistenceFinderByS_S.fetchFirst(
			finderCache, new Object[] {sourceClassNameId, sourceClassPK},
			orderByComparator);
	}

	/**
	 * Removes all the commerce qualifier entries where sourceClassNameId = &#63; and sourceClassPK = &#63; from the database.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 */
	@Override
	public void removeByS_S(long sourceClassNameId, long sourceClassPK) {
		_collectionPersistenceFinderByS_S.remove(
			finderCache, new Object[] {sourceClassNameId, sourceClassPK});
	}

	/**
	 * Returns the number of commerce qualifier entries where sourceClassNameId = &#63; and sourceClassPK = &#63;.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @return the number of matching commerce qualifier entries
	 */
	@Override
	public int countByS_S(long sourceClassNameId, long sourceClassPK) {
		return _collectionPersistenceFinderByS_S.count(
			finderCache, new Object[] {sourceClassNameId, sourceClassPK});
	}

	private CollectionPersistenceFinder
		<CommerceQualifierEntry, NoSuchCommerceQualifierEntryException>
			_collectionPersistenceFinderByT_T;

	/**
	 * Returns an ordered range of all the commerce qualifier entries where targetClassNameId = &#63; and targetClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceQualifierEntryModelImpl</code>.
	 * </p>
	 *
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @param start the lower bound of the range of commerce qualifier entries
	 * @param end the upper bound of the range of commerce qualifier entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce qualifier entries
	 */
	@Override
	public List<CommerceQualifierEntry> findByT_T(
		long targetClassNameId, long targetClassPK, int start, int end,
		OrderByComparator<CommerceQualifierEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByT_T.find(
			finderCache, new Object[] {targetClassNameId, targetClassPK}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce qualifier entry in the ordered set where targetClassNameId = &#63; and targetClassPK = &#63;.
	 *
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce qualifier entry
	 * @throws NoSuchCommerceQualifierEntryException if a matching commerce qualifier entry could not be found
	 */
	@Override
	public CommerceQualifierEntry findByT_T_First(
			long targetClassNameId, long targetClassPK,
			OrderByComparator<CommerceQualifierEntry> orderByComparator)
		throws NoSuchCommerceQualifierEntryException {

		return _collectionPersistenceFinderByT_T.findFirst(
			finderCache, new Object[] {targetClassNameId, targetClassPK},
			orderByComparator);
	}

	/**
	 * Returns the first commerce qualifier entry in the ordered set where targetClassNameId = &#63; and targetClassPK = &#63;.
	 *
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce qualifier entry, or <code>null</code> if a matching commerce qualifier entry could not be found
	 */
	@Override
	public CommerceQualifierEntry fetchByT_T_First(
		long targetClassNameId, long targetClassPK,
		OrderByComparator<CommerceQualifierEntry> orderByComparator) {

		return _collectionPersistenceFinderByT_T.fetchFirst(
			finderCache, new Object[] {targetClassNameId, targetClassPK},
			orderByComparator);
	}

	/**
	 * Removes all the commerce qualifier entries where targetClassNameId = &#63; and targetClassPK = &#63; from the database.
	 *
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 */
	@Override
	public void removeByT_T(long targetClassNameId, long targetClassPK) {
		_collectionPersistenceFinderByT_T.remove(
			finderCache, new Object[] {targetClassNameId, targetClassPK});
	}

	/**
	 * Returns the number of commerce qualifier entries where targetClassNameId = &#63; and targetClassPK = &#63;.
	 *
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @return the number of matching commerce qualifier entries
	 */
	@Override
	public int countByT_T(long targetClassNameId, long targetClassPK) {
		return _collectionPersistenceFinderByT_T.count(
			finderCache, new Object[] {targetClassNameId, targetClassPK});
	}

	private CollectionPersistenceFinder
		<CommerceQualifierEntry, NoSuchCommerceQualifierEntryException>
			_collectionPersistenceFinderByS_S_T;

	/**
	 * Returns an ordered range of all the commerce qualifier entries where sourceClassNameId = &#63; and sourceClassPK = &#63; and targetClassNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceQualifierEntryModelImpl</code>.
	 * </p>
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param targetClassNameId the target class name ID
	 * @param start the lower bound of the range of commerce qualifier entries
	 * @param end the upper bound of the range of commerce qualifier entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce qualifier entries
	 */
	@Override
	public List<CommerceQualifierEntry> findByS_S_T(
		long sourceClassNameId, long sourceClassPK, long targetClassNameId,
		int start, int end,
		OrderByComparator<CommerceQualifierEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByS_S_T.find(
			finderCache,
			new Object[] {sourceClassNameId, sourceClassPK, targetClassNameId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce qualifier entry in the ordered set where sourceClassNameId = &#63; and sourceClassPK = &#63; and targetClassNameId = &#63;.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param targetClassNameId the target class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce qualifier entry
	 * @throws NoSuchCommerceQualifierEntryException if a matching commerce qualifier entry could not be found
	 */
	@Override
	public CommerceQualifierEntry findByS_S_T_First(
			long sourceClassNameId, long sourceClassPK, long targetClassNameId,
			OrderByComparator<CommerceQualifierEntry> orderByComparator)
		throws NoSuchCommerceQualifierEntryException {

		return _collectionPersistenceFinderByS_S_T.findFirst(
			finderCache,
			new Object[] {sourceClassNameId, sourceClassPK, targetClassNameId},
			orderByComparator);
	}

	/**
	 * Returns the first commerce qualifier entry in the ordered set where sourceClassNameId = &#63; and sourceClassPK = &#63; and targetClassNameId = &#63;.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param targetClassNameId the target class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce qualifier entry, or <code>null</code> if a matching commerce qualifier entry could not be found
	 */
	@Override
	public CommerceQualifierEntry fetchByS_S_T_First(
		long sourceClassNameId, long sourceClassPK, long targetClassNameId,
		OrderByComparator<CommerceQualifierEntry> orderByComparator) {

		return _collectionPersistenceFinderByS_S_T.fetchFirst(
			finderCache,
			new Object[] {sourceClassNameId, sourceClassPK, targetClassNameId},
			orderByComparator);
	}

	/**
	 * Removes all the commerce qualifier entries where sourceClassNameId = &#63; and sourceClassPK = &#63; and targetClassNameId = &#63; from the database.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param targetClassNameId the target class name ID
	 */
	@Override
	public void removeByS_S_T(
		long sourceClassNameId, long sourceClassPK, long targetClassNameId) {

		_collectionPersistenceFinderByS_S_T.remove(
			finderCache,
			new Object[] {sourceClassNameId, sourceClassPK, targetClassNameId});
	}

	/**
	 * Returns the number of commerce qualifier entries where sourceClassNameId = &#63; and sourceClassPK = &#63; and targetClassNameId = &#63;.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param targetClassNameId the target class name ID
	 * @return the number of matching commerce qualifier entries
	 */
	@Override
	public int countByS_S_T(
		long sourceClassNameId, long sourceClassPK, long targetClassNameId) {

		return _collectionPersistenceFinderByS_S_T.count(
			finderCache,
			new Object[] {sourceClassNameId, sourceClassPK, targetClassNameId});
	}

	private CollectionPersistenceFinder
		<CommerceQualifierEntry, NoSuchCommerceQualifierEntryException>
			_collectionPersistenceFinderByS_T_T;

	/**
	 * Returns an ordered range of all the commerce qualifier entries where sourceClassNameId = &#63; and targetClassNameId = &#63; and targetClassPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceQualifierEntryModelImpl</code>.
	 * </p>
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @param start the lower bound of the range of commerce qualifier entries
	 * @param end the upper bound of the range of commerce qualifier entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce qualifier entries
	 */
	@Override
	public List<CommerceQualifierEntry> findByS_T_T(
		long sourceClassNameId, long targetClassNameId, long targetClassPK,
		int start, int end,
		OrderByComparator<CommerceQualifierEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByS_T_T.find(
			finderCache,
			new Object[] {sourceClassNameId, targetClassNameId, targetClassPK},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce qualifier entry in the ordered set where sourceClassNameId = &#63; and targetClassNameId = &#63; and targetClassPK = &#63;.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce qualifier entry
	 * @throws NoSuchCommerceQualifierEntryException if a matching commerce qualifier entry could not be found
	 */
	@Override
	public CommerceQualifierEntry findByS_T_T_First(
			long sourceClassNameId, long targetClassNameId, long targetClassPK,
			OrderByComparator<CommerceQualifierEntry> orderByComparator)
		throws NoSuchCommerceQualifierEntryException {

		return _collectionPersistenceFinderByS_T_T.findFirst(
			finderCache,
			new Object[] {sourceClassNameId, targetClassNameId, targetClassPK},
			orderByComparator);
	}

	/**
	 * Returns the first commerce qualifier entry in the ordered set where sourceClassNameId = &#63; and targetClassNameId = &#63; and targetClassPK = &#63;.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce qualifier entry, or <code>null</code> if a matching commerce qualifier entry could not be found
	 */
	@Override
	public CommerceQualifierEntry fetchByS_T_T_First(
		long sourceClassNameId, long targetClassNameId, long targetClassPK,
		OrderByComparator<CommerceQualifierEntry> orderByComparator) {

		return _collectionPersistenceFinderByS_T_T.fetchFirst(
			finderCache,
			new Object[] {sourceClassNameId, targetClassNameId, targetClassPK},
			orderByComparator);
	}

	/**
	 * Removes all the commerce qualifier entries where sourceClassNameId = &#63; and targetClassNameId = &#63; and targetClassPK = &#63; from the database.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 */
	@Override
	public void removeByS_T_T(
		long sourceClassNameId, long targetClassNameId, long targetClassPK) {

		_collectionPersistenceFinderByS_T_T.remove(
			finderCache,
			new Object[] {sourceClassNameId, targetClassNameId, targetClassPK});
	}

	/**
	 * Returns the number of commerce qualifier entries where sourceClassNameId = &#63; and targetClassNameId = &#63; and targetClassPK = &#63;.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @return the number of matching commerce qualifier entries
	 */
	@Override
	public int countByS_T_T(
		long sourceClassNameId, long targetClassNameId, long targetClassPK) {

		return _collectionPersistenceFinderByS_T_T.count(
			finderCache,
			new Object[] {sourceClassNameId, targetClassNameId, targetClassPK});
	}

	private UniquePersistenceFinder
		<CommerceQualifierEntry, NoSuchCommerceQualifierEntryException>
			_uniquePersistenceFinderByS_S_T_T;

	/**
	 * Returns the commerce qualifier entry where sourceClassNameId = &#63; and sourceClassPK = &#63; and targetClassNameId = &#63; and targetClassPK = &#63; or throws a <code>NoSuchCommerceQualifierEntryException</code> if it could not be found.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @return the matching commerce qualifier entry
	 * @throws NoSuchCommerceQualifierEntryException if a matching commerce qualifier entry could not be found
	 */
	@Override
	public CommerceQualifierEntry findByS_S_T_T(
			long sourceClassNameId, long sourceClassPK, long targetClassNameId,
			long targetClassPK)
		throws NoSuchCommerceQualifierEntryException {

		return _uniquePersistenceFinderByS_S_T_T.find(
			finderCache,
			new Object[] {
				sourceClassNameId, sourceClassPK, targetClassNameId,
				targetClassPK
			});
	}

	/**
	 * Returns the commerce qualifier entry where sourceClassNameId = &#63; and sourceClassPK = &#63; and targetClassNameId = &#63; and targetClassPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce qualifier entry, or <code>null</code> if a matching commerce qualifier entry could not be found
	 */
	@Override
	public CommerceQualifierEntry fetchByS_S_T_T(
		long sourceClassNameId, long sourceClassPK, long targetClassNameId,
		long targetClassPK, boolean useFinderCache) {

		return _uniquePersistenceFinderByS_S_T_T.fetch(
			finderCache,
			new Object[] {
				sourceClassNameId, sourceClassPK, targetClassNameId,
				targetClassPK
			},
			useFinderCache);
	}

	/**
	 * Removes the commerce qualifier entry where sourceClassNameId = &#63; and sourceClassPK = &#63; and targetClassNameId = &#63; and targetClassPK = &#63; from the database.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @return the commerce qualifier entry that was removed
	 */
	@Override
	public CommerceQualifierEntry removeByS_S_T_T(
			long sourceClassNameId, long sourceClassPK, long targetClassNameId,
			long targetClassPK)
		throws NoSuchCommerceQualifierEntryException {

		CommerceQualifierEntry commerceQualifierEntry = findByS_S_T_T(
			sourceClassNameId, sourceClassPK, targetClassNameId, targetClassPK);

		return remove(commerceQualifierEntry);
	}

	/**
	 * Returns the number of commerce qualifier entries where sourceClassNameId = &#63; and sourceClassPK = &#63; and targetClassNameId = &#63; and targetClassPK = &#63;.
	 *
	 * @param sourceClassNameId the source class name ID
	 * @param sourceClassPK the source class pk
	 * @param targetClassNameId the target class name ID
	 * @param targetClassPK the target class pk
	 * @return the number of matching commerce qualifier entries
	 */
	@Override
	public int countByS_S_T_T(
		long sourceClassNameId, long sourceClassPK, long targetClassNameId,
		long targetClassPK) {

		return _uniquePersistenceFinderByS_S_T_T.count(
			finderCache,
			new Object[] {
				sourceClassNameId, sourceClassPK, targetClassNameId,
				targetClassPK
			});
	}

	public CommerceQualifierEntryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put(
			"sourceCommerceQualifierMetadataKey",
			"sourceCQualifierMetadataKey");
		dbColumnNames.put(
			"targetCommerceQualifierMetadataKey",
			"targetCQualifierMetadataKey");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceQualifierEntry.class);

		setModelImplClass(CommerceQualifierEntryImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceQualifierEntryTable.INSTANCE);
	}

	/**
	 * Creates a new commerce qualifier entry with the primary key. Does not add the commerce qualifier entry to the database.
	 *
	 * @param commerceQualifierEntryId the primary key for the new commerce qualifier entry
	 * @return the new commerce qualifier entry
	 */
	@Override
	public CommerceQualifierEntry create(long commerceQualifierEntryId) {
		CommerceQualifierEntry commerceQualifierEntry =
			new CommerceQualifierEntryImpl();

		commerceQualifierEntry.setNew(true);
		commerceQualifierEntry.setPrimaryKey(commerceQualifierEntryId);

		commerceQualifierEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commerceQualifierEntry;
	}

	/**
	 * Removes the commerce qualifier entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceQualifierEntryId the primary key of the commerce qualifier entry
	 * @return the commerce qualifier entry that was removed
	 * @throws NoSuchCommerceQualifierEntryException if a commerce qualifier entry with the primary key could not be found
	 */
	@Override
	public CommerceQualifierEntry remove(long commerceQualifierEntryId)
		throws NoSuchCommerceQualifierEntryException {

		return remove((Serializable)commerceQualifierEntryId);
	}

	@Override
	protected CommerceQualifierEntry removeImpl(
		CommerceQualifierEntry commerceQualifierEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceQualifierEntry)) {
				commerceQualifierEntry = (CommerceQualifierEntry)session.get(
					CommerceQualifierEntryImpl.class,
					commerceQualifierEntry.getPrimaryKeyObj());
			}

			if (commerceQualifierEntry != null) {
				session.delete(commerceQualifierEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceQualifierEntry != null) {
			clearCache(commerceQualifierEntry);
		}

		return commerceQualifierEntry;
	}

	@Override
	public CommerceQualifierEntry updateImpl(
		CommerceQualifierEntry commerceQualifierEntry) {

		boolean isNew = commerceQualifierEntry.isNew();

		if (!(commerceQualifierEntry instanceof
				CommerceQualifierEntryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceQualifierEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceQualifierEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceQualifierEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceQualifierEntry implementation " +
					commerceQualifierEntry.getClass());
		}

		CommerceQualifierEntryModelImpl commerceQualifierEntryModelImpl =
			(CommerceQualifierEntryModelImpl)commerceQualifierEntry;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceQualifierEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceQualifierEntry.setCreateDate(date);
			}
			else {
				commerceQualifierEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceQualifierEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceQualifierEntry.setModifiedDate(date);
			}
			else {
				commerceQualifierEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceQualifierEntry);
			}
			else {
				commerceQualifierEntry = (CommerceQualifierEntry)session.merge(
					commerceQualifierEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceQualifierEntry, false);

		if (isNew) {
			commerceQualifierEntry.setNew(false);
		}

		commerceQualifierEntry.resetOriginalValues();

		return commerceQualifierEntry;
	}

	/**
	 * Returns the commerce qualifier entry with the primary key or throws a <code>NoSuchCommerceQualifierEntryException</code> if it could not be found.
	 *
	 * @param commerceQualifierEntryId the primary key of the commerce qualifier entry
	 * @return the commerce qualifier entry
	 * @throws NoSuchCommerceQualifierEntryException if a commerce qualifier entry with the primary key could not be found
	 */
	@Override
	public CommerceQualifierEntry findByPrimaryKey(
			long commerceQualifierEntryId)
		throws NoSuchCommerceQualifierEntryException {

		return findByPrimaryKey((Serializable)commerceQualifierEntryId);
	}

	/**
	 * Returns the commerce qualifier entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceQualifierEntryId the primary key of the commerce qualifier entry
	 * @return the commerce qualifier entry, or <code>null</code> if a commerce qualifier entry with the primary key could not be found
	 */
	@Override
	public CommerceQualifierEntry fetchByPrimaryKey(
		long commerceQualifierEntryId) {

		return fetchByPrimaryKey((Serializable)commerceQualifierEntryId);
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
		return "commerceQualifierEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEQUALIFIERENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceQualifierEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce qualifier entry persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByS_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByS_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"sourceClassNameId", "sourceClassPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByS_S",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"sourceClassNameId", "sourceClassPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByS_S",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"sourceClassNameId", "sourceClassPK"}, false),
			_SQL_SELECT_COMMERCEQUALIFIERENTRY_WHERE,
			_SQL_COUNT_COMMERCEQUALIFIERENTRY_WHERE,
			CommerceQualifierEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"commerceQualifierEntry.", "sourceClassNameId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceQualifierEntry::getSourceClassNameId),
			new FinderColumn<>(
				"commerceQualifierEntry.", "sourceClassPK",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceQualifierEntry::getSourceClassPK));

		_collectionPersistenceFinderByT_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByT_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"targetClassNameId", "targetClassPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByT_T",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"targetClassNameId", "targetClassPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByT_T",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"targetClassNameId", "targetClassPK"}, false),
			_SQL_SELECT_COMMERCEQUALIFIERENTRY_WHERE,
			_SQL_COUNT_COMMERCEQUALIFIERENTRY_WHERE,
			CommerceQualifierEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"commerceQualifierEntry.", "targetClassNameId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceQualifierEntry::getTargetClassNameId),
			new FinderColumn<>(
				"commerceQualifierEntry.", "targetClassPK",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceQualifierEntry::getTargetClassPK));

		_collectionPersistenceFinderByS_S_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByS_S_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {
					"sourceClassNameId", "sourceClassPK", "targetClassNameId"
				},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByS_S_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {
					"sourceClassNameId", "sourceClassPK", "targetClassNameId"
				},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByS_S_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {
					"sourceClassNameId", "sourceClassPK", "targetClassNameId"
				},
				false),
			_SQL_SELECT_COMMERCEQUALIFIERENTRY_WHERE,
			_SQL_COUNT_COMMERCEQUALIFIERENTRY_WHERE,
			CommerceQualifierEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"commerceQualifierEntry.", "sourceClassNameId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceQualifierEntry::getSourceClassNameId),
			new FinderColumn<>(
				"commerceQualifierEntry.", "sourceClassPK",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceQualifierEntry::getSourceClassPK),
			new FinderColumn<>(
				"commerceQualifierEntry.", "targetClassNameId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceQualifierEntry::getTargetClassNameId));

		_collectionPersistenceFinderByS_T_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByS_T_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {
					"sourceClassNameId", "targetClassNameId", "targetClassPK"
				},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByS_T_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {
					"sourceClassNameId", "targetClassNameId", "targetClassPK"
				},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByS_T_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {
					"sourceClassNameId", "targetClassNameId", "targetClassPK"
				},
				false),
			_SQL_SELECT_COMMERCEQUALIFIERENTRY_WHERE,
			_SQL_COUNT_COMMERCEQUALIFIERENTRY_WHERE,
			CommerceQualifierEntryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"commerceQualifierEntry.", "sourceClassNameId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceQualifierEntry::getSourceClassNameId),
			new FinderColumn<>(
				"commerceQualifierEntry.", "targetClassNameId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceQualifierEntry::getTargetClassNameId),
			new FinderColumn<>(
				"commerceQualifierEntry.", "targetClassPK",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceQualifierEntry::getTargetClassPK));

		_uniquePersistenceFinderByS_S_T_T = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByS_S_T_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), Long.class.getName()
				},
				new String[] {
					"sourceClassNameId", "sourceClassPK", "targetClassNameId",
					"targetClassPK"
				},
				0, 0, false, CommerceQualifierEntry::getSourceClassNameId,
				CommerceQualifierEntry::getSourceClassPK,
				CommerceQualifierEntry::getTargetClassNameId,
				CommerceQualifierEntry::getTargetClassPK),
			_SQL_SELECT_COMMERCEQUALIFIERENTRY_WHERE, "",
			new FinderColumn<>(
				"commerceQualifierEntry.", "sourceClassNameId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceQualifierEntry::getSourceClassNameId),
			new FinderColumn<>(
				"commerceQualifierEntry.", "sourceClassPK",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceQualifierEntry::getSourceClassPK),
			new FinderColumn<>(
				"commerceQualifierEntry.", "targetClassNameId",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceQualifierEntry::getTargetClassNameId),
			new FinderColumn<>(
				"commerceQualifierEntry.", "targetClassPK",
				FinderColumn.Type.LONG, "=", true, true,
				CommerceQualifierEntry::getTargetClassPK));

		CommerceQualifierEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceQualifierEntryUtil.setPersistence(null);

		entityCache.removeCache(CommerceQualifierEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		CommerceQualifierEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEQUALIFIERENTRY =
		"SELECT commerceQualifierEntry FROM CommerceQualifierEntry commerceQualifierEntry";

	private static final String _SQL_SELECT_COMMERCEQUALIFIERENTRY_WHERE =
		"SELECT commerceQualifierEntry FROM CommerceQualifierEntry commerceQualifierEntry WHERE ";

	private static final String _SQL_COUNT_COMMERCEQUALIFIERENTRY_WHERE =
		"SELECT COUNT(commerceQualifierEntry) FROM CommerceQualifierEntry commerceQualifierEntry WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {
			"sourceCommerceQualifierMetadataKey",
			"targetCommerceQualifierMetadataKey"
		});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:488697554