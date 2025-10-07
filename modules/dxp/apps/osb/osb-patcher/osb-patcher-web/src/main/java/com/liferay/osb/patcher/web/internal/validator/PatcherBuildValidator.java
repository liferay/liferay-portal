/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.validator;

import com.liferay.osb.patcher.configuration.PatcherConfiguration;
import com.liferay.osb.patcher.constants.PatcherBuildConstants;
import com.liferay.osb.patcher.constants.PatcherConstants;
import com.liferay.osb.patcher.constants.PatcherProductVersionConstants;
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.service.PatcherBuildLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherFixPackLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProductVersionLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;
import com.liferay.osb.patcher.util.HelpCenterUtil;
import com.liferay.osb.patcher.util.JenkinsUtil;
import com.liferay.osb.patcher.util.PatcherBuildRelUtil;
import com.liferay.osb.patcher.util.PatcherBuildUtil;
import com.liferay.osb.patcher.util.PatcherFixPackUtil;
import com.liferay.osb.patcher.util.PatcherProductVersionUtil;
import com.liferay.osb.patcher.util.PatcherProjectVersionUtil;
import com.liferay.osb.patcher.util.PatcherUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eudaldo Alonso
 */
public class PatcherBuildValidator {

	public PatcherBuildValidator(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;
	}

	public void validateAccount() throws Exception {
		String accountEntryCode = StringUtil.toUpperCase(
			ParamUtil.getString(_httpServletRequest, "accountEntryCode"));

		if (Validator.isNull(accountEntryCode)) {
			throw new PortalException("the-account-code-is-invalid");
		}

		for (int i = 0; i < accountEntryCode.length(); i++) {
			if (!Validator.isAscii(accountEntryCode.charAt(i))) {
				throw new PortalException(
					"the-account-code-contains-non-ascii-characters");
			}
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PatcherConfiguration patcherConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				PatcherConfiguration.class, themeDisplay.getCompanyId());

		List<String> accountWhitelist = ListUtil.fromArray(
			patcherConfiguration.patcherAccountWhitelist());

		if (!accountWhitelist.contains(
				StringUtil.toLowerCase(accountEntryCode))) {

			long accountEntryId = HelpCenterUtil.fetchAccountEntryId(
				accountEntryCode, themeDisplay.getCompanyId());

			if (accountEntryId <= 0) {
				_log.error(
					"The account does not exist in OSB: " + accountEntryCode);

				throw new PortalException("the-account-does-not-exist-in-osb");
			}
		}
	}

	public void validateAdd() throws Exception {
		validateAccount();
		validateProductVersion();
		validatePatcherProjectVersionId();

		PatcherBuild patcherBuild =
			PatcherBuildLocalServiceUtil.createPatcherBuild(0L);

		patcherBuild.setPatcherProductVersionId(
			ParamUtil.getLong(_httpServletRequest, "patcherProductVersionId"));
		patcherBuild.setPatcherProjectVersionId(
			ParamUtil.getLong(_httpServletRequest, "patcherProjectVersionId"));
		patcherBuild.setKeyVersion(PatcherBuildConstants.KEY_VERSION_DEFAULT);
		patcherBuild.setName(
			ParamUtil.getString(_httpServletRequest, "patcherBuildName"));

		validateKey(patcherBuild);
		validateName(patcherBuild);

		validateSupportTicket();
		validateType();
	}

	public void validateAddByName() throws Exception {
		validatePatcherBuildTypeLabel();
		validatePatcherProjectVersionName();
	}

	public void validateBuild(PatcherBuild patcherBuild) throws Exception {
		validatePatcherBuild(patcherBuild);

		validateChildPatcherBuild(patcherBuild);

		validatePatcherFixPack(patcherBuild);

		String message = JenkinsUtil.validateJenkinsSetup(
			patcherBuild.getCompanyId());

		if (Validator.isNotNull(message)) {
			throw new PortalException(message);
		}

		if (PatcherBuildUtil.isMergeOnly(patcherBuild)) {
			throw new PortalException(
				"the-build-cannot-be-built-because-the-build-is-merge-only");
		}

		List<PatcherFix> patcherFixes =
			PatcherBuildRelUtil.getChildPatcherBuildsMainFixes(patcherBuild);

		for (PatcherFix patcherFix : patcherFixes) {
			if (Validator.isNull(patcherFix.getGitHash())) {
				throw new PortalException(
					"the-build-cannot-be-built-because-its-fix-git-hash-is-" +
						"not-set");
			}
		}
	}

