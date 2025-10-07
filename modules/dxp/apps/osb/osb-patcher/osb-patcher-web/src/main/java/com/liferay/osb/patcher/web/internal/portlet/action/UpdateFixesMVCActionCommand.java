/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.portlet.action;

import com.liferay.osb.patcher.constants.PatcherFixConstants;
import com.liferay.osb.patcher.constants.PatcherPortletKeys;
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.permission.resource.PatcherPermission;
import com.liferay.osb.patcher.service.PatcherBuildLocalService;
import com.liferay.osb.patcher.service.PatcherFixLocalService;
import com.liferay.osb.patcher.service.PatcherFixPackLocalService;
import com.liferay.osb.patcher.service.PatcherFixRelLocalService;
import com.liferay.osb.patcher.util.JenkinsUtil;
import com.liferay.osb.patcher.util.PatcherBuildUtil;
import com.liferay.osb.patcher.util.PatcherFixRelUtil;
import com.liferay.osb.patcher.util.PatcherFixUtil;
import com.liferay.osb.patcher.web.internal.validator.PatcherFixValidator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PatcherPortletKeys.PATCHER,
		"mvc.command.name=/patcher/update_fixes"
	},
	service = MVCActionCommand.class
)
public class UpdateFixesMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long patcherFixId = ParamUtil.getLong(actionRequest, "patcherFixId");

		PatcherFix patcherFix = _patcherFixLocalService.getPatcherFix(
			patcherFixId);

		if (!PatcherPermission.contains(
				themeDisplay.getPermissionChecker(), patcherFix,
				ActionKeys.UPDATE, themeDisplay.getUserId())) {

			throw new PrincipalException.MustHavePermission(
				themeDisplay.getUserId());
		}

		String committish = ParamUtil.getString(actionRequest, "committish");
		String gitRemoteURL = ParamUtil.getString(
			actionRequest, "gitRemoteURL");
		boolean workaround = ParamUtil.getBoolean(actionRequest, "workaround");

		PatcherFixValidator patcherFixValidator = new PatcherFixValidator(
			_portal.getHttpServletRequest(actionRequest));

		patcherFixValidator.validateUpdate(patcherFix);

		List<PatcherFix> parentPatcherFixes =
			PatcherFixRelUtil.getParentPatcherFixes(patcherFix);

		if (patcherFix.getStatus() == WorkflowConstants.STATUS_FIX_COMPLETE) {
			patcherFix.setLatestFix(false);

			patcherFix = _patcherFixLocalService.updatePatcherFix(patcherFix);

			PatcherFixUtil.updateObsolete(patcherFixId, true);

			PatcherFix newPatcherFix = _patcherFixLocalService.createPatcherFix(
				0);

			newPatcherFix.setKey(patcherFix.getKey());
			newPatcherFix.setKeyVersion(
				BigDecimalUtil.add(patcherFix.getKeyVersion(), 0.1));
			newPatcherFix.setName(patcherFix.getName());
			newPatcherFix.setPatcherProductVersionId(
				patcherFix.getPatcherProductVersionId());
			newPatcherFix.setPatcherProjectVersionId(
				patcherFix.getPatcherProjectVersionId());
			newPatcherFix.setType(patcherFix.getType());

			newPatcherFix = _patcherFixLocalService.updatePatcherFix(
				newPatcherFix);

			patcherFix = newPatcherFix;
		}
		else if (PatcherFixUtil.isIncomplete(patcherFix)) {
			patcherFix.setGitHash(StringPool.BLANK);
			patcherFix.setJenkinsResults(StringPool.BLANK);

			_patcherFixPackLocalService.clearPatcherFixPatcherFixPacks(
				patcherFix.getPatcherFixId());
		}

		if (patcherFix.getType() == PatcherFixConstants.TYPE_REBASE) {
			_patcherFixRelLocalService.deletePatcherFixRelsByChildPatcherFixId(
				patcherFix.getPatcherFixId());

			if (Validator.isNotNull(committish) &&
				Validator.isNotNull(gitRemoteURL)) {

				patcherFix.setType(PatcherFixConstants.TYPE_PATCH);
			}
			else {
				PatcherFix rebaseFromPatcherFix = parentPatcherFixes.get(0);

				if (!rebaseFromPatcherFix.isLatestFix()) {
					rebaseFromPatcherFix =
						PatcherFixUtil.fetchPatcherFixByLatestFix(
							rebaseFromPatcherFix.getKey());
				}

				PatcherFixRelUtil.addPatcherFixRel(
					patcherFix.getPatcherFixId(),
					Collections.singletonList(
						rebaseFromPatcherFix.getPatcherFixId()));
			}
		}

		int status = WorkflowConstants.STATUS_FIX_ADDING;
		int type = PatcherFixConstants.TYPE_PATCH;

		if (patcherFix.getType() == PatcherFixConstants.TYPE_REBASE) {
			if (Validator.isNull(committish) ||
				Validator.isNull(gitRemoteURL)) {

				status = WorkflowConstants.STATUS_FIX_REBASING;
			}

			type = PatcherFixConstants.TYPE_REBASE;
		}
		else if (workaround) {
			type = PatcherFixConstants.TYPE_WORKAROUND;
		}

		patcherFix.setCommittish(committish);
		patcherFix.setGitRemoteURL(gitRemoteURL);
		patcherFix.setLatestFix(true);
		patcherFix.setObsolete(false);
		patcherFix.setStatus(status);
		patcherFix.setType(type);

		patcherFix = _patcherFixLocalService.updatePatcherFix(patcherFix);

		List<PatcherBuild> patcherBuilds =
			_patcherBuildLocalService.getPatcherFixPatcherBuilds(
				patcherFix.getPatcherFixId());

		for (PatcherBuild patcherBuild : patcherBuilds) {
			List<PatcherFix> incompletePatcherFixes =
				PatcherBuildUtil.getIncompletePatcherFixes(patcherBuild);

			if (incompletePatcherFixes.size() > 2) {
				continue;
			}

			patcherBuild = _patcherBuildLocalService.updateStatus(
				themeDisplay.getUserId(), patcherBuild.getPatcherBuildId(),
				PatcherBuildUtil.getNextPatcherBuildWorkflowStatus(
					patcherBuild, PatcherBuildUtil.isMergeOnly(patcherBuild)));

			PatcherBuildUtil.workflowParentPatcherBuild(
				themeDisplay.getUser(), patcherBuild);
		}

		JenkinsUtil.sendAgentJenkinsRequest(themeDisplay.getUser(), patcherFix);
	}

	@Reference
	private PatcherBuildLocalService _patcherBuildLocalService;

	@Reference
	private PatcherFixLocalService _patcherFixLocalService;

	@Reference
	private PatcherFixPackLocalService _patcherFixPackLocalService;

	@Reference
	private PatcherFixRelLocalService _patcherFixRelLocalService;

	@Reference
	private Portal _portal;

}