/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.taglib.servlet.taglib.renderer;

import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentWebKeys;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.renderer.FragmentRendererController;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.fragment.util.configuration.FragmentConfigurationField;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.frontend.taglib.clay.servlet.taglib.ButtonTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.ColTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.ContainerTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.PaginationBarTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.RowTag;
import com.liferay.frontend.taglib.servlet.taglib.ComponentTag;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.type.BooleanInfoFieldType;
import com.liferay.info.field.type.InfoFieldType;
import com.liferay.info.field.type.MultiselectInfoFieldType;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.info.item.provider.InfoItemPermissionProvider;
import com.liferay.info.list.renderer.DefaultInfoListRendererContext;
import com.liferay.info.list.renderer.InfoListRenderer;
import com.liferay.info.permission.provider.InfoPermissionProvider;
import com.liferay.info.search.InfoSearchClassMapperRegistryUtil;
import com.liferay.layout.constants.LayoutWebKeys;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.layout.helper.structure.LayoutStructureRulesHelper;
import com.liferay.layout.list.retriever.ListObjectReference;
import com.liferay.layout.responsive.ResponsiveLayoutStructureUtil;
import com.liferay.layout.taglib.constants.LayoutStructureRendererConstants;
import com.liferay.layout.taglib.internal.display.context.RenderCollectionLayoutStructureItemDisplayContext;
import com.liferay.layout.taglib.internal.display.context.RenderLayoutStructureDisplayContext;
import com.liferay.layout.taglib.internal.servlet.ServletContextUtil;
import com.liferay.layout.taglib.internal.util.SegmentsExperienceUtil;
import com.liferay.layout.util.CollectionPaginationUtil;
import com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem;
import com.liferay.layout.util.structure.ColumnLayoutStructureItem;
import com.liferay.layout.util.structure.ContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.DropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.FormRelationshipStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FormStepContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.RowStyledLayoutStructureItem;
import com.liferay.layout.util.structure.collection.EmptyCollectionOptions;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTemplate;
import com.liferay.portal.kernel.model.LayoutTemplateConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutTemplateLocalServiceUtil;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.layoutconfiguration.util.RuntimePageUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Mikel Lorza
 */
public class LayoutStructureRenderer {

	public LayoutStructureRenderer(
		HttpServletRequest httpServletRequest, LayoutStructure layoutStructure,
		String mainItemId, String mode, PageContext pageContext,
		boolean renderActionHandler, boolean showPreview) {

		_httpServletRequest = httpServletRequest;
		_layoutStructure = layoutStructure;
		_pageContext = pageContext;
		_renderActionHandler = renderActionHandler;

		_renderLayoutStructureDisplayContext =
			new RenderLayoutStructureDisplayContext(
				_httpServletRequest, _layoutStructure, mainItemId, mode,
				showPreview);
		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<LayoutStructureItemRenderTime>
		getLayoutStructureItemRenderTimes() {

		return _layoutStructureItemRenderTimes;
	}

	public void render() throws Exception {
		_renderLayoutStructure(
			_renderLayoutStructureDisplayContext.getMainChildrenItemIds());

		if (_renderActionHandler) {
			_renderComponent(
				"infoItemActionComponent",
				_renderLayoutStructureDisplayContext.
					getInfoItemActionComponentContext(),
				"{InfoItemActionHandler} from layout-taglib/render");
		}

		LayoutStructureRulesHelper.LayoutStructureRulesResult
			layoutStructureRulesResult =
				_renderLayoutStructureDisplayContext.
					getLayoutStructureRulesResult();

		if (MapUtil.isNotEmpty(
				layoutStructureRulesResult.getLayoutStructureRuleIdsMap())) {

			_renderComponent(
				"RulesHandlerComponent",
				_renderLayoutStructureDisplayContext.
					getRulesHandlerComponentContext(),
				"{RulesHandler} from layout-taglib/render");
		}
	}

	public class LayoutStructureItemRenderTime {

		public LayoutStructureItemRenderTime(
			LayoutStructureItem layoutStructureItem, long renderTime) {

			_layoutStructureItem = layoutStructureItem;
			_renderTime = renderTime;
		}

		public LayoutStructureItem getLayoutStructureItem() {
			return _layoutStructureItem;
		}

		public long getRenderTime() {
			return _renderTime;
		}

		private final LayoutStructureItem _layoutStructureItem;
		private final long _renderTime;

	}

	private LayoutTypePortlet _getLayoutTypePortlet(
		Layout layout, LayoutTypePortlet layoutTypePortlet, String themeId) {

		String layoutTemplateId = layoutTypePortlet.getLayoutTemplateId();

		if (Validator.isNull(layoutTemplateId)) {
			return layoutTypePortlet;
		}

		LayoutTemplate layoutTemplate =
			LayoutTemplateLocalServiceUtil.getLayoutTemplate(
				layoutTemplateId, false, themeId);

		if (layoutTemplate != null) {
			return layoutTypePortlet;
		}

		layoutTypePortlet.setLayoutTemplateId(
			layout.getUserId(), PropsValues.DEFAULT_LAYOUT_TEMPLATE_ID);

		return layoutTypePortlet;
	}

	private boolean _hasAddPermission(String className) {
		InfoItemServiceRegistry infoItemServiceRegistry =
			ServletContextUtil.getInfoItemServiceRegistry();

		InfoPermissionProvider infoPermissionProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoPermissionProvider.class, className);

		if ((infoPermissionProvider == null) ||
			((_themeDisplay != null) &&
			 infoPermissionProvider.hasAddPermission(
				 _themeDisplay.getScopeGroupId(),
				 _themeDisplay.getPermissionChecker()))) {

			return true;
		}

		return false;
	}

	private boolean _hasPermission(
			String actionId,
			LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider)
		throws Exception {

		InfoItemServiceRegistry infoItemServiceRegistry =
			ServletContextUtil.getInfoItemServiceRegistry();

		InfoItemPermissionProvider infoItemPermissionProvider =
			infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemPermissionProvider.class,
				layoutDisplayPageObjectProvider.getClassName());

		if ((infoItemPermissionProvider == null) ||
			((_themeDisplay != null) &&
			 infoItemPermissionProvider.hasPermission(
				 _themeDisplay.getPermissionChecker(),
				 layoutDisplayPageObjectProvider.getDisplayObject(),
				 actionId))) {

			return true;
		}

