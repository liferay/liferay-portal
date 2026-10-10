/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.service;

import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.model.AMImageEntry;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.InputStream;
import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for AMImageEntry. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see AMImageEntryLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface AMImageEntryLocalService
	extends BaseLocalService, CTService<AMImageEntry>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.adaptive.media.image.service.impl.AMImageEntryLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the am image entry local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link AMImageEntryLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds an adaptive media image entry in the database and stores the image
	 * bytes in the file store.
	 *
	 * @param amImageConfigurationEntry the configuration used to create the
	 adaptive media image
	 * @param fileVersion the file version used to create the adaptive media
	 image
	 * @param height the adaptive media image's height
	 * @param width the adaptive media image's width
	 * @param inputStream the adaptive media image's input stream to store in
	 the file store
	 * @param size the adaptive media image's size
	 * @return the adaptive media image
	 * @throws PortalException if an adaptive media image already exists for the
	 file version and configuration
	 */
	public AMImageEntry addAMImageEntry(
			AMImageConfigurationEntry amImageConfigurationEntry,
			FileVersion fileVersion, int height, int width,
			InputStream inputStream, long size)
		throws PortalException;

	/**
	 * Adds the am image entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AMImageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param amImageEntry the am image entry
	 * @return the am image entry that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public AMImageEntry addAMImageEntry(AMImageEntry amImageEntry);

	/**
	 * Creates a new am image entry with the primary key. Does not add the am image entry to the database.
	 *
	 * @param amImageEntryId the primary key for the new am image entry
	 * @return the new am image entry
	 */
	@Transactional(enabled = false)
	public AMImageEntry createAMImageEntry(long amImageEntryId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes all the adaptive media images generated for the configuration in
	 * the company. This method deletes both the adaptive media image entry from
	 * the database and the bytes from the file store.
	 *
	 * @param companyId the primary key of the company
	 * @param amImageConfigurationEntry the configuration used to create the
	 adaptive media image
	 */
	public void deleteAMImageEntries(
		long companyId, AMImageConfigurationEntry amImageConfigurationEntry);

	/**
	 * Deletes the am image entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AMImageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param amImageEntry the am image entry
	 * @return the am image entry that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public AMImageEntry deleteAMImageEntry(AMImageEntry amImageEntry);

	/**
	 * Deletes the am image entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AMImageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param amImageEntryId the primary key of the am image entry
	 * @return the am image entry that was removed
	 * @throws PortalException if a am image entry with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public AMImageEntry deleteAMImageEntry(long amImageEntryId)
		throws PortalException;

	/**
	 * Deletes all the adaptive media images generated for a file version. This
	 * method deletes both the adaptive media image entry from the database and
	 * the bytes from the file store.
	 *
	 * @param fileVersion the file version
	 * @throws PortalException if the file version was not found
	 */
	public void deleteAMImageEntryFileVersion(FileVersion fileVersion)
		throws PortalException;

	/**
	 * Deletes adaptive media images generated for a file version under a given
	 * configuration. This method deletes both the adaptive media image entry
	 * from the database and the bytes from the file store.
	 *
	 * @param configurationUuid the configuration UUID
	 * @param fileVersionId the primary key of the file version
	 * @throws PortalException if the file version was not found
	 */
	public void deleteAMImageEntryFileVersion(
			String configurationUuid, long fileVersionId)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AMImageEntry fetchAMImageEntry(long amImageEntryId);

	/**
	 * Returns the adaptive media image entry generated for the configuration
	 * and file version.
	 *
	 * @param configurationUuid the UUID of the configuration used to create
	 the adaptive media image
	 * @param fileVersionId the primary key of the file version
	 * @return the matching adaptive media image entry, or <code>null</code> if
	 a matching adaptive media image entry could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AMImageEntry fetchAMImageEntry(
		String configurationUuid, long fileVersionId);

	/**
	 * Returns the am image entry matching the UUID and group.
	 *
	 * @param uuid the am image entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching am image entry, or <code>null</code> if a matching am image entry could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AMImageEntry fetchAMImageEntryByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns a range of all the am image entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.adaptive.media.image.model.impl.AMImageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @return the range of am image entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AMImageEntry> getAMImageEntries(int start, int end);

	/**
	 * Returns the list of adaptive media image entries generated for the
	 * file version.
	 *
	 * @param fileVersionId the primary key of the file version
	 * @return the list of adaptive media image entries in the file version
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AMImageEntry> getAMImageEntries(long fileVersionId);

	/**
	 * Returns all the am image entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the am image entries
	 * @param companyId the primary key of the company
	 * @return the matching am image entries, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AMImageEntry> getAMImageEntriesByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of am image entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the am image entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of am image entries
	 * @param end the upper bound of the range of am image entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching am image entries, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AMImageEntry> getAMImageEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<AMImageEntry> orderByComparator);

	/**
	 * Returns the number of am image entries.
	 *
	 * @return the number of am image entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAMImageEntriesCount();

	/**
	 * Returns the number of adaptive media image entries generated for the
	 * configuration in the company.
	 *
	 * @param companyId the primary key of the company
	 * @param configurationUuid the UUID of the configuration used to create
	 the adaptive media image
	 * @return the number of adaptive media image entries in the company for the
	 configuration
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAMImageEntriesCount(long companyId, String configurationUuid);

	/**
	 * Returns the am image entry with the primary key.
	 *
	 * @param amImageEntryId the primary key of the am image entry
	 * @return the am image entry
	 * @throws PortalException if a am image entry with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AMImageEntry getAMImageEntry(long amImageEntryId)
		throws PortalException;

	/**
	 * Returns the am image entry matching the UUID and group.
	 *
	 * @param uuid the am image entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching am image entry
	 * @throws PortalException if a matching am image entry could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AMImageEntry getAMImageEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns the input stream of the adaptive media image generated for a file
	 * version and configuration.
	 *
	 * @param amImageConfigurationEntry the configuration used to create the
	 adaptive media image
	 * @param fileVersion the file version used to create the adaptive media
	 image
	 * @return the input stream of the adaptive media image generated for a file
	 version and configuration
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public InputStream getAMImageEntryContentStream(
		AMImageConfigurationEntry amImageConfigurationEntry,
		FileVersion fileVersion);

	/**
	 * Returns the total number of adaptive media images that are expected to be
	 * in a company once they are generated. The number of adaptive media images
	 * could be less if there are images that haven't generated the adaptive
	 * media image yet.
	 *
	 * @param companyId the primary key of the company
	 * @return the number of expected adaptive media images for a company
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getExpectedAMImageEntriesCount(long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getPercentage(
		long companyId, String configurationUuid,
		int expectedAMImageEntriesCount);

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasAMImageEntryContent(
		String configurationUuid, FileVersion fileVersion);

	/**
	 * Updates the am image entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AMImageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param amImageEntry the am image entry
	 * @return the am image entry that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public AMImageEntry updateAMImageEntry(AMImageEntry amImageEntry);

	@Override
	@Transactional(enabled = false)
	public CTPersistence<AMImageEntry> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<AMImageEntry> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<AMImageEntry>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1036008632