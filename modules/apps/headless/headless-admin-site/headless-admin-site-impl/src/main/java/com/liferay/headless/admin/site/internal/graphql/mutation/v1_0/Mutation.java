/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.graphql.mutation.v1_0;

import com.liferay.headless.admin.site.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplate;
import com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplateFolder;
import com.liferay.headless.admin.site.dto.v1_0.FragmentComposition;
import com.liferay.headless.admin.site.dto.v1_0.MasterPage;
import com.liferay.headless.admin.site.dto.v1_0.PageElement;
import com.liferay.headless.admin.site.dto.v1_0.PageExperience;
import com.liferay.headless.admin.site.dto.v1_0.PageRule;
import com.liferay.headless.admin.site.dto.v1_0.PageRuleAction;
import com.liferay.headless.admin.site.dto.v1_0.PageRuleCondition;
import com.liferay.headless.admin.site.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.dto.v1_0.PageTemplate;
import com.liferay.headless.admin.site.dto.v1_0.PageTemplateSet;
import com.liferay.headless.admin.site.dto.v1_0.SitePage;
import com.liferay.headless.admin.site.dto.v1_0.UtilityPage;
import com.liferay.headless.admin.site.dto.v1_0.WidgetPageWidgetInstance;
import com.liferay.headless.admin.site.resource.v1_0.DisplayPageTemplateFolderResource;
import com.liferay.headless.admin.site.resource.v1_0.DisplayPageTemplateResource;
import com.liferay.headless.admin.site.resource.v1_0.FragmentCompositionResource;
import com.liferay.headless.admin.site.resource.v1_0.MasterPageResource;
import com.liferay.headless.admin.site.resource.v1_0.PageElementResource;
import com.liferay.headless.admin.site.resource.v1_0.PageExperienceResource;
import com.liferay.headless.admin.site.resource.v1_0.PageRuleActionResource;
import com.liferay.headless.admin.site.resource.v1_0.PageRuleConditionResource;
import com.liferay.headless.admin.site.resource.v1_0.PageRuleResource;
import com.liferay.headless.admin.site.resource.v1_0.PageSpecificationResource;
import com.liferay.headless.admin.site.resource.v1_0.PageTemplateResource;
import com.liferay.headless.admin.site.resource.v1_0.PageTemplateSetResource;
import com.liferay.headless.admin.site.resource.v1_0.SitePageResource;
import com.liferay.headless.admin.site.resource.v1_0.UtilityPageResource;
import com.liferay.headless.admin.site.resource.v1_0.WidgetPageWidgetInstanceResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.pagination.Page;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.constraints.NotEmpty;

import jakarta.ws.rs.core.UriInfo;

