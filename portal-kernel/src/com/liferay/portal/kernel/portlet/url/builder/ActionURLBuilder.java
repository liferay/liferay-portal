/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet.url.builder;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionURL;
import jakarta.portlet.MimeResponse;
import jakarta.portlet.MutableActionParameters;
import jakarta.portlet.MutableRenderParameters;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletModeException;
import jakarta.portlet.PortletParameters;
import jakarta.portlet.PortletSecurityException;
import jakarta.portlet.WindowState;
import jakarta.portlet.WindowStateException;
import jakarta.portlet.annotations.PortletSerializable;

/**
 * @author Hugo Huijser
 * @author Neil Griffin
 */
public class ActionURLBuilder {

	public static ActionURLStep createActionURL(ActionURL actionURL) {
		return new ActionURLStep(actionURL);
	}

	public static ActionURLStep createActionURL(
		LiferayPortletResponse liferayPortletResponse) {

		return new ActionURLStep(liferayPortletResponse.createActionURL());
	}

	public static ActionURLStep createActionURL(
		LiferayPortletResponse liferayPortletResponse, MimeResponse.Copy copy) {

		return new ActionURLStep(liferayPortletResponse.createActionURL(copy));
	}

	public static ActionURLStep createActionURL(
		LiferayPortletResponse liferayPortletResponse, String portletName) {

		return new ActionURLStep(
			(ActionURL)liferayPortletResponse.createActionURL(portletName));
	}

	public static ActionURLStep createActionURL(
		LiferayPortletResponse liferayPortletResponse, String portletName,
		MimeResponse.Copy copy) {

		return new ActionURLStep(
			(ActionURL)liferayPortletResponse.createActionURL(
				portletName, copy));
	}

	public static ActionURLStep createActionURL(MimeResponse mimeResponse) {
		return new ActionURLStep(mimeResponse.createActionURL());
	}

	public static ActionURLStep createActionURL(
		MimeResponse mimeResponse, MimeResponse.Copy copy) {

		return new ActionURLStep(mimeResponse.createActionURL(copy));
	}

