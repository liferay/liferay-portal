/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.headless.asset.library.dto.v1_0.AssetLibrary;
import com.liferay.headless.asset.library.resource.v1_0.AssetLibraryResource;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Roberto Díaz
 */
@FeatureFlag("LPD-17564")
@RunWith(Arquillian.class)
public class SpaceListDisplayContextTest extends BaseDisplayContextTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		super.setUp();

		AssetLibraryResource.Builder builder =
			_assetLibraryResourceFactory.create();

		_assetLibraryResource = builder.user(
			UserTestUtil.getAdminUser(group.getCompanyId())
		).build();

		_layout = LayoutTestUtil.addTypeContentLayout(group);
	}

	@Test
	public void testGetProps() throws Exception {
		String name = RandomTestUtil.randomString();
		String logoColor = RandomTestUtil.randomString();

		AssetLibrary assetLibrary = _addAssetLibraryWithLogo(name, logoColor);

		Map<String, Object> props = ReflectionTestUtil.invoke(
			_getSpaceListDisplayContext(
				getMockHttpServletRequest(),
				_depotEntryLocalService.getDepotEntry(assetLibrary.getId())),
			"getProps", new Class<?>[0]);

		Assert.assertEquals(logoColor, props.get("displayType"));
		Assert.assertEquals(name, props.get("name"));
	}

	private AssetLibrary _addAssetLibraryWithLogo(String name, String logoColor)
		throws Exception {

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(LocaleUtil.getDefault(), name), null,
			new ServiceContext() {
				{
					setCompanyId(group.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});

		Group depotGroup = _groupLocalService.getGroup(depotEntry.getGroupId());

		UnicodeProperties unicodeProperties =
			depotGroup.getTypeSettingsProperties();

		unicodeProperties.setProperty("logoColor", logoColor);

		_groupLocalService.updateGroup(
			depotGroup.getGroupId(), unicodeProperties.toString());

		return _assetLibraryResource.getAssetLibrary(depotEntry.getGroupId());
	}

	private FragmentEntryLink _addFragmentEntryLink(long groupId)
		throws Exception {

		return _fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, TestPropsValues.getUserId(), groupId, 0, 0,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid()),
			_layout.getPlid(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, 0,
			"com.liferay.site.cms.site.initializer.internal.fragment." +
				"renderer.SpaceListComponentSectionFragmentRenderer",
			FragmentConstants.TYPE_COMPONENT,
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	private ObjectDefinition _addObjectDefinition(ObjectField objectField)
		throws Exception {

		return _objectDefinitionLocalService.addCustomObjectDefinition(
			TestPropsValues.getUserId(), 0, null, false, false, true, false,
			false, false, null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			ObjectDefinitionTestUtil.getRandomName(), null, null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			true, ObjectDefinitionConstants.SCOPE_DEPOT,
			ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
			Collections.emptyList(), Arrays.asList(objectField));
	}

	private Object _getSpaceListDisplayContext(
			HttpServletRequest httpServletRequest, DepotEntry depotEntry)
		throws Exception {

		ObjectDefinition objectDefinition = _addObjectDefinition(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"objectDefinitionTextObjectFieldName"
			).build());

		objectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		_objectDefinitionSettingLocalService.addObjectDefinitionSetting(
			objectDefinition.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS,
			String.valueOf(depotEntry.getGroupId()));

		ObjectEntry objectDefinitionObjectEntry =
			_objectEntryLocalService.addObjectEntry(
				TestPropsValues.getUserId(), depotEntry.getGroupId(),
				objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"objectDefinitionTextObjectFieldName",
					RandomTestUtil.randomString()
				).build(),
				ServiceContextTestUtil.getServiceContext());

		InfoItemDetailsProvider<ObjectEntry> infoItemDetailsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class, objectDefinition.getClassName());

		InfoItemDetails infoItemDetails =
			infoItemDetailsProvider.getInfoItemDetails(
				objectDefinitionObjectEntry);

		DefaultFragmentRendererContext defaultFragmentRendererContext =
			new DefaultFragmentRendererContext(
				_addFragmentEntryLink(depotEntry.getGroupId()));

		defaultFragmentRendererContext.setContextInfoItemReference(
			infoItemDetails.getInfoItemReference());

		_fragmentRenderer.render(
			defaultFragmentRendererContext, httpServletRequest,
			new MockHttpServletResponse());

		Object spaceListDisplayContext = httpServletRequest.getAttribute(
			"com.liferay.site.cms.site.initializer.internal.display.context." +
				"SpaceListDisplayContext");

		Assert.assertNotNull(spaceListDisplayContext);

		return spaceListDisplayContext;
	}

	private AssetLibraryResource _assetLibraryResource;

	@Inject
	private AssetLibraryResource.Factory _assetLibraryResourceFactory;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.fragment.renderer.SpaceListComponentSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	private Layout _layout;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}