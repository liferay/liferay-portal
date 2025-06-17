/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSON;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.AuditedModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.PortletPreferencesIds;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

/**
 * Contains context information about a given API call.
 *
 * <p>
 * The <code>ServiceContext</code> object simplifies method signatures and
 * provides a way to consolidate many different methods with different sets of
 * optional parameters into a single, easier to use method. It also aggregates
 * information necessary for transversal features such as permissioning,
 * tagging, categorization, etc.
 * </p>
 *
 * @author Raymond Augé
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 */
@JSON
public class ServiceContext implements Cloneable, Serializable {

	/**
	 * Returns a new service context object identical to this service context
	 * object.
	 *
	 * @return a new service context object
	 */
	@Override
	public Object clone() {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(isAddGroupPermissions());
		serviceContext.setAddGuestPermissions(isAddGuestPermissions());
		serviceContext.setAssetCategoryIds(getAssetCategoryIds());
		serviceContext.setAssetEntryVisible(isAssetEntryVisible());
		serviceContext.setAssetLinkEntryIds(getAssetLinkEntryIds());
		serviceContext.setAssetPriority(getAssetPriority());
		serviceContext.setAssetTagNames(getAssetTagNames());
		serviceContext.setAttributes(getAttributes());
		serviceContext.setCommand(getCommand());
		serviceContext.setCompanyId(getCompanyId());
		serviceContext.setCreateDate(getCreateDate());
		serviceContext.setCurrentURL(getCurrentURL());
		serviceContext.setExpandoBridgeAttributes(getExpandoBridgeAttributes());
		serviceContext.setFailOnPortalException(isFailOnPortalException());

		if (_headers != null) {
			serviceContext.setHeaders(_headers);
		}

		serviceContext.setIndexingEnabled(isIndexingEnabled());
		serviceContext.setLanguageId(getLanguageId());
		serviceContext.setLayoutFullURL(getLayoutFullURL());
		serviceContext.setLayoutURL(getLayoutURL());

		if (_modelPermissions != null) {
			serviceContext.setModelPermissions(_modelPermissions.clone());
		}

		serviceContext.setModifiedDate(getModifiedDate());
		serviceContext.setPathFriendlyURLPrivateGroup(
			getPathFriendlyURLPrivateGroup());
		serviceContext.setPathFriendlyURLPrivateUser(
			getPathFriendlyURLPrivateUser());
		serviceContext.setPathFriendlyURLPublic(getPathFriendlyURLPublic());
		serviceContext.setPathMain(getPathMain());

		if (_plid != null) {
			serviceContext.setPlid(_plid);
		}

		serviceContext.setPortalURL(getPortalURL());
		serviceContext.setPortletPreferencesIds(_portletPreferencesIds);
		serviceContext.setRemoteAddr(getRemoteAddr());
		serviceContext.setRemoteHost(getRemoteHost());
		serviceContext.setRequest(getRequest());
		serviceContext.setScopeGroupId(getScopeGroupId());
		serviceContext.setSignedIn(isSignedIn());
		serviceContext.setStrictAdd(isStrictAdd());

		if (_userDisplayURL != null) {
			serviceContext.setUserDisplayURL(_userDisplayURL);
		}

		serviceContext.setUserId(getUserId());
		serviceContext.setUuid(getUuid());
		serviceContext.setWorkflowAction(getWorkflowAction());

		return serviceContext;
	}

	/**
	 * Derive default permissions based on the logic found in
	 * portal-web/docroot/html/taglib/ui/input_permissions/page.jsp. Do not
	 * update this logic updating the logic in the JSP.
	 */
	public void deriveDefaultPermissions(long repositoryId, String modelName)
		throws PortalException {

		long siteGroupId = PortalUtil.getSiteGroupId(repositoryId);

		Group siteGroup = GroupLocalServiceUtil.getGroup(siteGroupId);

		Role defaultGroupRole = RoleLocalServiceUtil.getDefaultGroupRole(
			siteGroupId);

		List<String> groupPermissionsList = new ArrayList<>();
		List<String> guestPermissionsList = new ArrayList<>();

		String[] roleNames = {RoleConstants.GUEST, defaultGroupRole.getName()};

		List<String> supportedActions =
			ResourceActionsUtil.getModelResourceActions(modelName);
		List<String> groupDefaultActions =
			ResourceActionsUtil.getModelResourceGroupDefaultActions(modelName);
		List<String> guestDefaultActions =
			ResourceActionsUtil.getModelResourceGuestDefaultActions(modelName);
		List<String> guestUnsupportedActions =
			ResourceActionsUtil.getModelResourceGuestUnsupportedActions(
				modelName);

		for (String roleName : roleNames) {
			for (String action : supportedActions) {
				if (roleName.equals(RoleConstants.GUEST) &&
					!guestUnsupportedActions.contains(action) &&
					guestDefaultActions.contains(action) &&
					siteGroup.hasPublicLayouts()) {

					guestPermissionsList.add(action);
				}
				else if (roleName.equals(defaultGroupRole.getName()) &&
						 groupDefaultActions.contains(action)) {

					groupPermissionsList.add(action);
				}
			}
		}

		setModelPermissions(
			ModelPermissionsFactory.create(
				groupPermissionsList.toArray(new String[0]),
				guestPermissionsList.toArray(new String[0]), modelName));
	}

	public User fetchUser() {
		if (_userId == 0) {
			return null;
		}

		return UserLocalServiceUtil.fetchUserById(_userId);
	}

