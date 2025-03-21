/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.validation;

import jakarta.validation.Validation;
import jakarta.validation.ValidationProviderResolver;
import jakarta.validation.Validator;
import jakarta.validation.spi.ValidationProvider;

import java.util.Collections;
import java.util.List;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;

/**
 * @author Javier Gamarra
 */
public class ValidatorFactory {

	public static Validator getValidator() {
		if (_validator != null) {
			return _validator;
		}

		HibernateValidatorConfiguration hibernateValidatorConfiguration =
			(HibernateValidatorConfiguration)Validation.byDefaultProvider(
			).providerResolver(
				new OSGiServiceDiscoverer()
			).configure();

		hibernateValidatorConfiguration.
			allowOverridingMethodAlterParameterConstraint(true);

		hibernateValidatorConfiguration.messageInterpolator(
			new ResourceBundleMessageInterpolator());

		jakarta.validation.ValidatorFactory validatorFactory =
			hibernateValidatorConfiguration.buildValidatorFactory();

		_validator = validatorFactory.getValidator();

		return _validator;
	}

	private static Validator _validator;

	private static class OSGiServiceDiscoverer
		implements ValidationProviderResolver {

		@Override
		public List<ValidationProvider<?>> getValidationProviders() {
			return Collections.singletonList(new HibernateValidator());
		}

	}

}