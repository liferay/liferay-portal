/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.webdav;

import com.liferay.document.library.kernel.util.DL;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.comparator.GroupFriendlyURLComparator;
import com.liferay.portal.kernel.xml.Namespace;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 * @author Raymond Augé
 */
public class WebDAVUtil {

	public static final Namespace DAV_URI = SAXReaderUtil.createNamespace(
		"D", "DAV:");

	public static final int SC_LOCKED = 423;

	public static final int SC_MULTI_STATUS = 207;

	public static final String TOKEN_PREFIX = "opaquelocktoken:";

	public static Namespace createNamespace(String prefix, String uri) {
		Namespace namespace = null;

		if (uri.equals(WebDAVUtil.DAV_URI.getURI())) {
			namespace = WebDAVUtil.DAV_URI;
		}
		else if (Validator.isNull(prefix)) {
			namespace = SAXReaderUtil.createNamespace(uri);
		}
		else {
			namespace = SAXReaderUtil.createNamespace(prefix, uri);
		}

		return namespace;
	}

	public static long getDepth(HttpServletRequest httpServletRequest) {
		String value = GetterUtil.getString(
			httpServletRequest.getHeader("Depth"));

		if (_log.isDebugEnabled()) {
			_log.debug("\"Depth\" header is " + value);
		}

		if (value.equals("0")) {
			return 0;
		}

		return -1;
	}

	public static String getDestination(
		HttpServletRequest httpServletRequest, String rootPath) {

		String headerDestination = httpServletRequest.getHeader("Destination");

		String[] pathSegments = StringUtil.split(headerDestination, rootPath);

		String destination = pathSegments[pathSegments.length - 1];

		destination = HttpComponentsUtil.decodePath(destination);

		if (_log.isDebugEnabled()) {
			_log.debug("Destination " + destination);
		}

		return destination;
	}

	public static long getGroupId(long companyId, String path)
		throws WebDAVException {

		return getGroupId(companyId, getPathArray(path));
	}

	public static long getGroupId(long companyId, String[] pathArray)
		throws WebDAVException {

		try {
			if (pathArray.length == 0) {
				return 0;
			}

			String name = pathArray[0];

			Group group = GroupLocalServiceUtil.fetchFriendlyURLGroup(
				companyId, StringPool.SLASH + name);

			if (group != null) {
				return group.getGroupId();
			}

			User user = UserLocalServiceUtil.fetchUserByScreenName(
				companyId, name);

			if (user != null) {
				group = user.getGroup();

				return group.getGroupId();
			}
		}
		catch (Exception exception) {
			throw new WebDAVException(exception);
		}

		return 0;
	}

	public static List<Group> getGroups(long userId) throws Exception {
		return getGroups(UserLocalServiceUtil.getUser(userId));
	}

