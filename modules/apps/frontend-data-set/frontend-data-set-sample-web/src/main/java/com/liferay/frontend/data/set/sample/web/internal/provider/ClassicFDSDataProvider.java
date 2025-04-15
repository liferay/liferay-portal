/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.sample.web.internal.provider;

import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.frontend.data.set.sample.web.internal.constants.FDSSampleFDSNames;
import com.liferay.frontend.data.set.sample.web.internal.model.UserEntry;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marko Cikos
 */
@Component(
	property = "fds.data.provider.key=" + FDSSampleFDSNames.CLASSIC,
	service = FDSDataProvider.class
)
public class ClassicFDSDataProvider implements FDSDataProvider<UserEntry> {

	@Override
	public List<UserEntry> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return TransformUtil.transform(
			UsersAdminUtil.getUsers(
				_userLocalService.search(
					themeDisplay.getCompanyId(), fdsKeywords.getKeywords(),
					WorkflowConstants.STATUS_ANY,
					new LinkedHashMap<String, Object>(),
					fdsPagination.getStartPosition(),
					fdsPagination.getEndPosition(), sort)),
			user -> new UserEntry(
				user.isActive(), user.getEmailAddress(), user.getFirstName(),
				user.getUserId(), user.getLastName()));
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _userLocalService.searchCount(
			themeDisplay.getCompanyId(), fdsKeywords.getKeywords(),
			WorkflowConstants.STATUS_APPROVED, null);
	}

	@Reference
	private UserLocalService _userLocalService;

}