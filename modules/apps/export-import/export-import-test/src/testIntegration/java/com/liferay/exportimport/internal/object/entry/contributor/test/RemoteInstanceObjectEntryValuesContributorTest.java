/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.object.entry.contributor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.encryptor.Encryptor;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@FeatureFlag("LPD-71352")
@RunWith(Arquillian.class)
public class RemoteInstanceObjectEntryValuesContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_objectDefinition = _objectDefinitionLocalService.getObjectDefinition(
			TestPropsValues.getCompanyId(), "RemoteInstance");
	}

	@Test
	public void testContribute() throws Exception {
		_testContributeOnAdd();
		_testContributeOnAddWithKeyReference();
		_testContributeOnPartialUpdateWithoutClientSecret();
		_testContributeOnUpdateWithEncryptedClientSecret();
		_testContributeOnUpdateWithPlainClientSecret();
	}

	private ObjectEntry _addObjectEntry(String clientSecret) throws Exception {
		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"clientId", RandomTestUtil.randomString()
			).put(
				"clientSecret", clientSecret
			).put(
				"hostname", RandomTestUtil.randomString()
			).put(
				"name", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private String _decrypt(String clientSecret) throws Exception {
		Assert.assertTrue(clientSecret.startsWith("{AES_GCM}"));

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		return _encryptor.decryptAuthenticated(
			company.getKeyObj(), clientSecret.substring("{AES_GCM}".length()));
	}

	private String _getClientSecret(long objectEntryId) throws Exception {
		ObjectEntry objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntryId);

		return MapUtil.getString(objectEntry.getValues(), "clientSecret");
	}

	private void _testContributeOnAdd() throws Exception {
		String clientSecret = RandomTestUtil.randomString();

		ObjectEntry objectEntry = _addObjectEntry(clientSecret);

		String storedClientSecret = _getClientSecret(
			objectEntry.getObjectEntryId());

		Assert.assertNotEquals(clientSecret, storedClientSecret);
		Assert.assertEquals(clientSecret, _decrypt(storedClientSecret));

		_objectEntryLocalService.deleteObjectEntry(objectEntry);
	}

	private void _testContributeOnAddWithKeyReference() throws Exception {
		String clientSecret = StringBundler.concat(
			"${secretRef:", RandomTestUtil.randomString(), ":",
			RandomTestUtil.randomString(), "}");

		ObjectEntry objectEntry = _addObjectEntry(clientSecret);

		Assert.assertEquals(
			clientSecret, _getClientSecret(objectEntry.getObjectEntryId()));

		_objectEntryLocalService.deleteObjectEntry(objectEntry);
	}

	private void _testContributeOnPartialUpdateWithoutClientSecret()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry(
			RandomTestUtil.randomString());

		String storedClientSecret = _getClientSecret(
			objectEntry.getObjectEntryId());

		_objectEntryLocalService.partialUpdateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(), 0,
			HashMapBuilder.<String, Serializable>put(
				"hostname", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			storedClientSecret,
			_getClientSecret(objectEntry.getObjectEntryId()));

		_objectEntryLocalService.deleteObjectEntry(objectEntry);
	}

	private void _testContributeOnUpdateWithEncryptedClientSecret()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry(
			RandomTestUtil.randomString());

		String storedClientSecret = _getClientSecret(
			objectEntry.getObjectEntryId());

		_updateObjectEntry(objectEntry, storedClientSecret);

		Assert.assertEquals(
			storedClientSecret,
			_getClientSecret(objectEntry.getObjectEntryId()));

		_objectEntryLocalService.deleteObjectEntry(objectEntry);
	}

	private void _testContributeOnUpdateWithPlainClientSecret()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry(
			RandomTestUtil.randomString());

		String clientSecret = RandomTestUtil.randomString();

		_updateObjectEntry(objectEntry, clientSecret);

		Assert.assertEquals(
			clientSecret,
			_decrypt(_getClientSecret(objectEntry.getObjectEntryId())));

		_objectEntryLocalService.deleteObjectEntry(objectEntry);
	}

	private void _updateObjectEntry(
			ObjectEntry objectEntry, String clientSecret)
		throws Exception {

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(), 0,
			HashMapBuilder.<String, Serializable>putAll(
				objectEntry.getValues()
			).put(
				"clientSecret", clientSecret
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private Encryptor _encryptor;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}