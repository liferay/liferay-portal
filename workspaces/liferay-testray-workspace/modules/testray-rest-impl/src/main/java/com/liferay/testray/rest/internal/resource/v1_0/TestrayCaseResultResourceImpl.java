/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.testray.rest.internal.resource.v1_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.auth.FullNameGenerator;
import com.liferay.portal.kernel.security.auth.FullNameGeneratorFactory;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.testray.rest.dto.v1_0.TestrayCaseResult;
import com.liferay.testray.rest.internal.util.TestrayUtil;
import com.liferay.testray.rest.resource.v1_0.TestrayCaseResultResource;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.net.URI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Nilton Vieira
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/testray-case-result.properties",
	scope = ServiceScope.PROTOTYPE, service = TestrayCaseResultResource.class
)
public class TestrayCaseResultResourceImpl
	extends BaseTestrayCaseResultResourceImpl {

	@Override
	public Page<TestrayCaseResult> getTestrayCaseResultHistoryTestrayCasePage(
			Long testrayCaseId, String error, String issues,
			String maxExecutionDate, String minExecutionDate, Boolean noError,
			Boolean noIssues, String status, String testrayProductVersionIds,
			String testrayRoutineIds, String testrayRunName,
			String testrayTeamIds, String testrayUserId, String warning,
			Pagination pagination)
		throws Exception {

		StringBundler sb = new StringBundler(43);

		sb.append("select b.c_buildId_, b.dueDate_ as executionDate, ");
		sb.append("b.gitHash_,  cr.c_caseResultId_, cr.dueStatus_,");
		sb.append("cr.duration_, cr.errors_, cr.issues_, cr.warnings_, ");
		sb.append("pv.name_ as productVersion, r.name_ as runName, ro.name_ ");
		sb.append("as routineName, ro.c_routineId_, t.name_ as teamName from ");
		sb.append("O_[%COMPANY_ID%]_Build b, O_[%COMPANY_ID%]_CaseResult cr, ");
		sb.append("O_[%COMPANY_ID%]_Case c, O_[%COMPANY_ID%]_CaseType ct, ");
		sb.append("O_[%COMPANY_ID%]_Component co, O_[%COMPANY_ID%]_Run r, ");
		sb.append("O_[%COMPANY_ID%]_Routine ro, ");
		sb.append("O_[%COMPANY_ID%]_ProductVersion pv, O_[%COMPANY_ID%]_Team ");
		sb.append("t where c.c_caseId_ = ? and ");
		sb.append("cr.r_buildToCaseResult_c_buildId = b.c_buildId_ and ");
		sb.append("c.c_caseId_ = cr.r_caseToCaseResult_c_caseId and ");
		sb.append("ct.c_caseTypeId_ = c.r_caseTypeToCases_c_caseTypeId and ");
		sb.append("co.c_componentId_ = ");
		sb.append("cr.r_componentToCaseResult_c_componentId and r.c_runId_ = ");
		sb.append("cr.r_runToCaseResult_c_runId and t.c_teamId_ = ");
		sb.append("co.r_teamToComponents_c_teamId and ro.c_routineId_ = ");
		sb.append("b.r_routineToBuilds_c_routineId and ");
		sb.append("pv.c_productVersionId_ =");
		sb.append("b.r_productVersionToBuilds_c_productVersionId ");

		List<Object> params = new ArrayList<>();

		params.add(testrayCaseId);

		if (Validator.isNotNull(error)) {
			sb.append("and cr.errors_ like ? ");
			params.add("%" + error + "%");
		}

		if (Validator.isNotNull(issues)) {
			sb.append("and cr.issues_ like ? ");
			params.add("%" + issues + "%");
		}

		if (Validator.isNotNull(maxExecutionDate)) {
			sb.append("and b.dueDate_ <= ?");
			params.add(maxExecutionDate);
		}

		if (Validator.isNotNull(minExecutionDate)) {
			sb.append("and b.dueDate_ >= ?");
			params.add(minExecutionDate);
		}

		if (noError != null) {
			sb.append("and (cr.errors_ is null or cr.errors_ = '') ");
		}

		if (noIssues != null) {
			sb.append("and (cr.issues_ is null or cr.issues_ = '') ");
		}

		if (Validator.isNotNull(status)) {
			sb.append("and cr.dueStatus_ in (");
			sb.append(TestrayUtil.interpolateParams(params, status));
			sb.append(") ");
		}

		if (Validator.isNotNull(testrayProductVersionIds)) {
			sb.append("and pv.c_productVersionId_ in (");
			sb.append(
				TestrayUtil.interpolateParams(
					params, testrayProductVersionIds));
			sb.append(") ");
		}

		if (Validator.isNotNull(testrayRoutineIds)) {
			sb.append("and ro.c_routineId_ in (");
			sb.append(TestrayUtil.interpolateParams(params, testrayRoutineIds));
			sb.append(") ");
		}

		if (Validator.isNotNull(testrayRunName)) {
			sb.append("and r.name_ like ? ");
			params.add("%" + testrayRunName + "%");
		}

		if (Validator.isNotNull(testrayTeamIds)) {
			sb.append("and co.r_teamToComponents_c_teamId in (");
			sb.append(TestrayUtil.interpolateParams(params, testrayTeamIds));
			sb.append(") ");
		}

		if (Validator.isNotNull(testrayUserId)) {
			sb.append("and cr.r_userToCaseResults_userId  = ? ");
			params.add(GetterUtil.getLong(testrayUserId));
		}

		if (Validator.isNotNull(warning)) {
			sb.append("and cr.warnings_  = ? ");
			params.add(warning);
		}

		sb.append("order by b.dueDate_ desc ");

		String sql = StringUtil.replace(
			sb.toString(), "[%COMPANY_ID%]",
			String.valueOf(contextCompany.getCompanyId()));

		long totalCount = TestrayUtil.getTotalCount(sql, params);

		if (pagination != null) {
			sql += " limit ? offset ?";

			params.add(pagination.getPageSize());
			params.add(pagination.getStartPosition());
		}

		List<Map<String, Object>> values = TestrayUtil.executeQuery(
			sql, params);

		return Page.of(
			transform(
				values,
				value -> new TestrayCaseResult() {
					{
						error = GetterUtil.getString(value.get("errors_"));
						duration = GetterUtil.getLong(value.get("duration_"));
						gitHash = GetterUtil.getString(value.get("githash_"));
						issues = GetterUtil.getString(value.get("issues_"));
						status = GetterUtil.getString(value.get("duestatus_"));
						testrayBuildId = GetterUtil.getLong(
							value.get("c_buildid_"));
						testrayCaseResultId = GetterUtil.getLong(
							value.get("c_caseresultid_"));
						testrayProductVersionName = GetterUtil.getString(
							value.get("productversion"));
						testrayRoutineId = GetterUtil.getLong(
							value.get("c_routineid_"));
						testrayRoutineName = GetterUtil.getString(
							value.get("routinename"));
						testrayRunName = GetterUtil.getString(
							value.get("runname"));
						testrayTeamName = GetterUtil.getString(
							value.get("teamname"));
						userName = GetterUtil.getString(value.get("name"));
						warning = GetterUtil.getInteger(value.get("warnings_"));

						setExecutionDate(
							() -> {
								if (value.get("executiondate") == null) {
									return null;
								}

								return value.get(
									"executiondate"
								).toString();
							});
					}
				}),
			pagination, totalCount);
	}

	@Override
	public Page<TestrayCaseResult> getTestrayCaseResultsTestrayBuildPage(
			Long testrayBuildId, String comment, String error, Boolean flaky,
			String issues, Boolean noComment, Boolean noError, Boolean noIssues,
			String priority, String status, String testrayCaseName,
			String testrayCaseTypeIds, String testrayComponentIds,
			String testrayRunId, String testrayRunName, String testraySubtaskId,
			String testrayTeamIds, String testrayUserId, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		StringBundler sb = new StringBundler(52);

		sb.append("select cr.c_caseResultId_, cr.comment_, cr.duration_, ");
		sb.append("cr.dueStatus_, cr.errors_, cr.issues_, ct.name_ as ");
		sb.append("caseTypeName, c.name_ as caseName, c.priority_, c.flaky_, ");
		sb.append("r.name_ as runName, r.number_ as runNumber, co.name_ as ");
		sb.append("componentName, t.name_ as teamName, u.firstName, ");
		sb.append("u.lastName, u.middleName, u.uuid_, u.portraitId from ");
		sb.append("O_[%COMPANY_ID%]_Build b, O_[%COMPANY_ID%]_CaseResult cr ");
		sb.append("left outer join User_ u on u.userId = ");
		sb.append("cr.r_userToCaseResults_userId, O_[%COMPANY_ID%]_Case c, ");
		sb.append("O_[%COMPANY_ID%]_CaseType ct, O_[%COMPANY_ID%]_Component ");
		sb.append("co, O_[%COMPANY_ID%]_Run r, O_[%COMPANY_ID%]_Team t where ");
		sb.append("b.c_buildId_ = ? and cr.r_buildToCaseResult_c_buildId = ");
		sb.append("b.c_buildId_ and c.c_caseId_ = ");
		sb.append("cr.r_caseToCaseResult_c_caseId and ct.c_caseTypeId_ = ");
		sb.append("c.r_caseTypeToCases_c_caseTypeId and co.c_componentId_ = ");
		sb.append("cr.r_componentToCaseResult_c_componentId and r.c_runId_ = ");
		sb.append("cr.r_runToCaseResult_c_runId and t.c_teamId_ = ");
		sb.append("co.r_teamToComponents_c_teamId ");

		List<Object> params = new ArrayList<>();

		params.add(testrayBuildId);

		if (Validator.isNotNull(comment)) {
			sb.append("and cr.comment_ like ? ");
			params.add("%" + comment + "%");
		}

		if (Validator.isNotNull(error)) {
			sb.append("and cr.errors_ like ? ");
			params.add("%" + error + "%");
		}

		if (flaky != null) {
			sb.append("and c.flaky_ = ? ");
			params.add(flaky);
		}

		if (Validator.isNotNull(issues)) {
			sb.append("and cr.issues_ like ? ");
			params.add("%" + issues + "%");
		}

		if (noComment != null) {
			sb.append("and (cr.comment_ is null or cr.comment_ = '') ");
		}

		if (noError != null) {
			sb.append("and (cr.errors_ is null or cr.errors_ = '') ");
		}

		if (noIssues != null) {
			sb.append("and (cr.issues_ is null or cr.issues_ = '') ");
		}

		if (Validator.isNotNull(priority)) {
			sb.append("and c.priority_ in (");
			sb.append(TestrayUtil.interpolateParams(params, priority));
			sb.append(") ");
		}

		if (Validator.isNotNull(status)) {
			sb.append("and cr.dueStatus_ in (");
			sb.append(TestrayUtil.interpolateParams(params, status));
			sb.append(") ");
		}

		if (Validator.isNotNull(testrayCaseName)) {
			sb.append("and c.name_ like ? ");
			params.add("%" + testrayCaseName + "%");
		}

		if (Validator.isNotNull(testrayCaseTypeIds)) {
			sb.append("and c.r_caseTypeToCases_c_caseTypeId in (");
			sb.append(
				TestrayUtil.interpolateParams(params, testrayCaseTypeIds));
			sb.append(") ");
		}

		if (Validator.isNotNull(testrayComponentIds)) {
			sb.append("and cr.r_componentToCaseResult_c_componentId in (");
			sb.append(
				TestrayUtil.interpolateParams(params, testrayComponentIds));
			sb.append(") ");
		}

		if (Validator.isNotNull(testrayRunId)) {
			sb.append("and cr.r_runToCaseResult_c_runId = ? ");
			params.add(GetterUtil.getLong(testrayRunId));
		}

		if (Validator.isNotNull(testrayRunName)) {
			sb.append("and r.name_ like ? ");
			params.add("%" + testrayRunName + "%");
		}

		if (Validator.isNotNull(testraySubtaskId)) {
			sb.append("and cr.r_subtaskToCaseResults_c_subtaskId = ? ");
			params.add(GetterUtil.getLong(testraySubtaskId));
		}

		if (Validator.isNotNull(testrayTeamIds)) {
			sb.append("and co.r_teamToComponents_c_teamId in (");
			sb.append(TestrayUtil.interpolateParams(params, testrayTeamIds));
			sb.append(") ");
		}

		if (Validator.isNotNull(testrayUserId)) {
			sb.append("and cr.r_userToCaseResults_userId  = ? ");
			params.add(GetterUtil.getLong(testrayUserId));
		}

		sb.append("order by ");

		if (sorts != null) {
			sb.append("cr.duration_ ");

			if (sorts[0].isReverse()) {
				sb.append("desc");
			}

			sb.append(", ");
		}

		sb.append("cr.dueStatus_ asc, cr.errors_ is null asc,");
		sb.append("c.priority_ desc, t.name_ asc, co.name_ asc, ct.name_ ");
		sb.append("asc, c.name_ , r.number_");

		String sql = StringUtil.replace(
			sb.toString(), "[%COMPANY_ID%]",
			String.valueOf(contextCompany.getCompanyId()));

		long totalCount = TestrayUtil.getTotalCount(sql, params);

		if (pagination != null) {
			sql += " limit ? offset ?";

			params.add(pagination.getPageSize());
			params.add(pagination.getStartPosition());
		}

		List<Map<String, Object>> values = TestrayUtil.executeQuery(
			sql, params);

		return Page.of(
			transform(
				values,
				value -> new TestrayCaseResult() {
					{
						comment = GetterUtil.getString(value.get("comment_"));
						duration = GetterUtil.getLong(value.get("duration_"));
						error = GetterUtil.getString(value.get("errors_"));
						flaky = GetterUtil.getBoolean(
							String.valueOf(value.get("flaky_")));
						issues = GetterUtil.getString(value.get("issues_"));
						priority = GetterUtil.getLong(value.get("priority_"));
						status = GetterUtil.getString(value.get("duestatus_"));
						testrayCaseName = GetterUtil.getString(
							value.get("casename"));
						testrayCaseResultId = GetterUtil.getLong(
							value.get("c_caseresultid_"));
						testrayCaseTypeName = GetterUtil.getString(
							value.get("casetypename"));
						testrayComponentName = GetterUtil.getString(
							value.get("componentname"));
						testrayRunName = GetterUtil.getString(
							value.get("runname"));
						testrayRunNumber = GetterUtil.getLong(
							value.get("runnumber"));
						testrayTeamName = GetterUtil.getString(
							value.get("teamname"));

						setUserName(
							() -> {
								FullNameGenerator fullNameGenerator =
									FullNameGeneratorFactory.getInstance();

								return fullNameGenerator.getFullName(
									GetterUtil.getString(
										value.get("firstname")),
									GetterUtil.getString(
										value.get("middlename")),
									GetterUtil.getString(
										value.get("lastname")));
							});
						setUserPortraitUrl(
							() -> {
								long portraitId = GetterUtil.getLong(
									value.get("portraitid"));

								if (portraitId == 0) {
									return null;
								}

								return UserConstants.getPortraitURL(
									"/image", true, portraitId,
									GetterUtil.getString(value.get("uuid_")));
							});
					}
				}),
			pagination, totalCount);
	}

	@Override
	public Response getTestrayExportCaseResultTestrayBuild(
		Long testrayBuildId) {

		return Response.ok(
			new StreamingOutput() {

				@Override
				public void write(OutputStream outputStream)
					throws IOException {

					_write(outputStream, testrayBuildId);
				}

			}
		).header(
			"Content-Disposition", "attachment; filename=\"case_results.csv\""
		).build();
	}

	private void _write(OutputStream outputStream, long testrayBuildId)
		throws IOException {

		try (CSVPrinter csvPrinter = new CSVPrinter(
				new BufferedWriter(new OutputStreamWriter(outputStream)),
				CSVFormat.DEFAULT.builder(
				).setHeader(
					"Case Name", "Case Type", "Priority", "Team", "Component",
					"Run Number", "Run Name", "Duration", "Assignee", "Status",
					"Issues", "Errors", "Comments", "Case Result URL"
				).build())) {

			Page<TestrayCaseResult> page =
				getTestrayCaseResultsTestrayBuildPage(
					testrayBuildId, null, null, null, null, null, null, null,
					null, null, null, null, null, null, null, null, null, null,
					null, null);

			for (TestrayCaseResult testrayCaseResult : page.getItems()) {
				URI uri = contextUriInfo.getBaseUri();

				csvPrinter.printRecord(
					testrayCaseResult.getTestrayCaseName(),
					testrayCaseResult.getTestrayCaseTypeName(),
					testrayCaseResult.getPriority(),
					testrayCaseResult.getTestrayTeamName(),
					testrayCaseResult.getTestrayComponentName(),
					testrayCaseResult.getTestrayRunNumber(),
					testrayCaseResult.getTestrayRunName(),
					testrayCaseResult.getDuration(),
					testrayCaseResult.getUserName(),
					testrayCaseResult.getStatus(),
					testrayCaseResult.getIssues(), testrayCaseResult.getError(),
					testrayCaseResult.getComment(),
					uri.getScheme() + "://" + uri.getAuthority() +
						"/#/case-result/" +
							testrayCaseResult.getTestrayCaseResultId());
			}

			csvPrinter.flush();
		}
		catch (Exception exception) {
			throw new IOException(exception);
		}
	}

}