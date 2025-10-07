/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.validator;

import com.liferay.osb.patcher.constants.PatcherFixPackConstants;
import com.liferay.osb.patcher.constants.PatcherProductVersionConstants;
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherFixComponent;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.service.PatcherBuildLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixComponentLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;
import com.liferay.osb.patcher.util.JenkinsUtil;
import com.liferay.osb.patcher.util.PatcherFixPackUtil;
import com.liferay.osb.patcher.util.PatcherFixRelUtil;
import com.liferay.osb.patcher.util.PatcherFixUtil;
import com.liferay.osb.patcher.util.PatcherProductVersionUtil;
import com.liferay.osb.patcher.util.PatcherScanUtil;
import com.liferay.osb.patcher.util.PatcherUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Eudaldo Alonso
 */
public class PatcherFixPackValidator {

	public PatcherFixPackValidator(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;
	}

	public void validateAdd() throws Exception {
		long patcherFixComponentId = ParamUtil.getLong(
			_httpServletRequest, "patcherFixComponentId");

		PatcherFixComponent patcherFixComponent =
			PatcherFixComponentLocalServiceUtil.fetchPatcherFixComponent(
				patcherFixComponentId);

		if (patcherFixComponent == null) {
			throw new PortalException("the-fix-component-is-invalid");
		}

		int patcherFixPackVersion = ParamUtil.getInteger(
			_httpServletRequest, "patcherFixPackVersion",
			PatcherFixPackConstants.PATCHER_FIX_PACK_VERSION_DEFAULT);

		if (patcherFixPackVersion < 1) {
			throw new PortalException(
				"the-fix-pack-version-must-be-greater-than-zero");
		}

		validateRequirements();
	}

	public void validateBuild(PatcherFixPack patcherFixPack) throws Exception {
		String message = JenkinsUtil.validateJenkinsSetup(
			patcherFixPack.getCompanyId());

		if (Validator.isNotNull(message)) {
			throw new PortalException(message);
		}

		PatcherBuild patcherBuild =
			PatcherBuildLocalServiceUtil.fetchPatcherBuild(
				patcherFixPack.getPatcherBuildId());

		if (patcherBuild == null) {
			throw new PortalException(
				"the-fix-pack-cannot-be-built-because-it-is-not-merged");
		}

		PatcherFix patcherFix = PatcherFixLocalServiceUtil.getPatcherFix(
			patcherBuild.getPatcherFixId());

		if (Validator.isNull(patcherFix.getGitHash())) {
			throw new PortalException(
				"the-fix-pack-cannot-be-built-because-it-is-not-merged");
		}
	}

	public void validateDelete(PatcherFixPack patcherFixPack) throws Exception {
		validatePatcherFixPack(patcherFixPack);

		List<PatcherFixPack> newerPatcherFixPacks =
			PatcherFixPackUtil.getPatcherFixPackVersions(patcherFixPack, false);

		if (!newerPatcherFixPacks.isEmpty()) {
			throw new PortalException(
				"the-fix-pack-cannot-be-deleted-because-the-current-fix-pack-" +
					"is-not-the-latest");
		}
	}

	public void validateEdit(PatcherFixPack patcherFixPack) throws Exception {
		validatePatcherFixPack(patcherFixPack);
	}

	public void validateFrozenFixPack(PatcherFixPack patcherFixPack)
		throws Exception {

		validatePreviousPatcherFixPacks(patcherFixPack);

		List<String> pendingPatcherFixPackNames = new ArrayList<>();

		Set<PatcherFixPack> prerequisitePatcherFixPacks =
			PatcherFixPackUtil.getPrerequisitePatcherFixPacks(
				patcherFixPack.getPatcherFixPackId());

		for (PatcherFixPack prerequisitePatcherFixPack :
				prerequisitePatcherFixPacks) {

			if ((prerequisitePatcherFixPack.getStatus() !=
					WorkflowConstants.STATUS_FIX_PACK_FROZEN) &&
				(prerequisitePatcherFixPack.getStatus() !=
					WorkflowConstants.STATUS_FIX_PACK_RELEASED)) {

				pendingPatcherFixPackNames.add(
					prerequisitePatcherFixPack.getName());
			}
		}

		if (!pendingPatcherFixPackNames.isEmpty()) {
			String patcherFixPacks = StringUtil.merge(
				PatcherUtil.sortTokens(pendingPatcherFixPackNames));

			throw new PortalException(
				LanguageUtil.format(
					_httpServletRequest,
					"the-fix-pack-cannot-be-frozen-as-it-depends-on-the-" +
						"following-fix-packs-that-need-to-be-frozen-first-x",
					patcherFixPacks));
		}
	}

