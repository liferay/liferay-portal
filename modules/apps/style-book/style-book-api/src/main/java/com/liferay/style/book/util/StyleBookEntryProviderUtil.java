/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.util;

import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ScopeUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalServiceUtil;

import java.util.List;

/**
 * @author Gabriel Lima
 * @author Thiago Buarque
 */
public class StyleBookEntryProviderUtil {

	public static List<StyleBookEntry> getStyleBookEntries(
			long companyId, long groupId)
		throws PortalException {

		return StyleBookEntryLocalServiceUtil.getStyleBookEntries(
			_getGroupIds(companyId, groupId), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	public static List<StyleBookEntry> getStyleBookEntries(
			long companyId, long groupId, String themeId)
		throws PortalException {

		return StyleBookEntryLocalServiceUtil.getStyleBookEntries(
			_getGroupIds(companyId, groupId), themeId);
	}

	public static List<StyleBookEntry> getStyleBookEntries(
			long companyId, long groupId, String name, String themeId,
			int start, int end,
			OrderByComparator<StyleBookEntry> orderByComparator)
		throws PortalException {

		long[] groupIds = _getGroupIds(companyId, groupId);

		if (Validator.isNull(name)) {
			return StyleBookEntryLocalServiceUtil.getStyleBookEntries(
				groupIds, themeId, start, end, orderByComparator);
		}

		return StyleBookEntryLocalServiceUtil.getStyleBookEntries(
			groupIds, name, themeId, start, end, orderByComparator);
	}

	public static int getStyleBookEntriesCount(
			long companyId, long groupId, String name, String themeId)
		throws PortalException {

		long[] groupIds = _getGroupIds(companyId, groupId);

		if (Validator.isNull(name)) {
			return StyleBookEntryLocalServiceUtil.getStyleBookEntriesCount(
				groupIds, themeId);
		}

		return StyleBookEntryLocalServiceUtil.getStyleBookEntriesCount(
			groupIds, name, themeId);
	}

	public static StyleBookEntry getStyleBookEntry(Layout layout) {
		if (Validator.isNull(layout.getStyleBookEntryERC())) {
			return null;
		}

		StyleBookEntry styleBookEntry = null;

		Long groupId = ScopeUtil.getItemGroupId(
			layout.getCompanyId(), layout.getStyleBookEntryScopeERC(),
			layout.getGroupId());

		if ((groupId != null) && _isConnectedGroup(groupId, layout)) {
			styleBookEntry =
				StyleBookEntryLocalServiceUtil.
					fetchStyleBookEntryByExternalReferenceCode(
						layout.getStyleBookEntryERC(),
						StagingUtil.getLiveGroupId(groupId));
		}

		if ((styleBookEntry == null) && _log.isWarnEnabled()) {
			_log.warn(
				StringBundler.concat(
					"Unable to find style book entry with external reference ",
					"code ", layout.getStyleBookEntryERC(),
					" and scope external reference code ",
					layout.getStyleBookEntryScopeERC(), " for layout ",
					layout.getPlid()));
		}

		return styleBookEntry;
	}

	private static long[] _getGroupIds(long companyId, long groupId)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-57283")) {
			return new long[] {groupId};
		}

		SiteConnectedGroupGroupProvider siteConnectedGroupGroupProvider =
			_siteConnectedGroupGroupProviderSnapshot.get();

		if (siteConnectedGroupGroupProvider == null) {
			return new long[] {groupId};
		}

		return siteConnectedGroupGroupProvider.
			getCurrentAndAncestorSiteAndDepotGroupIds(groupId);
	}

	private static boolean _isConnectedGroup(long groupId, Layout layout) {
		try {
			return ArrayUtil.contains(
				_getGroupIds(layout.getCompanyId(), layout.getGroupId()),
				groupId);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StyleBookEntryProviderUtil.class);

	private static final Snapshot<SiteConnectedGroupGroupProvider>
		_siteConnectedGroupGroupProviderSnapshot = new Snapshot<>(
			StyleBookEntryProviderUtil.class,
			SiteConnectedGroupGroupProvider.class);

}