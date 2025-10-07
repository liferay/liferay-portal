/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.xml;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;

import java.net.ConnectException;

import java.util.NoSuchElementException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Tomas Polesovsky
 */
@NewEnv(type = NewEnv.Type.JVM)
@NewEnv.JVMArgsLine("-Dattached=true -Xmx15m")
public class SecureXMLFactoryProviderImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_secureXMLFactoryProviderImpl = new SecureXMLFactoryProviderImpl();

		_xmlBombBillionLaughsXML = readDependency(
			"xml-bomb-billion-laughs.xml");
		_xmlBombQuadraticBlowupXML = readDependency(
			"xml-bomb-quadratic-blowup.xml");
		_xxeGeneralEntitiesXML1 = readDependency("xxe-general-entities-1.xml");
		_xxeGeneralEntitiesXML2 = readDependency("xxe-general-entities-2.xml");
		_xxeParameterEntitiesXML1 = readDependency(
			"xxe-parameter-entities-1.xml");
		_xxeParameterEntitiesXML2 = readDependency(
			"xxe-parameter-entities-2.xml");
	}

	@Test
	public void testNewDocumentBuilderFactory() throws Throwable {
		XMLSecurityTest documentBuilderTest = new XMLSecurityTest() {

			@Override
			public void run(String xml) throws Exception {
				DocumentBuilderFactory documentBuilderFactory =
					_secureXMLFactoryProviderImpl.newDocumentBuilderFactory();

				DocumentBuilder documentBuilder =
					documentBuilderFactory.newDocumentBuilder();

				documentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
			}

		};

		runXMLSecurityTest(
			documentBuilderTest, _xmlBombBillionLaughsXML,
			OutOfMemoryError.class, "Billion Laughs XML attack does not work.",
			SAXParseException.class,
			"Vulnerable to Billion Laughs XML attack.");
		runXMLSecurityTest(
			documentBuilderTest, _xmlBombQuadraticBlowupXML,
			OutOfMemoryError.class,
			"Quadratic Blowup XML attack does not work.",
			SAXParseException.class,
			"Vulnerable to Quadratic Blowup XML attack.");
		runXMLSecurityTest(
			documentBuilderTest, _xxeGeneralEntitiesXML1,
			ConnectException.class,
			"General Entities XXE attack using SYSTEM entity does not work.",
			SAXParseException.class,
			"Vulnerable to General Entities XXE attack using SYSTEM entity.");
		runXMLSecurityTest(
			documentBuilderTest, _xxeGeneralEntitiesXML2,
			ConnectException.class,
			"General Entities XXE attack using PUBLIC entity does not work.",
			SAXParseException.class,
			"Vulnerable to General Entities XXE attack using PUBLIC entity.");
		runXMLSecurityTest(
			documentBuilderTest, _xxeParameterEntitiesXML1,
			ConnectException.class,
			"Parameter Entities XXE using SYSTEM entity does not work.",
			SAXParseException.class,
			"Vulnerable to Parameter Entities XXE using SYSTEM entity.");
		runXMLSecurityTest(
			documentBuilderTest, _xxeParameterEntitiesXML2,
			ConnectException.class,
			"Parameter Entities XXE attack using PUBLIC entity does not work.",
			SAXParseException.class,
			"Vulnerable to Parameter Entities XXE attack using PUBLIC entity.");
	}

	@Test
	public void testNewXMLInputFactory() throws Throwable {
		XMLSecurityTest xmlInputFactoryTest = new XMLSecurityTest() {

			@Override
			public void run(String xml) throws Exception {
				XMLInputFactory xmlInputFactory =
					_secureXMLFactoryProviderImpl.newXMLInputFactory();

				XMLEventReader xmlEventReader =
					xmlInputFactory.createXMLEventReader(new StringReader(xml));

				while (xmlEventReader.hasNext()) {
					xmlEventReader.next();
				}
			}

		};

		runXMLSecurityTest(
			xmlInputFactoryTest, _xmlBombBillionLaughsXML,
			NoSuchElementException.class,
			"Billion Laughs XML attack does not work.", null,
			"Vulnerable to Billion Laughs XML attack.");
		runXMLSecurityTest(
			xmlInputFactoryTest, _xmlBombQuadraticBlowupXML,
			NoSuchElementException.class,
			"Quadratic Blowup XML attack does not work.", null,
			"Vulnerable to Quadratic Blowup XML attack.");
		runXMLSecurityTest(
			xmlInputFactoryTest, _xxeGeneralEntitiesXML1,
			NoSuchElementException.class,
			"General Entities XXE attack using SYSTEM entity does not work.",
			null,
			"Vulnerable to General Entities XXE attack using SYSTEM entity.");
		runXMLSecurityTest(
			xmlInputFactoryTest, _xxeGeneralEntitiesXML2,
			NoSuchElementException.class,
			"General Entities XXE attack using PUBLIC entity does not work.",
			null,
			"Vulnerable to  General Entities XXE attack using PUBLIC entity.");
		runXMLSecurityTest(
			xmlInputFactoryTest, _xxeParameterEntitiesXML1,
			NoSuchElementException.class,
			"Parameter Entities XXE using SYSTEM entity does not work.", null,
			"Vulnerable to Parameter Entities XXE using SYSTEM entity.");
		runXMLSecurityTest(
			xmlInputFactoryTest, _xxeParameterEntitiesXML2,
			NoSuchElementException.class,
			"Parameter Entities XXE attack using PUBLIC entity does not work.",
			null,
			"Vulnerable to Parameter Entities XXE attack using PUBLIC entity.");
	}

	@Test
	public void testNewXMLReader() throws Throwable {
		XMLSecurityTest xmlReaderTest = new XMLSecurityTest() {

			@Override
			public void run(String xml) throws Exception {
				XMLReader xmlReader =
					_secureXMLFactoryProviderImpl.newXMLReader();

				if (xmlReader instanceof StripDoctypeXMLReader) {
					StripDoctypeXMLReader stripDoctypeXMLReader =
						(StripDoctypeXMLReader)xmlReader;

					xmlReader = stripDoctypeXMLReader.getXmlReader();
				}

				xmlReader.setContentHandler(
					new DefaultHandler() {

						@Override
						public void characters(
							char[] ch, int start, int length) {

							_contentLength += length;

							if (_contentLength > (1024 * 1024 * 10)) {
								throw new OutOfMemoryError();
							}
						}

						private int _contentLength;

					});

				xmlReader.parse(new InputSource(new StringReader(xml)));
			}

		};

		runXMLSecurityTest(
			xmlReaderTest, _xmlBombBillionLaughsXML, OutOfMemoryError.class,
			"Billion Laughs XML attack does not work.", SAXParseException.class,
			"Vulnerable to Billion Laughs XML attack.");
		runXMLSecurityTest(
			xmlReaderTest, _xmlBombQuadraticBlowupXML, OutOfMemoryError.class,
			"Quadratic Blowup XML attack does not work.",
			SAXParseException.class,
			"Vulnerable to Quadratic Blowup XML attack.");
		runXMLSecurityTest(
			xmlReaderTest, _xxeGeneralEntitiesXML1, ConnectException.class,
			"General Entities XXE attack using SYSTEM entity does not work.",
			SAXParseException.class,
			"Vulnerable to General Entities XXE attack using SYSTEM entity.");
		runXMLSecurityTest(
			xmlReaderTest, _xxeGeneralEntitiesXML2, ConnectException.class,
			"General Entities XXE attack using PUBLIC entity does not work.",
			SAXParseException.class,
			"Vulnerable to General Entities XXE attack using PUBLIC entity.");
		runXMLSecurityTest(
			xmlReaderTest, _xxeParameterEntitiesXML1, ConnectException.class,
			"Parameter Entities XXE using SYSTEM entity does not work.",
			SAXParseException.class,
			"Vulnerable to Parameter Entities XXE using SYSTEM entity.");
		runXMLSecurityTest(
			xmlReaderTest, _xxeParameterEntitiesXML2, ConnectException.class,
			"Parameter Entities XXE attack using PUBLIC entity does not work.",
			SAXParseException.class,
			"Vulnerable to Parameter Entities XXE attack using PUBLIC entity.");
	}

	protected static String readDependency(String name) throws IOException {
		return StringUtil.read(
			SecureXMLFactoryProviderImplTest.class.getResourceAsStream(
				"dependencies/" + name));
	}

	protected void runXMLSecurityTest(
			XMLSecurityTest xmlSecurityTest, String xml,
			Class<? extends Throwable> expectedException, String failMessage)
		throws Throwable {

		try {
			xmlSecurityTest.run(xml);

			if (expectedException != null) {
				Assert.fail(failMessage);
			}
		}
		catch (Throwable throwable) {
			if (expectedException == null) {
				throw throwable;
			}

			Throwable causeThrowable = throwable;

			while (causeThrowable.getCause() != null) {
				causeThrowable = causeThrowable.getCause();
			}

			Class<?> causeClass = causeThrowable.getClass();

			if (!causeClass.isAssignableFrom(expectedException)) {
				throw throwable;
			}
		}
	}

	protected void runXMLSecurityTest(
			XMLSecurityTest xmlSecurityTest, String xml,
			Class<? extends Throwable> disableXMLSecurityExpectedException,
			String disableXMLSecurityFailMessage,
			Class<? extends Throwable> enableXMLSecurityExpectedException,
			String enableXMLSecurityFailMessage)
		throws Throwable {

		boolean xmlSecurityEnabled = ReflectionTestUtil.getFieldValue(
			PropsValues.class, "XML_SECURITY_ENABLED");

		try {
			ReflectionTestUtil.setFieldValue(
				PropsValues.class, "XML_SECURITY_ENABLED", false);

			runXMLSecurityTest(
				xmlSecurityTest, xml, disableXMLSecurityExpectedException,
				disableXMLSecurityFailMessage);

			ReflectionTestUtil.setFieldValue(
				PropsValues.class, "XML_SECURITY_ENABLED", true);

			runXMLSecurityTest(
				xmlSecurityTest, xml, enableXMLSecurityExpectedException,
				enableXMLSecurityFailMessage);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				PropsValues.class, "XML_SECURITY_ENABLED", xmlSecurityEnabled);
		}
	}

	private static String _xmlBombBillionLaughsXML;
	private static String _xmlBombQuadraticBlowupXML;
	private static String _xxeGeneralEntitiesXML1;
	private static String _xxeGeneralEntitiesXML2;
	private static String _xxeParameterEntitiesXML1;
	private static String _xxeParameterEntitiesXML2;

	private SecureXMLFactoryProviderImpl _secureXMLFactoryProviderImpl;

	private abstract class XMLSecurityTest {

		public abstract void run(String xml) throws Exception;

	}

}