/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.expando.kernel.model.CustomAttributesDisplay;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.pop.MessageListener;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.OpenSearch;
import com.liferay.portal.kernel.security.permission.propagator.PermissionPropagator;
import com.liferay.portal.kernel.servlet.URLEncoder;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.webdav.WebDAVStorage;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.kernel.xmlrpc.Method;
import com.liferay.social.kernel.model.SocialActivityInterpreter;
import com.liferay.social.kernel.model.SocialRequestInterpreter;

import jakarta.portlet.Portlet;
import jakarta.portlet.PreferencesValidator;

import jakarta.servlet.ServletContext;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 */
@ProviderType
public interface PortletBag extends Cloneable {

	public Object clone();

	public void destroy();

	public ConfigurationAction getConfigurationActionInstance();

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getConfigurationActionInstance()}
	 */
	@Deprecated
	public List<ConfigurationAction> getConfigurationActionInstances();

	public ControlPanelEntry getControlPanelEntryInstance();

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getControlPanelEntryInstance()}
	 */
	@Deprecated
	public List<ControlPanelEntry> getControlPanelEntryInstances();

	public List<CustomAttributesDisplay> getCustomAttributesDisplayInstances();

	public FriendlyURLMapperTracker getFriendlyURLMapperTracker();

	public List<Indexer<?>> getIndexerInstances();

	public OpenSearch getOpenSearchInstance();

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getOpenSearchInstance()}
	 */
	@Deprecated
	public List<OpenSearch> getOpenSearchInstances();

	public PermissionPropagator getPermissionPropagatorInstance();

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getPermissionPropagatorInstance()}
	 */
	@Deprecated
	public List<PermissionPropagator> getPermissionPropagatorInstances();

	public MessageListener getPopMessageListenerInstance();

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getPopMessageListenerInstance()}
	 */
	@Deprecated
	public List<MessageListener> getPopMessageListenerInstances();

	public PortletConfigurationListener
		getPortletConfigurationListenerInstance();

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getPortletConfigurationListenerInstance()}
	 */
	@Deprecated
	public List<PortletConfigurationListener>
		getPortletConfigurationListenerInstances();

	public PortletDataHandler getPortletDataHandlerInstance(long companyId);

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getPortletDataHandlerInstance(long companyId)}
	 */
	@Deprecated
	public List<PortletDataHandler> getPortletDataHandlerInstances();

	public Portlet getPortletInstance();

	public PortletLayoutListener getPortletLayoutListenerInstance();

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getPortletLayoutListenerInstance()}
	 */
	@Deprecated
	public List<PortletLayoutListener> getPortletLayoutListenerInstances();

	public String getPortletName();

	public PreferencesValidator getPreferencesValidatorInstance();

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getPreferencesValidatorInstance()}
	 */
	@Deprecated
	public List<PreferencesValidator> getPreferencesValidatorInstances();

	public ResourceBundle getResourceBundle(Locale locale);

	public String getResourceBundleBaseName();

	public ServletContext getServletContext();

	public List<SocialActivityInterpreter>
		getSocialActivityInterpreterInstances();

	public SocialRequestInterpreter getSocialRequestInterpreterInstance();

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getSocialRequestInterpreterInstance()}
	 */
	@Deprecated
	public List<SocialRequestInterpreter>
		getSocialRequestInterpreterInstances();

	public List<StagedModelDataHandler<?>> getStagedModelDataHandlerInstances();

	public TemplateHandler getTemplateHandlerInstance();

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getTemplateHandlerInstance()}
	 */
	@Deprecated
	public List<TemplateHandler> getTemplateHandlerInstances();

	public List<TrashHandler> getTrashHandlerInstances();

	public URLEncoder getURLEncoderInstance();

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getURLEncoderInstance()}
	 */
	@Deprecated
	public List<URLEncoder> getURLEncoderInstances();

	/**
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	public List<UserNotificationDefinition>
		getUserNotificationDefinitionInstances();

	public List<UserNotificationHandler> getUserNotificationHandlerInstances();

	public WebDAVStorage getWebDAVStorageInstance();

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getWebDAVStorageInstance()}
	 */
	@Deprecated
	public List<WebDAVStorage> getWebDAVStorageInstances();

	public List<WorkflowHandler<?>> getWorkflowHandlerInstances();

	public Method getXmlRpcMethodInstance();

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #getXmlRpcMethodInstance()}
	 */
	@Deprecated
	public List<Method> getXmlRpcMethodInstances();

	public void setPortletInstance(Portlet portletInstance);

	public void setPortletName(String portletName);

}