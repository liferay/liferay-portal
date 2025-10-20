/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.upgrade.registry;

import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.upgrade.BaseExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.CTModelUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.segments.internal.upgrade.v2_0_0.SchemaUpgradeProcess;
import com.liferay.segments.internal.upgrade.v2_0_0.SegmentsExperienceUpgradeProcess;
import com.liferay.segments.internal.upgrade.v2_8_1.SegmentsExperimentUpgradeProcess;
import com.liferay.segments.internal.upgrade.v3_1_1.SegmentsEntryUpgradeProcess;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Arques
 */
@Component(service = UpgradeStepRegistrator.class)
public class SegmentsServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register("1.0.0", "1.0.1", new SchemaUpgradeProcess());

		registry.register(
			"1.0.1", "2.0.0",
			new SegmentsExperienceUpgradeProcess(_counterLocalService));

		registry.register(
			"2.0.0", "2.1.0",
			new MVCCVersionUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {
						"SegmentsEntry", "SegmentsEntryRel",
						"SegmentsExperience", "SegmentsExperiment",
						"SegmentsExperimentRel"
					};
				}

			});

		registry.register(
			"2.1.0", "2.2.0",
			new com.liferay.segments.internal.upgrade.v2_2_0.
				SchemaUpgradeProcess());

		registry.register(
			"2.2.0", "2.3.0",
			new CTModelUpgradeProcess(
				"SegmentsEntry", "SegmentsEntryRel", "SegmentsEntryRole",
				"SegmentsExperience", "SegmentsExperiment",
				"SegmentsExperimentRel"));

		registry.register(
			"2.3.0", "2.4.0",
			UpgradeProcessFactory.addColumns(
				"SegmentsExperience", "typeSettings VARCHAR(75) null"));

		registry.register("2.4.0", "2.5.0", new DummyUpgradeStep());

		registry.register(
			"2.5.0", "2.6.0",
			new com.liferay.segments.internal.upgrade.v2_6_0.
				SegmentsExperienceUpgradeProcess());

		registry.register("2.6.0", "2.6.1", new DummyUpgradeStep());

		registry.register(
			"2.6.1", "2.7.0",
			UpgradeProcessFactory.alterColumnName(
				"SegmentsExperience", "classPK", "plid LONG"),
			UpgradeProcessFactory.dropColumns(
				"SegmentsExperience", "classNameId"));

		registry.register(
			"2.7.0", "2.8.0",
			UpgradeProcessFactory.alterColumnName(
				"SegmentsExperiment", "classPK", "plid LONG"),
			UpgradeProcessFactory.dropColumns(
				"SegmentsExperiment", "classNameId"));

		registry.register(
			"2.8.0", "2.8.1", new SegmentsExperimentUpgradeProcess());

		registry.register(
			"2.8.1", "3.0.0",
			UpgradeProcessFactory.dropColumns("SegmentsEntry", "type_"));

		registry.register(
			"3.0.0", "3.1.0",
			new BaseExternalReferenceCodeUpgradeProcess() {

				@Override
				protected String[] getTableNames() {
					return new String[] {"SegmentsExperience"};
				}

			});

		registry.register("3.1.0", "3.1.1", new SegmentsEntryUpgradeProcess());

		registry.register(
			"3.1.1", "3.2.0",
			new com.liferay.segments.internal.upgrade.v3_2_0.
				SegmentsExperienceUpgradeProcess(_layoutLocalService));
	}

	@Reference
	private CounterLocalService _counterLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

}