/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.store.service.persistence.impl;

import com.liferay.change.tracking.store.exception.NoSuchContentException;
import com.liferay.change.tracking.store.model.CTSContent;
import com.liferay.change.tracking.store.model.CTSContentTable;
import com.liferay.change.tracking.store.model.impl.CTSContentImpl;
import com.liferay.change.tracking.store.model.impl.CTSContentModelImpl;
import com.liferay.change.tracking.store.service.persistence.CTSContentPersistence;
import com.liferay.change.tracking.store.service.persistence.CTSContentUtil;
import com.liferay.change.tracking.store.service.persistence.impl.constants.CTSPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
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
 * The persistence implementation for the cts content service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Shuyang Zhou
 * @generated
 */
@Component(service = CTSContentPersistence.class)
public class CTSContentPersistenceImpl
	extends BasePersistenceImpl<CTSContent, NoSuchContentException>
	implements CTSContentPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CTSContentUtil</code> to access the cts content persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CTSContentImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<CTSContent, NoSuchContentException>
		_collectionPersistenceFinderByR_P;

	/**
	 * Returns an ordered range of all the cts contents where repositoryId = &#63; and path = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSContentModelImpl</code>.
	 * </p>
	 *
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param start the lower bound of the range of cts contents
	 * @param end the upper bound of the range of cts contents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts contents
	 */
	@Override
	public List<CTSContent> findByR_P(
		long repositoryId, String path, int start, int end,
		OrderByComparator<CTSContent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_P.find(
			finderCache, new Object[] {repositoryId, path}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cts content in the ordered set where repositoryId = &#63; and path = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts content
	 * @throws NoSuchContentException if a matching cts content could not be found
	 */
	@Override
	public CTSContent findByR_P_First(
			long repositoryId, String path,
			OrderByComparator<CTSContent> orderByComparator)
		throws NoSuchContentException {

		return _collectionPersistenceFinderByR_P.findFirst(
			finderCache, new Object[] {repositoryId, path}, orderByComparator);
	}

	/**
	 * Returns the first cts content in the ordered set where repositoryId = &#63; and path = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts content, or <code>null</code> if a matching cts content could not be found
	 */
	@Override
	public CTSContent fetchByR_P_First(
		long repositoryId, String path,
		OrderByComparator<CTSContent> orderByComparator) {

		return _collectionPersistenceFinderByR_P.fetchFirst(
			finderCache, new Object[] {repositoryId, path}, orderByComparator);
	}

	/**
	 * Removes all the cts contents where repositoryId = &#63; and path = &#63; from the database.
	 *
	 * @param repositoryId the repository ID
	 * @param path the path
	 */
	@Override
	public void removeByR_P(long repositoryId, String path) {
		_collectionPersistenceFinderByR_P.remove(
			finderCache, new Object[] {repositoryId, path});
	}

	/**
	 * Returns the number of cts contents where repositoryId = &#63; and path = &#63;.
	 *
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @return the number of matching cts contents
	 */
	@Override
	public int countByR_P(long repositoryId, String path) {
		return _collectionPersistenceFinderByR_P.count(
			finderCache, new Object[] {repositoryId, path});
	}

	private CollectionPersistenceFinder<CTSContent, NoSuchContentException>
		_collectionPersistenceFinderByC_R_S;

	/**
	 * Returns an ordered range of all the cts contents where companyId = &#63; and repositoryId = &#63; and storeType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSContentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param storeType the store type
	 * @param start the lower bound of the range of cts contents
	 * @param end the upper bound of the range of cts contents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts contents
	 */
	@Override
	public List<CTSContent> findByC_R_S(
		long companyId, long repositoryId, String storeType, int start, int end,
		OrderByComparator<CTSContent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_R_S.find(
			finderCache, new Object[] {companyId, repositoryId, storeType},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cts content in the ordered set where companyId = &#63; and repositoryId = &#63; and storeType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param storeType the store type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts content
	 * @throws NoSuchContentException if a matching cts content could not be found
	 */
	@Override
	public CTSContent findByC_R_S_First(
			long companyId, long repositoryId, String storeType,
			OrderByComparator<CTSContent> orderByComparator)
		throws NoSuchContentException {

		return _collectionPersistenceFinderByC_R_S.findFirst(
			finderCache, new Object[] {companyId, repositoryId, storeType},
			orderByComparator);
	}

	/**
	 * Returns the first cts content in the ordered set where companyId = &#63; and repositoryId = &#63; and storeType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param storeType the store type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts content, or <code>null</code> if a matching cts content could not be found
	 */
	@Override
	public CTSContent fetchByC_R_S_First(
		long companyId, long repositoryId, String storeType,
		OrderByComparator<CTSContent> orderByComparator) {

		return _collectionPersistenceFinderByC_R_S.fetchFirst(
			finderCache, new Object[] {companyId, repositoryId, storeType},
			orderByComparator);
	}

	/**
	 * Removes all the cts contents where companyId = &#63; and repositoryId = &#63; and storeType = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param storeType the store type
	 */
	@Override
	public void removeByC_R_S(
		long companyId, long repositoryId, String storeType) {

		_collectionPersistenceFinderByC_R_S.remove(
			finderCache, new Object[] {companyId, repositoryId, storeType});
	}

	/**
	 * Returns the number of cts contents where companyId = &#63; and repositoryId = &#63; and storeType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param storeType the store type
	 * @return the number of matching cts contents
	 */
	@Override
	public int countByC_R_S(
		long companyId, long repositoryId, String storeType) {

		return _collectionPersistenceFinderByC_R_S.count(
			finderCache, new Object[] {companyId, repositoryId, storeType});
	}

	private CollectionPersistenceFinder<CTSContent, NoSuchContentException>
		_collectionPersistenceFinderByC_R_P_S;

	/**
	 * Returns an ordered range of all the cts contents where companyId = &#63; and repositoryId = &#63; and path = &#63; and storeType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSContentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param storeType the store type
	 * @param start the lower bound of the range of cts contents
	 * @param end the upper bound of the range of cts contents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts contents
	 */
	@Override
	public List<CTSContent> findByC_R_P_S(
		long companyId, long repositoryId, String path, String storeType,
		int start, int end, OrderByComparator<CTSContent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_R_P_S.find(
			finderCache,
			new Object[] {companyId, repositoryId, path, storeType}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cts content in the ordered set where companyId = &#63; and repositoryId = &#63; and path = &#63; and storeType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param storeType the store type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts content
	 * @throws NoSuchContentException if a matching cts content could not be found
	 */
	@Override
	public CTSContent findByC_R_P_S_First(
			long companyId, long repositoryId, String path, String storeType,
			OrderByComparator<CTSContent> orderByComparator)
		throws NoSuchContentException {

		return _collectionPersistenceFinderByC_R_P_S.findFirst(
			finderCache,
			new Object[] {companyId, repositoryId, path, storeType},
			orderByComparator);
	}

	/**
	 * Returns the first cts content in the ordered set where companyId = &#63; and repositoryId = &#63; and path = &#63; and storeType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param storeType the store type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts content, or <code>null</code> if a matching cts content could not be found
	 */
	@Override
	public CTSContent fetchByC_R_P_S_First(
		long companyId, long repositoryId, String path, String storeType,
		OrderByComparator<CTSContent> orderByComparator) {

		return _collectionPersistenceFinderByC_R_P_S.fetchFirst(
			finderCache,
			new Object[] {companyId, repositoryId, path, storeType},
			orderByComparator);
	}

	/**
	 * Removes all the cts contents where companyId = &#63; and repositoryId = &#63; and path = &#63; and storeType = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param storeType the store type
	 */
	@Override
	public void removeByC_R_P_S(
		long companyId, long repositoryId, String path, String storeType) {

		_collectionPersistenceFinderByC_R_P_S.remove(
			finderCache,
			new Object[] {companyId, repositoryId, path, storeType});
	}

	/**
	 * Returns the number of cts contents where companyId = &#63; and repositoryId = &#63; and path = &#63; and storeType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param storeType the store type
	 * @return the number of matching cts contents
	 */
	@Override
	public int countByC_R_P_S(
		long companyId, long repositoryId, String path, String storeType) {

		return _collectionPersistenceFinderByC_R_P_S.count(
			finderCache,
			new Object[] {companyId, repositoryId, path, storeType});
	}

	private CollectionPersistenceFinder<CTSContent, NoSuchContentException>
		_collectionPersistenceFinderByC_R_LikeP_S;

	/**
	 * Returns all the cts contents where companyId = &#63; and repositoryId = &#63; and path LIKE &#63; and storeType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param storeType the store type
	 * @return the matching cts contents
	 */
	@Override
	public List<CTSContent> findByC_R_LikeP_S(
		long companyId, long repositoryId, String path, String storeType) {

		return findByC_R_LikeP_S(
			companyId, repositoryId, path, storeType, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the cts contents where companyId = &#63; and repositoryId = &#63; and path LIKE &#63; and storeType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSContentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param storeType the store type
	 * @param start the lower bound of the range of cts contents
	 * @param end the upper bound of the range of cts contents (not inclusive)
	 * @return the range of matching cts contents
	 */
	@Override
	public List<CTSContent> findByC_R_LikeP_S(
		long companyId, long repositoryId, String path, String storeType,
		int start, int end) {

		return findByC_R_LikeP_S(
			companyId, repositoryId, path, storeType, start, end, null);
	}

	/**
	 * Returns an ordered range of all the cts contents where companyId = &#63; and repositoryId = &#63; and path LIKE &#63; and storeType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSContentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param storeType the store type
	 * @param start the lower bound of the range of cts contents
	 * @param end the upper bound of the range of cts contents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cts contents
	 */
	@Override
	public List<CTSContent> findByC_R_LikeP_S(
		long companyId, long repositoryId, String path, String storeType,
		int start, int end, OrderByComparator<CTSContent> orderByComparator) {

		return findByC_R_LikeP_S(
			companyId, repositoryId, path, storeType, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the cts contents where companyId = &#63; and repositoryId = &#63; and path LIKE &#63; and storeType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSContentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param storeType the store type
	 * @param start the lower bound of the range of cts contents
	 * @param end the upper bound of the range of cts contents (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts contents
	 */
	@Override
	public List<CTSContent> findByC_R_LikeP_S(
		long companyId, long repositoryId, String path, String storeType,
		int start, int end, OrderByComparator<CTSContent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_R_LikeP_S.find(
			finderCache,
			new Object[] {companyId, repositoryId, path, storeType}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cts content in the ordered set where companyId = &#63; and repositoryId = &#63; and path LIKE &#63; and storeType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param storeType the store type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts content
	 * @throws NoSuchContentException if a matching cts content could not be found
	 */
	@Override
	public CTSContent findByC_R_LikeP_S_First(
			long companyId, long repositoryId, String path, String storeType,
			OrderByComparator<CTSContent> orderByComparator)
		throws NoSuchContentException {

		return _collectionPersistenceFinderByC_R_LikeP_S.findFirst(
			finderCache,
			new Object[] {companyId, repositoryId, path, storeType},
			orderByComparator);
	}

	/**
	 * Returns the first cts content in the ordered set where companyId = &#63; and repositoryId = &#63; and path LIKE &#63; and storeType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param storeType the store type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts content, or <code>null</code> if a matching cts content could not be found
	 */
	@Override
	public CTSContent fetchByC_R_LikeP_S_First(
		long companyId, long repositoryId, String path, String storeType,
		OrderByComparator<CTSContent> orderByComparator) {

		return _collectionPersistenceFinderByC_R_LikeP_S.fetchFirst(
			finderCache,
			new Object[] {companyId, repositoryId, path, storeType},
			orderByComparator);
	}

	/**
	 * Removes all the cts contents where companyId = &#63; and repositoryId = &#63; and path LIKE &#63; and storeType = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param storeType the store type
	 */
	@Override
	public void removeByC_R_LikeP_S(
		long companyId, long repositoryId, String path, String storeType) {

		_collectionPersistenceFinderByC_R_LikeP_S.remove(
			finderCache,
			new Object[] {companyId, repositoryId, path, storeType});
	}

	/**
	 * Returns the number of cts contents where companyId = &#63; and repositoryId = &#63; and path LIKE &#63; and storeType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param storeType the store type
	 * @return the number of matching cts contents
	 */
	@Override
	public int countByC_R_LikeP_S(
		long companyId, long repositoryId, String path, String storeType) {

		return _collectionPersistenceFinderByC_R_LikeP_S.count(
			finderCache,
			new Object[] {companyId, repositoryId, path, storeType});
	}

	private UniquePersistenceFinder<CTSContent, NoSuchContentException>
		_uniquePersistenceFinderByC_R_P_V_S;

	/**
	 * Returns the cts content where companyId = &#63; and repositoryId = &#63; and path = &#63; and version = &#63; and storeType = &#63; or throws a <code>NoSuchContentException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param version the version
	 * @param storeType the store type
	 * @return the matching cts content
	 * @throws NoSuchContentException if a matching cts content could not be found
	 */
	@Override
	public CTSContent findByC_R_P_V_S(
			long companyId, long repositoryId, String path, String version,
			String storeType)
		throws NoSuchContentException {

		return _uniquePersistenceFinderByC_R_P_V_S.find(
			finderCache,
			new Object[] {companyId, repositoryId, path, version, storeType});
	}

	/**
	 * Returns the cts content where companyId = &#63; and repositoryId = &#63; and path = &#63; and version = &#63; and storeType = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param version the version
	 * @param storeType the store type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cts content, or <code>null</code> if a matching cts content could not be found
	 */
	@Override
	public CTSContent fetchByC_R_P_V_S(
		long companyId, long repositoryId, String path, String version,
		String storeType, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_R_P_V_S.fetch(
			finderCache,
			new Object[] {companyId, repositoryId, path, version, storeType},
			useFinderCache);
	}

	/**
	 * Removes the cts content where companyId = &#63; and repositoryId = &#63; and path = &#63; and version = &#63; and storeType = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param version the version
	 * @param storeType the store type
	 * @return the cts content that was removed
	 */
	@Override
	public CTSContent removeByC_R_P_V_S(
			long companyId, long repositoryId, String path, String version,
			String storeType)
		throws NoSuchContentException {

		CTSContent ctsContent = findByC_R_P_V_S(
			companyId, repositoryId, path, version, storeType);

		return remove(ctsContent);
	}

	/**
	 * Returns the number of cts contents where companyId = &#63; and repositoryId = &#63; and path = &#63; and version = &#63; and storeType = &#63;.
	 *
	 * @param companyId the company ID
	 * @param repositoryId the repository ID
	 * @param path the path
	 * @param version the version
	 * @param storeType the store type
	 * @return the number of matching cts contents
	 */
	@Override
	public int countByC_R_P_V_S(
		long companyId, long repositoryId, String path, String version,
		String storeType) {

		return _uniquePersistenceFinderByC_R_P_V_S.count(
			finderCache,
			new Object[] {companyId, repositoryId, path, version, storeType});
	}

	public CTSContentPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("path", "path_");
		dbColumnNames.put("data", "data_");
		dbColumnNames.put("size", "size_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CTSContent.class);

		setModelImplClass(CTSContentImpl.class);
		setModelPKClass(long.class);

		setTable(CTSContentTable.INSTANCE);
	}

	/**
	 * Creates a new cts content with the primary key. Does not add the cts content to the database.
	 *
	 * @param ctsContentId the primary key for the new cts content
	 * @return the new cts content
	 */
	@Override
	public CTSContent create(long ctsContentId) {
		CTSContent ctsContent = new CTSContentImpl();

		ctsContent.setNew(true);
		ctsContent.setPrimaryKey(ctsContentId);

		ctsContent.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ctsContent;
	}

	/**
	 * Removes the cts content with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ctsContentId the primary key of the cts content
	 * @return the cts content that was removed
	 * @throws NoSuchContentException if a cts content with the primary key could not be found
	 */
	@Override
	public CTSContent remove(long ctsContentId) throws NoSuchContentException {
		return remove((Serializable)ctsContentId);
	}

	@Override
	protected CTSContent removeImpl(CTSContent ctsContent) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ctsContent)) {
				ctsContent = (CTSContent)session.get(
					CTSContentImpl.class, ctsContent.getPrimaryKeyObj());
			}

			if ((ctsContent != null) &&
				ctPersistenceHelper.isRemove(ctsContent)) {

				session.delete(ctsContent);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ctsContent != null) {
			clearCache(ctsContent);
		}

		return ctsContent;
	}

	@Override
	public CTSContent updateImpl(CTSContent ctsContent) {
		boolean isNew = ctsContent.isNew();

		if (!(ctsContent instanceof CTSContentModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ctsContent.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(ctsContent);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ctsContent proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CTSContent implementation " +
					ctsContent.getClass());
		}

		CTSContentModelImpl ctsContentModelImpl =
			(CTSContentModelImpl)ctsContent;

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ctsContent)) {
				if (!isNew) {
					session.evict(
						CTSContentImpl.class, ctsContent.getPrimaryKeyObj());
				}

				session.save(ctsContent);
			}
			else {
				ctsContent = (CTSContent)session.merge(ctsContent);
			}

			session.flush();
			session.clear();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ctsContent, false);

		if (isNew) {
			ctsContent.setNew(false);
		}

		ctsContent.resetOriginalValues();

		return ctsContent;
	}

	/**
	 * Returns the cts content with the primary key or throws a <code>NoSuchContentException</code> if it could not be found.
	 *
	 * @param ctsContentId the primary key of the cts content
	 * @return the cts content
	 * @throws NoSuchContentException if a cts content with the primary key could not be found
	 */
	@Override
	public CTSContent findByPrimaryKey(long ctsContentId)
		throws NoSuchContentException {

		return findByPrimaryKey((Serializable)ctsContentId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the cts content with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ctsContentId the primary key of the cts content
	 * @return the cts content, or <code>null</code> if a cts content with the primary key could not be found
	 */
	@Override
	public CTSContent fetchByPrimaryKey(long ctsContentId) {
		return fetchByPrimaryKey((Serializable)ctsContentId);
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
		return "ctsContentId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CTSCONTENT;
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
		return CTSContentModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CTSContent";
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
		ctStrictColumnNames.add("companyId");
		ctMergeColumnNames.add("repositoryId");
		ctMergeColumnNames.add("path_");
		ctMergeColumnNames.add("version");
		ctMergeColumnNames.add("data_");
		ctMergeColumnNames.add("size_");
		ctMergeColumnNames.add("storeType");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("ctsContentId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {
				"companyId", "repositoryId", "path_", "version", "storeType"
			});
	}

	/**
	 * Initializes the cts content persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByR_P = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_P",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"repositoryId", "path_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_P",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"repositoryId", "path_"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByR_P",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"repositoryId", "path_"}, 0, 2, false, null),
			_SQL_SELECT_CTSCONTENT_WHERE, _SQL_COUNT_CTSCONTENT_WHERE,
			CTSContentModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"ctsContent.", "repositoryId", FinderColumn.Type.LONG, "=",
				true, true, CTSContent::getRepositoryId),
			new FinderColumn<>(
				"ctsContent.", "path", "path_", FinderColumn.Type.STRING, "=",
				true, true, CTSContent::getPath));

		_collectionPersistenceFinderByC_R_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_R_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"companyId", "repositoryId", "storeType"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_R_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "repositoryId", "storeType"}, 0, 4,
				true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_R_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "repositoryId", "storeType"}, 0, 4,
				false, null),
			_SQL_SELECT_CTSCONTENT_WHERE, _SQL_COUNT_CTSCONTENT_WHERE,
			CTSContentModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"ctsContent.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, CTSContent::getCompanyId),
			new FinderColumn<>(
				"ctsContent.", "repositoryId", FinderColumn.Type.LONG, "=",
				true, true, CTSContent::getRepositoryId),
			new FinderColumn<>(
				"ctsContent.", "storeType", FinderColumn.Type.STRING, "=", true,
				true, CTSContent::getStoreType));

		_collectionPersistenceFinderByC_R_P_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_R_P_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "repositoryId", "path_", "storeType"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_R_P_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), String.class.getName()
					},
					new String[] {
						"companyId", "repositoryId", "path_", "storeType"
					},
					0, 12, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_R_P_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), String.class.getName()
					},
					new String[] {
						"companyId", "repositoryId", "path_", "storeType"
					},
					0, 12, false, null),
				_SQL_SELECT_CTSCONTENT_WHERE, _SQL_COUNT_CTSCONTENT_WHERE,
				CTSContentModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"ctsContent.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, CTSContent::getCompanyId),
				new FinderColumn<>(
					"ctsContent.", "repositoryId", FinderColumn.Type.LONG, "=",
					true, true, CTSContent::getRepositoryId),
				new FinderColumn<>(
					"ctsContent.", "path", "path_", FinderColumn.Type.STRING,
					"=", true, true, CTSContent::getPath),
				new FinderColumn<>(
					"ctsContent.", "storeType", FinderColumn.Type.STRING, "=",
					true, true, CTSContent::getStoreType));

		_collectionPersistenceFinderByC_R_LikeP_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_R_LikeP_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "repositoryId", "path_", "storeType"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByC_R_LikeP_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), String.class.getName()
					},
					new String[] {
						"companyId", "repositoryId", "path_", "storeType"
					},
					false),
				_SQL_SELECT_CTSCONTENT_WHERE, _SQL_COUNT_CTSCONTENT_WHERE,
				CTSContentModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				null,
				new FinderColumn<>(
					"ctsContent.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, CTSContent::getCompanyId),
				new FinderColumn<>(
					"ctsContent.", "repositoryId", FinderColumn.Type.LONG, "=",
					true, true, CTSContent::getRepositoryId),
				new FinderColumn<>(
					"ctsContent.", "path", "path_", FinderColumn.Type.STRING,
					"LIKE", true, true, CTSContent::getPath),
				new FinderColumn<>(
					"ctsContent.", "storeType", FinderColumn.Type.STRING, "=",
					true, true, CTSContent::getStoreType));

		_uniquePersistenceFinderByC_R_P_V_S = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_R_P_V_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {
					"companyId", "repositoryId", "path_", "version", "storeType"
				},
				0, 28, false, CTSContent::getCompanyId,
				CTSContent::getRepositoryId,
				convertNullFunction(CTSContent::getPath),
				convertNullFunction(CTSContent::getVersion),
				convertNullFunction(CTSContent::getStoreType)),
			_SQL_SELECT_CTSCONTENT_WHERE, "",
			new FinderColumn<>(
				"ctsContent.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, CTSContent::getCompanyId),
			new FinderColumn<>(
				"ctsContent.", "repositoryId", FinderColumn.Type.LONG, "=",
				true, true, CTSContent::getRepositoryId),
			new FinderColumn<>(
				"ctsContent.", "path", "path_", FinderColumn.Type.STRING, "=",
				true, true, CTSContent::getPath),
			new FinderColumn<>(
				"ctsContent.", "version", FinderColumn.Type.STRING, "=", true,
				true, CTSContent::getVersion),
			new FinderColumn<>(
				"ctsContent.", "storeType", FinderColumn.Type.STRING, "=", true,
				true, CTSContent::getStoreType));

		CTSContentUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CTSContentUtil.setPersistence(null);

		entityCache.removeCache(CTSContentImpl.class.getName());
	}

	@Override
	@Reference(
		target = CTSPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CTSPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CTSPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		CTSContentModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CTSCONTENT =
		"SELECT ctsContent FROM CTSContent ctsContent";

	private static final String _SQL_SELECT_CTSCONTENT_WHERE =
		"SELECT ctsContent FROM CTSContent ctsContent WHERE ";

	private static final String _SQL_COUNT_CTSCONTENT_WHERE =
		"SELECT COUNT(ctsContent) FROM CTSContent ctsContent WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"path", "data", "size"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:333066147