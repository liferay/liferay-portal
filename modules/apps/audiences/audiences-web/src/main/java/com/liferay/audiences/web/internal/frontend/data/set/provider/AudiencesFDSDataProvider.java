/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.web.internal.frontend.data.set.provider;

import com.liferay.audiences.model.AudiencesEntry;
import com.liferay.audiences.service.AudiencesEntryService;
import com.liferay.audiences.web.internal.constants.AudiencesFDSNames;
import com.liferay.audiences.web.internal.frontend.data.set.model.FDSAudiencesEntry;
import com.liferay.audiences.web.internal.util.comparator.AudiencesEntryModifiedDateComparator;
import com.liferay.audiences.web.internal.util.comparator.AudiencesEntryNameComparator;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = "fds.data.provider.key=" + AudiencesFDSNames.AUDIENCES_ENTRIES,
	service = FDSDataProvider.class
)
public class AudiencesFDSDataProvider
	implements FDSDataProvider<FDSAudiencesEntry> {

	@Override
	public List<FDSAudiencesEntry> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		List<AudiencesEntry> audiencesEntries = null;

		if (Validator.isNotNull(fdsKeywords.getKeywords())) {
			audiencesEntries = _audiencesEntryService.getAudiencesEntries(
				themeDisplay.getCompanyId(), fdsKeywords.getKeywords(),
				fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition(), _getOrderByComparator(sort));
		}
		else {
			audiencesEntries = _audiencesEntryService.getAudiencesEntries(
				themeDisplay.getCompanyId(), fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition(), _getOrderByComparator(sort));
		}

		return TransformUtil.transform(
			audiencesEntries,
			audiencesEntry -> {
				User user = _userLocalService.fetchUser(
					audiencesEntry.getUserId());

				return new FDSAudiencesEntry(
					audiencesEntry.getAudiencesEntryId(),
					audiencesEntry.getModifiedDate(), audiencesEntry.getName(),
					(user == null) ? audiencesEntry.getUserName() :
						user.getFullName());
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (Validator.isNotNull(fdsKeywords.getKeywords())) {
			return _audiencesEntryService.getAudiencesEntriesCount(
				themeDisplay.getCompanyId(), fdsKeywords.getKeywords());
		}

		return _audiencesEntryService.getAudiencesEntriesCount(
			themeDisplay.getCompanyId());
	}

	private OrderByComparator<AudiencesEntry> _getOrderByComparator(Sort sort) {
		if (sort == null) {
			return AudiencesEntryModifiedDateComparator.getInstance(false);
		}

		boolean ascending = !sort.isReverse();

		if (Objects.equals(sort.getFieldName(), "name")) {
			return AudiencesEntryNameComparator.getInstance(ascending);
		}

		return AudiencesEntryModifiedDateComparator.getInstance(ascending);
	}

	@Reference
	private AudiencesEntryService _audiencesEntryService;

	@Reference
	private UserLocalService _userLocalService;

}