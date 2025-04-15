/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.portlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletRequestDispatcher;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

/**
 * @author Dante Wang
 */
public class MockPortletContext implements PortletContext {

	public MockPortletContext() {
		this(StringPool.BLANK, null);
	}

	public MockPortletContext(ResourceLoader resourceLoader) {
		this(StringPool.BLANK, resourceLoader);
	}

	public MockPortletContext(String resourceBasePath) {
		this(resourceBasePath, null);
	}

	public MockPortletContext(
		String resourceBasePath, ResourceLoader resourceLoader) {

		_attributes = new LinkedHashMap<>();

		String tempDir = System.getProperty("java.io.tmpdir");

		if (tempDir != null) {
			_attributes.put(
				"jakarta.servlet.context.tempdir", new File(tempDir));
		}

		_resourceBasePath = Objects.requireNonNullElse(
			resourceBasePath, StringPool.BLANK);
		_resourceLoader = Objects.requireNonNullElse(
			resourceLoader, new DefaultResourceLoader());

		_containerRuntimeOptions = new LinkedHashSet<>();
		_initParameters = new LinkedHashMap<>();
		_portletContextName = "MockPortletContext";
	}

	public void addContainerRuntimeOption(String key) {
		_containerRuntimeOptions.add(key);
	}

	public void addInitParameter(String name, String value) {
		Assert.notNull(name, "Parameter name must not be null");

		_initParameters.put(name, value);
	}

	@Override
	public Object getAttribute(String name) {
		return _attributes.get(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return Collections.enumeration(_attributes.keySet());
	}

	@Override
	public ClassLoader getClassLoader() {
		Class<?> clazz = getClass();

		return clazz.getClassLoader();
	}

	@Override
	public Enumeration<String> getContainerRuntimeOptions() {
		return Collections.enumeration(_containerRuntimeOptions);
	}

	@Override
	public String getContextPath() {
		return "/mock-context-path";
	}

	@Override
	public int getEffectiveMajorVersion() {
		return 3;
	}

	@Override
	public int getEffectiveMinorVersion() {
		return 0;
	}

	@Override
	public String getInitParameter(String name) {
		Assert.notNull(name, "Parameter name must not be null");

		return _initParameters.get(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return Collections.enumeration(_initParameters.keySet());
	}

	@Override
	public int getMajorVersion() {
		return 2;
	}

	@Override
	public String getMimeType(String filePath) {
		return null;
	}

	@Override
	public int getMinorVersion() {
		return 0;
	}

	@Override
	public PortletRequestDispatcher getNamedDispatcher(String path) {
		return null;
	}

	@Override
	public String getPortletContextName() {
		return _portletContextName;
	}

	@Override
	public String getRealPath(String path) {
		Resource resource = _resourceLoader.getResource(
			getResourceLocation(path));

		try {
			File file = resource.getFile();

			return file.getAbsolutePath();
		}
		catch (IOException ioException) {
			_log.error(
				"Unable to determine real path of resource " + resource,
				ioException);

			return null;
		}
	}

	@Override
	public PortletRequestDispatcher getRequestDispatcher(String path) {
		if (!path.startsWith("/")) {
			throw new IllegalArgumentException(
				"Portlet request dispatcher path at the portlet context " +
					"level must start with '/'");
		}

		return new PortletRequestDispatcher() {

			@Override
			public void forward(
				PortletRequest portletRequest,
				PortletResponse portletResponse) {

				if (portletResponse instanceof
						MockMimeResponse mockMimeResponse) {

					mockMimeResponse.setForwardedUrl(path);
				}
			}

			@Override
			public void include(
				PortletRequest portletRequest,
				PortletResponse portletResponse) {

				if (portletResponse instanceof
						MockMimeResponse mockMimeResponse) {

					mockMimeResponse.setIncludedUrl(path);
				}
			}

			@Override
			public void include(
				RenderRequest renderRequest, RenderResponse renderResponse) {

				include(
					(PortletRequest)renderRequest,
					(PortletResponse)renderResponse);
			}

		};
	}

	@Override
	public URL getResource(String path) throws MalformedURLException {
		Resource resource = _resourceLoader.getResource(
			getResourceLocation(path));

		try {
			return resource.getURL();
		}
		catch (IOException ioException) {
			_log.error("Unable to get URL for " + resource, ioException);

			return null;
		}
	}

	@Override
	public InputStream getResourceAsStream(String path) {
		Resource resource = _resourceLoader.getResource(
			getResourceLocation(path));

		try {
			return resource.getInputStream();
		}
		catch (IOException ioException) {
			_log.error(
				"Unable to open input stream for " + resource, ioException);

			return null;
		}
	}

	@Override
	public Set<String> getResourcePaths(String path) {
		Resource resource = _resourceLoader.getResource(
			getResourceLocation(path));

		try {
			File file = resource.getFile();

			String[] fileList = file.list();

			if (fileList == null) {
				return null;
			}

			String prefix = path;

			if (prefix.endsWith(StringPool.SLASH)) {
				prefix = prefix + StringPool.SLASH;
			}

			Set<String> resourcePaths = new HashSet<>();

			for (String fileEntry : fileList) {
				resourcePaths.add(prefix + fileEntry);
			}

			return resourcePaths;
		}
		catch (IOException ioException) {
			_log.error(
				"Unable to get resource paths for " + resource, ioException);

			return null;
		}
	}

	@Override
	public String getServerInfo() {
		return "MockPortal/1.0";
	}

	@Override
	public void log(String message) {
		if (_log.isInfoEnabled()) {
			_log.info(message);
		}
	}

	@Override
	public void log(String message, Throwable throwable) {
		_log.error(message, throwable);
	}

	@Override
	public void removeAttribute(String name) {
		_attributes.remove(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		if (value != null) {
			_attributes.put(name, value);
		}
		else {
			_attributes.remove(name);
		}
	}

	public void setPortletContextName(String portletContextName) {
		_portletContextName = portletContextName;
	}

	protected String getResourceLocation(String path) {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}

		return _resourceBasePath + path;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MockPortletContext.class);

	private final Map<String, Object> _attributes;
	private final Set<String> _containerRuntimeOptions;
	private final Map<String, String> _initParameters;
	private String _portletContextName;
	private final String _resourceBasePath;
	private final ResourceLoader _resourceLoader;

}