		return false;
	}

	private void _renderCollectionStyledLayoutStructureItem(
			InfoForm infoForm,
			CollectionStyledLayoutStructureItem
				collectionStyledLayoutStructureItem)
		throws Exception {

		RenderCollectionLayoutStructureItemDisplayContext
			renderCollectionLayoutStructureItemDisplayContext =
				new RenderCollectionLayoutStructureItemDisplayContext(
					collectionStyledLayoutStructureItem, _httpServletRequest);

		if (!renderCollectionLayoutStructureItemDisplayContext.
				hasViewPermission()) {

			return;
		}

		JspWriter jspWriter = _pageContext.getOut();

		jspWriter.write("<div class=\"");
		jspWriter.write(
			collectionStyledLayoutStructureItem.getUniqueCssClass());
		jspWriter.write(StringPool.SPACE);
		jspWriter.write(collectionStyledLayoutStructureItem.getCssClass());
		jspWriter.write("\" data-layout-structure-item-id=\"");
		jspWriter.write(collectionStyledLayoutStructureItem.getItemId());
		jspWriter.write("\"");

		ListObjectReference listObjectReference =
			renderCollectionLayoutStructureItemDisplayContext.
				getListObjectReference();

		if (listObjectReference != null) {
			jspWriter.write(" data-analytics-targetable-collection=\"");
			jspWriter.write(HtmlUtil.escape(listObjectReference.toString()));
			jspWriter.write("\"");
		}

		jspWriter.write(" id=\"analytics-targetable-collection-");
		jspWriter.write(collectionStyledLayoutStructureItem.getItemId());

		String style = _renderLayoutStructureDisplayContext.getStyle(
			collectionStyledLayoutStructureItem);

		if (Validator.isNotNull(style)) {
			jspWriter.write("\" style=\"");
			jspWriter.write(style);
		}

		jspWriter.write("\">");

		List<String> collectionStyledLayoutStructureItemIds =
			_renderLayoutStructureDisplayContext.
				getCollectionStyledLayoutStructureItemIds();

		collectionStyledLayoutStructureItemIds.add(
			collectionStyledLayoutStructureItem.getItemId());

		List<Object> collection =
			renderCollectionLayoutStructureItemDisplayContext.getCollection();

		if (ListUtil.isEmpty(collection)) {
			_renderEmptyState(
				collectionStyledLayoutStructureItem.getEmptyCollectionOptions(),
				jspWriter);

			jspWriter.write("</div>");

			collectionStyledLayoutStructureItemIds.remove(
				collectionStyledLayoutStructureItemIds.size() - 1);

			return;
		}

		InfoListRenderer<Object> infoListRenderer =
			(InfoListRenderer<Object>)
				renderCollectionLayoutStructureItemDisplayContext.
					getInfoListRenderer();

		if ((infoListRenderer != null) &&
			Objects.equals(
				infoListRenderer.getCollectionItemClassName(),
				listObjectReference.getItemType())) {

			UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

			PipingServletResponse pipingServletResponse =
				new PipingServletResponse(
					(HttpServletResponse)_pageContext.getResponse(),
					unsyncStringWriter);

			DefaultInfoListRendererContext defaultInfoListRendererContext =
				new DefaultInfoListRendererContext(
					_httpServletRequest, pipingServletResponse);

			defaultInfoListRendererContext.setListItemRendererKey(
				collectionStyledLayoutStructureItem.getListItemStyle());
			defaultInfoListRendererContext.setTemplateKey(
				collectionStyledLayoutStructureItem.getTemplateKey());

			infoListRenderer.render(collection, defaultInfoListRendererContext);

			jspWriter.write(unsyncStringWriter.toString());
		}
		else {
			InfoItemServiceRegistry infoItemServiceRegistry =
				ServletContextUtil.getInfoItemServiceRegistry();

			InfoItemDetailsProvider infoItemDetailsProvider =
				infoItemServiceRegistry.getFirstInfoItemService(
					InfoItemDetailsProvider.class,
					InfoSearchClassMapperRegistryUtil.getClassName(
						renderCollectionLayoutStructureItemDisplayContext.
							getCollectionItemType()));

			if (infoItemDetailsProvider == null) {
				_renderEmptyState(
					collectionStyledLayoutStructureItem.
						getEmptyCollectionOptions(),
					jspWriter);

				jspWriter.write("</div>");

				collectionStyledLayoutStructureItemIds.remove(
					collectionStyledLayoutStructureItemIds.size() - 1);

				return;
			}

			_renderCollectionStyledLayoutStructureItem(
				collection, collectionStyledLayoutStructureItem, infoForm,
				infoItemDetailsProvider,
				renderCollectionLayoutStructureItemDisplayContext);
		}

		if (Objects.equals(
				collectionStyledLayoutStructureItem.getPaginationType(),
				CollectionPaginationUtil.PAGINATION_TYPE_NUMERIC)) {

			PaginationBarTag paginationBarTag = new PaginationBarTag();

			paginationBarTag.setActiveDelta(
				renderCollectionLayoutStructureItemDisplayContext.
					getMaxNumberOfItemsPerPage());
			paginationBarTag.setActivePage(
				renderCollectionLayoutStructureItemDisplayContext.
					getActivePage());
			paginationBarTag.setAdditionalProps(
				Collections.singletonMap(
					"collectionId",
					collectionStyledLayoutStructureItem.getItemId()));
			paginationBarTag.setCssClass("pb-2 pt-3");
			paginationBarTag.setPropsTransformer(
				"{NumericCollectionPaginationPropsTransformer} from " +
					"layout-taglib/render");
			paginationBarTag.setShowDeltasDropDown(false);
			paginationBarTag.setTotalItems(
				renderCollectionLayoutStructureItemDisplayContext.
					getTotalNumberOfItems());

			paginationBarTag.doTag(_pageContext);
		}

		if (Objects.equals(
				collectionStyledLayoutStructureItem.getPaginationType(),
				CollectionPaginationUtil.PAGINATION_TYPE_SIMPLE)) {

			jspWriter.write("<div class=\"d-flex flex-grow-1 h-100 ");
			jspWriter.write("justify-content-center py-3\" ");
			jspWriter.write("id=\"paginationButtons_");
			jspWriter.write(collectionStyledLayoutStructureItem.getItemId());
			jspWriter.write("\">");

			ButtonTag previousButtonTag = new ButtonTag();

			previousButtonTag.setCssClass(
				"font-weight-semi-bold mr-3 previous text-secondary");
			previousButtonTag.setDisplayType("unstyled");
			previousButtonTag.setDynamicAttribute(
				StringPool.BLANK, "disabled",
				Objects.equals(
					renderCollectionLayoutStructureItemDisplayContext.
						getActivePage(),
					1));
			previousButtonTag.setId(
				"paginationPreviousButton_" +
					collectionStyledLayoutStructureItem.getItemId());
			previousButtonTag.setLabel(
				LanguageUtil.get(_httpServletRequest, "previous"));

			previousButtonTag.doTag(_pageContext);

			ButtonTag nextButtonTag = new ButtonTag();

			nextButtonTag.setCssClass(
				"font-weight-semi-bold ml-3 next text-secondary");
			nextButtonTag.setDisplayType("unstyled");
			nextButtonTag.setDynamicAttribute(
				StringPool.BLANK, "disabled",
				Objects.equals(
					renderCollectionLayoutStructureItemDisplayContext.
						getActivePage(),
					renderCollectionLayoutStructureItemDisplayContext.
						getNumberOfPages()));
			nextButtonTag.setId(
				"paginationNextButton_" +
					collectionStyledLayoutStructureItem.getItemId());
			nextButtonTag.setLabel(
				LanguageUtil.get(_httpServletRequest, "next"));

			nextButtonTag.doTag(_pageContext);

			jspWriter.write("</div>");

			_renderComponent(
				"paginationComponent" +
					collectionStyledLayoutStructureItem.getItemId(),
				HashMapBuilder.<String, Object>put(
					"activePage",
					renderCollectionLayoutStructureItemDisplayContext.
						getActivePage()
				).put(
					"collectionId",
					collectionStyledLayoutStructureItem.getItemId()
				).build(),
				"{SimpleCollectionPagination} from layout-taglib/render");
		}

		jspWriter.write("</div>");

		collectionStyledLayoutStructureItemIds.remove(
			collectionStyledLayoutStructureItemIds.size() - 1);
	}

	private void _renderCollectionStyledLayoutStructureItem(
			List<Object> collection,
			CollectionStyledLayoutStructureItem
				collectionStyledLayoutStructureItem,
			InfoForm infoForm, InfoItemDetailsProvider infoItemDetailsProvider,
			RenderCollectionLayoutStructureItemDisplayContext
				renderCollectionLayoutStructureItemDisplayContext)
		throws Exception {

		InfoItemReference currentInfoItemReference =
			(InfoItemReference)_httpServletRequest.getAttribute(
				InfoDisplayWebKeys.INFO_ITEM_REFERENCE);
		LayoutDisplayPageProvider<?> currentLayoutDisplayPageProvider =
			(LayoutDisplayPageProvider<?>)_httpServletRequest.getAttribute(
				LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_PROVIDER);

		try {
			_httpServletRequest.setAttribute(
				LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_PROVIDER,
				renderCollectionLayoutStructureItemDisplayContext.
					getCollectionLayoutDisplayPageProvider());

			ContainerTag containerTag = new ContainerTag();

			StringBundler sb = new StringBundler("overflow-hidden px-0");

			if (Objects.equals(
					collectionStyledLayoutStructureItem.getListStyle(),
					"flex-column")) {

				sb.append(" d-flex flex-column");
			}
			else if (Objects.equals(
						collectionStyledLayoutStructureItem.getListStyle(),
						"flex-row")) {

				sb.append(" d-flex flex-row");
			}

			String align = collectionStyledLayoutStructureItem.getAlign();

			if (Validator.isNotNull(align)) {
				sb.append(StringPool.SPACE);
				sb.append(align);
			}

			String flexWrap = collectionStyledLayoutStructureItem.getFlexWrap();

			if (Validator.isNotNull(flexWrap)) {
				sb.append(StringPool.SPACE);
				sb.append(flexWrap);
			}

			String justify = collectionStyledLayoutStructureItem.getJustify();

			if (Validator.isNotNull(justify)) {
				sb.append(StringPool.SPACE);
				sb.append(justify);
			}

			containerTag.setCssClass(sb.toString());

			containerTag.setFluid(true);
			containerTag.setPageContext(_pageContext);

			containerTag.doStartTag();

			RowTag rowTag = new RowTag();

			sb.setIndex(0);

			sb.append("align-items-");
			sb.append(
				collectionStyledLayoutStructureItem.getVerticalAlignment());

			if (!collectionStyledLayoutStructureItem.isGutters()) {
				sb.append(" no-gutters");
			}

			rowTag.setCssClass(sb.toString());

			rowTag.setPageContext(_pageContext);

			rowTag.doStartTag();

			int numberOfItemsToDisplay =
				renderCollectionLayoutStructureItemDisplayContext.
					getNumberOfItemsToDisplay();

			for (int i = 0; i < numberOfItemsToDisplay; i++) {
				if (i >= collection.size()) {
					break;
				}

				InfoItemDetails infoItemDetails =
					infoItemDetailsProvider.getInfoItemDetails(
						collection.get(i));

				_httpServletRequest.setAttribute(
					InfoDisplayWebKeys.INFO_ITEM_REFERENCE,
					infoItemDetails.getInfoItemReference());

				ColTag colTag = new ColTag();

				if (Validator.isNull(
						collectionStyledLayoutStructureItem.getListStyle())) {

					int numberOfColumns =
						collectionStyledLayoutStructureItem.
							getNumberOfColumns();

					colTag.setCssClass(
						ResponsiveLayoutStructureUtil.getColumnCssClass(
							collectionStyledLayoutStructureItem,
							i % numberOfColumns));
				}

				colTag.setPageContext(_pageContext);

				colTag.doStartTag();

				_renderLayoutStructure(
					collectionStyledLayoutStructureItem.getChildrenItemIds(),
					infoForm);

				colTag.doEndTag();
			}

			rowTag.doEndTag();

			containerTag.doEndTag();
		}
		finally {
			_httpServletRequest.setAttribute(
				InfoDisplayWebKeys.INFO_ITEM_REFERENCE,
				currentInfoItemReference);
			_httpServletRequest.setAttribute(
				LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_PROVIDER,
				currentLayoutDisplayPageProvider);
		}
	}

	private void _renderColumnLayoutStructureItem(
			InfoForm infoForm,
			ColumnLayoutStructureItem columnLayoutStructureItem)
		throws Exception {

		RowStyledLayoutStructureItem rowStyledLayoutStructureItem =
			(RowStyledLayoutStructureItem)
				_layoutStructure.getLayoutStructureItem(
					columnLayoutStructureItem.getParentItemId());

		ColTag colTag = new ColTag();

		colTag.setCssClass(
			ResponsiveLayoutStructureUtil.getColumnCssClass(
				columnLayoutStructureItem, rowStyledLayoutStructureItem));
		colTag.setPageContext(_pageContext);

		colTag.doStartTag();

		_renderLayoutStructure(
			columnLayoutStructureItem.getChildrenItemIds(), infoForm);

		colTag.doEndTag();
	}

	private void _renderComponent(
			String componentId, Map<String, Object> context, String module)
		throws Exception {

		ComponentTag componentTag = new ComponentTag();

		componentTag.setComponentId(componentId);
		componentTag.setContext(context);
		componentTag.setModule(module);
		componentTag.setPageContext(_pageContext);
		componentTag.setServletContext(ServletContextUtil.getServletContext());

		componentTag.doStartTag();

		componentTag.doEndTag();
	}

	private void _renderContainerStyledLayoutStructureItem(
			InfoForm infoForm,
			ContainerStyledLayoutStructureItem
				containerStyledLayoutStructureItem)
		throws Exception {

		JspWriter jspWriter = _pageContext.getOut();

		String containerLinkHref =
			_renderLayoutStructureDisplayContext.getContainerLinkHref(
				containerStyledLayoutStructureItem);

		if (Validator.isNotNull(containerLinkHref)) {
			jspWriter.write("<a href=\"");
			jspWriter.write(HtmlUtil.escapeAttribute(containerLinkHref));
			jspWriter.write("\"style=\"color: inherit; text-decoration: ");
			jspWriter.write("none;\" target=\"");
			jspWriter.write(
				_renderLayoutStructureDisplayContext.getContainerLinkTarget(
					containerStyledLayoutStructureItem));
			jspWriter.write("\">");
		}

		String htmlTag = containerStyledLayoutStructureItem.getHtmlTag();

		if (Validator.isNull(htmlTag)) {
			htmlTag = "div";
		}

		jspWriter.write(StringPool.LESS_THAN);
		jspWriter.write(htmlTag);
		jspWriter.write(" class=\"");
		jspWriter.write(containerStyledLayoutStructureItem.getUniqueCssClass());
		jspWriter.write(StringPool.SPACE);
		jspWriter.write(containerStyledLayoutStructureItem.getCssClass());
		jspWriter.write(StringPool.SPACE);
		jspWriter.write(
			containerStyledLayoutStructureItem.getStyledCssClasses());

		String colorCssClasses =
			_renderLayoutStructureDisplayContext.getColorCssClasses(
				containerStyledLayoutStructureItem);

		if (Validator.isNotNull(colorCssClasses)) {
			jspWriter.write(StringPool.SPACE);
			jspWriter.write(colorCssClasses);
		}

		if (Objects.equals(
				containerStyledLayoutStructureItem.getWidthType(), "fixed")) {

			jspWriter.write(" container-fluid container-fluid-max-xl");
		}

		if (!Objects.equals(
				containerStyledLayoutStructureItem.getDisplay(), "none")) {

			if (Objects.equals(
					containerStyledLayoutStructureItem.getContentDisplay(),
					"flex-column")) {

				jspWriter.write(" d-flex flex-column");
			}
			else if (Objects.equals(
						containerStyledLayoutStructureItem.getContentDisplay(),
						"flex-row")) {

				jspWriter.write(" d-flex flex-row");
			}

			String align = containerStyledLayoutStructureItem.getAlign();

			if (Validator.isNotNull(align)) {
				jspWriter.append(StringPool.SPACE);
				jspWriter.append(align);
			}

			String flexWrap = containerStyledLayoutStructureItem.getFlexWrap();

			if (Validator.isNotNull(flexWrap)) {
				jspWriter.append(StringPool.SPACE);
				jspWriter.append(flexWrap);
			}

			String justify = containerStyledLayoutStructureItem.getJustify();

			if (Validator.isNotNull(justify)) {
				jspWriter.append(StringPool.SPACE);
				jspWriter.append(justify);
			}
		}

		jspWriter.write("\" data-layout-structure-item-id=\"");
		jspWriter.write(containerStyledLayoutStructureItem.getItemId());

		StringBundler sb = new StringBundler(4);

		if (Validator.isNotNull(
				containerStyledLayoutStructureItem.getContentVisibility())) {

			sb.append("content-visibility:");
			sb.append(
				containerStyledLayoutStructureItem.getContentVisibility());
			sb.append(StringPool.SEMICOLON);
		}

		sb.append(
			_renderLayoutStructureDisplayContext.getStyle(
				containerStyledLayoutStructureItem));

		if (sb.length() > 0) {
			jspWriter.write("\" style=\"");
			jspWriter.write(sb.toString());
		}

		jspWriter.write("\">");

		_renderLayoutStructure(
			containerStyledLayoutStructureItem.getChildrenItemIds(), infoForm);

		jspWriter.write("</");
		jspWriter.write(htmlTag);
		jspWriter.write(StringPool.GREATER_THAN);

		if (Validator.isNotNull(containerLinkHref)) {
			jspWriter.write("</a>");
		}
	}

	private void _renderDropZoneLayoutStructureItem(
			InfoForm infoForm, LayoutStructureItem layoutStructureItem)
		throws Exception {

		Layout layout = _themeDisplay.getLayout();

		LayoutTypePortlet layoutTypePortlet =
			_themeDisplay.getLayoutTypePortlet();

		String ppid = ParamUtil.getString(_httpServletRequest, "p_p_id");

		if (layoutTypePortlet.hasStateMax() && Validator.isNotNull(ppid)) {
			String templateContent = LayoutTemplateLocalServiceUtil.getContent(
				"max", true, _themeDisplay.getThemeId());

			if (Validator.isNotNull(templateContent)) {
				HttpServletRequest originalHttpServletRequest =
					(HttpServletRequest)_httpServletRequest.getAttribute(
						"ORIGINAL_HTTP_SERVLET_REQUEST");

				if (originalHttpServletRequest == null) {
					originalHttpServletRequest = _httpServletRequest;
				}

				List<String> ppids = StringUtil.split(
					layoutTypePortlet.getStateMax());
				String templateId =
					_themeDisplay.getThemeId() +
						LayoutTemplateConstants.STANDARD_SEPARATOR + "max";

				RuntimePageUtil.processTemplate(
					originalHttpServletRequest,
					(HttpServletResponse)_pageContext.getResponse(),
					ppids.get(0), templateId, templateContent,
					LayoutTemplateLocalServiceUtil.getLangType(
						"max", true, _themeDisplay.getThemeId()));
			}
		}
		else if (Objects.equals(
					layout.getType(), LayoutConstants.TYPE_PORTLET)) {

			layoutTypePortlet = _getLayoutTypePortlet(
				layout, _themeDisplay.getLayoutTypePortlet(),
				_themeDisplay.getThemeId());

			String layoutTemplateId = layoutTypePortlet.getLayoutTemplateId();

			if (Validator.isNull(layoutTemplateId)) {
				layoutTemplateId = PropsValues.DEFAULT_LAYOUT_TEMPLATE_ID;
			}

			LayoutTemplate layoutTemplate =
				LayoutTemplateLocalServiceUtil.getLayoutTemplate(
					layoutTemplateId, false, _themeDisplay.getThemeId());

			String themeId = _themeDisplay.getThemeId();

			if (layoutTemplate != null) {
				themeId = layoutTemplate.getThemeId();
			}

			String templateContent = LayoutTemplateLocalServiceUtil.getContent(
				layoutTypePortlet.getLayoutTemplateId(), false,
				_themeDisplay.getThemeId());

			if (Validator.isNotNull(templateContent)) {
				HttpServletRequest originalHttpServletRequest =
					(HttpServletRequest)_httpServletRequest.getAttribute(
						"ORIGINAL_HTTP_SERVLET_REQUEST");

				String templateId =
					themeId + LayoutTemplateConstants.CUSTOM_SEPARATOR +
						layoutTypePortlet.getLayoutTemplateId();

				RuntimePageUtil.processTemplate(
					originalHttpServletRequest,
					(HttpServletResponse)_pageContext.getResponse(), null,
					templateId, templateContent,
					LayoutTemplateLocalServiceUtil.getLangType(
						layoutTypePortlet.getLayoutTemplateId(), false,
						_themeDisplay.getThemeId()));
			}
		}
		else {
			JspWriter jspWriter = _pageContext.getOut();

			if (Objects.equals(
					_renderLayoutStructureDisplayContext.getLayoutMode(),
					Constants.VIEW)) {

				jspWriter.write("<div class=\"layout-content portlet-layout\"");
				jspWriter.write("id=\"main-content\" role=\"main\">");
			}

			_renderLayoutStructure(
				layoutStructureItem.getChildrenItemIds(), infoForm);

			if (Objects.equals(
					_renderLayoutStructureDisplayContext.getLayoutMode(),
					Constants.VIEW)) {

				jspWriter.write("</div>");
			}
		}
	}

	private void _renderEmptyState(
			EmptyCollectionOptions emptyCollectionOptions, JspWriter jspWriter)
		throws Exception {

		if ((emptyCollectionOptions != null) &&
			!GetterUtil.getBoolean(
				emptyCollectionOptions.isDisplayMessage(), true)) {

			return;
		}

		jspWriter.write("<div class=\"c-empty-state\">");
		jspWriter.write("<div class=\"c-empty-state-text\">");

		String message = LanguageUtil.get(
			_httpServletRequest, "no-results-found");

		if ((emptyCollectionOptions != null) &&
			(emptyCollectionOptions.getMessage() != null)) {

			Map<String, String> messageMap =
				emptyCollectionOptions.getMessage();

			String customMessage = messageMap.get(
				String.valueOf(_themeDisplay.getLocale()));

			if (customMessage != null) {
				message = customMessage;
			}
		}

		jspWriter.write(message);

		jspWriter.write("</div></div>");
	}

	private void _renderFormRelationshipStyledLayoutStructureItem(
			InfoForm infoForm,
			FormRelationshipStyledLayoutStructureItem
				formRelationshipStyledLayoutStructureItem)
		throws Exception {

		JspWriter jspWriter = _pageContext.getOut();

		jspWriter.write("<div class=\"");
		jspWriter.write(
			formRelationshipStyledLayoutStructureItem.getUniqueCssClass());
		jspWriter.write(StringPool.SPACE);
		jspWriter.write(
			formRelationshipStyledLayoutStructureItem.getCssClass());
		jspWriter.write("\" data-layout-structure-item-id=\"");

		String itemId = formRelationshipStyledLayoutStructureItem.getItemId();

		jspWriter.write(itemId);

		jspWriter.write("\"");

		String style = _renderLayoutStructureDisplayContext.getStyle(
			formRelationshipStyledLayoutStructureItem);

		if (Validator.isNotNull(style)) {
			jspWriter.write("\" style=\"");
			jspWriter.write(style);
		}

		jspWriter.write("\">");

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			(LayoutDisplayPageObjectProvider<?>)
				_httpServletRequest.getAttribute(
					LayoutDisplayPageWebKeys.
						LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER);

		LayoutDisplayPageObjectProvider<?>
			currentRelatedLayoutDisplayPageObjectProvider =
				(LayoutDisplayPageObjectProvider<?>)
					_httpServletRequest.getAttribute(
						LayoutStructureRendererConstants.
							LAYOUT_RELATED_ITEM_DISPLAY_PAGE_OBJECT_PROVIDER);

		String currentParentItemExternalReferenceCode =
			(String)_httpServletRequest.getAttribute(
				LayoutStructureRendererConstants.
					LAYOUT_PARENT_ITEM_EXTERNAL_REFERENCE_CODE);
		String currentRelatedItemExternalReferenceCode =
			(String)_httpServletRequest.getAttribute(
				LayoutStructureRendererConstants.
					LAYOUT_RELATED_ITEM_EXTERNAL_REFERENCE_CODE);

		try {
			String parentItemExternalReferenceCode = GetterUtil.getString(
				GetterUtil.getString(
					_httpServletRequest.getAttribute(
						LayoutStructureRendererConstants.
							LAYOUT_PARENT_ITEM_EXTERNAL_REFERENCE_CODE +
								itemId),
					currentRelatedItemExternalReferenceCode),
				LayoutStructureRendererConstants.
					LAYOUT_DEFAULT_EXTERNAL_REFERENCE_CODE + 0);
			String relatedItemExternalReferenceCode = GetterUtil.getString(
				_httpServletRequest.getAttribute(
					LayoutStructureRendererConstants.
						LAYOUT_RELATED_ITEM_EXTERNAL_REFERENCE_CODE + itemId),
				LayoutStructureRendererConstants.
					LAYOUT_DEFAULT_EXTERNAL_REFERENCE_CODE + 0);

			_httpServletRequest.setAttribute(
				LayoutStructureRendererConstants.
					LAYOUT_PARENT_ITEM_EXTERNAL_REFERENCE_CODE,
				parentItemExternalReferenceCode);
			_httpServletRequest.setAttribute(
				LayoutStructureRendererConstants.
					LAYOUT_RELATED_ITEM_EXTERNAL_REFERENCE_CODE,
				relatedItemExternalReferenceCode);

			String formRelationshipStyledLayoutStructureItemContentId =
				PortalUUIDUtil.generate();

			if (layoutDisplayPageObjectProvider == null) {
				_renderFormRelationshipStyledLayoutStructureItemContent(
					formRelationshipStyledLayoutStructureItem,
					formRelationshipStyledLayoutStructureItemContentId,
					infoForm, null);
			}
			else {
				List<? extends LayoutDisplayPageObjectProvider<?>>
					relatedLayoutDisplayPageObjectProviders = null;

				if (currentRelatedLayoutDisplayPageObjectProvider != null) {
					relatedLayoutDisplayPageObjectProviders =
						currentRelatedLayoutDisplayPageObjectProvider.
							getRelatedLayoutDisplayPageObjectProviders(
								formRelationshipStyledLayoutStructureItem.
									getContentType());
				}
				else {
					relatedLayoutDisplayPageObjectProviders =
						layoutDisplayPageObjectProvider.
							getRelatedLayoutDisplayPageObjectProviders(
								formRelationshipStyledLayoutStructureItem.
									getContentType());
				}

				if (ListUtil.isEmpty(relatedLayoutDisplayPageObjectProviders)) {
					_renderFormRelationshipStyledLayoutStructureItemContent(
						formRelationshipStyledLayoutStructureItem,
						formRelationshipStyledLayoutStructureItemContentId,
						infoForm, null);
				}
				else {
					for (LayoutDisplayPageObjectProvider<?>
							relatedLayoutDisplayPageObjectProvider :
								relatedLayoutDisplayPageObjectProviders) {

						_httpServletRequest.setAttribute(
							LayoutStructureRendererConstants.
								LAYOUT_PARENT_ITEM_EXTERNAL_REFERENCE_CODE,
							relatedLayoutDisplayPageObjectProvider.
								getParentExternalReferenceCode());
						_httpServletRequest.setAttribute(
							LayoutStructureRendererConstants.
								LAYOUT_RELATED_ITEM_DISPLAY_PAGE_OBJECT_PROVIDER,
							relatedLayoutDisplayPageObjectProvider);
						_httpServletRequest.setAttribute(
							LayoutStructureRendererConstants.
								LAYOUT_RELATED_ITEM_EXTERNAL_REFERENCE_CODE,
							relatedLayoutDisplayPageObjectProvider.
								getExternalReferenceCode());

						_renderFormRelationshipStyledLayoutStructureItemContent(
							formRelationshipStyledLayoutStructureItem,
							formRelationshipStyledLayoutStructureItemContentId,
							infoForm, relatedLayoutDisplayPageObjectProvider);
					}
				}
			}

			_renderReactComponent(
				"{FormRelationshipAddButton} from layout-taglib/render",
				HashMapBuilder.<String, Object>put(
					"contentId",
					formRelationshipStyledLayoutStructureItemContentId
				).put(
					"itemId",
					formRelationshipStyledLayoutStructureItem.getItemId()
				).put(
					"label",
					formRelationshipStyledLayoutStructureItem.
						getButtonLabelJSONObject()
				).put(
					"renderURL",
					HttpComponentsUtil.addParameters(
						StringBundler.concat(
							_themeDisplay.getPortalURL(),
							_themeDisplay.getPathMain(), "/portal",
							"/render_form_relationship_layout_structure_item"),
						"formRelationshipLayoutStructureItemId",
						formRelationshipStyledLayoutStructureItem.getItemId(),
						"p_l_id", _themeDisplay.getPlid(),
						"parentItemExternalReferenceCode",
						parentItemExternalReferenceCode, "segmentsExperienceId",
						SegmentsExperienceUtil.getSegmentsExperienceId(
							_httpServletRequest))
				).build());
		}
		finally {
			_httpServletRequest.setAttribute(
				LayoutStructureRendererConstants.
					LAYOUT_PARENT_ITEM_EXTERNAL_REFERENCE_CODE,
				currentParentItemExternalReferenceCode);
			_httpServletRequest.setAttribute(
				LayoutStructureRendererConstants.
					LAYOUT_RELATED_ITEM_DISPLAY_PAGE_OBJECT_PROVIDER,
				currentRelatedLayoutDisplayPageObjectProvider);
			_httpServletRequest.setAttribute(
				LayoutStructureRendererConstants.
					LAYOUT_RELATED_ITEM_EXTERNAL_REFERENCE_CODE,
				currentRelatedItemExternalReferenceCode);
		}

		jspWriter.write("</div>");
	}

	private void _renderFormRelationshipStyledLayoutStructureItemContent(
			FormRelationshipStyledLayoutStructureItem
				formRelationshipStyledLayoutStructureItem,
			String formRelationshipStyledLayoutStructureItemContentId,
			InfoForm infoForm,
			LayoutDisplayPageObjectProvider<?>
				relatedLayoutDisplayPageObjectProvider)
		throws Exception {

		JspWriter jspWriter = _pageContext.getOut();

		jspWriter.write("<div data-form-relationship-");
		jspWriter.write("structure-item-content-id=\"");
		jspWriter.write(formRelationshipStyledLayoutStructureItemContentId);
		jspWriter.write("\"><div class=\"d-flex justify-content-end ");
		jspWriter.write("lfr-form-relationship-remove-button \">");

		ButtonTag removeButtonTag = new ButtonTag();

		removeButtonTag.setBorderless(true);
		removeButtonTag.setCssClass("d-none lfr-portal-tooltip mt-2");
		removeButtonTag.setDisplayType("secondary");

		if (relatedLayoutDisplayPageObjectProvider != null) {
			removeButtonTag.setDynamicAttribute(
				StringPool.BLANK, "data-classname",
				relatedLayoutDisplayPageObjectProvider.getClassName());

			removeButtonTag.setDynamicAttribute(
				StringPool.BLANK, "data-external-reference-code",
				relatedLayoutDisplayPageObjectProvider.
					getExternalReferenceCode());
		}

		removeButtonTag.setDynamicAttribute(
			StringPool.BLANK, "title",
			LanguageUtil.get(_httpServletRequest, "remove"));

		removeButtonTag.setIcon("times-circle");
		removeButtonTag.setSmall(true);

		removeButtonTag.doTag(_pageContext);

		jspWriter.write("</div>");

		_renderLayoutStructure(
			formRelationshipStyledLayoutStructureItem.getChildrenItemIds(),
			infoForm);

		jspWriter.write("</div>");
	}

	private void _renderFormStepContainerStyledLayoutStructureItem(
			InfoForm infoForm,
			FormStepContainerStyledLayoutStructureItem
				formStepContainerStyledLayoutStructureItem)
		throws Exception {

		JspWriter jspWriter = _pageContext.getOut();

		jspWriter.write("<div class=\"");
		jspWriter.write(
			formStepContainerStyledLayoutStructureItem.getUniqueCssClass());
		jspWriter.write(StringPool.SPACE);
		jspWriter.write(
			formStepContainerStyledLayoutStructureItem.getCssClass());
		jspWriter.write(StringPool.SPACE);
		jspWriter.write(
			formStepContainerStyledLayoutStructureItem.getStyledCssClasses());
		jspWriter.write("\" data-layout-structure-item-id=\"");
		jspWriter.write(formStepContainerStyledLayoutStructureItem.getItemId());

		String style = _renderLayoutStructureDisplayContext.getStyle(
			formStepContainerStyledLayoutStructureItem);

		if (Validator.isNotNull(style)) {
			jspWriter.write("\" style=\"");
			jspWriter.write(style);
		}

		jspWriter.write("\">");

		List<String> childrenItemIds =
			formStepContainerStyledLayoutStructureItem.getChildrenItemIds();

		for (int i = 0; i < childrenItemIds.size(); i++) {
			jspWriter.write("<div");

			if (i != 0) {
				jspWriter.write(" class=\"d-none\"");
			}

			jspWriter.write(" data-step-index=\"");
			jspWriter.write(String.valueOf(i));
			jspWriter.write(StringPool.QUOTE);

			jspWriter.write(StringPool.GREATER_THAN);

			LayoutStructureItem layoutStructureItem =
				_layoutStructure.getLayoutStructureItem(childrenItemIds.get(i));

			_renderLayoutStructure(
				layoutStructureItem.getChildrenItemIds(), infoForm);

			jspWriter.write("</div>");
		}

		jspWriter.write("</div>");

		_renderComponent(
			"FormStepComponent" +
				formStepContainerStyledLayoutStructureItem.getItemId(),
			HashMapBuilder.<String, Object>put(
				"formId",
				formStepContainerStyledLayoutStructureItem.getParentItemId()
			).build(),
			"{FormStepHandler} from layout-taglib/render");
	}

	private void _renderFormStyledLayoutStructureItem(
			InfoForm infoForm,
			FormStyledLayoutStructureItem formStyledLayoutStructureItem)
		throws Exception {

		String className = formStyledLayoutStructureItem.getClassName();

		if (Validator.isNull(className) || (infoForm == null)) {
			return;
		}

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			(LayoutDisplayPageObjectProvider<?>)
				_httpServletRequest.getAttribute(
					LayoutDisplayPageWebKeys.
						LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER);

		if ((layoutDisplayPageObjectProvider == null) &&
			!_hasAddPermission(className)) {

			return;
		}

		if ((layoutDisplayPageObjectProvider != null) &&
			!_hasPermission(ActionKeys.VIEW, layoutDisplayPageObjectProvider)) {

			return;
		}

		boolean readOnly = false;

		if ((layoutDisplayPageObjectProvider != null) &&
			!_hasPermission(
				ActionKeys.UPDATE, layoutDisplayPageObjectProvider)) {

			readOnly = true;
		}

		JspWriter jspWriter = _pageContext.getOut();

		jspWriter.write("<form action=\"");
		jspWriter.write(
			_renderLayoutStructureDisplayContext.getEditInfoItemActionURL());
		jspWriter.write("\" class=\"");
		jspWriter.write(formStyledLayoutStructureItem.getUniqueCssClass());
		jspWriter.write(StringPool.SPACE);
		jspWriter.write(formStyledLayoutStructureItem.getCssClass());
		jspWriter.write(StringPool.SPACE);
		jspWriter.write(formStyledLayoutStructureItem.getStyledCssClasses());

		if (Objects.equals(
				formStyledLayoutStructureItem.getWidthType(), "fixed")) {

			jspWriter.write(" container-fluid container-fluid-max-xl");
		}

		if (!Objects.equals(
				formStyledLayoutStructureItem.getDisplay(), "none")) {

			if (Objects.equals(
					formStyledLayoutStructureItem.getContentDisplay(),
					"flex-column")) {

				jspWriter.write(" d-flex flex-column");
			}
			else if (Objects.equals(
						formStyledLayoutStructureItem.getContentDisplay(),
						"flex-row")) {

				jspWriter.write(" d-flex flex-row");
			}

			String align = formStyledLayoutStructureItem.getAlign();

			if (Validator.isNotNull(align)) {
				jspWriter.append(StringPool.SPACE);
				jspWriter.append(align);
			}

			String flexWrap = formStyledLayoutStructureItem.getFlexWrap();

			if (Validator.isNotNull(flexWrap)) {
				jspWriter.append(StringPool.SPACE);
				jspWriter.append(flexWrap);
			}

			String justify = formStyledLayoutStructureItem.getJustify();

			if (Validator.isNotNull(justify)) {
				jspWriter.append(StringPool.SPACE);
				jspWriter.append(justify);
			}
		}

		jspWriter.write("\" data-layout-structure-item-id=\"");
		jspWriter.write(formStyledLayoutStructureItem.getItemId());
		jspWriter.write("\" enctype=\"multipart/form-data\" id=\"");
		jspWriter.write(formStyledLayoutStructureItem.getUniqueCssClass());
		jspWriter.write("\" method=\"POST");

		String style = _renderLayoutStructureDisplayContext.getStyle(
			formStyledLayoutStructureItem);

		if (Validator.isNotNull(style)) {
			jspWriter.write("\" style=\"");
			jspWriter.write(style);
		}

		jspWriter.write("\">");

		String redirect =
			_renderLayoutStructureDisplayContext.
				getFormStyledLayoutStructureItemRedirect(
					formStyledLayoutStructureItem);

		if (Validator.isNotNull(redirect)) {
			jspWriter.write(
				"<input name=\"redirect\" type=\"hidden\" value=\"");
			jspWriter.write(redirect);
			jspWriter.write("\">");
		}

		jspWriter.write("<input name=\"backURL\" type=\"hidden\" value=\"");
		jspWriter.write(_themeDisplay.getURLCurrent());
		jspWriter.write(
			"\"><input name=\"classNameId\" type=\"hidden\" value=\"");
		jspWriter.write(
			String.valueOf(formStyledLayoutStructureItem.getClassNameId()));
		jspWriter.write(
			"\"><input name=\"classTypeId\" type=\"hidden\" value=\"");
		jspWriter.write(
			String.valueOf(formStyledLayoutStructureItem.getClassTypeId()));

		if ((layoutDisplayPageObjectProvider != null) &&
			(layoutDisplayPageObjectProvider.getClassNameId() ==
				formStyledLayoutStructureItem.getClassNameId())) {

			jspWriter.write(
				"\"><input name=\"classPK\" type=\"hidden\" value=\"");
			jspWriter.write(
				String.valueOf(layoutDisplayPageObjectProvider.getClassPK()));

			String externalReferenceCode =
				layoutDisplayPageObjectProvider.getExternalReferenceCode();

			if (Validator.isNotNull(externalReferenceCode)) {
				jspWriter.write(
					"\"><input name=\"externalReferenceCode\" type=\"hidden\"");
				jspWriter.write(" value=\"");
				jspWriter.write(externalReferenceCode);
			}

			String scopeExternalReferenceCode =
				layoutDisplayPageObjectProvider.getScopeExternalReferenceCode(
					_themeDisplay.getScopeGroupId());

			if (Validator.isNotNull(scopeExternalReferenceCode)) {
				jspWriter.write(
					"\"><input name=\"scopeExternalReferenceCode\"");
				jspWriter.write(" type=\"hidden\" value=\"");
				jspWriter.write(scopeExternalReferenceCode);
			}
		}

		jspWriter.write(
			"\"><input name=\"displayPage\" type=\"hidden\" value=\"");
		jspWriter.write(
			_renderLayoutStructureDisplayContext.
				getFormStyledLayoutStructureItemSuccessMessageDisplayPage(
					formStyledLayoutStructureItem));
		jspWriter.write(
			"\"><input name=\"formItemId\" type=\"hidden\" value=\"");
		jspWriter.write(formStyledLayoutStructureItem.getItemId());
		jspWriter.write("\"><input name=\"groupId\" type=\"hidden\" value=\"");
		jspWriter.write(String.valueOf(_themeDisplay.getScopeGroupId()));
		jspWriter.write(
			"\"><input name=\"notificationText\" type=\"hidden\" value=\"");
		jspWriter.write(
			HtmlUtil.escape(
				_renderLayoutStructureDisplayContext.getNotificationText(
					formStyledLayoutStructureItem)));
		jspWriter.write("\"><input name=\"p_l_id\" type=\"hidden\" value=\"");
		jspWriter.write(String.valueOf(_themeDisplay.getPlid()));
		jspWriter.write("\"><input name=\"p_l_mode\" type=\"hidden\" value=\"");
		jspWriter.write(
			ParamUtil.getString(
				PortalUtil.getOriginalServletRequest(_httpServletRequest),
				"p_l_mode", Constants.VIEW));
		jspWriter.write("\"><input name=\"plid\" type=\"hidden\" value=\"");
		jspWriter.write(String.valueOf(_themeDisplay.getPlid()));
		jspWriter.write(
			"\"><input name=\"segmentsExperienceId\" type=\"hidden\" value=\"");
		jspWriter.write(
			String.valueOf(
				SegmentsExperienceUtil.getSegmentsExperienceId(
					_httpServletRequest)));
		jspWriter.write("\">");

		if (SessionErrors.contains(
				_httpServletRequest,
				formStyledLayoutStructureItem.getItemId())) {

			jspWriter.write("<div class=\"alert alert-danger\">");
			jspWriter.write(
				_renderLayoutStructureDisplayContext.getErrorMessage(
					formStyledLayoutStructureItem, infoForm));
			jspWriter.write("</div>");

			SessionErrors.remove(
				_httpServletRequest, formStyledLayoutStructureItem.getItemId());
		}

		Map<String, String> infoFormParameterMap =
			(Map<String, String>)SessionMessages.get(
				_httpServletRequest,
				"infoFormParameterMap" +
					formStyledLayoutStructureItem.getItemId());

		SessionMessages.add(
			_httpServletRequest, "infoFormParameterMap", infoFormParameterMap);

		SessionMessages.remove(
			_httpServletRequest,
			"infoFormParameterMap" + formStyledLayoutStructureItem.getItemId());

		if (readOnly) {
			jspWriter.write("<fieldset disabled=\"disabled\">");
		}

		_renderLayoutStructure(
			formStyledLayoutStructureItem.getChildrenItemIds(), infoForm);

		SessionMessages.remove(_httpServletRequest, "infoFormParameterMap");

		if (readOnly) {
			jspWriter.write("</fieldset>");
		}

		jspWriter.write("</form>");
	}

	private void _renderFormStyledLayoutStructureItemSuccessMessage(
			FormStyledLayoutStructureItem formStyledLayoutStructureItem)
		throws Exception {

		JspWriter jspWriter = _pageContext.getOut();

		jspWriter.write("<div class=\"bg-white font-weight-semi-bold ");
		jspWriter.write("p-5 text-3 text-center text-secondary\">");
		jspWriter.write(
			_renderLayoutStructureDisplayContext.getSuccessMessage(
				formStyledLayoutStructureItem));
		jspWriter.write("</div>");

		SessionMessages.remove(
			_httpServletRequest, formStyledLayoutStructureItem.getItemId());
	}

	private void _renderFragmentStyledLayoutStructureItem(
			InfoForm infoForm,
			FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem)
		throws Exception {

		JspWriter jspWriter = _pageContext.getOut();

		Layout layout = _themeDisplay.getLayout();

		if (Objects.equals(layout.getType(), LayoutConstants.TYPE_PORTLET)) {
			jspWriter.write("<div class=\"master-layout-fragment\">");
		}

		if (fragmentStyledLayoutStructureItem.getFragmentEntryLinkId() > 0) {
			FragmentEntryLink fragmentEntryLink =
				FragmentEntryLinkLocalServiceUtil.fetchFragmentEntryLink(
					fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

			if (fragmentEntryLink != null) {
				DefaultFragmentRendererContext defaultFragmentRendererContext =
					_renderLayoutStructureDisplayContext.
						getDefaultFragmentRendererContext(
							fragmentEntryLink, infoForm,
							fragmentStyledLayoutStructureItem.getItemId());

				Set<String> disabledItemIds =
					_renderLayoutStructureDisplayContext.getDisabledItemIds();
				Set<String> enabledItemIds =
					_renderLayoutStructureDisplayContext.getEnabledItemIds();

				if (disabledItemIds.contains(
						fragmentStyledLayoutStructureItem.getItemId())) {

					defaultFragmentRendererContext.setAttribute(
						"disabled", Boolean.TRUE);
				}
				else if (enabledItemIds.contains(
							fragmentStyledLayoutStructureItem.getItemId())) {

					defaultFragmentRendererContext.setAttribute(
						"enabled", Boolean.TRUE);
				}

				FragmentRendererController fragmentRendererController =
					ServletContextUtil.getFragmentRendererController();

				HttpServletResponse httpServletResponse =
					(HttpServletResponse)_pageContext.getResponse();

				// LPS-164462 Call render before getting attribute value

				String html = fragmentRendererController.render(
					defaultFragmentRendererContext, _httpServletRequest,
					httpServletResponse);

				Map<String, String> dataAttributes = new HashMap<>();

				if ((infoForm != null) &&
					Objects.equals(
						fragmentEntryLink.getType(),
						FragmentConstants.TYPE_INPUT)) {

					FragmentEntryConfigurationParser
						fragmentEntryConfigurationParser =
							ServletContextUtil.
								getFragmentEntryConfigurationParser();

					String infoFieldUniqueId = GetterUtil.getString(
						fragmentEntryConfigurationParser.getFieldValue(
							fragmentEntryLink.getEditableValuesJSONObject(),
							new FragmentConfigurationField(
								"inputFieldId", "string", "", false, "text"),
							_themeDisplay.getLocale()));

					InfoField<?> infoField = infoForm.getInfoField(
						infoFieldUniqueId);

					if (infoField != null) {
						InfoFieldType infoFieldType =
							infoField.getInfoFieldType();

						if (infoFieldType instanceof BooleanInfoFieldType ||
							infoFieldType instanceof MultiselectInfoFieldType) {

							jspWriter.write("<input name=\"checkboxNames\" ");
							jspWriter.write("type=\"hidden\" value=\"");
							jspWriter.write(infoFieldUniqueId);
							jspWriter.write("\">");
						}

						dataAttributes.put(
							"field-type", infoFieldType.getName());

						if (infoField.isLocalizable()) {
							dataAttributes.put(
								"localizable", Boolean.TRUE.toString());
						}
					}
				}

				if (GetterUtil.getBoolean(
						_httpServletRequest.getAttribute(
							FragmentWebKeys.
								ACCESS_ALLOWED_TO_FRAGMENT_ENTRY_LINK_ID +
									fragmentEntryLink.getFragmentEntryLinkId()),
						true)) {

					_write(
						dataAttributes, fragmentEntryLink,
						fragmentStyledLayoutStructureItem, jspWriter);
				}
				else {
					jspWriter.write("<div>");
				}

				jspWriter.write(html);
				jspWriter.write("</div>");
			}
		}

		if (Objects.equals(layout.getType(), LayoutConstants.TYPE_PORTLET)) {
			jspWriter.write("</div>");
		}
	}

	private void _renderLayoutStructure(List<String> childrenItemIds)
		throws Exception {

		_httpServletRequest.setAttribute(
			LayoutWebKeys.LAYOUT_STRUCTURE, _layoutStructure);

		_renderLayoutStructure(childrenItemIds, null);
	}

	private void _renderLayoutStructure(
			List<String> childrenItemIds, InfoForm infoForm)
		throws Exception {

		Set<String> hiddenItemIds =
			_renderLayoutStructureDisplayContext.getHiddenItemIds();

		for (String childrenItemId : childrenItemIds) {
			LayoutStructureItem layoutStructureItem =
				_layoutStructure.getLayoutStructureItem(childrenItemId);

			if (hiddenItemIds.contains(childrenItemId)) {
				continue;
			}

			long start = System.currentTimeMillis();

			if (layoutStructureItem instanceof
					CollectionStyledLayoutStructureItem) {

				_renderCollectionStyledLayoutStructureItem(
					infoForm,
					(CollectionStyledLayoutStructureItem)layoutStructureItem);
			}
			else if (layoutStructureItem instanceof ColumnLayoutStructureItem) {
				_renderColumnLayoutStructureItem(
					infoForm, (ColumnLayoutStructureItem)layoutStructureItem);
			}
			else if (layoutStructureItem instanceof
						ContainerStyledLayoutStructureItem) {

				ContainerStyledLayoutStructureItem
					containerStyledLayoutStructureItem =
						(ContainerStyledLayoutStructureItem)layoutStructureItem;

				if (Objects.equals(
						_renderLayoutStructureDisplayContext.getLayoutMode(),
						Constants.SEARCH) &&
					!containerStyledLayoutStructureItem.isIndexed()) {

					continue;
				}

				_renderContainerStyledLayoutStructureItem(
					infoForm, containerStyledLayoutStructureItem);
			}
			else if (layoutStructureItem instanceof
						DropZoneLayoutStructureItem) {

				_renderDropZoneLayoutStructureItem(
					infoForm, layoutStructureItem);
			}
			else if (layoutStructureItem instanceof
						FormRelationshipStyledLayoutStructureItem) {

				_renderFormRelationshipStyledLayoutStructureItem(
					infoForm,
					(FormRelationshipStyledLayoutStructureItem)
						layoutStructureItem);
			}
			else if (layoutStructureItem instanceof
						FormStepContainerStyledLayoutStructureItem) {

				_renderFormStepContainerStyledLayoutStructureItem(
					infoForm,
					(FormStepContainerStyledLayoutStructureItem)
						layoutStructureItem);
			}
			else if (layoutStructureItem instanceof
						FormStyledLayoutStructureItem) {

				FormStyledLayoutStructureItem formStyledLayoutStructureItem =
					(FormStyledLayoutStructureItem)layoutStructureItem;

				if (Objects.equals(
						_renderLayoutStructureDisplayContext.getLayoutMode(),
						Constants.SEARCH) &&
					!formStyledLayoutStructureItem.isIndexed()) {

					continue;
				}

				if (SessionMessages.contains(
						_httpServletRequest,
						formStyledLayoutStructureItem.getItemId())) {

					_renderFormStyledLayoutStructureItemSuccessMessage(
						formStyledLayoutStructureItem);
				}
				else {
					_renderFormStyledLayoutStructureItem(
						_renderLayoutStructureDisplayContext.getInfoForm(
							formStyledLayoutStructureItem),
						formStyledLayoutStructureItem);
				}
			}
			else if (layoutStructureItem instanceof
						FragmentStyledLayoutStructureItem) {

				FragmentStyledLayoutStructureItem
					fragmentStyledLayoutStructureItem =
						(FragmentStyledLayoutStructureItem)layoutStructureItem;

				if (Objects.equals(
						_renderLayoutStructureDisplayContext.getLayoutMode(),
						Constants.SEARCH) &&
					!fragmentStyledLayoutStructureItem.isIndexed()) {

					continue;
				}

				_renderFragmentStyledLayoutStructureItem(
					infoForm, fragmentStyledLayoutStructureItem);
			}
			else if (layoutStructureItem instanceof
						RowStyledLayoutStructureItem) {

				RowStyledLayoutStructureItem rowStyledLayoutStructureItem =
					(RowStyledLayoutStructureItem)layoutStructureItem;

				if (Objects.equals(
						_renderLayoutStructureDisplayContext.getLayoutMode(),
						Constants.SEARCH) &&
					!rowStyledLayoutStructureItem.isIndexed()) {

					continue;
				}

				_renderRowStyledLayoutStructureItem(
					infoForm, rowStyledLayoutStructureItem);
			}
			else {
				_renderLayoutStructure(
					layoutStructureItem.getChildrenItemIds(), infoForm);
			}

			_layoutStructureItemRenderTimes.add(
				new LayoutStructureItemRenderTime(
					layoutStructureItem, System.currentTimeMillis() - start));
		}
	}

	private void _renderReactComponent(String module, Map<String, Object> props)
		throws Exception {

		JspWriter jspWriter = _pageContext.getOut();

		jspWriter.write("<div><span aria-hidden=\"true\" class=\"");
		jspWriter.write("loading-animation\"></span>");

		com.liferay.frontend.taglib.react.servlet.taglib.ComponentTag
			componentTag =
				new com.liferay.frontend.taglib.react.servlet.taglib.
					ComponentTag();

		componentTag.setModule(module);
		componentTag.setPageContext(_pageContext);
		componentTag.setProps(props);
		componentTag.setServletContext(ServletContextUtil.getServletContext());

		componentTag.doStartTag();

		componentTag.doEndTag();

		jspWriter.write("</div>");
	}

	private void _renderRowStyledLayoutStructureItem(
			InfoForm infoForm,
			RowStyledLayoutStructureItem rowStyledLayoutStructureItem)
		throws Exception {

		JspWriter jspWriter = _pageContext.getOut();

		jspWriter.write("<div class=\"");
		jspWriter.write(rowStyledLayoutStructureItem.getUniqueCssClass());
		jspWriter.write(StringPool.SPACE);
		jspWriter.write(rowStyledLayoutStructureItem.getCssClass());
		jspWriter.write(StringPool.SPACE);
		jspWriter.write(rowStyledLayoutStructureItem.getStyledCssClasses());
		jspWriter.write("\" data-layout-structure-item-id=\"");
		jspWriter.write(rowStyledLayoutStructureItem.getItemId());

		String style = _renderLayoutStructureDisplayContext.getStyle(
			rowStyledLayoutStructureItem);

		if (Validator.isNotNull(style)) {
			jspWriter.write("\" style=\"");
			jspWriter.write(style);
		}

		jspWriter.write("\">");

		if (_renderLayoutStructureDisplayContext.isIncludeContainer(
				rowStyledLayoutStructureItem)) {

			ContainerTag containerTag = new ContainerTag();

			containerTag.setCssClass("p-0");
			containerTag.setFluid(true);
			containerTag.setPageContext(_pageContext);

			containerTag.doStartTag();

			RowTag rowTag = new RowTag();

			rowTag.setCssClass(
				ResponsiveLayoutStructureUtil.getRowCssClass(
					rowStyledLayoutStructureItem));
			rowTag.setPageContext(_pageContext);

			rowTag.doStartTag();

			_renderLayoutStructure(
				rowStyledLayoutStructureItem.getChildrenItemIds(), infoForm);

			rowTag.doEndTag();

			containerTag.doEndTag();
		}
		else {
			RowTag rowTag = new RowTag();

			rowTag.setCssClass(
				ResponsiveLayoutStructureUtil.getRowCssClass(
					rowStyledLayoutStructureItem));
			rowTag.setPageContext(_pageContext);

			rowTag.doStartTag();

			_renderLayoutStructure(
				rowStyledLayoutStructureItem.getChildrenItemIds(), infoForm);

			rowTag.doEndTag();
		}

		jspWriter.write("</div>");
	}

	private void _write(
			Map<String, String> dataAttributes,
			FragmentEntryLink fragmentEntryLink,
			FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem,
			JspWriter jspWriter)
		throws Exception {

		jspWriter.write("<div class=\"");

		if (!_renderLayoutStructureDisplayContext.includeCommonStyles(
				fragmentEntryLink)) {

			jspWriter.write(
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkCssClass(
					fragmentEntryLink));
			jspWriter.write(StringPool.SPACE);
			jspWriter.write(
				fragmentStyledLayoutStructureItem.getUniqueCssClass());
			jspWriter.write(StringPool.SPACE);
			jspWriter.write(
				fragmentStyledLayoutStructureItem.getStyledCssClasses());
		}

		String colorCssClasses =
			_renderLayoutStructureDisplayContext.getColorCssClasses(
				fragmentStyledLayoutStructureItem);

		if (Validator.isNotNull(colorCssClasses)) {
			jspWriter.write(StringPool.SPACE);
			jspWriter.write(colorCssClasses);
		}

		jspWriter.write("\" data-layout-structure-item-id=\"");
		jspWriter.write(fragmentStyledLayoutStructureItem.getItemId());
		jspWriter.write(StringPool.QUOTE);

		for (Map.Entry<String, String> entry : dataAttributes.entrySet()) {
			jspWriter.write(" data-" + entry.getKey() + "=\"");
			jspWriter.write(entry.getValue());
			jspWriter.write(StringPool.QUOTE);
		}

		String style = _renderLayoutStructureDisplayContext.getStyle(
			fragmentStyledLayoutStructureItem);

		if (Validator.isNotNull(style)) {
			jspWriter.write(" style=\"");
			jspWriter.write(style);
			jspWriter.write(StringPool.QUOTE);
		}

		jspWriter.write(StringPool.GREATER_THAN);
	}

	private final HttpServletRequest _httpServletRequest;
	private final LayoutStructure _layoutStructure;
	private final List<LayoutStructureItemRenderTime>
		_layoutStructureItemRenderTimes = new ArrayList<>();
	private final PageContext _pageContext;
	private final boolean _renderActionHandler;
	private final RenderLayoutStructureDisplayContext
		_renderLayoutStructureDisplayContext;
	private final ThemeDisplay _themeDisplay;

}