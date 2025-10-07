/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.internal.type.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryOrganizationRelLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.account.service.AccountRoleLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.engine.CommerceOrderEngine;
import com.liferay.commerce.payment.engine.CommerceSubscriptionEngine;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceSubscriptionEntryLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.notification.constants.NotificationConstants;
import com.liferay.notification.constants.NotificationPortletKeys;
import com.liferay.notification.constants.NotificationQueueEntryConstants;
import com.liferay.notification.constants.NotificationRecipientConstants;
import com.liferay.notification.constants.NotificationRecipientSettingConstants;
import com.liferay.notification.constants.NotificationTemplateConstants;
import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.model.NotificationQueueEntry;
import com.liferay.notification.model.NotificationQueueEntryAttachment;
import com.liferay.notification.model.NotificationRecipientSetting;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.NotificationQueueEntryAttachmentLocalService;
import com.liferay.notification.service.NotificationRecipientLocalServiceUtil;
import com.liferay.notification.test.util.NotificationTemplateUtil;
import com.liferay.notification.util.NotificationRecipientSettingUtil;
import com.liferay.object.action.trigger.ObjectActionTriggerRegistry;
import com.liferay.object.action.util.ObjectActionThreadLocal;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.DefaultObjectEntryManager;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectEntryFolderTestUtil;
import com.liferay.object.util.HttpServletRequestThreadLocal;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.template.TemplateContextContributor;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.mail.MailServiceTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.subscription.service.SubscriptionLocalService;

import jakarta.servlet.http.HttpServletRequest;

import java.text.DateFormat;

import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Feliphe Marinho
 */
