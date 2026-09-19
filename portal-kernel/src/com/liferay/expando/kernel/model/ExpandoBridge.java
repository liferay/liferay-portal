/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.kernel.model;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.io.Serializable;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author Raymond Augé
 */
public interface ExpandoBridge {

	public void addAttribute(String name) throws PortalException;

	public void addAttribute(String name, boolean secure)
		throws PortalException;

	public void addAttribute(String name, int type) throws PortalException;

	public void addAttribute(String name, int type, boolean secure)
		throws PortalException;

	public void addAttribute(String name, int type, Serializable defaultValue)
		throws PortalException;

	public void addAttribute(
			String name, int type, Serializable defaultValue, boolean secure)
		throws PortalException;

	public default void addAttribute(String name, String type)
		throws PortalException {
	}

	public default void addAttribute(String name, String type, boolean secure)
		throws PortalException {
	}

	public default void addAttribute(
			String name, String type, Serializable defaultValue)
		throws PortalException {
	}

	public default void addAttribute(
			String name, String type, Serializable defaultValue, boolean secure)
		throws PortalException {
	}

	public Serializable getAttribute(String name);

	public Serializable getAttribute(String name, boolean secure);

	public Serializable getAttributeDefault(String name);

	public Enumeration<String> getAttributeNames();

	public UnicodeProperties getAttributeProperties(String name);

	public int getAttributeType(String name);

	public Map<String, Serializable> getAttributes();

	public Map<String, Serializable> getAttributes(boolean secure);

	public Map<String, Serializable> getAttributes(Collection<String> names);

	public Map<String, Serializable> getAttributes(
		Collection<String> names, boolean secure);

	public String getClassName();

	public long getClassPK();

	public long getCompanyId();

	public boolean hasAttribute(String name);

	public void setAttribute(String name, Serializable value);

	public void setAttribute(String name, Serializable value, boolean secure);

	public void setAttributeDefault(String name, Serializable defaultValue);

	public void setAttributeProperties(
		String name, UnicodeProperties unicodeProperties);

	public void setAttributeProperties(
		String name, UnicodeProperties unicodeProperties, boolean secure);

	public void setAttributes(Map<String, Serializable> attributes);

	public void setAttributes(
		Map<String, Serializable> attributes, boolean secure);

	public void setAttributes(ServiceContext serviceContext);

	public void setAttributes(ServiceContext serviceContext, boolean secure);

	public void setClassName(String className);

	public void setClassPK(long classPK);

	public void setCompanyId(long companyId);

}