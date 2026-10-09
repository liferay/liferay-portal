/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleEvent;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleListener;
import com.liferay.exportimport.kernel.lifecycle.constants.ExportImportLifecycleConstants;
import com.liferay.exportimport.test.util.constants.DummyFolderPortletKeys;
import com.liferay.exportimport.test.util.exportimport.data.handler.DummyFolderWithMissingDummyPortletDataHandler;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.xml.Attribute;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.File;
import java.io.Serializable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Akos Thurzo
 */
@RunWith(Arquillian.class)
public class ExportedMissingReferenceBackwardCompatbilityExportImportTest
	extends ExportedMissingReferenceExportImportTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_removeAttributeFromLARExportImportLifecycleListener =
			new RemoveAttributeFromLARExportImportLifecycleListener();

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			ExportImportLifecycleListener.class,
			_removeAttributeFromLARExportImportLifecycleListener, null);
	}

	@After
	@Override
	public void tearDown() throws Exception {
		_serviceRegistration.unregister();

		super.tearDown();
	}

	@Test
	public void testBackwardCompatibility() throws Exception {
		long[] layoutIds = {layout.getLayoutId()};

		try (SafeCloseable safeCloseable =
				setPortletDataHandlerWithSafeCloseable(
					DummyFolderPortletKeys.DUMMY_FOLDER_WITH_MISSING_REFERENCE,
					DummyFolderWithMissingDummyPortletDataHandler.class)) {

			exportImportLayouts(layoutIds, getExportParameterMap(), true);
		}
		catch (PortletDataException portletDataException) {
			Throwable throwable = portletDataException.getCause();

			if (!(throwable instanceof NullPointerException)) {
				throw portletDataException;
			}

			StackTraceElement[] stackTrace = throwable.getStackTrace();

			if (Objects.equals(
					stackTrace[0].getClassName(),
					StagedModelDataHandlerUtil.class.getName()) &&
				Objects.equals(
					stackTrace[0].getMethodName(),
					"doImportReferenceStagedModel")) {

				throw portletDataException;
			}
		}
	}

	@Rule
	public final TestRule skipParentTestRule =
		(statement, description) -> new Statement() {

			@Override
			public void evaluate() throws Throwable {
				Assume.assumeFalse(
					ListUtil.exists(
						_parentTestMethods,
						method -> Objects.equals(
							description.getMethodName(), method.getName())));

				statement.evaluate();
			}

		};

	public class RemoveAttributeFromLARExportImportLifecycleListener
		implements ExportImportLifecycleListener {

		@Override
		public boolean isParallel() {
			return false;
		}

		@Override
		public void onExportImportLifecycleEvent(
				ExportImportLifecycleEvent exportImportLifecycleEvent)
			throws Exception {

			if (((exportImportLifecycleEvent.getCode() !=
					ExportImportLifecycleConstants.
						EVENT_PORTLET_EXPORT_SUCCEEDED) ||
				 (exportImportLifecycleEvent.getProcessFlag() !=
					 ExportImportLifecycleConstants.
						 PROCESS_FLAG_PORTLET_EXPORT_IN_PROCESS)) &&
				(exportImportLifecycleEvent.getCode() !=
					ExportImportLifecycleConstants.
						EVENT_LAYOUT_EXPORT_SUCCEEDED)) {

				return;
			}

			List<Serializable> attributes =
				exportImportLifecycleEvent.getAttributes();

			PortletDataContext portletDataContext =
				(PortletDataContext)attributes.get(0);

			ZipWriter zipWriter = portletDataContext.getZipWriter();

			File larFile = zipWriter.getFile();

			removeAttributeFromLAR(larFile);
		}

		protected void removeAttributeFromLAR(File larFile) throws Exception {
			String larFileName = larFile.getName();
			String larFilePath = larFile.getPath();

			int lastIndexOfPeriod = larFileName.lastIndexOf(CharPool.PERIOD);

			File file = new File(
				StringBundler.concat(
					FileUtil.getPath(larFilePath), File.separator,
					larFileName.substring(0, lastIndexOfPeriod), "-original",
					larFileName.substring(lastIndexOfPeriod)));

			FileUtil.move(larFile, file);

			try (ZipReader zipReader = _zipReaderFactory.getZipReader(file)) {
				ZipWriter zipWriter = _zipWriterFactory.getZipWriter(
					new File(larFilePath));

				for (String zipEntry : zipReader.getEntries()) {
					try {
						if (zipEntry.equals("manifest.xml")) {
							Document document = SAXReaderUtil.read(
								zipReader.getEntryAsInputStream(zipEntry));

							Element rootElement = document.getRootElement();

							List<Element> missingReferencesElements =
								rootElement.elements("missing-references");

							Element missingReferencesElement =
								missingReferencesElements.get(0);

							List<Element> missingReferenceElements =
								missingReferencesElement.elements(
									"missing-reference");

							for (Element missingReferenceElement :
									missingReferenceElements) {

								Attribute elementPathAttribute =
									missingReferenceElement.attribute(
										"element-path");

								if (elementPathAttribute == null) {
									continue;
								}

								missingReferencesElement.remove(
									missingReferenceElement);
							}

							zipWriter.addEntry(
								zipEntry, document.formattedString());
						}
						else {
							zipWriter.addEntry(
								zipEntry,
								zipReader.getEntryAsInputStream(zipEntry));
						}
					}
					catch (Exception exception) {
						throw new RuntimeException(exception);
					}
				}
			}

			FileUtil.delete(file);
		}

	}

	protected static List<Method> getMethodsAnnotatedWith(
		Class<?> clazz, Class<? extends Annotation> annotation) {

		List<Method> methods = new ArrayList<>();

		Class<?> currentClazz = clazz;

		while (currentClazz != Object.class) {
			methods.addAll(
				TransformUtil.transformToList(
					(Method[])currentClazz.getDeclaredMethods(),
					method -> {
						if (method.isAnnotationPresent(annotation)) {
							return method;
						}

						return null;
					}));

			currentClazz = currentClazz.getSuperclass();
		}

		return methods;
	}

	private final List<Method> _parentTestMethods = getMethodsAnnotatedWith(
		getClass().getSuperclass(), Test.class);
	private RemoveAttributeFromLARExportImportLifecycleListener
		_removeAttributeFromLARExportImportLifecycleListener;
	private ServiceRegistration<?> _serviceRegistration;

	@Inject
	private ZipReaderFactory _zipReaderFactory;

	@Inject
	private ZipWriterFactory _zipWriterFactory;

}