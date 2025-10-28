/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceSubscriptionEntry;
import com.liferay.commerce.order.engine.CommerceOrderEngine;
import com.liferay.commerce.payment.engine.CommerceSubscriptionEngine;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceSubscriptionEntryLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.test.util.ExpandoTestUtil;
import com.liferay.notification.constants.NotificationConstants;
import com.liferay.notification.constants.NotificationQueueEntryConstants;
import com.liferay.notification.constants.NotificationRecipientSettingConstants;
import com.liferay.notification.constants.NotificationTemplateConstants;
import com.liferay.notification.context.NotificationContext;
import com.liferay.notification.model.NotificationQueueEntry;
import com.liferay.notification.model.NotificationTemplate;
import com.liferay.notification.service.NotificationQueueEntryLocalService;
import com.liferay.notification.service.NotificationRecipientLocalServiceUtil;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.notification.service.NotificationTemplateLocalServiceUtil;
import com.liferay.notification.test.util.NotificationTemplateUtil;
import com.liferay.notification.util.NotificationRecipientSettingUtil;
import com.liferay.object.action.engine.ObjectActionEngine;
import com.liferay.object.action.executor.ObjectActionExecutorRegistry;
import com.liferay.object.action.trigger.ObjectActionTriggerRegistry;
import com.liferay.object.action.util.ObjectActionThreadLocal;
import com.liferay.object.constants.ObjectActionConstants;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectActionNameConstants;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.exception.ObjectActionErrorMessageException;
import com.liferay.object.exception.ObjectActionExecutorKeyException;
import com.liferay.object.exception.ObjectActionNameException;
import com.liferay.object.exception.ObjectActionParametersException;
import com.liferay.object.exception.ObjectActionSystemException;
import com.liferay.object.exception.ObjectActionTriggerKeyException;
import com.liferay.object.field.builder.AssigneeObjectFieldBuilder;
import com.liferay.object.field.builder.AutoIncrementObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.rest.resource.v1_0.ObjectEntryResource;
import com.liferay.object.scripting.executor.ObjectScriptingExecutor;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.object.test.util.TreeTestUtil;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.Authenticator;
import com.liferay.portal.kernel.security.auth.CompanyInheritableThreadLocalCallable;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.security.script.management.test.rule.ScriptManagementConfigurationTestRule;
import com.liferay.portal.security.script.management.test.util.ScriptManagementConfigurationTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.mail.MailMessage;
import com.liferay.portal.test.mail.MailServiceTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Closeable;
import java.io.Serializable;

import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.FutureTask;

import org.hamcrest.CoreMatchers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Brian Wing Shun Chan
 */
