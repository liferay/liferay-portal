/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.security.xml.SecureXMLFactoryProviderUtil;
import com.liferay.portal.kernel.servlet.TransferHeadersHelperUtil;
import com.liferay.portal.kernel.servlet.URLEncoder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionURL;
import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderURL;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Writer;

import java.lang.reflect.Constructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Brian Wing Shun Chan
 * @author Neil Griffin
 */
public abstract class PortletResponseImpl implements LiferayPortletResponse {

	@Override
	public void addDateHeader(String name, long date) {
		if (Validator.isNull(name)) {
			throw new IllegalArgumentException();
		}

		Long[] values = (Long[])_headers.get(name);

		if (values == null) {
			setDateHeader(name, date);
		}
		else {
			values = ArrayUtil.append(values, date);

			_headers.put(name, values);
		}
	}

	@Override
	public void addHeader(String name, String value) {
		if (Validator.isNull(name)) {
			throw new IllegalArgumentException();
		}

		String[] values = (String[])_headers.get(name);

		if (values == null) {
			setHeader(name, value);
		}
		else {
			values = ArrayUtil.append(values, value);

			_headers.put(name, values);
		}
	}

	@Override
	public void addIntHeader(String name, int value) {
		if (Validator.isNull(name)) {
			throw new IllegalArgumentException();
		}

		Integer[] values = (Integer[])_headers.get(name);

		if (values == null) {
			setIntHeader(name, value);
		}
		else {
			values = ArrayUtil.append(values, value);

			_headers.put(name, values);
		}
	}

	@Override
	public void addProperty(Cookie cookie) {
		if (cookie == null) {
			throw new IllegalArgumentException();
		}

		Cookie[] cookies = (Cookie[])_headers.get("cookies");

		if (cookies == null) {
			_headers.put("cookies", new Cookie[] {cookie});
		}
		else {
			cookies = ArrayUtil.append(cookies, cookie);

			_headers.put("cookies", cookies);
		}
	}

	@Override
	public void addProperty(String key, Element element) {
		if (key == null) {
			throw new IllegalArgumentException();
		}

		if (!StringUtil.equalsIgnoreCase(
				key, MimeResponse.MARKUP_HEAD_ELEMENT)) {

			return;
		}

		if ((element != null) &&
			StringUtil.equalsIgnoreCase(element.getNodeName(), "script") &&
			!element.hasChildNodes()) {

			// LPS-77798

			element = (Element)element.cloneNode(true);

			element.appendChild(_document.createTextNode(StringPool.SPACE));
		}

		List<Element> values = _markupHeadElements.get(key);

		if (values == null) {
			if (element != null) {
				values = new ArrayList<>();

				values.add(element);

				_markupHeadElements.put(key, values);
			}
		}
		else {
			if (element == null) {
				_markupHeadElements.remove(key);
			}
			else {
				values.add(element);
			}
		}
	}

	@Override
	public void addProperty(String key, String value) {
		if (Validator.isNull(key)) {
			throw new IllegalArgumentException();
		}

		addHeader(key, value);
	}

	@Override
	public <T extends PortletURL & ActionURL> T createActionURL() {
		Portlet portlet = getPortlet();

		PortletApp portletApp = portlet.getPortletApp();

		if (portletApp.getSpecMajorVersion() == 3) {
			return (T)createActionURL(portletName, MimeResponse.Copy.PUBLIC);
		}

		return (T)createActionURL(portletName);
	}

	@Override
	public ActionURL createActionURL(MimeResponse.Copy copy) {
		return (ActionURL)createActionURL(portletName, copy);
	}

	@Override
	public LiferayPortletURL createActionURL(String portletName) {
		return createLiferayPortletURL(
			portletName, PortletRequest.ACTION_PHASE);
	}

	@Override
	public LiferayPortletURL createActionURL(
		String portletName, MimeResponse.Copy copy) {

		return createLiferayPortletURL(
			portletName, PortletRequest.ACTION_PHASE, copy);
	}

	@Override
	public Element createElement(String tagName) throws DOMException {
		if (_document == null) {
			try {
				DocumentBuilderFactory documentBuilderFactory =
					SecureXMLFactoryProviderUtil.newDocumentBuilderFactory();

				DocumentBuilder documentBuilder =
					documentBuilderFactory.newDocumentBuilder();

				_document = documentBuilder.newDocument();
			}
			catch (ParserConfigurationException parserConfigurationException) {
				throw new DOMException(
					DOMException.INVALID_STATE_ERR,
					parserConfigurationException.getMessage());
			}
		}

		return _document.createElement(tagName);
	}

	@Override
	public LiferayPortletURL createLiferayPortletURL(
		long plid, String portletName, String lifecycle) {

		return createLiferayPortletURL(plid, portletName, lifecycle, true);
	}

	@Override
	public LiferayPortletURL createLiferayPortletURL(
		long plid, String portletName, String lifecycle,
		boolean includeLinkToLayoutUuid) {

		return createLiferayPortletURL(
			plid, portletName, lifecycle, null, includeLinkToLayoutUuid);
	}

