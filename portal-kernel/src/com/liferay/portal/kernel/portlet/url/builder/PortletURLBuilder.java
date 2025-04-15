/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet.url.builder;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionURL;
import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletModeException;
import jakarta.portlet.PortletSecurityException;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderURL;
import jakarta.portlet.WindowState;
import jakarta.portlet.WindowStateException;

import java.util.Map;

/**
 * @author Hugo Huijser
 */
public class PortletURLBuilder {

	public static PortletURLStep create(PortletURL portletURL) {
		return new PortletURLStep(portletURL);
	}

	public static PortletURLStep createActionURL(
		LiferayPortletResponse liferayPortletResponse) {

		return new PortletURLStep(liferayPortletResponse.createActionURL());
	}

	public static PortletURLStep createActionURL(
		LiferayPortletResponse liferayPortletResponse, MimeResponse.Copy copy) {

		return new PortletURLStep(liferayPortletResponse.createActionURL(copy));
	}

	public static PortletURLStep createActionURL(
		LiferayPortletResponse liferayPortletResponse, String portletName) {

		return new PortletURLStep(
			liferayPortletResponse.createActionURL(portletName));
	}

	public static PortletURLStep createActionURL(
		LiferayPortletResponse liferayPortletResponse, String portletName,
		MimeResponse.Copy copy) {

		return new PortletURLStep(
			liferayPortletResponse.createActionURL(portletName, copy));
	}

	public static PortletURLStep createActionURL(MimeResponse mimeResponse) {
		return new PortletURLStep(mimeResponse.createActionURL());
	}

	public static PortletURLStep createActionURL(
		MimeResponse mimeResponse, MimeResponse.Copy copy) {

		return new PortletURLStep(mimeResponse.createActionURL(copy));
	}

	public static PortletURLStep createLiferayPortletURL(
		LiferayPortletResponse liferayPortletResponse, long plid,
		String portletName, String lifecycle) {

		return new PortletURLStep(
			liferayPortletResponse.createLiferayPortletURL(
				plid, portletName, lifecycle));
	}

	public static PortletURLStep createLiferayPortletURL(
		LiferayPortletResponse liferayPortletResponse, long plid,
		String portletName, String lifecycle, boolean includeLinkToLayoutUuid) {

		return new PortletURLStep(
			liferayPortletResponse.createLiferayPortletURL(
				plid, portletName, lifecycle, includeLinkToLayoutUuid));
	}

	public static PortletURLStep createLiferayPortletURL(
		LiferayPortletResponse liferayPortletResponse, long plid,
		String portletName, String lifecycle, MimeResponse.Copy copy) {

		return new PortletURLStep(
			liferayPortletResponse.createLiferayPortletURL(
				plid, portletName, lifecycle, copy));
	}

	public static PortletURLStep createLiferayPortletURL(
		LiferayPortletResponse liferayPortletResponse, long plid,
		String portletName, String lifecycle, MimeResponse.Copy copy,
		boolean includeLinkToLayoutUuid) {

		return new PortletURLStep(
			liferayPortletResponse.createLiferayPortletURL(
				plid, portletName, lifecycle, copy, includeLinkToLayoutUuid));
	}

	public static PortletURLStep createLiferayPortletURL(
		LiferayPortletResponse liferayPortletResponse, String lifecycle) {

		return new PortletURLStep(
			liferayPortletResponse.createLiferayPortletURL(lifecycle));
	}

	public static PortletURLStep createLiferayPortletURL(
		LiferayPortletResponse liferayPortletResponse, String portletName,
		String lifecycle) {

		return new PortletURLStep(
			liferayPortletResponse.createLiferayPortletURL(
				portletName, lifecycle));
	}

	public static PortletURLStep createLiferayPortletURL(
		LiferayPortletResponse liferayPortletResponse, String portletName,
		String lifecycle, MimeResponse.Copy copy) {

		return new PortletURLStep(
			liferayPortletResponse.createLiferayPortletURL(
				portletName, lifecycle, copy));
	}