	public void validatePatcherFixPack(PatcherFixPack patcherFixPack)
		throws Exception {

		if (patcherFixPack == null) {
			throw new PortalException("the-fix-pack-does-not-exist");
		}
	}

	public void validatePreviousPatcherFixPacks(PatcherFixPack patcherFixPack)
		throws Exception {

		List<PatcherFixPack> patcherFixPackVersions =
			PatcherFixPackUtil.getPatcherFixPackVersions(patcherFixPack, true);

		for (PatcherFixPack patcherFixPackVersion : patcherFixPackVersions) {
			if (patcherFixPackVersion.getStatus() ==
					WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT) {

				throw new PortalException(
					"all-previous-fix-packs-of-the-same-component-must-no-" +
						"longer-be-under-development");
			}

			PatcherBuild patcherBuild =
				PatcherBuildLocalServiceUtil.fetchPatcherBuild(
					patcherFixPackVersion.getPatcherBuildId());

			if (patcherBuild == null) {
				throw new PortalException(
					"all-previous-fix-packs-of-the-same-component-must-" +
						"complete-merging");
			}

			PatcherFix patcherFix = PatcherFixLocalServiceUtil.getPatcherFix(
				patcherBuild.getPatcherFixId());

			if (Validator.isNull(patcherFix.getGitHash())) {
				throw new PortalException(
					"all-previous-fix-packs-of-the-same-component-must-" +
						"complete-merging");
			}
		}
	}

	public void validateReleaseFixPack(PatcherFixPack patcherFixPack)
		throws Exception {

		PatcherBuild patcherBuild =
			PatcherBuildLocalServiceUtil.fetchPatcherBuild(
				patcherFixPack.getPatcherBuildId());

		if (patcherBuild == null) {
			throw new PortalException(
				"the-fix-pack-must-complete-merging-before-release");
		}

		PatcherFix patcherFix = PatcherFixLocalServiceUtil.getPatcherFix(
			patcherBuild.getPatcherFixId());

		if (Validator.isNull(patcherFix.getGitHash())) {
			throw new PortalException(
				"the-fix-pack-must-complete-merging-before-release");
		}

		List<PatcherFixPack> patcherFixPackVersions =
			PatcherFixPackUtil.getPatcherFixPackVersions(patcherFixPack, true);

		for (PatcherFixPack patcherFixPackVersion : patcherFixPackVersions) {
			if (patcherFixPackVersion.getStatus() !=
					WorkflowConstants.STATUS_FIX_PACK_RELEASED) {

				throw new PortalException(
					"all-previous-fix-packs-of-the-same-component-must-" +
						"already-be-released");
			}

			patcherBuild = PatcherBuildLocalServiceUtil.fetchPatcherBuild(
				patcherFixPackVersion.getPatcherBuildId());

			if (patcherBuild == null) {
				throw new PortalException(
					"all-previous-fix-packs-of-the-same-component-must-" +
						"complete-merging");
			}

			patcherFix = PatcherFixLocalServiceUtil.getPatcherFix(
				patcherBuild.getPatcherFixId());

			if (Validator.isNull(patcherFix.getGitHash())) {
				throw new PortalException(
					"all-previous-fix-packs-of-the-same-component-must-" +
						"complete-merging");
			}
		}

		List<String> pendingPatcherFixPackNames = new ArrayList<>();

		Set<PatcherFixPack> prerequisitePatcherFixPacks =
			PatcherFixPackUtil.getPrerequisitePatcherFixPacks(
				patcherFixPack.getPatcherFixPackId());

		for (PatcherFixPack prerequisitePatcherFixPack :
				prerequisitePatcherFixPacks) {

			if (prerequisitePatcherFixPack.getStatus() !=
					WorkflowConstants.STATUS_FIX_PACK_RELEASED) {

				pendingPatcherFixPackNames.add(
					prerequisitePatcherFixPack.getName());
			}
		}

		if (!pendingPatcherFixPackNames.isEmpty()) {
			String patcherFixPacks = StringUtil.merge(
				PatcherUtil.sortTokens(pendingPatcherFixPackNames));

			throw new PortalException(
				LanguageUtil.format(
					_httpServletRequest,
					"the-fix-pack-cannot-be-released-as-it-depends-on-the-" +
						"following-fix-packs-that-need-to-be-released-first-x",
					patcherFixPacks));
		}
	}

