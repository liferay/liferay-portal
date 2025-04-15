/*******************************************************************************
 * Copyright (c) 2005, 2015 Cognos Incorporated, IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Cognos Incorporated - initial API and implementation
 *     IBM Corporation - bug fixes and enhancements
 *     Raymond Augé <raymond.auge@liferay.com> - Bug 436698
 *******************************************************************************/
package org.eclipse.equinox.http.servlet.internal.context;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.ServletContext;

/**
 * The ProxyContext provides something similar to a ServletContext for all servlets and resources under a particular ProxyServlet.
 * In particular it holds and represent the concept of "context path" through the Proxy Servlets servlet path.
 * The Http Service also requires a ServletContext namespaced by each individual HttpContext. The ProxyContext provides support for the
 * attribute map of a ServletContext again namespaced by HttpContext as specified in the Http Service specification. The ContextAttributes
 * are reference counted so that when the HttpContext is no longer referenced the associated context attributes can be
 * garbage collected and the context temp dir deleteted.
 */
public class ProxyContext implements ServletContextHelperDataContext {
	private static final String JAVAX_SERVLET_CONTEXT_TEMPDIR = "javax.servlet.context.tempdir"; //$NON-NLS-1$

	private final ContextAttributes contextAttributes = new ContextAttributes();
	File proxyContextTempDir;
	private ServletContext servletContext;

	public ProxyContext(String contextName, ServletContext servletContext) {
		this.servletContext = servletContext;
		File tempDir = (File) servletContext.getAttribute(JAVAX_SERVLET_CONTEXT_TEMPDIR);
		if (tempDir != null) {
			tempDir = new File(tempDir, "proxytemp"); //$NON-NLS-1$
			proxyContextTempDir = new File(tempDir, contextName);
			deleteDirectory(proxyContextTempDir);
			proxyContextTempDir.mkdirs();
		}
	}

	public void destroy() {
		if (proxyContextTempDir != null)
			deleteDirectory(proxyContextTempDir);
	}

	public Dictionary<String, Object> getContextAttributes() {
		return contextAttributes;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	/**
	 * deleteDirectory is a convenience method to recursively delete a directory
	 * @param directory - the directory to delete.
	 * @return was the delete succesful
	 */
	protected static boolean deleteDirectory(File directory) {
		if (directory.exists() && directory.isDirectory()) {
			File[] files = directory.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return directory.delete();
	}

	public class ContextAttributes
		extends Dictionary<String, Object> implements Serializable {

		private static final long serialVersionUID = 1916670423277243587L;
		private final AtomicInteger referenceCount = new AtomicInteger();

		public ContextAttributes() {
			if (proxyContextTempDir != null) {
				File contextTempDir = new File(
					proxyContextTempDir,
					"hc_" + ProxyContext.this.hashCode()); //$NON-NLS-1$

				contextTempDir.mkdirs();

				put(JAVAX_SERVLET_CONTEXT_TEMPDIR, contextTempDir);
			}
		}

		public void destroy() {
			File contextTempDir = (File) get(JAVAX_SERVLET_CONTEXT_TEMPDIR);
			if (contextTempDir != null)
				deleteDirectory(contextTempDir);
		}

		public int addReference() {
			return referenceCount.incrementAndGet();
		}

		public int removeReference() {
			return referenceCount.decrementAndGet();
		}

		public int referenceCount() {
			return referenceCount.get();
		}

		@Override
		public Enumeration<Object> elements() {
			return Collections.enumeration(_map.values());
		}

		@Override
		public Object get(Object key) {
			return _map.get(key);
		}

		@Override
		public boolean isEmpty() {
			return _map.isEmpty();
		}

		@Override
		public Enumeration<String> keys() {
			return Collections.enumeration(_map.keySet());
		}

		@Override
		public Object put(String key, Object value) {
			return _map.put(key, value);
		}

		@Override
		public Object remove(Object key) {
			return _map.remove(key);
		}

		@Override
		public int size() {
			return _map.size();
		}

		private final Map<String, Object> _map =
			new ConcurrentHashMap<String, Object>();
	}

}
/* @generated */