/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.internal.util;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.comment.DuplicateCommentException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.FriendlyURLMapper;
import com.liferay.portal.kernel.portlet.FriendlyURLMapperThreadLocal;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.service.IdentityServiceContextFunction;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.InetAddressUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xmlrpc.Method;
import com.liferay.portal.kernel.xmlrpc.Response;
import com.liferay.portal.kernel.xmlrpc.XmlRpcConstants;
import com.liferay.portal.xmlrpc.XmlRpcUtil;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;
import net.htmlparser.jericho.TextExtractor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alexander Chow
 */
@Component(service = Method.class)
public class PingbackMethodImpl implements Method {

	public static final int ACCESS_DENIED = 49;

	public static final int GENERIC_FAULT = 0;

	public static final int PINGBACK_ALREADY_REGISTERED = 48;

	public static final int SERVER_ERROR = 50;

	public static final int SOURCE_URI_DOES_NOT_EXIST = 16;

	public static final int SOURCE_URI_INVALID = 17;

	public static final int TARGET_URI_DOES_NOT_EXIST = 32;

	public static final int TARGET_URI_INVALID = 33;

	@Override
	public Response execute(long companyId) {
		try {
			Response response = _addPingback(companyId);

			if (response != null) {
				return response;
			}

			return XmlRpcUtil.createSuccess("Pingback accepted");
		}
		catch (DuplicateCommentException duplicateCommentException) {
			return XmlRpcUtil.createFault(
				PINGBACK_ALREADY_REGISTERED,
				"Pingback is already registered: " +
					duplicateCommentException.getMessage());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return XmlRpcUtil.createFault(
				TARGET_URI_INVALID, "Unable to parse target URI");
		}
	}

	@Override
	public String getMethodName() {
		return "pingback.ping";
	}

	@Override
	public String getToken() {
		return "pingback";
	}

	@Override
	public boolean setArguments(Object[] arguments) {
		try {
			_sourceURI = (String)arguments[0];
			_targetURI = (String)arguments[1];

			return true;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}
	}

	public void setInetAddressLookup(InetAddressLookup inetAddressLookup) {
		_inetAddressLookup = inetAddressLookup;
	}

	public void setPingbackProperties(PingbackProperties pingbackProperties) {
		_pingbackProperties = pingbackProperties;
	}

	public void setPortletIdLookup(PortletIdLookup portletIdLookup) {
		_portletIdLookup = portletIdLookup;
	}

	public interface InetAddressLookup {

		public InetAddress getInetAddressByName(String domain);

	}

	public interface PingbackProperties {

		public int getLinkbackExcerptLength();

		public boolean isPingbackEnabled();

	}

	public interface PortletIdLookup {

		public String getPortletId(
			String className, PortletProvider.Action action);

	}

	private Response _addPingback(long companyId) throws Exception {
		if (!_isPingbackEnabled()) {
			return XmlRpcUtil.createFault(
				XmlRpcConstants.REQUESTED_METHOD_NOT_FOUND,
				"Pingbacks are disabled");
		}

		BlogsEntry entry = _getBlogsEntry(companyId);

		if (!entry.isAllowPingbacks() ||
			Validator.isNull(entry.getUrlTitle())) {

			return XmlRpcUtil.createFault(
				XmlRpcConstants.REQUESTED_METHOD_NOT_FOUND,
				"Pingbacks are disabled");
		}

		Response response = _validateSource();

		if (response != null) {
			return response;
		}

		long userId = _userLocalService.getGuestUserId(companyId);
		long groupId = entry.getGroupId();
		String className = BlogsEntry.class.getName();
		long classPK = entry.getEntryId();

		String body = StringBundler.concat(
			"[...] ", _getExcerpt(), " [...] <a href=", _sourceURI, ">",
			_language.get(LocaleUtil.getSiteDefault(), "read-more"), "</a>");

		ServiceContext serviceContext = _buildServiceContext(
			companyId, groupId, entry.getUrlTitle());

		_commentManager.addComment(
			userId, groupId, className, classPK, body,
			new IdentityServiceContextFunction(serviceContext));

		return null;
	}

	private ServiceContext _buildServiceContext(
			long companyId, long groupId, String urlTitle)
		throws Exception {

		ServiceContext serviceContext = new ServiceContext();

		String pingbackUserName = _language.get(
			LocaleUtil.getSiteDefault(), "pingback");

		serviceContext.setAttribute("pingbackUserName", pingbackUserName);

		String portletId = _getPortletId(
			BlogsEntry.class.getName(), PortletProvider.Action.VIEW);

		if (Validator.isNull(portletId)) {
			return serviceContext;
		}

		StringBundler sb = new StringBundler(5);

		String layoutFullURL = _portal.getLayoutFullURL(groupId, portletId);

		sb.append(layoutFullURL);

		sb.append(Portal.FRIENDLY_URL_SEPARATOR);

		Portlet portlet = _portletLocalService.getPortletById(
			companyId, portletId);

		sb.append(portlet.getFriendlyURLMapping());

		sb.append(StringPool.SLASH);
		sb.append(urlTitle);

		serviceContext.setAttribute("redirect", sb.toString());
		serviceContext.setLayoutFullURL(layoutFullURL);

		return serviceContext;
	}

