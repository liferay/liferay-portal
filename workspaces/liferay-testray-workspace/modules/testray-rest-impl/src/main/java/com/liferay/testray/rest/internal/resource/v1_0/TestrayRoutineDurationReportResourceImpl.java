/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.testray.rest.internal.resource.v1_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.testray.rest.dto.v1_0.TestrayRoutineDurationReport;
import com.liferay.testray.rest.internal.util.TestrayUtil;
import com.liferay.testray.rest.manager.TestrayManager;
import com.liferay.testray.rest.resource.v1_0.TestrayRoutineDurationReportResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Nilton Vieira
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/testray-routine-duration-report.properties",
	scope = ServiceScope.PROTOTYPE,
	service = TestrayRoutineDurationReportResource.class
)
public class TestrayRoutineDurationReportResourceImpl
	extends BaseTestrayRoutineDurationReportResourceImpl {

	@Override
	public Page<TestrayRoutineDurationReport>
			getTestrayRoutineDurationReportsTestrayRoutinePage(
				Long testrayRoutineId, Boolean flaky, String priority,
				String testrayCaseName, String testrayCaseTypeIds,
				String testrayComponentIds, String testrayTeamIds,
				Pagination pagination)
		throws Exception {

		List<Object> params = new ArrayList<>();
		StringBundler sb = new StringBundler(36);

		sb.append("select c.c_caseid_ , c.flaky_ , c.name_ , c.priority_, ");
		sb.append("ct.name_ as casetypeName, co.name_ as componentName, ");
		sb.append("t.name_ as teamName, array_agg(cr.duestatus_ order by ");
		sb.append("cr.startdate_ asc) as results, array_agg(cr.duration_ ");
		sb.append("order by cr.startdate_ asc) as durations, ");
		sb.append("avg(cr.duration_) as avgduration from ");
		sb.append("o_[%COMPANY_ID%]_case c, o_[%COMPANY_ID%]_caseresult cr, ");
		sb.append("o_[%COMPANY_ID%]_casetype ct, o_[%COMPANY_ID%]_component ");
		sb.append("co, o_[%COMPANY_ID%]_team t where c.c_caseid_ = ");
		sb.append("cr.r_casetocaseresult_c_caseid and ");
		sb.append("c.r_casetypetocases_c_casetypeid = ct.c_casetypeid_ and ");
		sb.append("c.r_componenttocases_c_componentid = co.c_componentid_ ");
		sb.append("and cr.r_teamtocaseresult_c_teamid = t.c_teamid_ and ");
		sb.append("cr.r_buildtocaseresult_c_buildid in (select b.c_buildid_ ");
		sb.append("from o_[%COMPANY_ID%]_build b where ");
		sb.append("b.r_routinetobuilds_c_routineid in (");
		sb.append(
			TestrayUtil.interpolateParams(
				params,
				_testrayManager.getRelatedTestrayRoutineIds(
					contextCompany.getCompanyId(), testrayRoutineId)));
		sb.append(") order by b.duedate_ desc limit 20) and cr.duestatus_ != ");
		sb.append("'UNTESTED' ");

		if (flaky != null) {
			sb.append("and c.flaky_ = ? ");
			params.add(flaky);
		}

		if (Validator.isNotNull(priority)) {
			sb.append("and c.priority_ in (");
			sb.append(TestrayUtil.interpolateParams(params, priority));
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

		if (Validator.isNotNull(testrayTeamIds)) {
			sb.append("and co.r_teamToComponents_c_teamId in (");
			sb.append(TestrayUtil.interpolateParams(params, testrayTeamIds));
			sb.append(") ");
		}

		sb.append("group by c.c_caseid_, c.flaky_, c.priority_, c.name_, ");
		sb.append("ct.name_, co.name_, t.name_ order by avgduration desc, ");
		sb.append("c.priority_ desc, c.name_ asc ");

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
				value -> new TestrayRoutineDurationReport() {
					{
						testrayCaseFlaky = GetterUtil.getBoolean(
							String.valueOf(value.get("flaky_")));
						testrayCaseId = GetterUtil.getLong(
							value.get("c_caseid_"));
						testrayCaseName = GetterUtil.getString(
							value.get("name_"));
						testrayCasePriority = GetterUtil.getInteger(
							value.get("priority_"));
						testrayCaseResultAvgDuration = GetterUtil.getLong(
							value.get("avgduration"));
						testrayCaseResultDurations = unsafeTransform(
							StringUtil.split(
								StringUtil.removeChars(
									value.get(
										"durations"
									).toString(),
									'{', '}', '"')),
							Long::valueOf, Long.class);
						testrayCaseResultStatus = StringUtil.split(
							StringUtil.removeChars(
								value.get(
									"results"
								).toString(),
								'{', '}', '"'));
						testrayCaseTypeName = GetterUtil.getString(
							value.get("casetypename"));
						testrayComponentName = GetterUtil.getString(
							value.get("componentname"));
						testrayTeamName = GetterUtil.getString(
							value.get("teamname"));
					}
				}),
			pagination, totalCount);
	}

	@Reference
	private TestrayManager _testrayManager;

}