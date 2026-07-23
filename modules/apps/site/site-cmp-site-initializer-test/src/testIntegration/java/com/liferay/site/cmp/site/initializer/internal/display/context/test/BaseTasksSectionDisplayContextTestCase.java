/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.test.util.FrontendDataSetTestUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Fábio Alves
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-58677"))
@RunWith(Arquillian.class)
@Sync
public abstract class BaseTasksSectionDisplayContextTestCase
	extends BaseSectionDisplayContextTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		super.setUp();

		cmpProjectObjectDefinition =
			objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_PROJECT", TestPropsValues.getCompanyId());

		ObjectEntry cmpProjectObjectEntry =
			CMPTestUtil.addCMPProjectObjectEntry();

		cmpProjectObjectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(),
			cmpProjectObjectEntry.getObjectEntryId(),
			cmpProjectObjectEntry.getObjectEntryFolderId(),
			cmpProjectObjectEntry.getValues(),
			ServiceContextTestUtil.getServiceContext());

		assetEntry = _assetEntryLocalService.getEntry(
			cmpProjectObjectDefinition.getClassName(),
			cmpProjectObjectEntry.getObjectEntryId());
	}

	@Test
	public void testGetAdditionalProps() throws Exception {
		Map<String, Object> additionalProps = getAdditionalProps(null);

		Assert.assertEquals(
			cmpProjectObjectDefinition.getObjectDefinitionId(),
			additionalProps.get("cmpProjectObjectDefinitionId"));
		Assert.assertNull(additionalProps.get("cmpProjectObjectEntryId"));
		Assert.assertNotNull(additionalProps.get("states"));

		additionalProps = getAdditionalProps(assetEntry);

		Assert.assertEquals(
			cmpProjectObjectDefinition.getObjectDefinitionId(),
			additionalProps.get("cmpProjectObjectDefinitionId"));
		Assert.assertEquals(
			assetEntry.getClassPK(),
			additionalProps.get("cmpProjectObjectEntryId"));
		Assert.assertNotNull(additionalProps.get("states"));
	}

	@Test
	public void testGetBulkActionDropdownItems() throws Exception {
		List<DropdownItem> bulkActionDropdownItems = getBulkActionDropdownItems(
			null);

		Assert.assertEquals(
			bulkActionDropdownItems.toString(), 4,
			bulkActionDropdownItems.size());

		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"date-time", "update-due-date", "Update Due Date", "post",
			(FDSActionDropdownItem)bulkActionDropdownItems.get(0));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"user", "assign-to", "Assign to...", null,
			(FDSActionDropdownItem)bulkActionDropdownItems.get(1));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"arrow-start", "update-state", "Update State", "post",
			(FDSActionDropdownItem)bulkActionDropdownItems.get(2));
		FrontendDataSetTestUtil.assertFDSActionDropdownItem(
			"trash", "delete", "Delete", null,
			(FDSActionDropdownItem)bulkActionDropdownItems.get(3));
	}

	@Test
	public void testGetCreationMenu() throws Exception {
		DropdownItem dropdownItem = _getDropdownItem(
			getCreationMenu(assetEntry));

		Assert.assertEquals("createTask", getValue(dropdownItem, "action"));
		Assert.assertEquals(
			StringBundler.concat(
				themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
				GroupConstants.CMS_FRIENDLY_URL,
				"/add_project?objectDefinitionId=",
				cmpProjectObjectDefinition.getObjectDefinitionId(), "&plid=",
				themeDisplay.getPlid(), "&redirect=",
				themeDisplay.getURLCurrent(),
				"&action=createProjectGlobalTask"),
			getValue(dropdownItem, "addProjectURL"));
		Assert.assertEquals(
			StringBundler.concat(
				themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
				GroupConstants.CMS_FRIENDLY_URL,
				"/add_task?objectDefinitionId=",
				objectDefinition.getObjectDefinitionId(), "&plid=",
				themeDisplay.getPlid(), "&projectGroupId=0&projectId=0",
				"&redirect=", themeDisplay.getURLCurrent(),
				"&action=createGlobalTask"),
			getValue(dropdownItem, "addTaskURL"));
		Assert.assertEquals(
			String.valueOf(cmpProjectObjectDefinition.getObjectDefinitionId()),
			getValue(dropdownItem, "cmpProjectObjectDefinitionId"));
		Assert.assertEquals("New Task", dropdownItem.get("label"));
		Assert.assertEquals(
			String.valueOf(objectDefinition.getObjectDefinitionId()),
			getValue(dropdownItem, "objectDefinitionId"));
		Assert.assertEquals(
			StringBundler.concat(
				themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
				GroupConstants.CMS_FRIENDLY_URL,
				"/add_task?objectDefinitionId=",
				objectDefinition.getObjectDefinitionId(), "&plid=",
				themeDisplay.getPlid(), "&projectGroupId=",
				assetEntry.getGroupId(), "&projectId=", assetEntry.getClassPK(),
				"&redirect=", themeDisplay.getURLCurrent()),
			getValue(dropdownItem, "redirect"));
		Assert.assertEquals("Task", getValue(dropdownItem, "title"));

		dropdownItem = _getDropdownItem(getCreationMenu(null));

		Assert.assertEquals("New", dropdownItem.get("label"));
		Assert.assertTrue(Validator.isNull(getValue(dropdownItem, "redirect")));
	}

	@Test
	public void testGetFDSFilters() throws Exception {
		List<FDSFilter> fdsFilters = getFDSFilters(null);

		Assert.assertEquals(fdsFilters.toString(), 6, fdsFilters.size());

		assertFDSFilter(
			FDSEntityFieldTypes.STRING, "cmpAssignTo", "assignee",
			fdsFilters.get(0));
		assertFDSFilter(
			FDSEntityFieldTypes.DATE_TIME, "dateCreated", "create-date",
			fdsFilters.get(1));
		assertFDSFilter(
			FDSEntityFieldTypes.DATE_TIME, "cmpDueDate", "due-date",
			fdsFilters.get(2));
		assertFDSFilter(
			FDSEntityFieldTypes.INTEGER, "cmpTaskCMPProjectId", "project",
			fdsFilters.get(3));
		assertFDSFilter(
			FDSEntityFieldTypes.STRING, "cmpState", "state", fdsFilters.get(4));
		assertFDSFilter(
			FDSEntityFieldTypes.STRING, "keywords", "tag", fdsFilters.get(5));

		fdsFilters = getFDSFilters(assetEntry);

		Assert.assertEquals(fdsFilters.toString(), 5, fdsFilters.size());

		assertFDSFilter(
			FDSEntityFieldTypes.STRING, "cmpAssignTo", "assignee",
			fdsFilters.get(0));
		assertFDSFilter(
			FDSEntityFieldTypes.DATE_TIME, "dateCreated", "create-date",
			fdsFilters.get(1));
		assertFDSFilter(
			FDSEntityFieldTypes.DATE_TIME, "cmpDueDate", "due-date",
			fdsFilters.get(2));
		assertFDSFilter(
			FDSEntityFieldTypes.STRING, "cmpState", "state", fdsFilters.get(3));
		assertFDSFilter(
			FDSEntityFieldTypes.STRING, "keywords", "tag", fdsFilters.get(4));
	}

	@Override
	protected String getObjectDefinitionExternalReferenceCode() {
		return "L_CMP_TASK";
	}

	@Override
	protected Object getSectionDisplayContext(
			HttpServletRequest httpServletRequest)
		throws Exception {

		_fragmentRenderer.render(
			null, httpServletRequest, new MockHttpServletResponse());

		return httpServletRequest.getAttribute(
			"com.liferay.site.cmp.site.initializer.internal.display.context." +
				"ViewProjectTasksSectionDisplayContext");
	}

	protected static final String CLASS_NAME_KALEO_TASK_INSTANCE_TOKEN =
		"com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken";

	protected AssetEntry assetEntry;
	protected ObjectDefinition cmpProjectObjectDefinition;

	private DropdownItem _getDropdownItem(CreationMenu creationMenu) {
		List<DropdownItem> dropdownItems = (List<DropdownItem>)creationMenu.get(
			"primaryItems");

		Assert.assertEquals(dropdownItems.toString(), 1, dropdownItems.size());

		return dropdownItems.get(0);
	}

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cmp.site.initializer.internal.fragment.renderer.ViewProjectTasksJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}