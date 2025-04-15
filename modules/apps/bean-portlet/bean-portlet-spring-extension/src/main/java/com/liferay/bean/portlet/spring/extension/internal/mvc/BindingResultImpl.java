/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.mvc;

import jakarta.annotation.ManagedBean;

import jakarta.mvc.binding.BindingError;
import jakarta.mvc.binding.ParamError;
import jakarta.mvc.binding.ValidationError;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

/**
 * @author Neil Griffin
 */
@ManagedBean
@Scope(proxyMode = ScopedProxyMode.INTERFACES, value = "portletRedirect")
public class BindingResultImpl implements MutableBindingResult, Serializable {

	@Override
	public void addBindingError(BindingError bindingError) {
		_bindingErrors.add(bindingError);
	}

	@Override
	public void addValidationError(ValidationError validationError) {
		_validationErrors.add(validationError);
	}

	@Override
	public Set<ParamError> getAllErrors() {
		_consulted = true;

		Set<ParamError> allParamErrors = new LinkedHashSet<>();

		allParamErrors.addAll(_bindingErrors);
		allParamErrors.addAll(_validationErrors);

		return allParamErrors;
	}

	@Override
	public List<String> getAllMessages() {
		_consulted = true;

		List<String> allMessages = new ArrayList<>();

		for (BindingError bindingError : _bindingErrors) {
			allMessages.add(bindingError.getMessage());
		}

		for (ValidationError validationError : _validationErrors) {
			allMessages.add(validationError.getMessage());
		}

		return allMessages;
	}

	@Override
	public Set<ParamError> getErrors(String paramName) {
		_consulted = true;

		Set<ParamError> paramErrors = new LinkedHashSet<>();

		for (BindingError bindingError : _bindingErrors) {
			if (Objects.equals(bindingError.getParamName(), paramName)) {
				paramErrors.add(bindingError);
			}
		}

		for (ValidationError validationError : _validationErrors) {
			if (Objects.equals(validationError.getParamName(), paramName)) {
				paramErrors.add(validationError);
			}
		}

		return paramErrors;
	}

	@Override
	public boolean isConsulted() {
		return _consulted;
	}

	@Override
	public boolean isFailed() {
		_consulted = true;

		if (!_bindingErrors.isEmpty() || !_validationErrors.isEmpty()) {
			return true;
		}

		return false;
	}

	private static final long serialVersionUID = 2217732469752342741L;

	private final Set<BindingError> _bindingErrors = new LinkedHashSet<>();
	private boolean _consulted;
	private final Set<ValidationError> _validationErrors =
		new LinkedHashSet<>();

}