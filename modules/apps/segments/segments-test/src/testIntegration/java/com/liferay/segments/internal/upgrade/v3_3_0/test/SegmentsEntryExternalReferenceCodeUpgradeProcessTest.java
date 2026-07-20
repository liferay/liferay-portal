/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.upgrade.v3_3_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.BaseExternalReferenceCodeUpgradeProcessTestCase;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.CriteriaSerializer;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.service.SegmentsEntryLocalService;

import java.util.Collections;
import java.util.Locale;

import org.junit.runner.RunWith;

/**
 * @author Georgel Pop
 */
@RunWith(Arquillian.class)
public class SegmentsEntryExternalReferenceCodeUpgradeProcessTest
	extends BaseExternalReferenceCodeUpgradeProcessTestCase {

	@Override
	protected ExternalReferenceCodeModel[] addExternalReferenceCodeModels(
			String tableName)
		throws PortalException {

		Locale defaultLocale = LocaleUtil.getSiteDefault();

		return new ExternalReferenceCodeModel[] {
			_segmentsEntryLocalService.addSegmentsEntry(
				null, RandomTestUtil.randomString(),
				Collections.singletonMap(
					defaultLocale, RandomTestUtil.randomString()),
				Collections.singletonMap(
					defaultLocale, RandomTestUtil.randomString()),
				true, CriteriaSerializer.serialize(new Criteria()),
				SegmentsEntryConstants.SOURCE_DEFAULT,
				ServiceContextTestUtil.getServiceContext(group.getGroupId()))
		};
	}

	@Override
	protected ExternalReferenceCodeModel fetchExternalReferenceCodeModel(
		ExternalReferenceCodeModel externalReferenceCodeModel,
		String tableName) {

		SegmentsEntry segmentsEntry = (SegmentsEntry)externalReferenceCodeModel;

		return _segmentsEntryLocalService.fetchSegmentsEntry(
			segmentsEntry.getSegmentsEntryId());
	}

	@Override
	protected String[] getTableNames() {
		return new String[] {"SegmentsEntry"};
	}

	@Override
	protected UpgradeStepRegistrator getUpgradeStepRegistrator() {
		return _upgradeStepRegistrator;
	}

	@Override
	protected Version getVersion() {
		return new Version(3, 3, 0);
	}

	@Inject
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.segments.internal.upgrade.registry.SegmentsServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}