	/**
	 * Returns the asset category IDs to be applied to an asset entry if the
	 * service context is being passed as a parameter to a method which
	 * manipulates the asset entry.
	 *
	 * @return the asset category IDs
	 */
	public long[] getAssetCategoryIds() {
		return _assetCategoryIds;
	}

	/**
	 * Returns the primary keys of the asset entries linked to an asset entry if
	 * the service context is being passed as a parameter to a method which
	 * manipulates the asset entry.
	 *
	 * @return the primary keys of the asset entries
	 */
	public long[] getAssetLinkEntryIds() {
		return _assetLinkEntryIds;
	}

	/**
	 * Returns the priority of an asset entry if this service context is being
	 * passed as a parameter to a method which manipulates the asset entry.
	 *
	 * @return the asset entry's priority
	 */
	public double getAssetPriority() {
		return _assetPriority;
	}

	/**
	 * Returns the asset tag names to be applied to an asset entry if the
	 * service context is being passed as a parameter to a method which
	 * manipulates the asset entry.
	 *
	 * @return the asset tag names
	 */
	public String[] getAssetTagNames() {
		return _assetTagNames;
	}

	/**
	 * Returns the serializable object associated with the name of the standard
	 * parameter of this service context.
	 *
	 * @param  name the name of the standard parameter
	 * @return the serializable object associated with the name
	 */
	public Serializable getAttribute(String name) {
		return _attributes.get(name);
	}

	/**
	 * Returns the map of name/value pairs that are the standard parameters of
	 * this service context. Each value is serializable.
	 *
	 * @return the map of name/value pairs
	 */
	public Map<String, Serializable> getAttributes() {
		return _attributes;
	}

	/**
	 * Returns the value of the {@link Constants#CMD} parameter used in most
	 * Liferay forms for internal portlets.
	 *
	 * @return the value of the command parameter
	 */
	public String getCommand() {
		return _command;
	}

	/**
	 * Returns the company ID of this service context's current portal instance.
	 *
	 * @return the company ID
	 */
	public long getCompanyId() {
		return _companyId;
	}

	/**
	 * Returns the date when an entity was created if this service context is
	 * being passed as a parameter to a method which creates an entity.
	 *
	 * @return the creation date
	 */
	public Date getCreateDate() {
		return _createDate;
	}

	/**
	 * Returns the date when an entity was created (or a default date) if this
	 * service context is being passed as a parameter to a method which creates
	 * an entity.
	 *
	 * @param  defaultCreateDate an optional default create date to use if the
	 *         service context does not have a create date
	 * @return the creation date if available; the default date otherwise
	 */
	public Date getCreateDate(Date defaultCreateDate) {
		if (_createDate != null) {
			return _createDate;
		}
		else if (defaultCreateDate != null) {
			return defaultCreateDate;
		}

		return new Date();
	}

	/**
	 * Returns the current URL of this service context
	 *
	 * @return the current URL
	 */
	public String getCurrentURL() {
		return _currentURL;
	}

	/**
	 * Returns an arbitrary number of attributes of an entity to be persisted.
	 *
	 * <p>
	 * These attributes only include fields that this service context does not
	 * possess by default.
	 * </p>
	 *
	 * @return the expando bridge attributes
	 */
	public Map<String, Serializable> getExpandoBridgeAttributes() {
		return _expandoBridgeAttributes;
	}

	/**
	 * Returns the date when an <code>aui:form</code> was generated in this
	 * service context. The form date can be used in detecting situations in
	 * which an entity has been modified while another client was editing that
	 * entity.
	 *
	 * <p>
	 * Example:
	 * </p>
	 *
	 * <p>
	 * Person1 and person2 start editing the same version of a Web Content
	 * article. Person1 publishes changes to the article first. When person2
	 * attempts to publish changes to that article, the service implementation
	 * finds that a modification to that article has already been published some
	 * time after person2 started editing the article. Since the the article
	 * modification date was found to be later than the form date for person2,
	 * person2 could be alerted to the modification and make a backup copy of
	 * his edits before synchronizing with the published changes by person1.
	 * </p>
	 */
	public Date getFormDate() {
		return _formDate;
	}

	/**
	 * Returns this service context's user ID or guest ID if no user ID is
	 * available.
	 *
	 * @return the user ID, or guest ID if there is no user in this service
	 *         context, or <code>0</code> if there is no company in this service
	 *         context
	 */
	public long getGuestOrUserId() throws PortalException {
		long userId = getUserId();

		if (userId > 0) {
			return userId;
		}

		long companyId = getCompanyId();

		if (companyId > 0) {
			return UserLocalServiceUtil.getGuestUserId(getCompanyId());
		}

		return 0;
	}

	/**
	 * Returns the the map of request header name/value pairs of this service
	 * context.
	 *
	 * @return the the map of request header name/value pairs
	 * @see    HttpHeaders
	 */
	@JSON(include = false)
	public Map<String, String> getHeaders() {
		if ((_headers == null) && (_httpServletRequest != null)) {
			Map<String, String> headerMap = new HashMap<>();

			Enumeration<String> enumeration =
				_httpServletRequest.getHeaderNames();

			while (enumeration.hasMoreElements()) {
				String header = enumeration.nextElement();

				String value = _httpServletRequest.getHeader(header);

				headerMap.put(header, value);
			}

			_headers = headerMap;
		}

		return _headers;
	}

