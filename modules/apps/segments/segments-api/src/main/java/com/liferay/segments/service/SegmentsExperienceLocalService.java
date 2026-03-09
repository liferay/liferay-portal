/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.segments.model.SegmentsExperience;

import java.io.Serializable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for SegmentsExperience. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Eduardo Garcia
 * @see SegmentsExperienceLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface SegmentsExperienceLocalService
	extends BaseLocalService, CTService<SegmentsExperience>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.segments.service.impl.SegmentsExperienceLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the segments experience local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link SegmentsExperienceLocalServiceUtil} if injection and service tracking are not available.
	 */
	public SegmentsExperience addDefaultSegmentsExperience(
			String externalReferenceCode, long userId, long plid,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Adds the segments experience to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsExperienceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsExperience the segments experience
	 * @return the segments experience that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public SegmentsExperience addSegmentsExperience(
		SegmentsExperience segmentsExperience);

	public SegmentsExperience addSegmentsExperience(
			String externalReferenceCode, long userId, long groupId,
			String segmentsEntryERC, String segmentsEntryScopeERC, long plid,
			Map<Locale, String> nameMap, boolean active,
			UnicodeProperties typeSettingsUnicodeProperties,
			ServiceContext serviceContext)
		throws PortalException;

	public SegmentsExperience addSegmentsExperience(
			String externalReferenceCode, long userId, long groupId,
			String segmentsEntryERC, String segmentsEntryScopeERC, long plid,
			Map<Locale, String> nameMap, int priority, boolean active,
			UnicodeProperties typeSettingsUnicodeProperties,
			ServiceContext serviceContext)
		throws PortalException;

	public SegmentsExperience addSegmentsExperience(
			String externalReferenceCode, long userId, long groupId,
			String segmentsEntryERC, String segmentsEntryScopeERC,
			String segmentsExperienceKey, long plid,
			Map<Locale, String> nameMap, int priority, boolean active,
			UnicodeProperties typeSettingsUnicodeProperties,
			ServiceContext serviceContext)
		throws PortalException;

	public SegmentsExperience appendSegmentsExperience(
			long userId, long groupId, String segmentsEntryERC,
			String segmentsEntryScopeERC, long plid,
			Map<Locale, String> nameMap, boolean active,
			ServiceContext serviceContext)
		throws PortalException;

	public SegmentsExperience appendSegmentsExperience(
			long userId, long groupId, String segmentsEntryERC,
			String segmentsEntryScopeERC, long plid,
			Map<Locale, String> nameMap, boolean active,
			UnicodeProperties typeSettingsUnicodeProperties,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Creates a new segments experience with the primary key. Does not add the segments experience to the database.
	 *
	 * @param segmentsExperienceId the primary key for the new segments experience
	 * @return the new segments experience
	 */
	@Transactional(enabled = false)
	public SegmentsExperience createSegmentsExperience(
		long segmentsExperienceId);

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	public void deleteSegmentsEntrySegmentsExperiences(
			long groupId, String segmentsEntryERC, String segmentsEntryScopeERC)
		throws PortalException;

	public void deleteSegmentsEntrySegmentsExperiences(
			String segmentsEntryERC, String segmentsEntryScopeERC)
		throws PortalException;

	/**
	 * Deletes the segments experience with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsExperienceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsExperienceId the primary key of the segments experience
	 * @return the segments experience that was removed
	 * @throws PortalException if a segments experience with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public SegmentsExperience deleteSegmentsExperience(
			long segmentsExperienceId)
		throws PortalException;

	/**
	 * Deletes the segments experience from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsExperienceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsExperience the segments experience
	 * @return the segments experience that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public SegmentsExperience deleteSegmentsExperience(
			SegmentsExperience segmentsExperience)
		throws PortalException;

	public SegmentsExperience deleteSegmentsExperience(
			String externalReferenceCode, long groupId)
		throws PortalException;

	public void deleteSegmentsExperiences(long groupId, long plid)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
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
	public SegmentsExperience fetchDefaultSegmentsExperience(long plid);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long fetchDefaultSegmentsExperienceId(long plid);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SegmentsExperience fetchSegmentsExperience(
		long segmentsExperienceId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SegmentsExperience fetchSegmentsExperience(
		long groupId, long plid, int priority);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SegmentsExperience fetchSegmentsExperience(
		long groupId, String segmentsExperienceKey, long plid);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SegmentsExperience fetchSegmentsExperienceByExternalReferenceCode(
		String externalReferenceCode, long groupId);

	/**
	 * Returns the segments experience matching the UUID and group.
	 *
	 * @param uuid the segments experience's UUID
	 * @param groupId the primary key of the group
	 * @return the matching segments experience, or <code>null</code> if a matching segments experience could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SegmentsExperience fetchSegmentsExperienceByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLowestPriority(long groupId, long plid);

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Returns the segments experience with the primary key.
	 *
	 * @param segmentsExperienceId the primary key of the segments experience
	 * @return the segments experience
	 * @throws PortalException if a segments experience with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SegmentsExperience getSegmentsExperience(long segmentsExperienceId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SegmentsExperience getSegmentsExperience(
			long groupId, String segmentsExperienceKey, long plid)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SegmentsExperience getSegmentsExperienceByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the segments experience matching the UUID and group.
	 *
	 * @param uuid the segments experience's UUID
	 * @param groupId the primary key of the group
	 * @return the matching segments experience
	 * @throws PortalException if a matching segments experience could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SegmentsExperience getSegmentsExperienceByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the segments experiences.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsExperienceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @return the range of segments experiences
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SegmentsExperience> getSegmentsExperiences(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SegmentsExperience> getSegmentsExperiences(
			long groupId, boolean active)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SegmentsExperience> getSegmentsExperiences(
		long groupId, long plid);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SegmentsExperience> getSegmentsExperiences(
			long groupId, long plid, boolean active)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SegmentsExperience> getSegmentsExperiences(
		long groupId, long plid, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SegmentsExperience> getSegmentsExperiences(
		long groupId, String[] segmentsEntryERCs, String segmentsEntryScopeERC,
		long plid, boolean active);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SegmentsExperience> getSegmentsExperiences(
		long groupId, String[] segmentsEntryERCs, String segmentsEntryScopeERC,
		long plid, boolean active, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator);

	/**
	 * Returns all the segments experiences matching the UUID and company.
	 *
	 * @param uuid the UUID of the segments experiences
	 * @param companyId the primary key of the company
	 * @return the matching segments experiences, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SegmentsExperience> getSegmentsExperiencesByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of segments experiences matching the UUID and company.
	 *
	 * @param uuid the UUID of the segments experiences
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of segments experiences
	 * @param end the upper bound of the range of segments experiences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching segments experiences, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SegmentsExperience> getSegmentsExperiencesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SegmentsExperience> orderByComparator);

	/**
	 * Returns the number of segments experiences.
	 *
	 * @return the number of segments experiences
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSegmentsExperiencesCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSegmentsExperiencesCount(long groupId, long plid);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSegmentsExperiencesCount(
		long groupId, long plid, boolean active);

	public SegmentsExperience updateSegmentsExperience(
			long segmentsExperienceId, String segmentsEntryERC,
			String segmentsEntryScopeERC, Map<Locale, String> nameMap,
			boolean active)
		throws PortalException;

	public SegmentsExperience updateSegmentsExperience(
			long segmentsExperienceId, String segmentsEntryERC,
			String segmentsEntryScopeERC, Map<Locale, String> nameMap,
			boolean active, UnicodeProperties typeSettingsUnicodeProperties)
		throws PortalException;

	/**
	 * Updates the segments experience in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsExperienceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsExperience the segments experience
	 * @return the segments experience that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public SegmentsExperience updateSegmentsExperience(
		SegmentsExperience segmentsExperience);

	public SegmentsExperience updateSegmentsExperienceActive(
			long segmentsExperienceId, boolean active)
		throws PortalException;

	public SegmentsExperience updateSegmentsExperiencePriority(
			long segmentsExperienceId, int newPriority)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<SegmentsExperience> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<SegmentsExperience> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<SegmentsExperience>, R, E>
				updateUnsafeFunction)
		throws E;

}