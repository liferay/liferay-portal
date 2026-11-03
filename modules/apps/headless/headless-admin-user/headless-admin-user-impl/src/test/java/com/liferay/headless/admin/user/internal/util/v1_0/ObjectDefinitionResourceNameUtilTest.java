/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.util.v1_0;

import com.liferay.object.constants.ObjectConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.definition.util.ObjectDefinitionUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Lianne Louie
 */
public class ObjectDefinitionResourceNameUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		Mockito.when(
			_objectDefinition.getClassName()
		).thenReturn(
			_CLASS_NAME
		);

		Mockito.when(
			_objectDefinition.getExternalReferenceCode()
		).thenReturn(
			_EXTERNAL_REFERENCE_CODE
		);

		Mockito.when(
			_objectDefinition.getObjectDefinitionId()
		).thenReturn(
			_OBJECT_DEFINITION_ID
		);

		Mockito.when(
			_objectDefinition.getPortletId()
		).thenReturn(
			_PORTLET_ID
		);

		Mockito.when(
			_objectDefinitionLocalService.fetchObjectDefinition(
				_OBJECT_DEFINITION_ID)
		).thenReturn(
			_objectDefinition
		);

		Mockito.when(
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				_COMPANY_ID, _CLASS_NAME)
		).thenReturn(
			_objectDefinition
		);

		Mockito.when(
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					_EXTERNAL_REFERENCE_CODE, _COMPANY_ID)
		).thenReturn(
			_objectDefinition
		);
	}

	@Test
	public void testToExternalReferenceCodeResourceName() {
		Assert.assertEquals(
			_PREFIX_CLASS_NAME + _EXTERNAL_REFERENCE_CODE,
			ObjectDefinitionResourceNameUtil.
				toExternalReferenceCodeResourceName(
					_COMPANY_ID, _objectDefinitionLocalService, _CLASS_NAME));
		Assert.assertEquals(
			_PREFIX_PORTLET_ID + _EXTERNAL_REFERENCE_CODE,
			ObjectDefinitionResourceNameUtil.
				toExternalReferenceCodeResourceName(
					_COMPANY_ID, _objectDefinitionLocalService, _PORTLET_ID));
		Assert.assertEquals(
			_PREFIX_RESOURCE_NAME + _EXTERNAL_REFERENCE_CODE,
			ObjectDefinitionResourceNameUtil.
				toExternalReferenceCodeResourceName(
					_COMPANY_ID, _objectDefinitionLocalService,
					_PREFIX_RESOURCE_NAME + _OBJECT_DEFINITION_ID));

		String className = _PREFIX_CLASS_NAME + RandomTestUtil.randomString();

		Assert.assertEquals(
			className,
			ObjectDefinitionResourceNameUtil.
				toExternalReferenceCodeResourceName(
					_COMPANY_ID, _objectDefinitionLocalService, className));

		String portletId = _PREFIX_PORTLET_ID + RandomTestUtil.randomString();

		Assert.assertEquals(
			portletId,
			ObjectDefinitionResourceNameUtil.
				toExternalReferenceCodeResourceName(
					_COMPANY_ID, _objectDefinitionLocalService, portletId));

		String resourceName =
			_PREFIX_RESOURCE_NAME + RandomTestUtil.randomLong();

		Assert.assertEquals(
			resourceName,
			ObjectDefinitionResourceNameUtil.
				toExternalReferenceCodeResourceName(
					_COMPANY_ID, _objectDefinitionLocalService, resourceName));

		resourceName = RandomTestUtil.randomString();

		Assert.assertEquals(
			resourceName,
			ObjectDefinitionResourceNameUtil.
				toExternalReferenceCodeResourceName(
					_COMPANY_ID, _objectDefinitionLocalService, resourceName));
	}

	@Test
	public void testToResourceName() {
		Assert.assertEquals(
			_CLASS_NAME,
			ObjectDefinitionResourceNameUtil.toResourceName(
				_COMPANY_ID, _objectDefinitionLocalService,
				_PREFIX_CLASS_NAME + _EXTERNAL_REFERENCE_CODE));
		Assert.assertEquals(
			_PORTLET_ID,
			ObjectDefinitionResourceNameUtil.toResourceName(
				_COMPANY_ID, _objectDefinitionLocalService,
				_PREFIX_PORTLET_ID + _EXTERNAL_REFERENCE_CODE));
		Assert.assertEquals(
			_PREFIX_RESOURCE_NAME + _OBJECT_DEFINITION_ID,
			ObjectDefinitionResourceNameUtil.toResourceName(
				_COMPANY_ID, _objectDefinitionLocalService,
				_PREFIX_RESOURCE_NAME + _EXTERNAL_REFERENCE_CODE));

		String className = _PREFIX_CLASS_NAME + RandomTestUtil.randomString();

		Assert.assertEquals(
			className,
			ObjectDefinitionResourceNameUtil.toResourceName(
				_COMPANY_ID, _objectDefinitionLocalService, className));

		String portletId = _PREFIX_PORTLET_ID + RandomTestUtil.randomString();

		Assert.assertEquals(
			portletId,
			ObjectDefinitionResourceNameUtil.toResourceName(
				_COMPANY_ID, _objectDefinitionLocalService, portletId));

		String resourceName =
			_PREFIX_RESOURCE_NAME + RandomTestUtil.randomString();

		Assert.assertEquals(
			resourceName,
			ObjectDefinitionResourceNameUtil.toResourceName(
				_COMPANY_ID, _objectDefinitionLocalService, resourceName));

		resourceName = RandomTestUtil.randomString();

		Assert.assertEquals(
			resourceName,
			ObjectDefinitionResourceNameUtil.toResourceName(
				_COMPANY_ID, _objectDefinitionLocalService, resourceName));
	}

	private static final String _CLASS_NAME =
		ObjectDefinitionConstants.CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION +
			RandomTestUtil.randomString();

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final String _EXTERNAL_REFERENCE_CODE =
		RandomTestUtil.randomString();

	private static final long _OBJECT_DEFINITION_ID =
		RandomTestUtil.randomLong();

	private static final String _PORTLET_ID = ObjectDefinitionUtil.getPortletId(
		_CLASS_NAME);

	private static final String _PREFIX_CLASS_NAME =
		ObjectDefinitionConstants.CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION;

	private static final String _PREFIX_PORTLET_ID =
		ObjectPortletKeys.OBJECT_DEFINITIONS + StringPool.UNDERLINE;

	private static final String _PREFIX_RESOURCE_NAME =
		ObjectConstants.RESOURCE_NAME + StringPool.POUND;

	private final ObjectDefinition _objectDefinition = Mockito.mock(
		ObjectDefinition.class);
	private final ObjectDefinitionLocalService _objectDefinitionLocalService =
		Mockito.mock(ObjectDefinitionLocalService.class);

}