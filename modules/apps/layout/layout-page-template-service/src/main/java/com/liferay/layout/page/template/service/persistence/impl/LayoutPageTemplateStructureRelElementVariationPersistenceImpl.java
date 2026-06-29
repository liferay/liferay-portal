/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.persistence.impl;

import com.liferay.layout.page.template.exception.DuplicateLayoutPageTemplateStructureRelElementVariationExternalReferenceCodeException;
import com.liferay.layout.page.template.exception.NoSuchPageTemplateStructureRelElementVariationException;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariation;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariationTable;
import com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationImpl;
import com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationModelImpl;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateStructureRelElementVariationPersistence;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateStructureRelElementVariationUtil;
import com.liferay.layout.page.template.service.persistence.impl.constants.LayoutPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
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
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
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
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the layout page template structure rel element variation service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(
	service = LayoutPageTemplateStructureRelElementVariationPersistence.class
)
public class LayoutPageTemplateStructureRelElementVariationPersistenceImpl
	extends BasePersistenceImpl
		<LayoutPageTemplateStructureRelElementVariation,
		 NoSuchPageTemplateStructureRelElementVariationException>
	implements LayoutPageTemplateStructureRelElementVariationPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LayoutPageTemplateStructureRelElementVariationUtil</code> to access the layout page template structure rel element variation persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LayoutPageTemplateStructureRelElementVariationImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<LayoutPageTemplateStructureRelElementVariation,
		 NoSuchPageTemplateStructureRelElementVariationException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	@Override
	public List<LayoutPageTemplateStructureRelElementVariation> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
			orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a matching layout page template structure rel element variation could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariation findByUuid_First(
			String uuid,
			OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
				orderByComparator)
		throws NoSuchPageTemplateStructureRelElementVariationException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariation fetchByUuid_First(
		String uuid,
		OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
			orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the layout page template structure rel element variations where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of layout page template structure rel element variations where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout page template structure rel element variations
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<LayoutPageTemplateStructureRelElementVariation,
		 NoSuchPageTemplateStructureRelElementVariationException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the layout page template structure rel element variation where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateStructureRelElementVariationException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout page template structure rel element variation
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a matching layout page template structure rel element variation could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariation findByUUID_G(
			String uuid, long groupId)
		throws NoSuchPageTemplateStructureRelElementVariationException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the layout page template structure rel element variation where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariation fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the layout page template structure rel element variation where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the layout page template structure rel element variation that was removed
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariation removeByUUID_G(
			String uuid, long groupId)
		throws NoSuchPageTemplateStructureRelElementVariationException {

		LayoutPageTemplateStructureRelElementVariation
			layoutPageTemplateStructureRelElementVariation = findByUUID_G(
				uuid, groupId);

		return remove(layoutPageTemplateStructureRelElementVariation);
	}

	/**
	 * Returns the number of layout page template structure rel element variations where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching layout page template structure rel element variations
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<LayoutPageTemplateStructureRelElementVariation,
		 NoSuchPageTemplateStructureRelElementVariationException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	@Override
	public List<LayoutPageTemplateStructureRelElementVariation> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
			orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a matching layout page template structure rel element variation could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariation findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
				orderByComparator)
		throws NoSuchPageTemplateStructureRelElementVariationException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariation fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
			orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the layout page template structure rel element variations where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of layout page template structure rel element variations where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout page template structure rel element variations
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<LayoutPageTemplateStructureRelElementVariation,
		 NoSuchPageTemplateStructureRelElementVariationException>
			_collectionPersistenceFinderByPlid;

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	@Override
	public List<LayoutPageTemplateStructureRelElementVariation> findByPlid(
		long plid, int start, int end,
		OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
			orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByPlid.find(
			finderCache, new Object[] {plid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a matching layout page template structure rel element variation could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariation findByPlid_First(
			long plid,
			OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
				orderByComparator)
		throws NoSuchPageTemplateStructureRelElementVariationException {

		return _collectionPersistenceFinderByPlid.findFirst(
			finderCache, new Object[] {plid}, orderByComparator);
	}

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariation fetchByPlid_First(
		long plid,
		OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
			orderByComparator) {

		return _collectionPersistenceFinderByPlid.fetchFirst(
			finderCache, new Object[] {plid}, orderByComparator);
	}

	/**
	 * Removes all the layout page template structure rel element variations where plid = &#63; from the database.
	 *
	 * @param plid the plid
	 */
	@Override
	public void removeByPlid(long plid) {
		_collectionPersistenceFinderByPlid.remove(
			finderCache, new Object[] {plid});
	}

	/**
	 * Returns the number of layout page template structure rel element variations where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the number of matching layout page template structure rel element variations
	 */
	@Override
	public int countByPlid(long plid) {
		return _collectionPersistenceFinderByPlid.count(
			finderCache, new Object[] {plid});
	}

	private CollectionPersistenceFinder
		<LayoutPageTemplateStructureRelElementVariation,
		 NoSuchPageTemplateStructureRelElementVariationException>
			_collectionPersistenceFinderBySegmentsExperienceERC;

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	@Override
	public List<LayoutPageTemplateStructureRelElementVariation>
		findBySegmentsExperienceERC(
			String segmentsExperienceERC, int start, int end,
			OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
				orderByComparator,
			boolean useFinderCache) {

		return _collectionPersistenceFinderBySegmentsExperienceERC.find(
			finderCache, new Object[] {segmentsExperienceERC}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where segmentsExperienceERC = &#63;.
	 *
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a matching layout page template structure rel element variation could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariation
			findBySegmentsExperienceERC_First(
				String segmentsExperienceERC,
				OrderByComparator
					<LayoutPageTemplateStructureRelElementVariation>
						orderByComparator)
		throws NoSuchPageTemplateStructureRelElementVariationException {

		return _collectionPersistenceFinderBySegmentsExperienceERC.findFirst(
			finderCache, new Object[] {segmentsExperienceERC},
			orderByComparator);
	}

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where segmentsExperienceERC = &#63;.
	 *
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariation
		fetchBySegmentsExperienceERC_First(
			String segmentsExperienceERC,
			OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
				orderByComparator) {

		return _collectionPersistenceFinderBySegmentsExperienceERC.fetchFirst(
			finderCache, new Object[] {segmentsExperienceERC},
			orderByComparator);
	}

	/**
	 * Removes all the layout page template structure rel element variations where segmentsExperienceERC = &#63; from the database.
	 *
	 * @param segmentsExperienceERC the segments experience erc
	 */
	@Override
	public void removeBySegmentsExperienceERC(String segmentsExperienceERC) {
		_collectionPersistenceFinderBySegmentsExperienceERC.remove(
			finderCache, new Object[] {segmentsExperienceERC});
	}

	/**
	 * Returns the number of layout page template structure rel element variations where segmentsExperienceERC = &#63;.
	 *
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the number of matching layout page template structure rel element variations
	 */
	@Override
	public int countBySegmentsExperienceERC(String segmentsExperienceERC) {
		return _collectionPersistenceFinderBySegmentsExperienceERC.count(
			finderCache, new Object[] {segmentsExperienceERC});
	}

	private CollectionPersistenceFinder
		<LayoutPageTemplateStructureRelElementVariation,
		 NoSuchPageTemplateStructureRelElementVariationException>
			_collectionPersistenceFinderByP_SEERC;

	/**
	 * Returns an ordered range of all the layout page template structure rel element variations where plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureRelElementVariationModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @param start the lower bound of the range of layout page template structure rel element variations
	 * @param end the upper bound of the range of layout page template structure rel element variations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variations
	 */
	@Override
	public List<LayoutPageTemplateStructureRelElementVariation> findByP_SEERC(
		long plid, String segmentsExperienceERC, int start, int end,
		OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
			orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_SEERC.find(
			finderCache, new Object[] {plid, segmentsExperienceERC}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a matching layout page template structure rel element variation could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariation findByP_SEERC_First(
			long plid, String segmentsExperienceERC,
			OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
				orderByComparator)
		throws NoSuchPageTemplateStructureRelElementVariationException {

		return _collectionPersistenceFinderByP_SEERC.findFirst(
			finderCache, new Object[] {plid, segmentsExperienceERC},
			orderByComparator);
	}

	/**
	 * Returns the first layout page template structure rel element variation in the ordered set where plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariation fetchByP_SEERC_First(
		long plid, String segmentsExperienceERC,
		OrderByComparator<LayoutPageTemplateStructureRelElementVariation>
			orderByComparator) {

		return _collectionPersistenceFinderByP_SEERC.fetchFirst(
			finderCache, new Object[] {plid, segmentsExperienceERC},
			orderByComparator);
	}

	/**
	 * Removes all the layout page template structure rel element variations where plid = &#63; and segmentsExperienceERC = &#63; from the database.
	 *
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 */
	@Override
	public void removeByP_SEERC(long plid, String segmentsExperienceERC) {
		_collectionPersistenceFinderByP_SEERC.remove(
			finderCache, new Object[] {plid, segmentsExperienceERC});
	}

	/**
	 * Returns the number of layout page template structure rel element variations where plid = &#63; and segmentsExperienceERC = &#63;.
	 *
	 * @param plid the plid
	 * @param segmentsExperienceERC the segments experience erc
	 * @return the number of matching layout page template structure rel element variations
	 */
	@Override
	public int countByP_SEERC(long plid, String segmentsExperienceERC) {
		return _collectionPersistenceFinderByP_SEERC.count(
			finderCache, new Object[] {plid, segmentsExperienceERC});
	}

	private UniquePersistenceFinder
		<LayoutPageTemplateStructureRelElementVariation,
		 NoSuchPageTemplateStructureRelElementVariationException>
			_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the layout page template structure rel element variation where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateStructureRelElementVariationException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout page template structure rel element variation
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a matching layout page template structure rel element variation could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariation findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchPageTemplateStructureRelElementVariationException {

		return _uniquePersistenceFinderByERC_G.find(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the layout page template structure rel element variation where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template structure rel element variation, or <code>null</code> if a matching layout page template structure rel element variation could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariation fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			finderCache, new Object[] {externalReferenceCode, groupId},
			useFinderCache);
	}

	/**
	 * Removes the layout page template structure rel element variation where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the layout page template structure rel element variation that was removed
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariation removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchPageTemplateStructureRelElementVariationException {

		LayoutPageTemplateStructureRelElementVariation
			layoutPageTemplateStructureRelElementVariation = findByERC_G(
				externalReferenceCode, groupId);

		return remove(layoutPageTemplateStructureRelElementVariation);
	}

	/**
	 * Returns the number of layout page template structure rel element variations where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching layout page template structure rel element variations
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	public LayoutPageTemplateStructureRelElementVariationPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put(
			"layoutPageTemplateStructureRelElementVariationId",
			"lptsRelElementVariationId");

		setDBColumnNames(dbColumnNames);

		setModelClass(LayoutPageTemplateStructureRelElementVariation.class);

		setModelImplClass(
			LayoutPageTemplateStructureRelElementVariationImpl.class);
		setModelPKClass(long.class);

		setTable(LayoutPageTemplateStructureRelElementVariationTable.INSTANCE);
	}

	/**
	 * Creates a new layout page template structure rel element variation with the primary key. Does not add the layout page template structure rel element variation to the database.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationId the primary key for the new layout page template structure rel element variation
	 * @return the new layout page template structure rel element variation
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariation create(
		long layoutPageTemplateStructureRelElementVariationId) {

		LayoutPageTemplateStructureRelElementVariation
			layoutPageTemplateStructureRelElementVariation =
				new LayoutPageTemplateStructureRelElementVariationImpl();

		layoutPageTemplateStructureRelElementVariation.setNew(true);
		layoutPageTemplateStructureRelElementVariation.setPrimaryKey(
			layoutPageTemplateStructureRelElementVariationId);

		String uuid = PortalUUIDUtil.generate();

		layoutPageTemplateStructureRelElementVariation.setUuid(uuid);

		layoutPageTemplateStructureRelElementVariation.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return layoutPageTemplateStructureRelElementVariation;
	}

	/**
	 * Removes the layout page template structure rel element variation with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationId the primary key of the layout page template structure rel element variation
	 * @return the layout page template structure rel element variation that was removed
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a layout page template structure rel element variation with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariation remove(
			long layoutPageTemplateStructureRelElementVariationId)
		throws NoSuchPageTemplateStructureRelElementVariationException {

		return remove(
			(Serializable)layoutPageTemplateStructureRelElementVariationId);
	}

	@Override
	protected LayoutPageTemplateStructureRelElementVariation removeImpl(
		LayoutPageTemplateStructureRelElementVariation
			layoutPageTemplateStructureRelElementVariation) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(
					layoutPageTemplateStructureRelElementVariation)) {

				layoutPageTemplateStructureRelElementVariation =
					(LayoutPageTemplateStructureRelElementVariation)session.get(
						LayoutPageTemplateStructureRelElementVariationImpl.
							class,
						layoutPageTemplateStructureRelElementVariation.
							getPrimaryKeyObj());
			}

			if ((layoutPageTemplateStructureRelElementVariation != null) &&
				ctPersistenceHelper.isRemove(
					layoutPageTemplateStructureRelElementVariation)) {

				session.delete(layoutPageTemplateStructureRelElementVariation);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (layoutPageTemplateStructureRelElementVariation != null) {
			clearCache(layoutPageTemplateStructureRelElementVariation);
		}

		return layoutPageTemplateStructureRelElementVariation;
	}

	@Override
	public LayoutPageTemplateStructureRelElementVariation updateImpl(
		LayoutPageTemplateStructureRelElementVariation
			layoutPageTemplateStructureRelElementVariation) {

		boolean isNew = layoutPageTemplateStructureRelElementVariation.isNew();

		if (!(layoutPageTemplateStructureRelElementVariation instanceof
				LayoutPageTemplateStructureRelElementVariationModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					layoutPageTemplateStructureRelElementVariation.
						getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					layoutPageTemplateStructureRelElementVariation);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in layoutPageTemplateStructureRelElementVariation proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LayoutPageTemplateStructureRelElementVariation implementation " +
					layoutPageTemplateStructureRelElementVariation.getClass());
		}

		LayoutPageTemplateStructureRelElementVariationModelImpl
			layoutPageTemplateStructureRelElementVariationModelImpl =
				(LayoutPageTemplateStructureRelElementVariationModelImpl)
					layoutPageTemplateStructureRelElementVariation;

		if (Validator.isNull(
				layoutPageTemplateStructureRelElementVariation.getUuid())) {

			String uuid = PortalUUIDUtil.generate();

			layoutPageTemplateStructureRelElementVariation.setUuid(uuid);
		}

		if (Validator.isNull(
				layoutPageTemplateStructureRelElementVariation.
					getExternalReferenceCode())) {

			layoutPageTemplateStructureRelElementVariation.
				setExternalReferenceCode(
					layoutPageTemplateStructureRelElementVariation.getUuid());
		}
		else {
			if (!Objects.equals(
					layoutPageTemplateStructureRelElementVariationModelImpl.
						getColumnOriginalValue("externalReferenceCode"),
					layoutPageTemplateStructureRelElementVariation.
						getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId =
						layoutPageTemplateStructureRelElementVariation.
							getCompanyId();

					long groupId =
						layoutPageTemplateStructureRelElementVariation.
							getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK =
							layoutPageTemplateStructureRelElementVariation.
								getPrimaryKey();
					}

					try {
						layoutPageTemplateStructureRelElementVariation.
							setExternalReferenceCode(
								SanitizerUtil.sanitize(
									companyId, groupId, userId,
									LayoutPageTemplateStructureRelElementVariation.class.
										getName(),
									classPK, ContentTypes.TEXT_HTML,
									Sanitizer.MODE_ALL,
									layoutPageTemplateStructureRelElementVariation.
										getExternalReferenceCode(),
									null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			LayoutPageTemplateStructureRelElementVariation
				ercLayoutPageTemplateStructureRelElementVariation =
					fetchByERC_G(
						layoutPageTemplateStructureRelElementVariation.
							getExternalReferenceCode(),
						layoutPageTemplateStructureRelElementVariation.
							getGroupId());

			if (isNew) {
				if (ercLayoutPageTemplateStructureRelElementVariation != null) {
					throw new DuplicateLayoutPageTemplateStructureRelElementVariationExternalReferenceCodeException(
						"Duplicate layout page template structure rel element variation with external reference code " +
							layoutPageTemplateStructureRelElementVariation.
								getExternalReferenceCode() + " and group " +
									layoutPageTemplateStructureRelElementVariation.
										getGroupId());
				}
			}
			else {
				if ((ercLayoutPageTemplateStructureRelElementVariation !=
						null) &&
					(layoutPageTemplateStructureRelElementVariation.
						getLayoutPageTemplateStructureRelElementVariationId() !=
							ercLayoutPageTemplateStructureRelElementVariation.
								getLayoutPageTemplateStructureRelElementVariationId())) {

					throw new DuplicateLayoutPageTemplateStructureRelElementVariationExternalReferenceCodeException(
						"Duplicate layout page template structure rel element variation with external reference code " +
							layoutPageTemplateStructureRelElementVariation.
								getExternalReferenceCode() + " and group " +
									layoutPageTemplateStructureRelElementVariation.
										getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew &&
			(layoutPageTemplateStructureRelElementVariation.getCreateDate() ==
				null)) {

			if (serviceContext == null) {
				layoutPageTemplateStructureRelElementVariation.setCreateDate(
					date);
			}
			else {
				layoutPageTemplateStructureRelElementVariation.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!layoutPageTemplateStructureRelElementVariationModelImpl.
				hasSetModifiedDate()) {

			if (serviceContext == null) {
				layoutPageTemplateStructureRelElementVariation.setModifiedDate(
					date);
			}
			else {
				layoutPageTemplateStructureRelElementVariation.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(
					layoutPageTemplateStructureRelElementVariation)) {

				if (!isNew) {
					session.evict(
						LayoutPageTemplateStructureRelElementVariationImpl.
							class,
						layoutPageTemplateStructureRelElementVariation.
							getPrimaryKeyObj());
				}

				session.save(layoutPageTemplateStructureRelElementVariation);
			}
			else {
				layoutPageTemplateStructureRelElementVariation =
					(LayoutPageTemplateStructureRelElementVariation)
						session.merge(
							layoutPageTemplateStructureRelElementVariation);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(
			layoutPageTemplateStructureRelElementVariation, false);

		if (isNew) {
			layoutPageTemplateStructureRelElementVariation.setNew(false);
		}

		layoutPageTemplateStructureRelElementVariation.resetOriginalValues();

		return layoutPageTemplateStructureRelElementVariation;
	}

	/**
	 * Returns the layout page template structure rel element variation with the primary key or throws a <code>NoSuchPageTemplateStructureRelElementVariationException</code> if it could not be found.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationId the primary key of the layout page template structure rel element variation
	 * @return the layout page template structure rel element variation
	 * @throws NoSuchPageTemplateStructureRelElementVariationException if a layout page template structure rel element variation with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariation findByPrimaryKey(
			long layoutPageTemplateStructureRelElementVariationId)
		throws NoSuchPageTemplateStructureRelElementVariationException {

		return findByPrimaryKey(
			(Serializable)layoutPageTemplateStructureRelElementVariationId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the layout page template structure rel element variation with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationId the primary key of the layout page template structure rel element variation
	 * @return the layout page template structure rel element variation, or <code>null</code> if a layout page template structure rel element variation with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariation fetchByPrimaryKey(
		long layoutPageTemplateStructureRelElementVariationId) {

		return fetchByPrimaryKey(
			(Serializable)layoutPageTemplateStructureRelElementVariationId);
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
		return "lptsRelElementVariationId";
	}

	@Override
	protected String getPKFieldName() {
		return "layoutPageTemplateStructureRelElementVariationId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATION;
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
		return LayoutPageTemplateStructureRelElementVariationModelImpl.
			TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "LPTSRelElementVariation";
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
		ctStrictColumnNames.add("externalReferenceCode");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("audienceEntryERC");
		ctMergeColumnNames.add("hide");
		ctMergeColumnNames.add("html");
		ctMergeColumnNames.add("js");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("plid");
		ctMergeColumnNames.add("segmentsExperienceERC");
		ctMergeColumnNames.add("targetElement");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("lptsRelElementVariationId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the layout page template structure rel element variation persistence.
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
			_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATION_WHERE,
			_SQL_COUNT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATION_WHERE,
			LayoutPageTemplateStructureRelElementVariationModelImpl.
				ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"layoutPageTemplateStructureRelElementVariation.", "uuid",
				"uuid_", FinderColumn.Type.STRING, "=", true, true,
				LayoutPageTemplateStructureRelElementVariation::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(
					LayoutPageTemplateStructureRelElementVariation::getUuid),
				LayoutPageTemplateStructureRelElementVariation::getGroupId),
			_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATION_WHERE,
			"",
			new FinderColumn<>(
				"layoutPageTemplateStructureRelElementVariation.", "uuid",
				"uuid_", FinderColumn.Type.STRING, "=", true, true,
				LayoutPageTemplateStructureRelElementVariation::getUuid),
			new FinderColumn<>(
				"layoutPageTemplateStructureRelElementVariation.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				LayoutPageTemplateStructureRelElementVariation::getGroupId));

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
				_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATION_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATION_WHERE,
				LayoutPageTemplateStructureRelElementVariationModelImpl.
					ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"layoutPageTemplateStructureRelElementVariation.", "uuid",
					"uuid_", FinderColumn.Type.STRING, "=", true, true,
					LayoutPageTemplateStructureRelElementVariation::getUuid),
				new FinderColumn<>(
					"layoutPageTemplateStructureRelElementVariation.",
					"companyId", FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateStructureRelElementVariation::
						getCompanyId));

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
			_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATION_WHERE,
			_SQL_COUNT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATION_WHERE,
			LayoutPageTemplateStructureRelElementVariationModelImpl.
				ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"layoutPageTemplateStructureRelElementVariation.", "plid",
				FinderColumn.Type.LONG, "=", true, true,
				LayoutPageTemplateStructureRelElementVariation::getPlid));

		_collectionPersistenceFinderBySegmentsExperienceERC =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findBySegmentsExperienceERC",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"segmentsExperienceERC"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findBySegmentsExperienceERC",
					new String[] {String.class.getName()},
					new String[] {"segmentsExperienceERC"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countBySegmentsExperienceERC",
					new String[] {String.class.getName()},
					new String[] {"segmentsExperienceERC"}, 0, 1, false, null),
				_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATION_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATION_WHERE,
				LayoutPageTemplateStructureRelElementVariationModelImpl.
					ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"layoutPageTemplateStructureRelElementVariation.",
					"segmentsExperienceERC", FinderColumn.Type.STRING, "=",
					true, true,
					LayoutPageTemplateStructureRelElementVariation::
						getSegmentsExperienceERC));

		_collectionPersistenceFinderByP_SEERC =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_SEERC",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"plid", "segmentsExperienceERC"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_SEERC",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"plid", "segmentsExperienceERC"}, 0, 2, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_SEERC",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"plid", "segmentsExperienceERC"}, 0, 2, false,
					null),
				_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATION_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATION_WHERE,
				LayoutPageTemplateStructureRelElementVariationModelImpl.
					ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"layoutPageTemplateStructureRelElementVariation.", "plid",
					FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateStructureRelElementVariation::getPlid),
				new FinderColumn<>(
					"layoutPageTemplateStructureRelElementVariation.",
					"segmentsExperienceERC", FinderColumn.Type.STRING, "=",
					true, true,
					LayoutPageTemplateStructureRelElementVariation::
						getSegmentsExperienceERC));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(
					LayoutPageTemplateStructureRelElementVariation::
						getExternalReferenceCode),
				LayoutPageTemplateStructureRelElementVariation::getGroupId),
			_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATION_WHERE,
			"",
			new FinderColumn<>(
				"layoutPageTemplateStructureRelElementVariation.",
				"externalReferenceCode", FinderColumn.Type.STRING, "=", true,
				true,
				LayoutPageTemplateStructureRelElementVariation::
					getExternalReferenceCode),
			new FinderColumn<>(
				"layoutPageTemplateStructureRelElementVariation.", "groupId",
				FinderColumn.Type.LONG, "=", true, true,
				LayoutPageTemplateStructureRelElementVariation::getGroupId));

		LayoutPageTemplateStructureRelElementVariationUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		LayoutPageTemplateStructureRelElementVariationUtil.setPersistence(null);

		entityCache.removeCache(
			LayoutPageTemplateStructureRelElementVariationImpl.class.getName());
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
		LayoutPageTemplateStructureRelElementVariationModelImpl.ENTITY_ALIAS +
			".";

	private static final String
		_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATION =
			"SELECT layoutPageTemplateStructureRelElementVariation FROM LayoutPageTemplateStructureRelElementVariation layoutPageTemplateStructureRelElementVariation";

	private static final String
		_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATION_WHERE =
			"SELECT layoutPageTemplateStructureRelElementVariation FROM LayoutPageTemplateStructureRelElementVariation layoutPageTemplateStructureRelElementVariation WHERE ";

	private static final String
		_SQL_COUNT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATION_WHERE =
			"SELECT COUNT(layoutPageTemplateStructureRelElementVariation) FROM LayoutPageTemplateStructureRelElementVariation layoutPageTemplateStructureRelElementVariation WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No LayoutPageTemplateStructureRelElementVariation exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutPageTemplateStructureRelElementVariationPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {
			"uuid", "layoutPageTemplateStructureRelElementVariationId"
		});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:425218736