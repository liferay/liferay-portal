/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.rest.internal.resource.v1_0;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.segments.asah.connector.cache.AsahSegmentsEntryCache;
import com.liferay.segments.asah.rest.dto.v1_0.AsahSegmentsEntry;
import com.liferay.segments.asah.rest.dto.v1_0.Membership;
import com.liferay.segments.asah.rest.resource.v1_0.AsahSegmentsEntryResource;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.service.SegmentsEntryLocalService;

import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rachael Koestartyo
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/asah-segments-entry.properties",
	scope = ServiceScope.PROTOTYPE, service = AsahSegmentsEntryResource.class
)
public class AsahSegmentsEntryResourceImpl
	extends BaseAsahSegmentsEntryResourceImpl {

	@Override
	public Response postAsahSegmentsEntry(AsahSegmentsEntry asahSegmentsEntry)
		throws Exception {

		ServiceContext serviceContext = _getServiceContext();

		SegmentsEntry segmentsEntry =
			_segmentsEntryLocalService.fetchSegmentsEntry(
				serviceContext.getScopeGroupId(), asahSegmentsEntry.getId());

		try {
			Map<Locale, String> nameMap = Collections.singletonMap(
				_portal.getSiteDefaultLocale(serviceContext.getScopeGroupId()),
				asahSegmentsEntry.getName());

			if (segmentsEntry == null) {
				segmentsEntry = _segmentsEntryLocalService.addSegmentsEntry(
					null, asahSegmentsEntry.getId(), nameMap,
					Collections.emptyMap(), true, null,
					SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND,
					serviceContext);
			}
			else {
				segmentsEntry = _segmentsEntryLocalService.updateSegmentsEntry(
					segmentsEntry.getExternalReferenceCode(),
					segmentsEntry.getSegmentsEntryId(),
					asahSegmentsEntry.getId(), nameMap, null, true, null,
					serviceContext);
			}
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to process segment " + asahSegmentsEntry.getId(),
				portalException);
		}

		for (Membership membership : asahSegmentsEntry.getMemberships()) {
			_updateSegmentsEntryRels(
				membership.getIndividualPK(), membership.getRemoved(),
				segmentsEntry, membership.getUserId());
		}

		Response.ResponseBuilder responseBuilder = Response.ok();

		return responseBuilder.build();
	}

	private void _addSegmentsEntryRels(
		SegmentsEntry segmentsEntry, Set<Long> userIds) {

		try {
			_segmentsEntryLocalService.addSegmentsEntryClassPKs(
				segmentsEntry.getSegmentsEntryId(),
				ArrayUtil.toLongArray(userIds), _getServiceContext());
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to process user IDs " + userIds, portalException);
		}
	}

	private ServiceContext _getServiceContext() throws PortalException {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setScopeGroupId(contextCompany.getGroupId());

		User user = contextCompany.getGuestUser();

		serviceContext.setUserId(user.getUserId());

		return serviceContext;
	}

	private void _putSegmentsEntryIdsCache(
		String segmentsEntryId, String userId) {

		Set<Long> segmentsEntryIds = SetUtil.fromArray(
			_asahSegmentsEntryCache.getSegmentsEntryIds(userId));

		segmentsEntryIds.add(Long.valueOf(segmentsEntryId));

		_asahSegmentsEntryCache.putSegmentsEntryIds(
			userId, ArrayUtil.toLongArray(segmentsEntryIds));
	}

	private void _removeSegmentsEntryRels(
		SegmentsEntry segmentsEntry, Set<Long> userIds) {

		try {
			_segmentsEntryLocalService.deleteSegmentsEntryClassPKs(
				segmentsEntry.getSegmentsEntryId(),
				ArrayUtil.toLongArray(userIds));
		}
		catch (PortalException portalException) {
			_log.error("Unable to remove user IDs " + userIds, portalException);
		}
	}

	private void _updateSegmentsEntryRels(
		String individualPK, Boolean removed, SegmentsEntry segmentsEntry,
		Long userId) {

		if (userId != null) {
			if (removed) {
				_removeSegmentsEntryRels(
					segmentsEntry, Collections.singleton(userId));
			}
			else {
				_addSegmentsEntryRels(
					segmentsEntry, Collections.singleton(userId));
			}
		}
		else {
			_putSegmentsEntryIdsCache(
				segmentsEntry.getSegmentsEntryKey(), individualPK);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AsahSegmentsEntryResourceImpl.class);

	@Reference
	private AsahSegmentsEntryCache _asahSegmentsEntryCache;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsEntryLocalService _segmentsEntryLocalService;

}