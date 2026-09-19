/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.template;

import com.liferay.portal.kernel.xml.Element;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Juan Fernández
 */
@ProviderType
public interface TemplateHandler {

	/**
	 * Returns the template handler's class name.
	 *
	 * @return the template handler's class name
	 */
	public String getClassName();

	/**
	 * Returns the map of name/value pairs of the objects that should be
	 * injected into the context.
	 *
	 * @return the objects that should be injected into the context
	 */
	public Map<String, Object> getCustomContextObjects();

	/**
	 * Returns the elements containing the information of the portlet display
	 * templates to be installed by default.
	 *
	 * @return the elements containing the information of the portlet display
	 *         templates to be installed by default. These templates are
	 *         installed when registering the portlet.
	 * @throws Exception if an exception occurred assembling the default
	 *         template elements
	 */
	public List<Element> getDefaultTemplateElements() throws Exception;

	/**
	 * Returns the key of the template handler's default template.
	 *
	 * @return the key of the template handler's default template
	 */
	public String getDefaultTemplateKey();

	/**
	 * Returns the template handler's name.
	 *
	 * @param  locale the locale of the template handler name to get
	 * @return the template handler's name
	 */
	public String getName(Locale locale);

	/**
	 * Returns the name of the resource associated with the template.
	 * Permissions on the resource are checked when adding a new template.
	 *
	 * @return the name of the resource associated with the template
	 */
	public String getResourceName();

	/**
	 * Returns the restricted variables that are excluded from the template's
	 * context.
	 *
	 * @param  language the template's scripting language. Acceptable values for
	 *         the FreeMarker or Velocity languages are {@link
	 *         TemplateConstants#LANG_TYPE_FTL}, or {@link
	 *         TemplateConstants#LANG_TYPE_VM}, respectively.
	 * @return the restricted variables that are excluded from the template's
	 *         context
	 */
	public String[] getRestrictedVariables(String language);

	/**
	 * Returns the template's map of script variable groups for which hints are
	 * displayed in the template editor palette.
	 *
	 * <p>
	 * Script variables can be grouped arbitrarily. As examples, a group of
	 * entity fields could be mapped to the keyword <code>Fields</code>, or a
	 * group of general variables portal variables could be mapped to the phrase
	 * <code>General Variables</code>, etc.
	 * </p>
	 *
	 * @param  classPK the primary key of the entity that defines the variable
	 *         groups for the template. For example, consider specifying the
	 *         primary key of the structure associated to the template.
	 * @param  language the template's scripting language. Acceptable values for
	 *         the FreeMarker or Velocity languages are {@link
	 *         TemplateConstants#LANG_TYPE_FTL}, or {@link
	 *         TemplateConstants#LANG_TYPE_VM}, respectively.
	 * @param  locale the locale of the variable groups to get
	 * @return the template's map of script variable groups for which hints are
	 *         displayed in the template editor palette
	 * @throws Exception if an exception occurred retrieving the template
	 *         variable groups
	 */
	public Map<String, TemplateVariableGroup> getTemplateVariableGroups(
			long classPK, String language, Locale locale)
		throws Exception;

	/**
	 * Returns initial template content for helping the user create a new
	 * template.
	 *
	 * @param  language the template's scripting language. Acceptable values for
	 *         the FreeMarker or Velocity languages are {@link
	 *         TemplateConstants#LANG_TYPE_FTL}, or {@link
	 *         TemplateConstants#LANG_TYPE_VM}, respectively.
	 * @return initial template content for helping the user create a new
	 *         template
	 */
	public String getTemplatesHelpContent(String language);

	/**
	 * Returns the path to the template's help content.
	 *
	 * @param  language the template's scripting language. Acceptable values for
	 *         the FreeMarker or Velocity languages are {@link
	 *         TemplateConstants#LANG_TYPE_FTL}, or {@link
	 *         TemplateConstants#LANG_TYPE_VM}, respectively.
	 * @return the path to the template's help content
	 */
	public String getTemplatesHelpPath(String language);

	/**
	 * Returns the name of the property in <code>portal.properties</code> that
	 * defines the path to the template's help content.
	 *
	 * @return the name of the property in <code>portal.properties</code> that
	 *         defines the path to the template's help content
	 */
	public String getTemplatesHelpPropertyKey();

	/**
	 * Returns <code>true</code> if the entity is a display template handler.
	 *
	 * @return <code>true</code> if the entity is a display template handler;
	 *         <code>false</code> otherwise
	 */
	public boolean isDisplayTemplateHandler();

	public default boolean isEnabled(long companyId) {
		return true;
	}

}