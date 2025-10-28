/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
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

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for FragmentEntryLink. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see FragmentEntryLinkLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface FragmentEntryLinkLocalService
	extends BaseLocalService, CTService<FragmentEntryLink>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.fragment.service.impl.FragmentEntryLinkLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the fragment entry link local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link FragmentEntryLinkLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the fragment entry link to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentEntryLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fragmentEntryLink the fragment entry link
	 * @return the fragment entry link that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public FragmentEntryLink addFragmentEntryLink(
		FragmentEntryLink fragmentEntryLink);

	public FragmentEntryLink addFragmentEntryLink(
			String externalReferenceCode, long userId, long groupId,
			String originalFragmentEntryLinkERC, String fragmentEntryERC,
			String fragmentEntryScopeERC, long segmentsExperienceId, long plid,
			String css, String html, String js, String configuration,
			String editableValues, String namespace, int position,
			String rendererKey, int type, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new fragment entry link with the primary key. Does not add the fragment entry link to the database.
	 *
	 * @param fragmentEntryLinkId the primary key for the new fragment entry link
	 * @return the new fragment entry link
	 */
	@Transactional(enabled = false)
	public FragmentEntryLink createFragmentEntryLink(long fragmentEntryLinkId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the fragment entry link from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentEntryLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fragmentEntryLink the fragment entry link
	 * @return the fragment entry link that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public FragmentEntryLink deleteFragmentEntryLink(
		FragmentEntryLink fragmentEntryLink);

	/**
	 * Deletes the fragment entry link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentEntryLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fragmentEntryLinkId the primary key of the fragment entry link
	 * @return the fragment entry link that was removed
	 * @throws PortalException if a fragment entry link with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public FragmentEntryLink deleteFragmentEntryLink(long fragmentEntryLinkId)
		throws PortalException;

	public FragmentEntryLink deleteFragmentEntryLink(
			String externalReferenceCode, long groupId)
		throws PortalException;

	public void deleteFragmentEntryLinks(long groupId);

	public void deleteFragmentEntryLinks(
		long groupId, long plid, boolean deleted);

	public void deleteFragmentEntryLinks(long[] fragmentEntryLinkIds)
		throws PortalException;

	public void deleteFragmentEntryLinksByFragmentEntryERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC);

	public void deleteFragmentEntryLinksByFragmentEntryERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted);

	public List<FragmentEntryLink>
		deleteLayoutPageTemplateEntryFragmentEntryLinks(
			long groupId, long plid);

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #deleteLayoutPageTemplateEntryFragmentEntryLinks(long, long)}
	 */
	@Deprecated
	public List<FragmentEntryLink>
		deleteLayoutPageTemplateEntryFragmentEntryLinks(
			long groupId, long classNameId, long classPK);

	public List<FragmentEntryLink>
		deleteLayoutPageTemplateEntryFragmentEntryLinks(
			long groupId, long[] segmentsExperienceIds, long plid);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
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
	public FragmentEntryLink fetchFragmentEntryLink(long fragmentEntryLinkId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentEntryLink fetchFragmentEntryLinkByExternalReferenceCode(
		String externalReferenceCode, long groupId);

	/**
	 * Returns the fragment entry link matching the UUID and group.
	 *
	 * @param uuid the fragment entry link's UUID
	 * @param groupId the primary key of the group
	 * @return the matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentEntryLink fetchFragmentEntryLinkByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntryLink> getAllFragmentEntryLinksByFragmentEntryERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAllFragmentEntryLinksCountByFragmentEntryERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC);

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #getFragmentEntryLinksCountByPlid(long, long)}
	 */
	@Deprecated
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getClassedModelFragmentEntryLinksCount(
		long groupId, long classNameId, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	/**
	 * Returns the fragment entry link with the primary key.
	 *
	 * @param fragmentEntryLinkId the primary key of the fragment entry link
	 * @return the fragment entry link
	 * @throws PortalException if a fragment entry link with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentEntryLink getFragmentEntryLink(long fragmentEntryLinkId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentEntryLink getFragmentEntryLink(
		long groupId, String originalFragmentEntryLinkERC, long plid);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentEntryLink getFragmentEntryLinkByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the fragment entry link matching the UUID and group.
	 *
	 * @param uuid the fragment entry link's UUID
	 * @param groupId the primary key of the group
	 * @return the matching fragment entry link
	 * @throws PortalException if a matching fragment entry link could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FragmentEntryLink getFragmentEntryLinkByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the fragment entry links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.fragment.model.impl.FragmentEntryLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @return the range of fragment entry links
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntryLink> getFragmentEntryLinks(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntryLink> getFragmentEntryLinks(
		int type, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator);

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #getFragmentEntryLinksByPlid(long, long)}
	 */
	@Deprecated
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntryLink> getFragmentEntryLinks(
		long groupId, long classNameId, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntryLink> getFragmentEntryLinks(
		long companyId, String rendererKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntryLink> getFragmentEntryLinks(
		long companyId, String[] rendererKeys);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntryLink> getFragmentEntryLinks(String rendererKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntryLink> getFragmentEntryLinksByFragmentEntryERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntryLink> getFragmentEntryLinksByFragmentEntryERC(
		String fragmentEntryERC, String fragmentEntryScopeERC);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntryLink> getFragmentEntryLinksByPlid(
		long groupId, long plid);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntryLink> getFragmentEntryLinksBySegmentsExperienceId(
		long groupId, long segmentsExperienceId, long plid);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntryLink> getFragmentEntryLinksBySegmentsExperienceId(
		long groupId, long segmentsExperienceId, long plid, boolean deleted);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntryLink> getFragmentEntryLinksBySegmentsExperienceId(
		long groupId, long segmentsExperienceId, long plid, String rendererKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntryLink> getFragmentEntryLinksBySegmentsExperienceId(
		long groupId, long[] segmentsExperienceIds, long plid);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntryLink> getFragmentEntryLinksBySegmentsExperienceId(
		long groupId, long[] segmentsExperienceIds, long plid, boolean deleted);

	/**
	 * Returns all the fragment entry links matching the UUID and company.
	 *
	 * @param uuid the UUID of the fragment entry links
	 * @param companyId the primary key of the company
	 * @return the matching fragment entry links, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntryLink> getFragmentEntryLinksByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of fragment entry links matching the UUID and company.
	 *
	 * @param uuid the UUID of the fragment entry links
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of fragment entry links
	 * @param end the upper bound of the range of fragment entry links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching fragment entry links, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntryLink> getFragmentEntryLinksByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator);

	/**
	 * Returns the number of fragment entry links.
	 *
	 * @return the number of fragment entry links
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentEntryLinksCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentEntryLinksCountByFragmentEntryERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentEntryLinksCountByFragmentEntryERC(
		String fragmentEntryERC, String fragmentEntryScopeERC);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentEntryLinksCountByFragmentEntryERC(
		String fragmentEntryERC, String fragmentEntryScopeERC, boolean deleted);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFragmentEntryLinksCountByPlid(long groupId, long plid);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntryLink>
		getLayoutFragmentEntryLinksByFragmentEntryERC(
			long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
			int start, int end,
			OrderByComparator<FragmentEntryLink> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutFragmentEntryLinksCountByFragmentEntryERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FragmentEntryLink>
		getLayoutPageTemplateFragmentEntryLinksByFragmentEntryERC(
			long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
			int layoutPageTemplateType, int start, int end,
			OrderByComparator<FragmentEntryLink> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutPageTemplateFragmentEntryLinksCountByFragmentEntryERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		int layoutPageTemplateType);

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

	public void updateClassedModel(long userId, long plid);

	public FragmentEntryLink updateDeleted(
			long userId, long fragmentEntryLinkId, boolean deleted)
		throws PortalException;

	/**
	 * Updates the fragment entry link in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect FragmentEntryLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fragmentEntryLink the fragment entry link
	 * @return the fragment entry link that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public FragmentEntryLink updateFragmentEntryLink(
		FragmentEntryLink fragmentEntryLink);

	public FragmentEntryLink updateFragmentEntryLink(
			long userId, long fragmentEntryLinkId, String editableValues,
			boolean updateClassedModel)
		throws PortalException;

	public FragmentEntryLink updateFragmentEntryLink(
			long userId, long fragmentEntryLinkId,
			String originalFragmentEntryLinkERC, String fragmentEntryERC,
			String fragmentEntryScopeERC, long plid, String css, String html,
			String js, String configuration, String editableValues,
			String namespace, int position, int type,
			ServiceContext serviceContext)
		throws PortalException;

	public void updateLatestChanges(
			FragmentEntry fragmentEntry, FragmentEntryLink fragmentEntryLink)
		throws PortalException;

	public void updateLatestChanges(long fragmentEntryLinkId)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<FragmentEntryLink> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<FragmentEntryLink> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<FragmentEntryLink>, R, E>
				updateUnsafeFunction)
		throws E;

}