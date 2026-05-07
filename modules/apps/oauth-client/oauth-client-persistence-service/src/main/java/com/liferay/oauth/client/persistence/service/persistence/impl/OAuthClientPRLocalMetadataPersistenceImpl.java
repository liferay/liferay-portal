/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.persistence.impl;

import com.liferay.oauth.client.persistence.exception.DuplicateOAuthClientPRLocalMetadataExternalReferenceCodeException;
import com.liferay.oauth.client.persistence.exception.NoSuchOAuthClientPRLocalMetadataException;
import com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata;
import com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadataTable;
import com.liferay.oauth.client.persistence.model.impl.OAuthClientPRLocalMetadataImpl;
import com.liferay.oauth.client.persistence.model.impl.OAuthClientPRLocalMetadataModelImpl;
import com.liferay.oauth.client.persistence.service.persistence.OAuthClientPRLocalMetadataPersistence;
import com.liferay.oauth.client.persistence.service.persistence.OAuthClientPRLocalMetadataUtil;
import com.liferay.oauth.client.persistence.service.persistence.impl.constants.OAuthClientPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
 * The persistence implementation for the o auth client pr local metadata service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = OAuthClientPRLocalMetadataPersistence.class)
public class OAuthClientPRLocalMetadataPersistenceImpl
	extends BasePersistenceImpl
		<OAuthClientPRLocalMetadata, NoSuchOAuthClientPRLocalMetadataException>
	implements OAuthClientPRLocalMetadataPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>OAuthClientPRLocalMetadataUtil</code> to access the o auth client pr local metadata persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		OAuthClientPRLocalMetadataImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindByUuid;
	private FinderPath _finderPathWithoutPaginationFindByUuid;
	private FinderPath _finderPathCountByUuid;
	private CollectionPersistenceFinder<OAuthClientPRLocalMetadata>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns all the o auth client pr local metadatas where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching o auth client pr local metadatas
	 */
	@Override
	public List<OAuthClientPRLocalMetadata> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the o auth client pr local metadatas where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @return the range of matching o auth client pr local metadatas
	 */
	@Override
	public List<OAuthClientPRLocalMetadata> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the o auth client pr local metadatas where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client pr local metadatas
	 */
	@Override
	public List<OAuthClientPRLocalMetadata> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the o auth client pr local metadatas where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth client pr local metadatas
	 */
	@Override
	public List<OAuthClientPRLocalMetadata> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a matching o auth client pr local metadata could not be found
	 */
	@Override
	public OAuthClientPRLocalMetadata findByUuid_First(
			String uuid,
			OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientPRLocalMetadataException {

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			fetchByUuid_First(uuid, orderByComparator);

		if (oAuthClientPRLocalMetadata != null) {
			return oAuthClientPRLocalMetadata;
		}

		throw new NoSuchOAuthClientPRLocalMetadataException(
			_collectionPersistenceFinderByUuid.buildNoSuchKeyMessage(
				_NO_SUCH_ENTITY_WITH_KEY, new Object[] {uuid}));
	}

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	@Override
	public OAuthClientPRLocalMetadata fetchByUuid_First(
		String uuid,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the o auth client pr local metadatas where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of o auth client pr local metadatas where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching o auth client pr local metadatas
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;
	private CollectionPersistenceFinder<OAuthClientPRLocalMetadata>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns all the o auth client pr local metadatas where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching o auth client pr local metadatas
	 */
	@Override
	public List<OAuthClientPRLocalMetadata> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the o auth client pr local metadatas where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @return the range of matching o auth client pr local metadatas
	 */
	@Override
	public List<OAuthClientPRLocalMetadata> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the o auth client pr local metadatas where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client pr local metadatas
	 */
	@Override
	public List<OAuthClientPRLocalMetadata> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the o auth client pr local metadatas where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth client pr local metadatas
	 */
	@Override
	public List<OAuthClientPRLocalMetadata> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a matching o auth client pr local metadata could not be found
	 */
	@Override
	public OAuthClientPRLocalMetadata findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientPRLocalMetadataException {

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			fetchByUuid_C_First(uuid, companyId, orderByComparator);

		if (oAuthClientPRLocalMetadata != null) {
			return oAuthClientPRLocalMetadata;
		}

		throw new NoSuchOAuthClientPRLocalMetadataException(
			_collectionPersistenceFinderByUuid_C.buildNoSuchKeyMessage(
				_NO_SUCH_ENTITY_WITH_KEY, new Object[] {uuid, companyId}));
	}

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	@Override
	public OAuthClientPRLocalMetadata fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the o auth client pr local metadatas where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of o auth client pr local metadatas where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching o auth client pr local metadatas
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;
	private CollectionPersistenceFinder<OAuthClientPRLocalMetadata>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns all the o auth client pr local metadatas where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching o auth client pr local metadatas
	 */
	@Override
	public List<OAuthClientPRLocalMetadata> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the o auth client pr local metadatas where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @return the range of matching o auth client pr local metadatas
	 */
	@Override
	public List<OAuthClientPRLocalMetadata> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the o auth client pr local metadatas where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client pr local metadatas
	 */
	@Override
	public List<OAuthClientPRLocalMetadata> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the o auth client pr local metadatas where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth client pr local metadatas
	 */
	@Override
	public List<OAuthClientPRLocalMetadata> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a matching o auth client pr local metadata could not be found
	 */
	@Override
	public OAuthClientPRLocalMetadata findByCompanyId_First(
			long companyId,
			OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientPRLocalMetadataException {

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			fetchByCompanyId_First(companyId, orderByComparator);

		if (oAuthClientPRLocalMetadata != null) {
			return oAuthClientPRLocalMetadata;
		}

		throw new NoSuchOAuthClientPRLocalMetadataException(
			_collectionPersistenceFinderByCompanyId.buildNoSuchKeyMessage(
				_NO_SUCH_ENTITY_WITH_KEY, new Object[] {companyId}));
	}

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	@Override
	public OAuthClientPRLocalMetadata fetchByCompanyId_First(
		long companyId,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the o auth client pr local metadatas where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of o auth client pr local metadatas where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching o auth client pr local metadatas
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private FinderPath _finderPathWithPaginationFindByUserId;
	private FinderPath _finderPathWithoutPaginationFindByUserId;
	private FinderPath _finderPathCountByUserId;
	private CollectionPersistenceFinder<OAuthClientPRLocalMetadata>
		_collectionPersistenceFinderByUserId;

	/**
	 * Returns all the o auth client pr local metadatas where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching o auth client pr local metadatas
	 */
	@Override
	public List<OAuthClientPRLocalMetadata> findByUserId(long userId) {
		return findByUserId(userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the o auth client pr local metadatas where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @return the range of matching o auth client pr local metadatas
	 */
	@Override
	public List<OAuthClientPRLocalMetadata> findByUserId(
		long userId, int start, int end) {

		return findByUserId(userId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the o auth client pr local metadatas where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client pr local metadatas
	 */
	@Override
	public List<OAuthClientPRLocalMetadata> findByUserId(
		long userId, int start, int end,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator) {

		return findByUserId(userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the o auth client pr local metadatas where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth client pr local metadatas
	 */
	@Override
	public List<OAuthClientPRLocalMetadata> findByUserId(
		long userId, int start, int end,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			finderCache, new Object[] {userId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a matching o auth client pr local metadata could not be found
	 */
	@Override
	public OAuthClientPRLocalMetadata findByUserId_First(
			long userId,
			OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientPRLocalMetadataException {

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			fetchByUserId_First(userId, orderByComparator);

		if (oAuthClientPRLocalMetadata != null) {
			return oAuthClientPRLocalMetadata;
		}

		throw new NoSuchOAuthClientPRLocalMetadataException(
			_collectionPersistenceFinderByUserId.buildNoSuchKeyMessage(
				_NO_SUCH_ENTITY_WITH_KEY, new Object[] {userId}));
	}

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	@Override
	public OAuthClientPRLocalMetadata fetchByUserId_First(
		long userId,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Removes all the o auth client pr local metadatas where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			finderCache, new Object[] {userId});
	}

	/**
	 * Returns the number of o auth client pr local metadatas where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching o auth client pr local metadatas
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			finderCache, new Object[] {userId});
	}

	private FinderPath _finderPathWithPaginationFindByC_L;
	private FinderPath _finderPathWithoutPaginationFindByC_L;
	private FinderPath _finderPathCountByC_L;
	private CollectionPersistenceFinder<OAuthClientPRLocalMetadata>
		_collectionPersistenceFinderByC_L;

	/**
	 * Returns all the o auth client pr local metadatas where companyId = &#63; and localWellKnownEnabled = &#63;.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownEnabled the local well known enabled
	 * @return the matching o auth client pr local metadatas
	 */
	@Override
	public List<OAuthClientPRLocalMetadata> findByC_L(
		long companyId, boolean localWellKnownEnabled) {

		return findByC_L(
			companyId, localWellKnownEnabled, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the o auth client pr local metadatas where companyId = &#63; and localWellKnownEnabled = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param localWellKnownEnabled the local well known enabled
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @return the range of matching o auth client pr local metadatas
	 */
	@Override
	public List<OAuthClientPRLocalMetadata> findByC_L(
		long companyId, boolean localWellKnownEnabled, int start, int end) {

		return findByC_L(companyId, localWellKnownEnabled, start, end, null);
	}

	/**
	 * Returns an ordered range of all the o auth client pr local metadatas where companyId = &#63; and localWellKnownEnabled = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param localWellKnownEnabled the local well known enabled
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client pr local metadatas
	 */
	@Override
	public List<OAuthClientPRLocalMetadata> findByC_L(
		long companyId, boolean localWellKnownEnabled, int start, int end,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator) {

		return findByC_L(
			companyId, localWellKnownEnabled, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the o auth client pr local metadatas where companyId = &#63; and localWellKnownEnabled = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param localWellKnownEnabled the local well known enabled
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth client pr local metadatas
	 */
	@Override
	public List<OAuthClientPRLocalMetadata> findByC_L(
		long companyId, boolean localWellKnownEnabled, int start, int end,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_L.find(
			finderCache, new Object[] {companyId, localWellKnownEnabled}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where companyId = &#63; and localWellKnownEnabled = &#63;.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownEnabled the local well known enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a matching o auth client pr local metadata could not be found
	 */
	@Override
	public OAuthClientPRLocalMetadata findByC_L_First(
			long companyId, boolean localWellKnownEnabled,
			OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientPRLocalMetadataException {

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			fetchByC_L_First(
				companyId, localWellKnownEnabled, orderByComparator);

		if (oAuthClientPRLocalMetadata != null) {
			return oAuthClientPRLocalMetadata;
		}

		throw new NoSuchOAuthClientPRLocalMetadataException(
			_collectionPersistenceFinderByC_L.buildNoSuchKeyMessage(
				_NO_SUCH_ENTITY_WITH_KEY,
				new Object[] {companyId, localWellKnownEnabled}));
	}

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where companyId = &#63; and localWellKnownEnabled = &#63;.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownEnabled the local well known enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	@Override
	public OAuthClientPRLocalMetadata fetchByC_L_First(
		long companyId, boolean localWellKnownEnabled,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator) {

		return _collectionPersistenceFinderByC_L.fetchFirst(
			finderCache, new Object[] {companyId, localWellKnownEnabled},
			orderByComparator);
	}

	/**
	 * Removes all the o auth client pr local metadatas where companyId = &#63; and localWellKnownEnabled = &#63; from the database.
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
	 * Returns the number of o auth client pr local metadatas where companyId = &#63; and localWellKnownEnabled = &#63;.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownEnabled the local well known enabled
	 * @return the number of matching o auth client pr local metadatas
	 */
	@Override
	public int countByC_L(long companyId, boolean localWellKnownEnabled) {
		return _collectionPersistenceFinderByC_L.count(
			finderCache, new Object[] {companyId, localWellKnownEnabled});
	}

	private FinderPath _finderPathFetchByC_LWKURI;
	private UniquePersistenceFinder<OAuthClientPRLocalMetadata>
		_uniquePersistenceFinderByC_LWKURI;

	/**
	 * Returns the o auth client pr local metadata where companyId = &#63; and localWellKnownURI = &#63; or throws a <code>NoSuchOAuthClientPRLocalMetadataException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownURI the local well known uri
	 * @return the matching o auth client pr local metadata
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a matching o auth client pr local metadata could not be found
	 */
	@Override
	public OAuthClientPRLocalMetadata findByC_LWKURI(
			long companyId, String localWellKnownURI)
		throws NoSuchOAuthClientPRLocalMetadataException {

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata = fetchByC_LWKURI(
			companyId, localWellKnownURI);

		if (oAuthClientPRLocalMetadata == null) {
			String message =
				_uniquePersistenceFinderByC_LWKURI.buildNoSuchKeyMessage(
					_NO_SUCH_ENTITY_WITH_KEY,
					new Object[] {companyId, localWellKnownURI});

			if (_log.isDebugEnabled()) {
				_log.debug(message);
			}

			throw new NoSuchOAuthClientPRLocalMetadataException(message);
		}

		return oAuthClientPRLocalMetadata;
	}

	/**
	 * Returns the o auth client pr local metadata where companyId = &#63; and localWellKnownURI = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownURI the local well known uri
	 * @return the matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	@Override
	public OAuthClientPRLocalMetadata fetchByC_LWKURI(
		long companyId, String localWellKnownURI) {

		return fetchByC_LWKURI(companyId, localWellKnownURI, true);
	}

	/**
	 * Returns the o auth client pr local metadata where companyId = &#63; and localWellKnownURI = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownURI the local well known uri
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	@Override
	public OAuthClientPRLocalMetadata fetchByC_LWKURI(
		long companyId, String localWellKnownURI, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_LWKURI.fetch(
			finderCache, new Object[] {companyId, localWellKnownURI},
			useFinderCache);
	}

	/**
	 * Removes the o auth client pr local metadata where companyId = &#63; and localWellKnownURI = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownURI the local well known uri
	 * @return the o auth client pr local metadata that was removed
	 */
	@Override
	public OAuthClientPRLocalMetadata removeByC_LWKURI(
			long companyId, String localWellKnownURI)
		throws NoSuchOAuthClientPRLocalMetadataException {

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata = findByC_LWKURI(
			companyId, localWellKnownURI);

		return remove(oAuthClientPRLocalMetadata);
	}

	/**
	 * Returns the number of o auth client pr local metadatas where companyId = &#63; and localWellKnownURI = &#63;.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownURI the local well known uri
	 * @return the number of matching o auth client pr local metadatas
	 */
	@Override
	public int countByC_LWKURI(long companyId, String localWellKnownURI) {
		return _uniquePersistenceFinderByC_LWKURI.count(
			finderCache, new Object[] {companyId, localWellKnownURI});
	}

	private FinderPath _finderPathFetchByC_R;
	private UniquePersistenceFinder<OAuthClientPRLocalMetadata>
		_uniquePersistenceFinderByC_R;

	/**
	 * Returns the o auth client pr local metadata where companyId = &#63; and resource = &#63; or throws a <code>NoSuchOAuthClientPRLocalMetadataException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param resource the resource
	 * @return the matching o auth client pr local metadata
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a matching o auth client pr local metadata could not be found
	 */
	@Override
	public OAuthClientPRLocalMetadata findByC_R(long companyId, String resource)
		throws NoSuchOAuthClientPRLocalMetadataException {

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata = fetchByC_R(
			companyId, resource);

		if (oAuthClientPRLocalMetadata == null) {
			String message =
				_uniquePersistenceFinderByC_R.buildNoSuchKeyMessage(
					_NO_SUCH_ENTITY_WITH_KEY,
					new Object[] {companyId, resource});

			if (_log.isDebugEnabled()) {
				_log.debug(message);
			}

			throw new NoSuchOAuthClientPRLocalMetadataException(message);
		}

		return oAuthClientPRLocalMetadata;
	}

	/**
	 * Returns the o auth client pr local metadata where companyId = &#63; and resource = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param resource the resource
	 * @return the matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	@Override
	public OAuthClientPRLocalMetadata fetchByC_R(
		long companyId, String resource) {

		return fetchByC_R(companyId, resource, true);
	}

	/**
	 * Returns the o auth client pr local metadata where companyId = &#63; and resource = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param resource the resource
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	@Override
	public OAuthClientPRLocalMetadata fetchByC_R(
		long companyId, String resource, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_R.fetch(
			finderCache, new Object[] {companyId, resource}, useFinderCache);
	}

	/**
	 * Removes the o auth client pr local metadata where companyId = &#63; and resource = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param resource the resource
	 * @return the o auth client pr local metadata that was removed
	 */
	@Override
	public OAuthClientPRLocalMetadata removeByC_R(
			long companyId, String resource)
		throws NoSuchOAuthClientPRLocalMetadataException {

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata = findByC_R(
			companyId, resource);

		return remove(oAuthClientPRLocalMetadata);
	}

	/**
	 * Returns the number of o auth client pr local metadatas where companyId = &#63; and resource = &#63;.
	 *
	 * @param companyId the company ID
	 * @param resource the resource
	 * @return the number of matching o auth client pr local metadatas
	 */
	@Override
	public int countByC_R(long companyId, String resource) {
		return _uniquePersistenceFinderByC_R.count(
			finderCache, new Object[] {companyId, resource});
	}

	private FinderPath _finderPathFetchByERC_C;
	private UniquePersistenceFinder<OAuthClientPRLocalMetadata>
		_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the o auth client pr local metadata where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchOAuthClientPRLocalMetadataException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching o auth client pr local metadata
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a matching o auth client pr local metadata could not be found
	 */
	@Override
	public OAuthClientPRLocalMetadata findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchOAuthClientPRLocalMetadataException {

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata = fetchByERC_C(
			externalReferenceCode, companyId);

		if (oAuthClientPRLocalMetadata == null) {
			String message =
				_uniquePersistenceFinderByERC_C.buildNoSuchKeyMessage(
					_NO_SUCH_ENTITY_WITH_KEY,
					new Object[] {externalReferenceCode, companyId});

			if (_log.isDebugEnabled()) {
				_log.debug(message);
			}

			throw new NoSuchOAuthClientPRLocalMetadataException(message);
		}

		return oAuthClientPRLocalMetadata;
	}

	/**
	 * Returns the o auth client pr local metadata where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	@Override
	public OAuthClientPRLocalMetadata fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return fetchByERC_C(externalReferenceCode, companyId, true);
	}

	/**
	 * Returns the o auth client pr local metadata where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	@Override
	public OAuthClientPRLocalMetadata fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			finderCache, new Object[] {externalReferenceCode, companyId},
			useFinderCache);
	}

	/**
	 * Removes the o auth client pr local metadata where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the o auth client pr local metadata that was removed
	 */
	@Override
	public OAuthClientPRLocalMetadata removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchOAuthClientPRLocalMetadataException {

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata = findByERC_C(
			externalReferenceCode, companyId);

		return remove(oAuthClientPRLocalMetadata);
	}

	/**
	 * Returns the number of o auth client pr local metadatas where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching o auth client pr local metadatas
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			finderCache, new Object[] {externalReferenceCode, companyId});
	}

	public OAuthClientPRLocalMetadataPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(OAuthClientPRLocalMetadata.class);

		setModelImplClass(OAuthClientPRLocalMetadataImpl.class);
		setModelPKClass(long.class);

		setTable(OAuthClientPRLocalMetadataTable.INSTANCE);
	}

	/**
	 * Creates a new o auth client pr local metadata with the primary key. Does not add the o auth client pr local metadata to the database.
	 *
	 * @param oAuthClientPRLocalMetadataId the primary key for the new o auth client pr local metadata
	 * @return the new o auth client pr local metadata
	 */
	@Override
	public OAuthClientPRLocalMetadata create(
		long oAuthClientPRLocalMetadataId) {

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			new OAuthClientPRLocalMetadataImpl();

		oAuthClientPRLocalMetadata.setNew(true);
		oAuthClientPRLocalMetadata.setPrimaryKey(oAuthClientPRLocalMetadataId);

		String uuid = PortalUUIDUtil.generate();

		oAuthClientPRLocalMetadata.setUuid(uuid);

		oAuthClientPRLocalMetadata.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return oAuthClientPRLocalMetadata;
	}

	/**
	 * Removes the o auth client pr local metadata with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param oAuthClientPRLocalMetadataId the primary key of the o auth client pr local metadata
	 * @return the o auth client pr local metadata that was removed
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a o auth client pr local metadata with the primary key could not be found
	 */
	@Override
	public OAuthClientPRLocalMetadata remove(long oAuthClientPRLocalMetadataId)
		throws NoSuchOAuthClientPRLocalMetadataException {

		return remove((Serializable)oAuthClientPRLocalMetadataId);
	}

	@Override
	protected OAuthClientPRLocalMetadata removeImpl(
		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(oAuthClientPRLocalMetadata)) {
				oAuthClientPRLocalMetadata =
					(OAuthClientPRLocalMetadata)session.get(
						OAuthClientPRLocalMetadataImpl.class,
						oAuthClientPRLocalMetadata.getPrimaryKeyObj());
			}

			if (oAuthClientPRLocalMetadata != null) {
				session.delete(oAuthClientPRLocalMetadata);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (oAuthClientPRLocalMetadata != null) {
			clearCache(oAuthClientPRLocalMetadata);
		}

		return oAuthClientPRLocalMetadata;
	}

	@Override
	public OAuthClientPRLocalMetadata updateImpl(
		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata) {

		boolean isNew = oAuthClientPRLocalMetadata.isNew();

		if (!(oAuthClientPRLocalMetadata instanceof
				OAuthClientPRLocalMetadataModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(oAuthClientPRLocalMetadata.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					oAuthClientPRLocalMetadata);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in oAuthClientPRLocalMetadata proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom OAuthClientPRLocalMetadata implementation " +
					oAuthClientPRLocalMetadata.getClass());
		}

		OAuthClientPRLocalMetadataModelImpl
			oAuthClientPRLocalMetadataModelImpl =
				(OAuthClientPRLocalMetadataModelImpl)oAuthClientPRLocalMetadata;

		if (Validator.isNull(oAuthClientPRLocalMetadata.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			oAuthClientPRLocalMetadata.setUuid(uuid);
		}

		if (Validator.isNull(
				oAuthClientPRLocalMetadata.getExternalReferenceCode())) {

			oAuthClientPRLocalMetadata.setExternalReferenceCode(
				oAuthClientPRLocalMetadata.getUuid());
		}
		else {
			if (!Objects.equals(
					oAuthClientPRLocalMetadataModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					oAuthClientPRLocalMetadata.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = oAuthClientPRLocalMetadata.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = oAuthClientPRLocalMetadata.getPrimaryKey();
					}

					try {
						oAuthClientPRLocalMetadata.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								OAuthClientPRLocalMetadata.class.getName(),
								classPK, ContentTypes.TEXT_HTML,
								Sanitizer.MODE_ALL,
								oAuthClientPRLocalMetadata.
									getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			OAuthClientPRLocalMetadata ercOAuthClientPRLocalMetadata =
				fetchByERC_C(
					oAuthClientPRLocalMetadata.getExternalReferenceCode(),
					oAuthClientPRLocalMetadata.getCompanyId());

			if (isNew) {
				if (ercOAuthClientPRLocalMetadata != null) {
					throw new DuplicateOAuthClientPRLocalMetadataExternalReferenceCodeException(
						"Duplicate o auth client pr local metadata with external reference code " +
							oAuthClientPRLocalMetadata.
								getExternalReferenceCode() + " and company " +
									oAuthClientPRLocalMetadata.getCompanyId());
				}
			}
			else {
				if ((ercOAuthClientPRLocalMetadata != null) &&
					(oAuthClientPRLocalMetadata.
						getOAuthClientPRLocalMetadataId() !=
							ercOAuthClientPRLocalMetadata.
								getOAuthClientPRLocalMetadataId())) {

					throw new DuplicateOAuthClientPRLocalMetadataExternalReferenceCodeException(
						"Duplicate o auth client pr local metadata with external reference code " +
							oAuthClientPRLocalMetadata.
								getExternalReferenceCode() + " and company " +
									oAuthClientPRLocalMetadata.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (oAuthClientPRLocalMetadata.getCreateDate() == null)) {
			if (serviceContext == null) {
				oAuthClientPRLocalMetadata.setCreateDate(date);
			}
			else {
				oAuthClientPRLocalMetadata.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!oAuthClientPRLocalMetadataModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				oAuthClientPRLocalMetadata.setModifiedDate(date);
			}
			else {
				oAuthClientPRLocalMetadata.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(oAuthClientPRLocalMetadata);
			}
			else {
				oAuthClientPRLocalMetadata =
					(OAuthClientPRLocalMetadata)session.merge(
						oAuthClientPRLocalMetadata);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(oAuthClientPRLocalMetadata, false);

		if (isNew) {
			oAuthClientPRLocalMetadata.setNew(false);
		}

		oAuthClientPRLocalMetadata.resetOriginalValues();

		return oAuthClientPRLocalMetadata;
	}

	/**
	 * Returns the o auth client pr local metadata with the primary key or throws a <code>NoSuchOAuthClientPRLocalMetadataException</code> if it could not be found.
	 *
	 * @param oAuthClientPRLocalMetadataId the primary key of the o auth client pr local metadata
	 * @return the o auth client pr local metadata
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a o auth client pr local metadata with the primary key could not be found
	 */
	@Override
	public OAuthClientPRLocalMetadata findByPrimaryKey(
			long oAuthClientPRLocalMetadataId)
		throws NoSuchOAuthClientPRLocalMetadataException {

		return findByPrimaryKey((Serializable)oAuthClientPRLocalMetadataId);
	}

	/**
	 * Returns the o auth client pr local metadata with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param oAuthClientPRLocalMetadataId the primary key of the o auth client pr local metadata
	 * @return the o auth client pr local metadata, or <code>null</code> if a o auth client pr local metadata with the primary key could not be found
	 */
	@Override
	public OAuthClientPRLocalMetadata fetchByPrimaryKey(
		long oAuthClientPRLocalMetadataId) {

		return fetchByPrimaryKey((Serializable)oAuthClientPRLocalMetadataId);
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
		return "oAuthClientPRLocalMetadataId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_OAUTHCLIENTPRLOCALMETADATA;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return OAuthClientPRLocalMetadataModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the o auth client pr local metadata persistence.
	 */
	@Activate
	public void activate() {
		_finderPathWithPaginationFindByUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
			new String[] {
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"uuid_"}, true);

		_finderPathWithoutPaginationFindByUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
			new String[] {String.class.getName()}, new String[] {"uuid_"},
			true);

		_finderPathCountByUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
			new String[] {String.class.getName()}, new String[] {"uuid_"},
			false);

		_collectionPersistenceFinderByUuid = new CollectionPersistenceFinder<>(
			this, _finderPathWithPaginationFindByUuid,
			_finderPathWithoutPaginationFindByUuid, _finderPathCountByUuid,
			_SQL_SELECT_OAUTHCLIENTPRLOCALMETADATA_WHERE,
			_SQL_COUNT_OAUTHCLIENTPRLOCALMETADATA_WHERE,
			OAuthClientPRLocalMetadataModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX,
			new FinderColumn<>(
				"oAuthClientPRLocalMetadata.", "uuid", FinderColumn.Type.STRING,
				"=", true, true, OAuthClientPRLocalMetadata::getUuid));

		_finderPathWithPaginationFindByUuid_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
			new String[] {
				String.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"uuid_", "companyId"}, true);

		_finderPathWithoutPaginationFindByUuid_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"uuid_", "companyId"}, true);

		_finderPathCountByUuid_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"uuid_", "companyId"}, false);

		_collectionPersistenceFinderByUuid_C =
			new CollectionPersistenceFinder<>(
				this, _finderPathWithPaginationFindByUuid_C,
				_finderPathWithoutPaginationFindByUuid_C,
				_finderPathCountByUuid_C,
				_SQL_SELECT_OAUTHCLIENTPRLOCALMETADATA_WHERE,
				_SQL_COUNT_OAUTHCLIENTPRLOCALMETADATA_WHERE,
				OAuthClientPRLocalMetadataModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX,
				new FinderColumn<>(
					"oAuthClientPRLocalMetadata.", "uuid",
					FinderColumn.Type.STRING, "=", true, false,
					OAuthClientPRLocalMetadata::getUuid),
				new FinderColumn<>(
					"oAuthClientPRLocalMetadata.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					OAuthClientPRLocalMetadata::getCompanyId));

		_finderPathWithPaginationFindByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId"}, true);

		_finderPathWithoutPaginationFindByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCompanyId",
			new String[] {Long.class.getName()}, new String[] {"companyId"},
			true);

		_finderPathCountByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCompanyId",
			new String[] {Long.class.getName()}, new String[] {"companyId"},
			false);

		_collectionPersistenceFinderByCompanyId =
			new CollectionPersistenceFinder<>(
				this, _finderPathWithPaginationFindByCompanyId,
				_finderPathWithoutPaginationFindByCompanyId,
				_finderPathCountByCompanyId,
				_SQL_SELECT_OAUTHCLIENTPRLOCALMETADATA_WHERE,
				_SQL_COUNT_OAUTHCLIENTPRLOCALMETADATA_WHERE,
				OAuthClientPRLocalMetadataModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX,
				new FinderColumn<>(
					"oAuthClientPRLocalMetadata.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					OAuthClientPRLocalMetadata::getCompanyId));

		_finderPathWithPaginationFindByUserId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUserId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"userId"}, true);

		_finderPathWithoutPaginationFindByUserId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUserId",
			new String[] {Long.class.getName()}, new String[] {"userId"}, true);

		_finderPathCountByUserId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUserId",
			new String[] {Long.class.getName()}, new String[] {"userId"},
			false);

		_collectionPersistenceFinderByUserId =
			new CollectionPersistenceFinder<>(
				this, _finderPathWithPaginationFindByUserId,
				_finderPathWithoutPaginationFindByUserId,
				_finderPathCountByUserId,
				_SQL_SELECT_OAUTHCLIENTPRLOCALMETADATA_WHERE,
				_SQL_COUNT_OAUTHCLIENTPRLOCALMETADATA_WHERE,
				OAuthClientPRLocalMetadataModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX,
				new FinderColumn<>(
					"oAuthClientPRLocalMetadata.", "userId",
					FinderColumn.Type.LONG, "=", true, true,
					OAuthClientPRLocalMetadata::getUserId));

		_finderPathWithPaginationFindByC_L = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_L",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "localWellKnownEnabled"}, true);

		_finderPathWithoutPaginationFindByC_L = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_L",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"companyId", "localWellKnownEnabled"}, true);

		_finderPathCountByC_L = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_L",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"companyId", "localWellKnownEnabled"}, false);

		_collectionPersistenceFinderByC_L = new CollectionPersistenceFinder<>(
			this, _finderPathWithPaginationFindByC_L,
			_finderPathWithoutPaginationFindByC_L, _finderPathCountByC_L,
			_SQL_SELECT_OAUTHCLIENTPRLOCALMETADATA_WHERE,
			_SQL_COUNT_OAUTHCLIENTPRLOCALMETADATA_WHERE,
			OAuthClientPRLocalMetadataModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX,
			new FinderColumn<>(
				"oAuthClientPRLocalMetadata.", "companyId",
				FinderColumn.Type.LONG, "=", true, false,
				OAuthClientPRLocalMetadata::getCompanyId),
			new FinderColumn<>(
				"oAuthClientPRLocalMetadata.", "localWellKnownEnabled",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				OAuthClientPRLocalMetadata::isLocalWellKnownEnabled));

		_finderPathFetchByC_LWKURI = createUniqueFinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_LWKURI",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "localWellKnownURI"}, false,
			OAuthClientPRLocalMetadata::getCompanyId,
			OAuthClientPRLocalMetadata::getLocalWellKnownURI);

		_uniquePersistenceFinderByC_LWKURI = new UniquePersistenceFinder<>(
			this, _finderPathFetchByC_LWKURI,
			_SQL_SELECT_OAUTHCLIENTPRLOCALMETADATA_WHERE,
			new FinderColumn<>(
				"oAuthClientPRLocalMetadata.", "companyId",
				FinderColumn.Type.LONG, "=", true, false,
				OAuthClientPRLocalMetadata::getCompanyId),
			new FinderColumn<>(
				"oAuthClientPRLocalMetadata.", "localWellKnownURI",
				FinderColumn.Type.STRING, "=", true, true,
				OAuthClientPRLocalMetadata::getLocalWellKnownURI));

		_finderPathFetchByC_R = createUniqueFinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByC_R",
			new String[] {Long.class.getName(), String.class.getName()},
			new String[] {"companyId", "resource"}, false,
			OAuthClientPRLocalMetadata::getCompanyId,
			OAuthClientPRLocalMetadata::getResource);

		_uniquePersistenceFinderByC_R = new UniquePersistenceFinder<>(
			this, _finderPathFetchByC_R,
			_SQL_SELECT_OAUTHCLIENTPRLOCALMETADATA_WHERE,
			new FinderColumn<>(
				"oAuthClientPRLocalMetadata.", "companyId",
				FinderColumn.Type.LONG, "=", true, false,
				OAuthClientPRLocalMetadata::getCompanyId),
			new FinderColumn<>(
				"oAuthClientPRLocalMetadata.", "resource",
				FinderColumn.Type.STRING, "=", true, true,
				OAuthClientPRLocalMetadata::getResource));

		_finderPathFetchByERC_C = createUniqueFinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "companyId"}, false,
			OAuthClientPRLocalMetadata::getExternalReferenceCode,
			OAuthClientPRLocalMetadata::getCompanyId);

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this, _finderPathFetchByERC_C,
			_SQL_SELECT_OAUTHCLIENTPRLOCALMETADATA_WHERE,
			new FinderColumn<>(
				"oAuthClientPRLocalMetadata.", "externalReferenceCode",
				FinderColumn.Type.STRING, "=", true, false,
				OAuthClientPRLocalMetadata::getExternalReferenceCode),
			new FinderColumn<>(
				"oAuthClientPRLocalMetadata.", "companyId",
				FinderColumn.Type.LONG, "=", true, true,
				OAuthClientPRLocalMetadata::getCompanyId));

		OAuthClientPRLocalMetadataUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		OAuthClientPRLocalMetadataUtil.setPersistence(null);

		entityCache.removeCache(OAuthClientPRLocalMetadataImpl.class.getName());
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
		OAuthClientPRLocalMetadataModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_OAUTHCLIENTPRLOCALMETADATA =
		"SELECT oAuthClientPRLocalMetadata FROM OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata";

	private static final String _SQL_SELECT_OAUTHCLIENTPRLOCALMETADATA_WHERE =
		"SELECT oAuthClientPRLocalMetadata FROM OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata WHERE ";

	private static final String _SQL_COUNT_OAUTHCLIENTPRLOCALMETADATA_WHERE =
		"SELECT COUNT(oAuthClientPRLocalMetadata) FROM OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No OAuthClientPRLocalMetadata exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		OAuthClientPRLocalMetadataPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:197676819