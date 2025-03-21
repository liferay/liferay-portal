/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.health.status.web.internal;

import com.liferay.commerce.constants.CommerceHealthStatusConstants;
import com.liferay.commerce.constants.CommerceObjectActionExecutorConstants;
import com.liferay.commerce.health.status.CommerceHealthStatus;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(
	property = {
		"commerce.health.status.display.order:Integer=160",
		"commerce.health.status.key=" + CommerceHealthStatusConstants.SPLIT_COMMERCE_ORDER_BY_CATALOG_HEALTH_STATUS_KEY
	},
	service = CommerceHealthStatus.class
)
public class SplitOrderByCatalogHealthStatus implements CommerceHealthStatus {

	@Override
	public void fixIssue(HttpServletRequest httpServletRequest)
		throws PortalException {

		try {
			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				httpServletRequest);

			Callable<Object> orderSplitCallable = new OrderSplitCallable(
				serviceContext);

			TransactionInvokerUtil.invoke(
				_transactionConfig, orderSplitCallable);
		}
		catch (Throwable throwable) {
			_log.error(throwable, throwable);
		}
	}

	@Override
	public String getDescription(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.get(
			resourceBundle,
			CommerceHealthStatusConstants.
				SPLIT_COMMERCE_ORDER_BY_CATALOG_HEALTH_STATUS_DESCRIPTION);
	}

	@Override
	public String getKey() {
		return CommerceHealthStatusConstants.
			SPLIT_COMMERCE_ORDER_BY_CATALOG_HEALTH_STATUS_KEY;
	}

	@Override
	public String getName(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.get(
			resourceBundle,
			CommerceHealthStatusConstants.
				SPLIT_COMMERCE_ORDER_BY_CATALOG_HEALTH_STATUS_KEY);
	}

	@Override
	public int getType() {
		return CommerceHealthStatusConstants.
			COMMERCE_HEALTH_STATUS_TYPE_VIRTUAL_INSTANCE;
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public boolean isFixed(long companyId, long commerceChannelId)
		throws PortalException {

		ObjectDefinition commerceOrderObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				companyId, CommerceOrder.class.getName());

		if (commerceOrderObjectDefinition == null) {
			return false;
		}

		List<ObjectAction> objectActions = ListUtil.filter(
			_objectActionLocalService.getObjectActions(
				commerceOrderObjectDefinition.getObjectDefinitionId()),
			objectAction -> Objects.equals(
				objectAction.getObjectActionExecutorKey(),
				CommerceObjectActionExecutorConstants.
					KEY_SPLIT_COMMERCE_ORDER_BY_CATALOG));

		return ListUtil.isNotEmpty(objectActions);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SplitOrderByCatalogHealthStatus.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private Language _language;

	@Reference
	private ObjectActionLocalService _objectActionLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private class OrderSplitCallable implements Callable<Object> {

		@Override
		public Object call() throws Exception {
			ObjectDefinition commerceOrderObjectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
					_serviceContext.getCompanyId(),
					CommerceOrder.class.getName());

			if (commerceOrderObjectDefinition != null) {
				_objectActionLocalService.addObjectAction(
					null, _serviceContext.getUserId(),
					commerceOrderObjectDefinition.getObjectDefinitionId(),
					false, "orderStatus = 10",
					"This action splits an order into supplier orders by " +
						"catalog",
					null,
					HashMapBuilder.put(
						_serviceContext.getLocale(), "Split order by catalog"
					).build(),
					"SplitOrderByCatalog",
					CommerceObjectActionExecutorConstants.
						KEY_SPLIT_COMMERCE_ORDER_BY_CATALOG,
					"liferay/commerce_order_status",
					UnicodePropertiesBuilder.put(
						"objectDefinitionId",
						commerceOrderObjectDefinition.getObjectDefinitionId()
					).build(),
					false);
			}

			return null;
		}

		private OrderSplitCallable(ServiceContext serviceContext) {
			_serviceContext = serviceContext;
		}

		private final ServiceContext _serviceContext;

	}

}