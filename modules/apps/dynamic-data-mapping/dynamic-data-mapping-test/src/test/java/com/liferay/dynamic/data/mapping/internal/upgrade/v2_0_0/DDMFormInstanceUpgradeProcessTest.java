/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v2_0_0;

import com.liferay.dynamic.data.mapping.BaseDDMTestCase;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.model.impl.ResourceActionImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;
import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Pedro Queiroz
 */
public class DDMFormInstanceUpgradeProcessTest extends BaseDDMTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	@Override
	public void setUp() throws Exception {
		_setUpDDMFormInstanceUpgradeProcess();
	}

	@Test
	public void testGetNewActionIds1() {
		_mockResourceActionLocalService(
			"RecordSet", this::_createRecorSetResourceActionList,
			"FormInstance", this::_createFormInstanceResourceActionList);

		// VIEW, ADD_RECORD

		long currentNewActionIds = 9;

		Assert.assertEquals(
			17,
			_ddmFormInstanceUpgradeProcess.getNewActionIds(
				"RecordSet", "FormInstance", 0, currentNewActionIds));
	}

	@Test
	public void testGetNewActionIds2() {
		_mockResourceActionLocalService(
			"RecordSet", this::_createRecorSetResourceActionList,
			"FormInstance", this::_createFormInstanceResourceActionList);

		// VIEW, UPDATE, ADD_RECORD

		long currentNewActionIds = 25;

		Assert.assertEquals(
			25,
			_ddmFormInstanceUpgradeProcess.getNewActionIds(
				"RecordSet", "FormInstance", 0, currentNewActionIds));
	}

	private List<ResourceAction> _createFormInstanceResourceActionList() {
		return ListUtil.fromArray(
			_createResourceAction("VIEW", 1),
			_createResourceAction("DELETE", 2),
			_createResourceAction("PERMISSIONS", 4),
			_createResourceAction("UPDATE", 8),
			_createResourceAction("ADD_FORM_INSTANCE_RECORD", 16));
	}

	private List<ResourceAction> _createRecorSetResourceActionList() {
		return ListUtil.fromArray(
			_createResourceAction("VIEW", 1),
			_createResourceAction("DELETE", 2),
			_createResourceAction("PERMISSIONS", 4),
			_createResourceAction("ADD_RECORD", 8),
			_createResourceAction("UPDATE", 16));
	}

	private ResourceAction _createResourceAction(
		String actionId, long bitwiseValue) {

		ResourceAction resourceAction = new ResourceActionImpl();

		resourceAction.setActionId(actionId);
		resourceAction.setBitwiseValue(bitwiseValue);

		return resourceAction;
	}

	private void _mockResourceActionLocalService(
		String oldName,
		Supplier<List<ResourceAction>> oldResourceActionListSupplier,
		String newName,
		Supplier<List<ResourceAction>> newResourceActionListSupplier) {

		ResourceActionLocalService resourceActionLocalService = Mockito.mock(
			ResourceActionLocalService.class);

		Mockito.when(
			resourceActionLocalService.getResourceActions(Mockito.eq(oldName))
		).thenReturn(
			oldResourceActionListSupplier.get()
		);

		Mockito.when(
			resourceActionLocalService.getResourceActions(Mockito.eq(newName))
		).thenReturn(
			newResourceActionListSupplier.get()
		);

		ReflectionTestUtil.setFieldValue(
			_ddmFormInstanceUpgradeProcess, "_resourceActionLocalService",
			resourceActionLocalService);
	}

	private void _setUpDDMFormInstanceUpgradeProcess() {
		_ddmFormInstanceUpgradeProcess = new DDMFormInstanceUpgradeProcess(
			null, null, null, null, null);
	}

	private DDMFormInstanceUpgradeProcess _ddmFormInstanceUpgradeProcess;

}