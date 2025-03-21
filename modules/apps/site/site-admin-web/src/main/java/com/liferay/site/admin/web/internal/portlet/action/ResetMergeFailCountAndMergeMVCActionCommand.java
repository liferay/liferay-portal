/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.portlet.action;

import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.layout.set.prototype.helper.LayoutSetPrototypeHelper;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.site.admin.web.internal.constants.SiteAdminPortletKeys;
import com.liferay.sites.kernel.util.Sites;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Resets the number of failed merge attempts for the site template, which
 * is accessed by retrieving the layout set prototype ID. Once the counter
 * is reset, the modified site template is merged back into its linked site,
 * which is accessed by retrieving the group ID and private layout set.
 *
 * <p>
 * If the number of failed merge attempts is not equal to zero after the
 * merge, an error key is submitted to {@link SessionErrors}.
 * </p>
 *
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SiteAdminPortletKeys.SITE_ADMIN,
		"mvc.command.name=/site_admin/reset_merge_fail_count_and_merge"
	},
	service = MVCActionCommand.class
)
public class ResetMergeFailCountAndMergeMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long layoutSetPrototypeId = ParamUtil.getLong(
			actionRequest, "layoutSetPrototypeId");

		LayoutSetPrototype layoutSetPrototype =
			_layoutSetPrototypeService.getLayoutSetPrototype(
				layoutSetPrototypeId);

		_layoutSetPrototypeHelper.setMergeFailCount(layoutSetPrototype, 0);

		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		boolean privateLayoutSet = ParamUtil.getBoolean(
			actionRequest, "privateLayoutSet");

		LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
			groupId, privateLayoutSet);

		_layoutSetPrototypeHelper.resetPrototype(layoutSet);

		MergeLayoutPrototypesThreadLocal.setSkipMerge(false);

		_sites.mergeLayoutSetPrototypeLayouts(
			_groupLocalService.getGroup(groupId), layoutSet);

		layoutSetPrototype = _layoutSetPrototypeService.getLayoutSetPrototype(
			layoutSetPrototypeId);

		if (layoutSetPrototype.getMergeFailCount() > 0) {
			SessionErrors.add(actionRequest, "resetMergeFailCountAndMerge");
		}
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference
	private LayoutSetPrototypeHelper _layoutSetPrototypeHelper;

	@Reference
	private LayoutSetPrototypeService _layoutSetPrototypeService;

	@Reference
	private Sites _sites;

}