/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Eudaldo Alonso
 */
@ExtendedObjectClassDefinition(
	generateUI = false, scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.osb.patcher.configuration.PatcherConfiguration",
	localization = "content/Language"
)
public interface PatcherConfiguration {

	@Meta.AD(deflt = "", required = false)
	public String githubURL();

	@Meta.AD(deflt = "", required = false)
	public String helpCenterGetAccountApiEndpoint();

	@Meta.AD(deflt = "", required = false)
	public String helpCenterTicketAttachmentApiEndpoint();

	@Meta.AD(deflt = "", required = false)
	public String helpCenterTokenTicketDir();

	@Meta.AD(deflt = "", required = false)
	public String helpCenterURL();

	@Meta.AD(deflt = "", required = false)
	public String helpCenterFileRepoId();

	@Meta.AD(deflt = "", required = false)
	public String helpCenterFileRepoURL();

	@Meta.AD(deflt = "", required = false)
	public String helpCenterJsonwsURL();

	@Meta.AD(deflt = "", required = false)
	public String helpCenterApiPassword();

	@Meta.AD(deflt = "", required = false)
	public String helpCenterApiUserName();

	@Meta.AD(deflt = "", required = false)
	public String hotfixMountPath();

	@Meta.AD(deflt = "", required = false)
	public String infoModifyTicketsListURL();

	@Meta.AD(deflt = "", required = false)
	public String jenkinsToken();

	@Meta.AD(deflt = "", required = false)
	public String jenkinsAdminUserName();

	@Meta.AD(deflt = "", required = false)
	public String jenkinsAdminUserToken();

	@Meta.AD(deflt = "", required = false)
	public String jenkinsBuildWithParametersPath();

	@Meta.AD(deflt = "", required = false)
	public String jenkinsLoadBalancerBaseInvocationURL();

	@Meta.AD(deflt = "", required = false)
	public String jenkinsURL();

	@Meta.AD(deflt = "", required = false)
	public boolean jenkinsLoadBalancerEnabled();

	@Meta.AD(deflt = "", required = false)
	public String jiraURL();

	@Meta.AD(deflt = "", required = false)
	public String lesaURL();

	@Meta.AD(deflt = "", required = false)
	public String liferayUsersProfileURL();

	@Meta.AD(deflt = "", required = false)
	public String[] patcherAccountWhitelist();

	@Meta.AD(deflt = "", required = false)
	public String patcherAgentJenkinsURL();

	@Meta.AD(deflt = "", required = false)
	public String patcherBuildDownloadURL();

	@Meta.AD(deflt = "", required = false)
	public String patcherGitTagPrefix();

	@Meta.AD(deflt = "", required = false)
	public boolean patcherJenkinsRequestsEnabled();

	@Meta.AD(deflt = "", required = false)
	public String patcherLiferayPortalRepository();

	@Meta.AD(deflt = "", required = false)
	public String patcherPubsubCredentialFilePath();

	@Meta.AD(deflt = "", required = false)
	public String patcherPubsubProjectId();

	@Meta.AD(deflt = "", required = false)
	public String patcherPubsubSubscriptionId();

	@Meta.AD(deflt = "", required = false)
	public String patcherSharedRequestAddFixPatcherType();

	@Meta.AD(deflt = "", required = false)
	public String patcherSharedRequestBuildPatchPatcherType();

	@Meta.AD(deflt = "", required = false)
	public boolean patcherScanningEnabled();

	@Meta.AD(deflt = "", required = false)
	public String patcherStatusBuildJenkinsPath();

	@Meta.AD(deflt = "", required = false)
	public String patcherStatusBuildJenkinsTestPath();

	@Meta.AD(deflt = "", required = false)
	public String patcherStatusBuildPath();

	@Meta.AD(deflt = "", required = false)
	public String patcherStatusFixPath();

	@Meta.AD(deflt = "", required = false)
	public String patcherStatusPath();

	@Meta.AD(deflt = "", required = false)
	public boolean patcherTestsEnabled();

	@Meta.AD(deflt = "", required = false)
	public String troubleshootingURL();

}