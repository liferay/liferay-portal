/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.constants;

/**
 * @author Zsolt Balogh
 */
public class WorkflowConstants
	extends com.liferay.portal.kernel.workflow.WorkflowConstants {

	public static final String LABEL_BUILD_COMPILING = "compiling";

	public static final String LABEL_BUILD_COMPLETE = "complete";

	public static final String LABEL_BUILD_COMPLETE_MERGING_ONLY =
		"complete-merging-only";

	public static final String LABEL_BUILD_CONFLICT = "conflict";

	public static final String LABEL_BUILD_CONFLICT_MERGING_ONLY =
		"conflict-merging-only";

	public static final String LABEL_BUILD_FAILED = "failed";

	public static final String LABEL_BUILD_FAILED_MERGING_ONLY =
		"failed-merging-only";

	public static final String LABEL_BUILD_MERGING = "merging";

	public static final String LABEL_BUILD_MERGING_ONLY = "merging-only";

	public static final String LABEL_BUILD_QA_ANALYSIS_NEEDED =
		"qa-analysis-needed";

	public static final String LABEL_BUILD_QA_ANALYSIS_NEEDED_SMOKE_ONLY =
		"qa-analysis-needed-smoke-only";

	public static final String LABEL_BUILD_QA_ANALYSIS_STARTED =
		"qa-analysis-started";

	public static final String LABEL_BUILD_QA_ANALYSIS_STARTED_SMOKE_ONLY =
		"qa-analysis-started-smoke-only";

	public static final String LABEL_BUILD_QA_AUTOMATION_PASSED =
		"qa-automation-passed";

	public static final String LABEL_BUILD_QA_AUTOMATION_PASSED_SMOKE_ONLY =
		"qa-automation-passed-smoke-only";

	public static final String LABEL_BUILD_QA_AUTOMATION_STARTED =
		"qa-automation-started";

	public static final String LABEL_BUILD_QA_AUTOMATION_STARTED_SMOKE_ONLY =
		"qa-automation-started-smoke-only";

	public static final String LABEL_BUILD_QA_FAILED_MANUALLY =
		"qa-failed-manually";

	public static final String LABEL_BUILD_QA_FAILED_MANUALLY_SMOKE_ONLY =
		"qa-failed-manually-smoke-only";

	public static final String LABEL_BUILD_QA_PASSED_MANUALLY =
		"qa-passed-manually";

	public static final String LABEL_BUILD_QA_PASSED_MANUALLY_SMOKE_ONLY =
		"qa-passed-manually-smoke-only";

	public static final String LABEL_BUILD_QA_PENDING_SMOKE_ONLY =
		"qa-pending-smoke-only";

	public static final String LABEL_BUILD_QA_TESTING_SKIPPED =
		"qa-testing-skipped";

	public static final String LABEL_BUILD_QA_TESTING_SKIPPED_SMOKE_ONLY =
		"qa-testing-skipped-smoke-only";

	public static final String LABEL_BUILD_READY_TO_RELEASE =
		"ready-for-release";

	public static final String LABEL_BUILD_REBASE_CONFLICT = "rebase-conflict";

	public static final String LABEL_BUILD_REBASE_CONFLICT_MERGING_ONLY =
		"rebase-conflict-merging-only";

	public static final String LABEL_BUILD_REBASING = "rebasing";

	public static final String LABEL_BUILD_REBASING_MERGING_ONLY =
		"rebasing-merging-only";

	public static final String LABEL_BUILD_RELEASED = "released";

	public static final String LABEL_FIX_ADDING = "adding";

	public static final String LABEL_FIX_COMPLETE = "complete";

	public static final String LABEL_FIX_CONFLICT = "conflict";

	public static final String LABEL_FIX_FAILED = "failed";

	public static final String LABEL_FIX_FIX_PACK_GENERATED = "generated";

	public static final String LABEL_FIX_FIX_PACK_MULTI_COMPONENT =
		"multi-component";

	public static final String LABEL_FIX_FIX_PACK_NOT_COMPATIBLE =
		"not-compatible";

	public static final String LABEL_FIX_FIX_PACK_READY = "fix-pack-ready";

	public static final String LABEL_FIX_FIX_PACK_RESOLVED_BY_OTHER_FIXES =
		"resolved-by-other-fixes";

	public static final String LABEL_FIX_FIX_PACK_SINGLE_COMPONENT =
		"single-component";

	public static final String LABEL_FIX_FIX_PACK_UNKNOWN_COMPONENT =
		"unknown-component";

	public static final String LABEL_FIX_PACK_FROZEN = "frozen";

	public static final String LABEL_FIX_PACK_RELEASED = "released";

	public static final String LABEL_FIX_PACK_UNDER_DEVELOPMENT =
		"under-development";

	public static final String LABEL_FIX_REBASE_CONFLICT = "rebase-conflict";

	public static final String LABEL_FIX_REBASING = "rebasing";

	public static final int STATUS_BUILD_COMPILING = 202;

	public static final int STATUS_BUILD_COMPLETE = 200;

	public static final int STATUS_BUILD_COMPLETE_MERGING_ONLY = 207;

	public static final int STATUS_BUILD_CONFLICT = 208;

	public static final int STATUS_BUILD_CONFLICT_MERGING_ONLY = 209;

	public static final int STATUS_BUILD_FAILED = 205;

	public static final int STATUS_BUILD_FAILED_MERGING_ONLY = 217;

	public static final int STATUS_BUILD_MERGING = 201;

	public static final int STATUS_BUILD_MERGING_ONLY = 206;

	public static final int STATUS_BUILD_QA_ANALYSIS_NEEDED = 203;

	public static final int STATUS_BUILD_QA_ANALYSIS_NEEDED_SMOKE_ONLY = 211;

	public static final int STATUS_BUILD_QA_ANALYSIS_STARTED = 218;

	public static final int STATUS_BUILD_QA_ANALYSIS_STARTED_SMOKE_ONLY = 223;

	public static final int STATUS_BUILD_QA_AUTOMATION_PASSED = 204;

	public static final int STATUS_BUILD_QA_AUTOMATION_PASSED_SMOKE_ONLY = 212;

	public static final int STATUS_BUILD_QA_AUTOMATION_STARTED = 220;

	public static final int STATUS_BUILD_QA_AUTOMATION_STARTED_SMOKE_ONLY = 225;

	public static final int STATUS_BUILD_QA_FAILED_MANUALLY = 221;

	public static final int STATUS_BUILD_QA_FAILED_MANUALLY_SMOKE_ONLY = 222;

	public static final int STATUS_BUILD_QA_PASSED_MANUALLY = 219;

	public static final int STATUS_BUILD_QA_PASSED_MANUALLY_SMOKE_ONLY = 224;

	public static final int STATUS_BUILD_QA_PENDING_SMOKE_ONLY = 210;

	public static final int STATUS_BUILD_QA_TESTING_SKIPPED = 227;

	public static final int STATUS_BUILD_QA_TESTING_SKIPPED_SMOKE_ONLY = 228;

	public static final int STATUS_BUILD_READY_TO_RELEASE = 229;

	public static final int STATUS_BUILD_REBASE_CONFLICT = 213;

	public static final int STATUS_BUILD_REBASE_CONFLICT_MERGING_ONLY = 215;

	public static final int STATUS_BUILD_REBASING = 214;

	public static final int STATUS_BUILD_REBASING_MERGING_ONLY = 216;

	public static final int STATUS_BUILD_RELEASED = 226;

	public static final int STATUS_BUILD_TIMEOUT = 230;

	public static final int STATUS_FIX_ADDING = 101;

	public static final int STATUS_FIX_COMPLETE = 100;

	public static final int STATUS_FIX_CONFLICT = 102;

	public static final int STATUS_FIX_FAILED = 103;

	public static final int STATUS_FIX_FIX_PACK_GENERATED = 105;

	public static final int STATUS_FIX_FIX_PACK_MULTI_COMPONENT = 106;

	public static final int STATUS_FIX_FIX_PACK_NOT_COMPATIBLE = 107;

	public static final int STATUS_FIX_FIX_PACK_READY = 104;

	public static final int STATUS_FIX_FIX_PACK_RESOLVED_BY_OTHER_FIXES = 108;

	public static final int STATUS_FIX_FIX_PACK_SINGLE_COMPONENT = 109;

	public static final int STATUS_FIX_FIX_PACK_UNKNOWN_COMPONENT = 110;

	public static final int STATUS_FIX_PACK_FROZEN = 302;

	public static final int STATUS_FIX_PACK_RELEASED = 300;

	public static final int STATUS_FIX_PACK_UNDER_DEVELOPMENT = 301;

	public static final int STATUS_FIX_REBASE_CONFLICT = 111;

	public static final int STATUS_FIX_REBASING = 112;

	public static final int STATUS_FIX_TIMEOUT = 231;

	public static String getStatusLabel(int status) {
		if (status == STATUS_BUILD_COMPILING) {
			return LABEL_BUILD_COMPILING;
		}
		else if (status == STATUS_BUILD_COMPLETE) {
			return LABEL_BUILD_COMPLETE;
		}
		else if (status == STATUS_BUILD_COMPLETE_MERGING_ONLY) {
			return LABEL_BUILD_COMPLETE_MERGING_ONLY;
		}
		else if (status == STATUS_BUILD_CONFLICT) {
			return LABEL_BUILD_CONFLICT;
		}
		else if (status == STATUS_BUILD_CONFLICT_MERGING_ONLY) {
			return LABEL_BUILD_CONFLICT_MERGING_ONLY;
		}
		else if (status == STATUS_BUILD_FAILED) {
			return LABEL_BUILD_FAILED;
		}
		else if (status == STATUS_BUILD_FAILED_MERGING_ONLY) {
			return LABEL_BUILD_FAILED_MERGING_ONLY;
		}
		else if (status == STATUS_BUILD_MERGING) {
			return LABEL_BUILD_MERGING;
		}
		else if (status == STATUS_BUILD_MERGING_ONLY) {
			return LABEL_BUILD_MERGING_ONLY;
		}
		else if (status == STATUS_BUILD_QA_ANALYSIS_NEEDED) {
			return LABEL_BUILD_QA_ANALYSIS_NEEDED;
		}
		else if (status == STATUS_BUILD_QA_ANALYSIS_NEEDED_SMOKE_ONLY) {
			return LABEL_BUILD_QA_ANALYSIS_NEEDED_SMOKE_ONLY;
		}
		else if (status == STATUS_BUILD_QA_ANALYSIS_STARTED) {
			return LABEL_BUILD_QA_ANALYSIS_STARTED;
		}
		else if (status == STATUS_BUILD_QA_ANALYSIS_STARTED_SMOKE_ONLY) {
			return LABEL_BUILD_QA_ANALYSIS_STARTED_SMOKE_ONLY;
		}
		else if (status == STATUS_BUILD_QA_AUTOMATION_PASSED) {
			return LABEL_BUILD_QA_AUTOMATION_PASSED;
		}
		else if (status == STATUS_BUILD_QA_AUTOMATION_PASSED_SMOKE_ONLY) {
			return LABEL_BUILD_QA_AUTOMATION_PASSED_SMOKE_ONLY;
		}
		else if (status == STATUS_BUILD_QA_AUTOMATION_STARTED) {
			return LABEL_BUILD_QA_AUTOMATION_STARTED;
		}
		else if (status == STATUS_BUILD_QA_AUTOMATION_STARTED_SMOKE_ONLY) {
			return LABEL_BUILD_QA_AUTOMATION_STARTED_SMOKE_ONLY;
		}
		else if (status == STATUS_BUILD_QA_FAILED_MANUALLY) {
			return LABEL_BUILD_QA_FAILED_MANUALLY;
		}
		else if (status == STATUS_BUILD_QA_FAILED_MANUALLY_SMOKE_ONLY) {
			return LABEL_BUILD_QA_FAILED_MANUALLY_SMOKE_ONLY;
		}
		else if (status == STATUS_BUILD_QA_PASSED_MANUALLY) {
			return LABEL_BUILD_QA_PASSED_MANUALLY;
		}
		else if (status == STATUS_BUILD_QA_PASSED_MANUALLY_SMOKE_ONLY) {
			return LABEL_BUILD_QA_PASSED_MANUALLY_SMOKE_ONLY;
		}
		else if (status == STATUS_BUILD_QA_PENDING_SMOKE_ONLY) {
			return LABEL_BUILD_QA_PENDING_SMOKE_ONLY;
		}
		else if (status == STATUS_BUILD_QA_TESTING_SKIPPED) {
			return LABEL_BUILD_QA_TESTING_SKIPPED;
		}
		else if (status == STATUS_BUILD_QA_TESTING_SKIPPED_SMOKE_ONLY) {
			return LABEL_BUILD_QA_TESTING_SKIPPED_SMOKE_ONLY;
		}
		else if (status == STATUS_BUILD_READY_TO_RELEASE) {
			return LABEL_BUILD_READY_TO_RELEASE;
		}
		else if (status == STATUS_BUILD_REBASE_CONFLICT) {
			return LABEL_BUILD_REBASE_CONFLICT;
		}
		else if (status == STATUS_BUILD_REBASE_CONFLICT_MERGING_ONLY) {
			return LABEL_BUILD_REBASE_CONFLICT_MERGING_ONLY;
		}
		else if (status == STATUS_BUILD_REBASING) {
			return LABEL_BUILD_REBASING;
		}
		else if (status == STATUS_BUILD_REBASING_MERGING_ONLY) {
			return LABEL_BUILD_REBASING_MERGING_ONLY;
		}
		else if (status == STATUS_BUILD_RELEASED) {
			return LABEL_BUILD_RELEASED;
		}
		else if (status == STATUS_FIX_ADDING) {
			return LABEL_FIX_ADDING;
		}
		else if (status == STATUS_FIX_COMPLETE) {
			return LABEL_FIX_COMPLETE;
		}
		else if (status == STATUS_FIX_CONFLICT) {
			return LABEL_FIX_CONFLICT;
		}
		else if (status == STATUS_FIX_FAILED) {
			return LABEL_FIX_FAILED;
		}
		else if (status == STATUS_FIX_FIX_PACK_GENERATED) {
			return LABEL_FIX_FIX_PACK_GENERATED;
		}
		else if (status == STATUS_FIX_FIX_PACK_MULTI_COMPONENT) {
			return LABEL_FIX_FIX_PACK_MULTI_COMPONENT;
		}
		else if (status == STATUS_FIX_FIX_PACK_NOT_COMPATIBLE) {
			return LABEL_FIX_FIX_PACK_NOT_COMPATIBLE;
		}
		else if (status == STATUS_FIX_FIX_PACK_READY) {
			return LABEL_FIX_FIX_PACK_READY;
		}
		else if (status == STATUS_FIX_FIX_PACK_RESOLVED_BY_OTHER_FIXES) {
			return LABEL_FIX_FIX_PACK_RESOLVED_BY_OTHER_FIXES;
		}
		else if (status == STATUS_FIX_FIX_PACK_SINGLE_COMPONENT) {
			return LABEL_FIX_FIX_PACK_SINGLE_COMPONENT;
		}
		else if (status == STATUS_FIX_FIX_PACK_UNKNOWN_COMPONENT) {
			return LABEL_FIX_FIX_PACK_UNKNOWN_COMPONENT;
		}
		else if (status == STATUS_FIX_PACK_FROZEN) {
			return LABEL_FIX_PACK_FROZEN;
		}
		else if (status == STATUS_FIX_PACK_RELEASED) {
			return LABEL_FIX_PACK_RELEASED;
		}
		else if (status == STATUS_FIX_PACK_UNDER_DEVELOPMENT) {
			return LABEL_FIX_PACK_UNDER_DEVELOPMENT;
		}
		else if (status == STATUS_FIX_REBASE_CONFLICT) {
			return LABEL_FIX_REBASE_CONFLICT;
		}
		else if (status == STATUS_FIX_REBASING) {
			return LABEL_FIX_REBASING;
		}

		return com.liferay.portal.kernel.workflow.WorkflowConstants.
			getStatusLabel(status);
	}

}