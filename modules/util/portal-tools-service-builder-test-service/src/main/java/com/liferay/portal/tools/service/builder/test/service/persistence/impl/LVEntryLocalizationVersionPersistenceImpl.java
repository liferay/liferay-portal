/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchLVEntryLocalizationVersionException;
import com.liferay.portal.tools.service.builder.test.model.LVEntryLocalizationVersion;
import com.liferay.portal.tools.service.builder.test.model.LVEntryLocalizationVersionTable;
import com.liferay.portal.tools.service.builder.test.model.impl.LVEntryLocalizationVersionImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.LVEntryLocalizationVersionModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.LVEntryLocalizationVersionPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.LVEntryLocalizationVersionUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;

/**
 * The persistence implementation for the lv entry localization version service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class LVEntryLocalizationVersionPersistenceImpl
	extends BasePersistenceImpl
		<LVEntryLocalizationVersion, NoSuchLVEntryLocalizationVersionException>
	implements LVEntryLocalizationVersionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LVEntryLocalizationVersionUtil</code> to access the lv entry localization version persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LVEntryLocalizationVersionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<LVEntryLocalizationVersion, NoSuchLVEntryLocalizationVersionException>
			_collectionPersistenceFinderByLvEntryLocalizationId;

	/**
	 * Returns an ordered range of all the lv entry localization versions where lvEntryLocalizationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryLocalizationVersionModelImpl</code>.
	 * </p>
	 *
	 * @param lvEntryLocalizationId the lv entry localization ID
	 * @param start the lower bound of the range of lv entry localization versions
	 * @param end the upper bound of the range of lv entry localization versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching lv entry localization versions
	 */
	@Override
	public List<LVEntryLocalizationVersion> findByLvEntryLocalizationId(
		long lvEntryLocalizationId, int start, int end,
		OrderByComparator<LVEntryLocalizationVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLvEntryLocalizationId.find(
			finderCache, new Object[] {lvEntryLocalizationId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first lv entry localization version in the ordered set where lvEntryLocalizationId = &#63;.
	 *
	 * @param lvEntryLocalizationId the lv entry localization ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry localization version
	 * @throws NoSuchLVEntryLocalizationVersionException if a matching lv entry localization version could not be found
	 */
	@Override
	public LVEntryLocalizationVersion findByLvEntryLocalizationId_First(
			long lvEntryLocalizationId,
			OrderByComparator<LVEntryLocalizationVersion> orderByComparator)
		throws NoSuchLVEntryLocalizationVersionException {

		return _collectionPersistenceFinderByLvEntryLocalizationId.findFirst(
			finderCache, new Object[] {lvEntryLocalizationId},
			orderByComparator);
	}

	/**
	 * Returns the first lv entry localization version in the ordered set where lvEntryLocalizationId = &#63;.
	 *
	 * @param lvEntryLocalizationId the lv entry localization ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry localization version, or <code>null</code> if a matching lv entry localization version could not be found
	 */
	@Override
	public LVEntryLocalizationVersion fetchByLvEntryLocalizationId_First(
		long lvEntryLocalizationId,
		OrderByComparator<LVEntryLocalizationVersion> orderByComparator) {

		return _collectionPersistenceFinderByLvEntryLocalizationId.fetchFirst(
			finderCache, new Object[] {lvEntryLocalizationId},
			orderByComparator);
	}

	/**
	 * Removes all the lv entry localization versions where lvEntryLocalizationId = &#63; from the database.
	 *
	 * @param lvEntryLocalizationId the lv entry localization ID
	 */
	@Override
	public void removeByLvEntryLocalizationId(long lvEntryLocalizationId) {
		_collectionPersistenceFinderByLvEntryLocalizationId.remove(
			finderCache, new Object[] {lvEntryLocalizationId});
	}

	/**
	 * Returns the number of lv entry localization versions where lvEntryLocalizationId = &#63;.
	 *
	 * @param lvEntryLocalizationId the lv entry localization ID
	 * @return the number of matching lv entry localization versions
	 */
	@Override
	public int countByLvEntryLocalizationId(long lvEntryLocalizationId) {
		return _collectionPersistenceFinderByLvEntryLocalizationId.count(
			finderCache, new Object[] {lvEntryLocalizationId});
	}

	private UniquePersistenceFinder
		<LVEntryLocalizationVersion, NoSuchLVEntryLocalizationVersionException>
			_uniquePersistenceFinderByLvEntryLocalizationId_Version;

	/**
	 * Returns the lv entry localization version where lvEntryLocalizationId = &#63; and version = &#63; or throws a <code>NoSuchLVEntryLocalizationVersionException</code> if it could not be found.
	 *
	 * @param lvEntryLocalizationId the lv entry localization ID
	 * @param version the version
	 * @return the matching lv entry localization version
	 * @throws NoSuchLVEntryLocalizationVersionException if a matching lv entry localization version could not be found
	 */
	@Override
	public LVEntryLocalizationVersion findByLvEntryLocalizationId_Version(
			long lvEntryLocalizationId, int version)
		throws NoSuchLVEntryLocalizationVersionException {

		return _uniquePersistenceFinderByLvEntryLocalizationId_Version.find(
			finderCache, new Object[] {lvEntryLocalizationId, version});
	}

	/**
	 * Returns the lv entry localization version where lvEntryLocalizationId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param lvEntryLocalizationId the lv entry localization ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching lv entry localization version, or <code>null</code> if a matching lv entry localization version could not be found
	 */
	@Override
	public LVEntryLocalizationVersion fetchByLvEntryLocalizationId_Version(
		long lvEntryLocalizationId, int version, boolean useFinderCache) {

		return _uniquePersistenceFinderByLvEntryLocalizationId_Version.fetch(
			finderCache, new Object[] {lvEntryLocalizationId, version},
			useFinderCache);
	}

	/**
	 * Removes the lv entry localization version where lvEntryLocalizationId = &#63; and version = &#63; from the database.
	 *
	 * @param lvEntryLocalizationId the lv entry localization ID
	 * @param version the version
	 * @return the lv entry localization version that was removed
	 */
	@Override
	public LVEntryLocalizationVersion removeByLvEntryLocalizationId_Version(
			long lvEntryLocalizationId, int version)
		throws NoSuchLVEntryLocalizationVersionException {

		LVEntryLocalizationVersion lvEntryLocalizationVersion =
			findByLvEntryLocalizationId_Version(lvEntryLocalizationId, version);

		return remove(lvEntryLocalizationVersion);
	}

	/**
	 * Returns the number of lv entry localization versions where lvEntryLocalizationId = &#63; and version = &#63;.
	 *
	 * @param lvEntryLocalizationId the lv entry localization ID
	 * @param version the version
	 * @return the number of matching lv entry localization versions
	 */
	@Override
	public int countByLvEntryLocalizationId_Version(
		long lvEntryLocalizationId, int version) {

		return _uniquePersistenceFinderByLvEntryLocalizationId_Version.count(
			finderCache, new Object[] {lvEntryLocalizationId, version});
	}

	private CollectionPersistenceFinder
		<LVEntryLocalizationVersion, NoSuchLVEntryLocalizationVersionException>
			_collectionPersistenceFinderByLvEntryId;

	/**
	 * Returns an ordered range of all the lv entry localization versions where lvEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryLocalizationVersionModelImpl</code>.
	 * </p>
	 *
	 * @param lvEntryId the lv entry ID
	 * @param start the lower bound of the range of lv entry localization versions
	 * @param end the upper bound of the range of lv entry localization versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching lv entry localization versions
	 */
	@Override
	public List<LVEntryLocalizationVersion> findByLvEntryId(
		long lvEntryId, int start, int end,
		OrderByComparator<LVEntryLocalizationVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLvEntryId.find(
			finderCache, new Object[] {lvEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first lv entry localization version in the ordered set where lvEntryId = &#63;.
	 *
	 * @param lvEntryId the lv entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry localization version
	 * @throws NoSuchLVEntryLocalizationVersionException if a matching lv entry localization version could not be found
	 */
	@Override
	public LVEntryLocalizationVersion findByLvEntryId_First(
			long lvEntryId,
			OrderByComparator<LVEntryLocalizationVersion> orderByComparator)
		throws NoSuchLVEntryLocalizationVersionException {

		return _collectionPersistenceFinderByLvEntryId.findFirst(
			finderCache, new Object[] {lvEntryId}, orderByComparator);
	}

	/**
	 * Returns the first lv entry localization version in the ordered set where lvEntryId = &#63;.
	 *
	 * @param lvEntryId the lv entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry localization version, or <code>null</code> if a matching lv entry localization version could not be found
	 */
	@Override
	public LVEntryLocalizationVersion fetchByLvEntryId_First(
		long lvEntryId,
		OrderByComparator<LVEntryLocalizationVersion> orderByComparator) {

		return _collectionPersistenceFinderByLvEntryId.fetchFirst(
			finderCache, new Object[] {lvEntryId}, orderByComparator);
	}

	/**
	 * Removes all the lv entry localization versions where lvEntryId = &#63; from the database.
	 *
	 * @param lvEntryId the lv entry ID
	 */
	@Override
	public void removeByLvEntryId(long lvEntryId) {
		_collectionPersistenceFinderByLvEntryId.remove(
			finderCache, new Object[] {lvEntryId});
	}

	/**
	 * Returns the number of lv entry localization versions where lvEntryId = &#63;.
	 *
	 * @param lvEntryId the lv entry ID
	 * @return the number of matching lv entry localization versions
	 */
	@Override
	public int countByLvEntryId(long lvEntryId) {
		return _collectionPersistenceFinderByLvEntryId.count(
			finderCache, new Object[] {lvEntryId});
	}

	private CollectionPersistenceFinder
		<LVEntryLocalizationVersion, NoSuchLVEntryLocalizationVersionException>
			_collectionPersistenceFinderByLvEntryId_Version;

	/**
	 * Returns an ordered range of all the lv entry localization versions where lvEntryId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryLocalizationVersionModelImpl</code>.
	 * </p>
	 *
	 * @param lvEntryId the lv entry ID
	 * @param version the version
	 * @param start the lower bound of the range of lv entry localization versions
	 * @param end the upper bound of the range of lv entry localization versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching lv entry localization versions
	 */
	@Override
	public List<LVEntryLocalizationVersion> findByLvEntryId_Version(
		long lvEntryId, int version, int start, int end,
		OrderByComparator<LVEntryLocalizationVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLvEntryId_Version.find(
			finderCache, new Object[] {lvEntryId, version}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first lv entry localization version in the ordered set where lvEntryId = &#63; and version = &#63;.
	 *
	 * @param lvEntryId the lv entry ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry localization version
	 * @throws NoSuchLVEntryLocalizationVersionException if a matching lv entry localization version could not be found
	 */
	@Override
	public LVEntryLocalizationVersion findByLvEntryId_Version_First(
			long lvEntryId, int version,
			OrderByComparator<LVEntryLocalizationVersion> orderByComparator)
		throws NoSuchLVEntryLocalizationVersionException {

		return _collectionPersistenceFinderByLvEntryId_Version.findFirst(
			finderCache, new Object[] {lvEntryId, version}, orderByComparator);
	}

	/**
	 * Returns the first lv entry localization version in the ordered set where lvEntryId = &#63; and version = &#63;.
	 *
	 * @param lvEntryId the lv entry ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry localization version, or <code>null</code> if a matching lv entry localization version could not be found
	 */
	@Override
	public LVEntryLocalizationVersion fetchByLvEntryId_Version_First(
		long lvEntryId, int version,
		OrderByComparator<LVEntryLocalizationVersion> orderByComparator) {

		return _collectionPersistenceFinderByLvEntryId_Version.fetchFirst(
			finderCache, new Object[] {lvEntryId, version}, orderByComparator);
	}

	/**
	 * Removes all the lv entry localization versions where lvEntryId = &#63; and version = &#63; from the database.
	 *
	 * @param lvEntryId the lv entry ID
	 * @param version the version
	 */
	@Override
	public void removeByLvEntryId_Version(long lvEntryId, int version) {
		_collectionPersistenceFinderByLvEntryId_Version.remove(
			finderCache, new Object[] {lvEntryId, version});
	}

	/**
	 * Returns the number of lv entry localization versions where lvEntryId = &#63; and version = &#63;.
	 *
	 * @param lvEntryId the lv entry ID
	 * @param version the version
	 * @return the number of matching lv entry localization versions
	 */
	@Override
	public int countByLvEntryId_Version(long lvEntryId, int version) {
		return _collectionPersistenceFinderByLvEntryId_Version.count(
			finderCache, new Object[] {lvEntryId, version});
	}

	private CollectionPersistenceFinder
		<LVEntryLocalizationVersion, NoSuchLVEntryLocalizationVersionException>
			_collectionPersistenceFinderByLvEntryId_LanguageId;

	/**
	 * Returns an ordered range of all the lv entry localization versions where lvEntryId = &#63; and languageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryLocalizationVersionModelImpl</code>.
	 * </p>
	 *
	 * @param lvEntryId the lv entry ID
	 * @param languageId the language ID
	 * @param start the lower bound of the range of lv entry localization versions
	 * @param end the upper bound of the range of lv entry localization versions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching lv entry localization versions
	 */
	@Override
	public List<LVEntryLocalizationVersion> findByLvEntryId_LanguageId(
		long lvEntryId, String languageId, int start, int end,
		OrderByComparator<LVEntryLocalizationVersion> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLvEntryId_LanguageId.find(
			finderCache, new Object[] {lvEntryId, languageId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first lv entry localization version in the ordered set where lvEntryId = &#63; and languageId = &#63;.
	 *
	 * @param lvEntryId the lv entry ID
	 * @param languageId the language ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry localization version
	 * @throws NoSuchLVEntryLocalizationVersionException if a matching lv entry localization version could not be found
	 */
	@Override
	public LVEntryLocalizationVersion findByLvEntryId_LanguageId_First(
			long lvEntryId, String languageId,
			OrderByComparator<LVEntryLocalizationVersion> orderByComparator)
		throws NoSuchLVEntryLocalizationVersionException {

		return _collectionPersistenceFinderByLvEntryId_LanguageId.findFirst(
			finderCache, new Object[] {lvEntryId, languageId},
			orderByComparator);
	}

	/**
	 * Returns the first lv entry localization version in the ordered set where lvEntryId = &#63; and languageId = &#63;.
	 *
	 * @param lvEntryId the lv entry ID
	 * @param languageId the language ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry localization version, or <code>null</code> if a matching lv entry localization version could not be found
	 */
	@Override
	public LVEntryLocalizationVersion fetchByLvEntryId_LanguageId_First(
		long lvEntryId, String languageId,
		OrderByComparator<LVEntryLocalizationVersion> orderByComparator) {

		return _collectionPersistenceFinderByLvEntryId_LanguageId.fetchFirst(
			finderCache, new Object[] {lvEntryId, languageId},
			orderByComparator);
	}

	/**
	 * Removes all the lv entry localization versions where lvEntryId = &#63; and languageId = &#63; from the database.
	 *
	 * @param lvEntryId the lv entry ID
	 * @param languageId the language ID
	 */
	@Override
	public void removeByLvEntryId_LanguageId(
		long lvEntryId, String languageId) {

		_collectionPersistenceFinderByLvEntryId_LanguageId.remove(
			finderCache, new Object[] {lvEntryId, languageId});
	}

	/**
	 * Returns the number of lv entry localization versions where lvEntryId = &#63; and languageId = &#63;.
	 *
	 * @param lvEntryId the lv entry ID
	 * @param languageId the language ID
	 * @return the number of matching lv entry localization versions
	 */
	@Override
	public int countByLvEntryId_LanguageId(long lvEntryId, String languageId) {
		return _collectionPersistenceFinderByLvEntryId_LanguageId.count(
			finderCache, new Object[] {lvEntryId, languageId});
	}

	private UniquePersistenceFinder
		<LVEntryLocalizationVersion, NoSuchLVEntryLocalizationVersionException>
			_uniquePersistenceFinderByLvEntryId_LanguageId_Version;

	/**
	 * Returns the lv entry localization version where lvEntryId = &#63; and languageId = &#63; and version = &#63; or throws a <code>NoSuchLVEntryLocalizationVersionException</code> if it could not be found.
	 *
	 * @param lvEntryId the lv entry ID
	 * @param languageId the language ID
	 * @param version the version
	 * @return the matching lv entry localization version
	 * @throws NoSuchLVEntryLocalizationVersionException if a matching lv entry localization version could not be found
	 */
	@Override
	public LVEntryLocalizationVersion findByLvEntryId_LanguageId_Version(
			long lvEntryId, String languageId, int version)
		throws NoSuchLVEntryLocalizationVersionException {

		return _uniquePersistenceFinderByLvEntryId_LanguageId_Version.find(
			finderCache, new Object[] {lvEntryId, languageId, version});
	}

	/**
	 * Returns the lv entry localization version where lvEntryId = &#63; and languageId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param lvEntryId the lv entry ID
	 * @param languageId the language ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching lv entry localization version, or <code>null</code> if a matching lv entry localization version could not be found
	 */
	@Override
	public LVEntryLocalizationVersion fetchByLvEntryId_LanguageId_Version(
		long lvEntryId, String languageId, int version,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByLvEntryId_LanguageId_Version.fetch(
			finderCache, new Object[] {lvEntryId, languageId, version},
			useFinderCache);
	}

	/**
	 * Removes the lv entry localization version where lvEntryId = &#63; and languageId = &#63; and version = &#63; from the database.
	 *
	 * @param lvEntryId the lv entry ID
	 * @param languageId the language ID
	 * @param version the version
	 * @return the lv entry localization version that was removed
	 */
	@Override
	public LVEntryLocalizationVersion removeByLvEntryId_LanguageId_Version(
			long lvEntryId, String languageId, int version)
		throws NoSuchLVEntryLocalizationVersionException {

		LVEntryLocalizationVersion lvEntryLocalizationVersion =
			findByLvEntryId_LanguageId_Version(lvEntryId, languageId, version);

		return remove(lvEntryLocalizationVersion);
	}

	/**
	 * Returns the number of lv entry localization versions where lvEntryId = &#63; and languageId = &#63; and version = &#63;.
	 *
	 * @param lvEntryId the lv entry ID
	 * @param languageId the language ID
	 * @param version the version
	 * @return the number of matching lv entry localization versions
	 */
	@Override
	public int countByLvEntryId_LanguageId_Version(
		long lvEntryId, String languageId, int version) {

		return _uniquePersistenceFinderByLvEntryId_LanguageId_Version.count(
			finderCache, new Object[] {lvEntryId, languageId, version});
	}

	public LVEntryLocalizationVersionPersistenceImpl() {
		setModelClass(LVEntryLocalizationVersion.class);

		setModelImplClass(LVEntryLocalizationVersionImpl.class);
		setModelPKClass(long.class);

		setTable(LVEntryLocalizationVersionTable.INSTANCE);
	}

	/**
	 * Creates a new lv entry localization version with the primary key. Does not add the lv entry localization version to the database.
	 *
	 * @param lvEntryLocalizationVersionId the primary key for the new lv entry localization version
	 * @return the new lv entry localization version
	 */
	@Override
	public LVEntryLocalizationVersion create(
		long lvEntryLocalizationVersionId) {

		LVEntryLocalizationVersion lvEntryLocalizationVersion =
			new LVEntryLocalizationVersionImpl();

		lvEntryLocalizationVersion.setNew(true);
		lvEntryLocalizationVersion.setPrimaryKey(lvEntryLocalizationVersionId);

		lvEntryLocalizationVersion.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return lvEntryLocalizationVersion;
	}

	/**
	 * Removes the lv entry localization version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param lvEntryLocalizationVersionId the primary key of the lv entry localization version
	 * @return the lv entry localization version that was removed
	 * @throws NoSuchLVEntryLocalizationVersionException if a lv entry localization version with the primary key could not be found
	 */
	@Override
	public LVEntryLocalizationVersion remove(long lvEntryLocalizationVersionId)
		throws NoSuchLVEntryLocalizationVersionException {

		return remove((Serializable)lvEntryLocalizationVersionId);
	}

	@Override
	protected LVEntryLocalizationVersion removeImpl(
		LVEntryLocalizationVersion lvEntryLocalizationVersion) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(lvEntryLocalizationVersion)) {
				lvEntryLocalizationVersion =
					(LVEntryLocalizationVersion)session.get(
						LVEntryLocalizationVersionImpl.class,
						lvEntryLocalizationVersion.getPrimaryKeyObj());
			}

			if (lvEntryLocalizationVersion != null) {
				session.delete(lvEntryLocalizationVersion);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (lvEntryLocalizationVersion != null) {
			clearCache(lvEntryLocalizationVersion);
		}

		return lvEntryLocalizationVersion;
	}

	@Override
	public LVEntryLocalizationVersion updateImpl(
		LVEntryLocalizationVersion lvEntryLocalizationVersion) {

		boolean isNew = lvEntryLocalizationVersion.isNew();

		if (!(lvEntryLocalizationVersion instanceof
				LVEntryLocalizationVersionModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(lvEntryLocalizationVersion.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					lvEntryLocalizationVersion);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in lvEntryLocalizationVersion proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LVEntryLocalizationVersion implementation " +
					lvEntryLocalizationVersion.getClass());
		}

		LVEntryLocalizationVersionModelImpl
			lvEntryLocalizationVersionModelImpl =
				(LVEntryLocalizationVersionModelImpl)lvEntryLocalizationVersion;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(lvEntryLocalizationVersion);
			}
			else {
				throw new IllegalArgumentException(
					"LVEntryLocalizationVersion is read only, create a new version instead");
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(lvEntryLocalizationVersion, false);

		if (isNew) {
			lvEntryLocalizationVersion.setNew(false);
		}

		lvEntryLocalizationVersion.resetOriginalValues();

		return lvEntryLocalizationVersion;
	}

	/**
	 * Returns the lv entry localization version with the primary key or throws a <code>NoSuchLVEntryLocalizationVersionException</code> if it could not be found.
	 *
	 * @param lvEntryLocalizationVersionId the primary key of the lv entry localization version
	 * @return the lv entry localization version
	 * @throws NoSuchLVEntryLocalizationVersionException if a lv entry localization version with the primary key could not be found
	 */
	@Override
	public LVEntryLocalizationVersion findByPrimaryKey(
			long lvEntryLocalizationVersionId)
		throws NoSuchLVEntryLocalizationVersionException {

		return findByPrimaryKey((Serializable)lvEntryLocalizationVersionId);
	}

	/**
	 * Returns the lv entry localization version with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param lvEntryLocalizationVersionId the primary key of the lv entry localization version
	 * @return the lv entry localization version, or <code>null</code> if a lv entry localization version with the primary key could not be found
	 */
	@Override
	public LVEntryLocalizationVersion fetchByPrimaryKey(
		long lvEntryLocalizationVersionId) {

		return fetchByPrimaryKey((Serializable)lvEntryLocalizationVersionId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "lvEntryLocalizationVersionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LVENTRYLOCALIZATIONVERSION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return LVEntryLocalizationVersionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the lv entry localization version persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByLvEntryLocalizationId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByLvEntryLocalizationId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"lvEntryLocalizationId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByLvEntryLocalizationId",
					new String[] {Long.class.getName()},
					new String[] {"lvEntryLocalizationId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByLvEntryLocalizationId",
					new String[] {Long.class.getName()},
					new String[] {"lvEntryLocalizationId"}, false),
				_SQL_SELECT_LVENTRYLOCALIZATIONVERSION_WHERE,
				_SQL_COUNT_LVENTRYLOCALIZATIONVERSION_WHERE,
				LVEntryLocalizationVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"lvEntryLocalizationVersion.", "lvEntryLocalizationId",
					FinderColumn.Type.LONG, "=", true, true,
					LVEntryLocalizationVersion::getLvEntryLocalizationId));

		_uniquePersistenceFinderByLvEntryLocalizationId_Version =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY,
					"fetchByLvEntryLocalizationId_Version",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"lvEntryLocalizationId", "version"}, 0, 0,
					false, LVEntryLocalizationVersion::getLvEntryLocalizationId,
					LVEntryLocalizationVersion::getVersion),
				_SQL_SELECT_LVENTRYLOCALIZATIONVERSION_WHERE, "",
				new FinderColumn<>(
					"lvEntryLocalizationVersion.", "lvEntryLocalizationId",
					FinderColumn.Type.LONG, "=", true, true,
					LVEntryLocalizationVersion::getLvEntryLocalizationId),
				new FinderColumn<>(
					"lvEntryLocalizationVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					LVEntryLocalizationVersion::getVersion));

		_collectionPersistenceFinderByLvEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLvEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"lvEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByLvEntryId", new String[] {Long.class.getName()},
					new String[] {"lvEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByLvEntryId", new String[] {Long.class.getName()},
					new String[] {"lvEntryId"}, false),
				_SQL_SELECT_LVENTRYLOCALIZATIONVERSION_WHERE,
				_SQL_COUNT_LVENTRYLOCALIZATIONVERSION_WHERE,
				LVEntryLocalizationVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"lvEntryLocalizationVersion.", "lvEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					LVEntryLocalizationVersion::getLvEntryId));

		_collectionPersistenceFinderByLvEntryId_Version =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByLvEntryId_Version",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"lvEntryId", "version"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByLvEntryId_Version",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"lvEntryId", "version"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByLvEntryId_Version",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"lvEntryId", "version"}, false),
				_SQL_SELECT_LVENTRYLOCALIZATIONVERSION_WHERE,
				_SQL_COUNT_LVENTRYLOCALIZATIONVERSION_WHERE,
				LVEntryLocalizationVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"lvEntryLocalizationVersion.", "lvEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					LVEntryLocalizationVersion::getLvEntryId),
				new FinderColumn<>(
					"lvEntryLocalizationVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					LVEntryLocalizationVersion::getVersion));

		_collectionPersistenceFinderByLvEntryId_LanguageId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByLvEntryId_LanguageId",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"lvEntryId", "languageId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByLvEntryId_LanguageId",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"lvEntryId", "languageId"}, 0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByLvEntryId_LanguageId",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"lvEntryId", "languageId"}, 0, 2, false,
					null),
				_SQL_SELECT_LVENTRYLOCALIZATIONVERSION_WHERE,
				_SQL_COUNT_LVENTRYLOCALIZATIONVERSION_WHERE,
				LVEntryLocalizationVersionModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"lvEntryLocalizationVersion.", "lvEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					LVEntryLocalizationVersion::getLvEntryId),
				new FinderColumn<>(
					"lvEntryLocalizationVersion.", "languageId",
					FinderColumn.Type.STRING, "=", true, true,
					LVEntryLocalizationVersion::getLanguageId));

		_uniquePersistenceFinderByLvEntryId_LanguageId_Version =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY,
					"fetchByLvEntryId_LanguageId_Version",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName()
					},
					new String[] {"lvEntryId", "languageId", "version"}, 0, 2,
					false, LVEntryLocalizationVersion::getLvEntryId,
					convertNullFunction(
						LVEntryLocalizationVersion::getLanguageId),
					LVEntryLocalizationVersion::getVersion),
				_SQL_SELECT_LVENTRYLOCALIZATIONVERSION_WHERE, "",
				new FinderColumn<>(
					"lvEntryLocalizationVersion.", "lvEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					LVEntryLocalizationVersion::getLvEntryId),
				new FinderColumn<>(
					"lvEntryLocalizationVersion.", "languageId",
					FinderColumn.Type.STRING, "=", true, true,
					LVEntryLocalizationVersion::getLanguageId),
				new FinderColumn<>(
					"lvEntryLocalizationVersion.", "version",
					FinderColumn.Type.INTEGER, "=", true, true,
					LVEntryLocalizationVersion::getVersion));

		LVEntryLocalizationVersionUtil.setPersistence(this);
	}

	public void destroy() {
		LVEntryLocalizationVersionUtil.setPersistence(null);

		entityCache.removeCache(LVEntryLocalizationVersionImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		LVEntryLocalizationVersionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LVENTRYLOCALIZATIONVERSION =
		"SELECT lvEntryLocalizationVersion FROM LVEntryLocalizationVersion lvEntryLocalizationVersion";

	private static final String _SQL_SELECT_LVENTRYLOCALIZATIONVERSION_WHERE =
		"SELECT lvEntryLocalizationVersion FROM LVEntryLocalizationVersion lvEntryLocalizationVersion WHERE ";

	private static final String _SQL_COUNT_LVENTRYLOCALIZATIONVERSION_WHERE =
		"SELECT COUNT(lvEntryLocalizationVersion) FROM LVEntryLocalizationVersion lvEntryLocalizationVersion WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No LVEntryLocalizationVersion exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		LVEntryLocalizationVersionPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-40482402