/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.portlet.display.template;

import com.liferay.blogs.configuration.BlogsConfiguration;
import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.blogs.service.BlogsEntryService;
import com.liferay.blogs.web.internal.security.permission.resource.BlogsEntryPermission;
import com.liferay.blogs.web.internal.util.BlogsEntryAssetEntryUtil;
import com.liferay.blogs.web.internal.util.BlogsEntryUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateVariableGroup;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portlet.display.template.BasePortletDisplayTemplateHandler;
import com.liferay.portlet.display.template.constants.PortletDisplayTemplateConstants;
import com.liferay.taglib.security.PermissionsURLTag;
import com.liferay.trash.TrashHelper;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Juan Fernández
 */
@Component(
	configurationPid = "com.liferay.blogs.configuration.BlogsConfiguration",
	property = "jakarta.portlet.name=" + BlogsPortletKeys.BLOGS,
	service = TemplateHandler.class
)
public class BlogsPortletDisplayTemplateHandler
	extends BasePortletDisplayTemplateHandler {

	@Override
	public String getClassName() {
		return BlogsEntry.class.getName();
	}

	@Override
	public Map<String, Object> getCustomContextObjects() {
		return HashMapBuilder.<String, Object>put(
			"blogsEntryAssetEntryUtil", new BlogsEntryAssetEntryUtil()
		).put(
			"blogsEntryPermission", new BlogsEntryPermission()
		).put(
			"blogsEntryUtil", new BlogsEntryUtil()
		).put(
			"commentManager", _commentManager
		).put(
			"language", _language
		).put(
			"permissionsURLTag", new PermissionsURLTag()
		).put(
			"trashHelper", _trashHelper
		).build();
	}

	@Override
	public String getName(Locale locale) {
		String portletTitle = _portal.getPortletTitle(
			BlogsPortletKeys.BLOGS,
			ResourceBundleUtil.getBundle(
				"content.Language", locale, getClass()));

		return _language.format(locale, "x-template", portletTitle, false);
	}

	@Override
	public String getResourceName() {
		return BlogsPortletKeys.BLOGS;
	}

	@Override
	public Map<String, TemplateVariableGroup> getTemplateVariableGroups(
			long classPK, String language, Locale locale)
		throws Exception {

		Map<String, TemplateVariableGroup> templateVariableGroups =
			super.getTemplateVariableGroups(classPK, language, locale);

		String[] restrictedVariables = getRestrictedVariables(language);

		TemplateVariableGroup blogsUtilTemplateVariableGroup =
			new TemplateVariableGroup("blogs-util", restrictedVariables);

		blogsUtilTemplateVariableGroup.addVariable(
			"blogs-entry-asset-entry-util", BlogsEntryAssetEntryUtil.class,
			"blogsEntryAssetEntryUtil");

		templateVariableGroups.put(
			"blogs-util", blogsUtilTemplateVariableGroup);

		TemplateVariableGroup blogServicesTemplateVariableGroup =
			new TemplateVariableGroup("blog-services", restrictedVariables);

		blogServicesTemplateVariableGroup.setAutocompleteEnabled(false);

		blogServicesTemplateVariableGroup.addServiceLocatorVariables(
			BlogsEntryLocalService.class, BlogsEntryService.class);

		templateVariableGroups.put(
			blogServicesTemplateVariableGroup.getLabel(),
			blogServicesTemplateVariableGroup);

		TemplateVariableGroup fieldsTemplateVariableGroup =
			templateVariableGroups.get("fields");

		fieldsTemplateVariableGroup.empty();

		fieldsTemplateVariableGroup.addCollectionVariable(
			"blog-entries", List.class, PortletDisplayTemplateConstants.ENTRIES,
			"blog-entry", BlogsEntry.class, "curBlogEntry", "title");

		return templateVariableGroups;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_blogsConfiguration = ConfigurableUtil.createConfigurable(
			BlogsConfiguration.class, properties);
	}

	@Override
	protected String getTemplatesConfigPath() {
		return _blogsConfiguration.displayTemplatesConfig();
	}

	private volatile BlogsConfiguration _blogsConfiguration;

	@Reference
	private CommentManager _commentManager;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.blogs.service)(&(release.schema.version>=3.0.0)(!(release.schema.version>=4.0.0))))"
	)
	private Release _release;

	@Reference
	private TrashHelper _trashHelper;

}