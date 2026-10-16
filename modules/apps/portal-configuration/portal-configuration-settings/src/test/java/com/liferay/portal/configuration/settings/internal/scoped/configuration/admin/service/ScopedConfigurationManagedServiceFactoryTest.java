/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.settings.internal.scoped.configuration.admin.service;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class ScopedConfigurationManagedServiceFactoryTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testDeleted() throws Exception {
		_updated("pid1", _COMPANY_ID_1, "value1");
		_updated("pid2", _COMPANY_ID_1, "value2");
		_updated("pid3", _COMPANY_ID_1, "value3");
		_updated("pid1", _COMPANY_ID_1, "value4");
		_updated("pid4", _COMPANY_ID_2, "value5");
		_updated("pid5", _COMPANY_ID_1, _GROUP_ID, "value6");

		_scopedConfigurationManagedServiceFactory.deleted("pid2");

		Assert.assertEquals("value4", _getCompanyValue(_COMPANY_ID_1));

		_scopedConfigurationManagedServiceFactory.deleted("pid1");

		Assert.assertEquals("value3", _getCompanyValue(_COMPANY_ID_1));

		_scopedConfigurationManagedServiceFactory.deleted("pid3");

		Assert.assertNull(_getCompanyValue(_COMPANY_ID_1));
		Assert.assertEquals("value5", _getCompanyValue(_COMPANY_ID_2));
		Assert.assertEquals("value6", _getGroupValue(_COMPANY_ID_1, _GROUP_ID));
	}

	@Test
	public void testUpdated() throws Exception {
		_updated("pid1", _COMPANY_ID_1, "value1");
		_updated("pid2", _COMPANY_ID_2, "value2");
		_updated("pid3", _COMPANY_ID_1, _GROUP_ID, "value3");

		Assert.assertEquals("value1", _getCompanyValue(_COMPANY_ID_1));

		_updated("pid4", _COMPANY_ID_1, "value4");

		Assert.assertEquals("value4", _getCompanyValue(_COMPANY_ID_1));

		_updated("pid1", _COMPANY_ID_1, "value5");

		Assert.assertEquals("value5", _getCompanyValue(_COMPANY_ID_1));
		Assert.assertEquals("value2", _getCompanyValue(_COMPANY_ID_2));
		Assert.assertEquals("value3", _getGroupValue(_COMPANY_ID_1, _GROUP_ID));

		Assert.assertEquals(
			"value2",
			_getCompanyValueDuringUpdated("pid2", _COMPANY_ID_2, "value6"));
		Assert.assertEquals("value6", _getCompanyValue(_COMPANY_ID_2));

		Assert.assertEquals(
			"value5",
			_getCompanyValueDuringUpdated("pid1", _COMPANY_ID_1, "value7"));
		Assert.assertEquals("value7", _getCompanyValue(_COMPANY_ID_1));
	}

	@Meta.OCD(
		id = "com.liferay.portal.configuration.settings.internal.scoped.configuration.admin.service.ScopedConfigurationManagedServiceFactoryTest"
	)
	public interface TestConfiguration {

		public String value();

	}

	private String _getCompanyValue(long companyId) {
		return _getValue(
			_scopedConfigurationManagedServiceFactory.getConfiguration(
				companyId, ExtendedObjectClassDefinition.Scope.COMPANY,
				companyId));
	}

	private String _getCompanyValueDuringUpdated(
			String pid, long companyId, String value)
		throws Exception {

		AtomicReference<String> valueReference = new AtomicReference<>();

		_scopedConfigurationManagedServiceFactory.updated(
			pid,
			new HashMapDictionary<String, Object>(
				HashMapBuilder.<String, Object>put(
					ExtendedObjectClassDefinition.Scope.COMPANY.
						getPropertyKey(),
					companyId
				).put(
					"value", value
				).build()) {

				@Override
				public Object get(Object key) {
					if (Objects.equals(key, "value")) {
						valueReference.set(_getCompanyValue(companyId));
					}

					return super.get(key);
				}

			});

		return valueReference.get();
	}

	private String _getGroupValue(long companyId, long groupId) {
		return _getValue(
			_scopedConfigurationManagedServiceFactory.getConfiguration(
				companyId, ExtendedObjectClassDefinition.Scope.GROUP, groupId));
	}

	private String _getValue(Object configuration) {
		if (configuration instanceof TestConfiguration testConfiguration) {
			return testConfiguration.value();
		}

		return null;
	}

	private void _updated(
			String pid, long companyId, long groupId, String value)
		throws Exception {

		_scopedConfigurationManagedServiceFactory.updated(
			pid,
			HashMapDictionaryBuilder.<String, Object>put(
				ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey(),
				companyId
			).put(
				ExtendedObjectClassDefinition.Scope.GROUP.getPropertyKey(),
				groupId
			).put(
				"value", value
			).build());
	}

	private void _updated(String pid, long companyId, String value)
		throws Exception {

		_updated(pid, companyId, GroupConstants.ANY_PARENT_GROUP_ID, value);
	}

	private static final long _COMPANY_ID_1 = 1;

	private static final long _COMPANY_ID_2 = 2;

	private static final long _GROUP_ID = 3;

	private final ScopedConfigurationManagedServiceFactory
		_scopedConfigurationManagedServiceFactory =
			new ScopedConfigurationManagedServiceFactory(
				TestConfiguration.class, null);

}