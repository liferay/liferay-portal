/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.menu.web.internal.exportimport.portlet.preferences.processor;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.staging.MergeLayoutPrototypesThreadLocal;
import com.liferay.exportimport.portlet.preferences.processor.Capability;
import com.liferay.exportimport.portlet.preferences.processor.ExportImportPortletPreferencesProcessor;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.site.navigation.constants.SiteNavigationConstants;
import com.liferay.site.navigation.constants.SiteNavigationMenuPortletKeys;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.ReadOnlyException;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "jakarta.portlet.name=" + SiteNavigationMenuPortletKeys.SITE_NAVIGATION_MENU,
	service = ExportImportPortletPreferencesProcessor.class
)
public class SiteNavigationMenuExportImportPortletPreferencesProcessor
	implements ExportImportPortletPreferencesProcessor {

	@Override
	public List<Capability> getExportCapabilities() {
		return ListUtil.fromArray(_exportCapability);
	}

	@Override
	public List<Capability> getImportCapabilities() {
		return ListUtil.fromArray(_importCapability);
	}

	@Override
	public PortletPreferences processExportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		if (!MapUtil.getBoolean(
				portletDataContext.getParameterMap(),
				PortletDataHandlerKeys.PORTLET_DATA) &&
			MergeLayoutPrototypesThreadLocal.isInProgress()) {

			return portletPreferences;
		}

		try {
			portletDataContext.addPortletPermissions(
				SiteNavigationConstants.RESOURCE_NAME);
		}
		catch (PortalException portalException) {
			throw new PortletDataException(
				"Unable to export portlet permissions", portalException);
		}

		SiteNavigationMenu siteNavigationMenu = _getSiteNavigationMenu(
			portletDataContext, portletPreferences);

		if (siteNavigationMenu != null) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, portletDataContext.getPortletId(),
				siteNavigationMenu);
		}

		return portletPreferences;
	}

	@Override
	public PortletPreferences processImportPortletPreferences(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws PortletDataException {

		try {
			portletDataContext.importPortletPermissions(
				SiteNavigationConstants.RESOURCE_NAME);
		}
		catch (PortalException portalException) {
			throw new PortletDataException(
				"Unable to import portlet permissions", portalException);
		}

		if (!MapUtil.getBoolean(
				portletDataContext.getParameterMap(),
				PortletDataHandlerKeys.PORTLET_DATA) &&
			MergeLayoutPrototypesThreadLocal.isInProgress()) {

			String siteNavigationMenuExternalReferenceCode =
				portletPreferences.getValue(
					"siteNavigationMenuExternalReferenceCode",
					StringPool.BLANK);
			String siteNavigationMenuGroupExternalReferenceCode =
				portletPreferences.getValue(
					"siteNavigationMenuGroupExternalReferenceCode",
					StringPool.BLANK);

			SiteNavigationMenu siteNavigationMenu = _getSiteNavigationMenu(
				portletDataContext, portletPreferences);

			if ((siteNavigationMenu == null) &&
				Validator.isNotNull(siteNavigationMenuExternalReferenceCode)) {

				try {
					Layout layout = _layoutLocalService.getLayout(
						portletDataContext.getPlid());

					PortletPreferences existingPortletPreferences = null;

					if (layout.isPortletEmbedded(
							portletDataContext.getPortletId(),
							layout.getGroupId())) {

						existingPortletPreferences =
							PortletPreferencesFactoryUtil.getLayoutPortletSetup(
								layout.getCompanyId(), layout.getGroupId(),
								PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
								PortletKeys.PREFS_PLID_SHARED,
								portletDataContext.getPortletId(), null);
					}
					else {
						existingPortletPreferences =
							PortletPreferencesFactoryUtil.getPortletSetup(
								layout, portletDataContext.getPortletId(),
								StringPool.BLANK);
					}

					siteNavigationMenuExternalReferenceCode =
						existingPortletPreferences.getValue(
							"siteNavigationMenuExternalReferenceCode",
							StringPool.BLANK);

					siteNavigationMenuGroupExternalReferenceCode =
						existingPortletPreferences.getValue(
							"siteNavigationMenuGroupExternalReferenceCode",
							StringPool.BLANK);
				}
				catch (PortalException portalException) {
					PortletDataException portletDataException =
						new PortletDataException(portalException);

					throw portletDataException;
				}
			}

			try {
				portletPreferences.setValue(
					"siteNavigationMenuExternalReferenceCode",
					siteNavigationMenuExternalReferenceCode);
				portletPreferences.setValue(
					"siteNavigationMenuGroupExternalReferenceCode",
					siteNavigationMenuGroupExternalReferenceCode);
			}
			catch (ReadOnlyException readOnlyException) {
				PortletDataException portletDataException =
					new PortletDataException(readOnlyException);

				throw portletDataException;
			}

			return portletPreferences;
		}

		_importSiteNavigationMenuReference(portletDataContext);

		return portletPreferences;
	}

	private SiteNavigationMenu _getSiteNavigationMenu(
		PortletDataContext portletDataContext,
		PortletPreferences portletPreferences) {

		String siteNavigationMenuExternalReferenceCode =
			portletPreferences.getValue(
				"siteNavigationMenuExternalReferenceCode", null);

		if (Validator.isNull(siteNavigationMenuExternalReferenceCode)) {
			return null;
		}

		String siteNavigationMenuGroupExternalReferenceCode =
			portletPreferences.getValue(
				"siteNavigationMenuGroupExternalReferenceCode", null);

		if (Validator.isNull(siteNavigationMenuGroupExternalReferenceCode)) {
			return _siteNavigationMenuLocalService.
				fetchSiteNavigationMenuByExternalReferenceCode(
					siteNavigationMenuExternalReferenceCode,
					portletDataContext.getScopeGroupId());
		}

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			siteNavigationMenuGroupExternalReferenceCode,
			portletDataContext.getCompanyId());

		if (group == null) {
			return null;
		}

		return _siteNavigationMenuLocalService.
			fetchSiteNavigationMenuByExternalReferenceCode(
				siteNavigationMenuExternalReferenceCode, group.getGroupId());
	}

	private void _importSiteNavigationMenuReference(
			PortletDataContext portletDataContext)
		throws PortletDataException {

		Element importDataRootElement =
			portletDataContext.getImportDataRootElement();

		Element referencesElement = importDataRootElement.element("references");

		if (referencesElement == null) {
			return;
		}

		List<Element> referenceElements = referencesElement.elements();

		for (Element referenceElement : referenceElements) {
			String className = referenceElement.attributeValue("class-name");

			if (!className.equals(SiteNavigationMenu.class.getName())) {
				continue;
			}

			long classPK = GetterUtil.getLong(
				referenceElement.attributeValue("class-pk"));

			StagedModelDataHandlerUtil.importReferenceStagedModel(
				portletDataContext, className, Long.valueOf(classPK));
		}
	}

	@Reference(target = "(name=CommonPortletDisplayTemplateExportCapability)")
	private Capability _exportCapability;

	@Reference(unbind = "-")
	private GroupLocalService _groupLocalService;

	@Reference(target = "(name=CommonPortletDisplayTemplateImportCapability)")
	private Capability _importCapability;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference(unbind = "-")
	private SiteNavigationMenuLocalService _siteNavigationMenuLocalService;

}