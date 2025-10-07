/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.type.test;

import com.liferay.info.type.KeyLocalizedLabelPair;
import com.liferay.list.type.entry.util.ListTypeEntryUtil;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.notification.constants.NotificationRecipientConstants;
import com.liferay.notification.constants.NotificationTemplateConstants;
import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.model.NotificationQueueEntry;
import com.liferay.notification.model.NotificationRecipientSetting;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.NotificationQueueEntryLocalService;
import com.liferay.notification.service.NotificationRecipientLocalService;
import com.liferay.notification.service.NotificationRecipientSettingLocalService;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.notification.test.util.NotificationTemplateUtil;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.definition.notification.term.util.ObjectDefinitionNotificationTermUtil;
import com.liferay.object.field.builder.AssigneeObjectFieldBuilder;
import com.liferay.object.field.builder.AttachmentObjectFieldBuilder;
import com.liferay.object.field.builder.BooleanObjectFieldBuilder;
import com.liferay.object.field.builder.DateObjectFieldBuilder;
import com.liferay.object.field.builder.DateTimeObjectFieldBuilder;
import com.liferay.object.field.builder.IntegerObjectFieldBuilder;
import com.liferay.object.field.builder.LongIntegerObjectFieldBuilder;
import com.liferay.object.field.builder.MultiselectPicklistObjectFieldBuilder;
import com.liferay.object.field.builder.PicklistObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.rest.dto.v1_0.ListEntry;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectActionTestUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.time.Month;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * @author Feliphe Marinho
 */
