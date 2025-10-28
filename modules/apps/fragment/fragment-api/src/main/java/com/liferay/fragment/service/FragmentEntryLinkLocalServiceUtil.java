/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for FragmentEntryLink. This utility wraps
 * <code>com.liferay.fragment.service.impl.FragmentEntryLinkLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see FragmentEntryLinkLocalService
 * @generated
 */
public class FragmentEntryLinkLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.fragment.service.impl.FragmentEntryLinkLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
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
	public static FragmentEntryLink addFragmentEntryLink(
		FragmentEntryLink fragmentEntryLink) {

		return getService().addFragmentEntryLink(fragmentEntryLink);
	}

	public static FragmentEntryLink addFragmentEntryLink(
			String externalReferenceCode, long userId, long groupId,
			String originalFragmentEntryLinkERC, String fragmentEntryERC,
			String fragmentEntryScopeERC, long segmentsExperienceId, long plid,
			String css, String html, String js, String configuration,
			String editableValues, String namespace, int position,
			String rendererKey, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFragmentEntryLink(
			externalReferenceCode, userId, groupId,
			originalFragmentEntryLinkERC, fragmentEntryERC,
			fragmentEntryScopeERC, segmentsExperienceId, plid, css, html, js,
			configuration, editableValues, namespace, position, rendererKey,
			type, serviceContext);
	}

	/**
	 * Creates a new fragment entry link with the primary key. Does not add the fragment entry link to the database.
	 *
	 * @param fragmentEntryLinkId the primary key for the new fragment entry link
	 * @return the new fragment entry link
	 */
	public static FragmentEntryLink createFragmentEntryLink(
		long fragmentEntryLinkId) {

		return getService().createFragmentEntryLink(fragmentEntryLinkId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

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
	public static FragmentEntryLink deleteFragmentEntryLink(
		FragmentEntryLink fragmentEntryLink) {

		return getService().deleteFragmentEntryLink(fragmentEntryLink);
	}

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
	public static FragmentEntryLink deleteFragmentEntryLink(
			long fragmentEntryLinkId)
		throws PortalException {

		return getService().deleteFragmentEntryLink(fragmentEntryLinkId);
	}

	public static FragmentEntryLink deleteFragmentEntryLink(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().deleteFragmentEntryLink(
			externalReferenceCode, groupId);
	}

	public static void deleteFragmentEntryLinks(long groupId) {
		getService().deleteFragmentEntryLinks(groupId);
	}

	public static void deleteFragmentEntryLinks(
		long groupId, long plid, boolean deleted) {

		getService().deleteFragmentEntryLinks(groupId, plid, deleted);
	}

	public static void deleteFragmentEntryLinks(long[] fragmentEntryLinkIds)
		throws PortalException {

		getService().deleteFragmentEntryLinks(fragmentEntryLinkIds);
	}

	public static void deleteFragmentEntryLinksByFragmentEntryERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC) {

		getService().deleteFragmentEntryLinksByFragmentEntryERC(
			groupId, fragmentEntryERC, fragmentEntryScopeERC);
	}

	public static void deleteFragmentEntryLinksByFragmentEntryERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted) {

		getService().deleteFragmentEntryLinksByFragmentEntryERC(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, deleted);
	}

	public static List<FragmentEntryLink>
		deleteLayoutPageTemplateEntryFragmentEntryLinks(
			long groupId, long plid) {

		return getService().deleteLayoutPageTemplateEntryFragmentEntryLinks(
			groupId, plid);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #deleteLayoutPageTemplateEntryFragmentEntryLinks(long, long)}
	 */
	@Deprecated
	public static List<FragmentEntryLink>
		deleteLayoutPageTemplateEntryFragmentEntryLinks(
			long groupId, long classNameId, long classPK) {

		return getService().deleteLayoutPageTemplateEntryFragmentEntryLinks(
			groupId, classNameId, classPK);
	}

	public static List<FragmentEntryLink>
		deleteLayoutPageTemplateEntryFragmentEntryLinks(
			long groupId, long[] segmentsExperienceIds, long plid) {

		return getService().deleteLayoutPageTemplateEntryFragmentEntryLinks(
			groupId, segmentsExperienceIds, plid);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
	}

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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
	}

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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static FragmentEntryLink fetchFragmentEntryLink(
		long fragmentEntryLinkId) {

		return getService().fetchFragmentEntryLink(fragmentEntryLinkId);
	}

	public static FragmentEntryLink
		fetchFragmentEntryLinkByExternalReferenceCode(
			String externalReferenceCode, long groupId) {

		return getService().fetchFragmentEntryLinkByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the fragment entry link matching the UUID and group.
	 *
	 * @param uuid the fragment entry link's UUID
	 * @param groupId the primary key of the group
	 * @return the matching fragment entry link, or <code>null</code> if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink fetchFragmentEntryLinkByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchFragmentEntryLinkByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<FragmentEntryLink>
		getAllFragmentEntryLinksByFragmentEntryERC(
			long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
			int start, int end,
			OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getService().getAllFragmentEntryLinksByFragmentEntryERC(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, start, end,
			orderByComparator);
	}

	public static int getAllFragmentEntryLinksCountByFragmentEntryERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC) {

		return getService().getAllFragmentEntryLinksCountByFragmentEntryERC(
			groupId, fragmentEntryERC, fragmentEntryScopeERC);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #getFragmentEntryLinksCountByPlid(long, long)}
	 */
	@Deprecated
	public static int getClassedModelFragmentEntryLinksCount(
		long groupId, long classNameId, long classPK) {

		return getService().getClassedModelFragmentEntryLinksCount(
			groupId, classNameId, classPK);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	/**
	 * Returns the fragment entry link with the primary key.
	 *
	 * @param fragmentEntryLinkId the primary key of the fragment entry link
	 * @return the fragment entry link
	 * @throws PortalException if a fragment entry link with the primary key could not be found
	 */
	public static FragmentEntryLink getFragmentEntryLink(
			long fragmentEntryLinkId)
		throws PortalException {

		return getService().getFragmentEntryLink(fragmentEntryLinkId);
	}

	public static FragmentEntryLink getFragmentEntryLink(
		long groupId, String originalFragmentEntryLinkERC, long plid) {

		return getService().getFragmentEntryLink(
			groupId, originalFragmentEntryLinkERC, plid);
	}

	public static FragmentEntryLink getFragmentEntryLinkByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getFragmentEntryLinkByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the fragment entry link matching the UUID and group.
	 *
	 * @param uuid the fragment entry link's UUID
	 * @param groupId the primary key of the group
	 * @return the matching fragment entry link
	 * @throws PortalException if a matching fragment entry link could not be found
	 */
	public static FragmentEntryLink getFragmentEntryLinkByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getFragmentEntryLinkByUuidAndGroupId(uuid, groupId);
	}

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
	public static List<FragmentEntryLink> getFragmentEntryLinks(
		int start, int end) {

		return getService().getFragmentEntryLinks(start, end);
	}

	public static List<FragmentEntryLink> getFragmentEntryLinks(
		int type, int start, int end,
		OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getService().getFragmentEntryLinks(
			type, start, end, orderByComparator);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #getFragmentEntryLinksByPlid(long, long)}
	 */
	@Deprecated
	public static List<FragmentEntryLink> getFragmentEntryLinks(
		long groupId, long classNameId, long classPK) {

		return getService().getFragmentEntryLinks(
			groupId, classNameId, classPK);
	}

	public static List<FragmentEntryLink> getFragmentEntryLinks(
		long companyId, String rendererKey) {

		return getService().getFragmentEntryLinks(companyId, rendererKey);
	}

	public static List<FragmentEntryLink> getFragmentEntryLinks(
		long companyId, String[] rendererKeys) {

		return getService().getFragmentEntryLinks(companyId, rendererKeys);
	}

	public static List<FragmentEntryLink> getFragmentEntryLinks(
		String rendererKey) {

		return getService().getFragmentEntryLinks(rendererKey);
	}

	public static List<FragmentEntryLink>
		getFragmentEntryLinksByFragmentEntryERC(
			long groupId, String fragmentEntryERC,
			String fragmentEntryScopeERC) {

		return getService().getFragmentEntryLinksByFragmentEntryERC(
			groupId, fragmentEntryERC, fragmentEntryScopeERC);
	}

	public static List<FragmentEntryLink>
		getFragmentEntryLinksByFragmentEntryERC(
			String fragmentEntryERC, String fragmentEntryScopeERC) {

		return getService().getFragmentEntryLinksByFragmentEntryERC(
			fragmentEntryERC, fragmentEntryScopeERC);
	}

	public static List<FragmentEntryLink> getFragmentEntryLinksByPlid(
		long groupId, long plid) {

		return getService().getFragmentEntryLinksByPlid(groupId, plid);
	}

	public static List<FragmentEntryLink>
		getFragmentEntryLinksBySegmentsExperienceId(
			long groupId, long segmentsExperienceId, long plid) {

		return getService().getFragmentEntryLinksBySegmentsExperienceId(
			groupId, segmentsExperienceId, plid);
	}

	public static List<FragmentEntryLink>
		getFragmentEntryLinksBySegmentsExperienceId(
			long groupId, long segmentsExperienceId, long plid,
			boolean deleted) {

		return getService().getFragmentEntryLinksBySegmentsExperienceId(
			groupId, segmentsExperienceId, plid, deleted);
	}

	public static List<FragmentEntryLink>
		getFragmentEntryLinksBySegmentsExperienceId(
			long groupId, long segmentsExperienceId, long plid,
			String rendererKey) {

		return getService().getFragmentEntryLinksBySegmentsExperienceId(
			groupId, segmentsExperienceId, plid, rendererKey);
	}

	public static List<FragmentEntryLink>
		getFragmentEntryLinksBySegmentsExperienceId(
			long groupId, long[] segmentsExperienceIds, long plid) {

		return getService().getFragmentEntryLinksBySegmentsExperienceId(
			groupId, segmentsExperienceIds, plid);
	}

	public static List<FragmentEntryLink>
		getFragmentEntryLinksBySegmentsExperienceId(
			long groupId, long[] segmentsExperienceIds, long plid,
			boolean deleted) {

		return getService().getFragmentEntryLinksBySegmentsExperienceId(
			groupId, segmentsExperienceIds, plid, deleted);
	}

	/**
	 * Returns all the fragment entry links matching the UUID and company.
	 *
	 * @param uuid the UUID of the fragment entry links
	 * @param companyId the primary key of the company
	 * @return the matching fragment entry links, or an empty list if no matches were found
	 */
	public static List<FragmentEntryLink>
		getFragmentEntryLinksByUuidAndCompanyId(String uuid, long companyId) {

		return getService().getFragmentEntryLinksByUuidAndCompanyId(
			uuid, companyId);
	}

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
	public static List<FragmentEntryLink>
		getFragmentEntryLinksByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getService().getFragmentEntryLinksByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of fragment entry links.
	 *
	 * @return the number of fragment entry links
	 */
	public static int getFragmentEntryLinksCount() {
		return getService().getFragmentEntryLinksCount();
	}

	public static int getFragmentEntryLinksCountByFragmentEntryERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted) {

		return getService().getFragmentEntryLinksCountByFragmentEntryERC(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, deleted);
	}

	public static int getFragmentEntryLinksCountByFragmentEntryERC(
		String fragmentEntryERC, String fragmentEntryScopeERC) {

		return getService().getFragmentEntryLinksCountByFragmentEntryERC(
			fragmentEntryERC, fragmentEntryScopeERC);
	}

	public static int getFragmentEntryLinksCountByFragmentEntryERC(
		String fragmentEntryERC, String fragmentEntryScopeERC,
		boolean deleted) {

		return getService().getFragmentEntryLinksCountByFragmentEntryERC(
			fragmentEntryERC, fragmentEntryScopeERC, deleted);
	}

	public static int getFragmentEntryLinksCountByPlid(
		long groupId, long plid) {

		return getService().getFragmentEntryLinksCountByPlid(groupId, plid);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static List<FragmentEntryLink>
		getLayoutFragmentEntryLinksByFragmentEntryERC(
			long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
			int start, int end,
			OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getService().getLayoutFragmentEntryLinksByFragmentEntryERC(
			groupId, fragmentEntryERC, fragmentEntryScopeERC, start, end,
			orderByComparator);
	}

	public static int getLayoutFragmentEntryLinksCountByFragmentEntryERC(
		long groupId, String fragmentEntryERC, String fragmentEntryScopeERC) {

		return getService().getLayoutFragmentEntryLinksCountByFragmentEntryERC(
			groupId, fragmentEntryERC, fragmentEntryScopeERC);
	}

	public static List<FragmentEntryLink>
		getLayoutPageTemplateFragmentEntryLinksByFragmentEntryERC(
			long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
			int layoutPageTemplateType, int start, int end,
			OrderByComparator<FragmentEntryLink> orderByComparator) {

		return getService().
			getLayoutPageTemplateFragmentEntryLinksByFragmentEntryERC(
				groupId, fragmentEntryERC, fragmentEntryScopeERC,
				layoutPageTemplateType, start, end, orderByComparator);
	}

	public static int
		getLayoutPageTemplateFragmentEntryLinksCountByFragmentEntryERC(
			long groupId, String fragmentEntryERC, String fragmentEntryScopeERC,
			int layoutPageTemplateType) {

		return getService().
			getLayoutPageTemplateFragmentEntryLinksCountByFragmentEntryERC(
				groupId, fragmentEntryERC, fragmentEntryScopeERC,
				layoutPageTemplateType);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static void updateClassedModel(long userId, long plid) {
		getService().updateClassedModel(userId, plid);
	}

	public static FragmentEntryLink updateDeleted(
			long userId, long fragmentEntryLinkId, boolean deleted)
		throws PortalException {

		return getService().updateDeleted(userId, fragmentEntryLinkId, deleted);
	}

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
	public static FragmentEntryLink updateFragmentEntryLink(
		FragmentEntryLink fragmentEntryLink) {

		return getService().updateFragmentEntryLink(fragmentEntryLink);
	}

	public static FragmentEntryLink updateFragmentEntryLink(
			long userId, long fragmentEntryLinkId, String editableValues,
			boolean updateClassedModel)
		throws PortalException {

		return getService().updateFragmentEntryLink(
			userId, fragmentEntryLinkId, editableValues, updateClassedModel);
	}

	public static FragmentEntryLink updateFragmentEntryLink(
			long userId, long fragmentEntryLinkId,
			String originalFragmentEntryLinkERC, String fragmentEntryERC,
			String fragmentEntryScopeERC, long plid, String css, String html,
			String js, String configuration, String editableValues,
			String namespace, int position, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateFragmentEntryLink(
			userId, fragmentEntryLinkId, originalFragmentEntryLinkERC,
			fragmentEntryERC, fragmentEntryScopeERC, plid, css, html, js,
			configuration, editableValues, namespace, position, type,
			serviceContext);
	}

	public static void updateLatestChanges(
			com.liferay.fragment.model.FragmentEntry fragmentEntry,
			FragmentEntryLink fragmentEntryLink)
		throws PortalException {

		getService().updateLatestChanges(fragmentEntry, fragmentEntryLink);
	}

	public static void updateLatestChanges(long fragmentEntryLinkId)
		throws PortalException {

		getService().updateLatestChanges(fragmentEntryLinkId);
	}

	public static FragmentEntryLinkLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<FragmentEntryLinkLocalService>
		_serviceSnapshot = new Snapshot<>(
			FragmentEntryLinkLocalServiceUtil.class,
			FragmentEntryLinkLocalService.class);

}