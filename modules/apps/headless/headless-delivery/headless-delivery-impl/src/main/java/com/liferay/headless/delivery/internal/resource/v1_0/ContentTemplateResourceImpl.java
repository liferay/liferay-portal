/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.resource.v1_0;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateService;
import com.liferay.headless.delivery.dto.v1_0.ContentTemplate;
import com.liferay.headless.delivery.internal.dto.v1_0.util.ContentTemplateUtil;
import com.liferay.headless.delivery.internal.odata.entity.v1_0.ContentTemplateEntityModel;
import com.liferay.headless.delivery.resource.v1_0.ContentTemplateResource;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.core.MultivaluedMap;

import java.util.Collections;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/content-template.properties",
	scope = ServiceScope.PROTOTYPE, service = ContentTemplateResource.class
)
public class ContentTemplateResourceImpl
	extends BaseContentTemplateResourceImpl {

	@Override
	public Page<ContentTemplate> getAssetLibraryContentTemplatesPage(
			Long assetLibraryId, String search, Aggregation aggregation,
			Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		return _getContentTemplatesPage(
			Collections.singletonMap(
				"get",
				addAction(
					ActionKeys.MANAGE_LAYOUTS,
					"getAssetLibraryContentTemplatesPage",
					Group.class.getName(), assetLibraryId)),
			assetLibraryId, search, aggregation, filter, pagination, sorts);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public ContentTemplate getSiteContentTemplate(
			Long siteId, String contentTemplateId)
		throws Exception {

		DDMTemplate ddmTemplate = _ddmTemplateService.getTemplate(
			siteId, _classNameLocalService.getClassNameId(DDMStructure.class),
			contentTemplateId);

		return ContentTemplateUtil.toContentTemplate(
			ddmTemplate, _getDTOConverterContext(ddmTemplate),
			groupLocalService, _portal, _userLocalService);
	}

	@Override
	public Page<ContentTemplate> getSiteContentTemplatesPage(
			Long siteId, String search, Aggregation aggregation, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return _getContentTemplatesPage(
			Collections.singletonMap(
				"get",
				addAction(
					ActionKeys.MANAGE_LAYOUTS, "getSiteContentTemplatesPage",
					Group.class.getName(), siteId)),
			siteId, search, aggregation, filter, pagination, sorts);
	}

	private Page<ContentTemplate> _getContentTemplatesPage(
			Map<String, Map<String, String>> actions, Long assetLibraryId,
			String search, Aggregation aggregation, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			actions,
			booleanQuery -> {
			},
			filter, DDMTemplate.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.addVulcanAggregation(aggregation);
				searchContext.setAttribute(
					Field.STATUS, WorkflowConstants.STATUS_APPROVED);
				searchContext.setAttribute(
					"resourceClassNameId",
					_classNameLocalService.getClassNameId(
						JournalArticle.class));
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {assetLibraryId});
			},
			sorts,
			document -> {
				DDMTemplate ddmTemplate = _ddmTemplateService.getTemplate(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)));

				return ContentTemplateUtil.toContentTemplate(
					ddmTemplate, _getDTOConverterContext(ddmTemplate),
					groupLocalService, _portal, _userLocalService);
			});
	}

	private DTOConverterContext _getDTOConverterContext(
		DDMTemplate ddmTemplate) {

		return new DefaultDTOConverterContext(
			contextAcceptLanguage.isAcceptAllLanguages(),
			Collections.singletonMap(
				"get",
				addAction(
					ActionKeys.VIEW, ddmTemplate.getTemplateId(),
					"getSiteContentTemplate", ddmTemplate.getUserId(),
					DDMTemplate.class.getName() + "-" +
						JournalArticle.class.getName(),
					ddmTemplate.getGroupId())),
			null, null, contextAcceptLanguage.getPreferredLocale(),
			contextUriInfo, contextUser);
	}

	private static final EntityModel _entityModel =
		new ContentTemplateEntityModel();

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private DDMTemplateService _ddmTemplateService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}