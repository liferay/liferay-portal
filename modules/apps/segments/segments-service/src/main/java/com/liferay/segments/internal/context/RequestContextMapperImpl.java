/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.context;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.BooleanEntityField;
import com.liferay.portal.odata.entity.ComplexEntityField;
import com.liferay.portal.odata.entity.DateTimeEntityField;
import com.liferay.portal.odata.entity.DoubleEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.entity.IdEntityField;
import com.liferay.portal.odata.entity.IntegerEntityField;
import com.liferay.portal.odata.entity.StringEntityField;
import com.liferay.portal.servlet.BrowserSnifferUtil;
import com.liferay.segments.context.Context;
import com.liferay.segments.context.RequestContextMapper;
import com.liferay.segments.context.contributor.RequestContextContributor;
import com.liferay.segments.internal.odata.entity.ContextEntityModel;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Eduardo García
 * @author Raymond Augé
 */
@Component(service = RequestContextMapper.class)
public class RequestContextMapperImpl implements RequestContextMapper {

	@Override
	public Context map(HttpServletRequest httpServletRequest) {
		Context context = new Context();

		context.put(
			Context.BROWSER,
			BrowserSnifferUtil.getBrowserId(httpServletRequest));
		context.put(Context.COOKIES, _getCookies(httpServletRequest));
		context.put(Context.HOSTNAME, httpServletRequest.getServerName());
		context.put(
			Context.LANGUAGE_ID,
			LocaleUtil.toLanguageId(_portal.getLocale(httpServletRequest)));

		User user = null;

		try {
			user = _portal.initUser(httpServletRequest);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		ZonedDateTime lastSignInZonedDateTime = ZonedDateTime.of(
			LocalDateTime.MIN, ZoneOffset.UTC);

		if ((user != null) && (user.getLastLoginDate() != null)) {
			Date lastLoginDate = user.getLastLoginDate();

			lastSignInZonedDateTime = ZonedDateTime.ofInstant(
				lastLoginDate.toInstant(), ZoneOffset.UTC);
		}

		context.put(Context.LAST_SIGN_IN_DATE_TIME, lastSignInZonedDateTime);

		context.put(Context.LOCAL_DATE, LocalDate.from(ZonedDateTime.now()));
		context.put(
			Context.REFERRER_URL,
			GetterUtil.getString(
				httpServletRequest.getHeader(HttpHeaders.REFERER)));
		context.put(
			Context.REQUEST_PARAMETERS,
			_getRequestParameters(httpServletRequest));

		boolean signedIn = false;

		if (user != null) {
			signedIn = !user.isGuestUser();
		}

		context.put(Context.SIGNED_IN, signedIn);

		context.put(
			Context.URL, _portal.getCurrentCompleteURL(httpServletRequest));

		String userAgent = GetterUtil.getString(
			httpServletRequest.getHeader(HttpHeaders.USER_AGENT));

		context.put(Context.USER_AGENT, userAgent);

		for (RequestContextContributor requestContextContributor :
				_serviceTrackerMap.values()) {

			requestContextContributor.contribute(context, httpServletRequest);
		}

		return context;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceRegistration = bundleContext.registerService(
			EntityModel.class, _contextEntityModel,
			MapUtil.singletonDictionary(
				"entity.model.name", ContextEntityModel.NAME));

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, RequestContextContributor.class,
			"request.context.contributor.key",
			new RequestContextContributorServiceTrackerCustomizer(
				bundleContext));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();

		_serviceRegistration.unregister();
	}

	private String[] _getCookies(HttpServletRequest httpServletRequest) {
		Cookie[] httpServletRequestCookies = httpServletRequest.getCookies();

		if (httpServletRequestCookies == null) {
			return new String[0];
		}

		String[] cookies = new String[httpServletRequestCookies.length];

		for (int i = 0; i < httpServletRequestCookies.length; i++) {
			cookies[i] =
				httpServletRequestCookies[i].getName() + "=" +
					httpServletRequestCookies[i].getValue();
		}

		return cookies;
	}

