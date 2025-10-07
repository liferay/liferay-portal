/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.validator;

import com.liferay.osb.patcher.constants.PatcherBuildConstants;
import com.liferay.osb.patcher.constants.PatcherConstants;
import com.liferay.osb.patcher.constants.PatcherFixConstants;
import com.liferay.osb.patcher.constants.PatcherProductVersionConstants;
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherFixComponent;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.service.PatcherBuildLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixComponentLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixPackLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProductVersionLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;
import com.liferay.osb.patcher.util.JenkinsUtil;
import com.liferay.osb.patcher.util.PatcherFixPackUtil;
import com.liferay.osb.patcher.util.PatcherFixUtil;
import com.liferay.osb.patcher.util.PatcherProductVersionUtil;
import com.liferay.osb.patcher.util.PatcherProjectVersionUtil;
import com.liferay.osb.patcher.util.PatcherUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eudaldo Alonso
 */
public class PatcherFixValidator {

	public PatcherFixValidator(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;
	}

	public void validateAdd() throws Exception {
		validateCommittish();
		validateGitRemoteURL();
		validateProductVersion();
		validatePatcherProjectVersionId();

		long patcherProjectVersionId = ParamUtil.getLong(
			_httpServletRequest, "patcherProjectVersionId");

		validateName(patcherProjectVersionId);

		if (!PatcherProjectVersionUtil.isCombinedBranchPatcherProjectVersion(
				patcherProjectVersionId)) {

			validateSiblingProjectVersionFixes();
		}

		validateKey();
	}

	public void validateBuilds(PatcherFix patcherFix) throws Exception {
		validatePatcherFix(patcherFix);
	}

	public void validateCommittish() throws Exception {
		String committish = ParamUtil.getString(
			_httpServletRequest, "committish");

		if (Validator.isNull(committish)) {
			throw new PortalException("the-fix-branch-name-is-invalid");
		}

		PatcherProjectVersion patcherProjectVersion =
			PatcherProjectVersionLocalServiceUtil.
				fetchPatcherProjectVersionByCommittish(committish);

		if (patcherProjectVersion != null) {
			throw new PortalException(
				LanguageUtil.format(
					_httpServletRequest,
					"the-branch-name-cannot-be-the-same-as-the-project-" +
						"version-tag-name-x",
					patcherProjectVersion.getCommittish()));
		}
	}

	public void validateDelete(PatcherFix patcherFix) throws Exception {
		validatePatcherFix(patcherFix);

		PatcherFixUtil.validateDelete(patcherFix);
	}

	public void validateEdit(PatcherFix patcherFix) throws Exception {
		validatePatcherFix(patcherFix);
	}

	public void validateEditCommentsField(PatcherFix patcherFix)
		throws Exception {

		validatePatcherFix(patcherFix);
	}

	public void validateEditFixPackFields(PatcherFix patcherFix)
		throws Exception {

		validatePatcherFix(patcherFix);
	}

	public void validateExclude(PatcherFix patcherFix) throws Exception {
		validatePatcherFix(patcherFix);
	}

	public void validateFixes(PatcherFix patcherFix) throws Exception {
		validatePatcherFix(patcherFix);
	}

	public void validateGitRemoteURL() throws Exception {
		String gitRemoteURL = ParamUtil.getString(
			_httpServletRequest, "gitRemoteURL");

		Pattern pattern = Pattern.compile(
			PatcherConstants.GIT_REMOTE_URL_REGEX);

		Matcher matcher = pattern.matcher(gitRemoteURL);

		if (!matcher.find()) {
			throw new PortalException("the-fix-github-url-is-invalid");
		}
	}

	public void validateKey() throws Exception {
		long patcherProjectVersionId = ParamUtil.getLong(
			_httpServletRequest, "patcherProjectVersionId");

		String patcherFixName = PatcherUtil.preparePatcherName(
			ParamUtil.getString(_httpServletRequest, "patcherFixName"));

		String key = PatcherFixUtil.generateKey(
			patcherProjectVersionId, patcherFixName);

		List<PatcherFix> patcherFixes = PatcherFixUtil.getFilteredPatcherFixes(
			key, true);

		if (!patcherFixes.isEmpty()) {
			throw new PortalException("the-fix-already-exists");
		}
	}