	/**
	 * Returns the language ID of the locale of this service context's current
	 * user.
	 *
	 * @return the language ID
	 */
	public String getLanguageId() {
		if (_languageId != null) {
			return _languageId;
		}

		return LocaleUtil.toLanguageId(LocaleUtil.getMostRelevantLocale());
	}

	/**
	 * Returns the complete URL of the current page if a page context can be
	 * determined for this service context.
	 *
	 * @return the complete URL of the current page
	 */
	public String getLayoutFullURL() {
		return _layoutFullURL;
	}

	/**
	 * Returns the relative URL of the current page if a page context can be
	 * determined for this service context.
	 *
	 * @return the relative URL of the current page
	 */
	public String getLayoutURL() {
		return _layoutURL;
	}

	@JSON(include = false)
	public LiferayPortletRequest getLiferayPortletRequest() {
		if (_httpServletRequest == null) {
			return null;
		}

		PortletRequest portletRequest =
			(PortletRequest)_httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);

		if (portletRequest == null) {
			return null;
		}

		return PortalUtil.getLiferayPortletRequest(portletRequest);
	}

	@JSON(include = false)
	public LiferayPortletResponse getLiferayPortletResponse() {
		if (_httpServletRequest == null) {
			return null;
		}

		PortletResponse portletResponse =
			(PortletResponse)_httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		if (portletResponse == null) {
			return null;
		}

		return PortalUtil.getLiferayPortletResponse(portletResponse);
	}

	public Locale getLocale() {
		return LocaleUtil.fromLanguageId(_languageId);
	}

	public ModelPermissions getModelPermissions() {
		return _modelPermissions;
	}

	/**
	 * Returns the date when an entity was modified if this service context is
	 * being passed as a parameter to a method which updates an entity.
	 *
	 * @return the date when an entity was modified if this service context is
	 *         being passed as a parameter to a method which updates an entity
	 */
	public Date getModifiedDate() {
		return _modifiedDate;
	}

	/**
	 * Returns the date when an entity was modified if this service context is
	 * being passed as a parameter to a method which modifies an entity.
	 *
	 * @param  defaultModifiedDate an optional default modified date to use if
	 *         this service context does not have a modified date
	 * @return the modified date if available; the default date otherwise
	 */
	public Date getModifiedDate(Date defaultModifiedDate) {
		if (_modifiedDate != null) {
			return _modifiedDate;
		}
		else if (defaultModifiedDate != null) {
			return defaultModifiedDate;
		}

		return new Date();
	}

	public String getPathFriendlyURLPrivateGroup() {
		return _pathFriendlyURLPrivateGroup;
	}

	public String getPathFriendlyURLPrivateUser() {
		return _pathFriendlyURLPrivateUser;
	}

	public String getPathFriendlyURLPublic() {
		return _pathFriendlyURLPublic;
	}

	/**
	 * Returns the main context path of the portal, concatenated with
	 * <code>/c</code>.
	 *
	 * @return the main context path of the portal
	 */
	public String getPathMain() {
		return _pathMain;
	}

	/**
	 * Returns the portal layout ID of the current page of this service context.
	 *
	 * @return the portal layout ID of the current page
	 */
	public long getPlid() {
		if (_plid == null) {
			_plid = LayoutLocalServiceUtil.getDefaultPlid(_scopeGroupId, false);
		}

		return _plid;
	}

	/**
	 * Returns the URL of this service context's portal, including the protocol,
	 * domain, and non-default port relative to the company instance and any
	 * virtual host.
	 *
	 * <p>
	 * The URL returned does not include the port if a default port is used.
	 * </p>
	 *
	 * @return the URL of this service context's portal, including the protocol,
	 *         domain, and non-default port relative to company instance and any
	 *         virtual host
	 */
	public String getPortalURL() {
		return _portalURL;
	}

	/**
	 * Returns the ID of the current portlet if this service context is being
	 * passed as a parameter to a portlet.
	 *
	 * @return the ID of the current portlet
	 * @see    PortletPreferencesIds
	 */
	public String getPortletId() {
		if (_portletId != null) {
			return _portletId;
		}

		if (_portletPreferencesIds == null) {
			return null;
		}

		return _portletPreferencesIds.getPortletId();
	}

	/**
	 * Returns the portlet preferences IDs of the current portlet if the service
	 * context is being passed as a parameter to a portlet.
	 *
	 * <p>
	 * The {@link PortletPreferencesIds} can be used to look up portlet
	 * preferences of the current portlet.
	 * </p>
	 *
	 * @return the portlet preferences IDs of the current portlet
	 * @see    PortletPreferencesIds
	 */
	public PortletPreferencesIds getPortletPreferencesIds() {
		if (_portletPreferencesIds == null) {
			if (_portletId == null) {
				return null;
			}

			try {
				_portletPreferencesIds =
					PortletPreferencesFactoryUtil.getPortletPreferencesIds(
						_httpServletRequest, _portletId);
			}
			catch (PortalException portalException) {
				ReflectionUtil.throwException(portalException);
			}
		}

		return _portletPreferencesIds;
	}

	/**
	 * Returns the remote address of the user making the request in this service
	 * context.
	 *
	 * @return the remote address of the user making the request
	 */
	public String getRemoteAddr() {
		return _remoteAddr;
	}

	/**
	 * Returns the remote host name of the user making the request in this
	 * service context.
	 *
	 * @return the remote host name of the user making the request
	 */
	public String getRemoteHost() {
		return _remoteHost;
	}

	@JSON(include = false)
	public HttpServletRequest getRequest() {
		return _httpServletRequest;
	}

	@JSON(include = false)
	public HttpServletResponse getResponse() {
		LiferayPortletResponse liferayPortletResponse =
			getLiferayPortletResponse();

		if (liferayPortletResponse == null) {
			return null;
		}

		return PortalUtil.getHttpServletResponse(liferayPortletResponse);
	}

	public String getRootPortletId() {
		String portletId = getPortletId();

		if (portletId == null) {
			return null;
		}

		return PortletIdCodec.decodePortletName(portletId);
	}

	public Group getScopeGroup() throws PortalException {
		return GroupLocalServiceUtil.getGroup(_scopeGroupId);
	}

	/**
	 * Returns the ID of the group corresponding to the current data scope of
	 * this service context.
	 *
	 * @return the ID of the group corresponding to the current data scope
	 * @see    Group
	 */
	public long getScopeGroupId() {
		return _scopeGroupId;
	}

	public ThemeDisplay getThemeDisplay() {
		if (_httpServletRequest == null) {
			return null;
		}

		return (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public TimeZone getTimeZone() {
		return _timeZone;
	}

	/**
	 * Returns the user-agent request header of this service context.
	 *
	 * @return the user-agent request header
	 * @see    HttpHeaders
	 */
	public String getUserAgent() {
		if (_httpServletRequest == null) {
			return null;
		}

		return _httpServletRequest.getHeader(HttpHeaders.USER_AGENT);
	}

	/**
	 * Returns the complete URL of this service context's current user's profile
	 * page.
	 *
	 * @return the complete URL of this service context's current user's profile
	 *         page
	 */
	public String getUserDisplayURL() {
		if (_userDisplayURL == null) {
			ThemeDisplay themeDisplay = getThemeDisplay();

			if (themeDisplay == null) {
				return null;
			}

			User user = themeDisplay.getUser();

			try {
				_userDisplayURL = user.getDisplayURL(themeDisplay);
			}
			catch (PortalException portalException) {
				ReflectionUtil.throwException(portalException);
			}
		}

		return _userDisplayURL;
	}

	/**
	 * Returns the ID of this service context's current user.
	 *
	 * @return the ID of this service context's current user
	 */
	public long getUserId() {
		return _userId;
	}

	/**
	 * Returns the UUID of this service context's current entity.
	 *
	 * <p>
	 * To ensure the same UUID is never used by two entities, the UUID is reset
	 * to <code>null</code> upon invoking this method.
	 * </p>
	 *
	 * @return the UUID of this service context's current entity
	 */
	public String getUuid() {
		String uuid = _uuid;

		_uuid = null;

		return uuid;
	}

	public String getUuidWithoutReset() {
		return _uuid;
	}

	/**
	 * Returns the workflow action to take if this service context is being
	 * passed as a parameter to a method that processes a workflow action.
	 *
	 * @return the workflow action to take
	 */
	public int getWorkflowAction() {
		return _workflowAction;
	}

	/**
	 * Returns <code>true</code> if this service context is being passed as a
	 * parameter to a method which manipulates a resource to which default group
	 * permissions apply.
	 *
	 * @return <code>true</code> if this service context is being passed as a
	 *         parameter to a method which manipulates a resource to which
	 *         default group permissions apply; <code>false</code> otherwise
	 */
	public boolean isAddGroupPermissions() {
		return _addGroupPermissions;
	}

	/**
	 * Returns <code>true</code> if this service context is being passed as a
	 * parameter to a method which manipulates a resource to which default guest
	 * permissions apply.
	 *
	 * @return <code>true</code> if this service context is being passed as a
	 *         parameter to a method which manipulates a resource to which
	 *         default guest permissions apply; <code>false</code> otherwise
	 */
	public boolean isAddGuestPermissions() {
		return _addGuestPermissions;
	}

	public boolean isAssetEntryVisible() {
		return _assetEntryVisible;
	}

	/**
	 * Returns <code>true</code> if this service context contains an add command
	 * (i.e. has command value {@link Constants#ADD})
	 *
	 * @return <code>true</code> if this service context contains an add
	 *         command; <code>false</code> otherwise
	 */
	public boolean isCommandAdd() {
		if (Objects.equals(_command, Constants.ADD) ||
			Objects.equals(_command, Constants.ADD_DYNAMIC) ||
			Objects.equals(_command, Constants.ADD_MULTIPLE) ||
			Objects.equals(_command, Constants.ADD_WEBDAV)) {

			return true;
		}

		return false;
	}

	/**
	 * Returns <code>true</code> if this service context contains an update
	 * command (i.e. has command value {@link Constants#UPDATE})
	 *
	 * @return <code>true</code> if this service context contains an update
	 *         command; <code>false</code> otherwise
	 */
	public boolean isCommandUpdate() {
		if (Objects.equals(_command, Constants.UPDATE) ||
			Objects.equals(_command, Constants.UPDATE_AND_CHECKIN) ||
			Objects.equals(_command, Constants.UPDATE_WEBDAV)) {

			return true;
		}

		return false;
	}

	public boolean isDeriveDefaultPermissions() {
		return _deriveDefaultPermissions;
	}

	/**
	 * Returns <code>true</code> if portal exceptions should be handled as
	 * failures, possibly halting processing, or <code>false</code> if the
	 * exceptions should be handled differently, possibly allowing processing to
	 * continue in some manner. Services may check this flag to execute desired
	 * behavior.
	 *
	 * <p>
	 * Batch invocation of such services (exposed as a JSON web services) can
	 * result in execution of all service invocations, in spite of portal
	 * exceptions.
	 * </p>
	 *
	 * <p>
	 * If this flag is set to <code>false</code>, services can implement logic
	 * that allows processing to continue, while collecting information
	 * regarding the exceptions for returning to the caller. For example, the
	 * {@link
	 * com.liferay.portlet.asset.service.impl.AssetVocabularyServiceImpl#deleteVocabularies(
	 * long[], ServiceContext)} method uses the list it returns to give
	 * information on vocabularies it fails to delete; it returns an empty list
	 * if all deletions are successful.
	 * </p>
	 *
	 * @return <code>true</code> if portal exceptions are to be handled as
	 *         failures; <code>false</code> if portal exceptions can be handled
	 *         differently, possibly allowing processing to continue in some
	 *         manner
	 */
	public boolean isFailOnPortalException() {
		return _failOnPortalException;
	}

	/**
	 * Returns whether the primary entity of this service context is to be
	 * indexed/re-indexed.
	 *
	 * @return <code>true</code> the primary entity of this service context is
	 *         to be indexed/re-indexed; <code>false</code> otherwise
	 */
	public boolean isIndexingEnabled() {
		return _indexingEnabled;
	}

	/**
	 * Returns <code>true</code> if the sender of this service context's request
	 * is signed in.
	 *
	 * @return <code>true</code> if the sender of this service context's request
	 *         is signed in; <code>false</code> otherwise
	 */
	public boolean isSignedIn() {
		return _signedIn;
	}

	public boolean isStrictAdd() {
		return _strictAdd;
	}

	/**
	 * Merges all of the specified service context's non-<code>null</code>
	 * attributes, attributes greater than <code>0</code>, and fields (except
	 * the request) with this service context object.
	 *
	 * @param serviceContext the service context object to be merged
	 */
	public void merge(ServiceContext serviceContext) {
		setAddGroupPermissions(serviceContext.isAddGroupPermissions());
		setAddGuestPermissions(serviceContext.isAddGuestPermissions());

		if (serviceContext.getAssetCategoryIds() != null) {
			setAssetCategoryIds(serviceContext.getAssetCategoryIds());
		}

		setAssetEntryVisible(serviceContext.isAssetEntryVisible());

		if (serviceContext.getAssetLinkEntryIds() != null) {
			setAssetLinkEntryIds(serviceContext.getAssetLinkEntryIds());
		}

		if (serviceContext.getAssetPriority() > 0) {
			setAssetPriority(serviceContext.getAssetPriority());
		}

		if (serviceContext.getAssetTagNames() != null) {
			setAssetTagNames(serviceContext.getAssetTagNames());
		}

		if (serviceContext.getAttributes() != null) {
			setAttributes(serviceContext.getAttributes());
		}

		if (Validator.isNotNull(serviceContext.getCommand())) {
			setCommand(serviceContext.getCommand());
		}

		if (serviceContext.getCompanyId() > 0) {
			setCompanyId(serviceContext.getCompanyId());
		}

		if (serviceContext.getCreateDate() != null) {
			setCreateDate(serviceContext.getCreateDate());
		}

		if (Validator.isNotNull(serviceContext.getCurrentURL())) {
			setCurrentURL(serviceContext.getCurrentURL());
		}

		setDeriveDefaultPermissions(
			serviceContext.isDeriveDefaultPermissions());

		if (serviceContext.getExpandoBridgeAttributes() != null) {
			setExpandoBridgeAttributes(
				serviceContext.getExpandoBridgeAttributes());
		}

		setFailOnPortalException(serviceContext.isFailOnPortalException());

		if (serviceContext._headers != null) {
			setHeaders(serviceContext._headers);
		}

		setIndexingEnabled(serviceContext.isIndexingEnabled());
		setLanguageId(serviceContext.getLanguageId());

		if (Validator.isNotNull(serviceContext.getLayoutFullURL())) {
			setLayoutFullURL(serviceContext.getLayoutFullURL());
		}

		if (Validator.isNotNull(serviceContext.getLayoutURL())) {
			setLayoutURL(serviceContext.getLayoutURL());
		}

		if (serviceContext.getModelPermissions() != null) {
			setModelPermissions(serviceContext.getModelPermissions());
		}

		if (serviceContext.getModifiedDate() != null) {
			setModifiedDate(serviceContext.getModifiedDate());
		}

		if (Validator.isNotNull(
				serviceContext.getPathFriendlyURLPrivateGroup())) {

			setPathFriendlyURLPrivateGroup(
				serviceContext.getPathFriendlyURLPrivateGroup());
		}

		if (Validator.isNotNull(
				serviceContext.getPathFriendlyURLPrivateUser())) {

			setPathFriendlyURLPrivateUser(
				serviceContext.getPathFriendlyURLPrivateUser());
		}

		if (Validator.isNotNull(serviceContext.getPathFriendlyURLPublic())) {
			setPathFriendlyURLPublic(serviceContext.getPathFriendlyURLPublic());
		}

		if (Validator.isNotNull(serviceContext.getPathMain())) {
			setPathMain(serviceContext.getPathMain());
		}

		if (serviceContext.getPlid() > 0) {
			setPlid(serviceContext.getPlid());
		}

		if (Validator.isNotNull(serviceContext.getPortalURL())) {
			setPortalURL(serviceContext.getPortalURL());
		}

		if (serviceContext.getPortletPreferencesIds() != null) {
			setPortletPreferencesIds(serviceContext.getPortletPreferencesIds());
		}

		if (Validator.isNotNull(serviceContext.getRemoteAddr())) {
			setRemoteAddr(serviceContext.getRemoteAddr());
		}

		if (Validator.isNotNull(serviceContext.getRemoteHost())) {
			setRemoteHost(serviceContext.getRemoteHost());
		}

		if (serviceContext.getScopeGroupId() > 0) {
			setScopeGroupId(serviceContext.getScopeGroupId());
		}

		setSignedIn(serviceContext.isSignedIn());

		if (serviceContext.getTimeZone() != null) {
			setTimeZone(serviceContext.getTimeZone());
		}

		if (Validator.isNotNull(serviceContext._userDisplayURL)) {
			setUserDisplayURL(serviceContext._userDisplayURL);
		}

		if (serviceContext.getUserId() > 0) {
			setUserId(serviceContext.getUserId());
		}

		// Refrence serviceContext#_uuid directly because calling
		// serviceContext#getUuid() would set it to null

		if (Validator.isNotNull(serviceContext._uuid)) {
			setUuid(serviceContext._uuid);
		}

		if (serviceContext.getWorkflowAction() > 0) {
			setWorkflowAction(serviceContext.getWorkflowAction());
		}
	}

	/**
	 * Removes the mapping of the serializable object to the name of the
	 * standard parameter of this service context.
	 *
	 * @param  name the name of the standard parameter
	 * @return the serializable object associated to the name
	 */
	public Serializable removeAttribute(String name) {
		return _attributes.remove(name);
	}

	/**
	 * Sets whether or not default group permissions should apply to a resource
	 * being manipulated by a method to which this service context is passed as
	 * a parameter.
	 *
	 * @param addGroupPermissions indicates whether or not to apply default
	 *        group permissions
	 */
	public void setAddGroupPermissions(boolean addGroupPermissions) {
		_addGroupPermissions = addGroupPermissions;
	}

	/**
	 * Sets whether or not default guest permissions should apply to a resource
	 * being manipulated by a method to which this service context is passed as
	 * a parameter.
	 *
	 * @param addGuestPermissions indicates whether or not to apply default
	 *        guest permissions
	 */
	public void setAddGuestPermissions(boolean addGuestPermissions) {
		_addGuestPermissions = addGuestPermissions;
	}

	/**
	 * Sets an array of asset category IDs to be applied to an asset entry if
	 * this service context is being passed as a parameter to a method which
	 * manipulates the asset entry.
	 *
	 * @param assetCategoryIds the primary keys of the asset categories
	 */
	public void setAssetCategoryIds(long[] assetCategoryIds) {
		_assetCategoryIds = assetCategoryIds;
	}

	public void setAssetEntryVisible(boolean assetEntryVisible) {
		_assetEntryVisible = assetEntryVisible;
	}

	/**
	 * Sets an array of the primary keys of asset entries to be linked to an
	 * asset entry if this service context is being passed as a parameter to a
	 * method which manipulates the asset entry.
	 *
	 * @param assetLinkEntryIds the primary keys of the asset entries to be
	 *        linked to an asset entry
	 */
	public void setAssetLinkEntryIds(long[] assetLinkEntryIds) {
		_assetLinkEntryIds = assetLinkEntryIds;
	}

	/**
	 * Sets the priority of an asset entry if this service context is being
	 * passed as a parameter to a method which manipulates the asset entry.
	 *
	 * @param assetPriority the priority of an asset entry
	 */
	public void setAssetPriority(double assetPriority) {
		_assetPriority = assetPriority;
	}

	/**
	 * Sets an array of asset tag names to be applied to an asset entry if this
	 * service context is being passed as a parameter to a method which
	 * manipulates the asset entry.
	 *
	 * @param assetTagNames the tag names to be applied to an asset entry
	 */
	public void setAssetTagNames(String[] assetTagNames) {
		_assetTagNames = assetTagNames;
	}

	/**
	 * Sets a mapping of a standard parameter's name to its serializable object.
	 *
	 * @param name the standard parameter name to associate with the value
	 * @param value the serializable object to be associated with the name
	 */
	public void setAttribute(String name, Serializable value) {
		_attributes.put(name, value);
	}

	/**
	 * Sets the map of the name/value pairs that are the standard parameters of
	 * this service context. Each value must be serializable.
	 *
	 * @param attributes the map of the name/value pairs that are the standard
	 *        parameters of this service context
	 */
	public void setAttributes(Map<String, Serializable> attributes) {
		_attributes = attributes;
	}

	/**
	 * Sets the value of the {@link Constants#CMD} parameter used in most
	 * Liferay forms for internal portlets.
	 *
	 * @param command the value of the {@link Constants#CMD} parameter
	 */
	public void setCommand(String command) {
		_command = command;
	}

	/**
	 * Sets the company ID of this service context's current portal instance.
	 *
	 * @param companyId the primary key of this service context's current portal
	 *        instance
	 */
	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	/**
	 * Sets the date when an entity was created if this service context is being
	 * passed as a parameter to a method which creates an entity.
	 *
	 * @param createDate the date the entity was created
	 */
	public void setCreateDate(Date createDate) {
		_createDate = createDate;
	}

	/**
	 * Sets the current URL of this service context
	 *
	 * @param currentURL the current URL of this service context
	 */
	public void setCurrentURL(String currentURL) {
		_currentURL = currentURL;
	}

	public void setDeriveDefaultPermissions(boolean deriveDefaultPermissions) {
		_deriveDefaultPermissions = deriveDefaultPermissions;
	}

	/**
	 * Sets an arbitrary number of attributes of an entity to be persisted.
	 *
	 * <p>
	 * These attributes should only include fields that {@link ServiceContext}
	 * does not possess by default.
	 * </p>
	 *
	 * @param expandoBridgeAttributes the expando bridge attributes (optionally
	 *        <code>null</code>)
	 */
	public void setExpandoBridgeAttributes(
		Map<String, Serializable> expandoBridgeAttributes) {

		_expandoBridgeAttributes = expandoBridgeAttributes;
	}

	/**
	 * Sets whether portal exceptions should be handled as failures, possibly
	 * halting processing, or if exceptions should be handled differently,
	 * possibly allowing processing to continue in some manner.
	 *
	 * @param failOnPortalException whether portal exceptions should be handled
	 *        as failures, or if portal exceptions should be handled
	 *        differently, possibly allowing processing to continue in some
	 *        manner
	 * @see   #isFailOnPortalException()
	 */
	public void setFailOnPortalException(boolean failOnPortalException) {
		_failOnPortalException = failOnPortalException;
	}

	/**
	 * Sets the date when an <code>aui:form</code> was generated in this service
	 * context. The form date can be used in detecting situations in which an
	 * entity has been modified while another client was editing that entity.
	 *
	 * <p>
	 * Example:
	 * </p>
	 *
	 * <p>
	 * Person1 and person2 start editing the same version of a Web Content
	 * article. Person1 publishes changes to the article first. When person2
	 * attempts to publish changes to that article, the service implementation
	 * finds that a modification to that article has already been published some
	 * time after person2 started editing the article. Since the article
	 * modification date was found to be later than the form date for person2,
	 * person2 could be alerted to the modification and make a backup copy of
	 * his edits before synchronizing with the published changes by person1.
	 * </p>
	 *
	 * @param formDate the date that an <code>aui:form</code> was generated for
	 *        this service context (optionally <code>null</code>)
	 */
	public void setFormDate(Date formDate) {
		_formDate = formDate;
	}

	/**
	 * Sets the map of request header name/value pairs of this service context.
	 *
	 * @param headers map of request header name/value pairs of this service
	 *        context
	 * @see   HttpHeaders
	 */
	public void setHeaders(Map<String, String> headers) {
		_headers = headers;
	}

	/**
	 * Sets whether the primary entity of this service context is to be
	 * indexed/re-indexed.
	 *
	 * <p>
	 * The entity is only indexed/re-indexed if the method receiving this
	 * service context as a parameter does indexing.
	 * </p>
	 *
	 * @param indexingEnabled whether the primary entity of this service context
	 *        is to be indexed/re-indexed (default is <code>true</code>)
	 */
	public void setIndexingEnabled(boolean indexingEnabled) {
		_indexingEnabled = indexingEnabled;
	}

	/**
	 * Sets the language ID of the locale of this service context.
	 *
	 * @param languageId the language ID of the locale of this service context's
	 *        current user
	 */
	public void setLanguageId(String languageId) {
		_languageId = languageId;
	}

	/**
	 * Sets the complete URL of the current page for this service context.
	 *
	 * @param layoutFullURL the complete URL of the current page if a page
	 *        context can be determined for this service context
	 */
	public void setLayoutFullURL(String layoutFullURL) {
		_layoutFullURL = layoutFullURL;
	}

	/**
	 * Sets the relative URL of the current page for this service context.
	 *
	 * @param layoutURL the relative URL of the current page if a page context
	 *        can be determined for this service context
	 */
	public void setLayoutURL(String layoutURL) {
		_layoutURL = layoutURL;
	}

	public void setModelPermissions(ModelPermissions modelPermissions) {
		_modelPermissions = modelPermissions;
	}

	/**
	 * Sets the date when an entity was modified in this service context.
	 *
	 * @param modifiedDate the date when an entity was modified in this service
	 *        context
	 */
	public void setModifiedDate(Date modifiedDate) {
		_modifiedDate = modifiedDate;
	}

	public void setPathFriendlyURLPrivateGroup(
		String pathFriendlyURLPrivateGroup) {

		_pathFriendlyURLPrivateGroup = pathFriendlyURLPrivateGroup;
	}

	public void setPathFriendlyURLPrivateUser(
		String pathFriendlyURLPrivateUser) {

		_pathFriendlyURLPrivateUser = pathFriendlyURLPrivateUser;
	}

	public void setPathFriendlyURLPublic(String pathFriendlyURLPublic) {
		_pathFriendlyURLPublic = pathFriendlyURLPublic;
	}

	/**
	 * Sets the main context path of the portal, concatenated with
	 * <code>/c</code>.
	 *
	 * @param pathMain the main context path of the portal
	 */
	public void setPathMain(String pathMain) {
		_pathMain = pathMain;
	}

	/**
	 * Sets the portal layout ID of the current page in this service context.
	 *
	 * @param plid the portal layout ID of the current page
	 */
	public void setPlid(long plid) {
		_plid = plid;
	}

	/**
	 * Sets the URL of this service context's portal, including the protocol,
	 * domain, and non-default port relative to the company instance and any
	 * virtual host.
	 *
	 * <p>
	 * The URL should not include the port if a default port is used.
	 * </p>
	 *
	 * @param portalURL the portal URL
	 */
	public void setPortalURL(String portalURL) {
		_portalURL = portalURL;
	}

	public void setPortletId(String portletId) {
		_portletId = portletId;
	}

	/**
	 * Sets the portlet preferences IDs of the current portlet if this service
	 * context is being passed as a parameter to a portlet.
	 *
	 * <p>
	 * The {@link PortletPreferencesIds} can be used to look up portlet
	 * preferences of the current portlet.
	 * </p>
	 *
	 * @param portletPreferencesIds the portlet preferences
	 * @see   PortletPreferencesIds
	 */
	public void setPortletPreferencesIds(
		PortletPreferencesIds portletPreferencesIds) {

		_portletPreferencesIds = portletPreferencesIds;
	}

	/**
	 * Sets the remote address of the user making the request in this service
	 * context.
	 *
	 * @param remoteAddr the remote address of the user making the request in
	 *        this service context
	 */
	public void setRemoteAddr(String remoteAddr) {
		_remoteAddr = remoteAddr;
	}

	/**
	 * Sets the remote host name of the user making the request in this service
	 * context.
	 *
	 * @param remoteHost the remote host name of the user making the request in
	 *        this service context
	 */
	public void setRemoteHost(String remoteHost) {
		_remoteHost = remoteHost;
	}

	/**
	 * Sets the optional request used when instantiating this service context.
	 * The field is volatile and so will be discarded on serialization.
	 *
	 * @param httpServletRequest the request
	 */
	public void setRequest(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;
	}

	/**
	 * Sets the ID of the group corresponding to the current data scope of this
	 * service context.
	 *
	 * @param scopeGroupId the ID of the group corresponding to the current data
	 *        scope of this service context
	 * @see   Group
	 */
	public void setScopeGroupId(long scopeGroupId) {
		_scopeGroupId = scopeGroupId;
	}

	/**
	 * Sets whether the sender of this service context's request is signed in.
	 *
	 * @param signedIn whether the sender of this service context's request is
	 *        signed in
	 */
	public void setSignedIn(boolean signedIn) {
		_signedIn = signedIn;
	}

	public void setStrictAdd(boolean strictAdd) {
		_strictAdd = strictAdd;
	}

	public void setTimeZone(TimeZone timeZone) {
		_timeZone = timeZone;
	}

	/**
	 * Sets the complete URL of this service context's current user's profile
	 * page.
	 *
	 * @param userDisplayURL the complete URL of the current user's profile page
	 */
	public void setUserDisplayURL(String userDisplayURL) {
		_userDisplayURL = userDisplayURL;
	}

	/**
	 * Sets the ID of this service context's current user.
	 *
	 * @param userId the ID of the current user
	 */
	public void setUserId(long userId) {
		_userId = userId;
	}

	/**
	 * Sets the UUID of this service context's current entity.
	 *
	 * @param uuid the UUID of the current entity
	 */
	public void setUuid(String uuid) {
		_uuid = uuid;
	}

	/**
	 * Sets the workflow action to take if this service context is being passed
	 * as parameter to a method that processes a workflow action.
	 *
	 * @param workflowAction workflow action to take (default is {@link
	 *        WorkflowConstants#ACTION_PUBLISH})
	 */
	public void setWorkflowAction(int workflowAction) {
		_workflowAction = workflowAction;
	}

	public String translate(String pattern, Object... arguments) {
		return LanguageUtil.format(getLocale(), pattern, arguments);
	}

	public void validateModifiedDate(
			AuditedModel auditedModel, Class<? extends PortalException> clazz)
		throws PortalException {

		int value = DateUtil.compareTo(
			auditedModel.getModifiedDate(), _formDate);

		if (value > 0) {
			try {
				throw clazz.newInstance();
			}
			catch (IllegalAccessException illegalAccessException) {
				throw new RuntimeException(illegalAccessException);
			}
			catch (InstantiationException instantiationException) {
				throw new RuntimeException(instantiationException);
			}
		}
	}

	private boolean _addGroupPermissions;
	private boolean _addGuestPermissions;
	private long[] _assetCategoryIds;
	private boolean _assetEntryVisible = true;
	private long[] _assetLinkEntryIds;
	private double _assetPriority;
	private String[] _assetTagNames;
	private Map<String, Serializable> _attributes = new LinkedHashMap<>();
	private String _command;
	private long _companyId;
	private Date _createDate;
	private String _currentURL;
	private boolean _deriveDefaultPermissions;
	private Map<String, Serializable> _expandoBridgeAttributes =
		new LinkedHashMap<>();
	private boolean _failOnPortalException = true;
	private Date _formDate;
	private transient Map<String, String> _headers;
	private transient HttpServletRequest _httpServletRequest;
	private boolean _indexingEnabled = true;
	private String _languageId;
	private String _layoutFullURL;
	private String _layoutURL;
	private ModelPermissions _modelPermissions;
	private Date _modifiedDate;
	private String _pathFriendlyURLPrivateGroup;
	private String _pathFriendlyURLPrivateUser;
	private String _pathFriendlyURLPublic;
	private String _pathMain;
	private Long _plid;
	private String _portalURL;
	private String _portletId;
	private PortletPreferencesIds _portletPreferencesIds;
	private String _remoteAddr;
	private String _remoteHost;
	private long _scopeGroupId;
	private boolean _signedIn;
	private boolean _strictAdd;
	private TimeZone _timeZone;
	private String _userDisplayURL;
	private long _userId;
	private String _uuid;
	private int _workflowAction = WorkflowConstants.ACTION_PUBLISH;

}