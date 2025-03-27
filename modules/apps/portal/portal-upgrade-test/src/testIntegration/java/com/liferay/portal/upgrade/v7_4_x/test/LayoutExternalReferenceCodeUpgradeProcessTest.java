/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.BaseExternalReferenceCodeUpgradeProcessTestCase;
import com.liferay.portal.upgrade.v7_4_x.LayoutExternalReferenceCodeUpgradeProcess;

import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class LayoutExternalReferenceCodeUpgradeProcessTest
	extends BaseExternalReferenceCodeUpgradeProcessTestCase {

	@Override
	protected ExternalReferenceCodeModel[] addExternalReferenceCodeModels(
			String tableName)
		throws PortalException {

		serviceContext = ServiceContextTestUtil.getServiceContext(
			group.getGroupId());

		serviceContext.setUuid(_UUID);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		Layout layout = _layoutLocalService.addLayout(
			"", TestPropsValues.getUserId(), group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			LayoutConstants.TYPE_CONTENT, false, false, null, serviceContext);

		serviceContext = ServiceContextTestUtil.getServiceContext(
			group.getGroupId());

		serviceContext.setUuid(_UUID);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		Layout privateLayout = _layoutLocalService.addLayout(
			"", TestPropsValues.getUserId(), group.getGroupId(), true,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			LayoutConstants.TYPE_CONTENT, false, false, null, serviceContext);

		return new ExternalReferenceCodeModel[] {
			layout, layout.fetchDraftLayout(), privateLayout,
			privateLayout.fetchDraftLayout()
		};
	}

	@Override
	protected ExternalReferenceCodeModel fetchExternalReferenceCodeModel(
		ExternalReferenceCodeModel externalReferenceCodeModel,
		String tableName) {

		Layout layout = (Layout)externalReferenceCodeModel;

		return _layoutLocalService.fetchLayout(layout.getPlid());
	}

	@Override
	protected String getExternalReferenceCode(
		ExternalReferenceCodeModel externalReferenceCodeModel,
		String tableName) {

		return externalReferenceCodeModel.getExternalReferenceCode();
	}

	@Override
	protected String[] getTableNames() {
		return new String[] {"Layout"};
	}

	@Override
	protected UpgradeProcess getUpgradeProcess() {
		return new LayoutExternalReferenceCodeUpgradeProcess();
	}

	@Override
	protected UpgradeStepRegistrator getUpgradeStepRegistrator() {
		return null;
	}

	@Override
	protected Version getVersion() {
		return null;
	}

	private static final String _UUID = "uuid";

	@Inject
	private LayoutLocalService _layoutLocalService;

}