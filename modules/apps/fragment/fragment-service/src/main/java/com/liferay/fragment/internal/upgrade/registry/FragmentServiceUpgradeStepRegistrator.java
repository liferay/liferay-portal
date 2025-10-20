/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.upgrade.registry;

import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.fragment.internal.upgrade.v1_1_0.PortletPreferencesUpgradeProcess;
import com.liferay.fragment.internal.upgrade.v2_0_0.util.FragmentCollectionTable;
import com.liferay.fragment.internal.upgrade.v2_0_0.util.FragmentEntryLinkTable;
import com.liferay.fragment.internal.upgrade.v2_0_0.util.FragmentEntryTable;
import com.liferay.fragment.internal.upgrade.v2_1_0.SchemaUpgradeProcess;
import com.liferay.fragment.internal.upgrade.v2_4_0.FragmentEntryLinkUpgradeProcess;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.BaseSQLServerDatetimeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.CTModelUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.view.count.ViewCountManager;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author José Ángel Jiménez
 */
@Component(service = UpgradeStepRegistrator.class)
public class FragmentServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"1.0.0", "1.0.1",
			UpgradeProcessFactory.alterColumnType(
				"FragmentEntry", "css", "TEXT null"),
			UpgradeProcessFactory.alterColumnType(
				"FragmentEntry", "html", "TEXT null"),
			UpgradeProcessFactory.alterColumnType(
				"FragmentEntry", "js", "TEXT null"),
			UpgradeProcessFactory.alterColumnType(
				"FragmentEntryLink", "css", "TEXT null"),
			UpgradeProcessFactory.alterColumnType(
				"FragmentEntryLink", "html", "TEXT null"),
			UpgradeProcessFactory.alterColumnType(
				"FragmentEntryLink", "js", "TEXT null"),
			UpgradeProcessFactory.alterColumnType(
				"FragmentEntryLink", "editableValues", "TEXT null"));

		registry.register("1.0.1", "1.0.2", new DummyUpgradeStep());

		registry.register(
			"1.0.2", "1.1.0",
			new PortletPreferencesUpgradeProcess(_layoutLocalService));

		registry.register(
			"1.1.0", "2.0.0",
			new BaseSQLServerDatetimeUpgradeProcess(
				new Class<?>[] {
					FragmentCollectionTable.class, FragmentEntryLinkTable.class,
					FragmentEntryTable.class
				}));

		registry.register("2.0.0", "2.1.0", new SchemaUpgradeProcess());

		registry.register("2.1.0", "2.1.1", new DummyUpgradeStep());

		registry.register(
			"2.1.1", "2.1.2",
			UpgradeProcessFactory.addColumns(
				"FragmentEntry", "configuration TEXT"));

		registry.register(
			"2.1.2", "2.1.3",
			UpgradeProcessFactory.addColumns(
				"FragmentEntryLink", "configuration TEXT"));

		registry.register(
			"2.1.3", "2.2.0",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {
						"FragmentCollection", "FragmentEntry",
						"FragmentEntryLink"
					};
				}

			});

		registry.register(
			"2.2.0", "2.2.1",
			UpgradeProcessFactory.addColumns(
				"FragmentEntry", "readOnly BOOLEAN"));

		registry.register(
			"2.2.1", "2.2.2",
			UpgradeProcessFactory.addColumns(
				"FragmentEntry", "cacheable BOOLEAN"));

		registry.register(
			"2.2.2", "2.3.0",
			new com.liferay.fragment.internal.upgrade.v2_3_0.
				SchemaUpgradeProcess());

		registry.register(
			"2.3.0", "2.4.0", new FragmentEntryLinkUpgradeProcess());

		registry.register(
			"2.4.0", "2.5.0",
			new com.liferay.fragment.internal.upgrade.v2_5_0.
				FragmentEntryLinkUpgradeProcess());

		registry.register(
			"2.5.0", "2.5.1",
			new com.liferay.fragment.internal.upgrade.v2_6_0.
				FragmentEntryUpgradeProcess());

		registry.register(
			"2.5.1", "2.6.0",
			new com.liferay.fragment.internal.upgrade.v2_6_0.
				FragmentEntryVersionUpgradeProcess());

		registry.register(
			"2.6.0", "2.7.0",
			new CTModelUpgradeProcess(
				"FragmentCollection", "FragmentComposition", "FragmentEntry",
				"FragmentEntryLink", "FragmentEntryVersion"),
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"FragmentEntryVersion"};
				}

			});

		registry.register("2.7.0", "2.7.1", new DummyUpgradeStep());

		registry.register(
			"2.7.1", "2.8.0",
			UpgradeProcessFactory.addColumns(
				"FragmentEntry", "icon VARCHAR(75) null"),
			UpgradeProcessFactory.addColumns(
				"FragmentEntryVersion", "icon VARCHAR(75) null"));

		registry.register(
			"2.8.0", "2.9.0",
			UpgradeProcessFactory.addColumns(
				"FragmentEntry", "typeOptions TEXT"),
			UpgradeProcessFactory.addColumns(
				"FragmentEntryVersion", "typeOptions TEXT"));

		registry.register(
			"2.9.0", "2.9.1",
			new com.liferay.fragment.internal.upgrade.v2_9_1.
				FragmentEntryLinkUpgradeProcess());

		registry.register(
			"2.9.1", "2.9.2",
			UpgradeProcessFactory.addColumns(
				"FragmentEntryVersion", "typeOptions TEXT"));

		registry.register(
			"2.9.2", "2.9.3",
			UpgradeProcessFactory.alterColumnType(
				"FragmentComposition", "description", "STRING null"));

		registry.register(
			"2.9.3", "2.9.4",
			new com.liferay.fragment.internal.upgrade.v2_9_4.
				FragmentEntryLinkUpgradeProcess());

		registry.register(
			"2.9.4", "2.10.0",
			UpgradeProcessFactory.addColumns(
				"FragmentEntryLink", "deleted BOOLEAN"));

		registry.register(
			"2.10.0", "2.10.1",
			new com.liferay.fragment.internal.upgrade.v2_10_1.
				FragmentCollectionUpgradeProcess(
					_dlFolderLocalService, _portletFileRepository));

		registry.register(
			"2.10.1", "2.10.2",
			UpgradeProcessFactory.runSQL(
				"update FragmentEntryLink set deleted = [$FALSE$] where " +
					"deleted is null"));

		registry.register(
			"2.10.2", "2.10.3",
			UpgradeProcessFactory.runSQL(
				StringBundler.concat(
					"update FragmentEntryLink set originalFragmentEntryLinkId ",
					"= 0 where originalFragmentEntryLinkId > 0 and plid in ",
					"(select plid from Layout where classPK > 0)")));

		registry.register(
			"2.10.3", "2.11.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"FragmentCollection"};
				}

			});

		registry.register(
			"2.11.0", "2.12.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {
						"FragmentComposition", "FragmentEntry",
						"FragmentEntryVersion"
					};
				}

			});

		registry.register(
			"2.12.0", "2.13.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"FragmentEntryLink"};
				}

			});

		registry.register(
			"2.13.0", "2.13.1",
			new com.liferay.fragment.internal.upgrade.v2_13_1.
				FragmentEntryLinkUpgradeProcess(_jsonFactory, _portal));

		registry.register(
			"2.13.1", "2.14.0",
			UpgradeProcessFactory.addColumns(
				"FragmentComposition", "marketplace BOOLEAN"),
			UpgradeProcessFactory.addColumns(
				"FragmentEntry", "marketplace BOOLEAN"),
			UpgradeProcessFactory.addColumns(
				"FragmentEntryVersion", "marketplace BOOLEAN"));

		registry.register(
			"2.14.0", "2.15.0",
			UpgradeProcessFactory.addColumns(
				"FragmentCollection", "marketplace BOOLEAN"));
	}

	@Reference
	protected ViewCountManager viewCountManager;

	@Reference
	private DLFolderLocalService _dlFolderLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletFileRepository _portletFileRepository;

}