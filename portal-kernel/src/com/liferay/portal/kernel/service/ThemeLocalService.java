/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.ColorScheme;
import com.liferay.portal.kernel.model.PortletDecorator;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;

import java.util.List;

import jakarta.servlet.ServletContext;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for Theme. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see ThemeLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface ThemeLocalService extends BaseLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.service.impl.ThemeLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the theme local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link ThemeLocalServiceUtil} if injection and service tracking are not available.
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ColorScheme fetchColorScheme(
		long companyId, String themeId, String colorSchemeId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PortletDecorator fetchPortletDecorator(
		long companyId, String themeId, String colorSchemeId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Theme fetchTheme(long companyId, String themeId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ColorScheme getColorScheme(
		long companyId, String themeId, String colorSchemeId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Theme> getControlPanelThemes(long companyId, long userId);

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Theme> getPageThemes(long companyId, long groupId, long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PortletDecorator getPortletDecorator(
		long companyId, String themeId, String portletDecoratorId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Theme getTheme(long companyId, String themeId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Theme> getThemes(long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Theme> getWARThemes();

	public List<Theme> init(
		ServletContext servletContext, String themesPath,
		boolean loadFromServletContext, String[] xmls,
		PluginPackage pluginPackage);

	public List<Theme> init(
		String servletContextName, ServletContext servletContext,
		String themesPath, boolean loadFromServletContext, String[] xmls,
		PluginPackage pluginPackage);

	public void uninstallThemes(List<Theme> themes);

}