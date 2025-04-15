/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet.bridges.mvc;

import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

import java.util.concurrent.Callable;

/**
 * @author Bruno Basto
 */
public abstract class BaseTransactionalMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {

		try {
			Callable<Void> callable = new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					doTransactionalCommand(actionRequest, actionResponse);

					return null;
				}

			};

			TransactionInvokerUtil.invoke(getTransactionConfig(), callable);
		}
		catch (Throwable throwable) {
			if (throwable instanceof PortletException) {
				throw (PortletException)throwable;
			}

			throw new PortletException(throwable);
		}
	}

	protected abstract void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception;

	protected TransactionConfig getTransactionConfig() {
		return _transactionConfig;
	}

	private static final TransactionConfig _transactionConfig;

	static {
		TransactionConfig.Builder builder = new TransactionConfig.Builder();

		builder.setRollbackForClasses(Exception.class);

		_transactionConfig = builder.build();
	}

}