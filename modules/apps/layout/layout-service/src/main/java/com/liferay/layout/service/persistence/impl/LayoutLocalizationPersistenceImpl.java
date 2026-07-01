/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.service.persistence.impl;

import com.liferay.layout.exception.NoSuchLayoutLocalizationException;
import com.liferay.layout.model.LayoutLocalization;
import com.liferay.layout.model.LayoutLocalizationTable;
import com.liferay.layout.model.impl.LayoutLocalizationImpl;
import com.liferay.layout.model.impl.LayoutLocalizationModelImpl;
import com.liferay.layout.service.persistence.LayoutLocalizationPersistence;
import com.liferay.layout.service.persistence.LayoutLocalizationUtil;
import com.liferay.layout.service.persistence.impl.constants.LayoutPersistenceConstants;
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
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
 * The persistence implementation for the layout localization service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = LayoutLocalizationPersistence.class)
public class LayoutLocalizationPersistenceImpl
	extends BasePersistenceImpl
		<LayoutLocalization, NoSuchLayoutLocalizationException>
	implements LayoutLocalizationPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LayoutLocalizationUtil</code> to access the layout localization persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LayoutLocalizationImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<LayoutLocalization, NoSuchLayoutLocalizationException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the layout localizations where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout localizations
	 * @param end the upper bound of the range of layout localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout localizations
	 */
	@Override
	public List<LayoutLocalization> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutLocalization> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout localization in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout localization
	 * @throws NoSuchLayoutLocalizationException if a matching layout localization could not be found
	 */
	@Override
	public LayoutLocalization findByUuid_First(
			String uuid,
			OrderByComparator<LayoutLocalization> orderByComparator)
		throws NoSuchLayoutLocalizationException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first layout localization in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout localization, or <code>null</code> if a matching layout localization could not be found
	 */
	@Override
	public LayoutLocalization fetchByUuid_First(
		String uuid, OrderByComparator<LayoutLocalization> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the layout localizations where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of layout localizations where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout localizations
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<LayoutLocalization, NoSuchLayoutLocalizationException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the layout localization where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchLayoutLocalizationException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout localization
	 * @throws NoSuchLayoutLocalizationException if a matching layout localization could not be found
	 */
	@Override
	public LayoutLocalization findByUUID_G(String uuid, long groupId)
		throws NoSuchLayoutLocalizationException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the layout localization where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout localization, or <code>null</code> if a matching layout localization could not be found
	 */
	@Override
	public LayoutLocalization fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the layout localization where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the layout localization that was removed
	 */
	@Override
	public LayoutLocalization removeByUUID_G(String uuid, long groupId)
		throws NoSuchLayoutLocalizationException {

		LayoutLocalization layoutLocalization = findByUUID_G(uuid, groupId);

		return remove(layoutLocalization);
	}

	/**
	 * Returns the number of layout localizations where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching layout localizations
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<LayoutLocalization, NoSuchLayoutLocalizationException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the layout localizations where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout localizations
	 * @param end the upper bound of the range of layout localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout localizations
	 */
	@Override
	public List<LayoutLocalization> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutLocalization> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout localization in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout localization
	 * @throws NoSuchLayoutLocalizationException if a matching layout localization could not be found
	 */
	@Override
	public LayoutLocalization findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<LayoutLocalization> orderByComparator)
		throws NoSuchLayoutLocalizationException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first layout localization in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout localization, or <code>null</code> if a matching layout localization could not be found
	 */
	@Override
	public LayoutLocalization fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<LayoutLocalization> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the layout localizations where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of layout localizations where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout localizations
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<LayoutLocalization, NoSuchLayoutLocalizationException>
			_collectionPersistenceFinderByPlid;

	/**
	 * Returns an ordered range of all the layout localizations where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout localizations
	 * @param end the upper bound of the range of layout localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout localizations
	 */
	@Override
	public List<LayoutLocalization> findByPlid(
		long plid, int start, int end,
		OrderByComparator<LayoutLocalization> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByPlid.find(
			finderCache, new Object[] {plid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout localization in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout localization
	 * @throws NoSuchLayoutLocalizationException if a matching layout localization could not be found
	 */
	@Override
	public LayoutLocalization findByPlid_First(
			long plid, OrderByComparator<LayoutLocalization> orderByComparator)
		throws NoSuchLayoutLocalizationException {

		return _collectionPersistenceFinderByPlid.findFirst(
			finderCache, new Object[] {plid}, orderByComparator);
	}

	/**
	 * Returns the first layout localization in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout localization, or <code>null</code> if a matching layout localization could not be found
	 */
	@Override
	public LayoutLocalization fetchByPlid_First(
		long plid, OrderByComparator<LayoutLocalization> orderByComparator) {

		return _collectionPersistenceFinderByPlid.fetchFirst(
			finderCache, new Object[] {plid}, orderByComparator);
	}

	/**
	 * Removes all the layout localizations where plid = &#63; from the database.
	 *
	 * @param plid the plid
	 */
	@Override
	public void removeByPlid(long plid) {
		_collectionPersistenceFinderByPlid.remove(
			finderCache, new Object[] {plid});
	}

	/**
	 * Returns the number of layout localizations where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the number of matching layout localizations
	 */
	@Override
	public int countByPlid(long plid) {
		return _collectionPersistenceFinderByPlid.count(
			finderCache, new Object[] {plid});
	}

	private UniquePersistenceFinder
		<LayoutLocalization, NoSuchLayoutLocalizationException>
			_uniquePersistenceFinderByL_P;

	/**
	 * Returns the layout localization where languageId = &#63; and plid = &#63; or throws a <code>NoSuchLayoutLocalizationException</code> if it could not be found.
	 *
	 * @param languageId the language ID
	 * @param plid the plid
	 * @return the matching layout localization
	 * @throws NoSuchLayoutLocalizationException if a matching layout localization could not be found
	 */
	@Override
	public LayoutLocalization findByL_P(String languageId, long plid)
		throws NoSuchLayoutLocalizationException {

		return _uniquePersistenceFinderByL_P.find(
			finderCache, new Object[] {languageId, plid});
	}

	/**
	 * Returns the layout localization where languageId = &#63; and plid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param languageId the language ID
	 * @param plid the plid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout localization, or <code>null</code> if a matching layout localization could not be found
	 */
	@Override
	public LayoutLocalization fetchByL_P(
		String languageId, long plid, boolean useFinderCache) {

		return _uniquePersistenceFinderByL_P.fetch(
			finderCache, new Object[] {languageId, plid}, useFinderCache);
	}

	/**
	 * Removes the layout localization where languageId = &#63; and plid = &#63; from the database.
	 *
	 * @param languageId the language ID
	 * @param plid the plid
	 * @return the layout localization that was removed
	 */
	@Override
	public LayoutLocalization removeByL_P(String languageId, long plid)
		throws NoSuchLayoutLocalizationException {

		LayoutLocalization layoutLocalization = findByL_P(languageId, plid);

		return remove(layoutLocalization);
	}

	/**
	 * Returns the number of layout localizations where languageId = &#63; and plid = &#63;.
	 *
	 * @param languageId the language ID
	 * @param plid the plid
	 * @return the number of matching layout localizations
	 */
	@Override
	public int countByL_P(String languageId, long plid) {
		return _uniquePersistenceFinderByL_P.count(
			finderCache, new Object[] {languageId, plid});
	}

	private UniquePersistenceFinder
		<LayoutLocalization, NoSuchLayoutLocalizationException>
			_uniquePersistenceFinderByG_L_P;

	/**
	 * Returns the layout localization where groupId = &#63; and languageId = &#63; and plid = &#63; or throws a <code>NoSuchLayoutLocalizationException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param languageId the language ID
	 * @param plid the plid
	 * @return the matching layout localization
	 * @throws NoSuchLayoutLocalizationException if a matching layout localization could not be found
	 */
	@Override
	public LayoutLocalization findByG_L_P(
			long groupId, String languageId, long plid)
		throws NoSuchLayoutLocalizationException {

		return _uniquePersistenceFinderByG_L_P.find(
			finderCache, new Object[] {groupId, languageId, plid});
	}

	/**
	 * Returns the layout localization where groupId = &#63; and languageId = &#63; and plid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param languageId the language ID
	 * @param plid the plid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout localization, or <code>null</code> if a matching layout localization could not be found
	 */
	@Override
	public LayoutLocalization fetchByG_L_P(
		long groupId, String languageId, long plid, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_L_P.fetch(
			finderCache, new Object[] {groupId, languageId, plid},
			useFinderCache);
	}

	/**
	 * Removes the layout localization where groupId = &#63; and languageId = &#63; and plid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param languageId the language ID
	 * @param plid the plid
	 * @return the layout localization that was removed
	 */
	@Override
	public LayoutLocalization removeByG_L_P(
			long groupId, String languageId, long plid)
		throws NoSuchLayoutLocalizationException {

		LayoutLocalization layoutLocalization = findByG_L_P(
			groupId, languageId, plid);

		return remove(layoutLocalization);
	}

	/**
	 * Returns the number of layout localizations where groupId = &#63; and languageId = &#63; and plid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param languageId the language ID
	 * @param plid the plid
	 * @return the number of matching layout localizations
	 */
	@Override
	public int countByG_L_P(long groupId, String languageId, long plid) {
		return _uniquePersistenceFinderByG_L_P.count(
			finderCache, new Object[] {groupId, languageId, plid});
	}

	public LayoutLocalizationPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(LayoutLocalization.class);

		setModelImplClass(LayoutLocalizationImpl.class);
		setModelPKClass(long.class);

		setTable(LayoutLocalizationTable.INSTANCE);
	}

	/**
	 * Creates a new layout localization with the primary key. Does not add the layout localization to the database.
	 *
	 * @param layoutLocalizationId the primary key for the new layout localization
	 * @return the new layout localization
	 */
	@Override
	public LayoutLocalization create(long layoutLocalizationId) {
		LayoutLocalization layoutLocalization = new LayoutLocalizationImpl();

		layoutLocalization.setNew(true);
		layoutLocalization.setPrimaryKey(layoutLocalizationId);

		String uuid = PortalUUIDUtil.generate();

		layoutLocalization.setUuid(uuid);

		layoutLocalization.setCompanyId(CompanyThreadLocal.getCompanyId());

		return layoutLocalization;
	}

	/**
	 * Removes the layout localization with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutLocalizationId the primary key of the layout localization
	 * @return the layout localization that was removed
	 * @throws NoSuchLayoutLocalizationException if a layout localization with the primary key could not be found
	 */
	@Override
	public LayoutLocalization remove(long layoutLocalizationId)
		throws NoSuchLayoutLocalizationException {

		return remove((Serializable)layoutLocalizationId);
	}

	@Override
	protected LayoutLocalization removeImpl(
		LayoutLocalization layoutLocalization) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(layoutLocalization)) {
				layoutLocalization = (LayoutLocalization)session.get(
					LayoutLocalizationImpl.class,
					layoutLocalization.getPrimaryKeyObj());
			}

			if ((layoutLocalization != null) &&
				ctPersistenceHelper.isRemove(layoutLocalization)) {

				session.delete(layoutLocalization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (layoutLocalization != null) {
			clearCache(layoutLocalization);
		}

		return layoutLocalization;
	}

	@Override
	public LayoutLocalization updateImpl(
		LayoutLocalization layoutLocalization) {

		boolean isNew = layoutLocalization.isNew();

		if (!(layoutLocalization instanceof LayoutLocalizationModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(layoutLocalization.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					layoutLocalization);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in layoutLocalization proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LayoutLocalization implementation " +
					layoutLocalization.getClass());
		}

		LayoutLocalizationModelImpl layoutLocalizationModelImpl =
			(LayoutLocalizationModelImpl)layoutLocalization;

		if (Validator.isNull(layoutLocalization.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			layoutLocalization.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (layoutLocalization.getCreateDate() == null)) {
			if (serviceContext == null) {
				layoutLocalization.setCreateDate(date);
			}
			else {
				layoutLocalization.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!layoutLocalizationModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				layoutLocalization.setModifiedDate(date);
			}
			else {
				layoutLocalization.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(layoutLocalization)) {
				if (!isNew) {
					session.evict(
						LayoutLocalizationImpl.class,
						layoutLocalization.getPrimaryKeyObj());
				}

				session.save(layoutLocalization);
			}
			else {
				layoutLocalization = (LayoutLocalization)session.merge(
					layoutLocalization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(layoutLocalization, false);

		if (isNew) {
			layoutLocalization.setNew(false);
		}

		layoutLocalization.resetOriginalValues();

		return layoutLocalization;
	}

	/**
	 * Returns the layout localization with the primary key or throws a <code>NoSuchLayoutLocalizationException</code> if it could not be found.
	 *
	 * @param layoutLocalizationId the primary key of the layout localization
	 * @return the layout localization
	 * @throws NoSuchLayoutLocalizationException if a layout localization with the primary key could not be found
	 */
	@Override
	public LayoutLocalization findByPrimaryKey(long layoutLocalizationId)
		throws NoSuchLayoutLocalizationException {

		return findByPrimaryKey((Serializable)layoutLocalizationId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the layout localization with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutLocalizationId the primary key of the layout localization
	 * @return the layout localization, or <code>null</code> if a layout localization with the primary key could not be found
	 */
	@Override
	public LayoutLocalization fetchByPrimaryKey(long layoutLocalizationId) {
		return fetchByPrimaryKey((Serializable)layoutLocalizationId);
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
		return "layoutLocalizationId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAYOUTLOCALIZATION;
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
		return LayoutLocalizationModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "LayoutLocalization";
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
		Set<String> ctIgnoreColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("uuid_");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("content");
		ctMergeColumnNames.add("languageId");
		ctMergeColumnNames.add("plid");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("layoutLocalizationId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"languageId", "plid"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "languageId", "plid"});
	}

	/**
	 * Initializes the layout localization persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByUuid = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"uuid_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, false, null),
			_SQL_SELECT_LAYOUTLOCALIZATION_WHERE,
			_SQL_COUNT_LAYOUTLOCALIZATION_WHERE,
			LayoutLocalizationModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"layoutLocalization.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				LayoutLocalization::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(LayoutLocalization::getUuid),
				LayoutLocalization::getGroupId),
			_SQL_SELECT_LAYOUTLOCALIZATION_WHERE, "",
			new FinderColumn<>(
				"layoutLocalization.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				LayoutLocalization::getUuid),
			new FinderColumn<>(
				"layoutLocalization.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, LayoutLocalization::getGroupId));

		_collectionPersistenceFinderByUuid_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, false, null),
				_SQL_SELECT_LAYOUTLOCALIZATION_WHERE,
				_SQL_COUNT_LAYOUTLOCALIZATION_WHERE,
				LayoutLocalizationModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"layoutLocalization.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutLocalization::getUuid),
				new FinderColumn<>(
					"layoutLocalization.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, LayoutLocalization::getCompanyId));

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
			_SQL_SELECT_LAYOUTLOCALIZATION_WHERE,
			_SQL_COUNT_LAYOUTLOCALIZATION_WHERE,
			LayoutLocalizationModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"layoutLocalization.", "plid", FinderColumn.Type.LONG, "=",
				true, true, LayoutLocalization::getPlid));

		_uniquePersistenceFinderByL_P = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByL_P",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"languageId", "plid"}, 0, 1, false,
				convertNullFunction(LayoutLocalization::getLanguageId),
				LayoutLocalization::getPlid),
			_SQL_SELECT_LAYOUTLOCALIZATION_WHERE, "",
			new FinderColumn<>(
				"layoutLocalization.", "languageId", FinderColumn.Type.STRING,
				"=", true, true, LayoutLocalization::getLanguageId),
			new FinderColumn<>(
				"layoutLocalization.", "plid", FinderColumn.Type.LONG, "=",
				true, true, LayoutLocalization::getPlid));

		_uniquePersistenceFinderByG_L_P = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_L_P",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Long.class.getName()
				},
				new String[] {"groupId", "languageId", "plid"}, 0, 2, false,
				LayoutLocalization::getGroupId,
				convertNullFunction(LayoutLocalization::getLanguageId),
				LayoutLocalization::getPlid),
			_SQL_SELECT_LAYOUTLOCALIZATION_WHERE, "",
			new FinderColumn<>(
				"layoutLocalization.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, LayoutLocalization::getGroupId),
			new FinderColumn<>(
				"layoutLocalization.", "languageId", FinderColumn.Type.STRING,
				"=", true, true, LayoutLocalization::getLanguageId),
			new FinderColumn<>(
				"layoutLocalization.", "plid", FinderColumn.Type.LONG, "=",
				true, true, LayoutLocalization::getPlid));

		LayoutLocalizationUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		LayoutLocalizationUtil.setPersistence(null);

		entityCache.removeCache(LayoutLocalizationImpl.class.getName());
	}

	@Override
	@Reference(
		target = LayoutPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = LayoutPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = LayoutPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		LayoutLocalizationModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LAYOUTLOCALIZATION =
		"SELECT layoutLocalization FROM LayoutLocalization layoutLocalization";

	private static final String _SQL_SELECT_LAYOUTLOCALIZATION_WHERE =
		"SELECT layoutLocalization FROM LayoutLocalization layoutLocalization WHERE ";

	private static final String _SQL_COUNT_LAYOUTLOCALIZATION_WHERE =
		"SELECT COUNT(layoutLocalization) FROM LayoutLocalization layoutLocalization WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1501712712