/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.application.list.deploy.hot;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.adapter.PortletPanelAppAdapter;
import com.liferay.petra.io.StreamUtil;
import com.liferay.portal.kernel.deploy.hot.BaseHotDeployListener;
import com.liferay.portal.kernel.deploy.hot.HotDeployEvent;
import com.liferay.portal.kernel.deploy.hot.HotDeployException;
import com.liferay.portal.kernel.deploy.hot.HotDeployListener;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;
import com.liferay.portal.util.PortletCategoryUtil;

import jakarta.servlet.ServletContext;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = HotDeployListener.class)
public class LegacyPortletPanelAppHotDeployListener
	extends BaseHotDeployListener {

	public int getServiceRegistrationsSize() {
		return _serviceRegistrations.size();
	}

	@Override
	public void invokeDeploy(HotDeployEvent hotDeployEvent)
		throws HotDeployException {

		try {
			for (Dictionary<String, Object> properties :
					getPropertiesList(hotDeployEvent)) {

				String portletId = (String)properties.get(
					"panel.app.portlet.id");

				ServiceRegistration<PanelApp> serviceRegistration =
					_bundleContext.registerService(
						PanelApp.class,
						new PortletPanelAppAdapter(
							portletId,
							() -> _portletLocalService.getPortletById(
								portletId)),
						properties);

				_serviceRegistrations.put(portletId, serviceRegistration);
			}
		}
		catch (DocumentException | IOException exception) {
			throw new HotDeployException(exception);
		}
	}

	@Override
	public void invokeUndeploy(HotDeployEvent hotDeployEvent)
		throws HotDeployException {

		try {
			for (Dictionary<String, Object> properties :
					getPropertiesList(hotDeployEvent)) {

				String portletId = (String)properties.get(
					"panel.app.portlet.id");

				ServiceRegistration<PanelApp> serviceRegistration =
					_serviceRegistrations.remove(portletId);

				if (serviceRegistration != null) {
					serviceRegistration.unregister();
				}
			}
		}
		catch (DocumentException | IOException exception) {
			throw new HotDeployException(exception);
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	protected String getPortletId(
		String servletContextName, String portletName) {

		if (servletContextName == null) {
			return portletName;
		}

		return _portal.getJsSafePortletId(
			portletName + PortletConstants.WAR_SEPARATOR + servletContextName);
	}

	protected List<Dictionary<String, Object>> getPropertiesList(
			HotDeployEvent hotDeployEvent)
		throws DocumentException, IOException {

		ServletContext servletContext = hotDeployEvent.getServletContext();

		String xml = StreamUtil.toString(
			servletContext.getResourceAsStream("/WEB-INF/liferay-portlet.xml"));

		if (xml == null) {
			return Collections.emptyList();
		}

		List<Dictionary<String, Object>> properties = new ArrayList<>();

		Document document = UnsecureSAXReaderUtil.read(xml, true);

		Element rootElement = document.getRootElement();

		Iterator<Element> iterator = rootElement.elementIterator("portlet");

		while (iterator.hasNext()) {
			Element portletElement = iterator.next();

			String controlPanelEntryCategory = portletElement.elementText(
				"control-panel-entry-category");

			if (Validator.isNull(controlPanelEntryCategory)) {
				continue;
			}

			controlPanelEntryCategory =
				PortletCategoryUtil.getPortletCategoryKey(
					controlPanelEntryCategory);

			properties.add(
				HashMapDictionaryBuilder.<String, Object>put(
					"panel.app.order",
					() -> {
						String controlPanelEntryWeight =
							portletElement.elementText(
								"control-panel-entry-weight");

						if (Validator.isNull(controlPanelEntryWeight)) {
							return null;
						}

						return (int)Math.ceil(
							GetterUtil.getDouble(controlPanelEntryWeight) *
								100);
					}
				).put(
					"panel.app.portlet.id",
					getPortletId(
						hotDeployEvent.getServletContextName(),
						portletElement.elementText("portlet-name"))
				).put(
					"panel.category.key", controlPanelEntryCategory
				).build());
		}

		return properties;
	}

	private BundleContext _bundleContext;

	@Reference
	private Portal _portal;

	@Reference
	private PortletLocalService _portletLocalService;

	private final Map<String, ServiceRegistration<PanelApp>>
		_serviceRegistrations = new ConcurrentHashMap<>();

}