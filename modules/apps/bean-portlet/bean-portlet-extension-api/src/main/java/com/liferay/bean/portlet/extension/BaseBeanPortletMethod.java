/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.extension;

import com.liferay.petra.lang.HashUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletMode;
import jakarta.portlet.ProcessAction;
import jakarta.portlet.RenderMode;
import jakarta.portlet.annotations.ActionMethod;
import jakarta.portlet.annotations.EventMethod;
import jakarta.portlet.annotations.HeaderMethod;
import jakarta.portlet.annotations.PortletQName;
import jakarta.portlet.annotations.RenderMethod;
import jakarta.portlet.annotations.ServeResourceMethod;

import java.lang.reflect.Method;

import java.util.Objects;

import javax.xml.namespace.QName;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Neil Griffin
 */
@ProviderType
public abstract class BaseBeanPortletMethod implements BeanPortletMethod {

	public BaseBeanPortletMethod(
		BeanPortletMethodType beanPortletMethodType, Method method) {

		_beanPortletMethodType = beanPortletMethodType;
		_method = method;

		_ordinal = beanPortletMethodType.getOrdinal(method);
	}

	@Override
	public int compareTo(BeanPortletMethod beanPortletMethod) {
		return Integer.compare(_ordinal, beanPortletMethod.getOrdinal());
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof BeanPortletMethod)) {
			return false;
		}

		BaseBeanPortletMethod baseBeanPortletMethod =
			(BaseBeanPortletMethod)object;

		if (Objects.equals(
				_beanPortletMethodType,
				baseBeanPortletMethod._beanPortletMethodType) &&
			Objects.equals(_method, baseBeanPortletMethod._method) &&
			Objects.equals(_ordinal, baseBeanPortletMethod._ordinal)) {

			return true;
		}

		return false;
	}

	@Override
	public String getActionName() {
		ActionMethod actionMethod = _method.getAnnotation(ActionMethod.class);

		if (actionMethod != null) {
			String actionName = actionMethod.actionName();

			if (Validator.isNotNull(actionName)) {
				return actionName;
			}
		}

		ProcessAction processAction = _method.getAnnotation(
			ProcessAction.class);

		if (processAction == null) {
			return null;
		}

		return processAction.name();
	}

	@Override
	public BeanPortletMethodType getBeanPortletMethodType() {
		return _beanPortletMethodType;
	}

	@Override
	public Method getMethod() {
		return _method;
	}

	@Override
	public int getOrdinal() {
		return _ordinal;
	}

	@Override
	public PortletMode getPortletMode() {
		HeaderMethod headerMethod = _method.getAnnotation(HeaderMethod.class);

		if (headerMethod != null) {
			String portletMode = headerMethod.portletMode();

			if (Validator.isNull(portletMode)) {
				return null;
			}

			return new PortletMode(portletMode);
		}

		RenderMethod renderMethod = _method.getAnnotation(RenderMethod.class);

		if (renderMethod != null) {
			String portletMode = renderMethod.portletMode();

			if (Validator.isNull(portletMode)) {
				return null;
			}

			return new PortletMode(portletMode);
		}

		RenderMode renderMode = _method.getAnnotation(RenderMode.class);

		if (renderMode == null) {
			return null;
		}

		String name = renderMode.name();

		if (Validator.isNull(name)) {
			return null;
		}

		return new PortletMode(name);
	}

	@Override
	public String getResourceID() {
		ServeResourceMethod serveResourceMethod = _method.getAnnotation(
			ServeResourceMethod.class);

		if (serveResourceMethod == null) {
			return null;
		}

		String resourceID = serveResourceMethod.resourceID();

		if (Validator.isNotNull(resourceID)) {
			return resourceID;
		}

		return null;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, _beanPortletMethodType);

		hashCode = HashUtil.hash(hashCode, _method);

		return HashUtil.hash(hashCode, _ordinal);
	}

	@Override
	public boolean isEventProcessor(QName qName) {
		EventMethod eventMethod = _method.getAnnotation(EventMethod.class);

		if (eventMethod == null) {
			return false;
		}

		PortletQName[] portletQNames = eventMethod.processingEvents();

		for (PortletQName portletQName : portletQNames) {
			String localPart = portletQName.localPart();

			if (localPart.equals(qName.getLocalPart())) {
				String namespaceURI = portletQName.namespaceURI();

				if (namespaceURI.equals(qName.getNamespaceURI())) {
					return true;
				}
			}
		}

		return false;
	}

	private final BeanPortletMethodType _beanPortletMethodType;
	private final Method _method;
	private final int _ordinal;

}