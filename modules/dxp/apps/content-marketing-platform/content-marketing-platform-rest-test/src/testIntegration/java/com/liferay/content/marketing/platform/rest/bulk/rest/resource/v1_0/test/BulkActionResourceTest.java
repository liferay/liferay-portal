/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.rest.bulk.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.bulk.rest.client.dto.v1_0.AddObjectToProjectBulkSelectionAction;
import com.liferay.bulk.rest.client.dto.v1_0.BulkAction;
import com.liferay.bulk.rest.client.dto.v1_0.BulkActionItem;
import com.liferay.bulk.rest.client.dto.v1_0.BulkActionTask;
import com.liferay.bulk.rest.client.resource.v1_0.BulkActionResource;
import com.liferay.bulk.selection.constants.BulkSelectionActionStatusConstants;
import com.liferay.content.marketing.platform.test.util.CMPTestUtil;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.JAXRSWhiteboardTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class BulkActionResourceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		JAXRSWhiteboardTestUtil.ensureReady();
	}

	@Before
	public void setUp() throws Exception {
		_testCompany = CompanyLocalServiceUtil.getCompany(
			TestPropsValues.getCompanyId());

		User testCompanyAdminUser = UserTestUtil.getAdminUser(
			_testCompany.getCompanyId());

		_bulkActionResource = BulkActionResource.builder(
		).authentication(
			testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			_testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		_cmsBasicWebContentObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", _testCompany.getCompanyId());

		_depotEntry2 = _addDepotEntry(false);
	}

	@Test
	public void testPostBulkAction() throws Exception {
		_testPostBulkActionWithTypeAddObjectToProject();
	}

	private DepotEntry _addDepotEntry(boolean trashEnabled) throws Exception {
		Map<Locale, String> nameMap = Collections.singletonMap(
			LocaleUtil.US, RandomTestUtil.randomString());

		ServiceContext serviceContext = new ServiceContext() {
			{
				setCompanyId(_testCompany.getCompanyId());
				setUserId(TestPropsValues.getUserId());
			}
		};

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			nameMap, null, DepotConstants.TYPE_SPACE, serviceContext);

		return _depotEntryLocalService.updateDepotEntry(
			depotEntry.getDepotEntryId(), nameMap, null, Collections.emptyMap(),
			null,
			UnicodePropertiesBuilder.put(
				"trashEnabled", trashEnabled
			).build(),
			serviceContext);
	}

	private void _assertCMPProjectLinks(
			ObjectDefinition cmpProjectLinkObjectDefinition, long groupId,
			ObjectEntry[] objectEntries)
		throws Exception {

		List<ObjectEntry> cmpProjectLinkObjectEntries =
			_objectEntryLocalService.getObjectEntries(
				groupId, cmpProjectLinkObjectDefinition.getObjectDefinitionId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			cmpProjectLinkObjectEntries.toString(), objectEntries.length,
			cmpProjectLinkObjectEntries.size());

		Map<String, Map<String, Serializable>> valuesMaps = new HashMap<>();

		for (ObjectEntry cmpProjectLinkObjectEntry :
				cmpProjectLinkObjectEntries) {

			Map<String, Serializable> values =
				cmpProjectLinkObjectEntry.getValues();

			valuesMaps.put(
				GetterUtil.getString(values.get("classExternalReferenceCode")),
				values);
		}

		for (ObjectEntry objectEntry : objectEntries) {
			Group group = GroupLocalServiceUtil.getGroup(
				objectEntry.getGroupId());

			Map<String, Serializable> values = valuesMaps.get(
				objectEntry.getExternalReferenceCode());

			Assert.assertEquals(
				objectEntry.getModelClassName(),
				GetterUtil.getString(values.get("className")));
			Assert.assertEquals(
				group.getExternalReferenceCode(),
				GetterUtil.getString(values.get("groupExternalReferenceCode")));
		}
	}

	private void _assertNumberOfFailedItems(
			BulkActionTask bulkActionTask, int expectedNumberOfFailedItems)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			GetterUtil.getLong(bulkActionTask.getId()));

		Map<String, Serializable> values = objectEntry.getValues();

		Assert.assertEquals(
			expectedNumberOfFailedItems,
			GetterUtil.getInteger(values.get("numberOfFailedItems")));
	}

	private Map<String, Serializable> _getObjectEntryValues() {
		return HashMapBuilder.<String, Serializable>put(
			"title_i18n",
			HashMapBuilder.put(
				_LANGUAGE_ID, RandomTestUtil.randomString()
			).build()
		).build();
	}

	private BulkActionTask _postBulkAction(BulkAction bulkAction)
		throws Exception {

		BulkActionTask bulkActionTask = _bulkActionResource.postBulkAction(
			null, null, null, null, null, null, null, null, bulkAction);

		Assert.assertNotNull(bulkActionTask.getId());

		_waitForFinish(GetterUtil.getLong(bulkActionTask.getId()));

		BulkActionItem[] bulkActionItems = bulkAction.getBulkActionItems();

		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			bulkActionTask.getId());

		Map<String, Serializable> values = objectEntry.getValues();

		Assert.assertEquals(
			bulkActionItems.length,
			GetterUtil.getInteger(values.get("numberOfItems")));

		return bulkActionTask;
	}

	private void _testPostBulkActionWithTypeAddObjectToProject()
		throws Exception {

		// Invalid project scope key

		ObjectEntry cmsBasicWebContentObjectEntry1 =
			ObjectEntryTestUtil.addObjectEntry(
				_depotEntry2.getGroupId(), _cmsBasicWebContentObjectDefinition,
				_getObjectEntryValues());
		ObjectEntry cmsBasicWebContentObjectEntry2 =
			ObjectEntryTestUtil.addObjectEntry(
				_depotEntry2.getGroupId(), _cmsBasicWebContentObjectDefinition,
				_getObjectEntryValues());

		AddObjectToProjectBulkSelectionAction bulkAction =
			new AddObjectToProjectBulkSelectionAction();

		bulkAction.setBulkActionItems(
			_toBulkActionItems(
				_cmsBasicWebContentObjectDefinition,
				cmsBasicWebContentObjectEntry1,
				cmsBasicWebContentObjectEntry2));
		bulkAction.setProjectScopeKeys(
			new String[] {RandomTestUtil.randomString()});
		bulkAction.setType(
			BulkAction.Type.ADD_OBJECT_TO_PROJECT_BULK_SELECTION_ACTION);

		_assertNumberOfFailedItems(_postBulkAction(bulkAction), 2);

		// Link basic web content to projects

		bulkAction = new AddObjectToProjectBulkSelectionAction();

		bulkAction.setBulkActionItems(
			_toBulkActionItems(
				_cmsBasicWebContentObjectDefinition,
				cmsBasicWebContentObjectEntry1,
				cmsBasicWebContentObjectEntry2));

		CMPTestUtil.getOrAddGroup(BulkActionResourceTest.class);

		ObjectEntry cmpProjectObjectEntry1 =
			CMPTestUtil.addCMPProjectObjectEntry();

		_projectDepotEntry1 = _depotEntryLocalService.fetchGroupDepotEntry(
			cmpProjectObjectEntry1.getGroupId());

		ObjectEntry cmpProjectObjectEntry2 =
			CMPTestUtil.addCMPProjectObjectEntry();

		_projectDepotEntry2 = _depotEntryLocalService.fetchGroupDepotEntry(
			cmpProjectObjectEntry2.getGroupId());

		bulkAction.setProjectScopeKeys(
			new String[] {
				String.valueOf(cmpProjectObjectEntry1.getGroupId()),
				String.valueOf(cmpProjectObjectEntry2.getGroupId())
			});

		bulkAction.setType(
			BulkAction.Type.ADD_OBJECT_TO_PROJECT_BULK_SELECTION_ACTION);

		_assertNumberOfFailedItems(_postBulkAction(bulkAction), 0);

		ObjectDefinition cmpProjectLinkObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_PROJECT_LINK", _testCompany.getCompanyId());
		ObjectEntry[] cmsBasicWebContentObjectEntries = {
			cmsBasicWebContentObjectEntry1, cmsBasicWebContentObjectEntry2
		};

		_assertCMPProjectLinks(
			cmpProjectLinkObjectDefinition, cmpProjectObjectEntry1.getGroupId(),
			cmsBasicWebContentObjectEntries);
		_assertCMPProjectLinks(
			cmpProjectLinkObjectDefinition, cmpProjectObjectEntry2.getGroupId(),
			cmsBasicWebContentObjectEntries);

		// Skip duplicate basic web content

		_assertNumberOfFailedItems(_postBulkAction(bulkAction), 0);

		_assertCMPProjectLinks(
			cmpProjectLinkObjectDefinition, cmpProjectObjectEntry1.getGroupId(),
			cmsBasicWebContentObjectEntries);
		_assertCMPProjectLinks(
			cmpProjectLinkObjectDefinition, cmpProjectObjectEntry2.getGroupId(),
			cmsBasicWebContentObjectEntries);
	}

	private BulkActionItem[] _toBulkActionItems(
			ObjectDefinition objectDefinition, Object... objects)
		throws Exception {

		return TransformUtil.transform(
			objects,
			object -> new BulkActionItem() {
				{
					if (object instanceof ObjectDefinition) {
						ObjectDefinition objectDefinition =
							(ObjectDefinition)object;

						setClassExternalReferenceCode(
							objectDefinition::getExternalReferenceCode);

						setClassName(ObjectDefinition.class.getName());
						setClassPK(objectDefinition::getObjectDefinitionId);
						setName(objectDefinition::getName);
					}
					else if (object instanceof ObjectEntry) {
						ObjectEntry objectEntry = (ObjectEntry)object;

						setClassExternalReferenceCode(
							objectEntry::getExternalReferenceCode);

						setClassName(objectDefinition::getClassName);
						setClassPK(objectEntry::getObjectEntryId);
						setName(objectEntry::getTitleValue);
					}
					else if (object instanceof ObjectEntryFolder) {
						ObjectEntryFolder objectEntryFolder =
							(ObjectEntryFolder)object;

						setClassExternalReferenceCode(
							objectEntryFolder::getExternalReferenceCode);
						setClassName(objectEntryFolder::getModelClassName);
						setClassPK(objectEntryFolder::getObjectEntryFolderId);
						setName(objectEntryFolder::getName);
					}
				}
			},
			BulkActionItem.class);
	}

	private void _waitForFinish(long bulkActionTaskId) throws Exception {
		while (true) {
			ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
				bulkActionTaskId);

			Map<String, Serializable> values = objectEntry.getValues();

			String executionStatus = (String)values.get("executionStatus");

			if (StringUtil.equals(
					executionStatus,
					BulkSelectionActionStatusConstants.COMPLETED) ||
				StringUtil.equals(
					executionStatus,
					BulkSelectionActionStatusConstants.FAILED)) {

				Assert.assertEquals(
					BulkSelectionActionStatusConstants.COMPLETED,
					executionStatus);

				return;
			}
		}
	}

	private static final String _LANGUAGE_ID = "en_US";

	private BulkActionResource _bulkActionResource;
	private ObjectDefinition _cmsBasicWebContentObjectDefinition;
	private DepotEntry _depotEntry2;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@DeleteAfterTestRun
	private DepotEntry _projectDepotEntry1;

	@DeleteAfterTestRun
	private DepotEntry _projectDepotEntry2;

	private Company _testCompany;

}