/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.headless.admin.site.dto.v1_0.StyleBook;
import com.liferay.headless.admin.site.resource.v1_0.StyleBookResource;
import com.liferay.headless.admin.user.dto.v1_0.Creator;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegate;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.style.book.constants.StyleBookActionKeys;
import com.liferay.style.book.constants.StyleBookConstants;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryService;
import com.liferay.style.book.util.comparator.StyleBookEntryCreateDateComparator;
import com.liferay.style.book.util.comparator.StyleBookEntryNameComparator;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
		"crud.item.delegate=true"
	},
	scope = ServiceScope.PROTOTYPE, service = StyleBookResource.class
)
public class StyleBookResourceImpl
	extends BaseStyleBookResourceImpl
	implements VulcanCRUDItemDelegate<StyleBook> {

	@Override
	public void deleteDesignLibraryStyleBook(
			String designLibraryExternalReferenceCode,
			String styleBookExternalReferenceCode)
		throws Exception {

		_checkFeatureFlag();

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

		_checkFeatureFlag();

		StyleBookEntry styleBookEntry = _getStyleBookEntry(
			siteExternalReferenceCode, styleBookExternalReferenceCode);

		_styleBookEntryService.deleteStyleBookEntry(
			styleBookEntry.getStyleBookEntryId());
	}

	@Override
	public StyleBook getDesignLibraryStyleBook(
			String designLibraryExternalReferenceCode,
			String styleBookExternalReferenceCode)
		throws Exception {

		_checkFeatureFlag();

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

		_checkFeatureFlag();

		List<StyleBookEntry> styleBookEntries = null;
		long totalCount = 0L;

		long groupId = _getDesignLibraryGroupId(
			designLibraryExternalReferenceCode);

		OrderByComparator<StyleBookEntry> orderByComparator =
			_getStyleBookEntryOrderByComparator(sorts);

		if (Validator.isNull(search)) {
			styleBookEntries = _styleBookEntryService.getStyleBookEntries(
				groupId, pagination.getStartPosition(),
				pagination.getEndPosition(), orderByComparator);
			totalCount = _styleBookEntryService.getStyleBookEntriesCount(
				groupId);
		}
		else {
			styleBookEntries = _styleBookEntryService.getStyleBookEntries(
				groupId, search, pagination.getStartPosition(),
				pagination.getEndPosition(), orderByComparator);
			totalCount = _styleBookEntryService.getStyleBookEntriesCount(
				groupId, search);
		}

		return Page.of(
			transform(styleBookEntries, this::_toStyleBook), pagination,
			totalCount);
	}

	@Override
	public StyleBook getItem(Long id) throws Exception {
		_checkFeatureFlag();

		StyleBookEntry styleBookEntry =
			_styleBookEntryService.getStyleBookEntry(id);

		return _styleBookDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(),
				_getActions(styleBookEntry), _dtoConverterRegistry,
				contextHttpServletRequest, styleBookEntry.getStyleBookEntryId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser),
			styleBookEntry);
	}

	@Override
	public StyleBook getSiteStyleBook(
			String siteExternalReferenceCode,
			String styleBookExternalReferenceCode)
		throws Exception {

		_checkFeatureFlag();

		return _toStyleBook(
			_getStyleBookEntry(
				siteExternalReferenceCode, styleBookExternalReferenceCode));
	}

	@Override
	public Page<StyleBook> getSiteStyleBooksPage(
			String siteExternalReferenceCode, String search,
			Aggregation aggregation, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		_checkFeatureFlag();

		List<StyleBookEntry> styleBookEntries = null;
		long totalCount = 0L;

		long groupId = _getGroupId(siteExternalReferenceCode);

		OrderByComparator<StyleBookEntry> orderByComparator =
			_getStyleBookEntryOrderByComparator(sorts);

		if (Validator.isNull(search)) {
			styleBookEntries = _styleBookEntryService.getStyleBookEntries(
				groupId, pagination.getStartPosition(),
				pagination.getEndPosition(), orderByComparator);
			totalCount = _styleBookEntryService.getStyleBookEntriesCount(
				groupId);
		}
		else {
			styleBookEntries = _styleBookEntryService.getStyleBookEntries(
				groupId, search, pagination.getStartPosition(),
				pagination.getEndPosition(), orderByComparator);
			totalCount = _styleBookEntryService.getStyleBookEntriesCount(
				groupId, search);
		}

		return Page.of(
			HashMapBuilder.put(
				"createBatch",
				addAction(
					StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES,
					"postSiteStyleBookBatch", StyleBookConstants.RESOURCE_NAME,
					groupId)
			).build(),
			transform(styleBookEntries, this::_toStyleBook), pagination,
			totalCount);
	}

	@Override
	public StyleBook postDesignLibraryStyleBook(
			String designLibraryExternalReferenceCode, StyleBook styleBook)
		throws Exception {

		_checkFeatureFlag();

		long groupId = _getDesignLibraryGroupId(
			designLibraryExternalReferenceCode);

		StyleBookEntry styleBookEntry =
			_styleBookEntryService.addStyleBookEntry(
				styleBook.getExternalReferenceCode(), groupId,
				Boolean.TRUE.equals(styleBook.getDefaultStyleBook()),
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

		_checkFeatureFlag();

		long groupId = _getGroupId(siteExternalReferenceCode);

		StyleBookEntry styleBookEntry =
			_styleBookEntryService.addStyleBookEntry(
				styleBook.getExternalReferenceCode(), groupId,
				styleBook.getDefaultStyleBook(),
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
	public StyleBook putDesignLibraryStyleBook(
			String designLibraryExternalReferenceCode,
			String styleBookExternalReferenceCode, StyleBook styleBook)
		throws Exception {

		_checkFeatureFlag();

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
				Boolean.TRUE.equals(styleBook.getDefaultStyleBook()),
				styleBook.getFrontendTokensValues(), styleBook.getName(),
				styleBook.getKey(),
				_getPreviewFileEntryId(
					groupId,
					styleBook.getPreviewFileEntryExternalReferenceCode()),
				_getServiceContext(groupId)));
	}

	@Override
	public StyleBook putSiteStyleBook(
			String siteExternalReferenceCode,
			String styleBookExternalReferenceCode, StyleBook styleBook)
		throws Exception {

		_checkFeatureFlag();

		styleBook.setExternalReferenceCode(
			() -> styleBookExternalReferenceCode);

		StyleBookEntry styleBookEntry =
			_styleBookEntryService.fetchStyleBookEntryByExternalReferenceCode(
				styleBookExternalReferenceCode,
				_getGroupId(siteExternalReferenceCode));

		if (styleBookEntry == null) {
			return postSiteStyleBook(siteExternalReferenceCode, styleBook);
		}

		long groupId = _getGroupId(siteExternalReferenceCode);

		return _toStyleBook(
			_styleBookEntryService.updateStyleBookEntry(
				styleBookEntry.getStyleBookEntryId(),
				styleBook.getDefaultStyleBook(),
				styleBook.getFrontendTokensValues(), styleBook.getName(),
				styleBook.getKey(),
				_getPreviewFileEntryId(
					groupId,
					styleBook.getPreviewFileEntryExternalReferenceCode()),
				_getServiceContext(groupId)));
	}

	private void _checkFeatureFlag() {
		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-57283")) {

			throw new UnsupportedOperationException();
		}
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

	private StyleBookEntry _getStyleBookEntry(
			String groupExternalReferenceCode,
			String styleBookExternalReferenceCode)
		throws Exception {

		return _getStyleBookEntry(
			_getGroupId(groupExternalReferenceCode),
			styleBookExternalReferenceCode);
	}

	private OrderByComparator<StyleBookEntry>
		_getStyleBookEntryOrderByComparator(Sort[] sorts) {

		OrderByComparator<StyleBookEntry> orderByComparator = null;

		if (ArrayUtil.isNotEmpty(sorts)) {
			Sort sort = sorts[0];

			if (StringUtil.equalsIgnoreCase(sort.getFieldName(), "name")) {
				orderByComparator = StyleBookEntryNameComparator.getInstance(
					!sort.isReverse());
			}
			else if (StringUtil.equalsIgnoreCase(
						sort.getFieldName(), "dateCreated")) {

				orderByComparator =
					StyleBookEntryCreateDateComparator.getInstance(
						!sort.isReverse());
			}
			else {
				throw new BadRequestException(
					"Invalid sort field name '" + sort.getFieldName() + "'");
			}
		}

		return orderByComparator;
	}

	private Map<String, String> _resolveActionHref(
		Map<String, String> action, String designLibraryExternalReferenceCode,
		String styleBookExternalReferenceCode) {

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

	private StyleBook _toStyleBook(StyleBookEntry styleBookEntry) {
		StyleBook styleBook = new StyleBook();

		styleBook.setActions(() -> _getActions(styleBookEntry));
		styleBook.setCreator(
			() -> {
				User user = _userLocalService.fetchUser(
					styleBookEntry.getUserId());

				if (user == null) {
					return null;
				}

				return new Creator() {
					{
						setExternalReferenceCode(
							user::getExternalReferenceCode);
					}
				};
			});
		styleBook.setDateCreated(styleBookEntry::getCreateDate);
		styleBook.setDateModified(styleBookEntry::getModifiedDate);
		styleBook.setDefaultStyleBook(styleBookEntry::getDefaultStyleBookEntry);
		styleBook.setExternalReferenceCode(
			styleBookEntry::getExternalReferenceCode);
		styleBook.setFrontendTokensValues(
			styleBookEntry::getFrontendTokensValues);
		styleBook.setId(styleBookEntry::getStyleBookEntryId);
		styleBook.setKey(styleBookEntry::getStyleBookEntryKey);
		styleBook.setName(styleBookEntry::getName);
		styleBook.setPreviewFileEntryExternalReferenceCode(
			() -> {
				long previewFileEntryId =
					styleBookEntry.getPreviewFileEntryId();

				if (previewFileEntryId == 0) {
					return null;
				}

				FileEntry fileEntry = _dlAppLocalService.getFileEntry(
					previewFileEntryId);

				if (fileEntry == null) {
					return null;
				}

				return fileEntry.getExternalReferenceCode();
			});
		styleBook.setThemeId(styleBookEntry::getThemeId);

		return styleBook;
	}

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(
		target = "(resource.name=" + StyleBookConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.site.internal.dto.v1_0.converter.StyleBookDTOConverter)"
	)
	private DTOConverter<StyleBookEntry, StyleBook> _styleBookDTOConverter;

	@Reference
	private StyleBookEntryService _styleBookEntryService;

	@Reference
	private UserLocalService _userLocalService;

}