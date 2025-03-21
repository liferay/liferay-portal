/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.jsp.engine.internal;

import com.liferay.petra.lang.ClassLoaderPool;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.jsp.engine.internal.delegate.CheckEnabledServletDelegate;
import com.liferay.portal.jsp.engine.internal.delegate.JspConfigDescriptorServletContextDelegate;
import com.liferay.portal.jsp.engine.internal.jakarta.transformer.JakartaTransformerJDTCompiler;
import com.liferay.portal.kernel.dependency.manager.DependencyManagerSyncUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.util.PropsImpl;
import com.liferay.shielded.container.Ordered;
import com.liferay.shielded.container.ShieldedContainerInitializer;
import com.liferay.taglib.servlet.JspFactorySwapper;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletRequestWrapper;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.File;
import java.io.IOException;

import java.util.EnumSet;
import java.util.Map;

import org.apache.jasper.servlet.JasperInitializer;
import org.apache.jasper.servlet.JspServlet;
import org.apache.tomcat.JarScanFilter;
import org.apache.tomcat.JarScanType;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.util.scan.StandardJarScanner;

/**
 * @author Shuyang Zhou
 */
@Ordered(1)
public class JSPEngineShieldedContainerInitializer
	implements ShieldedContainerInitializer {

	@Override
	public void initialize(ServletContext servletContext)
		throws ServletException {

		File shieldedContainerLib = new File(
			servletContext.getRealPath(SHIELDED_CONTAINER_LIB));

		try {
			String path = shieldedContainerLib.getCanonicalPath();

			path = StringUtil.replace(
				path, CharPool.BACK_SLASH, CharPool.SLASH);

			if (!path.endsWith(StringPool.SLASH)) {
				path += StringPool.SLASH;
			}

			System.setProperty(
				PropsKeys.LIFERAY_SHIELDED_CONTAINER_LIB_PORTAL_DIR, path);
		}
		catch (IOException ioException) {
			throw new ServletException(ioException);
		}

		ClassLoaderPool.register(
			"ShieldedContainerClassLoader", servletContext.getClassLoader());

		PropsUtil.setProps(new PropsImpl());

		JarScanner jarScanner = new StandardJarScanner();

		jarScanner.setJarScanFilter(
			new JarScanFilter() {

				@Override
				public boolean check(JarScanType jarScanType, String jarName) {
					return false;
				}

				@Override
				public boolean isSkipAll() {
					return true;
				}

			});

		servletContext.setAttribute(JarScanner.class.getName(), jarScanner);

		JasperInitializer jasperInitializer = new JasperInitializer();

		jasperInitializer.onStartup(
			null,
			ProxyUtil.newDelegateProxyInstance(
				servletContext.getClassLoader(), ServletContext.class,
				new JspConfigDescriptorServletContextDelegate(servletContext),
				servletContext));

		DependencyManagerSyncUtil.registerSyncCallable(
			() -> {
				JspFactorySwapper.swap();

				return null;
			});

		Map<String, String> initParameters = PropertiesUtil.toMap(
			PropsUtil.getProperties("jsp.engine.", true));

		initParameters.put(
			"compilerClassName", JakartaTransformerJDTCompiler.class.getName());

		JspServlet jspServlet = new JspServlet();

		long checkInterval = GetterUtil.getLong(
			initParameters.get("checkInterval"));

		if (GetterUtil.getBoolean(initParameters.get("development"))) {
			checkInterval = 0;
		}

		Servlet portalJSPServlet;

		if (checkInterval > 0) {
			portalJSPServlet = ProxyUtil.newDelegateProxyInstance(
				servletContext.getClassLoader(), Servlet.class,
				new CheckEnabledServletDelegate(
					jspServlet, servletContext, checkInterval),
				jspServlet);
		}
		else {
			portalJSPServlet = jspServlet;
		}

		ServletRegistration.Dynamic servletDynamic = servletContext.addServlet(
			"Portal Jasper Servlet", portalJSPServlet);

		servletDynamic.setInitParameters(initParameters);

		servletDynamic.setLoadOnStartup(1);

		if (ServerDetector.isTomcat()) {
			servletDynamic.addMapping("*.jsp", "*.jspx");
		}
		else {
			FilterRegistration.Dynamic filterDynamic = servletContext.addFilter(
				"Portal Jasper Filter",
				new Filter() {

					@Override
					public void destroy() {
					}

					@Override
					public void doFilter(
							ServletRequest servletRequest,
							ServletResponse servletResponse,
							FilterChain filterChain)
						throws IOException, ServletException {

						if (servletRequest instanceof HttpServletRequest) {
							portalJSPServlet.service(
								new HttpServletRequestWrapper(
									(HttpServletRequest)servletRequest) {

									@Override
									public ServletContext getServletContext() {
										return servletContext;
									}

								},
								servletResponse);
						}
						else {
							portalJSPServlet.service(
								new ServletRequestWrapper(servletRequest) {

									@Override
									public ServletContext getServletContext() {
										return servletContext;
									}

								},
								servletResponse);
						}
					}

					@Override
					public void init(FilterConfig filterConfig) {
					}

				});

			filterDynamic.addMappingForUrlPatterns(
				EnumSet.allOf(DispatcherType.class), true, "*.jsp", "*.jspx");
		}
	}

}