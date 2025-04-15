/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.resource.v1_0;

import com.liferay.headless.admin.user.dto.v1_0.Segment;
import com.liferay.headless.admin.user.resource.v1_0.SegmentResource;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.segments.context.RequestContextMapper;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.provider.SegmentsEntryProviderRegistry;
import com.liferay.segments.service.SegmentsEntryService;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/segment.properties",
	scope = ServiceScope.PROTOTYPE, service = SegmentResource.class
)
public class SegmentResourceImpl extends BaseSegmentResourceImpl {

	@Override
	public Page<Segment> getSiteSegmentsPage(
		Long siteId, Pagination pagination) {

		return Page.of(
			transform(
				_segmentsEntryService.getSegmentsEntries(
					siteId, pagination.getStartPosition(),
					pagination.getEndPosition(), null),
				this::_toSegment),
			pagination, _segmentsEntryService.getSegmentsEntriesCount(siteId));
	}

	@Override
	public Page<Segment> getSiteUserAccountSegmentsPage(
			Long siteId, Long userAccountId)
		throws Exception {

		User user = _userService.getUserById(userAccountId);

		return Page.of(
			transformToList(
				ArrayUtil.toArray(
					_segmentsEntryProviderRegistry.getSegmentsEntryIds(
						siteId, user.getModelClassName(), user.getPrimaryKey(),
						_requestContextMapper.map(contextHttpServletRequest),
						new long[0])),
				segmentsEntryId -> _toSegment(
					_segmentsEntryService.getSegmentsEntry(segmentsEntryId))));
	}

	private Segment _toSegment(SegmentsEntry segmentsEntry) throws Exception {
		return _segmentDTOConverter.toDTO(segmentsEntry);
	}

	@Context
	private HttpHeaders _httpHeaders;

	@Reference
	private RequestContextMapper _requestContextMapper;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.user.internal.dto.v1_0.converter.SegmentDTOConverter)"
	)
	private DTOConverter<SegmentsEntry, Segment> _segmentDTOConverter;

	@Reference
	private SegmentsEntryProviderRegistry _segmentsEntryProviderRegistry;

	@Reference
	private SegmentsEntryService _segmentsEntryService;

	@Reference
	private UserService _userService;

}