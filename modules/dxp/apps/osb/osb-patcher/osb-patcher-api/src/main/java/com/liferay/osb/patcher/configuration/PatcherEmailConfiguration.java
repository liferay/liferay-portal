/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;

/**
 * @author Eudaldo Alonso
 */
@ExtendedObjectClassDefinition(
	generateUI = false, scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.osb.patcher.configuration.PatcherEmailConfiguration"
)
public interface PatcherEmailConfiguration {

	@Meta.AD(
		deflt = "${resource:com/liferay/osb/patcher/configuration/dependencies/email_patcher_build_complete_body.tmpl}",
		name = "email-patcher-build-complete-body", required = false
	)
	public LocalizedValuesMap emailPatcherBuildCompleteBody();

	@Meta.AD(
		deflt = "${resource:com/liferay/osb/patcher/configuration/dependencies/email_patcher_build_complete_subject.tmpl}",
		name = "email-patcher-build-complete-subject", required = false
	)
	public LocalizedValuesMap emailPatcherBuildCompleteSubject();

	@Meta.AD(
		deflt = "${resource:com/liferay/osb/patcher/configuration/dependencies/email_patcher_build_conflict_body.tmpl}",
		name = "email-patcher-build-conflict-body", required = false
	)
	public LocalizedValuesMap emailPatcherBuildConflictBody();

	@Meta.AD(
		deflt = "${resource:com/liferay/osb/patcher/configuration/dependencies/email_patcher_build_conflict_subject.tmpl}",
		name = "email-patcher-build-conflict-subject", required = false
	)
	public LocalizedValuesMap emailPatcherBuildConflictSubject();

	@Meta.AD(
		deflt = "${resource:com/liferay/osb/patcher/configuration/dependencies/email_patcher_build_failed_body.tmpl}",
		name = "email-patcher-build-failed-body", required = false
	)
	public LocalizedValuesMap emailPatcherBuildFailedBody();

	@Meta.AD(
		deflt = "${resource:com/liferay/osb/patcher/configuration/dependencies/email_patcher_build_failed_subject.tmpl}",
		name = "email-patcher-build-failed-subject", required = false
	)
	public LocalizedValuesMap emailPatcherBuildFailedSubject();

	@Meta.AD(
		deflt = "${resource:com/liferay/osb/patcher/configuration/dependencies/email_patcher_build_qa_analysis_needed_body.tmpl}",
		name = "email-patcher-build-qa-analysis-needed-body", required = false
	)
	public LocalizedValuesMap emailPatcherBuildQAAnalysisNeededBody();

	@Meta.AD(
		deflt = "${resource:com/liferay/osb/patcher/configuration/dependencies/email_patcher_build_qa_analysis_needed_subject.tmpl}",
		name = "email-patcher-build-qa-analysis-needed-subject",
		required = false
	)
	public LocalizedValuesMap emailPatcherBuildQAAnalysisNeededSubject();

	@Meta.AD(
		deflt = "${resource:com/liferay/osb/patcher/configuration/dependencies/email_patcher_build_qa_automation_passed_body.tmpl}",
		name = "email-patcher-build-qa-automation-passed-body", required = false
	)
	public LocalizedValuesMap emailPatcherBuildQAAutomationPassedBody();

	@Meta.AD(
		deflt = "${resource:com/liferay/osb/patcher/configuration/dependencies/email_patcher_build_qa_automation_passed_subject.tmpl}",
		name = "email-patcher-build-qa-automation-passed-subject",
		required = false
	)
	public LocalizedValuesMap emailPatcherBuildQAAutomationPassedSubject();

	@Meta.AD(
		deflt = "${resource:com/liferay/osb/patcher/configuration/dependencies/email_patcher_build_qa_failed_manually_body.tmpl}",
		name = "email-patcher-build-qa-failed-manually-body", required = false
	)
	public LocalizedValuesMap emailPatcherBuildQAFailedManuallyBody();

	@Meta.AD(
		deflt = "${resource:com/liferay/osb/patcher/configuration/dependencies/email_patcher_build_qa_failed_manually_subject.tmpl}",
		name = "email-patcher-build-qa-failed-manually-subject",
		required = false
	)
	public LocalizedValuesMap emailPatcherBuildQAFailedManuallySubject();