	public void validateName(long patcherProjectVersionId) throws Exception {
		String patcherFixName = PatcherUtil.preparePatcherName(
			ParamUtil.getString(_httpServletRequest, "patcherFixName"));

		if (Validator.isNull(patcherFixName)) {
			throw new PortalException("the-fix-name-is-invalid");
		}

		PatcherProjectVersion patcherProjectVersion =
			PatcherProjectVersionLocalServiceUtil.fetchPatcherProjectVersion(
				patcherProjectVersionId);

		if (!PatcherUtil.isPatcherTickets(
				patcherFixName,
				patcherProjectVersion.getPatcherProductVersionId())) {

			throw new PortalException("the-fix-name-cannot-be-evaluated");
		}

		if (patcherProjectVersion.getPatcherProductVersionId() !=
				PatcherProductVersionUtil.getPatcherProductVersionId(
					PatcherProductVersionConstants.
						LABEL_PRODUCT_VERSION_PORTAL_6X)) {

			List<String> fixedIssues = PatcherUtil.getTokens(
				patcherProjectVersion.getFixedIssues());

			fixedIssues.retainAll(PatcherUtil.getTokens(patcherFixName));

			if (!fixedIssues.isEmpty()) {
				throw new PortalException(
					LanguageUtil.format(
						_httpServletRequest,
						"the-tickets-x-is-already-included-in-project-" +
							"version-x",
						new Object[] {
							StringUtil.merge(fixedIssues),
							patcherProjectVersion.getName()
						},
						false));
			}
		}
	}

	public void validateParentPatcherBuildMainFix(PatcherFix patcherFix)
		throws Exception {

		long count = PatcherBuildLocalServiceUtil.getPatcherBuildsCount(
			patcherFix.getPatcherFixId(),
			PatcherProductVersionUtil.getPatcherProductVersionId(
				PatcherProductVersionConstants.LABEL_PRODUCT_VERSION_PORTAL_6X),
			false, PatcherBuildConstants.TYPE_FIX_PACK);

		if ((count > 0) &&
			(patcherFix.getType() == PatcherFixConstants.TYPE_GENERATED) &&
			(patcherFix.getStatus() == WorkflowConstants.STATUS_ANY)) {

			throw new PortalException(
				"the-main-fix-of-a-parent-build-cannot-change");
		}
	}

	public void validatePatcherFix(PatcherFix patcherFix) throws Exception {
		if (patcherFix == null) {
			throw new PortalException("the-fix-does-not-exist");
		}
	}

	public void validatePatcherFixPackMainFix(PatcherFix patcherFix)
		throws Exception {

		PatcherFixPack patcherFixPack = PatcherFixPackUtil.fetchPatcherFixPack(
			patcherFix.getName(), patcherFix.getPatcherProjectVersionId());

		if (patcherFixPack != null) {
			throw new PortalException(
				"the-main-fix-of-a-fix-pack-cannot-change");
		}
	}

	public void validatePatcherFixPackUnderDevelopment(
			PatcherFixPack patcherFixPack)
		throws Exception {

		if (patcherFixPack.getStatus() !=
				WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT) {

			throw new PortalException(
				LanguageUtil.format(
					_httpServletRequest,
					"the-fix-pack-x-is-not-under-development-so-its-" +
						"dependencies-cannot-change",
					patcherFixPack.getName()));
		}
	}

	public void validatePatcherProjectVersionId() throws Exception {
		long patcherProjectVersionId = ParamUtil.getLong(
			_httpServletRequest, "patcherProjectVersionId");

		validatePatcherProjectVersionId(patcherProjectVersionId);
	}

	public void validatePatcherProjectVersionId(long patcherProjectVersionId)
		throws Exception {

		if (patcherProjectVersionId == 0) {
			throw new PortalException("the-project-version-is-invalid");
		}
	}

