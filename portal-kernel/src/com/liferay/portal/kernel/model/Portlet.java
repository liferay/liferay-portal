/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the Portlet service. Represents a row in the &quot;Portlet&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see PortletModel
 * @generated
 */
@ImplementationClassName("com.liferay.portal.model.impl.PortletImpl")
@ProviderType
public interface Portlet extends PersistedModel, PortletModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>com.liferay.portal.model.impl.PortletImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<Portlet, Long> ID_ACCESSOR =
		new Accessor<Portlet, Long>() {

			@Override
			public Long get(Portlet portlet) {
				return portlet.getId();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<Portlet> getTypeClass() {
				return Portlet.class;
			}

		};
	public static final Accessor<Portlet, String> PORTLET_ID_ACCESSOR =
		new Accessor<Portlet, String>() {

			@Override
			public String get(Portlet portlet) {
				return portlet.getPortletId();
			}

			@Override
			public Class<String> getAttributeClass() {
				return String.class;
			}

			@Override
			public Class<Portlet> getTypeClass() {
				return Portlet.class;
			}

		};

	/**
	 * Adds an application type.
	 *
	 * @param applicationType an application type
	 */
	public void addApplicationType(
		com.liferay.portal.kernel.application.type.ApplicationType
			applicationType);

	/**
	 * Adds a portlet CSS/JavaScript resource dependency.
	 *
	 * @param portletDependency the portlet CSS/JavaScript resource dependency
	 */
	public void addPortletDependency(
		com.liferay.portal.kernel.model.portlet.PortletDependency
			portletDependency);

	/**
	 * Adds a processing event.
	 */
	public void addProcessingEvent(
		com.liferay.portal.kernel.xml.QName processingEvent);

	/**
	 * Adds a public render parameter.
	 *
	 * @param publicRenderParameter a public render parameter
	 */
	public void addPublicRenderParameter(
		PublicRenderParameter publicRenderParameter);

	/**
	 * Adds a publishing event.
	 */
	public void addPublishingEvent(
		com.liferay.portal.kernel.xml.QName publishingEvent);

	/**
	 * Adds a scheduler entry.
	 */
	public void addSchedulerEntry(
		com.liferay.portal.kernel.scheduler.SchedulerEntry schedulerEntry);

	/**
	 * Creates and returns a copy of this object.
	 *
	 * @return a copy of this object
	 */
	public Object clone();

	/**
	 * Compares this portlet to the specified object.
	 *
	 * @param portlet the portlet to compare this portlet against
	 * @return the value 0 if the argument portlet is equal to this portlet; a
	 value less than -1 if this portlet is less than the portlet
	 argument; and 1 if this portlet is greater than the portlet
	 argument
	 */
	public int compareTo(Portlet portlet);

	/**
	 * Checks whether this portlet is equal to the specified object.
	 *
	 * @param object the object to compare this portlet against
	 * @return <code>true</code> if the portlet is equal to the specified object
	 */
	@Override
	public boolean equals(Object object);

	/**
	 * Returns the action timeout of the portlet.
	 *
	 * @return the action timeout of the portlet
	 */
	public int getActionTimeout();

	/**
	 * Returns <code>true</code> if an action URL for this portlet should cause
	 * an auto redirect.
	 *
	 * @return <code>true</code> if an action URL for this portlet should cause
	 an auto redirect
	 */
	public boolean getActionURLRedirect();

	/**
	 * Returns <code>true</code> if default resources for the portlet are added
	 * to a page.
	 *
	 * @return <code>true</code> if default resources for the portlet are added
	 to a page
	 */
	public boolean getAddDefaultResource();

	/**
	 * Returns <code>true</code> if the portlet can be displayed via Ajax.
	 *
	 * @return <code>true</code> if the portlet can be displayed via Ajax
	 */
	public boolean getAjaxable();

	/**
	 * Returns the portlet modes of the portlet.
	 *
	 * @return the portlet modes of the portlet
	 */
	public java.util.Set<String> getAllPortletModes();

	/**
	 * Returns the window states of the portlet.
	 *
	 * @return the window states of the portlet
	 */
	public java.util.Set<String> getAllWindowStates();

	/**
	 * Returns the application types of the portlet.
	 *
	 * @return the application types of the portlet
	 */
	public java.util.Set
		<com.liferay.portal.kernel.application.type.ApplicationType>
			getApplicationTypes();

	/**
	 * Returns the names of the classes that represent asset types associated
	 * with the portlet.
	 *
	 * @return the names of the classes that represent asset types associated
	 with the portlet
	 */
	public java.util.List<String> getAssetRendererFactoryClasses();

	/**
	 * Returns the names of the parameters that will be automatically propagated
	 * through the portlet.
	 *
	 * @return the names of the parameters that will be automatically propagated
	 through the portlet
	 */
	public java.util.Set<String> getAutopropagatedParameters();

	/**
	 * Returns the category names of the portlet.
	 *
	 * @return the category names of the portlet
	 */
	public java.util.Set<String> getCategoryNames();

	/**
	 * Returns <code>true</code> if the portlet is found in a WAR file.
	 *
	 * @param portletId the cloned instance portlet ID
	 * @return a cloned instance of the portlet
	 */
	public Portlet getClonedInstance(String portletId);

	/**
	 * Returns the configuration action class of the portlet.
	 *
	 * @return the configuration action class of the portlet
	 */
	public String getConfigurationActionClass();

	/**
	 * Returns the configuration action instance of the portlet.
	 *
	 * @return the configuration action instance of the portlet
	 */
	public com.liferay.portal.kernel.portlet.ConfigurationAction
		getConfigurationActionInstance();

	/**
	 * Returns the servlet context name of the portlet.
	 *
	 * @return the servlet context name of the portlet
	 */
	public String getContextName();

	/**
	 * Returns the servlet context path of the portlet.
	 *
	 * @return the servlet context path of the portlet
	 */
	public String getContextPath();

	/**
	 * Returns the name of the category of the Control Panel where the portlet
	 * will be shown.
	 *
	 * @return the name of the category of the Control Panel where the portlet
	 will be shown
	 */
	public String getControlPanelEntryCategory();

	/**
	 * Returns the name of the class that will control when the portlet will be
	 * shown in the Control Panel.
	 *
	 * @return the name of the class that will control when the portlet will be
	 shown in the Control Panel
	 */
	public String getControlPanelEntryClass();

	/**
	 * Returns an instance of the class that will control when the portlet will
	 * be shown in the Control Panel.
	 *
	 * @return the instance of the class that will control when the portlet will
	 be shown in the Control Panel
	 */
	public com.liferay.portal.kernel.portlet.ControlPanelEntry
		getControlPanelEntryInstance();

	/**
	 * Returns the relative weight of the portlet with respect to the other
	 * portlets in the same category of the Control Panel.
	 *
	 * @return the relative weight of the portlet with respect to the other
	 portlets in the same category of the Control Panel
	 */
	public double getControlPanelEntryWeight();

	/**
	 * Returns the name of the CSS class that will be injected in the DIV that
	 * wraps this portlet.
	 *
	 * @return the name of the CSS class that will be injected in the DIV that
	 wraps this portlet
	 */
	public String getCssClassWrapper();

	/**
	 * Returns the names of the classes that represent custom attribute displays
	 * associated with the portlet.
	 *
	 * @return the names of the classes that represent asset types associated
	 with the portlet
	 */
	public java.util.List<String> getCustomAttributesDisplayClasses();

	/**
	 * Returns the custom attribute display instances of the portlet.
	 *
	 * @return the custom attribute display instances of the portlet
	 */
	public java.util.List
		<com.liferay.expando.kernel.model.CustomAttributesDisplay>
			getCustomAttributesDisplayInstances();

	/**
	 * Get the default plugin settings of the portlet.
	 *
	 * @return the plugin settings
	 */
	public PluginSetting getDefaultPluginSetting();

	/**
	 * Returns the default preferences of the portlet.
	 *
	 * @return the default preferences of the portlet
	 */
	public String getDefaultPreferences();

	/**
	 * Returns the display name of the portlet.
	 *
	 * @return the display name of the portlet
	 */
	public String getDisplayName();

	/**
	 * Returns expiration cache of the portlet.
	 *
	 * @return expiration cache of the portlet
	 */
	public Integer getExpCache();

	/**
	 * Returns a list of CSS files that will be referenced from the page's
	 * footer relative to the portal's context path.
	 *
	 * @return a list of CSS files that will be referenced from the page's
	 footer relative to the portal's context path
	 */
	public java.util.List<String> getFooterPortalCss();

	/**
	 * Returns a list of JavaScript files that will be referenced from the
	 * page's footer relative to the portal's context path.
	 *
	 * @return a list of JavaScript files that will be referenced from the
	 page's footer relative to the portal's context path
	 */
	public java.util.List<String> getFooterPortalJavaScript();

	/**
	 * Returns a list of CSS files that will be referenced from the page's
	 * footer relative to the portlet's context path.
	 *
	 * @return a list of CSS files that will be referenced from the page's
	 footer relative to the portlet's context path
	 */
	public java.util.List<String> getFooterPortletCss();

	/**
	 * Returns a list of JavaScript files that will be referenced from the
	 * page's footer relative to the portlet's context path.
	 *
	 * @return a list of JavaScript files that will be referenced from the
	 page's footer relative to the portlet's context path
	 */
	public java.util.List<String> getFooterPortletJavaScript();

	/**
	 * Returns the name of the friendly URL mapper class of the portlet.
	 *
	 * @return the name of the friendly URL mapper class of the portlet
	 */
	public String getFriendlyURLMapperClass();

	/**
	 * Returns the friendly URL mapper instance of the portlet.
	 *
	 * @return the friendly URL mapper instance of the portlet
	 */
	public com.liferay.portal.kernel.portlet.FriendlyURLMapper
		getFriendlyURLMapperInstance();

	/**
	 * Returns the name of the friendly URL mapping of the portlet.
	 *
	 * @return the name of the friendly URL mapping of the portlet
	 */
	public String getFriendlyURLMapping();

	public String getFriendlyURLMapping(boolean lookUpFriendlyURLMapper);

	/**
	 * Returns the class loader resource path to the friendly URL routes of the
	 * portlet.
	 *
	 * @return the class loader resource path to the friendly URL routes of the
	 portlet
	 */
	public String getFriendlyURLRoutes();

	/**
	 * Returns a list of CSS files that will be referenced from the page's
	 * header relative to the portal's context path.
	 *
	 * @return a list of CSS files that will be referenced from the page's
	 header relative to the portal's context path
	 */
	public java.util.List<String> getHeaderPortalCss();

	/**
	 * Returns a list of JavaScript files that will be referenced from the
	 * page's header relative to the portal's context path.
	 *
	 * @return a list of JavaScript files that will be referenced from the
	 page's header relative to the portal's context path
	 */
	public java.util.List<String> getHeaderPortalJavaScript();

	/**
	 * Returns a list of CSS files that will be referenced from the page's
	 * header relative to the portlet's context path.
	 *
	 * @return a list of CSS files that will be referenced from the page's
	 header relative to the portlet's context path
	 */
	public java.util.List<String> getHeaderPortletCss();

	/**
	 * Returns a list of JavaScript files that will be referenced from the
	 * page's header relative to the portlet's context path.
	 *
	 * @return a list of JavaScript files that will be referenced from the
	 page's header relative to the portlet's context path
	 */
	public java.util.List<String> getHeaderPortletJavaScript();

	/**
	 * Returns a list of attribute name prefixes that will be referenced after
	 * the HEADER_PHASE completes for each portlet. Header request attributes
	 * that have names starting with any of the prefixes will be copied from the
	 * header request to the subsequent render request.
	 *
	 * @return a list of attribute name prefixes that will be referenced after
	 the HEADER_PHASE completes for each portlet. Header request
	 attributes that have names starting with any of the prefixes will
	 be copied from the header request to the subsequent render
	 request.
	 */
	public java.util.List<String> getHeaderRequestAttributePrefixes();

	/**
	 * Returns the header timeout of the portlet.
	 *
	 * @return the header timeout of the portlet
	 */
	public int getHeaderTimeout();

	/**
	 * Returns the icon of the portlet.
	 *
	 * @return the icon of the portlet
	 */
	public String getIcon();

	/**
	 * Returns <code>true</code> to include the portlet and make it available to
	 * be made active.
	 *
	 * @return <code>true</code> to include the portlet and make it available to
	 be made active
	 */
	public boolean getInclude();

	/**
	 * Returns the names of the classes that represent indexers associated with
	 * the portlet.
	 *
	 * @return the names of the classes that represent indexers associated with
	 the portlet
	 */
	public java.util.List<String> getIndexerClasses();

	/**
	 * Returns the indexer instances of the portlet.
	 *
	 * @return the indexer instances of the portlet
	 */
	public java.util.List<com.liferay.portal.kernel.search.Indexer<?>>
		getIndexerInstances();

	/**
	 * Returns the init parameters of the portlet.
	 *
	 * @return init parameters of the portlet
	 */
	public java.util.Map<String, String> getInitParams();

	/**
	 * Returns <code>true</code> if the portlet can be added multiple times to a
	 * layout.
	 *
	 * @return <code>true</code> if the portlet can be added multiple times to a
	 layout
	 */
	public boolean getInstanceable();

	/**
	 * Returns the instance ID of the portlet.
	 *
	 * @return the instance ID of the portlet
	 */
	public String getInstanceId();

	/**
	 * Returns <code>true</code> to allow the portlet to be cached within the
	 * layout.
	 *
	 * @return <code>true</code> if the portlet can be cached within the layout
	 */
	public boolean getLayoutCacheable();

	/**
	 * Returns <code>true</code> if the portlet goes into the maximized state
	 * when the user goes into the edit mode.
	 *
	 * @return <code>true</code> if the portlet goes into the maximized state
	 when the user goes into the edit mode
	 */
	public boolean getMaximizeEdit();

	/**
	 * Returns <code>true</code> if the portlet goes into the maximized state
	 * when the user goes into the help mode.
	 *
	 * @return <code>true</code> if the portlet goes into the maximized state
	 when the user goes into the help mode
	 */
	public boolean getMaximizeHelp();

	/**
	 * Returns the maximum size of buffered bytes before storing occurs.
	 *
	 * @return the maximum size of buffered bytes before storing occurs
	 */
	public int getMultipartFileSizeThreshold();

	/**
	 * Returns the directory for storing uploaded files.
	 *
	 * @return the directory for storing uploaded files
	 */
	public String getMultipartLocation();

	/**
	 * Returns the maximum number of bytes permitted for an uploaded file.
	 *
	 * @return the maximum number of bytes permitted for an uploaded file
	 */
	public long getMultipartMaxFileSize();

	/**
	 * Returns the maximum number of bytes permitted for a multipart request.
	 *
	 * @return the maximum number of bytes permitted for a multipart request
	 */
	public long getMultipartMaxRequestSize();

	/**
	 * Returns the name of the open search class of the portlet.
	 *
	 * @return the name of the open search class of the portlet
	 */
	public String getOpenSearchClass();

	/**
	 * Returns the indexer instance of the portlet.
	 *
	 * @return the indexer instance of the portlet
	 */
	public com.liferay.portal.kernel.search.OpenSearch getOpenSearchInstance();

	/**
	 * Returns the parent struts path of the portlet.
	 *
	 * @return the parent struts path of the portlet.
	 */
	public String getParentStrutsPath();

	/**
	 * Returns the name of the permission propagator class of the portlet.
	 *
	 * @return the name of the permission propagator class of the portlet
	 */
	public String getPermissionPropagatorClass();

	/**
	 * Returns the permission propagator instance of the portlet.
	 *
	 * @return the permission propagator instance of the portlet
	 */
	public com.liferay.portal.kernel.security.permission.propagator.
		PermissionPropagator getPermissionPropagatorInstance();

	/**
	 * Returns the plugin ID of the portlet.
	 *
	 * @return the plugin ID of the portlet
	 */
	public String getPluginId();

	/**
	 * Returns this portlet's plugin package.
	 *
	 * @return this portlet's plugin package
	 */
	public com.liferay.portal.kernel.plugin.PluginPackage getPluginPackage();

	/**
	 * Returns the plugin type of the portlet.
	 *
	 * @return the plugin type of the portlet
	 */
	public String getPluginType();

	/**
	 * Returns the name of the POP message listener class of the portlet.
	 *
	 * @return the name of the POP message listener class of the portlet
	 */
	public String getPopMessageListenerClass();

	/**
	 * Returns the POP message listener instance of the portlet.
	 *
	 * @return the POP message listener instance of the portlet
	 */
	public com.liferay.portal.kernel.pop.MessageListener
		getPopMessageListenerInstance();

	/**
	 * Returns <code>true</code> if the portlet goes into the pop up state when
	 * the user goes into the print mode.
	 *
	 * @return <code>true</code> if the portlet goes into the pop up state when
	 the user goes into the print mode
	 */
	public boolean getPopUpPrint();

	/**
	 * Returns this portlet's application.
	 *
	 * @return this portlet's application
	 */
	public PortletApp getPortletApp();

	/**
	 * Returns the name of the portlet class of the portlet.
	 *
	 * @return the name of the portlet class of the portlet
	 */
	public String getPortletClass();

	public String getPortletConfigurationListenerClass();

	public com.liferay.portal.kernel.portlet.PortletConfigurationListener
		getPortletConfigurationListenerInstance();

	/**
	 * Returns the name of the portlet data handler class of the portlet.
	 *
	 * @return the name of the portlet data handler class of the portlet
	 */
	public String getPortletDataHandlerClass();

	/**
	 * Returns the portlet data handler instance of the portlet.
	 *
	 * @return the portlet data handler instance of the portlet
	 */
	public com.liferay.exportimport.kernel.lar.PortletDataHandler
		getPortletDataHandlerInstance();

	/**
	 * Returns the portlet's CSS/JavaScript resource dependencies.
	 *
	 * @return the portlet's CSS/JavaScript resource dependencies
	 */
	public java.util.List
		<com.liferay.portal.kernel.model.portlet.PortletDependency>
			getPortletDependencies();

	/**
	 * Returns the filters of the portlet.
	 *
	 * @return filters of the portlet
	 */
	public java.util.Map<String, PortletFilter> getPortletFilters();

	/**
	 * Returns the portlet info of the portlet.
	 *
	 * @return portlet info of the portlet
	 */
	public PortletInfo getPortletInfo();

	/**
	 * Returns the name of the portlet layout listener class of the portlet.
	 *
	 * @return the name of the portlet layout listener class of the portlet
	 */
	public String getPortletLayoutListenerClass();

	/**
	 * Returns the portlet layout listener instance of the portlet.
	 *
	 * @return the portlet layout listener instance of the portlet
	 */
	public com.liferay.portal.kernel.portlet.PortletLayoutListener
		getPortletLayoutListenerInstance();

	/**
	 * Returns the portlet modes of the portlet.
	 *
	 * @return portlet modes of the portlet
	 */
	public java.util.Map<String, java.util.Set<String>> getPortletModes();

	/**
	 * Returns the name of the portlet.
	 *
	 * @return the display name of the portlet
	 */
	public String getPortletName();

	/**
	 * Returns the name of the portlet URL class of the portlet.
	 *
	 * @return the name of the portlet URL class of the portlet
	 */
	public String getPortletURLClass();

	/**
	 * Returns <code>true</code> if preferences are shared across the entire
	 * company.
	 *
	 * @return <code>true</code> if preferences are shared across the entire
	 company
	 */
	public boolean getPreferencesCompanyWide();

	/**
	 * Returns <code>true</code> if preferences are owned by the group when the
	 * portlet is shown in a group layout. Returns <code>false</code> if
	 * preferences are owned by the user at all times.
	 *
	 * @return <code>true</code> if preferences are owned by the group when the
	 portlet is shown in a group layout; <code>false</code> if
	 preferences are owned by the user at all times.
	 */
	public boolean getPreferencesOwnedByGroup();

	/**
	 * Returns <code>true</code> if preferences are unique per layout.
	 *
	 * @return <code>true</code> if preferences are unique per layout
	 */
	public boolean getPreferencesUniquePerLayout();

	/**
	 * Returns the name of the preferences validator class of the portlet.
	 *
	 * @return the name of the preferences validator class of the portlet
	 */
	public String getPreferencesValidator();

	/**
	 * Returns <code>true</code> if the portlet does not share request
	 * attributes with the portal or portlets from another WAR.
	 *
	 * @return <code>true</code> if the portlet does not share request
	 attributes with the portal or portlets from another WAR
	 */
	public boolean getPrivateRequestAttributes();

	/**
	 * Returns <code>true</code> if the portlet does not share session
	 * attributes with the portal.
	 *
	 * @return <code>true</code> if the portlet does not share session
	 attributes with the portal
	 */
	public boolean getPrivateSessionAttributes();

	/**
	 * Returns the processing event from a namespace URI and a local part.
	 *
	 * @param uri the namespace URI
	 * @param localPart the local part
	 * @return the processing event from a namespace URI and a local part
	 */
	public com.liferay.portal.kernel.xml.QName getProcessingEvent(
		String uri, String localPart);

	/**
	 * Returns the processing events of the portlet.
	 *
	 * @return the processing events of the portlet
	 */
	public java.util.Set<com.liferay.portal.kernel.xml.QName>
		getProcessingEvents();

	/**
	 * Returns the public render parameter from an identifier.
	 *
	 * @param identifier the identifier
	 * @return the public render parameter from an identifier
	 */
	public PublicRenderParameter getPublicRenderParameter(String identifier);

	/**
	 * Returns the spublic render parameter from a namespace URI and a local
	 * part.
	 *
	 * @param uri the namespace URI
	 * @param localPart the local part
	 * @return the spublic render parameter from a namespace URI and a local
	 part
	 */
	public PublicRenderParameter getPublicRenderParameter(
		String uri, String localPart);

	/**
	 * Returns the public render parameters of the portlet.
	 *
	 * @return the public render parameters of the portlet
	 */
	public java.util.Set<PublicRenderParameter> getPublicRenderParameters();

	/**
	 * Returns the publishing events of the portlet.
	 *
	 * @return the publishing events of the portlet
	 */
	public java.util.Set<com.liferay.portal.kernel.xml.QName>
		getPublishingEvents();

	/**
	 * Returns <code>true</code> if the portlet is ready to be used.
	 *
	 * @return <code>true</code> if the portlet is ready to be used
	 */
	public boolean getReady();

	/**
	 * Returns the render timeout of the portlet.
	 *
	 * @return the render timeout of the portlet
	 */
	public int getRenderTimeout();

	/**
	 * Returns the render weight of the portlet.
	 *
	 * @return the render weight of the portlet
	 */
	public int getRenderWeight();

	/**
	 * Returns the resource bundle of the portlet.
	 *
	 * @return resource bundle of the portlet
	 */
	public String getResourceBundle();

	/**
	 * Returns <code>true</code> if the portlet restores to the current view
	 * from the maximized state.
	 *
	 * @return <code>true</code> if the portlet restores to the current view
	 from the maximized state
	 */
	public boolean getRestoreCurrentView();

	/**
	 * Returns the role mappers of the portlet.
	 *
	 * @return role mappers of the portlet
	 */
	public java.util.Map<String, String> getRoleMappers();

	/**
	 * Returns an array of required roles of the portlet.
	 *
	 * @return an array of required roles of the portlet
	 */
	public String[] getRolesArray();

	/**
	 * Returns the root portlet of this portlet instance.
	 *
	 * @return the root portlet of this portlet instance
	 */
	public Portlet getRootPortlet();

	/**
	 * Returns the root portlet ID of the portlet.
	 *
	 * @return the root portlet ID of the portlet
	 */
	public String getRootPortletId();

	/**
	 * Returns the scheduler entries of the portlet.
	 *
	 * @return the scheduler entries of the portlet
	 */
	public java.util.List<com.liferay.portal.kernel.scheduler.SchedulerEntry>
		getSchedulerEntries();

	/**
	 * Returns <code>true</code> if the portlet supports scoping of data.
	 *
	 * @return <code>true</code> if the portlet supports scoping of data
	 */
	public boolean getScopeable();

	/**
	 * Returns <code>true</code> if users are shown that they do not have access
	 * to the portlet.
	 *
	 * @return <code>true</code> if users are shown that they do not have access
	 to the portlet
	 */
	public boolean getShowPortletAccessDenied();

	/**
	 * Returns <code>true</code> if users are shown that the portlet is
	 * inactive.
	 *
	 * @return <code>true</code> if users are shown that the portlet is inactive
	 */
	public boolean getShowPortletInactive();

	/**
	 * Returns <code>true</code> if the portlet uses Single Page Application.
	 *
	 * @return <code>true</code> if the portlet uses Single Page Application
	 */
	public boolean getSinglePageApplication();

	/**
	 * Returns the names of the classes that represent social activity
	 * interpreters associated with the portlet.
	 *
	 * @return the names of the classes that represent social activity
	 interpreters associated with the portlet
	 */
	public java.util.List<String> getSocialActivityInterpreterClasses();

	/**
	 * Returns the social activity interpreter instances of the portlet.
	 *
	 * @return the social activity interpreter instances of the portlet
	 */
	public java.util.List
		<com.liferay.social.kernel.model.SocialActivityInterpreter>
			getSocialActivityInterpreterInstances();

	/**
	 * Returns the name of the social request interpreter class of the portlet.
	 *
	 * @return the name of the social request interpreter class of the portlet
	 */
	public String getSocialRequestInterpreterClass();

	/**
	 * Returns the name of the social request interpreter instance of the
	 * portlet.
	 *
	 * @return the name of the social request interpreter instance of the
	 portlet
	 */
	public com.liferay.social.kernel.model.SocialRequestInterpreter
		getSocialRequestInterpreterInstance();

	/**
	 * Returns the names of the classes that represent staged model data
	 * handlers associated with the portlet.
	 *
	 * @return the names of the classes that represent staged model data
	 handlers associated with the portlet
	 */
	public java.util.List<String> getStagedModelDataHandlerClasses();

	/**
	 * Returns the staged model data handler instances of the portlet.
	 *
	 * @return the staged model data handler instances of the portlet
	 */
	public java.util.List
		<com.liferay.exportimport.kernel.lar.StagedModelDataHandler<?>>
			getStagedModelDataHandlerInstances();

	/**
	 * Returns <code>true</code> if the portlet is a static portlet that is
	 * cannot be moved.
	 *
	 * @return <code>true</code> if the portlet is a static portlet that is
	 cannot be moved
	 */
	public boolean getStatic();

	/**
	 * Returns <code>true</code> if the portlet is a static portlet at the end
	 * of a list of portlets.
	 *
	 * @return <code>true</code> if the portlet is a static portlet at the end
	 of a list of portlets
	 */
	public boolean getStaticEnd();

	/**
	 * Returns the path for static resources served by this portlet.
	 *
	 * @return the path for static resources served by this portlet
	 */
	public String getStaticResourcePath();

	/**
	 * Returns <code>true</code> if the portlet is a static portlet at the start
	 * of a list of portlets.
	 *
	 * @return <code>true</code> if the portlet is a static portlet at the start
	 of a list of portlets
	 */
	public boolean getStaticStart();

	/**
	 * Returns the struts path of the portlet.
	 *
	 * @return the struts path of the portlet
	 */
	public String getStrutsPath();

	/**
	 * Returns the supported locales of the portlet.
	 *
	 * @return the supported locales of the portlet
	 */
	public java.util.Set<String> getSupportedLocales();

	/**
	 * Returns <code>true</code> if the portlet is a system portlet that a user
	 * cannot manually add to their page.
	 *
	 * @return <code>true</code> if the portlet is a system portlet that a user
	 cannot manually add to their page
	 */
	public boolean getSystem();

	/**
	 * Returns the name of the template handler class of the portlet.
	 *
	 * @return the name of the template handler class of the portlet
	 */
	public String getTemplateHandlerClass();

	/**
	 * Returns the template handler instance of the portlet.
	 *
	 * @return the template handler instance of the portlet
	 */
	public com.liferay.portal.kernel.template.TemplateHandler
		getTemplateHandlerInstance();

	/**
	 * Returns the timestamp of the portlet.
	 *
	 * @return the timestamp of the portlet
	 */
	public long getTimestamp();

	/**
	 * Returns the names of the classes that represent trash handlers associated
	 * with the portlet.
	 *
	 * @return the names of the classes that represent trash handlers associated
	 with the portlet
	 */
	public java.util.List<String> getTrashHandlerClasses();

	/**
	 * Returns the trash handler instances of the portlet.
	 *
	 * @return the trash handler instances of the portlet
	 */
	public java.util.List<com.liferay.portal.kernel.trash.TrashHandler>
		getTrashHandlerInstances();

	/**
	 * Returns <code>true</code> if the portlet is an undeployed portlet.
	 *
	 * @return <code>true</code> if the portlet is a placeholder of an
	 undeployed portlet
	 */
	public boolean getUndeployedPortlet();

	/**
	 * Returns the unlinked roles of the portlet.
	 *
	 * @return unlinked roles of the portlet
	 */
	public java.util.Set<String> getUnlinkedRoles();

	/**
	 * Returns the name of the URL encoder class of the portlet.
	 *
	 * @return the name of the URL encoder class of the portlet
	 */
	public String getURLEncoderClass();

	/**
	 * Returns the URL encoder instance of the portlet.
	 *
	 * @return the URL encoder instance of the portlet
	 */
	public com.liferay.portal.kernel.servlet.URLEncoder getURLEncoderInstance();

	/**
	 * Returns <code>true</code> if the portlet uses the default template.
	 *
	 * @return <code>true</code> if the portlet uses the default template
	 */
	public boolean getUseDefaultTemplate();

	/**
	 * Returns the user ID of the portlet. This only applies when the portlet is
	 * added by a user in a customizable layout.
	 *
	 * @return the user ID of the portlet
	 */
	public long getUserId();

	/**
	 * Returns the class loader resource path to the use notification
	 * definitions of the portlet.
	 *
	 * @return the class loader resource path to the use notification
	 definitions of the portlet
	 */
	public String getUserNotificationDefinitions();

	/**
	 * Returns the names of the classes that represent user notification
	 * handlers associated with the portlet.
	 *
	 * @return the names of the classes that represent user notification
	 handlers associated with the portlet
	 */
	public java.util.List<String> getUserNotificationHandlerClasses();

	/**
	 * Returns the user notification handler instances of the portlet.
	 *
	 * @return the user notification handler instances of the portlet
	 */
	public java.util.List
		<com.liferay.portal.kernel.notifications.UserNotificationHandler>
			getUserNotificationHandlerInstances();

	/**
	 * Returns the user principal strategy of the portlet.
	 *
	 * @return the user principal strategy of the portlet
	 */
	public String getUserPrincipalStrategy();

	/**
	 * Returns the virtual path of the portlet.
	 *
	 * @return the virtual path of the portlet
	 */
	public String getVirtualPath();

	/**
	 * Returns the name of the WebDAV storage class of the portlet.
	 *
	 * @return the name of the WebDAV storage class of the portlet
	 */
	public String getWebDAVStorageClass();

	/**
	 * Returns the name of the WebDAV storage instance of the portlet.
	 *
	 * @return the name of the WebDAV storage instance of the portlet
	 */
	public com.liferay.portal.kernel.webdav.WebDAVStorage
		getWebDAVStorageInstance();

	/**
	 * Returns the name of the WebDAV storage token of the portlet.
	 *
	 * @return the name of the WebDAV storage token of the portlet
	 */
	public String getWebDAVStorageToken();

	/**
	 * Returns the window states of the portlet.
	 *
	 * @return window states of the portlet
	 */
	public java.util.Map<String, java.util.Set<String>> getWindowStates();

	/**
	 * Returns the names of the classes that represent workflow handlers
	 * associated with the portlet.
	 *
	 * @return the names of the classes that represent workflow handlers
	 associated with the portlet
	 */
	public java.util.List<String> getWorkflowHandlerClasses();

	/**
	 * Returns the workflow handler instances of the portlet.
	 *
	 * @return the workflow handler instances of the portlet
	 */
	public java.util.List<com.liferay.portal.kernel.workflow.WorkflowHandler<?>>
		getWorkflowHandlerInstances();

	/**
	 * Returns the name of the XML-RPC method class of the portlet.
	 *
	 * @return the name of the XML-RPC method class of the portlet
	 */
	public String getXmlRpcMethodClass();

	/**
	 * Returns the name of the XML-RPC method instance of the portlet.
	 *
	 * @return the name of the XML-RPC method instance of the portlet
	 */
	public com.liferay.portal.kernel.xmlrpc.Method getXmlRpcMethodInstance();

	/**
	 * Returns <code>true</code> if the user has the permission to add the
	 * portlet to a layout.
	 *
	 * @param userId the primary key of the user
	 * @return <code>true</code> if the user has the permission to add the
	 portlet to a layout
	 */
	public boolean hasAddPortletPermission(long userId);

	public boolean hasFooterPortalCss();

	public boolean hasFooterPortalJavaScript();

	public boolean hasFooterPortletCss();

	public boolean hasFooterPortletJavaScript();

	public int hashCode();

	public boolean hasHeaderPortalCss();

	public boolean hasHeaderPortalJavaScript();

	public boolean hasHeaderPortletCss();

	public boolean hasHeaderPortletJavaScript();

	/**
	 * Returns <code>true</code> if the portlet supports more than one mime
	 * type.
	 *
	 * @return <code>true</code> if the portlet supports more than one mime type
	 */
	public boolean hasMultipleMimeTypes();

	/**
	 * Returns <code>true</code> if the portlet supports the specified mime type
	 * and portlet mode.
	 *
	 * @param mimeType the mime type
	 * @param portletMode the portlet mode
	 * @return <code>true</code> if the portlet supports the specified mime type
	 and portlet mode
	 */
	public boolean hasPortletMode(
		String mimeType, jakarta.portlet.PortletMode portletMode);

	/**
	 * Returns <code>true</code> if the portlet has a role with the specified
	 * name.
	 *
	 * @param roleName the role name
	 * @return <code>true</code> if the portlet has a role with the specified
	 name
	 */
	public boolean hasRoleWithName(String roleName);

	/**
	 * Returns <code>true</code> if the portlet supports the specified mime type
	 * and window state.
	 *
	 * @param mimeType the mime type
	 * @param windowState the window state
	 * @return <code>true</code> if the portlet supports the specified mime type
	 and window state
	 */
	public boolean hasWindowState(
		String mimeType, jakarta.portlet.WindowState windowState);

	/**
	 * Returns <code>true</code> if an action URL for this portlet should cause
	 * an auto redirect.
	 *
	 * @return <code>true</code> if an action URL for this portlet should cause
	 an auto redirect
	 */
	public boolean isActionURLRedirect();

	/**
	 * Returns <code>true</code> if default resources for the portlet are added
	 * to a page.
	 *
	 * @return <code>true</code> if default resources for the portlet are added
	 to a page
	 */
	public boolean isAddDefaultResource();

	/**
	 * Returns <code>true</code> if the portlet can be displayed via Ajax.
	 *
	 * @return <code>true</code> if the portlet can be displayed via Ajax
	 */
	public boolean isAjaxable();

	/**
	 * Returns <code>true</code> if the portlet supports asynchronous processing
	 * in resource requests.
	 *
	 * @return <code>true</code> if the portlet supports asynchrounous
	 processing in resource requests
	 */
	public boolean isAsyncSupported();

	public boolean isFullPageDisplayable();

	/**
	 * Returns <code>true</code> to include the portlet and make it available to
	 * be made active.
	 *
	 * @return <code>true</code> to include the portlet and make it available to
	 be made active
	 */
	public boolean isInclude();

	/**
	 * Returns <code>true</code> if the portlet can be added multiple times to a
	 * layout.
	 *
	 * @return <code>true</code> if the portlet can be added multiple times to a
	 layout
	 */
	public boolean isInstanceable();

	/**
	 * Returns <code>true</code> to allow the portlet to be cached within the
	 * layout.
	 *
	 * @return <code>true</code> if the portlet can be cached within the layout
	 */
	public boolean isLayoutCacheable();

	/**
	 * Returns <code>true</code> if the portlet goes into the maximized state
	 * when the user goes into the edit mode.
	 *
	 * @return <code>true</code> if the portlet goes into the maximized state
	 when the user goes into the edit mode
	 */
	public boolean isMaximizeEdit();

	/**
	 * Returns <code>true</code> if the portlet goes into the maximized state
	 * when the user goes into the help mode.
	 *
	 * @return <code>true</code> if the portlet goes into the maximized state
	 when the user goes into the help mode
	 */
	public boolean isMaximizeHelp();

	/**
	 * Returns <code>true</code> if the portlet's
	 * <code>serveResource(ResourceRequest,ResourceResponse)</code> method
	 * should be invoked during a partial action triggered by a different
	 * portlet on the same portal page.
	 *
	 * @return <code>true</code> if the portlet's
	 <code>serveResource(ResourceRequest,ResourceResponse)</code>
	 method should be invoked during a partial action triggered by a
	 different portlet on the same portal page
	 */
	public boolean isPartialActionServeResource();

	/**
	 * Returns <code>true</code> if the portlet goes into the pop up state when
	 * the user goes into the print mode.
	 *
	 * @return <code>true</code> if the portlet goes into the pop up state when
	 the user goes into the print mode
	 */
	public boolean isPopUpPrint();

	/**
	 * Returns <code>true</code> if the CSS resource dependencies specified in
	 * <code>portlet.xml</code>, @{@link jakarta.portlet.annotations.Dependency},
	 * {@link jakarta.portlet.HeaderResponse#addDependency(String, String,
	 * String)}, or {@link jakarta.portlet.HeaderResponse#addDependency(String,
	 * String, String, String)} are to be referenced in the page's header.
	 *
	 * @return <code>true</code> if the specified CSS resource dependencies are
	 to be referenced in the page's header
	 */
	public boolean isPortletDependencyCssEnabled();

	/**
	 * Returns <code>true</code> if the JavaScript resource dependencies
	 * specified in <code>portlet.xml</code>, @{@link
	 * jakarta.portlet.annotations.Dependency}, {@link
	 * jakarta.portlet.HeaderResponse#addDependency(String, String, String)}, or
	 * {@link jakarta.portlet.HeaderResponse#addDependency(String, String, String,
	 * String)} are to be referenced in the page's header.
	 *
	 * @return <code>true</code> if the specified JavaScript resource
	 dependencies are to be referenced in the page's header
	 */
	public boolean isPortletDependencyJavaScriptEnabled();

	/**
	 * Returns <code>true</code> if preferences are shared across the entire
	 * company.
	 *
	 * @return <code>true</code> if preferences are shared across the entire
	 company
	 */
	public boolean isPreferencesCompanyWide();

	/**
	 * Returns <code>true</code> if preferences are owned by the group when the
	 * portlet is shown in a group layout. Returns <code>false</code> if
	 * preferences are owned by the user at all times.
	 *
	 * @return <code>true</code> if preferences are owned by the group when the
	 portlet is shown in a group layout; <code>false</code> if
	 preferences are owned by the user at all times.
	 */
	public boolean isPreferencesOwnedByGroup();

	/**
	 * Returns <code>true</code> if preferences are unique per layout.
	 *
	 * @return <code>true</code> if preferences are unique per layout
	 */
	public boolean isPreferencesUniquePerLayout();

	/**
	 * Returns <code>true</code> if the portlet does not share request
	 * attributes with the portal or portlets from another WAR.
	 *
	 * @return <code>true</code> if the portlet does not share request
	 attributes with the portal or portlets from another WAR
	 */
	public boolean isPrivateRequestAttributes();

	/**
	 * Returns <code>true</code> if the portlet does not share session
	 * attributes with the portal.
	 *
	 * @return <code>true</code> if the portlet does not share session
	 attributes with the portal
	 */
	public boolean isPrivateSessionAttributes();

	/**
	 * Returns <code>true</code> if the portlet is ready to be used.
	 *
	 * @return <code>true</code> if the portlet is ready to be used
	 */
	public boolean isReady();

	/**
	 * Returns <code>true</code> if the portlet will only process namespaced
	 * parameters.
	 *
	 * @return <code>true</code> if the portlet will only process namespaced
	 parameters
	 */
	public boolean isRequiresNamespacedParameters();

	/**
	 * Returns <code>true</code> if the portlet restores to the current view
	 * from the maximized state.
	 *
	 * @return <code>true</code> if the portlet restores to the current view
	 from the maximized state
	 */
	public boolean isRestoreCurrentView();

	/**
	 * Returns <code>true</code> if the portlet supports scoping of data.
	 *
	 * @return <code>true</code> if the portlet supports scoping of data
	 */
	public boolean isScopeable();

	/**
	 * Returns <code>true</code> if users are shown that they do not have access
	 * to the portlet.
	 *
	 * @return <code>true</code> if users are shown that they do not have access
	 to the portlet
	 */
	public boolean isShowPortletAccessDenied();

	/**
	 * Returns <code>true</code> if users are shown that the portlet is
	 * inactive.
	 *
	 * @return <code>true</code> if users are shown that the portlet is inactive
	 */
	public boolean isShowPortletInactive();

	/**
	 * Returns <code>true</code> if the portlet uses Single Page Application.
	 *
	 * @return <code>true</code> if the portlet uses Single Page Application
	 */
	public boolean isSinglePageApplication();

	/**
	 * Returns <code>true</code> if the portlet is a static portlet that is
	 * cannot be moved.
	 *
	 * @return <code>true</code> if the portlet is a static portlet that is
	 cannot be moved
	 */
	public boolean isStatic();

	/**
	 * Returns <code>true</code> if the portlet is a static portlet at the end
	 * of a list of portlets.
	 *
	 * @return <code>true</code> if the portlet is a static portlet at the end
	 of a list of portlets
	 */
	public boolean isStaticEnd();

	/**
	 * Returns <code>true</code> if the portlet is a static portlet at the start
	 * of a list of portlets.
	 *
	 * @return <code>true</code> if the portlet is a static portlet at the start
	 of a list of portlets
	 */
	public boolean isStaticStart();

	/**
	 * Returns <code>true</code> if the portlet is a system portlet that a user
	 * cannot manually add to their page.
	 *
	 * @return <code>true</code> if the portlet is a system portlet that a user
	 cannot manually add to their page
	 */
	public boolean isSystem();

	/**
	 * Returns <code>true</code> if the portlet is an undeployed portlet.
	 *
	 * @return <code>true</code> if the portlet is a placeholder of an
	 undeployed portlet
	 */
	public boolean isUndeployedPortlet();

	/**
	 * Returns <code>true</code> if the portlet uses the default template.
	 *
	 * @return <code>true</code> if the portlet uses the default template
	 */
	public boolean isUseDefaultTemplate();

	/**
	 * Link the role names set in portlet.xml with the Liferay roles set in
	 * liferay-portlet.xml.
	 */
	public void linkRoles();

	/**
	 * Sets the action timeout of the portlet.
	 *
	 * @param actionTimeout the action timeout of the portlet
	 */
	public void setActionTimeout(int actionTimeout);

	/**
	 * Set to <code>true</code> if an action URL for this portlet should cause
	 * an auto redirect.
	 *
	 * @param actionURLRedirect boolean value for whether an action URL for this
	 portlet should cause an auto redirect
	 */
	public void setActionURLRedirect(boolean actionURLRedirect);

	/**
	 * Set to <code>true</code> if default resources for the portlet are added
	 * to a page.
	 *
	 * @param addDefaultResource boolean value for whether or not default
	 resources for the portlet are added to a page
	 */
	public void setAddDefaultResource(boolean addDefaultResource);

	/**
	 * Set to <code>true</code> if the portlet can be displayed via Ajax.
	 *
	 * @param ajaxable boolean value for whether the portlet can be displayed
	 via Ajax
	 */
	public void setAjaxable(boolean ajaxable);

	/**
	 * Sets the application types of the portlet.
	 *
	 * @param applicationTypes the application types of the portlet
	 */
	public void setApplicationTypes(
		java.util.Set
			<com.liferay.portal.kernel.application.type.ApplicationType>
				applicationTypes);

	/**
	 * Sets the names of the classes that represent asset types associated with
	 * the portlet.
	 *
	 * @param assetRendererFactoryClasses the names of the classes that
	 represent asset types associated with the portlet
	 */
	public void setAssetRendererFactoryClasses(
		java.util.List<String> assetRendererFactoryClasses);

	/**
	 * Set to <code>true</code> if the portlet supports asynchronous processing
	 * in resource requests.
	 *
	 * @param asyncSupported boolean value for whether the portlet supports
	 asynchronous processing in resource requests
	 */
	public void setAsyncSupported(boolean asyncSupported);

	/**
	 * Sets the names of the parameters that will be automatically propagated
	 * through the portlet.
	 *
	 * @param autopropagatedParameters the names of the parameters that will be
	 automatically propagated through the portlet
	 */
	public void setAutopropagatedParameters(
		java.util.Set<String> autopropagatedParameters);

	/**
	 * Sets the category names of the portlet.
	 *
	 * @param categoryNames the category names of the portlet
	 */
	public void setCategoryNames(java.util.Set<String> categoryNames);

	/**
	 * Sets the configuration action class of the portlet.
	 *
	 * @param configurationActionClass the configuration action class of the
	 portlet
	 */
	public void setConfigurationActionClass(String configurationActionClass);

	/**
	 * Set the name of the category of the Control Panel where the portlet will
	 * be shown.
	 *
	 * @param controlPanelEntryCategory the name of the category of the Control
	 Panel where the portlet will be shown
	 */
	public void setControlPanelEntryCategory(String controlPanelEntryCategory);

	/**
	 * Sets the name of the class that will control when the portlet will be
	 * shown in the Control Panel.
	 *
	 * @param controlPanelEntryClass the name of the class that will control
	 when the portlet will be shown in the Control Panel
	 */
	public void setControlPanelEntryClass(String controlPanelEntryClass);

	/**
	 * Sets the relative weight of the portlet with respect to the other
	 * portlets in the same category of the Control Panel.
	 *
	 * @param controlPanelEntryWeight the relative weight of the portlet with
	 respect to the other portlets in the same category of the Control
	 Panel
	 */
	public void setControlPanelEntryWeight(double controlPanelEntryWeight);

	/**
	 * Sets the name of the CSS class that will be injected in the DIV that
	 * wraps this portlet.
	 *
	 * @param cssClassWrapper the name of the CSS class that will be injected in
	 the DIV that wraps this portlet
	 */
	public void setCssClassWrapper(String cssClassWrapper);

	/**
	 * Sets the names of the classes that represent custom attribute displays
	 * associated with the portlet.
	 *
	 * @param customAttributesDisplayClasses the names of the classes that
	 represent custom attribute displays associated with the portlet
	 */
	public void setCustomAttributesDisplayClasses(
		java.util.List<String> customAttributesDisplayClasses);

	/**
	 * Sets the default plugin settings of the portlet.
	 *
	 * @param pluginSetting the plugin setting
	 */
	public void setDefaultPluginSetting(PluginSetting pluginSetting);

	/**
	 * Sets the default preferences of the portlet.
	 *
	 * @param defaultPreferences the default preferences of the portlet
	 */
	public void setDefaultPreferences(String defaultPreferences);

	/**
	 * Sets the display name of the portlet.
	 *
	 * @param displayName the display name of the portlet
	 */
	public void setDisplayName(String displayName);

	/**
	 * Sets expiration cache of the portlet.
	 *
	 * @param expCache expiration cache of the portlet
	 */
	public void setExpCache(Integer expCache);

	/**
	 * Sets a list of CSS files that will be referenced from the page's footer
	 * relative to the portal's context path.
	 *
	 * @param footerPortalCss a list of CSS files that will be referenced from
	 the page's footer relative to the portal's context path
	 */
	public void setFooterPortalCss(java.util.List<String> footerPortalCss);

	/**
	 * Sets a list of JavaScript files that will be referenced from the page's
	 * footer relative to the portal's context path.
	 *
	 * @param footerPortalJavaScript a list of JavaScript files that will be
	 referenced from the page's footer relative to the portal's context
	 path
	 */
	public void setFooterPortalJavaScript(
		java.util.List<String> footerPortalJavaScript);

	/**
	 * Sets a list of CSS files that will be referenced from the page's footer
	 * relative to the portlet's context path.
	 *
	 * @param footerPortletCss a list of CSS files that will be referenced from
	 the page's footer relative to the portlet's context path
	 */
	public void setFooterPortletCss(java.util.List<String> footerPortletCss);

	/**
	 * Sets a list of JavaScript files that will be referenced from the page's
	 * footer relative to the portlet's context path.
	 *
	 * @param footerPortletJavaScript a list of JavaScript files that will be
	 referenced from the page's footer relative to the portlet's
	 context path
	 */
	public void setFooterPortletJavaScript(
		java.util.List<String> footerPortletJavaScript);

	/**
	 * Sets the name of the friendly URL mapper class of the portlet.
	 *
	 * @param friendlyURLMapperClass the name of the friendly URL mapper class
	 of the portlet
	 */
	public void setFriendlyURLMapperClass(String friendlyURLMapperClass);

	/**
	 * Sets the name of the friendly URL mapping of the portlet.
	 *
	 * @param friendlyURLMapping the name of the friendly URL mapping of the
	 portlet
	 */
	public void setFriendlyURLMapping(String friendlyURLMapping);

	/**
	 * Sets the class loader resource path to the friendly URL routes of the
	 * portlet.
	 *
	 * @param friendlyURLRoutes the class loader resource path to the friendly
	 URL routes of the portlet
	 */
	public void setFriendlyURLRoutes(String friendlyURLRoutes);

	/**
	 * Sets a list of CSS files that will be referenced from the page's header
	 * relative to the portal's context path.
	 *
	 * @param headerPortalCss a list of CSS files that will be referenced from
	 the page's header relative to the portal's context path
	 */
	public void setHeaderPortalCss(java.util.List<String> headerPortalCss);

	/**
	 * Sets a list of JavaScript files that will be referenced from the page's
	 * header relative to the portal's context path.
	 *
	 * @param headerPortalJavaScript a list of JavaScript files that will be
	 referenced from the page's header relative to the portal's context
	 path
	 */
	public void setHeaderPortalJavaScript(
		java.util.List<String> headerPortalJavaScript);

	/**
	 * Sets a list of CSS files that will be referenced from the page's header
	 * relative to the portlet's context path.
	 *
	 * @param headerPortletCss a list of CSS files that will be referenced from
	 the page's header relative to the portlet's context path
	 */
	public void setHeaderPortletCss(java.util.List<String> headerPortletCss);

	/**
	 * Sets a list of JavaScript files that will be referenced from the page's
	 * header relative to the portlet's context path.
	 *
	 * @param headerPortletJavaScript a list of JavaScript files that will be
	 referenced from the page's header relative to the portlet's
	 context path
	 */
	public void setHeaderPortletJavaScript(
		java.util.List<String> headerPortletJavaScript);

	/**
	 * Sets a list of attribute name prefixes that will be referenced after the
	 * HEADER_PHASE completes for each portlet. Header request attributes that
	 * have names starting with any of the prefixes will be copied from the
	 * header request to the subsequent render request.
	 *
	 * @param headerRequestAttributePrefixes a list of attribute name prefixes
	 that will be referenced after the HEADER_PHASE completes for each
	 portlet. Header request attributes that have names starting with
	 any of the prefixes will be copied from the header request to the
	 subsequent render request.
	 */
	public void setHeaderRequestAttributePrefixes(
		java.util.List<String> headerRequestAttributePrefixes);

	/**
	 * Sets the header timeout of the portlet.
	 *
	 * @param headerTimeout the header timeout of the portlet
	 */
	public void setHeaderTimeout(int headerTimeout);

	/**
	 * Sets the icon of the portlet.
	 *
	 * @param icon the icon of the portlet
	 */
	public void setIcon(String icon);

	/**
	 * Set to <code>true</code> to include the portlet and make it available to
	 * be made active.
	 *
	 * @param include boolean value for whether to include the portlet and make
	 it available to be made active
	 */
	public void setInclude(boolean include);

	/**
	 * Sets the names of the classes that represent indexers associated with the
	 * portlet.
	 *
	 * @param indexerClasses the names of the classes that represent indexers
	 associated with the portlet
	 */
	public void setIndexerClasses(java.util.List<String> indexerClasses);

	/**
	 * Sets the init parameters of the portlet.
	 *
	 * @param initParams the init parameters of the portlet
	 */
	public void setInitParams(java.util.Map<String, String> initParams);

	/**
	 * Set to <code>true</code> if the portlet can be added multiple times to a
	 * layout.
	 *
	 * @param instanceable boolean value for whether the portlet can be added
	 multiple times to a layout
	 */
	public void setInstanceable(boolean instanceable);

	/**
	 * Set to <code>true</code> to allow the portlet to be cached within the
	 * layout.
	 *
	 * @param layoutCacheable boolean value for whether the portlet can be
	 cached within the layout
	 */
	public void setLayoutCacheable(boolean layoutCacheable);

	/**
	 * Set to <code>true</code> if the portlet goes into the maximized state
	 * when the user goes into the edit mode.
	 *
	 * @param maximizeEdit boolean value for whether the portlet goes into the
	 maximized state when the user goes into the edit mode
	 */
	public void setMaximizeEdit(boolean maximizeEdit);

	/**
	 * Set to <code>true</code> if the portlet goes into the maximized state
	 * when the user goes into the help mode.
	 *
	 * @param maximizeHelp boolean value for whether the portlet goes into the
	 maximized state when the user goes into the help mode
	 */
	public void setMaximizeHelp(boolean maximizeHelp);

	/**
	 * Sets the maximum size of buffered bytes before storing occurs.
	 *
	 * @param multipartFileSizeThreshold the maximum size of buffered bytes
	 before storing occurs
	 */
	public void setMultipartFileSizeThreshold(int multipartFileSizeThreshold);

	/**
	 * Sets the directory for storing uploaded files.
	 *
	 * @param multipartLocation the directory for storing uploaded files
	 */
	public void setMultipartLocation(String multipartLocation);

	/**
	 * Sets the maximum number of bytes permitted for an uploaded file.
	 *
	 * @param multipartMaxFileSize the maximum number of bytes permitted for an
	 uploaded file
	 */
	public void setMultipartMaxFileSize(long multipartMaxFileSize);

	/**
	 * Sets the maximum number of bytes permitted for a multipart request.
	 *
	 * @param multipartMaxRequestSize the maximum number of bytes permitted for
	 a multipart request
	 */
	public void setMultipartMaxRequestSize(long multipartMaxRequestSize);

	/**
	 * Sets the name of the open search class of the portlet.
	 *
	 * @param openSearchClass the name of the open search class of the portlet
	 */
	public void setOpenSearchClass(String openSearchClass);

	/**
	 * Sets the parent struts path of the portlet.
	 *
	 * @param parentStrutsPath the parent struts path of the portlet
	 */
	public void setParentStrutsPath(String parentStrutsPath);

	/**
	 * Sets whether the portlet's serve resource should be invoked during a
	 * partial action triggered by a different portlet on the same portal page.
	 *
	 * @param partialActionServeResource whether the portlet's
	 <code>serveResource(ResourceRequest,ResourceResponse)</code>
	 method should be invoked during a partial action triggered by a
	 different portlet on the same portal page
	 */
	public void setPartialActionServeResource(
		boolean partialActionServeResource);

	/**
	 * Sets the name of the permission propagator class of the portlet.
	 */
	public void setPermissionPropagatorClass(String permissionPropagatorClass);

	/**
	 * Sets this portlet's plugin package.
	 *
	 * @param pluginPackage this portlet's plugin package
	 */
	public void setPluginPackage(
		com.liferay.portal.kernel.plugin.PluginPackage pluginPackage);

	/**
	 * Sets the name of the POP message listener class of the portlet.
	 *
	 * @param popMessageListenerClass the name of the POP message listener class
	 of the portlet
	 */
	public void setPopMessageListenerClass(String popMessageListenerClass);

	/**
	 * Sets whether the portlet goes into the pop up state when the user goes
	 * into the print mode.
	 *
	 * @param popUpPrint whether the portlet goes into the pop up state when the
	 user goes into the print mode
	 */
	public void setPopUpPrint(boolean popUpPrint);

	/**
	 * Sets this portlet's application.
	 *
	 * @param portletApp this portlet's application
	 */
	public void setPortletApp(PortletApp portletApp);

	/**
	 * Sets the name of the portlet class of the portlet.
	 *
	 * @param portletClass the name of the portlet class of the portlet
	 */
	public void setPortletClass(String portletClass);

	public void setPortletConfigurationListenerClass(
		String portletConfigurationListenerClass);

	/**
	 * Sets the name of the portlet data handler class of the portlet.
	 *
	 * @param portletDataHandlerClass the name of portlet data handler class of
	 the portlet
	 */
	public void setPortletDataHandlerClass(String portletDataHandlerClass);

	/**
	 * Sets whether the CSS resource dependencies specified in
	 * <code>portlet.xml</code>, @{@link jakarta.portlet.annotations.Dependency},
	 * {@link jakarta.portlet.HeaderResponse#addDependency(String, String,
	 * String)}, or {@link jakarta.portlet.HeaderResponse#addDependency(String,
	 * String, String, String)} are to be referenced in the page's header.
	 *
	 * @param portletDependencyCssEnabled whether the CSS resource dependencies
	 that are specified in <code>portlet.xml</code>,
	 */
	public void setPortletDependencyCssEnabled(
		boolean portletDependencyCssEnabled);

	/**
	 * Sets whether the JavaScript resource dependencies specified in
	 * <code>portlet.xml</code>, @{@link jakarta.portlet.annotations.Dependency},
	 * {@link jakarta.portlet.HeaderResponse#addDependency(String, String,
	 * String)}, or {@link jakarta.portlet.HeaderResponse#addDependency(String,
	 * String, String, String)} are to be referenced in the page's header.
	 *
	 * @param portletDependencyJavaScriptEnabled whether the JavaScript resource
	 dependencies specified in <code>portlet.xml</code>, @{@link
	 jakarta.portlet.annotations.Dependency}, {@link
	 jakarta.portlet.HeaderResponse#addDependency(String, String,
	 String)}, or {@link
	 jakarta.portlet.HeaderResponse#addDependency(String, String, String,
	 String)} are to be referenced in the page's header
	 */
	public void setPortletDependencyJavaScriptEnabled(
		boolean portletDependencyJavaScriptEnabled);

	/**
	 * Sets the filters of the portlet.
	 *
	 * @param portletFilters the filters of the portlet
	 */
	public void setPortletFilters(
		java.util.Map<String, PortletFilter> portletFilters);

	/**
	 * Sets the portlet info of the portlet.
	 *
	 * @param portletInfo the portlet info of the portlet
	 */
	public void setPortletInfo(PortletInfo portletInfo);

	/**
	 * Sets the name of the portlet layout listener class of the portlet.
	 *
	 * @param portletLayoutListenerClass the name of the portlet layout listener
	 class of the portlet
	 */
	public void setPortletLayoutListenerClass(
		String portletLayoutListenerClass);

	/**
	 * Sets the portlet modes of the portlet.
	 *
	 * @param portletModes the portlet modes of the portlet
	 */
	public void setPortletModes(
		java.util.Map<String, java.util.Set<String>> portletModes);

	/**
	 * Sets the name of the portlet.
	 *
	 * @param portletName the name of the portlet
	 */
	public void setPortletName(String portletName);

	/**
	 * Sets the name of the portlet URL class of the portlet.
	 *
	 * @param portletURLClass the name of the portlet URL class of the portlet
	 */
	public void setPortletURLClass(String portletURLClass);

	/**
	 * Set to <code>true</code> if preferences are shared across the entire
	 * company.
	 *
	 * @param preferencesCompanyWide boolean value for whether preferences are
	 shared across the entire company
	 */
	public void setPreferencesCompanyWide(boolean preferencesCompanyWide);

	/**
	 * Set to <code>true</code> if preferences are owned by the group when the
	 * portlet is shown in a group layout. Set to <code>false</code> if
	 * preferences are owned by the user at all times.
	 *
	 * @param preferencesOwnedByGroup boolean value for whether preferences are
	 owned by the group when the portlet is shown in a group layout or
	 preferences are owned by the user at all times
	 */
	public void setPreferencesOwnedByGroup(boolean preferencesOwnedByGroup);

	/**
	 * Set to <code>true</code> if preferences are unique per layout.
	 *
	 * @param preferencesUniquePerLayout boolean value for whether preferences
	 are unique per layout
	 */
	public void setPreferencesUniquePerLayout(
		boolean preferencesUniquePerLayout);

	/**
	 * Sets the name of the preferences validator class of the portlet.
	 *
	 * @param preferencesValidator the name of the preferences validator class
	 of the portlet
	 */
	public void setPreferencesValidator(String preferencesValidator);

	/**
	 * Set to <code>true</code> if the portlet does not share request attributes
	 * with the portal or portlets from another WAR.
	 *
	 * @param privateRequestAttributes boolean value for whether the portlet
	 shares request attributes with the portal or portlets from another
	 WAR
	 */
	public void setPrivateRequestAttributes(boolean privateRequestAttributes);

	/**
	 * Set to <code>true</code> if the portlet does not share session attributes
	 * with the portal.
	 *
	 * @param privateSessionAttributes boolean value for whether the portlet
	 shares session attributes with the portal
	 */
	public void setPrivateSessionAttributes(boolean privateSessionAttributes);

	/**
	 * Sets the processing events of the portlet.
	 *
	 * @param processingEvents the processing events of the portlet
	 */
	public void setProcessingEvents(
		java.util.Set<com.liferay.portal.kernel.xml.QName> processingEvents);

	/**
	 * Sets the public render parameters of the portlet.
	 *
	 * @param publicRenderParameters the public render parameters of the portlet
	 */
	public void setPublicRenderParameters(
		java.util.Set<PublicRenderParameter> publicRenderParameters);

	/**
	 * Sets the publishing events of the portlet.
	 *
	 * @param publishingEvents the publishing events of the portlet
	 */
	public void setPublishingEvents(
		java.util.Set<com.liferay.portal.kernel.xml.QName> publishingEvents);

	/**
	 * Set to <code>true</code> if the portlet is ready to be used.
	 *
	 * @param ready whether the portlet is ready to be used
	 */
	public void setReady(boolean ready);

	/**
	 * Sets the render timeout of the portlet.
	 *
	 * @param renderTimeout the render timeout of the portlet
	 */
	public void setRenderTimeout(int renderTimeout);

	/**
	 * Sets the render weight of the portlet.
	 *
	 * @param renderWeight int value for the render weight of the portlet
	 */
	public void setRenderWeight(int renderWeight);

	/**
	 * Set to <code>true</code> if the portlet will only process namespaced
	 * parameters.
	 *
	 * @param requiresNamespacedParameters boolean value for whether the portlet
	 will only process namespaced parameters
	 */
	public void setRequiresNamespacedParameters(
		boolean requiresNamespacedParameters);

	/**
	 * Sets the resource bundle of the portlet.
	 *
	 * @param resourceBundle the resource bundle of the portlet
	 */
	public void setResourceBundle(String resourceBundle);

	/**
	 * Set to <code>true</code> if the portlet restores to the current view from
	 * the maximized state.
	 *
	 * @param restoreCurrentView boolean value for whether the portlet restores
	 to the current view from the maximized state
	 */
	public void setRestoreCurrentView(boolean restoreCurrentView);

	/**
	 * Sets the role mappers of the portlet.
	 *
	 * @param roleMappers the role mappers of the portlet
	 */
	public void setRoleMappers(java.util.Map<String, String> roleMappers);

	/**
	 * Sets an array of required roles of the portlet.
	 *
	 * @param rolesArray an array of required roles of the portlet
	 */
	public void setRolesArray(String[] rolesArray);

	/**
	 * Sets the scheduler entries of the portlet.
	 *
	 * @param schedulerEntries the scheduler entries of the portlet
	 */
	public void setSchedulerEntries(
		java.util.List<com.liferay.portal.kernel.scheduler.SchedulerEntry>
			schedulerEntries);

	/**
	 * Set to <code>true</code> if the portlet supports scoping of data.
	 *
	 * @param scopeable boolean value for whether or not the the portlet
	 supports scoping of data
	 */
	public void setScopeable(boolean scopeable);

	/**
	 * Set to <code>true</code> if users are shown that they do not have access
	 * to the portlet.
	 *
	 * @param showPortletAccessDenied boolean value for whether users are shown
	 that they do not have access to the portlet
	 */
	public void setShowPortletAccessDenied(boolean showPortletAccessDenied);

	/**
	 * Set to <code>true</code> if users are shown that the portlet is inactive.
	 *
	 * @param showPortletInactive boolean value for whether users are shown that
	 the portlet is inactive
	 */
	public void setShowPortletInactive(boolean showPortletInactive);

	/**
	 * Set to <code>true</code> if the portlet uses Single Page Application.
	 *
	 * @param singlePageApplication boolean value for whether or not the the
	 portlet uses Single Page Application
	 */
	public void setSinglePageApplication(boolean singlePageApplication);

	/**
	 * Sets the names of the classes that represent social activity interpreters
	 * associated with the portlet.
	 *
	 * @param socialActivityInterpreterClasses the names of the classes that
	 represent social activity interpreters associated with the portlet
	 */
	public void setSocialActivityInterpreterClasses(
		java.util.List<String> socialActivityInterpreterClasses);

	/**
	 * Sets the name of the social request interpreter class of the portlet.
	 *
	 * @param socialRequestInterpreterClass the name of the request interpreter
	 class of the portlet
	 */
	public void setSocialRequestInterpreterClass(
		String socialRequestInterpreterClass);

	/**
	 * Sets the names of the classes that represent staged model data handlers
	 * associated with the portlet.
	 *
	 * @param stagedModelDataHandlerClasses the names of the classes that
	 represent staged model data handlers associated with the portlet
	 */
	public void setStagedModelDataHandlerClasses(
		java.util.List<String> stagedModelDataHandlerClasses);

	/**
	 * Set to <code>true</code> if the portlet is a static portlet that is
	 * cannot be moved.
	 *
	 * @param staticPortlet boolean value for whether the portlet is a static
	 portlet that cannot be moved
	 */
	public void setStatic(boolean staticPortlet);

	/**
	 * Set to <code>true</code> if the portlet is a static portlet at the start
	 * of a list of portlets.
	 *
	 * @param staticPortletStart boolean value for whether the portlet is a
	 static portlet at the start of a list of portlets
	 */
	public void setStaticStart(boolean staticPortletStart);

	/**
	 * Sets the struts path of the portlet.
	 *
	 * @param strutsPath the struts path of the portlet
	 */
	public void setStrutsPath(String strutsPath);

	/**
	 * Sets the supported locales of the portlet.
	 *
	 * @param supportedLocales the supported locales of the portlet
	 */
	public void setSupportedLocales(java.util.Set<String> supportedLocales);

	/**
	 * Set to <code>true</code> if the portlet is a system portlet that a user
	 * cannot manually add to their page.
	 *
	 * @param system boolean value for whether the portlet is a system portlet
	 that a user cannot manually add to their page
	 */
	public void setSystem(boolean system);

	/**
	 * Sets the name of the template handler class of the portlet.
	 *
	 * @param templateHandlerClass the name of template handler class of the
	 portlet
	 */
	public void setTemplateHandlerClass(String templateHandlerClass);

	/**
	 * Sets the names of the classes that represent trash handlers associated to
	 * the portlet.
	 *
	 * @param trashHandlerClasses the names of the classes that represent trash
	 handlers associated with the portlet
	 */
	public void setTrashHandlerClasses(
		java.util.List<String> trashHandlerClasses);

	/**
	 * Set to <code>true</code> if the portlet is an undeployed portlet.
	 *
	 * @param undeployedPortlet boolean value for whether the portlet is an
	 undeployed portlet
	 */
	public void setUndeployedPortlet(boolean undeployedPortlet);

	/**
	 * Sets the unlinked roles of the portlet.
	 *
	 * @param unlinkedRoles the unlinked roles of the portlet
	 */
	public void setUnlinkedRoles(java.util.Set<String> unlinkedRoles);

	/**
	 * Sets the name of the URL encoder class of the portlet.
	 *
	 * @param urlEncoderClass the name of the URL encoder class of the portlet
	 */
	public void setURLEncoderClass(String urlEncoderClass);

	/**
	 * Set to <code>true</code> if the portlet uses the default template.
	 *
	 * @param useDefaultTemplate boolean value for whether the portlet uses the
	 default template
	 */
	public void setUseDefaultTemplate(boolean useDefaultTemplate);

	/**
	 * Sets the class loader resource path to the user notification definitions
	 * of the portlet.
	 *
	 * @param userNotificationDefinitions the class loader resource path to the
	 user notification definitions of the portlet
	 */
	public void setUserNotificationDefinitions(
		String userNotificationDefinitions);

	/**
	 * Sets the names of the classes that represent user notification handlers
	 * associated with the portlet.
	 *
	 * @param userNotificationHandlerClasses the names of the classes that
	 represent user notification handlers associated with the portlet
	 */
	public void setUserNotificationHandlerClasses(
		java.util.List<String> userNotificationHandlerClasses);

	/**
	 * Sets the user principal strategy of the portlet.
	 *
	 * @param userPrincipalStrategy the user principal strategy of the portlet
	 */
	public void setUserPrincipalStrategy(String userPrincipalStrategy);

	/**
	 * Sets the virtual path of the portlet.
	 *
	 * @param virtualPath the virtual path of the portlet
	 */
	public void setVirtualPath(String virtualPath);

	/**
	 * Sets the name of the WebDAV storage class of the portlet.
	 *
	 * @param webDAVStorageClass the name of the WebDAV storage class of the
	 portlet
	 */
	public void setWebDAVStorageClass(String webDAVStorageClass);

	/**
	 * Sets the name of the WebDAV storage token of the portlet.
	 *
	 * @param webDAVStorageToken the name of the WebDAV storage token of the
	 portlet
	 */
	public void setWebDAVStorageToken(String webDAVStorageToken);

	/**
	 * Sets the window states of the portlet.
	 *
	 * @param windowStates the window states of the portlet
	 */
	public void setWindowStates(
		java.util.Map<String, java.util.Set<String>> windowStates);

	/**
	 * Sets the names of the classes that represent workflow handlers associated
	 * to the portlet.
	 *
	 * @param workflowHandlerClasses the names of the classes that represent
	 workflow handlers associated with the portlet
	 */
	public void setWorkflowHandlerClasses(
		java.util.List<String> workflowHandlerClasses);

	/**
	 * Sets the name of the XML-RPC method class of the portlet.
	 *
	 * @param xmlRpcMethodClass the name of the XML-RPC method class of the
	 portlet
	 */
	public void setXmlRpcMethodClass(String xmlRpcMethodClass);

	public void unsetReady();

}