@RunWith(Arquillian.class)
public class EmailNotificationTypeTest extends BaseNotificationTypeTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), SynchronousMailTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseNotificationTypeTest.setUpClass();

		_freeMarkerEngineConfiguration = _configurationAdmin.getConfiguration(
			"com.liferay.portal.template.freemarker.configuration." +
				"FreeMarkerEngineConfiguration",
			StringPool.QUESTION);

		ConfigurationTestUtil.saveConfiguration(
			_freeMarkerEngineConfiguration,
			HashMapDictionaryBuilder.<String, Object>put(
				"restrictedVariables", true
			).build());

		Bundle bundle = FrameworkUtil.getBundle(
			EmailNotificationTypeTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			TemplateContextContributor.class,
			new TestTemplateContextContributor(),
			HashMapDictionaryBuilder.put(
				"type", TemplateContextContributor.TYPE_GLOBAL
			).build());

		_pushServiceContext();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(
			_freeMarkerEngineConfiguration);

		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}

		ServiceContextThreadLocal.popServiceContext();
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		MailServiceTestUtil.clearMessages();
	}

	@After
	public void tearDown() {
		HttpServletRequestThreadLocal.setHttpServletRequest(null);
	}

	@Test
	public void testFreeMarkerNotification() throws Exception {

		// Notification triggered by admin user

		String body = _read("notification_template_body_object_entry.ftl");

		ObjectAction objectAction = _addNotificationTemplateObjectAction(
			body, NotificationTemplateConstants.EDITOR_TYPE_FREEMARKER,
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE,
			childObjectDefinition);

		ObjectEntry objectEntry = objectEntryManager.addObjectEntry(
			dtoConverterContext, childObjectDefinition,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.putAll(
						childObjectEntryValues
					).build();
				}
			},
			group.getGroupKey());

		objectEntryManager.updateObjectEntry(
			TestPropsValues.getCompanyId(), dtoConverterContext,
			objectEntry.getExternalReferenceCode(), childObjectDefinition,
			objectEntry, group.getGroupKey());

		_assertNotificationQueueEntryBody(
			_getObjectEntryNotificationQueueEntryBody(objectEntry));

		_objectActionLocalService.deleteObjectAction(objectAction);

		// Notification triggered by guest user

		Role guestRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.GUEST);

		resourcePermissionLocalService.addResourcePermission(
			guestUser.getCompanyId(), childObjectDefinition.getResourceName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(guestUser.getCompanyId()), guestRole.getRoleId(),
			ObjectActionKeys.ADD_OBJECT_ENTRY);
		resourcePermissionLocalService.addResourcePermission(
			guestUser.getCompanyId(), childObjectDefinition.getClassName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(guestUser.getCompanyId()), guestRole.getRoleId(),
			ActionKeys.VIEW);
		resourcePermissionLocalService.addResourcePermission(
			guestUser.getCompanyId(), ListTypeDefinition.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(guestUser.getCompanyId()), guestRole.getRoleId(),
			ActionKeys.VIEW);

		objectAction = _addNotificationTemplateObjectAction(
			body, NotificationTemplateConstants.EDITOR_TYPE_FREEMARKER,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
			childObjectDefinition);

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		String originalName = PrincipalThreadLocal.getName();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(guestUser));
			PrincipalThreadLocal.setName(guestUser.getUserId());

			objectEntry = objectEntryManager.addObjectEntry(
				dtoConverterContext, childObjectDefinition,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.putAll(
							childObjectEntryValues
						).build();
					}
				},
				group.getGroupKey());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
			PrincipalThreadLocal.setName(originalName);
		}

		_assertNotificationQueueEntryBody(
			_getObjectEntryNotificationQueueEntryBody(objectEntry));

		_objectActionLocalService.deleteObjectAction(objectAction);
	}

	@Test
	public void testFreeMarkerNotificationTemplateContextContributor()
		throws Exception {

		NotificationTemplate notificationTemplate = _addNotificationTemplate(
			StringBundler.concat(
				"${testTemplateContextContributorKey};\n${.data_model[\"",
				"ObjectRelationship#C_ParentObjectDefinition#oneToMany",
				"ObjectRelationship_textObjectField\"].getData()}\n",
				"${ObjectField_r_oneToManyObjectRelationship_c_",
				"parentObjectDefinitionId.getData()}"),
			NotificationTemplateConstants.EDITOR_TYPE_FREEMARKER,
			Collections.singletonMap(
				LocaleUtil.US, "[%CURRENT_USER_FIRST_NAME%]"),
			false,
			Collections.singletonMap(LocaleUtil.US, user1.getEmailAddress()));

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

		ObjectEntry parentObjectEntry = objectEntryManager.addObjectEntry(
			dtoConverterContext, parentObjectDefinition,
			new ObjectEntry() {
				{
					properties = new LinkedHashMap<>(parentObjectEntryValues);
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		long parentObjectEntryId = parentObjectEntry.getId();

		ObjectEntry childObjectEntry = objectEntryManager.addObjectEntry(
			dtoConverterContext, childObjectDefinition,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.putAll(
						childObjectEntryValues
					).put(
						getObjectRelationshipObjectField2Name(),
						parentObjectEntryId
					).build();
				}
			},
			group.getGroupKey());

		_assertNotificationQueueEntryBody(
			StringBundler.concat(
				"testTemplateContextContributorValue;\n",
				parentObjectEntryValues.get("textObjectField"), "\n",
				parentObjectEntryId));

		objectActionLocalService.deleteObjectAction(
			objectAction.getObjectActionId());

		_objectEntryLocalService.deleteObjectEntry(childObjectEntry.getId());

		_objectEntryLocalService.deleteObjectEntry(parentObjectEntryId);
	}

	@Test
	public void testFreeMarkerNotificationWithCommerceOrder() throws Exception {
		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(
				TestPropsValues.getCompanyId());

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			TestPropsValues.getGroupId(), commerceCurrency.getCode());

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			TestPropsValues.getUserId(), commerceChannel.getGroupId(),
			commerceCurrency);

		commerceOrder = CommerceTestUtil.addCheckoutDetailsToCommerceOrder(
			commerceOrder, TestPropsValues.getUserId(), true, true);

		ObjectDefinition commerceOrderObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				TestPropsValues.getCompanyId(), CommerceOrder.class.getName());

		ObjectAction objectAction = _addNotificationTemplateObjectAction(
			_read("notification_template_body_commerce_order.ftl"),
			NotificationTemplateConstants.EDITOR_TYPE_FREEMARKER,
			DestinationNames.COMMERCE_PAYMENT_STATUS,
			commerceOrderObjectDefinition);

		_commerceOrderLocalService.updatePaymentStatus(
			TestPropsValues.getUserId(), commerceOrder.getCommerceOrderId(),
			CommerceOrderPaymentConstants.STATUS_PENDING);

		_assertNotificationQueueEntryBody(
			_getCommerceOrderNotificationQueueEntryBody(commerceOrder));

		_objectActionLocalService.deleteObjectAction(objectAction);

		_commerceOrderLocalService.deleteCommerceOrder(
			commerceOrder.getCommerceOrderId());

		_accountEntryLocalService.deleteAccountEntry(
			_accountEntryLocalService.fetchPersonAccountEntry(
				TestPropsValues.getUserId()));
	}

	@Test
	public void testRichTextNotificationTemplateWithDifferentUserLocale()
		throws Exception {

		ObjectAction objectAction = _addNotificationTemplateObjectAction(
			StringBundler.concat(
				_getTermName(childObjectDefinition, "createDate"),
				StringPool.COMMA,
				_getTermName(childObjectDefinition, "modifiedDate")),
			NotificationTemplateConstants.EDITOR_TYPE_RICH_TEXT,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
			childObjectDefinition);

		String originalName = PrincipalThreadLocal.getName();

		try {
			User user = UserTestUtil.addUser(
				TestPropsValues.getGroupId(), LocaleUtil.FRENCH);

			PrincipalThreadLocal.setName(user.getUserId());

			ObjectEntry objectEntry = objectEntryManager.addObjectEntry(
				dtoConverterContext, childObjectDefinition,
				new ObjectEntry() {
					{
						properties = HashMapBuilder.putAll(
							childObjectEntryValues
						).build();
					}
				},
				group.getGroupKey());

			DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
				"EEE MMM dd HH:mm:ss zzz yyyy", LocaleUtil.FRENCH);

			_assertNotificationQueueEntryBody(
				StringBundler.concat(
					dateFormat.format(objectEntry.getDateCreated()),
					StringPool.COMMA,
					dateFormat.format(objectEntry.getDateModified())));

			_objectEntryLocalService.deleteObjectEntry(objectEntry.getId());
		}
		finally {
			PrincipalThreadLocal.setName(originalName);

			_objectActionLocalService.deleteObjectAction(objectAction);
		}
	}

	@Test
	public void testSendNotification() throws Exception {

		// Multiples emails for each main recipient with a "," separator

		_testSendNotification(
			2,
			ListUtil.sort(
				Arrays.asList(
					user1.getEmailAddress(), user2.getEmailAddress())),
			true,
			StringBundler.concat(
				user1.getEmailAddress(), StringPool.COMMA,
				user2.getEmailAddress()));

		// Multiples emails for each main recipient with a ", " separator

		_testSendNotification(
			2,
			ListUtil.sort(
				Arrays.asList(
					user1.getEmailAddress(), user2.getEmailAddress())),
			true,
			StringBundler.concat(
				user1.getEmailAddress(), StringPool.COMMA_AND_SPACE,
				user2.getEmailAddress()));

		// Multiples emails for each main recipient with a ";" separator

		_testSendNotification(
			2,
			ListUtil.sort(
				Arrays.asList(
					user1.getEmailAddress(), user2.getEmailAddress())),
			true,
			StringBundler.concat(
				user1.getEmailAddress(), StringPool.SEMICOLON,
				user2.getEmailAddress()));

		// Multiples emails for each main recipient and terms with a ","
		// separator

		_testSendNotification(
			2,
			ListUtil.sort(
				Arrays.asList(
					user2.getEmailAddress(),
					GetterUtil.getString(
						childObjectEntryValues.get("emailTextObjectField")))),
			true,
			"[%CURRENT_USER_EMAIL_ADDRESS%]," +
				getTermName("emailTextObjectField"));

		// Multiples emails for each main recipient and terms with a ", "
		// separator

		_testSendNotification(
			2,
			ListUtil.sort(
				Arrays.asList(
					user2.getEmailAddress(),
					GetterUtil.getString(
						childObjectEntryValues.get("emailTextObjectField")))),
			true,
			"[%CURRENT_USER_EMAIL_ADDRESS%], " +
				getTermName("emailTextObjectField"));

		// Multiples emails for each main recipient and terms with a ";"
		// separator

		_testSendNotification(
			2,
			ListUtil.sort(
				Arrays.asList(
					user2.getEmailAddress(),
					GetterUtil.getString(
						childObjectEntryValues.get("emailTextObjectField")))),
			true,
			"[%CURRENT_USER_EMAIL_ADDRESS%];" +
				getTermName("emailTextObjectField"));

		// No email sent to an inactive user

		User user = UserTestUtil.addUser();

		_userLocalService.updateStatus(
			user.getUserId(), WorkflowConstants.STATUS_INACTIVE,
			ServiceContextThreadLocal.getServiceContext());

		_testSendNotification(
			0, Collections.emptyList(), true, user.getEmailAddress());

		_userLocalService.deleteUser(user.getUserId());

		// One email including all main recipients

		_testSendNotification(
			1,
			ListUtil.sort(
				Arrays.asList(
					StringBundler.concat(
						user1.getEmailAddress(), StringPool.COMMA,
						user2.getEmailAddress()))),
			false,
			StringBundler.concat(
				user1.getEmailAddress(), StringPool.COMMA,
				user2.getEmailAddress()));
	}

	@Test
	public void testSendNotificationToAccountRoles() throws Exception {
		AccountEntry accountEntry1 = _addAccountEntry();

		AccountRole accountRole1 = _addAccountRole(
			accountEntry1.getAccountEntryId());

		AccountEntry accountEntry2 = _addAccountEntry();

		AccountRole accountRole2 = _addAccountRole(
			accountEntry2.getAccountEntryId());
		AccountRole accountRole3 = _addAccountRole(
			accountEntry2.getAccountEntryId());

		AccountRole accountRole4 = _addAccountRole(
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT);

		Role organizationRole1 = _addRole(
			RoleConstants.TYPE_ORGANIZATION, TestPropsValues.getUser());
		Role organizationRole2 = _addRole(
			RoleConstants.TYPE_ORGANIZATION, TestPropsValues.getUser());

		NotificationTemplate notificationTemplate1 =
			notificationTemplateLocalService.addNotificationTemplate(
				NotificationTemplateUtil.createNotificationContext(
					TestPropsValues.getUser(), 0, RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					NotificationTemplateConstants.EDITOR_TYPE_RICH_TEXT,
					Arrays.asList(
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.NAME_BCC,
								accountRole3.getRoleName()),
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.NAME_BCC,
								organizationRole2.getName()),
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.
									NAME_BCC_TYPE,
								NotificationRecipientConstants.TYPE_ROLE),
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.NAME_CC,
								"[%CURRENT_USER_EMAIL_ADDRESS%]," +
									"cc@liferay.com"),
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.NAME_FROM,
								"[%CURRENT_USER_EMAIL_ADDRESS%]"),
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.
									NAME_FROM_NAME,
								Collections.singletonMap(
									LocaleUtil.US,
									"[%CURRENT_USER_FIRST_NAME%]")),
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.
									NAME_SINGLE_RECIPIENT,
								Boolean.FALSE.toString()),
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.NAME_TO,
								accountRole1.getRoleName()),
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.NAME_TO,
								accountRole2.getRoleName()),
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.NAME_TO,
								accountRole4.getRoleName()),
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.NAME_TO,
								organizationRole1.getName()),
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.
									NAME_TO_TYPE,
								NotificationRecipientConstants.TYPE_ROLE)),
					RandomTestUtil.randomString(),
					NotificationConstants.TYPE_EMAIL, Collections.emptyList()));

		ObjectDefinition objectDefinition =
			_addAndPublishCustomObjectDefinition(false);

		ObjectAction objectAction1 = _addObjectAction(
			objectDefinition.getObjectDefinitionId(),
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE,
			notificationTemplate1.getNotificationTemplateId());

		_testSendNotification(
			null, null, user2, 0, null,
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE, objectDefinition);

		User user1 = UserTestUtil.addUser();

		_accountRoleLocalService.associateUser(
			accountEntry1.getAccountEntryId(), accountRole1.getAccountRoleId(),
			user1.getUserId());
		_accountRoleLocalService.associateUser(
			accountEntry2.getAccountEntryId(), accountRole2.getAccountRoleId(),
			user1.getUserId());

		User user2 = UserTestUtil.addUser();

		_accountRoleLocalService.associateUser(
			accountEntry2.getAccountEntryId(), accountRole3.getAccountRoleId(),
			user2.getUserId());

		User user3 = UserTestUtil.addUser();

		_accountRoleLocalService.associateUser(
			accountEntry2.getAccountEntryId(), accountRole4.getAccountRoleId(),
			user3.getUserId());

		Organization organization1 = _organizationLocalService.addOrganization(
			TestPropsValues.getUserId(),
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		_userGroupRoleLocalService.addUserGroupRole(
			user3.getUserId(), organization1.getGroupId(),
			organizationRole1.getRoleId());

		User user4 = UserTestUtil.addUser();

		_userGroupRoleLocalService.addUserGroupRole(
			user4.getUserId(), organization1.getGroupId(),
			organizationRole1.getRoleId());

		User user5 = UserTestUtil.addUser();

		Organization organization2 = _organizationLocalService.addOrganization(
			TestPropsValues.getUserId(),
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		Organization childOrganization =
			_organizationLocalService.addOrganization(
				TestPropsValues.getUserId(), organization2.getOrganizationId(),
				RandomTestUtil.randomString(), false);

		_userGroupRoleLocalService.addUserGroupRole(
			user5.getUserId(), childOrganization.getGroupId(),
			organizationRole2.getRoleId());

		// Send email with an object definition not restricted by account entry

		_testSendNotification(
			null,
			StringUtil.merge(
				ListUtil.fromArray(
					user2.getEmailAddress(), user5.getEmailAddress())),
			BaseNotificationTypeTest.user2, 1,
			StringUtil.merge(
				ListUtil.fromArray(
					user1.getEmailAddress(), user3.getEmailAddress(),
					user4.getEmailAddress())),
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE, objectDefinition);

		// Send email with an object definition restricted by account entry

		ObjectDefinition accountEntryRestrictedObjectDefinition =
			_addAndPublishCustomObjectDefinition(true);

		ObjectAction objectAction2 = _addObjectAction(
			accountEntryRestrictedObjectDefinition.getObjectDefinitionId(),
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE,
			notificationTemplate1.getNotificationTemplateId());

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			role.getCompanyId(), AccountEntry.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(role.getCompanyId()), role.getRoleId(),
			new String[] {ActionKeys.VIEW});

		_userLocalService.addRoleUser(
			role.getRoleId(), BaseNotificationTypeTest.user2);

		_testSendNotification(
			accountEntry1, StringPool.BLANK, BaseNotificationTypeTest.user2, 1,
			user1.getEmailAddress(),
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE,
			accountEntryRestrictedObjectDefinition);
		_testSendNotification(
			accountEntry2, user2.getEmailAddress(),
			BaseNotificationTypeTest.user2, 1,
			StringUtil.merge(
				ListUtil.fromArray(
					user1.getEmailAddress(), user3.getEmailAddress())),
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE,
			accountEntryRestrictedObjectDefinition);

		AccountEntry accountEntry3 = _addAccountEntry();

		_accountRoleLocalService.associateUser(
			accountEntry3.getAccountEntryId(), accountRole4.getAccountRoleId(),
			user2.getUserId());

		// Send email with an object definition not restricted by account entry

		_testSendNotification(
			null,
			StringUtil.merge(
				ListUtil.fromArray(
					user2.getEmailAddress(), user5.getEmailAddress())),
			BaseNotificationTypeTest.user2, 1,
			StringUtil.merge(
				ListUtil.fromArray(
					user1.getEmailAddress(), user2.getEmailAddress(),
					user3.getEmailAddress(), user4.getEmailAddress())),
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE, objectDefinition);

		// Send email with an object definition restricted by account entry

		_userGroupRoleLocalService.addUserGroupRole(
			user4.getUserId(), organization1.getGroupId(),
			organizationRole2.getRoleId());

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			accountEntry3.getAccountEntryId(),
			childOrganization.getOrganizationId());

		User user6 = UserTestUtil.addUser();

		_userGroupRoleLocalService.addUserGroupRole(
			user6.getUserId(), organization2.getGroupId(),
			organizationRole2.getRoleId());

		_testSendNotification(
			accountEntry3,
			StringUtil.merge(
				ListUtil.fromArray(
					user5.getEmailAddress(), user6.getEmailAddress())),
			BaseNotificationTypeTest.user2, 1, user2.getEmailAddress(),
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE,
			accountEntryRestrictedObjectDefinition);

		_accountEntryOrganizationRelLocalService.
			deleteAccountEntryOrganizationRel(
				accountEntry3.getAccountEntryId(),
				childOrganization.getOrganizationId());

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			accountEntry3.getAccountEntryId(),
			organization2.getOrganizationId());

		_testSendNotification(
			accountEntry3, user6.getEmailAddress(),
			BaseNotificationTypeTest.user2, 1, user2.getEmailAddress(),
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE,
			accountEntryRestrictedObjectDefinition);

		_accountEntryUserRelLocalService.addAccountEntryUserRels(
			accountEntry1.getAccountEntryId(),
			new long[] {user1.getUserId(), user2.getUserId()});
		_accountEntryUserRelLocalService.addAccountEntryUserRels(
			accountEntry2.getAccountEntryId(), new long[] {user3.getUserId()});
		_organizationLocalService.addUserOrganization(
			user4.getUserId(), organization1.getOrganizationId());
		_organizationLocalService.addUserOrganization(
			user5.getUserId(), organization2.getOrganizationId());
		_organizationLocalService.addUserOrganization(
			user6.getUserId(), childOrganization.getOrganizationId());

		NotificationTemplate notificationTemplate2 =
			notificationTemplateLocalService.addNotificationTemplate(
				NotificationTemplateUtil.createNotificationContext(
					TestPropsValues.getUser(), 0, RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					NotificationTemplateConstants.EDITOR_TYPE_RICH_TEXT,
					Arrays.asList(
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.NAME_BCC,
								AccountRoleConstants.
									REQUIRED_ROLE_NAME_ACCOUNT_MEMBER),
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.
									NAME_BCC_TYPE,
								NotificationRecipientConstants.TYPE_ROLE),
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.NAME_CC,
								"[%CURRENT_USER_EMAIL_ADDRESS%]," +
									"cc@liferay.com"),
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.NAME_FROM,
								"[%CURRENT_USER_EMAIL_ADDRESS%]"),
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.
									NAME_FROM_NAME,
								Collections.singletonMap(
									LocaleUtil.US,
									"[%CURRENT_USER_FIRST_NAME%]")),
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.
									NAME_SINGLE_RECIPIENT,
								Boolean.FALSE.toString()),
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.NAME_TO,
								RoleConstants.ORGANIZATION_USER),
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.
									NAME_TO_TYPE,
								NotificationRecipientConstants.TYPE_ROLE)),
					RandomTestUtil.randomString(),
					NotificationConstants.TYPE_EMAIL, Collections.emptyList()));

		_updateObjectAction(
			notificationTemplate2.getNotificationTemplateId(), objectAction1);

		// Send email with an object definition not restricted by account entry

		_testSendNotification(
			null,
			StringUtil.merge(
				ListUtil.fromArray(
					user1.getEmailAddress(), user2.getEmailAddress(),
					user3.getEmailAddress())),
			BaseNotificationTypeTest.user2, 1,
			StringUtil.merge(
				ListUtil.fromArray(
					user4.getEmailAddress(), user5.getEmailAddress(),
					user6.getEmailAddress())),
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE, objectDefinition);

		// Send email with an object definition restricted by account entry

		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			accountEntry1.getAccountEntryId(),
			childOrganization.getOrganizationId());
		_accountEntryOrganizationRelLocalService.addAccountEntryOrganizationRel(
			accountEntry2.getAccountEntryId(),
			organization1.getOrganizationId());

		_updateObjectAction(
			notificationTemplate2.getNotificationTemplateId(), objectAction2);

		_testSendNotification(
			accountEntry1,
			StringUtil.merge(
				ListUtil.fromArray(
					user1.getEmailAddress(), user2.getEmailAddress())),
			BaseNotificationTypeTest.user2, 1,
			StringUtil.merge(
				ListUtil.fromArray(
					user5.getEmailAddress(), user6.getEmailAddress())),
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE,
			accountEntryRestrictedObjectDefinition);
		_testSendNotification(
			accountEntry2, user3.getEmailAddress(),
			BaseNotificationTypeTest.user2, 1, user4.getEmailAddress(),
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE,
			accountEntryRestrictedObjectDefinition);
		_testSendNotification(
			accountEntry3, null, BaseNotificationTypeTest.user2, 1,
			user5.getEmailAddress(),
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE,
			accountEntryRestrictedObjectDefinition);

		objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
		objectDefinitionLocalService.deleteObjectDefinition(
			accountEntryRestrictedObjectDefinition);
	}

	@FeatureFlag("LPD-6233")
	@Test
	public void testSendNotificationToAssignee() throws Exception {
		ObjectDefinition objectDefinition =
			addObjectDefinitionWithNotificationTemplateObjectAction(
				Arrays.asList(
					NotificationRecipientSettingUtil.
						createNotificationRecipientSetting(
							NotificationRecipientSettingConstants.NAME_FROM,
							"test@liferay.com"),
					NotificationRecipientSettingUtil.
						createNotificationRecipientSetting(
							NotificationRecipientSettingConstants.
								NAME_FROM_NAME,
							Collections.singletonMap(LocaleUtil.US, "Test")),
					NotificationRecipientSettingUtil.
						createNotificationRecipientSetting(
							NotificationRecipientSettingConstants.
								NAME_SINGLE_RECIPIENT,
							Boolean.FALSE.toString()),
					NotificationRecipientSettingUtil.
						createNotificationRecipientSetting(
							NotificationRecipientSettingConstants.NAME_TO,
							"[%OBJECT_ENTRY_ASSIGNEE%]"),
					NotificationRecipientSettingUtil.
						createNotificationRecipientSetting(
							NotificationRecipientSettingConstants.NAME_TO_TYPE,
							NotificationRecipientConstants.TYPE_TERM)),
				"[%OBJECT_ENTRY_ASSIGNEE%]", NotificationConstants.TYPE_EMAIL);

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		addViewResourcePermission(objectDefinition, role);

		User user1 = UserTestUtil.addUser();

		_roleLocalService.addUserRole(user1.getUserId(), role.getRoleId());

		objectEntryManager.addObjectEntry(
			dtoConverterContext, objectDefinition,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"assignee",
						HashMapBuilder.put(
							"externalReferenceCode",
							user1.getExternalReferenceCode()
						).put(
							"type", "User"
						).build()
					).build();
				}
			},
			null);

		_assertMailMessage(new String[] {user1.getEmailAddress()});
		assertNotificationQueueEntrySubject(user1.getFullName());

		User user2 = UserTestUtil.addUser();

		_roleLocalService.addUserRole(user2.getUserId(), role.getRoleId());

		objectEntryManager.addObjectEntry(
			dtoConverterContext, objectDefinition,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"assignee",
						HashMapBuilder.put(
							"externalReferenceCode",
							role.getExternalReferenceCode()
						).put(
							"type", "Role"
						).build()
					).build();
				}
			},
			null);

		_assertMailMessage(
			new String[] {user1.getEmailAddress(), user2.getEmailAddress()});
		assertNotificationQueueEntrySubject(role.getName());

		objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Test
	public void testSendNotificationToCurrentUser() throws Exception {
		NotificationTemplate notificationTemplate =
			notificationTemplateLocalService.addNotificationTemplate(
				NotificationTemplateUtil.createNotificationContext(
					TestPropsValues.getUser(), 0, RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					NotificationTemplateConstants.EDITOR_TYPE_RICH_TEXT,
					Arrays.asList(
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.NAME_FROM,
								"test@liferay.com"),
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.
									NAME_FROM_NAME,
								Collections.singletonMap(
									LocaleUtil.US, "Test")),
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.
									NAME_SINGLE_RECIPIENT,
								Boolean.FALSE.toString()),
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.NAME_TO,
								"[%CURRENT_USER_EMAIL_ADDRESS%]")),
					RandomTestUtil.randomString(),
					NotificationConstants.TYPE_EMAIL, Collections.emptyList()));

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, false, true,
				false, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"textObjectField"
					).build()),
				Collections.emptyList());

		_addObjectAction(
			objectDefinition.getObjectDefinitionId(),
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
			notificationTemplate.getNotificationTemplateId());
		_addObjectAction(
			objectDefinition.getObjectDefinitionId(),
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE,
			notificationTemplate.getNotificationTemplateId());

		objectDefinition =
			objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		String originalName = PrincipalThreadLocal.getName();

		_user = UserTestUtil.addUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		PrincipalThreadLocal.setName(_user.getUserId());

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		String name = objectDefinition.getClassName();

		String[] actionIds = {ObjectActionKeys.ADD_OBJECT_ENTRY};

		if (ArrayUtil.contains(actionIds, ObjectActionKeys.ADD_OBJECT_ENTRY)) {
			name = objectDefinition.getResourceName();
		}

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), name,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			actionIds);

		_userLocalService.addRoleUser(role.getRoleId(), _user);

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), objectDefinition.getClassName(),
			ResourceConstants.SCOPE_COMPANY, "0", role.getRoleId(),
			ActionKeys.UPDATE);

		_roleLocalService.addUserRole(_user.getUserId(), role.getRoleId());

		// Notification sent on after add

		ObjectEntry objectEntry = objectEntryManager.addObjectEntry(
			dtoConverterContext, objectDefinition,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"textObjectField", RandomTestUtil.randomString()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_testSendNotificationToCurrentUser();

		// Notification sent on after update

		objectEntryManager.updateObjectEntry(
			_user.getCompanyId(), dtoConverterContext,
			objectEntry.getExternalReferenceCode(), objectDefinition,
			objectEntry, ObjectDefinitionConstants.SCOPE_COMPANY);

		_testSendNotificationToCurrentUser();

		_objectEntryLocalService.deleteObjectEntry(objectEntry.getId());

		objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		PermissionThreadLocal.setPermissionChecker(originalPermissionChecker);

		PrincipalThreadLocal.setName(originalName);
	}

	@Test
	public void testSendNotificationToInheritedRoleUsers() throws Exception {
		Role role = _addRole(RoleConstants.TYPE_REGULAR, user1);

		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		_roleLocalService.addGroupRole(userGroup.getGroupId(), role);

		User user = UserTestUtil.addUser();

		_userGroupLocalService.addUserUserGroup(
			user.getUserId(), userGroup.getUserGroupId());

		ObjectDefinition objectDefinition =
			_addObjectDefinitionWithNotificationTemplateObjectAction(
				NotificationRecipientConstants.TYPE_ROLE, role.getName());

		resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), objectDefinition.getClassName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			ActionKeys.VIEW);

		_testSendNotification(
			null, null, user2, 1, user.getEmailAddress(),
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD, objectDefinition);

		objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		_roleLocalService.deleteRole(role.getRoleId());
	}

	@Test
	public void testSendNotificationToOwnerRole() throws Exception {
		ObjectDefinition objectDefinition =
			_addObjectDefinitionWithNotificationTemplateObjectAction(
				NotificationRecipientConstants.TYPE_ROLE, RoleConstants.OWNER);

		try {
			_testSendNotification(
				null, null, user2, 1, user2.getEmailAddress(),
				ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
				objectDefinition);

			_setUser(user1);

			_testSendNotification(
				null, null, user1, 1, user1.getEmailAddress(),
				ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
				objectDefinition);
		}
		finally {
			_setUser(user2);

			objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}
	}

	@Test
	public void testSendNotificationToRegularRoles() throws Exception {
		Role role1 = _addRole(RoleConstants.TYPE_REGULAR, user1);

		Role role2 = _addRole(RoleConstants.TYPE_REGULAR, user2);

		ObjectDefinition objectDefinition =
			_addObjectDefinitionWithNotificationTemplateObjectAction(
				NotificationRecipientConstants.TYPE_ROLE, role2.getName());

		try {
			_setUser(user1);

			_testSendNotification(
				null, StringPool.BLANK, user1, 0, null,
				ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
				objectDefinition);

			_roleLocalService.addUserRole(user1.getUserId(), role1.getRoleId());
			_roleLocalService.addUserRole(user2.getUserId(), role2.getRoleId());

			_testSendNotification(
				null, StringPool.BLANK, user1, 0, null,
				ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
				objectDefinition);

			resourcePermissionLocalService.addResourcePermission(
				TestPropsValues.getCompanyId(), objectDefinition.getClassName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(TestPropsValues.getCompanyId()),
				role1.getRoleId(), ActionKeys.VIEW);
			resourcePermissionLocalService.addResourcePermission(
				TestPropsValues.getCompanyId(), objectDefinition.getClassName(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(TestPropsValues.getCompanyId()),
				role2.getRoleId(), ActionKeys.VIEW);

			_testSendNotification(
				null, null, user1, 1, user2.getEmailAddress(),
				ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
				objectDefinition);

			objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}
		finally {
			_setUser(user2);
		}
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testSendNotificationToSubscribers() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				false, true, true,
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"firstName"
					).build()),
				ObjectDefinitionConstants.SCOPE_SITE);

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectDefinition.getObjectDefinitionId(), "firstName");

		objectDefinition =
			_objectDefinitionLocalService.updateTitleObjectFieldId(
				objectDefinition.getObjectDefinitionId(),
				objectField.getObjectFieldId());

		ObjectEntryFolder objectEntryFolder1 =
			ObjectEntryFolderTestUtil.addObjectEntryFolder(group.getGroupId());

		User user = UserTestUtil.addUser();

		_objectEntryFolderLocalService.subscribeObjectEntryFolder(
			user.getUserId(), group.getGroupId(),
			objectEntryFolder1.getObjectEntryFolderId());

		_setUser(user1);

		DefaultObjectEntryManager defaultObjectEntryManager =
			(DefaultObjectEntryManager)objectEntryManager;

		defaultObjectEntryManager.addObjectEntry(
			dtoConverterContext, objectDefinition,
			new ObjectEntry() {
				{
					objectEntryFolderExternalReferenceCode =
						objectEntryFolder1.getExternalReferenceCode();
					properties = HashMapBuilder.<String, Object>put(
						"firstName", "Charlie"
					).build();
				}
			},
			group.getGroupKey());

		_assertMailMessage(new String[] {user.getEmailAddress()});
		_assertNotificationQueueEntryBody(
			String.format(
				"<p>Dear %s,</p>\n\n<p>This is an autogenerated email for %s." +
					"</p>\n\n<p>Charlie 1 was added to %s.</p>",
				user.getFullName(), objectDefinition.getShortName(),
				objectEntryFolder1.getName()));

		_clearObjectEntryIdsMap();

		ObjectEntryFolder objectEntryFolder2 =
			ObjectEntryFolderTestUtil.addObjectEntryFolder(
				group.getGroupId(),
				objectEntryFolder1.getObjectEntryFolderId());

		ObjectEntry objectEntry = defaultObjectEntryManager.addObjectEntry(
			dtoConverterContext, objectDefinition,
			new ObjectEntry() {
				{
					objectEntryFolderExternalReferenceCode =
						objectEntryFolder2.getExternalReferenceCode();
					properties = HashMapBuilder.<String, Object>put(
						"firstName", "John"
					).build();
				}
			},
			group.getGroupKey());

		_assertMailMessage(new String[] {user.getEmailAddress()});
		_assertNotificationQueueEntryBody(
			String.format(
				"<p>Dear %s,</p>\n\n<p>This is an autogenerated email for %s." +
					"</p>\n\n<p>John 1 was added to %s.</p>",
				user.getFullName(), objectDefinition.getShortName(),
				objectEntryFolder2.getName()));

		_clearObjectEntryIdsMap();

		AssertUtils.assertFailure(
			UnsupportedOperationException.class, null,
			() -> _objectEntryLocalService.subscribeObjectEntry(
				user.getUserId(), group.getGroupId(), objectEntry.getId()));
		AssertUtils.assertFailure(
			UnsupportedOperationException.class, null,
			() -> _objectEntryFolderLocalService.subscribeObjectEntryFolder(
				user.getUserId(), group.getGroupId(),
				objectEntryFolder2.getObjectEntryFolderId()));

		_objectEntryFolderLocalService.unsubscribeObjectEntryFolder(
			user.getUserId(), group.getGroupId(),
			objectEntryFolder1.getObjectEntryFolderId());

		_objectEntryLocalService.subscribeObjectEntry(
			user.getUserId(), group.getGroupId(), objectEntry.getId());

		defaultObjectEntryManager.updateObjectEntry(
			dtoConverterContext, objectDefinition, objectEntry.getId(),
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"firstName", "Peter"
					).build();
				}
			});

		_assertMailMessage(new String[] {user.getEmailAddress()});
		_assertNotificationQueueEntryBody(
			String.format(
				"<p>Dear %s,</p>\n\n<p>This is an autogenerated email for %s." +
					"</p>\n\n<p>Peter 2 was updated.</p>",
				user.getFullName(), objectDefinition.getShortName()));

		_clearObjectEntryIdsMap();

		defaultObjectEntryManager.expireObjectEntry(
			dtoConverterContext, objectEntry.getId());

		_assertMailMessage(new String[] {user.getEmailAddress()});
		_assertNotificationQueueEntryBody(
			String.format(
				"<p>Dear %s,</p>\n\n<p>This is an autogenerated email for %s." +
					"</p>\n\n<p>Peter 2 has expired.</p>",
				user.getFullName(), objectDefinition.getShortName()));

		_clearObjectEntryIdsMap();

		_setUser(user2);

		_objectDefinitionLocalService.deleteObjectDefinition(
			objectDefinition.getObjectDefinitionId());
		_objectEntryFolderLocalService.deleteObjectEntryFolder(
			objectEntryFolder1.getObjectEntryFolderId());
	}

	@FeatureFlag("LPD-50091")
	@Test
	public void testSendNotificationToUserGroups() throws Exception {
		UserGroup userGroup1 = UserGroupTestUtil.addUserGroup();

		_userGroupLocalService.addUserUserGroup(
			user1.getUserId(), userGroup1.getUserGroupId());

		UserGroup userGroup2 = UserGroupTestUtil.addUserGroup();

		_userGroupLocalService.addUserUserGroup(
			user1.getUserId(), userGroup2.getUserGroupId());
		_userGroupLocalService.addUserUserGroup(
			user2.getUserId(), userGroup2.getUserGroupId());

		ObjectDefinition objectDefinition =
			_addObjectDefinitionWithNotificationTemplateObjectAction(
				NotificationRecipientConstants.TYPE_USER_GROUP,
				userGroup1.getName(), userGroup2.getName());

		Role guestRole = _roleLocalService.fetchRole(
			TestPropsValues.getCompanyId(), RoleConstants.GUEST);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), objectDefinition.getClassName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			guestRole.getRoleId(), new String[] {ActionKeys.VIEW});

		try {
			_setUser(user1);

			_testSendNotification(
				null, StringPool.BLANK, user1, 1,
				StringUtil.merge(
					ListUtil.fromArray(
						user1.getEmailAddress(), user2.getEmailAddress())),
				ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
				objectDefinition);

			objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}
		finally {
			_setUser(user2);

			_userGroupLocalService.setUserUserGroups(
				user1.getUserId(), new long[0]);

			_userGroupLocalService.deleteUserGroup(userGroup1);

			_userGroupLocalService.setUserUserGroups(
				user2.getUserId(), new long[0]);

			_userGroupLocalService.deleteUserGroup(userGroup2);
		}
	}

	private static void _pushServiceContext() throws Exception {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest(
			null, StringPool.BLANK, RandomTestUtil.randomString());

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			CompanyLocalServiceUtil.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setUser(
			UserLocalServiceUtil.getUser(TestPropsValues.getUserId()));

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		HttpServletRequestThreadLocal.setHttpServletRequest(httpServletRequest);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setRequest(httpServletRequest);

		themeDisplay = serviceContext.getThemeDisplay();

		themeDisplay.setScopeGroupId(serviceContext.getScopeGroupId());
		themeDisplay.setSiteGroupId(serviceContext.getScopeGroupId());

		ServiceContextThreadLocal.pushServiceContext(serviceContext);
	}

	private AccountEntry _addAccountEntry() throws Exception {
		return _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, TestPropsValues.getUserId(), 0L,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, null, RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());
	}

	private AccountRole _addAccountRole(long accountEntryId) throws Exception {
		return _accountRoleLocalService.addAccountRole(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			accountEntryId, RandomTestUtil.randomString(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap());
	}

	private ObjectDefinition _addAndPublishCustomObjectDefinition(
			boolean accountRestricted)
		throws Exception {

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, false, true,
				false, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"textObjectField"
					).build()),
				Collections.emptyList());

		if (accountRestricted) {
			ObjectDefinition accountEntryObjectDefinition =
				objectDefinitionLocalService.fetchObjectDefinition(
					TestPropsValues.getCompanyId(),
					AccountEntry.class.getSimpleName());

			objectDefinition =
				objectDefinitionLocalService.enableAccountEntryRestricted(
					objectRelationshipLocalService.addObjectRelationship(
						null, TestPropsValues.getUserId(),
						accountEntryObjectDefinition.getObjectDefinitionId(),
						objectDefinition.getObjectDefinitionId(), 0,
						ObjectRelationshipConstants.DELETION_TYPE_PREVENT,
						false,
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString()),
						"relationship", false,
						ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null));
		}

		objectDefinition =
			objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), objectDefinition.getResourceName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			ObjectActionKeys.ADD_OBJECT_ENTRY);

		return objectDefinition;
	}

	private NotificationTemplate _addNotificationTemplate(
			String body, String editorType, Map<Locale, String> fromName,
			boolean singleRecipient, Map<Locale, String> to)
		throws Exception {

		ObjectField objectField = objectFieldLocalService.getObjectField(
			childObjectDefinition.getObjectDefinitionId(),
			"attachmentObjectField");

		return notificationTemplateLocalService.addNotificationTemplate(
			NotificationTemplateUtil.createNotificationContext(
				TestPropsValues.getUser(),
				childObjectDefinition.getObjectDefinitionId(), body,
				RandomTestUtil.randomString(), editorType,
				Arrays.asList(
					NotificationRecipientSettingUtil.
						createNotificationRecipientSetting(
							"bcc",
							"[%CURRENT_USER_EMAIL_ADDRESS%],bcc@liferay.com"),
					NotificationRecipientSettingUtil.
						createNotificationRecipientSetting(
							"cc",
							"[%CURRENT_USER_EMAIL_ADDRESS%],cc@liferay.com"),
					NotificationRecipientSettingUtil.
						createNotificationRecipientSetting(
							"from", "[%CURRENT_USER_EMAIL_ADDRESS%]"),
					NotificationRecipientSettingUtil.
						createNotificationRecipientSetting(
							"fromName", fromName),
					NotificationRecipientSettingUtil.
						createNotificationRecipientSetting(
							"singleRecipient", String.valueOf(singleRecipient)),
					NotificationRecipientSettingUtil.
						createNotificationRecipientSetting("to", to)),
				ListUtil.toString(
					getTermNames(), StringPool.BLANK, StringPool.SEMICOLON),
				NotificationConstants.TYPE_EMAIL,
				Collections.singletonList(objectField.getObjectFieldId())));
	}

	private ObjectAction _addNotificationTemplateObjectAction(
			String body, String editorType, String objectActionTriggerKey,
			ObjectDefinition objectDefinition)
		throws Exception {

		NotificationTemplate notificationTemplate =
			notificationTemplateLocalService.createNotificationTemplate(
				RandomTestUtil.randomInt());

		notificationTemplate.setUserId(TestPropsValues.getUserId());
		notificationTemplate.setObjectDefinitionId(
			objectDefinition.getObjectDefinitionId());
		notificationTemplate.setBody(body);
		notificationTemplate.setDescription(RandomTestUtil.randomString());
		notificationTemplate.setEditorType(editorType);
		notificationTemplate.setName(RandomTestUtil.randomString());
		notificationTemplate.setSubject(RandomTestUtil.randomString());
		notificationTemplate.setType(NotificationConstants.TYPE_EMAIL);

		NotificationContext notificationContext = new NotificationContext();

		notificationContext.setAttachmentObjectFieldIds(
			Collections.emptyList());
		notificationContext.setNotificationRecipient(
			NotificationRecipientLocalServiceUtil.createNotificationRecipient(
				RandomTestUtil.randomInt()));
		notificationContext.setNotificationRecipientSettings(
			Arrays.asList(
				NotificationRecipientSettingUtil.
					createNotificationRecipientSetting(
						"from", "[%CURRENT_USER_EMAIL_ADDRESS%]"),
				NotificationRecipientSettingUtil.
					createNotificationRecipientSetting(
						"fromName",
						Collections.singletonMap(
							LocaleUtil.US, RandomTestUtil.randomString())),
				NotificationRecipientSettingUtil.
					createNotificationRecipientSetting(
						"to", "[%CURRENT_USER_EMAIL_ADDRESS%]")));
		notificationContext.setNotificationTemplate(notificationTemplate);
		notificationContext.setType(NotificationConstants.TYPE_EMAIL);

		notificationTemplate =
			notificationTemplateLocalService.addNotificationTemplate(
				notificationContext);

		return _objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), true, StringPool.BLANK,
			RandomTestUtil.randomString(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_NOTIFICATION,
			objectActionTriggerKey,
			UnicodePropertiesBuilder.put(
				"notificationTemplateId",
				notificationTemplate.getNotificationTemplateId()
			).build(),
			false);
	}

	private ObjectAction _addObjectAction(
			long objectDefinitionId, String objectActionTriggerKey,
			long objectNotificationTemplateId)
		throws Exception {

		return objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			objectDefinitionId, true, StringPool.BLANK,
			RandomTestUtil.randomString(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_NOTIFICATION,
			objectActionTriggerKey,
			UnicodePropertiesBuilder.put(
				"notificationTemplateId", objectNotificationTemplateId
			).build(),
			false);
	}

	private ObjectDefinition
			_addObjectDefinitionWithNotificationTemplateObjectAction(
				String type, String... names)
		throws Exception {

		List<NotificationRecipientSetting> notificationRecipientSettings =
			new ArrayList<>();

		notificationRecipientSettings.add(
			NotificationRecipientSettingUtil.createNotificationRecipientSetting(
				NotificationRecipientSettingConstants.NAME_CC,
				"[%CURRENT_USER_EMAIL_ADDRESS%],cc@liferay.com"));
		notificationRecipientSettings.add(
			NotificationRecipientSettingUtil.createNotificationRecipientSetting(
				NotificationRecipientSettingConstants.NAME_FROM,
				"[%CURRENT_USER_EMAIL_ADDRESS%]"));
		notificationRecipientSettings.add(
			NotificationRecipientSettingUtil.createNotificationRecipientSetting(
				NotificationRecipientSettingConstants.NAME_FROM_NAME,
				Collections.singletonMap(
					LocaleUtil.US, "[%CURRENT_USER_FIRST_NAME%]")));
		notificationRecipientSettings.add(
			NotificationRecipientSettingUtil.createNotificationRecipientSetting(
				NotificationRecipientSettingConstants.NAME_SINGLE_RECIPIENT,
				Boolean.FALSE.toString()));

		for (String name : names) {
			notificationRecipientSettings.add(
				NotificationRecipientSettingUtil.
					createNotificationRecipientSetting(
						NotificationRecipientSettingConstants.NAME_TO, name));
		}

		notificationRecipientSettings.add(
			NotificationRecipientSettingUtil.createNotificationRecipientSetting(
				NotificationRecipientSettingConstants.NAME_TO_TYPE, type));

		NotificationTemplate notificationTemplate =
			notificationTemplateLocalService.addNotificationTemplate(
				NotificationTemplateUtil.createNotificationContext(
					TestPropsValues.getUser(), 0, RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					NotificationTemplateConstants.EDITOR_TYPE_RICH_TEXT,
					notificationRecipientSettings,
					RandomTestUtil.randomString(),
					NotificationConstants.TYPE_EMAIL, Collections.emptyList()));

		ObjectDefinition objectDefinition =
			_addAndPublishCustomObjectDefinition(false);

		_addObjectAction(
			objectDefinition.getObjectDefinitionId(),
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
			notificationTemplate.getNotificationTemplateId());

		return objectDefinition;
	}

	private Role _addRole(int type, User user) throws Exception {
		return _roleLocalService.addRole(
			RandomTestUtil.randomString(), user.getUserId(), null, 0,
			RandomTestUtil.randomString(), null, null, type, null, null);
	}

	private void _assertMailMessage(String[] emailAddresses) {
		Assert.assertNotNull(
			MailServiceTestUtil.getMailMessage("To", emailAddresses));

		MailServiceTestUtil.clearMessages();
	}

	private void _assertNotificationQueueEntry(
			String expectedBcc, String expectedFileName,
			boolean expectedSingleRecipient, String expectedToEmailAddress,
			NotificationQueueEntry notificationQueueEntry)
		throws Exception {

		_assertNotificationQueueEntry(
			expectedBcc, user2, expectedSingleRecipient, expectedToEmailAddress,
			notificationQueueEntry);

		assertTermValues(
			getTermValues(),
			ListUtil.fromString(
				notificationQueueEntry.getBody(), StringPool.SEMICOLON));
		assertTermValues(
			getTermValues(),
			ListUtil.fromString(
				notificationQueueEntry.getSubject(), StringPool.SEMICOLON));

		Folder folder = _getFolder(notificationQueueEntry);

		FileEntry fileEntry = _portletFileRepository.getPortletFileEntry(
			folder.getGroupId(), folder.getFolderId(), expectedFileName);

		List<NotificationQueueEntryAttachment>
			notificationQueueEntryAttachments =
				_notificationQueueEntryAttachmentLocalService.
					getNotificationQueueEntryNotificationQueueEntryAttachments(
						notificationQueueEntry.getNotificationQueueEntryId());

		NotificationQueueEntryAttachment notificationQueueEntryAttachment =
			notificationQueueEntryAttachments.get(0);

		Assert.assertEquals(
			fileEntry.getFileEntryId(),
			notificationQueueEntryAttachment.getFileEntryId());
	}

	private void _assertNotificationQueueEntry(
		String expectedBcc, User expectedRecipient,
		boolean expectedSingleRecipient, String expectedToEmailAddress,
		NotificationQueueEntry notificationQueueEntry) {

		Assert.assertNotNull(
			MailServiceTestUtil.getMailMessage(
				"To", StringUtil.split(expectedToEmailAddress)));

		Map<String, Object> notificationRecipientSettingsMap =
			NotificationRecipientSettingUtil.
				getNotificationRecipientSettingsMap(notificationQueueEntry);

		Assert.assertEquals(
			expectedRecipient.getEmailAddress() + ",cc@liferay.com",
			notificationRecipientSettingsMap.get("cc"));
		Assert.assertEquals(
			expectedRecipient.getEmailAddress(),
			notificationRecipientSettingsMap.get("from"));
		Assert.assertEquals(
			expectedRecipient.getFirstName(),
			notificationRecipientSettingsMap.get("fromName"));
		Assert.assertEquals(
			expectedSingleRecipient,
			notificationRecipientSettingsMap.get("singleRecipient"));
		AssertUtils.assertEqualsSorted(
			StringUtil.split(expectedBcc),
			StringUtil.split(
				String.valueOf(notificationRecipientSettingsMap.get("bcc"))));
		AssertUtils.assertEqualsSorted(
			StringUtil.split(expectedToEmailAddress),
			StringUtil.split(
				String.valueOf(notificationRecipientSettingsMap.get("to"))));
	}

	private void _assertNotificationQueueEntryBody(
			String notificationQueueEntryBody)
		throws Exception {

		List<NotificationQueueEntry> notificationQueueEntries =
			notificationQueueEntryLocalService.getNotificationEntries(
				NotificationConstants.TYPE_EMAIL,
				NotificationQueueEntryConstants.STATUS_SENT);

		Assert.assertEquals(
			notificationQueueEntries.toString(), 1,
			notificationQueueEntries.size());

		NotificationQueueEntry notificationQueueEntry =
			notificationQueueEntries.get(0);

		Assert.assertEquals(
			notificationQueueEntryBody, notificationQueueEntry.getBody());

		notificationQueueEntryLocalService.deleteNotificationQueueEntry(
			notificationQueueEntry);
	}

	private void _clearObjectEntryIdsMap() {
		ThreadLocal<Map<Long, Set<Long>>> threadLocal =
			ReflectionTestUtil.getFieldValue(
				ObjectActionThreadLocal.class, "_objectEntryIdsMap");

		threadLocal.set(new HashMap<>());
	}

	private String _formatDate(Date date) {
		if (date == null) {
			return StringPool.BLANK;
		}

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			DateTimeFormatterBuilder.getLocalizedDateTimePattern(
				FormatStyle.SHORT, FormatStyle.SHORT, IsoChronology.INSTANCE,
				LocaleUtil.US),
			LocaleUtil.US);

		return dateFormat.format(date);
	}

	private String _getCommerceOrderNotificationQueueEntryBody(
			CommerceOrder commerceOrder)
		throws Exception {

		AccountEntry accountEntry = commerceOrder.getAccountEntry();

		return StringUtil.merge(
			Arrays.asList(
				String.valueOf(accountEntry.getAccountEntryId()),
				String.valueOf(commerceOrder.isInactive()),
				commerceOrder.getName(),
				_formatDate(commerceOrder.getOrderDate()),
				String.valueOf(commerceOrder.isPending()),
				_formatDate(commerceOrder.getRequestedDeliveryDate()),
				_formatDate(commerceOrder.getStatusDate())),
			StringPool.NEW_LINE);
	}

	private Folder _getFolder(NotificationQueueEntry notificationQueueEntry)
		throws Exception {

		Group group = _groupLocalService.getCompanyGroup(
			notificationQueueEntry.getCompanyId());

		Repository repository = _portletFileRepository.getPortletRepository(
			group.getGroupId(), NotificationPortletKeys.NOTIFICATION_TEMPLATES);

		return _portletFileRepository.getPortletFolder(
			repository.getRepositoryId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			String.valueOf(
				notificationQueueEntry.getNotificationQueueEntryId()));
	}

	private String _getObjectEntryNotificationQueueEntryBody(
			ObjectEntry objectEntry)
		throws Exception {

		com.liferay.object.model.ObjectEntry serviceBuilderObjectEntry =
			_objectEntryLocalService.getObjectEntry(objectEntry.getId());

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		return StringUtil.merge(
			Arrays.asList(
				serviceBuilderObjectEntry.getUserName(),
				_formatDate(serviceBuilderObjectEntry.getCreateDate()),
				serviceBuilderObjectEntry.getExternalReferenceCode(),
				_formatDate(serviceBuilderObjectEntry.getModifiedDate()),
				String.valueOf(serviceBuilderObjectEntry.getObjectEntryId()),
				_formatDate(serviceBuilderObjectEntry.getLastPublishDate()),
				WorkflowConstants.getStatusLabel(
					serviceBuilderObjectEntry.getStatus()),
				"true",
				_formatDate(
					DateUtil.parseDate(
						"MM/dd/yy hh:mm a", "9/25/24 12:00 AM", LocaleUtil.US)),
				"2024-09-25T00:00", "test@liferay.com", "12345", "123456789",
				"listTypeEntry1Value,listTypeEntry2Value",
				"listTypeEntry1Value", "", "textObjectFieldValue",
				LanguageUtil.getLanguageId(LocaleUtil.US),
				_portal.getPortalURL(serviceContext.getRequest()),
				StringPool.NEW_LINE, StringPool.NEW_LINE,
				serviceContext.getCompanyId()),
			StringPool.NEW_LINE);
	}

	private String _getTermName(
		ObjectDefinition objectDefinition, String objectFieldName) {

		return StringBundler.concat(
			"[%",
			StringUtil.toUpperCase(
				objectDefinition.getShortName() + "_" + objectFieldName),
			"%]");
	}

	private String _read(String fileName) throws Exception {
		return new String(
			FileUtil.getBytes(getClass(), "dependencies/" + fileName));
	}

	private void _setUser(User user) {
		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
		PrincipalThreadLocal.setName(user.getUserId());
	}

	private void _testSendNotification(
			AccountEntry accountEntry, String expectedBcc,
			User expectedCurrentUser, int expectedNotificationQueueEntriesCount,
			String expectedToEmailAddress, String objectActionTriggerKey,
			ObjectDefinition objectDefinition)
		throws Exception {

		ObjectEntry objectEntry = objectEntryManager.addObjectEntry(
			dtoConverterContext, objectDefinition,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"r_relationship_accountEntryId",
						() -> {
							if (accountEntry == null) {
								return null;
							}

							return accountEntry.getAccountEntryId();
						}
					).put(
						"textObjectField", RandomTestUtil.randomString()
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		if (StringUtil.equals(
				objectActionTriggerKey,
				ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE)) {

			_objectEntryLocalService.deleteObjectEntry(objectEntry.getId());
		}

		List<NotificationQueueEntry> notificationQueueEntries =
			notificationQueueEntryLocalService.getNotificationEntries(
				NotificationConstants.TYPE_EMAIL,
				NotificationQueueEntryConstants.STATUS_SENT);

		Assert.assertEquals(
			notificationQueueEntries.toString(),
			expectedNotificationQueueEntriesCount,
			notificationQueueEntries.size());

		if (expectedNotificationQueueEntriesCount == 0) {
			return;
		}

		_assertNotificationQueueEntry(
			expectedBcc, expectedCurrentUser, false, expectedToEmailAddress,
			notificationQueueEntries.get(0));

		MailServiceTestUtil.clearMessages();

		notificationQueueEntryLocalService.deleteNotificationQueueEntry(
			notificationQueueEntries.get(0));
	}

	private void _testSendNotification(
			int expectedNotificationQueueEntriesCount,
			List<String> expectedToEmailAddresses, boolean singleRecipient,
			String to)
		throws Exception {

		FileEntry fileEntry = TempFileEntryUtil.addTempFileEntry(
			TestPropsValues.getGroupId(), TestPropsValues.getUserId(),
			StringUtil.randomString(),
			TempFileEntryUtil.getTempFileName(
				StringUtil.randomString() + ".txt"),
			FileUtil.createTempFile(RandomTestUtil.randomBytes()),
			ContentTypes.TEXT_PLAIN);

		executeNotificationObjectAction(
			fileEntry.getFileEntryId(),
			_addNotificationTemplate(
				ListUtil.toString(
					getTermNames(), StringPool.BLANK, StringPool.SEMICOLON),
				NotificationTemplateConstants.EDITOR_TYPE_RICH_TEXT,
				Collections.singletonMap(
					LocaleUtil.US, "[%CURRENT_USER_FIRST_NAME%]"),
				singleRecipient, Collections.singletonMap(LocaleUtil.US, to)));

		List<NotificationQueueEntry> notificationQueueEntries = ListUtil.sort(
			notificationQueueEntryLocalService.getNotificationEntries(
				NotificationConstants.TYPE_EMAIL,
				NotificationQueueEntryConstants.STATUS_SENT),
			Comparator.comparing(
				notificationQueueEntry -> {
					Map<String, Object> notificationRecipientSettingsMap =
						NotificationRecipientSettingUtil.
							getNotificationRecipientSettingsMap(
								notificationQueueEntry);

					return String.valueOf(
						notificationRecipientSettingsMap.get(
							NotificationRecipientSettingConstants.NAME_TO));
				}));

		Assert.assertEquals(
			notificationQueueEntries.toString(),
			expectedNotificationQueueEntriesCount,
			notificationQueueEntries.size());

		if (expectedNotificationQueueEntriesCount == 0) {
			return;
		}

		_assertNotificationQueueEntry(
			user2.getEmailAddress() + ",bcc@liferay.com",
			TempFileEntryUtil.getOriginalTempFileName(fileEntry.getFileName()),
			singleRecipient, expectedToEmailAddresses.get(0),
			notificationQueueEntries.get(0));

		if (singleRecipient) {
			_assertNotificationQueueEntry(
				user2.getEmailAddress() + ",bcc@liferay.com",
				TempFileEntryUtil.getOriginalTempFileName(
					fileEntry.getFileName()),
				singleRecipient, expectedToEmailAddresses.get(1),
				notificationQueueEntries.get(1));
		}

		Assert.assertTrue(
			MailServiceTestUtil.lastMailMessageContains(
				ListUtil.toString(
					getTermValues(), StringPool.BLANK, StringPool.SEMICOLON)));

		MailServiceTestUtil.clearMessages();

		for (NotificationQueueEntry notificationQueueEntry :
				notificationQueueEntries) {

			Folder folder = _getFolder(notificationQueueEntry);

			notificationQueueEntryLocalService.deleteNotificationQueueEntry(
				notificationQueueEntry);

			AssertUtils.assertFailure(
				NoSuchFolderException.class,
				StringBundler.concat(
					"No Folder exists with the key {folderId=",
					folder.getFolderId(), "}"),
				() -> _portletFileRepository.getPortletFolder(
					folder.getFolderId()));

			Assert.assertTrue(
				ListUtil.isEmpty(
					_notificationQueueEntryAttachmentLocalService.
						getNotificationQueueEntryNotificationQueueEntryAttachments(
							notificationQueueEntry.
								getNotificationQueueEntryId())));
		}
	}

	private void _testSendNotificationToCurrentUser() throws Exception {
		List<NotificationQueueEntry> notificationQueueEntries =
			notificationQueueEntryLocalService.getNotificationEntries(
				NotificationConstants.TYPE_EMAIL,
				NotificationQueueEntryConstants.STATUS_SENT);

		Assert.assertEquals(
			notificationQueueEntries.toString(), 1,
			notificationQueueEntries.size());

		Map<String, Object> notificationRecipientSettingsMap =
			NotificationRecipientSettingUtil.
				getNotificationRecipientSettingsMap(
					notificationQueueEntries.get(0));

		AssertUtils.assertEqualsSorted(
			StringUtil.split(_user.getEmailAddress()),
			StringUtil.split(
				String.valueOf(notificationRecipientSettingsMap.get("to"))));

		MailServiceTestUtil.clearMessages();

		notificationQueueEntryLocalService.deleteNotificationQueueEntry(
			notificationQueueEntries.get(0));
	}

	private void _updateObjectAction(
			long notificationTemplateId, ObjectAction objectAction)
		throws Exception {

		_objectActionLocalService.updateObjectAction(
			objectAction.getExternalReferenceCode(),
			objectAction.getObjectActionId(), objectAction.isActive(),
			objectAction.getConditionExpression(),
			objectAction.getDescription(), objectAction.getErrorMessageMap(),
			objectAction.getLabelMap(), objectAction.getName(),
			objectAction.getObjectActionExecutorKey(),
			objectAction.getObjectActionTriggerKey(),
			UnicodePropertiesBuilder.put(
				"notificationTemplateId", notificationTemplateId
			).build());
	}

	@Inject
	private static ConfigurationAdmin _configurationAdmin;

	private static Configuration _freeMarkerEngineConfiguration;

	@Inject
	private static Portal _portal;

	private static ServiceRegistration<TemplateContextContributor>
		_serviceRegistration;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryOrganizationRelLocalService
		_accountEntryOrganizationRelLocalService;

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Inject
	private AccountRoleLocalService _accountRoleLocalService;

	@Inject
	private CommerceOrderEngine _commerceOrderEngine;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Inject
	private CommerceSubscriptionEngine _commerceSubscriptionEngine;

	@Inject
	private CommerceSubscriptionEntryLocalService
		_commerceSubscriptionEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject(
		filter = "mvc.command.name=/notification_templates/notification_template_ftl_elements"
	)
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private NotificationQueueEntryAttachmentLocalService
		_notificationQueueEntryAttachmentLocalService;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	@Inject
	private ObjectActionTriggerRegistry _objectActionTriggerRegistry;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	@Inject
	private PortletFileRepository _portletFileRepository;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SubscriptionLocalService _subscriptionLocalService;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

	private static class TestTemplateContextContributor
		implements TemplateContextContributor {

		@Override
		public void prepare(
			Map<String, Object> contextObjects,
			HttpServletRequest httpServletRequest) {

			contextObjects.put(
				"testTemplateContextContributorKey",
				"testTemplateContextContributorValue");
		}

	}

}