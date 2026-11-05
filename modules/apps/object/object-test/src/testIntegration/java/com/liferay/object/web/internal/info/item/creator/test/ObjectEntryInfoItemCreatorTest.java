/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item.creator.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.field.RelatedInfoFieldValue;
import com.liferay.info.field.type.RelationshipInfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.creator.InfoItemCreator;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.info.item.util.ObjectEntryInfoItemUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.ObjectRelatedModelsProvider;
import com.liferay.object.related.models.ObjectRelatedModelsProviderRegistry;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.object.web.internal.info.item.BaseObjectEntryInfoItemTestCase;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.InfoFormException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class ObjectEntryInfoItemCreatorTest
	extends BaseObjectEntryInfoItemTestCase {

	@Test
	public void testCreateFromInfoItemFieldValues() throws Exception {
		InfoItemCreator<ObjectEntry> infoItemCreator =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemCreator.class, objectDefinition2.getClassName());

		String name1 = RandomTestUtil.randomString();
		String name2 = RandomTestUtil.randomString();

		objectEntry2 = infoItemCreator.createFromInfoItemFieldValues(
			0, geInfoItemFieldValues(name1, name2),
			WorkflowConstants.STATUS_APPROVED);

		assertObjectEntryValues(name1, name2);
	}

	@Test
	public void testCreateFromInfoItemFieldValuesWithManyToManyObjectRelationship()
		throws Exception {

		_objectDefinition = _publishObjectDefinition();

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, objectDefinition2,
				_objectDefinition,
				ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE,
				StringUtil.randomId(),
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		_assertCreateFromInfoItemFieldValuesFails(
			objectRelationship, String.valueOf(RandomTestUtil.randomLong()));
		_assertCreateFromInfoItemFieldValuesFails(
			objectRelationship,
			String.valueOf(objectEntry2.getObjectEntryId()));

		InfoItemCreator<ObjectEntry> infoItemCreator =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemCreator.class, objectDefinition2.getClassName());

		ObjectEntry relatedObjectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"name", RandomTestUtil.randomString()
			).build());

		objectEntry2 = infoItemCreator.createFromInfoItemFieldValues(
			0,
			_getInfoItemFieldValues(
				objectRelationship,
				String.valueOf(relatedObjectEntry.getObjectEntryId())),
			WorkflowConstants.STATUS_APPROVED);

		List<ObjectEntry> objectEntries =
			objectEntryLocalService.getManyToManyObjectEntries(
				0, objectRelationship.getObjectRelationshipId(),
				objectEntry2.getObjectEntryId(), true, false, null,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(objectEntries.toString(), 1, objectEntries.size());

		ObjectEntry objectEntry = objectEntries.get(0);

		Assert.assertEquals(
			relatedObjectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryId());
	}

	@Test
	public void testCreateFromInfoItemFieldValuesWithManyToManySystemObjectRelationship()
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchSystemObjectDefinition(
				TestPropsValues.getCompanyId(), "Organization");

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, objectDefinition2,
				objectDefinition,
				ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE,
				StringUtil.randomId(),
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		_assertCreateFromInfoItemFieldValuesFails(
			objectRelationship, String.valueOf(RandomTestUtil.randomLong()));

		ServiceContextThreadLocal.pushServiceContext(new ServiceContext());

		try {
			_company = CompanyTestUtil.addCompany();
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company.getCompanyId())) {

			User user = UserTestUtil.getAdminUser(_company.getCompanyId());

			_organization = _organizationLocalService.addOrganization(
				user.getUserId(),
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
				RandomTestUtil.randomString(), false);
		}

		_assertCreateFromInfoItemFieldValuesFails(
			objectRelationship,
			String.valueOf(_organization.getOrganizationId()));

		InfoItemCreator<ObjectEntry> infoItemCreator =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemCreator.class, objectDefinition2.getClassName());

		_organization = OrganizationTestUtil.addOrganization();

		objectEntry2 = infoItemCreator.createFromInfoItemFieldValues(
			0,
			_getInfoItemFieldValues(
				objectRelationship,
				String.valueOf(_organization.getOrganizationId())),
			WorkflowConstants.STATUS_APPROVED);

		ObjectRelatedModelsProvider<Organization> objectRelatedModelsProvider =
			_objectRelatedModelsProviderRegistry.getObjectRelatedModelsProvider(
				objectDefinition.getClassName(), TestPropsValues.getCompanyId(),
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		List<Organization> organizations =
			objectRelatedModelsProvider.getRelatedModels(
				0, objectRelationship.getObjectRelationshipId(), null, false,
				objectEntry2.getObjectEntryId(), null, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(organizations.toString(), 1, organizations.size());

		Organization organization = organizations.get(0);

		Assert.assertEquals(
			_organization.getOrganizationId(),
			organization.getOrganizationId());
	}

	@Test
	public void testCreateFromInfoItemFieldValuesWithNestedManyToManyObjectRelationship()
		throws Exception {

		ObjectDefinition childObjectDefinition = _publishObjectDefinition();

		String namespace = ObjectEntryInfoItemUtil.getInfoFieldNamespace(
			childObjectDefinition,
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, objectDefinition2,
				childObjectDefinition,
				ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE,
				StringUtil.randomId(),
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY));

		_objectDefinition = _publishObjectDefinition();

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, childObjectDefinition,
				_objectDefinition,
				ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE,
				StringUtil.randomId(),
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		InfoItemCreator<ObjectEntry> infoItemCreator =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemCreator.class, objectDefinition2.getClassName());

		RelatedInfoFieldValue.RelatedInfoFieldValueIdentifier
			relatedInfoFieldValueIdentifier =
				new RelatedInfoFieldValue.RelatedInfoFieldValueIdentifier(
					StringUtil.randomId(), StringUtil.randomId());

		ObjectEntry relatedObjectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"name", RandomTestUtil.randomString()
			).build());

		InfoField<RelationshipInfoFieldType> relationshipInfoField =
			InfoField.builder(
			).infoFieldType(
				RelationshipInfoFieldType.INSTANCE
			).namespace(
				namespace
			).name(
				ObjectRelationshipConstants.
					OBJECT_RELATIONSHIP_FIELD_NAME_PREFIX +
						objectRelationship.getName()
			).attribute(
				RelationshipInfoFieldType.MULTIPLE, true
			).build();

		InfoField<TextInfoFieldType> textInfoField = InfoField.builder(
		).infoFieldType(
			TextInfoFieldType.INSTANCE
		).namespace(
			namespace
		).name(
			"name"
		).build();

		objectEntry2 = infoItemCreator.createFromInfoItemFieldValues(
			0,
			InfoItemFieldValues.builder(
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
					RandomTestUtil.randomString())
			).infoFieldValue(
				new InfoFieldValue<>(
					relationshipInfoField,
					new RelatedInfoFieldValue<>(
						HashMapBuilder.
							<RelatedInfoFieldValue.
								RelatedInfoFieldValueIdentifier,
							 InfoFieldValue<Object>>put(
								relatedInfoFieldValueIdentifier,
								new InfoFieldValue<>(
									relationshipInfoField,
									Collections.singletonList(
										String.valueOf(
											relatedObjectEntry.
												getObjectEntryId())))
							).build()))
			).infoFieldValue(
				new InfoFieldValue<>(
					textInfoField,
					new RelatedInfoFieldValue<>(
						HashMapBuilder.
							<RelatedInfoFieldValue.
								RelatedInfoFieldValueIdentifier,
							 InfoFieldValue<Object>>put(
								relatedInfoFieldValueIdentifier,
								new InfoFieldValue<>(
									textInfoField,
									RandomTestUtil.randomString())
							).build()))
			).build(),
			WorkflowConstants.STATUS_APPROVED);

		ObjectEntry childObjectEntry = objectEntryLocalService.fetchObjectEntry(
			relatedInfoFieldValueIdentifier.getExternalReferenceCode(), 0,
			childObjectDefinition.getObjectDefinitionId());

		List<ObjectEntry> objectEntries =
			objectEntryLocalService.getManyToManyObjectEntries(
				0, objectRelationship.getObjectRelationshipId(),
				childObjectEntry.getObjectEntryId(), true, false, null,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(objectEntries.toString(), 1, objectEntries.size());

		ObjectEntry objectEntry = objectEntries.get(0);

		Assert.assertEquals(
			relatedObjectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryId());

		_objectDefinitionLocalService.deleteObjectDefinition(
			childObjectDefinition);
	}

	private void _assertCreateFromInfoItemFieldValuesFails(
			ObjectRelationship objectRelationship, String relatedObjectEntryId)
		throws Exception {

		InfoItemCreator<ObjectEntry> infoItemCreator =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemCreator.class, objectDefinition2.getClassName());

		AssertUtils.assertFailure(
			InfoFormException.class, null,
			() -> infoItemCreator.createFromInfoItemFieldValues(
				0,
				_getInfoItemFieldValues(
					objectRelationship, relatedObjectEntryId),
				WorkflowConstants.STATUS_APPROVED));
	}

	private InfoItemFieldValues _getInfoItemFieldValues(
		ObjectRelationship objectRelationship, String relatedObjectEntryId) {

		return InfoItemFieldValues.builder(
		).infoFieldValue(
			new InfoFieldValue<>(
				InfoField.builder(
				).infoFieldType(
					RelationshipInfoFieldType.INSTANCE
				).namespace(
					ObjectField.class.getSimpleName()
				).name(
					ObjectRelationshipConstants.
						OBJECT_RELATIONSHIP_FIELD_NAME_PREFIX +
							objectRelationship.getName()
				).attribute(
					RelationshipInfoFieldType.MULTIPLE, true
				).build(),
				Collections.singletonList(relatedObjectEntryId))
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
				RandomTestUtil.randomString())
		).build();
	}

	private ObjectDefinition _publishObjectDefinition() throws Exception {
		return ObjectDefinitionTestUtil.publishObjectDefinition(
			Collections.singletonList(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"name"
				).build()),
			false);
	}

	@DeleteAfterTestRun
	private Company _company;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectRelatedModelsProviderRegistry
		_objectRelatedModelsProviderRegistry;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@DeleteAfterTestRun
	private Organization _organization;

	@Inject
	private OrganizationLocalService _organizationLocalService;

}