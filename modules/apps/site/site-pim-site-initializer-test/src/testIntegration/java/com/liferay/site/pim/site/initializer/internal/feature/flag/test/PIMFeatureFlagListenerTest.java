/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.feature.flag.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.model.DepotEntry;
import com.liferay.feature.flag.test.util.FeatureFlagTestHelper;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.entry.folder.util.ObjectEntryFolderThreadLocal;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.pim.site.initializer.constants.PIMObjectDefinitionConstants;
import com.liferay.site.pim.site.initializer.constants.PIMObjectEntryFolderConstants;
import com.liferay.site.pim.site.initializer.constants.PIMObjectFolderConstants;
import com.liferay.site.pim.site.initializer.test.util.PIMTestUtil;

import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Stefano Motta
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-96666"))
@RunWith(Arquillian.class)
public class PIMFeatureFlagListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_featureFlagTestHelper = new FeatureFlagTestHelper();

		_featureFlagTestHelper.setFeatureFlagValue(
			TestPropsValues.getCompanyId(), "LPD-96666", true);
	}

	@After
	public void tearDown() throws Exception {
		_featureFlagTestHelper.setFeatureFlagValue(
			TestPropsValues.getCompanyId(), "LPD-96666", false);

		_featureFlagTestHelper.tearDown();
	}

	@Test
	public void test() throws Exception {
		long companyId = TestPropsValues.getCompanyId();
		DepotEntry depotEntry = PIMTestUtil.addSpaceDepotEntry();

		try (SafeCloseable safeCloseable =
				ObjectEntryFolderThreadLocal.
					setForceDeleteSystemObjectEntryFolderWithSafeCloseable(
						true)) {

			_objectEntryFolderLocalService.
				deleteObjectEntryFolderByExternalReferenceCode(
					PIMObjectEntryFolderConstants.
						EXTERNAL_REFERENCE_CODE_PRODUCTS,
					depotEntry.getGroupId(), companyId);
		}

		Assert.assertNull(
			_objectEntryFolderLocalService.
				fetchObjectEntryFolderByExternalReferenceCode(
					PIMObjectEntryFolderConstants.
						EXTERNAL_REFERENCE_CODE_PRODUCTS,
					depotEntry.getGroupId(), companyId));

		Bundle bundle = FrameworkUtil.getBundle(
			_featureFlagListener.getClass());

		bundle.stop();
		bundle.start();

		Assert.assertNotNull(
			_objectEntryFolderLocalService.
				fetchObjectEntryFolderByExternalReferenceCode(
					PIMObjectEntryFolderConstants.
						EXTERNAL_REFERENCE_CODE_PRODUCTS,
					depotEntry.getGroupId(), companyId));
		Assert.assertNotNull(
			_objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				PIMObjectFolderConstants.EXTERNAL_REFERENCE_CODE_DEFINITIONS,
				companyId));
		Assert.assertNotNull(
			_objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				PIMObjectFolderConstants.EXTERNAL_REFERENCE_CODE_PRODUCT_TYPES,
				companyId));

		ObjectDefinition pimBasicProductObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					PIMObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_BASIC_PRODUCT,
					companyId);

		Assert.assertEquals(
			ObjectDefinitionConstants.SCOPE_DEPOT,
			pimBasicProductObjectDefinition.getScope());

		_assertObjectDefinitionSetting(
			"domain", pimBasicProductObjectDefinition, "space");

		ObjectDefinition pimLinkObjectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					PIMObjectDefinitionConstants.EXTERNAL_REFERENCE_CODE_LINK,
					companyId);

		Assert.assertEquals(
			ObjectDefinitionConstants.SCOPE_DEPOT,
			pimLinkObjectDefinition.getScope());

		_assertObjectDefinitionSetting(
			"domain", pimLinkObjectDefinition, "space");

		Group group = _groupLocalService.getGroup(
			companyId, GroupConstants.CMS);

		_assertFragmentRenderer(
			"/connectors", group,
			"ViewPIMConnectorsJSPSectionFragmentRenderer");
		_assertFragmentRenderer(
			"/edit-connector", group,
			"EditPIMConnectorJSPSectionFragmentRenderer");
		_assertFragmentRenderer(
			"/edit-field-mappings", group,
			"EditPIMConnectorFieldMappingsJSPSectionFragmentRenderer");
		_assertFragmentRenderer(
			"/field-mappings", group,
			"PIMConnectorFieldMappingsJSPSectionFragmentRenderer");
		_assertFragmentRenderer(
			"/products", group, "ProductsSectionFragmentRenderer");
	}

	private void _assertFragmentRenderer(
			String friendlyURL, Group group, String rendererKey)
		throws Exception {

		Layout layout = _layoutLocalService.getFriendlyURLLayout(
			group.getGroupId(), false, friendlyURL);

		Assert.assertTrue(
			ListUtil.exists(
				_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
					group.getGroupId(), layout.getPlid()),
				fragmentEntryLink -> Objects.equals(
					fragmentEntryLink.getRendererKey(),
					"com.liferay.site.pim.site.initializer.internal.fragment." +
						"renderer." + rendererKey)));
	}

	private void _assertObjectDefinitionSetting(
		String name, ObjectDefinition objectDefinition, String value) {

		ObjectDefinitionSetting objectDefinitionSetting =
			_objectDefinitionSettingLocalService.fetchObjectDefinitionSetting(
				objectDefinition.getObjectDefinitionId(), name);

		Assert.assertEquals(value, objectDefinitionSetting.getValue());
	}

	@Inject(filter = "feature.flag.key=LPD-96666")
	private FeatureFlagListener _featureFlagListener;

	private FeatureFlagTestHelper _featureFlagTestHelper;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

}