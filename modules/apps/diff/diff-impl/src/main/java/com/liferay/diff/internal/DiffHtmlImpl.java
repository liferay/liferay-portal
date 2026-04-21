/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.diff.internal;

import com.liferay.diff.DiffHtml;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProviderUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.Reader;

import java.util.Locale;
import java.util.Objects;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.osgi.service.component.annotations.Component;

import org.outerj.daisy.diff.helper.NekoHtmlParser;
import org.outerj.daisy.diff.html.HTMLDiffer;
import org.outerj.daisy.diff.html.HtmlSaxDiffOutput;
import org.outerj.daisy.diff.html.TextNodeComparator;
import org.outerj.daisy.diff.html.dom.DomTreeBuilder;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * @author Julio Camarero
 */
@Component(service = DiffHtml.class)
public class DiffHtmlImpl implements DiffHtml {

	@Override
	public String diff(Reader source, Reader target) throws Exception {
		if (source == null) {
			throw new NullPointerException("Source is null");
		}

		if (target == null) {
			throw new NullPointerException("Target is null");
		}

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				DiffHtmlImpl.class.getClassLoader())) {

			SAXTransformerFactory saxTransformerFactory =
				(SAXTransformerFactory)
					SecureXMLFactoryProviderUtil.newTransformerFactory();

			TransformerHandler transformerHandler =
				saxTransformerFactory.newTransformerHandler();

			Transformer transformer = transformerHandler.getTransformer();

			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			transformer.setOutputProperty(
				OutputKeys.OMIT_XML_DECLARATION, "yes");

			transformerHandler.setResult(new StreamResult(unsyncStringWriter));

			ContentHandler contentHandler = new DiffOutputFilter(
				transformerHandler);

			NekoHtmlParser nekoHtmlParser = new NekoHtmlParser();

			DomTreeBuilder oldDomTreeBuilder = new DomTreeBuilder();

			nekoHtmlParser.parse(new InputSource(source), oldDomTreeBuilder);

			Locale locale = LocaleUtil.getDefault();

			TextNodeComparator leftTextNodeComparator = new TextNodeComparator(
				oldDomTreeBuilder, locale);

			DomTreeBuilder newDomTreeBuilder = new DomTreeBuilder();

			nekoHtmlParser.parse(new InputSource(target), newDomTreeBuilder);

			TextNodeComparator rightTextNodeComparator = new TextNodeComparator(
				newDomTreeBuilder, locale);

			contentHandler.startDocument();

			HtmlSaxDiffOutput htmlSaxDiffOutput = new HtmlSaxDiffOutput(
				contentHandler, _DIFF);

			HTMLDiffer htmlDiffer = new HTMLDiffer(htmlSaxDiffOutput);

			htmlDiffer.diff(leftTextNodeComparator, rightTextNodeComparator);

			contentHandler.endDocument();

			unsyncStringWriter.flush();

			String string = unsyncStringWriter.toString();

			if (string.startsWith("<?xml")) {
				int index = string.indexOf("?>");

				string = string.substring(index + 2);
			}

			return string;
		}
	}

	@Override
	public String replaceStyles(String html) {
		return StringUtil.replace(
			html,
			new String[] {
				"changeType=\"diff-added-image\"",
				"changeType=\"diff-changed-image\"",
				"changeType=\"diff-removed-image\"",
				"class=\"diff-html-added\"", "class=\"diff-html-changed\"",
				"class=\"diff-html-removed\""
			},
			new String[] {
				"style=\"border: 10px solid #CFC;\"",
				"style=\"border: 10px solid blue;\"",
				"style=\"border: 10px solid #FDC6C6;\"",
				"style=\"background-color: #CFC;\"",
				"style=\"border-bottom: 2px dotted blue;\"",
				"style=\"background-color: #FDC6C6; text-decoration: " +
					"line-through;\""
			});
	}

	private static final String _DIFF = "diff";

	private static class DiffOutputFilter extends XMLFilterImpl {

		public DiffOutputFilter(TransformerHandler transformerHandler) {
			setContentHandler(transformerHandler);

			_transformerHandler = transformerHandler;
		}

		@Override
		public void characters(char[] ch, int start, int length)
			throws SAXException {

			if (length > 0) {
				_childless = false;
			}

			super.characters(ch, start, length);
		}

		@Override
		public void endElement(String uri, String localName, String qName)
			throws SAXException {

			if (_childless && !StringUtil.equalsIgnoreCase(localName, "img")) {
				_transformerHandler.comment(_EMPTY_CHARS, 0, 0);
			}

			_childless = false;

			super.endElement(uri, localName, qName);
		}

		@Override
		public void startElement(
				String uri, String localName, String qName,
				Attributes attributes)
			throws SAXException {

			_childless = true;

			if (!StringUtil.equalsIgnoreCase(localName, "img")) {
				super.startElement(uri, localName, qName, attributes);

				return;
			}

			String changeType = attributes.getValue("changeType");

			if (!Objects.equals(changeType, "diff-added-image") &&
				!Objects.equals(changeType, "diff-removed-image")) {

				super.startElement(uri, localName, qName, attributes);

				return;
			}

			AttributesImpl attributesImpl = new AttributesImpl(attributes);

			attributesImpl.addAttribute(
				StringPool.BLANK, "onAbort", "onAbort", "CDATA",
				"updateOverlays()");
			attributesImpl.addAttribute(
				StringPool.BLANK, "onError", "onError", "CDATA",
				"updateOverlays()");
			attributesImpl.addAttribute(
				StringPool.BLANK, "onLoad", "onLoad", "CDATA",
				"updateOverlays()");

			super.startElement(uri, localName, qName, attributesImpl);
		}

		private static final char[] _EMPTY_CHARS = new char[0];

		private boolean _childless;
		private final TransformerHandler _transformerHandler;

	}

}