/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.dao.search;

import com.liferay.account.admin.web.internal.security.permission.resource.AccountEntryPermission;
import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.retriever.AccountOrganizationRetriever;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.Objects;

/**
 * @author Pei-Jung Lan
 */
public class AccountOrganizationSearchContainerFactory {

	public static SearchContainer<Organization> create(
			long accountEntryId, LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws PortalException {

		String emptyResultsMessage =
			"there-are-no-organizations-associated-with-this-account";

		AccountEntryOrganizationRelLocalService
			accountEntryOrganizationRelLocalService =
				_accountEntryOrganizationRelLocalServiceSnapshot.get();

		int count =
			accountEntryOrganizationRelLocalService.
				getAccountEntryOrganizationRelsCount(accountEntryId);

		if (count > 0) {
			emptyResultsMessage = "no-organizations-were-found";
		}

		SearchContainer<Organization> searchContainer = new SearchContainer(
			liferayPortletRequest,
			PortletURLUtil.getCurrent(
				liferayPortletRequest, liferayPortletResponse),
			null, emptyResultsMessage);

		searchContainer.setId("accountOrganizations");

		String orderByCol = SearchOrderByUtil.getOrderByCol(
			liferayPortletRequest, AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
			"organization-order-by-col", "name");

		if (orderByCol.equals("id")) {
			orderByCol = "organizationId";
		}

		searchContainer.setOrderByCol(orderByCol);
		searchContainer.setOrderByType(
			SearchOrderByUtil.getOrderByType(
				liferayPortletRequest, AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
				"organization-order-by-type", "asc"));

		String keywords = ParamUtil.getString(
			liferayPortletRequest, "keywords", null);

		AccountOrganizationRetriever accountOrganizationRetriever =
			_accountOrganizationRetrieverSnapshot.get();

		searchContainer.setResultsAndTotal(
			accountOrganizationRetriever.searchAccountOrganizations(
				accountEntryId, keywords, searchContainer.getStart(),
				searchContainer.getDelta(), searchContainer.getOrderByCol(),
				Objects.equals(searchContainer.getOrderByType(), "desc")));

		if (AccountEntryPermission.hasEditOrManageOrganizationsPermission(
				PermissionCheckerFactoryUtil.create(
					PortalUtil.getUser(liferayPortletRequest)),
				accountEntryId)) {

			searchContainer.setRowChecker(
				new EmptyOnClickRowChecker(liferayPortletResponse));
		}

		return searchContainer;
	}

	private static final Snapshot<AccountEntryOrganizationRelLocalService>
		_accountEntryOrganizationRelLocalServiceSnapshot = new Snapshot<>(
			AccountOrganizationSearchContainerFactory.class,
			AccountEntryOrganizationRelLocalService.class);
	private static final Snapshot<AccountOrganizationRetriever>
		_accountOrganizationRetrieverSnapshot = new Snapshot<>(
			AccountOrganizationSearchContainerFactory.class,
			AccountOrganizationRetriever.class);

}