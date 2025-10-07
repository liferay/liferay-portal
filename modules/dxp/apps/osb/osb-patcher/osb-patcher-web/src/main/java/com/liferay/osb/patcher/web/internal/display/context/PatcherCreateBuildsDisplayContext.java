/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.display.context;

import com.liferay.osb.patcher.constants.PatcherBuildConstants;
import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.service.PatcherAccountLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherBuildLocalServiceUtil;
import com.liferay.osb.patcher.util.PatcherBuildUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class PatcherCreateBuildsDisplayContext {

	public PatcherCreateBuildsDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;
	}

	public String getAccountEntryCode() throws Exception {
		PatcherBuild templatePatcherBuild = _getTemplatePatcherBuild();

		if (templatePatcherBuild != null) {
			PatcherAccount patcherAccount =
				PatcherAccountLocalServiceUtil.getPatcherAccount(
					templatePatcherBuild.getPatcherAccountId());

			return patcherAccount.getAccountEntryCode();
		}

		return StringPool.BLANK;
	}

	public PatcherBuild getPatcherBuild() throws Exception {
		PatcherBuild patcherBuild =
			PatcherBuildLocalServiceUtil.createPatcherBuild(0);

		patcherBuild.setKeyVersion(PatcherBuildConstants.KEY_VERSION_DEFAULT);

		PatcherBuild templatePatcherBuild = _getTemplatePatcherBuild();

		if (templatePatcherBuild != null) {
			patcherBuild.setAccountEntryCode(getAccountEntryCode());
			patcherBuild.setPatcherAccountId(
				templatePatcherBuild.getPatcherAccountId());
			patcherBuild.setPatcherProductVersionId(
				templatePatcherBuild.getPatcherProductVersionId());
			patcherBuild.setPatcherProjectVersionId(
				templatePatcherBuild.getPatcherProjectVersionId());
			patcherBuild.setName(templatePatcherBuild.getName());
			patcherBuild.setType(templatePatcherBuild.getType());
		}

		if (patcherBuild.getType() != PatcherBuildConstants.TYPE_DEBUG) {
			patcherBuild.setType(PatcherBuildConstants.TYPE_OFFICIAL);
		}

		return patcherBuild;
	}

	public long getPatcherProductVersionId() {
		if (_patcherProductVersionId != null) {
			return _patcherProductVersionId;
		}

		_patcherProductVersionId = ParamUtil.getLong(
			_httpServletRequest, "patcherProductVersionId");

		return _patcherProductVersionId;
	}

	public long getPatcherProjectVersionId() {
		PatcherBuild templatePatcherBuild = _getTemplatePatcherBuild();

		if (templatePatcherBuild != null) {
			return templatePatcherBuild.getPatcherProjectVersionId();
		}

		return 0;
	}

	public String getRedirect() {
		if (_redirect != null) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		return _redirect;
	}

	public boolean isMergeOnly() {
		PatcherBuild templatePatcherBuild = _getTemplatePatcherBuild();

		if (templatePatcherBuild != null) {
			return PatcherBuildUtil.isMergeOnly(templatePatcherBuild);
		}

		return false;
	}

	private PatcherBuild _getTemplatePatcherBuild() {
		if (_templatePatcherBuild != null) {
			return _templatePatcherBuild;
		}

		long templatePatcherBuildId = ParamUtil.getLong(
			_httpServletRequest, "templatePatcherBuildId");

		_templatePatcherBuild = PatcherBuildLocalServiceUtil.fetchPatcherBuild(
			templatePatcherBuildId);

		return _templatePatcherBuild;
	}

	private final HttpServletRequest _httpServletRequest;
	private Long _patcherProductVersionId;
	private String _redirect;
	private PatcherBuild _templatePatcherBuild;

}