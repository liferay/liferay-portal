/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.util;

import com.liferay.blogs.kernel.model.BlogsEntry;
import com.liferay.document.library.kernel.exception.ImageSizeException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.expando.kernel.exception.ValueDataException;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.message.boards.kernel.model.MBMessage;
import com.liferay.message.boards.kernel.model.MBThread;
import com.liferay.petra.encryptor.Encryptor;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.comment.action.EditDiscussionStrutsAction;
import com.liferay.portal.comment.action.GetCommentsStrutsAction;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.cache.thread.local.Lifecycle;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCache;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCacheManager;
import com.liferay.portal.kernel.cluster.ClusterExecutorUtil;
import com.liferay.portal.kernel.cluster.ClusterInvokeThreadLocal;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.ImageTypeException;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.NoSuchImageException;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.RSSFeedException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.image.ImageBag;
import com.liferay.portal.kernel.image.ImageToolUtil;
import com.liferay.portal.kernel.language.LanguageConstants;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.AuditedModel;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.ColorScheme;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutFriendlyURL;
import com.liferay.portal.kernel.model.LayoutFriendlyURLComposite;
import com.liferay.portal.kernel.model.LayoutQueryStringComposite;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutType;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PublicRenderParameter;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.TicketConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.model.VirtualLayoutConstants;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.portlet.FriendlyURLMapper;
import com.liferay.portal.kernel.portlet.FriendlyURLMapperThreadLocal;
import com.liferay.portal.kernel.portlet.FriendlyURLResolver;
import com.liferay.portal.kernel.portlet.FriendlyURLResolverRegistryUtil;
import com.liferay.portal.kernel.portlet.InvokerPortlet;
import com.liferay.portal.kernel.portlet.LayoutFriendlyURLSeparatorComposite;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.LiferayPortletMode;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletBag;
import com.liferay.portal.kernel.portlet.PortletBagPool;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletInstanceFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletQNameUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.UserAttributes;
import com.liferay.portal.kernel.security.auth.AlwaysAllowDoAsUser;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.FullNameGenerator;
import com.liferay.portal.kernel.security.auth.FullNameGeneratorFactory;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.http.HttpAuthManagerUtil;
import com.liferay.portal.kernel.security.pacl.DoPrivileged;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ImageLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutFriendlyURLLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourceLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.TicketLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.UserServiceUtil;
import com.liferay.portal.kernel.service.VirtualHostLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil;
import com.liferay.portal.kernel.servlet.BrowserSnifferUtil;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.servlet.HttpSessionWrapper;
import com.liferay.portal.kernel.servlet.NonSerializableObjectRequestWrapper;
import com.liferay.portal.kernel.servlet.PersistentHttpServletRequestWrapper;
import com.liferay.portal.kernel.servlet.PortalMessages;
import com.liferay.portal.kernel.servlet.PortalSessionContext;
import com.liferay.portal.kernel.servlet.PortalSessionThreadLocal;
import com.liferay.portal.kernel.servlet.PortalWebResourcesUtil;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.servlet.ServletContextUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.filters.compoundsessionid.CompoundSessionIdSplitterUtil;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.upload.UploadServletRequest;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.CookieKeys;
import com.liferay.portal.kernel.util.DeterminateKeyGenerator;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.InheritableMap;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListMergeable;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalInetSocketAddressEventListener;
import com.liferay.portal.kernel.util.PortalPortEventListener;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletCategoryKeys;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.ReflectionUtil;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringComparator;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.xml.QName;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.model.impl.CookieRemotePreference;
import com.liferay.portal.model.impl.LayoutTypeImpl;
import com.liferay.portal.model.impl.LayoutTypePortletImpl;
import com.liferay.portal.plugin.PluginPackageUtil;
import com.liferay.portal.security.jaas.JAASHelper;
import com.liferay.portal.security.lang.DoPrivilegedUtil;
import com.liferay.portal.security.sso.SSOUtil;
import com.liferay.portal.servlet.filters.i18n.I18nFilter;
import com.liferay.portal.spring.context.PortalContextLoaderListener;
import com.liferay.portal.struts.StrutsUtil;
import com.liferay.portal.upload.UploadPortletRequestImpl;
import com.liferay.portal.upload.UploadServletRequestImpl;
import com.liferay.portal.webserver.WebServerServlet;
import com.liferay.portlet.PortletPreferencesImpl;
import com.liferay.portlet.PortletPreferencesWrapper;
import com.liferay.portlet.PortletRequestImpl;
import com.liferay.portlet.PortletResponseImpl;
import com.liferay.portlet.RenderRequestImpl;
import com.liferay.portlet.RenderResponseImpl;
import com.liferay.portlet.StateAwareResponseImpl;
import com.liferay.portlet.admin.util.OmniadminUtil;
import com.liferay.portlet.social.util.FacebookUtil;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.registry.ServiceReference;
import com.liferay.registry.ServiceTracker;
import com.liferay.registry.ServiceTrackerCustomizer;
import com.liferay.sites.kernel.util.Sites;
import com.liferay.sites.kernel.util.SitesUtil;
import com.liferay.social.kernel.model.SocialRelationConstants;
import com.liferay.util.JS;

import java.awt.image.RenderedImage;

import java.io.IOException;
import java.io.Serializable;

import java.lang.reflect.Method;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.PreferencesValidator;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.StateAwareResponse;
import javax.portlet.ValidatorException;
import javax.portlet.WindowState;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;

/**
 * @author Brian Wing Shun Chan
 * @author Brian Myunghun Kim
 * @author Jorge Ferrer
 * @author Raymond Augé
 * @author Eduardo Lundgren
 * @author Wesley Gong
 * @author Hugo Huijser
 * @author Juan Fernández
 * @author Marco Leo
 * @author Neil Griffin
 */
@DoPrivileged
public class PortalImpl implements Portal {

	public PortalImpl() {

		// Computer name

		String computerName = System.getProperty("env.COMPUTERNAME");

		if (Validator.isNull(computerName)) {
			computerName = System.getProperty("env.HOST");
		}

		if (Validator.isNull(computerName)) {
			computerName = System.getProperty("env.HOSTNAME");
		}

		if (Validator.isNull(computerName)) {
			try {
				InetAddress inetAddress = InetAddress.getLocalHost();

				computerName = inetAddress.getHostName();
			}
			catch (UnknownHostException uhe) {
			}
		}

		_computerName = computerName;

		try {
			List<NetworkInterface> networkInterfaces = Collections.list(
				NetworkInterface.getNetworkInterfaces());

			for (NetworkInterface networkInterface : networkInterfaces) {
				List<InetAddress> inetAddresses = Collections.list(
					networkInterface.getInetAddresses());

				for (InetAddress inetAddress : inetAddresses) {
					if (inetAddress instanceof Inet4Address) {
						_computerAddresses.add(inetAddress.getHostAddress());
					}
				}
			}
		}
		catch (Exception e) {
			_log.error("Unable to determine server's IP addresses");

			_log.error(e, e);
		}

		// Paths

		_pathProxy = PropsValues.PORTAL_PROXY_PATH;

		_pathContext = _pathProxy.concat(
			getContextPath(
				PortalContextLoaderListener.getPortalServletContextPath()));

		_pathFriendlyURLPrivateGroup =
			_pathContext + _PRIVATE_GROUP_SERVLET_MAPPING;
		_pathFriendlyURLPrivateUser =
			_pathContext + _PRIVATE_USER_SERVLET_MAPPING;
		_pathFriendlyURLPublic = _pathContext + _PUBLIC_GROUP_SERVLET_MAPPING;
		_pathImage = _pathContext + PATH_IMAGE;
		_pathMain = _pathContext + PATH_MAIN;
		_pathModule = _pathContext + PATH_MODULE;

		// Groups

		String[] customSystemGroups = PropsUtil.getArray(
			PropsKeys.SYSTEM_GROUPS);

		if (ArrayUtil.isEmpty(customSystemGroups)) {
			_allSystemGroups = GroupConstants.SYSTEM_GROUPS;
		}
		else {
			_allSystemGroups = ArrayUtil.append(
				GroupConstants.SYSTEM_GROUPS, customSystemGroups);
		}

		_sortedSystemGroups = new String[_allSystemGroups.length];

		System.arraycopy(
			_allSystemGroups, 0, _sortedSystemGroups, 0,
			_allSystemGroups.length);

		Arrays.sort(_sortedSystemGroups, new StringComparator());

		// Regular roles

		String[] customSystemRoles = PropsUtil.getArray(PropsKeys.SYSTEM_ROLES);

		if (ArrayUtil.isEmpty(customSystemRoles)) {
			_allSystemRoles = RoleConstants.SYSTEM_ROLES;
		}
		else {
			_allSystemRoles = ArrayUtil.append(
				RoleConstants.SYSTEM_ROLES, customSystemRoles);
		}

		_sortedSystemRoles = new String[_allSystemRoles.length];

		System.arraycopy(
			_allSystemRoles, 0, _sortedSystemRoles, 0, _allSystemRoles.length);

		Arrays.sort(_sortedSystemRoles, new StringComparator());

		// Organization roles

		String[] customSystemOrganizationRoles = PropsUtil.getArray(
			PropsKeys.SYSTEM_ORGANIZATION_ROLES);

		if (ArrayUtil.isEmpty(customSystemOrganizationRoles)) {
			_allSystemOrganizationRoles =
				RoleConstants.SYSTEM_ORGANIZATION_ROLES;
		}
		else {
			_allSystemOrganizationRoles = ArrayUtil.append(
				RoleConstants.SYSTEM_ORGANIZATION_ROLES,
				customSystemOrganizationRoles);
		}

		_sortedSystemOrganizationRoles =
			new String[_allSystemOrganizationRoles.length];

		System.arraycopy(
			_allSystemOrganizationRoles, 0, _sortedSystemOrganizationRoles, 0,
			_allSystemOrganizationRoles.length);

		Arrays.sort(_sortedSystemOrganizationRoles, new StringComparator());

		// Site roles

		String[] customSystemSiteRoles = PropsUtil.getArray(
			PropsKeys.SYSTEM_SITE_ROLES);

		if (ArrayUtil.isEmpty(customSystemSiteRoles)) {
			_allSystemSiteRoles = RoleConstants.SYSTEM_SITE_ROLES;
		}
		else {
			_allSystemSiteRoles = ArrayUtil.append(
				RoleConstants.SYSTEM_SITE_ROLES, customSystemSiteRoles);
		}

		_sortedSystemSiteRoles = new String[_allSystemSiteRoles.length];

		System.arraycopy(
			_allSystemSiteRoles, 0, _sortedSystemSiteRoles, 0,
			_allSystemSiteRoles.length);

		Arrays.sort(_sortedSystemSiteRoles, new StringComparator());

		// Reserved parameter names

		_reservedParams = new HashSet<>();

		// Portal authentication

		_reservedParams.add("p_auth");
		_reservedParams.add("p_auth_secret");

		// Portal layout

		_reservedParams.add("p_l_id");
		_reservedParams.add("p_l_reset");

		// Portal portlet

		_reservedParams.add("p_p_auth");
		_reservedParams.add("p_p_id");
		_reservedParams.add("p_p_i_id");
		_reservedParams.add("p_p_lifecycle");
		_reservedParams.add("p_p_url_type");
		_reservedParams.add("p_p_state");
		_reservedParams.add("p_p_state_rcv"); // LPS-14144
		_reservedParams.add("p_p_mode");
		_reservedParams.add("p_p_resource_id");
		_reservedParams.add("p_p_cacheability");
		_reservedParams.add("p_p_width");
		_reservedParams.add("p_p_col_id");
		_reservedParams.add("p_p_col_pos");
		_reservedParams.add("p_p_col_count");
		_reservedParams.add("p_p_boundary");
		_reservedParams.add("p_p_decorate");
		_reservedParams.add("p_p_static");
		_reservedParams.add("p_p_isolated");

		// Portal theme

		_reservedParams.add("p_t_lifecycle"); // LPS-14383

		// Portal virtual layout

		_reservedParams.add("p_v_l_s_g_id"); // LPS-23010

		// Portal fragment

		_reservedParams.add("p_f_id");

		// Portal journal article

		_reservedParams.add("p_j_a_id"); // LPS-16418

		// Miscellaneous

		_reservedParams.add("saveLastPath");
		_reservedParams.add("scroll");
		_reservedParams.add("switchGroup");

		_servletContextName =
			PortalContextLoaderListener.getPortalServletContextName();

		if (ArrayUtil.isEmpty(PropsValues.VIRTUAL_HOSTS_VALID_HOSTS) ||
			ArrayUtil.contains(
				PropsValues.VIRTUAL_HOSTS_VALID_HOSTS, StringPool.STAR)) {

			_validPortalDomainCheckDisabled = true;
		}
		else {
			_validPortalDomainCheckDisabled = false;
		}

		// Always allow do as user service tracker

		try {
			Registry registry = RegistryUtil.getRegistry();

			ServiceTracker<AlwaysAllowDoAsUser, AlwaysAllowDoAsUser>
				alwaysAllowDoAsUserServiceTracker = registry.trackServices(
					AlwaysAllowDoAsUser.class,
					new AlwaysAllowDoAsUserServiceTrackerCustomizer());

			alwaysAllowDoAsUserServiceTracker.open();

			ServiceTracker
				<PortalInetSocketAddressEventListener,
					PortalInetSocketAddressEventListener>
						portalInetSocketAddressEventListenerServiceTracker =
							registry.trackServices(
								PortalInetSocketAddressEventListener.class,
								new PortalInetSocketAddressEventListenerServiceTrackerCustomizer());

			portalInetSocketAddressEventListenerServiceTracker.open();
		}
		catch (NullPointerException npe) {
		}
	}

	@Override
	public void addPageDescription(
		String description, HttpServletRequest request) {

		ListMergeable<String> descriptionListMergeable =
			(ListMergeable<String>)request.getAttribute(
				WebKeys.PAGE_DESCRIPTION);

		if (descriptionListMergeable == null) {
			descriptionListMergeable = new ListMergeable<>();

			request.setAttribute(
				WebKeys.PAGE_DESCRIPTION, descriptionListMergeable);
		}

		descriptionListMergeable.add(description);
	}

	@Override
	public void addPageKeywords(String keywords, HttpServletRequest request) {
		ListMergeable<String> keywordsListMergeable =
			(ListMergeable<String>)request.getAttribute(WebKeys.PAGE_KEYWORDS);

		if (keywordsListMergeable == null) {
			keywordsListMergeable = new ListMergeable<>();

			request.setAttribute(WebKeys.PAGE_KEYWORDS, keywordsListMergeable);
		}

		String[] keywordsArray = StringUtil.split(keywords);

		for (String keyword : keywordsArray) {
			if (!keywordsListMergeable.contains(
					StringUtil.toLowerCase(keyword))) {

				keywordsListMergeable.add(StringUtil.toLowerCase(keyword));
			}
		}
	}

	@Override
	public void addPageSubtitle(String subtitle, HttpServletRequest request) {
		ListMergeable<String> subtitleListMergeable =
			(ListMergeable<String>)request.getAttribute(WebKeys.PAGE_SUBTITLE);

		if (subtitleListMergeable == null) {
			subtitleListMergeable = new ListMergeable<>();

			request.setAttribute(WebKeys.PAGE_SUBTITLE, subtitleListMergeable);
		}

		subtitleListMergeable.add(subtitle);
	}

	@Override
	public void addPageTitle(String title, HttpServletRequest request) {
		ListMergeable<String> titleListMergeable =
			(ListMergeable<String>)request.getAttribute(WebKeys.PAGE_TITLE);

		if (titleListMergeable == null) {
			titleListMergeable = new ListMergeable<>();

			request.setAttribute(WebKeys.PAGE_TITLE, titleListMergeable);
		}

		titleListMergeable.add(title);
	}

	@Override
	public boolean addPortalInetSocketAddressEventListener(
		PortalInetSocketAddressEventListener
			portalInetSocketAddressEventListener) {

		return _portalInetSocketAddressEventListeners.add(
			portalInetSocketAddressEventListener);
	}

	/**
	 * Adds the portal port event listener to the portal. The listener will be
	 * notified whenever the portal port is set.
	 *
	 * @param      portalPortEventListener the portal port event listener to add
	 * @deprecated As of 7.0.0, replaced by {@link
	 *             #addPortalInetSocketAddressEventListener(
	 *             PortalInetSocketAddressEventListener)}
	 */
	@Deprecated
	@Override
	public void addPortalPortEventListener(
		PortalPortEventListener portalPortEventListener) {

		if (!_portalPortEventListeners.contains(portalPortEventListener)) {
			_portalPortEventListeners.add(portalPortEventListener);
		}
	}

	@Override
	public void addPortletBreadcrumbEntry(
		HttpServletRequest request, String title, String url) {

		addPortletBreadcrumbEntry(request, title, url, null);
	}

	@Override
	public void addPortletBreadcrumbEntry(
		HttpServletRequest request, String title, String url,
		Map<String, Object> data) {

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		boolean portletBreadcrumbEntry = false;

		if (Validator.isNotNull(portletDisplay.getId()) &&
			!portletDisplay.isFocused()) {

			portletBreadcrumbEntry = true;
		}

		addPortletBreadcrumbEntry(
			request, title, url, null, portletBreadcrumbEntry);
	}

	@Override
	public void addPortletBreadcrumbEntry(
		HttpServletRequest request, String title, String url,
		Map<String, Object> data, boolean portletBreadcrumbEntry) {

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		String name = WebKeys.PORTLET_BREADCRUMBS;

		if (portletBreadcrumbEntry) {
			name += StringPool.UNDERLINE + portletDisplay.getId();
		}

		List<BreadcrumbEntry> breadcrumbEntries =
			(List<BreadcrumbEntry>)request.getAttribute(name);

		if (breadcrumbEntries == null) {
			breadcrumbEntries = new ArrayList<>();

			request.setAttribute(name, breadcrumbEntries);
		}

		BreadcrumbEntry breadcrumbEntry = new BreadcrumbEntry();

		breadcrumbEntry.setData(data);
		breadcrumbEntry.setTitle(title);
		breadcrumbEntry.setURL(url);

		breadcrumbEntries.add(breadcrumbEntry);
	}