	public void validatePatcherProjectVersionName() throws Exception {
		String patcherProjectVersionName = ParamUtil.getString(
			_httpServletRequest, "patcherProjectVersionName");

		if (Validator.isNull(patcherProjectVersionName)) {
			throw new PortalException("the-project-version-name-is-invalid");
		}

		PatcherProjectVersion patcherProjectVersion =
			PatcherProjectVersionLocalServiceUtil.
				fetchPatcherProjectVersionByName(patcherProjectVersionName);

		if (patcherProjectVersion == null) {
			throw new PortalException("the-project-version-name-is-invalid");
		}
	}

	public void validateProductVersion() throws Exception {
		long patcherProductVersionId = ParamUtil.getLong(
			_httpServletRequest, "patcherProductVersionId");

		PatcherProductVersion patcherProductVersion =
			PatcherProductVersionLocalServiceUtil.fetchPatcherProductVersion(
				patcherProductVersionId);

		if (patcherProductVersion == null) {
			throw new PortalException("the-product-version-id-is-invalid");
		}
	}

	public void validateSaveByName() throws Exception {
		validatePatcherProjectVersionName();

		String patcherProjectVersionName = ParamUtil.getString(
			_httpServletRequest, "patcherProjectVersionName");

		PatcherProjectVersion patcherProjectVersion =
			PatcherProjectVersionLocalServiceUtil.
				fetchPatcherProjectVersionByName(patcherProjectVersionName);

		validateName(patcherProjectVersion.getPatcherProjectVersionId());
	}

