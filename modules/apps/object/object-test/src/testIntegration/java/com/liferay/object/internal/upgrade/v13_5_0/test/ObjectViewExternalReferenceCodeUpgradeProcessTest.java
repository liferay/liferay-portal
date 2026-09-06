/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v13_5_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectView;
import com.liferay.object.service.ObjectViewLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.BaseExternalReferenceCodeUpgradeProcessTestCase;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Collections;

import org.junit.runner.RunWith;

/**
 * @author Jhosseph Gonzalez
 */
@RunWith(Arquillian.class)
public class ObjectViewExternalReferenceCodeUpgradeProcessTest
	extends BaseExternalReferenceCodeUpgradeProcessTestCase {

	@Override
	protected ExternalReferenceCodeModel[] addExternalReferenceCodeModels(
			String tableName)
		throws PortalException {

		try {
			_objectDefinition =
				ObjectDefinitionTestUtil.addCustomObjectDefinition();
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}

		return new ExternalReferenceCodeModel[] {
			_objectViewLocalService.addObjectView(
				null, TestPropsValues.getUserId(),
				_objectDefinition.getObjectDefinitionId(), false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList())
		};
	}

	@Override
	protected ExternalReferenceCodeModel fetchExternalReferenceCodeModel(
			ExternalReferenceCodeModel externalReferenceCodeModel,
			String tableName)
		throws PortalException {

		ObjectView objectView = (ObjectView)externalReferenceCodeModel;

		return _objectViewLocalService.fetchObjectView(
			objectView.getObjectViewId());
	}

	@Override
	protected String[] getTableNames() {
		return new String[] {"ObjectView"};
	}

	@Override
	protected UpgradeStepRegistrator getUpgradeStepRegistrator() {
		return _upgradeStepRegistrator;
	}

	@Override
	protected Version getVersion() {
		return new Version(13, 5, 0);
	}

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectViewLocalService _objectViewLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.object.internal.upgrade.registry.ObjectServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}