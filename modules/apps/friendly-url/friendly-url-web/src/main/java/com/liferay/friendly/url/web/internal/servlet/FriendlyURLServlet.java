/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.web.internal.servlet;

import com.liferay.friendly.url.info.item.provider.InfoItemFriendlyURLProvider;
import com.liferay.friendly.url.info.item.updater.InfoItemFriendlyURLUpdater;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.info.item.provider.InfoItemPermissionProvider;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONSerializable;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.translation.info.item.provider.InfoItemLanguagesProvider;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"osgi.http.whiteboard.servlet.name=com.liferay.friendly.url.web.internal.servlet.FriendlyURLServlet",
		"osgi.http.whiteboard.servlet.pattern=/friendly-url/*",
		"servlet.init.httpMethods=DELETE,GET,HEAD,POST"
	},
	service = Servlet.class
)
public class FriendlyURLServlet extends HttpServlet {

	@Override
	protected void doDelete(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		try {
			String className = _getClassName(httpServletRequest);

			InfoItemPermissionProvider infoItemPermissionProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemPermissionProvider.class, className);

			if (!infoItemPermissionProvider.hasPermission(
					_permissionCheckerFactory.create(
						_portal.getUser(httpServletRequest)),
					new InfoItemReference(
						className, _getClassPK(httpServletRequest)),
					ActionKeys.UPDATE)) {

				_writeJSON(httpServletResponse, JSONUtil.put("success", false));
			}
			else {
				_friendlyURLEntryLocalService.
					deleteFriendlyURLLocalizationEntry(
						_getEntryId(httpServletRequest),
						_getLanguageId(httpServletRequest));

				_writeJSON(httpServletResponse, JSONUtil.put("success", true));
			}
		}
		catch (Exception exception) {
			_log.error(exception);

			_writeJSON(httpServletResponse, JSONUtil.put("success", false));
		}
	}

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		try {
			User user = _portal.getUser(httpServletRequest);

			if ((user == null) || user.isGuestUser()) {
				_writeJSON(httpServletResponse, JSONUtil.put("success", false));
			}
			else {
				String className = _getClassName(httpServletRequest);
				long classPK = _getClassPK(httpServletRequest);

				InfoItemPermissionProvider<Object> infoItemPermissionProvider =
					_infoItemServiceRegistry.getFirstInfoItemService(
						InfoItemPermissionProvider.class, className);

				if (!infoItemPermissionProvider.hasPermission(
						_permissionCheckerFactory.create(
							_portal.getUser(httpServletRequest)),
						new InfoItemReference(className, classPK),
						ActionKeys.VIEW)) {

					_writeJSON(
						httpServletResponse, JSONUtil.put("success", false));
				}
				else {
					_writeJSON(
						httpServletResponse,
						_getFriendlyURLEntryLocalizationsJSONObject(
							className, classPK));
				}
			}
		}
		catch (Exception exception) {
			_log.error(exception);

			_writeJSON(httpServletResponse, JSONUtil.put("success", false));
		}
	}

	@Override
	protected void doPost(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		try {
			String className = _getClassName(httpServletRequest);
			long classPK = _getClassPK(httpServletRequest);

			InfoItemPermissionProvider<Object> infoItemPermissionProvider =
				_infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemPermissionProvider.class, className);

			if (!infoItemPermissionProvider.hasPermission(
					_permissionCheckerFactory.create(
						_portal.getUser(httpServletRequest)),
					new InfoItemReference(className, classPK),
					ActionKeys.UPDATE)) {

				_writeJSON(httpServletResponse, JSONUtil.put("success", false));
			}
			else {
				InfoItemFriendlyURLUpdater<Object> infoItemFriendlyURLUpdater =
					_infoItemServiceRegistry.getFirstInfoItemService(
						InfoItemFriendlyURLUpdater.class, className);

				infoItemFriendlyURLUpdater.restoreFriendlyURL(
					_portal.getUserId(httpServletRequest), classPK,
					_getEntryId(httpServletRequest),
					_getLanguageId(httpServletRequest));

				_writeJSON(httpServletResponse, JSONUtil.put("success", true));
			}
		}
		catch (Exception exception) {
			_log.error(exception);

			_writeJSON(httpServletResponse, JSONUtil.put("success", false));
		}
	}

	@Override
	protected void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		ServiceContext serviceContext = _getServiceContext(httpServletRequest);

		try {
			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			super.service(httpServletRequest, httpServletResponse);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private String _getClassName(HttpServletRequest httpServletRequest)
		throws PortalException {

		List<String> parts = StringUtil.split(
			httpServletRequest.getPathInfo(), CharPool.SLASH);

		return parts.get(1);
	}

	private long _getClassPK(HttpServletRequest httpServletRequest) {
		List<String> parts = StringUtil.split(
			httpServletRequest.getPathInfo(), CharPool.SLASH);

		return GetterUtil.getLong(parts.get(2));
	}

	private long _getEntryId(HttpServletRequest httpServletRequest) {
		List<String> parts = StringUtil.split(
			httpServletRequest.getPathInfo(), CharPool.SLASH);

		return GetterUtil.getLong(parts.get(3));
	}

	private JSONObject _getFriendlyURLEntryLocalizationsJSONObject(
			String className, long classPK)
		throws Exception {

		JSONObject friendlyURLEntryLocalizationsJSONObject =
			_jsonFactory.createJSONObject();

		InfoItemObjectProvider<Object> infoItemObjectProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectProvider.class, className,
				ClassPKInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);

		Object object = infoItemObjectProvider.getInfoItem(
			new ClassPKInfoItemIdentifier(classPK));

		InfoItemFriendlyURLProvider<Object> infoItemFriendlyURLProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFriendlyURLProvider.class, className);

		InfoItemLanguagesProvider<Object> infoItemLanguagesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemLanguagesProvider.class, className);

		if (infoItemLanguagesProvider == null) {
			infoItemLanguagesProvider = _defaultInfoItemLanguagesProvider;
		}

		for (String languageId :
				infoItemLanguagesProvider.getAvailableLanguageIds(object)) {

			List<FriendlyURLEntryLocalization> friendlyURLEntryLocalizations =
				infoItemFriendlyURLProvider.getFriendlyURLEntryLocalizations(
					object, languageId);

			String mainUrlTitle = infoItemFriendlyURLProvider.getFriendlyURL(
				object, languageId);

			friendlyURLEntryLocalizationsJSONObject.put(
				languageId,
				JSONUtil.put(
					"current", JSONUtil.put("urlTitle", mainUrlTitle)
				).put(
					"history",
					_getJSONArray(
						ListUtil.filter(
							friendlyURLEntryLocalizations,
							friendlyURLEntryLocalization -> !Objects.equals(
								friendlyURLEntryLocalization.getUrlTitle(),
								mainUrlTitle)),
						this::_serializeFriendlyURLEntryLocalization)
				).put(
					"success", true
				));
		}

		return friendlyURLEntryLocalizationsJSONObject;
	}

	private <T> JSONArray _getJSONArray(
		List<T> list, Function<T, JSONSerializable> serialize) {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		list.forEach(t -> jsonArray.put(serialize.apply(t)));

		return jsonArray;
	}

	private String _getLanguageId(HttpServletRequest httpServletRequest) {
		List<String> parts = StringUtil.split(
			httpServletRequest.getPathInfo(), CharPool.SLASH);

		return parts.get(4);
	}

	private ServiceContext _getServiceContext(
			HttpServletRequest httpServletRequest)
		throws ServletException {

		try {
			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				httpServletRequest);

			List<String> parts = StringUtil.split(
				httpServletRequest.getPathInfo(), CharPool.SLASH);

			serviceContext.setScopeGroupId(GetterUtil.getLong(parts.get(0)));

			return serviceContext;
		}
		catch (PortalException portalException) {
			throw new ServletException(portalException);
		}
	}

	private JSONObject _serializeFriendlyURLEntryLocalization(
		FriendlyURLEntryLocalization friendlyEntryLocalization) {

		if (friendlyEntryLocalization == null) {
			return null;
		}

		return JSONUtil.put(
			"friendlyURLEntryId",
			friendlyEntryLocalization.getFriendlyURLEntryId()
		).put(
			"friendlyURLEntryLocalizationId",
			Long.valueOf(
				friendlyEntryLocalization.getFriendlyURLEntryLocalizationId())
		).put(
			"urlTitle", friendlyEntryLocalization.getUrlTitle()
		);
	}

	private void _writeJSON(
			HttpServletResponse httpServletResponse, JSONObject jsonObject)
		throws IOException {

		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);

		if (jsonObject.has("success") && !jsonObject.getBoolean("success")) {
			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

		ServletOutputStream servletOutputStream =
			httpServletResponse.getOutputStream();

		servletOutputStream.print(jsonObject.toString());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FriendlyURLServlet.class);

	private static final InfoItemLanguagesProvider<Object>
		_defaultInfoItemLanguagesProvider =
			new InfoItemLanguagesProvider<Object>() {

				@Override
				public String[] getAvailableLanguageIds(Object object)
					throws PortalException {

					return new String[] {getDefaultLanguageId(object)};
				}

				@Override
				public String getDefaultLanguageId(Object object) {
					return LanguageUtil.getLanguageId(
						LocaleUtil.getSiteDefault());
				}

			};

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private Portal _portal;

}