/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.expando.kernel.util.ExpandoBridgeFactoryUtil;
import com.liferay.expando.kernel.util.ExpandoUtil;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.internal.service.permission.ModelPermissionsImpl;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 */
public class ServiceContextFactory {

	public static ServiceContext getInstance(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		ServiceContext serviceContext = _getInstance(httpServletRequest);

		_ensureValidModelPermissions(serviceContext);

		return serviceContext;
	}

	public static ServiceContext getInstance(PortletRequest portletRequest)
		throws PortalException {

		ServiceContext serviceContext = _getInstance(portletRequest);

		_ensureValidModelPermissions(serviceContext);

		return serviceContext;
	}

	public static ServiceContext getInstance(
			String className, HttpServletRequest httpServletRequest)
		throws PortalException {

		ServiceContext serviceContext = _getInstance(httpServletRequest);

		if ((className == null) || (httpServletRequest == null)) {
			return serviceContext;
		}

		// Permissions

		if (serviceContext.getModelPermissions() == null) {
			serviceContext.setModelPermissions(
				ModelPermissionsFactory.create(httpServletRequest, className));
		}
		else {
			ModelPermissions modelPermissions =
				serviceContext.getModelPermissions();

			modelPermissions.setResourceName(className);
		}

		// Expando

		Map<String, Serializable> expandoBridgeAttributes =
			ExpandoUtil.getExpandoBridgeAttributes(
				ExpandoBridgeFactoryUtil.getExpandoBridge(
					serviceContext.getCompanyId(), className),
				httpServletRequest);

		serviceContext.setExpandoBridgeAttributes(expandoBridgeAttributes);

		return serviceContext;
	}

	public static ServiceContext getInstance(
			String className, PortletRequest portletRequest)
		throws PortalException {

		ServiceContext serviceContext = _getInstance(portletRequest);

		if (className == null) {
			return serviceContext;
		}

		// Permissions

		if (serviceContext.getModelPermissions() == null) {
			serviceContext.setModelPermissions(
				ModelPermissionsFactory.create(portletRequest, className));
		}
		else {
			ModelPermissions modelPermissions =
				serviceContext.getModelPermissions();

			modelPermissions.setResourceName(className);
		}

		// Expando

		Map<String, Serializable> expandoBridgeAttributes =
			ExpandoUtil.getExpandoBridgeAttributes(
				ExpandoBridgeFactoryUtil.getExpandoBridge(
					serviceContext.getCompanyId(), className),
				portletRequest);

		serviceContext.setExpandoBridgeAttributes(expandoBridgeAttributes);

		return serviceContext;
	}

	private static void _ensureValidModelPermissions(
		ServiceContext serviceContext) {

		if (serviceContext.getModelPermissions() == null) {
			serviceContext.setModelPermissions(
				ModelPermissionsFactory.create(
					ModelPermissionsImpl.RESOURCE_NAME_UNINITIALIZED));
		}
	}

	private static ServiceContext _getInstance(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		if (httpServletRequest == null) {
			return serviceContext;
		}

		// Theme display

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay != null) {
			serviceContext.setCompanyId(themeDisplay.getCompanyId());
			serviceContext.setLanguageId(themeDisplay.getLanguageId());

			String layoutURL = PortalUtil.getLayoutURL(themeDisplay);

			String canonicalURL = PortalUtil.getCanonicalURL(
				layoutURL, themeDisplay, themeDisplay.getLayout(), true);

			String fullCanonicalURL = canonicalURL;

			if (!HttpComponentsUtil.hasProtocol(layoutURL)) {
				fullCanonicalURL = PortalUtil.getCanonicalURL(
					PortalUtil.getPortalURL(themeDisplay) + layoutURL,
					themeDisplay, themeDisplay.getLayout(), true);
			}

			serviceContext.setLayoutFullURL(fullCanonicalURL);
			serviceContext.setLayoutURL(canonicalURL);
			serviceContext.setPlid(themeDisplay.getPlid());
			serviceContext.setScopeGroupId(themeDisplay.getScopeGroupId());
			serviceContext.setSignedIn(themeDisplay.isSignedIn());
			serviceContext.setTimeZone(themeDisplay.getTimeZone());
			serviceContext.setUserId(themeDisplay.getUserId());
		}
		else {
			serviceContext.setCompanyId(
				PortalUtil.getCompanyId(httpServletRequest));

			Group guestGroup = GroupLocalServiceUtil.getGroup(
				serviceContext.getCompanyId(), GroupConstants.GUEST);

			serviceContext.setScopeGroupId(guestGroup.getGroupId());

			User user = null;

			try {
				user = PortalUtil.getUser(httpServletRequest);
			}
			catch (NoSuchUserException noSuchUserException) {

				// LPS-24160

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(noSuchUserException);
				}
			}

