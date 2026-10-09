/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.type.controller.TestLayoutTypeController;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.model.impl.LayoutImpl;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.LayoutTypeControllerTracker;

import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Philip Jones
 */
@RunWith(Arquillian.class)
public class LayoutTypeControllerTrackerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetLayoutTypeController1() {
		Layout layout = new TestLayoutImpl();

		LayoutTypeController defaultLayoutTypeController =
			_getDefaultLayoutTypeController();

		LayoutTypeController layoutTypeController =
			LayoutTypeControllerTracker.getLayoutTypeController(layout);

		Assert.assertNotNull(layoutTypeController);

		Assert.assertEquals(
			defaultLayoutTypeController.getClass(),
			layoutTypeController.getClass());

		layout.setType("testLayoutTypeController");

		layoutTypeController =
			LayoutTypeControllerTracker.getLayoutTypeController(layout);

		Assert.assertNotNull(layoutTypeController);

		Assert.assertEquals(
			TestLayoutTypeController.class, layoutTypeController.getClass());
	}

	@Test
	public void testGetLayoutTypeController2() {
		LayoutTypeController layoutTypeController =
			LayoutTypeControllerTracker.getLayoutTypeController(
				RandomTestUtil.randomString());

		Assert.assertNotNull(layoutTypeController);

		LayoutTypeController defaultLayoutTypeController =
			_getDefaultLayoutTypeController();

		Assert.assertEquals(
			defaultLayoutTypeController.getClass(),
			layoutTypeController.getClass());

		layoutTypeController =
			LayoutTypeControllerTracker.getLayoutTypeController(
				"testLayoutTypeController");

		Assert.assertNotNull(layoutTypeController);

		Assert.assertEquals(
			TestLayoutTypeController.class, layoutTypeController.getClass());
	}

	@Test
	public void testGetLayoutTypeControllers() {
		Map<String, LayoutTypeController> layoutTypeControllers =
			LayoutTypeControllerTracker.getLayoutTypeControllers();

		Assert.assertNotNull(layoutTypeControllers);

		LayoutTypeController layoutTypeController = layoutTypeControllers.get(
			"testLayoutTypeController");

		Class<?> clazz = layoutTypeController.getClass();

		Assert.assertEquals(
			TestLayoutTypeController.class.getName(), clazz.getName());
	}

	@Test
	public void testGetTypes() {
		String[] types = LayoutTypeControllerTracker.getTypes();

		Assert.assertNotNull(types);

		boolean found = false;

		for (String type : types) {
			if (!type.equals("testLayoutTypeController")) {
				continue;
			}

			found = true;
		}

		Assert.assertTrue(found);
	}

	private LayoutTypeController _getDefaultLayoutTypeController() {
		return LayoutTypeControllerTracker.getLayoutTypeController(
			LayoutConstants.TYPE_PORTLET);
	}

	private static class TestLayoutImpl extends LayoutImpl {

		@Override
		public String getName() {
			return toString();
		}

		@Override
		public String getName(Locale locale) {
			return toString();
		}

		@Override
		public String toString() {
			return getParentLayoutId() + "/" + getPlid();
		}

	}

}