	public static class ActionURLStep
		implements ActionNameStep, AfterActionNameStep, AfterBackURLStep,
				   AfterBeanParameterStep, AfterCMDStep, AfterKeywordsStep,
				   AfterMVCPathStep, AfterNavigationStep, AfterParameterStep,
				   AfterPortletModeStep, AfterPortletResourceStep,
				   AfterRedirectStep, AfterRenderParameterStep, AfterSecureStep,
				   AfterTabs1Step, AfterTabs2Step, AfterWindowStateStep,
				   BackURLStep, BeanParameterStep, BuildStep, CMDStep,
				   KeywordsStep, MVCPathStep, NavigationStep, ParameterStep,
				   PortletModeStep, PortletResourceStep, RedirectStep,
				   RenderParameterStep, SecureStep, Tabs1Step, Tabs2Step,
				   WindowStateStep {

		public ActionURLStep(ActionURL actionURL) {
			_actionURL = actionURL;
		}

		@Override
		public ActionURL buildActionURL() {
			return _actionURL;
		}

		@Override
		public String buildString() {
			return _actionURL.toString();
		}

		@Override
		public AfterParameterStep removeActionParameter(String name) {
			MutableActionParameters mutableActionParameters =
				_actionURL.getActionParameters();

			mutableActionParameters.removeParameter(name);

			return this;
		}

		@Override
		public AfterRenderParameterStep removeRenderParameter(String name) {
			MutableRenderParameters mutableRenderParameters =
				_actionURL.getRenderParameters();

			mutableRenderParameters.removeParameter(name);

			return this;
		}

		@Override
		public AfterActionNameStep setActionName(String value) {
			_setParameter(ActionRequest.ACTION_NAME, value, false);

			return this;
		}

		@Override
		public AfterActionNameStep setActionName(
			String value, boolean allowNullValue) {

			if (allowNullValue || Validator.isNotNull(value)) {
				_setParameter(ActionRequest.ACTION_NAME, value, false);
			}

			return this;
		}

		@Override
		public AfterActionNameStep setActionName(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier) {

			_setParameter(
				ActionRequest.ACTION_NAME, valueUnsafeSupplier, false);

			return this;
		}

		@Override
		public AfterBackURLStep setBackURL(String value) {
			_setParameter("backURL", value, false);

			return this;
		}

		@Override
		public AfterBackURLStep setBackURL(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier) {

			_setParameter("backURL", valueUnsafeSupplier, false);

			return this;
		}

		@Override
		public AfterBeanParameterStep setBeanParameter(
			PortletSerializable portletSerializable) {

			_actionURL.setBeanParameter(portletSerializable);

			return this;
		}

		@Override
		public AfterCMDStep setCMD(String value) {
			_setParameter(Constants.CMD, value, false);

			return this;
		}

		@Override
		public AfterCMDStep setCMD(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier) {

			_setParameter(Constants.CMD, valueUnsafeSupplier, false);

			return this;
		}

		@Override
		public AfterKeywordsStep setKeywords(String value) {
			_setParameter("keywords", value, false);

			return this;
		}

		@Override
		public AfterKeywordsStep setKeywords(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier) {

			_setParameter("keywords", valueUnsafeSupplier, false);

			return this;
		}

		@Override
		public AfterMVCPathStep setMVCPath(String value) {
			_setParameter("mvcPath", value, false);

			return this;
		}

		@Override
		public AfterMVCPathStep setMVCPath(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier) {

			_setParameter("mvcPath", valueUnsafeSupplier, false);

			return this;
		}

		@Override
		public AfterNavigationStep setNavigation(String value) {
			_setParameter("navigation", value, false);

			return this;
		}

		@Override
		public AfterNavigationStep setNavigation(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier) {

			_setParameter("navigation", valueUnsafeSupplier, false);

			return this;
		}

		@Override
		public AfterParameterStep setParameter(String key, Object value) {
			_setParameter(key, String.valueOf(value), true);

			return this;
		}

		@Override
		public AfterParameterStep setParameter(
			String name, Object value, boolean allowNullValue) {

			setParameter(name, String.valueOf(value), allowNullValue);

			return this;
		}

		@Override
		public AfterParameterStep setParameter(String key, String value) {
			_setParameter(key, value, true);

			return this;
		}

		@Override
		public AfterParameterStep setParameter(String key, String... values) {
			MutableActionParameters mutableActionParameters =
				_actionURL.getActionParameters();

			mutableActionParameters.setValues(key, values);

			return this;
		}

		@Override
		public AfterParameterStep setParameter(
			String name, String value, boolean allowNullValue) {

			if (allowNullValue || Validator.isNotNull(value)) {
				_setParameter(name, value, true);
			}

			return this;
		}

		@Override
		public AfterParameterStep setParameter(
			String key, UnsafeSupplier<Object, Exception> valueUnsafeSupplier) {

			_setParameter(key, valueUnsafeSupplier, true);

			return this;
		}

		@Override
		public AfterParameterStep setParameters(
			PortletParameters portletParameters) {

			MutableActionParameters mutableActionParameters =
				_actionURL.getActionParameters();

			mutableActionParameters.set(portletParameters);

			return this;
		}

		@Override
		public AfterPortletModeStep setPortletMode(PortletMode portletMode) {
			try {
				_actionURL.setPortletMode(portletMode);
			}
			catch (PortletModeException portletModeException) {
				throw new SystemException(portletModeException);
			}

			return this;
		}

		@Override
		public AfterPortletResourceStep setPortletResource(String value) {
			_setParameter("portletResource", value, false);

			return this;
		}

		@Override
		public AfterPortletResourceStep setPortletResource(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier) {

			_setParameter("portletResource", valueUnsafeSupplier, false);

			return this;
		}

		@Override
		public AfterRedirectStep setRedirect(Object value) {
			_setParameter("redirect", String.valueOf(value), false);

			return this;
		}

		@Override
		public AfterRedirectStep setRedirect(String value) {
			_setParameter("redirect", value, false);

			return this;
		}

		@Override
		public AfterRedirectStep setRedirect(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier) {

			_setParameter("redirect", valueUnsafeSupplier, false);

			return this;
		}

		@Override
		public AfterRenderParameterStep setRenderParameter(
			String key, Object value) {

			_setRenderParameter(key, String.valueOf(value), true);

			return this;
		}

		@Override
		public AfterRenderParameterStep setRenderParameter(
			String name, Object value, boolean allowNullValue) {

			setRenderParameter(name, String.valueOf(value), allowNullValue);

			return this;
		}

		@Override
		public AfterRenderParameterStep setRenderParameter(
			String key, String value) {

			_setRenderParameter(key, value, true);

			return this;
		}

		@Override
		public AfterRenderParameterStep setRenderParameter(
			String key, String... values) {

			MutableRenderParameters mutableRenderParameters =
				_actionURL.getRenderParameters();

			mutableRenderParameters.setValues(key, values);

			return this;
		}

		@Override
		public AfterRenderParameterStep setRenderParameter(
			String name, String value, boolean allowNullValue) {

			if (allowNullValue || Validator.isNotNull(value)) {
				_setRenderParameter(name, value, true);
			}

			return this;
		}

		@Override
		public AfterRenderParameterStep setRenderParameter(
			String key, UnsafeSupplier<Object, Exception> valueUnsafeSupplier) {

			_setRenderParameter(key, valueUnsafeSupplier, true);

			return this;
		}

		@Override
		public AfterRenderParameterStep setRenderParameters(
			PortletParameters portletParameters) {

			MutableRenderParameters mutableRenderParameters =
				_actionURL.getRenderParameters();

			mutableRenderParameters.set(portletParameters);

			return this;
		}

		@Override
		public AfterSecureStep setSecure(boolean secure) {
			try {
				_actionURL.setSecure(secure);
			}
			catch (PortletSecurityException portletSecurityException) {
				throw new SystemException(portletSecurityException);
			}

			return this;
		}

		@Override
		public AfterTabs1Step setTabs1(String value) {
			_setParameter("tabs1", value, false);

			return this;
		}

		@Override
		public AfterTabs1Step setTabs1(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier) {

			_setParameter("tabs1", valueUnsafeSupplier, false);

			return this;
		}

		@Override
		public AfterTabs2Step setTabs2(String value) {
			_setParameter("tabs2", value, false);

			return this;
		}

		@Override
		public AfterTabs2Step setTabs2(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier) {

			_setParameter("tabs2", valueUnsafeSupplier, false);

			return this;
		}

		@Override
		public AfterWindowStateStep setWindowState(WindowState windowState) {
			try {
				_actionURL.setWindowState(windowState);
			}
			catch (WindowStateException windowStateException) {
				throw new SystemException(windowStateException);
			}

			return this;
		}

		private void _setParameter(
			String key, String value, boolean validateKey) {

			if (validateKey) {
				_validateKey(key);
			}

			MutableActionParameters mutableActionParameters =
				_actionURL.getActionParameters();

			mutableActionParameters.setValue(key, value);
		}

		private void _setParameter(
			String key, UnsafeSupplier<Object, Exception> valueUnsafeSupplier,
			boolean validateKey) {

			if (validateKey) {
				_validateKey(key);
			}

			try {
				Object value = valueUnsafeSupplier.get();

				if (value == null) {
					return;
				}

				MutableActionParameters mutableActionParameters =
					_actionURL.getActionParameters();

				if (value instanceof String[]) {
					mutableActionParameters.setValues(key, (String[])value);
				}
				else {
					mutableActionParameters.setValue(
						key, String.valueOf(value));
				}
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		private void _setRenderParameter(
			String key, String value, boolean validateKey) {

			if (validateKey) {
				_validateKey(key);
			}

			MutableRenderParameters mutableRenderParameters =
				_actionURL.getRenderParameters();

			mutableRenderParameters.setValue(key, value);
		}

		private void _setRenderParameter(
			String key, UnsafeSupplier<Object, Exception> valueUnsafeSupplier,
			boolean validateKey) {

			if (validateKey) {
				_validateKey(key);
			}

			try {
				Object value = valueUnsafeSupplier.get();

				if (value == null) {
					return;
				}

				MutableRenderParameters mutableRenderParameters =
					_actionURL.getRenderParameters();

				if (value instanceof String[]) {
					mutableRenderParameters.setValues(key, (String[])value);
				}
				else {
					mutableRenderParameters.setValue(
						key, String.valueOf(value));
				}
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		private void _validateKey(String key) {
			if (key == null) {
				return;
			}

			for (String[] reservedKeywordArray : _RESERVED_KEYWORDS) {
				String reservedKey = reservedKeywordArray[0];

				if (key.equals(reservedKey)) {
					throw new RuntimeException(
						StringBundler.concat(
							"Use method \"", reservedKeywordArray[1],
							"\" when setting value for \"", reservedKey, "\""));
				}
			}
		}

		private static final String[][] _RESERVED_KEYWORDS = {
			{ActionRequest.ACTION_NAME, "setActionName"},
			{Constants.CMD, "setCMD"}, {"backURL", "setBackURL"},
			{"keywords", "setKeywords"}, {"mvcPath", "setMVCPath"},
			{"navigation", "setNavigation"}, {"p_p_mode", "setPortletMode"},
			{"p_p_state", "setWindowState"},
			{"portletResource", "setPortletResource"},
			{"redirect", "setRedirect"}, {"tabs1", "setTabs1"},
			{"tabs2", "setTabs2"}
		};

		private final ActionURL _actionURL;

	}

	public interface ActionNameStep {

		public AfterActionNameStep setActionName(String value);

		public AfterActionNameStep setActionName(
			String value, boolean allowNullValue);

		public AfterActionNameStep setActionName(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier);

	}

	public interface AfterActionNameStep
		extends BackURLStep, BeanParameterStep, BuildStep, CMDStep,
				KeywordsStep, NavigationStep, ParameterStep, PortletModeStep,
				PortletResourceStep, RedirectStep, RenderParameterStep,
				SecureStep, Tabs1Step, Tabs2Step, WindowStateStep {
	}

	public interface AfterBackURLStep
		extends BeanParameterStep, BuildStep, KeywordsStep, NavigationStep,
				ParameterStep, PortletModeStep, PortletResourceStep,
				RenderParameterStep, SecureStep, Tabs1Step, Tabs2Step,
				WindowStateStep {
	}

	public interface AfterBeanParameterStep
		extends BuildStep, ParameterStep, PortletModeStep, RenderParameterStep,
				SecureStep, WindowStateStep {
	}

	public interface AfterCMDStep
		extends BackURLStep, BeanParameterStep, BuildStep, KeywordsStep,
				NavigationStep, ParameterStep, PortletModeStep,
				PortletResourceStep, RedirectStep, RenderParameterStep,
				SecureStep, Tabs1Step, Tabs2Step, WindowStateStep {
	}

	public interface AfterKeywordsStep
		extends BeanParameterStep, BuildStep, NavigationStep, ParameterStep,
				PortletModeStep, PortletResourceStep, RenderParameterStep,
				SecureStep, Tabs1Step, Tabs2Step, WindowStateStep {
	}

	public interface AfterMVCPathStep
		extends ActionNameStep, BackURLStep, BeanParameterStep, BuildStep,
				CMDStep, KeywordsStep, NavigationStep, ParameterStep,
				PortletModeStep, PortletResourceStep, RedirectStep,
				RenderParameterStep, SecureStep, Tabs1Step, Tabs2Step,
				WindowStateStep {
	}

	public interface AfterNavigationStep
		extends BeanParameterStep, BuildStep, ParameterStep, PortletModeStep,
				PortletResourceStep, RenderParameterStep, SecureStep, Tabs1Step,
				Tabs2Step, WindowStateStep {
	}

	public interface AfterParameterStep
		extends BuildStep, ParameterStep, PortletModeStep, RenderParameterStep,
				SecureStep, WindowStateStep {
	}

	public interface AfterPortletModeStep
		extends BuildStep, RenderParameterStep, SecureStep, WindowStateStep {
	}

	public interface AfterPortletResourceStep
		extends BeanParameterStep, BuildStep, ParameterStep, PortletModeStep,
				RenderParameterStep, SecureStep, Tabs1Step, Tabs2Step,
				WindowStateStep {
	}

	public interface AfterRedirectStep
		extends BackURLStep, BeanParameterStep, BuildStep, KeywordsStep,
				NavigationStep, ParameterStep, PortletModeStep,
				PortletResourceStep, RenderParameterStep, SecureStep, Tabs1Step,
				Tabs2Step, WindowStateStep {
	}

	public interface AfterRenderParameterStep
		extends BuildStep, RenderParameterStep, SecureStep, WindowStateStep {
	}

	public interface AfterSecureStep extends BuildStep, WindowStateStep {
	}

	public interface AfterTabs1Step
		extends BeanParameterStep, BuildStep, ParameterStep, PortletModeStep,
				RenderParameterStep, SecureStep, Tabs2Step, WindowStateStep {
	}

	public interface AfterTabs2Step
		extends BeanParameterStep, BuildStep, ParameterStep, PortletModeStep,
				RenderParameterStep, SecureStep, WindowStateStep {
	}

	public interface AfterWindowStateStep extends BuildStep {
	}

	public interface BackURLStep {

		public AfterBackURLStep setBackURL(String value);

		public AfterBackURLStep setBackURL(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier);

	}

	public interface BeanParameterStep {

		public AfterBeanParameterStep setBeanParameter(
			PortletSerializable portletSerializable);

	}

	public interface BuildStep {

		public ActionURL buildActionURL();

		public String buildString();

	}

	public interface CMDStep {

		public AfterCMDStep setCMD(String value);

		public AfterCMDStep setCMD(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier);

	}

	public interface KeywordsStep {

		public AfterKeywordsStep setKeywords(String value);

		public AfterKeywordsStep setKeywords(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier);

	}

	public interface MVCPathStep {

		public AfterMVCPathStep setMVCPath(String value);

		public AfterMVCPathStep setMVCPath(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier);

	}

	public interface NavigationStep {

		public AfterNavigationStep setNavigation(String value);

		public AfterNavigationStep setNavigation(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier);

	}

	public interface ParameterStep {

		public AfterParameterStep removeActionParameter(String name);

		public AfterParameterStep setParameter(String key, Object value);

		public AfterParameterStep setParameter(
			String key, Object value, boolean allowNullValue);

		public AfterParameterStep setParameter(String key, String value);

		public AfterParameterStep setParameter(String key, String... values);

		public AfterParameterStep setParameter(
			String key, String value, boolean allowNullValue);

		public AfterParameterStep setParameter(
			String key, UnsafeSupplier<Object, Exception> valueUnsafeSupplier);

		public AfterParameterStep setParameters(
			PortletParameters portletParameters);

	}

	public interface PortletModeStep {

		public AfterPortletModeStep setPortletMode(PortletMode portletMode);

	}

	public interface PortletResourceStep {

		public AfterPortletResourceStep setPortletResource(String value);

		public AfterPortletResourceStep setPortletResource(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier);

	}

	public interface RedirectStep {

		public AfterRedirectStep setRedirect(Object value);

		public AfterRedirectStep setRedirect(String value);

		public AfterRedirectStep setRedirect(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier);

	}

	public interface RenderParameterStep {

		public AfterRenderParameterStep removeRenderParameter(String name);

		public AfterRenderParameterStep setRenderParameter(
			String key, Object value);

		public AfterRenderParameterStep setRenderParameter(
			String key, Object value, boolean allowNullValue);

		public AfterRenderParameterStep setRenderParameter(
			String key, String value);

		public AfterRenderParameterStep setRenderParameter(
			String key, String... values);

		public AfterRenderParameterStep setRenderParameter(
			String key, String value, boolean allowNullValue);

		public AfterRenderParameterStep setRenderParameter(
			String key, UnsafeSupplier<Object, Exception> valueUnsafeSupplier);

		public AfterRenderParameterStep setRenderParameters(
			PortletParameters portletParameters);

	}

	public interface SecureStep {

		public AfterSecureStep setSecure(boolean secure);

	}

	public interface Tabs1Step {

		public AfterTabs1Step setTabs1(String value);

		public AfterTabs1Step setTabs1(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier);

	}

	public interface Tabs2Step {

		public AfterTabs2Step setTabs2(String value);

		public AfterTabs2Step setTabs2(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier);

	}

	@FunctionalInterface
	public interface UnsafeSupplier<T, E extends Throwable> {

		public T get() throws E;

	}

	public interface WindowStateStep {

		public AfterWindowStateStep setWindowState(WindowState windowState);

	}

}