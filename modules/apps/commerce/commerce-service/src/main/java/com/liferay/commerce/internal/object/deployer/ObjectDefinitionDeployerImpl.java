/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.object.deployer;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.constants.CommerceDefinitionTermConstants;
import com.liferay.commerce.currency.model.CommerceMoneyFactory;
import com.liferay.commerce.internal.notification.term.evaluator.CommerceOrderAccountNotificationTermEvaluator;
import com.liferay.commerce.internal.notification.term.evaluator.CommerceOrderAddressNotificationTermEvaluator;
import com.liferay.commerce.internal.notification.term.evaluator.CommerceOrderItemsNotificationTermEvaluator;
import com.liferay.commerce.internal.notification.term.evaluator.CommerceOrderPaymentMethodNotificationTermEvaluator;
import com.liferay.commerce.internal.notification.term.evaluator.SalesAgentNotificationTermEvaluator;
import com.liferay.commerce.internal.notification.term.provider.CommerceOrderAccountNotificationTermProvider;
import com.liferay.commerce.internal.notification.term.provider.CommerceOrderAddressNotificationTermProvider;
import com.liferay.commerce.internal.notification.term.provider.CommerceOrderItemsNotificationTermProvider;
import com.liferay.commerce.internal.notification.term.provider.CommerceOrderPaymentMethodNotificationTermProvider;
import com.liferay.commerce.internal.notification.term.provider.SalesAgentNotificationTermProvider;
import com.liferay.commerce.internal.notification.type.ObjectDefinitionCommerceNotificationType;
import com.liferay.commerce.internal.order.term.contributor.ObjectCommerceDefinitionTermContributor;
import com.liferay.commerce.internal.order.term.contributor.ObjectRecipientCommerceDefinitionTermContributor;
import com.liferay.commerce.media.CommerceMediaResolver;
import com.liferay.commerce.notification.type.CommerceNotificationType;
import com.liferay.commerce.order.CommerceDefinitionTermContributor;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.price.CommerceOrderPriceCalculation;
import com.liferay.commerce.product.helper.CPInstanceHelper;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.util.CommerceOrderItemQuantityFormatter;
import com.liferay.notification.term.evaluator.NotificationTermEvaluator;
import com.liferay.notification.term.provider.NotificationTermProvider;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.deployer.ObjectDefinitionDeployer;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Marco Leo
 */
@Component(service = ObjectDefinitionDeployer.class)
public class ObjectDefinitionDeployerImpl implements ObjectDefinitionDeployer {

