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

import jakarta.portlet.MimeResponse;
import jakarta.portlet.MutableResourceParameters;
import jakarta.portlet.PortletParameters;
import jakarta.portlet.PortletSecurityException;
import jakarta.portlet.ResourceURL;

/**
 * @author Hugo Huijser
 * @author Neil Griffin
 */
public class ResourceURLBuilder {

	public static ResourceURLStep createResourceURL(
		LiferayPortletResponse liferayPortletResponse) {

		return new ResourceURLStep(liferayPortletResponse.createResourceURL());
	}

	public static ResourceURLStep createResourceURL(
		LiferayPortletResponse liferayPortletResponse, String portletName) {

		return new ResourceURLStep(
			(ResourceURL)liferayPortletResponse.createResourceURL(portletName));
	}

	public static ResourceURLStep createResourceURL(MimeResponse mimeResponse) {
		return new ResourceURLStep(mimeResponse.createResourceURL());
	}

	public static ResourceURLStep createResourceURL(ResourceURL resourceURL) {
		return new ResourceURLStep(resourceURL);
	}

	public static class ResourceURLStep
		implements AfterBackURLStep, AfterCacheabilityStep, AfterCMDStep,
				   AfterKeywordsStep, AfterMVCPathStep,
				   AfterMVCResourceCommandNameStep, AfterNavigationStep,
				   AfterParameterStep, AfterPortletResourceStep,
				   AfterRedirectStep, AfterResourceIDStep, AfterSecureStep,
				   AfterTabs1Step, AfterTabs2Step, BackURLStep, BuildStep,
				   CacheabilityStep, CMDStep, KeywordsStep, MVCPathStep,
				   MVCResourceCommandNameStep, NavigationStep, ParameterStep,
				   PortletResourceStep, RedirectStep, ResourceIDStep,
				   SecureStep, Tabs1Step, Tabs2Step {

		public ResourceURLStep(ResourceURL resourceURL) {
			_resourceURL = resourceURL;
		}

		@Override
		public ResourceURL buildResourceURL() {
			return _resourceURL;
		}

		@Override
		public String buildString() {
			return _resourceURL.toString();
		}

		@Override
		public AfterParameterStep removeParameter(String name) {
			MutableResourceParameters mutableResourceParameters =
				_resourceURL.getResourceParameters();

			mutableResourceParameters.removeParameter(name);

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
		public AfterCacheabilityStep setCacheability(String cacheLevel) {
			_resourceURL.setCacheability(cacheLevel);

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
		public AfterMVCResourceCommandNameStep setMVCResourceCommandName(
			String value) {

			_setParameter("mvcResourceCommandName", value, false);

			return this;
		}

		@Override
		public AfterMVCResourceCommandNameStep setMVCResourceCommandName(
			String value, boolean allowNullValue) {

			if (allowNullValue || Validator.isNotNull(value)) {
				_setParameter("mvcResourceCommandName", value, false);
			}

			return this;
		}

		@Override
		public AfterMVCResourceCommandNameStep setMVCResourceCommandName(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier) {

			_setParameter("mvcResourceCommandName", valueUnsafeSupplier, false);

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
			MutableResourceParameters mutableResourceParameters =
				_resourceURL.getResourceParameters();

			mutableResourceParameters.setValues(key, values);

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

			MutableResourceParameters mutableResourceParameters =
				_resourceURL.getResourceParameters();

			mutableResourceParameters.set(portletParameters);

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
		public AfterResourceIDStep setResourceID(String resourceID) {
			_resourceURL.setResourceID(resourceID);

			return this;
		}

		@Override
		public AfterSecureStep setSecure(boolean secure) {
			try {
				_resourceURL.setSecure(secure);
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

		private void _setParameter(
			String key, String value, boolean validateKey) {

			if (validateKey) {
				_validateKey(key);
			}

			MutableResourceParameters mutableResourceParameters =
				_resourceURL.getResourceParameters();

			mutableResourceParameters.setValue(key, value);
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

				MutableResourceParameters mutableResourceParameters =
					_resourceURL.getResourceParameters();

				if (value instanceof String[]) {
					mutableResourceParameters.setValues(key, (String[])value);
				}
				else {
					mutableResourceParameters.setValue(
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
			{Constants.CMD, "setCMD"}, {"backURL", "setBackURL"},
			{"keywords", "setKeywords"}, {"mvcPath", "setMVCPath"},
			{"mvcResourceCommandName", "setMVCResourceCommandName"},
			{"navigation", "setNavigation"},
			{"portletResource", "setPortletResource"},
			{"redirect", "setRedirect"}, {"tabs1", "setTabs1"},
			{"tabs2", "setTabs2"}
		};

		private final ResourceURL _resourceURL;

	}

	public interface AfterBackURLStep
		extends BuildStep, CacheabilityStep, KeywordsStep, NavigationStep,
				ParameterStep, PortletResourceStep, ResourceIDStep, SecureStep,
				Tabs1Step, Tabs2Step {
	}

	public interface AfterCacheabilityStep
		extends BuildStep, ResourceIDStep, SecureStep {
	}

	public interface AfterCMDStep
		extends BackURLStep, BuildStep, CacheabilityStep, KeywordsStep,
				NavigationStep, ParameterStep, PortletResourceStep,
				RedirectStep, ResourceIDStep, SecureStep, Tabs1Step, Tabs2Step {
	}

	public interface AfterKeywordsStep
		extends BuildStep, CacheabilityStep, NavigationStep, ParameterStep,
				PortletResourceStep, ResourceIDStep, SecureStep, Tabs1Step,
				Tabs2Step {
	}

	public interface AfterMVCPathStep
		extends BackURLStep, BuildStep, CacheabilityStep, CMDStep, KeywordsStep,
				MVCResourceCommandNameStep, NavigationStep, ParameterStep,
				PortletResourceStep, RedirectStep, ResourceIDStep, SecureStep,
				Tabs1Step, Tabs2Step {
	}

	public interface AfterMVCResourceCommandNameStep
		extends BackURLStep, BuildStep, CacheabilityStep, CMDStep, KeywordsStep,
				NavigationStep, ParameterStep, PortletResourceStep,
				RedirectStep, ResourceIDStep, SecureStep, Tabs1Step, Tabs2Step {
	}

	public interface AfterNavigationStep
		extends BuildStep, CacheabilityStep, ParameterStep, PortletResourceStep,
				ResourceIDStep, SecureStep, Tabs1Step, Tabs2Step {
	}

	public interface AfterParameterStep
		extends BuildStep, CacheabilityStep, ParameterStep, ResourceIDStep,
				SecureStep {
	}

	public interface AfterPortletResourceStep
		extends BuildStep, CacheabilityStep, ParameterStep, ResourceIDStep,
				SecureStep, Tabs1Step, Tabs2Step {
	}

	public interface AfterRedirectStep
		extends BackURLStep, BuildStep, CacheabilityStep, KeywordsStep,
				NavigationStep, ParameterStep, PortletResourceStep,
				ResourceIDStep, SecureStep, Tabs1Step, Tabs2Step {
	}

	public interface AfterResourceIDStep extends BuildStep, SecureStep {
	}

	public interface AfterSecureStep extends BuildStep {
	}

	public interface AfterTabs1Step
		extends BuildStep, CacheabilityStep, ParameterStep, ResourceIDStep,
				SecureStep, Tabs2Step {
	}

	public interface AfterTabs2Step
		extends BuildStep, CacheabilityStep, ParameterStep, ResourceIDStep,
				SecureStep {
	}

	public interface BackURLStep {

		public AfterBackURLStep setBackURL(String value);

		public AfterBackURLStep setBackURL(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier);

	}

	public interface BuildStep {

		public ResourceURL buildResourceURL();

		public String buildString();

	}

	public interface CacheabilityStep {

		public AfterCacheabilityStep setCacheability(String cacheLevel);

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

	public interface MVCResourceCommandNameStep {

		public AfterMVCResourceCommandNameStep setMVCResourceCommandName(
			String value);

		public AfterMVCResourceCommandNameStep setMVCResourceCommandName(
			String value, boolean allowNullValue);

		public AfterMVCResourceCommandNameStep setMVCResourceCommandName(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier);

	}

	public interface NavigationStep {

		public AfterNavigationStep setNavigation(String value);

		public AfterNavigationStep setNavigation(
			UnsafeSupplier<Object, Exception> valueUnsafeSupplier);

	}

	public interface ParameterStep {

		public AfterParameterStep removeParameter(String name);

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

	public interface ResourceIDStep {

		public AfterResourceIDStep setResourceID(String resourceID);

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

}