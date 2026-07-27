/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.info.field.converter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFactory;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.type.PhoneNumberInfoFieldType;
import com.liferay.info.field.type.RelationshipInfoFieldType;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.field.builder.IntegerObjectFieldBuilder;
import com.liferay.object.field.builder.PhoneNumberObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.info.field.converter.ObjectFieldInfoFieldConverter;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.rest.context.path.RESTContextPathResolver;
import com.liferay.object.rest.context.path.RESTContextPathResolverRegistry;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Carolina Barbosa
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class ObjectFieldInfoFieldConverterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			ListUtil.fromArray(
				new IntegerObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"integerObjectField"
				).build()),
			false);

		_objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"textObjectField"
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).readOnly(
				ObjectFieldConstants.READ_ONLY_CONDITIONAL
			).readOnlyConditionExpression(
				"not(isEmpty(integerObjectField))"
			).userId(
				TestPropsValues.getUserId()
			).build());
	}

	@Test
	public void testAddRelationshipInfoFieldAttributes() throws Exception {
		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, _objectDefinition,
				objectDefinition2);

		ObjectFieldInfoFieldConverter objectFieldInfoFieldConverter =
			new ObjectFieldInfoFieldConverter(
				null, null, null, _objectDefinitionLocalService,
				_objectFieldLocalService, null, _objectRelationshipLocalService,
				_objectScopeProviderRegistry, null, null, _portal,
				_restContextPathResolverRegistry, null, null);

		InfoField.FinalStep finalStep = InfoField.builder(
		).infoFieldType(
			RelationshipInfoFieldType.INSTANCE
		).namespace(
			RandomTestUtil.randomString()
		).name(
			RandomTestUtil.randomString()
		);

		Assert.assertEquals(
			StringPool.BLANK,
			_getRelationshipURL(
				finalStep, objectFieldInfoFieldConverter, objectRelationship,
				null));

		Group group = GroupTestUtil.addGroup();

		String url = _getRelationshipURL(
			finalStep, objectFieldInfoFieldConverter, objectRelationship,
			_getServiceContext(group.getGroupId(), null));

		Assert.assertTrue(
			url.startsWith(
				_portal.getPortalURL(new MockHttpServletRequest()) +
					_portal.getPathContext()));

		Group cmsGroup = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CMS);

		RESTContextPathResolver restContextPathResolver =
			_restContextPathResolverRegistry.getRESTContextPathResolver(
				_objectDefinition.getClassName());

		String portalURL = _portal.getPortalURL(new MockHttpServletRequest());
		String pathContext = _portal.getPathContext();

		Assert.assertEquals(
			portalURL + pathContext +
				restContextPathResolver.getRESTContextPath(0),
			_getRelationshipURL(
				finalStep, objectFieldInfoFieldConverter, objectRelationship,
				_getServiceContext(cmsGroup.getGroupId(), null)));

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), 0, null,
			Collections.emptyMap(), ServiceContextTestUtil.getServiceContext());

		Group siteGroup = GroupTestUtil.addGroup();

		objectEntry.setGroupId(siteGroup.getGroupId());

		String restContextPath = restContextPathResolver.getRESTContextPath(
			siteGroup.getGroupId());

		Assert.assertEquals(
			portalURL + pathContext + restContextPath,
			_getRelationshipURL(
				finalStep, objectFieldInfoFieldConverter, objectRelationship,
				_getServiceContext(cmsGroup.getGroupId(), objectEntry)));
	}

	@Test
	public void testAddRelationshipInfoFieldAttributesWithOrganization()
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchSystemObjectDefinition(
				TestPropsValues.getCompanyId(), "Organization");

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, objectDefinition,
				_objectDefinition);

		ObjectFieldInfoFieldConverter objectFieldInfoFieldConverter =
			new ObjectFieldInfoFieldConverter(
				null, null, null, _objectDefinitionLocalService,
				_objectFieldLocalService, null, _objectRelationshipLocalService,
				_objectScopeProviderRegistry, null, null, _portal,
				_restContextPathResolverRegistry,
				_systemObjectDefinitionManagerRegistry, null);

		InfoField.FinalStep finalStep = InfoField.builder(
		).infoFieldType(
			RelationshipInfoFieldType.INSTANCE
		).namespace(
			RandomTestUtil.randomString()
		).name(
			RandomTestUtil.randomString()
		);

		String url = _getRelationshipURL(
			finalStep, objectFieldInfoFieldConverter, objectRelationship,
			_getServiceContext(TestPropsValues.getGroupId(), null));

		Assert.assertTrue(url, url.endsWith("?fields=id,name&flatten=true"));
	}

	@Test
	public void testGetInfoField() throws Exception {
		ObjectFieldInfoFieldConverter objectFieldInfoFieldConverter =
			new ObjectFieldInfoFieldConverter(
				_ddmExpressionFactory, null, null, null, null,
				_objectFieldSettingLocalService, null, null, null, null, null,
				null, null, null);

		InfoField<?> infoField = objectFieldInfoFieldConverter.getInfoField(
			true, ObjectField.class.getSimpleName(), _objectField);

		Assert.assertFalse(infoField.isReadOnly());

		try {
			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext();

			HttpServletRequest httpServletRequest =
				new MockHttpServletRequest();

			LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
				_layoutDisplayPageProviderRegistry.
					getLayoutDisplayPageProviderByClassName(
						_objectDefinition.getCompanyId(),
						_objectDefinition.getClassName());

			ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
				0, _objectDefinition.getObjectDefinitionId(),
				Collections.singletonMap(
					"integerObjectField", RandomTestUtil.randomInt()));

			httpServletRequest.setAttribute(
				LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
				layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
					new InfoItemReference(
						_objectDefinition.getClassName(),
						objectEntry.getObjectEntryId())));

			serviceContext.setRequest(httpServletRequest);

			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			infoField = objectFieldInfoFieldConverter.getInfoField(
				true, ObjectField.class.getSimpleName(), _objectField);

			Assert.assertTrue(infoField.isReadOnly());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testGetPhoneNumberInfoField() throws Exception {
		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new PhoneNumberObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"a" + RandomTestUtil.randomString()
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).objectFieldSettings(
				Collections.singletonList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_COUNTRY_SOURCE
					).value(
						ObjectFieldSettingConstants.VALUE_DEFINED_BY_USER
					).build())
			).userId(
				TestPropsValues.getUserId()
			).build());

		ObjectFieldInfoFieldConverter objectFieldInfoFieldConverter =
			new ObjectFieldInfoFieldConverter(
				_ddmExpressionFactory, null, null, null, null,
				_objectFieldSettingLocalService, null, null, null, null, null,
				null, null, null);

		InfoField<PhoneNumberInfoFieldType> infoField =
			(InfoField<PhoneNumberInfoFieldType>)
				objectFieldInfoFieldConverter.getInfoField(
					true, ObjectField.class.getSimpleName(), objectField);

		Assert.assertSame(
			PhoneNumberInfoFieldType.INSTANCE, infoField.getInfoFieldType());
		Assert.assertNull(
			infoField.getAttribute(PhoneNumberInfoFieldType.COUNTRY));
		Assert.assertEquals(
			ObjectFieldSettingConstants.VALUE_DEFINED_BY_USER,
			infoField.getAttribute(PhoneNumberInfoFieldType.COUNTRY_SOURCE));

		objectField = ObjectFieldUtil.addCustomObjectField(
			new PhoneNumberObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"a" + RandomTestUtil.randomString()
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).objectFieldSettings(
				Arrays.asList(
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_COUNTRY
					).value(
						"US"
					).build(),
					new ObjectFieldSettingBuilder(
					).name(
						ObjectFieldSettingConstants.NAME_COUNTRY_SOURCE
					).value(
						ObjectFieldSettingConstants.VALUE_FIXED
					).build())
			).userId(
				TestPropsValues.getUserId()
			).build());

		infoField =
			(InfoField<PhoneNumberInfoFieldType>)
				objectFieldInfoFieldConverter.getInfoField(
					true, ObjectField.class.getSimpleName(), objectField);

		Assert.assertSame(
			PhoneNumberInfoFieldType.INSTANCE, infoField.getInfoFieldType());
		Assert.assertEquals(
			"US", infoField.getAttribute(PhoneNumberInfoFieldType.COUNTRY));
		Assert.assertEquals(
			ObjectFieldSettingConstants.VALUE_FIXED,
			infoField.getAttribute(PhoneNumberInfoFieldType.COUNTRY_SOURCE));
	}

	private String _getRelationshipURL(
		InfoField.FinalStep finalStep,
		ObjectFieldInfoFieldConverter objectFieldInfoFieldConverter,
		ObjectRelationship objectRelationship, ServiceContext serviceContext) {

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			InfoField<RelationshipInfoFieldType> infoField =
				(InfoField<RelationshipInfoFieldType>)
					objectFieldInfoFieldConverter.
						addRelationshipInfoFieldAttributes(
							finalStep, objectRelationship);

			return infoField.getAttribute(RelationshipInfoFieldType.URL);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private ServiceContext _getServiceContext(
			long groupId, ObjectEntry objectEntry)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		if (objectEntry != null) {
			mockHttpServletRequest.setAttribute(
				InfoDisplayWebKeys.INFO_ITEM, objectEntry);
		}

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setRequest(mockHttpServletRequest);
		serviceContext.setScopeGroupId(groupId);

		return serviceContext;
	}

	@Inject
	private DDMExpressionFactory _ddmExpressionFactory;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutDisplayPageProviderRegistry
		_layoutDisplayPageProviderRegistry;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	private ObjectField _objectField;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private ObjectScopeProviderRegistry _objectScopeProviderRegistry;

	@Inject
	private Portal _portal;

	@Inject
	private RESTContextPathResolverRegistry _restContextPathResolverRegistry;

	@Inject
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

}