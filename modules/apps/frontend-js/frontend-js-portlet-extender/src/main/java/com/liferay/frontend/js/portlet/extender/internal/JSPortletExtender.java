/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.portlet.extender.internal;

import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.frontend.js.portlet.extender.internal.portlet.JSPortlet;
import com.liferay.frontend.js.portlet.extender.internal.portlet.action.PortletExtenderConfigurationAction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.Portlet;

import java.net.URL;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.namespace.extender.ExtenderNamespace;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * @author Ray Augé
 * @author Iván Zaera Avellón
 * @author Gustavo Mantuan
 */
@Component(service = {})
public class JSPortletExtender {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleTracker = new BundleTracker<>(
			bundleContext, Bundle.ACTIVE, _bundleTrackerCustomizer);

		_bundleTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_bundleTracker.close();

		_bundleTracker = null;
	}

	private void _addServiceProperties(
		Dictionary<String, Object> properties, JSONObject portletJSONObject) {

		if (portletJSONObject == null) {
			return;
		}

		Iterator<String> iterator = portletJSONObject.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();

			Object value = portletJSONObject.get(key);

			if (value instanceof JSONObject) {
				String stringValue = value.toString();

				properties.put(key, stringValue);
			}
			else if (value instanceof JSONArray) {
				JSONArray jsonArray = (JSONArray)value;

				List<String> values = new ArrayList<>();

				for (int i = 0; i < jsonArray.length(); i++) {
					Object object = jsonArray.get(i);

					values.add(object.toString());
				}

				properties.put(key, values.toArray(new String[0]));
			}
			else {
				properties.put(key, value);
			}
		}
	}

	private String _getPortletName(JSONObject packageJSONObject) {
		String portletName = packageJSONObject.getString("name");

		JSONObject portletJSONObject = packageJSONObject.getJSONObject(
			"portlet");

		String javaxPortletName = portletJSONObject.getString(
			"jakarta.portlet.name");

		if (Validator.isNotNull(javaxPortletName)) {
			portletName = javaxPortletName;
		}

		return portletName;
	}

	private boolean _optIn(Bundle bundle) {
		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		List<BundleWire> bundleWires = bundleWiring.getRequiredWires(
			ExtenderNamespace.EXTENDER_NAMESPACE);

		for (BundleWire bundleWire : bundleWires) {
			BundleCapability bundleCapability = bundleWire.getCapability();

			Map<String, Object> attributes = bundleCapability.getAttributes();

			Object value = attributes.get(ExtenderNamespace.EXTENDER_NAMESPACE);

			if ((value != null) &&
				value.equals("liferay.frontend.js.portlet")) {

				return true;
			}
		}

		return false;
	}

	private JSONObject _parse(URL url) {
		if (url == null) {
			return null;
		}

		try {
			return _jsonFactory.createJSONObject(URLUtil.toString(url));
		}
		catch (Exception exception) {
			_log.error("Unable to parse " + url, exception);

			return null;
		}
	}

	private void _registerConfigurationActionService(
		BundleContext bundleContext, JSONObject packageJSONObject,
		JSONObject portletPreferencesJSONObject) {

		String portletName = _getPortletName(packageJSONObject);

		try {
			ConfigurationAction configurationAction =
				new PortletExtenderConfigurationAction(
					_ddm, _ddmFormRenderer, _ddmFormValuesFactory,
					portletPreferencesJSONObject);

			Dictionary<String, Object> properties = new Hashtable<>();

			properties.put("jakarta.portlet.name", portletName);

			bundleContext.registerService(
				new String[] {ConfigurationAction.class.getName()},
				configurationAction, properties);
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to register configuration action service for portlet" +
					portletName,
				portalException);
		}
	}

	private ServiceRegistration<?> _registerJSPortletService(
		BundleContext bundleContext, JSONObject packageJSONObject,
		Set<String> portletPreferencesFieldNames) {

		Dictionary<String, Object> properties = new Hashtable<>();

		_addServiceProperties(
			properties, packageJSONObject.getJSONObject("portlet"));

		String packageName = packageJSONObject.getString("name");

		properties.put(
			"jakarta.portlet.name", _getPortletName(packageJSONObject));
		properties.put("service.pid", packageName);

		String packageVersion = packageJSONObject.getString("version");

		return bundleContext.registerService(
			new String[] {
				ManagedService.class.getName(), Portlet.class.getName()
			},
			new JSPortlet(
				_jsonFactory, packageName, packageVersion, _portal,
				portletPreferencesFieldNames),
			properties);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JSPortletExtender.class);

	private BundleTracker<ServiceRegistration<?>> _bundleTracker;

	private final BundleTrackerCustomizer<ServiceRegistration<?>>
		_bundleTrackerCustomizer =
			new BundleTrackerCustomizer<ServiceRegistration<?>>() {

				@Override
				public ServiceRegistration<?> addingBundle(
					Bundle bundle, BundleEvent bundleEvent) {

					if (!_optIn(bundle)) {
						return null;
					}

					JSONObject packageJSONObject = _parse(
						bundle.getEntry("META-INF/resources/package.json"));

					if (packageJSONObject == null) {
						return null;
					}

					BundleContext bundleContext = bundle.getBundleContext();

					Set<String> portletPreferencesFieldNames = new HashSet<>();

					JSONObject portletPreferencesJSONObject = _parse(
						bundle.getEntry("features/portlet_preferences.json"));

					if (portletPreferencesJSONObject != null) {
						JSONArray fieldsJSONArray =
							portletPreferencesJSONObject.getJSONArray("fields");

						for (int i = 0; i < fieldsJSONArray.length(); i++) {
							JSONObject jsonObject =
								fieldsJSONArray.getJSONObject(i);

							portletPreferencesFieldNames.add(
								jsonObject.getString("name"));
						}
					}

					ServiceRegistration<?> serviceRegistration =
						_registerJSPortletService(
							bundleContext, packageJSONObject,
							portletPreferencesFieldNames);

					if (portletPreferencesJSONObject != null) {
						_registerConfigurationActionService(
							bundleContext, packageJSONObject,
							portletPreferencesJSONObject);
					}

					return serviceRegistration;
				}

				@Override
				public void modifiedBundle(
					Bundle bundle, BundleEvent bundleEvent,
					ServiceRegistration<?> serviceRegistration) {
				}

				@Override
				public void removedBundle(
					Bundle bundle, BundleEvent bundleEvent,
					ServiceRegistration<?> serviceRegistration) {

					serviceRegistration.unregister();
				}

			};

	@Reference
	private DDM _ddm;

	@Reference
	private DDMFormRenderer _ddmFormRenderer;

	@Reference
	private DDMFormValuesFactory _ddmFormValuesFactory;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

}