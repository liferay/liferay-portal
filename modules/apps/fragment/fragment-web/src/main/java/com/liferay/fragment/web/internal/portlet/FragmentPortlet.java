/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet;

import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.helper.DefaultInputFragmentEntryConfigurationProvider;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.processor.FragmentEntryProcessorRegistry;
import com.liferay.fragment.renderer.FragmentRendererController;
import com.liferay.fragment.service.FragmentCollectionService;
import com.liferay.fragment.web.internal.configuration.FragmentPortletConfiguration;
import com.liferay.fragment.web.internal.constants.FragmentWebKeys;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.staging.StagingGroupHelper;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	configurationPid = "com.liferay.fragment.web.internal.configuration.FragmentPortletConfiguration",
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-fragment-web",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Fragments",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + FragmentPortletKeys.FRAGMENT,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class FragmentPortlet extends MVCPortlet {

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Group scopeGroup = themeDisplay.getScopeGroup();

		if (_stagingGroupHelper.isLocalLiveGroup(scopeGroup) ||
			_stagingGroupHelper.isRemoteLiveGroup(scopeGroup)) {

			throw new PortletException();
		}

		FragmentPortletConfiguration fragmentPortletConfiguration = null;

		try {
			fragmentPortletConfiguration =
				_configurationProvider.getCompanyConfiguration(
					FragmentPortletConfiguration.class,
					themeDisplay.getCompanyId());
		}
		catch (ConfigurationException configurationException) {
			throw new PortletException(configurationException);
		}

		renderRequest.setAttribute(
			DefaultInputFragmentEntryConfigurationProvider.class.getName(),
			_defaultInputFragmentEntryConfigurationProvider);
		renderRequest.setAttribute(
			FragmentCollectionContributorRegistry.class.getName(),
			_fragmentCollectionContributorRegistry);
		renderRequest.setAttribute(
			FragmentEntryProcessorRegistry.class.getName(),
			_fragmentEntryProcessorRegistry);
		renderRequest.setAttribute(
			FragmentPortletConfiguration.class.getName(),
			fragmentPortletConfiguration);
		renderRequest.setAttribute(
			FragmentRendererController.class.getName(),
			_fragmentRendererController);
		renderRequest.setAttribute(
			FragmentWebKeys.FRAGMENT_COLLECTIONS,
			_fragmentCollectionService.getFragmentCollections(
				themeDisplay.getScopeGroupId()));
		renderRequest.setAttribute(
			FragmentWebKeys.INHERITED_FRAGMENT_COLLECTIONS,
			_getInheritedFragmentCollections(themeDisplay));
		renderRequest.setAttribute(
			FragmentWebKeys.SYSTEM_FRAGMENT_COLLECTIONS,
			_fragmentCollectionService.getFragmentCollections(
				CompanyConstants.SYSTEM));
		renderRequest.setAttribute(ItemSelector.class.getName(), _itemSelector);

		super.doDispatch(renderRequest, renderResponse);
	}

	private Map<String, List<FragmentCollection>>
		_getInheritedFragmentCollections(ThemeDisplay themeDisplay) {

		if (themeDisplay.getScopeGroupId() ==
				themeDisplay.getCompanyGroupId()) {

			return new TreeMap<>();
		}

		Map<String, List<FragmentCollection>> inheritedFragmentCollections =
			new TreeMap<>();

		List<FragmentCollection> fragmentCollections =
			_fragmentCollectionService.getFragmentCollections(
				themeDisplay.getCompanyGroupId());

		if (ListUtil.isNotEmpty(fragmentCollections)) {
			inheritedFragmentCollections.put(
				_language.get(themeDisplay.getLocale(), "global"),
				fragmentCollections);
		}

		return inheritedFragmentCollections;
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private DefaultInputFragmentEntryConfigurationProvider
		_defaultInputFragmentEntryConfigurationProvider;

	@Reference
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Reference
	private FragmentCollectionService _fragmentCollectionService;

	@Reference
	private FragmentEntryProcessorRegistry _fragmentEntryProcessorRegistry;

	@Reference
	private FragmentRendererController _fragmentRendererController;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private Language _language;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

}