	public static PortletURLStep createRenderURL(
		LiferayPortletResponse liferayPortletResponse) {

		return new PortletURLStep(liferayPortletResponse.createRenderURL());
	}

	public static PortletURLStep createRenderURL(
		LiferayPortletResponse liferayPortletResponse, MimeResponse.Copy copy) {

		return new PortletURLStep(liferayPortletResponse.createRenderURL(copy));
	}

	public static PortletURLStep createRenderURL(
		LiferayPortletResponse liferayPortletResponse, String portletName) {

		return new PortletURLStep(
			liferayPortletResponse.createRenderURL(portletName));
	}

	public static PortletURLStep createRenderURL(
		LiferayPortletResponse liferayPortletResponse, String portletName,
		MimeResponse.Copy copy) {

		return new PortletURLStep(
			liferayPortletResponse.createRenderURL(portletName, copy));
	}

	public static PortletURLStep createRenderURL(MimeResponse mimeResponse) {
		return new PortletURLStep(mimeResponse.createRenderURL());
	}

	public static PortletURLStep createRenderURL(
		MimeResponse mimeResponse, MimeResponse.Copy copy) {

		return new PortletURLStep(mimeResponse.createRenderURL(copy));
	}

	public static class PortletURLStep
		implements ActionNameStep, AfterActionNameStep, AfterBackURLStep,
				   AfterCMDStep, AfterGlobalParameterStep, AfterKeywordsStep,
				   AfterMVCPathStep, AfterMVCRenderCommandNameStep,
				   AfterNavigationStep, AfterParameterStep,
				   AfterPortletModeStep, AfterPortletResourceStep,
				   AfterRedirectStep, AfterSecureStep, AfterTabs1Step,
				   AfterTabs2Step, AfterWindowStateStep, BackURLStep, BuildStep,
				   CMDStep, GlobalParameterStep, KeywordsStep, MVCPathStep,
				   MVCRenderCommandNameStep, NavigationStep, ParameterStep,
				   PortletModeStep, PortletResourceStep, RedirectStep,
				   SecureStep, Tabs1Step, Tabs2Step, WindowStateStep {

		public PortletURLStep(PortletURL portletURL) {
			_portletURL = portletURL;
		}

		/**
		 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
		 *             #buildPortletURL()}
		 */
		@Deprecated
		@Override
		public PortletURL build() {
			return _portletURL;
		}

		@Override
		public ActionURL buildActionURL() {
			return (ActionURL)_portletURL;
		}

		@Override
		public PortletURL buildPortletURL() {
			return _portletURL;
		}

		@Override
		public RenderURL buildRenderURL() {
			return (RenderURL)_portletURL;
		}

		@Override
		public String buildString() {
			return _portletURL.toString();
		}

		@Override
		public AfterActionNameStep setActionName(String value) {
			_setParameter(ActionRequest.ACTION_NAME, value, false);

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
		public AfterGlobalParameterStep setGlobalParameter(
			String key, boolean value) {

			setGlobalParameter(key, String.valueOf(value));

			return this;
		}

		@Override
		public AfterGlobalParameterStep setGlobalParameter(
			String key, double value) {

			setGlobalParameter(key, String.valueOf(value));

			return this;
		}

		@Override
		public AfterGlobalParameterStep setGlobalParameter(
			String key, int value) {

			setGlobalParameter(key, String.valueOf(value));

			return this;
		}

		@Override
		public AfterGlobalParameterStep setGlobalParameter(
			String key, long value) {

			setGlobalParameter(key, String.valueOf(value));

			return this;
		}

		@Override
		public AfterGlobalParameterStep setGlobalParameter(
			String key, short value) {

			setGlobalParameter(key, String.valueOf(value));

			return this;
		}

		@Override
		public AfterGlobalParameterStep setGlobalParameter(
			String key, String value) {

			_setParameter(key, URLCodec.encodeURL(value), false);

			return this;
		}

		@Override
		public AfterGlobalParameterStep setGlobalParameter(
			String key, UnsafeSupplier<Object, Exception> valueUnsafeSupplier) {

			_setParameter(key, valueUnsafeSupplier, false);

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
		public AfterMVCRenderCommandNameStep setMVCRenderCommandName(
			String value) {

			_setParameter("mvcRenderCommandName", value, false);

			return this;
		}

		@Override
		public AfterMVCRenderCommandNameStep setMVCRenderCommandName(
			String value, boolean allowNullValue) {

			if (allowNullValue || Validator.isNotNull(value)) {
				_setParameter("mvcRenderCommandName", value, false);
			}

			return this;
		}

		@Override
		public AfterMVCRenderCommandNameStep setMVCRenderCommandName(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier) {

			_setParameter("mvcRenderCommandName", valueUnsafeSupplier, false);

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
			_portletURL.setParameter(key, values);

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
			Map<String, String[]> parameters) {

			_portletURL.setParameters(parameters);

			return this;
		}

		@Override
		public AfterPortletModeStep setPortletMode(PortletMode portletMode) {
			try {
				_portletURL.setPortletMode(portletMode);
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
		public AfterSecureStep setSecure(boolean secure) {
			try {
				_portletURL.setSecure(secure);
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
				_portletURL.setWindowState(windowState);
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

			_portletURL.setParameter(key, value);
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

				if (value instanceof String[]) {
					_portletURL.setParameter(key, (String[])value);
				}
				else {
					_portletURL.setParameter(key, String.valueOf(value));
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
			{"mvcRenderCommandName", "setMVCRenderCommandName"},
			{"navigation", "setNavigation"}, {"p_p_mode", "setPortletMode"},
			{"p_p_state", "setWindowState"},
			{"portletResource", "setPortletResource"},
			{"redirect", "setRedirect"}, {"tabs1", "setTabs1"},
			{"tabs2", "setTabs2"}
		};

		private final PortletURL _portletURL;

	}

	public interface ActionNameStep {

		public AfterActionNameStep setActionName(String value);

		public AfterActionNameStep setActionName(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier);

	}

	public interface AfterActionNameStep
		extends BackURLStep, BuildStep, CMDStep, GlobalParameterStep,
				KeywordsStep, MVCPathStep, MVCRenderCommandNameStep,
				NavigationStep, ParameterStep, PortletModeStep,
				PortletResourceStep, RedirectStep, SecureStep, Tabs1Step,
				Tabs2Step, WindowStateStep {
	}

	public interface AfterBackURLStep
		extends BuildStep, GlobalParameterStep, KeywordsStep, NavigationStep,
				ParameterStep, PortletModeStep, PortletResourceStep, SecureStep,
				Tabs1Step, Tabs2Step, WindowStateStep {
	}

	public interface AfterCMDStep
		extends BackURLStep, BuildStep, GlobalParameterStep, KeywordsStep,
				NavigationStep, ParameterStep, PortletModeStep,
				PortletResourceStep, RedirectStep, SecureStep, Tabs1Step,
				Tabs2Step, WindowStateStep {
	}

	public interface AfterGlobalParameterStep
		extends BuildStep, GlobalParameterStep, ParameterStep, PortletModeStep,
				SecureStep, WindowStateStep {
	}

	public interface AfterKeywordsStep
		extends BuildStep, GlobalParameterStep, NavigationStep, ParameterStep,
				PortletModeStep, PortletResourceStep, SecureStep, Tabs1Step,
				Tabs2Step, WindowStateStep {
	}

	public interface AfterMVCPathStep
		extends BackURLStep, BuildStep, CMDStep, GlobalParameterStep,
				KeywordsStep, MVCRenderCommandNameStep, NavigationStep,
				ParameterStep, PortletModeStep, PortletResourceStep,
				RedirectStep, SecureStep, Tabs1Step, Tabs2Step,
				WindowStateStep {
	}

	public interface AfterMVCRenderCommandNameStep
		extends BackURLStep, BuildStep, CMDStep, GlobalParameterStep,
				KeywordsStep, NavigationStep, ParameterStep, PortletModeStep,
				PortletResourceStep, RedirectStep, SecureStep, Tabs1Step,
				Tabs2Step, WindowStateStep {
	}

	public interface AfterNavigationStep
		extends BuildStep, GlobalParameterStep, ParameterStep, PortletModeStep,
				PortletResourceStep, SecureStep, Tabs1Step, Tabs2Step,
				WindowStateStep {
	}

	public interface AfterParameterStep
		extends BuildStep, GlobalParameterStep, ParameterStep, PortletModeStep,
				SecureStep, WindowStateStep {
	}

	public interface AfterPortletModeStep
		extends BuildStep, SecureStep, WindowStateStep {
	}

	public interface AfterPortletResourceStep
		extends BuildStep, GlobalParameterStep, ParameterStep, PortletModeStep,
				SecureStep, Tabs1Step, Tabs2Step, WindowStateStep {
	}

	public interface AfterRedirectStep
		extends BackURLStep, BuildStep, GlobalParameterStep, KeywordsStep,
				NavigationStep, ParameterStep, PortletModeStep,
				PortletResourceStep, SecureStep, Tabs1Step, Tabs2Step,
				WindowStateStep {
	}

	public interface AfterSecureStep extends BuildStep, WindowStateStep {
	}

	public interface AfterTabs1Step
		extends BuildStep, GlobalParameterStep, ParameterStep, PortletModeStep,
				SecureStep, Tabs2Step, WindowStateStep {
	}

	public interface AfterTabs2Step
		extends BuildStep, GlobalParameterStep, ParameterStep, PortletModeStep,
				SecureStep, WindowStateStep {
	}

	public interface AfterWindowStateStep extends BuildStep {
	}

	public interface BackURLStep {

		public AfterBackURLStep setBackURL(String value);

		public AfterBackURLStep setBackURL(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier);

	}

	public interface BuildStep {

		/**
		 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
		 *             #buildPortletURL()}
		 */
		@Deprecated
		public PortletURL build();

		public ActionURL buildActionURL();

		public PortletURL buildPortletURL();

		public RenderURL buildRenderURL();

		public String buildString();

	}

	public interface CMDStep {

		public AfterCMDStep setCMD(String value);

		public AfterCMDStep setCMD(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier);

	}

	public interface GlobalParameterStep {

		public AfterGlobalParameterStep setGlobalParameter(
			String key, boolean value);

		public AfterGlobalParameterStep setGlobalParameter(
			String key, double value);

		public AfterGlobalParameterStep setGlobalParameter(
			String key, int value);

		public AfterGlobalParameterStep setGlobalParameter(
			String key, long value);

		public AfterGlobalParameterStep setGlobalParameter(
			String key, short value);

		public AfterGlobalParameterStep setGlobalParameter(
			String key, String value);

		public AfterGlobalParameterStep setGlobalParameter(
			String key, UnsafeSupplier<Object, Exception> valueUnsafeSupplier);

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

	public interface MVCRenderCommandNameStep {

		public AfterMVCRenderCommandNameStep setMVCRenderCommandName(
			String value);

		public AfterMVCRenderCommandNameStep setMVCRenderCommandName(
			String value, boolean allowNullValue);

		public AfterMVCRenderCommandNameStep setMVCRenderCommandName(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier);

	}

	public interface NavigationStep {

		public AfterNavigationStep setNavigation(String value);

		public AfterNavigationStep setNavigation(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier);

	}

	public interface ParameterStep {

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
			Map<String, String[]> parameters);

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