	public void validateRequirements() throws PortalException {
		List<String> requirements = ListUtil.fromArray(
			StringUtil.split(
				ParamUtil.getString(_httpServletRequest, "requirements")));

		for (String requirement : requirements) {
			if (!JenkinsUtil.isValidJenkinsRequirement(requirement)) {
				throw new PortalException(
					"the-fix-pack's-requirement-is-invalid");
			}
		}
	}

	public void validateSetBuild(PatcherFixPack patcherFixPack)
		throws Exception {

		Set<Long> addPatcherFixIds = new HashSet<>();

		Set<Long> removePatcherFixIds = new HashSet<>();

		List<Long> patcherFixPackPatcherFixIds = new ArrayList<>();

		List<PatcherFix> patcherFixPackPatcherFixes =
			PatcherFixLocalServiceUtil.getPatcherFixPackPatcherFixes(
				patcherFixPack.getPatcherFixPackId());

		for (PatcherFix patcherFixPackPatcherFix : patcherFixPackPatcherFixes) {
			patcherFixPackPatcherFixIds.add(
				patcherFixPackPatcherFix.getPatcherFixId());

			if (!patcherFixPackPatcherFix.isLatestFix()) {
				removePatcherFixIds.add(
					patcherFixPackPatcherFix.getPatcherFixId());

				PatcherFix latestPatcherFix =
					PatcherFixUtil.fetchPatcherFixByLatestFix(
						patcherFixPackPatcherFix.getKey());

				addPatcherFixIds.add(latestPatcherFix.getPatcherFixId());
			}
		}

		List<Long> allPatcherFixIds = PatcherFixUtil.getPatcherFixIds(
			patcherFixPack);

		List<Long> copiedAllPatcherFixIds = ListUtil.copy(allPatcherFixIds);

		PatcherScanUtil.refinePatcherFixIds(allPatcherFixIds);

		List<Long> parentPatcherFixIds =
			PatcherFixRelUtil.getParentPatcherFixIds(
				allPatcherFixIds, copiedAllPatcherFixIds);

		for (long parentPatcherFixId : parentPatcherFixIds) {
			if (!patcherFixPackPatcherFixIds.contains(parentPatcherFixId)) {
				parentPatcherFixIds.remove(parentPatcherFixId);
			}
		}

		removePatcherFixIds.addAll(parentPatcherFixIds);

		addPatcherFixIds.addAll(
			PatcherFixRelUtil.getChildPatcherFixIds(
				allPatcherFixIds, copiedAllPatcherFixIds));

		if (!addPatcherFixIds.isEmpty() || !removePatcherFixIds.isEmpty()) {
			throw new PortalException(
				LanguageUtil.format(
					_httpServletRequest,
					"the-fix-pack-cannot-be-merged-as-x-needs-to-be-removed-" +
						"and-x-needs-to-be-added",
					new Object[] {removePatcherFixIds, addPatcherFixIds}));
		}

		validatePreviousPatcherFixPacks(patcherFixPack);
	}

	public void validateUnderDevelopmentFixPack(PatcherFixPack patcherFixPack)
		throws Exception {

		List<PatcherFixPack> patcherFixPackVersions =
			PatcherFixPackUtil.getPatcherFixPackVersions(patcherFixPack, false);

		for (PatcherFixPack patcherFixPackVersion : patcherFixPackVersions) {
			if (patcherFixPackVersion.getStatus() !=
					WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT) {

				throw new PortalException(
					"this-fix-pack-cannot-be-under-development-because-a-" +
						"newer-fix-pack-version-is-frozen");
			}
		}
	}

	public void validateUpdate(PatcherFixPack patcherFixPack) throws Exception {
		validatePatcherFixPack(patcherFixPack);
		validateRequirements();

		int status = ParamUtil.getInteger(_httpServletRequest, "status");

		if ((patcherFixPack.getStatus() !=
				WorkflowConstants.STATUS_FIX_PACK_FROZEN) &&
			(status == WorkflowConstants.STATUS_FIX_PACK_FROZEN)) {

			validateFrozenFixPack(patcherFixPack);
		}

		if ((patcherFixPack.getStatus() !=
				WorkflowConstants.STATUS_FIX_PACK_RELEASED) &&
			(status == WorkflowConstants.STATUS_FIX_PACK_RELEASED)) {

			validateReleaseFixPack(patcherFixPack);
		}

		if ((patcherFixPack.getStatus() !=
				WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT) &&
			(status == WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT)) {

			validateUnderDevelopmentFixPack(patcherFixPack);
		}
	}

