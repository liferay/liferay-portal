/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.upgrade.v5_2_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.constants.DDMTemplateConstants;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateVersionLocalService;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Yuri Monteiro
 */
@RunWith(Arquillian.class)
public class DDMFacetTemplateUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	@Test
	public void testUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		Map<String, String> facetClassNameMap =
			ReflectionTestUtil.getFieldValue(
				upgradeProcess, "_newFacetTemplateClassNames");

		Map.Entry<String, String> entry = facetClassNameMap.entrySet(
		).iterator(
		).next();

		String oldClassName = entry.getKey();
		String newClassName = entry.getValue();

		long oldClassNameId = _classNameLocalService.getClassNameId(
			oldClassName);
		long newClassNameId = _classNameLocalService.getClassNameId(
			newClassName);

		long resourceClassNameId = _classNameLocalService.getClassNameId(
			"com.liferay.portlet.display.template.PortletDisplayTemplate");

		DDMTemplate template = _ddmTemplateLocalService.addTemplate(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			oldClassNameId, 0, resourceClassNameId,
			HashMapBuilder.put(
				LocaleUtil.US, "Name"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.US, "Description"
			).build(),
			DDMTemplateConstants.TEMPLATE_TYPE_DISPLAY,
			DDMTemplateConstants.TEMPLATE_MODE_EDIT, "freemarker",
			"<#-- test -->", ServiceContextThreadLocal.getServiceContext());

		long templateId = template.getTemplateId();

		long persistedTemplateClassNameIdBefore;

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select classNameId from DDMTemplate where templateId = ?")) {

			preparedStatement.setLong(1, templateId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				Assert.assertTrue(resultSet.next());

				persistedTemplateClassNameIdBefore = resultSet.getLong(1);
			}
		}

		Assert.assertEquals(oldClassNameId, persistedTemplateClassNameIdBefore);

		long persistedTemplateVersionClassNameIdBefore;

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select classNameId from DDMTemplateVersion where templateId " +
					"= ?")) {

			preparedStatement.setLong(1, templateId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				Assert.assertTrue(resultSet.next());

				persistedTemplateVersionClassNameIdBefore = resultSet.getLong(
					1);
			}
		}

		Assert.assertEquals(
			oldClassNameId, persistedTemplateVersionClassNameIdBefore);

		upgradeProcess.upgrade();

		long persistedTemplateClassNameIdAfter;

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select classNameId from DDMTemplate where templateId = ?")) {

			preparedStatement.setLong(1, templateId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				Assert.assertTrue(resultSet.next());

				persistedTemplateClassNameIdAfter = resultSet.getLong(1);
			}
		}

		Assert.assertEquals(newClassNameId, persistedTemplateClassNameIdAfter);

		long persistedTemplateVersionClassNameIdAfter;

		try (Connection connection = DataAccess.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select classNameId from DDMTemplateVersion where templateId " +
					"= ?")) {

			preparedStatement.setLong(1, templateId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				Assert.assertTrue(resultSet.next());

				persistedTemplateVersionClassNameIdAfter = resultSet.getLong(1);
			}
		}

		Assert.assertEquals(
			newClassNameId, persistedTemplateVersionClassNameIdAfter);
	}

	private static final String _CLASS_NAME =
		"com.liferay.dynamic.data.mapping.internal.upgrade.v5_2_0." +
			"DDMFacetTemplateUpgradeProcess";

	@Inject(
		filter = "(&(component.name=com.liferay.dynamic.data.mapping.internal.upgrade.registry.DDMServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Inject
	private DDMTemplateVersionLocalService _ddmTemplateVersionLocalService;

	@DeleteAfterTestRun
	private Group _group;

}