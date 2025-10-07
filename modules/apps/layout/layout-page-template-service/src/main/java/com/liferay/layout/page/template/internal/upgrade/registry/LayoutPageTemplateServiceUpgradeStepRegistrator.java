/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.upgrade.registry;

import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.layout.page.template.internal.upgrade.v1_1_0.LayoutPrototypeUpgradeProcess;
import com.liferay.layout.page.template.internal.upgrade.v1_1_1.LayoutPageTemplateEntryUpgradeProcess;
import com.liferay.layout.page.template.internal.upgrade.v1_2_0.LayoutPageTemplateStructureUpgradeProcess;
import com.liferay.layout.page.template.internal.upgrade.v2_0_0.util.LayoutPageTemplateCollectionTable;
import com.liferay.layout.page.template.internal.upgrade.v2_0_0.util.LayoutPageTemplateEntryTable;
import com.liferay.layout.page.template.internal.upgrade.v2_1_0.LayoutUpgradeProcess;
import com.liferay.layout.page.template.internal.upgrade.v3_1_4.ResourcePermissionUpgradeProcess;
import com.liferay.layout.page.template.internal.upgrade.v3_3_0.LayoutPageTemplateStructureRelUpgradeProcess;
import com.liferay.layout.page.template.internal.upgrade.v3_4_1.FragmentEntryLinkEditableValuesUpgradeProcess;
import com.liferay.layout.page.template.internal.upgrade.v5_3_0.LayoutPageTemplateCollectionUpgradeProcess;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.BaseSQLServerDatetimeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.CTModelUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(service = UpgradeStepRegistrator.class)
public class LayoutPageTemplateServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register("0.0.1", "1.0.0", new DummyUpgradeStep());

		registry.register(
			"1.0.0", "1.1.0",
			new LayoutPrototypeUpgradeProcess(
				_companyLocalService, _layoutPrototypeLocalService));

		registry.register(
			"1.1.0", "1.1.1",
			new LayoutPageTemplateEntryUpgradeProcess(_companyLocalService));

		registry.register(
			"1.1.1", "1.2.0",
			new LayoutPageTemplateStructureUpgradeProcess(
				_fragmentEntryLinkLocalService, _layoutLocalService));

		registry.register(
			"1.2.0", "2.0.0",
			new BaseSQLServerDatetimeUpgradeProcess(
				new Class<?>[] {
					LayoutPageTemplateCollectionTable.class,
					LayoutPageTemplateEntryTable.class
				}));

		registry.register(
			"2.0.0", "2.1.0",
			new LayoutUpgradeProcess(
				_fragmentEntryLinkLocalService, _layoutLocalService,
				_layoutPrototypeLocalService));

		registry.register(
			"2.1.0", "3.0.0",
			new com.liferay.layout.page.template.internal.upgrade.v3_0_0.
				LayoutPageTemplateStructureUpgradeProcess());

		registry.register(
			"3.0.0", "3.0.1",
			UpgradeProcessFactory.alterColumnType(
				"LayoutPageTemplateStructureRel", "data_", "TEXT null"));

		registry.register(
			"3.0.1", "3.1.0",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {
						"LayoutPageTemplateCollection",
						"LayoutPageTemplateEntry",
						"LayoutPageTemplateStructure",
						"LayoutPageTemplateStructureRel"
					};
				}

			});

		registry.register("3.1.0", "3.1.1", new DummyUpgradeStep());

		registry.register("3.1.1", "3.1.2", new DummyUpgradeStep());

		registry.register("3.1.2", "3.1.3", new DummyUpgradeStep());

		registry.register(
			"3.1.3", "3.1.4", new ResourcePermissionUpgradeProcess());

		registry.register(
			"3.1.4", "3.1.5",
			new com.liferay.layout.page.template.internal.upgrade.v3_2_0.
				LayoutPageTemplateCollectionUpgradeProcess());

		registry.register(
			"3.1.5", "3.2.0",
			new com.liferay.layout.page.template.internal.upgrade.v3_2_0.
				LayoutPageTemplateEntryUpgradeProcess());

		registry.register(
			"3.2.0", "3.3.0",
			new LayoutPageTemplateStructureRelUpgradeProcess(
				_fragmentEntryLinkLocalService,
				_portletPreferencesLocalService));

		registry.register(
			"3.3.0", "3.3.1",
			new com.liferay.layout.page.template.internal.upgrade.v3_3_1.
				LayoutPageTemplateEntryUpgradeProcess(
					_layoutPrototypeLocalService));

		registry.register(
			"3.3.1", "3.4.0",
			new CTModelUpgradeProcess(
				"LayoutPageTemplateCollection", "LayoutPageTemplateEntry",
				"LayoutPageTemplateStructure",
				"LayoutPageTemplateStructureRel"));

		registry.register(
			"3.4.0", "3.4.0.step-1",
			new com.liferay.layout.page.template.internal.upgrade.v3_4_1.
				LayoutPageTemplateEntryUpgradeProcess(_portal));

		registry.register(
			"3.4.0.step-1", "3.4.1",
			new FragmentEntryLinkEditableValuesUpgradeProcess());

		registry.register(
			"3.4.1", "3.4.1.step-1",
			new com.liferay.layout.page.template.internal.upgrade.v3_4_2.
				FragmentEntryLinkEditableValuesUpgradeProcess());

		registry.register(
			"3.4.1.step-1", "3.4.2",
			new com.liferay.layout.page.template.internal.upgrade.v3_4_2.
				LayoutPageTemplateStructureRelUpgradeProcess(
					_fragmentEntryConfigurationParser));

		registry.register(
			"3.4.2", "3.4.3",
			new com.liferay.layout.page.template.internal.upgrade.v3_4_3.
				ResourcePermissionUpgradeProcess(
					_resourcePermissionLocalService));

		registry.register("3.4.3", "3.4.4", new DummyUpgradeStep());

		registry.register(
			"3.4.4", "3.5.0",
			new com.liferay.layout.page.template.internal.upgrade.v3_5_0.
				LayoutPageTemplateStructureRelUpgradeProcess());

		registry.register(
			"3.5.0", "4.0.0",
			new com.liferay.layout.page.template.internal.upgrade.v4_0_0.
				LayoutPageTemplateStructureRelUpgradeProcess());

		registry.register(
			"4.0.0", "5.0.0",
			new com.liferay.layout.page.template.internal.upgrade.v5_0_0.
				LayoutPageTemplateStructureUpgradeProcess());

		registry.register(
			"5.0.0", "5.0.1",
			new com.liferay.layout.page.template.internal.upgrade.v5_0_1.
				LayoutPageTemplateStructureUpgradeProcess(
					_fragmentEntryLinkLocalService,
					_segmentsExperienceLocalService));

		registry.register(
			"5.0.1", "5.1.0",
			new com.liferay.layout.page.template.internal.upgrade.v5_1_0.
				LayoutPageTemplateStructureUpgradeProcess(
					_layoutLocalService, _segmentsExperienceLocalService,
					_userLocalService));

		registry.register(
			"5.1.0", "5.1.1",
			new com.liferay.layout.page.template.internal.upgrade.v5_1_1.
				LayoutPageTemplateStructureUpgradeProcess(
					_layoutLocalService, _userLocalService));

		registry.register(
			"5.1.1", "5.2.0",
			UpgradeProcessFactory.alterColumnName(
				"LayoutPageTemplateStructure", "classPK", "plid LONG"),
			UpgradeProcessFactory.dropColumns(
				"LayoutPageTemplateStructure", "classNameId"));

		registry.register("5.2.0", "5.3.0", new DummyUpgradeProcess());

		registry.register("5.3.0", "5.3.1", new DummyUpgradeProcess());

		registry.register(
			"5.3.1", "5.4.0",
			new com.liferay.layout.page.template.internal.upgrade.v5_4_0.
				LayoutPageTemplateStructureRelUpgradeProcess());

		registry.register(
			"5.4.0", "5.5.0", new LayoutPageTemplateCollectionUpgradeProcess());

		registry.register(
			"5.5.0", "5.6.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[][] getTableAndPrimaryKeyColumnNames() {
					return new String[][] {
						{
							"LayoutPageTemplateCollection",
							"layoutPageTemplateCollectionId"
						}
					};
				}

			});

		registry.register(
			"5.6.0", "5.7.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[][] getTableAndPrimaryKeyColumnNames() {
					return new String[][] {
						{"LayoutPageTemplateEntry", "layoutPageTemplateEntryId"}
					};
				}

			});

		registry.register(
			"5.7.0", "5.7.1",
			new com.liferay.layout.page.template.internal.upgrade.v5_7_1.
				LayoutPageTemplateStructureRelUpgradeProcess());
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private FragmentEntryConfigurationParser _fragmentEntryConfigurationParser;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPrototypeLocalService _layoutPrototypeLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}