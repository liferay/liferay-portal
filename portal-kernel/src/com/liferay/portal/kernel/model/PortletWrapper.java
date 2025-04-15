/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link Portlet}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see Portlet
 * @generated
 */
public class PortletWrapper
	extends BaseModelWrapper<Portlet>
	implements ModelWrapper<Portlet>, Portlet {

	public PortletWrapper(Portlet portlet) {
		super(portlet);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("id", getId());
		attributes.put("companyId", getCompanyId());
		attributes.put("portletId", getPortletId());
		attributes.put("roles", getRoles());
		attributes.put("active", isActive());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long id = (Long)attributes.get("id");

		if (id != null) {
			setId(id);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		String portletId = (String)attributes.get("portletId");

		if (portletId != null) {
			setPortletId(portletId);
		}

		String roles = (String)attributes.get("roles");

		if (roles != null) {
			setRoles(roles);
		}

		Boolean active = (Boolean)attributes.get("active");

		if (active != null) {
			setActive(active);
		}
	}

	/**
	 * Adds an application type.
	 *
	 * @param applicationType an application type
	 */
	@Override
	public void addApplicationType(
		com.liferay.portal.kernel.application.type.ApplicationType
			applicationType) {

		model.addApplicationType(applicationType);
	}

	/**
	 * Adds a portlet CSS/JavaScript resource dependency.
	 *
	 * @param portletDependency the portlet CSS/JavaScript resource dependency
	 */
	@Override
	public void addPortletDependency(
		com.liferay.portal.kernel.model.portlet.PortletDependency
			portletDependency) {

		model.addPortletDependency(portletDependency);
	}

	/**
	 * Adds a processing event.
	 */
	@Override
	public void addProcessingEvent(
		com.liferay.portal.kernel.xml.QName processingEvent) {

		model.addProcessingEvent(processingEvent);
	}

	/**
	 * Adds a public render parameter.
	 *
	 * @param publicRenderParameter a public render parameter
	 */
	@Override
	public void addPublicRenderParameter(
		PublicRenderParameter publicRenderParameter) {

		model.addPublicRenderParameter(publicRenderParameter);
	}

	/**
	 * Adds a publishing event.
	 */
	@Override
	public void addPublishingEvent(
		com.liferay.portal.kernel.xml.QName publishingEvent) {

		model.addPublishingEvent(publishingEvent);
	}

	/**
	 * Adds a scheduler entry.
	 */
	@Override
	public void addSchedulerEntry(
		com.liferay.portal.kernel.scheduler.SchedulerEntry schedulerEntry) {

		model.addSchedulerEntry(schedulerEntry);
	}

	/**
	 * Creates and returns a copy of this object.
	 *
	 * @return a copy of this object
	 */
	@Override
	public Object clone() {
		return new PortletWrapper((Portlet)model.clone());
	}

	@Override
	public Portlet cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Compares this portlet to the specified object.
	 *
	 * @param portlet the portlet to compare this portlet against
	 * @return the value 0 if the argument portlet is equal to this portlet; a
	 value less than -1 if this portlet is less than the portlet
	 argument; and 1 if this portlet is greater than the portlet
	 argument
	 */
	@Override
	public int compareTo(Portlet portlet) {
		return model.compareTo(portlet);
	}

	/**
	 * Checks whether this portlet is equal to the specified object.
	 *
	 * @param object the object to compare this portlet against
	 * @return <code>true</code> if the portlet is equal to the specified object
	 */
	@Override
	public boolean equals(Object object) {
		return model.equals(object);
	}

	/**
	 * Returns the action timeout of the portlet.
	 *
	 * @return the action timeout of the portlet
	 */
	@Override
	public int getActionTimeout() {
		return model.getActionTimeout();
	}

	/**
	 * Returns <code>true</code> if an action URL for this portlet should cause
	 * an auto redirect.
	 *
	 * @return <code>true</code> if an action URL for this portlet should cause
	 an auto redirect
	 */
	@Override
	public boolean getActionURLRedirect() {
		return model.getActionURLRedirect();
	}

	/**
	 * Returns the active of this portlet.
	 *
	 * @return the active of this portlet
	 */
	@Override
	public boolean getActive() {
		return model.getActive();
	}

	/**
	 * Returns <code>true</code> if default resources for the portlet are added
	 * to a page.
	 *
	 * @return <code>true</code> if default resources for the portlet are added
	 to a page
	 */
	@Override
	public boolean getAddDefaultResource() {
		return model.getAddDefaultResource();
	}

	/**
	 * Returns <code>true</code> if the portlet can be displayed via Ajax.
	 *
	 * @return <code>true</code> if the portlet can be displayed via Ajax
	 */
	@Override
	public boolean getAjaxable() {
		return model.getAjaxable();
	}

	/**
	 * Returns the portlet modes of the portlet.
	 *
	 * @return the portlet modes of the portlet
	 */
	@Override
	public java.util.Set<String> getAllPortletModes() {
		return model.getAllPortletModes();
	}

	/**
	 * Returns the window states of the portlet.
	 *
	 * @return the window states of the portlet
	 */
	@Override
	public java.util.Set<String> getAllWindowStates() {
		return model.getAllWindowStates();
	}

	/**
	 * Returns the application types of the portlet.
	 *
	 * @return the application types of the portlet
	 */
	@Override
	public java.util.Set
		<com.liferay.portal.kernel.application.type.ApplicationType>
			getApplicationTypes() {

		return model.getApplicationTypes();
	}

	/**
	 * Returns the names of the classes that represent asset types associated
	 * with the portlet.
	 *
	 * @return the names of the classes that represent asset types associated
	 with the portlet
	 */
	@Override
	public java.util.List<String> getAssetRendererFactoryClasses() {
		return model.getAssetRendererFactoryClasses();
	}

	/**
	 * Returns the names of the parameters that will be automatically propagated
	 * through the portlet.
	 *
	 * @return the names of the parameters that will be automatically propagated
	 through the portlet
	 */
	@Override
	public java.util.Set<String> getAutopropagatedParameters() {
		return model.getAutopropagatedParameters();
	}

	/**
	 * Returns the category names of the portlet.
	 *
	 * @return the category names of the portlet
	 */
	@Override
	public java.util.Set<String> getCategoryNames() {
		return model.getCategoryNames();
	}

	/**
	 * Returns <code>true</code> if the portlet is found in a WAR file.
	 *
	 * @param portletId the cloned instance portlet ID
	 * @return a cloned instance of the portlet
	 */
	@Override
	public Portlet getClonedInstance(String portletId) {
		return model.getClonedInstance(portletId);
	}

	/**
	 * Returns the company ID of this portlet.
	 *
	 * @return the company ID of this portlet
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the configuration action class of the portlet.
	 *
	 * @return the configuration action class of the portlet
	 */
	@Override
	public String getConfigurationActionClass() {
		return model.getConfigurationActionClass();
	}

	/**
	 * Returns the configuration action instance of the portlet.
	 *
	 * @return the configuration action instance of the portlet
	 */
	@Override
	public com.liferay.portal.kernel.portlet.ConfigurationAction
		getConfigurationActionInstance() {

		return model.getConfigurationActionInstance();
	}

	/**
	 * Returns the servlet context name of the portlet.
	 *
	 * @return the servlet context name of the portlet
	 */
	@Override
	public String getContextName() {
		return model.getContextName();
	}

	/**
	 * Returns the servlet context path of the portlet.
	 *
	 * @return the servlet context path of the portlet
	 */
	@Override
	public String getContextPath() {
		return model.getContextPath();
	}

	/**
	 * Returns the name of the category of the Control Panel where the portlet
	 * will be shown.
	 *
	 * @return the name of the category of the Control Panel where the portlet
	 will be shown
	 */
	@Override
	public String getControlPanelEntryCategory() {
		return model.getControlPanelEntryCategory();
	}

	/**
	 * Returns the name of the class that will control when the portlet will be
	 * shown in the Control Panel.
	 *
	 * @return the name of the class that will control when the portlet will be
	 shown in the Control Panel
	 */
	@Override
	public String getControlPanelEntryClass() {
		return model.getControlPanelEntryClass();
	}

	/**
	 * Returns an instance of the class that will control when the portlet will
	 * be shown in the Control Panel.
	 *
	 * @return the instance of the class that will control when the portlet will
	 be shown in the Control Panel
	 */
	@Override
	public com.liferay.portal.kernel.portlet.ControlPanelEntry
		getControlPanelEntryInstance() {

		return model.getControlPanelEntryInstance();
	}

	/**
	 * Returns the relative weight of the portlet with respect to the other
	 * portlets in the same category of the Control Panel.
	 *
	 * @return the relative weight of the portlet with respect to the other
	 portlets in the same category of the Control Panel
	 */
	@Override
	public double getControlPanelEntryWeight() {
		return model.getControlPanelEntryWeight();
	}

	/**
	 * Returns the name of the CSS class that will be injected in the DIV that
	 * wraps this portlet.
	 *
	 * @return the name of the CSS class that will be injected in the DIV that
	 wraps this portlet
	 */
	@Override
	public String getCssClassWrapper() {
		return model.getCssClassWrapper();
	}

	/**
	 * Returns the names of the classes that represent custom attribute displays
	 * associated with the portlet.
	 *
	 * @return the names of the classes that represent asset types associated
	 with the portlet
	 */
	@Override
	public java.util.List<String> getCustomAttributesDisplayClasses() {
		return model.getCustomAttributesDisplayClasses();
	}

	/**
	 * Returns the custom attribute display instances of the portlet.
	 *
	 * @return the custom attribute display instances of the portlet
	 */
	@Override
	public java.util.List
		<com.liferay.expando.kernel.model.CustomAttributesDisplay>
			getCustomAttributesDisplayInstances() {

		return model.getCustomAttributesDisplayInstances();
	}

	/**
	 * Get the default plugin settings of the portlet.
	 *
	 * @return the plugin settings
	 */
	@Override
	public PluginSetting getDefaultPluginSetting() {
		return model.getDefaultPluginSetting();
	}

	/**
	 * Returns the default preferences of the portlet.
	 *
	 * @return the default preferences of the portlet
	 */
	@Override
	public String getDefaultPreferences() {
		return model.getDefaultPreferences();
	}

	/**
	 * Returns the display name of the portlet.
	 *
	 * @return the display name of the portlet
	 */
	@Override
	public String getDisplayName() {
		return model.getDisplayName();
	}

	/**
	 * Returns expiration cache of the portlet.
	 *
	 * @return expiration cache of the portlet
	 */
	@Override
	public Integer getExpCache() {
		return model.getExpCache();
	}

	/**
	 * Returns a list of CSS files that will be referenced from the page's
	 * footer relative to the portal's context path.
	 *
	 * @return a list of CSS files that will be referenced from the page's
	 footer relative to the portal's context path
	 */
	@Override
	public java.util.List<String> getFooterPortalCss() {
		return model.getFooterPortalCss();
	}

	/**
	 * Returns a list of JavaScript files that will be referenced from the
	 * page's footer relative to the portal's context path.
	 *
	 * @return a list of JavaScript files that will be referenced from the
	 page's footer relative to the portal's context path
	 */
	@Override
	public java.util.List<String> getFooterPortalJavaScript() {
		return model.getFooterPortalJavaScript();
	}

	/**
	 * Returns a list of CSS files that will be referenced from the page's
	 * footer relative to the portlet's context path.
	 *
	 * @return a list of CSS files that will be referenced from the page's
	 footer relative to the portlet's context path
	 */
	@Override
	public java.util.List<String> getFooterPortletCss() {
		return model.getFooterPortletCss();
	}

	/**
	 * Returns a list of JavaScript files that will be referenced from the
	 * page's footer relative to the portlet's context path.
	 *
	 * @return a list of JavaScript files that will be referenced from the
	 page's footer relative to the portlet's context path
	 */
	@Override
	public java.util.List<String> getFooterPortletJavaScript() {
		return model.getFooterPortletJavaScript();
	}

	/**
	 * Returns the name of the friendly URL mapper class of the portlet.
	 *
	 * @return the name of the friendly URL mapper class of the portlet
	 */
	@Override
	public String getFriendlyURLMapperClass() {
		return model.getFriendlyURLMapperClass();
	}

	/**
	 * Returns the friendly URL mapper instance of the portlet.
	 *
	 * @return the friendly URL mapper instance of the portlet
	 */
	@Override
	public com.liferay.portal.kernel.portlet.FriendlyURLMapper
		getFriendlyURLMapperInstance() {

		return model.getFriendlyURLMapperInstance();
	}

	/**
	 * Returns the name of the friendly URL mapping of the portlet.
	 *
	 * @return the name of the friendly URL mapping of the portlet
	 */
	@Override
	public String getFriendlyURLMapping() {
		return model.getFriendlyURLMapping();
	}

	@Override
	public String getFriendlyURLMapping(boolean lookUpFriendlyURLMapper) {
		return model.getFriendlyURLMapping(lookUpFriendlyURLMapper);
	}

	/**
	 * Returns the class loader resource path to the friendly URL routes of the
	 * portlet.
	 *
	 * @return the class loader resource path to the friendly URL routes of the
	 portlet
	 */
	@Override
	public String getFriendlyURLRoutes() {
		return model.getFriendlyURLRoutes();
	}

	/**
	 * Returns a list of CSS files that will be referenced from the page's
	 * header relative to the portal's context path.
	 *
	 * @return a list of CSS files that will be referenced from the page's
	 header relative to the portal's context path
	 */
	@Override
	public java.util.List<String> getHeaderPortalCss() {
		return model.getHeaderPortalCss();
	}

	/**
	 * Returns a list of JavaScript files that will be referenced from the
	 * page's header relative to the portal's context path.
	 *
	 * @return a list of JavaScript files that will be referenced from the
	 page's header relative to the portal's context path
	 */
	@Override
	public java.util.List<String> getHeaderPortalJavaScript() {
		return model.getHeaderPortalJavaScript();
	}

	/**
	 * Returns a list of CSS files that will be referenced from the page's
	 * header relative to the portlet's context path.
	 *
	 * @return a list of CSS files that will be referenced from the page's
	 header relative to the portlet's context path
	 */
	@Override
	public java.util.List<String> getHeaderPortletCss() {
		return model.getHeaderPortletCss();
	}

	/**
	 * Returns a list of JavaScript files that will be referenced from the
	 * page's header relative to the portlet's context path.
	 *
	 * @return a list of JavaScript files that will be referenced from the
	 page's header relative to the portlet's context path
	 */
	@Override
	public java.util.List<String> getHeaderPortletJavaScript() {
		return model.getHeaderPortletJavaScript();
	}

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
	@Override
	public java.util.List<String> getHeaderRequestAttributePrefixes() {
		return model.getHeaderRequestAttributePrefixes();
	}

	/**
	 * Returns the header timeout of the portlet.
	 *
	 * @return the header timeout of the portlet
	 */
	@Override
	public int getHeaderTimeout() {
		return model.getHeaderTimeout();
	}

	/**
	 * Returns the icon of the portlet.
	 *
	 * @return the icon of the portlet
	 */
	@Override
	public String getIcon() {
		return model.getIcon();
	}

	/**
	 * Returns the ID of this portlet.
	 *
	 * @return the ID of this portlet
	 */
	@Override
	public long getId() {
		return model.getId();
	}

	/**
	 * Returns <code>true</code> to include the portlet and make it available to
	 * be made active.
	 *
	 * @return <code>true</code> to include the portlet and make it available to
	 be made active
	 */
	@Override
	public boolean getInclude() {
		return model.getInclude();
	}

	/**
	 * Returns the names of the classes that represent indexers associated with
	 * the portlet.
	 *
	 * @return the names of the classes that represent indexers associated with
	 the portlet
	 */
	@Override
	public java.util.List<String> getIndexerClasses() {
		return model.getIndexerClasses();
	}

	/**
	 * Returns the indexer instances of the portlet.
	 *
	 * @return the indexer instances of the portlet
	 */
	@Override
	public java.util.List<com.liferay.portal.kernel.search.Indexer<?>>
		getIndexerInstances() {

		return model.getIndexerInstances();
	}

	/**
	 * Returns the init parameters of the portlet.
	 *
	 * @return init parameters of the portlet
	 */
	@Override
	public Map<String, String> getInitParams() {
		return model.getInitParams();
	}

	/**
	 * Returns <code>true</code> if the portlet can be added multiple times to a
	 * layout.
	 *
	 * @return <code>true</code> if the portlet can be added multiple times to a
	 layout
	 */
	@Override
	public boolean getInstanceable() {
		return model.getInstanceable();
	}

	/**
	 * Returns the instance ID of the portlet.
	 *
	 * @return the instance ID of the portlet
	 */
	@Override
	public String getInstanceId() {
		return model.getInstanceId();
	}

	/**
	 * Returns <code>true</code> to allow the portlet to be cached within the
	 * layout.
	 *
	 * @return <code>true</code> if the portlet can be cached within the layout
	 */
	@Override
	public boolean getLayoutCacheable() {
		return model.getLayoutCacheable();
	}

	/**
	 * Returns <code>true</code> if the portlet goes into the maximized state
	 * when the user goes into the edit mode.
	 *
	 * @return <code>true</code> if the portlet goes into the maximized state
	 when the user goes into the edit mode
	 */
	@Override
	public boolean getMaximizeEdit() {
		return model.getMaximizeEdit();
	}

	/**
	 * Returns <code>true</code> if the portlet goes into the maximized state
	 * when the user goes into the help mode.
	 *
	 * @return <code>true</code> if the portlet goes into the maximized state
	 when the user goes into the help mode
	 */
	@Override
	public boolean getMaximizeHelp() {
		return model.getMaximizeHelp();
	}

	/**
	 * Returns the maximum size of buffered bytes before storing occurs.
	 *
	 * @return the maximum size of buffered bytes before storing occurs
	 */
	@Override
	public int getMultipartFileSizeThreshold() {
		return model.getMultipartFileSizeThreshold();
	}

	/**
	 * Returns the directory for storing uploaded files.
	 *
	 * @return the directory for storing uploaded files
	 */
	@Override
	public String getMultipartLocation() {
		return model.getMultipartLocation();
	}

	/**
	 * Returns the maximum number of bytes permitted for an uploaded file.
	 *
	 * @return the maximum number of bytes permitted for an uploaded file
	 */
	@Override
	public long getMultipartMaxFileSize() {
		return model.getMultipartMaxFileSize();
	}

	/**
	 * Returns the maximum number of bytes permitted for a multipart request.
	 *
	 * @return the maximum number of bytes permitted for a multipart request
	 */
	@Override
	public long getMultipartMaxRequestSize() {
		return model.getMultipartMaxRequestSize();
	}

	/**
	 * Returns the mvcc version of this portlet.
	 *
	 * @return the mvcc version of this portlet
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of the open search class of the portlet.
	 *
	 * @return the name of the open search class of the portlet
	 */
	@Override
	public String getOpenSearchClass() {
		return model.getOpenSearchClass();
	}

	/**
	 * Returns the indexer instance of the portlet.
	 *
	 * @return the indexer instance of the portlet
	 */
	@Override
	public com.liferay.portal.kernel.search.OpenSearch getOpenSearchInstance() {
		return model.getOpenSearchInstance();
	}

	/**
	 * Returns the parent struts path of the portlet.
	 *
	 * @return the parent struts path of the portlet.
	 */
	@Override
	public String getParentStrutsPath() {
		return model.getParentStrutsPath();
	}

	/**
	 * Returns the name of the permission propagator class of the portlet.
	 *
	 * @return the name of the permission propagator class of the portlet
	 */
	@Override
	public String getPermissionPropagatorClass() {
		return model.getPermissionPropagatorClass();
	}

	/**
	 * Returns the permission propagator instance of the portlet.
	 *
	 * @return the permission propagator instance of the portlet
	 */
	@Override
	public com.liferay.portal.kernel.security.permission.propagator.
		PermissionPropagator getPermissionPropagatorInstance() {

		return model.getPermissionPropagatorInstance();
	}

	/**
	 * Returns the plugin ID of the portlet.
	 *
	 * @return the plugin ID of the portlet
	 */
	@Override
	public String getPluginId() {
		return model.getPluginId();
	}

	/**
	 * Returns this portlet's plugin package.
	 *
	 * @return this portlet's plugin package
	 */
	@Override
	public com.liferay.portal.kernel.plugin.PluginPackage getPluginPackage() {
		return model.getPluginPackage();
	}

	/**
	 * Returns the plugin type of the portlet.
	 *
	 * @return the plugin type of the portlet
	 */
	@Override
	public String getPluginType() {
		return model.getPluginType();
	}

	/**
	 * Returns the name of the POP message listener class of the portlet.
	 *
	 * @return the name of the POP message listener class of the portlet
	 */
	@Override
	public String getPopMessageListenerClass() {
		return model.getPopMessageListenerClass();
	}

	/**
	 * Returns the POP message listener instance of the portlet.
	 *
	 * @return the POP message listener instance of the portlet
	 */
	@Override
	public com.liferay.portal.kernel.pop.MessageListener
		getPopMessageListenerInstance() {

		return model.getPopMessageListenerInstance();
	}

	/**
	 * Returns <code>true</code> if the portlet goes into the pop up state when
	 * the user goes into the print mode.
	 *
	 * @return <code>true</code> if the portlet goes into the pop up state when
	 the user goes into the print mode
	 */
	@Override
	public boolean getPopUpPrint() {
		return model.getPopUpPrint();
	}

	/**
	 * Returns this portlet's application.
	 *
	 * @return this portlet's application
	 */
	@Override
	public PortletApp getPortletApp() {
		return model.getPortletApp();
	}

	/**
	 * Returns the name of the portlet class of the portlet.
	 *
	 * @return the name of the portlet class of the portlet
	 */
	@Override
	public String getPortletClass() {
		return model.getPortletClass();
	}

	@Override
	public String getPortletConfigurationListenerClass() {
		return model.getPortletConfigurationListenerClass();
	}

	@Override
	public com.liferay.portal.kernel.portlet.PortletConfigurationListener
		getPortletConfigurationListenerInstance() {

		return model.getPortletConfigurationListenerInstance();
	}

	/**
	 * Returns the name of the portlet data handler class of the portlet.
	 *
	 * @return the name of the portlet data handler class of the portlet
	 */
	@Override
	public String getPortletDataHandlerClass() {
		return model.getPortletDataHandlerClass();
	}

	/**
	 * Returns the portlet data handler instance of the portlet.
	 *
	 * @return the portlet data handler instance of the portlet
	 */
	@Override
	public com.liferay.exportimport.kernel.lar.PortletDataHandler
		getPortletDataHandlerInstance() {

		return model.getPortletDataHandlerInstance();
	}

	/**
	 * Returns the portlet's CSS/JavaScript resource dependencies.
	 *
	 * @return the portlet's CSS/JavaScript resource dependencies
	 */
	@Override
	public java.util.List
		<com.liferay.portal.kernel.model.portlet.PortletDependency>
			getPortletDependencies() {

		return model.getPortletDependencies();
	}

	/**
	 * Returns the filters of the portlet.
	 *
	 * @return filters of the portlet
	 */
	@Override
	public Map<String, PortletFilter> getPortletFilters() {
		return model.getPortletFilters();
	}

	/**
	 * Returns the portlet ID of this portlet.
	 *
	 * @return the portlet ID of this portlet
	 */
	@Override
	public String getPortletId() {
		return model.getPortletId();
	}

	/**
	 * Returns the portlet info of the portlet.
	 *
	 * @return portlet info of the portlet
	 */
	@Override
	public PortletInfo getPortletInfo() {
		return model.getPortletInfo();
	}

	/**
	 * Returns the name of the portlet layout listener class of the portlet.
	 *
	 * @return the name of the portlet layout listener class of the portlet
	 */
	@Override
	public String getPortletLayoutListenerClass() {
		return model.getPortletLayoutListenerClass();
	}

	/**
	 * Returns the portlet layout listener instance of the portlet.
	 *
	 * @return the portlet layout listener instance of the portlet
	 */
	@Override
	public com.liferay.portal.kernel.portlet.PortletLayoutListener
		getPortletLayoutListenerInstance() {

		return model.getPortletLayoutListenerInstance();
	}

	/**
	 * Returns the portlet modes of the portlet.
	 *
	 * @return portlet modes of the portlet
	 */
	@Override
	public Map<String, java.util.Set<String>> getPortletModes() {
		return model.getPortletModes();
	}

	/**
	 * Returns the name of the portlet.
	 *
	 * @return the display name of the portlet
	 */
	@Override
	public String getPortletName() {
		return model.getPortletName();
	}

	/**
	 * Returns the name of the portlet URL class of the portlet.
	 *
	 * @return the name of the portlet URL class of the portlet
	 */
	@Override
	public String getPortletURLClass() {
		return model.getPortletURLClass();
	}

	/**
	 * Returns <code>true</code> if preferences are shared across the entire
	 * company.
	 *
	 * @return <code>true</code> if preferences are shared across the entire
	 company
	 */
	@Override
	public boolean getPreferencesCompanyWide() {
		return model.getPreferencesCompanyWide();
	}

	/**
	 * Returns <code>true</code> if preferences are owned by the group when the
	 * portlet is shown in a group layout. Returns <code>false</code> if
	 * preferences are owned by the user at all times.
	 *
	 * @return <code>true</code> if preferences are owned by the group when the
	 portlet is shown in a group layout; <code>false</code> if
	 preferences are owned by the user at all times.
	 */
	@Override
	public boolean getPreferencesOwnedByGroup() {
		return model.getPreferencesOwnedByGroup();
	}

	/**
	 * Returns <code>true</code> if preferences are unique per layout.
	 *
	 * @return <code>true</code> if preferences are unique per layout
	 */
	@Override
	public boolean getPreferencesUniquePerLayout() {
		return model.getPreferencesUniquePerLayout();
	}

	/**
	 * Returns the name of the preferences validator class of the portlet.
	 *
	 * @return the name of the preferences validator class of the portlet
	 */
	@Override
	public String getPreferencesValidator() {
		return model.getPreferencesValidator();
	}

	/**
	 * Returns the primary key of this portlet.
	 *
	 * @return the primary key of this portlet
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns <code>true</code> if the portlet does not share request
	 * attributes with the portal or portlets from another WAR.
	 *
	 * @return <code>true</code> if the portlet does not share request
	 attributes with the portal or portlets from another WAR
	 */
	@Override
	public boolean getPrivateRequestAttributes() {
		return model.getPrivateRequestAttributes();
	}

	/**
	 * Returns <code>true</code> if the portlet does not share session
	 * attributes with the portal.
	 *
	 * @return <code>true</code> if the portlet does not share session
	 attributes with the portal
	 */
	@Override
	public boolean getPrivateSessionAttributes() {
		return model.getPrivateSessionAttributes();
	}

	/**
	 * Returns the processing event from a namespace URI and a local part.
	 *
	 * @param uri the namespace URI
	 * @param localPart the local part
	 * @return the processing event from a namespace URI and a local part
	 */
	@Override
	public com.liferay.portal.kernel.xml.QName getProcessingEvent(
		String uri, String localPart) {

		return model.getProcessingEvent(uri, localPart);
	}

	/**
	 * Returns the processing events of the portlet.
	 *
	 * @return the processing events of the portlet
	 */
	@Override
	public java.util.Set<com.liferay.portal.kernel.xml.QName>
		getProcessingEvents() {

		return model.getProcessingEvents();
	}

	/**
	 * Returns the public render parameter from an identifier.
	 *
	 * @param identifier the identifier
	 * @return the public render parameter from an identifier
	 */
	@Override
	public PublicRenderParameter getPublicRenderParameter(String identifier) {
		return model.getPublicRenderParameter(identifier);
	}

	/**
	 * Returns the spublic render parameter from a namespace URI and a local
	 * part.
	 *
	 * @param uri the namespace URI
	 * @param localPart the local part
	 * @return the spublic render parameter from a namespace URI and a local
	 part
	 */
	@Override
	public PublicRenderParameter getPublicRenderParameter(
		String uri, String localPart) {

		return model.getPublicRenderParameter(uri, localPart);
	}

	/**
	 * Returns the public render parameters of the portlet.
	 *
	 * @return the public render parameters of the portlet
	 */
	@Override
	public java.util.Set<PublicRenderParameter> getPublicRenderParameters() {
		return model.getPublicRenderParameters();
	}

	/**
	 * Returns the publishing events of the portlet.
	 *
	 * @return the publishing events of the portlet
	 */
	@Override
	public java.util.Set<com.liferay.portal.kernel.xml.QName>
		getPublishingEvents() {

		return model.getPublishingEvents();
	}

	/**
	 * Returns <code>true</code> if the portlet is ready to be used.
	 *
	 * @return <code>true</code> if the portlet is ready to be used
	 */
	@Override
	public boolean getReady() {
		return model.getReady();
	}

	/**
	 * Returns the render timeout of the portlet.
	 *
	 * @return the render timeout of the portlet
	 */
	@Override
	public int getRenderTimeout() {
		return model.getRenderTimeout();
	}

	/**
	 * Returns the render weight of the portlet.
	 *
	 * @return the render weight of the portlet
	 */
	@Override
	public int getRenderWeight() {
		return model.getRenderWeight();
	}

	/**
	 * Returns the resource bundle of the portlet.
	 *
	 * @return resource bundle of the portlet
	 */
	@Override
	public String getResourceBundle() {
		return model.getResourceBundle();
	}

	/**
	 * Returns <code>true</code> if the portlet restores to the current view
	 * from the maximized state.
	 *
	 * @return <code>true</code> if the portlet restores to the current view
	 from the maximized state
	 */
	@Override
	public boolean getRestoreCurrentView() {
		return model.getRestoreCurrentView();
	}

	/**
	 * Returns the role mappers of the portlet.
	 *
	 * @return role mappers of the portlet
	 */
	@Override
	public Map<String, String> getRoleMappers() {
		return model.getRoleMappers();
	}

	/**
	 * Returns the roles of this portlet.
	 *
	 * @return the roles of this portlet
	 */
	@Override
	public String getRoles() {
		return model.getRoles();
	}

	/**
	 * Returns an array of required roles of the portlet.
	 *
	 * @return an array of required roles of the portlet
	 */
	@Override
	public String[] getRolesArray() {
		return model.getRolesArray();
	}

	/**
	 * Returns the root portlet of this portlet instance.
	 *
	 * @return the root portlet of this portlet instance
	 */
	@Override
	public Portlet getRootPortlet() {
		return model.getRootPortlet();
	}

	/**
	 * Returns the root portlet ID of the portlet.
	 *
	 * @return the root portlet ID of the portlet
	 */
	@Override
	public String getRootPortletId() {
		return model.getRootPortletId();
	}

	/**
	 * Returns the scheduler entries of the portlet.
	 *
	 * @return the scheduler entries of the portlet
	 */
	@Override
	public java.util.List<com.liferay.portal.kernel.scheduler.SchedulerEntry>
		getSchedulerEntries() {

		return model.getSchedulerEntries();
	}

	/**
	 * Returns <code>true</code> if the portlet supports scoping of data.
	 *
	 * @return <code>true</code> if the portlet supports scoping of data
	 */
	@Override
	public boolean getScopeable() {
		return model.getScopeable();
	}

	/**
	 * Returns <code>true</code> if users are shown that they do not have access
	 * to the portlet.
	 *
	 * @return <code>true</code> if users are shown that they do not have access
	 to the portlet
	 */
	@Override
	public boolean getShowPortletAccessDenied() {
		return model.getShowPortletAccessDenied();
	}

	/**
	 * Returns <code>true</code> if users are shown that the portlet is
	 * inactive.
	 *
	 * @return <code>true</code> if users are shown that the portlet is inactive
	 */
	@Override
	public boolean getShowPortletInactive() {
		return model.getShowPortletInactive();
	}

	/**
	 * Returns <code>true</code> if the portlet uses Single Page Application.
	 *
	 * @return <code>true</code> if the portlet uses Single Page Application
	 */
	@Override
	public boolean getSinglePageApplication() {
		return model.getSinglePageApplication();
	}

	/**
	 * Returns the names of the classes that represent social activity
	 * interpreters associated with the portlet.
	 *
	 * @return the names of the classes that represent social activity
	 interpreters associated with the portlet
	 */
	@Override
	public java.util.List<String> getSocialActivityInterpreterClasses() {
		return model.getSocialActivityInterpreterClasses();
	}

	/**
	 * Returns the social activity interpreter instances of the portlet.
	 *
	 * @return the social activity interpreter instances of the portlet
	 */
	@Override
	public java.util.List
		<com.liferay.social.kernel.model.SocialActivityInterpreter>
			getSocialActivityInterpreterInstances() {

		return model.getSocialActivityInterpreterInstances();
	}

	/**
	 * Returns the name of the social request interpreter class of the portlet.
	 *
	 * @return the name of the social request interpreter class of the portlet
	 */
	@Override
	public String getSocialRequestInterpreterClass() {
		return model.getSocialRequestInterpreterClass();
	}

	/**
	 * Returns the name of the social request interpreter instance of the
	 * portlet.
	 *
	 * @return the name of the social request interpreter instance of the
	 portlet
	 */
	@Override
	public com.liferay.social.kernel.model.SocialRequestInterpreter
		getSocialRequestInterpreterInstance() {

		return model.getSocialRequestInterpreterInstance();
	}

	/**
	 * Returns the names of the classes that represent staged model data
	 * handlers associated with the portlet.
	 *
	 * @return the names of the classes that represent staged model data
	 handlers associated with the portlet
	 */
	@Override
	public java.util.List<String> getStagedModelDataHandlerClasses() {
		return model.getStagedModelDataHandlerClasses();
	}

	/**
	 * Returns the staged model data handler instances of the portlet.
	 *
	 * @return the staged model data handler instances of the portlet
	 */
	@Override
	public java.util.List
		<com.liferay.exportimport.kernel.lar.StagedModelDataHandler<?>>
			getStagedModelDataHandlerInstances() {

		return model.getStagedModelDataHandlerInstances();
	}

	/**
	 * Returns <code>true</code> if the portlet is a static portlet that is
	 * cannot be moved.
	 *
	 * @return <code>true</code> if the portlet is a static portlet that is
	 cannot be moved
	 */
	@Override
	public boolean getStatic() {
		return model.getStatic();
	}

	/**
	 * Returns <code>true</code> if the portlet is a static portlet at the end
	 * of a list of portlets.
	 *
	 * @return <code>true</code> if the portlet is a static portlet at the end
	 of a list of portlets
	 */
	@Override
	public boolean getStaticEnd() {
		return model.getStaticEnd();
	}

	/**
	 * Returns the path for static resources served by this portlet.
	 *
	 * @return the path for static resources served by this portlet
	 */
	@Override
	public String getStaticResourcePath() {
		return model.getStaticResourcePath();
	}

	/**
	 * Returns <code>true</code> if the portlet is a static portlet at the start
	 * of a list of portlets.
	 *
	 * @return <code>true</code> if the portlet is a static portlet at the start
	 of a list of portlets
	 */
	@Override
	public boolean getStaticStart() {
		return model.getStaticStart();
	}

	/**
	 * Returns the struts path of the portlet.
	 *
	 * @return the struts path of the portlet
	 */
	@Override
	public String getStrutsPath() {
		return model.getStrutsPath();
	}

	/**
	 * Returns the supported locales of the portlet.
	 *
	 * @return the supported locales of the portlet
	 */
	@Override
	public java.util.Set<String> getSupportedLocales() {
		return model.getSupportedLocales();
	}

	/**
	 * Returns <code>true</code> if the portlet is a system portlet that a user
	 * cannot manually add to their page.
	 *
	 * @return <code>true</code> if the portlet is a system portlet that a user
	 cannot manually add to their page
	 */
	@Override
	public boolean getSystem() {
		return model.getSystem();
	}

	/**
	 * Returns the name of the template handler class of the portlet.
	 *
	 * @return the name of the template handler class of the portlet
	 */
	@Override
	public String getTemplateHandlerClass() {
		return model.getTemplateHandlerClass();
	}

	/**
	 * Returns the template handler instance of the portlet.
	 *
	 * @return the template handler instance of the portlet
	 */
	@Override
	public com.liferay.portal.kernel.template.TemplateHandler
		getTemplateHandlerInstance() {

		return model.getTemplateHandlerInstance();
	}

	/**
	 * Returns the timestamp of the portlet.
	 *
	 * @return the timestamp of the portlet
	 */
	@Override
	public long getTimestamp() {
		return model.getTimestamp();
	}

	/**
	 * Returns the names of the classes that represent trash handlers associated
	 * with the portlet.
	 *
	 * @return the names of the classes that represent trash handlers associated
	 with the portlet
	 */
	@Override
	public java.util.List<String> getTrashHandlerClasses() {
		return model.getTrashHandlerClasses();
	}

	/**
	 * Returns the trash handler instances of the portlet.
	 *
	 * @return the trash handler instances of the portlet
	 */
	@Override
	public java.util.List<com.liferay.portal.kernel.trash.TrashHandler>
		getTrashHandlerInstances() {

		return model.getTrashHandlerInstances();
	}

	/**
	 * Returns <code>true</code> if the portlet is an undeployed portlet.
	 *
	 * @return <code>true</code> if the portlet is a placeholder of an
	 undeployed portlet
	 */
	@Override
	public boolean getUndeployedPortlet() {
		return model.getUndeployedPortlet();
	}

	/**
	 * Returns the unlinked roles of the portlet.
	 *
	 * @return unlinked roles of the portlet
	 */
	@Override
	public java.util.Set<String> getUnlinkedRoles() {
		return model.getUnlinkedRoles();
	}

	/**
	 * Returns the name of the URL encoder class of the portlet.
	 *
	 * @return the name of the URL encoder class of the portlet
	 */
	@Override
	public String getURLEncoderClass() {
		return model.getURLEncoderClass();
	}

	/**
	 * Returns the URL encoder instance of the portlet.
	 *
	 * @return the URL encoder instance of the portlet
	 */
	@Override
	public com.liferay.portal.kernel.servlet.URLEncoder
		getURLEncoderInstance() {

		return model.getURLEncoderInstance();
	}

	/**
	 * Returns <code>true</code> if the portlet uses the default template.
	 *
	 * @return <code>true</code> if the portlet uses the default template
	 */
	@Override
	public boolean getUseDefaultTemplate() {
		return model.getUseDefaultTemplate();
	}

	/**
	 * Returns the user ID of the portlet. This only applies when the portlet is
	 * added by a user in a customizable layout.
	 *
	 * @return the user ID of the portlet
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the class loader resource path to the use notification
	 * definitions of the portlet.
	 *
	 * @return the class loader resource path to the use notification
	 definitions of the portlet
	 */
	@Override
	public String getUserNotificationDefinitions() {
		return model.getUserNotificationDefinitions();
	}

	/**
	 * Returns the names of the classes that represent user notification
	 * handlers associated with the portlet.
	 *
	 * @return the names of the classes that represent user notification
	 handlers associated with the portlet
	 */
	@Override
	public java.util.List<String> getUserNotificationHandlerClasses() {
		return model.getUserNotificationHandlerClasses();
	}

	/**
	 * Returns the user notification handler instances of the portlet.
	 *
	 * @return the user notification handler instances of the portlet
	 */
	@Override
	public java.util.List
		<com.liferay.portal.kernel.notifications.UserNotificationHandler>
			getUserNotificationHandlerInstances() {

		return model.getUserNotificationHandlerInstances();
	}

	/**
	 * Returns the user principal strategy of the portlet.
	 *
	 * @return the user principal strategy of the portlet
	 */
	@Override
	public String getUserPrincipalStrategy() {
		return model.getUserPrincipalStrategy();
	}

	/**
	 * Returns the virtual path of the portlet.
	 *
	 * @return the virtual path of the portlet
	 */
	@Override
	public String getVirtualPath() {
		return model.getVirtualPath();
	}

	/**
	 * Returns the name of the WebDAV storage class of the portlet.
	 *
	 * @return the name of the WebDAV storage class of the portlet
	 */
	@Override
	public String getWebDAVStorageClass() {
		return model.getWebDAVStorageClass();
	}

	/**
	 * Returns the name of the WebDAV storage instance of the portlet.
	 *
	 * @return the name of the WebDAV storage instance of the portlet
	 */
	@Override
	public com.liferay.portal.kernel.webdav.WebDAVStorage
		getWebDAVStorageInstance() {

		return model.getWebDAVStorageInstance();
	}

	/**
	 * Returns the name of the WebDAV storage token of the portlet.
	 *
	 * @return the name of the WebDAV storage token of the portlet
	 */
	@Override
	public String getWebDAVStorageToken() {
		return model.getWebDAVStorageToken();
	}

	/**
	 * Returns the window states of the portlet.
	 *
	 * @return window states of the portlet
	 */
	@Override
	public Map<String, java.util.Set<String>> getWindowStates() {
		return model.getWindowStates();
	}

	/**
	 * Returns the names of the classes that represent workflow handlers
	 * associated with the portlet.
	 *
	 * @return the names of the classes that represent workflow handlers
	 associated with the portlet
	 */
	@Override
	public java.util.List<String> getWorkflowHandlerClasses() {
		return model.getWorkflowHandlerClasses();
	}

	/**
	 * Returns the workflow handler instances of the portlet.
	 *
	 * @return the workflow handler instances of the portlet
	 */
	@Override
	public java.util.List<com.liferay.portal.kernel.workflow.WorkflowHandler<?>>
		getWorkflowHandlerInstances() {

		return model.getWorkflowHandlerInstances();
	}

	/**
	 * Returns the name of the XML-RPC method class of the portlet.
	 *
	 * @return the name of the XML-RPC method class of the portlet
	 */
	@Override
	public String getXmlRpcMethodClass() {
		return model.getXmlRpcMethodClass();
	}

	/**
	 * Returns the name of the XML-RPC method instance of the portlet.
	 *
	 * @return the name of the XML-RPC method instance of the portlet
	 */
	@Override
	public com.liferay.portal.kernel.xmlrpc.Method getXmlRpcMethodInstance() {
		return model.getXmlRpcMethodInstance();
	}

	/**
	 * Returns <code>true</code> if the user has the permission to add the
	 * portlet to a layout.
	 *
	 * @param userId the primary key of the user
	 * @return <code>true</code> if the user has the permission to add the
	 portlet to a layout
	 */
	@Override
	public boolean hasAddPortletPermission(long userId) {
		return model.hasAddPortletPermission(userId);
	}

	@Override
	public boolean hasFooterPortalCss() {
		return model.hasFooterPortalCss();
	}

	@Override
	public boolean hasFooterPortalJavaScript() {
		return model.hasFooterPortalJavaScript();
	}

	@Override
	public boolean hasFooterPortletCss() {
		return model.hasFooterPortletCss();
	}

	@Override
	public boolean hasFooterPortletJavaScript() {
		return model.hasFooterPortletJavaScript();
	}

	@Override
	public int hashCode() {
		return model.hashCode();
	}

	@Override
	public boolean hasHeaderPortalCss() {
		return model.hasHeaderPortalCss();
	}

	@Override
	public boolean hasHeaderPortalJavaScript() {
		return model.hasHeaderPortalJavaScript();
	}

	@Override
	public boolean hasHeaderPortletCss() {
		return model.hasHeaderPortletCss();
	}

	@Override
	public boolean hasHeaderPortletJavaScript() {
		return model.hasHeaderPortletJavaScript();
	}

	/**
	 * Returns <code>true</code> if the portlet supports more than one mime
	 * type.
	 *
	 * @return <code>true</code> if the portlet supports more than one mime type
	 */
	@Override
	public boolean hasMultipleMimeTypes() {
		return model.hasMultipleMimeTypes();
	}

	/**
	 * Returns <code>true</code> if the portlet supports the specified mime type
	 * and portlet mode.
	 *
	 * @param mimeType the mime type
	 * @param portletMode the portlet mode
	 * @return <code>true</code> if the portlet supports the specified mime type
	 and portlet mode
	 */
	@Override
	public boolean hasPortletMode(
		String mimeType, jakarta.portlet.PortletMode portletMode) {

		return model.hasPortletMode(mimeType, portletMode);
	}

	/**
	 * Returns <code>true</code> if the portlet has a role with the specified
	 * name.
	 *
	 * @param roleName the role name
	 * @return <code>true</code> if the portlet has a role with the specified
	 name
	 */
	@Override
	public boolean hasRoleWithName(String roleName) {
		return model.hasRoleWithName(roleName);
	}

	/**
	 * Returns <code>true</code> if the portlet supports the specified mime type
	 * and window state.
	 *
	 * @param mimeType the mime type
	 * @param windowState the window state
	 * @return <code>true</code> if the portlet supports the specified mime type
	 and window state
	 */
	@Override
	public boolean hasWindowState(
		String mimeType, jakarta.portlet.WindowState windowState) {

		return model.hasWindowState(mimeType, windowState);
	}

	/**
	 * Returns <code>true</code> if an action URL for this portlet should cause
	 * an auto redirect.
	 *
	 * @return <code>true</code> if an action URL for this portlet should cause
	 an auto redirect
	 */
	@Override
	public boolean isActionURLRedirect() {
		return model.isActionURLRedirect();
	}

	/**
	 * Returns <code>true</code> if this portlet is active.
	 *
	 * @return <code>true</code> if this portlet is active; <code>false</code> otherwise
	 */
	@Override
	public boolean isActive() {
		return model.isActive();
	}

	/**
	 * Returns <code>true</code> if default resources for the portlet are added
	 * to a page.
	 *
	 * @return <code>true</code> if default resources for the portlet are added
	 to a page
	 */
	@Override
	public boolean isAddDefaultResource() {
		return model.isAddDefaultResource();
	}

	/**
	 * Returns <code>true</code> if the portlet can be displayed via Ajax.
	 *
	 * @return <code>true</code> if the portlet can be displayed via Ajax
	 */
	@Override
	public boolean isAjaxable() {
		return model.isAjaxable();
	}

	/**
	 * Returns <code>true</code> if the portlet supports asynchronous processing
	 * in resource requests.
	 *
	 * @return <code>true</code> if the portlet supports asynchrounous
	 processing in resource requests
	 */
	@Override
	public boolean isAsyncSupported() {
		return model.isAsyncSupported();
	}

	@Override
	public boolean isFullPageDisplayable() {
		return model.isFullPageDisplayable();
	}

	/**
	 * Returns <code>true</code> to include the portlet and make it available to
	 * be made active.
	 *
	 * @return <code>true</code> to include the portlet and make it available to
	 be made active
	 */
	@Override
	public boolean isInclude() {
		return model.isInclude();
	}

	/**
	 * Returns <code>true</code> if the portlet can be added multiple times to a
	 * layout.
	 *
	 * @return <code>true</code> if the portlet can be added multiple times to a
	 layout
	 */
	@Override
	public boolean isInstanceable() {
		return model.isInstanceable();
	}

	/**
	 * Returns <code>true</code> to allow the portlet to be cached within the
	 * layout.
	 *
	 * @return <code>true</code> if the portlet can be cached within the layout
	 */
	@Override
	public boolean isLayoutCacheable() {
		return model.isLayoutCacheable();
	}

	/**
	 * Returns <code>true</code> if the portlet goes into the maximized state
	 * when the user goes into the edit mode.
	 *
	 * @return <code>true</code> if the portlet goes into the maximized state
	 when the user goes into the edit mode
	 */
	@Override
	public boolean isMaximizeEdit() {
		return model.isMaximizeEdit();
	}

	/**
	 * Returns <code>true</code> if the portlet goes into the maximized state
	 * when the user goes into the help mode.
	 *
	 * @return <code>true</code> if the portlet goes into the maximized state
	 when the user goes into the help mode
	 */
	@Override
	public boolean isMaximizeHelp() {
		return model.isMaximizeHelp();
	}

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
	@Override
	public boolean isPartialActionServeResource() {
		return model.isPartialActionServeResource();
	}

	/**
	 * Returns <code>true</code> if the portlet goes into the pop up state when
	 * the user goes into the print mode.
	 *
	 * @return <code>true</code> if the portlet goes into the pop up state when
	 the user goes into the print mode
	 */
	@Override
	public boolean isPopUpPrint() {
		return model.isPopUpPrint();
	}

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
	@Override
	public boolean isPortletDependencyCssEnabled() {
		return model.isPortletDependencyCssEnabled();
	}

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
	@Override
	public boolean isPortletDependencyJavaScriptEnabled() {
		return model.isPortletDependencyJavaScriptEnabled();
	}

	/**
	 * Returns <code>true</code> if preferences are shared across the entire
	 * company.
	 *
	 * @return <code>true</code> if preferences are shared across the entire
	 company
	 */
	@Override
	public boolean isPreferencesCompanyWide() {
		return model.isPreferencesCompanyWide();
	}

	/**
	 * Returns <code>true</code> if preferences are owned by the group when the
	 * portlet is shown in a group layout. Returns <code>false</code> if
	 * preferences are owned by the user at all times.
	 *
	 * @return <code>true</code> if preferences are owned by the group when the
	 portlet is shown in a group layout; <code>false</code> if
	 preferences are owned by the user at all times.
	 */
	@Override
	public boolean isPreferencesOwnedByGroup() {
		return model.isPreferencesOwnedByGroup();
	}

	/**
	 * Returns <code>true</code> if preferences are unique per layout.
	 *
	 * @return <code>true</code> if preferences are unique per layout
	 */
	@Override
	public boolean isPreferencesUniquePerLayout() {
		return model.isPreferencesUniquePerLayout();
	}

	/**
	 * Returns <code>true</code> if the portlet does not share request
	 * attributes with the portal or portlets from another WAR.
	 *
	 * @return <code>true</code> if the portlet does not share request
	 attributes with the portal or portlets from another WAR
	 */
	@Override
	public boolean isPrivateRequestAttributes() {
		return model.isPrivateRequestAttributes();
	}

	/**
	 * Returns <code>true</code> if the portlet does not share session
	 * attributes with the portal.
	 *
	 * @return <code>true</code> if the portlet does not share session
	 attributes with the portal
	 */
	@Override
	public boolean isPrivateSessionAttributes() {
		return model.isPrivateSessionAttributes();
	}

	/**
	 * Returns <code>true</code> if the portlet is ready to be used.
	 *
	 * @return <code>true</code> if the portlet is ready to be used
	 */
	@Override
	public boolean isReady() {
		return model.isReady();
	}

	/**
	 * Returns <code>true</code> if the portlet will only process namespaced
	 * parameters.
	 *
	 * @return <code>true</code> if the portlet will only process namespaced
	 parameters
	 */
	@Override
	public boolean isRequiresNamespacedParameters() {
		return model.isRequiresNamespacedParameters();
	}

	/**
	 * Returns <code>true</code> if the portlet restores to the current view
	 * from the maximized state.
	 *
	 * @return <code>true</code> if the portlet restores to the current view
	 from the maximized state
	 */
	@Override
	public boolean isRestoreCurrentView() {
		return model.isRestoreCurrentView();
	}

	/**
	 * Returns <code>true</code> if the portlet supports scoping of data.
	 *
	 * @return <code>true</code> if the portlet supports scoping of data
	 */
	@Override
	public boolean isScopeable() {
		return model.isScopeable();
	}

	/**
	 * Returns <code>true</code> if users are shown that they do not have access
	 * to the portlet.
	 *
	 * @return <code>true</code> if users are shown that they do not have access
	 to the portlet
	 */
	@Override
	public boolean isShowPortletAccessDenied() {
		return model.isShowPortletAccessDenied();
	}

	/**
	 * Returns <code>true</code> if users are shown that the portlet is
	 * inactive.
	 *
	 * @return <code>true</code> if users are shown that the portlet is inactive
	 */
	@Override
	public boolean isShowPortletInactive() {
		return model.isShowPortletInactive();
	}

	/**
	 * Returns <code>true</code> if the portlet uses Single Page Application.
	 *
	 * @return <code>true</code> if the portlet uses Single Page Application
	 */
	@Override
	public boolean isSinglePageApplication() {
		return model.isSinglePageApplication();
	}

	/**
	 * Returns <code>true</code> if the portlet is a static portlet that is
	 * cannot be moved.
	 *
	 * @return <code>true</code> if the portlet is a static portlet that is
	 cannot be moved
	 */
	@Override
	public boolean isStatic() {
		return model.isStatic();
	}

	/**
	 * Returns <code>true</code> if the portlet is a static portlet at the end
	 * of a list of portlets.
	 *
	 * @return <code>true</code> if the portlet is a static portlet at the end
	 of a list of portlets
	 */
	@Override
	public boolean isStaticEnd() {
		return model.isStaticEnd();
	}

	/**
	 * Returns <code>true</code> if the portlet is a static portlet at the start
	 * of a list of portlets.
	 *
	 * @return <code>true</code> if the portlet is a static portlet at the start
	 of a list of portlets
	 */
	@Override
	public boolean isStaticStart() {
		return model.isStaticStart();
	}

	/**
	 * Returns <code>true</code> if the portlet is a system portlet that a user
	 * cannot manually add to their page.
	 *
	 * @return <code>true</code> if the portlet is a system portlet that a user
	 cannot manually add to their page
	 */
	@Override
	public boolean isSystem() {
		return model.isSystem();
	}

	/**
	 * Returns <code>true</code> if the portlet is an undeployed portlet.
	 *
	 * @return <code>true</code> if the portlet is a placeholder of an
	 undeployed portlet
	 */
	@Override
	public boolean isUndeployedPortlet() {
		return model.isUndeployedPortlet();
	}

	/**
	 * Returns <code>true</code> if the portlet uses the default template.
	 *
	 * @return <code>true</code> if the portlet uses the default template
	 */
	@Override
	public boolean isUseDefaultTemplate() {
		return model.isUseDefaultTemplate();
	}

	/**
	 * Link the role names set in portlet.xml with the Liferay roles set in
	 * liferay-portlet.xml.
	 */
	@Override
	public void linkRoles() {
		model.linkRoles();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the action timeout of the portlet.
	 *
	 * @param actionTimeout the action timeout of the portlet
	 */
	@Override
	public void setActionTimeout(int actionTimeout) {
		model.setActionTimeout(actionTimeout);
	}

	/**
	 * Set to <code>true</code> if an action URL for this portlet should cause
	 * an auto redirect.
	 *
	 * @param actionURLRedirect boolean value for whether an action URL for this
	 portlet should cause an auto redirect
	 */
	@Override
	public void setActionURLRedirect(boolean actionURLRedirect) {
		model.setActionURLRedirect(actionURLRedirect);
	}

	/**
	 * Sets whether this portlet is active.
	 *
	 * @param active the active of this portlet
	 */
	@Override
	public void setActive(boolean active) {
		model.setActive(active);
	}

	/**
	 * Set to <code>true</code> if default resources for the portlet are added
	 * to a page.
	 *
	 * @param addDefaultResource boolean value for whether or not default
	 resources for the portlet are added to a page
	 */
	@Override
	public void setAddDefaultResource(boolean addDefaultResource) {
		model.setAddDefaultResource(addDefaultResource);
	}

	/**
	 * Set to <code>true</code> if the portlet can be displayed via Ajax.
	 *
	 * @param ajaxable boolean value for whether the portlet can be displayed
	 via Ajax
	 */
	@Override
	public void setAjaxable(boolean ajaxable) {
		model.setAjaxable(ajaxable);
	}

	/**
	 * Sets the application types of the portlet.
	 *
	 * @param applicationTypes the application types of the portlet
	 */
	@Override
	public void setApplicationTypes(
		java.util.Set
			<com.liferay.portal.kernel.application.type.ApplicationType>
				applicationTypes) {

		model.setApplicationTypes(applicationTypes);
	}

	/**
	 * Sets the names of the classes that represent asset types associated with
	 * the portlet.
	 *
	 * @param assetRendererFactoryClasses the names of the classes that
	 represent asset types associated with the portlet
	 */
	@Override
	public void setAssetRendererFactoryClasses(
		java.util.List<String> assetRendererFactoryClasses) {

		model.setAssetRendererFactoryClasses(assetRendererFactoryClasses);
	}

	/**
	 * Set to <code>true</code> if the portlet supports asynchronous processing
	 * in resource requests.
	 *
	 * @param asyncSupported boolean value for whether the portlet supports
	 asynchronous processing in resource requests
	 */
	@Override
	public void setAsyncSupported(boolean asyncSupported) {
		model.setAsyncSupported(asyncSupported);
	}

	/**
	 * Sets the names of the parameters that will be automatically propagated
	 * through the portlet.
	 *
	 * @param autopropagatedParameters the names of the parameters that will be
	 automatically propagated through the portlet
	 */
	@Override
	public void setAutopropagatedParameters(
		java.util.Set<String> autopropagatedParameters) {

		model.setAutopropagatedParameters(autopropagatedParameters);
	}

	/**
	 * Sets the category names of the portlet.
	 *
	 * @param categoryNames the category names of the portlet
	 */
	@Override
	public void setCategoryNames(java.util.Set<String> categoryNames) {
		model.setCategoryNames(categoryNames);
	}

	/**
	 * Sets the company ID of this portlet.
	 *
	 * @param companyId the company ID of this portlet
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the configuration action class of the portlet.
	 *
	 * @param configurationActionClass the configuration action class of the
	 portlet
	 */
	@Override
	public void setConfigurationActionClass(String configurationActionClass) {
		model.setConfigurationActionClass(configurationActionClass);
	}

	/**
	 * Set the name of the category of the Control Panel where the portlet will
	 * be shown.
	 *
	 * @param controlPanelEntryCategory the name of the category of the Control
	 Panel where the portlet will be shown
	 */
	@Override
	public void setControlPanelEntryCategory(String controlPanelEntryCategory) {
		model.setControlPanelEntryCategory(controlPanelEntryCategory);
	}

	/**
	 * Sets the name of the class that will control when the portlet will be
	 * shown in the Control Panel.
	 *
	 * @param controlPanelEntryClass the name of the class that will control
	 when the portlet will be shown in the Control Panel
	 */
	@Override
	public void setControlPanelEntryClass(String controlPanelEntryClass) {
		model.setControlPanelEntryClass(controlPanelEntryClass);
	}

	/**
	 * Sets the relative weight of the portlet with respect to the other
	 * portlets in the same category of the Control Panel.
	 *
	 * @param controlPanelEntryWeight the relative weight of the portlet with
	 respect to the other portlets in the same category of the Control
	 Panel
	 */
	@Override
	public void setControlPanelEntryWeight(double controlPanelEntryWeight) {
		model.setControlPanelEntryWeight(controlPanelEntryWeight);
	}

	/**
	 * Sets the name of the CSS class that will be injected in the DIV that
	 * wraps this portlet.
	 *
	 * @param cssClassWrapper the name of the CSS class that will be injected in
	 the DIV that wraps this portlet
	 */
	@Override
	public void setCssClassWrapper(String cssClassWrapper) {
		model.setCssClassWrapper(cssClassWrapper);
	}

	/**
	 * Sets the names of the classes that represent custom attribute displays
	 * associated with the portlet.
	 *
	 * @param customAttributesDisplayClasses the names of the classes that
	 represent custom attribute displays associated with the portlet
	 */
	@Override
	public void setCustomAttributesDisplayClasses(
		java.util.List<String> customAttributesDisplayClasses) {

		model.setCustomAttributesDisplayClasses(customAttributesDisplayClasses);
	}

	/**
	 * Sets the default plugin settings of the portlet.
	 *
	 * @param pluginSetting the plugin setting
	 */
	@Override
	public void setDefaultPluginSetting(PluginSetting pluginSetting) {
		model.setDefaultPluginSetting(pluginSetting);
	}

	/**
	 * Sets the default preferences of the portlet.
	 *
	 * @param defaultPreferences the default preferences of the portlet
	 */
	@Override
	public void setDefaultPreferences(String defaultPreferences) {
		model.setDefaultPreferences(defaultPreferences);
	}

	/**
	 * Sets the display name of the portlet.
	 *
	 * @param displayName the display name of the portlet
	 */
	@Override
	public void setDisplayName(String displayName) {
		model.setDisplayName(displayName);
	}

	/**
	 * Sets expiration cache of the portlet.
	 *
	 * @param expCache expiration cache of the portlet
	 */
	@Override
	public void setExpCache(Integer expCache) {
		model.setExpCache(expCache);
	}

	/**
	 * Sets a list of CSS files that will be referenced from the page's footer
	 * relative to the portal's context path.
	 *
	 * @param footerPortalCss a list of CSS files that will be referenced from
	 the page's footer relative to the portal's context path
	 */
	@Override
	public void setFooterPortalCss(java.util.List<String> footerPortalCss) {
		model.setFooterPortalCss(footerPortalCss);
	}

	/**
	 * Sets a list of JavaScript files that will be referenced from the page's
	 * footer relative to the portal's context path.
	 *
	 * @param footerPortalJavaScript a list of JavaScript files that will be
	 referenced from the page's footer relative to the portal's context
	 path
	 */
	@Override
	public void setFooterPortalJavaScript(
		java.util.List<String> footerPortalJavaScript) {

		model.setFooterPortalJavaScript(footerPortalJavaScript);
	}

	/**
	 * Sets a list of CSS files that will be referenced from the page's footer
	 * relative to the portlet's context path.
	 *
	 * @param footerPortletCss a list of CSS files that will be referenced from
	 the page's footer relative to the portlet's context path
	 */
	@Override
	public void setFooterPortletCss(java.util.List<String> footerPortletCss) {
		model.setFooterPortletCss(footerPortletCss);
	}

	/**
	 * Sets a list of JavaScript files that will be referenced from the page's
	 * footer relative to the portlet's context path.
	 *
	 * @param footerPortletJavaScript a list of JavaScript files that will be
	 referenced from the page's footer relative to the portlet's
	 context path
	 */
	@Override
	public void setFooterPortletJavaScript(
		java.util.List<String> footerPortletJavaScript) {

		model.setFooterPortletJavaScript(footerPortletJavaScript);
	}

	/**
	 * Sets the name of the friendly URL mapper class of the portlet.
	 *
	 * @param friendlyURLMapperClass the name of the friendly URL mapper class
	 of the portlet
	 */
	@Override
	public void setFriendlyURLMapperClass(String friendlyURLMapperClass) {
		model.setFriendlyURLMapperClass(friendlyURLMapperClass);
	}

	/**
	 * Sets the name of the friendly URL mapping of the portlet.
	 *
	 * @param friendlyURLMapping the name of the friendly URL mapping of the
	 portlet
	 */
	@Override
	public void setFriendlyURLMapping(String friendlyURLMapping) {
		model.setFriendlyURLMapping(friendlyURLMapping);
	}

	/**
	 * Sets the class loader resource path to the friendly URL routes of the
	 * portlet.
	 *
	 * @param friendlyURLRoutes the class loader resource path to the friendly
	 URL routes of the portlet
	 */
	@Override
	public void setFriendlyURLRoutes(String friendlyURLRoutes) {
		model.setFriendlyURLRoutes(friendlyURLRoutes);
	}

	/**
	 * Sets a list of CSS files that will be referenced from the page's header
	 * relative to the portal's context path.
	 *
	 * @param headerPortalCss a list of CSS files that will be referenced from
	 the page's header relative to the portal's context path
	 */
	@Override
	public void setHeaderPortalCss(java.util.List<String> headerPortalCss) {
		model.setHeaderPortalCss(headerPortalCss);
	}

	/**
	 * Sets a list of JavaScript files that will be referenced from the page's
	 * header relative to the portal's context path.
	 *
	 * @param headerPortalJavaScript a list of JavaScript files that will be
	 referenced from the page's header relative to the portal's context
	 path
	 */
	@Override
	public void setHeaderPortalJavaScript(
		java.util.List<String> headerPortalJavaScript) {

		model.setHeaderPortalJavaScript(headerPortalJavaScript);
	}

	/**
	 * Sets a list of CSS files that will be referenced from the page's header
	 * relative to the portlet's context path.
	 *
	 * @param headerPortletCss a list of CSS files that will be referenced from
	 the page's header relative to the portlet's context path
	 */
	@Override
	public void setHeaderPortletCss(java.util.List<String> headerPortletCss) {
		model.setHeaderPortletCss(headerPortletCss);
	}

	/**
	 * Sets a list of JavaScript files that will be referenced from the page's
	 * header relative to the portlet's context path.
	 *
	 * @param headerPortletJavaScript a list of JavaScript files that will be
	 referenced from the page's header relative to the portlet's
	 context path
	 */
	@Override
	public void setHeaderPortletJavaScript(
		java.util.List<String> headerPortletJavaScript) {

		model.setHeaderPortletJavaScript(headerPortletJavaScript);
	}

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
	@Override
	public void setHeaderRequestAttributePrefixes(
		java.util.List<String> headerRequestAttributePrefixes) {

		model.setHeaderRequestAttributePrefixes(headerRequestAttributePrefixes);
	}

	/**
	 * Sets the header timeout of the portlet.
	 *
	 * @param headerTimeout the header timeout of the portlet
	 */
	@Override
	public void setHeaderTimeout(int headerTimeout) {
		model.setHeaderTimeout(headerTimeout);
	}

	/**
	 * Sets the icon of the portlet.
	 *
	 * @param icon the icon of the portlet
	 */
	@Override
	public void setIcon(String icon) {
		model.setIcon(icon);
	}

	/**
	 * Sets the ID of this portlet.
	 *
	 * @param id the ID of this portlet
	 */
	@Override
	public void setId(long id) {
		model.setId(id);
	}

	/**
	 * Set to <code>true</code> to include the portlet and make it available to
	 * be made active.
	 *
	 * @param include boolean value for whether to include the portlet and make
	 it available to be made active
	 */
	@Override
	public void setInclude(boolean include) {
		model.setInclude(include);
	}

	/**
	 * Sets the names of the classes that represent indexers associated with the
	 * portlet.
	 *
	 * @param indexerClasses the names of the classes that represent indexers
	 associated with the portlet
	 */
	@Override
	public void setIndexerClasses(java.util.List<String> indexerClasses) {
		model.setIndexerClasses(indexerClasses);
	}

	/**
	 * Sets the init parameters of the portlet.
	 *
	 * @param initParams the init parameters of the portlet
	 */
	@Override
	public void setInitParams(Map<String, String> initParams) {
		model.setInitParams(initParams);
	}

	/**
	 * Set to <code>true</code> if the portlet can be added multiple times to a
	 * layout.
	 *
	 * @param instanceable boolean value for whether the portlet can be added
	 multiple times to a layout
	 */
	@Override
	public void setInstanceable(boolean instanceable) {
		model.setInstanceable(instanceable);
	}

	/**
	 * Set to <code>true</code> to allow the portlet to be cached within the
	 * layout.
	 *
	 * @param layoutCacheable boolean value for whether the portlet can be
	 cached within the layout
	 */
	@Override
	public void setLayoutCacheable(boolean layoutCacheable) {
		model.setLayoutCacheable(layoutCacheable);
	}

	/**
	 * Set to <code>true</code> if the portlet goes into the maximized state
	 * when the user goes into the edit mode.
	 *
	 * @param maximizeEdit boolean value for whether the portlet goes into the
	 maximized state when the user goes into the edit mode
	 */
	@Override
	public void setMaximizeEdit(boolean maximizeEdit) {
		model.setMaximizeEdit(maximizeEdit);
	}

	/**
	 * Set to <code>true</code> if the portlet goes into the maximized state
	 * when the user goes into the help mode.
	 *
	 * @param maximizeHelp boolean value for whether the portlet goes into the
	 maximized state when the user goes into the help mode
	 */
	@Override
	public void setMaximizeHelp(boolean maximizeHelp) {
		model.setMaximizeHelp(maximizeHelp);
	}

	/**
	 * Sets the maximum size of buffered bytes before storing occurs.
	 *
	 * @param multipartFileSizeThreshold the maximum size of buffered bytes
	 before storing occurs
	 */
	@Override
	public void setMultipartFileSizeThreshold(int multipartFileSizeThreshold) {
		model.setMultipartFileSizeThreshold(multipartFileSizeThreshold);
	}

	/**
	 * Sets the directory for storing uploaded files.
	 *
	 * @param multipartLocation the directory for storing uploaded files
	 */
	@Override
	public void setMultipartLocation(String multipartLocation) {
		model.setMultipartLocation(multipartLocation);
	}

	/**
	 * Sets the maximum number of bytes permitted for an uploaded file.
	 *
	 * @param multipartMaxFileSize the maximum number of bytes permitted for an
	 uploaded file
	 */
	@Override
	public void setMultipartMaxFileSize(long multipartMaxFileSize) {
		model.setMultipartMaxFileSize(multipartMaxFileSize);
	}

	/**
	 * Sets the maximum number of bytes permitted for a multipart request.
	 *
	 * @param multipartMaxRequestSize the maximum number of bytes permitted for
	 a multipart request
	 */
	@Override
	public void setMultipartMaxRequestSize(long multipartMaxRequestSize) {
		model.setMultipartMaxRequestSize(multipartMaxRequestSize);
	}

	/**
	 * Sets the mvcc version of this portlet.
	 *
	 * @param mvccVersion the mvcc version of this portlet
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of the open search class of the portlet.
	 *
	 * @param openSearchClass the name of the open search class of the portlet
	 */
	@Override
	public void setOpenSearchClass(String openSearchClass) {
		model.setOpenSearchClass(openSearchClass);
	}

	/**
	 * Sets the parent struts path of the portlet.
	 *
	 * @param parentStrutsPath the parent struts path of the portlet
	 */
	@Override
	public void setParentStrutsPath(String parentStrutsPath) {
		model.setParentStrutsPath(parentStrutsPath);
	}

	/**
	 * Sets whether the portlet's serve resource should be invoked during a
	 * partial action triggered by a different portlet on the same portal page.
	 *
	 * @param partialActionServeResource whether the portlet's
	 <code>serveResource(ResourceRequest,ResourceResponse)</code>
	 method should be invoked during a partial action triggered by a
	 different portlet on the same portal page
	 */
	@Override
	public void setPartialActionServeResource(
		boolean partialActionServeResource) {

		model.setPartialActionServeResource(partialActionServeResource);
	}

	/**
	 * Sets the name of the permission propagator class of the portlet.
	 */
	@Override
	public void setPermissionPropagatorClass(String permissionPropagatorClass) {
		model.setPermissionPropagatorClass(permissionPropagatorClass);
	}

	/**
	 * Sets this portlet's plugin package.
	 *
	 * @param pluginPackage this portlet's plugin package
	 */
	@Override
	public void setPluginPackage(
		com.liferay.portal.kernel.plugin.PluginPackage pluginPackage) {

		model.setPluginPackage(pluginPackage);
	}

	/**
	 * Sets the name of the POP message listener class of the portlet.
	 *
	 * @param popMessageListenerClass the name of the POP message listener class
	 of the portlet
	 */
	@Override
	public void setPopMessageListenerClass(String popMessageListenerClass) {
		model.setPopMessageListenerClass(popMessageListenerClass);
	}

	/**
	 * Sets whether the portlet goes into the pop up state when the user goes
	 * into the print mode.
	 *
	 * @param popUpPrint whether the portlet goes into the pop up state when the
	 user goes into the print mode
	 */
	@Override
	public void setPopUpPrint(boolean popUpPrint) {
		model.setPopUpPrint(popUpPrint);
	}

	/**
	 * Sets this portlet's application.
	 *
	 * @param portletApp this portlet's application
	 */
	@Override
	public void setPortletApp(PortletApp portletApp) {
		model.setPortletApp(portletApp);
	}

	/**
	 * Sets the name of the portlet class of the portlet.
	 *
	 * @param portletClass the name of the portlet class of the portlet
	 */
	@Override
	public void setPortletClass(String portletClass) {
		model.setPortletClass(portletClass);
	}

	@Override
	public void setPortletConfigurationListenerClass(
		String portletConfigurationListenerClass) {

		model.setPortletConfigurationListenerClass(
			portletConfigurationListenerClass);
	}

	/**
	 * Sets the name of the portlet data handler class of the portlet.
	 *
	 * @param portletDataHandlerClass the name of portlet data handler class of
	 the portlet
	 */
	@Override
	public void setPortletDataHandlerClass(String portletDataHandlerClass) {
		model.setPortletDataHandlerClass(portletDataHandlerClass);
	}

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
	@Override
	public void setPortletDependencyCssEnabled(
		boolean portletDependencyCssEnabled) {

		model.setPortletDependencyCssEnabled(portletDependencyCssEnabled);
	}

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
	@Override
	public void setPortletDependencyJavaScriptEnabled(
		boolean portletDependencyJavaScriptEnabled) {

		model.setPortletDependencyJavaScriptEnabled(
			portletDependencyJavaScriptEnabled);
	}

	/**
	 * Sets the filters of the portlet.
	 *
	 * @param portletFilters the filters of the portlet
	 */
	@Override
	public void setPortletFilters(Map<String, PortletFilter> portletFilters) {
		model.setPortletFilters(portletFilters);
	}

	/**
	 * Sets the portlet ID of this portlet.
	 *
	 * @param portletId the portlet ID of this portlet
	 */
	@Override
	public void setPortletId(String portletId) {
		model.setPortletId(portletId);
	}

	/**
	 * Sets the portlet info of the portlet.
	 *
	 * @param portletInfo the portlet info of the portlet
	 */
	@Override
	public void setPortletInfo(PortletInfo portletInfo) {
		model.setPortletInfo(portletInfo);
	}

	/**
	 * Sets the name of the portlet layout listener class of the portlet.
	 *
	 * @param portletLayoutListenerClass the name of the portlet layout listener
	 class of the portlet
	 */
	@Override
	public void setPortletLayoutListenerClass(
		String portletLayoutListenerClass) {

		model.setPortletLayoutListenerClass(portletLayoutListenerClass);
	}

	/**
	 * Sets the portlet modes of the portlet.
	 *
	 * @param portletModes the portlet modes of the portlet
	 */
	@Override
	public void setPortletModes(
		Map<String, java.util.Set<String>> portletModes) {

		model.setPortletModes(portletModes);
	}

	/**
	 * Sets the name of the portlet.
	 *
	 * @param portletName the name of the portlet
	 */
	@Override
	public void setPortletName(String portletName) {
		model.setPortletName(portletName);
	}

	/**
	 * Sets the name of the portlet URL class of the portlet.
	 *
	 * @param portletURLClass the name of the portlet URL class of the portlet
	 */
	@Override
	public void setPortletURLClass(String portletURLClass) {
		model.setPortletURLClass(portletURLClass);
	}

	/**
	 * Set to <code>true</code> if preferences are shared across the entire
	 * company.
	 *
	 * @param preferencesCompanyWide boolean value for whether preferences are
	 shared across the entire company
	 */
	@Override
	public void setPreferencesCompanyWide(boolean preferencesCompanyWide) {
		model.setPreferencesCompanyWide(preferencesCompanyWide);
	}

	/**
	 * Set to <code>true</code> if preferences are owned by the group when the
	 * portlet is shown in a group layout. Set to <code>false</code> if
	 * preferences are owned by the user at all times.
	 *
	 * @param preferencesOwnedByGroup boolean value for whether preferences are
	 owned by the group when the portlet is shown in a group layout or
	 preferences are owned by the user at all times
	 */
	@Override
	public void setPreferencesOwnedByGroup(boolean preferencesOwnedByGroup) {
		model.setPreferencesOwnedByGroup(preferencesOwnedByGroup);
	}

	/**
	 * Set to <code>true</code> if preferences are unique per layout.
	 *
	 * @param preferencesUniquePerLayout boolean value for whether preferences
	 are unique per layout
	 */
	@Override
	public void setPreferencesUniquePerLayout(
		boolean preferencesUniquePerLayout) {

		model.setPreferencesUniquePerLayout(preferencesUniquePerLayout);
	}

	/**
	 * Sets the name of the preferences validator class of the portlet.
	 *
	 * @param preferencesValidator the name of the preferences validator class
	 of the portlet
	 */
	@Override
	public void setPreferencesValidator(String preferencesValidator) {
		model.setPreferencesValidator(preferencesValidator);
	}

	/**
	 * Sets the primary key of this portlet.
	 *
	 * @param primaryKey the primary key of this portlet
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Set to <code>true</code> if the portlet does not share request attributes
	 * with the portal or portlets from another WAR.
	 *
	 * @param privateRequestAttributes boolean value for whether the portlet
	 shares request attributes with the portal or portlets from another
	 WAR
	 */
	@Override
	public void setPrivateRequestAttributes(boolean privateRequestAttributes) {
		model.setPrivateRequestAttributes(privateRequestAttributes);
	}

	/**
	 * Set to <code>true</code> if the portlet does not share session attributes
	 * with the portal.
	 *
	 * @param privateSessionAttributes boolean value for whether the portlet
	 shares session attributes with the portal
	 */
	@Override
	public void setPrivateSessionAttributes(boolean privateSessionAttributes) {
		model.setPrivateSessionAttributes(privateSessionAttributes);
	}

	/**
	 * Sets the processing events of the portlet.
	 *
	 * @param processingEvents the processing events of the portlet
	 */
	@Override
	public void setProcessingEvents(
		java.util.Set<com.liferay.portal.kernel.xml.QName> processingEvents) {

		model.setProcessingEvents(processingEvents);
	}

	/**
	 * Sets the public render parameters of the portlet.
	 *
	 * @param publicRenderParameters the public render parameters of the portlet
	 */
	@Override
	public void setPublicRenderParameters(
		java.util.Set<PublicRenderParameter> publicRenderParameters) {

		model.setPublicRenderParameters(publicRenderParameters);
	}

	/**
	 * Sets the publishing events of the portlet.
	 *
	 * @param publishingEvents the publishing events of the portlet
	 */
	@Override
	public void setPublishingEvents(
		java.util.Set<com.liferay.portal.kernel.xml.QName> publishingEvents) {

		model.setPublishingEvents(publishingEvents);
	}

	/**
	 * Set to <code>true</code> if the portlet is ready to be used.
	 *
	 * @param ready whether the portlet is ready to be used
	 */
	@Override
	public void setReady(boolean ready) {
		model.setReady(ready);
	}

	/**
	 * Sets the render timeout of the portlet.
	 *
	 * @param renderTimeout the render timeout of the portlet
	 */
	@Override
	public void setRenderTimeout(int renderTimeout) {
		model.setRenderTimeout(renderTimeout);
	}

	/**
	 * Sets the render weight of the portlet.
	 *
	 * @param renderWeight int value for the render weight of the portlet
	 */
	@Override
	public void setRenderWeight(int renderWeight) {
		model.setRenderWeight(renderWeight);
	}

	/**
	 * Set to <code>true</code> if the portlet will only process namespaced
	 * parameters.
	 *
	 * @param requiresNamespacedParameters boolean value for whether the portlet
	 will only process namespaced parameters
	 */
	@Override
	public void setRequiresNamespacedParameters(
		boolean requiresNamespacedParameters) {

		model.setRequiresNamespacedParameters(requiresNamespacedParameters);
	}

	/**
	 * Sets the resource bundle of the portlet.
	 *
	 * @param resourceBundle the resource bundle of the portlet
	 */
	@Override
	public void setResourceBundle(String resourceBundle) {
		model.setResourceBundle(resourceBundle);
	}

	/**
	 * Set to <code>true</code> if the portlet restores to the current view from
	 * the maximized state.
	 *
	 * @param restoreCurrentView boolean value for whether the portlet restores
	 to the current view from the maximized state
	 */
	@Override
	public void setRestoreCurrentView(boolean restoreCurrentView) {
		model.setRestoreCurrentView(restoreCurrentView);
	}

	/**
	 * Sets the role mappers of the portlet.
	 *
	 * @param roleMappers the role mappers of the portlet
	 */
	@Override
	public void setRoleMappers(Map<String, String> roleMappers) {
		model.setRoleMappers(roleMappers);
	}

	/**
	 * Sets the roles of this portlet.
	 *
	 * @param roles the roles of this portlet
	 */
	@Override
	public void setRoles(String roles) {
		model.setRoles(roles);
	}

	/**
	 * Sets an array of required roles of the portlet.
	 *
	 * @param rolesArray an array of required roles of the portlet
	 */
	@Override
	public void setRolesArray(String[] rolesArray) {
		model.setRolesArray(rolesArray);
	}

	/**
	 * Sets the scheduler entries of the portlet.
	 *
	 * @param schedulerEntries the scheduler entries of the portlet
	 */
	@Override
	public void setSchedulerEntries(
		java.util.List<com.liferay.portal.kernel.scheduler.SchedulerEntry>
			schedulerEntries) {

		model.setSchedulerEntries(schedulerEntries);
	}

	/**
	 * Set to <code>true</code> if the portlet supports scoping of data.
	 *
	 * @param scopeable boolean value for whether or not the the portlet
	 supports scoping of data
	 */
	@Override
	public void setScopeable(boolean scopeable) {
		model.setScopeable(scopeable);
	}

	/**
	 * Set to <code>true</code> if users are shown that they do not have access
	 * to the portlet.
	 *
	 * @param showPortletAccessDenied boolean value for whether users are shown
	 that they do not have access to the portlet
	 */
	@Override
	public void setShowPortletAccessDenied(boolean showPortletAccessDenied) {
		model.setShowPortletAccessDenied(showPortletAccessDenied);
	}

	/**
	 * Set to <code>true</code> if users are shown that the portlet is inactive.
	 *
	 * @param showPortletInactive boolean value for whether users are shown that
	 the portlet is inactive
	 */
	@Override
	public void setShowPortletInactive(boolean showPortletInactive) {
		model.setShowPortletInactive(showPortletInactive);
	}

	/**
	 * Set to <code>true</code> if the portlet uses Single Page Application.
	 *
	 * @param singlePageApplication boolean value for whether or not the the
	 portlet uses Single Page Application
	 */
	@Override
	public void setSinglePageApplication(boolean singlePageApplication) {
		model.setSinglePageApplication(singlePageApplication);
	}

	/**
	 * Sets the names of the classes that represent social activity interpreters
	 * associated with the portlet.
	 *
	 * @param socialActivityInterpreterClasses the names of the classes that
	 represent social activity interpreters associated with the portlet
	 */
	@Override
	public void setSocialActivityInterpreterClasses(
		java.util.List<String> socialActivityInterpreterClasses) {

		model.setSocialActivityInterpreterClasses(
			socialActivityInterpreterClasses);
	}

	/**
	 * Sets the name of the social request interpreter class of the portlet.
	 *
	 * @param socialRequestInterpreterClass the name of the request interpreter
	 class of the portlet
	 */
	@Override
	public void setSocialRequestInterpreterClass(
		String socialRequestInterpreterClass) {

		model.setSocialRequestInterpreterClass(socialRequestInterpreterClass);
	}

	/**
	 * Sets the names of the classes that represent staged model data handlers
	 * associated with the portlet.
	 *
	 * @param stagedModelDataHandlerClasses the names of the classes that
	 represent staged model data handlers associated with the portlet
	 */
	@Override
	public void setStagedModelDataHandlerClasses(
		java.util.List<String> stagedModelDataHandlerClasses) {

		model.setStagedModelDataHandlerClasses(stagedModelDataHandlerClasses);
	}

	/**
	 * Set to <code>true</code> if the portlet is a static portlet that is
	 * cannot be moved.
	 *
	 * @param staticPortlet boolean value for whether the portlet is a static
	 portlet that cannot be moved
	 */
	@Override
	public void setStatic(boolean staticPortlet) {
		model.setStatic(staticPortlet);
	}

	/**
	 * Set to <code>true</code> if the portlet is a static portlet at the start
	 * of a list of portlets.
	 *
	 * @param staticPortletStart boolean value for whether the portlet is a
	 static portlet at the start of a list of portlets
	 */
	@Override
	public void setStaticStart(boolean staticPortletStart) {
		model.setStaticStart(staticPortletStart);
	}

	/**
	 * Sets the struts path of the portlet.
	 *
	 * @param strutsPath the struts path of the portlet
	 */
	@Override
	public void setStrutsPath(String strutsPath) {
		model.setStrutsPath(strutsPath);
	}

	/**
	 * Sets the supported locales of the portlet.
	 *
	 * @param supportedLocales the supported locales of the portlet
	 */
	@Override
	public void setSupportedLocales(java.util.Set<String> supportedLocales) {
		model.setSupportedLocales(supportedLocales);
	}

	/**
	 * Set to <code>true</code> if the portlet is a system portlet that a user
	 * cannot manually add to their page.
	 *
	 * @param system boolean value for whether the portlet is a system portlet
	 that a user cannot manually add to their page
	 */
	@Override
	public void setSystem(boolean system) {
		model.setSystem(system);
	}

	/**
	 * Sets the name of the template handler class of the portlet.
	 *
	 * @param templateHandlerClass the name of template handler class of the
	 portlet
	 */
	@Override
	public void setTemplateHandlerClass(String templateHandlerClass) {
		model.setTemplateHandlerClass(templateHandlerClass);
	}

	/**
	 * Sets the names of the classes that represent trash handlers associated to
	 * the portlet.
	 *
	 * @param trashHandlerClasses the names of the classes that represent trash
	 handlers associated with the portlet
	 */
	@Override
	public void setTrashHandlerClasses(
		java.util.List<String> trashHandlerClasses) {

		model.setTrashHandlerClasses(trashHandlerClasses);
	}

	/**
	 * Set to <code>true</code> if the portlet is an undeployed portlet.
	 *
	 * @param undeployedPortlet boolean value for whether the portlet is an
	 undeployed portlet
	 */
	@Override
	public void setUndeployedPortlet(boolean undeployedPortlet) {
		model.setUndeployedPortlet(undeployedPortlet);
	}

	/**
	 * Sets the unlinked roles of the portlet.
	 *
	 * @param unlinkedRoles the unlinked roles of the portlet
	 */
	@Override
	public void setUnlinkedRoles(java.util.Set<String> unlinkedRoles) {
		model.setUnlinkedRoles(unlinkedRoles);
	}

	/**
	 * Sets the name of the URL encoder class of the portlet.
	 *
	 * @param urlEncoderClass the name of the URL encoder class of the portlet
	 */
	@Override
	public void setURLEncoderClass(String urlEncoderClass) {
		model.setURLEncoderClass(urlEncoderClass);
	}

	/**
	 * Set to <code>true</code> if the portlet uses the default template.
	 *
	 * @param useDefaultTemplate boolean value for whether the portlet uses the
	 default template
	 */
	@Override
	public void setUseDefaultTemplate(boolean useDefaultTemplate) {
		model.setUseDefaultTemplate(useDefaultTemplate);
	}

	/**
	 * Sets the class loader resource path to the user notification definitions
	 * of the portlet.
	 *
	 * @param userNotificationDefinitions the class loader resource path to the
	 user notification definitions of the portlet
	 */
	@Override
	public void setUserNotificationDefinitions(
		String userNotificationDefinitions) {

		model.setUserNotificationDefinitions(userNotificationDefinitions);
	}

	/**
	 * Sets the names of the classes that represent user notification handlers
	 * associated with the portlet.
	 *
	 * @param userNotificationHandlerClasses the names of the classes that
	 represent user notification handlers associated with the portlet
	 */
	@Override
	public void setUserNotificationHandlerClasses(
		java.util.List<String> userNotificationHandlerClasses) {

		model.setUserNotificationHandlerClasses(userNotificationHandlerClasses);
	}

	/**
	 * Sets the user principal strategy of the portlet.
	 *
	 * @param userPrincipalStrategy the user principal strategy of the portlet
	 */
	@Override
	public void setUserPrincipalStrategy(String userPrincipalStrategy) {
		model.setUserPrincipalStrategy(userPrincipalStrategy);
	}

	/**
	 * Sets the virtual path of the portlet.
	 *
	 * @param virtualPath the virtual path of the portlet
	 */
	@Override
	public void setVirtualPath(String virtualPath) {
		model.setVirtualPath(virtualPath);
	}

	/**
	 * Sets the name of the WebDAV storage class of the portlet.
	 *
	 * @param webDAVStorageClass the name of the WebDAV storage class of the
	 portlet
	 */
	@Override
	public void setWebDAVStorageClass(String webDAVStorageClass) {
		model.setWebDAVStorageClass(webDAVStorageClass);
	}

	/**
	 * Sets the name of the WebDAV storage token of the portlet.
	 *
	 * @param webDAVStorageToken the name of the WebDAV storage token of the
	 portlet
	 */
	@Override
	public void setWebDAVStorageToken(String webDAVStorageToken) {
		model.setWebDAVStorageToken(webDAVStorageToken);
	}

	/**
	 * Sets the window states of the portlet.
	 *
	 * @param windowStates the window states of the portlet
	 */
	@Override
	public void setWindowStates(
		Map<String, java.util.Set<String>> windowStates) {

		model.setWindowStates(windowStates);
	}

	/**
	 * Sets the names of the classes that represent workflow handlers associated
	 * to the portlet.
	 *
	 * @param workflowHandlerClasses the names of the classes that represent
	 workflow handlers associated with the portlet
	 */
	@Override
	public void setWorkflowHandlerClasses(
		java.util.List<String> workflowHandlerClasses) {

		model.setWorkflowHandlerClasses(workflowHandlerClasses);
	}

	/**
	 * Sets the name of the XML-RPC method class of the portlet.
	 *
	 * @param xmlRpcMethodClass the name of the XML-RPC method class of the
	 portlet
	 */
	@Override
	public void setXmlRpcMethodClass(String xmlRpcMethodClass) {
		model.setXmlRpcMethodClass(xmlRpcMethodClass);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	public void unsetReady() {
		model.unsetReady();
	}

	@Override
	protected PortletWrapper wrap(Portlet portlet) {
		return new PortletWrapper(portlet);
	}

}