/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet.action;

import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FragmentPortletKeys.FRAGMENT,
		"mvc.command.name=/fragment/propagate_fragment_entry_changes"
	},
	service = MVCActionCommand.class
)
public class PropagateFragmentEntryChangesMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long[] fragmentEntryLinkIds = ParamUtil.getLongValues(
			actionRequest, "rowIds");

		for (long fragmentEntryLinkId : fragmentEntryLinkIds) {
			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
					fragmentEntryLinkId);

			FragmentEntry fragmentEntry =
				_fragmentEntryLocalService.
					fetchFragmentEntryByExternalReferenceCode(
						fragmentEntryLink.getFragmentEntryERC(),
						fragmentEntryLink.getFragmentEntryGroupId());

			ActionableDynamicQuery actionableDynamicQuery =
				_fragmentEntryLinkLocalService.getActionableDynamicQuery();

			actionableDynamicQuery.setAddCriteriaMethod(
				dynamicQuery -> {
					Property fragmentEntryIdProperty =
						PropertyFactoryUtil.forName("fragmentEntryERC");

					dynamicQuery.add(
						fragmentEntryIdProperty.eq(
							fragmentEntry.getExternalReferenceCode()));

					Property fragmentEntryScopeERCProperty =
						PropertyFactoryUtil.forName("fragmentEntryScopeERC");

					if (Validator.isNull(fragmentEntry.getScopeERC())) {
						dynamicQuery.add(
							fragmentEntryScopeERCProperty.isNull());
					}
					else {
						dynamicQuery.add(
							fragmentEntryScopeERCProperty.eq(
								fragmentEntry.getScopeERC()));
					}

					Property plidProperty = PropertyFactoryUtil.forName("plid");

					dynamicQuery.add(
						plidProperty.eq(fragmentEntryLink.getPlid()));
				});
			actionableDynamicQuery.setCompanyId(
				fragmentEntryLink.getCompanyId());
			actionableDynamicQuery.setGroupId(fragmentEntryLink.getGroupId());
			actionableDynamicQuery.setPerformActionMethod(
				(FragmentEntryLink curFragmentEntryLink) ->
					_fragmentEntryLinkLocalService.updateLatestChanges(
						curFragmentEntryLink.getFragmentEntryLinkId()));

			actionableDynamicQuery.performActions();
		}
	}

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private FragmentEntryLocalService _fragmentEntryLocalService;

}