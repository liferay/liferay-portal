/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal.mvc;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.annotation.PostConstruct;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;

import jakarta.inject.Inject;

import jakarta.validation.MessageInterpolator;
import jakarta.validation.NoProviderFoundException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Iterator;

/**
 * @author Neil Griffin
 */
@ApplicationScoped
public class BeanValidationProducer {

	@BeanValidationMessageInterpolator
	@Dependent
	@Produces
	public MessageInterpolator getMessageInterpolator() {
		return _messageInterpolator;
	}

	@BeanValidationValidator
	@Dependent
	@Produces
	public Validator getValidator() {
		return _validator;
	}

	@PostConstruct
	public void postConstruct() {
		ValidatorFactory validatorFactory = null;

		Iterator<ValidatorFactory> iterator =
			_validatorFactoryInstance.iterator();

		if (iterator.hasNext()) {
			validatorFactory = iterator.next();
		}

		if (validatorFactory == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"The ValidatorFactory was not injected. If you are using " +
						"Hibernate Validator, then include the " +
							"hibernate-validator-cdi dependency.");
			}

			try {
				validatorFactory = Validation.buildDefaultValidatorFactory();
			}
			catch (NoProviderFoundException noProviderFoundException) {
				_log.error(noProviderFoundException);
			}
		}

		if (validatorFactory != null) {
			_messageInterpolator = validatorFactory.getMessageInterpolator();

			if ((_messageInterpolator == null) && _log.isWarnEnabled()) {
				_log.warn(
					"The bean validation MessageInterpolator is not available");
			}

			_validator = validatorFactory.getValidator();

			if ((_validator == null) && _log.isWarnEnabled()) {
				_log.warn("The bean validation validator is not available");
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BeanValidationProducer.class);

	private MessageInterpolator _messageInterpolator;
	private Validator _validator;

	@Inject
	private Instance<ValidatorFactory> _validatorFactoryInstance;

}