import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class Mutation {

	public static void setDisplayPageTemplateResourceComponentServiceObjects(
		ComponentServiceObjects<DisplayPageTemplateResource>
			displayPageTemplateResourceComponentServiceObjects) {

		_displayPageTemplateResourceComponentServiceObjects =
			displayPageTemplateResourceComponentServiceObjects;
	}

	public static void
		setDisplayPageTemplateFolderResourceComponentServiceObjects(
			ComponentServiceObjects<DisplayPageTemplateFolderResource>
				displayPageTemplateFolderResourceComponentServiceObjects) {

		_displayPageTemplateFolderResourceComponentServiceObjects =
			displayPageTemplateFolderResourceComponentServiceObjects;
	}

	public static void setFragmentCompositionResourceComponentServiceObjects(
		ComponentServiceObjects<FragmentCompositionResource>
			fragmentCompositionResourceComponentServiceObjects) {

		_fragmentCompositionResourceComponentServiceObjects =
			fragmentCompositionResourceComponentServiceObjects;
	}

	public static void setMasterPageResourceComponentServiceObjects(
		ComponentServiceObjects<MasterPageResource>
			masterPageResourceComponentServiceObjects) {

		_masterPageResourceComponentServiceObjects =
			masterPageResourceComponentServiceObjects;
	}

	public static void setPageElementResourceComponentServiceObjects(
		ComponentServiceObjects<PageElementResource>
			pageElementResourceComponentServiceObjects) {

		_pageElementResourceComponentServiceObjects =
			pageElementResourceComponentServiceObjects;
	}

	public static void setPageExperienceResourceComponentServiceObjects(
		ComponentServiceObjects<PageExperienceResource>
			pageExperienceResourceComponentServiceObjects) {

		_pageExperienceResourceComponentServiceObjects =
			pageExperienceResourceComponentServiceObjects;
	}

	public static void setPageRuleResourceComponentServiceObjects(
		ComponentServiceObjects<PageRuleResource>
			pageRuleResourceComponentServiceObjects) {

		_pageRuleResourceComponentServiceObjects =
			pageRuleResourceComponentServiceObjects;
	}

	public static void setPageRuleActionResourceComponentServiceObjects(
		ComponentServiceObjects<PageRuleActionResource>
			pageRuleActionResourceComponentServiceObjects) {

		_pageRuleActionResourceComponentServiceObjects =
			pageRuleActionResourceComponentServiceObjects;
	}

	public static void setPageRuleConditionResourceComponentServiceObjects(
		ComponentServiceObjects<PageRuleConditionResource>
			pageRuleConditionResourceComponentServiceObjects) {

		_pageRuleConditionResourceComponentServiceObjects =
			pageRuleConditionResourceComponentServiceObjects;
	}

	public static void setPageSpecificationResourceComponentServiceObjects(
		ComponentServiceObjects<PageSpecificationResource>
			pageSpecificationResourceComponentServiceObjects) {

		_pageSpecificationResourceComponentServiceObjects =
			pageSpecificationResourceComponentServiceObjects;
	}

	public static void setPageTemplateResourceComponentServiceObjects(
		ComponentServiceObjects<PageTemplateResource>
			pageTemplateResourceComponentServiceObjects) {

		_pageTemplateResourceComponentServiceObjects =
			pageTemplateResourceComponentServiceObjects;
	}

	public static void setPageTemplateSetResourceComponentServiceObjects(
		ComponentServiceObjects<PageTemplateSetResource>
			pageTemplateSetResourceComponentServiceObjects) {

		_pageTemplateSetResourceComponentServiceObjects =
			pageTemplateSetResourceComponentServiceObjects;
	}

	public static void setSitePageResourceComponentServiceObjects(
		ComponentServiceObjects<SitePageResource>
			sitePageResourceComponentServiceObjects) {

		_sitePageResourceComponentServiceObjects =
			sitePageResourceComponentServiceObjects;
	}

	public static void setUtilityPageResourceComponentServiceObjects(
		ComponentServiceObjects<UtilityPageResource>
			utilityPageResourceComponentServiceObjects) {

		_utilityPageResourceComponentServiceObjects =
			utilityPageResourceComponentServiceObjects;
	}

	public static void
		setWidgetPageWidgetInstanceResourceComponentServiceObjects(
			ComponentServiceObjects<WidgetPageWidgetInstanceResource>
				widgetPageWidgetInstanceResourceComponentServiceObjects) {

		_widgetPageWidgetInstanceResourceComponentServiceObjects =
			widgetPageWidgetInstanceResourceComponentServiceObjects;
	}

	@GraphQLField(
		description = "Deletes a specific display page template of a site."
	)
	public boolean deleteSiteSiteByExternalReferenceCodeDisplayPageTemplate(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("displayPageTemplateExternalReferenceCode") String
				displayPageTemplateExternalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_displayPageTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			displayPageTemplateResource ->
				displayPageTemplateResource.
					deleteSiteSiteByExternalReferenceCodeDisplayPageTemplate(
						siteExternalReferenceCode,
						displayPageTemplateExternalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates only the fields received in the request body, leaving any other fields untouched."
	)
	public DisplayPageTemplate
			patchSiteSiteByExternalReferenceCodeDisplayPageTemplate(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("displayPageTemplateExternalReferenceCode") String
					displayPageTemplateExternalReferenceCode,
				@GraphQLName("displayPageTemplate") DisplayPageTemplate
					displayPageTemplate)
		throws Exception {

		return _applyComponentServiceObjects(
			_displayPageTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			displayPageTemplateResource ->
				displayPageTemplateResource.
					patchSiteSiteByExternalReferenceCodeDisplayPageTemplate(
						siteExternalReferenceCode,
						displayPageTemplateExternalReferenceCode,
						displayPageTemplate));
	}

	@GraphQLField(description = "Adds a new display page template")
	public DisplayPageTemplate
			createSiteSiteByExternalReferenceCodeDisplayPageTemplate(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("displayPageTemplate") DisplayPageTemplate
					displayPageTemplate)
		throws Exception {

		return _applyComponentServiceObjects(
			_displayPageTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			displayPageTemplateResource ->
				displayPageTemplateResource.
					postSiteSiteByExternalReferenceCodeDisplayPageTemplate(
						siteExternalReferenceCode, displayPageTemplate));
	}

	@GraphQLField(
		description = "Adds a new display page template in draft status to a display page template folder."
	)
	public DisplayPageTemplate
			createSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplate(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("displayPageTemplateFolderExternalReferenceCode")
					String displayPageTemplateFolderExternalReferenceCode,
				@GraphQLName("displayPageTemplate") DisplayPageTemplate
					displayPageTemplate)
		throws Exception {

		return _applyComponentServiceObjects(
			_displayPageTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			displayPageTemplateResource ->
				displayPageTemplateResource.
					postSiteSiteByExternalReferenceCodeDisplayPageTemplateFolderDisplayPageTemplate(
						siteExternalReferenceCode,
						displayPageTemplateFolderExternalReferenceCode,
						displayPageTemplate));
	}

	@GraphQLField(
		description = "Adds a new page specification in draft status to a display page template."
	)
	public ContentPageSpecification
			createSiteSiteByExternalReferenceCodeDisplayPageTemplatePageSpecification(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("displayPageTemplateExternalReferenceCode") String
					displayPageTemplateExternalReferenceCode,
				@GraphQLName("contentPageSpecification")
					ContentPageSpecification contentPageSpecification)
		throws Exception {

		return _applyComponentServiceObjects(
			_displayPageTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			displayPageTemplateResource ->
				displayPageTemplateResource.
					postSiteSiteByExternalReferenceCodeDisplayPageTemplatePageSpecification(
						siteExternalReferenceCode,
						displayPageTemplateExternalReferenceCode,
						contentPageSpecification));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateSiteDisplayPageTemplatePermissionsPage(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("displayPageTemplateExternalReferenceCode") String
					displayPageTemplateExternalReferenceCode,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_displayPageTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			displayPageTemplateResource -> {
				Page paginationPage =
					displayPageTemplateResource.
						putSiteDisplayPageTemplatePermissionsPage(
							siteExternalReferenceCode,
							displayPageTemplateExternalReferenceCode,
							permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField(
		description = "Updates the display page template with the given external reference code, or creates it if it does not exist."
	)
	public DisplayPageTemplate
			updateSiteSiteByExternalReferenceCodeDisplayPageTemplate(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("displayPageTemplateExternalReferenceCode") String
					displayPageTemplateExternalReferenceCode,
				@GraphQLName("displayPageTemplate") DisplayPageTemplate
					displayPageTemplate)
		throws Exception {

		return _applyComponentServiceObjects(
			_displayPageTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			displayPageTemplateResource ->
				displayPageTemplateResource.
					putSiteSiteByExternalReferenceCodeDisplayPageTemplate(
						siteExternalReferenceCode,
						displayPageTemplateExternalReferenceCode,
						displayPageTemplate));
	}

	@GraphQLField(
		description = "Deletes a specific display page template folder of a site."
	)
	public boolean
			deleteSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("displayPageTemplateFolderExternalReferenceCode")
					String displayPageTemplateFolderExternalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_displayPageTemplateFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			displayPageTemplateFolderResource ->
				displayPageTemplateFolderResource.
					deleteSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
						siteExternalReferenceCode,
						displayPageTemplateFolderExternalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates only the fields received in the request body, leaving any other fields untouched."
	)
	public DisplayPageTemplateFolder
			patchSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("displayPageTemplateFolderExternalReferenceCode")
					String displayPageTemplateFolderExternalReferenceCode,
				@GraphQLName("displayPageTemplateFolder")
					DisplayPageTemplateFolder displayPageTemplateFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_displayPageTemplateFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			displayPageTemplateFolderResource ->
				displayPageTemplateFolderResource.
					patchSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
						siteExternalReferenceCode,
						displayPageTemplateFolderExternalReferenceCode,
						displayPageTemplateFolder));
	}

	@GraphQLField(description = "Adds a new display page template folder.")
	public DisplayPageTemplateFolder
			createSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("displayPageTemplateFolder")
					DisplayPageTemplateFolder displayPageTemplateFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_displayPageTemplateFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			displayPageTemplateFolderResource ->
				displayPageTemplateFolderResource.
					postSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
						siteExternalReferenceCode, displayPageTemplateFolder));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateSiteDisplayPageTemplateFolderPermissionsPage(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("displayPageTemplateFolderExternalReferenceCode")
					String displayPageTemplateFolderExternalReferenceCode,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_displayPageTemplateFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			displayPageTemplateFolderResource -> {
				Page paginationPage =
					displayPageTemplateFolderResource.
						putSiteDisplayPageTemplateFolderPermissionsPage(
							siteExternalReferenceCode,
							displayPageTemplateFolderExternalReferenceCode,
							permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField(
		description = "Updates the display page template folder with the given external reference code, or creates it if it does not exist."
	)
	public DisplayPageTemplateFolder
			updateSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("displayPageTemplateFolderExternalReferenceCode")
					String displayPageTemplateFolderExternalReferenceCode,
				@GraphQLName("displayPageTemplateFolder")
					DisplayPageTemplateFolder displayPageTemplateFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_displayPageTemplateFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			displayPageTemplateFolderResource ->
				displayPageTemplateFolderResource.
					putSiteSiteByExternalReferenceCodeDisplayPageTemplateFolder(
						siteExternalReferenceCode,
						displayPageTemplateFolderExternalReferenceCode,
						displayPageTemplateFolder));
	}

	@GraphQLField(
		description = "Deletes a specific fragment composition of a site."
	)
	public boolean deleteSiteSiteByExternalReferenceCodeFragmentComposition(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("fragmentCompositionExternalReferenceCode") String
				fragmentCompositionExternalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_fragmentCompositionResourceComponentServiceObjects,
			this::_populateResourceContext,
			fragmentCompositionResource ->
				fragmentCompositionResource.
					deleteSiteSiteByExternalReferenceCodeFragmentComposition(
						siteExternalReferenceCode,
						fragmentCompositionExternalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates only the fields received in the request body, leaving any other fields untouched."
	)
	public FragmentComposition
			patchSiteSiteByExternalReferenceCodeFragmentComposition(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("fragmentCompositionExternalReferenceCode") String
					fragmentCompositionExternalReferenceCode,
				@GraphQLName("fragmentComposition") FragmentComposition
					fragmentComposition)
		throws Exception {

		return _applyComponentServiceObjects(
			_fragmentCompositionResourceComponentServiceObjects,
			this::_populateResourceContext,
			fragmentCompositionResource ->
				fragmentCompositionResource.
					patchSiteSiteByExternalReferenceCodeFragmentComposition(
						siteExternalReferenceCode,
						fragmentCompositionExternalReferenceCode,
						fragmentComposition));
	}

	@GraphQLField(
		description = "Adds a new fragment composition. If the page element of the fragment composition does not contain a definition property and contains an external reference code, the page element will be retrieved based on the externalReferenceCode and used for creating the fragment composition."
	)
	public FragmentComposition
			createSiteSiteByExternalReferenceCodeFragmentComposition(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("saveInlineContent") Boolean saveInlineContent,
				@GraphQLName("saveMapping") Boolean saveMapping,
				@GraphQLName("fragmentComposition") FragmentComposition
					fragmentComposition)
		throws Exception {

		return _applyComponentServiceObjects(
			_fragmentCompositionResourceComponentServiceObjects,
			this::_populateResourceContext,
			fragmentCompositionResource ->
				fragmentCompositionResource.
					postSiteSiteByExternalReferenceCodeFragmentComposition(
						siteExternalReferenceCode, saveInlineContent,
						saveMapping, fragmentComposition));
	}

	@GraphQLField(
		description = "Updates the fragment composition with the given external reference code, or creates it if it does not exist."
	)
	public FragmentComposition
			updateSiteSiteByExternalReferenceCodeFragmentComposition(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("fragmentCompositionExternalReferenceCode") String
					fragmentCompositionExternalReferenceCode,
				@GraphQLName("fragmentComposition") FragmentComposition
					fragmentComposition)
		throws Exception {

		return _applyComponentServiceObjects(
			_fragmentCompositionResourceComponentServiceObjects,
			this::_populateResourceContext,
			fragmentCompositionResource ->
				fragmentCompositionResource.
					putSiteSiteByExternalReferenceCodeFragmentComposition(
						siteExternalReferenceCode,
						fragmentCompositionExternalReferenceCode,
						fragmentComposition));
	}

	@GraphQLField(description = "Deletes a specific master page of a site.")
	public boolean deleteSiteSiteByExternalReferenceCodeMasterPage(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("masterPageExternalReferenceCode") String
				masterPageExternalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_masterPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			masterPageResource ->
				masterPageResource.
					deleteSiteSiteByExternalReferenceCodeMasterPage(
						siteExternalReferenceCode,
						masterPageExternalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates only the fields received in the request body, leaving any other fields untouched."
	)
	public MasterPage patchSiteSiteByExternalReferenceCodeMasterPage(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("masterPageExternalReferenceCode") String
				masterPageExternalReferenceCode,
			@GraphQLName("masterPage") MasterPage masterPage)
		throws Exception {

		return _applyComponentServiceObjects(
			_masterPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			masterPageResource ->
				masterPageResource.
					patchSiteSiteByExternalReferenceCodeMasterPage(
						siteExternalReferenceCode,
						masterPageExternalReferenceCode, masterPage));
	}

	@GraphQLField(description = "Adds a new master page.")
	public MasterPage createSiteSiteByExternalReferenceCodeMasterPage(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("masterPage") MasterPage masterPage)
		throws Exception {

		return _applyComponentServiceObjects(
			_masterPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			masterPageResource ->
				masterPageResource.
					postSiteSiteByExternalReferenceCodeMasterPage(
						siteExternalReferenceCode, masterPage));
	}

	@GraphQLField(
		description = "Adds a new page specification in draft status to a master page."
	)
	public ContentPageSpecification
			createSiteSiteByExternalReferenceCodeMasterPagePageSpecification(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("masterPageExternalReferenceCode") String
					masterPageExternalReferenceCode,
				@GraphQLName("contentPageSpecification")
					ContentPageSpecification contentPageSpecification)
		throws Exception {

		return _applyComponentServiceObjects(
			_masterPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			masterPageResource ->
				masterPageResource.
					postSiteSiteByExternalReferenceCodeMasterPagePageSpecification(
						siteExternalReferenceCode,
						masterPageExternalReferenceCode,
						contentPageSpecification));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateSiteMasterPagePermissionsPage(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("masterPageExternalReferenceCode") String
					masterPageExternalReferenceCode,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_masterPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			masterPageResource -> {
				Page paginationPage =
					masterPageResource.putSiteMasterPagePermissionsPage(
						siteExternalReferenceCode,
						masterPageExternalReferenceCode, permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField(
		description = "Updates the master page with the given external reference code, or creates it if it does not exist."
	)
	public MasterPage updateSiteSiteByExternalReferenceCodeMasterPage(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("masterPageExternalReferenceCode") String
				masterPageExternalReferenceCode,
			@GraphQLName("masterPage") MasterPage masterPage)
		throws Exception {

		return _applyComponentServiceObjects(
			_masterPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			masterPageResource ->
				masterPageResource.putSiteSiteByExternalReferenceCodeMasterPage(
					siteExternalReferenceCode, masterPageExternalReferenceCode,
					masterPage));
	}

	@GraphQLField(
		description = "Deletes a page element within an experience of a specific page specification of a site page within a site."
	)
	public boolean deleteSiteSiteByExternalReferenceCodePageElement(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("pageSpecificationExternalReferenceCode") String
				pageSpecificationExternalReferenceCode,
			@GraphQLName("pageExperienceExternalReferenceCode") String
				pageExperienceExternalReferenceCode,
			@GraphQLName("pageElementExternalReferenceCode") String
				pageElementExternalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_pageElementResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageElementResource ->
				pageElementResource.
					deleteSiteSiteByExternalReferenceCodePageElement(
						siteExternalReferenceCode,
						pageSpecificationExternalReferenceCode,
						pageExperienceExternalReferenceCode,
						pageElementExternalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates a page element within an experience of a specific page specification of a site page within a site. Updates only the fields received in the request body, leaving any other fields untouched."
	)
	public PageElement patchSiteSiteByExternalReferenceCodePageElement(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("pageSpecificationExternalReferenceCode") String
				pageSpecificationExternalReferenceCode,
			@GraphQLName("pageExperienceExternalReferenceCode") String
				pageExperienceExternalReferenceCode,
			@GraphQLName("pageElementExternalReferenceCode") String
				pageElementExternalReferenceCode,
			@GraphQLName("pageElement") PageElement pageElement)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageElementResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageElementResource ->
				pageElementResource.
					patchSiteSiteByExternalReferenceCodePageElement(
						siteExternalReferenceCode,
						pageSpecificationExternalReferenceCode,
						pageExperienceExternalReferenceCode,
						pageElementExternalReferenceCode, pageElement));
	}

	@GraphQLField(
		description = "Adds a new fragment composition under a page element of an experience in a page specification of a site page. If successful, the response will contain the page element in which the fragment composition is converted."
	)
	public PageElement
			createSiteSiteByExternalReferenceCodePageElementFragmentComposition(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("pageSpecificationExternalReferenceCode") String
					pageSpecificationExternalReferenceCode,
				@GraphQLName("pageExperienceExternalReferenceCode") String
					pageExperienceExternalReferenceCode,
				@GraphQLName("pageElementExternalReferenceCode") String
					pageElementExternalReferenceCode,
				@GraphQLName("position") Integer position,
				@GraphQLName("fragmentComposition") FragmentComposition
					fragmentComposition)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageElementResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageElementResource ->
				pageElementResource.
					postSiteSiteByExternalReferenceCodePageElementFragmentComposition(
						siteExternalReferenceCode,
						pageSpecificationExternalReferenceCode,
						pageExperienceExternalReferenceCode,
						pageElementExternalReferenceCode, position,
						fragmentComposition));
	}

	@GraphQLField(
		description = "Adds a new page element to an experience in a page specification in draft status of a site page."
	)
	public PageElement
			createSiteSiteByExternalReferenceCodePageExperiencePageElement(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("pageSpecificationExternalReferenceCode") String
					pageSpecificationExternalReferenceCode,
				@GraphQLName("pageExperienceExternalReferenceCode") String
					pageExperienceExternalReferenceCode,
				@GraphQLName("pageElement") PageElement pageElement)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageElementResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageElementResource ->
				pageElementResource.
					postSiteSiteByExternalReferenceCodePageExperiencePageElement(
						siteExternalReferenceCode,
						pageSpecificationExternalReferenceCode,
						pageExperienceExternalReferenceCode, pageElement));
	}

	@GraphQLField(
		description = "Updates a page element within an experience of a specific page specification of a site page within a site."
	)
	public PageElement updateSiteSiteByExternalReferenceCodePageElement(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("pageSpecificationExternalReferenceCode") String
				pageSpecificationExternalReferenceCode,
			@GraphQLName("pageExperienceExternalReferenceCode") String
				pageExperienceExternalReferenceCode,
			@GraphQLName("pageElementExternalReferenceCode") String
				pageElementExternalReferenceCode,
			@GraphQLName("pageElement") PageElement pageElement)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageElementResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageElementResource ->
				pageElementResource.
					putSiteSiteByExternalReferenceCodePageElement(
						siteExternalReferenceCode,
						pageSpecificationExternalReferenceCode,
						pageExperienceExternalReferenceCode,
						pageElementExternalReferenceCode, pageElement));
	}

	@GraphQLField(
		description = "Deletes an experience of a specific page specification of a site page within a site. The default experience cannot be deleted."
	)
	public boolean deleteSiteSiteByExternalReferenceCodePageExperience(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("pageExperienceExternalReferenceCode") String
				pageExperienceExternalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_pageExperienceResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageExperienceResource ->
				pageExperienceResource.
					deleteSiteSiteByExternalReferenceCodePageExperience(
						siteExternalReferenceCode,
						pageExperienceExternalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates an experience of a specific page specification of a site page within a site. Updates only the fields received in the request body, leaving any other fields untouched."
	)
	public PageExperience patchSiteSiteByExternalReferenceCodePageExperience(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("pageExperienceExternalReferenceCode") String
				pageExperienceExternalReferenceCode,
			@GraphQLName("pageExperience") PageExperience pageExperience)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageExperienceResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageExperienceResource ->
				pageExperienceResource.
					patchSiteSiteByExternalReferenceCodePageExperience(
						siteExternalReferenceCode,
						pageExperienceExternalReferenceCode, pageExperience));
	}

	@GraphQLField(
		description = "Adds a new experience to a page specification of a site page."
	)
	public PageExperience
			createSiteSiteByExternalReferenceCodePageSpecificationPageExperience(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("pageSpecificationExternalReferenceCode") String
					pageSpecificationExternalReferenceCode,
				@GraphQLName("pageExperience") PageExperience pageExperience)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageExperienceResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageExperienceResource ->
				pageExperienceResource.
					postSiteSiteByExternalReferenceCodePageSpecificationPageExperience(
						siteExternalReferenceCode,
						pageSpecificationExternalReferenceCode,
						pageExperience));
	}

	@GraphQLField(
		description = "Updates an experience of a specific page specification of a site page within a site."
	)
	public PageExperience updateSiteSiteByExternalReferenceCodePageExperience(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("pageExperienceExternalReferenceCode") String
				pageExperienceExternalReferenceCode,
			@GraphQLName("pageExperience") PageExperience pageExperience)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageExperienceResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageExperienceResource ->
				pageExperienceResource.
					putSiteSiteByExternalReferenceCodePageExperience(
						siteExternalReferenceCode,
						pageExperienceExternalReferenceCode, pageExperience));
	}

	@GraphQLField(
		description = "Deletes a page rule within an experience of a specific page specification of a site page within a site."
	)
	public boolean deleteSiteSiteByExternalReferenceCodePageRule(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("pageRuleExternalReferenceCode") String
				pageRuleExternalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_pageRuleResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageRuleResource ->
				pageRuleResource.deleteSiteSiteByExternalReferenceCodePageRule(
					siteExternalReferenceCode, pageRuleExternalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates a page rule within an experience of a specific page specification of a site page within a site. Updates only the fields received in the request body, leaving any other fields untouched."
	)
	public PageRule patchSiteSiteByExternalReferenceCodePageRule(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("pageRuleExternalReferenceCode") String
				pageRuleExternalReferenceCode,
			@GraphQLName("pageRule") PageRule pageRule)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageRuleResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageRuleResource ->
				pageRuleResource.patchSiteSiteByExternalReferenceCodePageRule(
					siteExternalReferenceCode, pageRuleExternalReferenceCode,
					pageRule));
	}

	@GraphQLField(
		description = "Adds a new page rule to an experience in a page specification in draft status of a site page."
	)
	public PageRule createSiteSiteByExternalReferenceCodePageExperiencePageRule(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("pageExperienceExternalReferenceCode") String
				pageExperienceExternalReferenceCode,
			@GraphQLName("pageRule") PageRule pageRule)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageRuleResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageRuleResource ->
				pageRuleResource.
					postSiteSiteByExternalReferenceCodePageExperiencePageRule(
						siteExternalReferenceCode,
						pageExperienceExternalReferenceCode, pageRule));
	}

	@GraphQLField(
		description = "Updates a page rule within an experience of a specific page specification of a site page within a site."
	)
	public PageRule updateSiteSiteByExternalReferenceCodePageRule(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("pageRuleExternalReferenceCode") String
				pageRuleExternalReferenceCode,
			@GraphQLName("pageRule") PageRule pageRule)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageRuleResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageRuleResource ->
				pageRuleResource.putSiteSiteByExternalReferenceCodePageRule(
					siteExternalReferenceCode, pageRuleExternalReferenceCode,
					pageRule));
	}

	@GraphQLField(
		description = "Deletes a page rule action within a page rule of an experience of a specific page specification of a site page within a site."
	)
	public boolean deleteSiteSiteByExternalReferenceCodePageRuleAction(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("pageRuleActionExternalReferenceCode") String
				pageRuleActionExternalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_pageRuleActionResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageRuleActionResource ->
				pageRuleActionResource.
					deleteSiteSiteByExternalReferenceCodePageRuleAction(
						siteExternalReferenceCode,
						pageRuleActionExternalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates a page rule action within a page rule of an experience of a specific page specification of a site page within a site. Updates only the fields received in the request body, leaving any other fields untouched."
	)
	public PageRuleAction patchSiteSiteByExternalReferenceCodePageRuleAction(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("pageRuleActionExternalReferenceCode") String
				pageRuleActionExternalReferenceCode,
			@GraphQLName("pageRuleAction") PageRuleAction pageRuleAction)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageRuleActionResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageRuleActionResource ->
				pageRuleActionResource.
					patchSiteSiteByExternalReferenceCodePageRuleAction(
						siteExternalReferenceCode,
						pageRuleActionExternalReferenceCode, pageRuleAction));
	}

	@GraphQLField(
		description = "Adds a new page rule action to a page rule in an experience in a page specification in draft status of a site page."
	)
	public PageRuleAction
			createSiteSiteByExternalReferenceCodePageRulePageRuleAction(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("pageRuleExternalReferenceCode") String
					pageRuleExternalReferenceCode,
				@GraphQLName("pageRuleAction") PageRuleAction pageRuleAction)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageRuleActionResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageRuleActionResource ->
				pageRuleActionResource.
					postSiteSiteByExternalReferenceCodePageRulePageRuleAction(
						siteExternalReferenceCode,
						pageRuleExternalReferenceCode, pageRuleAction));
	}

	@GraphQLField(
		description = "Updates a page rule action within a page rule of an experience of a specific page specification of a site page within a site."
	)
	public PageRuleAction updateSiteSiteByExternalReferenceCodePageRuleAction(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("pageRuleActionExternalReferenceCode") String
				pageRuleActionExternalReferenceCode,
			@GraphQLName("pageRuleAction") PageRuleAction pageRuleAction)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageRuleActionResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageRuleActionResource ->
				pageRuleActionResource.
					putSiteSiteByExternalReferenceCodePageRuleAction(
						siteExternalReferenceCode,
						pageRuleActionExternalReferenceCode, pageRuleAction));
	}

	@GraphQLField(
		description = "Deletes a page rule condition within a page rule of an experience of a specific page specification of a site page within a site."
	)
	public boolean deleteSiteSiteByExternalReferenceCodePageRuleCondition(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("pageRuleConditionExternalReferenceCode") String
				pageRuleConditionExternalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_pageRuleConditionResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageRuleConditionResource ->
				pageRuleConditionResource.
					deleteSiteSiteByExternalReferenceCodePageRuleCondition(
						siteExternalReferenceCode,
						pageRuleConditionExternalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates a page rule condition within a page rule of an experience of a specific page specification of a site page within a site. Updates only the fields received in the request body, leaving any other fields untouched."
	)
	public PageRuleCondition
			patchSiteSiteByExternalReferenceCodePageRuleCondition(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("pageRuleConditionExternalReferenceCode") String
					pageRuleConditionExternalReferenceCode,
				@GraphQLName("pageRuleCondition") PageRuleCondition
					pageRuleCondition)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageRuleConditionResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageRuleConditionResource ->
				pageRuleConditionResource.
					patchSiteSiteByExternalReferenceCodePageRuleCondition(
						siteExternalReferenceCode,
						pageRuleConditionExternalReferenceCode,
						pageRuleCondition));
	}

	@GraphQLField(
		description = "Adds a new page rule condition to a page rule in an experience in a page specification in draft status of a site page."
	)
	public PageRuleCondition
			createSiteSiteByExternalReferenceCodePageRulePageRuleCondition(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("pageRuleExternalReferenceCode") String
					pageRuleExternalReferenceCode,
				@GraphQLName("pageRuleCondition") PageRuleCondition
					pageRuleCondition)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageRuleConditionResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageRuleConditionResource ->
				pageRuleConditionResource.
					postSiteSiteByExternalReferenceCodePageRulePageRuleCondition(
						siteExternalReferenceCode,
						pageRuleExternalReferenceCode, pageRuleCondition));
	}

	@GraphQLField(
		description = "Updates a page rule condition within a page rule of an experience of a specific page specification of a site page within a site."
	)
	public PageRuleCondition
			updateSiteSiteByExternalReferenceCodePageRuleCondition(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("pageRuleConditionExternalReferenceCode") String
					pageRuleConditionExternalReferenceCode,
				@GraphQLName("pageRuleCondition") PageRuleCondition
					pageRuleCondition)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageRuleConditionResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageRuleConditionResource ->
				pageRuleConditionResource.
					putSiteSiteByExternalReferenceCodePageRuleCondition(
						siteExternalReferenceCode,
						pageRuleConditionExternalReferenceCode,
						pageRuleCondition));
	}

	@GraphQLField(description = "Deletes a page specification of a site page.")
	public boolean deleteSiteSiteByExternalReferenceCodePageSpecification(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("pageSpecificationExternalReferenceCode") String
				pageSpecificationExternalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_pageSpecificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageSpecificationResource ->
				pageSpecificationResource.
					deleteSiteSiteByExternalReferenceCodePageSpecification(
						siteExternalReferenceCode,
						pageSpecificationExternalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates a page specification of a site page. Updates only the fields received in the request body, leaving any other fields untouched."
	)
	public PageSpecification
			patchSiteSiteByExternalReferenceCodePageSpecification(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("pageSpecificationExternalReferenceCode") String
					pageSpecificationExternalReferenceCode,
				@GraphQLName("pageSpecification") PageSpecification
					pageSpecification)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageSpecificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageSpecificationResource ->
				pageSpecificationResource.
					patchSiteSiteByExternalReferenceCodePageSpecification(
						siteExternalReferenceCode,
						pageSpecificationExternalReferenceCode,
						pageSpecification));
	}

	@GraphQLField(
		description = "Publishes a page specification in draft status of a site page."
	)
	public PageSpecification
			createSiteSiteByExternalReferenceCodePageSpecificationPublish(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("pageSpecificationExternalReferenceCode") String
					pageSpecificationExternalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageSpecificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageSpecificationResource ->
				pageSpecificationResource.
					postSiteSiteByExternalReferenceCodePageSpecificationPublish(
						siteExternalReferenceCode,
						pageSpecificationExternalReferenceCode));
	}

	@GraphQLField(description = "Updates a page specification of a site page.")
	public PageSpecification
			updateSiteSiteByExternalReferenceCodePageSpecification(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("pageSpecificationExternalReferenceCode") String
					pageSpecificationExternalReferenceCode,
				@GraphQLName("pageSpecification") PageSpecification
					pageSpecification)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageSpecificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageSpecificationResource ->
				pageSpecificationResource.
					putSiteSiteByExternalReferenceCodePageSpecification(
						siteExternalReferenceCode,
						pageSpecificationExternalReferenceCode,
						pageSpecification));
	}

	@GraphQLField(description = "Deletes a specific page template of a site.")
	public boolean deleteSiteSiteByExternalReferenceCodePageTemplate(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("pageTemplateExternalReferenceCode") String
				pageTemplateExternalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_pageTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageTemplateResource ->
				pageTemplateResource.
					deleteSiteSiteByExternalReferenceCodePageTemplate(
						siteExternalReferenceCode,
						pageTemplateExternalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates only the fields received in the request body, leaving any other fields untouched."
	)
	public PageTemplate patchSiteSiteByExternalReferenceCodePageTemplate(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("pageTemplateExternalReferenceCode") String
				pageTemplateExternalReferenceCode,
			@GraphQLName("pageTemplate") PageTemplate pageTemplate)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageTemplateResource ->
				pageTemplateResource.
					patchSiteSiteByExternalReferenceCodePageTemplate(
						siteExternalReferenceCode,
						pageTemplateExternalReferenceCode, pageTemplate));
	}

	@GraphQLField(description = "Adds a new page template")
	public PageTemplate createSiteSiteByExternalReferenceCodePageTemplate(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("pageTemplate") PageTemplate pageTemplate)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageTemplateResource ->
				pageTemplateResource.
					postSiteSiteByExternalReferenceCodePageTemplate(
						siteExternalReferenceCode, pageTemplate));
	}

	@GraphQLField(
		description = "Adds a new page specification in draft status to a page template."
	)
	public ContentPageSpecification
			createSiteSiteByExternalReferenceCodePageTemplatePageSpecification(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("pageTemplateExternalReferenceCode") String
					pageTemplateExternalReferenceCode,
				@GraphQLName("contentPageSpecification")
					ContentPageSpecification contentPageSpecification)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageTemplateResource ->
				pageTemplateResource.
					postSiteSiteByExternalReferenceCodePageTemplatePageSpecification(
						siteExternalReferenceCode,
						pageTemplateExternalReferenceCode,
						contentPageSpecification));
	}

	@GraphQLField(
		description = "Adds a new page template in draft status to a page template set."
	)
	public PageTemplate
			createSiteSiteByExternalReferenceCodePageTemplateSetPageTemplate(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("pageTemplateSetExternalReferenceCode") String
					pageTemplateSetExternalReferenceCode,
				@GraphQLName("pageTemplate") PageTemplate pageTemplate)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageTemplateResource ->
				pageTemplateResource.
					postSiteSiteByExternalReferenceCodePageTemplateSetPageTemplate(
						siteExternalReferenceCode,
						pageTemplateSetExternalReferenceCode, pageTemplate));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateSitePageTemplatePermissionsPage(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("pageTemplateExternalReferenceCode") String
					pageTemplateExternalReferenceCode,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageTemplateResource -> {
				Page paginationPage =
					pageTemplateResource.putSitePageTemplatePermissionsPage(
						siteExternalReferenceCode,
						pageTemplateExternalReferenceCode, permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField(
		description = "Updates the page template with the given external reference code, or creates it if it does not exist."
	)
	public PageTemplate updateSiteSiteByExternalReferenceCodePageTemplate(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("pageTemplateExternalReferenceCode") String
				pageTemplateExternalReferenceCode,
			@GraphQLName("pageTemplate") PageTemplate pageTemplate)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageTemplateResource ->
				pageTemplateResource.
					putSiteSiteByExternalReferenceCodePageTemplate(
						siteExternalReferenceCode,
						pageTemplateExternalReferenceCode, pageTemplate));
	}

	@GraphQLField(
		description = "Deletes a specific page template set of a site."
	)
	public boolean deleteSiteSiteByExternalReferenceCodePageTemplateSet(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("pageTemplateSetExternalReferenceCode") String
				pageTemplateSetExternalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_pageTemplateSetResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageTemplateSetResource ->
				pageTemplateSetResource.
					deleteSiteSiteByExternalReferenceCodePageTemplateSet(
						siteExternalReferenceCode,
						pageTemplateSetExternalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates only the fields received in the request body, leaving any other fields untouched."
	)
	public PageTemplateSet patchSiteSiteByExternalReferenceCodePageTemplateSet(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("pageTemplateSetExternalReferenceCode") String
				pageTemplateSetExternalReferenceCode,
			@GraphQLName("pageTemplateSet") PageTemplateSet pageTemplateSet)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageTemplateSetResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageTemplateSetResource ->
				pageTemplateSetResource.
					patchSiteSiteByExternalReferenceCodePageTemplateSet(
						siteExternalReferenceCode,
						pageTemplateSetExternalReferenceCode, pageTemplateSet));
	}

	@GraphQLField(description = "Adds a new page template set")
	public PageTemplateSet createSiteSiteByExternalReferenceCodePageTemplateSet(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("pageTemplateSet") PageTemplateSet pageTemplateSet)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageTemplateSetResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageTemplateSetResource ->
				pageTemplateSetResource.
					postSiteSiteByExternalReferenceCodePageTemplateSet(
						siteExternalReferenceCode, pageTemplateSet));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateSitePageTemplateSetPermissionsPage(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("pageTemplateSetExternalReferenceCode") String
					pageTemplateSetExternalReferenceCode,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageTemplateSetResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageTemplateSetResource -> {
				Page paginationPage =
					pageTemplateSetResource.
						putSitePageTemplateSetPermissionsPage(
							siteExternalReferenceCode,
							pageTemplateSetExternalReferenceCode, permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField(
		description = "Updates the page template set with the given external reference code, or creates it if it does not exist."
	)
	public PageTemplateSet updateSiteSiteByExternalReferenceCodePageTemplateSet(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("pageTemplateSetExternalReferenceCode") String
				pageTemplateSetExternalReferenceCode,
			@GraphQLName("pageTemplateSet") PageTemplateSet pageTemplateSet)
		throws Exception {

		return _applyComponentServiceObjects(
			_pageTemplateSetResourceComponentServiceObjects,
			this::_populateResourceContext,
			pageTemplateSetResource ->
				pageTemplateSetResource.
					putSiteSiteByExternalReferenceCodePageTemplateSet(
						siteExternalReferenceCode,
						pageTemplateSetExternalReferenceCode, pageTemplateSet));
	}

	@GraphQLField(description = "Deletes a specific public page of a site.")
	public boolean deleteSiteSiteByExternalReferenceCodeSitePage(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("sitePageExternalReferenceCode") String
				sitePageExternalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_sitePageResourceComponentServiceObjects,
			this::_populateResourceContext,
			sitePageResource ->
				sitePageResource.deleteSiteSiteByExternalReferenceCodeSitePage(
					siteExternalReferenceCode, sitePageExternalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates only the fields received in the request body, leaving any other fields untouched."
	)
	public SitePage patchSiteSiteByExternalReferenceCodeSitePage(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("sitePageExternalReferenceCode") String
				sitePageExternalReferenceCode,
			@GraphQLName("sitePage") SitePage sitePage)
		throws Exception {

		return _applyComponentServiceObjects(
			_sitePageResourceComponentServiceObjects,
			this::_populateResourceContext,
			sitePageResource ->
				sitePageResource.patchSiteSiteByExternalReferenceCodeSitePage(
					siteExternalReferenceCode, sitePageExternalReferenceCode,
					sitePage));
	}

	@GraphQLField(description = "Adds a new site page")
	public SitePage createByExternalReferenceCodeSitePage(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("sitePage") SitePage sitePage)
		throws Exception {

		return _applyComponentServiceObjects(
			_sitePageResourceComponentServiceObjects,
			this::_populateResourceContext,
			sitePageResource ->
				sitePageResource.postByExternalReferenceCodeSitePage(
					siteExternalReferenceCode, sitePage));
	}

	@GraphQLField(description = "Adds a new page specification to a site page.")
	public ContentPageSpecification
			createSiteSiteByExternalReferenceCodeSitePagePageSpecification(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("sitePageExternalReferenceCode") String
					sitePageExternalReferenceCode,
				@GraphQLName("contentPageSpecification")
					ContentPageSpecification contentPageSpecification)
		throws Exception {

		return _applyComponentServiceObjects(
			_sitePageResourceComponentServiceObjects,
			this::_populateResourceContext,
			sitePageResource ->
				sitePageResource.
					postSiteSiteByExternalReferenceCodeSitePagePageSpecification(
						siteExternalReferenceCode,
						sitePageExternalReferenceCode,
						contentPageSpecification));
	}

	@GraphQLField(
		description = "Updates the site page with the given external reference code, or creates it if it does not exist."
	)
	public SitePage updateSiteSiteByExternalReferenceCodeSitePage(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("sitePageExternalReferenceCode") String
				sitePageExternalReferenceCode,
			@GraphQLName("sitePage") SitePage sitePage)
		throws Exception {

		return _applyComponentServiceObjects(
			_sitePageResourceComponentServiceObjects,
			this::_populateResourceContext,
			sitePageResource ->
				sitePageResource.putSiteSiteByExternalReferenceCodeSitePage(
					siteExternalReferenceCode, sitePageExternalReferenceCode,
					sitePage));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateSiteSitePagePermissionsPage(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("sitePageExternalReferenceCode") String
					sitePageExternalReferenceCode,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_sitePageResourceComponentServiceObjects,
			this::_populateResourceContext,
			sitePageResource -> {
				Page paginationPage =
					sitePageResource.putSiteSitePagePermissionsPage(
						siteExternalReferenceCode,
						sitePageExternalReferenceCode, permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField(description = "Deletes a specific utility page of a site.")
	public boolean deleteSiteSiteByExternalReferenceCodeUtilityPage(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("utilityPageExternalReferenceCode") String
				utilityPageExternalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_utilityPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			utilityPageResource ->
				utilityPageResource.
					deleteSiteSiteByExternalReferenceCodeUtilityPage(
						siteExternalReferenceCode,
						utilityPageExternalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates only the fields received in the request body, leaving any other fields untouched."
	)
	public UtilityPage patchSiteSiteByExternalReferenceCodeUtilityPage(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("utilityPageExternalReferenceCode") String
				utilityPageExternalReferenceCode,
			@GraphQLName("utilityPage") UtilityPage utilityPage)
		throws Exception {

		return _applyComponentServiceObjects(
			_utilityPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			utilityPageResource ->
				utilityPageResource.
					patchSiteSiteByExternalReferenceCodeUtilityPage(
						siteExternalReferenceCode,
						utilityPageExternalReferenceCode, utilityPage));
	}

	@GraphQLField(description = "Adds a new utility page")
	public UtilityPage createSiteSiteByExternalReferenceCodeUtilityPage(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("utilityPage") UtilityPage utilityPage)
		throws Exception {

		return _applyComponentServiceObjects(
			_utilityPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			utilityPageResource ->
				utilityPageResource.
					postSiteSiteByExternalReferenceCodeUtilityPage(
						siteExternalReferenceCode, utilityPage));
	}

	@GraphQLField(
		description = "Adds a new page specification to a utility page."
	)
	public ContentPageSpecification
			createSiteSiteByExternalReferenceCodeUtilityPagePageSpecification(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("utilityPageExternalReferenceCode") String
					utilityPageExternalReferenceCode,
				@GraphQLName("contentPageSpecification")
					ContentPageSpecification contentPageSpecification)
		throws Exception {

		return _applyComponentServiceObjects(
			_utilityPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			utilityPageResource ->
				utilityPageResource.
					postSiteSiteByExternalReferenceCodeUtilityPagePageSpecification(
						siteExternalReferenceCode,
						utilityPageExternalReferenceCode,
						contentPageSpecification));
	}

	@GraphQLField(
		description = "Updates the utility page with the given external reference code, or creates it if it does not exist."
	)
	public UtilityPage updateSiteSiteByExternalReferenceCodeUtilityPage(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("utilityPageExternalReferenceCode") String
				utilityPageExternalReferenceCode,
			@GraphQLName("utilityPage") UtilityPage utilityPage)
		throws Exception {

		return _applyComponentServiceObjects(
			_utilityPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			utilityPageResource ->
				utilityPageResource.
					putSiteSiteByExternalReferenceCodeUtilityPage(
						siteExternalReferenceCode,
						utilityPageExternalReferenceCode, utilityPage));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateSiteUtilityPagePermissionsPage(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("utilityPageExternalReferenceCode") String
					utilityPageExternalReferenceCode,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_utilityPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			utilityPageResource -> {
				Page paginationPage =
					utilityPageResource.putSiteUtilityPagePermissionsPage(
						siteExternalReferenceCode,
						utilityPageExternalReferenceCode, permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField(
		description = "Deletes a widget instance of a specific widget page or widget page template within a site."
	)
	public boolean
			deleteSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("sitePageExternalReferenceCode") String
					sitePageExternalReferenceCode,
				@GraphQLName("widgetInstanceExternalReferenceCode") String
					widgetInstanceExternalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_widgetPageWidgetInstanceResourceComponentServiceObjects,
			this::_populateResourceContext,
			widgetPageWidgetInstanceResource ->
				widgetPageWidgetInstanceResource.
					deleteSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode(
						siteExternalReferenceCode,
						sitePageExternalReferenceCode,
						widgetInstanceExternalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates a widget instance of a widget page or widget page template within a site. Updates only the fields received in the request body, leaving any other fields untouched."
	)
	public WidgetPageWidgetInstance
			patchSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("sitePageExternalReferenceCode") String
					sitePageExternalReferenceCode,
				@GraphQLName("widgetInstanceExternalReferenceCode") String
					widgetInstanceExternalReferenceCode,
				@GraphQLName("widgetPageWidgetInstance")
					WidgetPageWidgetInstance widgetPageWidgetInstance)
		throws Exception {

		return _applyComponentServiceObjects(
			_widgetPageWidgetInstanceResourceComponentServiceObjects,
			this::_populateResourceContext,
			widgetPageWidgetInstanceResource ->
				widgetPageWidgetInstanceResource.
					patchSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode(
						siteExternalReferenceCode,
						sitePageExternalReferenceCode,
						widgetInstanceExternalReferenceCode,
						widgetPageWidgetInstance));
	}

	@GraphQLField(description = "Adds a new widget instance to a widget page.")
	public WidgetPageWidgetInstance
			createSiteSiteByExternalReferenceCodeSitePageWidgetInstance(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("sitePageExternalReferenceCode") String
					sitePageExternalReferenceCode,
				@GraphQLName("widgetPageWidgetInstance")
					WidgetPageWidgetInstance widgetPageWidgetInstance)
		throws Exception {

		return _applyComponentServiceObjects(
			_widgetPageWidgetInstanceResourceComponentServiceObjects,
			this::_populateResourceContext,
			widgetPageWidgetInstanceResource ->
				widgetPageWidgetInstanceResource.
					postSiteSiteByExternalReferenceCodeSitePageWidgetInstance(
						siteExternalReferenceCode,
						sitePageExternalReferenceCode,
						widgetPageWidgetInstance));
	}

	@GraphQLField(
		description = "Updates a widget instance of a widget page or widget page template within a site."
	)
	public WidgetPageWidgetInstance
			updateSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode(
				@GraphQLName("siteExternalReferenceCode") @NotEmpty String
					siteExternalReferenceCode,
				@GraphQLName("sitePageExternalReferenceCode") String
					sitePageExternalReferenceCode,
				@GraphQLName("widgetInstanceExternalReferenceCode") String
					widgetInstanceExternalReferenceCode,
				@GraphQLName("widgetPageWidgetInstance")
					WidgetPageWidgetInstance widgetPageWidgetInstance)
		throws Exception {

		return _applyComponentServiceObjects(
			_widgetPageWidgetInstanceResourceComponentServiceObjects,
			this::_populateResourceContext,
			widgetPageWidgetInstanceResource ->
				widgetPageWidgetInstanceResource.
					putSiteSiteByExternalReferenceCodeWidgetInstanceWidgetInstanceExternalReferenceCode(
						siteExternalReferenceCode,
						sitePageExternalReferenceCode,
						widgetInstanceExternalReferenceCode,
						widgetPageWidgetInstance));
	}

	private <T, R, E1 extends Throwable, E2 extends Throwable> R
			_applyComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeFunction<T, R, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			return unsafeFunction.apply(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private <T, E1 extends Throwable, E2 extends Throwable> void
			_applyVoidComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeConsumer<T, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			unsafeFunction.accept(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private void _populateResourceContext(
			DisplayPageTemplateResource displayPageTemplateResource)
		throws Exception {

		displayPageTemplateResource.setContextAcceptLanguage(_acceptLanguage);
		displayPageTemplateResource.setContextCompany(_company);
		displayPageTemplateResource.setContextHttpServletRequest(
			_httpServletRequest);
		displayPageTemplateResource.setContextHttpServletResponse(
			_httpServletResponse);
		displayPageTemplateResource.setContextUriInfo(_uriInfo);
		displayPageTemplateResource.setContextUser(_user);
		displayPageTemplateResource.setGroupLocalService(_groupLocalService);
		displayPageTemplateResource.setRoleLocalService(_roleLocalService);

		displayPageTemplateResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		displayPageTemplateResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			DisplayPageTemplateFolderResource displayPageTemplateFolderResource)
		throws Exception {

		displayPageTemplateFolderResource.setContextAcceptLanguage(
			_acceptLanguage);
		displayPageTemplateFolderResource.setContextCompany(_company);
		displayPageTemplateFolderResource.setContextHttpServletRequest(
			_httpServletRequest);
		displayPageTemplateFolderResource.setContextHttpServletResponse(
			_httpServletResponse);
		displayPageTemplateFolderResource.setContextUriInfo(_uriInfo);
		displayPageTemplateFolderResource.setContextUser(_user);
		displayPageTemplateFolderResource.setGroupLocalService(
			_groupLocalService);
		displayPageTemplateFolderResource.setRoleLocalService(
			_roleLocalService);

		displayPageTemplateFolderResource.
			setVulcanBatchEngineExportTaskResource(
				_vulcanBatchEngineExportTaskResource);

		displayPageTemplateFolderResource.
			setVulcanBatchEngineImportTaskResource(
				_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			FragmentCompositionResource fragmentCompositionResource)
		throws Exception {

		fragmentCompositionResource.setContextAcceptLanguage(_acceptLanguage);
		fragmentCompositionResource.setContextCompany(_company);
		fragmentCompositionResource.setContextHttpServletRequest(
			_httpServletRequest);
		fragmentCompositionResource.setContextHttpServletResponse(
			_httpServletResponse);
		fragmentCompositionResource.setContextUriInfo(_uriInfo);
		fragmentCompositionResource.setContextUser(_user);
		fragmentCompositionResource.setGroupLocalService(_groupLocalService);
		fragmentCompositionResource.setRoleLocalService(_roleLocalService);

		fragmentCompositionResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		fragmentCompositionResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(MasterPageResource masterPageResource)
		throws Exception {

		masterPageResource.setContextAcceptLanguage(_acceptLanguage);
		masterPageResource.setContextCompany(_company);
		masterPageResource.setContextHttpServletRequest(_httpServletRequest);
		masterPageResource.setContextHttpServletResponse(_httpServletResponse);
		masterPageResource.setContextUriInfo(_uriInfo);
		masterPageResource.setContextUser(_user);
		masterPageResource.setGroupLocalService(_groupLocalService);
		masterPageResource.setRoleLocalService(_roleLocalService);

		masterPageResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		masterPageResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			PageElementResource pageElementResource)
		throws Exception {

		pageElementResource.setContextAcceptLanguage(_acceptLanguage);
		pageElementResource.setContextCompany(_company);
		pageElementResource.setContextHttpServletRequest(_httpServletRequest);
		pageElementResource.setContextHttpServletResponse(_httpServletResponse);
		pageElementResource.setContextUriInfo(_uriInfo);
		pageElementResource.setContextUser(_user);
		pageElementResource.setGroupLocalService(_groupLocalService);
		pageElementResource.setRoleLocalService(_roleLocalService);

		pageElementResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		pageElementResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			PageExperienceResource pageExperienceResource)
		throws Exception {

		pageExperienceResource.setContextAcceptLanguage(_acceptLanguage);
		pageExperienceResource.setContextCompany(_company);
		pageExperienceResource.setContextHttpServletRequest(
			_httpServletRequest);
		pageExperienceResource.setContextHttpServletResponse(
			_httpServletResponse);
		pageExperienceResource.setContextUriInfo(_uriInfo);
		pageExperienceResource.setContextUser(_user);
		pageExperienceResource.setGroupLocalService(_groupLocalService);
		pageExperienceResource.setRoleLocalService(_roleLocalService);

		pageExperienceResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		pageExperienceResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(PageRuleResource pageRuleResource)
		throws Exception {

		pageRuleResource.setContextAcceptLanguage(_acceptLanguage);
		pageRuleResource.setContextCompany(_company);
		pageRuleResource.setContextHttpServletRequest(_httpServletRequest);
		pageRuleResource.setContextHttpServletResponse(_httpServletResponse);
		pageRuleResource.setContextUriInfo(_uriInfo);
		pageRuleResource.setContextUser(_user);
		pageRuleResource.setGroupLocalService(_groupLocalService);
		pageRuleResource.setRoleLocalService(_roleLocalService);

		pageRuleResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		pageRuleResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			PageRuleActionResource pageRuleActionResource)
		throws Exception {

		pageRuleActionResource.setContextAcceptLanguage(_acceptLanguage);
		pageRuleActionResource.setContextCompany(_company);
		pageRuleActionResource.setContextHttpServletRequest(
			_httpServletRequest);
		pageRuleActionResource.setContextHttpServletResponse(
			_httpServletResponse);
		pageRuleActionResource.setContextUriInfo(_uriInfo);
		pageRuleActionResource.setContextUser(_user);
		pageRuleActionResource.setGroupLocalService(_groupLocalService);
		pageRuleActionResource.setRoleLocalService(_roleLocalService);

		pageRuleActionResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		pageRuleActionResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			PageRuleConditionResource pageRuleConditionResource)
		throws Exception {

		pageRuleConditionResource.setContextAcceptLanguage(_acceptLanguage);
		pageRuleConditionResource.setContextCompany(_company);
		pageRuleConditionResource.setContextHttpServletRequest(
			_httpServletRequest);
		pageRuleConditionResource.setContextHttpServletResponse(
			_httpServletResponse);
		pageRuleConditionResource.setContextUriInfo(_uriInfo);
		pageRuleConditionResource.setContextUser(_user);
		pageRuleConditionResource.setGroupLocalService(_groupLocalService);
		pageRuleConditionResource.setRoleLocalService(_roleLocalService);

		pageRuleConditionResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		pageRuleConditionResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			PageSpecificationResource pageSpecificationResource)
		throws Exception {

		pageSpecificationResource.setContextAcceptLanguage(_acceptLanguage);
		pageSpecificationResource.setContextCompany(_company);
		pageSpecificationResource.setContextHttpServletRequest(
			_httpServletRequest);
		pageSpecificationResource.setContextHttpServletResponse(
			_httpServletResponse);
		pageSpecificationResource.setContextUriInfo(_uriInfo);
		pageSpecificationResource.setContextUser(_user);
		pageSpecificationResource.setGroupLocalService(_groupLocalService);
		pageSpecificationResource.setRoleLocalService(_roleLocalService);

		pageSpecificationResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		pageSpecificationResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			PageTemplateResource pageTemplateResource)
		throws Exception {

		pageTemplateResource.setContextAcceptLanguage(_acceptLanguage);
		pageTemplateResource.setContextCompany(_company);
		pageTemplateResource.setContextHttpServletRequest(_httpServletRequest);
		pageTemplateResource.setContextHttpServletResponse(
			_httpServletResponse);
		pageTemplateResource.setContextUriInfo(_uriInfo);
		pageTemplateResource.setContextUser(_user);
		pageTemplateResource.setGroupLocalService(_groupLocalService);
		pageTemplateResource.setRoleLocalService(_roleLocalService);

		pageTemplateResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		pageTemplateResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			PageTemplateSetResource pageTemplateSetResource)
		throws Exception {

		pageTemplateSetResource.setContextAcceptLanguage(_acceptLanguage);
		pageTemplateSetResource.setContextCompany(_company);
		pageTemplateSetResource.setContextHttpServletRequest(
			_httpServletRequest);
		pageTemplateSetResource.setContextHttpServletResponse(
			_httpServletResponse);
		pageTemplateSetResource.setContextUriInfo(_uriInfo);
		pageTemplateSetResource.setContextUser(_user);
		pageTemplateSetResource.setGroupLocalService(_groupLocalService);
		pageTemplateSetResource.setRoleLocalService(_roleLocalService);

		pageTemplateSetResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		pageTemplateSetResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(SitePageResource sitePageResource)
		throws Exception {

		sitePageResource.setContextAcceptLanguage(_acceptLanguage);
		sitePageResource.setContextCompany(_company);
		sitePageResource.setContextHttpServletRequest(_httpServletRequest);
		sitePageResource.setContextHttpServletResponse(_httpServletResponse);
		sitePageResource.setContextUriInfo(_uriInfo);
		sitePageResource.setContextUser(_user);
		sitePageResource.setGroupLocalService(_groupLocalService);
		sitePageResource.setRoleLocalService(_roleLocalService);

		sitePageResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		sitePageResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			UtilityPageResource utilityPageResource)
		throws Exception {

		utilityPageResource.setContextAcceptLanguage(_acceptLanguage);
		utilityPageResource.setContextCompany(_company);
		utilityPageResource.setContextHttpServletRequest(_httpServletRequest);
		utilityPageResource.setContextHttpServletResponse(_httpServletResponse);
		utilityPageResource.setContextUriInfo(_uriInfo);
		utilityPageResource.setContextUser(_user);
		utilityPageResource.setGroupLocalService(_groupLocalService);
		utilityPageResource.setRoleLocalService(_roleLocalService);

		utilityPageResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		utilityPageResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			WidgetPageWidgetInstanceResource widgetPageWidgetInstanceResource)
		throws Exception {

		widgetPageWidgetInstanceResource.setContextAcceptLanguage(
			_acceptLanguage);
		widgetPageWidgetInstanceResource.setContextCompany(_company);
		widgetPageWidgetInstanceResource.setContextHttpServletRequest(
			_httpServletRequest);
		widgetPageWidgetInstanceResource.setContextHttpServletResponse(
			_httpServletResponse);
		widgetPageWidgetInstanceResource.setContextUriInfo(_uriInfo);
		widgetPageWidgetInstanceResource.setContextUser(_user);
		widgetPageWidgetInstanceResource.setGroupLocalService(
			_groupLocalService);
		widgetPageWidgetInstanceResource.setRoleLocalService(_roleLocalService);

		widgetPageWidgetInstanceResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		widgetPageWidgetInstanceResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private static ComponentServiceObjects<DisplayPageTemplateResource>
		_displayPageTemplateResourceComponentServiceObjects;
	private static ComponentServiceObjects<DisplayPageTemplateFolderResource>
		_displayPageTemplateFolderResourceComponentServiceObjects;
	private static ComponentServiceObjects<FragmentCompositionResource>
		_fragmentCompositionResourceComponentServiceObjects;
	private static ComponentServiceObjects<MasterPageResource>
		_masterPageResourceComponentServiceObjects;
	private static ComponentServiceObjects<PageElementResource>
		_pageElementResourceComponentServiceObjects;
	private static ComponentServiceObjects<PageExperienceResource>
		_pageExperienceResourceComponentServiceObjects;
	private static ComponentServiceObjects<PageRuleResource>
		_pageRuleResourceComponentServiceObjects;
	private static ComponentServiceObjects<PageRuleActionResource>
		_pageRuleActionResourceComponentServiceObjects;
	private static ComponentServiceObjects<PageRuleConditionResource>
		_pageRuleConditionResourceComponentServiceObjects;
	private static ComponentServiceObjects<PageSpecificationResource>
		_pageSpecificationResourceComponentServiceObjects;
	private static ComponentServiceObjects<PageTemplateResource>
		_pageTemplateResourceComponentServiceObjects;
	private static ComponentServiceObjects<PageTemplateSetResource>
		_pageTemplateSetResourceComponentServiceObjects;
	private static ComponentServiceObjects<SitePageResource>
		_sitePageResourceComponentServiceObjects;
	private static ComponentServiceObjects<UtilityPageResource>
		_utilityPageResourceComponentServiceObjects;
	private static ComponentServiceObjects<WidgetPageWidgetInstanceResource>
		_widgetPageWidgetInstanceResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private com.liferay.portal.kernel.model.Company _company;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]>
		_sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;
	private VulcanBatchEngineExportTaskResource
		_vulcanBatchEngineExportTaskResource;
	private VulcanBatchEngineImportTaskResource
		_vulcanBatchEngineImportTaskResource;

}