	@Meta.AD(
		deflt = "${resource:com/liferay/osb/patcher/configuration/dependencies/email_patcher_build_qa_passed_manually_body.tmpl}",
		name = "email-patcher-build-qa-passed-manually-body", required = false
	)
	public LocalizedValuesMap emailPatcherBuildQAPassedManuallyBody();

	@Meta.AD(
		deflt = "${resource:com/liferay/osb/patcher/configuration/dependencies/email_patcher_build_qa_passed_manually_subject.tmpl}",
		name = "email-patcher-build-qa-passed-manually-subject",
		required = false
	)
	public LocalizedValuesMap emailPatcherBuildQAPassedManuallySubject();

	@Meta.AD(
		deflt = "${resource:com/liferay/osb/patcher/configuration/dependencies/email_patcher_build_rebase_conflict_body.tmpl}",
		name = "email-patcher-build-rebase-conflict-body", required = false
	)
	public LocalizedValuesMap emailPatcherBuildRebaseConflictBody();

	@Meta.AD(
		deflt = "${resource:com/liferay/osb/patcher/configuration/dependencies/email_patcher_build_rebase_conflict_subject.tmpl}",
		name = "email-patcher-build-rebase-conflict-subject", required = false
	)
	public LocalizedValuesMap emailPatcherBuildRebaseConflictSubject();

	@Meta.AD(
		deflt = "${resource:com/liferay/osb/patcher/configuration/dependencies/email_patcher_build_timeout_body.tmpl}",
		name = "email-patcher-build-timeout-body", required = false
	)
	public LocalizedValuesMap emailPatcherBuildTimeoutBody();

	@Meta.AD(
		deflt = "${resource:com/liferay/osb/patcher/configuration/dependencies/email_patcher_build_timeout_subject.tmpl}",
		name = "email-patcher-build-timeout-subject", required = false
	)
	public LocalizedValuesMap emailPatcherBuildTimeoutSubject();

	@Meta.AD(
		deflt = "${resource:com/liferay/osb/patcher/configuration/dependencies/email_patcher_fix_complete_body.tmpl}",
		name = "email-patcher-fix-complete-body", required = false
	)
	public LocalizedValuesMap emailPatcherFixCompleteBody();

	@Meta.AD(
		deflt = "${resource:com/liferay/osb/patcher/configuration/dependencies/email_patcher_fix_complete_subject.tmpl}",
		name = "email-patcher-fix-complete-subject", required = false
	)
	public LocalizedValuesMap emailPatcherFixCompleteSubject();

	@Meta.AD(
		deflt = "${resource:com/liferay/osb/patcher/configuration/dependencies/email_patcher_fix_failed_body.tmpl}",
		name = "email-patcher-fix-failed-body", required = false
	)
	public LocalizedValuesMap emailPatcherFixFailedBody();

	@Meta.AD(
		deflt = "${resource:com/liferay/osb/patcher/configuration/dependencies/email_patcher_fix_failed_subject.tmpl}",
		name = "email-patcher-fix-failed-subject", required = false
	)
	public LocalizedValuesMap emailPatcherFixFailedSubject();

	@Meta.AD(
		deflt = "${resource:com/liferay/osb/patcher/configuration/dependencies/email_patcher_fix_timeout_body.tmpl}",
		name = "email-patcher-fix-timeout-body", required = false
	)
	public LocalizedValuesMap emailPatcherFixTimeoutBody();

	@Meta.AD(
		deflt = "${resource:com/liferay/osb/patcher/configuration/dependencies/email_patcher_fix_timeout_subject.tmpl}",
		name = "email-patcher-fix-timeout-subject", required = false
	)
	public LocalizedValuesMap emailPatcherFixTimeoutSubject();

	@Meta.AD(
		deflt = "${resource:com/liferay/osb/patcher/configuration/dependencies/email_qa_comments.tmpl}",
		name = "email-qa-comments", required = false
	)
	public LocalizedValuesMap emailQAComments();

}