	public void validateChildPatcherBuild(PatcherBuild patcherBuild)
		throws Exception {

		if (PatcherBuildRelUtil.hasParentPatcherBuilds(patcherBuild)) {
			throw new PortalException(
				"the-action-cannot-be-performed-on-child-builds");
		}
	}

	public void validateContent(PatcherBuild patcherBuild) throws Exception {
		validatePatcherBuild(patcherBuild);

		validateChildPatcherBuild(patcherBuild);
	}

	public void validateDelete(PatcherBuild patcherBuild) throws Exception {
		validatePatcherBuild(patcherBuild);

		if (!patcherBuild.isLatestKeyBuild()) {
			throw new PortalException(
				"the-build-cannot-be-deleted-because-the-current-build-is-" +
					"not-the-latest");
		}

		PatcherFixPack patcherFixPack =
			PatcherFixPackLocalServiceUtil.fetchPatcherFixPackByPatcherBuildId(
				patcherBuild.getPatcherBuildId());

		if (patcherFixPack != null) {
			throw new PortalException(
				LanguageUtil.format(
					_httpServletRequest,
					"the-build-cannot-be-deleted-because-fix-pack-x-depends-" +
						"on-it",
					patcherFixPack.getName()));
		}
	}

	public void validateEdit(PatcherBuild patcherBuild) throws Exception {
		validatePatcherBuild(patcherBuild);

		validateChildPatcherBuild(patcherBuild);
	}

	public void validateEditCommentsField(PatcherBuild patcherBuild)
		throws Exception {

		validatePatcherBuild(patcherBuild);

		validateChildPatcherBuild(patcherBuild);
	}

	public void validateEditQAFields(PatcherBuild patcherBuild)
		throws Exception {

		validatePatcherBuild(patcherBuild);

		validateChildPatcherBuild(patcherBuild);
	}

	public void validateKey(PatcherBuild patcherBuild) throws Exception {
		PatcherConfiguration patcherConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				PatcherConfiguration.class, patcherBuild.getCompanyId());

		if (patcherConfiguration.patcherScanningEnabled()) {
			return;
		}

		String accountEntryCode = StringUtil.toUpperCase(
			ParamUtil.getString(_httpServletRequest, "accountEntryCode"));

		String key = PatcherBuildUtil.generateKey(
			patcherBuild.getPatcherProjectVersionId(), patcherBuild.getName(),
			accountEntryCode);

		List<PatcherBuild> patcherBuilds =
			PatcherBuildLocalServiceUtil.getPatcherBuildsByKey(
				key, patcherBuild.getKeyVersion(), true);