	@Override
	public void addPortletDefaultResource(
			HttpServletRequest request, Portlet portlet)
		throws PortalException {

		String name = ResourceActionsUtil.getPortletBaseResource(
			portlet.getRootPortletId());

		if (Validator.isNull(name)) {
			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		long groupId = 0;

		if (layout.isTypeControlPanel()) {
			groupId = themeDisplay.getScopeGroupId();
		}
		else {
			groupId = getScopeGroupId(layout, portlet.getPortletId());
		}

		addRootModelResource(themeDisplay.getCompanyId(), groupId, name);
	}

	@Override
	public void addPortletDefaultResource(
			long companyId, Layout layout, Portlet portlet)
		throws PortalException {

		String name = ResourceActionsUtil.getPortletBaseResource(
			portlet.getRootPortletId());

		if (Validator.isNull(name)) {
			return;
		}

		long groupId = getScopeGroupId(layout, portlet.getPortletId());

		addRootModelResource(companyId, groupId, name);
	}

	@Override
	public String addPreservedParameters(
		ThemeDisplay themeDisplay, Layout layout, String url,
		boolean doAsUser) {

		return addPreservedParameters(
			themeDisplay, url, layout.isTypeControlPanel(), doAsUser);
	}

	@Override
	public String addPreservedParameters(
		ThemeDisplay themeDisplay, String url) {

		return addPreservedParameters(
			themeDisplay, themeDisplay.getLayout(), url, true);
	}

	@Override
	public String addPreservedParameters(
		ThemeDisplay themeDisplay, String url, boolean typeControlPanel,
		boolean doAsUser) {

		if (doAsUser) {
			if (Validator.isNotNull(themeDisplay.getDoAsUserId())) {
				url = HttpUtil.setParameter(
					url, "doAsUserId", themeDisplay.getDoAsUserId());
			}

			if (Validator.isNotNull(themeDisplay.getDoAsUserLanguageId())) {
				url = HttpUtil.setParameter(
					url, "doAsUserLanguageId",
					themeDisplay.getDoAsUserLanguageId());
			}
		}

		if (typeControlPanel) {
			if (Validator.isNotNull(themeDisplay.getPpid())) {
				url = HttpUtil.setParameter(
					url, "p_p_id", themeDisplay.getPpid());
			}

			if (themeDisplay.getDoAsGroupId() > 0) {
				url = HttpUtil.setParameter(
					url, "doAsGroupId", themeDisplay.getDoAsGroupId());
			}

			if (themeDisplay.getRefererGroupId() !=
					GroupConstants.DEFAULT_PARENT_GROUP_ID) {

				url = HttpUtil.setParameter(
					url, "refererGroupId", themeDisplay.getRefererGroupId());
			}

			if (themeDisplay.getRefererPlid() != LayoutConstants.DEFAULT_PLID) {
				url = HttpUtil.setParameter(
					url, "refererPlid", themeDisplay.getRefererPlid());
			}
		}

		return url;
	}

	@Override
	public void addUserLocaleOptionsMessage(HttpServletRequest request) {
		boolean ignoreUserLocaleOptions = GetterUtil.getBoolean(
			SessionClicks.get(
				request.getSession(), "ignoreUserLocaleOptions",
				Boolean.FALSE.toString()));

		if (ignoreUserLocaleOptions) {
			return;
		}

		boolean showUserLocaleOptionsMessage = ParamUtil.getBoolean(
			request, "showUserLocaleOptionsMessage", true);

		if (!showUserLocaleOptionsMessage) {
			return;
		}

		PortalMessages.add(request, PortalMessages.KEY_ANIMATION, false);
		PortalMessages.add(
			request, PortalMessages.KEY_JSP_PATH,
			"/html/common/themes/user_locale_options.jsp");
		PortalMessages.add(request, PortalMessages.KEY_TIMEOUT, -1);
	}

	@Override
	public void clearRequestParameters(RenderRequest renderRequest) {
		RenderRequestImpl renderRequestImpl = (RenderRequestImpl)renderRequest;

		if (renderRequestImpl.isTriggeredByActionURL()) {
			renderRequestImpl.clearRenderParameters();
		}
	}

	@Override
	public void copyRequestParameters(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		if (actionResponse instanceof StateAwareResponseImpl) {
			StateAwareResponseImpl stateAwareResponseImpl =
				(StateAwareResponseImpl)actionResponse;

			if (stateAwareResponseImpl.getRedirectLocation() != null) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Cannot copy parameters on a redirected " +
							"StateAwareResponseImpl");
				}

				return;
			}
		}

		LiferayPortletResponse liferayPortletResponse =
			getLiferayPortletResponse(actionResponse);

		StateAwareResponse stateAwareResponse =
			(StateAwareResponse)liferayPortletResponse;

		Map<String, String[]> renderParameters =
			stateAwareResponse.getRenderParameterMap();

		actionResponse.setRenderParameter("p_p_lifecycle", "1");

		Enumeration<String> enu = actionRequest.getParameterNames();

		while (enu.hasMoreElements()) {
			String param = enu.nextElement();

			String[] values = actionRequest.getParameterValues(param);

			if (renderParameters.get(actionResponse.getNamespace() + param) ==
					null) {

				actionResponse.setRenderParameter(param, values);
			}
		}
	}

	@Override
	public String escapeRedirect(String url) {
		if (Validator.isNull(url)) {
			return url;
		}

		url = url.trim();

		if ((url.charAt(0) == CharPool.SLASH) && (url.length() > 1) &&
			(url.charAt(1) != CharPool.SLASH)) {

			return url;
		}

		String domain = HttpUtil.getDomain(url);

		if (domain.isEmpty()) {
			return null;
		}

		if (!_validPortalDomainCheckDisabled && isValidPortalDomain(domain)) {
			return url;
		}

		String securityMode = PropsValues.REDIRECT_URL_SECURITY_MODE;

		if (securityMode.equals("domain")) {
			String[] allowedDomains = PropsValues.REDIRECT_URL_DOMAINS_ALLOWED;

			if (allowedDomains.length == 0) {
				return url;
			}

			for (String allowedDomain : allowedDomains) {
				if (allowedDomain.startsWith("*.") &&
					(allowedDomain.regionMatches(
						1, domain,
						domain.length() - (allowedDomain.length() - 1),
						allowedDomain.length() - 1) ||
					 allowedDomain.regionMatches(
						 2, domain, 0, domain.length()))) {

					return url;
				}
				else if (allowedDomain.equals(domain)) {
					return url;
				}
			}

			if (_log.isWarnEnabled()) {
				_log.warn("Redirect URL " + url + " is not allowed");
			}

			url = null;
		}
		else if (securityMode.equals("ip")) {
			String[] allowedIps = PropsValues.REDIRECT_URL_IPS_ALLOWED;

			if (allowedIps.length == 0) {
				return url;
			}

			try {
				InetAddress inetAddress = InetAddress.getByName(domain);

				String hostAddress = inetAddress.getHostAddress();

				boolean serverIpIsHostAddress = _computerAddresses.contains(
					hostAddress);

				for (String ip : allowedIps) {
					if ((serverIpIsHostAddress && ip.equals("SERVER_IP")) ||
						ip.equals(hostAddress)) {

						return url;
					}
				}

				if (_log.isWarnEnabled()) {
					_log.warn("Redirect URL " + url + " is not allowed");
				}
			}
			catch (UnknownHostException uhe) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to determine IP for redirect URL " + url);
				}
			}

			url = null;
		}

		return url;
	}

	@Override
	public String generateRandomKey(HttpServletRequest request, String input) {
		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (themeDisplay.isAjax() || themeDisplay.isIsolated() ||
			themeDisplay.isLifecycleResource() ||
			themeDisplay.isStateExclusive()) {

			return StringUtil.randomId();
		}
		else {
			StringBundler sb = new StringBundler(5);

			sb.append(DeterminateKeyGenerator.generate(input));
			sb.append(StringPool.UNDERLINE);
			sb.append(request.getAttribute(WebKeys.RENDER_PORTLET_COLUMN_ID));
			sb.append(StringPool.UNDERLINE);
			sb.append(request.getAttribute(WebKeys.RENDER_PORTLET_COLUMN_POS));

			return JS.getSafeName(sb.toString());
		}
	}

	@Override
	public String getAbsoluteURL(HttpServletRequest request, String url) {
		String portalURL = getPortalURL(request);

		if (url.charAt(0) == CharPool.SLASH) {
			if (Validator.isNotNull(portalURL)) {
				url = portalURL.concat(url);
			}
		}

		if (!CookieKeys.hasSessionId(request) && url.startsWith(portalURL)) {
			HttpSession session = request.getSession();

			url = getURLWithSessionId(url, session.getId());
		}

		return url;
	}

	@Override
	public LayoutQueryStringComposite getActualLayoutQueryStringComposite(
			long groupId, boolean privateLayout, String friendlyURL,
			Map<String, String[]> params, Map<String, Object> requestContext)
		throws PortalException {

		Layout layout = null;
		String layoutQueryStringCompositeFriendlyURL = friendlyURL;
		String queryString = StringPool.BLANK;

		if (Validator.isNull(friendlyURL)) {

			// We need to ensure that virtual layouts are merged

			List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(
				groupId, privateLayout,
				LayoutConstants.DEFAULT_PARENT_LAYOUT_ID);

			if (!layouts.isEmpty()) {
				layout = layouts.get(0);
			}
			else {
				throw new NoSuchLayoutException(
					StringBundler.concat(
						"{groupId=", String.valueOf(groupId),
						", privateLayout=", String.valueOf(privateLayout),
						"}"));
			}
		}
		else {
			return getPortletFriendlyURLMapperLayoutQueryStringComposite(
				groupId, privateLayout, friendlyURL, params, requestContext);
		}

		return new LayoutQueryStringComposite(
			layout, layoutQueryStringCompositeFriendlyURL, queryString);
	}

	@Override
	public String getActualURL(
			long groupId, boolean privateLayout, String mainPath,
			String friendlyURL, Map<String, String[]> params,
			Map<String, Object> requestContext)
		throws PortalException {

		String actualURL = null;

		if (friendlyURL != null) {
			HttpServletRequest request = (HttpServletRequest)requestContext.get(
				"request");

			long companyId = PortalInstances.getCompanyId(request);

			Collection<FriendlyURLResolver> friendlyURLResolvers =
				FriendlyURLResolverRegistryUtil.
					getFriendlyURLResolversAsCollection();

			for (FriendlyURLResolver friendlyURLResolver :
					friendlyURLResolvers) {

				if (!friendlyURL.startsWith(
						friendlyURLResolver.getURLSeparator())) {

					continue;
				}

				try {
					actualURL = friendlyURLResolver.getActualURL(
						companyId, groupId, privateLayout, mainPath,
						friendlyURL, params, requestContext);

					break;
				}
				catch (Exception e) {
					throw new NoSuchLayoutException(e);
				}
			}
		}

		if (actualURL == null) {
			actualURL = getLayoutActualURL(
				groupId, privateLayout, mainPath, friendlyURL, params,
				requestContext);
		}

		return actualURL;
	}

	@Override
	public String getAlternateURL(
			String canonicalURL, ThemeDisplay themeDisplay, Locale locale,
			Layout layout)
		throws PortalException {

		Map<Locale, String> alternateURLs = _getAlternateURLs(
			canonicalURL, themeDisplay, layout, Collections.singleton(locale));

		return alternateURLs.get(locale);
	}

	@Override
	public Map<Locale, String> getAlternateURLs(
			String canonicalURL, ThemeDisplay themeDisplay, Layout layout)
		throws PortalException {

		Set<Locale> availableLocales = LanguageUtil.getAvailableLocales(
			themeDisplay.getSiteGroupId());

		return _getAlternateURLs(
			canonicalURL, themeDisplay, layout, availableLocales);
	}

	@Override
	public long[] getAncestorSiteGroupIds(long groupId) throws PortalException {
		int i = 0;

		Set<Group> groups = doGetAncestorSiteGroups(groupId, false);

		long[] groupIds = new long[groups.size()];

		for (Group group : groups) {
			groupIds[i++] = group.getGroupId();
		}

		return groupIds;
	}

	@Override
	public BaseModel<?> getBaseModel(ResourcePermission resourcePermission)
		throws PortalException {

		String modelName = resourcePermission.getName();
		String primKey = resourcePermission.getPrimKey();

		return getBaseModel(modelName, primKey);
	}

	@Override
	public BaseModel<?> getBaseModel(String modelName, String primKey)
		throws PortalException {

		if (!modelName.contains(".model.")) {
			return null;
		}

		String[] parts = StringUtil.split(modelName, CharPool.PERIOD);

		if ((parts.length <= 2) || !parts[parts.length - 2].equals("model")) {
			return null;
		}

		parts[parts.length - 2] = "service";

		String serviceName =
			StringUtil.merge(parts, StringPool.PERIOD) + "LocalServiceUtil";
		String methodName = "get" + parts[parts.length - 1];

		Method method = null;

		try {
			Class<?> serviceUtil = Class.forName(serviceName);

			if (Validator.isNumber(primKey)) {
				method = serviceUtil.getMethod(
					methodName, new Class<?>[] {Long.TYPE});

				return (BaseModel<?>)method.invoke(null, Long.valueOf(primKey));
			}

			method = serviceUtil.getMethod(
				methodName, new Class<?>[] {String.class});

			return (BaseModel<?>)method.invoke(null, primKey);
		}
		catch (Exception e) {
			Throwable cause = e.getCause();

			if (cause instanceof PortalException) {
				throw (PortalException)cause;
			}
			else if (cause instanceof SystemException) {
				throw (SystemException)cause;
			}
			else {
				throw new SystemException(cause);
			}
		}
	}

	/**
	 * @deprecated As of 7.0.0, replaced by {@link
	 *             HttpAuthManagerUtil#getBasicUserId(HttpServletRequest)}
	 */
	@Deprecated
	@Override
	public long getBasicAuthUserId(HttpServletRequest request)
		throws PortalException {

		long companyId = PortalInstances.getCompanyId(request);

		return getBasicAuthUserId(request, companyId);
	}

	/**
	 * @deprecated As of 7.0.0, replaced by {@link
	 *             HttpAuthManagerUtil#getBasicUserId(HttpServletRequest)}
	 */
	@Deprecated
	@Override
	public long getBasicAuthUserId(HttpServletRequest request, long companyId)
		throws PortalException {

		return HttpAuthManagerUtil.getBasicUserId(request);
	}

	@Override
	public List<Group> getBrowsableScopeGroups(
			long userId, long companyId, long groupId, String portletId)
		throws PortalException {

		List<Group> groups = new ArrayList<>();

		LinkedHashMap<String, Object> params = new LinkedHashMap<>();

		params.put("usersGroups", Long.valueOf(userId));

		groups.addAll(
			0,
			GroupLocalServiceUtil.search(
				companyId, null, null, params, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS));

		List<Organization> organizations =
			OrganizationLocalServiceUtil.getUserOrganizations(userId);

		for (Organization organization : organizations) {
			groups.add(0, organization.getGroup());
		}

		if (PropsValues.LAYOUT_USER_PRIVATE_LAYOUTS_ENABLED ||
			PropsValues.LAYOUT_USER_PUBLIC_LAYOUTS_ENABLED) {

			groups.add(
				0, GroupLocalServiceUtil.getUserGroup(companyId, userId));
		}

		groups.addAll(0, getCurrentAndAncestorSiteGroups(groupId));

		List<Group> filteredGroups = new ArrayList<>();

		for (Group group : ListUtil.unique(groups)) {
			if (group.hasStagingGroup()) {
				Group stagingGroup = group.getStagingGroup();

				if ((stagingGroup.getGroupId() == groupId) &&
					group.isStagedPortlet(portletId) &&
					!group.isStagedRemotely() &&
					group.isStagedPortlet(PortletKeys.DOCUMENT_LIBRARY)) {

					filteredGroups.add(stagingGroup);
				}
			}
			else {
				filteredGroups.add(group);
			}
		}

		return filteredGroups;
	}

	@Override
	public String getCanonicalURL(
			String completeURL, ThemeDisplay themeDisplay, Layout layout)
		throws PortalException {

		return getCanonicalURL(completeURL, themeDisplay, layout, false);
	}

	@Override
	public String getCanonicalURL(
			String completeURL, ThemeDisplay themeDisplay, Layout layout,
			boolean forceLayoutFriendlyURL)
		throws PortalException {

		String groupFriendlyURL = StringPool.BLANK;
		String parametersURL = StringPool.BLANK;

		if (Validator.isNotNull(completeURL)) {
			completeURL = removeRedirectParameter(completeURL);

			int pos = completeURL.indexOf(Portal.FRIENDLY_URL_SEPARATOR);

			if (pos == -1) {
				pos = completeURL.indexOf(StringPool.QUESTION);
			}

			groupFriendlyURL = completeURL;

			if (pos != -1) {
				groupFriendlyURL = completeURL.substring(0, pos);
				parametersURL = completeURL.substring(pos);
			}
		}

		if (layout == null) {
			layout = themeDisplay.getLayout();
		}

		String canonicalLayoutFriendlyURL = StringPool.BLANK;

		String defaultLayoutFriendlyURL = null;

		Locale siteDefaultLocale = getSiteDefaultLocale(layout.getGroupId());

		if (siteDefaultLocale.equals(themeDisplay.getLocale())) {
			defaultLayoutFriendlyURL = themeDisplay.getLayoutFriendlyURL(
				layout);
		}
		else {
			defaultLayoutFriendlyURL = layout.getFriendlyURL(
				getSiteDefaultLocale(layout.getGroupId()));
		}

		if ((!layout.isFirstParent() || Validator.isNotNull(parametersURL)) &&
			(groupFriendlyURL.contains(
				themeDisplay.getLayoutFriendlyURL(layout)) ||
			 groupFriendlyURL.contains(
				 StringPool.SLASH + layout.getLayoutId()))) {

			canonicalLayoutFriendlyURL = defaultLayoutFriendlyURL;
		}
		else if (forceLayoutFriendlyURL) {
			canonicalLayoutFriendlyURL = defaultLayoutFriendlyURL;
		}

		groupFriendlyURL = getGroupFriendlyURL(
			layout.getLayoutSet(), themeDisplay, true,
			layout.isTypeControlPanel());

		if (PropsValues.LOCALE_PREPEND_FRIENDLY_URL_STYLE == 2) {
			String groupFriendlyURLDomain = HttpUtil.getDomain(
				groupFriendlyURL);

			int pos = groupFriendlyURL.indexOf(groupFriendlyURLDomain);

			if (pos > 0) {
				pos = groupFriendlyURL.indexOf(
					CharPool.SLASH, pos + groupFriendlyURLDomain.length());

				if (Validator.isNotNull(_pathContext)) {
					pos = groupFriendlyURL.indexOf(
						CharPool.SLASH, pos + _pathContext.length());
				}
			}

			StringBundler sb = new StringBundler(5);

			if ((pos <= 0) || (pos >= groupFriendlyURL.length())) {
				sb.append(groupFriendlyURL);
				sb.append(buildI18NPath(siteDefaultLocale));

				if (!canonicalLayoutFriendlyURL.startsWith(StringPool.SLASH)) {
					sb.append(StringPool.SLASH);
				}
			}
			else {
				String groupFriendlyURLPrefix = groupFriendlyURL.substring(
					0, pos);

				String groupFriendlyURLSuffix = groupFriendlyURL.substring(pos);

				sb.append(groupFriendlyURLPrefix);
				sb.append(buildI18NPath(siteDefaultLocale));
				sb.append(groupFriendlyURLSuffix);
			}

			sb.append(canonicalLayoutFriendlyURL);
			sb.append(parametersURL);

			return sb.toString();
		}

		return groupFriendlyURL.concat(canonicalLayoutFriendlyURL).concat(
			parametersURL);
	}

	@Override
	public String getCDNHost(boolean secure) {
		long companyId = CompanyThreadLocal.getCompanyId();

		if (secure) {
			return getCDNHostHttps(companyId);
		}
		else {
			return getCDNHostHttp(companyId);
		}
	}

	@Override
	public String getCDNHost(HttpServletRequest request)
		throws PortalException {

		boolean cdnEnabled = ParamUtil.getBoolean(request, "cdn_enabled", true);
		String portletId = ParamUtil.getString(request, "p_p_id");

		if (!cdnEnabled || portletId.equals(PortletKeys.PORTAL_SETTINGS)) {
			return StringPool.BLANK;
		}

		String cdnHost = null;

		Company company = getCompany(request);

		if (request.isSecure()) {
			cdnHost = getCDNHostHttps(company.getCompanyId());
		}
		else {
			cdnHost = getCDNHostHttp(company.getCompanyId());
		}

		if (Validator.isUrl(cdnHost)) {
			return cdnHost;
		}

		return StringPool.BLANK;
	}

	@Override
	public String getCDNHostHttp(long companyId) {
		String cdnHostHttp = _cdnHostHttpMap.get(companyId);

		if (cdnHostHttp != null) {
			return cdnHostHttp;
		}

		try {
			cdnHostHttp = PrefsPropsUtil.getString(
				companyId, PropsKeys.CDN_HOST_HTTP, PropsValues.CDN_HOST_HTTP);
		}
		catch (Exception e) {
		}

		if ((cdnHostHttp == null) || cdnHostHttp.startsWith("${") ||
			!Validator.isUrl(cdnHostHttp)) {

			cdnHostHttp = StringPool.BLANK;
		}

		_cdnHostHttpMap.put(companyId, cdnHostHttp);

		return cdnHostHttp;
	}

	@Override
	public String getCDNHostHttps(long companyId) {
		String cdnHostHttps = _cdnHostHttpsMap.get(companyId);

		if (cdnHostHttps != null) {
			return cdnHostHttps;
		}

		try {
			cdnHostHttps = PrefsPropsUtil.getString(
				companyId, PropsKeys.CDN_HOST_HTTPS,
				PropsValues.CDN_HOST_HTTPS);
		}
		catch (SystemException se) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(se, se);
			}
		}

		if ((cdnHostHttps == null) || cdnHostHttps.startsWith("${") ||
			!Validator.isUrl(cdnHostHttps)) {

			cdnHostHttps = StringPool.BLANK;
		}

		_cdnHostHttpsMap.put(companyId, cdnHostHttps);

		return cdnHostHttps;
	}

	@Override
	public String getClassName(long classNameId) {
		try {
			ClassName className = ClassNameLocalServiceUtil.getClassName(
				classNameId);

			return className.getValue();
		}
		catch (Exception e) {
			throw new RuntimeException(
				"Unable to get class name from id " + classNameId, e);
		}
	}

	@Override
	public long getClassNameId(Class<?> clazz) {
		return getClassNameId(clazz.getName());
	}

	@Override
	public long getClassNameId(String value) {
		if (!StartupHelperUtil.isUpgrading()) {
			return ClassNameLocalServiceUtil.getClassNameId(value);
		}

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"select classNameId from ClassName_ where value = ?");

			ps.setString(1, value);

			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getLong("classNameId");
			}
		}
		catch (Exception e) {
			throw new RuntimeException(
				"Unable to get class name ID from value " + value, e);
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}

		return 0;
	}

	@Override
	public Company getCompany(HttpServletRequest request)
		throws PortalException {

		long companyId = getCompanyId(request);

		if (companyId <= 0) {
			return null;
		}

		Company company = (Company)request.getAttribute(WebKeys.COMPANY);

		if (company == null) {

			// LEP-5994

			company = CompanyLocalServiceUtil.fetchCompanyById(companyId);

			if (company == null) {
				company = CompanyLocalServiceUtil.getCompanyById(
					PortalInstances.getDefaultCompanyId());
			}

			request.setAttribute(WebKeys.COMPANY, company);
		}

		return company;
	}

	@Override
	public Company getCompany(PortletRequest portletRequest)
		throws PortalException {

		return getCompany(getHttpServletRequest(portletRequest));
	}

	@Override
	public long getCompanyId(HttpServletRequest request) {
		return PortalInstances.getCompanyId(request);
	}

	@Override
	public long getCompanyId(PortletRequest portletRequest) {
		return getCompanyId(getHttpServletRequest(portletRequest));
	}

	@Override
	public long[] getCompanyIds() {
		return PortalInstances.getCompanyIds();
	}

	@Override
	public Set<String> getComputerAddresses() {
		return _computerAddresses;
	}

	@Override
	public String getComputerName() {
		return _computerName;
	}

	@Override
	public String getControlPanelFullURL(
			long scopeGroupId, String ppid, Map<String, String[]> params)
		throws PortalException {

		Group group = GroupLocalServiceUtil.getGroup(scopeGroupId);

		Group controlPanelDisplayGroup = getControlPanelDisplayGroup(
			group.getCompanyId(), scopeGroupId, 0, ppid);

		return getSiteAdminURL(controlPanelDisplayGroup, ppid, params);
	}

	@Override
	public long getControlPanelPlid(long companyId) throws PortalException {
		Group controlPanelGroup = GroupLocalServiceUtil.getGroup(
			companyId, GroupConstants.CONTROL_PANEL);

		return LayoutLocalServiceUtil.getDefaultPlid(
			controlPanelGroup.getGroupId(), true);
	}

	@Override
	public long getControlPanelPlid(PortletRequest portletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return getControlPanelPlid(themeDisplay.getCompanyId());
	}

	@Override
	public PortletURL getControlPanelPortletURL(
		HttpServletRequest request, Group group, String portletId,
		long refererGroupId, long refererPlid, String lifecycle) {

		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(request);

		if (group == null) {
			ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
				WebKeys.THEME_DISPLAY);

			group = getControlPanelDisplayGroup(
				themeDisplay.getControlPanelGroup(),
				themeDisplay.getScopeGroup(), themeDisplay.getDoAsGroupId(),
				portletId);
		}

		return requestBackedPortletURLFactory.createControlPanelPortletURL(
			portletId, group, refererGroupId, refererPlid, lifecycle);
	}

	@Override
	public PortletURL getControlPanelPortletURL(
		HttpServletRequest request, String portletId, String lifecycle) {

		return getControlPanelPortletURL(
			request, null, portletId, 0, 0, lifecycle);
	}

	@Override
	public PortletURL getControlPanelPortletURL(
		PortletRequest portletRequest, Group group, String portletId,
		long refererGroupId, long refererPlid, String lifecycle) {

		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(portletRequest);

		if (group == null) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = getControlPanelDisplayGroup(
				themeDisplay.getControlPanelGroup(),
				themeDisplay.getScopeGroup(), themeDisplay.getDoAsGroupId(),
				portletId);
		}

		return requestBackedPortletURLFactory.createControlPanelPortletURL(
			portletId, group, refererGroupId, refererPlid, lifecycle);
	}

	@Override
	public PortletURL getControlPanelPortletURL(
		PortletRequest portletRequest, String portletId, String lifecycle) {

		return getControlPanelPortletURL(
			portletRequest, null, portletId, 0, 0, lifecycle);
	}

	@Override
	public String getCreateAccountURL(
			HttpServletRequest request, ThemeDisplay themeDisplay)
		throws Exception {

		if (Validator.isNull(PropsValues.COMPANY_SECURITY_STRANGERS_URL)) {
			long plid = themeDisplay.getPlid();

			Layout layout = themeDisplay.getLayout();

			if (layout.isPrivateLayout()) {
				plid = LayoutLocalServiceUtil.getDefaultPlid(
					layout.getGroupId(), false);
			}

			PortletURL createAccountURL = PortletURLFactoryUtil.create(
				request, PortletKeys.LOGIN, plid, PortletRequest.RENDER_PHASE);

			createAccountURL.setParameter(
				"saveLastPath", Boolean.FALSE.toString());
			createAccountURL.setParameter(
				"mvcRenderCommandName", "/login/create_account");
			createAccountURL.setPortletMode(PortletMode.VIEW);
			createAccountURL.setWindowState(WindowState.MAXIMIZED);

			if (!PropsValues.COMPANY_SECURITY_AUTH_REQUIRES_HTTPS) {
				return createAccountURL.toString();
			}

			String portalURL = getPortalURL(request);
			String portalURLSecure = getPortalURL(request, true);

			return StringUtil.replaceFirst(
				createAccountURL.toString(), portalURL, portalURLSecure);
		}

		try {
			Layout layout = LayoutLocalServiceUtil.getFriendlyURLLayout(
				themeDisplay.getScopeGroupId(), false,
				PropsValues.COMPANY_SECURITY_STRANGERS_URL);

			return getLayoutURL(layout, themeDisplay);
		}
		catch (NoSuchLayoutException nsle) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(nsle, nsle);
			}
		}

		return StringPool.BLANK;
	}

	@Override
	public long[] getCurrentAndAncestorSiteGroupIds(long groupId)
		throws PortalException {

		return getCurrentAndAncestorSiteGroupIds(groupId, false);
	}

	@Override
	public long[] getCurrentAndAncestorSiteGroupIds(
			long groupId, boolean checkContentSharingWithChildrenEnabled)
		throws PortalException {

		List<Group> groups = getCurrentAndAncestorSiteGroups(
			groupId, checkContentSharingWithChildrenEnabled);

		long[] groupIds = new long[groups.size()];

		for (int i = 0; i < groups.size(); i++) {
			Group group = groups.get(i);

			groupIds[i] = group.getGroupId();
		}

		return groupIds;
	}

	@Override
	public long[] getCurrentAndAncestorSiteGroupIds(long[] groupIds)
		throws PortalException {

		return getCurrentAndAncestorSiteGroupIds(groupIds, false);
	}

	@Override
	public long[] getCurrentAndAncestorSiteGroupIds(
			long[] groupIds, boolean checkContentSharingWithChildrenEnabled)
		throws PortalException {

		List<Group> groups = getCurrentAndAncestorSiteGroups(
			groupIds, checkContentSharingWithChildrenEnabled);

		long[] currentAndAncestorSiteGroupIds = new long[groups.size()];

		for (int i = 0; i < groups.size(); i++) {
			Group group = groups.get(i);

			currentAndAncestorSiteGroupIds[i] = group.getGroupId();
		}

		return currentAndAncestorSiteGroupIds;
	}

	@Override
	public List<Group> getCurrentAndAncestorSiteGroups(long groupId)
		throws PortalException {

		return getCurrentAndAncestorSiteGroups(groupId, false);
	}

	@Override
	public List<Group> getCurrentAndAncestorSiteGroups(
			long groupId, boolean checkContentSharingWithChildrenEnabled)
		throws PortalException {

		Set<Group> groups = new LinkedHashSet<>();

		Group siteGroup = doGetCurrentSiteGroup(groupId);

		if (siteGroup != null) {
			groups.add(siteGroup);
		}

		groups.addAll(
			doGetAncestorSiteGroups(
				groupId, checkContentSharingWithChildrenEnabled));

		return new ArrayList<>(groups);
	}

	@Override
	public List<Group> getCurrentAndAncestorSiteGroups(long[] groupIds)
		throws PortalException {

		return getCurrentAndAncestorSiteGroups(groupIds, false);
	}

	@Override
	public List<Group> getCurrentAndAncestorSiteGroups(
			long[] groupIds, boolean checkContentSharingWithChildrenEnabled)
		throws PortalException {

		Set<Group> groups = new LinkedHashSet<>();

		for (int i = 0; i < groupIds.length; i++) {
			groups.addAll(
				getCurrentAndAncestorSiteGroups(
					groupIds[i], checkContentSharingWithChildrenEnabled));
		}

		return new ArrayList<>(groups);
	}

	@Override
	public String getCurrentCompleteURL(HttpServletRequest request) {
		String currentCompleteURL = (String)request.getAttribute(
			WebKeys.CURRENT_COMPLETE_URL);

		if (currentCompleteURL == null) {
			currentCompleteURL = HttpUtil.getCompleteURL(request);

			request.setAttribute(
				WebKeys.CURRENT_COMPLETE_URL, currentCompleteURL);
		}

		return currentCompleteURL;
	}

	@Override
	public String getCurrentURL(HttpServletRequest request) {
		String currentURL = (String)request.getAttribute(WebKeys.CURRENT_URL);

		if (currentURL != null) {
			return currentURL;
		}

		currentURL = ParamUtil.getString(request, "currentURL");

		if (Validator.isNull(currentURL)) {
			currentURL = HttpUtil.getCompleteURL(request);

			if (Validator.isNotNull(currentURL) &&
				!currentURL.contains(_J_SECURITY_CHECK)) {

				currentURL = currentURL.substring(
					currentURL.indexOf(Http.PROTOCOL_DELIMITER) +
						Http.PROTOCOL_DELIMITER.length());

				currentURL = currentURL.substring(
					currentURL.indexOf(CharPool.SLASH));
			}

			if (Validator.isNotNull(currentURL) &&
				FacebookUtil.isFacebook(currentURL)) {

				String[] facebookData = FacebookUtil.getFacebookData(request);

				if (facebookData != null) {
					currentURL =
						FacebookUtil.FACEBOOK_APPS_URL + facebookData[0] +
							facebookData[2];
				}
			}
		}

		if (Validator.isNull(currentURL)) {
			currentURL = getPathMain();
		}

		request.setAttribute(WebKeys.CURRENT_URL, currentURL);

		return currentURL;
	}

	@Override
	public String getCurrentURL(PortletRequest portletRequest) {
		return (String)portletRequest.getAttribute(WebKeys.CURRENT_URL);
	}

	@Override
	public String getCustomSQLFunctionIsNotNull() {
		return PropsValues.CUSTOM_SQL_FUNCTION_ISNOTNULL;
	}

	@Override
	public String getCustomSQLFunctionIsNull() {
		return PropsValues.CUSTOM_SQL_FUNCTION_ISNULL;
	}

	@Override
	public Date getDate(int month, int day, int year) {
		try {
			return getDate(month, day, year, null);
		}
		catch (PortalException pe) {
			throw new RuntimeException(pe);
		}
	}

	@Override
	public Date getDate(
			int month, int day, int year,
			Class<? extends PortalException> clazz)
		throws PortalException {

		return getDate(month, day, year, null, clazz);
	}

	@Override
	public Date getDate(
			int month, int day, int year, int hour, int min,
			Class<? extends PortalException> clazz)
		throws PortalException {

		return getDate(month, day, year, hour, min, null, clazz);
	}

	@Override
	public Date getDate(
			int month, int day, int year, int hour, int min, TimeZone timeZone,
			Class<? extends PortalException> clazz)
		throws PortalException {

		if (!Validator.isGregorianDate(month, day, year)) {
			if (clazz != null) {
				try {
					throw clazz.newInstance();
				}
				catch (PortalException pe) {
					throw pe;
				}
				catch (Exception e) {
					throw new PortalException(e);
				}
			}
			else {
				return null;
			}
		}
		else {
			Calendar cal = null;

			if (timeZone == null) {
				cal = CalendarFactoryUtil.getCalendar();
			}
			else {
				cal = CalendarFactoryUtil.getCalendar(timeZone);
			}

			if ((hour == -1) || (min == -1)) {
				cal.set(year, month, day, 0, 0, 0);
			}
			else {
				cal.set(year, month, day, hour, min, 0);
			}

			cal.set(Calendar.MILLISECOND, 0);

			Date date = cal.getTime();

			/*if ((timeZone != null) &&
				cal.before(CalendarFactoryUtil.getCalendar(timeZone))) {

				throw pe;
			}*/

			return date;
		}
	}

	@Override
	public Date getDate(
			int month, int day, int year, TimeZone timeZone,
			Class<? extends PortalException> clazz)
		throws PortalException {

		return getDate(month, day, year, -1, -1, timeZone, clazz);
	}

	@Override
	public long getDefaultCompanyId() {
		return PortalInstances.getDefaultCompanyId();
	}

	/**
	 * @deprecated As of 7.0.0, replaced by {@link
	 *             HttpAuthManagerUtil#getDigestUserId(HttpServletRequest)}
	 */
	@Deprecated
	@Override
	public long getDigestAuthUserId(HttpServletRequest request)
		throws PortalException {

		return HttpAuthManagerUtil.getDigestUserId(request);
	}

	@Override
	public String getEmailFromAddress(
		PortletPreferences preferences, long companyId, String defaultValue) {

		if (Validator.isNull(defaultValue)) {
			defaultValue = PrefsPropsUtil.getString(
				companyId, PropsKeys.ADMIN_EMAIL_FROM_ADDRESS);
		}

		return preferences.getValue("emailFromAddress", defaultValue);
	}

	@Override
	public String getEmailFromName(
		PortletPreferences preferences, long companyId, String defaultValue) {

		if (Validator.isNull(defaultValue)) {
			defaultValue = PrefsPropsUtil.getString(
				companyId, PropsKeys.ADMIN_EMAIL_FROM_NAME);
		}

		return preferences.getValue("emailFromName", defaultValue);
	}

	@Override
	public Map<String, Serializable> getExpandoBridgeAttributes(
			ExpandoBridge expandoBridge, HttpServletRequest request)
		throws PortalException {

		Map<String, Serializable> attributes = new HashMap<>();

		List<String> names = new ArrayList<>();

		Enumeration<String> enu = request.getParameterNames();

		while (enu.hasMoreElements()) {
			String param = enu.nextElement();

			if (param.contains("ExpandoAttributeName--")) {
				String name = ParamUtil.getString(request, param);

				names.add(name);
			}
		}

		for (String name : names) {
			int type = expandoBridge.getAttributeType(name);

			UnicodeProperties properties = expandoBridge.getAttributeProperties(
				name);

			String displayType = GetterUtil.getString(
				properties.getProperty(
					ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE),
				ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_TEXT_BOX);

			Serializable value = getExpandoValue(
				request, "ExpandoAttribute--" + name + "--", type, displayType);

			attributes.put(name, value);
		}

		return attributes;
	}

	@Override
	public Map<String, Serializable> getExpandoBridgeAttributes(
			ExpandoBridge expandoBridge, PortletRequest portletRequest)
		throws PortalException {

		return getExpandoBridgeAttributes(
			expandoBridge, getHttpServletRequest(portletRequest));
	}

	@Override
	public Map<String, Serializable> getExpandoBridgeAttributes(
			ExpandoBridge expandoBridge,
			UploadPortletRequest uploadPortletRequest)
		throws PortalException {

		return getExpandoBridgeAttributes(
			expandoBridge, (HttpServletRequest)uploadPortletRequest);
	}

	@Override
	public Serializable getExpandoValue(
			HttpServletRequest request, String name, int type,
			String displayType)
		throws PortalException {

		Serializable value = null;

		if (type == ExpandoColumnConstants.BOOLEAN) {
			value = ParamUtil.getBoolean(request, name);
		}
		else if (type == ExpandoColumnConstants.BOOLEAN_ARRAY) {
		}
		else if (type == ExpandoColumnConstants.DATE) {
			int valueDateMonth = ParamUtil.getInteger(request, name + "Month");
			int valueDateDay = ParamUtil.getInteger(request, name + "Day");
			int valueDateYear = ParamUtil.getInteger(request, name + "Year");
			int valueDateHour = ParamUtil.getInteger(request, name + "Hour");
			int valueDateMinute = ParamUtil.getInteger(
				request, name + "Minute");
			int valueDateAmPm = ParamUtil.getInteger(request, name + "AmPm");

			if (valueDateAmPm == Calendar.PM) {
				valueDateHour += 12;
			}

			TimeZone timeZone = null;

			User user = getUser(request);

			if (user != null) {
				timeZone = user.getTimeZone();
			}

			value = getDate(
				valueDateMonth, valueDateDay, valueDateYear, valueDateHour,
				valueDateMinute, timeZone, ValueDataException.class);
		}
		else if (type == ExpandoColumnConstants.DATE_ARRAY) {
		}
		else if (type == ExpandoColumnConstants.DOUBLE) {
			value = ParamUtil.getDouble(request, name);
		}
		else if (type == ExpandoColumnConstants.DOUBLE_ARRAY) {
			String[] values = request.getParameterValues(name);

			if (displayType.equals(
					ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_TEXT_BOX) &&
				!ArrayUtil.isEmpty(values)) {

				values = StringUtil.splitLines(values[0]);
			}

			value = GetterUtil.getDoubleValues(values);
		}
		else if (type == ExpandoColumnConstants.FLOAT) {
			value = ParamUtil.getFloat(request, name);
		}
		else if (type == ExpandoColumnConstants.FLOAT_ARRAY) {
			String[] values = request.getParameterValues(name);

			if (displayType.equals(
					ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_TEXT_BOX) &&
				!ArrayUtil.isEmpty(values)) {

				values = StringUtil.splitLines(values[0]);
			}

			value = GetterUtil.getFloatValues(values);
		}
		else if (type == ExpandoColumnConstants.INTEGER) {
			value = ParamUtil.getInteger(request, name);
		}
		else if (type == ExpandoColumnConstants.INTEGER_ARRAY) {
			String[] values = request.getParameterValues(name);

			if (displayType.equals(
					ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_TEXT_BOX) &&
				!ArrayUtil.isEmpty(values)) {

				values = StringUtil.splitLines(values[0]);
			}

			value = GetterUtil.getIntegerValues(values);
		}
		else if (type == ExpandoColumnConstants.LONG) {
			value = ParamUtil.getLong(request, name);
		}
		else if (type == ExpandoColumnConstants.LONG_ARRAY) {
			String[] values = request.getParameterValues(name);

			if (displayType.equals(
					ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_TEXT_BOX) &&
				!ArrayUtil.isEmpty(values)) {

				values = StringUtil.splitLines(values[0]);
			}

			value = GetterUtil.getLongValues(values);
		}
		else if (type == ExpandoColumnConstants.NUMBER) {
			value = ParamUtil.getNumber(request, name);
		}
		else if (type == ExpandoColumnConstants.NUMBER_ARRAY) {
			String[] values = request.getParameterValues(name);

			if (displayType.equals(
					ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_TEXT_BOX) &&
				!ArrayUtil.isEmpty(values)) {

				values = StringUtil.splitLines(values[0]);
			}

			value = GetterUtil.getNumberValues(values);
		}
		else if (type == ExpandoColumnConstants.SHORT) {
			value = ParamUtil.getShort(request, name);
		}
		else if (type == ExpandoColumnConstants.SHORT_ARRAY) {
			String[] values = request.getParameterValues(name);

			if (displayType.equals(
					ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_TEXT_BOX) &&
				!ArrayUtil.isEmpty(values)) {

				values = StringUtil.splitLines(values[0]);
			}

			value = GetterUtil.getShortValues(values);
		}
		else if (type == ExpandoColumnConstants.STRING_ARRAY) {
			String[] values = request.getParameterValues(name);

			if (displayType.equals(
					ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_TEXT_BOX) &&
				!ArrayUtil.isEmpty(values)) {

				values = StringUtil.splitLines(values[0]);
			}

			value = values;
		}
		else if (type == ExpandoColumnConstants.STRING_LOCALIZED) {
			value = (Serializable)LocalizationUtil.getLocalizationMap(
				request, name);
		}
		else {
			value = ParamUtil.getString(request, name);
		}

		return value;
	}

	@Override
	public Serializable getExpandoValue(
			PortletRequest portletRequest, String name, int type,
			String displayType)
		throws PortalException {

		return getExpandoValue(
			getHttpServletRequest(portletRequest), name, type, displayType);
	}

	@Override
	public Serializable getExpandoValue(
			UploadPortletRequest uploadPortletRequest, String name, int type,
			String displayType)
		throws PortalException {

		return getExpandoValue(
			(HttpServletRequest)uploadPortletRequest, name, type, displayType);
	}

	@Override
	public String getFacebookURL(
			Portlet portlet, String facebookCanvasPageURL,
			ThemeDisplay themeDisplay)
		throws PortalException {

		String facebookURL = getServletURL(
			portlet, FacebookUtil.FACEBOOK_SERVLET_PATH + facebookCanvasPageURL,
			themeDisplay);

		if (!facebookURL.endsWith(StringPool.SLASH)) {
			facebookURL += StringPool.SLASH;
		}

		return facebookURL;
	}

	@Override
	public String getFirstPageLayoutTypes(HttpServletRequest request) {
		StringBundler sb = new StringBundler();

		for (String type : LayoutTypeControllerTracker.getTypes()) {
			LayoutTypeController layoutTypeController =
				LayoutTypeControllerTracker.getLayoutTypeController(type);

			if (layoutTypeController.isFirstPageable()) {
				sb.append(LanguageUtil.get(request, "layout.types." + type));
				sb.append(StringPool.COMMA);
				sb.append(StringPool.SPACE);
			}
		}

		if (sb.index() >= 2) {
			sb.setIndex(sb.index() - 2);
		}

		return sb.toString();
	}

	@Override
	public String getForwardedHost(HttpServletRequest request) {
		if (!PropsValues.WEB_SERVER_FORWARDED_HOST_ENABLED) {
			return request.getServerName();
		}

		return GetterUtil.get(
			request.getHeader(PropsValues.WEB_SERVER_FORWARDED_HOST_HEADER),
			request.getServerName());
	}

	@Override
	public int getForwardedPort(HttpServletRequest request) {
		if (!PropsValues.WEB_SERVER_FORWARDED_PORT_ENABLED) {
			return request.getServerPort();
		}

		return GetterUtil.getInteger(
			request.getHeader(PropsValues.WEB_SERVER_FORWARDED_PORT_HEADER),
			request.getServerPort());
	}

	@Override
	public String getFullName(
		String firstName, String middleName, String lastName) {

		FullNameGenerator fullNameGenerator =
			FullNameGeneratorFactory.getInstance();

		return fullNameGenerator.getFullName(firstName, middleName, lastName);
	}

	@Override
	public String getGlobalLibDir() {
		return PropsValues.LIFERAY_LIB_GLOBAL_DIR;
	}

	@Override
	public String getGoogleGadgetURL(Portlet portlet, ThemeDisplay themeDisplay)
		throws PortalException {

		return getServletURL(
			portlet, PropsValues.GOOGLE_GADGET_SERVLET_MAPPING, themeDisplay);
	}

	@Override
	public String getGroupFriendlyURL(
			LayoutSet layoutSet, ThemeDisplay themeDisplay)
		throws PortalException {

		return getGroupFriendlyURL(layoutSet, themeDisplay, false, false);
	}

	@Override
	public String getGroupFriendlyURL(
			LayoutSet layoutSet, ThemeDisplay themeDisplay, Locale locale)
		throws PortalException {

		String i18nLanguageId = themeDisplay.getI18nLanguageId();
		String i18nPath = themeDisplay.getI18nPath();
		Locale originalLocale = themeDisplay.getLocale();

		try {
			setThemeDisplayI18n(themeDisplay, locale);

			return getGroupFriendlyURL(layoutSet, themeDisplay);
		}
		finally {
			resetThemeDisplayI18n(
				themeDisplay, i18nLanguageId, i18nPath, originalLocale);
		}
	}

	@Override
	public int[] getGroupFriendlyURLIndex(String requestURI) {
		if (requestURI.startsWith(
				_PRIVATE_GROUP_SERVLET_MAPPING + StringPool.SLASH) ||
			requestURI.startsWith(
				_PRIVATE_USER_SERVLET_MAPPING + StringPool.SLASH) ||
			requestURI.startsWith(
				_PUBLIC_GROUP_SERVLET_MAPPING + StringPool.SLASH)) {

			int x = requestURI.indexOf(StringPool.SLASH, 1);

			int y = requestURI.indexOf(CharPool.SLASH, x + 1);

			if (y == -1) {

				// /web/alpha

				requestURI += StringPool.SLASH;

				y = requestURI.indexOf(CharPool.SLASH, x + 1);
			}

			return new int[] {x, y};
		}

		return null;
	}

	@Override
	public String[] getGroupPermissions(HttpServletRequest request) {
		return request.getParameterValues("groupPermissions");
	}

	@Override
	public String[] getGroupPermissions(
		HttpServletRequest request, String className) {

		String[] groupPermissions = request.getParameterValues(
			"groupPermissions_" + className);

		String inputPermissionsShowOptions = request.getParameter(
			"inputPermissionsShowOptions");

		return getGroupPermissions(
			groupPermissions, className, inputPermissionsShowOptions);
	}

	@Override
	public String[] getGroupPermissions(PortletRequest portletRequest) {
		return portletRequest.getParameterValues("groupPermissions");
	}

	@Override
	public String[] getGroupPermissions(
		PortletRequest portletRequest, String className) {

		String[] groupPermissions = portletRequest.getParameterValues(
			"groupPermissions_" + className);

		String inputPermissionsShowOptions = portletRequest.getParameter(
			"inputPermissionsShowOptions");

		return getGroupPermissions(
			groupPermissions, className, inputPermissionsShowOptions);
	}

	@Override
	public String[] getGuestPermissions(HttpServletRequest request) {
		return request.getParameterValues("guestPermissions");
	}

	@Override
	public String[] getGuestPermissions(
		HttpServletRequest request, String className) {

		String[] guestPermissions = request.getParameterValues(
			"guestPermissions_" + className);

		String inputPermissionsShowOptions = request.getParameter(
			"inputPermissionsShowOptions");

		return getGuestPermissions(
			guestPermissions, className, inputPermissionsShowOptions);
	}

	@Override
	public String[] getGuestPermissions(PortletRequest portletRequest) {
		return portletRequest.getParameterValues("guestPermissions");
	}

	@Override
	public String[] getGuestPermissions(
		PortletRequest portletRequest, String className) {

		String[] guestPermissions = portletRequest.getParameterValues(
			"guestPermissions_" + className);

		String inputPermissionsShowOptions = portletRequest.getParameter(
			"inputPermissionsShowOptions");

		return getGuestPermissions(
			guestPermissions, className, inputPermissionsShowOptions);
	}

	@Override
	public String getHomeURL(HttpServletRequest request)
		throws PortalException {

		String portalURL = getPortalURL(request);

		return portalURL + _pathContext + getRelativeHomeURL(request);
	}

	@Override
	public String getHost(HttpServletRequest request) {
		request = getOriginalServletRequest(request);

		String host = request.getHeader("Host");

		if (host != null) {
			host = StringUtil.toLowerCase(host.trim());

			int pos = host.indexOf(':');

			if (pos >= 0) {
				host = host.substring(0, pos);
			}
		}

		return host;
	}

	@Override
	public String getHost(PortletRequest portletRequest) {
		return getHost(getHttpServletRequest(portletRequest));
	}

	@Override
	public HttpServletRequest getHttpServletRequest(
		PortletRequest portletRequest) {

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)portletRequest.getAttribute(
				PortletServlet.PORTLET_SERVLET_REQUEST);

		if (httpServletRequest != null) {
			return httpServletRequest;
		}

		if (portletRequest instanceof LiferayPortletRequest) {
			LiferayPortletRequest liferayPortletRequest =
				(LiferayPortletRequest)portletRequest;

			return liferayPortletRequest.getHttpServletRequest();
		}

		PortletRequestImpl portletRequestImpl =
			PortletRequestImpl.getPortletRequestImpl(portletRequest);

		return portletRequestImpl.getHttpServletRequest();
	}

	@Override
	public HttpServletResponse getHttpServletResponse(
		PortletResponse portletResponse) {

		if (portletResponse instanceof LiferayPortletResponse) {
			LiferayPortletResponse liferayPortletResponse =
				(LiferayPortletResponse)portletResponse;

			return liferayPortletResponse.getHttpServletResponse();
		}

		PortletResponseImpl portletResponseImpl =
			PortletResponseImpl.getPortletResponseImpl(portletResponse);

		return portletResponseImpl.getHttpServletResponse();
	}

	@Override
	public String getI18nPathLanguageId(
		Locale locale, String defaultI18nPathLanguageId) {

		String i18nPathLanguageId = defaultI18nPathLanguageId;

		if (!LanguageUtil.isDuplicateLanguageCode(locale.getLanguage())) {
			i18nPathLanguageId = locale.getLanguage();
		}
		else {
			Locale priorityLocale = LanguageUtil.getLocale(
				locale.getLanguage());

			if (locale.equals(priorityLocale)) {
				i18nPathLanguageId = locale.getLanguage();
			}
		}

		return i18nPathLanguageId;
	}

	/**
	 * @deprecated As of 7.0.0, with no direct replacement
	 */
	@Deprecated
	@Override
	public String getJournalArticleActualURL(
			long groupId, boolean privateLayout, String mainPath,
			String friendlyURL, Map<String, String[]> params,
			Map<String, Object> requestContext)
		throws PortalException {

		FriendlyURLResolver friendlyURLResolver =
			FriendlyURLResolverRegistryUtil.getFriendlyURLResolver(
				_JOURNAL_ARTICLE_CANONICAL_URL_SEPARATOR);

		if (friendlyURLResolver == null) {
			return null;
		}

		HttpServletRequest request = (HttpServletRequest)requestContext.get(
			"request");

		long companyId = PortalInstances.getCompanyId(request);

		return friendlyURLResolver.getActualURL(
			companyId, groupId, privateLayout, mainPath, friendlyURL, params,
			requestContext);
	}

	/**
	 * @deprecated As of 7.0.0, with no direct replacement
	 */
	@Deprecated
	@Override
	public Layout getJournalArticleLayout(
			long groupId, boolean privateLayout, String friendlyURL)
		throws PortalException {

		FriendlyURLResolver friendlyURLResolver =
			FriendlyURLResolverRegistryUtil.getFriendlyURLResolver(
				_JOURNAL_ARTICLE_CANONICAL_URL_SEPARATOR);

		if (friendlyURLResolver == null) {
			return null;
		}

		LayoutFriendlyURLComposite layoutFriendlyURLComposite =
			friendlyURLResolver.getLayoutFriendlyURLComposite(
				0, groupId, privateLayout, friendlyURL,
				new HashMap<String, String[]>(), new HashMap<String, Object>());

		return layoutFriendlyURLComposite.getLayout();
	}

	@Override
	public String getJsSafePortletId(String portletId) {
		return JS.getSafeName(portletId);
	}

	@Override
	public String getLayoutActualURL(Layout layout) {
		return getLayoutActualURL(layout, getPathMain());
	}

	@Override
	public String getLayoutActualURL(Layout layout, String mainPath) {
		Map<String, String> variables = new HashMap<>();

		layout = getBrowsableLayout(layout);

		variables.put("liferay:groupId", String.valueOf(layout.getGroupId()));

		variables.put("liferay:mainPath", mainPath);
		variables.put("liferay:plid", String.valueOf(layout.getPlid()));

		if (layout instanceof VirtualLayout) {
			variables.put(
				"liferay:pvlsgid", String.valueOf(layout.getGroupId()));
		}
		else {
			variables.put("liferay:pvlsgid", "0");
		}

		UnicodeProperties typeSettingsProperties =
			layout.getTypeSettingsProperties();

		variables.putAll(typeSettingsProperties);

		LayoutTypeController layoutTypeController =
			LayoutTypeControllerTracker.getLayoutTypeController(
				layout.getType());

		return LayoutTypeImpl.getURL(layoutTypeController.getURL(), variables);
	}

	@Override
	public String getLayoutActualURL(
			long groupId, boolean privateLayout, String mainPath,
			String friendlyURL)
		throws PortalException {

		return getLayoutActualURL(
			groupId, privateLayout, mainPath, friendlyURL, null, null);
	}

	@Override
	public String getLayoutActualURL(
			long groupId, boolean privateLayout, String mainPath,
			String friendlyURL, Map<String, String[]> params,
			Map<String, Object> requestContext)
		throws PortalException {

		LayoutQueryStringComposite actualLayoutQueryStringComposite =
			getActualLayoutQueryStringComposite(
				groupId, privateLayout, friendlyURL, params, requestContext);

		Layout layout = actualLayoutQueryStringComposite.getLayout();
		String queryString = actualLayoutQueryStringComposite.getQueryString();

		String layoutActualURL = getLayoutActualURL(layout, mainPath);

		if (Validator.isNotNull(queryString)) {
			layoutActualURL = layoutActualURL.concat(queryString);
		}
		else if (params.isEmpty()) {
			UnicodeProperties typeSettingsProperties =
				layout.getTypeSettingsProperties();

			queryString = typeSettingsProperties.getProperty("query-string");

			if (Validator.isNotNull(queryString) &&
				layoutActualURL.contains(StringPool.QUESTION)) {

				layoutActualURL = layoutActualURL.concat(
					StringPool.AMPERSAND).concat(queryString);
			}
		}

		return layoutActualURL;
	}

	@Override
	public String getLayoutFriendlyURL(Layout layout, ThemeDisplay themeDisplay)
		throws PortalException {

		if (themeDisplay.getLayout() == layout) {
			return getLayoutFriendlyURL(themeDisplay);
		}

		LayoutTypeController layoutTypeController =
			LayoutTypeControllerTracker.getLayoutTypeController(
				layout.getType());

		if (!layoutTypeController.isURLFriendliable()) {
			return null;
		}

		LayoutSet layoutSet = themeDisplay.getLayoutSet();

		if ((layoutSet == null) ||
			(layoutSet.getGroupId() != layout.getGroupId()) ||
			(layoutSet.isPrivateLayout() != layout.isPrivateLayout())) {

			layoutSet = layout.getLayoutSet();
		}

		String groupFriendlyURL = getGroupFriendlyURL(
			layoutSet, themeDisplay, false, layout.isTypeControlPanel());

		return groupFriendlyURL.concat(
			themeDisplay.getLayoutFriendlyURL(layout));
	}

	@Override
	public String getLayoutFriendlyURL(
			Layout layout, ThemeDisplay themeDisplay, Locale locale)
		throws PortalException {

		String i18nLanguageId = themeDisplay.getI18nLanguageId();
		String i18nPath = themeDisplay.getI18nPath();
		Locale originalLocale = themeDisplay.getLocale();

		try {
			setThemeDisplayI18n(themeDisplay, locale);

			return getLayoutFriendlyURL(layout, themeDisplay);
		}
		finally {
			resetThemeDisplayI18n(
				themeDisplay, i18nLanguageId, i18nPath, originalLocale);
		}
	}

	@Override
	public String getLayoutFriendlyURL(ThemeDisplay themeDisplay)
		throws PortalException {

		Layout layout = themeDisplay.getLayout();

		LayoutTypeController layoutTypeController =
			LayoutTypeControllerTracker.getLayoutTypeController(
				layout.getType());

		if (!layoutTypeController.isURLFriendliable()) {
			return null;
		}

		Group group = themeDisplay.getSiteGroup();

		LayoutSet layoutSet = themeDisplay.getLayoutSet();

		if (group.getGroupId() != layoutSet.getGroupId()) {
			group = layoutSet.getGroup();
		}

		String groupFriendlyURL = _getGroupFriendlyURL(
			group, themeDisplay.getLayoutSet(), themeDisplay, false,
			layout.isTypeControlPanel());

		return groupFriendlyURL.concat(
			themeDisplay.getLayoutFriendlyURL(layout));
	}

	/**
	 * @deprecated As of 7.0.0, replaced by {@link
	 *             #getLayoutFriendlyURLSeparatorComposite(long, boolean,
	 *             String, Map<String, String[]>, Map<String, Object>)}
	 */
	@Deprecated
	@Override
	public LayoutFriendlyURLComposite getLayoutFriendlyURLComposite(
			long groupId, boolean privateLayout, String friendlyURL,
			Map<String, String[]> params, Map<String, Object> requestContext)
		throws PortalException {

		LayoutFriendlyURLSeparatorComposite
			layoutFriendlyURLSeparatorComposite =
				getLayoutFriendlyURLSeparatorComposite(
					groupId, privateLayout, friendlyURL, params,
					requestContext);

		return (LayoutFriendlyURLComposite)layoutFriendlyURLSeparatorComposite;
	}

	@Override
	public LayoutFriendlyURLSeparatorComposite
			getLayoutFriendlyURLSeparatorComposite(
				long groupId, boolean privateLayout, String friendlyURL,
				Map<String, String[]> params,
				Map<String, Object> requestContext)
		throws PortalException {

		LayoutFriendlyURLSeparatorComposite
			layoutFriendlyURLSeparatorComposite = null;

		if (friendlyURL != null) {
			HttpServletRequest request = (HttpServletRequest)requestContext.get(
				"request");

			long companyId = PortalInstances.getCompanyId(request);

			Collection<FriendlyURLResolver> friendlyURLResolvers =
				FriendlyURLResolverRegistryUtil.
					getFriendlyURLResolversAsCollection();

			for (FriendlyURLResolver friendlyURLResolver :
					friendlyURLResolvers) {

				if (!friendlyURL.startsWith(
						friendlyURLResolver.getURLSeparator())) {

					continue;
				}

				try {
					layoutFriendlyURLSeparatorComposite =
						friendlyURLResolver.
							getLayoutFriendlyURLSeparatorComposite(
								companyId, groupId, privateLayout, friendlyURL,
								params, requestContext);

					break;
				}
				catch (Exception e) {
					throw new NoSuchLayoutException(e);
				}
			}
		}

		if (layoutFriendlyURLSeparatorComposite != null) {
			return layoutFriendlyURLSeparatorComposite;
		}

		LayoutQueryStringComposite layoutQueryStringComposite =
			getActualLayoutQueryStringComposite(
				groupId, privateLayout, friendlyURL, params, requestContext);

		return new LayoutFriendlyURLSeparatorComposite(
			layoutQueryStringComposite.getLayout(),
			layoutQueryStringComposite.getFriendlyURL(),
			FRIENDLY_URL_SEPARATOR);
	}

	@Override
	public String getLayoutFullURL(Layout layout, ThemeDisplay themeDisplay)
		throws PortalException {

		return getLayoutFullURL(layout, themeDisplay, true);
	}

	@Override
	public String getLayoutFullURL(
			Layout layout, ThemeDisplay themeDisplay, boolean doAsUser)
		throws PortalException {

		String layoutURL = getLayoutURL(layout, themeDisplay, doAsUser);

		if (!HttpUtil.hasProtocol(layoutURL)) {
			layoutURL = getPortalURL(layout, themeDisplay) + layoutURL;
		}

		return layoutURL;
	}

	@Override
	public String getLayoutFullURL(long groupId, String portletId)
		throws PortalException {

		return getLayoutFullURL(groupId, portletId, false);
	}

	@Override
	public String getLayoutFullURL(
			long groupId, String portletId, boolean secure)
		throws PortalException {

		long plid = getPlidFromPortletId(groupId, portletId);

		if (plid == LayoutConstants.DEFAULT_PLID) {
			return null;
		}

		StringBundler sb = new StringBundler(4);

		Layout layout = LayoutLocalServiceUtil.getLayout(plid);

		Group group = GroupLocalServiceUtil.getGroup(groupId);

		if (group.isLayout()) {
			long parentGroupId = group.getParentGroupId();

			if (parentGroupId > 0) {
				group = GroupLocalServiceUtil.getGroup(parentGroupId);
			}
		}

		String virtualHostname = null;

		LayoutSet layoutSet = layout.getLayoutSet();

		if (Validator.isNotNull(layoutSet.getVirtualHostname())) {
			virtualHostname = layoutSet.getVirtualHostname();
		}
		else {
			Company company = CompanyLocalServiceUtil.getCompany(
				layout.getCompanyId());

			virtualHostname = company.getVirtualHostname();
		}

		String portalURL = getPortalURL(
			virtualHostname, getPortalServerPort(secure), secure);

		sb.append(portalURL);

		if (layout.isPrivateLayout()) {
			if (group.isUser()) {
				sb.append(getPathFriendlyURLPrivateUser());
			}
			else {
				sb.append(getPathFriendlyURLPrivateGroup());
			}
		}
		else {
			sb.append(getPathFriendlyURLPublic());
		}

		sb.append(group.getFriendlyURL());
		sb.append(layout.getFriendlyURL());

		return sb.toString();
	}

	@Override
	public String getLayoutFullURL(ThemeDisplay themeDisplay)
		throws PortalException {

		return getLayoutFullURL(themeDisplay.getLayout(), themeDisplay);
	}

	@Override
	public String getLayoutRelativeURL(Layout layout, ThemeDisplay themeDisplay)
		throws PortalException {

		return getLayoutRelativeURL(layout, themeDisplay, true);
	}

	@Override
	public String getLayoutRelativeURL(
			Layout layout, ThemeDisplay themeDisplay, boolean doAsUser)
		throws PortalException {

		String layoutFullURL = getLayoutFullURL(layout, themeDisplay, doAsUser);

		return HttpUtil.removeDomain(layoutFullURL);
	}

	@Override
	public String getLayoutSetDisplayURL(
			LayoutSet layoutSet, boolean secureConnection)
		throws PortalException {

		Company company = CompanyLocalServiceUtil.getCompany(
			layoutSet.getCompanyId());
		int portalPort = getPortalServerPort(secureConnection);

		String portalURL = getPortalURL(
			company.getVirtualHostname(), portalPort, secureConnection);

		String virtualHostname = getVirtualHostname(layoutSet);

		if (Validator.isNotNull(virtualHostname) &&
			!StringUtil.equalsIgnoreCase(virtualHostname, "localhost")) {

			int index = portalURL.indexOf("://");

			String portalDomain = portalURL.substring(index + 3);

			virtualHostname = getCanonicalDomain(virtualHostname, portalDomain);

			virtualHostname = getPortalURL(
				virtualHostname, portalPort, secureConnection);

			if (virtualHostname.contains(portalDomain)) {
				return virtualHostname.concat(getPathContext());
			}
		}

		StringBundler sb = new StringBundler(4);

		sb.append(portalURL);
		sb.append(getPathContext());

		Group group = layoutSet.getGroup();

		String friendlyURL = null;

		if (layoutSet.isPrivateLayout()) {
			if (group.isUser()) {
				friendlyURL = _PRIVATE_USER_SERVLET_MAPPING;
			}
			else {
				friendlyURL = _PRIVATE_GROUP_SERVLET_MAPPING;
			}
		}
		else {
			friendlyURL = _PUBLIC_GROUP_SERVLET_MAPPING;
		}

		sb.append(friendlyURL);

		sb.append(group.getFriendlyURL());

		return sb.toString();
	}

	@Override
	public String getLayoutSetFriendlyURL(
			LayoutSet layoutSet, ThemeDisplay themeDisplay)
		throws PortalException {

		String virtualHostname = getVirtualHostname(layoutSet);

		if (Validator.isNotNull(virtualHostname) &&
			!StringUtil.equalsIgnoreCase(virtualHostname, _LOCALHOST)) {

			String portalURL = getPortalURL(
				virtualHostname, themeDisplay.getServerPort(),
				themeDisplay.isSecure());

			// Use the layout set's virtual host setting only if the layout set
			// is already used for the current request

			Layout curLayout = themeDisplay.getLayout();

			LayoutSet curLayoutSet = curLayout.getLayoutSet();

			long curLayoutSetId = curLayoutSet.getLayoutSetId();

			if ((layoutSet.getLayoutSetId() != curLayoutSetId) ||
				portalURL.startsWith(themeDisplay.getURLPortal())) {

				String layoutSetFriendlyURL = portalURL + _pathContext;

				if (themeDisplay.isI18n()) {
					layoutSetFriendlyURL += themeDisplay.getI18nPath();
				}

				return addPreservedParameters(
					themeDisplay, layoutSetFriendlyURL);
			}
		}

		Group group = GroupLocalServiceUtil.getGroup(layoutSet.getGroupId());

		String friendlyURL = null;

		if (layoutSet.isPrivateLayout()) {
			if (group.isUser()) {
				friendlyURL = _PRIVATE_USER_SERVLET_MAPPING;
			}
			else {
				friendlyURL = _PRIVATE_GROUP_SERVLET_MAPPING;
			}
		}
		else {
			friendlyURL = _PUBLIC_GROUP_SERVLET_MAPPING;
		}

		if (themeDisplay.isI18n()) {
			friendlyURL = themeDisplay.getI18nPath() + friendlyURL;
		}

		String layoutSetFriendlyURL =
			_pathContext + friendlyURL + group.getFriendlyURL();

		return addPreservedParameters(themeDisplay, layoutSetFriendlyURL);
	}

	@Override
	public String getLayoutTarget(Layout layout) {
		UnicodeProperties typeSettingsProps =
			layout.getTypeSettingsProperties();

		String target = typeSettingsProps.getProperty("target");

		if (Validator.isNull(target)) {
			target = StringPool.BLANK;
		}
		else {
			target = "target=\"" + HtmlUtil.escapeAttribute(target) + "\"";
		}

		return target;
	}

	@Override
	public String getLayoutURL(Layout layout, ThemeDisplay themeDisplay)
		throws PortalException {

		return getLayoutURL(layout, themeDisplay, true);
	}

	@Override
	public String getLayoutURL(
			Layout layout, ThemeDisplay themeDisplay, boolean doAsUser)
		throws PortalException {

		if (layout == null) {
			return themeDisplay.getPathMain() + PATH_PORTAL_LAYOUT;
		}

		if (layout.isTypeURL()) {
			return getLayoutActualURL(layout);
		}

		String layoutFriendlyURL = getLayoutFriendlyURL(layout, themeDisplay);

		if (Validator.isNotNull(layoutFriendlyURL)) {
			layoutFriendlyURL = addPreservedParameters(
				themeDisplay, layout, layoutFriendlyURL, doAsUser);

			return layoutFriendlyURL;
		}

		String layoutURL = getLayoutActualURL(layout);

		layoutURL = addPreservedParameters(
			themeDisplay, layout, layoutURL, doAsUser);

		return layoutURL;
	}

	@Override
	public String getLayoutURL(
			Layout layout, ThemeDisplay themeDisplay, Locale locale)
		throws PortalException {

		String i18nLanguageId = themeDisplay.getI18nLanguageId();
		String i18nPath = themeDisplay.getI18nPath();
		Locale originalLocale = themeDisplay.getLocale();

		try {
			setThemeDisplayI18n(themeDisplay, locale);

			return getLayoutURL(layout, themeDisplay, true);
		}
		finally {
			resetThemeDisplayI18n(
				themeDisplay, i18nLanguageId, i18nPath, originalLocale);
		}
	}

	@Override
	public String getLayoutURL(ThemeDisplay themeDisplay)
		throws PortalException {

		return getLayoutURL(themeDisplay.getLayout(), themeDisplay);
	}

	@Override
	public LiferayPortletRequest getLiferayPortletRequest(
		PortletRequest portletRequest) {

		if (portletRequest instanceof LiferayPortletRequest) {
			return (LiferayPortletRequest)portletRequest;
		}

		PortletRequestImpl portletRequestImpl =
			PortletRequestImpl.getPortletRequestImpl(portletRequest);

		return DoPrivilegedUtil.wrapWhenActive(portletRequestImpl);
	}

	@Override
	public LiferayPortletResponse getLiferayPortletResponse(
		PortletResponse portletResponse) {

		if (portletResponse instanceof LiferayPortletResponse) {
			return (LiferayPortletResponse)portletResponse;
		}

		PortletResponseImpl portletResponseImpl =
			PortletResponseImpl.getPortletResponseImpl(portletResponse);

		return DoPrivilegedUtil.wrapWhenActive(portletResponseImpl);
	}

	@Override
	public Locale getLocale(HttpServletRequest request) {
		Locale locale = (Locale)request.getAttribute(WebKeys.LOCALE);

		if (locale == _NULL_LOCALE) {
			return null;
		}

		if (locale == null) {
			locale = getLocale(request, null, false);

			if (locale == null) {
				request.setAttribute(WebKeys.LOCALE, _NULL_LOCALE);
			}
			else {
				request.setAttribute(WebKeys.LOCALE, locale);
			}
		}

		return locale;
	}

	@Override
	public Locale getLocale(
		HttpServletRequest request, HttpServletResponse response,
		boolean initialize) {

		User user = null;

		if (initialize) {
			try {
				user = initUser(request);
			}
			catch (NoSuchUserException nsue) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(nsue, nsue);
				}

				return null;
			}
			catch (Exception e) {
			}
		}

		Locale locale = null;

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (themeDisplay != null) {
			locale = themeDisplay.getLocale();

			if (LanguageUtil.isAvailableLocale(
					themeDisplay.getSiteGroupId(), locale)) {

				return locale;
			}
		}

		long groupId = 0;

		Layout layout = (Layout)request.getAttribute(WebKeys.LAYOUT);

		if ((layout != null) && !layout.isTypeControlPanel()) {
			try {
				long scopeGroupId = getScopeGroupId(request);

				groupId = getSiteGroupId(scopeGroupId);
			}
			catch (Exception e) {
			}
		}

		String i18nLanguageId = (String)request.getAttribute(
			WebKeys.I18N_LANGUAGE_ID);

		if (Validator.isNotNull(i18nLanguageId)) {
			locale = LocaleUtil.fromLanguageId(i18nLanguageId, true, false);

			if (LanguageUtil.isAvailableLocale(groupId, locale)) {
				return locale;
			}
			else if (groupId > 0) {
				boolean inheritLocales = true;

				try {
					inheritLocales = LanguageUtil.isInheritLocales(groupId);
				}
				catch (PortalException pe) {
					_log.error(
						"Unable to check if group " + groupId +
							" inherits locales",
						pe);
				}

				if (!inheritLocales) {
					String i18nLanguageCode = (String)request.getAttribute(
						WebKeys.I18N_LANGUAGE_CODE);

					locale = LanguageUtil.getLocale(groupId, i18nLanguageCode);

					if (LanguageUtil.isAvailableLocale(groupId, locale)) {
						return locale;
					}
				}
			}
		}

		String doAsUserLanguageId = ParamUtil.getString(
			request, "doAsUserLanguageId");

		if (Validator.isNotNull(doAsUserLanguageId)) {
			locale = LocaleUtil.fromLanguageId(doAsUserLanguageId);

			if (LanguageUtil.isAvailableLocale(groupId, locale)) {
				return locale;
			}
		}

		HttpSession session = request.getSession(false);

		if (session != null) {
			locale = (Locale)session.getAttribute(Globals.LOCALE_KEY);

			if (LanguageUtil.isAvailableLocale(groupId, locale)) {
				return locale;
			}
		}

		// Get locale from the user

		if (user == null) {
			try {
				user = getUser(request);
			}
			catch (Exception e) {
			}
		}

		if ((user != null) && !user.isDefaultUser()) {
			Locale userLocale = getAvailableLocale(groupId, user.getLocale());

			if (LanguageUtil.isAvailableLocale(groupId, userLocale)) {
				if (initialize) {
					setLocale(request, response, userLocale);
				}

				return userLocale;
			}
		}

		// Get locale from the cookie

		String languageId = CookieKeys.getCookie(
			request, CookieKeys.GUEST_LANGUAGE_ID, false);

		if (Validator.isNotNull(languageId)) {
			Locale cookieLocale = getAvailableLocale(
				groupId, LocaleUtil.fromLanguageId(languageId));

			if (LanguageUtil.isAvailableLocale(groupId, cookieLocale)) {
				if (initialize) {
					setLocale(request, response, cookieLocale);
				}

				return cookieLocale;
			}
		}

		// Get locale from the request

		if (PropsValues.LOCALE_DEFAULT_REQUEST) {
			Enumeration<Locale> locales = request.getLocales();

			while (locales.hasMoreElements()) {
				Locale requestLocale = getAvailableLocale(
					groupId, locales.nextElement());

				if (LanguageUtil.isAvailableLocale(groupId, requestLocale)) {
					if (initialize) {
						setLocale(request, response, requestLocale);
					}

					return requestLocale;
				}
			}
		}

		// Get locale from the group

		if (groupId > 0) {
			try {
				Group group = GroupLocalServiceUtil.getGroup(groupId);

				UnicodeProperties typeSettingsProperties =
					group.getTypeSettingsProperties();

				String defaultLanguageId = typeSettingsProperties.getProperty(
					"languageId");

				if (Validator.isNotNull(defaultLanguageId)) {
					locale = LocaleUtil.fromLanguageId(defaultLanguageId);

					if (LanguageUtil.isAvailableLocale(groupId, locale)) {
						if (initialize) {
							setLocale(request, response, locale);
						}

						return locale;
					}
				}
			}
			catch (Exception e) {
			}
		}

		// Get locale from the default user

		Company company = null;

		try {
			company = getCompany(request);
		}
		catch (Exception e) {
		}

		if (company == null) {
			return null;
		}

		User defaultUser = null;

		try {
			defaultUser = company.getDefaultUser();
		}
		catch (Exception e) {
		}

		if (defaultUser == null) {
			return null;
		}

		Locale defaultUserLocale = getAvailableLocale(
			groupId, defaultUser.getLocale());

		if (LanguageUtil.isAvailableLocale(groupId, defaultUserLocale)) {
			if (initialize) {
				setLocale(request, response, defaultUserLocale);
			}

			return defaultUserLocale;
		}

		try {
			if (themeDisplay != null) {
				return themeDisplay.getSiteDefaultLocale();
			}
		}
		catch (Exception e) {
		}

		try {
			return getSiteDefaultLocale(groupId);
		}
		catch (Exception e) {
			return LocaleUtil.getDefault();
		}
	}

	@Override
	public Locale getLocale(PortletRequest portletRequest) {
		return getLocale(getHttpServletRequest(portletRequest));
	}

	@Override
	public String getLocalizedFriendlyURL(
			HttpServletRequest request, Layout layout, Locale locale,
			Locale originalLocale)
		throws Exception {

		String contextPath = getPathContext();

		String requestURI = request.getRequestURI();

		if (Validator.isNotNull(contextPath) &&
			requestURI.contains(contextPath)) {

			requestURI = requestURI.substring(contextPath.length());
		}

		requestURI = StringUtil.replace(
			requestURI, StringPool.DOUBLE_SLASH, StringPool.SLASH);

		String path = request.getPathInfo();

		int x = path.indexOf(CharPool.SLASH, 1);

		String layoutFriendlyURL = null;

		if (originalLocale == null) {
			if ((x != -1) && ((x + 1) != path.length())) {
				layoutFriendlyURL = path.substring(x);
			}

			int y = layoutFriendlyURL.indexOf(
				VirtualLayoutConstants.CANONICAL_URL_SEPARATOR);

			if (y != -1) {
				y = layoutFriendlyURL.indexOf(CharPool.SLASH, 3);

				if ((y != -1) && ((y + 1) != layoutFriendlyURL.length())) {
					layoutFriendlyURL = layoutFriendlyURL.substring(y);
				}
			}

			y = layoutFriendlyURL.indexOf(Portal.FRIENDLY_URL_SEPARATOR);

			if (y != -1) {
				layoutFriendlyURL = layoutFriendlyURL.substring(0, y);
			}
		}
		else {
			layoutFriendlyURL = layout.getFriendlyURL(originalLocale);
		}

		if (requestURI.contains(layoutFriendlyURL)) {
			requestURI = StringUtil.replaceFirst(
				requestURI, layoutFriendlyURL, layout.getFriendlyURL(locale));
		}

		String i18nPath =
			StringPool.SLASH +
				getI18nPathLanguageId(locale, LocaleUtil.toLanguageId(locale));

		boolean appendI18nPath = true;

		if ((PropsValues.LOCALE_PREPEND_FRIENDLY_URL_STYLE == 0) ||
			((PropsValues.LOCALE_PREPEND_FRIENDLY_URL_STYLE == 1) &&
			 locale.equals(LocaleUtil.getDefault()))) {

			appendI18nPath = false;
		}

		String localizedFriendlyURL = contextPath;

		if (appendI18nPath) {
			localizedFriendlyURL += i18nPath;
		}

		localizedFriendlyURL += requestURI;

		String queryString = request.getQueryString();

		if (Validator.isNull(queryString)) {
			queryString = (String)request.getAttribute(
				JavaConstants.JAVAX_SERVLET_FORWARD_QUERY_STRING);
		}

		if (Validator.isNotNull(queryString)) {
			localizedFriendlyURL += StringPool.QUESTION + queryString;
		}

		return localizedFriendlyURL;
	}

	@Override
	public String getMailId(String mx, String popPortletPrefix, Object... ids) {
		StringBundler sb = new StringBundler(ids.length * 2 + 7);

		sb.append(StringPool.LESS_THAN);
		sb.append(popPortletPrefix);

		if (!popPortletPrefix.endsWith(StringPool.PERIOD)) {
			sb.append(StringPool.PERIOD);
		}

		for (int i = 0; i < ids.length; i++) {
			Object id = ids[i];

			if (i != 0) {
				sb.append(StringPool.PERIOD);
			}

			sb.append(id);
		}

		sb.append(StringPool.AT);

		if (Validator.isNotNull(PropsValues.POP_SERVER_SUBDOMAIN)) {
			sb.append(PropsValues.POP_SERVER_SUBDOMAIN);
			sb.append(StringPool.PERIOD);
		}

		sb.append(mx);
		sb.append(StringPool.GREATER_THAN);

		return sb.toString();
	}

	@Override
	public String getNetvibesURL(Portlet portlet, ThemeDisplay themeDisplay)
		throws PortalException {

		return getServletURL(
			portlet, PropsValues.NETVIBES_SERVLET_MAPPING, themeDisplay);
	}

	@Override
	public String getNewPortletTitle(
		String portletTitle, String oldScopeName, String newScopeName) {

		if (portletTitle.endsWith(" (" + oldScopeName + ")")) {
			int pos = portletTitle.lastIndexOf(" (" + oldScopeName + ")");

			portletTitle = portletTitle.substring(0, pos);
		}

		if (Validator.isNull(newScopeName)) {
			return portletTitle;
		}

		return StringUtil.appendParentheticalSuffix(portletTitle, newScopeName);
	}

	@Override
	public HttpServletRequest getOriginalServletRequest(
		HttpServletRequest request) {

		HttpServletRequest currentRequest = request;
		HttpServletRequestWrapper currentRequestWrapper = null;
		HttpServletRequest originalRequest = null;

		while (currentRequest instanceof HttpServletRequestWrapper) {
			if (currentRequest instanceof
					PersistentHttpServletRequestWrapper) {

				PersistentHttpServletRequestWrapper
					persistentHttpServletRequestWrapper =
						(PersistentHttpServletRequestWrapper)currentRequest;

				persistentHttpServletRequestWrapper =
					persistentHttpServletRequestWrapper.clone();

				if (originalRequest == null) {
					originalRequest = persistentHttpServletRequestWrapper;
				}

				if (currentRequestWrapper != null) {
					currentRequestWrapper.setRequest(
						persistentHttpServletRequestWrapper);
				}

				currentRequestWrapper = persistentHttpServletRequestWrapper;
			}

			// Get original request so that portlets inside portlets render
			// properly

			HttpServletRequestWrapper httpServletRequestWrapper =
				(HttpServletRequestWrapper)currentRequest;

			currentRequest =
				(HttpServletRequest)httpServletRequestWrapper.getRequest();
		}

		if (currentRequestWrapper != null) {
			currentRequestWrapper.setRequest(currentRequest);
		}

		if (originalRequest != null) {
			return originalRequest;
		}

		return currentRequest;
	}

	@Override
	public String getPathContext() {
		return _pathContext;
	}

	@Override
	public String getPathContext(HttpServletRequest request) {
		return getPathContext(request.getContextPath());
	}

	@Override
	public String getPathContext(PortletRequest portletRequest) {
		return getPathContext(portletRequest.getContextPath());
	}

	@Override
	public String getPathContext(String contextPath) {
		return _pathProxy.concat(getContextPath(contextPath));
	}

	@Override
	public String getPathFriendlyURLPrivateGroup() {
		return _pathFriendlyURLPrivateGroup;
	}

	@Override
	public String getPathFriendlyURLPrivateUser() {
		return _pathFriendlyURLPrivateUser;
	}

	@Override
	public String getPathFriendlyURLPublic() {
		return _pathFriendlyURLPublic;
	}

	@Override
	public String getPathImage() {
		return _pathImage;
	}

	@Override
	public String getPathMain() {
		return _pathMain;
	}

	@Override
	public String getPathModule() {
		return _pathModule;
	}

	@Override
	public String getPathProxy() {
		return _pathProxy;
	}

	@Override
	public long getPlidFromFriendlyURL(long companyId, String friendlyURL) {
		if (Validator.isNull(friendlyURL) ||
			friendlyURL.equals(StringPool.SLASH)) {

			return LayoutConstants.DEFAULT_PLID;
		}

		String[] urlParts = friendlyURL.split("\\/", 4);

		if ((friendlyURL.charAt(0) != CharPool.SLASH) &&
			(urlParts.length != 4)) {

			return LayoutConstants.DEFAULT_PLID;
		}

		boolean privateLayout = true;

		String urlPrefix = StringPool.SLASH + urlParts[1];

		if (_PUBLIC_GROUP_SERVLET_MAPPING.equals(urlPrefix)) {
			privateLayout = false;
		}
		else if (_PRIVATE_GROUP_SERVLET_MAPPING.equals(urlPrefix) ||
				 _PRIVATE_USER_SERVLET_MAPPING.equals(urlPrefix)) {

			privateLayout = true;
		}
		else {
			return LayoutConstants.DEFAULT_PLID;
		}

		Group group = null;

		try {
			group = GroupLocalServiceUtil.getFriendlyURLGroup(
				companyId, StringPool.SLASH + urlParts[2]);
		}
		catch (Exception e) {
		}

		if (group == null) {
			return LayoutConstants.DEFAULT_PLID;
		}

		Layout layout = null;

		try {
			String layoutFriendlyURL = null;

			if (urlParts.length == 4) {
				layoutFriendlyURL = StringPool.SLASH + urlParts[3];

				layout = LayoutLocalServiceUtil.getFriendlyURLLayout(
					group.getGroupId(), privateLayout, layoutFriendlyURL);
			}
			else {
				List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(
					group.getGroupId(), privateLayout,
					LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, true, 0, 1);

				if (!layouts.isEmpty()) {
					layout = layouts.get(0);
				}
				else {
					return LayoutConstants.DEFAULT_PLID;
				}
			}

			return layout.getPlid();
		}
		catch (Exception e) {
		}

		return LayoutConstants.DEFAULT_PLID;
	}

	@Override
	public long getPlidFromPortletId(
		long groupId, boolean privateLayout, String portletId) {

		long plid = LayoutConstants.DEFAULT_PLID;

		StringBundler sb = new StringBundler(5);

		sb.append(groupId);
		sb.append(StringPool.SPACE);
		sb.append(privateLayout);
		sb.append(StringPool.SPACE);
		sb.append(portletId);

		String key = sb.toString();

		Long plidObj = _plidToPortletIdMap.get(key);

		if (plidObj == null) {
			plid = doGetPlidFromPortletId(groupId, privateLayout, portletId);

			if (plid != LayoutConstants.DEFAULT_PLID) {
				_plidToPortletIdMap.put(key, plid);
			}
		}
		else {
			plid = plidObj.longValue();

			boolean validPlid = false;

			try {
				Layout layout = LayoutLocalServiceUtil.getLayout(plid);

				LayoutTypePortlet layoutTypePortlet =
					(LayoutTypePortlet)layout.getLayoutType();

				if (layoutTypePortlet.hasDefaultScopePortletId(
						groupId, portletId)) {

					validPlid = true;
				}
			}
			catch (Exception e) {
			}

			if (!validPlid) {
				_plidToPortletIdMap.remove(key);

				plid = doGetPlidFromPortletId(
					groupId, privateLayout, portletId);

				if (plid != LayoutConstants.DEFAULT_PLID) {
					_plidToPortletIdMap.put(key, plid);
				}
			}
		}

		return plid;
	}

	@Override
	public long getPlidFromPortletId(long groupId, String portletId) {
		long plid = getPlidFromPortletId(groupId, false, portletId);

		if (plid == LayoutConstants.DEFAULT_PLID) {
			plid = getPlidFromPortletId(groupId, true, portletId);
		}

		if (plid == LayoutConstants.DEFAULT_PLID) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Portlet ", portletId,
						" does not exist on a page in group ",
						String.valueOf(groupId)));
			}
		}

		return plid;
	}

	@Override
	public PortalInetSocketAddressEventListener[]
		getPortalInetSocketAddressEventListeners() {

		return _portalInetSocketAddressEventListeners.toArray(
			new PortalInetSocketAddressEventListener[
				_portalInetSocketAddressEventListeners.size()]);
	}

	@Override
	public String getPortalLibDir() {
		return PropsValues.LIFERAY_LIB_PORTAL_DIR;
	}

	@Override
	public InetAddress getPortalLocalInetAddress(boolean secure) {
		InetSocketAddress inetSocketAddress = null;

		if (secure) {
			inetSocketAddress = _securePortalLocalInetSocketAddress.get();
		}
		else {
			inetSocketAddress = _portalLocalInetSocketAddress.get();
		}

		if (inetSocketAddress == null) {
			return null;
		}

		return inetSocketAddress.getAddress();
	}

	@Override
	public int getPortalLocalPort(boolean secure) {
		InetSocketAddress inetSocketAddress = null;

		if (secure) {
			inetSocketAddress = _securePortalLocalInetSocketAddress.get();
		}
		else {
			inetSocketAddress = _portalLocalInetSocketAddress.get();
		}

		if (inetSocketAddress == null) {
			return -1;
		}

		return inetSocketAddress.getPort();
	}

	/**
	 * @deprecated As of 7.0.0, replaced by {@link
	 *             #getPortalServerPort(boolean)}
	 */
	@Deprecated
	@Override
	public int getPortalPort(boolean secure) {
		return getPortalServerPort(secure);
	}

	@Override
	public Properties getPortalProperties() {
		return PropsUtil.getProperties();
	}

	@Override
	public InetAddress getPortalServerInetAddress(boolean secure) {
		InetSocketAddress inetSocketAddress = null;

		if (secure) {
			inetSocketAddress = _securePortalServerInetSocketAddress.get();
		}
		else {
			inetSocketAddress = _portalServerInetSocketAddress.get();
		}

		if (inetSocketAddress == null) {
			return null;
		}

		return inetSocketAddress.getAddress();
	}

	@Override
	public int getPortalServerPort(boolean secure) {
		InetSocketAddress inetSocketAddress = null;

		if (secure) {
			inetSocketAddress = _securePortalServerInetSocketAddress.get();
		}
		else {
			inetSocketAddress = _portalServerInetSocketAddress.get();
		}

		if (inetSocketAddress == null) {
			if (secure) {
				return PropsValues.WEB_SERVER_HTTPS_PORT;
			}
			else {
				return PropsValues.WEB_SERVER_HTTP_PORT;
			}
		}

		return inetSocketAddress.getPort();
	}

	@Override
	public String getPortalURL(HttpServletRequest request) {
		return getPortalURL(request, isSecure(request));
	}

	@Override
	public String getPortalURL(HttpServletRequest request, boolean secure) {
		int serverPort = getForwardedPort(request);

		if (Validator.isNull(PropsValues.WEB_SERVER_HOST)) {
			return _getPortalURL(getForwardedHost(request), serverPort, secure);
		}

		return _getPortalURL(PropsValues.WEB_SERVER_HOST, serverPort, secure);
	}

	@Override
	public String getPortalURL(Layout layout, ThemeDisplay themeDisplay)
		throws PortalException {

		String serverName = themeDisplay.getServerName();

		if (layout == null) {
			layout = themeDisplay.getLayout();
		}

		if (layout != null) {
			Layout virtualHostLayout = layout;

			long refererPlid = themeDisplay.getRefererPlid();

			if (refererPlid > 0) {
				virtualHostLayout = LayoutLocalServiceUtil.getLayout(
					refererPlid);
			}

			return getPortalURL(virtualHostLayout.getLayoutSet(), themeDisplay);
		}

		return getPortalURL(
			serverName, themeDisplay.getServerPort(), themeDisplay.isSecure());
	}

	@Override
	public String getPortalURL(LayoutSet layoutSet, ThemeDisplay themeDisplay) {
		String serverName = themeDisplay.getServerName();

		String virtualHostname = layoutSet.getVirtualHostname();

		if (Validator.isNotNull(virtualHostname)) {
			String domain = themeDisplay.getPortalDomain();

			if (domain.startsWith(virtualHostname)) {
				serverName = virtualHostname;
			}
		}

		return getPortalURL(
			serverName, themeDisplay.getServerPort(), themeDisplay.isSecure());
	}

	@Override
	public String getPortalURL(PortletRequest portletRequest) {
		return getPortalURL(portletRequest, portletRequest.isSecure());
	}

	@Override
	public String getPortalURL(PortletRequest portletRequest, boolean secure) {
		int port = portletRequest.getServerPort();

		if (Validator.isNull(PropsValues.WEB_SERVER_HOST)) {
			return _getPortalURL(portletRequest.getServerName(), port, secure);
		}

		return _getPortalURL(PropsValues.WEB_SERVER_HOST, port, secure);
	}

	@Override
	public String getPortalURL(
		String serverName, int serverPort, boolean secure) {

		if (Validator.isNull(PropsValues.WEB_SERVER_HOST)) {
			return _getPortalURL(serverName, serverPort, secure);
		}

		return _getPortalURL(PropsValues.WEB_SERVER_HOST, serverPort, secure);
	}

	@Override
	public String getPortalURL(ThemeDisplay themeDisplay)
		throws PortalException {

		return getPortalURL((Layout)null, themeDisplay);
	}

	@Override
	public String getPortalWebDir() {
		return PropsValues.LIFERAY_WEB_PORTAL_DIR;
	}

	/**
	 * @deprecated As of 7.0.0, replaced by {@link
	 *             BreadcrumbUtil#getPortletBreadcrumbEntries(
	 *             HttpServletRequest)}
	 */
	@Deprecated
	@Override
	public List<BreadcrumbEntry> getPortletBreadcrumbs(
		HttpServletRequest request) {

		return BreadcrumbUtil.getPortletBreadcrumbEntries(request);
	}

	@Override
	public PortletConfig getPortletConfig(
			long companyId, String portletId, ServletContext servletContext)
		throws PortletException {

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			companyId, portletId);

		InvokerPortlet invokerPortlet = PortletInstanceFactoryUtil.create(
			portlet, servletContext);

		return invokerPortlet.getPortletConfig();
	}

	@Override
	public String getPortletDescription(
		Portlet portlet, ServletContext servletContext, Locale locale) {

		PortletConfig portletConfig = PortletConfigFactoryUtil.create(
			portlet, servletContext);

		ResourceBundle resourceBundle = portletConfig.getResourceBundle(locale);

		String portletDescription = LanguageUtil.get(
			resourceBundle,
			JavaConstants.JAVAX_PORTLET_DESCRIPTION.concat(
				StringPool.PERIOD).concat(portlet.getRootPortletId()),
			null);

		if (Validator.isNull(portletDescription)) {
			portletDescription = LanguageUtil.get(
				resourceBundle, JavaConstants.JAVAX_PORTLET_DESCRIPTION);
		}

		return portletDescription;
	}

	@Override
	public String getPortletDescription(Portlet portlet, User user) {
		return getPortletDescription(portlet.getPortletId(), user);
	}

	@Override
	public String getPortletDescription(String portletId, Locale locale) {
		return LanguageUtil.get(
			locale,
			JavaConstants.JAVAX_PORTLET_DESCRIPTION.concat(
				StringPool.PERIOD).concat(portletId));
	}

	@Override
	public String getPortletDescription(String portletId, String languageId) {
		Locale locale = LocaleUtil.fromLanguageId(languageId);

		return getPortletDescription(portletId, locale);
	}

	@Override
	public String getPortletDescription(String portletId, User user) {
		return LanguageUtil.get(
			user.getLocale(),
			JavaConstants.JAVAX_PORTLET_DESCRIPTION.concat(
				StringPool.PERIOD).concat(portletId));
	}

	public LayoutQueryStringComposite
			getPortletFriendlyURLMapperLayoutQueryStringComposite(
				long groupId, boolean privateLayout, String url,
				Map<String, String[]> params,
				Map<String, Object> requestContext)
		throws PortalException {

		boolean foundFriendlyURLMapper = false;

		String friendlyURL = url;
		String queryString = StringPool.BLANK;

		List<Portlet> portlets =
			PortletLocalServiceUtil.getFriendlyURLMapperPortlets();

		for (Portlet portlet : portlets) {
			FriendlyURLMapper friendlyURLMapper =
				portlet.getFriendlyURLMapperInstance();

			if (url.endsWith(
					StringPool.SLASH + friendlyURLMapper.getMapping())) {

				url += StringPool.SLASH;
			}

			int pos = -1;

			if (friendlyURLMapper.isCheckMappingWithPrefix()) {
				pos = url.indexOf(
					FRIENDLY_URL_SEPARATOR + friendlyURLMapper.getMapping() +
						StringPool.SLASH);
			}
			else {
				pos = url.indexOf(
					StringPool.SLASH + friendlyURLMapper.getMapping() +
						StringPool.SLASH);
			}

			if (pos != -1) {
				foundFriendlyURLMapper = true;

				friendlyURL = url.substring(0, pos);

				InheritableMap<String, String[]> actualParams =
					new InheritableMap<>();

				if (params != null) {
					actualParams.setParentMap(params);
				}

				Map<String, String> prpIdentifiers = new HashMap<>();

				Set<PublicRenderParameter> publicRenderParameters =
					portlet.getPublicRenderParameters();

				for (PublicRenderParameter publicRenderParameter :
						publicRenderParameters) {

					QName qName = publicRenderParameter.getQName();

					String publicRenderParameterIdentifier =
						qName.getLocalPart();
					String publicRenderParameterName =
						PortletQNameUtil.getPublicRenderParameterName(qName);

					prpIdentifiers.put(
						publicRenderParameterIdentifier,
						publicRenderParameterName);
				}

				FriendlyURLMapperThreadLocal.setPRPIdentifiers(prpIdentifiers);

				if (friendlyURLMapper.isCheckMappingWithPrefix()) {
					friendlyURLMapper.populateParams(
						url.substring(pos + 2), actualParams, requestContext);
				}
				else {
					friendlyURLMapper.populateParams(
						url.substring(pos), actualParams, requestContext);
				}

				queryString =
					StringPool.AMPERSAND +
						HttpUtil.parameterMapToString(actualParams, false);

				break;
			}
		}

		if (!foundFriendlyURLMapper) {
			int x = url.indexOf(FRIENDLY_URL_SEPARATOR);

			if (x != -1) {
				int y = url.indexOf(CharPool.SLASH, x + 3);

				if (y == -1) {
					y = url.length();
				}

				String ppid = url.substring(x + 3, y);

				if (Validator.isNotNull(ppid)) {
					friendlyURL = url.substring(0, x);

					Map<String, String[]> actualParams = null;

					if (params != null) {
						actualParams = new HashMap<>(params);
					}
					else {
						actualParams = new HashMap<>();
					}

					actualParams.put("p_p_id", new String[] {ppid});
					actualParams.put("p_p_lifecycle", new String[] {"0"});
					actualParams.put(
						"p_p_mode", new String[] {PortletMode.VIEW.toString()});
					actualParams.put(
						"p_p_state",
						new String[] {WindowState.MAXIMIZED.toString()});

					queryString =
						StringPool.AMPERSAND +
							HttpUtil.parameterMapToString(actualParams, false);
				}
			}
		}

		friendlyURL = StringUtil.replace(
			friendlyURL, StringPool.DOUBLE_SLASH, StringPool.SLASH);

		if (friendlyURL.endsWith(StringPool.SLASH)) {
			friendlyURL = friendlyURL.substring(0, friendlyURL.length() - 1);
		}

		Layout layout = null;

		if (Validator.isNotNull(friendlyURL)) {
			layout = LayoutLocalServiceUtil.getFriendlyURLLayout(
				groupId, privateLayout, friendlyURL);
		}
		else {
			long defaultPlid = LayoutLocalServiceUtil.getDefaultPlid(
				groupId, privateLayout);

			layout = LayoutLocalServiceUtil.getLayout(defaultPlid);
		}

		return new LayoutQueryStringComposite(layout, friendlyURL, queryString);
	}

	@Override
	public String getPortletId(HttpServletRequest request) {
		LiferayPortletConfig liferayPortletConfig =
			(LiferayPortletConfig)request.getAttribute(
				JavaConstants.JAVAX_PORTLET_CONFIG);

		if (liferayPortletConfig != null) {
			return liferayPortletConfig.getPortletId();
		}
		else {
			return null;
		}
	}

	@Override
	public String getPortletId(PortletRequest portletRequest) {
		LiferayPortletConfig liferayPortletConfig =
			(LiferayPortletConfig)portletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_CONFIG);

		if (liferayPortletConfig != null) {
			return liferayPortletConfig.getPortletId();
		}
		else {
			return null;
		}
	}

	@Override
	public String getPortletLongTitle(Portlet portlet, Locale locale) {
		return getPortletLongTitle(portlet.getPortletId(), locale);
	}

	@Override
	public String getPortletLongTitle(
		Portlet portlet, ServletContext servletContext, Locale locale) {

		PortletConfig portletConfig = PortletConfigFactoryUtil.create(
			portlet, servletContext);

		ResourceBundle resourceBundle = portletConfig.getResourceBundle(locale);

		try {
			String portletLongTitle = ResourceBundleUtil.getString(
				resourceBundle, JavaConstants.JAVAX_PORTLET_LONG_TITLE);

			if (portletLongTitle.startsWith(
					JavaConstants.JAVAX_PORTLET_LONG_TITLE)) {

				portletLongTitle = getPortletTitle(
					portlet, servletContext, locale);
			}

			return portletLongTitle;
		}
		catch (Exception e) {
			return getPortletTitle(portlet, servletContext, locale);
		}
	}

	@Override
	public String getPortletLongTitle(Portlet portlet, String languageId) {
		return getPortletLongTitle(portlet.getPortletId(), languageId);
	}

	@Override
	public String getPortletLongTitle(Portlet portlet, User user) {
		return getPortletLongTitle(portlet.getPortletId(), user);
	}

	@Override
	public String getPortletLongTitle(String portletId, Locale locale) {
		String portletLongTitle = LanguageUtil.get(
			locale,
			JavaConstants.JAVAX_PORTLET_LONG_TITLE.concat(
				StringPool.PERIOD).concat(portletId),
			StringPool.BLANK);

		if (Validator.isNull(portletLongTitle)) {
			portletLongTitle = getPortletTitle(portletId, locale);
		}

		return portletLongTitle;
	}

	@Override
	public String getPortletLongTitle(String portletId, String languageId) {
		Locale locale = LocaleUtil.fromLanguageId(languageId);

		return getPortletLongTitle(portletId, locale);
	}

	@Override
	public String getPortletLongTitle(String portletId, User user) {
		return getPortletLongTitle(portletId, user.getLocale());
	}

	@Override
	public String getPortletNamespace(String portletId) {
		return StringPool.UNDERLINE.concat(portletId).concat(
			StringPool.UNDERLINE);
	}

	@Override
	public String getPortletTitle(Portlet portlet, Locale locale) {
		return getPortletTitle(portlet.getPortletId(), locale);
	}

	@Override
	public String getPortletTitle(
		Portlet portlet, ServletContext servletContext, Locale locale) {

		PortletConfig portletConfig = PortletConfigFactoryUtil.create(
			portlet, servletContext);

		return _getPortletTitle(
			portlet.getRootPortletId(), portletConfig, locale);
	}

	@Override
	public String getPortletTitle(Portlet portlet, String languageId) {
		return getPortletTitle(portlet.getPortletId(), languageId);
	}

	@Override
	public String getPortletTitle(Portlet portlet, User user) {
		return getPortletTitle(portlet.getPortletId(), user);
	}

	@Override
	public String getPortletTitle(PortletRequest portletRequest) {
		String portletId = (String)portletRequest.getAttribute(
			WebKeys.PORTLET_ID);

		PortletConfig portletConfig = PortletConfigFactoryUtil.get(portletId);

		Locale locale = portletRequest.getLocale();

		if (portletConfig == null) {
			Portlet portlet = PortletLocalServiceUtil.getPortletById(
				getCompanyId(portletRequest), portletId);

			HttpServletRequest request = getHttpServletRequest(portletRequest);

			ServletContext servletContext =
				(ServletContext)request.getAttribute(WebKeys.CTX);

			return getPortletTitle(portlet, servletContext, locale);
		}

		return _getPortletTitle(
			PortletIdCodec.decodePortletName(portletId), portletConfig, locale);
	}

	@Override
	public String getPortletTitle(PortletResponse portletResponse) {
		PortletResponseImpl portletResponseImpl =
			PortletResponseImpl.getPortletResponseImpl(portletResponse);

		return ((RenderResponseImpl)portletResponseImpl).getTitle();
	}

	@Override
	public String getPortletTitle(String portletId, Locale locale) {
		PortletConfig portletConfig = PortletConfigFactoryUtil.get(portletId);

		return getPortletTitle(
			portletId, portletConfig.getResourceBundle(locale));
	}

	@Override
	public String getPortletTitle(
		String portletId, ResourceBundle resourceBundle) {

		portletId = PortletIdCodec.decodePortletName(portletId);

		String portletTitle = LanguageUtil.get(
			resourceBundle,
			JavaConstants.JAVAX_PORTLET_TITLE.concat(StringPool.PERIOD).concat(
				portletId),
			null);

		if (Validator.isNull(portletTitle)) {
			portletTitle = ResourceBundleUtil.getString(
				resourceBundle, JavaConstants.JAVAX_PORTLET_TITLE);
		}

		return portletTitle;
	}

	@Override
	public String getPortletTitle(String portletId, String languageId) {
		Locale locale = LocaleUtil.fromLanguageId(languageId);

		return getPortletTitle(portletId, locale);
	}

	@Override
	public String getPortletTitle(String portletId, User user) {
		return LanguageUtil.get(
			user.getLocale(),
			JavaConstants.JAVAX_PORTLET_TITLE.concat(StringPool.PERIOD).concat(
				portletId));
	}

	@Override
	public String getPortletXmlFileName() {
		if (PropsValues.AUTO_DEPLOY_CUSTOM_PORTLET_XML) {
			return PORTLET_XML_FILE_NAME_CUSTOM;
		}
		else {
			return PORTLET_XML_FILE_NAME_STANDARD;
		}
	}

	@Override
	public PortletPreferences getPreferences(HttpServletRequest request) {
		RenderRequest renderRequest = (RenderRequest)request.getAttribute(
			JavaConstants.JAVAX_PORTLET_REQUEST);

		PortletPreferences portletPreferences = null;

		if (renderRequest != null) {
			PortletPreferencesWrapper portletPreferencesWrapper =
				(PortletPreferencesWrapper)renderRequest.getPreferences();

			portletPreferences =
				portletPreferencesWrapper.getPortletPreferencesImpl();
		}

		return portletPreferences;
	}

	@Override
	public PreferencesValidator getPreferencesValidator(Portlet portlet) {
		PortletBag portletBag = PortletBagPool.get(portlet.getRootPortletId());

		if (portletBag == null) {
			return null;
		}

		List<PreferencesValidator> preferencesValidatorInstances =
			portletBag.getPreferencesValidatorInstances();

		if (preferencesValidatorInstances.isEmpty()) {
			return null;
		}

		return preferencesValidatorInstances.get(0);
	}

	@Override
	public String getRelativeHomeURL(HttpServletRequest request)
		throws PortalException {

		Company company = getCompany(request);

		String homeURL = company.getHomeURL();

		if (Validator.isNull(homeURL)) {
			homeURL = PropsValues.COMPANY_DEFAULT_HOME_URL;
		}

		return homeURL;
	}

	@Override
	public ResourceBundle getResourceBundle(Locale locale) {
		return LanguageResources.getResourceBundle(locale);
	}

	@Override
	public long getScopeGroupId(HttpServletRequest request)
		throws PortalException {

		String portletId = getPortletId(request);

		return getScopeGroupId(request, portletId);
	}

	@Override
	public long getScopeGroupId(HttpServletRequest request, String portletId)
		throws PortalException {

		return getScopeGroupId(request, portletId, false);
	}

	@Override
	public long getScopeGroupId(
			HttpServletRequest request, String portletId,
			boolean checkStagingGroup)
		throws PortalException {

		Layout layout = (Layout)request.getAttribute(WebKeys.LAYOUT);
		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		long scopeGroupId = 0;

		if (layout != null) {
			Group group = layout.getGroup();

			long doAsGroupId = ParamUtil.getLong(request, "doAsGroupId");

			if (doAsGroupId <= 0) {
				HttpServletRequest originalRequest = getOriginalServletRequest(
					request);

				doAsGroupId = ParamUtil.getLong(originalRequest, "doAsGroupId");
			}

			Group doAsGroup = null;

			if (doAsGroupId > 0) {
				doAsGroup = GroupLocalServiceUtil.fetchGroup(doAsGroupId);
			}

			if (group.isControlPanel()) {
				if (doAsGroupId > 0) {
					scopeGroupId = doAsGroupId;
				}

				group = GroupLocalServiceUtil.fetchGroup(scopeGroupId);

				if ((group != null) && group.hasStagingGroup()) {
					try {
						Group stagingGroup =
							StagingUtil.getPermissionStagingGroup(group);

						scopeGroupId = stagingGroup.getGroupId();
					}
					catch (Exception e) {
					}
				}
			}
			else if (doAsGroup != null) {
				scopeGroupId = doAsGroupId;
			}

			if ((group != null) && group.isInheritContent()) {
				Group layoutGroup = layout.getGroup();

				if (!layoutGroup.isControlPanel()) {
					scopeGroupId = group.getParentGroupId();
				}
			}

			if ((portletId != null) && (group != null) &&
				(group.isStaged() || group.isStagingGroup())) {

				Group liveGroup = group;

				if (group.isStagingGroup()) {
					liveGroup = group.getLiveGroup();
				}

				if (liveGroup.isStaged() &&
					!liveGroup.isStagedPortlet(portletId)) {

					Layout liveGroupLayout =
						LayoutLocalServiceUtil.fetchLayoutByUuidAndGroupId(
							layout.getUuid(), liveGroup.getGroupId(),
							layout.isPrivateLayout());

					if ((liveGroupLayout != null) &&
						liveGroupLayout.hasScopeGroup()) {

						scopeGroupId = _getScopeGroupId(
							themeDisplay, liveGroupLayout, portletId);
					}
					else if (checkStagingGroup &&
							 !liveGroup.isStagedRemotely()) {

						Group stagingGroup = liveGroup.getStagingGroup();

						scopeGroupId = stagingGroup.getGroupId();
					}
					else {
						scopeGroupId = liveGroup.getGroupId();
					}
				}
			}
		}

		if (scopeGroupId <= 0) {
			scopeGroupId = _getScopeGroupId(themeDisplay, layout, portletId);
		}

		return scopeGroupId;
	}

	@Override
	public long getScopeGroupId(Layout layout) {
		if (layout == null) {
			return 0;
		}
		else {
			return layout.getGroupId();
		}
	}

	@Override
	public long getScopeGroupId(Layout layout, String portletId) {
		return _getScopeGroupId(null, layout, portletId);
	}

	@Override
	public long getScopeGroupId(long plid) {
		Layout layout = null;

		try {
			layout = LayoutLocalServiceUtil.getLayout(plid);
		}
		catch (Exception e) {
		}

		return getScopeGroupId(layout);
	}

	@Override
	public long getScopeGroupId(PortletRequest portletRequest)
		throws PortalException {

		return getScopeGroupId(getHttpServletRequest(portletRequest));
	}

	@Override
	public User getSelectedUser(HttpServletRequest request)
		throws PortalException {

		return getSelectedUser(request, true);
	}

	@Override
	public User getSelectedUser(
			HttpServletRequest request, boolean checkPermission)
		throws PortalException {

		long userId = ParamUtil.getLong(request, "p_u_i_d");

		User user = null;

		try {
			if (checkPermission) {
				user = UserServiceUtil.getUserById(userId);
			}
			else {
				user = UserLocalServiceUtil.getUserById(userId);
			}
		}
		catch (NoSuchUserException nsue) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(nsue, nsue);
			}
		}

		return user;
	}

	@Override
	public User getSelectedUser(PortletRequest portletRequest)
		throws PortalException {

		return getSelectedUser(portletRequest, true);
	}

	@Override
	public User getSelectedUser(
			PortletRequest portletRequest, boolean checkPermission)
		throws PortalException {

		return getSelectedUser(
			getHttpServletRequest(portletRequest), checkPermission);
	}

	@Override
	public String getServletContextName() {
		return _servletContextName;
	}

	@Override
	public long[] getSharedContentSiteGroupIds(
			long companyId, long groupId, long userId)
		throws PortalException {

		Set<Group> groups = new LinkedHashSet<>();

		Group siteGroup = doGetCurrentSiteGroup(groupId);

		if (siteGroup != null) {

			// Current site

			groups.add(siteGroup);

			// Descendant sites

			groups.addAll(siteGroup.getDescendants(true));

			// Layout scopes

			groups.addAll(
				GroupLocalServiceUtil.getGroups(
					siteGroup.getCompanyId(), Layout.class.getName(),
					siteGroup.getGroupId()));
		}

		// Administered sites

		if (PrefsPropsUtil.getBoolean(
				companyId,
				PropsKeys.
					SITES_CONTENT_SHARING_THROUGH_ADMINISTRATORS_ENABLED)) {

			groups.addAll(GroupLocalServiceUtil.getUserSitesGroups(userId));
		}

		// Ancestor sites and global site

		int sitesContentSharingWithChildrenEnabled = PrefsPropsUtil.getInteger(
			companyId, PropsKeys.SITES_CONTENT_SHARING_WITH_CHILDREN_ENABLED);

		if (sitesContentSharingWithChildrenEnabled !=
				Sites.CONTENT_SHARING_WITH_CHILDREN_DISABLED) {

			groups.addAll(doGetAncestorSiteGroups(groupId, true));
		}

		Iterator<Group> iterator = groups.iterator();

		while (iterator.hasNext()) {
			Group group = iterator.next();

			if (!StagingUtil.isGroupAccessible(group, siteGroup)) {
				iterator.remove();
			}
		}

		long[] groupIds = new long[groups.size()];

		int i = 0;

		for (Group group : groups) {
			groupIds[i++] = group.getGroupId();
		}

		return groupIds;
	}

	/**
	 * @deprecated As of 7.0.0, replaced by {@link
	 *             #getControlPanelPortletURL(PortletRequest, Group, String,
	 *             String)}
	 */
	@Deprecated
	@Override
	public PortletURL getSiteAdministrationURL(
		HttpServletRequest request, ThemeDisplay themeDisplay,
		String portletId) {

		PortletURL portletURL = getControlPanelPortletURL(
			request, portletId, PortletRequest.RENDER_PHASE);

		portletURL.setParameter("redirect", themeDisplay.getURLCurrent());

		return portletURL;
	}

	/**
	 * @deprecated As of 7.0.0, replaced by {@link
	 *             #getControlPanelPortletURL(PortletRequest, Group, String,
	 *             String)}
	 */
	@Deprecated
	@Override
	public PortletURL getSiteAdministrationURL(
		PortletResponse portletResponse, ThemeDisplay themeDisplay,
		String portletName) {

		LiferayPortletResponse liferayPortletResponse =
			getLiferayPortletResponse(portletResponse);

		LiferayPortletURL siteAdministrationURL =
			liferayPortletResponse.createRenderURL(portletName);

		siteAdministrationURL.setDoAsGroupId(themeDisplay.getScopeGroupId());
		siteAdministrationURL.setParameter(
			"redirect", themeDisplay.getURLCurrent());

		return siteAdministrationURL;
	}

	/**
	 * @deprecated As of 7.0.0, replaced by {@link
	 *             #getSiteAdminURL(ThemeDisplay, String, Map)}
	 */
	@Deprecated
	@Override
	public String getSiteAdminURL(
			Company company, Group group, String ppid,
			Map<String, String[]> params)
		throws PortalException {

		return _getSiteAdminURL(
			getPortalURL(
				company.getVirtualHostname(), getPortalServerPort(false),
				false),
			group, ppid, params);
	}

	/**
	 * @deprecated As of 7.0.0, replaced by {@link
	 *             #getSiteAdminURL(ThemeDisplay, String, Map)}
	 */
	@Deprecated
	@Override
	public String getSiteAdminURL(
			Group group, String ppid, Map<String, String[]> params)
		throws PortalException {

		Company company = CompanyLocalServiceUtil.getCompany(
			group.getCompanyId());

		return _getSiteAdminURL(
			getPortalURL(
				company.getVirtualHostname(), getPortalServerPort(false),
				false),
			group, ppid, params);
	}

	@Override
	public String getSiteAdminURL(
			ThemeDisplay themeDisplay, String ppid,
			Map<String, String[]> params)
		throws PortalException {

		return _getSiteAdminURL(
			themeDisplay.getPortalURL(), themeDisplay.getScopeGroup(), ppid,
			params);
	}

	/**
	 * @deprecated As of 7.0.0, replaced by {@link
	 *             #getCurrentAndAncestorSiteGroupIds(long)}
	 */
	@Deprecated
	@Override
	public long[] getSiteAndCompanyGroupIds(long groupId)
		throws PortalException {

		Group scopeGroup = GroupLocalServiceUtil.getGroup(groupId);

		Group companyGroup = GroupLocalServiceUtil.getCompanyGroup(
			scopeGroup.getCompanyId());

		if (scopeGroup.isLayout()) {
			return new long[] {
				groupId, scopeGroup.getParentGroupId(),
				companyGroup.getGroupId()
			};
		}
		else if (scopeGroup.isLayoutSetPrototype() ||
				 scopeGroup.isOrganization() || scopeGroup.isRegularSite() ||
				 scopeGroup.isUser()) {

			return new long[] {groupId, companyGroup.getGroupId()};
		}
		else {
			return new long[] {companyGroup.getGroupId()};
		}
	}

	/**
	 * @deprecated As of 7.0.0, replaced by {@link
	 *             #getCurrentAndAncestorSiteGroupIds(long)}
	 */
	@Deprecated
	@Override
	public long[] getSiteAndCompanyGroupIds(ThemeDisplay themeDisplay)
		throws PortalException {

		return getSiteAndCompanyGroupIds(themeDisplay.getScopeGroupId());
	}

	@Override
	public Locale getSiteDefaultLocale(long groupId) throws PortalException {
		if (groupId <= 0) {
			return LocaleUtil.getDefault();
		}

		Group group = GroupLocalServiceUtil.getGroup(groupId);

		Group liveGroup = group;

		if (group.isStagingGroup()) {
			liveGroup = group.getLiveGroup();
		}

		if (LanguageUtil.isInheritLocales(liveGroup.getGroupId())) {
			Company company = CompanyLocalServiceUtil.getCompany(
				liveGroup.getCompanyId());

			return company.getLocale();
		}

		UnicodeProperties typeSettingsProperties =
			liveGroup.getTypeSettingsProperties();

		User defaultUser = UserLocalServiceUtil.getDefaultUser(
			group.getCompanyId());

		String languageId = GetterUtil.getString(
			typeSettingsProperties.getProperty("languageId"),
			defaultUser.getLanguageId());

		return LocaleUtil.fromLanguageId(languageId);
	}

	@Override
	public long getSiteGroupId(long groupId) {
		try {
			Group group = _getSiteGroup(groupId);

			if (group == null) {
				return 0;
			}

			return group.getGroupId();
		}
		catch (PortalException pe) {
			return ReflectionUtil.throwException(pe);
		}
	}

	@Override
	public String getSiteLoginURL(ThemeDisplay themeDisplay)
		throws PortalException {

		if (Validator.isNull(PropsValues.AUTH_LOGIN_SITE_URL)) {
			return null;
		}

		List<Layout> layouts = themeDisplay.getUnfilteredLayouts();

		if (layouts == null) {
			return null;
		}

		for (Layout layout : layouts) {
			String friendlyURL = themeDisplay.getLayoutFriendlyURL(layout);

			if (friendlyURL.equals(PropsValues.AUTH_LOGIN_SITE_URL)) {
				if (themeDisplay.getLayout() == null) {
					break;
				}

				String layoutSetFriendlyURL = getLayoutSetFriendlyURL(
					layout.getLayoutSet(), themeDisplay);

				return layoutSetFriendlyURL + PropsValues.AUTH_LOGIN_SITE_URL;
			}
		}

		return null;
	}

	@Override
	public String getStaticResourceURL(HttpServletRequest request, String uri) {
		return getStaticResourceURL(request, uri, null, 0);
	}

	@Override
	public String getStaticResourceURL(
		HttpServletRequest request, String uri, long timestamp) {

		return getStaticResourceURL(request, uri, null, timestamp);
	}

	@Override
	public String getStaticResourceURL(
		HttpServletRequest request, String uri, String queryString) {

		return getStaticResourceURL(request, uri, queryString, 0);
	}

	@Override
	public String getStaticResourceURL(
		HttpServletRequest request, String uri, String queryString,
		long timestamp) {

		if (uri.indexOf(CharPool.QUESTION) != -1) {
			return uri;
		}

		if (uri.startsWith(StringPool.DOUBLE_SLASH)) {
			uri = uri.substring(1);
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		Theme theme = themeDisplay.getTheme();
		ColorScheme colorScheme = themeDisplay.getColorScheme();

		Map<String, String[]> parameterMap = null;

		if (Validator.isNotNull(queryString)) {
			parameterMap = HttpUtil.getParameterMap(queryString);
		}

		StringBundler sb = new StringBundler(17);

		// URI

		sb.append(uri);

		boolean firstParam = true;

		// Browser id

		if ((parameterMap == null) || !parameterMap.containsKey("browserId")) {
			sb.append("?browserId=");
			sb.append(BrowserSnifferUtil.getBrowserId(request));

			firstParam = false;
		}

		// Theme and color scheme

		if ((uri.endsWith(".css") || uri.endsWith(".jsp")) &&
			((parameterMap == null) || !parameterMap.containsKey("themeId"))) {

			if (firstParam) {
				sb.append("?themeId=");

				firstParam = false;
			}
			else {
				sb.append("&themeId=");
			}

			sb.append(URLCodec.encodeURL(theme.getThemeId()));
		}

		if (uri.endsWith(".jsp") &&
			((parameterMap == null) ||
			 !parameterMap.containsKey("colorSchemeId"))) {

			if (firstParam) {
				sb.append("?colorSchemeId=");

				firstParam = false;
			}
			else {
				sb.append("&colorSchemeId=");
			}

			sb.append(URLCodec.encodeURL(colorScheme.getColorSchemeId()));
		}

		// Minifier

		if ((parameterMap == null) ||
			!parameterMap.containsKey("minifierType")) {

			String minifierType = StringPool.BLANK;

			if (uri.endsWith(".css") || uri.endsWith("css.jsp") ||
				(uri.endsWith(".jsp") && uri.contains("/css/"))) {

				if (themeDisplay.isThemeCssFastLoad()) {
					minifierType = "css";
				}
			}
			else if (themeDisplay.isThemeJsFastLoad()) {
				minifierType = "js";
			}

			if (Validator.isNotNull(minifierType)) {
				if (firstParam) {
					sb.append("?minifierType=");

					firstParam = false;
				}
				else {
					sb.append("&minifierType=");
				}

				sb.append(minifierType);
			}
		}

		// Query string

		if (Validator.isNotNull(queryString)) {
			if (queryString.charAt(0) == CharPool.AMPERSAND) {
				if (firstParam) {
					sb.append(StringPool.QUESTION);
					sb.append(queryString.substring(1));
				}
				else {
					sb.append(queryString);
				}
			}
			else if (firstParam) {
				sb.append(StringPool.QUESTION);
				sb.append(queryString);
			}
			else {
				sb.append(StringPool.AMPERSAND);
				sb.append(queryString);
			}

			firstParam = false;
		}

		// Language id

		if (firstParam) {
			sb.append("?languageId=");
		}
		else {
			sb.append("&languageId=");
		}

		sb.append(themeDisplay.getLanguageId());

		// Build number

		sb.append("&b=");
		sb.append(ReleaseInfo.getBuildNumber());

		// Timestamp

		if (((parameterMap == null) || !parameterMap.containsKey("t")) &&
			 !(timestamp < 0)) {

			if (timestamp == 0) {
				String portalURL = getPortalURL(request);

				String path = uri;

				if (uri.startsWith(portalURL)) {
					path = uri.substring(portalURL.length());
				}

				if (path.startsWith(StrutsUtil.TEXT_HTML_DIR)) {
					ServletContext servletContext =
						(ServletContext)request.getAttribute(WebKeys.CTX);

					timestamp = ServletContextUtil.getLastModified(
						servletContext, path, true);
				}
				else {
					timestamp = PortalWebResourcesUtil.getPathLastModified(
						path, theme.getTimestamp());
				}
			}

			sb.append("&t=");
			sb.append(timestamp);
		}

		return sb.toString();
	}

	@Override
	public String getStrutsAction(HttpServletRequest request) {
		String strutsAction = ParamUtil.getString(request, "struts_action");

		if (Validator.isNotNull(strutsAction)) {

			// This method should only return a Struts action if you're dealing
			// with a regular HTTP servlet request, not a portlet HTTP servlet
			// request.

			return StringPool.BLANK;
		}

		return getPortletParam(request, "struts_action");
	}

	@Override
	public String[] getSystemGroups() {
		return _allSystemGroups;
	}

	@Override
	public String[] getSystemOrganizationRoles() {
		return _allSystemOrganizationRoles;
	}

	@Override
	public String[] getSystemRoles() {
		return _allSystemRoles;
	}

	@Override
	public String[] getSystemSiteRoles() {
		return _allSystemSiteRoles;
	}

	@Override
	public String getUniqueElementId(
		HttpServletRequest request, String namespace, String elementId) {

		String uniqueElementId = elementId;

		Set<String> uniqueElementIds = (Set<String>)request.getAttribute(
			WebKeys.UNIQUE_ELEMENT_IDS);

		if (uniqueElementIds == null) {
			uniqueElementIds = Collections.newSetFromMap(
				new ConcurrentHashMap<>());

			request.setAttribute(WebKeys.UNIQUE_ELEMENT_IDS, uniqueElementIds);
		}
		else {
			int i = 1;

			while (uniqueElementIds.contains(
						namespace.concat(uniqueElementId))) {

				if (Validator.isNull(elementId) ||
					elementId.endsWith(StringPool.UNDERLINE)) {

					uniqueElementId = elementId.concat(String.valueOf(i));
				}
				else {
					uniqueElementId =
						elementId.concat(StringPool.UNDERLINE).concat(
							String.valueOf(i));
				}

				i++;
			}
		}

		uniqueElementIds.add(namespace.concat(uniqueElementId));

		return uniqueElementId;
	}

	@Override
	public String getUniqueElementId(
		PortletRequest request, String namespace, String elementId) {

		return getUniqueElementId(
			getHttpServletRequest(request), namespace, elementId);
	}

	@Override
	public UploadPortletRequest getUploadPortletRequest(
		PortletRequest portletRequest) {

		PortletRequestImpl portletRequestImpl =
			PortletRequestImpl.getPortletRequestImpl(portletRequest);

		DynamicServletRequest dynamicRequest =
			(DynamicServletRequest)portletRequestImpl.getHttpServletRequest();

		HttpServletRequestWrapper requestWrapper =
			(HttpServletRequestWrapper)dynamicRequest.getRequest();

		UploadServletRequest uploadServletRequest = getUploadServletRequest(
			requestWrapper);

		return new UploadPortletRequestImpl(
			uploadServletRequest, portletRequestImpl,
			getPortletNamespace(portletRequestImpl.getPortletName()));
	}

	@Override
	public UploadServletRequest getUploadServletRequest(
		HttpServletRequest request) {

		List<PersistentHttpServletRequestWrapper>
			persistentHttpServletRequestWrappers = new ArrayList<>();

		HttpServletRequest currentRequest = request;

		while (currentRequest instanceof HttpServletRequestWrapper) {
			if (currentRequest instanceof UploadServletRequest) {
				return (UploadServletRequest)currentRequest;
			}

			Class<?> currentRequestClass = currentRequest.getClass();

			String currentRequestClassName = currentRequestClass.getName();

			if (!isUnwrapRequest(currentRequestClassName)) {
				break;
			}

			if (currentRequest instanceof
					PersistentHttpServletRequestWrapper) {

				PersistentHttpServletRequestWrapper
					persistentHttpServletRequestWrapper =
						(PersistentHttpServletRequestWrapper)currentRequest;

				persistentHttpServletRequestWrappers.add(
					persistentHttpServletRequestWrapper.clone());
			}

			HttpServletRequestWrapper httpServletRequestWrapper =
				(HttpServletRequestWrapper)currentRequest;

			currentRequest =
				(HttpServletRequest)httpServletRequestWrapper.getRequest();
		}

		if (ServerDetector.isWebLogic()) {
			currentRequest = new NonSerializableObjectRequestWrapper(
				currentRequest);
		}

		for (int i = persistentHttpServletRequestWrappers.size() - 1; i >= 0;
			i--) {

			HttpServletRequestWrapper httpServletRequestWrapper =
				persistentHttpServletRequestWrappers.get(i);

			httpServletRequestWrapper.setRequest(currentRequest);

			currentRequest = httpServletRequestWrapper;
		}

		return new UploadServletRequestImpl(currentRequest);
	}

	@Override
	public Date getUptime() {
		return _upTime;
	}

	@Override
	public String getURLWithSessionId(String url, String sessionId) {
		if (!PropsValues.SESSION_ENABLE_URL_WITH_SESSION_ID) {
			return url;
		}

		if (Validator.isNull(url)) {
			return url;
		}

		// LEP-4787

		int x = url.indexOf(CharPool.SEMICOLON);

		if (x != -1) {
			return url;
		}

		// LPS-73785

		if (CompoundSessionIdSplitterUtil.hasSessionDelimiter()) {
			HttpSession httpSession = PortalSessionContext.get(sessionId);

			if (httpSession != null) {
				while (httpSession instanceof HttpSessionWrapper) {
					HttpSessionWrapper httpSessionWrapper =
						(HttpSessionWrapper)httpSession;

					httpSession = httpSessionWrapper.getWrappedSession();
				}

				sessionId = httpSession.getId();
			}
		}

		x = url.indexOf(CharPool.QUESTION);

		if (x != -1) {
			StringBundler sb = new StringBundler(4);

			sb.append(url.substring(0, x));
			sb.append(JSESSIONID);
			sb.append(sessionId);
			sb.append(url.substring(x));

			return sb.toString();
		}

		// In IE6, http://www.abc.com;jsessionid=XYZ does not work, but
		// http://www.abc.com/;jsessionid=XYZ does work.

		x = url.indexOf(StringPool.DOUBLE_SLASH);

		StringBundler sb = new StringBundler(4);

		sb.append(url);

		if (x != -1) {
			int y = url.lastIndexOf(CharPool.SLASH);

			if ((x + 1) == y) {
				sb.append(StringPool.SLASH);
			}
		}

		sb.append(JSESSIONID);
		sb.append(sessionId);

		return sb.toString();
	}

	@Override
	public User getUser(HttpServletRequest request) throws PortalException {
		User user = (User)request.getAttribute(WebKeys.USER);

		if (user != null) {
			return user;
		}

		long userId = getUserId(request);

		if (userId <= 0) {

			// Portlet WARs may have the correct remote user and not have the
			// correct user id because the user id is saved in the session and
			// may not be accessible by the portlet WAR's session. This behavior
			// is inconsistent across different application servers.

			String remoteUser = request.getRemoteUser();

			if ((remoteUser == null) && !PropsValues.PORTAL_JAAS_ENABLE) {
				HttpSession session = request.getSession();

				remoteUser = (String)session.getAttribute("j_remoteuser");
			}

			if (remoteUser == null) {
				return null;
			}

			if (PropsValues.PORTAL_JAAS_ENABLE) {
				long companyId = getCompanyId(request);

				try {
					userId = JAASHelper.getJaasUserId(companyId, remoteUser);
				}
				catch (Exception e) {
					if (_log.isWarnEnabled()) {
						_log.warn(e, e);
					}
				}
			}
			else {
				userId = GetterUtil.getLong(remoteUser);
			}
		}

		if (userId > 0) {
			user = UserLocalServiceUtil.getUserById(userId);

			request.setAttribute(WebKeys.USER, user);
		}

		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				String cookieName = cookie.getName();

				if (cookieName.startsWith(
						CookieKeys.REMOTE_PREFERENCE_PREFIX)) {

					user.addRemotePreference(
						new CookieRemotePreference(cookie));
				}
			}
		}

		return user;
	}

	@Override
	public User getUser(PortletRequest portletRequest) throws PortalException {
		return getUser(getHttpServletRequest(portletRequest));
	}

	@Override
	public String getUserEmailAddress(long userId) {
		try {
			User user = UserLocalServiceUtil.getUserById(userId);

			return user.getEmailAddress();
		}
		catch (PortalException pe) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(pe, pe);
			}

			return StringPool.BLANK;
		}
	}

	@Override
	public long getUserId(HttpServletRequest request) {
		Long userIdObj = (Long)request.getAttribute(WebKeys.USER_ID);

		if (userIdObj != null) {
			return userIdObj.longValue();
		}

		String actionName = getPortletParam(request, "actionName");
		String mvcRenderCommandName = ParamUtil.getString(
			request, "mvcRenderCommandName");
		String path = GetterUtil.getString(request.getPathInfo());
		String strutsAction = getStrutsAction(request);

		boolean alwaysAllowDoAsUser = false;

		if (actionName.equals("addFile") ||
			mvcRenderCommandName.equals("/document_library/edit_file_entry") ||
			path.equals("/portal/session_click") ||
			isAlwaysAllowDoAsUser(
				actionName, mvcRenderCommandName, path, strutsAction)) {

			try {
				alwaysAllowDoAsUser = isAlwaysAllowDoAsUser(request);
			}
			catch (Exception e) {
				_log.error(e, e);
			}
		}

		if ((!PropsValues.PORTAL_JAAS_ENABLE &&
			 PropsValues.PORTAL_IMPERSONATION_ENABLE) ||
			alwaysAllowDoAsUser) {

			String doAsUserIdString = ParamUtil.getString(
				request, "doAsUserId");

			try {
				long doAsUserId = getDoAsUserId(
					request, doAsUserIdString, alwaysAllowDoAsUser);

				if (doAsUserId > 0) {
					if (_log.isDebugEnabled()) {
						_log.debug("Impersonating user " + doAsUserId);
					}

					return doAsUserId;
				}
			}
			catch (Exception e) {
				_log.error("Unable to impersonate user " + doAsUserIdString, e);
			}
		}

		HttpSession session = request.getSession();

		userIdObj = (Long)session.getAttribute(WebKeys.USER_ID);

		if (userIdObj != null) {
			request.setAttribute(WebKeys.USER_ID, userIdObj);

			return userIdObj.longValue();
		}
		else {
			return 0;
		}
	}

	@Override
	public long getUserId(PortletRequest portletRequest) {
		return getUserId(getHttpServletRequest(portletRequest));
	}

	@Override
	public String getUserName(BaseModel<?> baseModel) {
		long userId = 0;
		String userName = StringPool.BLANK;

		if (baseModel instanceof AuditedModel) {
			AuditedModel auditedModel = (AuditedModel)baseModel;

			userId = auditedModel.getUserId();
			userName = auditedModel.getUserName();
		}
		else {
			userId = BeanPropertiesUtil.getLongSilent(baseModel, "userId");
			userName = BeanPropertiesUtil.getStringSilent(
				baseModel, "userName");
		}

		if (userId == 0) {
			return StringPool.BLANK;
		}

		if (baseModel.isEscapedModel()) {
			userName = HtmlUtil.unescape(userName);
		}

		userName = getUserName(userId, userName);

		if (baseModel.isEscapedModel()) {
			userName = HtmlUtil.escape(userName);
		}

		return userName;
	}

	@Override
	public String getUserName(long userId, String defaultUserName) {
		return getUserName(
			userId, defaultUserName, UserAttributes.USER_NAME_FULL);
	}

	@Override
	public String getUserName(
		long userId, String defaultUserName, HttpServletRequest request) {

		return getUserName(
			userId, defaultUserName, UserAttributes.USER_NAME_FULL, request);
	}

	@Override
	public String getUserName(
		long userId, String defaultUserName, String userAttribute) {

		return getUserName(userId, defaultUserName, userAttribute, null);
	}

	@Override
	public String getUserName(
		long userId, String defaultUserName, String userAttribute,
		HttpServletRequest request) {

		String userName = defaultUserName;

		try {
			User user = UserLocalServiceUtil.getUserById(userId);

			if (userAttribute.equals(UserAttributes.USER_NAME_FULL)) {
				userName = user.getFullName();
			}
			else {
				userName = user.getScreenName();
			}

			if (request != null) {
				Layout layout = (Layout)request.getAttribute(WebKeys.LAYOUT);

				PortletURL portletURL = PortletURLFactoryUtil.create(
					request, PortletKeys.DIRECTORY, layout,
					PortletRequest.RENDER_PHASE);

				portletURL.setParameter(
					"struts_action", "/directory/view_user");
				portletURL.setParameter(
					"p_u_i_d", String.valueOf(user.getUserId()));
				portletURL.setPortletMode(PortletMode.VIEW);
				portletURL.setWindowState(WindowState.MAXIMIZED);

				userName = StringBundler.concat(
					"<a href=\"", portletURL.toString(), "\">",
					HtmlUtil.escape(userName), "</a>");
			}
		}
		catch (Exception e) {
		}

		return userName;
	}

	@Override
	public String getUserPassword(HttpServletRequest request) {
		request = getOriginalServletRequest(request);

		HttpSession session = request.getSession();

		return getUserPassword(session);
	}

	@Override
	public String getUserPassword(HttpSession session) {
		return (String)session.getAttribute(WebKeys.USER_PASSWORD);
	}

	@Override
	public String getUserPassword(PortletRequest portletRequest) {
		return getUserPassword(getHttpServletRequest(portletRequest));
	}

	/**
	 * @deprecated As of 7.0.0, with no direct replacement
	 */
	@Deprecated
	@Override
	public String getUserValue(long userId, String param, String defaultValue) {
		if (Validator.isNotNull(defaultValue)) {
			return defaultValue;
		}

		try {
			User user = UserLocalServiceUtil.getUserById(userId);

			return BeanPropertiesUtil.getString(user, param, defaultValue);
		}
		catch (PortalException pe) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(pe, pe);
			}

			return StringPool.BLANK;
		}
	}

	@Override
	public String getValidPortalDomain(long companyId, String domain) {
		if (_validPortalDomainCheckDisabled) {
			return domain;
		}

		for (String virtualHost : PropsValues.VIRTUAL_HOSTS_VALID_HOSTS) {
			if (StringUtil.equalsIgnoreCase(domain, virtualHost) ||
				StringUtil.wildcardMatches(
					domain, virtualHost, CharPool.QUESTION, CharPool.STAR,
					CharPool.PERCENT, false)) {

				return domain;
			}
		}

		if (_log.isWarnEnabled()) {
			_log.warn(
				StringBundler.concat(
					"Set the property \"", PropsKeys.VIRTUAL_HOSTS_VALID_HOSTS,
					"\" in portal.properties to allow \"", domain,
					"\" as a domain"));
		}

		try {
			Company company = CompanyLocalServiceUtil.getCompanyById(
				getDefaultCompanyId());

			return company.getVirtualHostname();
		}
		catch (Exception e) {
			_log.error("Unable to load default portal instance", e);
		}

		return _LOCALHOST;
	}

	@Override
	public long getValidUserId(long companyId, long userId)
		throws PortalException {

		User user = UserLocalServiceUtil.fetchUser(userId);

		if (user == null) {
			return UserLocalServiceUtil.getDefaultUserId(companyId);
		}

		if (user.getCompanyId() == companyId) {
			return user.getUserId();
		}

		return userId;
	}

	@Override
	public String getVirtualHostname(LayoutSet layoutSet) {
		String virtualHostname = layoutSet.getVirtualHostname();

		if (Validator.isNull(virtualHostname)) {
			virtualHostname = layoutSet.getCompanyFallbackVirtualHostname();
		}

		return virtualHostname;
	}

	/**
	 * @deprecated As of 7.0.0, with no direct replacement
	 */
	@Deprecated
	@Override
	public String getVirtualLayoutActualURL(
			long groupId, boolean privateLayout, String mainPath,
			String friendlyURL, Map<String, String[]> params,
			Map<String, Object> requestContext)
		throws PortalException {

		FriendlyURLResolver friendlyURLResolver =
			FriendlyURLResolverRegistryUtil.getFriendlyURLResolver(
				VirtualLayoutConstants.CANONICAL_URL_SEPARATOR);

		if (friendlyURLResolver == null) {
			return null;
		}

		HttpServletRequest request = (HttpServletRequest)requestContext.get(
			"request");

		long companyId = PortalInstances.getCompanyId(request);

		return friendlyURLResolver.getActualURL(
			companyId, groupId, privateLayout, mainPath, friendlyURL, params,
			requestContext);
	}

	/**
	 * @deprecated As of 7.0.0, with no direct replacement
	 */
	@Deprecated
	@Override
	public LayoutFriendlyURLComposite getVirtualLayoutFriendlyURLComposite(
			boolean privateLayout, String friendlyURL,
			Map<String, String[]> params, Map<String, Object> requestContext)
		throws PortalException {

		FriendlyURLResolver friendlyURLResolver =
			FriendlyURLResolverRegistryUtil.getFriendlyURLResolver(
				VirtualLayoutConstants.CANONICAL_URL_SEPARATOR);

		if (friendlyURLResolver == null) {
			return null;
		}

		HttpServletRequest request = (HttpServletRequest)requestContext.get(
			"request");

		long companyId = PortalInstances.getCompanyId(request);

		return friendlyURLResolver.getLayoutFriendlyURLComposite(
			companyId, 0, privateLayout, friendlyURL, params, requestContext);
	}

	@Override
	public String getWidgetURL(Portlet portlet, ThemeDisplay themeDisplay)
		throws PortalException {

		return getServletURL(
			portlet, PropsValues.WIDGET_SERVLET_MAPPING, themeDisplay);
	}

	@Override
	public void initCustomSQL() {
		_customSqlKeys = new String[] {
			"[$CLASS_NAME_ID_COM.LIFERAY.PORTAL.MODEL.GROUP$]",
			"[$CLASS_NAME_ID_COM.LIFERAY.PORTAL.MODEL.LAYOUT$]",
			"[$CLASS_NAME_ID_COM.LIFERAY.PORTAL.MODEL.ORGANIZATION$]",
			"[$CLASS_NAME_ID_COM.LIFERAY.PORTAL.MODEL.ROLE$]",
			"[$CLASS_NAME_ID_COM.LIFERAY.PORTAL.MODEL.TEAM$]",
			"[$CLASS_NAME_ID_COM.LIFERAY.PORTAL.MODEL.USER$]",
			"[$CLASS_NAME_ID_COM.LIFERAY.PORTAL.MODEL.USERGROUP$]",
			"[$CLASS_NAME_ID_COM.LIFERAY.PORTLET.BLOGS.MODEL.BLOGSENTRY$]",
			"[$CLASS_NAME_ID_COM.LIFERAY.PORTLET.DOCUMENTLIBRARY.MODEL." +
				"DLFILEENTRY$]",
			"[$CLASS_NAME_ID_COM.LIFERAY.PORTLET.DOCUMENTLIBRARY.MODEL." +
				"DLFOLDER$]",
			"[$CLASS_NAME_ID_COM.LIFERAY.PORTLET.MESSAGEBOARDS.MODEL." +
				"MBMESSAGE$]",
			"[$CLASS_NAME_ID_COM.LIFERAY.PORTLET.MESSAGEBOARDS.MODEL." +
				"MBTHREAD$]",
			"[$RESOURCE_SCOPE_COMPANY$]", "[$RESOURCE_SCOPE_GROUP$]",
			"[$RESOURCE_SCOPE_GROUP_TEMPLATE$]",
			"[$RESOURCE_SCOPE_INDIVIDUAL$]",
			"[$SOCIAL_RELATION_TYPE_BI_COWORKER$]",
			"[$SOCIAL_RELATION_TYPE_BI_FRIEND$]",
			"[$SOCIAL_RELATION_TYPE_BI_ROMANTIC_PARTNER$]",
			"[$SOCIAL_RELATION_TYPE_BI_SIBLING$]",
			"[$SOCIAL_RELATION_TYPE_BI_SPOUSE$]",
			"[$SOCIAL_RELATION_TYPE_UNI_CHILD$]",
			"[$SOCIAL_RELATION_TYPE_UNI_ENEMY$]",
			"[$SOCIAL_RELATION_TYPE_UNI_FOLLOWER$]",
			"[$SOCIAL_RELATION_TYPE_UNI_PARENT$]",
			"[$SOCIAL_RELATION_TYPE_UNI_SUBORDINATE$]",
			"[$SOCIAL_RELATION_TYPE_UNI_SUPERVISOR$]", "[$FALSE$]", "[$TRUE$]"
		};

		DB db = DBManagerUtil.getDB();

		Object[] customSqlValues = {
			getClassNameId(Group.class), getClassNameId(Layout.class),
			getClassNameId(Organization.class), getClassNameId(Role.class),
			getClassNameId(Team.class), getClassNameId(User.class),
			getClassNameId(UserGroup.class), getClassNameId(BlogsEntry.class),
			getClassNameId(DLFileEntry.class), getClassNameId(DLFolder.class),
			getClassNameId(MBMessage.class), getClassNameId(MBThread.class),
			ResourceConstants.SCOPE_COMPANY, ResourceConstants.SCOPE_GROUP,
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			ResourceConstants.SCOPE_INDIVIDUAL,
			SocialRelationConstants.TYPE_BI_COWORKER,
			SocialRelationConstants.TYPE_BI_FRIEND,
			SocialRelationConstants.TYPE_BI_ROMANTIC_PARTNER,
			SocialRelationConstants.TYPE_BI_SIBLING,
			SocialRelationConstants.TYPE_BI_SPOUSE,
			SocialRelationConstants.TYPE_UNI_CHILD,
			SocialRelationConstants.TYPE_UNI_ENEMY,
			SocialRelationConstants.TYPE_UNI_FOLLOWER,
			SocialRelationConstants.TYPE_UNI_PARENT,
			SocialRelationConstants.TYPE_UNI_SUBORDINATE,
			SocialRelationConstants.TYPE_UNI_SUPERVISOR, db.getTemplateFalse(),
			db.getTemplateTrue()
		};

		_customSqlValues = ArrayUtil.toStringArray(customSqlValues);
	}

	@Override
	public User initUser(HttpServletRequest request) throws Exception {
		User user = null;

		try {
			user = getUser(request);
		}
		catch (NoSuchUserException nsue) {
			if (_log.isWarnEnabled()) {
				_log.warn(nsue.getMessage());
			}

			long userId = getUserId(request);

			if (userId > 0) {
				HttpSession session = request.getSession();

				session.invalidate();
			}

			throw nsue;
		}

		if (user != null) {
			return user;
		}

		Company company = getCompany(request);

		return company.getDefaultUser();
	}

	/**
	 * @deprecated As of 7.0.0, with no direct replacement
	 */
	@Deprecated
	@Override
	public void invokeTaglibDiscussion(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		_editDiscussionStrutsAction.execute(
			getHttpServletRequest(actionRequest),
			getHttpServletResponse(actionResponse));
	}

	/**
	 * @deprecated As of 7.0.0, with no direct replacement
	 */
	@Deprecated
	@Override
	public void invokeTaglibDiscussionPagination(
			PortletConfig portletConfig, ResourceRequest resourceRequest,
			ResourceResponse resourceResponse)
		throws IOException, PortletException {

		try {
			_getCommentsStrutsAction.execute(
				getHttpServletRequest(resourceRequest),
				getHttpServletResponse(resourceResponse));
		}
		catch (IOException | PortletException | RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			throw new PortletException(e);
		}
	}

	@Override
	public boolean isCDNDynamicResourcesEnabled(HttpServletRequest request)
		throws PortalException {

		Company company = getCompany(request);

		return isCDNDynamicResourcesEnabled(company.getCompanyId());
	}

	@Override
	public boolean isCDNDynamicResourcesEnabled(long companyId) {
		try {
			return PrefsPropsUtil.getBoolean(
				companyId, PropsKeys.CDN_DYNAMIC_RESOURCES_ENABLED,
				PropsValues.CDN_DYNAMIC_RESOURCES_ENABLED);
		}
		catch (SystemException se) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(se, se);
			}
		}

		return PropsValues.CDN_DYNAMIC_RESOURCES_ENABLED;
	}

	@Override
	public boolean isCompanyAdmin(User user) throws Exception {
		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		return permissionChecker.isCompanyAdmin();
	}

	@Override
	public boolean isCompanyControlPanelPortlet(
			String portletId, String category, ThemeDisplay themeDisplay)
		throws PortalException {

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (permissionChecker.isCompanyAdmin()) {
			return true;
		}

		Group companyGroup = GroupLocalServiceUtil.getCompanyGroup(
			themeDisplay.getCompanyId());

		themeDisplay.setScopeGroupId(companyGroup.getGroupId());

		return isControlPanelPortlet(portletId, category, themeDisplay);
	}

	@Override
	public boolean isCompanyControlPanelPortlet(
			String portletId, ThemeDisplay themeDisplay)
		throws PortalException {

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (permissionChecker.isCompanyAdmin()) {
			return true;
		}

		Group companyGroup = GroupLocalServiceUtil.getCompanyGroup(
			themeDisplay.getCompanyId());

		themeDisplay.setScopeGroupId(companyGroup.getGroupId());

		return isControlPanelPortlet(portletId, themeDisplay);
	}

	@Override
	public boolean isControlPanelPortlet(
		String portletId, String category, ThemeDisplay themeDisplay) {

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			themeDisplay.getCompanyId(), portletId);

		String controlPanelEntryCategory =
			portlet.getControlPanelEntryCategory();

		if (controlPanelEntryCategory.equals(category) ||
			(category.endsWith(StringPool.PERIOD) &&
			 StringUtil.startsWith(controlPanelEntryCategory, category))) {

			return isControlPanelPortlet(portletId, themeDisplay);
		}

		return false;
	}

	@Override
	public boolean isControlPanelPortlet(
		String portletId, ThemeDisplay themeDisplay) {

		try {
			return PortletPermissionUtil.hasControlPanelAccessPermission(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(), portletId);
		}
		catch (PortalException pe) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to check control panel access permission", pe);
			}
		}

		return false;
	}

	@Override
	public boolean isCustomPortletMode(PortletMode portletMode) {
		if (LiferayPortletMode.ABOUT.equals(portletMode) ||
			LiferayPortletMode.CONFIG.equals(portletMode) ||
			LiferayPortletMode.EDIT.equals(portletMode) ||
			LiferayPortletMode.EDIT_DEFAULTS.equals(portletMode) ||
			LiferayPortletMode.EDIT_GUEST.equals(portletMode) ||
			LiferayPortletMode.HELP.equals(portletMode) ||
			LiferayPortletMode.PREVIEW.equals(portletMode) ||
			LiferayPortletMode.PRINT.equals(portletMode) ||
			LiferayPortletMode.VIEW.equals(portletMode)) {

			return false;
		}

		return true;
	}

	@Override
	public boolean isForwardedSecure(HttpServletRequest request) {
		if (PropsValues.WEB_SERVER_FORWARDED_PROTOCOL_ENABLED) {
			String forwardedProtocol = request.getHeader(
				PropsValues.WEB_SERVER_FORWARDED_PROTOCOL_HEADER);

			if (Validator.isNotNull(forwardedProtocol) &&
				Objects.equals(Http.HTTPS, forwardedProtocol)) {

				return true;
			}
		}

		return request.isSecure();
	}

	@Override
	public boolean isGroupAdmin(User user, long groupId) throws Exception {
		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		return permissionChecker.isGroupAdmin(groupId);
	}

	@Override
	public boolean isGroupFriendlyURL(
		String fullURL, String groupFriendlyURL, String layoutFriendlyURL) {

		if (fullURL.endsWith(groupFriendlyURL) &&
			!fullURL.endsWith(groupFriendlyURL.concat(layoutFriendlyURL))) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isGroupOwner(User user, long groupId) throws Exception {
		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		return permissionChecker.isGroupOwner(groupId);
	}

	@Override
	public boolean isLayoutDescendant(Layout layout, long layoutId)
		throws PortalException {

		if (layout.getLayoutId() == layoutId) {
			return true;
		}

		for (Layout childLayout : layout.getChildren()) {
			if (isLayoutDescendant(childLayout, layoutId)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isLayoutSitemapable(Layout layout) {
		if (layout.isPrivateLayout()) {
			return false;
		}

		LayoutType layoutType = layout.getLayoutType();

		return layoutType.isSitemapable();
	}

	@Override
	public boolean isLoginRedirectRequired(HttpServletRequest request) {
		if (PropsValues.COMPANY_SECURITY_AUTH_REQUIRES_HTTPS &&
			!request.isSecure()) {

			return true;
		}

		long companyId = getCompanyId(request);

		if (SSOUtil.isLoginRedirectRequired(companyId)) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isMethodGet(PortletRequest portletRequest) {
		HttpServletRequest request = getHttpServletRequest(portletRequest);

		String method = GetterUtil.getString(request.getMethod());

		if (StringUtil.equalsIgnoreCase(method, HttpMethods.GET)) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isMethodPost(PortletRequest portletRequest) {
		HttpServletRequest request = getHttpServletRequest(portletRequest);

		String method = GetterUtil.getString(request.getMethod());

		if (StringUtil.equalsIgnoreCase(method, HttpMethods.POST)) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isMultipartRequest(HttpServletRequest request) {
		String contentType = request.getHeader(HttpHeaders.CONTENT_TYPE);

		if ((contentType != null) &&
			contentType.startsWith(ContentTypes.MULTIPART_FORM_DATA)) {

			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isOmniadmin(long userId) {
		return OmniadminUtil.isOmniadmin(userId);
	}

	@Override
	public boolean isOmniadmin(User user) {
		return OmniadminUtil.isOmniadmin(user);
	}

	@Override
	public boolean isReservedParameter(String name) {
		return _reservedParams.contains(name);
	}

	@Override
	public boolean isRightToLeft(HttpServletRequest request) {
		String languageId = LanguageUtil.getLanguageId(request);

		Locale locale = LocaleUtil.fromLanguageId(languageId);

		String langDir = LanguageUtil.get(locale, LanguageConstants.KEY_DIR);

		return langDir.equals("rtl");
	}

	@Override
	public boolean isRSSFeedsEnabled() {
		return PropsValues.RSS_FEEDS_ENABLED;
	}

	@Override
	public boolean isSecure(HttpServletRequest request) {
		if (PropsValues.WEB_SERVER_FORWARDED_PROTOCOL_ENABLED) {
			return isForwardedSecure(request);
		}

		if (!PropsValues.COMPANY_SECURITY_AUTH_REQUIRES_HTTPS ||
			PropsValues.SESSION_ENABLE_PHISHING_PROTECTION) {

			return request.isSecure();
		}

		HttpSession session = request.getSession();

		if (session == null) {
			return request.isSecure();
		}

		Boolean httpsInitial = (Boolean)session.getAttribute(
			WebKeys.HTTPS_INITIAL);

		if ((httpsInitial == null) || httpsInitial) {
			return request.isSecure();
		}

		return false;
	}

	@Override
	public boolean isSkipPortletContentProcessing(
			Group group, HttpServletRequest httpServletRequest,
			LayoutTypePortlet layoutTypePortlet, PortletDisplay portletDisplay,
			String portletName)
		throws Exception {

		boolean skipPortletContentRendering = isSkipPortletContentRendering(
			group, layoutTypePortlet, portletDisplay, portletName);

		if (!skipPortletContentRendering) {
			return false;
		}

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			group.getCompanyId(), portletDisplay.getId());
		ServletContext servletContext =
			(ServletContext)httpServletRequest.getAttribute(WebKeys.CTX);

		InvokerPortlet invokerPortlet = PortletInstanceFactoryUtil.create(
			portlet, servletContext);

		if (invokerPortlet.isStrutsBridgePortlet() ||
			invokerPortlet.isStrutsPortlet()) {

			return false;
		}

		return true;
	}

	@Override
	public boolean isSkipPortletContentRendering(
		Group group, LayoutTypePortlet layoutTypePortlet,
		PortletDisplay portletDisplay, String portletName) {

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			group.getCompanyId(), portletDisplay.getId());

		if (portlet.isSystem()) {
			return false;
		}

		if (group.isLayoutPrototype() &&
			layoutTypePortlet.hasPortletId(portletDisplay.getId()) &&
			portletDisplay.isModeView() &&
			!portletName.equals(PortletKeys.NESTED_PORTLETS)) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isSystemGroup(String groupName) {
		if (groupName == null) {
			return false;
		}

		groupName = groupName.trim();

		int pos = Arrays.binarySearch(
			_sortedSystemGroups, groupName, new StringComparator());

		if (pos >= 0) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isSystemRole(String roleName) {
		if (roleName == null) {
			return false;
		}

		roleName = roleName.trim();

		int pos = Arrays.binarySearch(
			_sortedSystemRoles, roleName, new StringComparator());

		if (pos >= 0) {
			return true;
		}

		pos = Arrays.binarySearch(
			_sortedSystemSiteRoles, roleName, new StringComparator());

		if (pos >= 0) {
			return true;
		}

		pos = Arrays.binarySearch(
			_sortedSystemOrganizationRoles, roleName, new StringComparator());

		if (pos >= 0) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isUpdateAvailable() {
		return PluginPackageUtil.isUpdateAvailable();
	}

	@Override
	public boolean isValidResourceId(String resourceId) {
		if (Validator.isNull(resourceId)) {
			return true;
		}

		Matcher matcher = _bannedResourceIdPattern.matcher(resourceId);

		if (matcher.matches()) {
			return false;
		}

		return true;
	}

	@Override
	public boolean removePortalInetSocketAddressEventListener(
		PortalInetSocketAddressEventListener
			portalInetSocketAddressEventListener) {

		return _portalInetSocketAddressEventListeners.remove(
			portalInetSocketAddressEventListener);
	}

	/**
	 * @deprecated As of 7.0.0, replaced by {@link
	 *             #removePortalInetSocketAddressEventListener(
	 *             PortalInetSocketAddressEventListener)}
	 */
	@Deprecated
	@Override
	public void removePortalPortEventListener(
		PortalPortEventListener portalPortEventListener) {

		_portalPortEventListeners.remove(portalPortEventListener);
	}

	@Override
	public void resetCDNHosts() {
		_cdnHostHttpMap.clear();
		_cdnHostHttpsMap.clear();

		if (!ClusterInvokeThreadLocal.isEnabled()) {
			return;
		}

		ClusterRequest clusterRequest = ClusterRequest.createMulticastRequest(
			_resetCDNHostsMethodHandler, true);

		try {
			ClusterExecutorUtil.execute(clusterRequest);
		}
		catch (Exception e) {
			_log.error("Unable to clear cluster wide CDN hosts", e);
		}
	}

	@Override
	public String resetPortletParameters(String url, String portletId) {
		if (Validator.isNull(url) || Validator.isNull(portletId)) {
			return url;
		}

		String portletNamespace = getPortletNamespace(portletId);

		Map<String, String[]> parameterMap = HttpUtil.getParameterMap(url);

		for (String name : parameterMap.keySet()) {
			if (name.startsWith(portletNamespace)) {
				url = HttpUtil.removeParameter(url, name);
			}
		}

		return url;
	}

	@Override
	public void sendError(
			Exception e, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws IOException {

		sendError(0, e, actionRequest, actionResponse);
	}

	@Override
	public void sendError(
			Exception e, HttpServletRequest request,
			HttpServletResponse response)
		throws IOException, ServletException {

		sendError(0, e, request, response);
	}

	@Override
	public void sendError(
			int status, Exception e, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws IOException {

		StringBundler sb = new StringBundler(7);

		sb.append(_pathMain);
		sb.append("/portal/status?status=");
		sb.append(status);
		sb.append("&exception=");

		Class<?> clazz = e.getClass();

		sb.append(clazz.getName());

		sb.append("&previousURL=");
		sb.append(URLCodec.encodeURL(getCurrentURL(actionRequest)));

		actionResponse.sendRedirect(sb.toString());
	}

	@Override
	public void sendError(
			int status, Exception e, HttpServletRequest request,
			HttpServletResponse response)
		throws IOException, ServletException {

		if (_log.isDebugEnabled()) {
			String currentURL = (String)request.getAttribute(
				WebKeys.CURRENT_URL);

			_log.debug(
				StringBundler.concat(
					"Current URL ", currentURL, " generates exception: ",
					e.getMessage()));
		}

		if (e instanceof NoSuchImageException) {
			if (_logWebServerServlet.isWarnEnabled()) {
				_logWebServerServlet.warn(e, e);
			}
		}
		else if ((e instanceof PortalException) && _log.isDebugEnabled()) {
			if (e instanceof NoSuchLayoutException ||
				e instanceof PrincipalException) {

				String msg = e.getMessage();

				if (Validator.isNotNull(msg)) {
					_log.debug(msg);
				}
			}
			else {
				_log.debug(e, e);
			}
		}
		else if ((e instanceof SystemException) && _log.isWarnEnabled()) {
			_log.warn(e, e);
		}

		if (response.isCommitted()) {
			return;
		}

		if (status == 0) {
			if (e instanceof PrincipalException) {
				status = HttpServletResponse.SC_FORBIDDEN;
			}
			else {
				Class<?> clazz = e.getClass();

				String name = clazz.getName();

				name = name.substring(name.lastIndexOf(CharPool.PERIOD) + 1);

				if (name.startsWith("NoSuch") && name.endsWith("Exception")) {
					status = HttpServletResponse.SC_NOT_FOUND;
				}
			}

			if (status == 0) {
				status = HttpServletResponse.SC_BAD_REQUEST;
			}
		}

		String redirect = null;

		if ((e instanceof NoSuchGroupException) &&
			Validator.isNotNull(
				PropsValues.SITES_FRIENDLY_URL_PAGE_NOT_FOUND)) {

			redirect = PropsValues.SITES_FRIENDLY_URL_PAGE_NOT_FOUND;
		}
		else if ((e instanceof NoSuchLayoutException) &&
				 Validator.isNotNull(
					 PropsValues.LAYOUT_FRIENDLY_URL_PAGE_NOT_FOUND)) {

			redirect = PropsValues.LAYOUT_FRIENDLY_URL_PAGE_NOT_FOUND;
		}
		else if (PropsValues.LAYOUT_SHOW_HTTP_STATUS) {
			redirect = PATH_MAIN + "/portal/status";
		}

		if (Objects.equals(redirect, request.getRequestURI())) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to redirect to missing URI: " + redirect);
			}

			redirect = null;
		}

		if (Validator.isNotNull(redirect)) {
			HttpSession session = PortalSessionThreadLocal.getHttpSession();

			if (session == null) {
				session = request.getSession();
			}

			response.setStatus(status);

			SessionErrors.add(session, e.getClass(), e);

			ServletContext servletContext = session.getServletContext();

			RequestDispatcher requestDispatcher =
				servletContext.getRequestDispatcher(redirect);

			if (requestDispatcher != null) {
				requestDispatcher.forward(request, response);
			}
		}
		else if (e != null) {
			response.sendError(status, e.getMessage());
		}
		else {
			String currentURL = (String)request.getAttribute(
				WebKeys.CURRENT_URL);

			response.sendError(status, "Current URL " + currentURL);
		}
	}

	@Override
	public void sendRSSFeedsDisabledError(
			HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {

		sendError(
			HttpServletResponse.SC_NOT_FOUND, new RSSFeedException(), request,
			response);
	}

	@Override
	public void sendRSSFeedsDisabledError(
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws IOException, ServletException {

		HttpServletRequest request = getHttpServletRequest(portletRequest);
		HttpServletResponse response = getHttpServletResponse(portletResponse);

		sendRSSFeedsDisabledError(request, response);
	}

	@Override
	public void setPageDescription(
		String description, HttpServletRequest request) {

		ListMergeable<String> descriptionListMergeable = new ListMergeable<>();

		descriptionListMergeable.add(description);

		request.setAttribute(
			WebKeys.PAGE_DESCRIPTION, descriptionListMergeable);
	}

	@Override
	public void setPageKeywords(String keywords, HttpServletRequest request) {
		request.removeAttribute(WebKeys.PAGE_KEYWORDS);

		addPageKeywords(keywords, request);
	}

	@Override
	public void setPageSubtitle(String subtitle, HttpServletRequest request) {
		ListMergeable<String> subtitleListMergeable = new ListMergeable<>();

		subtitleListMergeable.add(subtitle);

		request.setAttribute(WebKeys.PAGE_SUBTITLE, subtitleListMergeable);
	}

	@Override
	public void setPageTitle(String title, HttpServletRequest request) {
		ListMergeable<String> titleListMergeable = new ListMergeable<>();

		titleListMergeable.add(title);

		request.setAttribute(WebKeys.PAGE_TITLE, titleListMergeable);
	}

	@Override
	public void setPortalInetSocketAddresses(HttpServletRequest request) {
		boolean secure = request.isSecure();

		if ((secure && (_securePortalLocalInetSocketAddress.get() != null) &&
			 (_securePortalServerInetSocketAddress.get() != null)) ||
			(!secure && (_portalLocalInetSocketAddress.get() != null) &&
			 (_portalServerInetSocketAddress.get() != null))) {

			return;
		}

		InetAddress localInetAddress = null;
		InetAddress serverInetAddress = null;

		try {
			localInetAddress = InetAddress.getByName(request.getLocalAddr());
			serverInetAddress = InetAddress.getByName(request.getServerName());
		}
		catch (UnknownHostException uhe) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to resolve portal host", uhe);
			}

			return;
		}

		InetSocketAddress localInetSocketAddress = new InetSocketAddress(
			localInetAddress, request.getLocalPort());
		InetSocketAddress serverInetSocketAddress = new InetSocketAddress(
			serverInetAddress, request.getServerPort());

		if (secure) {
			if (_securePortalLocalInetSocketAddress.compareAndSet(
					null, localInetSocketAddress)) {

				notifyPortalInetSocketAddressEventListeners(
					localInetSocketAddress, true, true);
			}

			if (_securePortalServerInetSocketAddress.compareAndSet(
					null, serverInetSocketAddress)) {

				notifyPortalInetSocketAddressEventListeners(
					serverInetSocketAddress, false, true);
			}
		}
		else {
			if (_portalLocalInetSocketAddress.compareAndSet(
					null, localInetSocketAddress)) {

				notifyPortalInetSocketAddressEventListeners(
					localInetSocketAddress, true, false);
			}

			if (_portalServerInetSocketAddress.compareAndSet(
					null, serverInetSocketAddress)) {

				notifyPortalInetSocketAddressEventListeners(
					serverInetSocketAddress, false, false);
			}
		}
	}

	/**
	 * Sets the port obtained on the first request to the portal.
	 *
	 * @deprecated As of 7.0.0, replaced by {@link
	 *             #setPortalInetSocketAddresses(HttpServletRequest)}
	 */
	@Deprecated
	@Override
	public void setPortalPort(HttpServletRequest request) {
		if (request.isSecure()) {
			if (_securePortalPort.get() == -1) {
				int securePortalPort = request.getServerPort();

				if (_securePortalPort.compareAndSet(-1, securePortalPort) &&
					StringUtil.equalsIgnoreCase(
						Http.HTTPS, PropsValues.WEB_SERVER_PROTOCOL)) {

					notifyPortalPortEventListeners(securePortalPort);
				}
			}
		}
		else {
			if (_portalPort.get() == -1) {
				int portalPort = request.getServerPort();

				if (_portalPort.compareAndSet(-1, portalPort)) {
					notifyPortalPortEventListeners(portalPort);
				}
			}
		}
	}

	@Override
	public void storePreferences(PortletPreferences portletPreferences)
		throws IOException, ValidatorException {

		PortletPreferencesWrapper portletPreferencesWrapper =
			(PortletPreferencesWrapper)portletPreferences;

		PortletPreferencesImpl portletPreferencesImpl =
			portletPreferencesWrapper.getPortletPreferencesImpl();

		portletPreferencesImpl.store();
	}

	@Override
	public String[] stripURLAnchor(String url, String separator) {
		String anchor = StringPool.BLANK;

		int pos = url.indexOf(separator);

		if (pos != -1) {
			anchor = url.substring(pos);
			url = url.substring(0, pos);
		}

		return new String[] {url, anchor};
	}

	@Override
	public String transformCustomSQL(String sql) {
		if ((_customSqlKeys == null) || (_customSqlValues == null)) {
			initCustomSQL();
		}

		return StringUtil.replace(sql, _customSqlKeys, _customSqlValues);
	}

	@Override
	public String transformSQL(String sql) {
		return SQLTransformer.transform(sql);
	}

	@Override
	public void updateImageId(
			BaseModel<?> baseModel, boolean hasImage, byte[] bytes,
			String fieldName, long maxSize, int maxHeight, int maxWidth)
		throws PortalException {

		long imageId = BeanPropertiesUtil.getLong(baseModel, fieldName);

		if (!hasImage) {
			if (imageId > 0) {
				ImageLocalServiceUtil.deleteImage(imageId);

				BeanPropertiesUtil.setProperty(baseModel, fieldName, 0);
			}

			return;
		}

		if (ArrayUtil.isEmpty(bytes)) {
			return;
		}

		if ((maxSize > 0) && (bytes.length > maxSize)) {
			throw new ImageSizeException();
		}

		if ((maxHeight > 0) || (maxWidth > 0)) {
			try {
				ImageBag imageBag = ImageToolUtil.read(bytes);

				RenderedImage renderedImage = imageBag.getRenderedImage();

				if (renderedImage == null) {
					throw new ImageTypeException();
				}

				renderedImage = ImageToolUtil.scale(
					renderedImage, maxHeight, maxWidth);

				bytes = ImageToolUtil.getBytes(
					renderedImage, imageBag.getType());
			}
			catch (IOException ioe) {
				throw new ImageSizeException(ioe);
			}
		}

		Image image = ImageLocalServiceUtil.moveImage(imageId, bytes);

		BeanPropertiesUtil.setProperty(
			baseModel, fieldName, image.getImageId());
	}

	@Override
	public PortletMode updatePortletMode(
			String portletId, User user, Layout layout, PortletMode portletMode,
			HttpServletRequest request)
		throws PortalException {

		LayoutTypePortlet layoutType =
			(LayoutTypePortlet)layout.getLayoutType();

		if ((portletMode == null) || Validator.isNull(portletMode.toString())) {
			if (layoutType.hasModeAboutPortletId(portletId)) {
				return LiferayPortletMode.ABOUT;
			}
			else if (layoutType.hasModeConfigPortletId(portletId)) {
				return LiferayPortletMode.CONFIG;
			}
			else if (layoutType.hasModeEditPortletId(portletId)) {
				return PortletMode.EDIT;
			}
			else if (layoutType.hasModeEditDefaultsPortletId(portletId)) {
				return LiferayPortletMode.EDIT_DEFAULTS;
			}
			else if (layoutType.hasModeEditGuestPortletId(portletId)) {
				return LiferayPortletMode.EDIT_GUEST;
			}
			else if (layoutType.hasModeHelpPortletId(portletId)) {
				return PortletMode.HELP;
			}
			else if (layoutType.hasModePreviewPortletId(portletId)) {
				return LiferayPortletMode.PREVIEW;
			}
			else if (layoutType.hasModePrintPortletId(portletId)) {
				return LiferayPortletMode.PRINT;
			}
			else {
				return PortletMode.VIEW;
			}
		}
		else {
			ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
				WebKeys.THEME_DISPLAY);

			PermissionChecker permissionChecker =
				themeDisplay.getPermissionChecker();

			Portlet portlet = PortletLocalServiceUtil.getPortletById(
				getCompanyId(request), portletId);

			boolean updateLayout = false;

			if (portletMode.equals(LiferayPortletMode.ABOUT) &&
				!layoutType.hasModeAboutPortletId(portletId)) {

				layoutType.addModeAboutPortletId(portletId);

				updateLayout = true;
			}
			else if (portletMode.equals(LiferayPortletMode.CONFIG) &&
					 !layoutType.hasModeConfigPortletId(portletId) &&
					 PortletPermissionUtil.contains(
						 permissionChecker, getScopeGroupId(request), layout,
						 portlet, ActionKeys.CONFIGURATION)) {

				layoutType.addModeConfigPortletId(portletId);

				updateLayout = true;
			}
			else if (portletMode.equals(PortletMode.EDIT) &&
					 !layoutType.hasModeEditPortletId(portletId) &&
					 PortletPermissionUtil.contains(
						 permissionChecker, getScopeGroupId(request), layout,
						 portlet, ActionKeys.PREFERENCES)) {

				layoutType.addModeEditPortletId(portletId);

				updateLayout = true;
			}
			else if (portletMode.equals(LiferayPortletMode.EDIT_DEFAULTS) &&
					 !layoutType.hasModeEditDefaultsPortletId(portletId) &&
					 PortletPermissionUtil.contains(
						 permissionChecker, getScopeGroupId(request), layout,
						 portlet, ActionKeys.PREFERENCES)) {

				layoutType.addModeEditDefaultsPortletId(portletId);

				updateLayout = true;
			}
			else if (portletMode.equals(LiferayPortletMode.EDIT_GUEST) &&
					 !layoutType.hasModeEditGuestPortletId(portletId) &&
					 PortletPermissionUtil.contains(
						 permissionChecker, getScopeGroupId(request), layout,
						 portlet, ActionKeys.GUEST_PREFERENCES)) {

				layoutType.addModeEditGuestPortletId(portletId);

				updateLayout = true;
			}
			else if (portletMode.equals(PortletMode.HELP) &&
					 !layoutType.hasModeHelpPortletId(portletId)) {

				layoutType.addModeHelpPortletId(portletId);

				updateLayout = true;
			}
			else if (portletMode.equals(LiferayPortletMode.PREVIEW) &&
					 !layoutType.hasModePreviewPortletId(portletId)) {

				layoutType.addModePreviewPortletId(portletId);

				updateLayout = true;
			}
			else if (portletMode.equals(LiferayPortletMode.PRINT) &&
					 !layoutType.hasModePrintPortletId(portletId)) {

				layoutType.addModePrintPortletId(portletId);

				updateLayout = true;
			}
			else if (portletMode.equals(PortletMode.VIEW) &&
					 !layoutType.hasModeViewPortletId(portletId)) {

				layoutType.removeModesPortletId(portletId);

				updateLayout = true;
			}
			else if (layoutType instanceof LayoutTypePortletImpl) {
				LayoutTypePortletImpl layoutTypePortletImpl =
					(LayoutTypePortletImpl)layoutType;

				if (isCustomPortletMode(portletMode) &&
					!layoutTypePortletImpl.hasModeCustomPortletId(
						portletId, portletMode.toString())) {

					layoutTypePortletImpl.addModeCustomPortletId(
						portletId, portletMode.toString());

					updateLayout = true;
				}
			}

			if (updateLayout &&
				PortletPermissionUtil.contains(
					permissionChecker, getScopeGroupId(request), layout,
					portlet, ActionKeys.VIEW)) {

				LayoutClone layoutClone = LayoutCloneFactory.getInstance();

				if (layoutClone != null) {
					layoutClone.update(
						request, layout.getPlid(), layout.getTypeSettings());
				}
			}

			return portletMode;
		}
	}

	@Override
	public String updateRedirect(
		String redirect, String oldPath, String newPath) {

		if (Validator.isNull(redirect) || (oldPath == null) ||
			oldPath.equals(newPath)) {

			return redirect;
		}

		String queryString = HttpUtil.getQueryString(redirect);

		String redirectParam = HttpUtil.getParameter(
			redirect, "redirect", false);

		if (Validator.isNotNull(redirectParam)) {
			String newRedirectParam = StringUtil.replace(
				redirectParam, URLCodec.encodeURL(oldPath),
				URLCodec.encodeURL(newPath));

			queryString = StringUtil.replace(
				queryString, redirectParam, newRedirectParam);
		}

		String redirectPath = HttpUtil.getPath(redirect);

		int pos = redirect.indexOf(redirectPath);

		String prefix = redirect.substring(0, pos);

		pos = redirectPath.lastIndexOf(oldPath);

		if (pos != -1) {
			prefix += redirectPath.substring(0, pos);

			String suffix = redirectPath.substring(pos + oldPath.length());

			redirect = prefix + newPath + suffix;
		}
		else {
			redirect = prefix + redirectPath;
		}

		if (Validator.isNotNull(queryString)) {
			redirect += StringPool.QUESTION + queryString;
		}

		return redirect;
	}

	@Override
	public WindowState updateWindowState(
		String portletId, User user, Layout layout, WindowState windowState,
		HttpServletRequest request) {

		LayoutTypePortlet layoutType =
			(LayoutTypePortlet)layout.getLayoutType();

		if ((windowState == null) || Validator.isNull(windowState.toString())) {
			if (layoutType.hasStateMaxPortletId(portletId)) {
				windowState = WindowState.MAXIMIZED;
			}
			else if (layoutType.hasStateMinPortletId(portletId)) {
				windowState = WindowState.MINIMIZED;
			}
			else {
				windowState = WindowState.NORMAL;
			}
		}
		else {
			boolean updateLayout = false;

			if (windowState.equals(WindowState.MAXIMIZED) &&
				!layoutType.hasStateMaxPortletId(portletId)) {

				layoutType.addStateMaxPortletId(portletId);

				if (PropsValues.LAYOUT_REMEMBER_MAXIMIZED_WINDOW_STATE) {
					updateLayout = true;
				}
			}
			else if (windowState.equals(WindowState.MINIMIZED) &&
					 !layoutType.hasStateMinPortletId(portletId)) {

				layoutType.addStateMinPortletId(portletId);

				updateLayout = true;
			}
			else if (windowState.equals(WindowState.NORMAL) &&
					 !layoutType.hasStateNormalPortletId(portletId)) {

				layoutType.removeStatesPortletId(portletId);

				updateLayout = true;
			}

			if (updateLayout) {
				LayoutClone layoutClone = LayoutCloneFactory.getInstance();

				if (layoutClone != null) {
					layoutClone.update(
						request, layout.getPlid(), layout.getTypeSettings());
				}
			}
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		themeDisplay.setStateExclusive(
			windowState.equals(LiferayWindowState.EXCLUSIVE));
		themeDisplay.setStateMaximized(
			windowState.equals(WindowState.MAXIMIZED));
		themeDisplay.setStatePopUp(
			windowState.equals(LiferayWindowState.POP_UP));

		request.setAttribute(WebKeys.WINDOW_STATE, windowState);

		return windowState;
	}

	/**
	 * @deprecated As of 7.0.0
	 */
	@Deprecated
	protected void addDefaultResource(
			long companyId, Layout layout, Portlet portlet,
			boolean portletActions)
		throws PortalException {

		long groupId = getScopeGroupId(layout, portlet.getPortletId());

		addRootModelResource(companyId, groupId, portlet);
	}

	/**
	 * @deprecated As of 7.0.0
	 */
	@Deprecated
	protected void addDefaultResource(
			long companyId, long groupId, Layout layout, Portlet portlet,
			boolean portletActions)
		throws PortalException {

		addRootModelResource(companyId, groupId, portlet);
	}

	/**
	 * @deprecated As of 7.0.0, replaced by {@link #addRootModelResource(long,
	 *             long, String)}
	 */
	@Deprecated
	protected void addRootModelResource(
			long companyId, long groupId, Portlet portlet)
		throws PortalException {

		String name = ResourceActionsUtil.getPortletBaseResource(
			portlet.getRootPortletId());

		if (Validator.isNull(name)) {
			return;
		}

		addRootModelResource(companyId, groupId, name);
	}

	protected void addRootModelResource(
			long companyId, long groupId, String name)
		throws PortalException {

		Group group = GroupLocalServiceUtil.fetchGroup(groupId);

		if ((group != null) && group.isStagingGroup()) {
			groupId = group.getLiveGroupId();
		}

		String primaryKey = String.valueOf(groupId);

		int count =
			ResourcePermissionLocalServiceUtil.getResourcePermissionsCount(
				companyId, name, ResourceConstants.SCOPE_INDIVIDUAL,
				primaryKey);

		if (count > 0) {
			return;
		}

		ResourceLocalServiceUtil.addResources(
			companyId, groupId, 0, name, primaryKey, false, true, true);
	}

	protected String buildI18NPath(Locale locale) {
		String languageId = LocaleUtil.toLanguageId(locale);

		return _buildI18NPath(languageId, locale);
	}

	protected Set<Group> doGetAncestorSiteGroups(
			long groupId, boolean checkContentSharingWithChildrenEnabled)
		throws PortalException {

		Group siteGroup = _getSiteGroup(groupId);

		if (siteGroup == null) {
			return Collections.emptySet();
		}

		Set<Group> groups = null;

		for (Group group : siteGroup.getAncestors()) {
			if (checkContentSharingWithChildrenEnabled &&
				!SitesUtil.isContentSharingWithChildrenEnabled(group)) {

				continue;
			}

			if (groups == null) {
				groups = new LinkedHashSet<>();
			}

			groups.add(group);
		}

		if (!siteGroup.isCompany()) {
			ThreadLocalCache<Group> threadLocalCache =
				ThreadLocalCacheManager.getThreadLocalCache(
					Lifecycle.REQUEST, Company.class.getName());

			String cacheKey = StringUtil.toHexString(siteGroup.getCompanyId());

			Group companyGroup = threadLocalCache.get(cacheKey);

			if (companyGroup == null) {
				companyGroup = GroupLocalServiceUtil.fetchCompanyGroup(
					siteGroup.getCompanyId());

				threadLocalCache.put(cacheKey, companyGroup);
			}

			if (companyGroup != null) {
				if (groups == null) {
					return Collections.singleton(companyGroup);
				}

				groups.add(companyGroup);

				return groups;
			}
		}

		if (groups == null) {
			return Collections.emptySet();
		}

		return groups;
	}

	protected Group doGetCurrentSiteGroup(long groupId) throws PortalException {
		Group siteGroup = _getSiteGroup(groupId);

		if (!siteGroup.isLayoutPrototype()) {
			return siteGroup;
		}

		return null;
	}

	protected long doGetPlidFromPortletId(
		long groupId, boolean privateLayout, String portletId) {

		long scopeGroupId = groupId;

		try {
			Group group = GroupLocalServiceUtil.getGroup(groupId);

			if (group.isLayout()) {
				Layout scopeLayout = LayoutLocalServiceUtil.getLayout(
					group.getClassPK());

				groupId = scopeLayout.getGroupId();
			}
		}
		catch (Exception e) {
		}

		long plid = LayoutConstants.DEFAULT_PLID;

		List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(
			groupId, privateLayout, LayoutConstants.TYPE_PORTLET);

		for (Layout layout : layouts) {
			LayoutTypePortlet layoutTypePortlet =
				(LayoutTypePortlet)layout.getLayoutType();

			if (layoutTypePortlet.hasPortletId(portletId, true)) {
				if (getScopeGroupId(layout, portletId) == scopeGroupId) {
					plid = layout.getPlid();

					break;
				}
			}
		}

		return plid;
	}

	protected List<Portlet> filterControlPanelPortlets(
		Set<Portlet> portlets, ThemeDisplay themeDisplay) {

		List<Portlet> filteredPortlets = new ArrayList<>(portlets);

		Iterator<Portlet> itr = filteredPortlets.iterator();

		while (itr.hasNext()) {
			Portlet portlet = itr.next();

			try {
				if (!portlet.isActive() || portlet.isInstanceable() ||
					!PortletPermissionUtil.hasControlPanelAccessPermission(
						themeDisplay.getPermissionChecker(),
						themeDisplay.getScopeGroupId(), portlet)) {

					itr.remove();
				}
			}
			catch (Exception e) {
				_log.error(e, e);

				itr.remove();
			}
		}

		return filteredPortlets;
	}

	protected Locale getAvailableLocale(long groupId, Locale locale) {
		if (Validator.isNull(locale.getCountry())) {

			// Locales must contain a country code

			locale = LanguageUtil.getLocale(locale.getLanguage());
		}

		if (!LanguageUtil.isAvailableLocale(groupId, locale)) {
			return null;
		}

		return locale;
	}

	protected Layout getBrowsableLayout(Layout layout) {
		LayoutTypeController layoutTypeController =
			LayoutTypeControllerTracker.getLayoutTypeController(
				layout.getType());

		if (layoutTypeController.isBrowsable()) {
			return layout;
		}

		Layout browsableChildLayout = null;

		List<Layout> childLayouts = layout.getAllChildren();

		for (Layout childLayout : childLayouts) {
			LayoutTypeController childLayoutTypeController =
				LayoutTypeControllerTracker.getLayoutTypeController(
					childLayout.getType());

			if (childLayoutTypeController.isBrowsable()) {
				browsableChildLayout = childLayout;

				break;
			}
		}

		if (browsableChildLayout != null) {
			return browsableChildLayout;
		}

		long defaultPlid = LayoutLocalServiceUtil.getDefaultPlid(
			layout.getGroupId(), layout.isPrivateLayout());

		return LayoutLocalServiceUtil.fetchLayout(defaultPlid);
	}

	protected String getCanonicalDomain(
		String virtualHostname, String portalDomain) {

		if (Validator.isBlank(portalDomain) ||
			StringUtil.equalsIgnoreCase(portalDomain, _LOCALHOST) ||
			!StringUtil.equalsIgnoreCase(virtualHostname, _LOCALHOST)) {

			return virtualHostname;
		}

		int pos = portalDomain.indexOf(CharPool.COLON);

		if (pos == -1) {
			return portalDomain;
		}

		return portalDomain.substring(0, pos);
	}

	protected String getContextPath(String contextPath) {
		contextPath = GetterUtil.getString(contextPath);

		if ((contextPath.length() == 0) ||
			contextPath.equals(StringPool.SLASH)) {

			contextPath = StringPool.BLANK;
		}
		else if (!contextPath.startsWith(StringPool.SLASH)) {
			contextPath = StringPool.SLASH.concat(contextPath);
		}

		return contextPath;
	}

	protected Group getControlPanelDisplayGroup(
		Group controlPanelGroup, Group scopeGroup, long doAsGroupId,
		String portletId) {

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			controlPanelGroup.getCompanyId(), portletId);

		String portletCategory = portlet.getControlPanelEntryCategory();

		if (portletCategory.equals(PortletCategoryKeys.CONTROL_PANEL_APPS) ||
			portletCategory.equals(
				PortletCategoryKeys.CONTROL_PANEL_CONFIGURATION) ||
			portletCategory.equals(PortletCategoryKeys.CONTROL_PANEL_SITES) ||
			portletCategory.equals(PortletCategoryKeys.CONTROL_PANEL_SYSTEM) ||
			portletCategory.equals(PortletCategoryKeys.CONTROL_PANEL_USERS) ||
			portletCategory.equals(PortletCategoryKeys.USER_MY_ACCOUNT)) {

			return controlPanelGroup;
		}

		if (doAsGroupId > 0) {
			Group doAsGroup = GroupLocalServiceUtil.fetchGroup(doAsGroupId);

			if (doAsGroup != null) {
				return doAsGroup;
			}
		}

		return scopeGroup;
	}

	protected Group getControlPanelDisplayGroup(
		long companyId, long scopeGroupId, long doAsGroupId, String portletId) {

		return getControlPanelDisplayGroup(
			GroupLocalServiceUtil.fetchGroup(
				companyId, GroupConstants.CONTROL_PANEL),
			GroupLocalServiceUtil.fetchGroup(scopeGroupId), doAsGroupId,
			portletId);
	}

	protected long getDoAsUserId(
			HttpServletRequest request, String doAsUserIdString,
			boolean alwaysAllowDoAsUser)
		throws Exception {

		if (Validator.isNull(doAsUserIdString)) {
			return 0;
		}

		long doAsUserId = 0;

		try {
			Company company = getCompany(request);

			doAsUserId = GetterUtil.getLong(
				Encryptor.decrypt(company.getKeyObj(), doAsUserIdString));
		}
		catch (Exception e) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to impersonate " + doAsUserIdString +
						" because the string cannot be decrypted");
			}

			return 0;
		}

		if (_log.isDebugEnabled()) {
			if (alwaysAllowDoAsUser) {
				_log.debug(
					"doAsUserId path or Struts action is always allowed");
			}
			else {
				_log.debug(
					"doAsUserId path is Struts action not always allowed");
			}
		}

		if (alwaysAllowDoAsUser) {
			request.setAttribute(WebKeys.USER_ID, Long.valueOf(doAsUserId));

			return doAsUserId;
		}

		HttpSession session = request.getSession();

		Long realUserIdObj = (Long)session.getAttribute(WebKeys.USER_ID);

		if (realUserIdObj == null) {
			return 0;
		}

		User doAsUser = UserLocalServiceUtil.getUserById(doAsUserId);

		long[] organizationIds = doAsUser.getOrganizationIds();

		User realUser = UserLocalServiceUtil.getUserById(
			realUserIdObj.longValue());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(realUser);

		if (doAsUser.isDefaultUser() ||
			UserPermissionUtil.contains(
				permissionChecker, doAsUserId, organizationIds,
				ActionKeys.IMPERSONATE)) {

			request.setAttribute(WebKeys.USER_ID, Long.valueOf(doAsUserId));

			return doAsUserId;
		}

		_log.error(
			StringBundler.concat(
				"User ", String.valueOf(realUserIdObj),
				" does not have the permission to impersonate ",
				String.valueOf(doAsUserId)));

		return 0;
	}

	protected String getGroupFriendlyURL(
			LayoutSet layoutSet, ThemeDisplay themeDisplay,
			boolean canonicalURL)
		throws PortalException {

		return getGroupFriendlyURL(
			layoutSet, themeDisplay, canonicalURL, false);
	}

	protected String getGroupFriendlyURL(
			LayoutSet layoutSet, ThemeDisplay themeDisplay,
			boolean canonicalURL, boolean controlPanel)
		throws PortalException {

		Group group = themeDisplay.getSiteGroup();

		if (group.getGroupId() != layoutSet.getGroupId()) {
			group = layoutSet.getGroup();
		}

		return _getGroupFriendlyURL(
			group, layoutSet, themeDisplay, canonicalURL, controlPanel);
	}

	protected String[] getGroupPermissions(
		String[] groupPermissions, String className,
		String inputPermissionsShowOptions) {

		if ((groupPermissions != null) ||
			(inputPermissionsShowOptions != null)) {

			return groupPermissions;
		}

		List<String> groupDefaultActions =
			ResourceActionsUtil.getModelResourceGroupDefaultActions(className);

		return groupDefaultActions.toArray(
			new String[groupDefaultActions.size()]);
	}

	protected String[] getGuestPermissions(
		String[] guestPermissions, String className,
		String inputPermissionsShowOptions) {

		if ((guestPermissions != null) ||
			(inputPermissionsShowOptions != null)) {

			return guestPermissions;
		}

		List<String> guestDefaultActions =
			ResourceActionsUtil.getModelResourceGuestDefaultActions(className);

		return guestDefaultActions.toArray(
			new String[guestDefaultActions.size()]);
	}

	protected String getPortletParam(HttpServletRequest request, String name) {
		String portletId = ParamUtil.getString(request, "p_p_id");

		if (Validator.isNull(portletId)) {
			return StringPool.BLANK;
		}

		String value = null;

		int valueCount = 0;

		String keyName = StringPool.UNDERLINE.concat(name);

		Map<String, String[]> parameterMap = request.getParameterMap();

		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			String parameterName = entry.getKey();

			int pos = parameterName.indexOf(keyName);

			if (pos == -1) {
				continue;
			}

			valueCount++;

			// There should never be more than one value

			if (valueCount > 1) {
				return StringPool.BLANK;
			}

			String[] parameterValues = entry.getValue();

			if (ArrayUtil.isEmpty(parameterValues) ||
				Validator.isNull(parameterValues[0])) {

				continue;
			}

			// The Struts action must be for the correct portlet

			String portletId1 = parameterName.substring(1, pos);

			if (portletId.equals(portletId1)) {
				value = parameterValues[0];
			}
		}

		if (value == null) {
			value = StringPool.BLANK;
		}

		return value;
	}

	protected String getServletURL(
			Portlet portlet, String servletPath, ThemeDisplay themeDisplay)
		throws PortalException {

		Layout layout = themeDisplay.getLayout();

		StringBundler sb = new StringBundler(9);

		sb.append(themeDisplay.getPortalURL());

		if (Validator.isNotNull(_pathContext)) {
			sb.append(_pathContext);
		}

		if (themeDisplay.isI18n()) {
			sb.append(themeDisplay.getI18nPath());
		}

		sb.append(servletPath);

		Group group = layout.getGroup();

		if (layout.isPrivateLayout()) {
			if (group.isUser()) {
				sb.append(_PRIVATE_USER_SERVLET_MAPPING);
			}
			else {
				sb.append(_PRIVATE_GROUP_SERVLET_MAPPING);
			}
		}
		else {
			sb.append(_PUBLIC_GROUP_SERVLET_MAPPING);
		}

		sb.append(group.getFriendlyURL());
		sb.append(themeDisplay.getLayoutFriendlyURL(layout));

		sb.append(FRIENDLY_URL_SEPARATOR);

		FriendlyURLMapper friendlyURLMapper =
			portlet.getFriendlyURLMapperInstance();

		if ((friendlyURLMapper != null) && !portlet.isInstanceable()) {
			sb.append(friendlyURLMapper.getMapping());
		}
		else {
			sb.append(portlet.getPortletId());
		}

		return sb.toString();
	}

	protected boolean isAlwaysAllowDoAsUser(HttpServletRequest request)
		throws Exception {

		String ticketKey = ParamUtil.getString(request, "ticketKey");

		if (Validator.isNull(ticketKey)) {
			return false;
		}

		Ticket ticket = TicketLocalServiceUtil.fetchTicket(ticketKey);

		if ((ticket == null) ||
			(ticket.getType() != TicketConstants.TYPE_IMPERSONATE)) {

			return false;
		}

		String className = ticket.getClassName();

		if (!className.equals(User.class.getName())) {
			return false;
		}

		long doAsUserId = 0;

		try {
			Company company = getCompany(request);

			String doAsUserIdString = ParamUtil.getString(
				request, "doAsUserId");

			if (Validator.isNotNull(doAsUserIdString)) {
				doAsUserId = GetterUtil.getLong(
					Encryptor.decrypt(company.getKeyObj(), doAsUserIdString));
			}
		}
		catch (Exception e) {
			return false;
		}

		if (ticket.getClassPK() != doAsUserId) {
			return false;
		}

		if (ticket.isExpired()) {
			TicketLocalServiceUtil.deleteTicket(ticket);

			return false;
		}

		Date expirationDate = new Date(
			System.currentTimeMillis() +
				PropsValues.SESSION_TIMEOUT * Time.MINUTE);

		ticket.setExpirationDate(expirationDate);

		TicketLocalServiceUtil.updateTicket(ticket);

		return true;
	}

	protected boolean isAlwaysAllowDoAsUser(
		String actionName, String mvcRenderCommandName, String path,
		String strutsAction) {

		for (AlwaysAllowDoAsUser alwaysAllowDoAsUser : _alwaysAllowDoAsUsers) {
			Collection<String> actionNames =
				alwaysAllowDoAsUser.getActionNames();

			if (actionNames.contains(actionName)) {
				return true;
			}

			Collection<String> mvcRenderCommandNames =
				alwaysAllowDoAsUser.getMVCRenderCommandNames();

			if (mvcRenderCommandNames.contains(mvcRenderCommandName)) {
				return true;
			}

			Collection<String> paths = alwaysAllowDoAsUser.getPaths();

			if (paths.contains(path)) {
				return true;
			}

			Collection<String> strutsActions =
				alwaysAllowDoAsUser.getStrutsActions();

			if (strutsActions.contains(strutsAction)) {
				return true;
			}
		}

		return false;
	}

	protected boolean isUnwrapRequest(String currentRequestClassName) {
		for (String packageName : PropsValues.REQUEST_UNWRAP_PACKAGES) {
			if (currentRequestClassName.startsWith(packageName)) {
				return true;
			}
		}

		return false;
	}

	protected boolean isValidPortalDomain(long companyId, String domain) {
		if (_validPortalDomainCheckDisabled) {
			return true;
		}

		if (!Validator.isHostName(domain)) {
			return false;
		}

		for (String virtualHost : PropsValues.VIRTUAL_HOSTS_VALID_HOSTS) {
			if (StringUtil.equalsIgnoreCase(domain, virtualHost) ||
				StringUtil.wildcardMatches(
					domain, virtualHost, CharPool.QUESTION, CharPool.STAR,
					CharPool.PERCENT, false)) {

				return true;
			}
		}

		if (StringUtil.equalsIgnoreCase(domain, PropsValues.WEB_SERVER_HOST)) {
			return true;
		}

		if (isValidVirtualHostname(domain)) {
			return true;
		}

		if (StringUtil.equalsIgnoreCase(domain, getCDNHostHttp(companyId))) {
			return true;
		}

		if (StringUtil.equalsIgnoreCase(domain, getCDNHostHttps(companyId))) {
			return true;
		}

		return false;
	}

	protected boolean isValidPortalDomain(String domain) {
		long companyId = CompanyThreadLocal.getCompanyId();

		return isValidPortalDomain(companyId, domain);
	}

	protected boolean isValidVirtualHostname(String virtualHostname) {
		try {
			virtualHostname = StringUtil.toLowerCase(virtualHostname.trim());

			VirtualHost virtualHost =
				VirtualHostLocalServiceUtil.fetchVirtualHost(virtualHostname);

			if (virtualHost != null) {
				return true;
			}
		}
		catch (Exception e) {
		}

		return false;
	}

	protected void notifyPortalInetSocketAddressEventListeners(
		InetSocketAddress inetSocketAddress, boolean local, boolean secure) {

		for (PortalInetSocketAddressEventListener
				portalInetSocketAddressEventListener :
					_portalInetSocketAddressEventListeners) {

			if (local) {
				portalInetSocketAddressEventListener.
					portalLocalInetSocketAddressConfigured(
						inetSocketAddress, secure);
			}
			else {
				portalInetSocketAddressEventListener.
					portalServerInetSocketAddressConfigured(
						inetSocketAddress, secure);
			}
		}
	}

	/**
	 * @deprecated As of 7.0.0, replaced by {@link
	 *             #notifyPortalInetSocketAddressEventListeners(
	 *             InetSocketAddress, boolean, boolean)}
	 */
	@Deprecated
	protected void notifyPortalPortEventListeners(int portalPort) {
		for (PortalPortEventListener portalPortEventListener :
				_portalPortEventListeners) {

			portalPortEventListener.portalPortConfigured(portalPort);
		}
	}

	protected String removeRedirectParameter(String url) {
		String queryString = HttpUtil.getQueryString(url);

		Map<String, String[]> parameterMap = HttpUtil.getParameterMap(
			queryString);

		for (String parameter : parameterMap.keySet()) {
			if (parameter.endsWith("redirect")) {
				url = HttpUtil.removeParameter(url, parameter);
			}
		}

		return url;
	}

	protected void resetThemeDisplayI18n(
		ThemeDisplay themeDisplay, String languageId, String path,
		Locale locale) {

		themeDisplay.setI18nLanguageId(languageId);
		themeDisplay.setI18nPath(path);
		themeDisplay.setLocale(locale);
	}

	protected void setLocale(
		HttpServletRequest request, HttpServletResponse response,
		Locale locale) {

		HttpSession session = request.getSession();

		session.setAttribute(Globals.LOCALE_KEY, locale);

		LanguageUtil.updateCookie(request, response, locale);
	}

	protected void setThemeDisplayI18n(
		ThemeDisplay themeDisplay, Locale locale) {

		String i18nPath = null;

		Set<String> languageIds = I18nFilter.getLanguageIds();

		if ((languageIds.contains(locale.toString()) &&
			 (PropsValues.LOCALE_PREPEND_FRIENDLY_URL_STYLE == 1) &&
			 !locale.equals(LocaleUtil.getDefault())) ||
			(PropsValues.LOCALE_PREPEND_FRIENDLY_URL_STYLE == 2)) {

			i18nPath = buildI18NPath(locale);
		}

		themeDisplay.setI18nLanguageId(locale.toString());
		themeDisplay.setI18nPath(i18nPath);
		themeDisplay.setLocale(locale);
	}

	private String _buildI18NPath(String languageId, Locale locale) {
		if (Validator.isNull(languageId)) {
			return null;
		}

		if (LanguageUtil.isDuplicateLanguageCode(locale.getLanguage())) {
			Locale priorityLocale = LanguageUtil.getLocale(
				locale.getLanguage());

			if (locale.equals(priorityLocale)) {
				languageId = locale.getLanguage();
			}
		}
		else {
			languageId = locale.getLanguage();
		}

		return StringPool.SLASH.concat(languageId);
	}

	private Map<Locale, String> _getAlternateURLs(
			String canonicalURL, ThemeDisplay themeDisplay, Layout layout,
			Set<Locale> availableLocales)
		throws PortalException {

		String virtualHostname = getVirtualHostname(
			themeDisplay.getLayoutSet());

		if (Validator.isNull(virtualHostname)) {
			Company company = themeDisplay.getCompany();

			virtualHostname = company.getVirtualHostname();
		}

		String portalDomain = themeDisplay.getPortalDomain();

		if (!Validator.isBlank(portalDomain) &&
			!StringUtil.equalsIgnoreCase(portalDomain, _LOCALHOST) &&
			StringUtil.equalsIgnoreCase(virtualHostname, _LOCALHOST)) {

			virtualHostname = portalDomain;
		}

		Map<Locale, String> alternateURLs = new HashMap<>();

		if (Validator.isNull(virtualHostname)) {
			for (Locale locale : availableLocales) {
				String i18nPath = buildI18NPath(locale);

				alternateURLs.put(
					locale,
					canonicalURL.replaceFirst(
						_PUBLIC_GROUP_SERVLET_MAPPING,
						i18nPath.concat(_PUBLIC_GROUP_SERVLET_MAPPING)));
			}

			return alternateURLs;
		}

		// www.liferay.com:8080/ctx/page to www.liferay.com:8080/ctx/es/page

		int pos = canonicalURL.indexOf(virtualHostname);

		if (pos > 0) {
			pos = canonicalURL.indexOf(
				CharPool.SLASH, pos + virtualHostname.length());

			if (Validator.isNotNull(_pathContext)) {
				pos = canonicalURL.indexOf(
					CharPool.SLASH, pos + _pathContext.length());
			}
		}

		if ((pos <= 0) || (pos >= canonicalURL.length())) {
			for (Locale locale : availableLocales) {
				alternateURLs.put(
					locale, canonicalURL.concat(buildI18NPath(locale)));
			}

			return alternateURLs;
		}

		boolean replaceFriendlyURL = true;

		String currentURL = canonicalURL.substring(pos);

		int[] friendlyURLIndex = getGroupFriendlyURLIndex(currentURL);

		if (friendlyURLIndex != null) {
			int y = friendlyURLIndex[1];

			currentURL = currentURL.substring(y);

			if (currentURL.equals(StringPool.SLASH)) {
				replaceFriendlyURL = false;
			}
		}

		Locale siteDefaultLocale = getSiteDefaultLocale(layout.getGroupId());

		List<LayoutFriendlyURL> layoutFriendlyURLs = null;

		String groupFriendlyURLPrefix = null;

		if (replaceFriendlyURL) {
			layoutFriendlyURLs =
				LayoutFriendlyURLLocalServiceUtil.getLayoutFriendlyURLs(
					layout.getPlid());

			if (layout instanceof VirtualLayout) {
				VirtualLayout virtualLayout = (VirtualLayout)layout;

				layout = virtualLayout.getSourceLayout();

				Group group = layout.getGroup();

				groupFriendlyURLPrefix =
					VirtualLayoutConstants.CANONICAL_URL_SEPARATOR.concat(
						group.getFriendlyURL());
			}
		}

		String canonicalURLPrefix = canonicalURL.substring(0, pos);
		String canonicalURLSuffix = canonicalURL.substring(pos);

		if (PropsValues.LOCALE_PREPEND_FRIENDLY_URL_STYLE == 2) {
			String i18nPath = buildI18NPath(siteDefaultLocale);

			if (canonicalURLSuffix.startsWith(i18nPath)) {
				canonicalURLSuffix = canonicalURLSuffix.substring(
					i18nPath.length());
			}
		}

		for (Locale locale : availableLocales) {
			String alternateURL = canonicalURL;
			String alternateURLSuffix = canonicalURLSuffix;
			String languageId = LocaleUtil.toLanguageId(locale);

			if (replaceFriendlyURL) {
				String friendlyURL = null;

				for (LayoutFriendlyURL layoutFriendlyURL : layoutFriendlyURLs) {
					if (!languageId.equals(layoutFriendlyURL.getLanguageId())) {
						continue;
					}

					friendlyURL = layoutFriendlyURL.getFriendlyURL();

					if (groupFriendlyURLPrefix != null) {
						friendlyURL = groupFriendlyURLPrefix.concat(
							friendlyURL);
					}

					break;
				}

				if (friendlyURL != null) {
					alternateURLSuffix = StringUtil.replaceFirst(
						alternateURLSuffix, layout.getFriendlyURL(),
						friendlyURL);
				}

				alternateURL = canonicalURLPrefix.concat(alternateURLSuffix);
			}

			if (siteDefaultLocale.equals(locale) &&
				(PropsValues.LOCALE_PREPEND_FRIENDLY_URL_STYLE != 2)) {

				alternateURLs.put(locale, alternateURL);
			}
			else {
				alternateURLs.put(
					locale,
					canonicalURLPrefix.concat(
						_buildI18NPath(languageId, locale)).concat(
							alternateURLSuffix));
			}
		}

		return alternateURLs;
	}

	private String _getGroupFriendlyURL(
			Group group, LayoutSet layoutSet, ThemeDisplay themeDisplay,
			boolean canonicalURL, boolean controlPanel)
		throws PortalException {

		boolean privateLayoutSet = layoutSet.isPrivateLayout();

		String portalURL = themeDisplay.getPortalURL();

		boolean useGroupVirtualHostName = false;

		if (canonicalURL ||
			!StringUtil.equalsIgnoreCase(
				themeDisplay.getServerName(), _LOCALHOST)) {

			useGroupVirtualHostName = true;
		}

		long refererPlid = themeDisplay.getRefererPlid();

		if (refererPlid > 0) {
			Layout refererLayout = LayoutLocalServiceUtil.fetchLayout(
				refererPlid);

			if ((refererLayout != null) &&
				((refererLayout.getGroupId() != group.getGroupId()) ||
				 (refererLayout.isPrivateLayout() != privateLayoutSet))) {

				useGroupVirtualHostName = false;
			}
		}

		if (useGroupVirtualHostName) {
			String virtualHostname = getVirtualHostname(layoutSet);

			String portalDomain = themeDisplay.getPortalDomain();

			if (Validator.isNotNull(virtualHostname) &&
				(canonicalURL ||
				 !StringUtil.equalsIgnoreCase(virtualHostname, _LOCALHOST))) {

				if (!controlPanel) {
					if (canonicalURL) {
						String path = StringPool.BLANK;

						if (themeDisplay.isWidget()) {
							path = PropsValues.WIDGET_SERVLET_MAPPING;
						}

						if (!StringUtil.equalsIgnoreCase(
								virtualHostname, _LOCALHOST) &&
							!_isSameHostName(virtualHostname, portalDomain)) {

							portalURL = getPortalURL(
								virtualHostname, themeDisplay.getServerPort(),
								themeDisplay.isSecure());
						}

						return portalURL.concat(_pathContext).concat(path);
					}

					if (_isSameHostName(virtualHostname, portalDomain) &&
						(virtualHostname.equals(
							layoutSet.getVirtualHostname()) ||
						 PropsValues.VIRTUAL_HOSTS_DEFAULT_SITE_NAME.equals(
							 group.getGroupKey()))) {

						String path = StringPool.BLANK;

						if (themeDisplay.isWidget()) {
							path = PropsValues.WIDGET_SERVLET_MAPPING;
						}

						if (themeDisplay.isI18n()) {
							path = themeDisplay.getI18nPath();
						}

						return portalURL.concat(_pathContext).concat(path);
					}
				}
			}
			else {
				if (canonicalURL ||
					((layoutSet.getGroupId() !=
						themeDisplay.getSiteGroupId()) &&
					 (group.getClassPK() != themeDisplay.getUserId()))) {

					if (group.isControlPanel()) {
						virtualHostname = themeDisplay.getServerName();

						if (Validator.isNull(virtualHostname) ||
							StringUtil.equalsIgnoreCase(
								virtualHostname, _LOCALHOST)) {

							LayoutSet curLayoutSet =
								LayoutSetLocalServiceUtil.getLayoutSet(
									themeDisplay.getSiteGroupId(),
									privateLayoutSet);

							virtualHostname = curLayoutSet.getVirtualHostname();
						}
					}

					if (Validator.isNull(virtualHostname) ||
						StringUtil.equalsIgnoreCase(
							virtualHostname, _LOCALHOST)) {

						Company company = themeDisplay.getCompany();

						virtualHostname = company.getVirtualHostname();
					}

					if (canonicalURL ||
						!StringUtil.equalsIgnoreCase(
							virtualHostname, _LOCALHOST)) {

						virtualHostname = getCanonicalDomain(
							virtualHostname, portalDomain);

						portalURL = getPortalURL(
							virtualHostname, themeDisplay.getServerPort(),
							themeDisplay.isSecure());
					}
				}
			}
		}

		String friendlyURL = null;

		if (privateLayoutSet) {
			if (group.isUser()) {
				friendlyURL = _PRIVATE_USER_SERVLET_MAPPING;
			}
			else {
				friendlyURL = _PRIVATE_GROUP_SERVLET_MAPPING;
			}
		}
		else {
			friendlyURL = _PUBLIC_GROUP_SERVLET_MAPPING;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(portalURL);
		sb.append(_pathContext);

		if (themeDisplay.isI18n() && !canonicalURL) {
			sb.append(themeDisplay.getI18nPath());
		}

		if (themeDisplay.isWidget()) {
			sb.append(PropsValues.WIDGET_SERVLET_MAPPING);
		}

		sb.append(friendlyURL);
		sb.append(group.getFriendlyURL());

		return sb.toString();
	}

	private String _getPortalURL(
		String serverName, int serverPort, boolean secure) {

		StringBundler sb = new StringBundler(2);

		boolean https = false;

		if (secure ||
			StringUtil.equalsIgnoreCase(
				Http.HTTPS, PropsValues.WEB_SERVER_PROTOCOL)) {

			https = true;
		}

		if (https) {
			sb.append(Http.HTTPS_WITH_SLASH);
		}
		else {
			sb.append(Http.HTTP_WITH_SLASH);
		}

		sb.append(serverName);

		if (!https) {
			if (PropsValues.WEB_SERVER_HTTP_PORT == -1) {
				if ((serverPort != -1) && (serverPort != Http.HTTP_PORT) &&
					(serverPort != Http.HTTPS_PORT)) {

					sb.append(StringPool.COLON);
					sb.append(serverPort);
				}
			}
			else {
				if (PropsValues.WEB_SERVER_HTTP_PORT != Http.HTTP_PORT) {
					sb.append(StringPool.COLON);
					sb.append(PropsValues.WEB_SERVER_HTTP_PORT);
				}
			}
		}
		else {
			if (PropsValues.WEB_SERVER_HTTPS_PORT == -1) {
				if ((serverPort != -1) && (serverPort != Http.HTTP_PORT) &&
					(serverPort != Http.HTTPS_PORT)) {

					sb.append(StringPool.COLON);
					sb.append(serverPort);
				}
			}
			else {
				if (PropsValues.WEB_SERVER_HTTPS_PORT != Http.HTTPS_PORT) {
					sb.append(StringPool.COLON);
					sb.append(PropsValues.WEB_SERVER_HTTPS_PORT);
				}
			}
		}

		return sb.toString();
	}

	private String _getPortletTitle(
		String rootPortletId, PortletConfig portletConfig, Locale locale) {

		ResourceBundle resourceBundle = portletConfig.getResourceBundle(locale);

		String portletTitle = LanguageUtil.get(
			resourceBundle,
			JavaConstants.JAVAX_PORTLET_TITLE.concat(StringPool.PERIOD).concat(
				rootPortletId),
			null);

		if (Validator.isNull(portletTitle)) {
			portletTitle = LanguageUtil.get(
				resourceBundle, JavaConstants.JAVAX_PORTLET_TITLE);
		}

		return portletTitle;
	}

	private long _getScopeGroupId(
		ThemeDisplay themeDisplay, Layout layout, String portletId) {

		if (layout == null) {
			return 0;
		}

		if (Validator.isNull(portletId)) {
			return layout.getGroupId();
		}

		try {
			PortletPreferences portletSetup = null;

			if (themeDisplay == null) {
				portletSetup =
					PortletPreferencesFactoryUtil.getStrictLayoutPortletSetup(
						layout, portletId);
			}
			else {
				portletSetup = themeDisplay.getStrictLayoutPortletSetup(
					layout, portletId);
			}

			String scopeType = GetterUtil.getString(
				portletSetup.getValue("lfrScopeType", null));

			if (Validator.isNull(scopeType)) {
				return layout.getGroupId();
			}

			if (scopeType.equals("company")) {
				Group companyGroup = GroupLocalServiceUtil.getCompanyGroup(
					layout.getCompanyId());

				return companyGroup.getGroupId();
			}

			String scopeLayoutUuid = GetterUtil.getString(
				portletSetup.getValue("lfrScopeLayoutUuid", null));

			Layout scopeLayout =
				LayoutLocalServiceUtil.getLayoutByUuidAndGroupId(
					scopeLayoutUuid, layout.getGroupId(),
					layout.isPrivateLayout());

			Group scopeGroup = scopeLayout.getScopeGroup();

			return scopeGroup.getGroupId();
		}
		catch (Exception e) {
			return layout.getGroupId();
		}
	}

	private String _getSiteAdminURL(
			String portalURL, Group group, String ppid,
			Map<String, String[]> params)
		throws PortalException {

		StringBundler sb = new StringBundler(7);

		sb.append(portalURL);
		sb.append(getPathFriendlyURLPrivateGroup());

		if ((group != null) && !group.isControlPanel()) {
			sb.append(group.getFriendlyURL());
			sb.append(VirtualLayoutConstants.CANONICAL_URL_SEPARATOR);
		}

		sb.append(GroupConstants.CONTROL_PANEL_FRIENDLY_URL);
		sb.append(PropsValues.CONTROL_PANEL_LAYOUT_FRIENDLY_URL);

		if (params != null) {
			params = new LinkedHashMap<>(params);
		}
		else {
			params = new LinkedHashMap<>();
		}

		if (Validator.isNotNull(ppid)) {
			params.put("p_p_id", new String[] {ppid});
		}

		params.put("p_p_lifecycle", new String[] {"0"});
		params.put(
			"p_p_state", new String[] {WindowState.MAXIMIZED.toString()});
		params.put("p_p_mode", new String[] {PortletMode.VIEW.toString()});

		sb.append(HttpUtil.parameterMapToString(params, true));

		return sb.toString();
	}

	private Group _getSiteGroup(long groupId) throws PortalException {
		if (groupId <= 0) {
			return null;
		}

		Group group = GroupLocalServiceUtil.fetchGroup(groupId);

		if (group == null) {
			return null;
		}

		if (group.isLayout()) {
			return group.getParentGroup();
		}

		return group;
	}

	private boolean _isSameHostName(
		String virtualHostName, String portalDomain) {

		int pos = portalDomain.indexOf(CharPool.COLON);

		if (pos > 0) {
			return virtualHostName.regionMatches(0, portalDomain, 0, pos);
		}

		return virtualHostName.equals(portalDomain);
	}

	private static final Log _logWebServerServlet = LogFactoryUtil.getLog(
		WebServerServlet.class);

	private static final String _J_SECURITY_CHECK = "j_security_check";

	private static final String _JOURNAL_ARTICLE_CANONICAL_URL_SEPARATOR =
		"/-/";

	private static final String _LOCALHOST = "localhost";

	private static final Locale _NULL_LOCALE;

	private static final String _PRIVATE_GROUP_SERVLET_MAPPING =
		PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING;

	private static final String _PRIVATE_USER_SERVLET_MAPPING =
		PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_USER_SERVLET_MAPPING;

	private static final String _PUBLIC_GROUP_SERVLET_MAPPING =
		PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING;

	private static final Log _log = LogFactoryUtil.getLog(PortalImpl.class);

	private static final Map<Long, String> _cdnHostHttpMap =
		new ConcurrentHashMap<>();
	private static final Map<Long, String> _cdnHostHttpsMap =
		new ConcurrentHashMap<>();
	private static final MethodHandler _resetCDNHostsMethodHandler =
		new MethodHandler(new MethodKey(PortalUtil.class, "resetCDNHosts"));
	private static final Date _upTime = new Date();

	static {
		Locale locale = Locale.getDefault();

		_NULL_LOCALE = (Locale)locale.clone();
	}

	private final String[] _allSystemGroups;
	private final String[] _allSystemOrganizationRoles;
	private final String[] _allSystemRoles;
	private final String[] _allSystemSiteRoles;
	private final List<AlwaysAllowDoAsUser> _alwaysAllowDoAsUsers =
		new ArrayList<>();
	private final Pattern _bannedResourceIdPattern = Pattern.compile(
		PropsValues.PORTLET_RESOURCE_ID_BANNED_PATHS_REGEXP,
		Pattern.CASE_INSENSITIVE);
	private final Set<String> _computerAddresses = new HashSet<>();
	private final String _computerName;
	private String[] _customSqlKeys;
	private String[] _customSqlValues;
	private final EditDiscussionStrutsAction _editDiscussionStrutsAction =
		new EditDiscussionStrutsAction();
	private final GetCommentsStrutsAction _getCommentsStrutsAction =
		new GetCommentsStrutsAction();
	private final String _pathContext;
	private final String _pathFriendlyURLPrivateGroup;
	private final String _pathFriendlyURLPrivateUser;
	private final String _pathFriendlyURLPublic;
	private final String _pathImage;
	private final String _pathMain;
	private final String _pathModule;
	private final String _pathProxy;
	private final Map<String, Long> _plidToPortletIdMap =
		new ConcurrentHashMap<>();
	private final Set<PortalInetSocketAddressEventListener>
		_portalInetSocketAddressEventListeners = new CopyOnWriteArraySet<>();
	private final AtomicReference<InetSocketAddress>
		_portalLocalInetSocketAddress = new AtomicReference<>();

	/**
	 * @deprecated As of 7.0.0, replaced by {@link
	 *             #_portalServerInetSocketAddress}
	 */
	@Deprecated
	private final AtomicInteger _portalPort = new AtomicInteger(-1);

	/**
	 * @deprecated As of 7.0.0, replaced by {@link
	 *             #_portalInetSocketAddressEventListeners}
	 */
	@Deprecated
	private final List<PortalPortEventListener> _portalPortEventListeners =
		new ArrayList<>();

	private final AtomicReference<InetSocketAddress>
		_portalServerInetSocketAddress = new AtomicReference<>();
	private final Set<String> _reservedParams;
	private final AtomicReference<InetSocketAddress>
		_securePortalLocalInetSocketAddress = new AtomicReference<>();

	/**
	 * @deprecated As of 7.0.0, replaced by {@link
	 *             #_securePortalServerInetSocketAddress}
	 */
	@Deprecated
	private final AtomicInteger _securePortalPort = new AtomicInteger(-1);

	private final AtomicReference<InetSocketAddress>
		_securePortalServerInetSocketAddress = new AtomicReference<>();
	private final String _servletContextName;
	private final String[] _sortedSystemGroups;
	private final String[] _sortedSystemOrganizationRoles;
	private final String[] _sortedSystemRoles;
	private final String[] _sortedSystemSiteRoles;
	private final boolean _validPortalDomainCheckDisabled;

	private class AlwaysAllowDoAsUserServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<AlwaysAllowDoAsUser, AlwaysAllowDoAsUser> {

		@Override
		public AlwaysAllowDoAsUser addingService(
			ServiceReference<AlwaysAllowDoAsUser> serviceReference) {

			Registry registry = RegistryUtil.getRegistry();

			AlwaysAllowDoAsUser alwaysAllowDoAsUser = registry.getService(
				serviceReference);

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Add alway sallow do as user " +
						ClassUtil.getClassName(alwaysAllowDoAsUser));
			}

			_alwaysAllowDoAsUsers.add(alwaysAllowDoAsUser);

			if (_log.isDebugEnabled()) {
				_log.debug(
					"There are " + _alwaysAllowDoAsUsers.size() +
						" alway sallow do as user instances");
			}

			return alwaysAllowDoAsUser;
		}

		@Override
		public void modifiedService(
			ServiceReference<AlwaysAllowDoAsUser> serviceReference,
			AlwaysAllowDoAsUser alwaysAllowDoAsUser) {
		}

		@Override
		public void removedService(
			ServiceReference<AlwaysAllowDoAsUser> serviceReference,
			AlwaysAllowDoAsUser alwaysAllowDoAsUser) {

			Registry registry = RegistryUtil.getRegistry();

			registry.ungetService(serviceReference);

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Delete alway sallow do as user " +
						ClassUtil.getClassName(alwaysAllowDoAsUser));
			}

			_alwaysAllowDoAsUsers.remove(alwaysAllowDoAsUser);

			if (_log.isDebugEnabled()) {
				_log.debug(
					"There are " + _alwaysAllowDoAsUsers.size() +
						" alway sallow do as user instances");
			}
		}

	}

	private class PortalInetSocketAddressEventListenerServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<PortalInetSocketAddressEventListener,
				PortalInetSocketAddressEventListener> {

		@Override
		public PortalInetSocketAddressEventListener addingService(
			ServiceReference<PortalInetSocketAddressEventListener>
				serviceReference) {

			Registry registry = RegistryUtil.getRegistry();

			PortalInetSocketAddressEventListener
				portalInetSocketAddressEventListener = registry.getService(
					serviceReference);

			addPortalInetSocketAddressEventListener(
				portalInetSocketAddressEventListener);

			return portalInetSocketAddressEventListener;
		}

		@Override
		public void modifiedService(
			ServiceReference<PortalInetSocketAddressEventListener>
				serviceReference,
			PortalInetSocketAddressEventListener
				portalInetSocketAddressEventListener) {
		}

		@Override
		public void removedService(
			ServiceReference<PortalInetSocketAddressEventListener>
				serviceReference,
			PortalInetSocketAddressEventListener
				portalInetSocketAddressEventListener) {

			Registry registry = RegistryUtil.getRegistry();

			registry.ungetService(serviceReference);

			removePortalInetSocketAddressEventListener(
				portalInetSocketAddressEventListener);
		}

	}

}