	public void validateSetFixPackFields(PatcherFix patcherFix)
		throws Exception {

		validatePatcherFix(patcherFix);

		validatePatcherFixPackMainFix(patcherFix);

		Set<String> dependenciesComponentNames = new HashSet<>();

		String dependencies = ParamUtil.getString(
			_httpServletRequest, "dependencies");

		List<String> dependenciesTokens = PatcherUtil.getTokens(dependencies);

		for (String dependenciesToken : dependenciesTokens) {
			String[] names = dependenciesToken.split("->");

			if ((names.length % 2) != 0) {
				throw new PortalException("the-fix's-dependencies-are-invalid");
			}

			for (String name : names) {
				PatcherFixComponent patcherFixComponent =
					PatcherFixComponentLocalServiceUtil.
						fetchPatcherFixComponent(name);

				if (patcherFixComponent == null) {
					throw new PortalException(
						"the-fix's-dependencies-has-an-invalid-fix-component");
				}

				dependenciesComponentNames.add(name);
			}
		}

		Set<Long> patcherFixComponentIds = new HashSet<>();

		Map<String, Set<String>> patcherFixComponentDependencies =
			PatcherFixUtil.getComponentDependencies(
				patcherFix.getDependencies());
		Map<String, Set<String>> requestComponentDependencies =
			PatcherFixUtil.getComponentDependencies(dependencies);

		Set<Long> patcherFixPackIds = SetUtil.fromArray(
			ParamUtil.getLongValues(_httpServletRequest, "patcherFixPackIds"));

		for (long patcherFixPackId : patcherFixPackIds) {
			PatcherFixPack patcherFixPack =
				PatcherFixPackLocalServiceUtil.getPatcherFixPack(
					patcherFixPackId);

			if (patcherFixComponentIds.contains(
					patcherFixPack.getPatcherFixComponentId())) {

				throw new PortalException(
					"the-fix-cannot-be-in-multiple-fix-packs-with-the-same-" +
						"component");
			}

			patcherFixComponentIds.add(
				patcherFixPack.getPatcherFixComponentId());

			PatcherFixComponent patcherFixComponent =
				PatcherFixComponentLocalServiceUtil.getPatcherFixComponent(
					patcherFixPack.getPatcherFixComponentId());

			dependenciesComponentNames.remove(patcherFixComponent.getName());

			if (patcherFixPack.getStatus() ==
					WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT) {

				continue;
			}

			Set<String> patcherFixComponentNameDependencies = new HashSet<>();

			if (patcherFixComponentDependencies.containsKey(
					patcherFixComponent.getName())) {

				patcherFixComponentNameDependencies =
					patcherFixComponentDependencies.get(
						patcherFixComponent.getName());
			}

			Set<String> requestComponentNameDependencies = new HashSet<>();

			if (requestComponentDependencies.containsKey(
					patcherFixComponent.getName())) {

				requestComponentNameDependencies =
					requestComponentDependencies.get(
						patcherFixComponent.getName());
			}

			if (!requestComponentNameDependencies.equals(
					patcherFixComponentNameDependencies)) {

				throw new PortalException(
					LanguageUtil.format(
						_httpServletRequest,
						"the-fix-pack-x-is-not-under-development-so-its-" +
							"dependencies-cannot-change",
						patcherFixPack.getName()));
			}
		}

		if (!dependenciesComponentNames.isEmpty()) {
			throw new PortalException(
				"the-fix's-current-fix-packs-must-include-the-fix's-" +
					"dependency-components");
		}

		Set<Long> oldPatcherFixPackIds = new HashSet<>();

		List<PatcherFixPack> patcherFixPatcherFixPacks =
			PatcherFixPackLocalServiceUtil.getPatcherFixPatcherFixPacks(
				patcherFix.getPatcherFixId());

		for (PatcherFixPack patcherFixPatcherFixPack :
				patcherFixPatcherFixPacks) {

			oldPatcherFixPackIds.add(
				patcherFixPatcherFixPack.getPatcherFixPackId());
		}

		Set<Long> newPatcherFixPackIds = new HashSet<>(patcherFixPackIds);

		newPatcherFixPackIds.removeAll(oldPatcherFixPackIds);

		for (long newPatcherFixPackId : newPatcherFixPackIds) {
			List<String> patcherFixTokens = PatcherUtil.getTokens(
				patcherFix.getName());

			List<PatcherFix> patcherFixPackPatcherFixes =
				PatcherFixLocalServiceUtil.getPatcherFixPackPatcherFixes(
					newPatcherFixPackId);

			String patcherFixesNames = ListUtil.toString(
				patcherFixPackPatcherFixes, "name");

			patcherFixTokens.retainAll(
				PatcherUtil.getTokens(patcherFixesNames));

			if (patcherFixTokens.isEmpty()) {
				continue;
			}

			List<Long> patcherFixIds = new ArrayList<>();

			for (PatcherFix patcherFixPackPatcherFix :
					patcherFixPackPatcherFixes) {

				List<String> patcherFixPackPatcherFixTokens =
					PatcherUtil.getTokens(patcherFixPackPatcherFix.getName());

				if (!Collections.disjoint(
						patcherFixTokens, patcherFixPackPatcherFixTokens)) {

					patcherFixIds.add(
						patcherFixPackPatcherFix.getPatcherFixId());
				}
			}

			PatcherFixPack patcherFixPack =
				PatcherFixPackLocalServiceUtil.getPatcherFixPack(
					newPatcherFixPackId);

			throw new PortalException(
				LanguageUtil.format(
					_httpServletRequest,
					"the-fix-pack-x-already-has-tickets-x-in-fixes-x",
					new Object[] {
						patcherFixPack.getName(),
						StringUtil.merge(patcherFixTokens),
						StringUtil.merge(patcherFixIds)
					}));
		}

		Set<Long> changedPatcherFixPackIds = new HashSet<>(
			newPatcherFixPackIds);

		oldPatcherFixPackIds.removeAll(patcherFixPackIds);

		changedPatcherFixPackIds.addAll(oldPatcherFixPackIds);

		for (long changedPatcherFixPackId : changedPatcherFixPackIds) {
			validatePatcherFixPackUnderDevelopment(
				PatcherFixPackLocalServiceUtil.getPatcherFixPack(
					changedPatcherFixPackId));
		}

		List<String> requirementsTokens = PatcherUtil.getTokens(
			ParamUtil.getString(_httpServletRequest, "requirements"));

		for (String requirementsToken : requirementsTokens) {
			if (!JenkinsUtil.isValidJenkinsRequirement(requirementsToken)) {
				throw new PortalException(
					LanguageUtil.format(
						_httpServletRequest,
						"the-fix's-requirement-x-is-invalid",
						requirementsToken));
			}
		}
	}

