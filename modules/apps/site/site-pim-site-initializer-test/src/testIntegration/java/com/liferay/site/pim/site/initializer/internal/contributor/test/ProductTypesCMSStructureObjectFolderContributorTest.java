/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.contributor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.model.DepotEntry;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cms.site.initializer.contributor.CMSStructureObjectFolderContributor;
import com.liferay.site.pim.site.initializer.test.util.PIMTestUtil;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Stefano Motta
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-96666"))
@RunWith(Arquillian.class)
public class ProductTypesCMSStructureObjectFolderContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		DepotEntry depotEntry = PIMTestUtil.addSpaceDepotEntry();

		ObjectEntryFolder productsObjectEntryFolder =
			_objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_PRODUCTS,
					depotEntry.getGroupId(), depotEntry.getCompanyId());

		_objectEntryFolder =
			_objectEntryFolderLocalService.addObjectEntryFolder(
				RandomTestUtil.randomString(), depotEntry.getGroupId(),
				TestPropsValues.getUserId(),
				productsObjectEntryFolder.getObjectEntryFolderId(),
				RandomTestUtil.randomString(),
				HashMapBuilder.put(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()
				).build(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(
					depotEntry.getGroupId()));
	}

	@Test
	public void testGetBulkActionDropdownItems() {
		Assert.assertEquals(
			List.of("delete"),
			TransformUtil.transform(
				_cmsStructureObjectFolderContributor.getBulkActionDropdownItems(
					_mockHttpServletRequest),
				dropdownItem -> {
					Map<String, Object> data =
						(Map<String, Object>)dropdownItem.get("data");

					return data.get("id");
				}));
	}

	@Test
	public void testGetCreationMenuDropdownItems() {
		List<DropdownItem> dropdownItems =
			_cmsStructureObjectFolderContributor.getCreationMenuDropdownItems(
				_mockHttpServletRequest, _objectEntryFolder);

		Assert.assertTrue(dropdownItems.size() > 1);

		DropdownItem dropdownItem = dropdownItems.get(0);

		Map<String, Object> data = (Map<String, Object>)dropdownItem.get(
			"data");

		Assert.assertEquals("createFolder", data.get("action"));
		Assert.assertEquals(
			_objectEntryFolder.getExternalReferenceCode(),
			data.get("parentObjectEntryFolderExternalReferenceCode"));

		for (DropdownItem productTypeDropdownItem :
				dropdownItems.subList(1, dropdownItems.size())) {

			data = (Map<String, Object>)productTypeDropdownItem.get("data");

			Assert.assertEquals("createAsset", data.get("action"));

			String redirect = String.valueOf(data.get("redirect"));

			Assert.assertTrue(
				redirect.contains(
					"objectEntryFolderExternalReferenceCode=" +
						_objectEntryFolder.getExternalReferenceCode()));
		}

		Assert.assertNull(
			_cmsStructureObjectFolderContributor.getCreationMenuDropdownItems(
				_mockHttpServletRequest, null));
	}

	@Test
	public void testGetEmptyState() {
		Map<String, Object> emptyState =
			_cmsStructureObjectFolderContributor.getEmptyState(
				_mockHttpServletRequest);

		Assert.assertEquals("products", emptyState.get("symbol"));
	}

	@Test
	public void testGetFDSActionDropdownItems() {
		Assert.assertEquals(
			List.of(
				"actionLinkFolder", "editFolder", "edit", "permissions",
				"delete"),
			TransformUtil.transform(
				_cmsStructureObjectFolderContributor.getFDSActionDropdownItems(
					_mockHttpServletRequest),
				dropdownItem -> {
					Map<String, Object> data =
						(Map<String, Object>)dropdownItem.get("data");

					return data.get("id");
				}));
	}

	@Test
	public void testGetFDSName() {
		Assert.assertEquals(
			"com.liferay.site.pim.site.initializer-products",
			_cmsStructureObjectFolderContributor.getFDSName());
	}

	@Test
	public void testGetPropsTransformerModule() {
		Assert.assertEquals(
			"{ProductsFDSPropsTransformer} from site-pim-site-initializer",
			_cmsStructureObjectFolderContributor.getPropsTransformerModule());
	}

	@Test
	public void testIsGalleryViewEnabled() {
		Assert.assertFalse(
			_cmsStructureObjectFolderContributor.isGalleryViewEnabled());
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));

		Group group = PIMTestUtil.getOrAddGroup();

		Layout layout = LayoutTestUtil.addTypeContentLayout(group);

		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());

		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPathMain(_portal.getPathMain());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setPortalURL(
			"http://localhost:" + PortalUtil.getPortalServerPort(false));
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setRequest(_mockHttpServletRequest);
		themeDisplay.setScopeGroupId(group.getGroupId());
		themeDisplay.setSiteGroupId(group.getGroupId());
		themeDisplay.setURLCurrent(
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/currentURL");
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject(
		filter = "component.name=com.liferay.site.pim.site.initializer.internal.contributor.ProductTypesCMSStructureObjectFolderContributor"
	)
	private CMSStructureObjectFolderContributor
		_cmsStructureObjectFolderContributor;

	@Inject
	private CompanyLocalService _companyLocalService;

	private final MockHttpServletRequest _mockHttpServletRequest =
		new MockHttpServletRequest();
	private ObjectEntryFolder _objectEntryFolder;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private Portal _portal;

}