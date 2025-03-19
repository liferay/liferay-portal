/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.jasper.jspc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.Deque;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.jasper.JspC;
import org.apache.jasper.servlet.JspCServletContext;
import org.apache.jasper.servlet.TldScanner;
import org.apache.tomcat.JarScanType;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.JarScannerCallback;
import org.apache.tomcat.util.descriptor.tld.TldResourcePath;
import org.apache.tomcat.util.scan.StandardJarScanner;

import org.gradle.api.GradleException;

import org.xml.sax.SAXException;

/**
 * @author Drew Brokke
 */
public class CompileJSPUtil {

	public static void compileJSP(
		String compilerClassName, String[] completeArgs,
		String jspCClasspathPath) {

		JspC jspC = new JspC() {

			@Override
			public String getCompilerClassName() {
				return compilerClassName;
			}

			@Override
			protected TldScanner newTldScanner(
				JspCServletContext jspCServletContext, boolean namespaceAware,
				boolean validate, boolean blockExternal) {

				return new TldScanner(
					jspCServletContext, namespaceAware, validate,
					blockExternal) {

					@Override
					public void scanJars() {
						jspCServletContext.setAttribute(
							JarScanner.class.getName(),
							new StandardJarScanner() {

								@Override
								protected void processURLs(
									JarScanType scanType,
									JarScannerCallback callback,
									Set<URL> processedURLs, boolean webApp,
									Deque<URL> classPathUrlsToProcess) {

									if (!webApp) {
										classPathUrlsToProcess.clear();

										return;
									}

									super.processURLs(
										scanType, callback, processedURLs,
										webApp, classPathUrlsToProcess);
								}

							});

						super.scanJars();
					}

					@Override
					protected void parseTld(TldResourcePath tldResourcePath)
						throws IOException, SAXException {

						super.parseTld(
							new TldResourcePath(
								tldResourcePath.getUrl(),
								tldResourcePath.getWebappPath(),
								tldResourcePath.getEntryName()) {

								@Override
								public InputStream openStream()
									throws IOException {

									URL url = getUrl();

									String entryName = getEntryName();

									String path = url.getPath();

									String key =
										path + "#" + String.valueOf(entryName);

									byte[] data = _dataMap.get(key);

									if (data != null) {
										return new ByteArrayInputStream(data);
									}

									if (entryName == null) {
										try (InputStream inputStream =
												url.openStream()) {

											return _toCachedInputStream(
												key, inputStream);
										}
									}

									try (ZipFile zipFile = new ZipFile(path)) {
										ZipEntry zipEntry = zipFile.getEntry(
											entryName);

										try (InputStream inputStream =
												zipFile.getInputStream(
													zipEntry)) {

											return _toCachedInputStream(
												key, inputStream);
										}
									}
								}

							});
					}

				};
			}

		};

		Logger logger = Logger.getLogger("org.apache.tomcat");

		logger.setLevel(Level.INFO);

		try {
			jspC.setArgs(completeArgs);
			jspC.setClassPath(jspCClasspathPath);

			jspC.execute();
		}
		catch (Exception exception) {
			throw new GradleException(exception.getMessage(), exception);
		}
	}

	private static InputStream _toCachedInputStream(
			String key, InputStream inputStream)
		throws IOException {

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		inputStream.transferTo(byteArrayOutputStream);

		byte[] data = byteArrayOutputStream.toByteArray();

		_dataMap.put(key, data);

		return new ByteArrayInputStream(data);
	}

	private static final Map<String, byte[]> _dataMap =
		new ConcurrentHashMap<>();

}