	@Override
	public List<ServiceRegistration<?>> deploy(
		ObjectDefinition objectDefinition) {

		if (StringUtil.equalsIgnoreCase(
				"CommerceOrder", objectDefinition.getShortName())) {

			return Arrays.asList(
				_bundleContext.registerService(
					NotificationTermEvaluator.class,
					new CommerceOrderAccountNotificationTermEvaluator(
						_commerceOrderLocalService, objectDefinition),
					HashMapDictionaryBuilder.<String, Object>put(
						"class.name", objectDefinition.getClassName()
					).build()),
				_bundleContext.registerService(
					NotificationTermEvaluator.class,
					new CommerceOrderAddressNotificationTermEvaluator(
						_commerceOrderLocalService, objectDefinition),
					HashMapDictionaryBuilder.<String, Object>put(
						"class.name", objectDefinition.getClassName()
					).build()),
				_bundleContext.registerService(
					NotificationTermEvaluator.class,
					new CommerceOrderItemsNotificationTermEvaluator(
						_commerceMediaResolver, _commerceMoneyFactory,
						_commerceOrderItemQuantityFormatter,
						_commerceOrderLocalService,
						_commerceOrderPriceCalculation, _companyLocalService,
						_cpDefinitionLocalService,
						_cpInstanceUnitOfMeasureLocalService, _cpInstanceHelper,
						_language, objectDefinition, _userLocalService),
					HashMapDictionaryBuilder.<String, Object>put(
						"class.name", objectDefinition.getClassName()
					).build()),
				_bundleContext.registerService(
					NotificationTermEvaluator.class,
					new CommerceOrderPaymentMethodNotificationTermEvaluator(
						_commerceOrderLocalService,
						_commercePaymentMethodGroupRelLocalService,
						objectDefinition, _userLocalService),
					HashMapDictionaryBuilder.<String, Object>put(
						"class.name", objectDefinition.getClassName()
					).build()),
				_bundleContext.registerService(
					NotificationTermEvaluator.class,
					new SalesAgentNotificationTermEvaluator(
						_accountEntryModelResourcePermission,
						_commerceOrderLocalService, objectDefinition,
						_permissionCheckerFactory, _roleLocalService,
						_userLocalService),
					HashMapDictionaryBuilder.<String, Object>put(
						"class.name", objectDefinition.getClassName()
					).build()),
				_bundleContext.registerService(
					NotificationTermProvider.class,
					new CommerceOrderAccountNotificationTermProvider(),
					HashMapDictionaryBuilder.<String, Object>put(
						"class.name", objectDefinition.getClassName()
					).build()),
				_bundleContext.registerService(
					NotificationTermProvider.class,
					new CommerceOrderAddressNotificationTermProvider(),
					HashMapDictionaryBuilder.<String, Object>put(
						"class.name", objectDefinition.getClassName()
					).build()),
				_bundleContext.registerService(
					NotificationTermProvider.class,
					new CommerceOrderItemsNotificationTermProvider(),
					HashMapDictionaryBuilder.<String, Object>put(
						"class.name", objectDefinition.getClassName()
					).build()),
				_bundleContext.registerService(
					NotificationTermProvider.class,
					new CommerceOrderPaymentMethodNotificationTermProvider(),
					HashMapDictionaryBuilder.<String, Object>put(
						"class.name", objectDefinition.getClassName()
					).build()),
				_bundleContext.registerService(
					NotificationTermProvider.class,
					new SalesAgentNotificationTermProvider(),
					HashMapDictionaryBuilder.<String, Object>put(
						"class.name", objectDefinition.getClassName()
					).build()));
		}

		if (objectDefinition.isUnmodifiableSystemObject() ||
			Objects.equals(
				objectDefinition.getScope(),
				ObjectDefinitionConstants.SCOPE_COMPANY)) {

			return Collections.emptyList();
		}

		return Arrays.asList(
			_bundleContext.registerService(
				CommerceNotificationType.class,
				new ObjectDefinitionCommerceNotificationType(
					"create", objectDefinition.getClassName() + "#create",
					objectDefinition.getShortName()),
				HashMapDictionaryBuilder.put(
					"commerce.notification.type.key",
					objectDefinition.getClassName() + "#create"
				).build()),
			_bundleContext.registerService(
				CommerceNotificationType.class,
				new ObjectDefinitionCommerceNotificationType(
					"delete", objectDefinition.getClassName() + "#delete",
					objectDefinition.getShortName()),
				HashMapDictionaryBuilder.put(
					"commerce.notification.type.key",
					objectDefinition.getClassName() + "#delete"
				).build()),
			_bundleContext.registerService(
				CommerceNotificationType.class,
				new ObjectDefinitionCommerceNotificationType(
					"update", objectDefinition.getClassName() + "#update",
					objectDefinition.getShortName()),
				HashMapDictionaryBuilder.put(
					"commerce.notification.type.key",
					objectDefinition.getClassName() + "#update"
				).build()),
			_bundleContext.registerService(
				CommerceDefinitionTermContributor.class,
				new ObjectCommerceDefinitionTermContributor(
					objectDefinition.getObjectDefinitionId(),
					_objectFieldLocalService, _userLocalService),
				HashMapDictionaryBuilder.<String, Object>put(
					"commerce.definition.term.contributor.key",
					CommerceDefinitionTermConstants.
						BODY_AND_SUBJECT_DEFINITION_TERMS_CONTRIBUTOR
				).put(
					"commerce.notification.type.key",
					new String[] {
						objectDefinition.getClassName() + "#create",
						objectDefinition.getClassName() + "#delete",
						objectDefinition.getClassName() + "#update"
					}
				).build()),
			_bundleContext.registerService(
				CommerceDefinitionTermContributor.class,
				new ObjectRecipientCommerceDefinitionTermContributor(
					objectDefinition.getObjectDefinitionId(),
					_objectFieldLocalService, _userGroupLocalService,
					_userLocalService),
				HashMapDictionaryBuilder.<String, Object>put(
					"commerce.definition.term.contributor.key",
					CommerceDefinitionTermConstants.
						RECIPIENT_DEFINITION_TERMS_CONTRIBUTOR
				).put(
					"commerce.notification.type.key",
					new String[] {
						objectDefinition.getClassName() + "#create",
						objectDefinition.getClassName() + "#delete",
						objectDefinition.getClassName() + "#update"
					}
				).build()));
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.account.model.AccountEntry)"
	)
	private volatile ModelResourcePermission<AccountEntry>
		_accountEntryModelResourcePermission;

	private BundleContext _bundleContext;

	@Reference
	private CommerceMediaResolver _commerceMediaResolver;

	@Reference
	private CommerceMoneyFactory _commerceMoneyFactory;

	@Reference
	private CommerceOrderItemQuantityFormatter
		_commerceOrderItemQuantityFormatter;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private CommerceOrderPriceCalculation _commerceOrderPriceCalculation;

	@Reference
	private CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CPInstanceHelper _cpInstanceHelper;

	@Reference
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

	@Reference
	private Language _language;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

	@Reference
	private UserLocalService _userLocalService;

}