	private BlogsEntry _getBlogsEntry(long companyId) throws Exception {
		BlogsEntry entry = null;

		URL url = new URL(_targetURI);

		String friendlyURL = url.getPath();

		int end = friendlyURL.indexOf(Portal.FRIENDLY_URL_SEPARATOR);

		if (end != -1) {
			friendlyURL = friendlyURL.substring(0, end);
		}

		long plid = _portal.getPlidFromFriendlyURL(companyId, friendlyURL);

		Map<String, String[]> params = new HashMap<>();

		Portlet portlet = _portletLocalService.getPortletById(
			_getPortletId(
				BlogsEntry.class.getName(), PortletProvider.Action.VIEW));

		FriendlyURLMapper friendlyURLMapper =
			portlet.getFriendlyURLMapperInstance();

		friendlyURL = url.getPath();

		end = friendlyURL.indexOf(Portal.FRIENDLY_URL_SEPARATOR);

		if (end != -1) {
			friendlyURL = friendlyURL.substring(
				end + Portal.FRIENDLY_URL_SEPARATOR.length() - 1);
		}

		Map<String, Object> requestContext = new HashMap<>();

		try (SafeCloseable safeCloseable =
				FriendlyURLMapperThreadLocal.setPRPIdentifiersWithSafeCloseable(
					new HashMap<>())) {

			friendlyURLMapper.populateParams(
				friendlyURL, params, requestContext);
		}

		String param = _getParam(params, "entryId");

		if (Validator.isNotNull(param)) {
			long entryId = GetterUtil.getLong(param);

			entry = _blogsEntryLocalService.getEntry(entryId);
		}
		else {
			long groupId = _portal.getScopeGroupId(plid);
			String urlTitle = _getParam(params, "urlTitle");

			entry = _blogsEntryLocalService.getEntry(groupId, urlTitle);
		}

		return entry;
	}

	private String _getExcerpt() throws Exception {
		String html = _http.URLtoString(_sourceURI);

		Source source = new Source(html);

		source.fullSequentialParse();

		List<Element> elements = source.getAllElements("a");

		for (Element element : elements) {
			String href = GetterUtil.getString(
				element.getAttributeValue("href"));

			if (href.equals(_targetURI)) {
				element = element.getParentElement();

				TextExtractor textExtractor = new TextExtractor(element);

				String body = textExtractor.toString();

				if (body.length() < _getLinkbackExcerptLength()) {
					element = element.getParentElement();

					if (element != null) {
						textExtractor = new TextExtractor(element);

						body = textExtractor.toString();
					}
				}

				return StringUtil.shorten(body, _getLinkbackExcerptLength());
			}
		}

		return StringPool.BLANK;
	}

	private InetAddress _getInetAddressByName(String domain)
		throws UnknownHostException {

		if (_inetAddressLookup != null) {
			return _inetAddressLookup.getInetAddressByName(domain);
		}

		return InetAddressUtil.getInetAddressByName(domain);
	}

	private int _getLinkbackExcerptLength() {
		if (_pingbackProperties != null) {
			return _pingbackProperties.getLinkbackExcerptLength();
		}

		return PropsValues.BLOGS_LINKBACK_EXCERPT_LENGTH;
	}

	private String _getParam(Map<String, String[]> params, String name) {
		String[] paramArray = params.get(name);

		if (paramArray == null) {
			String namespace = _portal.getPortletNamespace(
				_getPortletId(
					BlogsEntry.class.getName(), PortletProvider.Action.VIEW));

			paramArray = params.get(namespace + name);
		}

		if (ArrayUtil.isNotEmpty(paramArray)) {
			return paramArray[0];
		}

		return null;
	}

	private String _getPortletId(
		String className, PortletProvider.Action action) {

		if (_portletIdLookup != null) {
			return _portletIdLookup.getPortletId(className, action);
		}

		return PortletProviderUtil.getPortletId(className, action);
	}

	private boolean _isPingbackEnabled() {
		if (_pingbackProperties != null) {
			return _pingbackProperties.isPingbackEnabled();
		}

		return PropsValues.BLOGS_PINGBACK_ENABLED;
	}

	private boolean _isSourceURILocalNetwork() {
		try {
			URL url = new URL(_sourceURI);

			return InetAddressUtil.isLocalInetAddress(
				_getInetAddressByName(url.getHost()));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return true;
	}

	private Response _validateSource() throws Exception {
		if (_isSourceURILocalNetwork()) {
			return XmlRpcUtil.createFault(ACCESS_DENIED, "Access Denied");
		}

		Source source = null;

		try {
			String html = _http.URLtoString(_sourceURI);

			source = new Source(html);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return XmlRpcUtil.createFault(
				SOURCE_URI_DOES_NOT_EXIST, "Error accessing source URI");
		}

		List<StartTag> startTags = source.getAllStartTags("a");

		for (StartTag startTag : startTags) {
			String href = GetterUtil.getString(
				startTag.getAttributeValue("href"));

			if (href.equals(_targetURI)) {
				return null;
			}
		}

		return XmlRpcUtil.createFault(
			SOURCE_URI_INVALID, "Unable to find target URI in source");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PingbackMethodImpl.class);

	@Reference
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Reference
	private CommentManager _commentManager;

	@Reference
	private Http _http;

	private InetAddressLookup _inetAddressLookup;

	@Reference
	private Language _language;

	private PingbackProperties _pingbackProperties;

	@Reference
	private Portal _portal;

	private PortletIdLookup _portletIdLookup;

	@Reference
	private PortletLocalService _portletLocalService;

	private String _sourceURI;
	private String _targetURI;

	@Reference
	private UserLocalService _userLocalService;

}