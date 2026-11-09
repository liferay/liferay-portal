/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.data.set.FDSEntryItemImportPolicy;
import com.liferay.frontend.data.set.SystemFDSEntry;
import com.liferay.frontend.data.set.action.FDSItemsActions;
import com.liferay.frontend.data.set.action.util.FDSActionUtil;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemBuilder;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemList;
import com.liferay.frontend.data.set.test.util.FrontendDataSetTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayResourceResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Juanjo Fernandez
 */
@RunWith(Arquillian.class)
public class ImportSystemDataSetMVCResourceCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		FrontendDataSetTestUtil.initialize(
			ImportSystemDataSetMVCResourceCommandTest.class);

		Bundle bundle = FrameworkUtil.getBundle(
			ImportSystemDataSetMVCResourceCommandTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistrations.add(
			bundleContext.registerService(
				FDSItemsActions.class, new TestFDSItemsActions(),
				HashMapDictionaryBuilder.<String, Object>put(
					"frontend.data.set.name", _FDS_NAME
				).build()));
		_serviceRegistrations.add(
			bundleContext.registerService(
				SystemFDSEntry.class, new TestSystemFDSEntry(),
				HashMapDictionaryBuilder.<String, Object>put(
					"frontend.data.set.name", _FDS_NAME
				).build()));
	}

	@After
	public void tearDown() throws Exception {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET_ACTION", TestPropsValues.getCompanyId());

		for (String id : new String[] {"headless", "own", "plain"}) {
			ObjectEntry objectEntry = _fetchDataSetActionObjectEntry(
				id, objectDefinition);

			if (objectEntry == null) {
				continue;
			}

			_objectEntryLocalService.deleteObjectEntry(objectEntry);
		}

		objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET", TestPropsValues.getCompanyId());

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			_FDS_NAME, 0, objectDefinition.getObjectDefinitionId());

		if (objectEntry != null) {
			_objectEntryLocalService.deleteObjectEntry(objectEntry);
		}

		FrontendDataSetTestUtil.tearDown();
	}

	@Test
	public void testServeResource() throws Exception {
		MockLiferayResourceRequest mockLiferayResourceRequest =
			new MockLiferayResourceRequest();

		mockLiferayResourceRequest.addParameter("name", _FDS_NAME);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setUser(TestPropsValues.getUser());

		mockLiferayResourceRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		_mvcResourceCommand.serveResource(
			mockLiferayResourceRequest, new MockLiferayResourceResponse());

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_DATA_SET_ACTION", TestPropsValues.getCompanyId());

		_assertPermissionKey("delete", "own", objectDefinition);
		_assertPermissionKey("update", "plain", objectDefinition);
		_assertPermissionKey(StringPool.BLANK, "headless", objectDefinition);
	}

	private void _assertPermissionKey(
		String expectedPermissionKey, String id,
		ObjectDefinition objectDefinition) {

		ObjectEntry objectEntry = _fetchDataSetActionObjectEntry(
			id, objectDefinition);

		Map<String, Serializable> values = objectEntry.getValues();

		Assert.assertEquals(expectedPermissionKey, values.get("permissionKey"));
	}

	private ObjectEntry _fetchDataSetActionObjectEntry(
		String id, ObjectDefinition objectDefinition) {

		return _objectEntryLocalService.fetchObjectEntry(
			FDSActionUtil.getFDSItemActionExternalReferenceCode(_FDS_NAME, id),
			0, objectDefinition.getObjectDefinitionId());
	}

	private static final String _FDS_NAME = RandomTestUtil.randomString();

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "mvc.command.name=/frontend_data_set_admin/import_system_data_set"
	)
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();

	private static class TestFDSItemsActions implements FDSItemsActions {

		@Override
		public List<FDSActionDropdownItem> getFDSActionDropdownItems(
			HttpServletRequest httpServletRequest) {

			return FDSActionDropdownItemList.of(
				FDSActionDropdownItemBuilder.setFDSActionDropdownItems(
					FDSActionDropdownItemList.of(
						FDSActionDropdownItemBuilder.setLabel(
							RandomTestUtil.randomString()
						).setTarget(
							"headless"
						).build(
							"headless"
						),
						FDSActionDropdownItemBuilder.setLabel(
							RandomTestUtil.randomString()
						).setPermissionKey(
							"delete"
						).setTarget(
							"link"
						).build(
							"own"
						),
						FDSActionDropdownItemBuilder.setLabel(
							RandomTestUtil.randomString()
						).setTarget(
							"link"
						).build(
							"plain"
						))
				).setLabel(
					RandomTestUtil.randomString()
				).setPermissionKey(
					"update"
				).setType(
					"group"
				).build(
					"group"
				));
		}

		@Override
		public FDSEntryItemImportPolicy getFDSEntryItemImportPolicy() {
			return FDSEntryItemImportPolicy.DETACHED;
		}

	}

	private static class TestSystemFDSEntry implements SystemFDSEntry {

		@Override
		public String getDescription() {
			return null;
		}

		@Override
		public String getName() {
			return _FDS_NAME;
		}

		@Override
		public String getRESTApplication() {
			return RandomTestUtil.randomString();
		}

		@Override
		public String getRESTEndpoint() {
			return RandomTestUtil.randomString();
		}

		@Override
		public String getRESTSchema() {
			return RandomTestUtil.randomString();
		}

		@Override
		public String getTitle() {
			return RandomTestUtil.randomString();
		}

	}

}