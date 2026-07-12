/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0;

import com.liferay.headless.admin.site.dto.v1_0.WidgetPageWidgetInstance;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.DTOConverterContextUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.util.LayoutUtil;
import com.liferay.headless.admin.site.resource.v1_0.WidgetPageWidgetInstanceResource;
import com.liferay.headless.common.spi.util.GroupUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.pagination.Page;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;

import jakarta.ws.rs.NotFoundException;

import java.util.List;
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
	@Tags({@Tag(description = "[DEV]", name = "WidgetPageWidgetInstance")})
	public void deleteSiteSitePageWidgetInstance(
			String siteExternalReferenceCode,
			String sitePageExternalReferenceCode,
			String widgetInstanceExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-74328")) {

			throw new UnsupportedOperationException();
		}

		Layout layout = _getTypePortletLayout(
			siteExternalReferenceCode, sitePageExternalReferenceCode);

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		if (!layoutTypePortlet.hasPortletId(
				widgetInstanceExternalReferenceCode)) {

			throw new NotFoundException(
				StringBundler.concat(
					"No widget instance with external reference code \"",
					widgetInstanceExternalReferenceCode,
					"\" exists in this site page"));
		}

		layoutTypePortlet.removePortletId(
			contextUser.getUserId(), widgetInstanceExternalReferenceCode);

		_layoutLocalService.updateTypeSettings(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getTypeSettings());
	}

	@Override
	public WidgetPageWidgetInstance getSiteSitePageWidgetInstance(
			String siteExternalReferenceCode,
			String sitePageExternalReferenceCode,
			String widgetInstanceExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-74328")) {

			throw new UnsupportedOperationException();
		}

		Layout layout = _getTypePortletLayout(
			siteExternalReferenceCode, sitePageExternalReferenceCode);

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		if (!layoutTypePortlet.hasPortletId(
				widgetInstanceExternalReferenceCode)) {

			throw new NotFoundException(
				StringBundler.concat(
					"No widget instance with external reference code \"",
					widgetInstanceExternalReferenceCode,
					"\" exists in this site page"));
		}

		return _toWidgetPageWidgetInstance(
			layout, widgetInstanceExternalReferenceCode);
	}

	@Override
	public Page<WidgetPageWidgetInstance> getSiteSitePageWidgetInstancesPage(
			String siteExternalReferenceCode,
			String sitePageExternalReferenceCode)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-74328")) {

			throw new UnsupportedOperationException();
		}

		Layout layout = _getTypePortletLayout(
			siteExternalReferenceCode, sitePageExternalReferenceCode);

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		DTOConverterContext dtoConverterContext =
			DTOConverterContextUtil.getDTOConverterContext(
				contextAcceptLanguage, _dtoConverterRegistry,
				contextHttpServletRequest, layout.getPlid(), contextUriInfo,
				contextUser);

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

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-74328")) {

			throw new UnsupportedOperationException();
		}

		Layout layout = _getTypePortletLayout(
			siteExternalReferenceCode, sitePageExternalReferenceCode);

		_validateParentSectionId(
			layout, widgetPageWidgetInstance.getParentSectionId());

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

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-74328")) {

			throw new UnsupportedOperationException();
		}

		Layout layout = _getTypePortletLayout(
			siteExternalReferenceCode, sitePageExternalReferenceCode);

		_validateParentSectionId(
			layout, widgetPageWidgetInstance.getParentSectionId());

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

		layout = _layoutLocalService.updateTypeSettings(
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
			throw new IllegalArgumentException(
				StringBundler.concat(
					"The widget ", portletId,
					" could not be added to the site page ",
					layout.getExternalReferenceCode()));
		}

		layout = _layoutLocalService.updateTypeSettings(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getTypeSettings());

		return _toWidgetPageWidgetInstance(layout, addedPortletId);
	}

	private Layout _getTypePortletLayout(
			String siteExternalReferenceCode,
			String sitePageExternalReferenceCode)
		throws Exception {

		Layout layout = _layoutLocalService.fetchLayoutByExternalReferenceCode(
			sitePageExternalReferenceCode,
			GroupUtil.getGroupId(
				false, contextCompany.getCompanyId(),
				siteExternalReferenceCode));

		if (layout == null) {
			throw new NoSuchLayoutException(
				StringBundler.concat(
					"No site page exists with the external reference code \"",
					sitePageExternalReferenceCode, "\""));
		}

		if (!Objects.equals(layout.getType(), LayoutConstants.TYPE_PORTLET)) {
			throw new IllegalArgumentException(
				"The site page with external reference code \"" +
					sitePageExternalReferenceCode + "\" is not a widget page");
		}

		return layout;
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

		return _toWidgetPageWidgetInstance(
			DTOConverterContextUtil.getDTOConverterContext(
				contextAcceptLanguage, _dtoConverterRegistry,
				contextHttpServletRequest, layout.getPlid(), contextUriInfo,
				contextUser),
			layout, portletId);
	}

	private void _validateParentSectionId(
		Layout layout, String parentSectionId) {

		if (Validator.isNull(parentSectionId)) {
			return;
		}

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		List<String> columns = layoutTypePortlet.getColumns();

		if (!columns.contains(parentSectionId)) {
			throw new IllegalArgumentException(
				"The widget page section " + parentSectionId +
					" does not exist");
		}
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