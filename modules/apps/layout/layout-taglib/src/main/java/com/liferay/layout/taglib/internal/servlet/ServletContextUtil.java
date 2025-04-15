/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.taglib.internal.servlet;

import com.liferay.fragment.entry.processor.helper.FragmentEntryProcessorHelper;
import com.liferay.fragment.helper.FragmentEntryLinkHelper;
import com.liferay.fragment.renderer.FragmentRendererController;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.list.renderer.InfoListRendererRegistry;
import com.liferay.layout.adaptive.media.LayoutAdaptiveMediaProcessor;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.helper.structure.LayoutStructureRulesHelper;
import com.liferay.layout.list.permission.provider.LayoutListPermissionProviderRegistry;
import com.liferay.layout.list.retriever.LayoutListRetrieverRegistry;
import com.liferay.layout.list.retriever.ListObjectReferenceFactoryRegistry;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.taglib.internal.helper.LayoutClassedModelUsagesHelper;
import com.liferay.layout.util.LayoutsTree;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.segments.SegmentsEntryRetriever;
import com.liferay.segments.context.RequestContextMapper;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.servlet.ServletContext;

/**
 * @author Chema Balsas
 */
public class ServletContextUtil {

	public static FragmentEntryConfigurationParser
		getFragmentEntryConfigurationParser() {

		return _fragmentEntryConfigurationParserSnapshot.get();
	}

	public static FragmentEntryLinkHelper getFragmentEntryLinkHelper() {
		return _fragmentEntryLinkHelperSnapshot.get();
	}

	public static FragmentEntryProcessorHelper
		getFragmentEntryProcessorHelper() {

		return _fragmentEntryProcessorHelperSnapshot.get();
	}

	public static FragmentRendererController getFragmentRendererController() {
		return _fragmentRendererControllerSnapshot.get();
	}

	public static FrontendTokenDefinitionRegistry
		getFrontendTokenDefinitionRegistry() {

		return _frontendTokenDefinitionRegistrySnapshot.get();
	}

	public static InfoItemServiceRegistry getInfoItemServiceRegistry() {
		return _infoItemServiceRegistrySnapshot.get();
	}

	public static InfoListRendererRegistry getInfoListRendererRegistry() {
		return _infoListRendererRegistrySnapshot.get();
	}

	public static LayoutAdaptiveMediaProcessor
		getLayoutAdaptiveMediaProcessor() {

		return _layoutAdaptiveMediaProcessorSnapshot.get();
	}

	public static LayoutClassedModelUsagesHelper
		getLayoutClassedModelUsagesHelper() {

		return _layoutClassedModelUsagesHelperSnapshot.get();
	}

	public static LayoutDisplayPageProviderRegistry
		getLayoutDisplayPageProviderRegistry() {

		return _layoutDisplayPageProviderRegistrySnapshot.get();
	}

	public static LayoutListPermissionProviderRegistry
		getLayoutListPermissionProviderRegistry() {

		return _layoutListPermissionProviderRegistrySnapshot.get();
	}

	public static LayoutListRetrieverRegistry getLayoutListRetrieverRegistry() {
		return _layoutListRetrieverRegistrySnapshot.get();
	}

	public static LayoutsTree getLayoutsTree() {
		return _layoutsTreeSnapshot.get();
	}

	public static LayoutStructureProvider getLayoutStructureHelper() {
		return _layoutStructureProviderSnapshot.get();
	}

	public static LayoutStructureRulesHelper getLayoutStructureRulesHelper() {
		return _layoutStructureRulesHelperSnapshot.get();
	}

	public static ListObjectReferenceFactoryRegistry
		getListObjectReferenceFactoryRegistry() {

		return _listObjectReferenceFactoryRegistrySnapshot.get();
	}

	public static RequestContextMapper getRequestContextMapper() {
		return _requestContextMapperSnapshot.get();
	}

	public static SegmentsEntryRetriever getSegmentsEntryRetriever() {
		return _segmentsEntryRetrieverSnapshot.get();
	}

	public static SegmentsExperienceLocalService
		getSegmentsExperienceLocalService() {

		return _segmentsExperienceLocalServiceSnapshot.get();
	}

	public static ServletContext getServletContext() {
		return _servletContextSnapshot.get();
	}

