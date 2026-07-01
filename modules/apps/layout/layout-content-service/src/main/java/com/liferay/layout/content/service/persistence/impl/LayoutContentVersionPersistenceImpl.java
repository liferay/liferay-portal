/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.service.persistence.impl;

import com.liferay.layout.content.exception.DuplicateLayoutContentVersionExternalReferenceCodeException;
import com.liferay.layout.content.exception.NoSuchLayoutContentVersionException;
import com.liferay.layout.content.model.LayoutContentVersion;
import com.liferay.layout.content.model.LayoutContentVersionTable;
import com.liferay.layout.content.model.impl.LayoutContentVersionImpl;
import com.liferay.layout.content.model.impl.LayoutContentVersionModelImpl;
import com.liferay.layout.content.service.persistence.LayoutContentVersionPersistence;
import com.liferay.layout.content.service.persistence.LayoutContentVersionUtil;
import com.liferay.layout.content.service.persistence.impl.constants.LayoutContentVersionPersistenceConstants;
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
 * The persistence implementation for the layout content version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Lourdes Fernández Besada
 * @generated
 */
@Component(service = LayoutContentVersionPersistence.class)
public class LayoutContentVersionPersistenceImpl
	extends BasePersistenceImpl
		<LayoutContentVersion, NoSuchLayoutContentVersionException>
	implements LayoutContentVersionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LayoutContentVersionUtil</code> to access the layout content version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LayoutContentVersionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<LayoutContentVersion, NoSuchLayoutContentVersionException>
			_collectionPersistenceFinderByPlid;

	/**
	 * Returns an ordered range of all the layout content versions where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutContentVersionModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout content versions
	 * @param end the upper bound of the range of layout content versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout content versions
	 */
	@Override
	public List<LayoutContentVersion> findByPlid(
		long plid, int start, int end,
		OrderByComparator<LayoutContentVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByPlid.find(
			finderCache, new Object[] {plid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout content version in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version
	 * @throws NoSuchLayoutContentVersionException if a matching layout content version could not be found
	 */
	@Override
	public LayoutContentVersion findByPlid_First(
			long plid,
			OrderByComparator<LayoutContentVersion> orderByComparator)
		throws NoSuchLayoutContentVersionException {

		return _collectionPersistenceFinderByPlid.findFirst(
			finderCache, new Object[] {plid}, orderByComparator);
	}

	/**
	 * Returns the first layout content version in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version, or <code>null</code> if a matching layout content version could not be found
	 */
	@Override
	public LayoutContentVersion fetchByPlid_First(
		long plid, OrderByComparator<LayoutContentVersion> orderByComparator) {

		return _collectionPersistenceFinderByPlid.fetchFirst(
			finderCache, new Object[] {plid}, orderByComparator);
	}

	/**
	 * Removes all the layout content versions where plid = &#63; from the database.
	 *
	 * @param plid the plid
	 */
	@Override
	public void removeByPlid(long plid) {
		_collectionPersistenceFinderByPlid.remove(
			finderCache, new Object[] {plid});
	}

	/**
	 * Returns the number of layout content versions where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the number of matching layout content versions
	 */
	@Override
	public int countByPlid(long plid) {
		return _collectionPersistenceFinderByPlid.count(
			finderCache, new Object[] {plid});
	}

	private CollectionPersistenceFinder
		<LayoutContentVersion, NoSuchLayoutContentVersionException>
			_collectionPersistenceFinderByG_DH;

	/**
	 * Returns an ordered range of all the layout content versions where groupId = &#63; and dataHash = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutContentVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param dataHash the data hash
	 * @param start the lower bound of the range of layout content versions
	 * @param end the upper bound of the range of layout content versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout content versions
	 */
	@Override
	public List<LayoutContentVersion> findByG_DH(
		long groupId, String dataHash, int start, int end,
		OrderByComparator<LayoutContentVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_DH.find(
			finderCache, new Object[] {groupId, dataHash}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout content version in the ordered set where groupId = &#63; and dataHash = &#63;.
	 *
	 * @param groupId the group ID
	 * @param dataHash the data hash
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version
	 * @throws NoSuchLayoutContentVersionException if a matching layout content version could not be found
	 */
	@Override
	public LayoutContentVersion findByG_DH_First(
			long groupId, String dataHash,
			OrderByComparator<LayoutContentVersion> orderByComparator)
		throws NoSuchLayoutContentVersionException {

		return _collectionPersistenceFinderByG_DH.findFirst(
			finderCache, new Object[] {groupId, dataHash}, orderByComparator);
	}

	/**
	 * Returns the first layout content version in the ordered set where groupId = &#63; and dataHash = &#63;.
	 *
	 * @param groupId the group ID
	 * @param dataHash the data hash
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version, or <code>null</code> if a matching layout content version could not be found
	 */
	@Override
	public LayoutContentVersion fetchByG_DH_First(
		long groupId, String dataHash,
		OrderByComparator<LayoutContentVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_DH.fetchFirst(
			finderCache, new Object[] {groupId, dataHash}, orderByComparator);
	}

	/**
	 * Removes all the layout content versions where groupId = &#63; and dataHash = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param dataHash the data hash
	 */
	@Override
	public void removeByG_DH(long groupId, String dataHash) {
		_collectionPersistenceFinderByG_DH.remove(
			finderCache, new Object[] {groupId, dataHash});
	}

	/**
	 * Returns the number of layout content versions where groupId = &#63; and dataHash = &#63;.
	 *
	 * @param groupId the group ID
	 * @param dataHash the data hash
	 * @return the number of matching layout content versions
	 */
	@Override
	public int countByG_DH(long groupId, String dataHash) {
		return _collectionPersistenceFinderByG_DH.count(
			finderCache, new Object[] {groupId, dataHash});
	}

	private CollectionPersistenceFinder
		<LayoutContentVersion, NoSuchLayoutContentVersionException>
			_collectionPersistenceFinderByG_S;

	/**
	 * Returns an ordered range of all the layout content versions where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutContentVersionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of layout content versions
	 * @param end the upper bound of the range of layout content versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout content versions
	 */
	@Override
	public List<LayoutContentVersion> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<LayoutContentVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_S.find(
			finderCache, new Object[] {groupId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout content version in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version
	 * @throws NoSuchLayoutContentVersionException if a matching layout content version could not be found
	 */
	@Override
	public LayoutContentVersion findByG_S_First(
			long groupId, int status,
			OrderByComparator<LayoutContentVersion> orderByComparator)
		throws NoSuchLayoutContentVersionException {

		return _collectionPersistenceFinderByG_S.findFirst(
			finderCache, new Object[] {groupId, status}, orderByComparator);
	}

	/**
	 * Returns the first layout content version in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version, or <code>null</code> if a matching layout content version could not be found
	 */
	@Override
	public LayoutContentVersion fetchByG_S_First(
		long groupId, int status,
		OrderByComparator<LayoutContentVersion> orderByComparator) {

		return _collectionPersistenceFinderByG_S.fetchFirst(
			finderCache, new Object[] {groupId, status}, orderByComparator);
	}

	/**
	 * Removes all the layout content versions where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	@Override
	public void removeByG_S(long groupId, int status) {
		_collectionPersistenceFinderByG_S.remove(
			finderCache, new Object[] {groupId, status});
	}

	/**
	 * Returns the number of layout content versions where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching layout content versions
	 */
	@Override
	public int countByG_S(long groupId, int status) {
		return _collectionPersistenceFinderByG_S.count(
			finderCache, new Object[] {groupId, status});
	}

	private UniquePersistenceFinder
		<LayoutContentVersion, NoSuchLayoutContentVersionException>
			_uniquePersistenceFinderByP_V;

	/**
	 * Returns the layout content version where plid = &#63; and version = &#63; or throws a <code>NoSuchLayoutContentVersionException</code> if it could not be found.
	 *
	 * @param plid the plid
	 * @param version the version
	 * @return the matching layout content version
	 * @throws NoSuchLayoutContentVersionException if a matching layout content version could not be found
	 */
	@Override
	public LayoutContentVersion findByP_V(long plid, int version)
		throws NoSuchLayoutContentVersionException {

		return _uniquePersistenceFinderByP_V.find(
			finderCache, new Object[] {plid, version});
	}

	/**
	 * Returns the layout content version where plid = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param plid the plid
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout content version, or <code>null</code> if a matching layout content version could not be found
	 */
	@Override
	public LayoutContentVersion fetchByP_V(
		long plid, int version, boolean useFinderCache) {

		return _uniquePersistenceFinderByP_V.fetch(
			finderCache, new Object[] {plid, version}, useFinderCache);
	}

	/**
	 * Removes the layout content version where plid = &#63; and version = &#63; from the database.
	 *
	 * @param plid the plid
	 * @param version the version
	 * @return the layout content version that was removed
	 */
	@Override
	public LayoutContentVersion removeByP_V(long plid, int version)
		throws NoSuchLayoutContentVersionException {

		LayoutContentVersion layoutContentVersion = findByP_V(plid, version);

		return remove(layoutContentVersion);
	}

	/**
	 * Returns the number of layout content versions where plid = &#63; and version = &#63;.
	 *
	 * @param plid the plid
	 * @param version the version
	 * @return the number of matching layout content versions
	 */
	@Override
	public int countByP_V(long plid, int version) {
		return _uniquePersistenceFinderByP_V.count(
			finderCache, new Object[] {plid, version});
	}

	private CollectionPersistenceFinder
		<LayoutContentVersion, NoSuchLayoutContentVersionException>
			_collectionPersistenceFinderByP_S;

	/**
	 * Returns an ordered range of all the layout content versions where plid = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutContentVersionModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param status the status
	 * @param start the lower bound of the range of layout content versions
	 * @param end the upper bound of the range of layout content versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout content versions
	 */
	@Override
	public List<LayoutContentVersion> findByP_S(
		long plid, int status, int start, int end,
		OrderByComparator<LayoutContentVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_S.find(
			finderCache, new Object[] {plid, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout content version in the ordered set where plid = &#63; and status = &#63;.
	 *
	 * @param plid the plid
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version
	 * @throws NoSuchLayoutContentVersionException if a matching layout content version could not be found
	 */
	@Override
	public LayoutContentVersion findByP_S_First(
			long plid, int status,
			OrderByComparator<LayoutContentVersion> orderByComparator)
		throws NoSuchLayoutContentVersionException {

		return _collectionPersistenceFinderByP_S.findFirst(
			finderCache, new Object[] {plid, status}, orderByComparator);
	}

	/**
	 * Returns the first layout content version in the ordered set where plid = &#63; and status = &#63;.
	 *
	 * @param plid the plid
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout content version, or <code>null</code> if a matching layout content version could not be found
	 */
	@Override
	public LayoutContentVersion fetchByP_S_First(
		long plid, int status,
		OrderByComparator<LayoutContentVersion> orderByComparator) {

		return _collectionPersistenceFinderByP_S.fetchFirst(
			finderCache, new Object[] {plid, status}, orderByComparator);
	}

	/**
	 * Removes all the layout content versions where plid = &#63; and status = &#63; from the database.
	 *
	 * @param plid the plid
	 * @param status the status
	 */
	@Override
	public void removeByP_S(long plid, int status) {
		_collectionPersistenceFinderByP_S.remove(
			finderCache, new Object[] {plid, status});
	}

	/**
	 * Returns the number of layout content versions where plid = &#63; and status = &#63;.
	 *
	 * @param plid the plid
	 * @param status the status
	 * @return the number of matching layout content versions
	 */
	@Override
	public int countByP_S(long plid, int status) {
		return _collectionPersistenceFinderByP_S.count(
			finderCache, new Object[] {plid, status});
	}

	private UniquePersistenceFinder
		<LayoutContentVersion, NoSuchLayoutContentVersionException>
			_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the layout content version where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchLayoutContentVersionException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout content version
	 * @throws NoSuchLayoutContentVersionException if a matching layout content version could not be found
	 */
	@Override
	public LayoutContentVersion findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchLayoutContentVersionException {

		return _uniquePersistenceFinderByERC_G.find(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the layout content version where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout content version, or <code>null</code> if a matching layout content version could not be found
	 */
	@Override
	public LayoutContentVersion fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			finderCache, new Object[] {externalReferenceCode, groupId},
			useFinderCache);
	}

	/**
	 * Removes the layout content version where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the layout content version that was removed
	 */
	@Override
	public LayoutContentVersion removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchLayoutContentVersionException {

		LayoutContentVersion layoutContentVersion = findByERC_G(
			externalReferenceCode, groupId);

		return remove(layoutContentVersion);
	}

	/**
	 * Returns the number of layout content versions where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching layout content versions
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	public LayoutContentVersionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("data", "data_");

		setDBColumnNames(dbColumnNames);

		setModelClass(LayoutContentVersion.class);

		setModelImplClass(LayoutContentVersionImpl.class);
		setModelPKClass(long.class);

		setTable(LayoutContentVersionTable.INSTANCE);
	}

	/**
	 * Creates a new layout content version with the primary key. Does not add the layout content version to the database.
	 *
	 * @param layoutContentVersionId the primary key for the new layout content version
	 * @return the new layout content version
	 */
	@Override
	public LayoutContentVersion create(long layoutContentVersionId) {
		LayoutContentVersion layoutContentVersion =
			new LayoutContentVersionImpl();

		layoutContentVersion.setNew(true);
		layoutContentVersion.setPrimaryKey(layoutContentVersionId);

		layoutContentVersion.setCompanyId(CompanyThreadLocal.getCompanyId());

		return layoutContentVersion;
	}

	/**
	 * Removes the layout content version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutContentVersionId the primary key of the layout content version
	 * @return the layout content version that was removed
	 * @throws NoSuchLayoutContentVersionException if a layout content version with the primary key could not be found
	 */
	@Override
	public LayoutContentVersion remove(long layoutContentVersionId)
		throws NoSuchLayoutContentVersionException {

		return remove((Serializable)layoutContentVersionId);
	}

	@Override
	protected LayoutContentVersion removeImpl(
		LayoutContentVersion layoutContentVersion) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(layoutContentVersion)) {
				layoutContentVersion = (LayoutContentVersion)session.get(
					LayoutContentVersionImpl.class,
					layoutContentVersion.getPrimaryKeyObj());
			}

			if (layoutContentVersion != null) {
				session.delete(layoutContentVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (layoutContentVersion != null) {
			clearCache(layoutContentVersion);
		}

		return layoutContentVersion;
	}

	@Override
	public LayoutContentVersion updateImpl(
		LayoutContentVersion layoutContentVersion) {

		boolean isNew = layoutContentVersion.isNew();

		if (!(layoutContentVersion instanceof LayoutContentVersionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(layoutContentVersion.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					layoutContentVersion);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in layoutContentVersion proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LayoutContentVersion implementation " +
					layoutContentVersion.getClass());
		}

		LayoutContentVersionModelImpl layoutContentVersionModelImpl =
			(LayoutContentVersionModelImpl)layoutContentVersion;

		if (Validator.isNull(layoutContentVersion.getExternalReferenceCode())) {
			layoutContentVersion.setExternalReferenceCode(
				String.valueOf(layoutContentVersion.getPrimaryKey()));
		}
		else {
			if (!Objects.equals(
					layoutContentVersionModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					layoutContentVersion.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = layoutContentVersion.getCompanyId();

					long groupId = layoutContentVersion.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = layoutContentVersion.getPrimaryKey();
					}

					try {
						layoutContentVersion.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								LayoutContentVersion.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								layoutContentVersion.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			LayoutContentVersion ercLayoutContentVersion = fetchByERC_G(
				layoutContentVersion.getExternalReferenceCode(),
				layoutContentVersion.getGroupId());

			if (isNew) {
				if (ercLayoutContentVersion != null) {
					throw new DuplicateLayoutContentVersionExternalReferenceCodeException(
						"Duplicate layout content version with external reference code " +
							layoutContentVersion.getExternalReferenceCode() +
								" and group " +
									layoutContentVersion.getGroupId());
				}
			}
			else {
				if ((ercLayoutContentVersion != null) &&
					(layoutContentVersion.getLayoutContentVersionId() !=
						ercLayoutContentVersion.getLayoutContentVersionId())) {

					throw new DuplicateLayoutContentVersionExternalReferenceCodeException(
						"Duplicate layout content version with external reference code " +
							layoutContentVersion.getExternalReferenceCode() +
								" and group " +
									layoutContentVersion.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (layoutContentVersion.getCreateDate() == null)) {
			if (serviceContext == null) {
				layoutContentVersion.setCreateDate(date);
			}
			else {
				layoutContentVersion.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!layoutContentVersionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				layoutContentVersion.setModifiedDate(date);
			}
			else {
				layoutContentVersion.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(layoutContentVersion);
			}
			else {
				layoutContentVersion = (LayoutContentVersion)session.merge(
					layoutContentVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(layoutContentVersion, false);

		if (isNew) {
			layoutContentVersion.setNew(false);
		}

		layoutContentVersion.resetOriginalValues();

		return layoutContentVersion;
	}

	/**
	 * Returns the layout content version with the primary key or throws a <code>NoSuchLayoutContentVersionException</code> if it could not be found.
	 *
	 * @param layoutContentVersionId the primary key of the layout content version
	 * @return the layout content version
	 * @throws NoSuchLayoutContentVersionException if a layout content version with the primary key could not be found
	 */
	@Override
	public LayoutContentVersion findByPrimaryKey(long layoutContentVersionId)
		throws NoSuchLayoutContentVersionException {

		return findByPrimaryKey((Serializable)layoutContentVersionId);
	}

	/**
	 * Returns the layout content version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutContentVersionId the primary key of the layout content version
	 * @return the layout content version, or <code>null</code> if a layout content version with the primary key could not be found
	 */
	@Override
	public LayoutContentVersion fetchByPrimaryKey(long layoutContentVersionId) {
		return fetchByPrimaryKey((Serializable)layoutContentVersionId);
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
		return "layoutContentVersionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAYOUTCONTENTVERSION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return LayoutContentVersionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the layout content version persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByPlid = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByPlid",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"plid"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByPlid",
				new String[] {Long.class.getName()}, new String[] {"plid"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByPlid",
				new String[] {Long.class.getName()}, new String[] {"plid"},
				false),
			_SQL_SELECT_LAYOUTCONTENTVERSION_WHERE,
			_SQL_COUNT_LAYOUTCONTENTVERSION_WHERE,
			LayoutContentVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"layoutContentVersion.", "plid", FinderColumn.Type.LONG, "=",
				true, true, LayoutContentVersion::getPlid));

		_collectionPersistenceFinderByG_DH = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_DH",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "dataHash"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_DH",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "dataHash"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_DH",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "dataHash"}, 0, 2, false, null),
			_SQL_SELECT_LAYOUTCONTENTVERSION_WHERE,
			_SQL_COUNT_LAYOUTCONTENTVERSION_WHERE,
			LayoutContentVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"layoutContentVersion.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, LayoutContentVersion::getGroupId),
			new FinderColumn<>(
				"layoutContentVersion.", "dataHash", FinderColumn.Type.STRING,
				"=", true, true, LayoutContentVersion::getDataHash));

		_collectionPersistenceFinderByG_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"groupId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"groupId", "status"}, false),
			_SQL_SELECT_LAYOUTCONTENTVERSION_WHERE,
			_SQL_COUNT_LAYOUTCONTENTVERSION_WHERE,
			LayoutContentVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"layoutContentVersion.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, LayoutContentVersion::getGroupId),
			new FinderColumn<>(
				"layoutContentVersion.", "status", FinderColumn.Type.INTEGER,
				"=", true, true, LayoutContentVersion::getStatus));

		_uniquePersistenceFinderByP_V = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByP_V",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"plid", "version"}, 0, 0, false,
				LayoutContentVersion::getPlid,
				LayoutContentVersion::getVersion),
			_SQL_SELECT_LAYOUTCONTENTVERSION_WHERE, "",
			new FinderColumn<>(
				"layoutContentVersion.", "plid", FinderColumn.Type.LONG, "=",
				true, true, LayoutContentVersion::getPlid),
			new FinderColumn<>(
				"layoutContentVersion.", "version", FinderColumn.Type.INTEGER,
				"=", true, true, LayoutContentVersion::getVersion));

		_collectionPersistenceFinderByP_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"plid", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"plid", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"plid", "status"}, false),
			_SQL_SELECT_LAYOUTCONTENTVERSION_WHERE,
			_SQL_COUNT_LAYOUTCONTENTVERSION_WHERE,
			LayoutContentVersionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"layoutContentVersion.", "plid", FinderColumn.Type.LONG, "=",
				true, true, LayoutContentVersion::getPlid),
			new FinderColumn<>(
				"layoutContentVersion.", "status", FinderColumn.Type.INTEGER,
				"=", true, true, LayoutContentVersion::getStatus));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(
					LayoutContentVersion::getExternalReferenceCode),
				LayoutContentVersion::getGroupId),
			_SQL_SELECT_LAYOUTCONTENTVERSION_WHERE, "",
			new FinderColumn<>(
				"layoutContentVersion.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				LayoutContentVersion::getExternalReferenceCode),
			new FinderColumn<>(
				"layoutContentVersion.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, LayoutContentVersion::getGroupId));

		LayoutContentVersionUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		LayoutContentVersionUtil.setPersistence(null);

		entityCache.removeCache(LayoutContentVersionImpl.class.getName());
	}

	@Override
	@Reference(
		target = LayoutContentVersionPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = LayoutContentVersionPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = LayoutContentVersionPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		LayoutContentVersionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LAYOUTCONTENTVERSION =
		"SELECT layoutContentVersion FROM LayoutContentVersion layoutContentVersion";

	private static final String _SQL_SELECT_LAYOUTCONTENTVERSION_WHERE =
		"SELECT layoutContentVersion FROM LayoutContentVersion layoutContentVersion WHERE ";

	private static final String _SQL_COUNT_LAYOUTCONTENTVERSION_WHERE =
		"SELECT COUNT(layoutContentVersion) FROM LayoutContentVersion layoutContentVersion WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No LayoutContentVersion exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutContentVersionPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"data"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-2012401171