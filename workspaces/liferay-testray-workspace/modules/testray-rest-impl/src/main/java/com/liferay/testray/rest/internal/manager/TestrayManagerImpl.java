/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.testray.rest.internal.manager;

import com.liferay.headless.commerce.core.util.ServiceContextHelper;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryModel;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProviderUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.aggregation.Facet;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.testray.rest.dto.v1_0.TestrayCache;
import com.liferay.testray.rest.internal.util.TestrayUtil;
import com.liferay.testray.rest.manager.TestrayManager;

import java.io.File;
import java.io.Serializable;

import java.nio.file.Files;
import java.nio.file.Path;

import java.sql.Timestamp;

import java.time.OffsetDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author José Abelenda
 */
@Component(service = TestrayManager.class)
public class TestrayManagerImpl implements TestrayManager {

	@Override
	public int autofillTestrayBuilds(
			long companyId, long testrayBuildId1, long testrayBuildId2,
			long userId)
		throws Exception {

		int caseAmount = 0;

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				companyId, "C_CaseResult");

		Map<Long, List<Map<String, Serializable>>>
			testrayCaseResultsGroupedByTestrayCase1 =
				_getTestrayCaseResultsByTestrayBuildGroupedByTestrayCase(
					companyId, objectDefinition, testrayBuildId1, userId);
		Map<Long, List<Map<String, Serializable>>>
			testrayCaseResultsGroupedByTestrayCase2 =
				_getTestrayCaseResultsByTestrayBuildGroupedByTestrayCase(
					companyId, objectDefinition, testrayBuildId2, userId);

		for (Map.Entry<Long, List<Map<String, Serializable>>> entry :
				testrayCaseResultsGroupedByTestrayCase1.entrySet()) {

			List<Map<String, Serializable>> testrayCaseResults2 =
				testrayCaseResultsGroupedByTestrayCase2.get(entry.getKey());

			if (testrayCaseResults2 == null) {
				continue;
			}

			List<Map<String, Serializable>> testrayCaseResults1 =
				entry.getValue();

			for (Map<String, Serializable> testrayCaseResult1 :
					testrayCaseResults1) {

				for (Map<String, Serializable> testrayCaseResult2 :
						testrayCaseResults2) {

					if (!Objects.equals(
							String.valueOf(testrayCaseResult1.get("errors")),
							String.valueOf(testrayCaseResult2.get("errors")))) {

						continue;
					}

					ObjectEntry objectEntry = _autofillTestrayCaseResult(
						testrayCaseResult1, testrayCaseResult2, userId);

					if (objectEntry != null) {
						caseAmount++;
					}
				}
			}
		}

		if (caseAmount != 0) {
			updateTestrayBuildSummary(companyId, testrayBuildId1, userId);
			updateTestrayBuildSummary(companyId, testrayBuildId2, userId);
		}