		if (!patcherBuilds.isEmpty()) {
			PatcherBuild oldPatcherBuild = patcherBuilds.get(0);

			if (oldPatcherBuild.getPatcherBuildId() !=
					patcherBuild.getPatcherBuildId()) {

				throw new PortalException("the-build-name-already-exists");
			}
		}
	}

	public void validateName(PatcherBuild patcherBuild) throws Exception {
		if (Validator.isNull(patcherBuild.getName())) {
			throw new PortalException("the-build-name-is-invalid");
		}

		String patcherBuildName = PatcherUtil.preparePatcherName(
			patcherBuild.getName());

		List<String> tokens = PatcherUtil.sortTokens(patcherBuildName);

		for (String token : tokens) {
			Matcher matcher = null;

			if (patcherBuild.getPatcherProductVersionId() ==
					PatcherProductVersionUtil.getPatcherProductVersionId(
						PatcherProductVersionConstants.
							LABEL_PRODUCT_VERSION_PORTAL_6X)) {

				matcher = _patcherTicketName6xPattern.matcher(token);
			}
			else {
				matcher = _patcherTicketNameAllPattern.matcher(token);
			}

			if (matcher.find()) {
				continue;
			}

			matcher = _patcherFixPackNamePattern.matcher(token);

			if (!matcher.find()) {
				throw new PortalException(
					LanguageUtil.format(
						_httpServletRequest,
						"the-build-name-has-invalid-token-x", token));
			}
			else if (patcherBuild.getPatcherProductVersionId() !=
						PatcherProductVersionUtil.getPatcherProductVersionId(
							PatcherProductVersionConstants.
								LABEL_PRODUCT_VERSION_PORTAL_6X)) {

				throw new PortalException(
					"the-build-name-cannot-contain-fix-packs");
			}

			PatcherFixPack patcherFixPack =
				PatcherFixPackUtil.fetchPatcherFixPack(
					token, patcherBuild.getPatcherProjectVersionId());

			if (patcherFixPack == null) {
				throw new PortalException(
					LanguageUtil.format(
						_httpServletRequest, "the-fix-pack-name-x-is-invalid",
						token));
			}

			if (patcherFixPack.getStatus() !=
					WorkflowConstants.STATUS_FIX_PACK_RELEASED) {

				throw new PortalException(
					LanguageUtil.format(
						_httpServletRequest,
						"the-fix-pack-name-x-is-not-released", token));
			}
		}

		if (patcherBuild.getPatcherProductVersionId() !=
				PatcherProductVersionUtil.getPatcherProductVersionId(
					PatcherProductVersionConstants.
						LABEL_PRODUCT_VERSION_PORTAL_6X)) {

			List<String> cumulativePatcherProjectVersionFixedIssues =
				PatcherProjectVersionUtil.
					getCumulativePatcherProjectVersionFixedIssues(
						patcherBuild.getPatcherProjectVersionId());

			if (cumulativePatcherProjectVersionFixedIssues.containsAll(
					tokens)) {

				PatcherProjectVersion patcherProjectVersion =
					PatcherProjectVersionLocalServiceUtil.
						getPatcherProjectVersion(
							patcherBuild.getPatcherProjectVersionId());

				throw new PortalException(
					LanguageUtil.format(
						_httpServletRequest,
						"all-the-tickets-in-the-ticket-list-are-included-in-x",
						patcherProjectVersion.getName()));
			}
		}
	}

	public void validatePatcherBuild(PatcherBuild patcherBuild)
		throws Exception {

		if (patcherBuild == null) {
			throw new PortalException("the-build-does-not-exist");
		}
	}

	public void validatePatcherBuildTypeLabel() throws Exception {
		String typeLabel = ParamUtil.getString(
			_httpServletRequest, "typeLabel");

		if (Validator.isNull(typeLabel)) {
			throw new PortalException("the-type-label-is-invalid");
		}
	}

	public void validatePatcherFixPack(PatcherBuild patcherBuild)
		throws Exception {

		if (patcherBuild.getType() != PatcherBuildConstants.TYPE_FIX_PACK) {
			return;
		}

		PatcherFixPack patcherFixPack = PatcherFixPackUtil.getPatcherFixPack(
			patcherBuild);

		if (patcherFixPack.getStatus() ==
				WorkflowConstants.STATUS_FIX_PACK_RELEASED) {

			throw new PortalException(
				"the-main-build-of-a-released-fix-pack-cannot-change");
		}
	}

	public void validatePatcherProjectVersionId() throws Exception {
		long patcherProjectVersionId = ParamUtil.getLong(
			_httpServletRequest, "patcherProjectVersionId");

		if (patcherProjectVersionId == 0) {
			throw new PortalException("the-build-project-version-is-invalid");
		}

		long patcherProductVersionId = ParamUtil.getLong(
			_httpServletRequest, "patcherProductVersionId");

		if (patcherProductVersionId !=
				PatcherProductVersionUtil.getPatcherProductVersionId(
					PatcherProductVersionConstants.
						LABEL_PRODUCT_VERSION_PORTAL_6X)) {

			PatcherProjectVersion patcherProjectVersion =
				PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersion(
					patcherProjectVersionId);

			if (patcherProjectVersion.getPatcherProductVersionId() ==
					PatcherProductVersionUtil.getPatcherProductVersionId(
						PatcherProductVersionConstants.
							LABEL_PRODUCT_VERSION_PORTAL_6X)) {

				throw new PortalException(
					"the-project-version-is-invalid-because-its-product-" +
						"version-is-6x");
			}

			Pattern pattern = Pattern.compile(
				PatcherConstants.LIFERAY_PORTAL_REPOSITORY_REGEX);

			Matcher matcher = pattern.matcher(
				patcherProjectVersion.getRepositoryName());

			if (!matcher.find() ||
				(patcherProjectVersion.getPatcherProductVersionId() ==
					PatcherProductVersionUtil.getPatcherProductVersionId(
						PatcherProductVersionConstants.
							LABEL_PRODUCT_VERSION_PORTAL_6X))) {

				throw new PortalException(
					"the-project-version-is-invalid-because-its-repository-" +
						"is-not-portal");
			}
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
		PatcherProductVersion patcherProductVersion =
			PatcherProductVersionLocalServiceUtil.getPatcherProductVersion(
				ParamUtil.getLong(
					_httpServletRequest, "patcherProductVersionId"));

		if (patcherProductVersion == null) {
			throw new PortalException("the-product-version-id-is-invalid");
		}
	}

	public void validateRelease(PatcherBuild patcherBuild) throws Exception {
		validatePatcherBuild(patcherBuild);
		validateChildPatcherBuild(patcherBuild);

		if (!PatcherBuildUtil.isCompleteOrReady(patcherBuild)) {
			throw new PortalException(
				"the-build-cannot-be-released-before-completion");
		}

		if (!Validator.isNumber(patcherBuild.getSupportTicket())) {
			throw new PortalException(
				"the-build-cannot-be-released-because-the-support-ticket-" +
					"does-not-point-to-zendesk");
		}
	}

	public void validateSmokeTest(PatcherBuild patcherBuild) throws Exception {
		if (patcherBuild.getStatus() !=
				WorkflowConstants.STATUS_BUILD_COMPLETE) {

			throw new PortalException(
				"the-build-cannot-be-tested-because-its-status-is-not-" +
					"complete");
		}

		if (Validator.isNull(patcherBuild.getFileName())) {
			throw new PortalException(
				"the-build-cannot-be-tested-because-its-filename-is-not-set");
		}
	}

	public void validateSupportTicket() throws PortalException {
		String supportTicket = ParamUtil.getString(
			_httpServletRequest, "supportTicket");

		if (Validator.isNull(supportTicket)) {
			throw new PortalException("the-support-ticket-is-invalid");
		}

		if (supportTicket.contains(StringPool.SPACE)) {
			throw new PortalException(
				"the-support-ticket-cannot-contain-spaces");
		}

		for (int i = 0; i < supportTicket.length(); i++) {
			if (!Validator.isAscii(supportTicket.charAt(i))) {
				throw new PortalException(
					"the-support-ticket-contains-non-ascii-characters");
			}
		}
	}

	public void validateTest(PatcherBuild patcherBuild) throws Exception {
		if (patcherBuild.getStatus() !=
				WorkflowConstants.STATUS_BUILD_COMPLETE) {

			throw new PortalException(
				"the-build-cannot-be-tested-because-its-status-is-not-" +
					"complete");
		}

		if (Validator.isNull(patcherBuild.getFileName())) {
			throw new PortalException(
				"the-build-cannot-be-tested-because-its-filename-is-not-set");
		}
	}

	public void validateType() throws Exception {
		int type = ParamUtil.getInteger(_httpServletRequest, "type");

		if ((type != PatcherBuildConstants.TYPE_DEBUG) &&
			(type != PatcherBuildConstants.TYPE_IGNORE) &&
			(type != PatcherBuildConstants.TYPE_OFFICIAL)) {

			throw new PortalException("the-type-is-invalid");
		}
	}

	public void validateUpdate(PatcherBuild patcherBuild) throws Exception {
		validatePatcherBuild(patcherBuild);

		PatcherConfiguration patcherConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				PatcherConfiguration.class, patcherBuild.getCompanyId());

		if (!patcherConfiguration.patcherScanningEnabled() &&
			!PatcherBuildUtil.isLatestPatcherBuild(patcherBuild)) {

			throw new PortalException(
				"the-build-cannot-be-versioned-because-the-current-build-is-" +
					"not-the-latest");
		}

		validateKey(patcherBuild);
		validateName(patcherBuild);
		validatePatcherFixPack(patcherBuild);

		validateSupportTicket();
		validateType();
	}

	public void validateUpdateCommentsField(PatcherBuild patcherBuild)
		throws Exception {

		validatePatcherBuild(patcherBuild);
	}

	public void validateUpdateQAFields(PatcherBuild patcherBuild)
		throws Exception {

		validatePatcherBuild(patcherBuild);
	}

	public void validateView(PatcherBuild patcherBuild) throws Exception {
		validatePatcherBuild(patcherBuild);
	}

	public void validateViewBuilds(PatcherBuild patcherBuild) throws Exception {
		validatePatcherBuild(patcherBuild);
	}

	public void validateViewFixes(PatcherBuild patcherBuild) throws Exception {
		validatePatcherBuild(patcherBuild);
	}

	public void validateViewMostRecent() throws Exception {
		int limit = ParamUtil.getInteger(_httpServletRequest, "limit");

		if (limit < 1) {
			throw new PortalException("the-limit-is-invalid");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PatcherBuildValidator.class);

	private static final Pattern _patcherFixPackNamePattern = Pattern.compile(
		PatcherConstants.FIX_PACKS_REGEX);
	private static final Pattern _patcherTicketName6xPattern = Pattern.compile(
		PatcherConstants.TICKET_NAME_6X_REGEX);
	private static final Pattern _patcherTicketNameAllPattern = Pattern.compile(
		PatcherConstants.TICKET_NAME_ALL_REGEX);

	private final HttpServletRequest _httpServletRequest;

}