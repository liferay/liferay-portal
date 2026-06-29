/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.persistence.impl;

import com.liferay.layout.page.template.exception.NoSuchPageTemplateStructureRelException;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRel;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelTable;
import com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelImpl;
import com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelModelImpl;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateStructureRelPersistence;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateStructureRelUtil;
import com.liferay.layout.page.template.service.persistence.impl.constants.LayoutPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
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
 * The persistence implementation for the layout page template structure rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = LayoutPageTemplateStructureRelPersistence.class)
public class LayoutPageTemplateStructureRelPersistenceImpl
	extends BasePersistenceImpl
		<LayoutPageTemplateStructureRel,
		 NoSuchPageTemplateStructureRelException>
	implements LayoutPageTemplateStructureRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LayoutPageTemplateStructureRelUtil</code> to access the layout page template structure rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LayoutPageTemplateStructureRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<LayoutPageTemplateStructureRel,
		 NoSuchPageTemplateStructureRelException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the layout page template structure rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template structure rels
	 * @param end the upper bound of the range of layout page template structure rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rels
	 */
	@Override
	public List<LayoutPageTemplateStructureRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout page template structure rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel
	 * @throws NoSuchPageTemplateStructureRelException if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel findByUuid_First(
			String uuid,
			OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator)
		throws NoSuchPageTemplateStructureRelException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first layout page template structure rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel, or <code>null</code> if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel fetchByUuid_First(
		String uuid,
		OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the layout page template structure rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of layout page template structure rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout page template structure rels
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<LayoutPageTemplateStructureRel,
		 NoSuchPageTemplateStructureRelException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the layout page template structure rel where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateStructureRelException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout page template structure rel
	 * @throws NoSuchPageTemplateStructureRelException if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel findByUUID_G(
			String uuid, long groupId)
		throws NoSuchPageTemplateStructureRelException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the layout page template structure rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template structure rel, or <code>null</code> if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the layout page template structure rel where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the layout page template structure rel that was removed
	 */
	@Override
	public LayoutPageTemplateStructureRel removeByUUID_G(
			String uuid, long groupId)
		throws NoSuchPageTemplateStructureRelException {

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			findByUUID_G(uuid, groupId);

		return remove(layoutPageTemplateStructureRel);
	}

	/**
	 * Returns the number of layout page template structure rels where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching layout page template structure rels
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<LayoutPageTemplateStructureRel,
		 NoSuchPageTemplateStructureRelException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the layout page template structure rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template structure rels
	 * @param end the upper bound of the range of layout page template structure rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rels
	 */
	@Override
	public List<LayoutPageTemplateStructureRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template structure rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel
	 * @throws NoSuchPageTemplateStructureRelException if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator)
		throws NoSuchPageTemplateStructureRelException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first layout page template structure rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel, or <code>null</code> if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the layout page template structure rels where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of layout page template structure rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout page template structure rels
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<LayoutPageTemplateStructureRel,
		 NoSuchPageTemplateStructureRelException>
			_collectionPersistenceFinderByLayoutPageTemplateStructureId;

	/**
	 * Returns an ordered range of all the layout page template structure rels where layoutPageTemplateStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureRelModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureId the layout page template structure ID
	 * @param start the lower bound of the range of layout page template structure rels
	 * @param end the upper bound of the range of layout page template structure rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rels
	 */
	@Override
	public List<LayoutPageTemplateStructureRel>
		findByLayoutPageTemplateStructureId(
			long layoutPageTemplateStructureId, int start, int end,
			OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator,
			boolean useFinderCache) {

		return _collectionPersistenceFinderByLayoutPageTemplateStructureId.find(
			finderCache, new Object[] {layoutPageTemplateStructureId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template structure rel in the ordered set where layoutPageTemplateStructureId = &#63;.
	 *
	 * @param layoutPageTemplateStructureId the layout page template structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel
	 * @throws NoSuchPageTemplateStructureRelException if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel
			findByLayoutPageTemplateStructureId_First(
				long layoutPageTemplateStructureId,
				OrderByComparator<LayoutPageTemplateStructureRel>
					orderByComparator)
		throws NoSuchPageTemplateStructureRelException {

		return _collectionPersistenceFinderByLayoutPageTemplateStructureId.
			findFirst(
				finderCache, new Object[] {layoutPageTemplateStructureId},
				orderByComparator);
	}

	/**
	 * Returns the first layout page template structure rel in the ordered set where layoutPageTemplateStructureId = &#63;.
	 *
	 * @param layoutPageTemplateStructureId the layout page template structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel, or <code>null</code> if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel
		fetchByLayoutPageTemplateStructureId_First(
			long layoutPageTemplateStructureId,
			OrderByComparator<LayoutPageTemplateStructureRel>
				orderByComparator) {

		return _collectionPersistenceFinderByLayoutPageTemplateStructureId.
			fetchFirst(
				finderCache, new Object[] {layoutPageTemplateStructureId},
				orderByComparator);
	}

	/**
	 * Removes all the layout page template structure rels where layoutPageTemplateStructureId = &#63; from the database.
	 *
	 * @param layoutPageTemplateStructureId the layout page template structure ID
	 */
	@Override
	public void removeByLayoutPageTemplateStructureId(
		long layoutPageTemplateStructureId) {

		_collectionPersistenceFinderByLayoutPageTemplateStructureId.remove(
			finderCache, new Object[] {layoutPageTemplateStructureId});
	}

	/**
	 * Returns the number of layout page template structure rels where layoutPageTemplateStructureId = &#63;.
	 *
	 * @param layoutPageTemplateStructureId the layout page template structure ID
	 * @return the number of matching layout page template structure rels
	 */
	@Override
	public int countByLayoutPageTemplateStructureId(
		long layoutPageTemplateStructureId) {

		return _collectionPersistenceFinderByLayoutPageTemplateStructureId.
			count(finderCache, new Object[] {layoutPageTemplateStructureId});
	}

	private CollectionPersistenceFinder
		<LayoutPageTemplateStructureRel,
		 NoSuchPageTemplateStructureRelException>
			_collectionPersistenceFinderBySegmentsExperienceId;

	/**
	 * Returns an ordered range of all the layout page template structure rels where segmentsExperienceId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureRelModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsExperienceId the segments experience ID
	 * @param start the lower bound of the range of layout page template structure rels
	 * @param end the upper bound of the range of layout page template structure rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rels
	 */
	@Override
	public List<LayoutPageTemplateStructureRel> findBySegmentsExperienceId(
		long segmentsExperienceId, int start, int end,
		OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderBySegmentsExperienceId.find(
			finderCache, new Object[] {segmentsExperienceId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template structure rel in the ordered set where segmentsExperienceId = &#63;.
	 *
	 * @param segmentsExperienceId the segments experience ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel
	 * @throws NoSuchPageTemplateStructureRelException if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel findBySegmentsExperienceId_First(
			long segmentsExperienceId,
			OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator)
		throws NoSuchPageTemplateStructureRelException {

		return _collectionPersistenceFinderBySegmentsExperienceId.findFirst(
			finderCache, new Object[] {segmentsExperienceId},
			orderByComparator);
	}

	/**
	 * Returns the first layout page template structure rel in the ordered set where segmentsExperienceId = &#63;.
	 *
	 * @param segmentsExperienceId the segments experience ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel, or <code>null</code> if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel fetchBySegmentsExperienceId_First(
		long segmentsExperienceId,
		OrderByComparator<LayoutPageTemplateStructureRel> orderByComparator) {

		return _collectionPersistenceFinderBySegmentsExperienceId.fetchFirst(
			finderCache, new Object[] {segmentsExperienceId},
			orderByComparator);
	}

	/**
	 * Removes all the layout page template structure rels where segmentsExperienceId = &#63; from the database.
	 *
	 * @param segmentsExperienceId the segments experience ID
	 */
	@Override
	public void removeBySegmentsExperienceId(long segmentsExperienceId) {
		_collectionPersistenceFinderBySegmentsExperienceId.remove(
			finderCache, new Object[] {segmentsExperienceId});
	}

	/**
	 * Returns the number of layout page template structure rels where segmentsExperienceId = &#63;.
	 *
	 * @param segmentsExperienceId the segments experience ID
	 * @return the number of matching layout page template structure rels
	 */
	@Override
	public int countBySegmentsExperienceId(long segmentsExperienceId) {
		return _collectionPersistenceFinderBySegmentsExperienceId.count(
			finderCache, new Object[] {segmentsExperienceId});
	}

	private UniquePersistenceFinder
		<LayoutPageTemplateStructureRel,
		 NoSuchPageTemplateStructureRelException> _uniquePersistenceFinderByL_S;

	/**
	 * Returns the layout page template structure rel where layoutPageTemplateStructureId = &#63; and segmentsExperienceId = &#63; or throws a <code>NoSuchPageTemplateStructureRelException</code> if it could not be found.
	 *
	 * @param layoutPageTemplateStructureId the layout page template structure ID
	 * @param segmentsExperienceId the segments experience ID
	 * @return the matching layout page template structure rel
	 * @throws NoSuchPageTemplateStructureRelException if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel findByL_S(
			long layoutPageTemplateStructureId, long segmentsExperienceId)
		throws NoSuchPageTemplateStructureRelException {

		return _uniquePersistenceFinderByL_S.find(
			finderCache,
			new Object[] {layoutPageTemplateStructureId, segmentsExperienceId});
	}

	/**
	 * Returns the layout page template structure rel where layoutPageTemplateStructureId = &#63; and segmentsExperienceId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param layoutPageTemplateStructureId the layout page template structure ID
	 * @param segmentsExperienceId the segments experience ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template structure rel, or <code>null</code> if a matching layout page template structure rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel fetchByL_S(
		long layoutPageTemplateStructureId, long segmentsExperienceId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByL_S.fetch(
			finderCache,
			new Object[] {layoutPageTemplateStructureId, segmentsExperienceId},
			useFinderCache);
	}

	/**
	 * Removes the layout page template structure rel where layoutPageTemplateStructureId = &#63; and segmentsExperienceId = &#63; from the database.
	 *
	 * @param layoutPageTemplateStructureId the layout page template structure ID
	 * @param segmentsExperienceId the segments experience ID
	 * @return the layout page template structure rel that was removed
	 */
	@Override
	public LayoutPageTemplateStructureRel removeByL_S(
			long layoutPageTemplateStructureId, long segmentsExperienceId)
		throws NoSuchPageTemplateStructureRelException {

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			findByL_S(layoutPageTemplateStructureId, segmentsExperienceId);

		return remove(layoutPageTemplateStructureRel);
	}

	/**
	 * Returns the number of layout page template structure rels where layoutPageTemplateStructureId = &#63; and segmentsExperienceId = &#63;.
	 *
	 * @param layoutPageTemplateStructureId the layout page template structure ID
	 * @param segmentsExperienceId the segments experience ID
	 * @return the number of matching layout page template structure rels
	 */
	@Override
	public int countByL_S(
		long layoutPageTemplateStructureId, long segmentsExperienceId) {

		return _uniquePersistenceFinderByL_S.count(
			finderCache,
			new Object[] {layoutPageTemplateStructureId, segmentsExperienceId});
	}

	public LayoutPageTemplateStructureRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put(
			"layoutPageTemplateStructureRelId", "lPageTemplateStructureRelId");
		dbColumnNames.put("data", "data_");

		setDBColumnNames(dbColumnNames);

		setModelClass(LayoutPageTemplateStructureRel.class);

		setModelImplClass(LayoutPageTemplateStructureRelImpl.class);
		setModelPKClass(long.class);

		setTable(LayoutPageTemplateStructureRelTable.INSTANCE);
	}

	/**
	 * Creates a new layout page template structure rel with the primary key. Does not add the layout page template structure rel to the database.
	 *
	 * @param layoutPageTemplateStructureRelId the primary key for the new layout page template structure rel
	 * @return the new layout page template structure rel
	 */
	@Override
	public LayoutPageTemplateStructureRel create(
		long layoutPageTemplateStructureRelId) {

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			new LayoutPageTemplateStructureRelImpl();

		layoutPageTemplateStructureRel.setNew(true);
		layoutPageTemplateStructureRel.setPrimaryKey(
			layoutPageTemplateStructureRelId);

		String uuid = PortalUUIDUtil.generate();

		layoutPageTemplateStructureRel.setUuid(uuid);

		layoutPageTemplateStructureRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return layoutPageTemplateStructureRel;
	}

	/**
	 * Removes the layout page template structure rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutPageTemplateStructureRelId the primary key of the layout page template structure rel
	 * @return the layout page template structure rel that was removed
	 * @throws NoSuchPageTemplateStructureRelException if a layout page template structure rel with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel remove(
			long layoutPageTemplateStructureRelId)
		throws NoSuchPageTemplateStructureRelException {

		return remove((Serializable)layoutPageTemplateStructureRelId);
	}

	@Override
	protected LayoutPageTemplateStructureRel removeImpl(
		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(layoutPageTemplateStructureRel)) {
				layoutPageTemplateStructureRel =
					(LayoutPageTemplateStructureRel)session.get(
						LayoutPageTemplateStructureRelImpl.class,
						layoutPageTemplateStructureRel.getPrimaryKeyObj());
			}

			if ((layoutPageTemplateStructureRel != null) &&
				ctPersistenceHelper.isRemove(layoutPageTemplateStructureRel)) {

				session.delete(layoutPageTemplateStructureRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (layoutPageTemplateStructureRel != null) {
			clearCache(layoutPageTemplateStructureRel);
		}

		return layoutPageTemplateStructureRel;
	}

	@Override
	public LayoutPageTemplateStructureRel updateImpl(
		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel) {

		boolean isNew = layoutPageTemplateStructureRel.isNew();

		if (!(layoutPageTemplateStructureRel instanceof
				LayoutPageTemplateStructureRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					layoutPageTemplateStructureRel.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					layoutPageTemplateStructureRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in layoutPageTemplateStructureRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LayoutPageTemplateStructureRel implementation " +
					layoutPageTemplateStructureRel.getClass());
		}

		LayoutPageTemplateStructureRelModelImpl
			layoutPageTemplateStructureRelModelImpl =
				(LayoutPageTemplateStructureRelModelImpl)
					layoutPageTemplateStructureRel;

		if (Validator.isNull(layoutPageTemplateStructureRel.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			layoutPageTemplateStructureRel.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (layoutPageTemplateStructureRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				layoutPageTemplateStructureRel.setCreateDate(date);
			}
			else {
				layoutPageTemplateStructureRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!layoutPageTemplateStructureRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				layoutPageTemplateStructureRel.setModifiedDate(date);
			}
			else {
				layoutPageTemplateStructureRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(layoutPageTemplateStructureRel)) {
				if (!isNew) {
					session.evict(
						LayoutPageTemplateStructureRelImpl.class,
						layoutPageTemplateStructureRel.getPrimaryKeyObj());
				}

				session.save(layoutPageTemplateStructureRel);
			}
			else {
				layoutPageTemplateStructureRel =
					(LayoutPageTemplateStructureRel)session.merge(
						layoutPageTemplateStructureRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(layoutPageTemplateStructureRel, false);

		if (isNew) {
			layoutPageTemplateStructureRel.setNew(false);
		}

		layoutPageTemplateStructureRel.resetOriginalValues();

		return layoutPageTemplateStructureRel;
	}

	/**
	 * Returns the layout page template structure rel with the primary key or throws a <code>NoSuchPageTemplateStructureRelException</code> if it could not be found.
	 *
	 * @param layoutPageTemplateStructureRelId the primary key of the layout page template structure rel
	 * @return the layout page template structure rel
	 * @throws NoSuchPageTemplateStructureRelException if a layout page template structure rel with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel findByPrimaryKey(
			long layoutPageTemplateStructureRelId)
		throws NoSuchPageTemplateStructureRelException {

		return findByPrimaryKey((Serializable)layoutPageTemplateStructureRelId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the layout page template structure rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutPageTemplateStructureRelId the primary key of the layout page template structure rel
	 * @return the layout page template structure rel, or <code>null</code> if a layout page template structure rel with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRel fetchByPrimaryKey(
		long layoutPageTemplateStructureRelId) {

		return fetchByPrimaryKey(
			(Serializable)layoutPageTemplateStructureRelId);
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
		return "lPageTemplateStructureRelId";
	}

	@Override
	protected String getPKFieldName() {
		return "layoutPageTemplateStructureRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTUREREL;
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
		return LayoutPageTemplateStructureRelModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "LayoutPageTemplateStructureRel";
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
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("layoutPageTemplateStructureId");
		ctMergeColumnNames.add("segmentsExperienceId");
		ctMergeColumnNames.add("data_");
		ctMergeColumnNames.add("lastPublishDate");
		ctMergeColumnNames.add("status");
		ctMergeColumnNames.add("statusByUserId");
		ctMergeColumnNames.add("statusByUserName");
		ctMergeColumnNames.add("statusDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("lPageTemplateStructureRelId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {
				"layoutPageTemplateStructureId", "segmentsExperienceId"
			});
	}

	/**
	 * Initializes the layout page template structure rel persistence.
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
			_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE,
			_SQL_COUNT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE,
			LayoutPageTemplateStructureRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"layoutPageTemplateStructureRel.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				LayoutPageTemplateStructureRel::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(LayoutPageTemplateStructureRel::getUuid),
				LayoutPageTemplateStructureRel::getGroupId),
			_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE, "",
			new FinderColumn<>(
				"layoutPageTemplateStructureRel.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				LayoutPageTemplateStructureRel::getUuid),
			new FinderColumn<>(
				"layoutPageTemplateStructureRel.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				LayoutPageTemplateStructureRel::getGroupId));

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
				_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE,
				LayoutPageTemplateStructureRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"layoutPageTemplateStructureRel.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutPageTemplateStructureRel::getUuid),
				new FinderColumn<>(
					"layoutPageTemplateStructureRel.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateStructureRel::getCompanyId));

		_collectionPersistenceFinderByLayoutPageTemplateStructureId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByLayoutPageTemplateStructureId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"layoutPageTemplateStructureId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByLayoutPageTemplateStructureId",
					new String[] {Long.class.getName()},
					new String[] {"layoutPageTemplateStructureId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByLayoutPageTemplateStructureId",
					new String[] {Long.class.getName()},
					new String[] {"layoutPageTemplateStructureId"}, false),
				_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE,
				LayoutPageTemplateStructureRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"layoutPageTemplateStructureRel.",
					"layoutPageTemplateStructureId", FinderColumn.Type.LONG,
					"=", true, true,
					LayoutPageTemplateStructureRel::
						getLayoutPageTemplateStructureId));

		_collectionPersistenceFinderBySegmentsExperienceId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findBySegmentsExperienceId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"segmentsExperienceId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findBySegmentsExperienceId",
					new String[] {Long.class.getName()},
					new String[] {"segmentsExperienceId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countBySegmentsExperienceId",
					new String[] {Long.class.getName()},
					new String[] {"segmentsExperienceId"}, false),
				_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE,
				LayoutPageTemplateStructureRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"layoutPageTemplateStructureRel.", "segmentsExperienceId",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateStructureRel::getSegmentsExperienceId));

		_uniquePersistenceFinderByL_S = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByL_S",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {
					"layoutPageTemplateStructureId", "segmentsExperienceId"
				},
				0, 0, false,
				LayoutPageTemplateStructureRel::
					getLayoutPageTemplateStructureId,
				LayoutPageTemplateStructureRel::getSegmentsExperienceId),
			_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE, "",
			new FinderColumn<>(
				"layoutPageTemplateStructureRel.",
				"layoutPageTemplateStructureId", FinderColumn.Type.LONG, "=",
				true, true,
				LayoutPageTemplateStructureRel::
					getLayoutPageTemplateStructureId),
			new FinderColumn<>(
				"layoutPageTemplateStructureRel.", "segmentsExperienceId",
				FinderColumn.Type.LONG, "=", true, true,
				LayoutPageTemplateStructureRel::getSegmentsExperienceId));

		LayoutPageTemplateStructureRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		LayoutPageTemplateStructureRelUtil.setPersistence(null);

		entityCache.removeCache(
			LayoutPageTemplateStructureRelImpl.class.getName());
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
		LayoutPageTemplateStructureRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTUREREL =
		"SELECT layoutPageTemplateStructureRel FROM LayoutPageTemplateStructureRel layoutPageTemplateStructureRel";

	private static final String
		_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE =
			"SELECT layoutPageTemplateStructureRel FROM LayoutPageTemplateStructureRel layoutPageTemplateStructureRel WHERE ";

	private static final String
		_SQL_COUNT_LAYOUTPAGETEMPLATESTRUCTUREREL_WHERE =
			"SELECT COUNT(layoutPageTemplateStructureRel) FROM LayoutPageTemplateStructureRel layoutPageTemplateStructureRel WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No LayoutPageTemplateStructureRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutPageTemplateStructureRelPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "layoutPageTemplateStructureRelId", "data"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:298885241