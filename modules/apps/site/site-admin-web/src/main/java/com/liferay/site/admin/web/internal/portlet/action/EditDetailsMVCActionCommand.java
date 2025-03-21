/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.GroupNameException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.MembershipRequest;
import com.liferay.portal.kernel.model.MembershipRequestConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.MembershipRequestLocalService;
import com.liferay.portal.kernel.service.MembershipRequestService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.liveusers.LiveUsers;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.SITE_SETTINGS,
		"mvc.command.name=/site_admin/edit_details"
	},
	service = MVCActionCommand.class
)
public class EditDetailsMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long liveGroupId = ParamUtil.getLong(actionRequest, "liveGroupId");

		long defaultParentGroupId = ParamUtil.getLong(
			actionRequest, "parentGroupId",
			GroupConstants.DEFAULT_PARENT_GROUP_ID);

		long parentGroupId = ParamUtil.getLong(
			actionRequest, "parentGroupSearchContainerPrimaryKeys",
			defaultParentGroupId);

		int membershipRestriction =
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION;

		boolean actionRequestMembershipRestriction = ParamUtil.getBoolean(
			actionRequest, "membershipRestriction");

		if (actionRequestMembershipRestriction &&
			(parentGroupId != GroupConstants.DEFAULT_PARENT_GROUP_ID)) {

			membershipRestriction =
				GroupConstants.MEMBERSHIP_RESTRICTION_TO_PARENT_SITE_MEMBERS;
		}

		ServiceContext serviceContext = ActionUtil.getServiceContext(
			actionRequest, liveGroupId);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		Group liveGroup = _groupLocalService.getGroup(liveGroupId);

		Map<Locale, String> nameMap = _localization.getLocalizationMap(
			actionRequest, "name", liveGroup.getNameMap());
		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "description", liveGroup.getDescriptionMap());
		int type = ParamUtil.getInteger(
			actionRequest, "type", liveGroup.getType());
		boolean manualMembership = ParamUtil.getBoolean(
			actionRequest, "manualMembership", liveGroup.isManualMembership());
		boolean inheritContent = ParamUtil.getBoolean(
			actionRequest, "inheritContent", liveGroup.isInheritContent());
		boolean active = ParamUtil.getBoolean(
			actionRequest, "active", liveGroup.isActive());

		if (!liveGroup.isGuest() && !liveGroup.isOrganization()) {
			UnicodeProperties unicodeProperties =
				PropertiesParamUtil.getProperties(
					actionRequest, "TypeSettingsProperties--");

			Locale defaultLocale = LocaleUtil.fromLanguageId(
				unicodeProperties.getProperty("languageId"));

			_validateDefaultLocaleGroupName(nameMap, defaultLocale);
		}

		_groupService.updateGroup(
			liveGroupId, parentGroupId, nameMap, descriptionMap, type,
			manualMembership, membershipRestriction, liveGroup.getFriendlyURL(),
			inheritContent, active, serviceContext);

		if (type == GroupConstants.TYPE_SITE_OPEN) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			List<MembershipRequest> membershipRequests =
				_membershipRequestLocalService.search(
					liveGroupId, MembershipRequestConstants.STATUS_PENDING,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			for (MembershipRequest membershipRequest : membershipRequests) {
				_membershipRequestService.updateStatus(
					membershipRequest.getMembershipRequestId(),
					themeDisplay.translate("your-membership-has-been-approved"),
					MembershipRequestConstants.STATUS_APPROVED, serviceContext);

				LiveUsers.joinGroup(
					themeDisplay.getCompanyId(), membershipRequest.getGroupId(),
					new long[] {membershipRequest.getUserId()});
			}
		}
	}

	private void _validateDefaultLocaleGroupName(
			Map<Locale, String> nameMap, Locale defaultLocale)
		throws Exception {

		if ((nameMap == null) || Validator.isNull(nameMap.get(defaultLocale))) {
			throw new GroupNameException();
		}
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private GroupService _groupService;

	@Reference
	private Localization _localization;

	@Reference
	private MembershipRequestLocalService _membershipRequestLocalService;

	@Reference
	private MembershipRequestService _membershipRequestService;

}