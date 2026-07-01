/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.persistence.impl;

import com.liferay.oauth.client.persistence.exception.DuplicateOAuthClientASLocalMetadataExternalReferenceCodeException;
import com.liferay.oauth.client.persistence.exception.NoSuchOAuthClientASLocalMetadataException;
import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadataTable;
import com.liferay.oauth.client.persistence.model.impl.OAuthClientASLocalMetadataImpl;
import com.liferay.oauth.client.persistence.model.impl.OAuthClientASLocalMetadataModelImpl;
import com.liferay.oauth.client.persistence.service.persistence.OAuthClientASLocalMetadataPersistence;
import com.liferay.oauth.client.persistence.service.persistence.OAuthClientASLocalMetadataUtil;
import com.liferay.oauth.client.persistence.service.persistence.impl.constants.OAuthClientPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
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
 * The persistence implementation for the o auth client as local metadata service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = OAuthClientASLocalMetadataPersistence.class)
public class OAuthClientASLocalMetadataPersistenceImpl
	extends BasePersistenceImpl
		<OAuthClientASLocalMetadata, NoSuchOAuthClientASLocalMetadataException>
	implements OAuthClientASLocalMetadataPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>OAuthClientASLocalMetadataUtil</code> to access the o auth client as local metadata persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		OAuthClientASLocalMetadataImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<OAuthClientASLocalMetadata, NoSuchOAuthClientASLocalMetadataException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the o auth client as local metadatas where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth client as local metadatas
	 */
	@Override
	public List<OAuthClientASLocalMetadata> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first o auth client as local metadata in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata findByUuid_First(
			String uuid,
			OrderByComparator<OAuthClientASLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientASLocalMetadataException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first o auth client as local metadata in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata fetchByUuid_First(
		String uuid,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the o auth client as local metadatas that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client as local metadatas that the user has permission to view
	 */
	@Override
	public List<OAuthClientASLocalMetadata> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		return _collectionPersistenceFinderByUuid.filterFind(
			finderCache, new Object[] {uuid}, start, end, orderByComparator);
	}

	/**
	 * Removes all the o auth client as local metadatas where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of o auth client as local metadatas where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching o auth client as local metadatas
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of o auth client as local metadatas that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching o auth client as local metadatas that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.filterCount(
			finderCache, new Object[] {uuid});
	}

	private FilterCollectionPersistenceFinder
		<OAuthClientASLocalMetadata, NoSuchOAuthClientASLocalMetadataException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the o auth client as local metadatas where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth client as local metadatas
	 */
	@Override
	public List<OAuthClientASLocalMetadata> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first o auth client as local metadata in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<OAuthClientASLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientASLocalMetadataException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first o auth client as local metadata in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the o auth client as local metadatas that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client as local metadatas that the user has permission to view
	 */
	@Override
	public List<OAuthClientASLocalMetadata> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.filterFind(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the o auth client as local metadatas where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of o auth client as local metadatas where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching o auth client as local metadatas
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of o auth client as local metadatas that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching o auth client as local metadatas that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.filterCount(
			finderCache, new Object[] {uuid, companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<OAuthClientASLocalMetadata, NoSuchOAuthClientASLocalMetadataException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the o auth client as local metadatas where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth client as local metadatas
	 */
	@Override
	public List<OAuthClientASLocalMetadata> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first o auth client as local metadata in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata findByCompanyId_First(
			long companyId,
			OrderByComparator<OAuthClientASLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientASLocalMetadataException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first o auth client as local metadata in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata fetchByCompanyId_First(
		long companyId,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the o auth client as local metadatas that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client as local metadatas that the user has permission to view
	 */
	@Override
	public List<OAuthClientASLocalMetadata> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.filterFind(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the o auth client as local metadatas where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of o auth client as local metadatas where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching o auth client as local metadatas
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of o auth client as local metadatas that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching o auth client as local metadatas that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.filterCount(
			finderCache, new Object[] {companyId}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<OAuthClientASLocalMetadata, NoSuchOAuthClientASLocalMetadataException>
			_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the o auth client as local metadatas where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth client as local metadatas
	 */
	@Override
	public List<OAuthClientASLocalMetadata> findByUserId(
		long userId, int start, int end,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			finderCache, new Object[] {userId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first o auth client as local metadata in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata findByUserId_First(
			long userId,
			OrderByComparator<OAuthClientASLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientASLocalMetadataException {

		return _collectionPersistenceFinderByUserId.findFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Returns the first o auth client as local metadata in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata fetchByUserId_First(
		long userId,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the o auth client as local metadatas that the user has permissions to view where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client as local metadatas that the user has permission to view
	 */
	@Override
	public List<OAuthClientASLocalMetadata> filterFindByUserId(
		long userId, int start, int end,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		return _collectionPersistenceFinderByUserId.filterFind(
			finderCache, new Object[] {userId}, start, end, orderByComparator);
	}

	/**
	 * Removes all the o auth client as local metadatas where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			finderCache, new Object[] {userId});
	}

	/**
	 * Returns the number of o auth client as local metadatas where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching o auth client as local metadatas
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			finderCache, new Object[] {userId});
	}

	/**
	 * Returns the number of o auth client as local metadatas that the user has permission to view where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching o auth client as local metadatas that the user has permission to view
	 */
	@Override
	public int filterCountByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.filterCount(
			finderCache, new Object[] {userId});
	}

	private UniquePersistenceFinder
		<OAuthClientASLocalMetadata, NoSuchOAuthClientASLocalMetadataException>
			_uniquePersistenceFinderByC_I;

	/**
	 * Returns the o auth client as local metadata where companyId = &#63; and issuer = &#63; or throws a <code>NoSuchOAuthClientASLocalMetadataException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @return the matching o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata findByC_I(long companyId, String issuer)
		throws NoSuchOAuthClientASLocalMetadataException {

		return _uniquePersistenceFinderByC_I.find(
			finderCache, new Object[] {companyId, issuer});
	}

	/**
	 * Returns the o auth client as local metadata where companyId = &#63; and issuer = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata fetchByC_I(
		long companyId, String issuer, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_I.fetch(
			finderCache, new Object[] {companyId, issuer}, useFinderCache);
	}

	/**
	 * Removes the o auth client as local metadata where companyId = &#63; and issuer = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @return the o auth client as local metadata that was removed
	 */
	@Override
	public OAuthClientASLocalMetadata removeByC_I(long companyId, String issuer)
		throws NoSuchOAuthClientASLocalMetadataException {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata = findByC_I(
			companyId, issuer);

		return remove(oAuthClientASLocalMetadata);
	}

	/**
	 * Returns the number of o auth client as local metadatas where companyId = &#63; and issuer = &#63;.
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @return the number of matching o auth client as local metadatas
	 */
	@Override
	public int countByC_I(long companyId, String issuer) {
		return _uniquePersistenceFinderByC_I.count(
			finderCache, new Object[] {companyId, issuer});
	}

	private FilterCollectionPersistenceFinder
		<OAuthClientASLocalMetadata, NoSuchOAuthClientASLocalMetadataException>
			_collectionPersistenceFinderByC_L;

	/**
	 * Returns an ordered range of all the o auth client as local metadatas where companyId = &#63; and localWellKnownEnabled = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param localWellKnownEnabled the local well known enabled
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth client as local metadatas
	 */
	@Override
	public List<OAuthClientASLocalMetadata> findByC_L(
		long companyId, boolean localWellKnownEnabled, int start, int end,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_L.find(
			finderCache, new Object[] {companyId, localWellKnownEnabled}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first o auth client as local metadata in the ordered set where companyId = &#63; and localWellKnownEnabled = &#63;.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownEnabled the local well known enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata findByC_L_First(
			long companyId, boolean localWellKnownEnabled,
			OrderByComparator<OAuthClientASLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientASLocalMetadataException {

		return _collectionPersistenceFinderByC_L.findFirst(
			finderCache, new Object[] {companyId, localWellKnownEnabled},
			orderByComparator);
	}

	/**
	 * Returns the first o auth client as local metadata in the ordered set where companyId = &#63; and localWellKnownEnabled = &#63;.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownEnabled the local well known enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata fetchByC_L_First(
		long companyId, boolean localWellKnownEnabled,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		return _collectionPersistenceFinderByC_L.fetchFirst(
			finderCache, new Object[] {companyId, localWellKnownEnabled},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the o auth client as local metadatas that the user has permissions to view where companyId = &#63; and localWellKnownEnabled = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param localWellKnownEnabled the local well known enabled
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client as local metadatas that the user has permission to view
	 */
	@Override
	public List<OAuthClientASLocalMetadata> filterFindByC_L(
		long companyId, boolean localWellKnownEnabled, int start, int end,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		return _collectionPersistenceFinderByC_L.filterFind(
			finderCache, new Object[] {companyId, localWellKnownEnabled}, start,
			end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the o auth client as local metadatas where companyId = &#63; and localWellKnownEnabled = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownEnabled the local well known enabled
	 */
	@Override
	public void removeByC_L(long companyId, boolean localWellKnownEnabled) {
		_collectionPersistenceFinderByC_L.remove(
			finderCache, new Object[] {companyId, localWellKnownEnabled});
	}

	/**
	 * Returns the number of o auth client as local metadatas where companyId = &#63; and localWellKnownEnabled = &#63;.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownEnabled the local well known enabled
	 * @return the number of matching o auth client as local metadatas
	 */
	@Override
	public int countByC_L(long companyId, boolean localWellKnownEnabled) {
		return _collectionPersistenceFinderByC_L.count(
			finderCache, new Object[] {companyId, localWellKnownEnabled});
	}

	/**
	 * Returns the number of o auth client as local metadatas that the user has permission to view where companyId = &#63; and localWellKnownEnabled = &#63;.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownEnabled the local well known enabled
	 * @return the number of matching o auth client as local metadatas that the user has permission to view
	 */
	@Override
	public int filterCountByC_L(long companyId, boolean localWellKnownEnabled) {
		return _collectionPersistenceFinderByC_L.filterCount(
			finderCache, new Object[] {companyId, localWellKnownEnabled},
			companyId, 0);
	}

	private UniquePersistenceFinder
		<OAuthClientASLocalMetadata, NoSuchOAuthClientASLocalMetadataException>
			_uniquePersistenceFinderByC_LWKURI;

	/**
	 * Returns the o auth client as local metadata where companyId = &#63; and localWellKnownURI = &#63; or throws a <code>NoSuchOAuthClientASLocalMetadataException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownURI the local well known uri
	 * @return the matching o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata findByC_LWKURI(
			long companyId, String localWellKnownURI)
		throws NoSuchOAuthClientASLocalMetadataException {

		return _uniquePersistenceFinderByC_LWKURI.find(
			finderCache, new Object[] {companyId, localWellKnownURI});
	}

	/**
	 * Returns the o auth client as local metadata where companyId = &#63; and localWellKnownURI = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownURI the local well known uri
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata fetchByC_LWKURI(
		long companyId, String localWellKnownURI, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_LWKURI.fetch(
			finderCache, new Object[] {companyId, localWellKnownURI},
			useFinderCache);
	}

	/**
	 * Removes the o auth client as local metadata where companyId = &#63; and localWellKnownURI = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownURI the local well known uri
	 * @return the o auth client as local metadata that was removed
	 */
	@Override
	public OAuthClientASLocalMetadata removeByC_LWKURI(
			long companyId, String localWellKnownURI)
		throws NoSuchOAuthClientASLocalMetadataException {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata = findByC_LWKURI(
			companyId, localWellKnownURI);

		return remove(oAuthClientASLocalMetadata);
	}

	/**
	 * Returns the number of o auth client as local metadatas where companyId = &#63; and localWellKnownURI = &#63;.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownURI the local well known uri
	 * @return the number of matching o auth client as local metadatas
	 */
	@Override
	public int countByC_LWKURI(long companyId, String localWellKnownURI) {
		return _uniquePersistenceFinderByC_LWKURI.count(
			finderCache, new Object[] {companyId, localWellKnownURI});
	}

	private UniquePersistenceFinder
		<OAuthClientASLocalMetadata, NoSuchOAuthClientASLocalMetadataException>
			_uniquePersistenceFinderByC_O;

	/**
	 * Returns the o auth client as local metadata where companyId = &#63; and oAuthASLocalWellKnownURI = &#63; or throws a <code>NoSuchOAuthClientASLocalMetadataException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param oAuthASLocalWellKnownURI the o auth as local well known uri
	 * @return the matching o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata findByC_O(
			long companyId, String oAuthASLocalWellKnownURI)
		throws NoSuchOAuthClientASLocalMetadataException {

		return _uniquePersistenceFinderByC_O.find(
			finderCache, new Object[] {companyId, oAuthASLocalWellKnownURI});
	}

	/**
	 * Returns the o auth client as local metadata where companyId = &#63; and oAuthASLocalWellKnownURI = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param oAuthASLocalWellKnownURI the o auth as local well known uri
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata fetchByC_O(
		long companyId, String oAuthASLocalWellKnownURI,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_O.fetch(
			finderCache, new Object[] {companyId, oAuthASLocalWellKnownURI},
			useFinderCache);
	}

	/**
	 * Removes the o auth client as local metadata where companyId = &#63; and oAuthASLocalWellKnownURI = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param oAuthASLocalWellKnownURI the o auth as local well known uri
	 * @return the o auth client as local metadata that was removed
	 */
	@Override
	public OAuthClientASLocalMetadata removeByC_O(
			long companyId, String oAuthASLocalWellKnownURI)
		throws NoSuchOAuthClientASLocalMetadataException {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata = findByC_O(
			companyId, oAuthASLocalWellKnownURI);

		return remove(oAuthClientASLocalMetadata);
	}

	/**
	 * Returns the number of o auth client as local metadatas where companyId = &#63; and oAuthASLocalWellKnownURI = &#63;.
	 *
	 * @param companyId the company ID
	 * @param oAuthASLocalWellKnownURI the o auth as local well known uri
	 * @return the number of matching o auth client as local metadatas
	 */
	@Override
	public int countByC_O(long companyId, String oAuthASLocalWellKnownURI) {
		return _uniquePersistenceFinderByC_O.count(
			finderCache, new Object[] {companyId, oAuthASLocalWellKnownURI});
	}

	private UniquePersistenceFinder
		<OAuthClientASLocalMetadata, NoSuchOAuthClientASLocalMetadataException>
			_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the o auth client as local metadata where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchOAuthClientASLocalMetadataException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchOAuthClientASLocalMetadataException {

		return _uniquePersistenceFinderByERC_C.find(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the o auth client as local metadata where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the o auth client as local metadata where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the o auth client as local metadata that was removed
	 */
	@Override
	public OAuthClientASLocalMetadata removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchOAuthClientASLocalMetadataException {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata = findByERC_C(
			externalReferenceCode, companyId);

		return remove(oAuthClientASLocalMetadata);
	}

	/**
	 * Returns the number of o auth client as local metadatas where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching o auth client as local metadatas
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public OAuthClientASLocalMetadataPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(OAuthClientASLocalMetadata.class);

		setModelImplClass(OAuthClientASLocalMetadataImpl.class);
		setModelPKClass(long.class);

		setTable(OAuthClientASLocalMetadataTable.INSTANCE);
	}

	/**
	 * Creates a new o auth client as local metadata with the primary key. Does not add the o auth client as local metadata to the database.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key for the new o auth client as local metadata
	 * @return the new o auth client as local metadata
	 */
	@Override
	public OAuthClientASLocalMetadata create(
		long oAuthClientASLocalMetadataId) {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			new OAuthClientASLocalMetadataImpl();

		oAuthClientASLocalMetadata.setNew(true);
		oAuthClientASLocalMetadata.setPrimaryKey(oAuthClientASLocalMetadataId);

		String uuid = PortalUUIDUtil.generate();

		oAuthClientASLocalMetadata.setUuid(uuid);

		oAuthClientASLocalMetadata.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return oAuthClientASLocalMetadata;
	}

	/**
	 * Removes the o auth client as local metadata with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the o auth client as local metadata
	 * @return the o auth client as local metadata that was removed
	 * @throws NoSuchOAuthClientASLocalMetadataException if a o auth client as local metadata with the primary key could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata remove(long oAuthClientASLocalMetadataId)
		throws NoSuchOAuthClientASLocalMetadataException {

		return remove((Serializable)oAuthClientASLocalMetadataId);
	}

	@Override
	protected OAuthClientASLocalMetadata removeImpl(
		OAuthClientASLocalMetadata oAuthClientASLocalMetadata) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(oAuthClientASLocalMetadata)) {
				oAuthClientASLocalMetadata =
					(OAuthClientASLocalMetadata)session.get(
						OAuthClientASLocalMetadataImpl.class,
						oAuthClientASLocalMetadata.getPrimaryKeyObj());
			}

			if (oAuthClientASLocalMetadata != null) {
				session.delete(oAuthClientASLocalMetadata);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (oAuthClientASLocalMetadata != null) {
			clearCache(oAuthClientASLocalMetadata);
		}

		return oAuthClientASLocalMetadata;
	}

	@Override
	public OAuthClientASLocalMetadata updateImpl(
		OAuthClientASLocalMetadata oAuthClientASLocalMetadata) {

		boolean isNew = oAuthClientASLocalMetadata.isNew();

		if (!(oAuthClientASLocalMetadata instanceof
				OAuthClientASLocalMetadataModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(oAuthClientASLocalMetadata.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					oAuthClientASLocalMetadata);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in oAuthClientASLocalMetadata proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom OAuthClientASLocalMetadata implementation " +
					oAuthClientASLocalMetadata.getClass());
		}

		OAuthClientASLocalMetadataModelImpl
			oAuthClientASLocalMetadataModelImpl =
				(OAuthClientASLocalMetadataModelImpl)oAuthClientASLocalMetadata;

		if (Validator.isNull(oAuthClientASLocalMetadata.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			oAuthClientASLocalMetadata.setUuid(uuid);
		}

		if (Validator.isNull(
				oAuthClientASLocalMetadata.getExternalReferenceCode())) {

			oAuthClientASLocalMetadata.setExternalReferenceCode(
				oAuthClientASLocalMetadata.getUuid());
		}
		else {
			if (!Objects.equals(
					oAuthClientASLocalMetadataModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					oAuthClientASLocalMetadata.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = oAuthClientASLocalMetadata.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = oAuthClientASLocalMetadata.getPrimaryKey();
					}

					try {
						oAuthClientASLocalMetadata.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								OAuthClientASLocalMetadata.class.getName(),
								classPK, ContentTypes.TEXT_HTML,
								Sanitizer.MODE_ALL,
								oAuthClientASLocalMetadata.
									getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			OAuthClientASLocalMetadata ercOAuthClientASLocalMetadata =
				fetchByERC_C(
					oAuthClientASLocalMetadata.getExternalReferenceCode(),
					oAuthClientASLocalMetadata.getCompanyId());

			if (isNew) {
				if (ercOAuthClientASLocalMetadata != null) {
					throw new DuplicateOAuthClientASLocalMetadataExternalReferenceCodeException(
						"Duplicate o auth client as local metadata with external reference code " +
							oAuthClientASLocalMetadata.
								getExternalReferenceCode() + " and company " +
									oAuthClientASLocalMetadata.getCompanyId());
				}
			}
			else {
				if ((ercOAuthClientASLocalMetadata != null) &&
					(oAuthClientASLocalMetadata.
						getOAuthClientASLocalMetadataId() !=
							ercOAuthClientASLocalMetadata.
								getOAuthClientASLocalMetadataId())) {

					throw new DuplicateOAuthClientASLocalMetadataExternalReferenceCodeException(
						"Duplicate o auth client as local metadata with external reference code " +
							oAuthClientASLocalMetadata.
								getExternalReferenceCode() + " and company " +
									oAuthClientASLocalMetadata.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (oAuthClientASLocalMetadata.getCreateDate() == null)) {
			if (serviceContext == null) {
				oAuthClientASLocalMetadata.setCreateDate(date);
			}
			else {
				oAuthClientASLocalMetadata.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!oAuthClientASLocalMetadataModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				oAuthClientASLocalMetadata.setModifiedDate(date);
			}
			else {
				oAuthClientASLocalMetadata.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(oAuthClientASLocalMetadata);
			}
			else {
				oAuthClientASLocalMetadata =
					(OAuthClientASLocalMetadata)session.merge(
						oAuthClientASLocalMetadata);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(oAuthClientASLocalMetadata, false);

		if (isNew) {
			oAuthClientASLocalMetadata.setNew(false);
		}

		oAuthClientASLocalMetadata.resetOriginalValues();

		return oAuthClientASLocalMetadata;
	}

	/**
	 * Returns the o auth client as local metadata with the primary key or throws a <code>NoSuchOAuthClientASLocalMetadataException</code> if it could not be found.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the o auth client as local metadata
	 * @return the o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a o auth client as local metadata with the primary key could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata findByPrimaryKey(
			long oAuthClientASLocalMetadataId)
		throws NoSuchOAuthClientASLocalMetadataException {

		return findByPrimaryKey((Serializable)oAuthClientASLocalMetadataId);
	}

	/**
	 * Returns the o auth client as local metadata with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the o auth client as local metadata
	 * @return the o auth client as local metadata, or <code>null</code> if a o auth client as local metadata with the primary key could not be found
	 */
	@Override
	public OAuthClientASLocalMetadata fetchByPrimaryKey(
		long oAuthClientASLocalMetadataId) {

		return fetchByPrimaryKey((Serializable)oAuthClientASLocalMetadataId);
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
		return "oAuthClientASLocalMetadataId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OAUTHCLIENTASLOCALMETADATA;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return OAuthClientASLocalMetadataModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the o auth client as local metadata persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByUuid =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
					new String[] {String.class.getName()},
					new String[] {"uuid_"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
					new String[] {String.class.getName()},
					new String[] {"uuid_"}, 0, 1, false, null),
				_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_WHERE,
				_SQL_COUNT_OAUTHCLIENTASLOCALMETADATA_WHERE,
				OAuthClientASLocalMetadataModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"oAuthClientASLocalMetadata.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					OAuthClientASLocalMetadata::getUuid));

		_collectionPersistenceFinderByUuid_C =
			new FilterCollectionPersistenceFinder<>(
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
				_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_WHERE,
				_SQL_COUNT_OAUTHCLIENTASLOCALMETADATA_WHERE,
				OAuthClientASLocalMetadataModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"oAuthClientASLocalMetadata.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					OAuthClientASLocalMetadata::getUuid),
				new FinderColumn<>(
					"oAuthClientASLocalMetadata.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					OAuthClientASLocalMetadata::getCompanyId));

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
				_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_WHERE,
				_SQL_COUNT_OAUTHCLIENTASLOCALMETADATA_WHERE,
				OAuthClientASLocalMetadataModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"oAuthClientASLocalMetadata.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					OAuthClientASLocalMetadata::getCompanyId));

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
				_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_WHERE,
				_SQL_COUNT_OAUTHCLIENTASLOCALMETADATA_WHERE,
				OAuthClientASLocalMetadataModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"oAuthClientASLocalMetadata.", "userId",
					FinderColumn.Type.LONG, "=", true, true,
					OAuthClientASLocalMetadata::getUserId));

		_uniquePersistenceFinderByC_I = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_I",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "issuer"}, 0, 2, false,
				OAuthClientASLocalMetadata::getCompanyId,
				convertNullFunction(OAuthClientASLocalMetadata::getIssuer)),
			_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_WHERE, "",
			new FinderColumn<>(
				"oAuthClientASLocalMetadata.", "companyId",
				FinderColumn.Type.LONG, "=", true, true,
				OAuthClientASLocalMetadata::getCompanyId),
			new FinderColumn<>(
				"oAuthClientASLocalMetadata.", "issuer",
				FinderColumn.Type.STRING, "=", true, true,
				OAuthClientASLocalMetadata::getIssuer));

		_collectionPersistenceFinderByC_L =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_L",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "localWellKnownEnabled"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_L",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"companyId", "localWellKnownEnabled"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_L",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"companyId", "localWellKnownEnabled"}, false),
				_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_WHERE,
				_SQL_COUNT_OAUTHCLIENTASLOCALMETADATA_WHERE,
				OAuthClientASLocalMetadataModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"oAuthClientASLocalMetadata.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					OAuthClientASLocalMetadata::getCompanyId),
				new FinderColumn<>(
					"oAuthClientASLocalMetadata.", "localWellKnownEnabled",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					OAuthClientASLocalMetadata::isLocalWellKnownEnabled));

		_uniquePersistenceFinderByC_LWKURI = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_LWKURI",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "localWellKnownURI"}, 0, 2, false,
				OAuthClientASLocalMetadata::getCompanyId,
				convertNullFunction(
					OAuthClientASLocalMetadata::getLocalWellKnownURI)),
			_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_WHERE, "",
			new FinderColumn<>(
				"oAuthClientASLocalMetadata.", "companyId",
				FinderColumn.Type.LONG, "=", true, true,
				OAuthClientASLocalMetadata::getCompanyId),
			new FinderColumn<>(
				"oAuthClientASLocalMetadata.", "localWellKnownURI",
				FinderColumn.Type.STRING, "=", true, true,
				OAuthClientASLocalMetadata::getLocalWellKnownURI));

		_uniquePersistenceFinderByC_O = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_O",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "oAuthASLocalWellKnownURI"}, 0, 2,
				false, OAuthClientASLocalMetadata::getCompanyId,
				convertNullFunction(
					OAuthClientASLocalMetadata::getOAuthASLocalWellKnownURI)),
			_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_WHERE, "",
			new FinderColumn<>(
				"oAuthClientASLocalMetadata.", "companyId",
				FinderColumn.Type.LONG, "=", true, true,
				OAuthClientASLocalMetadata::getCompanyId),
			new FinderColumn<>(
				"oAuthClientASLocalMetadata.", "oAuthASLocalWellKnownURI",
				FinderColumn.Type.STRING, "=", true, true,
				OAuthClientASLocalMetadata::getOAuthASLocalWellKnownURI));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false,
				convertNullFunction(
					OAuthClientASLocalMetadata::getExternalReferenceCode),
				OAuthClientASLocalMetadata::getCompanyId),
			_SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_WHERE, "",
			new FinderColumn<>(
				"oAuthClientASLocalMetadata.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, true,
				OAuthClientASLocalMetadata::getExternalReferenceCode),
			new FinderColumn<>(
				"oAuthClientASLocalMetadata.", "companyId",
				FinderColumn.Type.LONG, "=", true, true,
				OAuthClientASLocalMetadata::getCompanyId));

		OAuthClientASLocalMetadataUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		OAuthClientASLocalMetadataUtil.setPersistence(null);

		entityCache.removeCache(OAuthClientASLocalMetadataImpl.class.getName());
	}

	@Override
	@Reference(
		target = OAuthClientPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = OAuthClientPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = OAuthClientPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		OAuthClientASLocalMetadataModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OAUTHCLIENTASLOCALMETADATA =
		"SELECT oAuthClientASLocalMetadata FROM OAuthClientASLocalMetadata oAuthClientASLocalMetadata";

	private static final String _SQL_SELECT_OAUTHCLIENTASLOCALMETADATA_WHERE =
		"SELECT oAuthClientASLocalMetadata FROM OAuthClientASLocalMetadata oAuthClientASLocalMetadata WHERE ";

	private static final String _SQL_COUNT_OAUTHCLIENTASLOCALMETADATA_WHERE =
		"SELECT COUNT(oAuthClientASLocalMetadata) FROM OAuthClientASLocalMetadata oAuthClientASLocalMetadata WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-69283284