	@Override
	public LiferayPortletURL createLiferayPortletURL(
		long plid, String portletName, String lifecycle,
		MimeResponse.Copy copy) {

		return createLiferayPortletURL(
			plid, portletName, lifecycle, copy, true);
	}

	@Override
	public LiferayPortletURL createLiferayPortletURL(
		long plid, String portletName, String lifecycle, MimeResponse.Copy copy,
		boolean includeLinkToLayoutUuid) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)portletRequestImpl.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = getLayout(portletRequestImpl, themeDisplay);

		if (_portletPreferences == null) {
			_portletPreferences = getPortletPreferences(
				themeDisplay, layout, portletName);
		}

		LiferayPortletURLPrivilegedAction liferayPortletURLPrivilegedAction =
			new LiferayPortletURLPrivilegedAction(
				plid, portletName, lifecycle, copy, includeLinkToLayoutUuid,
				layout, getPortlet(), _portletPreferences, portletRequestImpl,
				this, _plid, _constructors);

		return liferayPortletURLPrivilegedAction.run();
	}

	@Override
	public LiferayPortletURL createLiferayPortletURL(String lifecycle) {
		return createLiferayPortletURL(portletName, lifecycle);
	}

	@Override
	public LiferayPortletURL createLiferayPortletURL(
		String portletName, String lifecycle) {

		return createLiferayPortletURL(_plid, portletName, lifecycle);
	}

	@Override
	public LiferayPortletURL createLiferayPortletURL(
		String portletName, String lifecycle, MimeResponse.Copy copy) {

		return createLiferayPortletURL(_plid, portletName, lifecycle, copy);
	}

	@Override
	public <T extends PortletURL & RenderURL> T createRenderURL() {
		Portlet portlet = getPortlet();

		PortletApp portletApp = portlet.getPortletApp();

		if (portletApp.getSpecMajorVersion() == 3) {
			return (T)createRenderURL(portletName, MimeResponse.Copy.PUBLIC);
		}

		return (T)createRenderURL(portletName);
	}

	@Override
	public RenderURL createRenderURL(MimeResponse.Copy copy) {
		return (RenderURL)createRenderURL(portletName, copy);
	}

	@Override
	public LiferayPortletURL createRenderURL(String portletName) {
		return createLiferayPortletURL(
			portletName, PortletRequest.RENDER_PHASE);
	}

	@Override
	public LiferayPortletURL createRenderURL(
		String portletName, MimeResponse.Copy copy) {

		return createLiferayPortletURL(
			portletName, PortletRequest.RENDER_PHASE, copy);
	}

	@Override
	public ResourceURL createResourceURL() {
		return createResourceURL(portletName);
	}

	@Override
	public LiferayPortletURL createResourceURL(String portletName) {
		return createLiferayPortletURL(
			_plid, portletName, PortletRequest.RESOURCE_PHASE,
			MimeResponse.Copy.ALL, true);
	}

	@Override
	public String encodeURL(String path) {
		if ((path == null) ||
			(!path.startsWith("#") && !path.startsWith("/") &&
			 !path.contains("://"))) {

			// Allow '#' as well to workaround a bug in Oracle ADF 10.1.3

			throw new IllegalArgumentException(
				"URL path must start with a '/' or include '://'");
		}

		if (_urlEncoder != null) {
			return _urlEncoder.encodeURL(httpServletResponse, path);
		}

		return path;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public HttpServletRequest getHttpServletRequest() {
		return portletRequestImpl.getHttpServletRequest();
	}

	@Override
	public HttpServletResponse getHttpServletResponse() {
		return httpServletResponse;
	}

	@Override
	public abstract String getLifecycle();

	@Override
	public String getNamespace() {
		if (_namespace == null) {
			_namespace = PortalUtil.getPortletNamespace(portletName);
		}

		return _namespace;
	}

	public long getPlid() {
		return _plid;
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	public String getPortletName() {
		return portletName;
	}

	public PortletRequestImpl getPortletRequest() {
		return portletRequestImpl;
	}

	@Override
	public Map<String, String[]> getProperties() {
		Map<String, String[]> properties = new LinkedHashMap<>();

		for (Map.Entry<String, Object[]> entry : _headers.entrySet()) {
			String name = entry.getKey();

			Object[] values = entry.getValue();

			String[] valuesString = new String[values.length];

			for (int i = 0; i < values.length; i++) {
				valuesString[i] = values[i].toString();
			}

			properties.put(name, valuesString);
		}

		return properties;
	}

	@Override
	public String getProperty(String key) {
		Object[] values = _headers.get(key);

		if (values instanceof String[]) {
			return (String)values[0];
		}

		return null;
	}

	@Override
	public Collection<String> getPropertyNames() {
		if (_headers.isEmpty()) {
			return Collections.emptySet();
		}

		List<String> propertyNames = new ArrayList<>();

		for (Map.Entry<String, Object[]> entry : _headers.entrySet()) {
			Object[] values = entry.getValue();

			if (values instanceof String[]) {
				propertyNames.add(entry.getKey());
			}
		}

		return propertyNames;
	}

	@Override
	public Collection<String> getPropertyValues(String key) {
		Object[] values = _headers.get(key);

		if (values instanceof String[]) {
			return Arrays.asList((String[])values);
		}

		return Collections.emptySet();
	}

	public URLEncoder getUrlEncoder() {
		return _urlEncoder;
	}

	public void init(
		PortletRequestImpl portletRequestImpl,
		HttpServletResponse httpServletResponse) {

		this.portletRequestImpl = portletRequestImpl;
		this.httpServletResponse = httpServletResponse;

		_portlet = portletRequestImpl.getPortlet();

		portletName = _portlet.getPortletId();

		_companyId = _portlet.getCompanyId();

		setPlid(portletRequestImpl.getPlid());
	}

	@Override
	public void setDateHeader(String name, long date) {
		if (Validator.isNull(name)) {
			throw new IllegalArgumentException();
		}

		if (date <= 0) {
			_headers.remove(name);
		}
		else {
			_headers.put(name, new Long[] {date});
		}
	}

	@Override
	public void setHeader(String name, String value) {
		if (Validator.isNull(name)) {
			throw new IllegalArgumentException();
		}

		if (Validator.isNull(value)) {
			_headers.remove(name);
		}
		else {
			_headers.put(name, new String[] {value});
		}
	}

	@Override
	public void setIntHeader(String name, int value) {
		if (Validator.isNull(name)) {
			throw new IllegalArgumentException();
		}

		if (value <= 0) {
			_headers.remove(name);
		}
		else {
			_headers.put(name, new Integer[] {value});
		}
	}

	public void setPlid(long plid) {
		_plid = plid;

		if (_plid <= 0) {
			Layout layout = (Layout)portletRequestImpl.getAttribute(
				WebKeys.LAYOUT);

			if (layout != null) {
				_plid = layout.getPlid();
			}
		}
	}

	@Override
	public void setProperty(String key, String value) {
		if (key == null) {
			throw new IllegalArgumentException();
		}

		setHeader(key, value);
	}

	@Override
	public void setURLEncoder(URLEncoder urlEncoder) {
		_urlEncoder = urlEncoder;
	}

	@Override
	public void transferHeaders(HttpServletResponse httpServletResponse) {
		TransferHeadersHelperUtil.transferHeaders(
			_headers, httpServletResponse);
	}

	@Override
	public void transferMarkupHeadElements() {
		List<Element> elements = _markupHeadElements.get(
			MimeResponse.MARKUP_HEAD_ELEMENT);

		if (ListUtil.isEmpty(elements)) {
			return;
		}

		HttpServletRequest httpServletRequest = getHttpServletRequest();

		List<String> markupHeadElements =
			(List<String>)httpServletRequest.getAttribute(
				MimeResponse.MARKUP_HEAD_ELEMENT);

		if (markupHeadElements == null) {
			markupHeadElements = new ArrayList<>();

			httpServletRequest.setAttribute(
				MimeResponse.MARKUP_HEAD_ELEMENT, markupHeadElements);
		}

		for (Element element : elements) {
			try {
				Writer writer = new UnsyncStringWriter();

				TransformerFactory transformerFactory =
					SecureXMLFactoryProviderUtil.newTransformerFactory();

				Transformer transformer = transformerFactory.newTransformer();

				transformer.setOutputProperty(
					OutputKeys.OMIT_XML_DECLARATION, "yes");

				transformer.transform(
					new DOMSource(element), new StreamResult(writer));

				markupHeadElements.add(writer.toString());
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}
		}
	}

	protected void clearHeaders() {
		_headers.clear();
		_markupHeadElements.clear();
	}

	protected Layout getLayout(
		PortletRequest portletRequest, ThemeDisplay themeDisplay) {

		Layout layout = (Layout)portletRequest.getAttribute(WebKeys.LAYOUT);

		if ((layout == null) && (themeDisplay != null)) {
			layout = themeDisplay.getLayout();
		}

		return layout;
	}

	protected PortletPreferences getPortletPreferences(
		ThemeDisplay themeDisplay, Layout layout, String portletName) {

		if (themeDisplay == null) {
			return PortletPreferencesFactoryUtil.getStrictLayoutPortletSetup(
				layout, portletName);
		}

		return themeDisplay.getStrictLayoutPortletSetup(layout, portletName);
	}

	protected HttpServletResponse httpServletResponse;
	protected String portletName;
	protected PortletRequestImpl portletRequestImpl;

	private static final Log _log = LogFactoryUtil.getLog(
		PortletResponseImpl.class);

	private long _companyId;
	private final Map<String, Constructor<? extends PortletURLImpl>>
		_constructors = new ConcurrentHashMap<>();
	private Document _document;
	private final Map<String, Object[]> _headers = new LinkedHashMap<>();
	private final Map<String, List<Element>> _markupHeadElements =
		new LinkedHashMap<>();
	private String _namespace;
	private long _plid;
	private Portlet _portlet;
	private PortletPreferences _portletPreferences;
	private URLEncoder _urlEncoder;

}