			if (user != null) {
				serviceContext.setSignedIn(!user.isGuestUser());
				serviceContext.setUserId(user.getUserId());
			}
			else {
				serviceContext.setSignedIn(false);
			}
		}

		serviceContext.setPathFriendlyURLPrivateGroup(
			PortalUtil.getPathFriendlyURLPrivateGroup());
		serviceContext.setPathFriendlyURLPrivateUser(
			PortalUtil.getPathFriendlyURLPrivateUser());
		serviceContext.setPathFriendlyURLPublic(
			PortalUtil.getPathFriendlyURLPublic());
		serviceContext.setPathMain(PortalUtil.getPathMain());
		serviceContext.setPortalURL(
			PortalUtil.getPortalURL(httpServletRequest));

		// Attributes

		Map<String, Serializable> attributes = new HashMap<>();

		Map<String, String[]> parameters = httpServletRequest.getParameterMap();

		for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
			String[] values = entry.getValue();

			if (ArrayUtil.isNotEmpty(values)) {
				String name = entry.getKey();

				if (values.length == 1) {
					attributes.put(name, values[0]);
				}
				else {
					attributes.put(name, values);
				}
			}
		}

		serviceContext.setAttributes(attributes);

		// Command

		serviceContext.setCommand(
			ParamUtil.getString(httpServletRequest, Constants.CMD));

		// Current URL

		serviceContext.setCurrentURL(
			PortalUtil.getCurrentURL(httpServletRequest));

		// Form date

		long formDateLong = ParamUtil.getLong(httpServletRequest, "formDate");

		if (formDateLong > 0) {
			serviceContext.setFormDate(new Date(formDateLong));
		}

		// Permissions

		serviceContext.setModelPermissions(
			ModelPermissionsFactory.create(httpServletRequest));

		// Portlet preferences ids

		String portletId = PortalUtil.getPortletId(httpServletRequest);

		if (Validator.isNotNull(portletId)) {
			serviceContext.setPortletId(portletId);
		}

		// Request

		serviceContext.setRemoteAddr(httpServletRequest.getRemoteAddr());
		serviceContext.setRemoteHost(httpServletRequest.getRemoteHost());
		serviceContext.setRequest(httpServletRequest);

		// Asset

		long[] assetCategoryIds = new long[0];

		Map<String, String[]> parameterMap =
			httpServletRequest.getParameterMap();

		Set<Long> assetCategoryIdsSet = new HashSet<>();

		boolean updateAssetCategoryIds = false;

		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			String name = entry.getKey();

			if (!name.startsWith("assetCategoryIds")) {
				continue;
			}

			updateAssetCategoryIds = true;

			long[] assetVocabularyAssetCategoryIds = ParamUtil.getLongValues(
				httpServletRequest, name);

			for (long assetCategoryId : assetVocabularyAssetCategoryIds) {
				assetCategoryIdsSet.add(assetCategoryId);
			}
		}

		if (updateAssetCategoryIds) {
			assetCategoryIds = ArrayUtil.toArray(
				assetCategoryIdsSet.toArray(new Long[0]));
		}

		serviceContext.setAssetCategoryIds(assetCategoryIds);

		serviceContext.setAssetEntryVisible(
			ParamUtil.getBoolean(
				httpServletRequest, "assetEntryVisible", true));

		String assetLinkEntryIdsString = httpServletRequest.getParameter(
			"assetLinksSearchContainerPrimaryKeys");

		if (assetLinkEntryIdsString != null) {
			serviceContext.setAssetLinkEntryIds(
				StringUtil.split(assetLinkEntryIdsString, 0L));
		}

		serviceContext.setAssetPriority(
			ParamUtil.getDouble(httpServletRequest, "assetPriority"));
		serviceContext.setAssetTagNames(
			ParamUtil.getStringValues(httpServletRequest, "assetTagNames"));

		// Workflow

		serviceContext.setWorkflowAction(
			ParamUtil.getInteger(
				httpServletRequest, "workflowAction",
				WorkflowConstants.ACTION_PUBLISH));

		return serviceContext;
	}

	private static ServiceContext _getInstance(PortletRequest portletRequest)
		throws PortalException {

		// Theme display

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (serviceContext != null) {
			serviceContext = (ServiceContext)serviceContext.clone();
		}
		else {
			serviceContext = new ServiceContext();

			serviceContext.setPathFriendlyURLPrivateGroup(
				PortalUtil.getPathFriendlyURLPrivateGroup());
			serviceContext.setPathFriendlyURLPrivateUser(
				PortalUtil.getPathFriendlyURLPrivateUser());
			serviceContext.setPathFriendlyURLPublic(
				PortalUtil.getPathFriendlyURLPublic());
			serviceContext.setPathMain(PortalUtil.getPathMain());
		}

		serviceContext.setCompanyId(themeDisplay.getCompanyId());
		serviceContext.setLanguageId(themeDisplay.getLanguageId());
		serviceContext.setLayoutFullURL(
			PortalUtil.getLayoutFullURL(themeDisplay));
		serviceContext.setLayoutURL(PortalUtil.getLayoutURL(themeDisplay));
		serviceContext.setPlid(themeDisplay.getPlid());
		serviceContext.setPortalURL(PortalUtil.getPortalURL(portletRequest));
		serviceContext.setScopeGroupId(themeDisplay.getScopeGroupId());
		serviceContext.setSignedIn(themeDisplay.isSignedIn());
		serviceContext.setTimeZone(themeDisplay.getTimeZone());
		serviceContext.setUserId(themeDisplay.getUserId());

		// Attributes

		Map<String, Serializable> attributes = new HashMap<>();

		Map<String, String[]> parameters = portletRequest.getParameterMap();

		for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
			String[] values = entry.getValue();

			if (ArrayUtil.isNotEmpty(values)) {
				String name = entry.getKey();

				if (values.length == 1) {
					attributes.put(name, values[0]);
				}
				else {
					attributes.put(name, values);
				}
			}
		}

		serviceContext.setAttributes(attributes);

		// Command

		serviceContext.setCommand(
			ParamUtil.getString(portletRequest, Constants.CMD));

		// Current URL

		serviceContext.setCurrentURL(PortalUtil.getCurrentURL(portletRequest));

		// Form date

		long formDateLong = ParamUtil.getLong(portletRequest, "formDate");

		if (formDateLong > 0) {
			serviceContext.setFormDate(new Date(formDateLong));
		}

		// Permissions

		serviceContext.setModelPermissions(
			ModelPermissionsFactory.create(portletRequest));

		// Portlet preferences ids

		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(portletRequest);

		String portletId = PortalUtil.getPortletId(portletRequest);

		if (Validator.isNotNull(portletId)) {
			serviceContext.setPortletId(portletId);
		}

		// Request

		serviceContext.setRemoteAddr(httpServletRequest.getRemoteAddr());
		serviceContext.setRemoteHost(httpServletRequest.getRemoteHost());
		serviceContext.setRequest(httpServletRequest);

		// Asset

		long[] assetCategoryIds = new long[0];

		Map<String, String[]> parameterMap = portletRequest.getParameterMap();

		List<Long> assetCategoryIdsList = new ArrayList<>();

		boolean updateAssetCategoryIds = false;

		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			String name = entry.getKey();

			if (!name.startsWith("assetCategoryIds")) {
				continue;
			}

			updateAssetCategoryIds = true;

			long[] assetVocabularyAssetCategoryIds = ParamUtil.getLongValues(
				portletRequest, name);

			for (long assetCategoryId : assetVocabularyAssetCategoryIds) {
				assetCategoryIdsList.add(assetCategoryId);
			}
		}

		if (updateAssetCategoryIds) {
			assetCategoryIds = ArrayUtil.toArray(
				assetCategoryIdsList.toArray(new Long[0]));
		}

		serviceContext.setAssetCategoryIds(assetCategoryIds);

		serviceContext.setAssetEntryVisible(
			ParamUtil.getBoolean(portletRequest, "assetEntryVisible", true));

		String assetLinkEntryIdsString = httpServletRequest.getParameter(
			"assetLinksSearchContainerPrimaryKeys");

		if (assetLinkEntryIdsString != null) {
			serviceContext.setAssetLinkEntryIds(
				StringUtil.split(assetLinkEntryIdsString, 0L));
		}

		serviceContext.setAssetPriority(
			ParamUtil.getDouble(httpServletRequest, "assetPriority"));
		serviceContext.setAssetTagNames(
			ParamUtil.getStringValues(httpServletRequest, "assetTagNames"));

		// Workflow

		serviceContext.setWorkflowAction(
			ParamUtil.getInteger(
				portletRequest, "workflowAction",
				WorkflowConstants.ACTION_PUBLISH));

		return serviceContext;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ServiceContextFactory.class);

}