public class BaseNotificationTypeTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		ListTypeEntry listTypeEntry1 = ListTypeEntryUtil.createListTypeEntry(
			RandomTestUtil.randomString(),
			Collections.singletonMap(LocaleUtil.US, "listTypeEntry1Value"));
		ListTypeEntry listTypeEntry2 = ListTypeEntryUtil.createListTypeEntry(
			RandomTestUtil.randomString(),
			Collections.singletonMap(LocaleUtil.US, "listTypeEntry2Value"));

		_listTypeDefinition =
			_listTypeDefinitionLocalService.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				false, Arrays.asList(listTypeEntry1, listTypeEntry2));

		childObjectEntryValues = LinkedHashMapBuilder.<String, Object>put(
			"booleanObjectField", "true"
		).put(
			"dateObjectField", "2024-09-25"
		).put(
			"dateTimeObjectField", "2024-09-25 00:00:00.0"
		).put(
			"emailTextObjectField", "test@liferay.com"
		).put(
			"integerObjectField", "12345"
		).put(
			"localizedTextObjectField", "localizedTextObjectFieldValue"
		).put(
			"longIntegerObjectField", "123456789"
		).put(
			"multiselectPicklistObjectField",
			Arrays.asList(
				new ListEntry() {
					{
						key = listTypeEntry1.getKey();
						name = listTypeEntry1.getName(LocaleUtil.US);
					}
				},
				new ListEntry() {
					{
						key = listTypeEntry2.getKey();
						name = listTypeEntry2.getName(LocaleUtil.US);
					}
				})
		).put(
			"picklistObjectField",
			new ListEntry() {
				{
					key = listTypeEntry1.getKey();
					name = listTypeEntry1.getName(LocaleUtil.US);
				}
			}
		).put(
			"textObjectField", "textObjectFieldValue"
		).build();

		group = GroupTestUtil.addGroup();

		guestUser = userLocalService.getGuestUser(
			TestPropsValues.getCompanyId());

		parentObjectEntryValues = LinkedHashMapBuilder.<String, Object>put(
			"dateObjectField",
			() -> {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd");

				return simpleDateFormat.format(RandomTestUtil.nextDate());
			}
		).put(
			"dateTimeObjectField",
			() -> {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd 00:00:00.0");

				return simpleDateFormat.format(RandomTestUtil.nextDate());
			}
		).put(
			"multiselectPicklistObjectField",
			Arrays.asList(
				new ListEntry() {
					{
						key = listTypeEntry1.getKey();
						name = listTypeEntry1.getName(LocaleUtil.US);
					}
				},
				new ListEntry() {
					{
						key = listTypeEntry2.getKey();
						name = listTypeEntry2.getName(LocaleUtil.US);
					}
				})
		).put(
			"picklistObjectField",
			new ListEntry() {
				{
					key = listTypeEntry1.getKey();
					name = listTypeEntry1.getName(LocaleUtil.US);
				}
			}
		).put(
			"systemObjectField", RandomTestUtil.randomString()
		).put(
			"textObjectField", RandomTestUtil.randomString()
		).build();

		user1 = TestPropsValues.getUser();

		dtoConverterContext = new DefaultDTOConverterContext(
			false, Collections.emptyMap(),
			BaseNotificationTypeTest.dtoConverterRegistry, null,
			LocaleUtil.getDefault(), null, user1);

		ListType prefixListType = _listTypeLocalService.getListType(
			user1.getCompanyId(), "dr", ListTypeConstants.CONTACT_PREFIX);
		ListType suffixListType = _listTypeLocalService.getListType(
			user1.getCompanyId(), "ii", ListTypeConstants.CONTACT_SUFFIX);

		role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), ListTypeDefinition.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			ActionKeys.VIEW);

		user2 = userLocalService.addUser(
			user1.getUserId(), user1.getCompanyId(), true, null, null, true,
			null, RandomTestUtil.randomString() + "@liferay.com",
			user1.getLocale(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			prefixListType.getListTypeId(), suffixListType.getListTypeId(),
			true, Month.FEBRUARY.getValue(), 7, 1988, null,
			UserConstants.TYPE_REGULAR, null, null,
			new long[] {role.getRoleId()}, null, true, null);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user2));
		PrincipalThreadLocal.setName(user2.getUserId());
	}

	@Before
	public void setUp() throws Exception {
		childObjectDefinition =
			objectDefinitionLocalService.addCustomObjectDefinition(
				user1.getUserId(), 0, null, false, true, false, true, true,
				false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_SITE,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Arrays.asList(
					new AttachmentObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"attachmentObjectField"
					).objectFieldSettings(
						Arrays.asList(
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.
									NAME_ACCEPTED_FILE_EXTENSIONS
							).value(
								"txt"
							).build(),
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.NAME_FILE_SOURCE
							).value(
								ObjectFieldSettingConstants.VALUE_USER_COMPUTER
							).build(),
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
							).value(
								"100"
							).build())
					).build(),
					new BooleanObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"booleanObjectField"
					).build(),
					new DateObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"dateObjectField"
					).build(),
					new DateTimeObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"dateTimeObjectField"
					).objectFieldSettings(
						Collections.singletonList(
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.NAME_TIME_STORAGE
							).value(
								ObjectFieldSettingConstants.
									VALUE_USE_INPUT_AS_ENTERED
							).build())
					).build(),
					new IntegerObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"integerObjectField"
					).build(),
					new LongIntegerObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"longIntegerObjectField"
					).build(),
					new MultiselectPicklistObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"multiselectPicklistObjectField"
					).listTypeDefinitionId(
						_listTypeDefinition.getListTypeDefinitionId()
					).build(),
					new PicklistObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"picklistObjectField"
					).listTypeDefinitionId(
						_listTypeDefinition.getListTypeDefinitionId()
					).build(),
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"emailTextObjectField"
					).build(),
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).localized(
						true
					).name(
						"localizedTextObjectField"
					).build(),
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"textObjectField"
					).build()),
				Collections.emptyList());

		childObjectDefinition =
			objectDefinitionLocalService.publishCustomObjectDefinition(
				user1.getUserId(),
				childObjectDefinition.getObjectDefinitionId());

		parentObjectDefinition =
			objectDefinitionLocalService.addCustomObjectDefinition(
				user1.getUserId(), 0, null, false, true, false, true, false,
				false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"ParentObjectDefinition", null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				false, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Arrays.asList(
					new DateObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"dateObjectField"
					).build(),
					new DateTimeObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"dateTimeObjectField"
					).objectFieldSettings(
						Collections.singletonList(
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.NAME_TIME_STORAGE
							).value(
								ObjectFieldSettingConstants.
									VALUE_USE_INPUT_AS_ENTERED
							).build())
					).build(),
					new MultiselectPicklistObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"multiselectPicklistObjectField"
					).listTypeDefinitionId(
						_listTypeDefinition.getListTypeDefinitionId()
					).build(),
					new PicklistObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"picklistObjectField"
					).listTypeDefinitionId(
						_listTypeDefinition.getListTypeDefinitionId()
					).build(),
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"systemObjectField"
					).objectFieldSettings(
						Collections.emptyList()
					).system(
						true
					).build(),
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"textObjectField"
					).objectFieldSettings(
						Collections.emptyList()
					).build()),
				Collections.emptyList());

		parentObjectDefinition =
			objectDefinitionLocalService.publishCustomObjectDefinition(
				user1.getUserId(),
				parentObjectDefinition.getObjectDefinitionId());

		objectRelationship =
			objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				parentObjectDefinition.getObjectDefinitionId(),
				childObjectDefinition.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"oneToManyObjectRelationship", false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		_childAuthorTermValues = HashMapBuilder.<String, Object>put(
			getTermName("AUTHOR_EMAIL_ADDRESS"), user2.getEmailAddress()
		).put(
			getTermName("AUTHOR_FIRST_NAME"), user2.getFirstName()
		).put(
			getTermName("AUTHOR_ID"), user2.getUserId()
		).put(
			getTermName("AUTHOR_LAST_NAME"), user2.getLastName()
		).put(
			getTermName("AUTHOR_MIDDLE_NAME"), user2.getMiddleName()
		).put(
			getTermName("AUTHOR_PREFIX"), _getListType("PREFIX", user2)
		).put(
			getTermName("AUTHOR_SUFFIX"), _getListType("SUFFIX", user2)
		).build();
		_generalTermValues = HashMapBuilder.<String, Object>put(
			"[%CURRENT_DATE%]",
			() -> {
				DateFormat dateFormat =
					DateFormatFactoryUtil.getSimpleDateFormat("yyyy-MM-dd");

				return dateFormat.format(new Date());
			}
		).put(
			"[%CURRENT_USER_EMAIL_ADDRESS%]", user2.getEmailAddress()
		).put(
			"[%CURRENT_USER_FIRST_NAME%]", user2.getFirstName()
		).put(
			"[%CURRENT_USER_ID%]", user2.getUserId()
		).put(
			"[%CURRENT_USER_LAST_NAME%]", user2.getLastName()
		).put(
			"[%CURRENT_USER_MIDDLE_NAME%]", user2.getMiddleName()
		).put(
			"[%CURRENT_USER_PREFIX%]", _getListType("PREFIX", user2)
		).put(
			"[%CURRENT_USER_SUFFIX%]", _getListType("SUFFIX", user2)
		).build();
		_parentAuthorTermValues = HashMapBuilder.<String, Object>put(
			getTermName(true, "AUTHOR_EMAIL_ADDRESS"), user2.getEmailAddress()
		).put(
			getTermName(true, "AUTHOR_FIRST_NAME"), user2.getFirstName()
		).put(
			getTermName(true, "AUTHOR_ID"), user2.getUserId()
		).put(
			getTermName(true, "AUTHOR_LAST_NAME"), user2.getLastName()
		).put(
			getTermName(true, "AUTHOR_MIDDLE_NAME"), user2.getMiddleName()
		).put(
			getTermName(true, "AUTHOR_PREFIX"), _getListType("PREFIX", user2)
		).put(
			getTermName(true, "AUTHOR_SUFFIX"), _getListType("SUFFIX", user2)
		).build();

		resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(),
			childObjectDefinition.getResourceName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			ObjectActionKeys.ADD_OBJECT_ENTRY);

		resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(),
			parentObjectDefinition.getResourceName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			ObjectActionKeys.ADD_OBJECT_ENTRY);
	}

	protected ObjectDefinition
			addObjectDefinitionWithNotificationTemplateObjectAction(
				List<NotificationRecipientSetting>
					notificationRecipientSettings,
				String subject, String type)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Arrays.asList(
					new AssigneeObjectFieldBuilder(
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).name(
						"assignee"
					).build(),
					new TextObjectFieldBuilder(
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).name(
						"textObjectField"
					).build()));

		resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), objectDefinition.getResourceName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			ObjectActionKeys.ADD_OBJECT_ENTRY);

		NotificationContext notificationContext =
			NotificationTemplateUtil.createNotificationContext(
				TestPropsValues.getUser(), 0, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				NotificationTemplateConstants.EDITOR_TYPE_RICH_TEXT,
				notificationRecipientSettings, subject, type,
				Collections.emptyList());

		NotificationTemplate notificationTemplate =
			notificationContext.getNotificationTemplate();

		notificationTemplate.setRecipientType(
			NotificationRecipientConstants.TYPE_TERM);

		ObjectActionTestUtil.addObjectAction(
			notificationTemplateLocalService.addNotificationTemplate(
				notificationContext),
			objectDefinition);

		return objectDefinition;
	}

	protected void addViewResourcePermission(
			ObjectDefinition objectDefinition, Role role)
		throws Exception {

		resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), objectDefinition.getClassName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			ActionKeys.VIEW);
	}

	protected void assertNotificationQueueEntrySubject(String subject)
		throws Exception {

		List<NotificationQueueEntry> notificationQueueEntries =
			notificationQueueEntryLocalService.getNotificationQueueEntries(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			notificationQueueEntries.toString(), 1,
			notificationQueueEntries.size());

		NotificationQueueEntry notificationQueueEntry =
			notificationQueueEntries.get(0);

		Assert.assertEquals(subject, notificationQueueEntry.getSubject());

		notificationQueueEntryLocalService.deleteNotificationQueueEntry(
			notificationQueueEntry);
	}

	protected void assertTermValues(
		List<String> expectedTermValues, List<String> actualTermValues) {

		Assert.assertEquals(
			expectedTermValues.toString(), expectedTermValues.size(),
			actualTermValues.size());

		for (int i = 0; i < actualTermValues.size(); i++) {
			Assert.assertEquals(
				expectedTermValues.get(i), actualTermValues.get(i));
		}
	}

	protected void executeNotificationObjectAction(
			long fileEntryId, NotificationTemplate notificationTemplate)
		throws Exception {

		ObjectAction objectAction = objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			childObjectDefinition.getObjectDefinitionId(), true,
			StringPool.BLANK, RandomTestUtil.randomString(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_NOTIFICATION,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
			UnicodePropertiesBuilder.put(
				"notificationTemplateId",
				notificationTemplate.getNotificationTemplateId()
			).build(),
			false);

		ObjectEntry objectEntry = objectEntryManager.addObjectEntry(
			dtoConverterContext, parentObjectDefinition,
			new ObjectEntry() {
				{
					properties = new LinkedHashMap<>(parentObjectEntryValues);
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		objectEntryManager.addObjectEntry(
			dtoConverterContext, childObjectDefinition,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.putAll(
						childObjectEntryValues
					).put(
						getObjectRelationshipObjectField2Name(),
						objectEntry.getId()
					).put(
						"attachmentObjectField", fileEntryId
					).build();
				}
			},
			group.getGroupKey());

		objectActionLocalService.deleteObjectAction(
			objectAction.getObjectActionId());
	}

	protected String getObjectRelationshipObjectField2Name()
		throws PortalException {

		ObjectField objectField = objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());

		return objectField.getName();
	}

	protected String getTermName(boolean parent, String termNameSuffix) {
		String termNamePrefix = childObjectDefinition.getShortName();

		if (parent) {
			termNamePrefix =
				ObjectRelationshipUtil.getNotificationTermNamePrefix(
					parentObjectDefinition, objectRelationship);
		}

		return ObjectDefinitionNotificationTermUtil.getObjectFieldTermName(
			termNamePrefix, termNameSuffix);
	}

	protected String getTermName(String termNameSuffix) {
		return getTermName(false, termNameSuffix);
	}

	protected List<String> getTermNames() {
		return ListUtil.concat(
			ListUtil.fromMapKeys(_childAuthorTermValues),
			ListUtil.fromMapKeys(_generalTermValues),
			ListUtil.fromMapKeys(_parentAuthorTermValues),
			Arrays.asList(
				getTermName("booleanObjectField"),
				getTermName("dateObjectField"),
				getTermName("dateTimeObjectField"),
				getTermName("emailTextObjectField"),
				getTermName("integerObjectField"),
				getTermName("localizedTextObjectField"),
				getTermName("longIntegerObjectField"),
				getTermName("multiselectPicklistObjectField"),
				getTermName("picklistObjectField"),
				getTermName("textObjectField"),
				getTermName(true, "dateObjectField"),
				getTermName(true, "dateTimeObjectField"),
				getTermName(true, "multiselectPicklistObjectField"),
				getTermName(true, "picklistObjectField"),
				getTermName(true, "systemObjectField"),
				getTermName(true, "textObjectField")));
	}

	protected List<String> getTermValues() {
		return TransformUtil.transform(
			ListUtil.concat(
				ListUtil.fromMapValues(_childAuthorTermValues),
				ListUtil.fromMapValues(_generalTermValues),
				ListUtil.fromMapValues(_parentAuthorTermValues),
				ListUtil.fromMapValues(childObjectEntryValues),
				ListUtil.fromMapValues(parentObjectEntryValues)),
			this::parseTermValueToString);
	}

	protected String parseTermValueToString(Object termValue) {
		if (termValue instanceof List) {
			List<?> list = (List<?>)termValue;

			if (list.isEmpty()) {
				return StringPool.BLANK;
			}

			if (list.get(0) instanceof KeyLocalizedLabelPair) {
				List<KeyLocalizedLabelPair> keyLocalizedLabelPairs =
					(List<KeyLocalizedLabelPair>)termValue;

				return StringUtil.merge(
					TransformUtil.transform(
						keyLocalizedLabelPairs,
						keyLocalizedLabelPair -> keyLocalizedLabelPair.getLabel(
							LocaleUtil.US)),
					StringPool.COMMA);
			}

			List<ListEntry> listTypeEntries = (List<ListEntry>)termValue;

			return StringUtil.merge(
				TransformUtil.transform(listTypeEntries, ListEntry::getName),
				StringPool.COMMA_AND_SPACE);
		}
		else if (termValue instanceof ListEntry) {
			ListEntry listEntry = (ListEntry)termValue;

			return listEntry.getName();
		}

		return String.valueOf(termValue);
	}

	@DeleteAfterTestRun
	protected static ObjectDefinition childObjectDefinition;

	protected static LinkedHashMap<String, Object> childObjectEntryValues;
	protected static DTOConverterContext dtoConverterContext;

	@Inject
	protected static DTOConverterRegistry dtoConverterRegistry;

	protected static Group group;
	protected static User guestUser;

	@Inject
	protected static ObjectDefinitionLocalService objectDefinitionLocalService;

	@Inject
	protected static ObjectFieldLocalService objectFieldLocalService;

	@DeleteAfterTestRun
	protected static ObjectRelationship objectRelationship;

	@Inject
	protected static ObjectRelationshipLocalService
		objectRelationshipLocalService;

	@DeleteAfterTestRun
	protected static ObjectDefinition parentObjectDefinition;

	protected static LinkedHashMap<String, Object> parentObjectEntryValues;

	@Inject
	protected static ResourcePermissionLocalService
		resourcePermissionLocalService;

	protected static Role role;
	protected static User user1;
	protected static User user2;

	@Inject
	protected static UserLocalService userLocalService;

	@DeleteAfterTestRun
	protected NotificationQueueEntry notificationQueueEntry;

	@Inject
	protected NotificationQueueEntryLocalService
		notificationQueueEntryLocalService;

	@Inject
	protected NotificationRecipientLocalService
		notificationRecipientLocalService;

	@Inject
	protected NotificationTemplateLocalService notificationTemplateLocalService;

	@Inject
	protected ObjectActionLocalService objectActionLocalService;

	@Inject(
		filter = "object.entry.manager.storage.type=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT
	)
	protected ObjectEntryManager objectEntryManager;

	private String _getListType(String type, User user) throws Exception {
		Contact contact = user.fetchContact();

		if (contact == null) {
			return StringPool.BLANK;
		}

		long listTypeId = contact.getPrefixListTypeId();

		if (type.equals("SUFFIX")) {
			listTypeId = contact.getSuffixListTypeId();
		}

		if (listTypeId == 0) {
			return StringPool.BLANK;
		}

		ListType listType = _listTypeLocalService.getListType(listTypeId);

		return listType.getName();
	}

	private static ListTypeDefinition _listTypeDefinition;

	@Inject
	private static ListTypeDefinitionLocalService
		_listTypeDefinitionLocalService;

	@Inject
	private static ListTypeEntryLocalService _listTypeEntryLocalService;

	@Inject
	private static ListTypeLocalService _listTypeLocalService;

	private Map<String, Object> _childAuthorTermValues;
	private Map<String, Object> _generalTermValues;

	@Inject
	private NotificationRecipientSettingLocalService
		_notificationRecipientSettingLocalService;

	private Map<String, Object> _parentAuthorTermValues;

}