	public void validateSiblingProjectVersionFixes() throws Exception {
		Set<String> siblingPatcherProjectVersionFixTickets = new HashSet<>();

		long patcherProjectVersionId = ParamUtil.getLong(
			_httpServletRequest, "patcherProjectVersionId");

		PatcherProjectVersion patcherProjectVersion =
			PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(
				patcherProjectVersionId);

		PatcherProjectVersion siblingPatcherProjectVersion =
			PatcherProjectVersionUtil.getSiblingPatcherProjectVersion(
				patcherProjectVersion.getCommittish());

		List<PatcherFix> siblingPatcherProjectVersionFixes =
			PatcherFixUtil.getFilteredPatcherFixes(
				siblingPatcherProjectVersion.getPatcherProjectVersionId(),
				WorkflowConstants.STATUS_ANY);

		for (PatcherFix siblingPatcherProjectVersionFix :
				siblingPatcherProjectVersionFixes) {

			if (siblingPatcherProjectVersionFix.getType() ==
					PatcherFixConstants.TYPE_EXCLUDED) {

				continue;
			}

			siblingPatcherProjectVersionFixTickets.addAll(
				PatcherUtil.getTickets(
					siblingPatcherProjectVersionFix.getName()));
		}

		String patcherFixName = PatcherUtil.preparePatcherName(
			ParamUtil.getString(_httpServletRequest, "patcherFixName"));

		List<String> patcherFixTickets = PatcherUtil.getTickets(patcherFixName);

		for (String patcherFixTicket : patcherFixTickets) {
			if (siblingPatcherProjectVersionFixTickets.contains(
					patcherFixTicket)) {

				throw new PortalException(
					LanguageUtil.format(
						_httpServletRequest,
						"the-fix-cannot-be-added-because-there-are-fixes-" +
							"containing-x-on-project-version-x",
						new Object[] {
							patcherFixTicket,
							siblingPatcherProjectVersion.getCommittish()
						}));
			}
		}
	}

	public void validateUpdate(PatcherFix patcherFix) throws Exception {
		validatePatcherFix(patcherFix);

		if ((patcherFix.getType() != PatcherFixConstants.TYPE_REBASE) ||
			(patcherFix.getStatus() ==
				WorkflowConstants.STATUS_FIX_REBASE_CONFLICT)) {

			validateCommittish();
			validateGitRemoteURL();
		}

		validateParentPatcherBuildMainFix(patcherFix);

		long patcherProductVersionId = ParamUtil.getLong(
			_httpServletRequest, "patcherProductVersionId");
		long patcherProjectVersionId = ParamUtil.getLong(
			_httpServletRequest, "patcherProjectVersionId");

		if ((patcherProductVersionId != 0) &&
			(patcherProductVersionId !=
				patcherFix.getPatcherProductVersionId())) {

			throw new PortalException("the-product-version-cannot-be-changed");
		}
		else if ((patcherProjectVersionId != 0L) &&
				 (patcherProjectVersionId !=
					 patcherFix.getPatcherProjectVersionId())) {

			throw new PortalException("the-project-version-cannot-be-changed");
		}

		validatePatcherFixPackMainFix(patcherFix);

		List<PatcherFixPack> patcherFixPacks =
			PatcherFixPackLocalServiceUtil.getPatcherFixPatcherFixPacks(
				patcherFix.getPatcherFixId());

		for (PatcherFixPack patcherFixPack : patcherFixPacks) {
			validatePatcherFixPackUnderDevelopment(patcherFixPack);
		}

		if (!patcherFix.isLatestFix()) {
			throw new PortalException(
				"the-fix-cannot-be-versioned-because-the-current-fix-is-not-" +
					"the-latest");
		}
	}

	public void validateUpdateCommentsField(PatcherFix patcherFix)
		throws Exception {

		validatePatcherFix(patcherFix);
	}

	public void validateView(PatcherFix patcherFix) throws Exception {
		validatePatcherFix(patcherFix);
	}

	private final HttpServletRequest _httpServletRequest;

}