/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.web.internal.display.util;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.segments.configuration.provider.SegmentsConfigurationProvider;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.provider.SegmentsEntryProviderRegistry;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;

/**
 * @author Pei-Jung Lan
 */
public class SegmentsEntryDisplayUtil {

	public static String getGroupDescriptiveName(
			SegmentsEntry segmentsEntry, Locale locale)
		throws Exception {

		GroupLocalService groupLocalService = _groupLocalServiceSnapshot.get();

		Group group = groupLocalService.fetchGroup(segmentsEntry.getGroupId());

		return group.getDescriptiveName(locale);
	}

	public static String getSegmentsCompanyConfigurationURL(
		HttpServletRequest httpServletRequest) {

		try {
			SegmentsConfigurationProvider segmentsConfigurationProvider =
				_segmentsConfigurationProviderSnapshot.get();

			return segmentsConfigurationProvider.getCompanyConfigurationURL(
				httpServletRequest);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return StringPool.BLANK;
	}

	public static List<User> getSegmentsEntryUsers(
			long segmentsEntryId, int start, int end)
		throws Exception {

		SegmentsEntryProviderRegistry segmentsEntryProviderRegistry =
			_segmentsEntryProviderRegistrySnapshot.get();

		return TransformUtil.transformToList(
			ArrayUtil.toLongArray(
				segmentsEntryProviderRegistry.getSegmentsEntryClassPKs(
					segmentsEntryId, start, end)),
			userId -> {
				UserLocalService userLocalService =
					_userLocalServiceSnapshot.get();

				return userLocalService.fetchUser(userId);
			});
	}

	public static int getSegmentsEntryUsersCount(long segmentsEntryId)
		throws Exception {

		SegmentsEntryProviderRegistry segmentsEntryProviderRegistry =
			_segmentsEntryProviderRegistrySnapshot.get();

		return segmentsEntryProviderRegistry.getSegmentsEntryClassPKsCount(
			segmentsEntryId);
	}

	public static boolean isRoleSegmentationEnabled(long companyId) {
		try {
			SegmentsConfigurationProvider segmentsConfigurationProvider =
				_segmentsConfigurationProviderSnapshot.get();

			return segmentsConfigurationProvider.isRoleSegmentationEnabled(
				companyId);
		}
		catch (ConfigurationException configurationException) {
			_log.error(configurationException);
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SegmentsEntryDisplayUtil.class);

	private static final Snapshot<GroupLocalService>
		_groupLocalServiceSnapshot = new Snapshot<>(
			SegmentsEntryDisplayUtil.class, GroupLocalService.class);
	private static final Snapshot<SegmentsConfigurationProvider>
		_segmentsConfigurationProviderSnapshot = new Snapshot<>(
			SegmentsEntryDisplayUtil.class,
			SegmentsConfigurationProvider.class);
	private static final Snapshot<SegmentsEntryProviderRegistry>
		_segmentsEntryProviderRegistrySnapshot = new Snapshot<>(
			SegmentsEntryDisplayUtil.class,
			SegmentsEntryProviderRegistry.class);
	private static final Snapshot<UserLocalService> _userLocalServiceSnapshot =
		new Snapshot<>(SegmentsEntryDisplayUtil.class, UserLocalService.class);

}