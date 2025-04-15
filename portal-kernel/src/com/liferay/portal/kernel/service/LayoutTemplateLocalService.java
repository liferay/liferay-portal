/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.LayoutTemplate;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.xml.Element;

import java.util.List;
import java.util.Set;

import jakarta.servlet.ServletContext;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for LayoutTemplate. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutTemplateLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface LayoutTemplateLocalService extends BaseLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.service.impl.LayoutTemplateLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the layout template local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link LayoutTemplateLocalServiceUtil} if injection and service tracking are not available.
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String getContent(
		String layoutTemplateId, boolean standard, String themeId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String getLangType(
		String layoutTemplateId, boolean standard, String themeId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutTemplate getLayoutTemplate(
		String layoutTemplateId, boolean standard, String themeId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutTemplate> getLayoutTemplates();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutTemplate> getLayoutTemplates(String themeId);

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	public List<LayoutTemplate> init(
		ServletContext servletContext, String[] xmls,
		PluginPackage pluginPackage);

	public List<LayoutTemplate> init(
		String servletContextName, ServletContext servletContext, String[] xmls,
		PluginPackage pluginPackage);

	public void readLayoutTemplate(
		String servletContextName, ServletContext servletContext,
		Set<LayoutTemplate> layoutTemplates, Element element, boolean standard,
		String themeId, PluginPackage pluginPackage);

	public void uninstallLayoutTemplate(
		String layoutTemplateId, boolean standard);

	public void uninstallLayoutTemplates(String themeId);

}