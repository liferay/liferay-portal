/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.connector.internal.provider;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.segments.asah.connector.cache.AsahSegmentsEntryCache;
import com.liferay.segments.asah.connector.internal.context.contributor.SegmentsAsahRequestContextContributor;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.context.Context;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsEntryModel;
import com.liferay.segments.model.SegmentsEntryRelModel;
import com.liferay.segments.provider.SegmentsEntryProvider;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsEntryRelLocalService;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Arques
 * @author Eduardo García
 */
@Component(
	property = {
		"segments.entry.provider.order:Integer=50",
		"segments.entry.provider.source=" + SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND
	},
	service = SegmentsEntryProvider.class
)
public class AsahSegmentsEntryProvider implements SegmentsEntryProvider {

	@Override
	public long[] getSegmentsEntryClassPKs(
			long segmentsEntryId, boolean memberLookup, int start, int end)
		throws PortalException {

		return getSegmentsEntryClassPKs(segmentsEntryId, start, end);
	}

	@Override
	public long[] getSegmentsEntryClassPKs(
			long segmentsEntryId, int start, int end)
		throws PortalException {

		return ListUtil.toLongArray(
			_segmentsEntryRelLocalService.getSegmentsEntryRels(
				segmentsEntryId, start, end, null),
			SegmentsEntryRelModel::getClassPK);
	}

	@Override
	public int getSegmentsEntryClassPKsCount(long segmentsEntryId)
		throws PortalException {

		return _segmentsEntryRelLocalService.getSegmentsEntryRelsCount(
			segmentsEntryId);
	}

	@Override
	public int getSegmentsEntryClassPKsCount(
			long segmentsEntryId, boolean memberLookup)
		throws PortalException {

		return getSegmentsEntryClassPKsCount(segmentsEntryId);
	}

	@Override
	public long[] getSegmentsEntryIds(
			long groupId, String className, long classPK, Context context)
		throws PortalException {

		return getSegmentsEntryIds(
			groupId, className, classPK, context, new long[0], new long[0]);
	}

	@Override
	public long[] getSegmentsEntryIds(
		long groupId, String className, long classPK, Context context,
		long[] filterSegmentsEntryIds, long[] segmentsEntryIds) {

		if (context == null) {
			return new long[0];
		}

		if (GetterUtil.getBoolean(context.get(Context.SIGNED_IN))) {
			List<SegmentsEntry> segmentsEntries =
				_segmentsEntryLocalService.getSegmentsEntries(
					groupId,
					new String[] {
						SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND
					},
					new int[] {
						SegmentsEntryConstants.TYPE_BATCH,
						SegmentsEntryConstants.TYPE_DEFAULT
					},
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			if (segmentsEntries.isEmpty()) {
				return new long[0];
			}

			segmentsEntries = ListUtil.filter(
				segmentsEntries,
				segmentsEntry ->
					(ArrayUtil.isEmpty(filterSegmentsEntryIds) ||
					 ArrayUtil.contains(
						 filterSegmentsEntryIds,
						 segmentsEntry.getSegmentsEntryId())) &&
					_segmentsEntryRelLocalService.hasSegmentsEntryRel(
						segmentsEntry.getSegmentsEntryId(),
						_portal.getClassNameId(className), classPK));

			segmentsEntries.sort(
				(segmentsEntry1, segmentsEntry2) -> {
					Date modifiedDate = segmentsEntry2.getModifiedDate();

					return modifiedDate.compareTo(
						segmentsEntry1.getModifiedDate());
				});

			return ListUtil.toLongArray(
				segmentsEntries, SegmentsEntryModel::getSegmentsEntryId);
		}

		String userId = GetterUtil.getString(
			context.get(
				SegmentsAsahRequestContextContributor.
					KEY_SEGMENTS_ANONYMOUS_USER_ID));

		if (Validator.isNull(userId)) {
			return new long[0];
		}

		long[] cachedSegmentsEntryIds =
			_asahSegmentsEntryCache.getSegmentsEntryIds(userId);

		if (cachedSegmentsEntryIds == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Asah segments cache not found for user ID " + userId);
			}

			return new long[0];
		}

		return cachedSegmentsEntryIds;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AsahSegmentsEntryProvider.class);

	@Reference
	private AsahSegmentsEntryCache _asahSegmentsEntryCache;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Reference
	private SegmentsEntryRelLocalService _segmentsEntryRelLocalService;

}