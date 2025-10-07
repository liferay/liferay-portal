/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.system.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectField;
import com.liferay.object.test.util.BaseSystemObjectDefinitionManagerTestCase;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Matyas Wollner
 */
@RunWith(Arquillian.class)
public class AddressSystemObjectDefinitionManagerTest
	extends BaseSystemObjectDefinitionManagerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testGetObjectFields() throws Exception {
		List<ObjectField> objectFields =
			systemObjectDefinitionManager.getObjectFields();

		Assert.assertEquals(objectFields.toString(), 12, objectFields.size());

		Assert.assertFalse(
			ListUtil.filter(
				objectFields,
				item -> Objects.equals(item.getName(), "addressCountry")
			).isEmpty());
		Assert.assertFalse(
			ListUtil.filter(
				objectFields,
				item -> Objects.equals(item.getName(), "addressLocality")
			).isEmpty());
		Assert.assertFalse(
			ListUtil.filter(
				objectFields,
				item -> Objects.equals(item.getName(), "addressRegion")
			).isEmpty());
		Assert.assertFalse(
			ListUtil.filter(
				objectFields,
				item -> Objects.equals(item.getName(), "addressSubtype")
			).isEmpty());
		Assert.assertFalse(
			ListUtil.filter(
				objectFields,
				item -> Objects.equals(item.getName(), "addressType")
			).isEmpty());
		Assert.assertFalse(
			ListUtil.filter(
				objectFields, item -> Objects.equals(item.getName(), "name")
			).isEmpty());
		Assert.assertFalse(
			ListUtil.filter(
				objectFields,
				item -> Objects.equals(item.getName(), "phoneNumber")
			).isEmpty());
		Assert.assertFalse(
			ListUtil.filter(
				objectFields,
				item -> Objects.equals(item.getName(), "postalCode")
			).isEmpty());
		Assert.assertFalse(
			ListUtil.filter(
				objectFields, item -> Objects.equals(item.getName(), "primary")
			).isEmpty());
		Assert.assertFalse(
			ListUtil.filter(
				objectFields,
				item -> Objects.equals(item.getName(), "streetAddressLine1")
			).isEmpty());
		Assert.assertFalse(
			ListUtil.filter(
				objectFields,
				item -> Objects.equals(item.getName(), "streetAddressLine2")
			).isEmpty());
		Assert.assertFalse(
			ListUtil.filter(
				objectFields,
				item -> Objects.equals(item.getName(), "streetAddressLine3")
			).isEmpty());
	}

	@Override
	@Test
	@TestInfo("LPD-63933")
	public void testGetOrAddEmptyBaseModel() throws Exception {
		super.testGetOrAddEmptyBaseModel();
	}

	@Override
	protected void assertGetOrAddEmptyBaseModelWithoutPermissions(
			BaseModel<?> baseModel, User user)
		throws PortalException {

		User adminUser = TestPropsValues.getUser();

		AssertUtils.assertFailure(
			PortalException.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must have UPDATE permission for ",
				User.class.getName(), " ", adminUser.getUserId()),
			() -> systemObjectDefinitionManager.getOrAddEmptyBaseModel(
				RandomTestUtil.randomString(), adminUser));

		Address address = (Address)baseModel;

		AssertUtils.assertFailure(
			PortalException.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must have VIEW permission for ",
				User.class.getName(), " ", adminUser.getUserId()),
			() -> systemObjectDefinitionManager.getOrAddEmptyBaseModel(
				address.getExternalReferenceCode(), adminUser));
	}

	@Override
	protected void assertGetOrAddEmptyBaseModelWithPermissions(
		BaseModel<?> baseModel) {

		Address address = (Address)baseModel;

		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, address.getStatus());
	}

	@Override
	protected String getSystemObjectDefinitionName() {
		return "Address";
	}

}