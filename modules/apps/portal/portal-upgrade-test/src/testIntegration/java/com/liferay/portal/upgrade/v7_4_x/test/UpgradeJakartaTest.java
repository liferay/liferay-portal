/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.dispatch.test.util.DispatchTriggerTestUtil;
import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.DDMTemplateVersion;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateVersionLocalService;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.model.FragmentEntryVersion;
import com.liferay.fragment.service.FragmentCollectionLocalServiceUtil;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectValidationRuleConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectValidationRule;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectValidationRuleService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.security.script.management.test.util.ScriptManagementConfigurationTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.v7_4_x.UpgradeJakarta;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.kaleo.definition.Condition;
import com.liferay.portal.workflow.kaleo.definition.Notification;
import com.liferay.portal.workflow.kaleo.definition.ScriptAction;
import com.liferay.portal.workflow.kaleo.definition.ScriptAssignment;
import com.liferay.portal.workflow.kaleo.definition.Task;
import com.liferay.portal.workflow.kaleo.model.KaleoAction;
import com.liferay.portal.workflow.kaleo.model.KaleoCondition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoLog;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.model.KaleoNotification;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignment;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.util.WorkflowContextUtil;
import com.liferay.portal.workflow.kaleo.service.KaleoActionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoConditionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceTokenLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoLogLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoNodeLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoNotificationLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskAssignmentLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskInstanceTokenLocalService;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.io.InputStream;
import java.io.Serializable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class UpgradeJakartaTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_group = GroupTestUtil.addGroup();

		_fragmentCollection =
			FragmentCollectionLocalServiceUtil.addFragmentCollection(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), StringPool.BLANK,
				_serviceContext);
		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_originalName = PrincipalThreadLocal.getName();
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		_serviceContext = ServiceContextTestUtil.getServiceContext();
		_upgradeProcess = new UpgradeJakarta();

		_user = TestPropsValues.getUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		PrincipalThreadLocal.setName(_user.getUserId());

		ScriptManagementConfigurationTestUtil.save(true);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		FragmentCollectionLocalServiceUtil.deleteFragmentCollection(
			_fragmentCollection);

		LayoutLocalServiceUtil.deleteLayout(_layout);

		GroupTestUtil.deleteGroup(_group);

		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);

		PrincipalThreadLocal.setName(_originalName);

		ScriptManagementConfigurationTestUtil.save(false);
	}

	@Test
	@TestInfo("LPD-52638")
	public void testUpgradeConfiguration() throws Exception {
		DB db = DBManagerUtil.getDB();

		try {
			db.runSQL(
				StringBundler.concat(
					"insert into Configuration_ (configurationId, dictionary) ",
					"values ('", _JAVAX_CLASS_NAME, "', 'key=",
					_JAVAX_CLASS_NAME, "')"));

			_upgradeProcess.upgrade();

			try (Connection connection = DataAccess.getConnection();
				PreparedStatement preparedStatement =
					connection.prepareStatement(
						StringBundler.concat(
							"select dictionary from Configuration_ where ",
							"configurationId = '", _JAKARTA_CLASS_NAME, "'"));
				ResultSet resultSet = preparedStatement.executeQuery()) {

				Assert.assertTrue(resultSet.next());

				Assert.assertEquals(
					resultSet.getString(1), "key=" + _JAKARTA_CLASS_NAME,
					resultSet.getString(1));
			}
		}
		finally {
			db.runSQL(
				"delete from Configuration_ where configurationId = '" +
					_JAKARTA_CLASS_NAME + "'");

			db.runSQL(
				"delete from Configuration_ where configurationId = '" +
					_JAVAX_CLASS_NAME + "'");
		}
	}

	@Test
	@TestInfo("LPD-52638")
	public void testUpgradeDDMFieldAttribute() throws Throwable {
		DB db = DBManagerUtil.getDB();

		try {
			db.runSQL(
				StringBundler.concat(
					"insert into DDMFieldAttribute (fieldAttributeId, ",
					"largeAttributeValue) values (10000,'", _JAVAX_IMPORT,
					"')"));

			_upgradeProcess.upgrade();

			try (Connection connection = DataAccess.getConnection();
				PreparedStatement preparedStatement =
					connection.prepareStatement(
						StringBundler.concat(
							"select largeAttributeValue from ",
							"DDMFieldAttribute where fieldAttributeId = ",
							"10000"));
				ResultSet resultSet = preparedStatement.executeQuery()) {

				Assert.assertTrue(resultSet.next());

				Assert.assertEquals(_JAKARTA_IMPORT, resultSet.getString(1));
			}
		}
		finally {
			db.runSQL(
				"delete from DDMFieldAttribute where fieldAttributeId = 10000");
		}
	}

	@Test
	@TestInfo("LPD-52638")
	public void testUpgradeDDMTemplate() throws Exception {
		DDMTemplate ddmTemplate = _addDDMTemplate(_group);

		_upgradeProcess.upgrade();

		_multiVMPool.clear();

		DDMTemplate updatedDDMTemplate = _ddmTemplateLocalService.getTemplate(
			ddmTemplate.getTemplateId());

		Assert.assertEquals(_JAKARTA_IMPORT, updatedDDMTemplate.getScript());

		_ddmTemplateLocalService.deleteTemplate(ddmTemplate.getTemplateId());
	}

	@Test
	@TestInfo("LPD-52638")
	public void testUpgradeDDMTemplateVersion() throws Exception {
		DDMTemplate ddmTemplate = _addDDMTemplate(_group);

		ddmTemplate = _ddmTemplateLocalService.updateTemplate(
			ddmTemplate.getUserId(), ddmTemplate.getTemplateId(),
			ddmTemplate.getClassPK(), ddmTemplate.getNameMap(),
			ddmTemplate.getDescriptionMap(), ddmTemplate.getType(),
			ddmTemplate.getMode(), ddmTemplate.getLanguage(),
			ddmTemplate.getScript(), ddmTemplate.isCacheable(),
			_serviceContext);

		_upgradeProcess.upgrade();

		_multiVMPool.clear();

		List<DDMTemplateVersion> ddmTemplateVersions =
			_ddmTemplateVersionLocalService.getTemplateVersions(
				ddmTemplate.getTemplateId());

		Assert.assertFalse(ddmTemplateVersions.isEmpty());

		for (DDMTemplateVersion ddmTemplateVersion : ddmTemplateVersions) {
			Assert.assertEquals(
				_JAKARTA_IMPORT, ddmTemplateVersion.getScript());
		}

		_ddmTemplateLocalService.deleteTemplate(ddmTemplate.getTemplateId());
	}

	@Test
	@TestInfo("LPD-52638")
	public void testUpgradeDispatchTrigger() throws Exception {
		DispatchTrigger dispatchTrigger =
			DispatchTriggerTestUtil.randomDispatchTrigger(
				_user, "batch-planner", 1);

		dispatchTrigger.setDispatchTaskSettingsUnicodeProperties(
			new UnicodeProperties(
				HashMapBuilder.put(
					_PARAMETERS_KEY,
					"-Xms256M -Xmx1024M -Djavax.xml.ws.client=xyz"
				).build(),
				false));

		dispatchTrigger = _dispatchTriggerLocalService.addDispatchTrigger(
			null, dispatchTrigger.getUserId(),
			dispatchTrigger.getDispatchTaskExecutorType(),
			dispatchTrigger.getDispatchTaskSettingsUnicodeProperties(),
			dispatchTrigger.getName(), dispatchTrigger.isSystem());

		_upgradeProcess.upgrade();

		_multiVMPool.clear();

		DispatchTrigger updatedDispatchTrigger =
			_dispatchTriggerLocalService.getDispatchTrigger(
				dispatchTrigger.getDispatchTriggerId());

		Assert.assertNotNull(updatedDispatchTrigger);

		UnicodeProperties unicodeProperties =
			updatedDispatchTrigger.getDispatchTaskSettingsUnicodeProperties();

		Assert.assertEquals(
			"-Xms256M -Xmx1024M -Djakarta.xml.ws.client=xyz",
			unicodeProperties.getProperty(_PARAMETERS_KEY));

		_dispatchTriggerLocalService.deleteDispatchTrigger(
			dispatchTrigger.getDispatchTriggerId());
	}

	@Test
	@TestInfo("LPD-52638")
	public void testUpgradeExportImportConfiguration() throws Exception {
		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildExportLayoutSettingsMap(
							TestPropsValues.getUser(), _group.getGroupId(),
							false, new long[0],
							HashMapBuilder.put(
								"className", new String[] {_JAVAX_CLASS_NAME}
							).build()));

		_upgradeProcess.upgrade();

		_multiVMPool.clear();

		ExportImportConfiguration updatedExportImportConfiguration =
			_exportImportConfigurationLocalService.getExportImportConfiguration(
				exportImportConfiguration.getExportImportConfigurationId());

		Assert.assertNotNull(updatedExportImportConfiguration);

		Assert.assertTrue(
			updatedExportImportConfiguration.getSettings(
			).contains(
				_JAKARTA_CLASS_NAME
			));

		_exportImportConfigurationLocalService.deleteExportImportConfiguration(
			exportImportConfiguration.getExportImportConfigurationId());
	}

	@Test
	@TestInfo("LPD-52638")
	public void testUpgradeFragmentEntry() throws Exception {
		FragmentEntry fragmentEntry = _addFragmentEntry();

		_upgradeProcess.upgrade();

		_multiVMPool.clear();

		FragmentEntry updatedFragmentEntry =
			_fragmentEntryLocalService.getFragmentEntry(
				fragmentEntry.getFragmentEntryId());

		Assert.assertNotNull(updatedFragmentEntry);

		Assert.assertEquals(
			_JAKARTA_CONFIGURATION, updatedFragmentEntry.getConfiguration());
		Assert.assertEquals(_JAKARTA_HTML, updatedFragmentEntry.getHtml());

		_fragmentEntryLocalService.deleteFragmentEntry(fragmentEntry);
	}

	@Test
	@TestInfo("LPD-52638")
	public void testUpgradeFragmentEntryLink() throws Exception {
		FragmentEntry fragmentEntry = _addFragmentEntry();

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), 0,
				fragmentEntry.getFragmentEntryId(),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()),
				_layout.getPlid(), fragmentEntry.getCss(), _JAVAX_HTML,
				fragmentEntry.getJs(), _JAVAX_CONFIGURATION,
				"{\"javax.servlet.test.UpgradeJakartaTest\":{\"" +
					"editable\":true}}",
				StringPool.BLANK, 0, null, fragmentEntry.getType(),
				_serviceContext);

		_upgradeProcess.upgrade();

		_multiVMPool.clear();

		FragmentEntryLink updatedFragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				fragmentEntryLink.getFragmentEntryLinkId());

		Assert.assertNotNull(updatedFragmentEntryLink);

		Assert.assertEquals(
			_JAKARTA_CONFIGURATION,
			updatedFragmentEntryLink.getConfiguration());
		Assert.assertEquals(
			"{\"jakarta.servlet.test.UpgradeJakartaTest\":{\"editable\":true}}",
			updatedFragmentEntryLink.getEditableValues());
		Assert.assertEquals(_JAKARTA_HTML, updatedFragmentEntryLink.getHtml());

		_fragmentEntryLinkLocalService.deleteFragmentEntryLink(
			fragmentEntryLink);

		_fragmentEntryLocalService.deleteFragmentEntry(fragmentEntry);
	}

	@Test
	@TestInfo("LPD-52638")
	public void testUpgradeFragmentEntryVersion() throws Exception {
		FragmentEntry fragmentEntry = _addFragmentEntry();

		fragmentEntry.setCss(RandomTestUtil.randomString());

		fragmentEntry = _fragmentEntryLocalService.updateFragmentEntry(
			fragmentEntry);

		_upgradeProcess.upgrade();

		_multiVMPool.clear();

		List<FragmentEntryVersion> fragmentEntryVersions =
			_fragmentEntryLocalService.getVersions(fragmentEntry);

		Assert.assertFalse(fragmentEntryVersions.isEmpty());

		for (FragmentEntryVersion updatedFragmentEntryVersion :
				fragmentEntryVersions) {

			Assert.assertEquals(
				_JAKARTA_CONFIGURATION,
				updatedFragmentEntryVersion.getConfiguration());
			Assert.assertEquals(
				_JAKARTA_HTML, updatedFragmentEntryVersion.getHtml());
		}

		_fragmentEntryLocalService.deleteFragmentEntry(fragmentEntry);
	}

	@Test
	@TestInfo("LPD-52638")
	public void testUpgradeKaleoAction() throws Exception {
		KaleoInstance kaleoInstance = _addKaleoInstance();

		KaleoNode kaleoNode = _addKaleoNode(kaleoInstance);

		KaleoAction kaleoAction = _kaleoActionLocalService.addKaleoAction(
			KaleoNode.class.getName(), kaleoNode.getKaleoNodeId(),
			kaleoInstance.getKaleoDefinitionId(),
			kaleoInstance.getKaleoDefinitionVersionId(), kaleoNode.getName(),
			new ScriptAction(
				StringUtil.randomString(), StringUtil.randomString(),
				"onAssignment", StringPool.BLANK, "groovy", StringPool.BLANK,
				0),
			_serviceContext);

		kaleoAction = _kaleoActionLocalService.getKaleoAction(
			kaleoAction.getKaleoActionId());

		kaleoAction.setScript(_JAVAX_SCRIPT);

		kaleoAction = _kaleoActionLocalService.updateKaleoAction(kaleoAction);

		_upgradeProcess.upgrade();

		_multiVMPool.clear();

		KaleoAction updatedKaleoAction =
			_kaleoActionLocalService.getKaleoAction(
				kaleoAction.getKaleoActionId());

		Assert.assertNotNull(updatedKaleoAction);

		Assert.assertEquals(_JAKARTA_SCRIPT, updatedKaleoAction.getScript());

		_kaleoActionLocalService.deleteKaleoAction(kaleoAction);
		_kaleoInstanceLocalService.deleteKaleoInstance(kaleoInstance);
		_kaleoNodeLocalService.deleteKaleoNode(kaleoNode);
	}

	@Test
	@TestInfo("LPD-52638")
	public void testUpgradeKaleoCondition() throws Exception {
		KaleoInstance kaleoInstance = _addKaleoInstance();

		KaleoNode kaleoNode = _addKaleoNode(kaleoInstance);

		Condition condition = new Condition(
			RandomTestUtil.randomString(), StringPool.BLANK, _JAVAX_SCRIPT,
			"java", StringPool.BLANK);

		KaleoCondition kaleoCondition =
			_kaleoConditionLocalService.addKaleoCondition(
				kaleoInstance.getKaleoDefinitionId(),
				kaleoInstance.getKaleoDefinitionVersionId(),
				kaleoNode.getKaleoNodeId(), condition, _serviceContext);

		_upgradeProcess.upgrade();

		_multiVMPool.clear();

		KaleoCondition updatedKaleoCondition =
			_kaleoConditionLocalService.getKaleoCondition(
				kaleoCondition.getKaleoConditionId());

		Assert.assertNotNull(updatedKaleoCondition);

		Assert.assertEquals(_JAKARTA_SCRIPT, updatedKaleoCondition.getScript());

		_kaleoConditionLocalService.deleteKaleoCondition(kaleoCondition);
		_kaleoInstanceLocalService.deleteKaleoInstance(kaleoInstance);
		_kaleoNodeLocalService.deleteKaleoNode(kaleoNode);
	}

	@Test
	@TestInfo("LPD-52638")
	public void testUpgradeKaleoDefinition() throws Exception {
		KaleoInstance kaleoInstance = _addKaleoInstance();

		KaleoNode kaleoNode = _addKaleoNode(kaleoInstance);

		KaleoDefinition kaleoDefinition = _addKaleoDefinition();

		_upgradeProcess.upgrade();

		_multiVMPool.clear();

		KaleoDefinition updatedKaleoDefinition =
			_kaleoDefinitionLocalService.getKaleoDefinition(
				kaleoDefinition.getKaleoDefinitionId());

		Assert.assertNotNull(updatedKaleoDefinition);

		Assert.assertTrue(
			updatedKaleoDefinition.getContentAsXML(
			).contains(
				_JAKARTA_IMPORT
			));

		_kaleoDefinitionLocalService.deleteKaleoDefinition(kaleoDefinition);
		_kaleoInstanceLocalService.deleteKaleoInstance(kaleoInstance);
		_kaleoNodeLocalService.deleteKaleoNode(kaleoNode);
	}

	@Test
	@TestInfo("LPD-52638")
	public void testUpgradeKaleoDefinitionVersion() throws Exception {
		KaleoInstance kaleoInstance = _addKaleoInstance();

		KaleoNode kaleoNode = _addKaleoNode(kaleoInstance);

		KaleoDefinition kaleoDefinition = _addKaleoDefinition();

		_upgradeProcess.upgrade();

		_multiVMPool.clear();

		KaleoDefinition updatedKaleoDefinition =
			_kaleoDefinitionLocalService.getKaleoDefinition(
				kaleoDefinition.getKaleoDefinitionId());

		Assert.assertNotNull(updatedKaleoDefinition);

		Assert.assertTrue(
			updatedKaleoDefinition.getContentAsXML(
			).contains(
				_JAKARTA_IMPORT
			));

		List<KaleoDefinitionVersion> kaleoDefinitionVersions =
			kaleoDefinition.getKaleoDefinitionVersions();

		Assert.assertEquals(
			kaleoDefinitionVersions.toString(), 1,
			kaleoDefinitionVersions.size());

		Assert.assertTrue(
			kaleoDefinitionVersions.get(
				0
			).getContentAsXML(
			).contains(
				_JAKARTA_IMPORT
			));

		_kaleoDefinitionLocalService.deleteKaleoDefinition(kaleoDefinition);
		_kaleoInstanceLocalService.deleteKaleoInstance(kaleoInstance);
		_kaleoNodeLocalService.deleteKaleoNode(kaleoNode);
	}

	@Test
	@TestInfo("LPD-52638")
	public void testUpgradeKaleoInstance() throws Exception {
		KaleoInstance kaleoInstance = _addKaleoInstance();

		KaleoNode kaleoNode = _addKaleoNode(kaleoInstance);

		_upgradeProcess.upgrade();

		_multiVMPool.clear();

		KaleoInstance updatedKaleoInstance =
			_kaleoInstanceLocalService.getKaleoInstance(
				kaleoInstance.getKaleoInstanceId());

		Assert.assertNotNull(updatedKaleoInstance);

		Map<String, Serializable> workflowContext = WorkflowContextUtil.convert(
			updatedKaleoInstance.getWorkflowContext());

		Assert.assertEquals(
			_JAKARTA_URL, workflowContext.get(WorkflowConstants.CONTEXT_URL));

		_kaleoInstanceLocalService.deleteKaleoInstance(kaleoInstance);
		_kaleoNodeLocalService.deleteKaleoNode(kaleoNode);
	}

	@Test
	@TestInfo("LPD-52638")
	public void testUpgradeKaleoLog() throws Exception {
		KaleoInstance kaleoInstance = _addKaleoInstance();

		KaleoNode kaleoNode = _addKaleoNode(kaleoInstance);

		KaleoInstanceToken kaleoInstanceToken = _addKaleoInstanceToken(
			kaleoInstance, kaleoNode);

		KaleoTaskInstanceToken kaleoTaskInstanceToken =
			_addKaleoTaskInstaceToken(kaleoInstance, kaleoInstanceToken);

		KaleoLog kaleoLog = _kaleoLogLocalService.addTaskAssignmentKaleoLog(
			Collections.emptyList(), null, kaleoTaskInstanceToken,
			StringPool.BLANK,
			WorkflowContextUtil.convert(kaleoInstance.getWorkflowContext()),
			_serviceContext);

		_upgradeProcess.upgrade();

		_multiVMPool.clear();

		KaleoLog updatedKaleoLog = _kaleoLogLocalService.getKaleoLog(
			kaleoLog.getKaleoLogId());

		Assert.assertNotNull(updatedKaleoLog);

		Map<String, Serializable> workflowContext = WorkflowContextUtil.convert(
			updatedKaleoLog.getWorkflowContext());

		Assert.assertEquals(
			_JAKARTA_URL, workflowContext.get(WorkflowConstants.CONTEXT_URL));

		_kaleoInstanceLocalService.deleteKaleoInstance(kaleoInstance);
		_kaleoLogLocalService.deleteKaleoLog(kaleoLog);
		_kaleoNodeLocalService.deleteKaleoNode(kaleoNode);
		_kaleoTaskInstanceTokenLocalService.deleteKaleoTaskInstanceToken(
			kaleoTaskInstanceToken);
	}

	@Test
	@TestInfo("LPD-52638")
	public void testUpgradeKaleoNotification() throws Exception {
		KaleoInstance kaleoInstance = _addKaleoInstance();

		KaleoNode kaleoNode = _addKaleoNode(kaleoInstance);

		KaleoDefinition kaleoDefinition = _addKaleoDefinition();

		KaleoNotification kaleoNotification =
			_kaleoNotificationLocalService.addKaleoNotification(
				KaleoNode.class.getName(), kaleoInstance.getClassPK(),
				kaleoDefinition.getKaleoDefinitionId(),
				kaleoDefinition.getKaleoDefinitionVersions(
				).get(
					0
				).getKaleoDefinitionVersionId(),
				kaleoNode.getName(),
				new Notification(
					StringUtil.randomString(), StringUtil.randomString(),
					"onTimer", _JAVAX_SCRIPT, "freemarker"),
				_serviceContext);

		_upgradeProcess.upgrade();

		_multiVMPool.clear();

		KaleoNotification updatedKaleoNotification =
			_kaleoNotificationLocalService.getKaleoNotification(
				kaleoNotification.getKaleoNotificationId());

		Assert.assertNotNull(updatedKaleoNotification);

		Assert.assertEquals(
			_JAKARTA_SCRIPT, updatedKaleoNotification.getTemplate());

		_kaleoDefinitionLocalService.deleteKaleoDefinition(kaleoDefinition);
		_kaleoInstanceLocalService.deleteKaleoInstance(kaleoInstance);
		_kaleoNodeLocalService.deleteKaleoNode(kaleoNode);
		_kaleoNotificationLocalService.deleteKaleoNotification(
			kaleoNotification);
	}

	@Test
	@TestInfo("LPD-52638")
	public void testUpgradeKaleoTaskAssignment() throws Exception {
		KaleoInstance kaleoInstance = _addKaleoInstance();

		KaleoNode kaleoNode = _addKaleoNode(kaleoInstance);

		KaleoTaskAssignment kaleoTaskAssignment =
			_kaleoTaskAssignmentLocalService.addKaleoTaskAssignment(
				KaleoNode.class.getName(), kaleoInstance.getClassPK(),
				kaleoInstance.getKaleoDefinitionId(),
				kaleoInstance.getKaleoDefinitionVersionId(),
				new ScriptAssignment(
					_JAVAX_SCRIPT, "java", RandomTestUtil.randomString()),
				_serviceContext);

		_upgradeProcess.upgrade();

		_multiVMPool.clear();

		KaleoTaskAssignment updatedKaleoTaskAssignment =
			_kaleoTaskAssignmentLocalService.getKaleoTaskAssignment(
				kaleoTaskAssignment.getKaleoTaskAssignmentId());

		Assert.assertNotNull(updatedKaleoTaskAssignment);

		Assert.assertEquals(
			_JAKARTA_SCRIPT, updatedKaleoTaskAssignment.getAssigneeScript());

		_kaleoInstanceLocalService.deleteKaleoInstance(kaleoInstance);
		_kaleoNodeLocalService.deleteKaleoNode(kaleoNode);
		_kaleoTaskAssignmentLocalService.deleteKaleoTaskAssignment(
			kaleoTaskAssignment);
	}

	@Test
	@TestInfo("LPD-52638")
	public void testUpgradeKaleoTaskInstanceToken() throws Exception {
		KaleoInstance kaleoInstance = _addKaleoInstance();

		KaleoNode kaleoNode = _addKaleoNode(kaleoInstance);

		KaleoInstanceToken kaleoInstanceToken = _addKaleoInstanceToken(
			kaleoInstance, kaleoNode);

		KaleoTaskInstanceToken kaleoTaskInstanceToken =
			_addKaleoTaskInstaceToken(kaleoInstance, kaleoInstanceToken);

		_upgradeProcess.upgrade();

		_multiVMPool.clear();

		KaleoTaskInstanceToken updatedKaleoTaskInstanceToken =
			_kaleoTaskInstanceTokenLocalService.getKaleoTaskInstanceToken(
				kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId());

		Assert.assertNotNull(updatedKaleoTaskInstanceToken);

		Map<String, Serializable> workflowContext = WorkflowContextUtil.convert(
			updatedKaleoTaskInstanceToken.getWorkflowContext());

		Assert.assertEquals(
			_JAKARTA_URL, workflowContext.get(WorkflowConstants.CONTEXT_URL));

		_kaleoInstanceLocalService.deleteKaleoInstance(kaleoInstance);
		_kaleoInstanceTokenLocalService.deleteKaleoInstanceToken(
			kaleoInstanceToken);
		_kaleoNodeLocalService.deleteKaleoNode(kaleoNode);
		_kaleoTaskInstanceTokenLocalService.deleteKaleoTaskInstanceToken(
			kaleoTaskInstanceToken);
	}

	@Test
	@TestInfo("LPD-52638")
	public void testUpgradeObjectAction() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"a" + RandomTestUtil.randomString()
					).build()));

		ObjectAction objectAction = _objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), true, StringPool.BLANK,
			RandomTestUtil.randomString(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_GROOVY,
			ObjectActionTriggerConstants.KEY_STANDALONE,
			UnicodePropertiesBuilder.put(
				"script", _JAVAX_SCRIPT
			).build(),
			false);

		_upgradeProcess.upgrade();

		_multiVMPool.clear();

		ObjectAction updatedObjectAction =
			_objectActionLocalService.getObjectAction(
				objectAction.getObjectActionId());

		Assert.assertNotNull(updatedObjectAction);

		Assert.assertEquals(
			"script=" + _JAKARTA_SCRIPT + "\n",
			updatedObjectAction.getParameters());

		_objectActionLocalService.deleteObjectAction(objectAction);

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Test
	@TestInfo("LPD-52638")
	public void testUpgradeObjectValidationRule() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"a" + RandomTestUtil.randomString()
					).build()));

		ObjectValidationRule objectValidationRule =
			_objectValidationRuleService.addObjectValidationRule(
				StringPool.BLANK, objectDefinition.getObjectDefinitionId(),
				true, ObjectValidationRuleConstants.ENGINE_TYPE_GROOVY,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
				_JAVAX_SCRIPT, false, Collections.emptyList());

		_upgradeProcess.upgrade();

		_multiVMPool.clear();

		ObjectValidationRule updatedObjectValidationRule =
			_objectValidationRuleService.getObjectValidationRule(
				objectValidationRule.getObjectValidationRuleId());

		Assert.assertNotNull(updatedObjectValidationRule);

		Assert.assertEquals(
			_JAKARTA_SCRIPT, updatedObjectValidationRule.getScript());

		_objectValidationRuleService.deleteObjectValidationRule(
			objectValidationRule.getObjectValidationRuleId());

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	private DDMTemplate _addDDMTemplate(Group group) throws Exception {
		String languageId = UpgradeProcessUtil.getDefaultLanguageId(
			TestPropsValues.getCompanyId());

		return _ddmTemplateLocalService.addTemplate(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			PortalUtil.getClassNameId(
				"com.liferay.dynamic.data.lists.model.DDLRecord"),
			0,
			PortalUtil.getClassNameId(
				"com.liferay.dynamic.data.lists.model.DDLRecordSet"),
			RandomTestUtil.randomString(),
			Collections.singletonMap(
				LocaleUtil.fromLanguageId(languageId),
				RandomTestUtil.randomString()),
			null, DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY, null,
			TemplateConstants.LANG_TYPE_VM, _JAVAX_IMPORT, false, false, null,
			null, _serviceContext);
	}

	private FragmentEntry _addFragmentEntry() throws Exception {
		return _fragmentEntryLocalService.addFragmentEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), null,
			RandomTestUtil.randomString(), StringPool.BLANK, _JAVAX_HTML,
			RandomTestUtil.randomString(), false, _JAVAX_CONFIGURATION, null, 0,
			false, false, FragmentConstants.TYPE_COMPONENT, null,
			WorkflowConstants.STATUS_APPROVED, _serviceContext);
	}

	private KaleoDefinition _addKaleoDefinition() throws Exception {
		return _kaleoDefinitionLocalService.addKaleoDefinition(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			_read("javax-workflow-definition.xml"),
			WorkflowDefinitionConstants.SCOPE_ALL, 1, _serviceContext);
	}

	private KaleoInstance _addKaleoInstance() throws Exception {
		return _kaleoInstanceLocalService.addKaleoInstance(
			1, 1, "Test", 1,
			HashMapBuilder.<String, Serializable>put(
				WorkflowConstants.CONTEXT_ENTRY_CLASS_NAME,
				(Serializable)UpgradeJakarta.class.getName()
			).put(
				WorkflowConstants.CONTEXT_SERVICE_CONTEXT,
				(Serializable)_serviceContext
			).put(
				WorkflowConstants.CONTEXT_URL, _JAVAX_URL
			).build(),
			_serviceContext);
	}

	private KaleoInstanceToken _addKaleoInstanceToken(
			KaleoInstance kaleoInstance, KaleoNode kaleoNode)
		throws Exception {

		return _kaleoInstanceTokenLocalService.addKaleoInstanceToken(
			kaleoNode.getKaleoNodeId(), kaleoInstance.getKaleoDefinitionId(),
			kaleoInstance.getKaleoDefinitionVersionId(),
			kaleoInstance.getKaleoInstanceId(), 0,
			WorkflowContextUtil.convert(kaleoInstance.getWorkflowContext()),
			_serviceContext);
	}

	private KaleoNode _addKaleoNode(KaleoInstance kaleoInstance)
		throws Exception {

		return _kaleoNodeLocalService.addKaleoNode(
			kaleoInstance.getKaleoDefinitionId(),
			kaleoInstance.getKaleoDefinitionVersionId(),
			new Task("task", StringPool.BLANK), _serviceContext);
	}

	private KaleoTaskInstanceToken _addKaleoTaskInstaceToken(
			KaleoInstance kaleoInstance, KaleoInstanceToken kaleoInstanceToken)
		throws Exception {

		return _kaleoTaskInstanceTokenLocalService.addKaleoTaskInstanceToken(
			kaleoInstanceToken.getKaleoInstanceTokenId(), 1, "task",
			Collections.emptyList(), null,
			WorkflowContextUtil.convert(kaleoInstance.getWorkflowContext()),
			_serviceContext);
	}

	private String _read(String name) throws Exception {
		try (InputStream inputStream =
				UpgradeJakartaTest.class.getResourceAsStream(
					"dependencies/" + name)) {

			return StringUtil.read(inputStream);
		}
	}

	private static final String _JAKARTA_CLASS_NAME =
		"jakarta.portlet.test.UpgradeJakartaTest";

	private static final String _JAKARTA_CONFIGURATION = StringBundler.concat(
		"{\"fieldSets\": [{\"fields\": [{\"dataType\": \"string\",\"",
		"defaultValue\": false,\"label\": \"jakarta-servlet-test-",
		"UpgradeJakartaTest\",\"name\": \"test1\",\"type\": ",
		"\"checkbox\"}]}]}");

	private static final String _JAKARTA_HTML =
		"<#assign upgradeProcess = serviceLocator.findService(\"" +
			"jakarta.servlet.test.UpgradeJakartaTest\")/>";

	private static final String _JAKARTA_IMPORT =
		"import jakarta.servlet.test.UpgradeJakartaTest;";

	private static final String _JAKARTA_SCRIPT =
		"System.out.println(\"import jakarta.servlet.GenericServlet\");";

	private static final String _JAKARTA_URL =
		"https://liferay.com?portletAction=jakarta.servlet.action";

	private static final String _JAVAX_CLASS_NAME =
		"javax.portlet.test.UpgradeJakartaTest";

	private static final String _JAVAX_CONFIGURATION = StringBundler.concat(
		"{\"fieldSets\": [{\"fields\": [{\"dataType\": \"string\",\"",
		"defaultValue\": false,\"label\": \"javax-servlet-test-",
		"UpgradeJakartaTest\",\"name\": \"test1\",\"type\": ",
		"\"checkbox\"}]}]}");

	private static final String _JAVAX_HTML =
		"<#assign upgradeProcess = serviceLocator.findService(\"" +
			"javax.servlet.test.UpgradeJakartaTest\")/>";

	private static final String _JAVAX_IMPORT =
		"import javax.servlet.test.UpgradeJakartaTest;";

	private static final String _JAVAX_SCRIPT =
		"System.out.println(\"import javax.servlet.GenericServlet\");";

	private static final String _JAVAX_URL =
		"https://liferay.com?portletAction=javax.servlet.action";

	private static final String _PARAMETERS_KEY = "JAVA_OPTS";

	private static FragmentCollection _fragmentCollection;
	private static Group _group;
	private static Layout _layout;
	private static String _originalName;
	private static PermissionChecker _originalPermissionChecker;
	private static ServiceContext _serviceContext;
	private static UpgradeProcess _upgradeProcess;
	private static User _user;

	@Inject
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Inject
	private DDMTemplateVersionLocalService _ddmTemplateVersionLocalService;

	@Inject
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

	@Inject
	private EntityCache _entityCache;

	@Inject
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Inject
	private FinderCache _finderCache;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject
	private KaleoActionLocalService _kaleoActionLocalService;

	@Inject
	private KaleoConditionLocalService _kaleoConditionLocalService;

	@Inject
	private KaleoDefinitionLocalService _kaleoDefinitionLocalService;

	@Inject
	private KaleoInstanceLocalService _kaleoInstanceLocalService;

	@Inject
	private KaleoInstanceTokenLocalService _kaleoInstanceTokenLocalService;

	@Inject
	private KaleoLogLocalService _kaleoLogLocalService;

	@Inject
	private KaleoNodeLocalService _kaleoNodeLocalService;

	@Inject
	private KaleoNotificationLocalService _kaleoNotificationLocalService;

	@Inject
	private KaleoTaskAssignmentLocalService _kaleoTaskAssignmentLocalService;

	@Inject
	private KaleoTaskInstanceTokenLocalService
		_kaleoTaskInstanceTokenLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectValidationRuleService _objectValidationRuleService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}