/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.internal.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutFriendlyURLComposite;
import com.liferay.portal.kernel.portlet.FriendlyURLResolver;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class CustomAssetDisplayPageFriendlyURLResolverTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			ListUtil.fromArray(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING,
					RandomTestUtil.randomString(), "text")),
			ObjectDefinitionConstants.SCOPE_SITE);

		_objectDefinition.setEnableObjectEntryVersioning(true);

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);
	}

	@Test
	public void testGetLayoutDisplayPageObjectProvider() throws Exception {
		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"text", "textValue1"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"text", "textValue2"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(2, objectEntry.getVersion());

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			_getLayoutDisplayPageObjectProvider(
				StringBundler.concat(
					"/e/", _objectDefinition.getName(), StringPool.SLASH,
					_portal.getClassNameId(_objectDefinition.getClassName()),
					StringPool.SLASH, objectEntry.getObjectEntryId()),
				HashMapBuilder.put(
					"version", new String[] {"1"}
				).build());

		Assert.assertNotNull(layoutDisplayPageObjectProvider);

		ObjectEntry actualObjectEntry =
			(ObjectEntry)layoutDisplayPageObjectProvider.getDisplayObject();

		Assert.assertEquals(1, actualObjectEntry.getVersion());
	}

	@FeatureFlag("LPD-57283")
	@Test
	@TestInfo("LPD-104244")
	public void testGetLayoutDisplayPageObjectProviderLayout()
		throws Exception {

		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-57283");

		_testGetLayoutDisplayPageObjectProviderLayout();
		_testGetLayoutDisplayPageObjectProviderLayoutWhenDisconnected();
	}

	private DepotEntry _addDesignLibraryDepotEntry() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_depotEntries.add(depotEntry);

		return depotEntry;
	}

	private Layout _addDisplayPageTemplateLayout(long groupId, String name)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			DisplayPageTemplateTestUtil.addDisplayPageTemplate(
				groupId,
				_portal.getClassNameId(_objectDefinition.getClassName()), null,
				false, null, name, WorkflowConstants.STATUS_APPROVED);

		return _layoutLocalService.getLayout(layoutPageTemplateEntry.getPlid());
	}

	private ObjectEntry _addObjectEntry() throws Exception {
		return _objectEntryLocalService.addObjectEntry(
			_group.getGroupId(), TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"text", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private void _assertLayout(
			Layout expectedLayout, String layoutFriendlyURL,
			ObjectEntry objectEntry)
		throws Exception {

		LayoutFriendlyURLComposite layoutFriendlyURLComposite =
			_friendlyURLResolver.getLayoutFriendlyURLComposite(
				TestPropsValues.getCompanyId(), _group.getGroupId(), false,
				StringBundler.concat(
					_friendlyURLResolver.getURLSeparator(),
					layoutFriendlyURL.substring(1), StringPool.SLASH,
					_portal.getClassNameId(_objectDefinition.getClassName()),
					StringPool.SLASH, objectEntry.getObjectEntryId()),
				Collections.emptyMap(),
				HashMapBuilder.<String, Object>put(
					WebKeys.LOCALE, LocaleUtil.getDefault()
				).build());

		Layout layout = layoutFriendlyURLComposite.getLayout();

		if (expectedLayout == null) {
			Assert.assertNull(layout);

			return;
		}

		Assert.assertEquals(expectedLayout.getPlid(), layout.getPlid());
	}

	private LayoutDisplayPageObjectProvider<?>
			_getLayoutDisplayPageObjectProvider(
				String friendlyURL, Map<String, String[]> params)
		throws Exception {

		Method method = ReflectionUtil.getDeclaredMethod(
			_friendlyURLResolver.getClass(),
			"getLayoutDisplayPageObjectProvider",
			LayoutDisplayPageProvider.class, long.class, String.class,
			Map.class);

		return (LayoutDisplayPageObjectProvider<?>)method.invoke(
			_friendlyURLResolver,
			_layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(
					_objectDefinition.getCompanyId(),
					_objectDefinition.getClassName()),
			RandomTestUtil.randomLong(), friendlyURL, params);
	}

	private void _testGetLayoutDisplayPageObjectProviderLayout()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry();

		DepotEntry depotEntry = _addDesignLibraryDepotEntry();

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), _group.getGroupId());

		String name = RandomTestUtil.randomString();

		Layout designLibraryLayout = _addDisplayPageTemplateLayout(
			depotEntry.getGroupId(), name);

		String layoutFriendlyURL = designLibraryLayout.getFriendlyURL();

		_assertLayout(designLibraryLayout, layoutFriendlyURL, objectEntry);

		Layout layout = _addDisplayPageTemplateLayout(
			_group.getGroupId(), name);

		_assertLayout(layout, layoutFriendlyURL, objectEntry);
	}

	private void _testGetLayoutDisplayPageObjectProviderLayoutWhenDisconnected()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry();

		DepotEntry depotEntry = _addDesignLibraryDepotEntry();

		Layout designLibraryLayout = _addDisplayPageTemplateLayout(
			depotEntry.getGroupId(), RandomTestUtil.randomString());

		_assertLayout(null, designLibraryLayout.getFriendlyURL(), objectEntry);
	}

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.asset.display.page.internal.portlet.CustomAssetDisplayPageFriendlyURLResolver"
	)
	private FriendlyURLResolver _friendlyURLResolver;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutDisplayPageProviderRegistry
		_layoutDisplayPageProviderRegistry;

	@Inject
	private LayoutLocalService _layoutLocalService;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private Portal _portal;

}