	private String[] _getRequestParameters(
		HttpServletRequest httpServletRequest) {

		Map<String, String[]> parameterMap =
			httpServletRequest.getParameterMap();

		if (parameterMap.isEmpty()) {
			return new String[0];
		}

		List<String> requestParameters = new ArrayList<>();

		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			requestParameters.add(
				entry.getKey() + "=" + StringUtil.merge(entry.getValue()));
		}

		return requestParameters.toArray(new String[0]);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RequestContextMapperImpl.class);

	private final ContextEntityModel _contextEntityModel =
		new ContextEntityModel(Collections.emptyList());

	@Reference
	private Portal _portal;

	private ServiceRegistration<EntityModel> _serviceRegistration;
	private ServiceTrackerMap<String, RequestContextContributor>
		_serviceTrackerMap;

	private class RequestContextContributorServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<RequestContextContributor, RequestContextContributor> {

		@Override
		public RequestContextContributor addingService(
			ServiceReference<RequestContextContributor> serviceReference) {

			String requestContextContributorKey = GetterUtil.getString(
				serviceReference.getProperty(
					"request.context.contributor.key"));
			String requestContextContributorType = GetterUtil.getString(
				serviceReference.getProperty(
					"request.context.contributor.type"));

			List<EntityField> customEntityFields = _addCustomEntityField(
				requestContextContributorKey, requestContextContributorType);

			Map<String, EntityField> entityFieldsMap =
				_contextEntityModel.getEntityFieldsMap();

			entityFieldsMap.put(
				"customContext",
				new ComplexEntityField("customContext", customEntityFields));

			return _bundleContext.getService(serviceReference);
		}

		@Override
		public void modifiedService(
			ServiceReference<RequestContextContributor> serviceReference,
			RequestContextContributor requestContextContributor) {

			removedService(serviceReference, requestContextContributor);

			addingService(serviceReference);
		}

		@Override
		public void removedService(
			ServiceReference<RequestContextContributor> serviceReference,
			RequestContextContributor requestContextContributor) {

			String requestContextContributorKey = GetterUtil.getString(
				serviceReference.getProperty(
					"request.context.contributor.key"));

			List<EntityField> customEntityFields = _removeCustomEntityField(
				requestContextContributorKey);

			Map<String, EntityField> entityFieldsMap =
				_contextEntityModel.getEntityFieldsMap();

			entityFieldsMap.put(
				"customContext",
				new ComplexEntityField("customContext", customEntityFields));

			_bundleContext.ungetService(serviceReference);
		}

		private RequestContextContributorServiceTrackerCustomizer(
			BundleContext bundleContext) {

			_bundleContext = bundleContext;
		}

		private List<EntityField> _addCustomEntityField(
			String contextFieldKey, String contextFieldType) {

			EntityField entityField = null;

			if (contextFieldType.equals("boolean")) {
				entityField = new BooleanEntityField(
					contextFieldKey, locale -> contextFieldKey);
			}
			else if (contextFieldType.equals("date")) {
				entityField = new DateTimeEntityField(
					contextFieldKey,
					locale -> Field.getSortableFieldName(contextFieldKey),
					locale -> contextFieldKey);
			}
			else if (contextFieldType.equals("double")) {
				entityField = new DoubleEntityField(
					contextFieldKey, locale -> contextFieldKey);
			}
			else if (contextFieldType.equals("id")) {
				entityField = new IdEntityField(
					contextFieldKey,
					locale -> Field.getSortableFieldName(contextFieldKey),
					locale -> contextFieldKey);
			}
			else if (contextFieldType.equals("integer")) {
				entityField = new IntegerEntityField(
					contextFieldKey, locale -> contextFieldKey);
			}
			else {
				entityField = new StringEntityField(
					contextFieldKey, locale -> contextFieldKey);
			}

			_customEntityFields.put(contextFieldKey, entityField);

			return new ArrayList<>(_customEntityFields.values());
		}

		private List<EntityField> _removeCustomEntityField(
			String requestContextContributorKey) {

			_customEntityFields.remove(requestContextContributorKey);

			return new ArrayList<>(_customEntityFields.values());
		}

		private final BundleContext _bundleContext;
		private final Map<String, EntityField> _customEntityFields =
			new HashMap<>();

	}

}