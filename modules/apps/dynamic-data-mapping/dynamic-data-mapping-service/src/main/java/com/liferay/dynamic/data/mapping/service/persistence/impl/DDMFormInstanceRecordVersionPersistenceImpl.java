/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.impl;

import com.liferay.dynamic.data.mapping.exception.NoSuchFormInstanceRecordVersionException;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersionTable;
import com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordVersionModelImpl;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFormInstanceRecordVersionPersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFormInstanceRecordVersionUtil;
import com.liferay.dynamic.data.mapping.service.persistence.impl.constants.DDMPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
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
 * The persistence implementation for the ddm form instance record version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DDMFormInstanceRecordVersionPersistence.class)
public class DDMFormInstanceRecordVersionPersistenceImpl
	extends BasePersistenceImpl
		<DDMFormInstanceRecordVersion, NoSuchFormInstanceRecordVersionException>
	implements DDMFormInstanceRecordVersionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DDMFormInstanceRecordVersionUtil</code> to access the ddm form instance record version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DDMFormInstanceRecordVersionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<DDMFormInstanceRecordVersion, NoSuchFormInstanceRecordVersionException>
			_collectionPersistenceFinderByFormInstanceRecordId;

	/**
	 * Returns an ordered range of all the ddm form instance record versions where formInstanceRecordId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instance record versions
	 */
	@Override
	public List<DDMFormInstanceRecordVersion> findByFormInstanceRecordId(
		long formInstanceRecordId, int start, int end,
		OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByFormInstanceRecordId.find(
			finderCache, new Object[] {formInstanceRecordId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm form instance record version in the ordered set where formInstanceRecordId = &#63;.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record version
	 * @throws NoSuchFormInstanceRecordVersionException if a matching ddm form instance record version could not be found
	 */
	@Override
	public DDMFormInstanceRecordVersion findByFormInstanceRecordId_First(
			long formInstanceRecordId,
			OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator)
		throws NoSuchFormInstanceRecordVersionException {

		return _collectionPersistenceFinderByFormInstanceRecordId.findFirst(
			finderCache, new Object[] {formInstanceRecordId},
			orderByComparator);
	}

	/**
	 * Returns the first ddm form instance record version in the ordered set where formInstanceRecordId = &#63;.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record version, or <code>null</code> if a matching ddm form instance record version could not be found
	 */
	@Override
	public DDMFormInstanceRecordVersion fetchByFormInstanceRecordId_First(
		long formInstanceRecordId,
		OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator) {

		return _collectionPersistenceFinderByFormInstanceRecordId.fetchFirst(
			finderCache, new Object[] {formInstanceRecordId},
			orderByComparator);
	}

	/**
	 * Removes all the ddm form instance record versions where formInstanceRecordId = &#63; from the database.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 */
	@Override
	public void removeByFormInstanceRecordId(long formInstanceRecordId) {
		_collectionPersistenceFinderByFormInstanceRecordId.remove(
			finderCache, new Object[] {formInstanceRecordId});
	}

	/**
	 * Returns the number of ddm form instance record versions where formInstanceRecordId = &#63;.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @return the number of matching ddm form instance record versions
	 */
	@Override
	public int countByFormInstanceRecordId(long formInstanceRecordId) {
		return _collectionPersistenceFinderByFormInstanceRecordId.count(
			finderCache, new Object[] {formInstanceRecordId});
	}

	private CollectionPersistenceFinder
		<DDMFormInstanceRecordVersion, NoSuchFormInstanceRecordVersionException>
			_collectionPersistenceFinderByU_F;

	/**
	 * Returns an ordered range of all the ddm form instance record versions where userId = &#63; and formInstanceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instance record versions
	 */
	@Override
	public List<DDMFormInstanceRecordVersion> findByU_F(
		long userId, long formInstanceId, int start, int end,
		OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_F.find(
			finderCache, new Object[] {userId, formInstanceId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm form instance record version in the ordered set where userId = &#63; and formInstanceId = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record version
	 * @throws NoSuchFormInstanceRecordVersionException if a matching ddm form instance record version could not be found
	 */
	@Override
	public DDMFormInstanceRecordVersion findByU_F_First(
			long userId, long formInstanceId,
			OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator)
		throws NoSuchFormInstanceRecordVersionException {

		return _collectionPersistenceFinderByU_F.findFirst(
			finderCache, new Object[] {userId, formInstanceId},
			orderByComparator);
	}

	/**
	 * Returns the first ddm form instance record version in the ordered set where userId = &#63; and formInstanceId = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record version, or <code>null</code> if a matching ddm form instance record version could not be found
	 */
	@Override
	public DDMFormInstanceRecordVersion fetchByU_F_First(
		long userId, long formInstanceId,
		OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator) {

		return _collectionPersistenceFinderByU_F.fetchFirst(
			finderCache, new Object[] {userId, formInstanceId},
			orderByComparator);
	}

	/**
	 * Removes all the ddm form instance record versions where userId = &#63; and formInstanceId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 */
	@Override
	public void removeByU_F(long userId, long formInstanceId) {
		_collectionPersistenceFinderByU_F.remove(
			finderCache, new Object[] {userId, formInstanceId});
	}

	/**
	 * Returns the number of ddm form instance record versions where userId = &#63; and formInstanceId = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @return the number of matching ddm form instance record versions
	 */
	@Override
	public int countByU_F(long userId, long formInstanceId) {
		return _collectionPersistenceFinderByU_F.count(
			finderCache, new Object[] {userId, formInstanceId});
	}

	private CollectionPersistenceFinder
		<DDMFormInstanceRecordVersion, NoSuchFormInstanceRecordVersionException>
			_collectionPersistenceFinderByF_F;

	/**
	 * Returns an ordered range of all the ddm form instance record versions where formInstanceId = &#63; and formInstanceVersion = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceId the form instance ID
	 * @param formInstanceVersion the form instance version
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instance record versions
	 */
	@Override
	public List<DDMFormInstanceRecordVersion> findByF_F(
		long formInstanceId, String formInstanceVersion, int start, int end,
		OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByF_F.find(
			finderCache, new Object[] {formInstanceId, formInstanceVersion},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm form instance record version in the ordered set where formInstanceId = &#63; and formInstanceVersion = &#63;.
	 *
	 * @param formInstanceId the form instance ID
	 * @param formInstanceVersion the form instance version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record version
	 * @throws NoSuchFormInstanceRecordVersionException if a matching ddm form instance record version could not be found
	 */
	@Override
	public DDMFormInstanceRecordVersion findByF_F_First(
			long formInstanceId, String formInstanceVersion,
			OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator)
		throws NoSuchFormInstanceRecordVersionException {

		return _collectionPersistenceFinderByF_F.findFirst(
			finderCache, new Object[] {formInstanceId, formInstanceVersion},
			orderByComparator);
	}

	/**
	 * Returns the first ddm form instance record version in the ordered set where formInstanceId = &#63; and formInstanceVersion = &#63;.
	 *
	 * @param formInstanceId the form instance ID
	 * @param formInstanceVersion the form instance version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record version, or <code>null</code> if a matching ddm form instance record version could not be found
	 */
	@Override
	public DDMFormInstanceRecordVersion fetchByF_F_First(
		long formInstanceId, String formInstanceVersion,
		OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator) {

		return _collectionPersistenceFinderByF_F.fetchFirst(
			finderCache, new Object[] {formInstanceId, formInstanceVersion},
			orderByComparator);
	}

	/**
	 * Removes all the ddm form instance record versions where formInstanceId = &#63; and formInstanceVersion = &#63; from the database.
	 *
	 * @param formInstanceId the form instance ID
	 * @param formInstanceVersion the form instance version
	 */
	@Override
	public void removeByF_F(long formInstanceId, String formInstanceVersion) {
		_collectionPersistenceFinderByF_F.remove(
			finderCache, new Object[] {formInstanceId, formInstanceVersion});
	}

	/**
	 * Returns the number of ddm form instance record versions where formInstanceId = &#63; and formInstanceVersion = &#63;.
	 *
	 * @param formInstanceId the form instance ID
	 * @param formInstanceVersion the form instance version
	 * @return the number of matching ddm form instance record versions
	 */
	@Override
	public int countByF_F(long formInstanceId, String formInstanceVersion) {
		return _collectionPersistenceFinderByF_F.count(
			finderCache, new Object[] {formInstanceId, formInstanceVersion});
	}

	private UniquePersistenceFinder
		<DDMFormInstanceRecordVersion, NoSuchFormInstanceRecordVersionException>
			_uniquePersistenceFinderByF_V;

	/**
	 * Returns the ddm form instance record version where formInstanceRecordId = &#63; and version = &#63; or throws a <code>NoSuchFormInstanceRecordVersionException</code> if it could not be found.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param version the version
	 * @return the matching ddm form instance record version
	 * @throws NoSuchFormInstanceRecordVersionException if a matching ddm form instance record version could not be found
	 */
	@Override
	public DDMFormInstanceRecordVersion findByF_V(
			long formInstanceRecordId, String version)
		throws NoSuchFormInstanceRecordVersionException {

		return _uniquePersistenceFinderByF_V.find(
			finderCache, new Object[] {formInstanceRecordId, version});
	}

	/**
	 * Returns the ddm form instance record version where formInstanceRecordId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm form instance record version, or <code>null</code> if a matching ddm form instance record version could not be found
	 */
	@Override
	public DDMFormInstanceRecordVersion fetchByF_V(
		long formInstanceRecordId, String version, boolean useFinderCache) {

		return _uniquePersistenceFinderByF_V.fetch(
			finderCache, new Object[] {formInstanceRecordId, version},
			useFinderCache);
	}

	/**
	 * Removes the ddm form instance record version where formInstanceRecordId = &#63; and version = &#63; from the database.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param version the version
	 * @return the ddm form instance record version that was removed
	 */
	@Override
	public DDMFormInstanceRecordVersion removeByF_V(
			long formInstanceRecordId, String version)
		throws NoSuchFormInstanceRecordVersionException {

		DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion = findByF_V(
			formInstanceRecordId, version);

		return remove(ddmFormInstanceRecordVersion);
	}

	/**
	 * Returns the number of ddm form instance record versions where formInstanceRecordId = &#63; and version = &#63;.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param version the version
	 * @return the number of matching ddm form instance record versions
	 */
	@Override
	public int countByF_V(long formInstanceRecordId, String version) {
		return _uniquePersistenceFinderByF_V.count(
			finderCache, new Object[] {formInstanceRecordId, version});
	}

	private CollectionPersistenceFinder
		<DDMFormInstanceRecordVersion, NoSuchFormInstanceRecordVersionException>
			_collectionPersistenceFinderByF_S;

	/**
	 * Returns an ordered range of all the ddm form instance record versions where formInstanceRecordId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param status the status
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instance record versions
	 */
	@Override
	public List<DDMFormInstanceRecordVersion> findByF_S(
		long formInstanceRecordId, int status, int start, int end,
		OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByF_S.find(
			finderCache, new Object[] {formInstanceRecordId, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm form instance record version in the ordered set where formInstanceRecordId = &#63; and status = &#63;.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record version
	 * @throws NoSuchFormInstanceRecordVersionException if a matching ddm form instance record version could not be found
	 */
	@Override
	public DDMFormInstanceRecordVersion findByF_S_First(
			long formInstanceRecordId, int status,
			OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator)
		throws NoSuchFormInstanceRecordVersionException {

		return _collectionPersistenceFinderByF_S.findFirst(
			finderCache, new Object[] {formInstanceRecordId, status},
			orderByComparator);
	}

	/**
	 * Returns the first ddm form instance record version in the ordered set where formInstanceRecordId = &#63; and status = &#63;.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record version, or <code>null</code> if a matching ddm form instance record version could not be found
	 */
	@Override
	public DDMFormInstanceRecordVersion fetchByF_S_First(
		long formInstanceRecordId, int status,
		OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator) {

		return _collectionPersistenceFinderByF_S.fetchFirst(
			finderCache, new Object[] {formInstanceRecordId, status},
			orderByComparator);
	}

	/**
	 * Removes all the ddm form instance record versions where formInstanceRecordId = &#63; and status = &#63; from the database.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param status the status
	 */
	@Override
	public void removeByF_S(long formInstanceRecordId, int status) {
		_collectionPersistenceFinderByF_S.remove(
			finderCache, new Object[] {formInstanceRecordId, status});
	}

	/**
	 * Returns the number of ddm form instance record versions where formInstanceRecordId = &#63; and status = &#63;.
	 *
	 * @param formInstanceRecordId the form instance record ID
	 * @param status the status
	 * @return the number of matching ddm form instance record versions
	 */
	@Override
	public int countByF_S(long formInstanceRecordId, int status) {
		return _collectionPersistenceFinderByF_S.count(
			finderCache, new Object[] {formInstanceRecordId, status});
	}

	private CollectionPersistenceFinder
		<DDMFormInstanceRecordVersion, NoSuchFormInstanceRecordVersionException>
			_collectionPersistenceFinderByU_F_F_S;

	/**
	 * Returns an ordered range of all the ddm form instance record versions where userId = &#63; and formInstanceId = &#63; and formInstanceVersion = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMFormInstanceRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param formInstanceVersion the form instance version
	 * @param status the status
	 * @param start the lower bound of the range of ddm form instance record versions
	 * @param end the upper bound of the range of ddm form instance record versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm form instance record versions
	 */
	@Override
	public List<DDMFormInstanceRecordVersion> findByU_F_F_S(
		long userId, long formInstanceId, String formInstanceVersion,
		int status, int start, int end,
		OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_F_F_S.find(
			finderCache,
			new Object[] {userId, formInstanceId, formInstanceVersion, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first ddm form instance record version in the ordered set where userId = &#63; and formInstanceId = &#63; and formInstanceVersion = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param formInstanceVersion the form instance version
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record version
	 * @throws NoSuchFormInstanceRecordVersionException if a matching ddm form instance record version could not be found
	 */
	@Override
	public DDMFormInstanceRecordVersion findByU_F_F_S_First(
			long userId, long formInstanceId, String formInstanceVersion,
			int status,
			OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator)
		throws NoSuchFormInstanceRecordVersionException {

		return _collectionPersistenceFinderByU_F_F_S.findFirst(
			finderCache,
			new Object[] {userId, formInstanceId, formInstanceVersion, status},
			orderByComparator);
	}

	/**
	 * Returns the first ddm form instance record version in the ordered set where userId = &#63; and formInstanceId = &#63; and formInstanceVersion = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param formInstanceVersion the form instance version
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm form instance record version, or <code>null</code> if a matching ddm form instance record version could not be found
	 */
	@Override
	public DDMFormInstanceRecordVersion fetchByU_F_F_S_First(
		long userId, long formInstanceId, String formInstanceVersion,
		int status,
		OrderByComparator<DDMFormInstanceRecordVersion> orderByComparator) {

		return _collectionPersistenceFinderByU_F_F_S.fetchFirst(
			finderCache,
			new Object[] {userId, formInstanceId, formInstanceVersion, status},
			orderByComparator);
	}

	/**
	 * Removes all the ddm form instance record versions where userId = &#63; and formInstanceId = &#63; and formInstanceVersion = &#63; and status = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param formInstanceVersion the form instance version
	 * @param status the status
	 */
	@Override
	public void removeByU_F_F_S(
		long userId, long formInstanceId, String formInstanceVersion,
		int status) {

		_collectionPersistenceFinderByU_F_F_S.remove(
			finderCache,
			new Object[] {userId, formInstanceId, formInstanceVersion, status});
	}

	/**
	 * Returns the number of ddm form instance record versions where userId = &#63; and formInstanceId = &#63; and formInstanceVersion = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param formInstanceId the form instance ID
	 * @param formInstanceVersion the form instance version
	 * @param status the status
	 * @return the number of matching ddm form instance record versions
	 */
	@Override
	public int countByU_F_F_S(
		long userId, long formInstanceId, String formInstanceVersion,
		int status) {

		return _collectionPersistenceFinderByU_F_F_S.count(
			finderCache,
			new Object[] {userId, formInstanceId, formInstanceVersion, status});
	}

	public DDMFormInstanceRecordVersionPersistenceImpl() {
		setModelClass(DDMFormInstanceRecordVersion.class);

		setModelImplClass(DDMFormInstanceRecordVersionImpl.class);
		setModelPKClass(long.class);

		setTable(DDMFormInstanceRecordVersionTable.INSTANCE);
	}

	/**
	 * Creates a new ddm form instance record version with the primary key. Does not add the ddm form instance record version to the database.
	 *
	 * @param formInstanceRecordVersionId the primary key for the new ddm form instance record version
	 * @return the new ddm form instance record version
	 */
	@Override
	public DDMFormInstanceRecordVersion create(
		long formInstanceRecordVersionId) {

		DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion =
			new DDMFormInstanceRecordVersionImpl();

		ddmFormInstanceRecordVersion.setNew(true);
		ddmFormInstanceRecordVersion.setPrimaryKey(formInstanceRecordVersionId);

		ddmFormInstanceRecordVersion.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return ddmFormInstanceRecordVersion;
	}

	/**
	 * Removes the ddm form instance record version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param formInstanceRecordVersionId the primary key of the ddm form instance record version
	 * @return the ddm form instance record version that was removed
	 * @throws NoSuchFormInstanceRecordVersionException if a ddm form instance record version with the primary key could not be found
	 */
	@Override
	public DDMFormInstanceRecordVersion remove(long formInstanceRecordVersionId)
		throws NoSuchFormInstanceRecordVersionException {

		return remove((Serializable)formInstanceRecordVersionId);
	}

	@Override
	protected DDMFormInstanceRecordVersion removeImpl(
		DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ddmFormInstanceRecordVersion)) {
				ddmFormInstanceRecordVersion =
					(DDMFormInstanceRecordVersion)session.get(
						DDMFormInstanceRecordVersionImpl.class,
						ddmFormInstanceRecordVersion.getPrimaryKeyObj());
			}

			if ((ddmFormInstanceRecordVersion != null) &&
				ctPersistenceHelper.isRemove(ddmFormInstanceRecordVersion)) {

				session.delete(ddmFormInstanceRecordVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ddmFormInstanceRecordVersion != null) {
			clearCache(ddmFormInstanceRecordVersion);
		}

		return ddmFormInstanceRecordVersion;
	}

	@Override
	public DDMFormInstanceRecordVersion updateImpl(
		DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion) {

		boolean isNew = ddmFormInstanceRecordVersion.isNew();

		if (!(ddmFormInstanceRecordVersion instanceof
				DDMFormInstanceRecordVersionModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					ddmFormInstanceRecordVersion.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					ddmFormInstanceRecordVersion);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ddmFormInstanceRecordVersion proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DDMFormInstanceRecordVersion implementation " +
					ddmFormInstanceRecordVersion.getClass());
		}

		DDMFormInstanceRecordVersionModelImpl
			ddmFormInstanceRecordVersionModelImpl =
				(DDMFormInstanceRecordVersionModelImpl)
					ddmFormInstanceRecordVersion;

		if (isNew && (ddmFormInstanceRecordVersion.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				ddmFormInstanceRecordVersion.setCreateDate(date);
			}
			else {
				ddmFormInstanceRecordVersion.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ddmFormInstanceRecordVersion)) {
				if (!isNew) {
					session.evict(
						DDMFormInstanceRecordVersionImpl.class,
						ddmFormInstanceRecordVersion.getPrimaryKeyObj());
				}

				session.save(ddmFormInstanceRecordVersion);
			}
			else {
				ddmFormInstanceRecordVersion =
					(DDMFormInstanceRecordVersion)session.merge(
						ddmFormInstanceRecordVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ddmFormInstanceRecordVersion, false);

		if (isNew) {
			ddmFormInstanceRecordVersion.setNew(false);
		}

		ddmFormInstanceRecordVersion.resetOriginalValues();

		return ddmFormInstanceRecordVersion;
	}

	/**
	 * Returns the ddm form instance record version with the primary key or throws a <code>NoSuchFormInstanceRecordVersionException</code> if it could not be found.
	 *
	 * @param formInstanceRecordVersionId the primary key of the ddm form instance record version
	 * @return the ddm form instance record version
	 * @throws NoSuchFormInstanceRecordVersionException if a ddm form instance record version with the primary key could not be found
	 */
	@Override
	public DDMFormInstanceRecordVersion findByPrimaryKey(
			long formInstanceRecordVersionId)
		throws NoSuchFormInstanceRecordVersionException {

		return findByPrimaryKey((Serializable)formInstanceRecordVersionId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the ddm form instance record version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param formInstanceRecordVersionId the primary key of the ddm form instance record version
	 * @return the ddm form instance record version, or <code>null</code> if a ddm form instance record version with the primary key could not be found
	 */
	@Override
	public DDMFormInstanceRecordVersion fetchByPrimaryKey(
		long formInstanceRecordVersionId) {

		return fetchByPrimaryKey((Serializable)formInstanceRecordVersionId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "formInstanceRecordVersionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DDMFORMINSTANCERECORDVERSION;
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
		return DDMFormInstanceRecordVersionModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DDMFormInstanceRecordVersion";
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
		ctMergeColumnNames.add("formInstanceId");
		ctMergeColumnNames.add("formInstanceVersion");
		ctMergeColumnNames.add("formInstanceRecordId");
		ctMergeColumnNames.add("version");
		ctMergeColumnNames.add("storageId");
		ctMergeColumnNames.add("status");
		ctMergeColumnNames.add("statusByUserId");
		ctMergeColumnNames.add("statusByUserName");
		ctMergeColumnNames.add("statusDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("formInstanceRecordVersionId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"formInstanceRecordId", "version"});
	}

	/**
	 * Initializes the ddm form instance record version persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByFormInstanceRecordId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByFormInstanceRecordId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"formInstanceRecordId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByFormInstanceRecordId",
					new String[] {Long.class.getName()},
					new String[] {"formInstanceRecordId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByFormInstanceRecordId",
					new String[] {Long.class.getName()},
					new String[] {"formInstanceRecordId"}, false),
				_SQL_SELECT_DDMFORMINSTANCERECORDVERSION_WHERE,
				_SQL_COUNT_DDMFORMINSTANCERECORDVERSION_WHERE,
				DDMFormInstanceRecordVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"ddmFormInstanceRecordVersion.", "formInstanceRecordId",
					FinderColumn.Type.LONG, "=", true, true,
					DDMFormInstanceRecordVersion::getFormInstanceRecordId));

		_collectionPersistenceFinderByU_F = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_F",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"userId", "formInstanceId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_F",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"userId", "formInstanceId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_F",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"userId", "formInstanceId"}, false),
			_SQL_SELECT_DDMFORMINSTANCERECORDVERSION_WHERE,
			_SQL_COUNT_DDMFORMINSTANCERECORDVERSION_WHERE,
			DDMFormInstanceRecordVersionModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"ddmFormInstanceRecordVersion.", "userId",
				FinderColumn.Type.LONG, "=", true, true,
				DDMFormInstanceRecordVersion::getUserId),
			new FinderColumn<>(
				"ddmFormInstanceRecordVersion.", "formInstanceId",
				FinderColumn.Type.LONG, "=", true, true,
				DDMFormInstanceRecordVersion::getFormInstanceId));

		_collectionPersistenceFinderByF_F = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByF_F",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"formInstanceId", "formInstanceVersion"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByF_F",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"formInstanceId", "formInstanceVersion"}, 0, 2,
				true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByF_F",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"formInstanceId", "formInstanceVersion"}, 0, 2,
				false, null),
			_SQL_SELECT_DDMFORMINSTANCERECORDVERSION_WHERE,
			_SQL_COUNT_DDMFORMINSTANCERECORDVERSION_WHERE,
			DDMFormInstanceRecordVersionModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"ddmFormInstanceRecordVersion.", "formInstanceId",
				FinderColumn.Type.LONG, "=", true, true,
				DDMFormInstanceRecordVersion::getFormInstanceId),
			new FinderColumn<>(
				"ddmFormInstanceRecordVersion.", "formInstanceVersion",
				FinderColumn.Type.STRING, "=", true, true,
				DDMFormInstanceRecordVersion::getFormInstanceVersion));

		_uniquePersistenceFinderByF_V = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByF_V",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"formInstanceRecordId", "version"}, 0, 2, false,
				DDMFormInstanceRecordVersion::getFormInstanceRecordId,
				convertNullFunction(DDMFormInstanceRecordVersion::getVersion)),
			_SQL_SELECT_DDMFORMINSTANCERECORDVERSION_WHERE, "",
			new FinderColumn<>(
				"ddmFormInstanceRecordVersion.", "formInstanceRecordId",
				FinderColumn.Type.LONG, "=", true, true,
				DDMFormInstanceRecordVersion::getFormInstanceRecordId),
			new FinderColumn<>(
				"ddmFormInstanceRecordVersion.", "version",
				FinderColumn.Type.STRING, "=", true, true,
				DDMFormInstanceRecordVersion::getVersion));

		_collectionPersistenceFinderByF_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByF_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"formInstanceRecordId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByF_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"formInstanceRecordId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByF_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"formInstanceRecordId", "status"}, false),
			_SQL_SELECT_DDMFORMINSTANCERECORDVERSION_WHERE,
			_SQL_COUNT_DDMFORMINSTANCERECORDVERSION_WHERE,
			DDMFormInstanceRecordVersionModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"ddmFormInstanceRecordVersion.", "formInstanceRecordId",
				FinderColumn.Type.LONG, "=", true, true,
				DDMFormInstanceRecordVersion::getFormInstanceRecordId),
			new FinderColumn<>(
				"ddmFormInstanceRecordVersion.", "status",
				FinderColumn.Type.INTEGER, "=", true, true,
				DDMFormInstanceRecordVersion::getStatus));

		_collectionPersistenceFinderByU_F_F_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_F_F_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"userId", "formInstanceId", "formInstanceVersion",
						"status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_F_F_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {
						"userId", "formInstanceId", "formInstanceVersion",
						"status"
					},
					0, 4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_F_F_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Integer.class.getName()
					},
					new String[] {
						"userId", "formInstanceId", "formInstanceVersion",
						"status"
					},
					0, 4, false, null),
				_SQL_SELECT_DDMFORMINSTANCERECORDVERSION_WHERE,
				_SQL_COUNT_DDMFORMINSTANCERECORDVERSION_WHERE,
				DDMFormInstanceRecordVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"ddmFormInstanceRecordVersion.", "userId",
					FinderColumn.Type.LONG, "=", true, true,
					DDMFormInstanceRecordVersion::getUserId),
				new FinderColumn<>(
					"ddmFormInstanceRecordVersion.", "formInstanceId",
					FinderColumn.Type.LONG, "=", true, true,
					DDMFormInstanceRecordVersion::getFormInstanceId),
				new FinderColumn<>(
					"ddmFormInstanceRecordVersion.", "formInstanceVersion",
					FinderColumn.Type.STRING, "=", true, true,
					DDMFormInstanceRecordVersion::getFormInstanceVersion),
				new FinderColumn<>(
					"ddmFormInstanceRecordVersion.", "status",
					FinderColumn.Type.INTEGER, "=", true, true,
					DDMFormInstanceRecordVersion::getStatus));

		DDMFormInstanceRecordVersionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		DDMFormInstanceRecordVersionUtil.setPersistence(null);

		entityCache.removeCache(
			DDMFormInstanceRecordVersionImpl.class.getName());
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		DDMFormInstanceRecordVersionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_DDMFORMINSTANCERECORDVERSION =
		"SELECT ddmFormInstanceRecordVersion FROM DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion";

	private static final String _SQL_SELECT_DDMFORMINSTANCERECORDVERSION_WHERE =
		"SELECT ddmFormInstanceRecordVersion FROM DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion WHERE ";

	private static final String _SQL_COUNT_DDMFORMINSTANCERECORDVERSION_WHERE =
		"SELECT COUNT(ddmFormInstanceRecordVersion) FROM DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-878710435