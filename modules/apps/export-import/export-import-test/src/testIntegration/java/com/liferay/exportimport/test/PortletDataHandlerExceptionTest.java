/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.PortletPreferences;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Máté Thurzó
 */
@RunWith(Arquillian.class)
public class PortletDataHandlerExceptionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_portletDataContextExport =
			ExportImportTestUtil.getExportPortletDataContext();

		_portletDataContextExport.setPortletId(RandomTestUtil.randomString());

		_portletDataContextImport =
			ExportImportTestUtil.getImportPortletDataContext();
	}

	@Test
	public void testPortletDataHandlerDeleteException() throws Exception {
		PortletDataHandler portletDataHandler = new TestPortletDataHandler() {

			@Override
			protected PortletPreferences doDeleteData(
					PortletDataContext portletDataContext, String portletId,
					PortletPreferences portletPreferences)
				throws Exception {

				throw new Exception();
			}

		};

		try {
			portletDataHandler.deleteData(
				_portletDataContextImport, RandomTestUtil.randomString(), null);
		}
		catch (Exception exception) {
			_validateException(exception, null);
		}
	}

	@Test
	public void testPortletDataHandlerDeleteExceptionMessage()
		throws Exception {

		final String message = RandomTestUtil.randomString();

		PortletDataHandler portletDataHandler = new TestPortletDataHandler() {

			@Override
			protected PortletPreferences doDeleteData(
					PortletDataContext portletDataContext, String portletId,
					PortletPreferences portletPreferences)
				throws Exception {

				throw new Exception(message);
			}

		};

		try {
			portletDataHandler.deleteData(
				_portletDataContextImport, RandomTestUtil.randomString(), null);
		}
		catch (Exception exception) {
			_validateException(exception, message);
		}
	}

	@Test
	public void testPortletDataHandlerDeletePortletDataException()
		throws Exception {

		PortletDataHandler portletDataHandler = new TestPortletDataHandler() {

			@Override
			protected PortletPreferences doDeleteData(
					PortletDataContext portletDataContext, String portletId,
					PortletPreferences portletPreferences)
				throws Exception {

				throw new PortletDataException();
			}

		};

		try {
			portletDataHandler.deleteData(
				_portletDataContextImport, RandomTestUtil.randomString(), null);
		}
		catch (Exception exception) {
			_validateException(exception, null);
		}
	}

	@Test
	public void testPortletDataHandlerDeletePortletDataExceptionMessage()
		throws Exception {

		final String message = RandomTestUtil.randomString();

		PortletDataHandler portletDataHandler = new TestPortletDataHandler() {

			@Override
			protected PortletPreferences doDeleteData(
					PortletDataContext portletDataContext, String portletId,
					PortletPreferences portletPreferences)
				throws Exception {

				throw new PortletDataException(message);
			}

		};

		try {
			portletDataHandler.deleteData(
				_portletDataContextImport, RandomTestUtil.randomString(), null);
		}
		catch (Exception exception) {
			_validateException(exception, message);
		}
	}

	@Test
	public void testPortletDataHandlerDeletePortletDataExceptionMessageType()
		throws Exception {

		final String message = RandomTestUtil.randomString();

		PortletDataHandler portletDataHandler = new TestPortletDataHandler() {

			@Override
			protected PortletPreferences doDeleteData(
					PortletDataContext portletDataContext, String portletId,
					PortletPreferences portletPreferences)
				throws Exception {

				PortletDataException portletDataException =
					new PortletDataException(message);

				portletDataException.setType(
					PortletDataException.INVALID_GROUP);

				throw portletDataException;
			}

		};

		try {
			portletDataHandler.deleteData(
				_portletDataContextImport, RandomTestUtil.randomString(), null);
		}
		catch (Exception exception) {
			_validateException(exception, message);
		}
	}

	@Test
	public void testPortletDataHandlerDeletePortletDataExceptionType()
		throws Exception {

		PortletDataHandler portletDataHandler = new TestPortletDataHandler() {

			@Override
			protected PortletPreferences doDeleteData(
					PortletDataContext portletDataContext, String portletId,
					PortletPreferences portletPreferences)
				throws Exception {

				throw new PortletDataException(
					PortletDataException.INVALID_GROUP);
			}

		};

		try {
			portletDataHandler.deleteData(
				_portletDataContextImport, RandomTestUtil.randomString(), null);
		}
		catch (Exception exception) {
			_validateException(exception, null);
		}
	}

	@Test
	public void testPortletDataHandlerExportException() throws Exception {
		PortletDataHandler portletDataHandler = new TestPortletDataHandler() {

			@Override
			protected String doExportData(
					PortletDataContext portletDataContext, String portletId,
					PortletPreferences portletPreferences)
				throws Exception {

				throw new Exception();
			}

		};

		try {
			portletDataHandler.exportData(
				_portletDataContextExport, RandomTestUtil.randomString(), null);
		}
		catch (Exception exception) {
			_validateException(exception, null);
		}
	}

	@Test
	public void testPortletDataHandlerExportExceptionMessage()
		throws Exception {

		final String message = RandomTestUtil.randomString();

		PortletDataHandler portletDataHandler = new TestPortletDataHandler() {

			@Override
			protected String doExportData(
					PortletDataContext portletDataContext, String portletId,
					PortletPreferences portletPreferences)
				throws Exception {

				throw new Exception(message);
			}

		};

		try {
			portletDataHandler.exportData(
				_portletDataContextExport, RandomTestUtil.randomString(), null);
		}
		catch (Exception exception) {
			_validateException(exception, message);
		}
	}

	@Test
	public void testPortletDataHandlerExportPortletDataException()
		throws Exception {

		PortletDataHandler portletDataHandler = new TestPortletDataHandler() {

			@Override
			protected String doExportData(
					PortletDataContext portletDataContext, String portletId,
					PortletPreferences portletPreferences)
				throws Exception {

				throw new PortletDataException();
			}

		};

		try {
			portletDataHandler.exportData(
				_portletDataContextExport, RandomTestUtil.randomString(), null);
		}
		catch (Exception exception) {
			_validateException(exception, null);
		}
	}

	@Test
	public void testPortletDataHandlerExportPortletDataExceptionMessage()
		throws Exception {

		final String message = RandomTestUtil.randomString();

		PortletDataHandler portletDataHandler = new TestPortletDataHandler() {

			@Override
			protected String doExportData(
					PortletDataContext portletDataContext, String portletId,
					PortletPreferences portletPreferences)
				throws Exception {

				throw new PortletDataException(message);
			}

		};

		try {
			portletDataHandler.exportData(
				_portletDataContextExport, RandomTestUtil.randomString(), null);
		}
		catch (Exception exception) {
			_validateException(exception, message);
		}
	}

	@Test
	public void testPortletDataHandlerExportPortletDataExceptionMessageType()
		throws Exception {

		final String message = RandomTestUtil.randomString();

		PortletDataHandler portletDataHandler = new TestPortletDataHandler() {

			@Override
			protected String doExportData(
					PortletDataContext portletDataContext, String portletId,
					PortletPreferences portletPreferences)
				throws Exception {

				PortletDataException portletDataException =
					new PortletDataException(message);

				portletDataException.setType(
					PortletDataException.INVALID_GROUP);

				throw portletDataException;
			}

		};

		try {
			portletDataHandler.exportData(
				_portletDataContextExport, RandomTestUtil.randomString(), null);
		}
		catch (Exception exception) {
			_validateException(exception, message);
		}
	}

	@Test
	public void testPortletDataHandlerExportPortletDataExceptionType()
		throws Exception {

		PortletDataHandler portletDataHandler = new TestPortletDataHandler() {

			@Override
			protected String doExportData(
					PortletDataContext portletDataContext, String portletId,
					PortletPreferences portletPreferences)
				throws Exception {

				throw new PortletDataException(
					PortletDataException.INVALID_GROUP);
			}

		};

		try {
			portletDataHandler.exportData(
				_portletDataContextExport, RandomTestUtil.randomString(), null);
		}
		catch (Exception exception) {
			_validateException(exception, null);
		}
	}

	@Test
	public void testPortletDataHandlerImportException() throws Exception {
		PortletDataHandler portletDataHandler = new TestPortletDataHandler() {

			@Override
			protected PortletPreferences doImportData(
					PortletDataContext portletDataContext, String portletId,
					PortletPreferences portletPreferences, String data)
				throws Exception {

				throw new Exception();
			}

		};

		try {
			portletDataHandler.importData(
				_portletDataContextImport, RandomTestUtil.randomString(), null,
				null);
		}
		catch (Exception exception) {
			_validateException(exception, null);
		}
	}

	@Test
	public void testPortletDataHandlerImportExceptionMessage()
		throws Exception {

		final String message = RandomTestUtil.randomString();

		PortletDataHandler portletDataHandler = new TestPortletDataHandler() {

			@Override
			protected PortletPreferences doImportData(
					PortletDataContext portletDataContext, String portletId,
					PortletPreferences portletPreferences, String data)
				throws Exception {

				throw new Exception(message);
			}

		};

		try {
			portletDataHandler.importData(
				_portletDataContextImport, RandomTestUtil.randomString(), null,
				null);
		}
		catch (Exception exception) {
			_validateException(exception, message);
		}
	}

	@Test
	public void testPortletDataHandlerImportPortletDataException()
		throws Exception {

		PortletDataHandler portletDataHandler = new TestPortletDataHandler() {

			@Override
			protected PortletPreferences doImportData(
					PortletDataContext portletDataContext, String portletId,
					PortletPreferences portletPreferences, String data)
				throws Exception {

				throw new PortletDataException();
			}

		};

		try {
			portletDataHandler.importData(
				_portletDataContextImport, RandomTestUtil.randomString(), null,
				null);
		}
		catch (Exception exception) {
			_validateException(exception, null);
		}
	}

	@Test
	public void testPortletDataHandlerImportPortletDataExceptionMessage()
		throws Exception {

		final String message = RandomTestUtil.randomString();

		PortletDataHandler portletDataHandler = new TestPortletDataHandler() {

			@Override
			protected PortletPreferences doImportData(
					PortletDataContext portletDataContext, String portletId,
					PortletPreferences portletPreferences, String data)
				throws Exception {

				throw new PortletDataException(message);
			}

		};

		try {
			portletDataHandler.importData(
				_portletDataContextImport, RandomTestUtil.randomString(), null,
				null);
		}
		catch (Exception exception) {
			_validateException(exception, message);
		}
	}

	@Test
	public void testPortletDataHandlerImportPortletDataExceptionMessageType()
		throws Exception {

		final String message = RandomTestUtil.randomString();

		PortletDataHandler portletDataHandler = new TestPortletDataHandler() {

			@Override
			protected PortletPreferences doImportData(
					PortletDataContext portletDataContext, String portletId,
					PortletPreferences portletPreferences, String data)
				throws Exception {

				PortletDataException portletDataException =
					new PortletDataException(message);

				portletDataException.setType(
					PortletDataException.INVALID_GROUP);

				throw portletDataException;
			}

		};

		try {
			portletDataHandler.importData(
				_portletDataContextImport, RandomTestUtil.randomString(), null,
				null);
		}
		catch (Exception exception) {
			_validateException(exception, message);
		}
	}

	@Test
	public void testPortletDataHandlerImportPortletDataExceptionType()
		throws Exception {

		PortletDataHandler portletDataHandler = new TestPortletDataHandler() {

			@Override
			protected PortletPreferences doImportData(
					PortletDataContext portletDataContext, String portletId,
					PortletPreferences portletPreferences, String data)
				throws Exception {

				throw new PortletDataException(
					PortletDataException.INVALID_GROUP);
			}

		};

		try {
			portletDataHandler.importData(
				_portletDataContextImport, RandomTestUtil.randomString(), null,
				null);
		}
		catch (Exception exception) {
			_validateException(exception, null);
		}
	}

	@Test
	public void testPortletDataHandlerPrepareManifestException()
		throws Exception {

		PortletDataHandler portletDataHandler = new TestPortletDataHandler() {

			@Override
			protected void doPrepareManifestSummary(
					PortletDataContext portletDataContext,
					PortletPreferences portletPreferences)
				throws Exception {

				throw new Exception();
			}

		};

		try {
			portletDataHandler.prepareManifestSummary(
				_portletDataContextExport);
		}
		catch (Exception exception) {
			_validateException(exception, null);
		}
	}

	@Test
	public void testPortletDataHandlerPrepareManifestExceptionMessage()
		throws Exception {

		final String message = RandomTestUtil.randomString();

		PortletDataHandler portletDataHandler = new TestPortletDataHandler() {

			@Override
			protected void doPrepareManifestSummary(
					PortletDataContext portletDataContext,
					PortletPreferences portletPreferences)
				throws Exception {

				throw new Exception(message);
			}

		};

		try {
			portletDataHandler.prepareManifestSummary(
				_portletDataContextExport);
		}
		catch (Exception exception) {
			_validateException(exception, message);
		}
	}

	@Test
	public void testPortletDataHandlerPrepareManifestPortletDataException()
		throws Exception {

		PortletDataHandler portletDataHandler = new TestPortletDataHandler() {

			@Override
			protected void doPrepareManifestSummary(
					PortletDataContext portletDataContext,
					PortletPreferences portletPreferences)
				throws Exception {

				throw new PortletDataException();
			}

		};

		try {
			portletDataHandler.prepareManifestSummary(
				_portletDataContextExport);
		}
		catch (Exception exception) {
			_validateException(exception, null);
		}
	}

	@Test
	public void testPortletDataHandlerPrepareManifestPortletDataExceptionMessage()
		throws Exception {

		final String message = RandomTestUtil.randomString();

		PortletDataHandler portletDataHandler = new TestPortletDataHandler() {

			@Override
			protected void doPrepareManifestSummary(
					PortletDataContext portletDataContext,
					PortletPreferences portletPreferences)
				throws Exception {

				throw new PortletDataException(message);
			}

		};

		try {
			portletDataHandler.prepareManifestSummary(
				_portletDataContextExport);
		}
		catch (Exception exception) {
			_validateException(exception, message);
		}
	}

	@Test
	public void testPortletDataHandlerPrepareManifestPortletDataExceptionMessageType()
		throws Exception {

		final String message = RandomTestUtil.randomString();

		PortletDataHandler portletDataHandler = new TestPortletDataHandler() {

			@Override
			protected void doPrepareManifestSummary(
					PortletDataContext portletDataContext,
					PortletPreferences portletPreferences)
				throws Exception {

				PortletDataException portletDataException =
					new PortletDataException(message);

				portletDataException.setType(
					PortletDataException.INVALID_GROUP);

				throw portletDataException;
			}

		};

		try {
			portletDataHandler.prepareManifestSummary(
				_portletDataContextExport);
		}
		catch (Exception exception) {
			_validateException(exception, message);
		}
	}

	@Test
	public void testPortletDataHandlerPrepareManifestPortletDataExceptionType()
		throws Exception {

		PortletDataHandler portletDataHandler = new TestPortletDataHandler() {

			@Override
			protected void doPrepareManifestSummary(
					PortletDataContext portletDataContext,
					PortletPreferences portletPreferences)
				throws Exception {

				throw new PortletDataException(
					PortletDataException.INVALID_GROUP);
			}

		};

		try {
			portletDataHandler.prepareManifestSummary(
				_portletDataContextExport);
		}
		catch (Exception exception) {
			_validateException(exception, null);
		}
	}

	private void _validateException(Exception exception, String message) {

		// Ensure that thrown exceptions are an instance of PortletDataException

		Assert.assertTrue(
			"Exception thrown always have to be type of PortletDataException",
			exception instanceof PortletDataException);

		PortletDataException portletDataException =
			(PortletDataException)exception;

		String portletId = portletDataException.getPortletId();
		int type = portletDataException.getType();

		// At this point, the portlet ID is mandatory

		Assert.assertFalse(
			"Exceptions coming from a PortletDataHandler has to have a " +
				"portletId attribute",
			Validator.isNull(portletId));

		// It should also have a type by now

		Assert.assertFalse(
			"Exception coming from a PortletDatahandler has to have a type " +
				"attribute",
			type == PortletDataException.DEFAULT);

		// If there was a message, validate that it has not disappeared

		Assert.assertEquals(message, portletDataException.getMessage());
	}

	private static PortletDataContext _portletDataContextExport;
	private static PortletDataContext _portletDataContextImport;

	private class TestPortletDataHandler extends BasePortletDataHandler {
	}

}