		return caseAmount;
	}

	@Override
	public int createTestraySubtasks(
			long companyId, long testrayBuildId, long testrayTaskId,
			long userId)
		throws Exception {

		StringBundler sb = new StringBundler(9);

		sb.append("select cr.errors_ , sum(c.priority_) as score from ");
		sb.append("O_[%COMPANY_ID%]_CaseResult cr, O_[%COMPANY_ID%]_Case c ");
		sb.append("where cr.errors_ is not null and cr.errors_ != '' and ");
		sb.append("cr.r_caseToCaseResult_c_caseId = c.c_caseId_ and ");
		sb.append("cr.r_buildToCaseResult_c_buildId = ? group by cr.errors_ ");
		sb.append("order by score desc");

		List<Map<String, Object>> values = TestrayUtil.executeQuery(
			StringUtil.replace(
				sb.toString(), "[%COMPANY_ID%]", String.valueOf(companyId)),
			ListUtil.fromArray(GetterUtil.getLong(testrayBuildId)));

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				companyId, "C_Subtask");
		int testraySubtasksAmount = 0;

		for (Map<String, Object> value : values) {
			testraySubtasksAmount++;

			ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
				userId, 0, objectDefinition.getObjectDefinitionId(),
				HashMapBuilder.<String, Serializable>put(
					"dueStatus", "OPEN"
				).put(
					"errors", String.valueOf(value.get("errors_"))
				).put(
					"name", "ST-" + testraySubtasksAmount
				).put(
					"number", testraySubtasksAmount
				).put(
					"r_taskToSubtasks_c_taskId", testrayTaskId
				).put(
					"score", String.valueOf(value.get("score"))
				).build(),
				_serviceContextHelper.getServiceContext());

			sb = new StringBundler();

			sb.append("update O_[%COMPANY_ID%]_CaseResult set ");
			sb.append("r_subtaskToCaseResults_c_subtaskId = ? where ");
			sb.append("r_buildToCaseResult_c_buildId = ? and errors_ = ?");

			TestrayUtil.executeUpdate(
				StringUtil.replace(
					sb.toString(), "[%COMPANY_ID%]", String.valueOf(companyId)),
				ListUtil.fromArray(
					objectEntry.getObjectEntryId(),
					GetterUtil.getLong(testrayBuildId),
					String.valueOf(value.get("errors_"))));
		}

		return testraySubtasksAmount;
	}

	@Override
	public Map<String, Object> fetchTestrayCaseFlakyParameters(
			long companyId, OffsetDateTime offsetDateTime, long testrayCaseId)
		throws Exception {

		StringBundler sb = new StringBundler(15);

		sb.append("select cr.r_caseToCaseResult_c_caseId, sum(case when ");
		sb.append("cr.previousStatus != cr.dueStatus_ and cr.previousStatus ");
		sb.append("is not null then 1 end) as totalChanges, ");
		sb.append("count(c_caseResultId_) as totalCases from (select ");
		sb.append("c_caseResultId_, r_caseToCaseResult_c_caseId, dueStatus_, ");
		sb.append("lag(dueStatus_) over (partition by ");
		sb.append("r_caseToCaseResult_c_caseId order by r.name_, ");
		sb.append("crr.c_caseResultId_) previousStatus from ");
		sb.append("O_[%COMPANY_ID%]_CaseResult crr, O_[%COMPANY_ID%]_Run r ");
		sb.append("where crr.r_runtocaseresult_c_runid = r.c_runId_ and ");
		sb.append("crr.r_caseToCaseResult_c_caseId = ? and (crr.dueStatus_ = ");
		sb.append("'PASSED' or crr.dueStatus_ ='FAILED') and crr.startDate_ ");
		sb.append("is not null and crr.startDate_ >= ? order by r.name_, ");
		sb.append("crr.c_caseResultId_) as cr group by ");
		sb.append("r_caseToCaseResult_c_caseId");

		List<Map<String, Object>> values = TestrayUtil.executeQuery(
			StringUtil.replace(
				sb.toString(), "[%COMPANY_ID%]", String.valueOf(companyId)),
			ListUtil.fromArray(
				GetterUtil.getLong(testrayCaseId),
				Timestamp.valueOf(offsetDateTime.toLocalDateTime())));

		if (values.isEmpty()) {
			return null;
		}

		return values.get(0);
	}

	public Long[] getRelatedTestrayRoutineIds(
			long companyId, long testrayRoutineId)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				companyId, "C_Routine");

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.
				fetchObjectRelationshipByObjectDefinitionId1(
					objectDefinition.getObjectDefinitionId(), "parentRoutines");

		List<ObjectEntry> objectEntries =
			_objectEntryLocalService.getManyToManyObjectEntries(
				0, objectRelationship.getObjectRelationshipId(),
				testrayRoutineId, true, false, null, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		if (objectEntries.isEmpty()) {
			return new Long[] {testrayRoutineId};
		}

		List<Long> objectEntryIds = TransformUtil.transform(
			objectEntries, ObjectEntryModel::getObjectEntryId);

		objectEntryIds.add(testrayRoutineId);

		return objectEntryIds.toArray(new Long[0]);
	}

	@Override
	public void loadTestrayCache(
			long companyId, TestrayCache testrayCache, long userId)
		throws Exception {

		_loadObjectDefinitions(companyId, testrayCache);

		_loadTestrayProjects(companyId, testrayCache, userId);
	}

	@Override
	public void processArchive(
			long companyId, byte[] bytes, String fileName,
			ServiceContext serviceContext, long userId)
		throws Exception {

		long startTime = System.currentTimeMillis();

		String dueStatus = "successful";
		Path tempDirectoryPath = null;
		Path tempFilePath = null;

		TestrayCache testrayCache = new TestrayCache();

		loadTestrayCache(companyId, testrayCache, userId);

		try {
			tempDirectoryPath = Files.createTempDirectory(null);

			tempFilePath = Files.createTempFile(null, null);

			Files.write(tempFilePath, bytes);

			Archiver archiver = ArchiverFactory.createArchiver("tar");

			File tempDirectoryFile = tempDirectoryPath.toFile();

			archiver.extract(tempFilePath.toFile(), tempDirectoryFile);

			DocumentBuilderFactory documentBuilderFactory =
				SecureXMLFactoryProviderUtil.newDocumentBuilderFactory();

			DocumentBuilder documentBuilder =
				documentBuilderFactory.newDocumentBuilder();

			for (File file : tempDirectoryFile.listFiles()) {
				if (_log.isInfoEnabled()) {
					_log.info("Processing " + file.getName());
				}

				try {
					Document document = documentBuilder.parse(file);

					processDocument(
						companyId, document, null, 0, serviceContext,
						testrayCache, userId);
				}
				catch (Exception exception) {
					dueStatus = "failed";

					_log.error(exception);
				}
				finally {
					file.delete();
				}
			}
		}
		catch (Exception exception) {
			dueStatus = "failed";

			_log.error(exception);
		}
		finally {
			if (tempDirectoryPath != null) {
				Files.deleteIfExists(tempDirectoryPath);
			}

			if (tempFilePath != null) {
				Files.deleteIfExists(tempFilePath);
			}

			_addTestrayTestSuite(
				testrayCache.getTestrayBuildId(),
				testrayCache.getTestrayCaseResultAmount(), dueStatus,
				System.currentTimeMillis() - startTime, fileName, bytes.length,
				serviceContext, testrayCache, userId);

			Map<String, Serializable> values =
				_objectEntryLocalService.getValues(
					testrayCache.getTestrayRoutineId());

			if (!GetterUtil.getBoolean(values.get("autoanalyze"))) {
				updateTestrayBuildSummary(
					companyId, testrayCache.getTestrayBuildId(), userId);

				return;
			}

			List<Map<String, Serializable>> valuesList =
				_objectEntryLocalService.getValuesList(
					0, companyId, userId,
					testrayCache.getObjectDefinition(
						"Build"
					).getObjectDefinitionId(),
					new String[] {"c_buildid"},
					_filterFactory.create(
						"importStatus eq 'DONE' and routineId eq '" +
							testrayCache.getTestrayRoutineId() + "'",
						testrayCache.getObjectDefinition("Build")),
					null, 0, 1, new Sort[] {new Sort("dueDate", true)});

			if (ListUtil.isNotEmpty(valuesList)) {
				values = valuesList.get(0);

				autofillTestrayBuilds(
					companyId, testrayCache.getTestrayBuildId(),
					GetterUtil.getLong(values.get("c_buildId")), userId);
			}

			updateTestrayBuildSummary(
				companyId, testrayCache.getTestrayBuildId(), userId);

			long testrayTaskId = _getObjectEntryId(
				companyId,
				"buildId eq '" + testrayCache.getTestrayBuildId() + "'", null,
				new String[] {"c_taskId"}, "Task", testrayCache, userId);

			if (testrayTaskId != 0) {
				_objectEntryLocalService.deleteObjectEntry(testrayTaskId);
			}

			ObjectEntry objectEntry = _addObjectEntry(
				"Task", serviceContext, testrayCache, userId,
				HashMapBuilder.<String, Serializable>put(
					"dueStatus", "INANALYSIS"
				).put(
					"name", testrayCache.getTestrayBuildName()
				).put(
					"r_buildToTasks_c_buildId", testrayCache.getTestrayBuildId()
				).build());

			if (_testrayLeadUserIds == null) {
				_testrayLeadUserIds = _userLocalService.getRoleUserIds(
					_roleLocalService.getRole(
						companyId, "Testray Lead"
					).getRoleId());
			}

			for (long testrayLeadUserId : _testrayLeadUserIds) {
				_addObjectEntry(
					"TasksUsers", serviceContext, testrayCache, userId,
					HashMapBuilder.<String, Serializable>put(
						"name",
						objectEntry.getObjectEntryId() + "-" + testrayLeadUserId
					).put(
						"r_taskToTasksUsers_c_taskId",
						objectEntry.getObjectEntryId()
					).put(
						"r_userToTasksUsers_userId", testrayLeadUserId
					).build());
			}

			createTestraySubtasks(
				companyId, testrayCache.getTestrayBuildId(),
				objectEntry.getObjectEntryId(), userId);
		}
	}

	@Override
	public void processDocument(
			long companyId, Document document, String fileName, long fileSize,
			ServiceContext serviceContext, TestrayCache testrayCache,
			long userId)
		throws Exception {

		long startTime = System.currentTimeMillis();

		_dtoConverterContext = new DefaultDTOConverterContext(
			false, null, null, null, null, LocaleUtil.getSiteDefault(), null,
			_userLocalService.fetchUser(userId));

		String dueStatus = "successful";

		try {
			Element element = document.getDocumentElement();

			Map<String, String> propertiesMap = _getPropertiesMap(
				element, 0, "properties");

			long testrayProjectId = _getTestrayProjectId(
				companyId, serviceContext, testrayCache,
				propertiesMap.get("testray.project.name"), userId);

			long testrayRoutineId = _getTestrayRoutineId(
				companyId, serviceContext, testrayCache, testrayProjectId,
				propertiesMap.get("testray.build.type"), userId);

			testrayCache.setTestrayRoutineId(testrayRoutineId);

			long testrayBuildId = _getTestrayBuildId(
				companyId, propertiesMap, serviceContext,
				propertiesMap.get("testray.build.name"), testrayCache,
				testrayProjectId, testrayRoutineId, userId);

			testrayCache.setTestrayBuildId(testrayBuildId);

			testrayCache.setTestrayBuildName(
				propertiesMap.get("testray.build.name"));

			long testrayRunId = _getTestrayRunId(
				companyId, element, serviceContext, propertiesMap,
				testrayBuildId, testrayCache,
				propertiesMap.get("testray.run.id"), userId);

			JSONObject jsonObject = _addTestrayCases(
				companyId, element, serviceContext,
				propertiesMap.get("testray.build.date"), testrayBuildId,
				testrayCache, testrayProjectId, testrayRunId, userId);

			_patchObjectEntry(
				Collections.singletonMap(
					"playwrightReports", jsonObject.toString()),
				testrayBuildId, userId);
		}
		catch (Exception exception) {
			_log.error(exception);

			dueStatus = "failed";

			throw exception;
		}
		finally {
			if (fileName != null) {
				_addTestrayTestSuite(
					testrayCache.getTestrayBuildId(),
					testrayCache.getTestrayCaseResultAmount(), dueStatus,
					System.currentTimeMillis() - startTime, fileName, fileSize,
					serviceContext, testrayCache, userId);
			}
		}
	}

	@Override
	public ObjectEntry updateTestrayBuildSummary(
			long companyId, long testrayBuildId, long userId)
		throws Exception {

		Page<com.liferay.object.rest.dto.v1_0.ObjectEntry> objectEntriesPage =
			_objectEntryManager.getObjectEntries(
				companyId,
				_objectDefinitionLocalService.fetchObjectDefinition(
					companyId, "C_CaseResult"),
				null,
				new Aggregation() {
					{
						setAggregationTerms(
							HashMapBuilder.put(
								"dueStatus", "dueStatus"
							).build());
					}
				},
				new DefaultDTOConverterContext(
					false, null, null, null, null, LocaleUtil.getSiteDefault(),
					null, _userLocalService.fetchUser(userId)),
				"buildId eq '" + testrayBuildId + "'", Pagination.of(1, 8),
				null, null);

		List<Facet> facets = objectEntriesPage.getFacets();

		Facet facet = facets.get(0);

		List<Facet.FacetValue> facetValues = facet.getFacetValues();

		Map<String, Serializable> map =
			HashMapBuilder.<String, Serializable>put(
				"caseResultBlocked", 0
			).put(
				"caseResultFailed", 0
			).put(
				"caseResultIncomplete", 0
			).put(
				"caseResultPassed", 0
			).put(
				"caseResultTestFix", 0
			).put(
				"caseResultUntested", 0
			).put(
				"importStatus", "DONE"
			).build();

		for (Facet.FacetValue facetValue : facetValues) {
			String key = facetValue.getTerm();

			if (key.equals("INPROGRESS")) {
				key = "InProgress";
			}
			else if (key.equals("TESTFIX")) {
				key = "TestFix";
			}
			else {
				key = StringUtil.upperCaseFirstLetter(
					StringUtil.lowerCase(key));
			}

			map.put("caseResult" + key, facetValue.getNumberOfOccurrences());
		}

		return _patchObjectEntry(map, testrayBuildId, userId);
	}

	private void _addDefaultFactors(
			long companyId, ServiceContext serviceContext,
			TestrayCache testrayCache, long testrayRoutineId, long userId)
		throws Exception {

		List<Map<String, Serializable>> valuesList = _getValuesList(
			companyId,
			"externalReferenceCode in ('TRFCAT-001', 'TRFCAT-002', " +
				"'TRFCAT-003', 'TRFCAT-004', 'TRFCAT-009')",
			new String[] {"c_factorCategoryId"}, "FactorCategory", testrayCache,
			userId);

		if (ListUtil.isEmpty(valuesList)) {
			return;
		}

		for (Map<String, Serializable> values : valuesList) {
			_addObjectEntry(
				"Factor", serviceContext, testrayCache, userId,
				HashMapBuilder.<String, Serializable>put(
					"r_factorCategoryToFactors_c_factorCategoryId",
					GetterUtil.getLong(values.get("c_factorCategoryId"))
				).put(
					"r_routineToFactors_c_routineId", testrayRoutineId
				).build());
		}
	}

	private ObjectEntry _addObjectEntry(
			String shortName, ServiceContext serviceContext,
			TestrayCache testrayCache, long userId,
			Map<String, Serializable> values)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			userId, 0,
			testrayCache.getObjectDefinition(
				shortName
			).getObjectDefinitionId(),
			values, serviceContext);
	}

	private void _addOrUpdateTestrayCaseDetail(
			Map<String, String> propertiesMap, ServiceContext serviceContext,
			long testrayBuildId, TestrayCache testrayCache, long testrayCaseId,
			long userId)
		throws Exception {

		long testrayCaseDetailId = _getObjectEntryId(
			serviceContext.getCompanyId(),
			StringBundler.concat(
				"r_buildToCaseDetail_c_buildId eq '", testrayBuildId,
				"' and r_caseToCaseDetails_c_caseId eq '", testrayCaseId,
				"' and name eq '",
				StringUtil.replace(
					propertiesMap.get("testray.testcase.detail.name"), '\'',
					"''"),
				"'"),
			"", new String[] {"objectEntryId"}, "CaseDetail", testrayCache,
			userId);

		com.liferay.object.rest.dto.v1_0.ObjectEntry objectEntry =
			new com.liferay.object.rest.dto.v1_0.ObjectEntry();

		objectEntry.setProperties(
			() -> HashMapBuilder.<String, Object>put(
				"caseDetailsToJiraIssues",
				TransformUtil.transform(
					StringUtil.split(propertiesMap.get("testray.jira.issues")),
					jiraIssue -> Collections.singletonMap(
						"externalReferenceCode", StringUtil.trim(jiraIssue)),
					Object.class)
			).put(
				"dueStatus",
				() -> {
					String testrayTestDetailStatus = GetterUtil.getString(
						propertiesMap.get("testray.testcase.detail.status"));

					if (testrayTestDetailStatus.equals("FAILED")) {
						return "FAILED";
					}
					else if (testrayTestDetailStatus.equals("PASSED")) {
						return "PASSED";
					}

					return "UNTESTED";
				}
			).put(
				"name", propertiesMap.get("testray.testcase.detail.name")
			).put(
				"r_buildToCaseDetail_c_buildId", testrayBuildId
			).put(
				"r_caseToCaseDetails_c_caseId", testrayCaseId
			).build());

		if (testrayCaseDetailId == 0) {
			_objectEntryManager.addObjectEntry(
				_dtoConverterContext,
				testrayCache.getObjectDefinition("CaseDetail"), objectEntry,
				null);

			return;
		}

		Page<com.liferay.object.rest.dto.v1_0.ObjectEntry> objectEntriesPage =
			_objectEntryManager.getObjectEntries(
				serviceContext.getCompanyId(),
				testrayCache.getObjectDefinition("CaseDetail"), null, null,
				_dtoConverterContext, "id eq '" + testrayCaseDetailId + "'",
				null, null, null);

		_objectEntryManager.updateObjectEntry(
			testrayCaseDetailId, _dtoConverterContext,
			GetterUtil.getString(
				objectEntriesPage.fetchFirstItem(
				).getPropertyValue(
					"externalReferenceCode"
				)),
			testrayCache.getObjectDefinition("CaseDetail"), objectEntry, null);
	}

	private void _addOrUpdateTestrayCaseResult(
			ServiceContext serviceContext, Node testcaseNode,
			JSONArray testrayAttachmentsJSONArray, String testrayBuildDate,
			long testrayBuildId, TestrayCache testrayCache, long testrayCaseId,
			Map<String, String> testrayCasePropertiesMap,
			long testrayComponentId, long testrayRunId, long testrayTeamId,
			long userId)
		throws Exception {

		String objectEntryIdsKey = StringBundler.concat(
			"RunId#", testrayRunId, "#CaseId#", testrayCaseId);

		long testrayCaseResultId = _getObjectEntryId(
			serviceContext.getCompanyId(),
			StringBundler.concat(
				"r_runToCaseResult_c_runId eq '", testrayRunId,
				"' and r_caseToCaseResult_c_caseId eq '", testrayCaseId, "'"),
			objectEntryIdsKey, new String[] {"c_caseResultId"}, "CaseResult",
			testrayCache, userId);

		Map<String, Serializable> properties =
			HashMapBuilder.<String, Serializable>put(
				"attachments", testrayAttachmentsJSONArray
			).put(
				"closedDate", Timestamp.valueOf(testrayBuildDate)
			).put(
				"dueStatus",
				() -> {
					String testrayTestcaseStatus = testrayCasePropertiesMap.get(
						"testray.testcase.status");

					if (testrayTestcaseStatus.equals("blocked")) {
						return "BLOCKED";
					}
					else if (testrayTestcaseStatus.equals("dnr")) {
						return "DIDNOTRUN";
					}
					else if (testrayTestcaseStatus.equals("failed")) {
						return "FAILED";
					}
					else if (testrayTestcaseStatus.equals("incomplete")) {
						return "INCOMPLETE";
					}
					else if (testrayTestcaseStatus.equals("in-progress")) {
						return "INPROGRESS";
					}
					else if (testrayTestcaseStatus.equals("passed")) {
						return "PASSED";
					}
					else if (testrayTestcaseStatus.equals("test-fix")) {
						return "TESTFIX";
					}

					return "UNTESTED";
				}
			).put(
				"duration",
				GetterUtil.getLong(
					testrayCasePropertiesMap.get("testray.testcase.duration"))
			).put(
				"r_buildToCaseResult_c_buildId", testrayBuildId
			).put(
				"r_caseToCaseResult_c_caseId", testrayCaseId
			).put(
				"r_componentToCaseResult_c_componentId", testrayComponentId
			).put(
				"r_runToCaseResult_c_runId", testrayRunId
			).put(
				"r_teamToCaseResult_c_teamId", testrayTeamId
			).put(
				"startDate", Timestamp.valueOf(testrayBuildDate)
			).put(
				"warnings",
				GetterUtil.getInteger(
					testrayCasePropertiesMap.get("testray.testcase.warnings"))
			).build();

		Element element = (Element)testcaseNode;

		NodeList nodeList = element.getElementsByTagName("failure");

		Node failureNode = nodeList.item(0);

		if (failureNode != null) {
			String message = _getAttributeValue("message", failureNode);

			if (!message.isEmpty()) {
				properties.put("errors", message);
			}
		}

		if (testrayCaseResultId == 0) {
			ObjectEntry objectEntry = _addObjectEntry(
				"CaseResult", serviceContext, testrayCache, userId, properties);

			testrayCache.addObjectEntryId(
				objectEntryIdsKey, objectEntry.getObjectEntryId());

			testrayCache.incrementTestrayCaseResultAmount();

			return;
		}

		_updateObjectEntry(
			testrayCaseResultId, serviceContext, userId, properties);

		testrayCache.incrementTestrayCaseResultAmount();
	}

	private void _addTestrayCase(
			long companyId, ServiceContext serviceContext, Node testcaseNode,
			JSONArray testrayAttachmentsJSONArray, String testrayBuildDate,
			long testrayBuildId, TestrayCache testrayCache,
			Map<String, String> testrayCasePropertiesMap, long testrayProjectId,
			long testrayRunId, long userId)
		throws Exception {

		String testrayCaseName = testrayCasePropertiesMap.get(
			"testray.testcase.name");
		long testrayCaseTypeId = _getTestrayCaseTypeId(
			companyId, serviceContext, testrayCache,
			testrayCasePropertiesMap.get("testray.case.type.name"), userId);

		String objectEntryIdsKey = StringBundler.concat(
			"Case#", testrayCaseName, "#CaseTypeId#", testrayCaseTypeId,
			"#ProjectId#", testrayProjectId);

		long testrayCaseId = _getObjectEntryId(
			companyId,
			StringBundler.concat(
				"name eq '",
				StringUtil.removeChar(
					StringUtil.replace(testrayCaseName, '\'', "''"), '\\'),
				"' and projectId eq '", testrayProjectId,
				"' and r_caseTypeToCases_c_caseTypeId eq '", testrayCaseTypeId,
				"'"),
			objectEntryIdsKey, new String[] {"c_caseId"}, "Case", testrayCache,
			userId);

		long testrayTeamId = _getTestrayTeamId(
			companyId, serviceContext, testrayCache, testrayProjectId,
			testrayCasePropertiesMap.get("testray.team.name"), userId);

		long testrayComponentId = _getTestrayComponentId(
			companyId, serviceContext, testrayCache,
			testrayCasePropertiesMap.get("testray.main.component.name"),
			testrayProjectId, testrayTeamId, userId);

		if (testrayCaseId == 0) {
			ObjectEntry objectEntry = _addObjectEntry(
				"Case", serviceContext, testrayCache, userId,
				HashMapBuilder.<String, Serializable>put(
					"description",
					testrayCasePropertiesMap.get("testray.testcase.description")
				).put(
					"name",
					testrayCasePropertiesMap.get("testray.testcase.name")
				).put(
					"number", 0
				).put(
					"priority",
					GetterUtil.getInteger(
						testrayCasePropertiesMap.get(
							"testray.testcase.priority"))
				).put(
					"r_caseTypeToCases_c_caseTypeId", testrayCaseTypeId
				).put(
					"r_componentToCases_c_componentId", testrayComponentId
				).put(
					"r_projectToCases_c_projectId", testrayProjectId
				).build());

			testrayCaseId = objectEntry.getObjectEntryId();

			testrayCache.addObjectEntryId(objectEntryIdsKey, testrayCaseId);
		}

		if (ListUtil.isEmpty(
				_getValuesList(
					companyId,
					StringBundler.concat(
						"buildId eq '", testrayBuildId, "' and caseId eq '",
						testrayCaseId, "'"),
					new String[] {"objectEntryId"}, "BuildsCases", testrayCache,
					userId))) {

			_addObjectEntry(
				"BuildsCases", serviceContext, testrayCache, userId,
				HashMapBuilder.<String, Serializable>put(
					"r_buildToBuildsCases_c_buildId", testrayBuildId
				).put(
					"r_caseToBuildsCases_c_caseId", testrayCaseId
				).build());
		}

		_addOrUpdateTestrayCaseResult(
			serviceContext, testcaseNode, testrayAttachmentsJSONArray,
			testrayBuildDate, testrayBuildId, testrayCache, testrayCaseId,
			testrayCasePropertiesMap, testrayComponentId, testrayRunId,
			testrayTeamId, userId);

		if (StringUtil.contains(
				testrayCasePropertiesMap.get("testray.case.type.name"),
				"Playwright", " ")) {

			_addOrUpdateTestrayCaseDetail(
				HashMapBuilder.put(
					"testray.jira.issues",
					testrayCasePropertiesMap.get("testray.jira.issues")
				).put(
					"testray.testcase.detail.name",
					StringUtil.extractLast(
						testrayCasePropertiesMap.get("testray.testcase.name"),
						">")
				).put(
					"testray.testcase.detail.status",
					testrayCasePropertiesMap.get("testray.testcase.status")
				).build(),
				serviceContext, testrayBuildId, testrayCache, testrayCaseId,
				userId);

			return;
		}

		Element element = (Element)testcaseNode;

		NodeList detailsNodeList = element.getElementsByTagName("details");

		if (detailsNodeList.getLength() == 0) {
			return;
		}

		Element detailsElement = (Element)detailsNodeList.item(0);

		NodeList detailNodeList = detailsElement.getElementsByTagName("detail");

		for (int i = 0; i < detailNodeList.getLength(); i++) {
			_addOrUpdateTestrayCaseDetail(
				_getPropertiesMap(detailsElement, i, "detail"), serviceContext,
				testrayBuildId, testrayCache, testrayCaseId, userId);
		}
	}

	private JSONObject _addTestrayCases(
			long companyId, Element element, ServiceContext serviceContext,
			String testrayBuildDate, long testrayBuildId,
			TestrayCache testrayCache, long testrayProjectId, long testrayRunId,
			long userId)
		throws Exception {

		JSONObject playwrightReportsJSONObject = _jsonFactory.createJSONObject(
			GetterUtil.getString(
				_objectEntryLocalService.getValues(
					testrayBuildId
				).get(
					"playwrightReports"
				)));

		NodeList testCaseNodeList = element.getElementsByTagName("testcase");

		for (int i = 0; i < testCaseNodeList.getLength(); i++) {
			Node testcaseNode = testCaseNodeList.item(i);

			JSONArray testrayAttachmentsJSONArray =
				_getTestrayAttachmentsJSONArray(testcaseNode);

			Map<String, String> testrayCasePropertiesMap = _getPropertiesMap(
				(Element)testcaseNode, 0, "properties");

			_addTestrayCase(
				companyId, serviceContext, testcaseNode,
				testrayAttachmentsJSONArray, testrayBuildDate, testrayBuildId,
				testrayCache, testrayCasePropertiesMap, testrayProjectId,
				testrayRunId, userId);

			for (int j = 0; j < testrayAttachmentsJSONArray.length(); j++) {
				JSONObject jsonObject =
					testrayAttachmentsJSONArray.getJSONObject(j);

				if (!StringUtil.startsWith(
						jsonObject.getString("name"), "Playwright Report")) {

					continue;
				}

				playwrightReportsJSONObject.put(
					jsonObject.getString("url"),
					testrayCasePropertiesMap.get("testray.testcase.name"));
			}
		}

		return playwrightReportsJSONObject;
	}

	private void _addTestrayFactor(
			ServiceContext serviceContext, TestrayCache testrayCache,
			long testrayFactorCategoryId, long testrayFactorOptionId,
			long testrayRunId, long userId)
		throws Exception {

		_addObjectEntry(
			"Factor", serviceContext, testrayCache, userId,
			HashMapBuilder.<String, Serializable>put(
				"r_factorCategoryToFactors_c_factorCategoryId",
				testrayFactorCategoryId
			).put(
				"r_factorOptionToFactors_c_factorOptionId",
				testrayFactorOptionId
			).put(
				"r_runToFactors_c_runId", testrayRunId
			).build());
	}

	private void _addTestrayTestSuite(
			long buildId, long caseResultAmount, String dueStatus,
			long executionTime, String fileName, long fileSize,
			ServiceContext serviceContext, TestrayCache testrayCache,
			long userId)
		throws Exception {

		_addObjectEntry(
			"TestSuite", serviceContext, testrayCache, userId,
			HashMapBuilder.<String, Serializable>put(
				"caseResultAmount", caseResultAmount
			).put(
				"dueStatus", dueStatus
			).put(
				"executionTime", executionTime
			).put(
				"fileName", fileName
			).put(
				"fileSize", fileSize
			).put(
				"r_buildToTestSuite_c_buildId", buildId
			).build());
	}

	private ObjectEntry _autofillTestrayCaseResult(
			Map<String, Serializable> testrayCaseResult1,
			Map<String, Serializable> testrayCaseResult2, long userId)
		throws Exception {

		Map<String, Serializable> targetTestrayCaseResult = null;
		Map<String, Serializable> sourceTestrayCaseResult = null;

		if (((Long)testrayCaseResult1.get("r_userToCaseResults_userId") > 0) &&
			Validator.isNotNull(testrayCaseResult1.get("issues")) &&
			((Long)testrayCaseResult2.get("r_userToCaseResults_userId") <= 0) &&
			Validator.isNull(testrayCaseResult2.get("issues"))) {

			sourceTestrayCaseResult = testrayCaseResult1;
			targetTestrayCaseResult = testrayCaseResult2;
		}
		else if (((Long)testrayCaseResult1.get("r_userToCaseResults_userId") <=
					0) &&
				 Validator.isNull(testrayCaseResult1.get("issues")) &&
				 ((Long)testrayCaseResult2.get("r_userToCaseResults_userId") >
					 0) &&
				 Validator.isNotNull(testrayCaseResult2.get("issues"))) {

			sourceTestrayCaseResult = testrayCaseResult2;
			targetTestrayCaseResult = testrayCaseResult1;
		}

		if (targetTestrayCaseResult == null) {
			return null;
		}

		targetTestrayCaseResult.put(
			"dueStatus", sourceTestrayCaseResult.get("dueStatus"));
		targetTestrayCaseResult.put(
			"r_userToCaseResults_userId",
			sourceTestrayCaseResult.get("r_userToCaseResults_userId"));
		targetTestrayCaseResult.put(
			"issues", String.valueOf(sourceTestrayCaseResult.get("issues")));

		return _objectEntryLocalService.updateObjectEntry(
			userId,
			GetterUtil.getLong(targetTestrayCaseResult.get("c_caseResultId")),
			targetTestrayCaseResult, _serviceContextHelper.getServiceContext());
	}

	private String _getAttributeValue(String attributeName, Node node) {
		NamedNodeMap namedNodeMap = node.getAttributes();

		if (namedNodeMap == null) {
			return null;
		}

		Node attributeNode = namedNodeMap.getNamedItem(attributeName);

		if (attributeNode == null) {
			return null;
		}

		return attributeNode.getTextContent();
	}

	private long _getObjectEntryId(
			long companyId, String filterString, String objectEntryIdsKey,
			String[] selectedObjectFieldNames, String shortName,
			TestrayCache testrayCache, long userId)
		throws Exception {

		Long objectEntryId = testrayCache.getObjectEntryId(objectEntryIdsKey);

		if (objectEntryId != null) {
			return objectEntryId;
		}

		List<Map<String, Serializable>> valuesList = _getValuesList(
			companyId, filterString, selectedObjectFieldNames, shortName,
			testrayCache, userId);

		if (ListUtil.isNotEmpty(valuesList)) {
			Map<String, Serializable> values = valuesList.get(0);

			return GetterUtil.getLong(values.get("objectEntryId"));
		}

		return 0;
	}

	private Map<String, String> _getPropertiesMap(
		Element element, int index, String tagName) {

		Map<String, String> map = new HashMap<>();

		NodeList propertiesNodeList = element.getElementsByTagName(tagName);

		Node propertiesNode = propertiesNodeList.item(index);

		Element propertiesElement = (Element)propertiesNode;

		NodeList propertyNodeList = propertiesElement.getElementsByTagName(
			"property");

		for (int i = 0; i < propertyNodeList.getLength(); i++) {
			Node propertyNode = propertyNodeList.item(i);

			if (!propertyNode.hasAttributes()) {
				continue;
			}

			map.put(
				_getAttributeValue("name", propertyNode),
				_getAttributeValue("value", propertyNode));
		}

		return map;
	}

	private JSONArray _getTestrayAttachmentsJSONArray(Node testcaseNode)
		throws Exception {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		Element testcaseElement = (Element)testcaseNode;

		NodeList attachmentsNodeList = testcaseElement.getElementsByTagName(
			"attachments");

		for (int i = 0; i < attachmentsNodeList.getLength(); i++) {
			Node attachmentsNode = attachmentsNodeList.item(i);

			if (attachmentsNode.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			Element attachmentsElement = (Element)attachmentsNode;

			NodeList fileNodeList = attachmentsElement.getElementsByTagName(
				"file");

			for (int j = 0; j < fileNodeList.getLength(); j++) {
				Node fileNode = fileNodeList.item(j);

				if (fileNode.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}

				Element fileElement = (Element)fileNode;

				jsonArray.put(
					JSONUtil.put(
						"name", fileElement.getAttribute("name")
					).put(
						"url", fileElement.getAttribute("url")
					).put(
						"value", fileElement.getAttribute("value")
					));
			}
		}

		return jsonArray;
	}

	private String _getTestrayBuildDescription(
		Map<String, String> propertiesMap) {

		StringBundler sb = new StringBundler(15);

		if (propertiesMap.get("liferay.portal.bundle") != null) {
			sb.append("Bundle: ");
			sb.append(propertiesMap.get("liferay.portal.bundle"));
			sb.append(StringPool.SEMICOLON);
			sb.append(StringPool.NEW_LINE);
		}

		if (propertiesMap.get("liferay.plugins.git.id") != null) {
			sb.append("Plugins hash: ");
			sb.append(propertiesMap.get("liferay.plugins.git.id"));
			sb.append(StringPool.SEMICOLON);
			sb.append(StringPool.NEW_LINE);
		}

		if (propertiesMap.get("liferay.portal.branch") != null) {
			sb.append("Portal branch: ");
			sb.append(propertiesMap.get("liferay.portal.branch"));
			sb.append(StringPool.SEMICOLON);
			sb.append(StringPool.NEW_LINE);
		}

		if (propertiesMap.get("liferay.portal.git.id") != null) {
			sb.append("Portal hash: ");
			sb.append(propertiesMap.get("liferay.portal.git.id"));
			sb.append(StringPool.SEMICOLON);
		}

		return sb.toString();
	}

	private long _getTestrayBuildId(
			long companyId, Map<String, String> propertiesMap,
			ServiceContext serviceContext, String testrayBuildName,
			TestrayCache testrayCache, long testrayProjectId,
			long testrayRoutineId, long userId)
		throws Exception {

		String objectEntryIdsKey = StringBundler.concat(
			"Build#", testrayBuildName, "#ProjectId#", testrayProjectId);

		long testrayBuildId = _getObjectEntryId(
			companyId,
			StringBundler.concat(
				"projectId eq '", testrayProjectId, "' and name eq '",
				testrayBuildName, "'"),
			objectEntryIdsKey, new String[] {"c_buildId"}, "Build",
			testrayCache, userId);

		if (testrayBuildId != 0) {
			_patchObjectEntry(
				HashMapBuilder.<String, Serializable>put(
					"cpuUseTime",
					propertiesMap.get("testray.total.cpu.use.time")
				).put(
					"importStatus", "INPROGRESS"
				).build(),
				testrayBuildId, userId);

			return testrayBuildId;
		}

		long testrayProductVersionId = _getTestrayProductVersionId(
			companyId, serviceContext, testrayCache,
			propertiesMap.get("testray.product.version"), testrayProjectId,
			userId);

		ObjectEntry objectEntry = _addObjectEntry(
			"Build", serviceContext, testrayCache, userId,
			HashMapBuilder.<String, Serializable>put(
				"cpuUseTime", propertiesMap.get("testray.total.cpu.use.time")
			).put(
				"description", _getTestrayBuildDescription(propertiesMap)
			).put(
				"dueDate",
				Timestamp.valueOf(propertiesMap.get("testray.build.date"))
			).put(
				"dueStatus", "ACTIVATED"
			).put(
				"gitHash", propertiesMap.get("git.id")
			).put(
				"githubCompareURLs", propertiesMap.get("liferay.compare.urls")
			).put(
				"importStatus", "INPROGRESS"
			).put(
				"name", testrayBuildName
			).put(
				"r_productVersionToBuilds_c_productVersionId",
				testrayProductVersionId
			).put(
				"r_projectToBuilds_c_projectId", testrayProjectId
			).put(
				"r_routineToBuilds_c_routineId", testrayRoutineId
			).build());

		testrayBuildId = objectEntry.getObjectEntryId();

		testrayCache.addObjectEntryId(objectEntryIdsKey, testrayBuildId);

		return testrayBuildId;
	}

	private Map<Long, List<Map<String, Serializable>>>
			_getTestrayCaseResultsByTestrayBuildGroupedByTestrayCase(
				long companyId, ObjectDefinition objectDefinition,
				long testrayBuildId1, long userId)
		throws Exception {

		Map<Long, List<Map<String, Serializable>>>
			testrayCaseResultsGroupedByTestrayCase = new HashMap<>();

		for (Map<String, Serializable> values :
				_objectEntryLocalService.getValuesList(
					0, companyId, userId,
					objectDefinition.getObjectDefinitionId(), null,
					_filterFactory.create(
						"buildId eq '" + testrayBuildId1 + "' and errors ne ''",
						objectDefinition),
					null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			long testrayCaseId = (Long)values.get(
				"r_caseToCaseResult_c_caseId");

			List<Map<String, Serializable>> testrayCaseResults =
				testrayCaseResultsGroupedByTestrayCase.get(testrayCaseId);

			if (testrayCaseResults == null) {
				testrayCaseResults = new ArrayList<>();

				testrayCaseResultsGroupedByTestrayCase.put(
					testrayCaseId, testrayCaseResults);
			}

			testrayCaseResults.add(values);
		}

		return testrayCaseResultsGroupedByTestrayCase;
	}

	private long _getTestrayCaseTypeId(
			long companyId, ServiceContext serviceContext,
			TestrayCache testrayCache, String testrayCaseTypeName, long userId)
		throws Exception {

		String objectEntryIdsKey = "CaseType#" + testrayCaseTypeName;

		long testrayCaseTypeId = _getObjectEntryId(
			companyId, "name eq '" + testrayCaseTypeName + "'",
			objectEntryIdsKey, new String[] {"c_caseTypeId"}, "CaseType",
			testrayCache, userId);

		if (testrayCaseTypeId != 0) {
			return testrayCaseTypeId;
		}

		ObjectEntry objectEntry = _addObjectEntry(
			"CaseType", serviceContext, testrayCache, userId,
			HashMapBuilder.<String, Serializable>put(
				"name", testrayCaseTypeName
			).build());

		testrayCaseTypeId = objectEntry.getObjectEntryId();

		testrayCache.addObjectEntryId(objectEntryIdsKey, testrayCaseTypeId);

		return testrayCaseTypeId;
	}

	private long _getTestrayComponentId(
			long companyId, ServiceContext serviceContext,
			TestrayCache testrayCache, String testrayComponentName,
			long testrayProjectId, long testrayTeamId, long userId)
		throws Exception {

		String objectEntryIdsKey = StringBundler.concat(
			"Component#", testrayComponentName, "#ProjectId#",
			testrayProjectId);

		Long objectEntryId = testrayCache.getObjectEntryId(objectEntryIdsKey);

		Map<String, Serializable> values = null;

		if (objectEntryId == null) {
			List<Map<String, Serializable>> valuesList = _getValuesList(
				companyId,
				StringBundler.concat(
					"projectId eq '", testrayProjectId, "' and name eq '",
					testrayComponentName, "'"),
				null, "Component", testrayCache, userId);

			if (ListUtil.isNotEmpty(valuesList)) {
				values = valuesList.get(0);

				objectEntryId = GetterUtil.getLong(values.get("objectEntryId"));
			}
			else {
				ObjectEntry objectEntry = _addObjectEntry(
					"Component", serviceContext, testrayCache, userId,
					HashMapBuilder.<String, Serializable>put(
						"name", testrayComponentName
					).put(
						"r_projectToComponents_c_projectId", testrayProjectId
					).put(
						"r_teamToComponents_c_teamId", testrayTeamId
					).build());

				long testrayComponentId = objectEntry.getObjectEntryId();

				testrayCache.addObjectEntryId(
					objectEntryIdsKey, testrayComponentId);

				return testrayComponentId;
			}
		}

		if (values == null) {
			values = _objectEntryLocalService.getValues(objectEntryId);
		}

		long currentTestrayTeamId = GetterUtil.getLong(
			values.get("r_teamToComponents_c_teamId"));

		if (testrayTeamId == currentTestrayTeamId) {
			return objectEntryId;
		}

		_updateObjectEntry(
			GetterUtil.getLong(values.get("objectEntryId")), serviceContext,
			userId,
			HashMapBuilder.<String, Serializable>put(
				"name", testrayComponentName
			).put(
				"r_projectToComponents_c_projectId", testrayProjectId
			).put(
				"r_teamToComponents_c_teamId", testrayTeamId
			).build());

		return objectEntryId;
	}

	private long _getTestrayFactorCategoryId(
			long companyId, ServiceContext serviceContext,
			TestrayCache testrayCache, String testrayFactorCategoryName,
			long userId)
		throws Exception {

		String objectEntryIdsKey =
			"FactorCategory#" + testrayFactorCategoryName;

		long testrayFactorCategoryId = _getObjectEntryId(
			companyId, "name eq '" + testrayFactorCategoryName + "'",
			objectEntryIdsKey, new String[] {"c_factorCategoryId"},
			"FactorCategory", testrayCache, userId);

		if (testrayFactorCategoryId != 0) {
			return testrayFactorCategoryId;
		}

		ObjectEntry objectEntry = _addObjectEntry(
			"FactorCategory", serviceContext, testrayCache, userId,
			HashMapBuilder.<String, Serializable>put(
				"name", testrayFactorCategoryName
			).build());

		testrayFactorCategoryId = objectEntry.getObjectEntryId();

		testrayCache.addObjectEntryId(
			objectEntryIdsKey, testrayFactorCategoryId);

		return testrayFactorCategoryId;
	}

	private long _getTestrayFactorOptionId(
			long companyId, ServiceContext serviceContext,
			TestrayCache testrayCache, long testrayFactorCategoryId,
			String testrayFactorOptionName, long userId)
		throws Exception {

		String objectEntryIdsKey = StringBundler.concat(
			"FactorOption#", testrayFactorOptionName, "#FactorCategoryId#",
			testrayFactorCategoryId);

		long testrayFactorOptionId = _getObjectEntryId(
			companyId,
			StringBundler.concat(
				"factorCategoryId eq '", testrayFactorCategoryId,
				"' and name eq '", testrayFactorOptionName, "'"),
			objectEntryIdsKey, new String[] {"c_factorOptionId"},
			"FactorOption", testrayCache, userId);

		if (testrayFactorOptionId != 0) {
			return testrayFactorOptionId;
		}

		ObjectEntry objectEntry = _addObjectEntry(
			"FactorOption", serviceContext, testrayCache, userId,
			HashMapBuilder.<String, Serializable>put(
				"name", testrayFactorOptionName
			).put(
				"r_factorCategoryToOptions_c_factorCategoryId",
				testrayFactorCategoryId
			).build());

		testrayFactorOptionId = objectEntry.getObjectEntryId();

		testrayCache.addObjectEntryId(objectEntryIdsKey, testrayFactorOptionId);

		return testrayFactorOptionId;
	}

	private long _getTestrayProductVersionId(
			long companyId, ServiceContext serviceContext,
			TestrayCache testrayCache, String testrayProductVersionName,
			long testrayProjectId, long userId)
		throws Exception {

		String objectEntryIdsKey =
			"ProductVersion#" + testrayProductVersionName;

		long testrayProductVersionId = _getObjectEntryId(
			companyId, "name eq '" + testrayProductVersionName + "'",
			objectEntryIdsKey, new String[] {"c_productVersionId"},
			"ProductVersion", testrayCache, userId);

		if (testrayProductVersionId != 0) {
			return testrayProductVersionId;
		}

		ObjectEntry objectEntry = _addObjectEntry(
			"ProductVersion", serviceContext, testrayCache, userId,
			HashMapBuilder.<String, Serializable>put(
				"name", testrayProductVersionName
			).put(
				"r_projectToProductVersions_c_projectId", testrayProjectId
			).build());

		testrayProductVersionId = objectEntry.getObjectEntryId();

		testrayCache.addObjectEntryId(
			objectEntryIdsKey, testrayProductVersionId);

		return testrayProductVersionId;
	}

	private long _getTestrayProjectId(
			long companyId, ServiceContext serviceContext,
			TestrayCache testrayCache, String testrayProjectName, long userId)
		throws Exception {

		String objectEntryIdsKey = "Project#" + testrayProjectName;

		long testrayProjectId = _getObjectEntryId(
			companyId, "name eq '" + testrayProjectName + "'",
			objectEntryIdsKey, new String[] {"c_projectId"}, "Project",
			testrayCache, userId);

		if (testrayProjectId != 0) {
			return testrayProjectId;
		}

		ObjectEntry objectEntry = _addObjectEntry(
			"Project", serviceContext, testrayCache, userId,
			HashMapBuilder.<String, Serializable>put(
				"name", testrayProjectName
			).build());

		testrayProjectId = objectEntry.getObjectEntryId();

		testrayCache.addObjectEntryId(objectEntryIdsKey, testrayProjectId);

		return testrayProjectId;
	}

	private long _getTestrayRoutineId(
			long companyId, ServiceContext serviceContext,
			TestrayCache testrayCache, long testrayProjectId,
			String testrayRoutineName, long userId)
		throws Exception {

		String objectEntryIdsKey = StringBundler.concat(
			"Routine#", testrayRoutineName, "#ProjectId#", testrayProjectId);

		long testrayRoutineId = _getObjectEntryId(
			companyId,
			StringBundler.concat(
				"projectId eq '", testrayProjectId, "' and name eq '",
				testrayRoutineName, "'"),
			objectEntryIdsKey, new String[] {"c_RoutineId"}, "Routine",
			testrayCache, userId);

		if (testrayRoutineId != 0) {
			return testrayRoutineId;
		}

		ObjectEntry objectEntry = _addObjectEntry(
			"Routine", serviceContext, testrayCache, userId,
			HashMapBuilder.<String, Serializable>put(
				"name", testrayRoutineName
			).put(
				"r_routineToProjects_c_projectId", testrayProjectId
			).build());

		testrayRoutineId = objectEntry.getObjectEntryId();

		testrayCache.addObjectEntryId(objectEntryIdsKey, testrayRoutineId);

		_addDefaultFactors(
			companyId, serviceContext, testrayCache, testrayRoutineId, userId);

		return testrayRoutineId;
	}

	private String _getTestrayRunEnvironmentHash(
			long companyId, Element element, ServiceContext serviceContext,
			TestrayCache testrayCache, long testrayRunId, long userId)
		throws Exception {

		StringBundler sb = new StringBundler();

		NodeList environmentNodeList = element.getElementsByTagName(
			"environment");

		for (int i = 0; i < environmentNodeList.getLength(); i++) {
			Node node = environmentNodeList.item(i);

			if (!node.hasAttributes()) {
				continue;
			}

			String testrayFactorCategoryName = _getAttributeValue("type", node);

			long testrayFactorCategoryId = _getTestrayFactorCategoryId(
				companyId, serviceContext, testrayCache,
				testrayFactorCategoryName, userId);

			String testrayFactorOptionName = _getAttributeValue("option", node);

			long testrayFactorOptionId = _getTestrayFactorOptionId(
				companyId, serviceContext, testrayCache,
				testrayFactorCategoryId, testrayFactorOptionName, userId);

			_addTestrayFactor(
				serviceContext, testrayCache, testrayFactorCategoryId,
				testrayFactorOptionId, testrayRunId, userId);

			sb.append(testrayFactorCategoryId);
			sb.append(testrayFactorOptionId);
		}

		String testrayFactorsString = sb.toString();

		return String.valueOf(testrayFactorsString.hashCode());
	}

	private long _getTestrayRunId(
			long companyId, Element element, ServiceContext serviceContext,
			Map<String, String> propertiesMap, long testrayBuildId,
			TestrayCache testrayCache, String testrayRunName, long userId)
		throws Exception {

		String objectEntryIdsKey = StringBundler.concat(
			"Run#", testrayRunName, "#BuildId#", testrayBuildId);

		long testrayRunId = _getObjectEntryId(
			companyId,
			StringBundler.concat(
				"buildId eq '", testrayBuildId, "' and name eq '",
				testrayRunName, "'"),
			objectEntryIdsKey, new String[] {"c_runId"}, "Run", testrayCache,
			userId);

		if (testrayRunId != 0) {
			return testrayRunId;
		}

		ObjectEntry objectEntry = _addObjectEntry(
			"Run", serviceContext, testrayCache, userId,
			HashMapBuilder.<String, Serializable>put(
				"externalReferencePK", propertiesMap.get("testray.run.id")
			).put(
				"externalReferenceType", 1
			).put(
				"jenkinsJobKey",
				GetterUtil.getLong(propertiesMap.get("jenkins.job.id"))
			).put(
				"name", testrayRunName
			).put(
				"number", testrayCache.getNextTestrayRunNumber()
			).put(
				"r_buildToRuns_c_buildId", testrayBuildId
			).build());

		testrayRunId = objectEntry.getObjectEntryId();

		objectEntry.getValues(
		).put(
			"environmentHash",
			_getTestrayRunEnvironmentHash(
				companyId, element, serviceContext, testrayCache, testrayRunId,
				userId)
		);

		_objectEntryLocalService.updateObjectEntry(
			userId, objectEntry.getObjectEntryId(), objectEntry.getValues(),
			serviceContext);

		testrayCache.addObjectEntryId(objectEntryIdsKey, testrayRunId);

		return testrayRunId;
	}

	private long _getTestrayTeamId(
			long companyId, ServiceContext serviceContext,
			TestrayCache testrayCache, long testrayProjectId,
			String testrayTeamName, long userId)
		throws Exception {

		String objectEntryIdsKey = StringBundler.concat(
			"Team#", testrayTeamName, "#ProjectId#", testrayProjectId);

		long testrayTeamId = _getObjectEntryId(
			companyId,
			StringBundler.concat(
				"projectId eq '", testrayProjectId, "' and name eq '",
				testrayTeamName, "'"),
			objectEntryIdsKey, new String[] {"c_teamId"}, "Team", testrayCache,
			userId);

		if (testrayTeamId != 0) {
			return testrayTeamId;
		}

		ObjectEntry objectEntry = _addObjectEntry(
			"Team", serviceContext, testrayCache, userId,
			HashMapBuilder.<String, Serializable>put(
				"name", testrayTeamName
			).put(
				"r_projectToTeams_c_projectId", testrayProjectId
			).build());

		testrayCache.addObjectEntryId(
			objectEntryIdsKey, objectEntry.getObjectEntryId());

		return objectEntry.getObjectEntryId();
	}

	private List<Map<String, Serializable>> _getValuesList(
			long companyId, String filterString,
			String[] selectedObjectFieldNames, String shortName,
			TestrayCache testrayCache, long userId)
		throws Exception {

		ObjectDefinition objectDefinition = testrayCache.getObjectDefinition(
			shortName);

		return _objectEntryLocalService.getValuesList(
			0, companyId, userId, objectDefinition.getObjectDefinitionId(),
			selectedObjectFieldNames,
			_filterFactory.create(filterString, objectDefinition), null,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	private void _loadObjectDefinitions(
		long companyId, TestrayCache testrayCache) {

		List<ObjectDefinition> objectDefinitions =
			_objectDefinitionLocalService.getObjectDefinitions(
				companyId, true, WorkflowConstants.STATUS_APPROVED);

		if (ListUtil.isEmpty(objectDefinitions)) {
			return;
		}

		for (ObjectDefinition objectDefinition : objectDefinitions) {
			testrayCache.addObjectDefinition(objectDefinition);
		}
	}

	private void _loadTestrayProjects(
			long companyId, TestrayCache testrayCache, long userId)
		throws Exception {

		List<Map<String, Serializable>> valuesList = _getValuesList(
			companyId, null, new String[] {"c_projectId", "name"}, "Project",
			testrayCache, userId);

		if (ListUtil.isEmpty(valuesList)) {
			return;
		}

		for (Map<String, Serializable> values : valuesList) {
			testrayCache.addObjectEntryId(
				"Project#" + GetterUtil.getString(values.get("name")),
				GetterUtil.getLong(values.get("c_projectId")));
		}
	}

	private ObjectEntry _patchObjectEntry(
			Map<String, Serializable> map, long objectEntryId, long userId)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntryId);

		Map<String, Serializable> values = objectEntry.getValues();

		values.putAll(map);

		return _objectEntryLocalService.updateObjectEntry(
			userId, objectEntry.getObjectEntryId(), values,
			new ServiceContext());
	}

	private ObjectEntry _updateObjectEntry(
			long objectEntryId, ServiceContext serviceContext, long userId,
			Map<String, Serializable> values)
		throws Exception {

		return _objectEntryLocalService.updateObjectEntry(
			userId, objectEntryId, values, serviceContext);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TestrayManagerImpl.class);

	private DTOConverterContext _dtoConverterContext;

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference(target = "(object.entry.manager.storage.type=default)")
	private ObjectEntryManager _objectEntryManager;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

	private long[] _testrayLeadUserIds;

	@Reference
	private UserLocalService _userLocalService;

}