	public static List<Group> getGroups(User user) throws Exception {

		// Guest

		if (user.isGuestUser()) {
			return ListUtil.fromArray(
				GroupLocalServiceUtil.getGroup(
					user.getCompanyId(), GroupConstants.GUEST));
		}

		// Communities

		Set<Group> groups = new HashSet<>();

		OrderByComparator<Group> orderByComparator =
			GroupFriendlyURLComparator.getInstance(true);

		groups.addAll(
			GroupLocalServiceUtil.search(
				user.getCompanyId(), null, null,
				LinkedHashMapBuilder.<String, Object>put(
					"usersGroups", user.getUserId()
				).build(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator));

		// Organizations

		groups.addAll(
			GroupLocalServiceUtil.getUserOrganizationsGroups(
				user.getUserId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS));

		// User

		if (!user.isGuestUser()) {
			groups.add(user.getGroup());
		}

		List<Group> groupsList = new ArrayList<>(groups);

		Collections.sort(groupsList, orderByComparator);

		return groupsList;
	}

	public static String getLockUuid(HttpServletRequest httpServletRequest)
		throws WebDAVException {

		String value = GetterUtil.getString(httpServletRequest.getHeader("If"));

		if (_log.isDebugEnabled()) {
			_log.debug("\"If\" header is " + value);
		}

		if (value.contains("(<DAV:no-lock>)")) {
			if (_log.isWarnEnabled()) {
				_log.warn("Lock tokens can never be <DAV:no-lock>");
			}

			throw new WebDAVException();
		}

		String token = StringPool.BLANK;

		int beg = value.indexOf(TOKEN_PREFIX);

		if (beg >= 0) {
			beg += TOKEN_PREFIX.length();

			if (beg < value.length()) {
				int end = value.indexOf(CharPool.GREATER_THAN, beg);

				token = GetterUtil.getString(value.substring(beg, end));
			}
		}

		return token;
	}

	public static String[] getPathArray(String path) {
		return getPathArray(path, false);
	}

	public static String[] getPathArray(String path, boolean fixTrailing) {
		path = HttpComponentsUtil.fixPath(path, true, fixTrailing);

		return StringUtil.split(path, CharPool.SLASH);
	}

	public static String getResourceName(String[] pathArray) {
		if (pathArray.length <= 2) {
			return StringPool.BLANK;
		}

		return pathArray[pathArray.length - 1];
	}

	public static WebDAVStorage getStorage(String token) {
		return _storages.getService(token);
	}

	public static String getStorageToken(Portlet portlet) {
		WebDAVStorage webDAVStorageInstance =
			portlet.getWebDAVStorageInstance();

		if (webDAVStorageInstance == null) {
			return null;
		}

		return webDAVStorageInstance.getToken();
	}

	public static Collection<String> getStorageTokens() {
		return _storages.keySet();
	}

	public static long getTimeout(HttpServletRequest httpServletRequest) {
		long timeout = 0;

		String value = GetterUtil.getString(
			httpServletRequest.getHeader("Timeout"));

		if (_log.isDebugEnabled()) {
			_log.debug("\"Timeout\" header is " + value);
		}

		int index = value.indexOf(_TIME_PREFIX);

		if (index >= 0) {
			index += _TIME_PREFIX.length();

			if (index < value.length()) {
				timeout = GetterUtil.getLong(value.substring(index));
			}
		}

		return timeout * Time.SECOND;
	}

	public static boolean isOverwrite(HttpServletRequest httpServletRequest) {
		String value = GetterUtil.getString(
			httpServletRequest.getHeader("Overwrite"));

		if (StringUtil.equalsIgnoreCase(value, "F") ||
			!GetterUtil.getBoolean(value)) {

			return false;
		}

		return true;
	}

	public static String stripManualCheckInRequiredPath(String url) {
		return stripToken(url, DL.MANUAL_CHECK_IN_REQUIRED_PATH);
	}

	public static String stripOfficeExtension(String url) {
		String strippedUrl = stripToken(url, DL.OFFICE_EXTENSION_PATH);

		if (strippedUrl.length() != url.length()) {
			strippedUrl = FileUtil.stripExtension(strippedUrl);
		}

		return strippedUrl;
	}

	public static String stripToken(String url, String token) {
		if (Validator.isNull(url)) {
			return StringPool.BLANK;
		}

		int index = url.indexOf(token);

		if (index >= 0) {
			url =
				url.substring(0, index) + url.substring(index + token.length());
		}

		return url;
	}

	private static final String _TIME_PREFIX = "Second-";

	private static final Log _log = LogFactoryUtil.getLog(WebDAVUtil.class);

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	private static final ServiceTrackerMap<String, WebDAVStorage> _storages =
		ServiceTrackerMapFactory.openSingleValueMap(
			_bundleContext, WebDAVStorage.class, "webdav.storage.token",
			new ServiceTrackerCustomizer<WebDAVStorage, WebDAVStorage>() {

				@Override
				public WebDAVStorage addingService(
					ServiceReference<WebDAVStorage> serviceReference) {

					WebDAVStorage webDAVStorage = _bundleContext.getService(
						serviceReference);

					setToken(serviceReference, webDAVStorage);

					return webDAVStorage;
				}

				@Override
				public void modifiedService(
					ServiceReference<WebDAVStorage> serviceReference,
					WebDAVStorage webDAVStorage) {

					setToken(serviceReference, webDAVStorage);
				}

				@Override
				public void removedService(
					ServiceReference<WebDAVStorage> serviceReference,
					WebDAVStorage webDAVStorage) {

					_bundleContext.ungetService(serviceReference);
				}

				protected void setToken(
					ServiceReference<WebDAVStorage> serviceReference,
					WebDAVStorage webDAVStorage) {

					String token = (String)serviceReference.getProperty(
						"webdav.storage.token");

					webDAVStorage.setToken(token);
				}

			});

}