@FeatureFlags(
	featureFlags = {
		@FeatureFlag(value = "LPD-34594"), @FeatureFlag(value = "LPS-173537")
	}
)
@RunWith(Arquillian.class)
public class ObjectActionLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			ScriptManagementConfigurationTestRule.INSTANCE,
			SynchronousMailTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_accountEntry = CommerceTestUtil.addAccount(
			_group.getGroupId(), TestPropsValues.getUserId());

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			TestPropsValues.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		_objectDefinition = ObjectDefinitionTestUtil.addCustomObjectDefinition(
			FeatureFlagManagerUtil.isEnabled(
				TestPropsValues.getCompanyId(), "LPD-32050"),
			Arrays.asList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_DATE,
					ObjectFieldConstants.DB_TYPE_DATE, true, false, null,
					"Birthday", "birthday", false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME,
					ObjectFieldConstants.DB_TYPE_DATE_TIME, true, true, null,
					"Time", "time",
					Collections.singletonList(
						new ObjectFieldSettingBuilder(
						).name(
							"timeStorage"
						).value(
							"useInputAsEntered"
						).build()),
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
					"First Name", "firstName", true),
				ObjectFieldUtil.createObjectField(
					0, ObjectFieldConstants.BUSINESS_TYPE_TEXT, null,
					ObjectFieldConstants.DB_TYPE_STRING, true, false, null,
					"Last Name", "lastName", false, true)));
		_originalHttp = (Http)_getAndSetFieldValue(
			Http.class, "_http", ObjectActionExecutorConstants.KEY_WEBHOOK);
		_originalObjectScriptingExecutor =
			(ObjectScriptingExecutor)_getAndSetFieldValue(
				ObjectScriptingExecutor.class, "_objectScriptingExecutor",
				ObjectActionExecutorConstants.KEY_GROOVY);
		_user = UserTestUtil.addUser();
	}

	@After
	public void tearDown() throws PortalException {
		ReflectionTestUtil.setFieldValue(
			_objectActionExecutorRegistry.getObjectActionExecutor(
				0, ObjectActionExecutorConstants.KEY_WEBHOOK),
			"_http", _originalHttp);
		ReflectionTestUtil.setFieldValue(
			_objectActionExecutorRegistry.getObjectActionExecutor(
				0, ObjectActionExecutorConstants.KEY_GROOVY),
			"_objectScriptingExecutor", _originalObjectScriptingExecutor);

		_objectDefinitionLocalService.deleteObjectDefinition(_objectDefinition);
	}

	@Test
	public void testAddNotificationTemplateObjectActionWithSystemObjectDefinition()
		throws Exception {

		// Account entry system object definition

		ObjectDefinition accountEntryObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				TestPropsValues.getCompanyId(),
				AccountEntry.class.getSimpleName());

		// Add object action to send an email notification after updating
		// account entry

		ObjectAction objectAction1 = _addNotificationTemplateObjectAction(
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE,
			accountEntryObjectDefinition);

		User adminUser = UserTestUtil.getAdminUser(
			TestPropsValues.getCompanyId());

		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, adminUser.getUserId(), 0L,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, null, RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());

		_accountEntryLocalService.updateAccountEntry(accountEntry);

		List<NotificationQueueEntry> notificationQueueEntries =
			_notificationQueueEntryLocalService.getNotificationEntries(
				NotificationConstants.TYPE_EMAIL,
				NotificationQueueEntryConstants.STATUS_SENT);

		Assert.assertEquals(
			notificationQueueEntries.toString(), 1,
			notificationQueueEntries.size());

		// Commerce order system object definition

		ObjectDefinition commerceOrderObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				TestPropsValues.getCompanyId(), CommerceOrder.class.getName());

		// Add object action to send an email notification after updating
		// payment status

		ObjectAction objectAction2 = _addNotificationTemplateObjectAction(
			DestinationNames.COMMERCE_PAYMENT_STATUS,
			commerceOrderObjectDefinition);

		CommerceOrder commerceOrder = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency);

		commerceOrder = CommerceTestUtil.addCheckoutDetailsToCommerceOrder(
			commerceOrder, _user.getUserId(), true, true);

		_commerceOrderLocalService.updatePaymentStatus(
			commerceOrder.getUserId(), commerceOrder.getCommerceOrderId(),
			CommerceOrderPaymentConstants.STATUS_COMPLETED);

		notificationQueueEntries =
			_notificationQueueEntryLocalService.getNotificationEntries(
				NotificationConstants.TYPE_EMAIL,
				NotificationQueueEntryConstants.STATUS_SENT);

		Assert.assertEquals(
			notificationQueueEntries.toString(), 2,
			notificationQueueEntries.size());

		// Add object action to send an email notification after updating
		// subscription status

		_objectActionLocalService.deleteObjectAction(objectAction2);

		objectAction2 = _addNotificationTemplateObjectAction(
			DestinationNames.COMMERCE_SUBSCRIPTION_STATUS,
			commerceOrderObjectDefinition);

		commerceOrder = _commerceOrderEngine.checkoutCommerceOrder(
			commerceOrder, _user.getUserId());

		Assert.assertEquals(
			CommerceOrderConstants.ORDER_STATUS_PENDING,
			commerceOrder.getOrderStatus());

		List<CommerceSubscriptionEntry> commerceSubscriptionEntries =
			_commerceSubscriptionEntryLocalService.
				getCommerceSubscriptionEntries(
					_user.getCompanyId(), _commerceChannel.getGroupId(),
					_user.getUserId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null);

		CommerceSubscriptionEntry commerceSubscriptionEntry =
			commerceSubscriptionEntries.get(0);

		_commerceSubscriptionEngine.suspendRecurringPayment(
			commerceSubscriptionEntry.getCommerceSubscriptionEntryId());

		notificationQueueEntries =
			_notificationQueueEntryLocalService.getNotificationEntries(
				NotificationConstants.TYPE_EMAIL,
				NotificationQueueEntryConstants.STATUS_SENT);

		Assert.assertEquals(
			notificationQueueEntries.toString(), 3,
			notificationQueueEntries.size());

		_notificationQueueEntryLocalService.deleteNotificationQueueEntry(
			notificationQueueEntries.get(0));
		_notificationQueueEntryLocalService.deleteNotificationQueueEntry(
			notificationQueueEntries.get(1));
		_notificationQueueEntryLocalService.deleteNotificationQueueEntry(
			notificationQueueEntries.get(2));

		_objectActionLocalService.deleteObjectAction(objectAction1);
		_objectActionLocalService.deleteObjectAction(objectAction2);
	}

	@Test
	public void testAddObjectAction() throws Exception {

		// Add object actions

		AssertUtils.assertFailure(
			ObjectActionErrorMessageException.class,
			"Error message is null for locale " +
				LocaleUtil.US.getDisplayName(),
			() -> _addObjectAction(
				StringPool.BLANK, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				ObjectActionTriggerConstants.KEY_STANDALONE, false));

		try (Closeable closeable =
				ScriptManagementConfigurationTestUtil.saveWithCloseable(
					false)) {

			AssertUtils.assertFailure(
				ObjectActionExecutorKeyException.class,
				"Groovy script based object actions are not allowed",
				() -> _addObjectAction(
					RandomTestUtil.randomString(),
					ObjectActionExecutorConstants.KEY_GROOVY,
					ObjectActionTriggerConstants.KEY_STANDALONE,
					UnicodePropertiesBuilder.put(
						"script", "println \"Hello World\""
					).build(),
					false));
		}

		AssertUtils.assertFailure(
			ObjectActionSystemException.class, false,
			"Only allowed bundles can add system object actions",
			() -> _addObjectAction(
				StringPool.BLANK, RandomTestUtil.randomString(),
				StringPool.BLANK, RandomTestUtil.randomString(),
				ObjectActionTriggerConstants.KEY_ON_AFTER_ADD, true));

		String name = RandomTestUtil.randomString();

		ObjectAction objectAction1 = _addObjectAction(
			name, ObjectActionExecutorConstants.KEY_WEBHOOK,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
			UnicodePropertiesBuilder.put(
				"secret", "onafteradd"
			).put(
				"url", "https://onafteradd.com"
			).build(),
			false);

		AssertUtils.assertFailure(
			ObjectActionNameException.class, "Duplicate name " + name,
			() -> _addObjectAction(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), name,
				ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE, false));

		AssertUtils.assertFailure(
			ObjectActionNameException.class, "Name is null",
			() -> _addObjectAction(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), StringPool.BLANK,
				ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE, false));
		AssertUtils.assertFailure(
			ObjectActionNameException.class,
			"Name must be less than 41 characters",
			() -> _addObjectAction(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(42),
				ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE, false));
		AssertUtils.assertFailure(
			ObjectActionNameException.class,
			"Name must only contain letters and digits",
			() -> _addObjectAction(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), "Abl e",
				ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE, false));
		AssertUtils.assertFailure(
			ObjectActionNameException.class,
			"Name must only contain letters and digits",
			() -> _addObjectAction(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), "Abl-e",
				ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE, false));
		AssertUtils.assertFailure(
			ObjectActionTriggerKeyException.class,
			"The object action trigger key onAfterRootUpdate can only be " +
				"used by a root object definition",
			() -> _addObjectAction(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				ObjectActionTriggerConstants.KEY_ON_AFTER_ROOT_UPDATE, false));

		ObjectAction objectAction2 = _addObjectAction(
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_WEBHOOK,
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE,
			UnicodePropertiesBuilder.put(
				"secret", "onafterdelete"
			).put(
				"url", "https://onafterdelete.com"
			).build(),
			false);
		ObjectAction objectAction3 = _addObjectAction(
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_WEBHOOK,
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE,
			UnicodePropertiesBuilder.put(
				"secret", "onafterupdate"
			).put(
				"url", "https://onafterupdate.com"
			).build(),
			false);
		ObjectAction objectAction4 = _addObjectAction(
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_GROOVY,
			ObjectActionTriggerConstants.KEY_STANDALONE,
			UnicodePropertiesBuilder.put(
				"script", "println \"Hello World\""
			).build(),
			false);

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.put(
			"objectDefinitionId", RandomTestUtil.randomLong()
		).put(
			"predefinedValues",
			JSONUtil.putAll(
				JSONUtil.put(
					"inputAsValue", true
				).put(
					"name", "firstName"
				).put(
					"value", "Peter"
				),
				JSONUtil.put(
					"inputAsValue", true
				).put(
					"name", "lastName"
				).put(
					"value", "White"
				),
				JSONUtil.put(
					"inputAsValue", true
				).put(
					"name", "time"
				).put(
					"value", "2023-06-01 06:42:08.0"
				)
			).toString()
		).build();

		try {
			_addObjectAction(
				RandomTestUtil.randomString(),
				ObjectActionExecutorConstants.KEY_ADD_OBJECT_ENTRY,
				ObjectActionTriggerConstants.KEY_STANDALONE, unicodeProperties,
				false);

			Assert.fail();
		}
		catch (ObjectActionParametersException
					objectActionParametersException) {

			Assert.assertEquals(
				"invalid",
				MapUtil.getString(
					objectActionParametersException.getMessageKeys(),
					"objectDefinitionId"));
		}

		unicodeProperties.setProperty(
			"objectDefinitionId",
			String.valueOf(_objectDefinition.getObjectDefinitionId()));

		ObjectAction objectAction5 = _addObjectAction(
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_UPDATE_OBJECT_ENTRY,
			ObjectActionTriggerConstants.KEY_STANDALONE, unicodeProperties,
			false);

		ObjectAction systemObjectAction = _addObjectAction(
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_UPDATE_OBJECT_ENTRY,
			ObjectActionTriggerConstants.KEY_STANDALONE,
			UnicodePropertiesBuilder.put(
				"objectDefinitionId", _objectDefinition.getObjectDefinitionId()
			).put(
				"predefinedValues",
				JSONUtil.putAll(
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "firstName"
					).put(
						"value", "Jack"
					)
				).toString()
			).build(),
			true);

		_objectDefinition = _publishCustomObjectDefinition();

		// Auto increment object field should not be populated by object actions

		ObjectField autoIncrementObjectField =
			ObjectFieldUtil.addCustomObjectField(
				new AutoIncrementObjectFieldBuilder(
				).userId(
					TestPropsValues.getUserId()
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					StringUtil.randomId()
				).objectDefinitionId(
					_objectDefinition.getObjectDefinitionId()
				).objectFieldSettings(
					Arrays.asList(
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_INITIAL_VALUE
						).value(
							"0123"
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_PREFIX
						).value(
							"LPS-"
						).build())
				).build());

		try {
			_addObjectAction(
				RandomTestUtil.randomString(),
				ObjectActionExecutorConstants.KEY_ADD_OBJECT_ENTRY,
				ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
				UnicodePropertiesBuilder.put(
					"objectDefinitionId",
					_objectDefinition.getObjectDefinitionId()
				).put(
					"predefinedValues",
					JSONUtil.putAll(
						JSONUtil.put(
							"inputAsValue", true
						).put(
							"name", autoIncrementObjectField.getName()
						).put(
							"value", RandomTestUtil.randomString()
						)
					).toString()
				).build(),
				false);

			Assert.fail();
		}
		catch (ObjectActionParametersException
					objectActionParametersException) {

			Map<String, Object> messageKeys =
				objectActionParametersException.getMessageKeys();

			Assert.assertEquals(
				"invalid",
				MapUtil.getString(
					(Map<String, String>)messageKeys.get("predefinedValues"),
					autoIncrementObjectField.getName()));
		}

		_objectFieldLocalService.deleteObjectField(autoIncrementObjectField);

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		String originalName = PrincipalThreadLocal.getName();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(_user));
			PrincipalThreadLocal.setName(_user.getUserId());

			// Add object entry

			Assert.assertEquals(0, _argumentsList.size());

			ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
				0, TestPropsValues.getUserId(),
				_objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"birthday", "2000-12-25"
				).put(
					"firstName", "John"
				).put(
					"lastName", "Smith"
				).build(),
				ServiceContextTestUtil.getServiceContext());

			// On after create

			_assertWebhookObjectAction(
				"2000-12-25T00:00:00.000Z", "John", "Smith",
				ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
				_objectDefinition, null, null,
				WorkflowConstants.STATUS_APPROVED);

			// Execute standalone action to run a Groovy script

			ObjectEntryResource objectEntryResource = _getObjectEntryResource(
				_user);

			try {
				objectEntryResource.putObjectEntryObjectActionObjectActionName(
					objectEntry.getObjectEntryId(), objectAction4.getName());

				Assert.fail();
			}
			catch (PrincipalException.MustHavePermission principalException) {
				Assert.assertThat(
					principalException.getMessage(),
					CoreMatchers.containsString(
						StringBundler.concat(
							"User ", _user.getUserId(), " must have ",
							objectAction4.getName(), " permission for")));
			}

			_addModelResourcePermissions(
				objectAction4.getName(), objectEntry.getObjectEntryId(),
				_user.getUserId());

			objectEntryResource.putObjectEntryObjectActionObjectActionName(
				objectEntry.getObjectEntryId(), objectAction4.getName());

			_assertGroovyObjectActionExecutorArguments("John", objectEntry);

			// Update object entry

			Assert.assertEquals(0, _argumentsList.size());

			objectEntry = _objectEntryLocalService.partialUpdateObjectEntry(
				TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
				objectEntry.getObjectEntryFolderId(),
				HashMapBuilder.<String, Serializable>put(
					"firstName", "João"
				).put(
					"lastName", "o Discípulo Amado"
				).build(),
				ServiceContextTestUtil.getServiceContext());

			// On after update

			_assertWebhookObjectAction(
				"2000-12-25T00:00:00.000Z", "João", "o Discípulo Amado",
				ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE,
				_objectDefinition, "John", "Smith",
				WorkflowConstants.STATUS_APPROVED);

			// Execute standalone action to update the current object entry

			try {
				objectEntryResource.
					putByExternalReferenceCodeObjectActionObjectActionName(
						objectEntry.getExternalReferenceCode(),
						objectAction5.getName());

				Assert.fail();
			}
			catch (PrincipalException.MustHavePermission principalException) {
				Assert.assertThat(
					principalException.getMessage(),
					CoreMatchers.containsString(
						StringBundler.concat(
							"User ", _user.getUserId(), " must have ",
							objectAction5.getName(), " permission for")));
			}

			_addModelResourcePermissions(
				objectAction5.getName(), objectEntry.getObjectEntryId(),
				_user.getUserId());

			objectEntryResource.
				putByExternalReferenceCodeObjectActionObjectActionName(
					objectEntry.getExternalReferenceCode(),
					objectAction5.getName());

			Map<String, Serializable> values =
				_objectEntryLocalService.getValues(
					objectEntry.getObjectEntryId());

			Assert.assertEquals(
				"Peter", MapUtil.getString(values, "firstName"));
			Assert.assertEquals("White", MapUtil.getString(values, "lastName"));
			Assert.assertEquals(
				"2023-06-01 06:42:08.0", MapUtil.getString(values, "time"));

			// Execute standalone system action to update the current object
			// entry

			objectEntry = _objectEntryLocalService.partialUpdateObjectEntry(
				TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
				objectEntry.getObjectEntryFolderId(),
				HashMapBuilder.<String, Serializable>put(
					"firstName", RandomTestUtil.randomString()
				).build(),
				ServiceContextTestUtil.getServiceContext());

			_addModelResourcePermissions(
				systemObjectAction.getName(), objectEntry.getObjectEntryId(),
				_user.getUserId());

			objectEntryResource.
				putByExternalReferenceCodeObjectActionObjectActionName(
					objectEntry.getExternalReferenceCode(),
					systemObjectAction.getName());

			values = _objectEntryLocalService.getValues(
				objectEntry.getObjectEntryId());

			Assert.assertEquals("Jack", MapUtil.getString(values, "firstName"));

			// Delete object entry

			Assert.assertEquals(0, _argumentsList.size());

			_objectEntryLocalService.deleteObjectEntry(objectEntry);

			// On after remove

			_assertWebhookObjectAction(
				"2000-12-25T00:00:00.000Z", "Jack", "White",
				ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE,
				_objectDefinition, "Jack", "White",
				WorkflowConstants.STATUS_APPROVED);

			// Draft

			_objectDefinition.setEnableObjectEntryDraft(true);

			_objectDefinition =
				_objectDefinitionLocalService.updateObjectDefinition(
					_objectDefinition);

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext();

			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);

			objectEntry = _objectEntryLocalService.addObjectEntry(
				0, TestPropsValues.getUserId(),
				_objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"firstName", "John"
				).build(),
				serviceContext);

			_assertWebhookObjectAction(
				null, "John", null,
				ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
				_objectDefinition, null, null, WorkflowConstants.STATUS_DRAFT);

			serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

			_objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
				objectEntry.getObjectEntryFolderId(),
				HashMapBuilder.<String, Serializable>put(
					"firstName", "Peter"
				).build(),
				serviceContext);

			_assertWebhookObjectAction(
				null, "Peter", StringPool.BLANK,
				ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE,
				_objectDefinition, "John", StringPool.BLANK,
				WorkflowConstants.STATUS_APPROVED);

			// Hierarchy, root object entry

			ObjectDefinition objectDefinitionA =
				ObjectDefinitionTestUtil.addCustomObjectDefinition(
					false,
					Collections.singletonList(
						ObjectFieldUtil.createObjectField(
							ObjectFieldConstants.BUSINESS_TYPE_TEXT,
							ObjectFieldConstants.DB_TYPE_STRING, "First Name",
							"firstName")));

			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinitionA.getObjectDefinitionId());

			ObjectDefinition objectDefinitionAA =
				ObjectDefinitionTestUtil.addCustomObjectDefinition(
					ObjectDefinitionTestUtil.getRandomName());

			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinitionAA.getObjectDefinitionId());

			ObjectRelationship objectRelationshipA_AA =
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService, objectDefinitionA,
					objectDefinitionAA);

			ObjectDefinition objectDefinitionAAA =
				ObjectDefinitionTestUtil.addCustomObjectDefinition(
					ObjectDefinitionTestUtil.getRandomName());

			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinitionAAA.getObjectDefinitionId());

			ObjectRelationship objectRelationshipAA_AAA =
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService, objectDefinitionAA,
					objectDefinitionAAA);

			TreeTestUtil.bind(
				_objectRelationshipLocalService,
				Arrays.asList(
					objectRelationshipAA_AAA, objectRelationshipA_AA));

			_addObjectAction(
				objectDefinitionA.getObjectDefinitionId(),
				ObjectActionExecutorConstants.KEY_WEBHOOK,
				ObjectActionTriggerConstants.KEY_ON_AFTER_ROOT_UPDATE,
				UnicodePropertiesBuilder.put(
					"secret", "onafterrootupdate"
				).put(
					"url", "https://onafterrootupdate.com"
				).build());

			ObjectEntry rootObjectEntry =
				_objectEntryLocalService.addObjectEntry(
					0, TestPropsValues.getUserId(),
					objectDefinitionA.getObjectDefinitionId(),
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
					null,
					HashMapBuilder.<String, Serializable>put(
						"firstName", "John"
					).build(),
					ServiceContextTestUtil.getServiceContext());

			// Hierarchy, add object entry in a child node

			ObjectField relationshipObjectField =
				_objectFieldLocalService.getObjectField(
					objectRelationshipA_AA.getObjectFieldId2());

			objectEntry = _objectEntryLocalService.addObjectEntry(
				0, TestPropsValues.getUserId(),
				objectDefinitionAA.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"able", RandomTestUtil.randomString()
				).put(
					relationshipObjectField.getName(),
					rootObjectEntry.getObjectEntryId()
				).build(),
				ServiceContextTestUtil.getServiceContext());

			// Hierarchy, on after root update

			_assertWebhookObjectAction(
				null, "John", null,
				ObjectActionTriggerConstants.KEY_ON_AFTER_ROOT_UPDATE,
				objectDefinitionA, null, null,
				WorkflowConstants.STATUS_APPROVED);

			// Hierarchy, add object entry in a grandchild node

			relationshipObjectField = _objectFieldLocalService.getObjectField(
				objectRelationshipAA_AAA.getObjectFieldId2());

			_objectEntryLocalService.addObjectEntry(
				0, TestPropsValues.getUserId(),
				objectDefinitionAAA.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"able", RandomTestUtil.randomString()
				).put(
					relationshipObjectField.getName(),
					objectEntry.getObjectEntryId()
				).build(),
				ServiceContextTestUtil.getServiceContext());

			// Hierarchy, on after root update

			_assertWebhookObjectAction(
				null, "John", null,
				ObjectActionTriggerConstants.KEY_ON_AFTER_ROOT_UPDATE,
				objectDefinitionA, null, null,
				WorkflowConstants.STATUS_APPROVED);

			_objectEntryLocalService.deleteObjectEntry(
				rootObjectEntry.getObjectEntryId());

			_objectRelationshipLocalService.updateObjectRelationship(
				objectRelationshipA_AA.getExternalReferenceCode(),
				objectRelationshipA_AA.getObjectRelationshipId(), 0,
				objectRelationshipA_AA.getDeletionType(), false,
				objectRelationshipA_AA.getLabelMap(), null);

			_objectRelationshipLocalService.updateObjectRelationship(
				objectRelationshipAA_AAA.getExternalReferenceCode(),
				objectRelationshipAA_AAA.getObjectRelationshipId(), 0,
				objectRelationshipAA_AAA.getDeletionType(), false,
				objectRelationshipAA_AAA.getLabelMap(), null);

			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinitionA);
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinitionAA);
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinitionAAA);
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
			PrincipalThreadLocal.setName(originalName);
		}

		// Delete object actions

		_objectActionLocalService.deleteObjectAction(objectAction1);
		_objectActionLocalService.deleteObjectAction(objectAction2);
		_objectActionLocalService.deleteObjectAction(objectAction3);
		_objectActionLocalService.deleteObjectAction(objectAction4);
		_objectActionLocalService.deleteObjectAction(objectAction5);
		_objectActionLocalService.deleteObjectAction(systemObjectAction);
	}

	@Test
	public void testAddObjectActionWithCircularReference() throws Exception {
		_publishCustomObjectDefinition();

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.put(
			"objectDefinitionId", _objectDefinition.getObjectDefinitionId()
		).put(
			"predefinedValues",
			JSONUtil.putAll(
				JSONUtil.put(
					"inputAsValue", true
				).put(
					"name", "firstName"
				).put(
					"value", RandomTestUtil.randomString()
				)
			).toString()
		).build();

		// When you add a new object entry that belongs to an object definition,
		// update the newly added object entry

		_addObjectAction(
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_UPDATE_OBJECT_ENTRY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD, unicodeProperties,
			false);

		// When you update an object entry that belongs to an object definition,
		// add a new object entry to the object definition

		_addObjectAction(
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_ADD_OBJECT_ENTRY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE, unicodeProperties,
			false);

		// Each call to the method _testAddObjectActionWithCircularReference
		// should increase the expected objects entries count by 2. The only
		// exception is for the 4th call when we inject a broken thread local.

		_testAddObjectActionWithCircularReference(2);
		_testAddObjectActionWithCircularReference(4);
		_testAddObjectActionWithCircularReference(6);

		Object clearObjectEntryIdsMap = ReflectionTestUtil.getAndSetFieldValue(
			ObjectActionThreadLocal.class, "_clearObjectEntryIdsMap",
			new ThreadLocal<Boolean>() {

				@Override
				public Boolean get() {
					return true;
				}

			});

		try {
			_testAddObjectActionWithCircularReference(8);

			Assert.fail();
		}
		catch (StackOverflowError stackOverflowError) {
			Assert.assertNotNull(stackOverflowError);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				ObjectActionThreadLocal.class, "_clearObjectEntryIdsMap",
				clearObjectEntryIdsMap);
		}
	}

	@Test
	public void testAddObjectActionWithSystemObjectDefinition()
		throws Exception {

		// Commerce order system object definition

		ObjectDefinition commerceOrderObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				TestPropsValues.getCompanyId(), CommerceOrder.class.getName());

		Set<String> objectActionTriggerKeys = new HashSet<>();

		ListUtil.isNotEmptyForEach(
			_objectActionTriggerRegistry.getObjectActionTriggers(
				commerceOrderObjectDefinition.getClassName()),
			objectActionTrigger -> objectActionTriggerKeys.add(
				objectActionTrigger.getKey()));

		Assert.assertTrue(
			objectActionTriggerKeys.contains(
				DestinationNames.COMMERCE_ORDER_STATUS));
		Assert.assertTrue(
			objectActionTriggerKeys.contains(
				DestinationNames.COMMERCE_PAYMENT_STATUS));

		// Add object action to update commerce order status to
		// CommerceOrderConstants#ORDER_STATUS_PROCESSING after updating payment
		// status if the old value for order status is 1

		ObjectAction objectAction1 = _objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			commerceOrderObjectDefinition.getObjectDefinitionId(), true,
			"oldValue(\"orderStatus\") == 1", RandomTestUtil.randomString(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_UPDATE_OBJECT_ENTRY,
			DestinationNames.COMMERCE_PAYMENT_STATUS,
			UnicodePropertiesBuilder.put(
				"objectDefinitionId",
				commerceOrderObjectDefinition.getObjectDefinitionId()
			).put(
				"predefinedValues",
				JSONUtil.putAll(
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "orderStatus"
					).put(
						"value", CommerceOrderConstants.ORDER_STATUS_PROCESSING
					)
				).toString()
			).build(),
			false);

		// Add object action to create commerce order after updating order
		// status to CommerceOrderConstants#ORDER_STATUS_PROCESSING

		ObjectAction objectAction2 = _objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			commerceOrderObjectDefinition.getObjectDefinitionId(), true,
			"orderStatus == 10", RandomTestUtil.randomString(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_ADD_OBJECT_ENTRY,
			DestinationNames.COMMERCE_ORDER_STATUS,
			UnicodePropertiesBuilder.put(
				"objectDefinitionId",
				commerceOrderObjectDefinition.getObjectDefinitionId()
			).put(
				"predefinedValues",
				JSONUtil.putAll(
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "accountId"
					).put(
						"value", _accountEntry.getAccountEntryId()
					),
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "channelId"
					).put(
						"value", _commerceChannel.getCommerceChannelId()
					),
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "currencyCode"
					).put(
						"value", _commerceCurrency.getCode()
					),
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "externalReferenceCode"
					).put(
						"value", "newCommerceOrder"
					),
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "orderStatus"
					).put(
						"value", CommerceOrderConstants.ORDER_STATUS_OPEN
					),
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "paymentStatus"
					).put(
						"value", CommerceOrderPaymentConstants.STATUS_PENDING
					),
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "shippingAmount"
					).put(
						"value", "10"
					),
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "taxAmount"
					).put(
						"value", "10"
					),
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "total"
					).put(
						"value", "10"
					)
				).toString()
			).build(),
			false);

		CommerceOrder commerceOrder1 = CommerceTestUtil.addB2CCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_commerceCurrency);

		commerceOrder1 = _commerceOrderEngine.checkoutCommerceOrder(
			commerceOrder1, _user.getUserId());

		Assert.assertEquals(
			CommerceOrderConstants.ORDER_STATUS_PENDING,
			commerceOrder1.getOrderStatus());

		_commerceOrderLocalService.updatePaymentStatus(
			commerceOrder1.getUserId(), commerceOrder1.getCommerceOrderId(),
			CommerceOrderPaymentConstants.STATUS_COMPLETED);

		commerceOrder1 = _commerceOrderLocalService.getCommerceOrder(
			commerceOrder1.getCommerceOrderId());

		Assert.assertEquals(
			CommerceOrderConstants.ORDER_STATUS_PROCESSING,
			commerceOrder1.getOrderStatus());

		CommerceOrder commerceOrder2 =
			_commerceOrderLocalService.
				fetchCommerceOrderByExternalReferenceCode(
					"newCommerceOrder", TestPropsValues.getCompanyId());

		Assert.assertNotNull(commerceOrder2);

		Assert.assertEquals(
			_accountEntry.getAccountEntryId(),
			commerceOrder2.getCommerceAccountId());
		Assert.assertEquals(
			_commerceCurrency.getCode(),
			commerceOrder2.getCommerceCurrencyCode());
		Assert.assertEquals(
			CommerceOrderConstants.ORDER_STATUS_OPEN,
			commerceOrder2.getOrderStatus());

		// Commerce product definition system object definition

		ObjectDefinition commerceProductObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				TestPropsValues.getCompanyId(), CPDefinition.class.getName());

		_publishCustomObjectDefinition();

		ObjectAction objectAction3 = _addObjectAction(
			commerceProductObjectDefinition.getObjectDefinitionId(),
			ObjectActionExecutorConstants.KEY_ADD_OBJECT_ENTRY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE,
			UnicodePropertiesBuilder.put(
				"objectDefinitionId", _objectDefinition.getObjectDefinitionId()
			).put(
				"predefinedValues",
				JSONUtil.putAll(
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "firstName"
					).put(
						"value", RandomTestUtil.randomString()
					)
				).toString()
			).build());

		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			_group.getGroupId());

		Date displayDate = cpDefinition.getDisplayDate();
		Date expirationDate = cpDefinition.getExpirationDate();

		_cpDefinitionLocalService.updateCPDefinition(
			cpDefinition.getCPDefinitionId(), cpDefinition.getNameMap(),
			cpDefinition.getShortDescriptionMap(),
			cpDefinition.getDescriptionMap(), cpDefinition.getUrlTitleMap(),
			cpDefinition.getMetaTitleMap(),
			cpDefinition.getMetaDescriptionMap(),
			cpDefinition.getMetaKeywordsMap(),
			cpDefinition.isIgnoreSKUCombinations(), true, true, true,
			cpDefinition.getShippingExtraPrice(), cpDefinition.getWidth(),
			cpDefinition.getHeight(), cpDefinition.getDepth(),
			cpDefinition.getWeight(), cpDefinition.getCPTaxCategoryId(),
			cpDefinition.isTaxExempt(), cpDefinition.isTelcoOrElectronics(),
			cpDefinition.getDDMStructureKey(), cpDefinition.isPublished(),
			displayDate.getMonth(), displayDate.getDate(),
			displayDate.getYear(), displayDate.getHours(),
			displayDate.getMinutes(), expirationDate.getMonth(),
			expirationDate.getDate(), expirationDate.getYear(),
			expirationDate.getHours(), expirationDate.getMinutes(), true,
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			1,
			_objectEntryLocalService.getObjectEntriesCount(
				0, _objectDefinition.getObjectDefinitionId()));

		// Organization system object definition

		ObjectDefinition organizationObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				TestPropsValues.getCompanyId(), Organization.class.getName());

		ObjectField objectField1 = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).indexed(
				true
			).indexedAsKeyword(
				true
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				StringUtil.randomId()
			).objectDefinitionId(
				organizationObjectDefinition.getObjectDefinitionId()
			).build());

		String comment1 = RandomTestUtil.randomString();
		String objectFieldValue1 = RandomTestUtil.randomString();
		String organizationName1 = RandomTestUtil.randomString();

		ObjectAction objectAction4 = _addObjectAction(
			organizationObjectDefinition.getObjectDefinitionId(),
			ObjectActionExecutorConstants.KEY_ADD_OBJECT_ENTRY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
			UnicodePropertiesBuilder.put(
				"objectDefinitionId",
				organizationObjectDefinition.getObjectDefinitionId()
			).put(
				"predefinedValues",
				JSONUtil.putAll(
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", objectField1.getName()
					).put(
						"value", objectFieldValue1
					),
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "comment"
					).put(
						"value", comment1
					),
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "name"
					).put(
						"value", organizationName1
					)
				).toString()
			).build());

		String comment2 = RandomTestUtil.randomString();
		String objectFieldValue2 = RandomTestUtil.randomString();
		String organizationName2 = RandomTestUtil.randomString();

		ObjectAction objectAction5 = _addObjectAction(
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_ADD_OBJECT_ENTRY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
			UnicodePropertiesBuilder.put(
				"objectDefinitionId",
				organizationObjectDefinition.getObjectDefinitionId()
			).put(
				"predefinedValues",
				JSONUtil.putAll(
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", objectField1.getName()
					).put(
						"value", objectFieldValue2
					),
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "comment"
					).put(
						"value", comment2
					),
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "name"
					).put(
						"value", organizationName2
					)
				).toString()
			).build(),
			false);

		OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		_assertOrganization(
			comment1, organizationName1, organizationObjectDefinition,
			objectField1, objectFieldValue1);

		_objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"firstName", "John"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_assertOrganization(
			comment1, organizationName1, organizationObjectDefinition,
			objectField1, objectFieldValue1);
		_assertOrganization(
			comment2, organizationName2, organizationObjectDefinition,
			objectField1, objectFieldValue2);

		// User system object definition

		ObjectDefinition userObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				TestPropsValues.getCompanyId(), User.class.getName());

		ObjectField objectField2 = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).indexed(
				true
			).indexedAsKeyword(
				true
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				StringUtil.randomId()
			).objectDefinitionId(
				userObjectDefinition.getObjectDefinitionId()
			).build());
		ObjectField objectField3 = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).indexed(
				true
			).indexedAsKeyword(
				true
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				StringUtil.randomId()
			).objectDefinitionId(
				userObjectDefinition.getObjectDefinitionId()
			).build());

		// Add object action to create user after adding an object entry

		ObjectAction objectAction6 = _addObjectAction(
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_ADD_OBJECT_ENTRY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
			UnicodePropertiesBuilder.put(
				"objectDefinitionId",
				userObjectDefinition.getObjectDefinitionId()
			).put(
				"predefinedValues",
				JSONUtil.putAll(
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", objectField2.getName()
					).put(
						"value", "John"
					),
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "alternateName"
					).put(
						"value", "ScreenName"
					),
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "emailAddress"
					).put(
						"value", "email@liferay.com"
					),
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "familyName"
					).put(
						"value", "LastName"
					),
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "givenName"
					).put(
						"value", "FirstName"
					)
				).toString()
			).build(),
			false);

		// Add object action to update user after adding a user

		ObjectAction objectAction7 = _addObjectAction(
			userObjectDefinition.getObjectDefinitionId(),
			ObjectActionExecutorConstants.KEY_UPDATE_OBJECT_ENTRY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
			UnicodePropertiesBuilder.put(
				"objectDefinitionId",
				userObjectDefinition.getObjectDefinitionId()
			).put(
				"predefinedValues",
				JSONUtil.putAll(
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", objectField3.getName()
					).put(
						"value", "Peter"
					),
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "additionalName"
					).put(
						"value", "MiddleName"
					)
				).toString()
			).build());

		_objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"firstName", "John"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		User user = _userLocalService.getUserByScreenName(
			TestPropsValues.getCompanyId(), "ScreenName");

		Assert.assertEquals("email@liferay.com", user.getEmailAddress());
		Assert.assertEquals("FirstName", user.getFirstName());
		Assert.assertEquals("LastName", user.getLastName());
		Assert.assertEquals("MiddleName", user.getMiddleName());

		Map<String, Serializable> values =
			_objectEntryLocalService.
				getExtensionDynamicObjectDefinitionTableValues(
					userObjectDefinition, user.getUserId());

		Assert.assertEquals("John", values.get(objectField2.getName()));
		Assert.assertEquals("Peter", values.get(objectField3.getName()));

		_userLocalService.deleteUser(user);

		_objectActionLocalService.deleteObjectAction(objectAction4);
		_objectActionLocalService.deleteObjectAction(objectAction5);
		_objectActionLocalService.deleteObjectAction(objectAction6);
		_objectActionLocalService.deleteObjectAction(objectAction7);

		// Add object action to execute Groovy after adding a user

		objectAction4 = _addObjectAction(
			userObjectDefinition.getObjectDefinitionId(),
			ObjectActionExecutorConstants.KEY_GROOVY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
			UnicodePropertiesBuilder.put(
				"script", "println \"Hello World 1\""
			).build());

		// Add object action to execute Groovy after updating a user

		objectAction5 = _addObjectAction(
			userObjectDefinition.getObjectDefinitionId(),
			ObjectActionExecutorConstants.KEY_GROOVY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE,
			UnicodePropertiesBuilder.put(
				"script", "println \"Hello World 2\""
			).build());

		// While adding a user, the user is updated and it must not trigger
		// object actions

		user = UserTestUtil.addUser();

		Assert.assertEquals(1, _argumentsList.size());

		Object[] arguments = _argumentsList.poll();

		Map<String, Object> inputObjects = (Map<String, Object>)arguments[0];

		Assert.assertEquals(
			user.getUserId(), GetterUtil.getLong(inputObjects.get("id")));

		Assert.assertEquals(Collections.emptySet(), arguments[1]);
		Assert.assertEquals("println \"Hello World 1\"", arguments[2]);

		_objectActionLocalService.deleteObjectAction(objectAction1);
		_objectActionLocalService.deleteObjectAction(objectAction2);
		_objectActionLocalService.deleteObjectAction(objectAction3);
		_objectActionLocalService.deleteObjectAction(objectAction4);
		_objectActionLocalService.deleteObjectAction(objectAction5);
		_objectFieldLocalService.deleteObjectField(objectField1);
		_objectFieldLocalService.deleteObjectField(objectField2);
		_objectFieldLocalService.deleteObjectField(objectField3);
	}

	@Test
	public void testAddObjectActionWithUsePreferredLanguageForGuestsParameter()
		throws Exception {

		Assert.assertNotNull(
			_addObjectAction(
				RandomTestUtil.randomString(),
				ObjectActionExecutorConstants.KEY_NOTIFICATION,
				ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
				UnicodePropertiesBuilder.put(
					"notificationTemplateExternalReferenceCode",
					RandomTestUtil.randomString()
				).put(
					"type", NotificationConstants.TYPE_EMAIL
				).put(
					"usePreferredLanguageForGuests", "true"
				).build(),
				false));
		AssertUtils.assertFailure(
			ObjectActionParametersException.class,
			"The parameter \"usePreferredLanguageForGuests\" is invalid for " +
				"this object action",
			() -> _addObjectAction(
				RandomTestUtil.randomString(),
				ObjectActionExecutorConstants.KEY_NOTIFICATION,
				ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
				UnicodePropertiesBuilder.put(
					"notificationTemplateExternalReferenceCode",
					RandomTestUtil.randomString()
				).put(
					"type", NotificationConstants.TYPE_USER_NOTIFICATION
				).put(
					"usePreferredLanguageForGuests", "true"
				).build(),
				false));
		AssertUtils.assertFailure(
			ObjectActionParametersException.class,
			"The parameter \"usePreferredLanguageForGuests\" is invalid for " +
				"this object action",
			() -> _addObjectAction(
				RandomTestUtil.randomString(),
				ObjectActionExecutorConstants.KEY_NOTIFICATION,
				ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE,
				UnicodePropertiesBuilder.put(
					"notificationTemplateExternalReferenceCode",
					RandomTestUtil.randomString()
				).put(
					"type", NotificationConstants.TYPE_EMAIL
				).put(
					"usePreferredLanguageForGuests", "true"
				).build(),
				false));
		Assert.assertNotNull(
			_addObjectAction(
				RandomTestUtil.randomString(),
				ObjectActionExecutorConstants.KEY_NOTIFICATION,
				ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE,
				UnicodePropertiesBuilder.put(
					"notificationTemplateExternalReferenceCode",
					RandomTestUtil.randomString()
				).put(
					"type", NotificationConstants.TYPE_EMAIL
				).put(
					"usePreferredLanguageForGuests", "true"
				).build(),
				false));
		AssertUtils.assertFailure(
			ObjectActionParametersException.class,
			"The parameter \"usePreferredLanguageForGuests\" is invalid for " +
				"this object action",
			() -> _addObjectAction(
				RandomTestUtil.randomString(),
				ObjectActionExecutorConstants.KEY_WEBHOOK,
				ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
				UnicodePropertiesBuilder.put(
					"notificationTemplateExternalReferenceCode",
					RandomTestUtil.randomString()
				).put(
					"type", NotificationConstants.TYPE_EMAIL
				).put(
					"usePreferredLanguageForGuests", "true"
				).build(),
				false));
		AssertUtils.assertFailure(
			ObjectActionParametersException.class,
			"The parameter \"usePreferredLanguageForGuests\" is invalid for " +
				"this object action",
			() -> _addObjectAction(
				RandomTestUtil.randomString(),
				ObjectActionExecutorConstants.KEY_WEBHOOK,
				ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE,
				UnicodePropertiesBuilder.put(
					"notificationTemplateExternalReferenceCode",
					RandomTestUtil.randomString()
				).put(
					"type", NotificationConstants.TYPE_EMAIL
				).put(
					"usePreferredLanguageForGuests", "true"
				).build(),
				false));

		ObjectAction objectAction = _addObjectAction(
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_NOTIFICATION,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
			UnicodePropertiesBuilder.put(
				"notificationTemplateExternalReferenceCode",
				RandomTestUtil.randomString()
			).put(
				"type", NotificationConstants.TYPE_EMAIL
			).build(),
			false);

		UnicodeProperties parametersUnicodeProperties =
			objectAction.getParametersUnicodeProperties();

		Assert.assertTrue(
			GetterUtil.getBoolean(
				parametersUnicodeProperties.get(
					"usePreferredLanguageForGuests")));
	}

	@FeatureFlags(
		featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-32050")}
	)
	@Test
	public void testAddOrUpdateSubscriptionObjectActions() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		objectDefinition.setEnableObjectEntrySubscription(true);

		objectDefinition = _objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);

		_objectActionLocalService.addOrUpdateSubscriptionObjectActions(
			objectDefinition);

		List<ObjectAction> objectActions =
			_objectActionLocalService.getObjectActions(
				objectDefinition.getObjectDefinitionId());

		Assert.assertEquals(objectActions.size(), 3, objectActions.size());

		_assertObjectAction(
			true, "SubscriptionAdded",
			"L_SUBSCRIPTION_ADDED_NOTIFICATION_TEMPLATE", objectActions.get(0));
		_assertObjectAction(
			true, "SubscriptionExpired",
			"L_SUBSCRIPTION_EXPIRED_NOTIFICATION_TEMPLATE",
			objectActions.get(1));
		_assertObjectAction(
			true, "SubscriptionUpdated",
			"L_SUBSCRIPTION_UPDATED_NOTIFICATION_TEMPLATE",
			objectActions.get(2));

		_objectActionLocalService.deleteObjectAction(objectActions.get(2));

		objectDefinition.setEnableObjectEntrySubscription(false);

		objectDefinition = _objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);

		_objectActionLocalService.addOrUpdateSubscriptionObjectActions(
			objectDefinition);

		objectActions = _objectActionLocalService.getObjectActions(
			objectDefinition.getObjectDefinitionId());

		Assert.assertEquals(objectActions.size(), 2, objectActions.size());

		_assertObjectAction(false, "SubscriptionAdded", objectActions.get(0));
		_assertObjectAction(false, "SubscriptionExpired", objectActions.get(1));

		objectDefinition.setEnableObjectEntrySubscription(true);

		objectDefinition = _objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);

		_objectActionLocalService.addOrUpdateSubscriptionObjectActions(
			objectDefinition);

		objectActions = _objectActionLocalService.getObjectActions(
			objectDefinition.getObjectDefinitionId());

		Assert.assertEquals(objectActions.size(), 3, objectActions.size());

		_assertObjectAction(true, "SubscriptionAdded", objectActions.get(0));
		_assertObjectAction(true, "SubscriptionExpired", objectActions.get(1));
		_assertObjectAction(true, "SubscriptionUpdated", objectActions.get(2));

		_objectDefinitionLocalService.deleteObjectDefinition(
			objectDefinition.getObjectDefinitionId());
	}

	@Test
	public void testConcurrentObjectActions() throws Exception {
		_addObjectAction(
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_GROOVY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
			UnicodePropertiesBuilder.put(
				"script", "println \"Hello World\""
			).build(),
			false);

		_objectDefinition = _publishCustomObjectDefinition();

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		String originalName = PrincipalThreadLocal.getName();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(_user));
			PrincipalThreadLocal.setName(_user.getUserId());

			Thread thread1 = new Thread(_getAddObjectEntryFutureTask("John"));
			Thread thread2 = new Thread(_getAddObjectEntryFutureTask("Peter"));

			thread1.start();
			thread2.start();

			thread1.join();
			thread2.join();

			Assert.assertEquals(
				2,
				_objectEntryLocalService.getObjectEntriesCount(
					0, _objectDefinition.getObjectDefinitionId()));

			Assert.assertEquals(2, _argumentsList.size());

			Object[] arguments = _argumentsList.poll();

			Set<String> firstNames = SetUtil.fromArray(
				new String[] {"John", "Peter"});

			Map<String, Object> inputObjects =
				(Map<String, Object>)arguments[0];

			Assert.assertTrue(firstNames.remove(inputObjects.get("firstName")));

			arguments = _argumentsList.poll();

			inputObjects = (Map<String, Object>)arguments[0];

			Assert.assertTrue(firstNames.remove(inputObjects.get("firstName")));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
			PrincipalThreadLocal.setName(originalName);
		}
	}

	@Test
	public void testDeleteObjectAction() throws Exception {
		ObjectAction systemObjectAction =
			_objectActionLocalService.addObjectAction(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_objectDefinition.getObjectDefinitionId(), true,
				"equals(firstName, \"John\")", "Able Description",
				LocalizedMapUtil.getLocalizedMap("Able Error Message"),
				LocalizedMapUtil.getLocalizedMap("Able Label"), "Able",
				ObjectActionExecutorConstants.KEY_WEBHOOK,
				ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
				UnicodePropertiesBuilder.put(
					"secret", "0123456789"
				).put(
					"url", "https://onafteradd.com"
				).build(),
				true);

		AssertUtils.assertFailure(
			ObjectActionSystemException.class, false,
			"Only allowed bundles can delete system object actions",
			() -> _objectActionLocalService.deleteObjectAction(
				systemObjectAction));

		_objectActionLocalService.deleteObjectAction(systemObjectAction);
	}

	@Test
	public void testExecuteObjectActionAfterObjectEntryStatusUpdate()
		throws Exception {

		_objectDefinition = _publishCustomObjectDefinition();

		_addObjectAction(
			_objectDefinition.getObjectDefinitionId(),
			ObjectActionExecutorConstants.KEY_WEBHOOK,
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE,
			UnicodePropertiesBuilder.put(
				"secret", "onafterupdate"
			).put(
				"url", "https://onafterupdate.com"
			).build());

		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(), 0,
			_objectDefinition.getClassName(), 0, 0, "Single Approver", 1);

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"firstName", "John"
			).put(
				"lastName", "Smith"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, objectEntry.getStatus());

		List<WorkflowTask> workflowTasks =
			_workflowTaskManager.getWorkflowTasksBySubmittingUser(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				false, 0, 1, null);

		WorkflowTask workflowTask = workflowTasks.get(0);

		_workflowTaskManager.assignWorkflowTaskToUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			workflowTask.getWorkflowTaskId(), TestPropsValues.getUserId(),
			StringPool.BLANK, null, null);

		_workflowTaskManager.completeWorkflowTask(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			workflowTask.getWorkflowTaskId(), Constants.APPROVE,
			StringPool.BLANK, null);

		objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntry.getObjectEntryId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, objectEntry.getStatus());

		_assertWebhookObjectAction(
			null, "John", "Smith",
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE, _objectDefinition,
			"John", "Smith", WorkflowConstants.STATUS_APPROVED);
	}

	@Test
	public void testExecuteObjectActionAfterUserLogin() throws Exception {
		ObjectDefinition userObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				TestPropsValues.getCompanyId(), User.class.getName());

		ObjectAction objectAction = _addObjectAction(
			userObjectDefinition.getObjectDefinitionId(),
			ObjectActionExecutorConstants.KEY_WEBHOOK,
			ObjectActionTriggerConstants.KEY_ON_AFTER_LOGIN,
			UnicodePropertiesBuilder.put(
				"secret", "onafterlogin"
			).put(
				"url", "https://onafterlogin.com"
			).build());

		Assert.assertEquals(0, _argumentsList.size());

		int authResult = _userLocalService.authenticateByUserId(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			TestPropsValues.USER_PASSWORD, null, null, null);

		Assert.assertEquals(Authenticator.SUCCESS, authResult);

		Assert.assertEquals(1, _argumentsList.size());

		Object[] arguments = _argumentsList.poll();

		Http.Options options = (Http.Options)arguments[0];

		Http.Body body = options.getBody();

		JSONObject payloadJSONObject = _jsonFactory.createJSONObject(
			body.getContent());

		Assert.assertEquals(
			ObjectActionTriggerConstants.KEY_ON_AFTER_LOGIN,
			payloadJSONObject.getString("objectActionTriggerKey"));
		Assert.assertEquals(
			userObjectDefinition.getObjectDefinitionId(),
			payloadJSONObject.getLong("objectDefinitionId"));

		Assert.assertTrue(payloadJSONObject.has("modelUser"));

		User user = _userLocalService.getUser(TestPropsValues.getUserId());

		Assert.assertEquals(
			user.getExternalReferenceCode(),
			JSONUtil.getValue(
				payloadJSONObject, "JSONObject/modelUser",
				"Object/externalReferenceCode"));
		Assert.assertEquals(
			user.getFirstName(),
			JSONUtil.getValue(
				payloadJSONObject, "JSONObject/modelUser", "Object/firstName"));
		Assert.assertEquals(
			user.getLastName(),
			JSONUtil.getValue(
				payloadJSONObject, "JSONObject/modelUser", "Object/lastName"));

		authResult = _userLocalService.authenticateByUserId(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(), null, null, null);

		Assert.assertEquals(Authenticator.FAILURE, authResult);

		Assert.assertEquals(0, _argumentsList.size());

		_objectActionLocalService.deleteObjectAction(objectAction);
	}

	@Test
	public void testExecuteObjectActionMultipleTimesInTheSameThread()
		throws Exception {

		_testExecuteObjectActionMultipleTimesInTheSameThreadWithACustomObjectDefinition();
		_testExecuteObjectActionMultipleTimesInTheSameThreadWithASystemObjectDefinition();
	}

	@FeatureFlag("LPD-6233")
	@Test
	public void testExecuteObjectActionWithAssigneeObjectField()
		throws Exception {

		ObjectFieldUtil.addCustomObjectField(
			new AssigneeObjectFieldBuilder(
			).labelMap(
				RandomTestUtil.randomLocaleStringMap()
			).name(
				"assignee"
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).userId(
				TestPropsValues.getUserId()
			).build());

		_objectDefinition = _publishCustomObjectDefinition();

		ObjectAction objectAction = _objectActionLocalService.fetchObjectAction(
			_objectDefinition.getObjectDefinitionId(),
			ObjectActionNameConstants.NAME_ASSIGN_TO_ME);
		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"firstName", "John"
			).build());

		User user = UserTestUtil.addUser();

		_addModelResourcePermissions(
			objectAction.getName(), objectEntry.getObjectEntryId(),
			user.getUserId());

		ObjectEntryResource objectEntryResource = _getObjectEntryResource(user);

		objectEntryResource.putObjectEntryObjectActionObjectActionName(
			objectEntry.getObjectEntryId(), objectAction.getName());

		objectEntry = _objectEntryLocalService.fetchObjectEntry(
			objectEntry.getObjectEntryId());

		Map<String, Serializable> values = objectEntry.getValues();

		Assert.assertEquals(
			user.getUserId(),
			MapUtil.getLong(
				(Map<String, Long>)values.get("assignee"), "classPK"));
	}

	@Test
	public void testExecuteObjectActionWithConditionExpressionInCustomObjectDefinition()
		throws Exception {

		_publishCustomObjectDefinition();

		ObjectAction objectAction1 = _addObjectAction(
			"equals(firstName, \"João\")",
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_GROOVY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE,
			UnicodePropertiesBuilder.put(
				"script", "println \"Hello World\""
			).build(),
			false);

		// Add object entry with unsatisfied condition

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"firstName", "John"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_objectEntryLocalService.deleteObjectEntry(objectEntry);

		Assert.assertNull(_argumentsList.poll());

		// Add object entry with satisfied condition

		objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"firstName", "João"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		objectEntry = _objectEntryLocalService.deleteObjectEntry(objectEntry);

		_assertGroovyObjectActionExecutorArguments("João", objectEntry);

		_objectActionLocalService.deleteObjectAction(objectAction1);

		ObjectAction objectAction2 = _addObjectAction(
			"currentUserId == creator",
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_UPDATE_OBJECT_ENTRY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE,
			UnicodePropertiesBuilder.put(
				"objectDefinitionId", _objectDefinition.getObjectDefinitionId()
			).put(
				"predefinedValues",
				JSONUtil.putAll(
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "firstName"
					).put(
						"value", "John"
					)
				).toString()
			).build(),
			false);
		ObjectAction objectAction3 = _addObjectAction(
			"currentUserId != creator",
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_UPDATE_OBJECT_ENTRY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE,
			UnicodePropertiesBuilder.put(
				"objectDefinitionId", _objectDefinition.getObjectDefinitionId()
			).put(
				"predefinedValues",
				JSONUtil.putAll(
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "firstName"
					).put(
						"value", "Peter"
					)
				).toString()
			).build(),
			false);

		objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"firstName", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"firstName", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			"John",
			MapUtil.getString(
				_objectEntryLocalService.getValues(
					objectEntry.getObjectEntryId()),
				"firstName"));

		_objectActionLocalService.deleteObjectAction(objectAction2);
		_objectActionLocalService.deleteObjectAction(objectAction3);

		ObjectAction objectAction4 = _addObjectAction(
			"oldValue(\"firstName\") == \"Paulo\"",
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_ADD_OBJECT_ENTRY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE,
			UnicodePropertiesBuilder.put(
				"objectDefinitionId", _objectDefinition.getObjectDefinitionId()
			).put(
				"predefinedValues",
				JSONUtil.putAll(
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "firstName"
					).put(
						"value", RandomTestUtil.randomString()
					)
				).toString()
			).build(),
			false);

		objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"firstName", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		int objectEntriesCount = _objectEntryLocalService.getObjectEntriesCount(
			0, _objectDefinition.getObjectDefinitionId());

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"firstName", "Paulo"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			objectEntriesCount,
			_objectEntryLocalService.getObjectEntriesCount(
				0, _objectDefinition.getObjectDefinitionId()));

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"firstName", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			objectEntriesCount + 1,
			_objectEntryLocalService.getObjectEntriesCount(
				0, _objectDefinition.getObjectDefinitionId()));

		_objectActionLocalService.deleteObjectAction(objectAction4);
	}

	@Test
	public void testExecuteObjectActionWithConditionExpressionInSystemObjectDefinition()
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchSystemObjectDefinition(
				TestPropsValues.getCompanyId(), "User");

		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"name"
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).userId(
				TestPropsValues.getUserId()
			).build());

		_objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), true,
			"oldValue(\"name\") == \"Paul\"", RandomTestUtil.randomString(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_GROOVY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE,
			new UnicodeProperties(), false);

		Map<String, Object> values = HashMapBuilder.<String, Object>put(
			"alternateName", RandomTestUtil.randomString()
		).put(
			"emailAddress", RandomTestUtil.randomString() + "@liferay.com"
		).put(
			"familyName", RandomTestUtil.randomString()
		).put(
			"givenName", RandomTestUtil.randomString()
		).build();

		SystemObjectDefinitionManager systemObjectDefinitionManager =
			_systemObjectDefinitionManagerRegistry.
				getSystemObjectDefinitionManager("User");

		long userId = systemObjectDefinitionManager.addBaseModel(
			false, TestPropsValues.getUser(),
			HashMapBuilder.putAll(
				values
			).put(
				"name", "Paul"
			).build());

		Assert.assertNull(_argumentsList.poll());

		systemObjectDefinitionManager.updateBaseModel(
			userId, TestPropsValues.getUser(),
			HashMapBuilder.putAll(
				values
			).put(
				"name", RandomTestUtil.randomString()
			).build());

		Assert.assertNotNull(_argumentsList.poll());

		_objectFieldLocalService.deleteObjectField(objectField);
	}

	@Test
	public void testExecuteObjectActionWithGroovy() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_objectActionExecutorRegistry.getObjectActionExecutor(
				0, ObjectActionExecutorConstants.KEY_GROOVY),
			"_objectScriptingExecutor", _originalObjectScriptingExecutor);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"GROOVY_OBJECT_ACTION", LoggerTestUtil.INFO)) {

			ObjectDefinition objectDefinition =
				ObjectDefinitionTestUtil.publishObjectDefinition(
					Collections.singletonList(
						new TextObjectFieldBuilder(
						).labelMap(
							LocalizedMapUtil.getLocalizedMap(
								RandomTestUtil.randomString())
						).name(
							"restContextPath"
						).build()));

			Class<?> clazz = getClass();

			_addObjectAction(
				objectDefinition.getObjectDefinitionId(),
				ObjectActionExecutorConstants.KEY_GROOVY,
				ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"script",
					StringUtil.read(
						clazz,
						StringBundler.concat(
							"dependencies/", clazz.getSimpleName(),
							StringPool.PERIOD, testName.getMethodName(),
							".groovy"))
				).build());

			ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
				0, objectDefinition.getObjectDefinitionId(),
				Collections.singletonMap(
					"restContextPath", objectDefinition.getRESTContextPath()));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Object entry ID " + objectEntry.getObjectEntryId(),
				logEntry.getMessage());

			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}
	}

	@Test
	public void testExecuteObjectActionWithUnmodifiableSystemObjectDefinition()
		throws Exception {

		ObjectDefinition accountEntryObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				TestPropsValues.getCompanyId(), AccountEntry.class.getName());

		ObjectAction objectAction = _addObjectAction(
			accountEntryObjectDefinition.getObjectDefinitionId(),
			ObjectActionExecutorConstants.KEY_WEBHOOK,
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE,
			UnicodePropertiesBuilder.put(
				"secret", "onafterupdate"
			).put(
				"url", "https://onafterupdate.com"
			).build());

		String expandoColumnName = "A" + RandomTestUtil.randomString();
		String expandoValue1 = RandomTestUtil.randomString();
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setExpandoBridgeAttributes(
			HashMapBuilder.<String, Serializable>put(
				() -> {
					ExpandoColumn expandoColumn = ExpandoTestUtil.addColumn(
						ExpandoTestUtil.addTable(
							PortalUtil.getClassNameId(AccountEntry.class),
							ExpandoTableConstants.DEFAULT_TABLE_NAME),
						expandoColumnName, ExpandoColumnConstants.STRING);

					return expandoColumn.getName();
				},
				expandoValue1
			).build());

		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, TestPropsValues.getUserId(), 0L,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, null, RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		String expandoValue2 = RandomTestUtil.randomString();
		serviceContext = ServiceContextTestUtil.getServiceContext();

		serviceContext.setExpandoBridgeAttributes(
			HashMapBuilder.<String, Serializable>put(
				expandoColumnName, expandoValue2
			).build());

		_accountEntryLocalService.updateAccountEntry(
			accountEntry.getExternalReferenceCode(),
			accountEntry.getAccountEntryId(), 0, accountEntry.getName(),
			accountEntry.getDescription(), false, null,
			accountEntry.getEmailAddress(), null, accountEntry.getTaxIdNumber(),
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		Assert.assertEquals(1, _argumentsList.size());

		Object[] arguments = _argumentsList.poll();

		Http.Options options = (Http.Options)arguments[0];

		Http.Body body = options.getBody();

		JSONObject payloadJSONObject = _jsonFactory.createJSONObject(
			body.getContent());

		Assert.assertEquals(
			expandoValue1,
			JSONUtil.getValue(
				payloadJSONObject, "JSONObject/originalDTOAccount",
				"JSONArray/customFields", "JSONObject/0",
				"JSONObject/customValue", "Object/data"));
		Assert.assertEquals(
			expandoValue2,
			JSONUtil.getValue(
				payloadJSONObject, "JSONObject/modelDTOAccount",
				"JSONArray/customFields", "JSONObject/0",
				"JSONObject/customValue", "Object/data"));

		_objectActionLocalService.deleteObjectAction(objectAction);
	}

	@Test
	public void testExecuteObjectActionWithUsePreferredLanguageForGuestsParameter()
		throws Exception {

		// Use default language for guest users

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"a" + RandomTestUtil.randomString()
					).build()));

		NotificationTemplate notificationTemplate =
			_notificationTemplateLocalService.addNotificationTemplate(
				NotificationTemplateUtil.createNotificationContext(
					TestPropsValues.getUser(),
					objectDefinition.getObjectDefinitionId(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					NotificationTemplateConstants.EDITOR_TYPE_RICH_TEXT,
					Arrays.asList(
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.NAME_FROM,
								_user.getEmailAddress()),
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.
									NAME_FROM_NAME,
								LocalizedMapUtil.getLocalizedMap(
									RandomTestUtil.randomString())),
						NotificationRecipientSettingUtil.
							createNotificationRecipientSetting(
								NotificationRecipientSettingConstants.NAME_TO,
								_user.getEmailAddress())),
					LocalizationUtil.updateLocalization(
						HashMapBuilder.put(
							LocaleUtil.BRAZIL, "Assunto"
						).put(
							LocaleUtil.US, "Subject"
						).build(),
						null, "Subject", "en_US"),
					NotificationConstants.TYPE_EMAIL, Collections.emptyList()));

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.put(
			"notificationTemplateId",
			String.valueOf(notificationTemplate.getNotificationTemplateId())
		).put(
			"usePreferredLanguageForGuests", "false"
		).build();

		ObjectAction objectAction = _addObjectAction(
			objectDefinition.getObjectDefinitionId(),
			ObjectActionExecutorConstants.KEY_NOTIFICATION,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD, unicodeProperties);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setLanguageId(LocaleUtil.BRAZIL.toLanguageTag());

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.GUEST);

		User guestUser = _userLocalService.getGuestUser(
			TestPropsValues.getCompanyId());

		_resourcePermissionLocalService.addResourcePermission(
			guestUser.getCompanyId(), objectDefinition.getResourceName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(guestUser.getCompanyId()), role.getRoleId(),
			ObjectActionKeys.ADD_OBJECT_ENTRY);

		_objectEntryLocalService.addObjectEntry(
			0, guestUser.getUserId(), objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, Collections.emptyMap(), serviceContext);

		_assertNotificationQueueEntrySubject("Subject");

		User user = UserTestUtil.addUser();

		_userLocalService.updateLanguageId(
			user.getUserId(), LocaleUtil.BRAZIL.toLanguageTag());

		_objectEntryLocalService.addObjectEntry(
			0, user.getUserId(), objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, Collections.emptyMap(), serviceContext);

		_assertNotificationQueueEntrySubject("Assunto");

		// Use preferred language for guest users

		unicodeProperties.put("usePreferredLanguageForGuests", "true");

		_objectActionLocalService.updateObjectAction(
			objectAction.getExternalReferenceCode(),
			objectAction.getObjectActionId(), true,
			objectAction.getConditionExpression(), StringPool.BLANK,
			objectAction.getErrorMessageMap(), objectAction.getLabelMap(),
			objectAction.getName(), objectAction.getObjectActionExecutorKey(),
			objectAction.getObjectActionTriggerKey(), unicodeProperties);

		_objectEntryLocalService.addObjectEntry(
			0, guestUser.getUserId(), objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, Collections.emptyMap(), serviceContext);

		_assertNotificationQueueEntrySubject("Assunto");

		_userLocalService.updateLanguageId(
			user.getUserId(), LocaleUtil.US.toLanguageTag());

		_objectEntryLocalService.addObjectEntry(
			0, user.getUserId(), objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, Collections.emptyMap(), serviceContext);

		_assertNotificationQueueEntrySubject("Subject");

		serviceContext.setLanguageId(LocaleUtil.HUNGARY.toLanguageTag());

		_objectEntryLocalService.addObjectEntry(
			0, guestUser.getUserId(), objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, Collections.emptyMap(), serviceContext);

		_assertNotificationQueueEntrySubject("Subject");

		_objectEntryLocalService.addObjectEntry(
			0, user.getUserId(), objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, Collections.emptyMap(), serviceContext);

		_assertNotificationQueueEntrySubject("Subject");

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		_userLocalService.deleteUser(user);
	}

	@Test
	public void testOnAfterAddObjectActionWithHierarchy() throws Exception {
		ObjectDefinition objectDefinitionA =
			_publishObjectDefinitionWithObjectAction();
		ObjectDefinition objectDefinitionAA =
			_publishObjectDefinitionWithObjectAction();
		ObjectDefinition objectDefinitionAAA =
			_publishObjectDefinitionWithObjectAction();

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			Arrays.asList(
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService, objectDefinitionA,
					objectDefinitionAA,
					ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
					"objectRelationship1"),
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService, objectDefinitionAA,
					objectDefinitionAAA,
					ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
					"objectRelationship2")));

		ObjectEntry objectEntryA = _addObjectEntry(
			objectDefinitionA,
			HashMapBuilder.<String, Serializable>put(
				"firstName", "John"
			).build());

		_assertGroovyObjectActionExecutorArguments("John", objectEntryA);

		ObjectEntry objectEntryAA = _addObjectEntry(
			objectDefinitionAA,
			HashMapBuilder.<String, Serializable>put(
				"firstName", "Paul"
			).put(
				"r_objectRelationship1_" +
					objectDefinitionA.getPKObjectFieldName(),
				objectEntryA.getObjectEntryId()
			).build());

		_assertGroovyObjectActionExecutorArguments("Paul", objectEntryAA);

		ObjectEntry objectEntryAAA = _addObjectEntry(
			objectDefinitionAAA,
			HashMapBuilder.<String, Serializable>put(
				"firstName", "Peter"
			).put(
				"r_objectRelationship2_" +
					objectDefinitionAA.getPKObjectFieldName(),
				objectEntryAA.getObjectEntryId()
			).build());

		_assertGroovyObjectActionExecutorArguments("Peter", objectEntryAAA);

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {
				objectDefinitionA.getName(), objectDefinitionAA.getName(),
				objectDefinitionAAA.getName()
			},
			_objectEntryLocalService, _objectRelationshipLocalService);
	}

	@Test
	@TestInfo("LPS-189995")
	public void testOnAfterUpdateObjectActionWithAttachmentObjectField()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				false,
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
						ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
						RandomTestUtil.randomString(), "attachment",
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
							).build()),
						false),
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_INTEGER,
						ObjectFieldConstants.DB_TYPE_INTEGER, true, false, null,
						RandomTestUtil.randomString(), "integer", false)));

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());

		_addObjectAction(
			objectDefinition.getObjectDefinitionId(),
			ObjectActionExecutorConstants.KEY_UPDATE_OBJECT_ENTRY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE,
			UnicodePropertiesBuilder.put(
				"objectDefinitionId", objectDefinition.getObjectDefinitionId()
			).put(
				"predefinedValues",
				JSONUtil.putAll(
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "integer"
					).put(
						"value", "1"
					)
				).toString()
			).build());

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"attachment", StringPool.BLANK
			).put(
				"integer", String.valueOf(RandomTestUtil.randomInt())
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_objectEntryLocalService.addOrUpdateObjectEntry(
			objectEntry.getExternalReferenceCode(), 0,
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			HashMapBuilder.<String, Serializable>put(
				"attachment", StringPool.BLANK
			).put(
				"integer", String.valueOf(RandomTestUtil.randomInt())
			).build(),
			ServiceContextTestUtil.getServiceContext());

		objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntry.getObjectEntryId());

		Map<String, Serializable> values = objectEntry.getValues();

		Assert.assertEquals(1, values.get("integer"));
	}

	@Test
	public void testPopulateLabelMap() throws Exception {

		// Label map has multiple locales but no default locale

		ObjectAction objectAction1 = _addObjectAction(
			StringPool.BLANK,
			HashMapBuilder.put(
				LocaleUtil.BRAZIL, "pt_BR objectAction2Label"
			).put(
				LocaleUtil.SPAIN, "es_ES objectAction2Label"
			).build(),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_GROOVY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE,
			new UnicodeProperties(), false);

		Assert.assertEquals(
			HashMapBuilder.put(
				_objectDefinition.getDefaultLocale(), objectAction1.getName()
			).put(
				LocaleUtil.BRAZIL, "pt_BR objectAction2Label"
			).put(
				LocaleUtil.SPAIN, "es_ES objectAction2Label"
			).build(),
			objectAction1.getLabelMap());

		// Label map has one locale that is not the default locale

		ObjectAction objectAction2 = _addObjectAction(
			StringPool.BLANK,
			HashMapBuilder.put(
				LocaleUtil.BRAZIL, "pt_BR objectAction3Label"
			).build(),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_GROOVY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE,
			new UnicodeProperties(), false);

		Assert.assertEquals(
			HashMapBuilder.put(
				_objectDefinition.getDefaultLocale(), "pt_BR objectAction3Label"
			).put(
				LocaleUtil.BRAZIL, "pt_BR objectAction3Label"
			).build(),
			objectAction2.getLabelMap());

		// Label map is null

		ObjectAction objectAction3 = _addObjectAction(
			StringPool.BLANK, null, RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_GROOVY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE,
			new UnicodeProperties(), false);

		Assert.assertEquals(
			Collections.singletonMap(
				_objectDefinition.getDefaultLocale(), objectAction3.getName()),
			objectAction3.getLabelMap());

		_objectActionLocalService.deleteObjectAction(objectAction1);
		_objectActionLocalService.deleteObjectAction(objectAction2);
		_objectActionLocalService.deleteObjectAction(objectAction3);
	}

	@Test
	public void testSequentialObjectActions() throws Exception {
		_publishCustomObjectDefinition();

		ObjectAction objectAction1 = _addObjectAction(
			null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_UPDATE_OBJECT_ENTRY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE,
			UnicodePropertiesBuilder.put(
				"objectDefinitionId", _objectDefinition.getObjectDefinitionId()
			).put(
				"predefinedValues",
				JSONUtil.putAll(
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "firstName"
					).put(
						"value", "John"
					)
				).toString()
			).build(),
			false);
		ObjectAction objectAction2 = _addObjectAction(
			null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_UPDATE_OBJECT_ENTRY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE,
			UnicodePropertiesBuilder.put(
				"objectDefinitionId", _objectDefinition.getObjectDefinitionId()
			).put(
				"predefinedValues",
				JSONUtil.putAll(
					JSONUtil.put(
						"inputAsValue", true
					).put(
						"name", "lastName"
					).put(
						"value", "Smith"
					)
				).toString()
			).build(),
			false);

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"firstName", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"firstName", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Map<String, Serializable> values = _objectEntryLocalService.getValues(
			objectEntry.getObjectEntryId());

		Assert.assertEquals("John", MapUtil.getString(values, "firstName"));
		Assert.assertEquals("Smith", MapUtil.getString(values, "lastName"));

		_objectActionLocalService.deleteObjectAction(objectAction1);
		_objectActionLocalService.deleteObjectAction(objectAction2);
	}

	@Test
	public void testUpdateNotificationTemplateObjectAction() throws Exception {
		MailServiceTestUtil.clearMessages();

		ObjectDefinition objectDefinition = _publishCustomObjectDefinition();

		String body = RandomTestUtil.randomString();

		NotificationTemplate notificationTemplate = _addNotificationTemplate(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), body,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		_addObjectAction(
			objectDefinition.getObjectDefinitionId(),
			ObjectActionExecutorConstants.KEY_NOTIFICATION,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
			UnicodePropertiesBuilder.put(
				"notificationTemplateId",
				String.valueOf(notificationTemplate.getNotificationTemplateId())
			).build());

		_objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"firstName", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_assertEmailNotificationSent(body, 1);

		body = RandomTestUtil.randomString();

		notificationTemplate.setBody(body);

		_notificationTemplateLocalService.updateNotificationTemplate(
			notificationTemplate);

		_objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"firstName", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_assertEmailNotificationSent(body, 2);
	}

	@Test
	public void testUpdateObjectAction() throws Exception {
		String externalReferenceCode1 = RandomTestUtil.randomString();

		ObjectAction objectAction = _objectActionLocalService.addObjectAction(
			externalReferenceCode1, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), true,
			"equals(firstName, \"John\")", "Able Description",
			LocalizedMapUtil.getLocalizedMap("Able Error Message"),
			LocalizedMapUtil.getLocalizedMap("Able Label"), "Able",
			ObjectActionExecutorConstants.KEY_WEBHOOK,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
			UnicodePropertiesBuilder.put(
				"secret", "0123456789"
			).put(
				"url", "https://onafteradd.com"
			).build(),
			false);

		_assertObjectAction(
			true, "equals(firstName, \"John\")", "Able Description",
			LocalizedMapUtil.getLocalizedMap("Able Error Message"),
			LocalizedMapUtil.getLocalizedMap("Able Label"), "Able",
			objectAction, ObjectActionExecutorConstants.KEY_WEBHOOK,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
			UnicodePropertiesBuilder.put(
				"secret", "0123456789"
			).put(
				"url", "https://onafteradd.com"
			).build(),
			ObjectActionConstants.STATUS_NEVER_RAN);

		objectAction = _objectActionLocalService.updateObjectAction(
			externalReferenceCode1, objectAction.getObjectActionId(), false,
			"equals(firstName, \"João\")", "Baker Description",
			LocalizedMapUtil.getLocalizedMap("Baker Error Message"),
			LocalizedMapUtil.getLocalizedMap("Baker Label"), "Baker",
			ObjectActionExecutorConstants.KEY_GROOVY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE,
			UnicodePropertiesBuilder.put(
				"secret", "30624700"
			).put(
				"url", "https://onafterdelete.com"
			).build());

		_assertObjectAction(
			false, "equals(firstName, \"João\")", "Baker Description",
			LocalizedMapUtil.getLocalizedMap("Baker Error Message"),
			LocalizedMapUtil.getLocalizedMap("Baker Label"), "Baker",
			objectAction, ObjectActionExecutorConstants.KEY_GROOVY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE,
			UnicodePropertiesBuilder.put(
				"secret", "30624700"
			).put(
				"url", "https://onafterdelete.com"
			).build(),
			ObjectActionConstants.STATUS_NEVER_RAN);

		_publishCustomObjectDefinition();

		objectAction = _objectActionLocalService.updateObjectAction(
			externalReferenceCode1, objectAction.getObjectActionId(), true,
			"equals(firstName, \"John\")", "Charlie Description",
			LocalizedMapUtil.getLocalizedMap("Charlie Error Message"),
			LocalizedMapUtil.getLocalizedMap("Charlie Label"), "Charlie",
			ObjectActionExecutorConstants.KEY_WEBHOOK,
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE,
			UnicodePropertiesBuilder.put(
				"secret", "0123456789"
			).put(
				"url", "https://onafterdelete.com"
			).build());

		_assertObjectAction(
			true, "equals(firstName, \"John\")", "Charlie Description",
			LocalizedMapUtil.getLocalizedMap("Charlie Error Message"),
			LocalizedMapUtil.getLocalizedMap("Charlie Label"), "Baker",
			objectAction, ObjectActionExecutorConstants.KEY_WEBHOOK,
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE,
			UnicodePropertiesBuilder.put(
				"secret", "0123456789"
			).put(
				"url", "https://onafterdelete.com"
			).build(),
			ObjectActionConstants.STATUS_NEVER_RAN);

		String externalReferenceCode2 = RandomTestUtil.randomString();

		ObjectAction systemObjectAction =
			_objectActionLocalService.addObjectAction(
				externalReferenceCode2, TestPropsValues.getUserId(),
				_objectDefinition.getObjectDefinitionId(), true,
				"equals(firstName, \"John\")", "Able Description",
				LocalizedMapUtil.getLocalizedMap("Able Error Message"),
				LocalizedMapUtil.getLocalizedMap("Able Label"), "Able",
				ObjectActionExecutorConstants.KEY_WEBHOOK,
				ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
				UnicodePropertiesBuilder.put(
					"secret", "0123456789"
				).put(
					"url", "https://onafteradd.com"
				).build(),
				true);

		systemObjectAction = _objectActionLocalService.updateObjectAction(
			externalReferenceCode2, systemObjectAction.getObjectActionId(),
			false, "equals(firstName, \"João\")", "Baker Description",
			LocalizedMapUtil.getLocalizedMap("Baker Error Message"),
			LocalizedMapUtil.getLocalizedMap("Baker Label"), "Baker",
			ObjectActionExecutorConstants.KEY_GROOVY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE,
			UnicodePropertiesBuilder.put(
				"secret", "30624700"
			).put(
				"url", "https://onafterdelete.com"
			).build());

		_assertObjectAction(
			false, "equals(firstName, \"João\")", "Baker Description",
			LocalizedMapUtil.getLocalizedMap("Baker Error Message"),
			LocalizedMapUtil.getLocalizedMap("Baker Label"), "Able",
			systemObjectAction, ObjectActionExecutorConstants.KEY_GROOVY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
			UnicodePropertiesBuilder.put(
				"secret", "30624700"
			).put(
				"url", "https://onafterdelete.com"
			).build(),
			ObjectActionConstants.STATUS_NEVER_RAN);

		// Requests from forbidden bundles can only update the label

		String liferayMode = SystemProperties.get("liferay.mode");

		SystemProperties.clear("liferay.mode");

		try {
			systemObjectAction = _objectActionLocalService.updateObjectAction(
				externalReferenceCode2, systemObjectAction.getObjectActionId(),
				false, "equals(firstName, \"John\")", "Charlie Description",
				LocalizedMapUtil.getLocalizedMap("Charlie Error Message"),
				LocalizedMapUtil.getLocalizedMap("Charlie Label"), "Able",
				ObjectActionExecutorConstants.KEY_WEBHOOK,
				ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE,
				UnicodePropertiesBuilder.put(
					"secret", "0123456789"
				).put(
					"url", "https://onafteradd.com"
				).build());
		}
		finally {
			SystemProperties.set("liferay.mode", liferayMode);
		}

		_assertObjectAction(
			false, "equals(firstName, \"João\")", "Baker Description",
			LocalizedMapUtil.getLocalizedMap("Baker Error Message"),
			LocalizedMapUtil.getLocalizedMap("Charlie Label"), "Able",
			systemObjectAction, ObjectActionExecutorConstants.KEY_GROOVY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
			UnicodePropertiesBuilder.put(
				"secret", "30624700"
			).put(
				"url", "https://onafterdelete.com"
			).build(),
			ObjectActionConstants.STATUS_NEVER_RAN);

		_objectActionLocalService.deleteObjectAction(objectAction);
		_objectActionLocalService.deleteObjectAction(systemObjectAction);
	}

	@Rule
	public TestName testName = new TestName();

	private void _addModelResourcePermissions(
			String objectActionName, long objectEntryId, long userId)
		throws Exception {

		_resourcePermissionLocalService.addModelResourcePermissions(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			userId, _objectDefinition.getClassName(),
			String.valueOf(objectEntryId),
			ModelPermissionsFactory.create(
				HashMapBuilder.put(
					RoleConstants.USER, new String[] {objectActionName}
				).build(),
				_objectDefinition.getClassName()));
	}

	private NotificationTemplate _addNotificationTemplate(
			long userId, long objectDefinitionId, String body,
			String description, String name, String subject)
		throws Exception {

		NotificationTemplate notificationTemplate =
			NotificationTemplateLocalServiceUtil.createNotificationTemplate(
				RandomTestUtil.randomInt());

		notificationTemplate.setUserId(userId);
		notificationTemplate.setObjectDefinitionId(objectDefinitionId);
		notificationTemplate.setBody(body);
		notificationTemplate.setDescription(description);
		notificationTemplate.setEditorType(
			NotificationTemplateConstants.EDITOR_TYPE_RICH_TEXT);
		notificationTemplate.setName(name);
		notificationTemplate.setSubject(subject);
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
						"bcc", "[%CURRENT_USER_EMAIL_ADDRESS%]"),
				NotificationRecipientSettingUtil.
					createNotificationRecipientSetting(
						"cc", "[%CURRENT_USER_EMAIL_ADDRESS%],cc@liferay.com"),
				NotificationRecipientSettingUtil.
					createNotificationRecipientSetting(
						"from", "[%CURRENT_USER_EMAIL_ADDRESS%]"),
				NotificationRecipientSettingUtil.
					createNotificationRecipientSetting(
						"fromName",
						Collections.singletonMap(
							LocaleUtil.US, "[%CURRENT_USER_FIRST_NAME%]")),
				NotificationRecipientSettingUtil.
					createNotificationRecipientSetting(
						"to", "[%CURRENT_USER_EMAIL_ADDRESS%]")));
		notificationContext.setNotificationTemplate(notificationTemplate);
		notificationContext.setType(NotificationConstants.TYPE_EMAIL);

		return _notificationTemplateLocalService.addNotificationTemplate(
			notificationContext);
	}

	private ObjectAction _addNotificationTemplateObjectAction(
			String objectActionTriggerKey, ObjectDefinition objectDefinition)
		throws Exception {

		NotificationTemplate notificationTemplate = _addNotificationTemplate(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		return _addObjectAction(
			objectDefinition.getObjectDefinitionId(),
			ObjectActionExecutorConstants.KEY_NOTIFICATION,
			objectActionTriggerKey,
			UnicodePropertiesBuilder.put(
				"notificationTemplateId",
				notificationTemplate.getNotificationTemplateId()
			).build());
	}

	private ObjectAction _addObjectAction(
			long objectDefinitionId, String objectActionExecutorKey,
			String objectActionTriggerKey,
			UnicodeProperties parametersUnicodeProperties)
		throws PortalException {

		return _objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			objectDefinitionId, true, StringPool.BLANK,
			RandomTestUtil.randomString(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(), objectActionExecutorKey,
			objectActionTriggerKey, parametersUnicodeProperties, false);
	}

	private ObjectAction _addObjectAction(
			String conditionExpression, Map<Locale, String> labelMap,
			String name, String objectActionExecutorKey,
			String objectActionTriggerKey, UnicodeProperties unicodeProperties,
			boolean system)
		throws Exception {

		return _objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), true,
			conditionExpression, RandomTestUtil.randomString(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			labelMap, name, objectActionExecutorKey, objectActionTriggerKey,
			unicodeProperties, system);
	}

	private void _addObjectAction(
			String errorMessage, String externalReferenceCode, String label,
			String name, String objectActionTriggerKey, boolean system)
		throws Exception {

		_objectActionLocalService.addObjectAction(
			externalReferenceCode, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), true, StringPool.BLANK,
			RandomTestUtil.randomString(),
			LocalizedMapUtil.getLocalizedMap(errorMessage),
			LocalizedMapUtil.getLocalizedMap(label), name,
			ObjectActionExecutorConstants.KEY_GROOVY, objectActionTriggerKey,
			new UnicodeProperties(), system);
	}

	private ObjectAction _addObjectAction(
			String name, String objectActionExecutorKey,
			String objectActionTriggerKey, UnicodeProperties unicodeProperties,
			boolean system)
		throws Exception {

		return _addObjectAction(
			StringPool.BLANK,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			name, objectActionExecutorKey, objectActionTriggerKey,
			unicodeProperties, system);
	}

	private ObjectEntry _addObjectEntry(
			ObjectDefinition objectDefinition, Map<String, Serializable> values)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, values, ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			values.get("firstName"),
			MapUtil.getString(
				_objectEntryLocalService.getValues(
					objectEntry.getObjectEntryId()),
				"firstName"));

		return objectEntry;
	}

	private void _assertEmailNotificationSent(String body, int inboxSize) {
		List<NotificationQueueEntry> notificationQueueEntries =
			_notificationQueueEntryLocalService.getNotificationEntries(
				NotificationConstants.TYPE_EMAIL,
				NotificationQueueEntryConstants.STATUS_SENT);

		Assert.assertEquals(
			notificationQueueEntries.toString(), inboxSize,
			notificationQueueEntries.size());

		Assert.assertEquals(inboxSize, MailServiceTestUtil.getInboxSize());

		MailMessage lastMailMessage = MailServiceTestUtil.getLastMailMessage();

		String mailMessageBody = lastMailMessage.getBody();

		Assert.assertTrue(mailMessageBody.contains(body));
	}

	private void _assertGroovyObjectActionExecutorArguments(
		String firstName, ObjectEntry objectEntry) {

		Assert.assertEquals(1, _argumentsList.size());

		Object[] arguments = _argumentsList.poll();

		Map<String, Object> inputObjects = (Map<String, Object>)arguments[0];

		Assert.assertEquals(
			objectEntry.getExternalReferenceCode(),
			inputObjects.get("externalReferenceCode"));
		Assert.assertEquals(firstName, inputObjects.get("firstName"));
		Assert.assertEquals(
			objectEntry.getObjectEntryId(), inputObjects.get("id"));

		Assert.assertEquals(Collections.emptySet(), arguments[1]);
		Assert.assertEquals("println \"Hello World\"", arguments[2]);
	}

	private void _assertGroovyObjectActionExecutorArguments(
		String expectedObjectFieldValue, String objectFieldName) {

		Assert.assertEquals(1, _argumentsList.size());

		Object[] arguments = _argumentsList.poll();

		Map<String, Object> inputObjects = (Map<String, Object>)arguments[0];

		Assert.assertEquals(
			expectedObjectFieldValue, inputObjects.get(objectFieldName));
	}

	private void _assertNotificationQueueEntrySubject(String expectedSubject)
		throws Exception {

		List<NotificationQueueEntry> notificationQueueEntries =
			_notificationQueueEntryLocalService.getNotificationEntries(
				NotificationConstants.TYPE_EMAIL,
				NotificationQueueEntryConstants.STATUS_SENT);

		Assert.assertEquals(
			notificationQueueEntries.toString(), 1,
			notificationQueueEntries.size());

		NotificationQueueEntry notificationQueueEntry =
			notificationQueueEntries.get(0);

		Assert.assertEquals(
			expectedSubject, notificationQueueEntry.getSubject());

		_notificationQueueEntryLocalService.deleteNotificationQueueEntry(
			notificationQueueEntry);
	}

	private void _assertObjectAction(
		boolean active, String name, ObjectAction objectAction) {

		Assert.assertEquals(active, objectAction.isActive());
		Assert.assertEquals(name, objectAction.getName());
	}

	private void _assertObjectAction(
		boolean active, String conditionExpression, String description,
		Map<Locale, String> errorMessageMap, Map<Locale, String> labelMap,
		String name, ObjectAction objectAction, String objectActionExecutorKey,
		String objectActionTriggerKey,
		UnicodeProperties parametersUnicodeProperties, int status) {

		Assert.assertEquals(active, objectAction.isActive());
		Assert.assertEquals(
			conditionExpression, objectAction.getConditionExpression());
		Assert.assertEquals(description, objectAction.getDescription());
		Assert.assertEquals(errorMessageMap, objectAction.getErrorMessageMap());
		Assert.assertEquals(labelMap, objectAction.getLabelMap());
		Assert.assertEquals(name, objectAction.getName());
		Assert.assertEquals(
			objectActionExecutorKey, objectAction.getObjectActionExecutorKey());
		Assert.assertEquals(
			objectActionTriggerKey, objectAction.getObjectActionTriggerKey());
		Assert.assertEquals(
			parametersUnicodeProperties,
			objectAction.getParametersUnicodeProperties());
		Assert.assertEquals(status, objectAction.getStatus());
	}

	private void _assertObjectAction(
		boolean active, String name,
		String notificationTemplateExternalReferenceCode,
		ObjectAction objectAction) {

		_assertObjectAction(active, name, objectAction);

		UnicodeProperties unicodeProperties =
			objectAction.getParametersUnicodeProperties();

		NotificationTemplate notificationTemplate =
			_notificationTemplateLocalService.fetchNotificationTemplate(
				GetterUtil.getLong(
					unicodeProperties.getProperty("notificationTemplateId")));

		Assert.assertEquals(
			notificationTemplateExternalReferenceCode,
			notificationTemplate.getExternalReferenceCode());
		Assert.assertTrue(notificationTemplate.isSystem());
		Assert.assertEquals(
			NotificationConstants.TYPE_EMAIL, notificationTemplate.getType());
	}

	private void _assertOrganization(
			String comments, String name, ObjectDefinition objectDefinition,
			ObjectField objectField, String objectFieldValue)
		throws Exception {

		Organization organization = _organizationLocalService.getOrganization(
			TestPropsValues.getCompanyId(), name);

		Assert.assertEquals(comments, organization.getComments());

		Map<String, Serializable> values =
			_objectEntryLocalService.
				getExtensionDynamicObjectDefinitionTableValues(
					objectDefinition, organization.getOrganizationId());

		Assert.assertEquals(
			objectFieldValue, values.get(objectField.getName()));

		_organizationLocalService.deleteOrganization(organization);
	}

	private void _assertWebhookObjectAction(
			String birthday, String firstName, String lastName,
			String objectActionTriggerKey, ObjectDefinition objectDefinition,
			String originalFirstName, String originalLastName, int status)
		throws Exception {

		Assert.assertEquals(1, _argumentsList.size());

		Object[] arguments = _argumentsList.poll();

		Http.Options options = (Http.Options)arguments[0];

		Assert.assertEquals(
			StringUtil.toLowerCase(objectActionTriggerKey),
			options.getHeader("x-api-key"));
		Assert.assertEquals(
			"https://" + StringUtil.toLowerCase(objectActionTriggerKey) +
				".com",
			options.getLocation());

		Http.Body body = options.getBody();

		Assert.assertEquals(StringPool.UTF8, body.getCharset());
		Assert.assertEquals(
			ContentTypes.APPLICATION_JSON, body.getContentType());

		JSONObject payloadJSONObject = _jsonFactory.createJSONObject(
			body.getContent());

		Assert.assertEquals(
			objectActionTriggerKey,
			payloadJSONObject.getString("objectActionTriggerKey"));
		Assert.assertEquals(
			birthday,
			JSONUtil.getValue(
				payloadJSONObject,
				"JSONObject/objectEntryDTO" + objectDefinition.getShortName(),
				"JSONObject/properties", "Object/birthday"));
		Assert.assertEquals(
			firstName,
			JSONUtil.getValue(
				payloadJSONObject, "JSONObject/objectEntry",
				"JSONObject/values", "Object/firstName"));
		Assert.assertEquals(
			firstName,
			JSONUtil.getValue(
				payloadJSONObject,
				"JSONObject/objectEntryDTO" + objectDefinition.getShortName(),
				"JSONObject/properties", "Object/firstName"));
		Assert.assertEquals(
			lastName,
			JSONUtil.getValue(
				payloadJSONObject, "JSONObject/objectEntry",
				"JSONObject/values", "Object/lastName"));
		Assert.assertEquals(
			status,
			JSONUtil.getValue(
				payloadJSONObject, "JSONObject/objectEntry", "Object/status"));

		if (StringUtil.equals(
				objectActionTriggerKey,
				ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE) ||
			StringUtil.equals(
				objectActionTriggerKey,
				ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE)) {

			Assert.assertEquals(
				originalFirstName,
				JSONUtil.getValue(
					payloadJSONObject, "JSONObject/originalObjectEntry",
					"JSONObject/values", "Object/firstName"));
			Assert.assertEquals(
				originalLastName,
				JSONUtil.getValue(
					payloadJSONObject, "JSONObject/originalObjectEntry",
					"JSONObject/values", "Object/lastName"));
		}
		else {
			Assert.assertNull(
				JSONUtil.getValue(
					payloadJSONObject, "JSONObject/originalObjectEntry"));
		}
	}

	private FutureTask<Void> _getAddObjectEntryFutureTask(String firstName) {
		return new FutureTask<Void>(
			new CompanyInheritableThreadLocalCallable(
				() -> {
					try {
						_objectEntryLocalService.addObjectEntry(
							0, TestPropsValues.getUserId(),
							_objectDefinition.getObjectDefinitionId(),
							ObjectEntryFolderConstants.
								PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
							null,
							HashMapBuilder.<String, Serializable>put(
								"firstName", firstName
							).build(),
							ServiceContextTestUtil.getServiceContext());
					}
					catch (PortalException portalException) {
					}

					return null;
				}));
	}

	private Object _getAndSetFieldValue(
		Class<?> clazz, String fieldName, String objectActionExecutorKey) {

		return ReflectionTestUtil.getAndSetFieldValue(
			_objectActionExecutorRegistry.getObjectActionExecutor(
				0, objectActionExecutorKey),
			fieldName,
			ProxyUtil.newProxyInstance(
				clazz.getClassLoader(), new Class<?>[] {clazz},
				(proxy, method, arguments) -> {
					_argumentsList.add(arguments);

					if (Objects.equals(
							method.getDeclaringClass(),
							ObjectScriptingExecutor.class) &&
						Objects.equals(method.getName(), "execute")) {

						return Collections.emptyMap();
					}

					return null;
				}));
	}

	private ObjectEntryResource _getObjectEntryResource(User user)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(
			ObjectActionLocalServiceTest.class);

		try (ServiceTrackerMap<String, ObjectEntryResource> serviceTrackerMap =
				ServiceTrackerMapFactory.openSingleValueMap(
					bundle.getBundleContext(), ObjectEntryResource.class,
					"entity.class.name")) {

			ObjectEntryResource objectEntryResource =
				serviceTrackerMap.getService(
					StringBundler.concat(
						com.liferay.object.rest.dto.v1_0.ObjectEntry.class.
							getName(),
						StringPool.POUND,
						StringUtil.toLowerCase(
							_objectDefinition.getShortName())));

			objectEntryResource.setContextAcceptLanguage(
				new AcceptLanguage() {

					@Override
					public List<Locale> getLocales() {
						return Arrays.asList(LocaleUtil.getDefault());
					}

					@Override
					public String getPreferredLanguageId() {
						return LocaleUtil.toLanguageId(LocaleUtil.getDefault());
					}

					@Override
					public Locale getPreferredLocale() {
						return LocaleUtil.getDefault();
					}

				});
			objectEntryResource.setContextCompany(
				_companyLocalService.getCompany(
					_objectDefinition.getCompanyId()));
			objectEntryResource.setContextUser(user);

			Class<?> clazz = objectEntryResource.getClass();

			Method method = clazz.getMethod(
				"setObjectDefinition", ObjectDefinition.class);

			method.invoke(objectEntryResource, _objectDefinition);

			return objectEntryResource;
		}
	}

	private ObjectDefinition _publishCustomObjectDefinition() throws Exception {
		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId());
	}

	private ObjectDefinition _publishObjectDefinitionWithObjectAction()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				false,
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), "firstName")));

		_addObjectAction(
			objectDefinition.getObjectDefinitionId(),
			ObjectActionExecutorConstants.KEY_GROOVY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
			UnicodePropertiesBuilder.put(
				"script", "println \"Hello World\""
			).build());

		return objectDefinition;
	}

	private void _testAddObjectActionWithCircularReference(
			int expectedObjectEntriesCount)
		throws Exception {

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		String originalName = PrincipalThreadLocal.getName();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(_user));
			PrincipalThreadLocal.setName(_user.getUserId());

			_objectEntryLocalService.addObjectEntry(
				0, TestPropsValues.getUserId(),
				_objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				Collections.singletonMap(
					"firstName", RandomTestUtil.randomString()),
				ServiceContextTestUtil.getServiceContext());

			Assert.assertEquals(
				expectedObjectEntriesCount,
				_objectEntryLocalService.getObjectEntriesCount(
					0, _objectDefinition.getObjectDefinitionId()));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
			PrincipalThreadLocal.setName(originalName);
		}
	}

	private void _testExecuteObjectActionMultipleTimesInTheSameThreadWithACustomObjectDefinition()
		throws Exception {

		// On after add

		_publishCustomObjectDefinition();

		ObjectAction objectAction1 = _addObjectAction(
			_objectDefinition.getObjectDefinitionId(),
			ObjectActionExecutorConstants.KEY_WEBHOOK,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
			UnicodePropertiesBuilder.put(
				"secret", "onafteradd"
			).put(
				"url", "https://onafteradd.com"
			).build());

		Assert.assertEquals(0, _argumentsList.size());

		ObjectEntry objectEntry1 = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"firstName", "John"
			).put(
				"lastName", "Smith"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_assertWebhookObjectAction(
			null, "John", "Smith",
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD, _objectDefinition,
			null, null, WorkflowConstants.STATUS_APPROVED);

		ObjectEntry objectEntry2 = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"firstName", "Peter"
			).put(
				"lastName", "White"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_assertWebhookObjectAction(
			null, "Peter", "White",
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD, _objectDefinition,
			null, null, WorkflowConstants.STATUS_APPROVED);

		_objectEntryLocalService.deleteObjectEntry(objectEntry1);
		_objectEntryLocalService.deleteObjectEntry(objectEntry2);

		_objectActionLocalService.deleteObjectAction(objectAction1);

		// On after delete

		ObjectAction objectAction2 = _addObjectAction(
			_objectDefinition.getObjectDefinitionId(),
			ObjectActionExecutorConstants.KEY_WEBHOOK,
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE,
			UnicodePropertiesBuilder.put(
				"secret", "onafterdelete"
			).put(
				"url", "https://onafterdelete.com"
			).build());

		Assert.assertEquals(0, _argumentsList.size());

		ObjectEntry objectEntry3 = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"firstName", "John"
			).put(
				"lastName", "Smith"
			).build(),
			ServiceContextTestUtil.getServiceContext());
		ObjectEntry objectEntry4 = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"firstName", "Peter"
			).put(
				"lastName", "White"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_objectEntryLocalService.deleteObjectEntry(objectEntry3);

		_assertWebhookObjectAction(
			null, "John", "Smith",
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE, _objectDefinition,
			"John", "Smith", WorkflowConstants.STATUS_APPROVED);

		_objectEntryLocalService.deleteObjectEntry(objectEntry4);

		_assertWebhookObjectAction(
			null, "Peter", "White",
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE, _objectDefinition,
			"Peter", "White", WorkflowConstants.STATUS_APPROVED);

		_objectActionLocalService.deleteObjectAction(objectAction2);

		// On after update

		ObjectAction objectAction3 = _addObjectAction(
			_objectDefinition.getObjectDefinitionId(),
			ObjectActionExecutorConstants.KEY_WEBHOOK,
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE,
			UnicodePropertiesBuilder.put(
				"secret", "onafterupdate"
			).put(
				"url", "https://onafterupdate.com"
			).build());

		ObjectEntry objectEntry5 = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"firstName", "Peter"
			).put(
				"lastName", "White"
			).build(),
			ServiceContextTestUtil.getServiceContext());
		ObjectEntry objectEntry6 = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"firstName", "Peter"
			).put(
				"lastName", "White"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		objectEntry5 = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry5.getObjectEntryId(),
			objectEntry5.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"firstName", "John"
			).put(
				"lastName", "Smith"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_assertWebhookObjectAction(
			null, "John", "Smith",
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE, _objectDefinition,
			"Peter", "White", WorkflowConstants.STATUS_APPROVED);

		objectEntry6 = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry6.getObjectEntryId(),
			objectEntry6.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"firstName", "João"
			).put(
				"lastName", "o Discípulo Amado"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_assertWebhookObjectAction(
			null, "João", "o Discípulo Amado",
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE, _objectDefinition,
			"Peter", "White", WorkflowConstants.STATUS_APPROVED);

		_objectEntryLocalService.deleteObjectEntry(objectEntry5);
		_objectEntryLocalService.deleteObjectEntry(objectEntry6);

		_objectActionLocalService.deleteObjectAction(objectAction3);
	}

	private void _testExecuteObjectActionMultipleTimesInTheSameThreadWithASystemObjectDefinition()
		throws Exception {

		// On after add

		ObjectDefinition organizationObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				TestPropsValues.getCompanyId(), Organization.class.getName());

		ObjectAction objectAction = _addObjectAction(
			organizationObjectDefinition.getObjectDefinitionId(),
			ObjectActionExecutorConstants.KEY_GROOVY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
			new UnicodeProperties());

		Organization organization1 = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		_assertGroovyObjectActionExecutorArguments(
			organization1.getName(), "name");

		Organization organization2 = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		_assertGroovyObjectActionExecutorArguments(
			organization2.getName(), "name");

		_objectActionLocalService.deleteObjectAction(objectAction);

		// On after delete

		objectAction = _addObjectAction(
			organizationObjectDefinition.getObjectDefinitionId(),
			ObjectActionExecutorConstants.KEY_GROOVY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE,
			new UnicodeProperties());

		_organizationLocalService.deleteOrganization(
			organization1.getOrganizationId());

		_assertGroovyObjectActionExecutorArguments(
			organization1.getName(), "name");

		_organizationLocalService.deleteOrganization(
			organization2.getOrganizationId());

		_assertGroovyObjectActionExecutorArguments(
			organization2.getName(), "name");

		_objectActionLocalService.deleteObjectAction(objectAction);

		// On after update

		objectAction = _addObjectAction(
			organizationObjectDefinition.getObjectDefinitionId(),
			ObjectActionExecutorConstants.KEY_GROOVY,
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE,
			new UnicodeProperties());

		organization1.setName(RandomTestUtil.randomString());

		organization1 = _organizationLocalService.updateOrganization(
			organization1);

		_assertGroovyObjectActionExecutorArguments(
			organization1.getName(), "name");

		organization2.setName(RandomTestUtil.randomString());

		organization2 = _organizationLocalService.updateOrganization(
			organization2);

		_assertGroovyObjectActionExecutorArguments(
			organization2.getName(), "name");

		_objectActionLocalService.deleteObjectAction(objectAction);
	}

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	private final Queue<Object[]> _argumentsList =
		new ConcurrentLinkedQueue<>();
	private CommerceChannel _commerceChannel;
	private CommerceCurrency _commerceCurrency;

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
	private CompanyLocalService _companyLocalService;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private NotificationQueueEntryLocalService
		_notificationQueueEntryLocalService;

	@Inject
	private NotificationTemplateLocalService _notificationTemplateLocalService;

	@Inject
	private ObjectActionEngine _objectActionEngine;

	@Inject
	private ObjectActionExecutorRegistry _objectActionExecutorRegistry;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	@Inject
	private ObjectActionTriggerRegistry _objectActionTriggerRegistry;

	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	private Http _originalHttp;
	private ObjectScriptingExecutor _originalObjectScriptingExecutor;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowTaskManager _workflowTaskManager;

}