/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item;

import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.field.type.RelationshipInfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.info.item.util.ObjectEntryInfoItemUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.Collections;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Carolina Barbosa
 */
public abstract class BaseObjectEntryInfoItemTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_objectDefinition1 = ObjectDefinitionTestUtil.publishObjectDefinition(
			Collections.singletonList(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"name"
				).build()));

		_objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition1.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"name", RandomTestUtil.randomString()
			).build());

		objectDefinition2 = ObjectDefinitionTestUtil.publishObjectDefinition(
			Collections.singletonList(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"name"
				).build()));

		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			0, objectDefinition2.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"name", RandomTestUtil.randomString()
			).build());

		_objectRelationship = ObjectRelationshipTestUtil.addObjectRelationship(
			_objectRelationshipLocalService, _objectDefinition1,
			objectDefinition2);

		_objectField = _objectFieldLocalService.fetchObjectField(
			_objectRelationship.getObjectFieldId2());

		ServiceContextThreadLocal.pushServiceContext(_getServiceContext());
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	protected void assertObjectEntryValues(String name1, String name2) {
		_objectEntry1 = objectEntryLocalService.fetchObjectEntry(
			_objectEntry1.getObjectEntryId());

		Assert.assertEquals(
			name1, MapUtil.getString(_objectEntry1.getValues(), "name"));

		objectEntry2 = objectEntryLocalService.fetchObjectEntry(
			objectEntry2.getObjectEntryId());

		Assert.assertEquals(
			name2, MapUtil.getString(objectEntry2.getValues(), "name"));
	}

	protected InfoItemFieldValues geInfoItemFieldValues(
		String name1, String name2) {

		return InfoItemFieldValues.builder(
		).infoFieldValue(
			new InfoFieldValue<>(
				InfoField.builder(
				).infoFieldType(
					RelationshipInfoFieldType.INSTANCE
				).namespace(
					ObjectField.class.getSimpleName()
				).name(
					_objectField.getName()
				).build(),
				_objectEntry1.getObjectEntryId())
		).infoFieldValue(
			new InfoFieldValue<>(
				InfoField.builder(
				).infoFieldType(
					TextInfoFieldType.INSTANCE
				).namespace(
					ObjectEntryInfoItemUtil.getInfoFieldNamespace(
						_objectDefinition1, _objectRelationship)
				).name(
					"name"
				).build(),
				name1)
		).infoFieldValue(
			new InfoFieldValue<>(
				InfoField.builder(
				).infoFieldType(
					TextInfoFieldType.INSTANCE
				).namespace(
					ObjectField.class.getSimpleName()
				).name(
					"name"
				).build(),
				name2)
		).build();
	}

	@Inject
	protected InfoItemServiceRegistry infoItemServiceRegistry;

	@DeleteAfterTestRun
	protected ObjectDefinition objectDefinition2;

	protected ObjectEntry objectEntry2;

	@Inject
	protected ObjectEntryLocalService objectEntryLocalService;

	private ServiceContext _getServiceContext() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		serviceContext.setRequest(httpServletRequest);

		return serviceContext;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setScopeGroupId(TestPropsValues.getGroupId());
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition1;

	private ObjectEntry _objectEntry1;
	private ObjectField _objectField;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	private ObjectRelationship _objectRelationship;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}