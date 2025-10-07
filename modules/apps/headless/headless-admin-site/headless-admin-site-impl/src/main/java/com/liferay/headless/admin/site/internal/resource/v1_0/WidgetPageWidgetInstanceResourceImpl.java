/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0;

import com.liferay.headless.admin.site.dto.v1_0.WidgetPageWidgetInstance;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.GroupUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.LayoutUtil;
import com.liferay.headless.admin.site.resource.v1_0.WidgetPageWidgetInstanceResource;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.NoSuchPortletException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutType;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rubén Pulido
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/widget-page-widget-instance.properties",
	scope = ServiceScope.PROTOTYPE,
	service = WidgetPageWidgetInstanceResource.class
)
public class WidgetPageWidgetInstanceResourceImpl
	extends BaseWidgetPageWidgetInstanceResourceImpl {

	@Override
	public void deleteSiteSitePageWidgetInstance(
			String siteExternalReferenceCode,
			String sitePageExternalReferenceCode,
			String widgetInstanceExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		Layout layout = _layoutLocalService.fetchLayoutByExternalReferenceCode(
			sitePageExternalReferenceCode,
			GroupUtil.getGroupId(
				false, contextCompany.getCompanyId(),
				siteExternalReferenceCode));

		if (layout == null) {
			throw new UnsupportedOperationException();
		}

		LayoutType layoutType = layout.getLayoutType();

		if (!(layoutType instanceof LayoutTypePortlet)) {
			throw new UnsupportedOperationException();
		}

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		if (!layoutTypePortlet.hasPortletId(
				widgetInstanceExternalReferenceCode)) {

			throw new NoSuchPortletException();
		}

		layoutTypePortlet.removePortletId(
			contextUser.getUserId(), widgetInstanceExternalReferenceCode);

		_layoutLocalService.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getTypeSettings());
	}

	@Override
	public WidgetPageWidgetInstance getSiteSitePageWidgetInstance(
			String siteExternalReferenceCode,
			String sitePageExternalReferenceCode,
			String widgetInstanceExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		Layout layout = _layoutLocalService.fetchLayoutByExternalReferenceCode(
			sitePageExternalReferenceCode,
			GroupUtil.getGroupId(
				false, contextCompany.getCompanyId(),
				siteExternalReferenceCode));

		if (layout == null) {
			throw new UnsupportedOperationException();
		}

		LayoutType layoutType = layout.getLayoutType();

		if (!(layoutType instanceof LayoutTypePortlet)) {
			throw new UnsupportedOperationException();
		}

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		if (!layoutTypePortlet.hasPortletId(
				widgetInstanceExternalReferenceCode)) {

			throw new NoSuchPortletException();
		}

		return _toWidgetPageWidgetInstance(
			layout, widgetInstanceExternalReferenceCode);
	}

	@Override
	public Page<WidgetPageWidgetInstance> getSiteSitePageWidgetInstancesPage(
			String siteExternalReferenceCode,
			String sitePageExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		Layout layout = _layoutLocalService.fetchLayoutByExternalReferenceCode(
			sitePageExternalReferenceCode,
			GroupUtil.getGroupId(
				false, contextCompany.getCompanyId(),
				siteExternalReferenceCode));

		if (layout == null) {
			throw new UnsupportedOperationException();
		}

		LayoutType layoutType = layout.getLayoutType();

		if (!(layoutType instanceof LayoutTypePortlet)) {
			throw new UnsupportedOperationException();
		}

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		DTOConverterContext dtoConverterContext =
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(), null,
				_dtoConverterRegistry, contextHttpServletRequest,
				layout.getPlid(), contextAcceptLanguage.getPreferredLocale(),
				contextUriInfo, contextUser);

		return Page.of(
			transform(
				layoutTypePortlet.getPortletIds(),
				portletId -> _toWidgetPageWidgetInstance(
					dtoConverterContext, layout, portletId)));
	}

	@Override
	public WidgetPageWidgetInstance postSiteSitePageWidgetInstance(
			String siteExternalReferenceCode,
			String sitePageExternalReferenceCode,
			WidgetPageWidgetInstance widgetPageWidgetInstance)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		Layout layout = _layoutLocalService.fetchLayoutByExternalReferenceCode(
			sitePageExternalReferenceCode,
			GroupUtil.getGroupId(
				false, contextCompany.getCompanyId(),
				siteExternalReferenceCode));

		if (layout == null) {
			throw new UnsupportedOperationException();
		}

		LayoutType layoutType = layout.getLayoutType();

		if (!(layoutType instanceof LayoutTypePortlet)) {
			throw new UnsupportedOperationException();
		}

		String portletId = PortletIdCodec.encode(
			widgetPageWidgetInstance.getWidgetName(),
			widgetPageWidgetInstance.getWidgetInstanceId());

		return _addPortletId(
			widgetPageWidgetInstance.getParentSectionId(), layout, portletId,
			widgetPageWidgetInstance.getPosition());
	}

	@Override
	public WidgetPageWidgetInstance putSiteSitePageWidgetInstance(
			String siteExternalReferenceCode,
			String sitePageExternalReferenceCode,
			String widgetInstanceExternalReferenceCode,
			WidgetPageWidgetInstance widgetPageWidgetInstance)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-35443")) {
			throw new UnsupportedOperationException();
		}

		Layout layout = _layoutLocalService.fetchLayoutByExternalReferenceCode(
			sitePageExternalReferenceCode,
			GroupUtil.getGroupId(
				false, contextCompany.getCompanyId(),
				siteExternalReferenceCode));

		if (layout == null) {
			throw new UnsupportedOperationException();
		}

		LayoutType layoutType = layout.getLayoutType();

		if (!(layoutType instanceof LayoutTypePortlet)) {
			throw new UnsupportedOperationException();
		}

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		String portletId = PortletIdCodec.encode(
			widgetPageWidgetInstance.getWidgetName(),
			widgetPageWidgetInstance.getWidgetInstanceId());

		if (!layoutTypePortlet.hasPortletId(portletId)) {
			return _addPortletId(
				widgetPageWidgetInstance.getParentSectionId(), layout,
				portletId, widgetPageWidgetInstance.getPosition());
		}

		if (!Objects.equals(
				widgetPageWidgetInstance.getParentSectionId(),
				LayoutUtil.getParentSectionId(layout, portletId)) ||
			!Objects.equals(
				widgetPageWidgetInstance.getPosition(),
				LayoutUtil.getPosition(layout, portletId))) {

			layoutTypePortlet.movePortletId(
				contextUser.getUserId(), portletId,
				widgetPageWidgetInstance.getParentSectionId(),
				widgetPageWidgetInstance.getPosition());
		}

		layout = _layoutLocalService.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getTypeSettings());

		return _toWidgetPageWidgetInstance(layout, portletId);
	}

	private WidgetPageWidgetInstance _addPortletId(
			String columnId, Layout layout, String portletId, int position)
		throws Exception {

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		String addedPortletId = layoutTypePortlet.addPortletId(
			contextUser.getUserId(), portletId, columnId, position);

		if (addedPortletId == null) {
			throw new PortalException(
				StringBundler.concat(
					"Portlet ", portletId, " cannot be added to layout ",
					layout.getPlid(), " by user ", contextUser.getUserId()));
		}

		layout = _layoutLocalService.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getTypeSettings());

		return _toWidgetPageWidgetInstance(layout, addedPortletId);
	}

	private WidgetPageWidgetInstance _toWidgetPageWidgetInstance(
			DTOConverterContext dtoConverterContext, Layout layout,
			String portletId)
		throws Exception {

		dtoConverterContext.setAttribute("portletId", portletId);

		return _widgetPageWidgetInstanceDTOConverter.toDTO(
			dtoConverterContext, layout);
	}

	private WidgetPageWidgetInstance _toWidgetPageWidgetInstance(
			Layout layout, String portletId)
		throws Exception {

		DTOConverterContext dtoConverterContext =
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(), null,
				_dtoConverterRegistry, contextHttpServletRequest,
				layout.getPlid(), contextAcceptLanguage.getPreferredLocale(),
				contextUriInfo, contextUser);

		return _toWidgetPageWidgetInstance(
			dtoConverterContext, layout, portletId);
	}

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.WidgetPageWidgetInstanceDTOConverter)"
	)
	private DTOConverter<Layout, WidgetPageWidgetInstance>
		_widgetPageWidgetInstanceDTOConverter;

}