	private static final Snapshot<FragmentEntryConfigurationParser>
		_fragmentEntryConfigurationParserSnapshot = new Snapshot<>(
			ServletContextUtil.class, FragmentEntryConfigurationParser.class);
	private static final Snapshot<FragmentEntryLinkHelper>
		_fragmentEntryLinkHelperSnapshot = new Snapshot<>(
			ServletContextUtil.class, FragmentEntryLinkHelper.class);
	private static final Snapshot<FragmentEntryProcessorHelper>
		_fragmentEntryProcessorHelperSnapshot = new Snapshot<>(
			ServletContextUtil.class, FragmentEntryProcessorHelper.class);
	private static final Snapshot<FragmentRendererController>
		_fragmentRendererControllerSnapshot = new Snapshot<>(
			ServletContextUtil.class, FragmentRendererController.class);
	private static final Snapshot<FrontendTokenDefinitionRegistry>
		_frontendTokenDefinitionRegistrySnapshot = new Snapshot<>(
			ServletContextUtil.class, FrontendTokenDefinitionRegistry.class);
	private static final Snapshot<InfoItemServiceRegistry>
		_infoItemServiceRegistrySnapshot = new Snapshot<>(
			ServletContextUtil.class, InfoItemServiceRegistry.class);
	private static final Snapshot<InfoListRendererRegistry>
		_infoListRendererRegistrySnapshot = new Snapshot<>(
			ServletContextUtil.class, InfoListRendererRegistry.class);
	private static final Snapshot<LayoutAdaptiveMediaProcessor>
		_layoutAdaptiveMediaProcessorSnapshot = new Snapshot<>(
			ServletContextUtil.class, LayoutAdaptiveMediaProcessor.class);
	private static final Snapshot<LayoutClassedModelUsagesHelper>
		_layoutClassedModelUsagesHelperSnapshot = new Snapshot<>(
			ServletContextUtil.class, LayoutClassedModelUsagesHelper.class);
	private static final Snapshot<LayoutDisplayPageProviderRegistry>
		_layoutDisplayPageProviderRegistrySnapshot = new Snapshot<>(
			ServletContextUtil.class, LayoutDisplayPageProviderRegistry.class);
	private static final Snapshot<LayoutListPermissionProviderRegistry>
		_layoutListPermissionProviderRegistrySnapshot = new Snapshot<>(
			ServletContextUtil.class,
			LayoutListPermissionProviderRegistry.class);
	private static final Snapshot<LayoutListRetrieverRegistry>
		_layoutListRetrieverRegistrySnapshot = new Snapshot<>(
			ServletContextUtil.class, LayoutListRetrieverRegistry.class);
	private static final Snapshot<LayoutsTree> _layoutsTreeSnapshot =
		new Snapshot<>(ServletContextUtil.class, LayoutsTree.class);
	private static final Snapshot<LayoutStructureProvider>
		_layoutStructureProviderSnapshot = new Snapshot<>(
			ServletContextUtil.class, LayoutStructureProvider.class);
	private static final Snapshot<LayoutStructureRulesHelper>
		_layoutStructureRulesHelperSnapshot = new Snapshot<>(
			ServletContextUtil.class, LayoutStructureRulesHelper.class);
	private static final Snapshot<ListObjectReferenceFactoryRegistry>
		_listObjectReferenceFactoryRegistrySnapshot = new Snapshot<>(
			ServletContextUtil.class, ListObjectReferenceFactoryRegistry.class);
	private static final Snapshot<RequestContextMapper>
		_requestContextMapperSnapshot = new Snapshot<>(
			ServletContextUtil.class, RequestContextMapper.class);
	private static final Snapshot<SegmentsEntryRetriever>
		_segmentsEntryRetrieverSnapshot = new Snapshot<>(
			ServletContextUtil.class, SegmentsEntryRetriever.class);
	private static final Snapshot<SegmentsExperienceLocalService>
		_segmentsExperienceLocalServiceSnapshot = new Snapshot<>(
			ServletContextUtil.class, SegmentsExperienceLocalService.class);
	private static final Snapshot<ServletContext> _servletContextSnapshot =
		new Snapshot<>(
			ServletContextUtil.class, ServletContext.class,
			"(osgi.web.symbolicname=com.liferay.layout.taglib)");

}