	public void validateView(PatcherFixPack patcherFixPack) throws Exception {
		validatePatcherFixPack(patcherFixPack);
	}

	public void validateViewIssues() throws Exception {
		int fixPackVersion = ParamUtil.getInteger(
			_httpServletRequest, "fixPackVersion");

		if (fixPackVersion <= 0) {
			throw new PortalException(
				"the-patcher-fix-pack-version-is-invalid");
		}

		String rootPatcherProjectVersionName = ParamUtil.getString(
			_httpServletRequest, "rootPatcherProjectVersionName");

		if (Validator.isNull(rootPatcherProjectVersionName)) {
			throw new PortalException(
				"the-root-patcher-project-version-name-is-invalid");
		}

		PatcherProjectVersion rootPatcherProjectVersion =
			PatcherProjectVersionLocalServiceUtil.
				fetchPatcherProjectVersionByName(rootPatcherProjectVersionName);

		if ((rootPatcherProjectVersion == null) ||
			(rootPatcherProjectVersion.getRootPatcherProjectVersionId() != 0)) {

			throw new PortalException(
				"the-root-patcher-project-version-name-does-not-exist");
		}

		String patcherProjectVersionName = _getPatcherProjectVersionName(
			rootPatcherProjectVersion, fixPackVersion);

		PatcherProjectVersion patcherProjectVersion =
			PatcherProjectVersionLocalServiceUtil.
				fetchPatcherProjectVersionByName(patcherProjectVersionName);

		if (patcherProjectVersion == null) {
			throw new PortalException(
				LanguageUtil.format(
					_httpServletRequest,
					"the-patcher-project-version-name-x-does-not-exist",
					new Object[] {patcherProjectVersionName}));
		}

		if (rootPatcherProjectVersion.getPatcherProductVersionId() ==
				PatcherProductVersionUtil.getPatcherProductVersionId(
					PatcherProductVersionConstants.
						LABEL_PRODUCT_VERSION_PORTAL_6X)) {

			String patcherFixComponentName = ParamUtil.getString(
				_httpServletRequest, "patcherFixComponentName");

			if (Validator.isNull(patcherFixComponentName)) {
				throw new PortalException(
					"the-patcher-fix-component-name-is-invalid");
			}

			PatcherFixComponent patcherFixComponent =
				PatcherFixComponentLocalServiceUtil.fetchPatcherFixComponent(
					patcherFixComponentName);

			if (patcherFixComponent == null) {
				throw new PortalException(
					"the-patcher-fix-component-name-does-not-exist");
			}

			PatcherFixPack patcherFixPack =
				PatcherFixPackUtil.
					fetchPatcherFixPackByRootPatcherProjectVersion(
						patcherFixComponent.getPatcherFixComponentId(),
						fixPackVersion,
						rootPatcherProjectVersion.getPatcherProjectVersionId());

			if (patcherFixPack == null) {
				throw new PortalException("the-fix-pack-does-not-exist");
			}
		}
	}

	private String _getPatcherProjectVersionName(
			PatcherProjectVersion rootPatcherProjectVersion,
			int patcherFixPackVersion)
		throws Exception {

		if (rootPatcherProjectVersion.getPatcherProductVersionId() ==
				PatcherProductVersionUtil.getPatcherProductVersionId(
					PatcherProductVersionConstants.
						LABEL_PRODUCT_VERSION_PORTAL_70)) {

			return StringUtil.replace(
				rootPatcherProjectVersion.getName(),
				PatcherFixPackConstants.FIX_PACK_COMPONENT_BASE,
				PatcherFixPackConstants.FIX_PACK_COMPONENT_DE +
					StringPool.DASH + patcherFixPackVersion);
		}
		else if (rootPatcherProjectVersion.getPatcherProductVersionId() !=
					PatcherProductVersionUtil.getPatcherProductVersionId(
						PatcherProductVersionConstants.
							LABEL_PRODUCT_VERSION_PORTAL_6X)) {

			return StringUtil.replace(
				rootPatcherProjectVersion.getName(),
				PatcherFixPackConstants.FIX_PACK_COMPONENT_BASE,
				PatcherFixPackConstants.FIX_PACK_COMPONENT_DXP +
					StringPool.DASH + patcherFixPackVersion);
		}

		return rootPatcherProjectVersion.getName();
	}

	private final HttpServletRequest _httpServletRequest;

}