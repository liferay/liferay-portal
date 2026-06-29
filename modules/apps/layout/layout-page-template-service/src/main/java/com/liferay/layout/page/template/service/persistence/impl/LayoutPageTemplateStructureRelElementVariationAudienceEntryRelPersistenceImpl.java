/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.persistence.impl;

import com.liferay.layout.page.template.exception.DuplicateLayoutPageTemplateStructureRelElementVariationAudienceEntryRelExternalReferenceCodeException;
import com.liferay.layout.page.template.exception.NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariationAudienceEntryRel;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelTable;
import com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelImpl;
import com.liferay.layout.page.template.model.impl.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelPersistence;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateStructureRelElementVariationAudienceEntryRelUtil;
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
 * The persistence implementation for the layout page template structure rel element variation audience entry rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(
	service = LayoutPageTemplateStructureRelElementVariationAudienceEntryRelPersistence.class
)
public class
	LayoutPageTemplateStructureRelElementVariationAudienceEntryRelPersistenceImpl
		extends BasePersistenceImpl
			<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel,
			 NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException>
		implements LayoutPageTemplateStructureRelElementVariationAudienceEntryRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LayoutPageTemplateStructureRelElementVariationAudienceEntryRelUtil</code> to access the layout page template structure rel element variation audience entry rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRelImpl.
			class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel,
		 NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the layout page template structure rel element variation audience entry rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variation audience entry rels
	 */
	@Override
	public List<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
		findByUuid(
			String uuid, int start, int end,
			OrderByComparator
				<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
					orderByComparator,
			boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout page template structure rel element variation audience entry rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation audience entry rel
	 * @throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			findByUuid_First(
				String uuid,
				OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator)
		throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first layout page template structure rel element variation audience entry rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation audience entry rel, or <code>null</code> if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchByUuid_First(
			String uuid,
			OrderByComparator
				<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
					orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the layout page template structure rel element variation audience entry rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of layout page template structure rel element variation audience entry rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout page template structure rel element variation audience entry rels
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel,
		 NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the layout page template structure rel element variation audience entry rel where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout page template structure rel element variation audience entry rel
	 * @throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			findByUUID_G(String uuid, long groupId)
		throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the layout page template structure rel element variation audience entry rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template structure rel element variation audience entry rel, or <code>null</code> if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchByUUID_G(String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the layout page template structure rel element variation audience entry rel where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the layout page template structure rel element variation audience entry rel that was removed
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			removeByUUID_G(String uuid, long groupId)
		throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException {

		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			layoutPageTemplateStructureRelElementVariationAudienceEntryRel =
				findByUUID_G(uuid, groupId);

		return remove(
			layoutPageTemplateStructureRelElementVariationAudienceEntryRel);
	}

	/**
	 * Returns the number of layout page template structure rel element variation audience entry rels where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching layout page template structure rel element variation audience entry rels
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel,
		 NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the layout page template structure rel element variation audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variation audience entry rels
	 */
	@Override
	public List<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
		findByUuid_C(
			String uuid, long companyId, int start, int end,
			OrderByComparator
				<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
					orderByComparator,
			boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template structure rel element variation audience entry rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation audience entry rel
	 * @throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			findByUuid_C_First(
				String uuid, long companyId,
				OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator)
		throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first layout page template structure rel element variation audience entry rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation audience entry rel, or <code>null</code> if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator
				<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
					orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the layout page template structure rel element variation audience entry rels where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of layout page template structure rel element variation audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout page template structure rel element variation audience entry rels
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel,
		 NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException>
			_collectionPersistenceFinderByLayoutPageTemplateStructureRelElementVariationERC;

	/**
	 * Returns an ordered range of all the layout page template structure rel element variation audience entry rels where layoutPageTemplateStructureRelElementVariationERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param layoutPageTemplateStructureRelElementVariationERC the layout page template structure rel element variation erc
	 * @param start the lower bound of the range of layout page template structure rel element variation audience entry rels
	 * @param end the upper bound of the range of layout page template structure rel element variation audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template structure rel element variation audience entry rels
	 */
	@Override
	public List<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
		findByLayoutPageTemplateStructureRelElementVariationERC(
			String layoutPageTemplateStructureRelElementVariationERC, int start,
			int end,
			OrderByComparator
				<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
					orderByComparator,
			boolean useFinderCache) {

		return _collectionPersistenceFinderByLayoutPageTemplateStructureRelElementVariationERC.
			find(
				finderCache,
				new Object[] {
					layoutPageTemplateStructureRelElementVariationERC
				},
				start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template structure rel element variation audience entry rel in the ordered set where layoutPageTemplateStructureRelElementVariationERC = &#63;.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationERC the layout page template structure rel element variation erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation audience entry rel
	 * @throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			findByLayoutPageTemplateStructureRelElementVariationERC_First(
				String layoutPageTemplateStructureRelElementVariationERC,
				OrderByComparator
					<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
						orderByComparator)
		throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException {

		return _collectionPersistenceFinderByLayoutPageTemplateStructureRelElementVariationERC.
			findFirst(
				finderCache,
				new Object[] {
					layoutPageTemplateStructureRelElementVariationERC
				},
				orderByComparator);
	}

	/**
	 * Returns the first layout page template structure rel element variation audience entry rel in the ordered set where layoutPageTemplateStructureRelElementVariationERC = &#63;.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationERC the layout page template structure rel element variation erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template structure rel element variation audience entry rel, or <code>null</code> if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchByLayoutPageTemplateStructureRelElementVariationERC_First(
			String layoutPageTemplateStructureRelElementVariationERC,
			OrderByComparator
				<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel>
					orderByComparator) {

		return _collectionPersistenceFinderByLayoutPageTemplateStructureRelElementVariationERC.
			fetchFirst(
				finderCache,
				new Object[] {
					layoutPageTemplateStructureRelElementVariationERC
				},
				orderByComparator);
	}

	/**
	 * Removes all the layout page template structure rel element variation audience entry rels where layoutPageTemplateStructureRelElementVariationERC = &#63; from the database.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationERC the layout page template structure rel element variation erc
	 */
	@Override
	public void removeByLayoutPageTemplateStructureRelElementVariationERC(
		String layoutPageTemplateStructureRelElementVariationERC) {

		_collectionPersistenceFinderByLayoutPageTemplateStructureRelElementVariationERC.
			remove(
				finderCache,
				new Object[] {
					layoutPageTemplateStructureRelElementVariationERC
				});
	}

	/**
	 * Returns the number of layout page template structure rel element variation audience entry rels where layoutPageTemplateStructureRelElementVariationERC = &#63;.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationERC the layout page template structure rel element variation erc
	 * @return the number of matching layout page template structure rel element variation audience entry rels
	 */
	@Override
	public int countByLayoutPageTemplateStructureRelElementVariationERC(
		String layoutPageTemplateStructureRelElementVariationERC) {

		return _collectionPersistenceFinderByLayoutPageTemplateStructureRelElementVariationERC.
			count(
				finderCache,
				new Object[] {
					layoutPageTemplateStructureRelElementVariationERC
				});
	}

	private UniquePersistenceFinder
		<LayoutPageTemplateStructureRelElementVariationAudienceEntryRel,
		 NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException>
			_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the layout page template structure rel element variation audience entry rel where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout page template structure rel element variation audience entry rel
	 * @throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException {

		return _uniquePersistenceFinderByERC_G.find(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the layout page template structure rel element variation audience entry rel where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template structure rel element variation audience entry rel, or <code>null</code> if a matching layout page template structure rel element variation audience entry rel could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchByERC_G(
			String externalReferenceCode, long groupId,
			boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			finderCache, new Object[] {externalReferenceCode, groupId},
			useFinderCache);
	}

	/**
	 * Removes the layout page template structure rel element variation audience entry rel where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the layout page template structure rel element variation audience entry rel that was removed
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			removeByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException {

		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			layoutPageTemplateStructureRelElementVariationAudienceEntryRel =
				findByERC_G(externalReferenceCode, groupId);

		return remove(
			layoutPageTemplateStructureRelElementVariationAudienceEntryRel);
	}

	/**
	 * Returns the number of layout page template structure rel element variation audience entry rels where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching layout page template structure rel element variation audience entry rels
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			finderCache, new Object[] {externalReferenceCode, groupId});
	}

	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put(
			"layoutPageTemplateStructureRelElementVariationAudienceEntryRelId",
			"lptsrevAudienceEntryRelId");
		dbColumnNames.put(
			"layoutPageTemplateStructureRelElementVariationERC",
			"lptsRelElementVariationERC");

		setDBColumnNames(dbColumnNames);

		setModelClass(
			LayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				class);

		setModelImplClass(
			LayoutPageTemplateStructureRelElementVariationAudienceEntryRelImpl.
				class);
		setModelPKClass(long.class);

		setTable(
			LayoutPageTemplateStructureRelElementVariationAudienceEntryRelTable.
				INSTANCE);
	}

	/**
	 * Creates a new layout page template structure rel element variation audience entry rel with the primary key. Does not add the layout page template structure rel element variation audience entry rel to the database.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationAudienceEntryRelId the primary key for the new layout page template structure rel element variation audience entry rel
	 * @return the new layout page template structure rel element variation audience entry rel
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		create(
			long
				layoutPageTemplateStructureRelElementVariationAudienceEntryRelId) {

		LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			layoutPageTemplateStructureRelElementVariationAudienceEntryRel =
				new LayoutPageTemplateStructureRelElementVariationAudienceEntryRelImpl();

		layoutPageTemplateStructureRelElementVariationAudienceEntryRel.setNew(
			true);
		layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setPrimaryKey(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRelId);

		String uuid = PortalUUIDUtil.generate();

		layoutPageTemplateStructureRelElementVariationAudienceEntryRel.setUuid(
			uuid);

		layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			setCompanyId(CompanyThreadLocal.getCompanyId());

		return layoutPageTemplateStructureRelElementVariationAudienceEntryRel;
	}

	/**
	 * Removes the layout page template structure rel element variation audience entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationAudienceEntryRelId the primary key of the layout page template structure rel element variation audience entry rel
	 * @return the layout page template structure rel element variation audience entry rel that was removed
	 * @throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException if a layout page template structure rel element variation audience entry rel with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			remove(
				long
					layoutPageTemplateStructureRelElementVariationAudienceEntryRelId)
		throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException {

		return remove(
			(Serializable)
				layoutPageTemplateStructureRelElementVariationAudienceEntryRelId);
	}

	@Override
	protected LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		removeImpl(
			LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(
					layoutPageTemplateStructureRelElementVariationAudienceEntryRel)) {

				layoutPageTemplateStructureRelElementVariationAudienceEntryRel =
					(LayoutPageTemplateStructureRelElementVariationAudienceEntryRel)
						session.get(
							LayoutPageTemplateStructureRelElementVariationAudienceEntryRelImpl.class,
							layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
								getPrimaryKeyObj());
			}

			if ((layoutPageTemplateStructureRelElementVariationAudienceEntryRel !=
					null) &&
				ctPersistenceHelper.isRemove(
					layoutPageTemplateStructureRelElementVariationAudienceEntryRel)) {

				session.delete(
					layoutPageTemplateStructureRelElementVariationAudienceEntryRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (layoutPageTemplateStructureRelElementVariationAudienceEntryRel !=
				null) {

			clearCache(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel);
		}

		return layoutPageTemplateStructureRelElementVariationAudienceEntryRel;
	}

	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		updateImpl(
			LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel) {

		boolean isNew =
			layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				isNew();

		if (!(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel instanceof
					LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
						getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					layoutPageTemplateStructureRelElementVariationAudienceEntryRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in layoutPageTemplateStructureRelElementVariationAudienceEntryRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LayoutPageTemplateStructureRelElementVariationAudienceEntryRel implementation " +
					layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
						getClass());
		}

		LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl
			layoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl =
				(LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl)
					layoutPageTemplateStructureRelElementVariationAudienceEntryRel;

		if (Validator.isNull(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
					getUuid())) {

			String uuid = PortalUUIDUtil.generate();

			layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				setUuid(uuid);
		}

		if (Validator.isNull(
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
					getExternalReferenceCode())) {

			layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				setExternalReferenceCode(
					layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
						getUuid());
		}
		else {
			if (!Objects.equals(
					layoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl.
						getColumnOriginalValue("externalReferenceCode"),
					layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
						getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId =
						layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
							getCompanyId();

					long groupId =
						layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
							getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK =
							layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
								getPrimaryKey();
					}

					try {
						layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
							setExternalReferenceCode(
								SanitizerUtil.sanitize(
									companyId, groupId, userId,
									LayoutPageTemplateStructureRelElementVariationAudienceEntryRel.class.
										getName(),
									classPK, ContentTypes.TEXT_HTML,
									Sanitizer.MODE_ALL,
									layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
										getExternalReferenceCode(),
									null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
				ercLayoutPageTemplateStructureRelElementVariationAudienceEntryRel =
					fetchByERC_G(
						layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
							getExternalReferenceCode(),
						layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
							getGroupId());

			if (isNew) {
				if (ercLayoutPageTemplateStructureRelElementVariationAudienceEntryRel !=
						null) {

					throw new DuplicateLayoutPageTemplateStructureRelElementVariationAudienceEntryRelExternalReferenceCodeException(
						"Duplicate layout page template structure rel element variation audience entry rel with external reference code " +
							layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
								getExternalReferenceCode() + " and group " +
									layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
										getGroupId());
				}
			}
			else {
				if ((ercLayoutPageTemplateStructureRelElementVariationAudienceEntryRel !=
						null) &&
					(layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
						getLayoutPageTemplateStructureRelElementVariationAudienceEntryRelId() !=
							ercLayoutPageTemplateStructureRelElementVariationAudienceEntryRel.
								getLayoutPageTemplateStructureRelElementVariationAudienceEntryRelId())) {

					throw new DuplicateLayoutPageTemplateStructureRelElementVariationAudienceEntryRelExternalReferenceCodeException(
						"Duplicate layout page template structure rel element variation audience entry rel with external reference code " +
							layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
								getExternalReferenceCode() + " and group " +
									layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
										getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew &&
			(layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				getCreateDate() == null)) {

			if (serviceContext == null) {
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
					setCreateDate(date);
			}
			else {
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
					setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!layoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl.
				hasSetModifiedDate()) {

			if (serviceContext == null) {
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
					setModifiedDate(date);
			}
			else {
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
					setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(
					layoutPageTemplateStructureRelElementVariationAudienceEntryRel)) {

				if (!isNew) {
					session.evict(
						LayoutPageTemplateStructureRelElementVariationAudienceEntryRelImpl.class,
						layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
							getPrimaryKeyObj());
				}

				session.save(
					layoutPageTemplateStructureRelElementVariationAudienceEntryRel);
			}
			else {
				layoutPageTemplateStructureRelElementVariationAudienceEntryRel =
					(LayoutPageTemplateStructureRelElementVariationAudienceEntryRel)
						session.merge(
							layoutPageTemplateStructureRelElementVariationAudienceEntryRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(
			layoutPageTemplateStructureRelElementVariationAudienceEntryRel,
			false);

		if (isNew) {
			layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
				setNew(false);
		}

		layoutPageTemplateStructureRelElementVariationAudienceEntryRel.
			resetOriginalValues();

		return layoutPageTemplateStructureRelElementVariationAudienceEntryRel;
	}

	/**
	 * Returns the layout page template structure rel element variation audience entry rel with the primary key or throws a <code>NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException</code> if it could not be found.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationAudienceEntryRelId the primary key of the layout page template structure rel element variation audience entry rel
	 * @return the layout page template structure rel element variation audience entry rel
	 * @throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException if a layout page template structure rel element variation audience entry rel with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
			findByPrimaryKey(
				long
					layoutPageTemplateStructureRelElementVariationAudienceEntryRelId)
		throws NoSuchPageTemplateStructureRelElementVariationAudienceEntryRelException {

		return findByPrimaryKey(
			(Serializable)
				layoutPageTemplateStructureRelElementVariationAudienceEntryRelId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the layout page template structure rel element variation audience entry rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutPageTemplateStructureRelElementVariationAudienceEntryRelId the primary key of the layout page template structure rel element variation audience entry rel
	 * @return the layout page template structure rel element variation audience entry rel, or <code>null</code> if a layout page template structure rel element variation audience entry rel with the primary key could not be found
	 */
	@Override
	public LayoutPageTemplateStructureRelElementVariationAudienceEntryRel
		fetchByPrimaryKey(
			long
				layoutPageTemplateStructureRelElementVariationAudienceEntryRelId) {

		return fetchByPrimaryKey(
			(Serializable)
				layoutPageTemplateStructureRelElementVariationAudienceEntryRelId);
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
		return "lptsrevAudienceEntryRelId";
	}

	@Override
	protected String getPKFieldName() {
		return "layoutPageTemplateStructureRelElementVariationAudienceEntryRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATIONAUDIENCEENTRYREL;
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
		return LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "LPTSREVAudienceEntryRel";
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
		ctMergeColumnNames.add("lptsRelElementVariationERC");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("lptsrevAudienceEntryRelId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the layout page template structure rel element variation audience entry rel persistence.
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
			_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATIONAUDIENCEENTRYREL_WHERE,
			_SQL_COUNT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATIONAUDIENCEENTRYREL_WHERE,
			LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"layoutPageTemplateStructureRelElementVariationAudienceEntryRel.",
				"uuid", "uuid_", FinderColumn.Type.STRING, "=", true, true,
				LayoutPageTemplateStructureRelElementVariationAudienceEntryRel::
					getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(
					LayoutPageTemplateStructureRelElementVariationAudienceEntryRel::
						getUuid),
				LayoutPageTemplateStructureRelElementVariationAudienceEntryRel::
					getGroupId),
			_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATIONAUDIENCEENTRYREL_WHERE,
			"",
			new FinderColumn<>(
				"layoutPageTemplateStructureRelElementVariationAudienceEntryRel.",
				"uuid", "uuid_", FinderColumn.Type.STRING, "=", true, true,
				LayoutPageTemplateStructureRelElementVariationAudienceEntryRel::
					getUuid),
			new FinderColumn<>(
				"layoutPageTemplateStructureRelElementVariationAudienceEntryRel.",
				"groupId", FinderColumn.Type.LONG, "=", true, true,
				LayoutPageTemplateStructureRelElementVariationAudienceEntryRel::
					getGroupId));

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
				_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATIONAUDIENCEENTRYREL_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATIONAUDIENCEENTRYREL_WHERE,
				LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"layoutPageTemplateStructureRelElementVariationAudienceEntryRel.",
					"uuid", "uuid_", FinderColumn.Type.STRING, "=", true, true,
					LayoutPageTemplateStructureRelElementVariationAudienceEntryRel::
						getUuid),
				new FinderColumn<>(
					"layoutPageTemplateStructureRelElementVariationAudienceEntryRel.",
					"companyId", FinderColumn.Type.LONG, "=", true, true,
					LayoutPageTemplateStructureRelElementVariationAudienceEntryRel::
						getCompanyId));

		_collectionPersistenceFinderByLayoutPageTemplateStructureRelElementVariationERC =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByLayoutPageTemplateStructureRelElementVariationERC",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"lptsRelElementVariationERC"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByLayoutPageTemplateStructureRelElementVariationERC",
					new String[] {String.class.getName()},
					new String[] {"lptsRelElementVariationERC"}, 0, 1, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByLayoutPageTemplateStructureRelElementVariationERC",
					new String[] {String.class.getName()},
					new String[] {"lptsRelElementVariationERC"}, 0, 1, false,
					null),
				_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATIONAUDIENCEENTRYREL_WHERE,
				_SQL_COUNT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATIONAUDIENCEENTRYREL_WHERE,
				LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"layoutPageTemplateStructureRelElementVariationAudienceEntryRel.",
					"layoutPageTemplateStructureRelElementVariationERC",
					"lptsRelElementVariationERC", FinderColumn.Type.STRING, "=",
					true, true,
					LayoutPageTemplateStructureRelElementVariationAudienceEntryRel::
						getLayoutPageTemplateStructureRelElementVariationERC));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(
					LayoutPageTemplateStructureRelElementVariationAudienceEntryRel::
						getExternalReferenceCode),
				LayoutPageTemplateStructureRelElementVariationAudienceEntryRel::
					getGroupId),
			_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATIONAUDIENCEENTRYREL_WHERE,
			"",
			new FinderColumn<>(
				"layoutPageTemplateStructureRelElementVariationAudienceEntryRel.",
				"externalReferenceCode", FinderColumn.Type.STRING, "=", true,
				true,
				LayoutPageTemplateStructureRelElementVariationAudienceEntryRel::
					getExternalReferenceCode),
			new FinderColumn<>(
				"layoutPageTemplateStructureRelElementVariationAudienceEntryRel.",
				"groupId", FinderColumn.Type.LONG, "=", true, true,
				LayoutPageTemplateStructureRelElementVariationAudienceEntryRel::
					getGroupId));

		LayoutPageTemplateStructureRelElementVariationAudienceEntryRelUtil.
			setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRelUtil.
			setPersistence(null);

		entityCache.removeCache(
			LayoutPageTemplateStructureRelElementVariationAudienceEntryRelImpl.
				class.getName());
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
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRelModelImpl.
			ENTITY_ALIAS + ".";

	private static final String
		_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATIONAUDIENCEENTRYREL =
			"SELECT layoutPageTemplateStructureRelElementVariationAudienceEntryRel FROM LayoutPageTemplateStructureRelElementVariationAudienceEntryRel layoutPageTemplateStructureRelElementVariationAudienceEntryRel";

	private static final String
		_SQL_SELECT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATIONAUDIENCEENTRYREL_WHERE =
			"SELECT layoutPageTemplateStructureRelElementVariationAudienceEntryRel FROM LayoutPageTemplateStructureRelElementVariationAudienceEntryRel layoutPageTemplateStructureRelElementVariationAudienceEntryRel WHERE ";

	private static final String
		_SQL_COUNT_LAYOUTPAGETEMPLATESTRUCTURERELELEMENTVARIATIONAUDIENCEENTRYREL_WHERE =
			"SELECT COUNT(layoutPageTemplateStructureRelElementVariationAudienceEntryRel) FROM LayoutPageTemplateStructureRelElementVariationAudienceEntryRel layoutPageTemplateStructureRelElementVariationAudienceEntryRel WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No LayoutPageTemplateStructureRelElementVariationAudienceEntryRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutPageTemplateStructureRelElementVariationAudienceEntryRelPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {
			"uuid",
			"layoutPageTemplateStructureRelElementVariationAudienceEntryRelId",
			"layoutPageTemplateStructureRelElementVariationERC"
		});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1356081103