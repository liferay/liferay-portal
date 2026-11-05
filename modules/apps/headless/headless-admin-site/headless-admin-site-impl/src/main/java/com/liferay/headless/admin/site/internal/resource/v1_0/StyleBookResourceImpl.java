/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.exportimport.constants.ExportImportConstants;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.exportimport.vulcan.batch.engine.ExportImportVulcanBatchEngineTaskItemDelegate;
import com.liferay.headless.admin.site.dto.v1_0.StyleBook;
import com.liferay.headless.admin.site.internal.odata.entity.v1_0.StyleBookEntityModel;
import com.liferay.headless.admin.site.internal.util.EnabledUtil;
import com.liferay.headless.admin.site.resource.v1_0.StyleBookResource;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.headless.common.spi.util.GroupUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegate;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.style.book.constants.StyleBookActionKeys;
import com.liferay.style.book.constants.StyleBookConstants;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.service.StyleBookEntryService;
import com.liferay.style.book.util.StyleBookEntryProviderUtil;
import com.liferay.style.book.util.StyleBookUtil;
import com.liferay.style.book.util.comparator.StyleBookEntryNameComparator;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MultivaluedMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rubén Pulido
 * @author Thiago Buarque
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/style-book.properties",
	property = {
		"crud.entity.class.name=com.liferay.headless.admin.site.dto.v1_0.StyleBook",
		"crud.item.delegate=true",
		"export.import.vulcan.batch.engine.task.item.delegate=true"
	},
	scope = ServiceScope.PROTOTYPE, service = StyleBookResource.class
)
public class StyleBookResourceImpl
	extends BaseStyleBookResourceImpl
	implements ExportImportVulcanBatchEngineTaskItemDelegate<StyleBook>,
			   VulcanCRUDItemDelegate<StyleBook> {

	@Override
	public void deleteDesignLibraryStyleBook(
			String designLibraryExternalReferenceCode,
			String styleBookExternalReferenceCode)
		throws Exception {

		StyleBookEntry styleBookEntry = _getStyleBookEntry(
			_getDesignLibraryGroupId(designLibraryExternalReferenceCode),
			styleBookExternalReferenceCode);

		_styleBookEntryService.deleteStyleBookEntry(
			styleBookEntry.getStyleBookEntryId());
	}

	@Override
	public void deleteSiteStyleBook(
			String siteExternalReferenceCode,
			String styleBookExternalReferenceCode)
		throws Exception {

		_styleBookEntryService.deleteStyleBookEntry(
			styleBookExternalReferenceCode,
			_getLiveGroupId(siteExternalReferenceCode));
	}

	@Override
	public StyleBook getDesignLibraryStyleBook(
			String designLibraryExternalReferenceCode,
			String styleBookExternalReferenceCode)
		throws Exception {

		return _toStyleBook(
			_getStyleBookEntry(
				_getDesignLibraryGroupId(designLibraryExternalReferenceCode),
				styleBookExternalReferenceCode));
	}

	@Override
	public Page<StyleBook> getDesignLibraryStyleBooksPage(
			String designLibraryExternalReferenceCode, String search,
			Aggregation aggregation, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		return _getStyleBooksPage(
			Collections.emptyMap(), filter,
			_getDesignLibraryGroupId(designLibraryExternalReferenceCode),
			pagination, search, sorts);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public ExportImportDescriptor<StyleBookEntry> getExportImportDescriptor() {
		return new ExportImportDescriptor<>() {

			@Override
			public String getKey() {
				return StyleBookResourceImpl.class.getName();
			}

			@Override
			public String getLabelLanguageKey() {
				return "style-books";
			}

			@Override
			public Class<StyleBookEntry> getModelClass() {
				return StyleBookEntry.class;
			}

			@Override
			public String getPortletId() {
				return StyleBookPortletKeys.STYLE_BOOK;
			}

			@Override
			public Scope getScope() {
				return Scope.SITE;
			}

			@Override
			public String getSectionKey() {
				return ExportImportConstants.SECTION_KEY_DESIGN;
			}

			@Override
			public boolean isScopeSupported(long companyId, long groupId) {
				DepotEntry depotEntry =
					_depotEntryLocalService.fetchGroupDepotEntry(groupId);

				if ((depotEntry != null) &&
					(depotEntry.getType() ==
						DepotConstants.TYPE_DESIGN_LIBRARY)) {

					return FeatureFlagManagerUtil.isEnabled(
						companyId, "LPD-57283");
				}

				return ExportImportDescriptor.super.isScopeSupported(
					companyId, groupId);
			}

		};
	}

	@Override
	public StyleBook getItem(Long id) throws Exception {
		StyleBookEntry styleBookEntry =
			_styleBookEntryService.getStyleBookEntry(id);

		Group group = _groupLocalService.getGroup(styleBookEntry.getGroupId());

		if (group.isDepot()) {
			EnabledUtil.checkDesignLibrariesEnabled(contextCompany);
		}

		return _toStyleBook(styleBookEntry);
	}

	@Override
	public Page<StyleBook> getSitePageSpecificationStyleBooksPage(
			String siteExternalReferenceCode,
			String pageSpecificationExternalReferenceCode, String search,
			Pagination pagination)
		throws Exception {

		long groupId = GroupUtil.getGroupId(
			true, contextCompany.getCompanyId(), siteExternalReferenceCode);

		Layout layout = _getLayout(
			pageSpecificationExternalReferenceCode, groupId);

		LayoutPermissionUtil.checkLayoutRestrictedUpdatePermission(
			PermissionThreadLocal.getPermissionChecker(), layout);

		long companyId = contextCompany.getCompanyId();

		StyleBookEntry styleFromThemeStyleBookEntry =
			StyleBookUtil.getStyleFromThemeStyleBookEntry(
				layout, contextAcceptLanguage.getPreferredLocale());

		String themeId = styleFromThemeStyleBookEntry.getThemeId();

		boolean matchesStyleThemeStyleBookEntry = false;

		int styleBookEntriesCount =
			StyleBookEntryProviderUtil.getStyleBookEntriesCount(
				companyId, groupId, search, themeId);

		String name = StringUtil.toLowerCase(
			styleFromThemeStyleBookEntry.getName());

		if (Validator.isNull(search) ||
			name.contains(StringUtil.toLowerCase(search))) {

			matchesStyleThemeStyleBookEntry = true;

			styleBookEntriesCount++;
		}

		List<StyleBookEntry> styleBookEntries = new ArrayList<>();

		if (matchesStyleThemeStyleBookEntry &&
			(pagination.getStartPosition() == 0) &&
			(pagination.getEndPosition() > 0)) {

			styleBookEntries.add(styleFromThemeStyleBookEntry);
		}

		int offset = matchesStyleThemeStyleBookEntry ? 1 : 0;

		int start = Math.max(0, pagination.getStartPosition() - offset);
		int end = pagination.getEndPosition() - offset;

		if (end > start) {
			styleBookEntries.addAll(
				StyleBookEntryProviderUtil.getStyleBookEntries(
					companyId, groupId, search, themeId, start, end,
					StyleBookEntryNameComparator.getInstance(true)));
		}

		return Page.of(
			transform(
				styleBookEntries,
				styleBookEntry -> _toStyleBook(groupId, styleBookEntry)),
			pagination, styleBookEntriesCount);
	}

	@Override
	public StyleBook getSiteStyleBook(
			String siteExternalReferenceCode,
			String styleBookExternalReferenceCode)
		throws Exception {

		return _toStyleBook(
			_getStyleBookEntry(
				_getLiveGroupId(siteExternalReferenceCode),
				styleBookExternalReferenceCode));
	}

	@Override
	public Page<StyleBook> getSiteStyleBooksPage(
			String siteExternalReferenceCode, String search,
			Aggregation aggregation, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		long groupId = _getLiveGroupId(siteExternalReferenceCode);

		return _getStyleBooksPage(
			HashMapBuilder.put(
				"createBatch",
				addAction(
					StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES,
					"postSiteStyleBookBatch", StyleBookConstants.RESOURCE_NAME,
					groupId)
			).build(),
			filter, groupId, pagination, search, sorts);
	}

	@Override
	public StyleBook postDesignLibraryStyleBook(
			String designLibraryExternalReferenceCode, StyleBook styleBook)
		throws Exception {

		long groupId = _getDesignLibraryGroupId(
			designLibraryExternalReferenceCode);

		StyleBookEntry styleBookEntry =
			_styleBookEntryService.addStyleBookEntry(
				styleBook.getExternalReferenceCode(), groupId,
				GetterUtil.getBoolean(styleBook.getDefaultStyleBook()),
				styleBook.getFrontendTokenDefinition(),
				styleBook.getFrontendTokensValues(), styleBook.getName(),
				styleBook.getKey(), styleBook.getThemeId(),
				_getServiceContext(groupId));

		long previewFileEntryId = _getPreviewFileEntryId(
			groupId, styleBook.getPreviewFileEntryExternalReferenceCode());

		if (previewFileEntryId != 0) {
			styleBookEntry = _styleBookEntryService.updatePreviewFileEntryId(
				styleBookEntry.getStyleBookEntryId(), previewFileEntryId,
				_getServiceContext(groupId));
		}

		return _toStyleBook(styleBookEntry);
	}

	@Override
	public StyleBook postSiteStyleBook(
			String siteExternalReferenceCode, StyleBook styleBook)
		throws Exception {

		long groupId = _getLiveGroupId(siteExternalReferenceCode);

		String name = _getName(groupId, styleBook.getName(), 0);

		String styleBookEntryKey = StringPool.BLANK;

		if (Objects.equals(name, styleBook.getName())) {
			styleBookEntryKey = _getStyleBookEntryKey(
				StringPool.BLANK, groupId, styleBook.getKey());
		}

		ServiceContext serviceContext = _getServiceContext(groupId, styleBook);

		StyleBookEntry styleBookEntry =
			_styleBookEntryService.addStyleBookEntry(
				styleBook.getExternalReferenceCode(), groupId,
				GetterUtil.getBoolean(styleBook.getDefaultStyleBook()),
				styleBook.getFrontendTokenDefinition(),
				styleBook.getFrontendTokensValues(), name, styleBookEntryKey,
				styleBook.getThemeId(), serviceContext);

		long previewFileEntryId = _getPreviewFileEntryId(
			groupId, styleBook.getPreviewFileEntryExternalReferenceCode());

		if (previewFileEntryId != 0) {
			styleBookEntry = _styleBookEntryService.updatePreviewFileEntryId(
				styleBookEntry.getStyleBookEntryId(), previewFileEntryId,
				serviceContext);
		}

		return _toStyleBook(styleBookEntry);
	}

	@Override
	public StyleBook putDesignLibraryStyleBook(
			String designLibraryExternalReferenceCode,
			String styleBookExternalReferenceCode, StyleBook styleBook)
		throws Exception {

		styleBook.setExternalReferenceCode(
			() -> styleBookExternalReferenceCode);

		long groupId = _getDesignLibraryGroupId(
			designLibraryExternalReferenceCode);

		StyleBookEntry styleBookEntry =
			_styleBookEntryService.fetchStyleBookEntryByExternalReferenceCode(
				styleBookExternalReferenceCode, groupId);

		if (styleBookEntry == null) {
			return postDesignLibraryStyleBook(
				designLibraryExternalReferenceCode, styleBook);
		}

		return _toStyleBook(
			_styleBookEntryService.updateStyleBookEntry(
				styleBookEntry.getStyleBookEntryId(),
				GetterUtil.getBoolean(styleBook.getDefaultStyleBook()),
				styleBook.getFrontendTokenDefinition(),
				styleBook.getFrontendTokensValues(), styleBook.getName(),
				styleBook.getKey(),
				_getPreviewFileEntryId(
					groupId,
					styleBook.getPreviewFileEntryExternalReferenceCode()),
				styleBook.getThemeId(), _getServiceContext(groupId)));
	}

	@Override
	public StyleBook putSiteStyleBook(
			String siteExternalReferenceCode,
			String styleBookExternalReferenceCode, StyleBook styleBook)
		throws Exception {

		styleBook.setExternalReferenceCode(
			() -> styleBookExternalReferenceCode);

		long groupId = _getLiveGroupId(siteExternalReferenceCode);

		StyleBookEntry styleBookEntry =
			_styleBookEntryService.fetchStyleBookEntryByExternalReferenceCode(
				styleBookExternalReferenceCode, groupId);

		if (styleBookEntry == null) {
			return postSiteStyleBook(siteExternalReferenceCode, styleBook);
		}

		String name = _getName(
			groupId, styleBook.getName(), styleBookEntry.getStyleBookEntryId());
		String styleBookEntryKey = _getStyleBookEntryKey(
			styleBookEntry.getStyleBookEntryKey(), groupId, styleBook.getKey());

		long previewFileEntryId = _getPreviewFileEntryId(
			groupId, styleBook.getPreviewFileEntryExternalReferenceCode());

		if ((previewFileEntryId == 0) &&
			ExportImportThreadLocal.isImportInProcess()) {

			previewFileEntryId = styleBookEntry.getPreviewFileEntryId();
		}

		styleBookEntry = _styleBookEntryService.updateStyleBookEntry(
			styleBookEntry.getStyleBookEntryId(),
			GetterUtil.getBoolean(styleBook.getDefaultStyleBook()),
			styleBook.getFrontendTokenDefinition(),
			styleBook.getFrontendTokensValues(), name, styleBookEntryKey,
			previewFileEntryId, styleBook.getThemeId(),
			_getServiceContext(groupId, styleBook));

		return _toStyleBook(styleBookEntry);
	}

	private Map<String, Map<String, String>> _getActions(
		StyleBookEntry styleBookEntry) {

		Group group = _groupLocalService.fetchGroup(
			styleBookEntry.getGroupId());

		if ((group == null) || !group.isDepot() ||
			!_portletResourcePermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				styleBookEntry.getGroupId(),
				StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES)) {

			return Collections.emptyMap();
		}

		return HashMapBuilder.<String, Map<String, String>>put(
			"delete",
			_resolveActionHref(
				addAction(
					StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES,
					"deleteDesignLibraryStyleBook",
					StyleBookConstants.RESOURCE_NAME,
					styleBookEntry.getGroupId()),
				group.getExternalReferenceCode(),
				styleBookEntry.getExternalReferenceCode())
		).put(
			"get",
			_resolveActionHref(
				addAction(
					StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES,
					"getDesignLibraryStyleBook",
					StyleBookConstants.RESOURCE_NAME,
					styleBookEntry.getGroupId()),
				group.getExternalReferenceCode(),
				styleBookEntry.getExternalReferenceCode())
		).build();
	}

	private long _getDesignLibraryGroupId(
		String designLibraryExternalReferenceCode) {

		long groupId = _getGroupId(designLibraryExternalReferenceCode);

		DepotEntry depotEntry = _depotEntryLocalService.fetchGroupDepotEntry(
			groupId);

		if ((depotEntry == null) ||
			(depotEntry.getType() != DepotConstants.TYPE_DESIGN_LIBRARY)) {

			throw new NotFoundException(
				"Unable to find design library with external reference code '" +
					designLibraryExternalReferenceCode + "'");
		}

		return groupId;
	}

	private long _getGroupId(String externalReferenceCode) {
		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			externalReferenceCode, contextCompany.getCompanyId());

		if (group == null) {
			throw new NotFoundException(
				"Unable to find group with external reference code '" +
					externalReferenceCode + "'");
		}

		return group.getGroupId();
	}

	private Layout _getLayout(String externalReferenceCode, long groupId)
		throws Exception {

		Layout layout = _layoutLocalService.fetchLayoutByExternalReferenceCode(
			externalReferenceCode, groupId);

		if (layout != null) {
			return layout;
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					externalReferenceCode, groupId);

		if ((layoutPageTemplateEntry != null) &&
			(layoutPageTemplateEntry.getType() ==
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE)) {

			return _layoutLocalService.getLayout(
				layoutPageTemplateEntry.getPlid());
		}

		throw new NoSuchLayoutException(
			"No page specification with external reference code " +
				externalReferenceCode);
	}

	private long _getLiveGroupId(String siteExternalReferenceCode)
		throws Exception {

		return _staging.getLiveGroupId(
			GroupUtil.getGroupId(
				true, contextCompany.getCompanyId(),
				siteExternalReferenceCode));
	}

	private String _getName(long groupId, String name, long styleBookEntryId) {
		if (!ExportImportThreadLocal.isImportInProcess()) {
			return name;
		}

		return _styleBookEntryLocalService.generateStyleBookEntryName(
			groupId, name, styleBookEntryId);
	}

	private long _getPreviewFileEntryId(
			long groupId, String previewFileEntryExternalReferenceCode)
		throws Exception {

		long previewFileEntryId = 0;

		FileEntry fileEntry =
			_dlAppLocalService.fetchFileEntryByExternalReferenceCode(
				groupId, previewFileEntryExternalReferenceCode);

		if (fileEntry != null) {
			previewFileEntryId = fileEntry.getFileEntryId();
		}

		return previewFileEntryId;
	}

	private ServiceContext _getServiceContext(long groupId) {
		ServiceContext serviceContext = ServiceContextBuilder.create(
			groupId, contextHttpServletRequest, null
		).build();

		serviceContext.setCompanyId(contextCompany.getCompanyId());
		serviceContext.setUserId(contextUser.getUserId());

		return serviceContext;
	}

	private ServiceContext _getServiceContext(
		long groupId, StyleBook styleBook) {

		ServiceContext serviceContext = _getServiceContext(groupId);

		serviceContext.setCreateDate(styleBook.getDateCreated());
		serviceContext.setModifiedDate(styleBook.getDateModified());

		return serviceContext;
	}

	private StyleBookEntry _getStyleBookEntry(
			long groupId, String styleBookExternalReferenceCode)
		throws Exception {

		StyleBookEntry styleBookEntry =
			_styleBookEntryService.fetchStyleBookEntryByExternalReferenceCode(
				styleBookExternalReferenceCode, groupId);

		if (styleBookEntry == null) {
			throw new NotFoundException(
				"Unable to find style book with external reference code " +
					styleBookExternalReferenceCode);
		}

		return styleBookEntry;
	}

	private String _getStyleBookEntryKey(
		String defaultStyleBookEntryKey, long groupId,
		String styleBookEntryKey) {

		if (!ExportImportThreadLocal.isImportInProcess()) {
			return styleBookEntryKey;
		}

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.fetchStyleBookEntry(
				groupId, styleBookEntryKey);

		if (styleBookEntry == null) {
			return styleBookEntryKey;
		}

		return defaultStyleBookEntryKey;
	}

	private Page<StyleBook> _getStyleBooksPage(
			Map<String, Map<String, String>> actions, Filter filter,
			long groupId, Pagination pagination, String search, Sort[] sorts)
		throws Exception {

		_portletResourcePermission.check(
			PermissionThreadLocal.getPermissionChecker(), groupId,
			StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES);

		if (ArrayUtil.isEmpty(sorts)) {
			sorts = new Sort[] {
				new Sort(
					Field.getSortableFieldName(Field.CREATE_DATE),
					Sort.LONG_TYPE, true),
				new Sort(Field.ENTRY_CLASS_PK, Sort.LONG_TYPE, true)
			};
		}

		return SearchUtil.search(
			actions,
			booleanQuery -> {
			},
			filter, StyleBookEntry.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {groupId});
			},
			sorts,
			document -> {
				StyleBookEntry styleBookEntry =
					_styleBookEntryLocalService.fetchStyleBookEntry(
						GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)));

				if (styleBookEntry == null) {
					return null;
				}

				return _toStyleBook(styleBookEntry);
			});
	}

	private Map<String, String> _resolveActionHref(
		Map<String, String> action, String designLibraryExternalReferenceCode,
		String styleBookExternalReferenceCode) {

		if (action == null) {
			return null;
		}

		String href = action.get("href");

		if (href != null) {
			action.put(
				"href",
				StringUtil.replace(
					href,
					new String[] {
						"{designLibraryExternalReferenceCode}",
						"{styleBookExternalReferenceCode}"
					},
					new String[] {
						designLibraryExternalReferenceCode,
						styleBookExternalReferenceCode
					}));
		}

		return action;
	}

	private StyleBook _toStyleBook(
			long scopeGroupId, StyleBookEntry styleBookEntry)
		throws Exception {

		DefaultDTOConverterContext dtoConverterContext =
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				_getActions(styleBookEntry), _dtoConverterRegistry,
				contextHttpServletRequest, styleBookEntry.getStyleBookEntryId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser);

		dtoConverterContext.setAttribute(
			"companyId", contextCompany.getCompanyId());
		dtoConverterContext.setAttribute("scopeGroupId", scopeGroupId);

		return _styleBookDTOConverter.toDTO(
			dtoConverterContext, styleBookEntry);
	}

	private StyleBook _toStyleBook(StyleBookEntry styleBookEntry)
		throws Exception {

		return _toStyleBook(styleBookEntry.getGroupId(), styleBookEntry);
	}

	private static final EntityModel _entityModel = new StyleBookEntityModel();

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Reference(
		target = "(resource.name=" + StyleBookConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference
	private Staging _staging;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.StyleBookDTOConverter)"
	)
	private DTOConverter<StyleBookEntry, StyleBook> _styleBookDTOConverter;

	@Reference
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	@Reference
	private StyleBookEntryService _styleBookEntryService;

}