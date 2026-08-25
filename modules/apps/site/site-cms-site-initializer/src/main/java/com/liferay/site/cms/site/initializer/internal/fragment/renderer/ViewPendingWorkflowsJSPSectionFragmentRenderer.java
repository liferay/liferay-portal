/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.frontend.data.set.SystemFDSEntry;
import com.liferay.frontend.data.set.action.FDSItemsActions;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;
import com.liferay.site.cms.site.initializer.internal.display.context.ViewPendingWorkflowsSectionDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Veronica Gonzalez
 */
@Component(
	configurationPid = "com.liferay.document.library.configuration.DLConfiguration",
	service = FragmentRenderer.class
)
public class ViewPendingWorkflowsJSPSectionFragmentRenderer
	extends BaseEnterpriseJSPSectionFragmentRenderer
		<ViewPendingWorkflowsSectionDisplayContext> {

	@Override
	public String getCollectionKey() {
		return "sections";
	}

	@Override
	public String getLabelKey() {
		return "pending-workflows-section";
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_dlConfiguration = ConfigurableUtil.createConfigurable(
			DLConfiguration.class, properties);
	}

	@Override
	protected ViewPendingWorkflowsSectionDisplayContext getDisplayContext(
		HttpServletRequest httpServletRequest) {

		return new ViewPendingWorkflowsSectionDisplayContext(
			_depotEntryLocalService, _dlConfiguration, groupLocalService,
			httpServletRequest, language, _objectDefinitionService, _portal,
			translationInfoItemFieldValuesExporterRegistry,
			_viewPendingWorkflowsSectionFDSItemsActions,
			_viewPendingWorkflowsSectionSystemFDSEntry);
	}

	@Override
	protected String getJSPPath() {
		return "/view_pending_workflows.jsp";
	}

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	private volatile DLConfiguration _dlConfiguration;

	@Reference
	private ObjectDefinitionService _objectDefinitionService;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(frontend.data.set.name=" + CMSSiteInitializerFDSNames.PENDING_WORKFLOWS_SECTION + ")"
	)
	private FDSItemsActions _viewPendingWorkflowsSectionFDSItemsActions;

	@Reference(
		target = "(frontend.data.set.name=" + CMSSiteInitializerFDSNames.PENDING_WORKFLOWS_SECTION + ")"
	)
	private SystemFDSEntry _viewPendingWorkflowsSectionSystemFDSEntry;

}