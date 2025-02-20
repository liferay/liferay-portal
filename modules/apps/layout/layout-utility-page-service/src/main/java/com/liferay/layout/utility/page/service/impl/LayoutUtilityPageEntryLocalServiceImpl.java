/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.service.impl;

import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.util.validator.LayoutValidator;
import com.liferay.layout.utility.page.exception.DefaultLayoutUtilityPageEntryException;
import com.liferay.layout.utility.page.exception.LayoutUtilityPageEntryNameException;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.base.LayoutUtilityPageEntryLocalServiceBaseImpl;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.ColorScheme;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.layout.utility.page.model.LayoutUtilityPageEntry",
	service = AopService.class
)
public class LayoutUtilityPageEntryLocalServiceImpl
	extends LayoutUtilityPageEntryLocalServiceBaseImpl {

	@Override
	public LayoutUtilityPageEntry addLayoutUtilityPageEntry(
			String externalReferenceCode, long userId, long groupId, long plid,
			long previewFileEntryId, boolean defaultLayoutUtilityPageEntry,
			String name, String type, long masterLayoutPlid,
			ServiceContext serviceContext)
		throws PortalException {

		_validateName(groupId, 0, name, type);

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			layoutUtilityPageEntryPersistence.create(
				counterLocalService.increment());

		layoutUtilityPageEntry.setUuid(serviceContext.getUuid());

		if (Validator.isNotNull(externalReferenceCode)) {
			layoutUtilityPageEntry.setExternalReferenceCode(
				externalReferenceCode);
		}
		else {
			layoutUtilityPageEntry.setExternalReferenceCode(
				_getExternalReferenceCode(groupId, name));
		}

		layoutUtilityPageEntry.setGroupId(groupId);

		User user = _userLocalService.getUser(userId);

		layoutUtilityPageEntry.setCompanyId(user.getCompanyId());
		layoutUtilityPageEntry.setUserId(user.getUserId());
		layoutUtilityPageEntry.setUserName(user.getFullName());

		if (plid == 0) {
			Layout layout = _addLayout(
				userId, groupId, name, masterLayoutPlid,
				defaultLayoutUtilityPageEntry, serviceContext);

			if (layout != null) {
				plid = layout.getPlid();
			}
		}

		layoutUtilityPageEntry.setPlid(plid);

		layoutUtilityPageEntry.setPreviewFileEntryId(previewFileEntryId);
		layoutUtilityPageEntry.setName(name);
		layoutUtilityPageEntry.setType(type);

		layoutUtilityPageEntry = layoutUtilityPageEntryPersistence.update(
			layoutUtilityPageEntry);

		if (defaultLayoutUtilityPageEntry) {
			layoutUtilityPageEntry = setDefaultLayoutUtilityPageEntry(
				layoutUtilityPageEntry.getLayoutUtilityPageEntryId());
		}

		_resourceLocalService.addResources(
			layoutUtilityPageEntry.getCompanyId(),
			layoutUtilityPageEntry.getGroupId(),
			layoutUtilityPageEntry.getUserId(),
			LayoutUtilityPageEntry.class.getName(),
			layoutUtilityPageEntry.getLayoutUtilityPageEntryId(), false, true,
			true);

		return layoutUtilityPageEntry;
	}

	@Override
	public LayoutUtilityPageEntry copyLayoutUtilityPageEntry(
			long userId, long groupId, long sourceLayoutUtilityPageEntryId,
			ServiceContext serviceContext)
		throws Exception {

		LayoutUtilityPageEntry sourceLayoutUtilityPageEntry =
			layoutUtilityPageEntryPersistence.findByPrimaryKey(
				sourceLayoutUtilityPageEntryId);

		String name = _getUniqueCopyName(
			groupId, sourceLayoutUtilityPageEntry.getName(),
			sourceLayoutUtilityPageEntry.getType());

		long masterLayoutPlid = 0;

		Layout layout = _layoutLocalService.fetchLayout(
			sourceLayoutUtilityPageEntry.getPlid());

		if (layout != null) {
			masterLayoutPlid = layout.getMasterLayoutPlid();
		}

		LayoutUtilityPageEntry targetLayoutUtilityPageEntry =
			addLayoutUtilityPageEntry(
				null, userId, serviceContext.getScopeGroupId(), 0, 0, false,
				name, sourceLayoutUtilityPageEntry.getType(), masterLayoutPlid,
				serviceContext);

		long previewFileEntryId = _copyPreviewFileEntryId(
			targetLayoutUtilityPageEntry.getLayoutUtilityPageEntryId(),
			sourceLayoutUtilityPageEntry.getPreviewFileEntryId(),
			serviceContext);

		if (previewFileEntryId > 0) {
			return layoutUtilityPageEntryLocalService.
				updateLayoutUtilityPageEntry(
					targetLayoutUtilityPageEntry.getLayoutUtilityPageEntryId(),
					previewFileEntryId);
		}

		return targetLayoutUtilityPageEntry;
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public LayoutUtilityPageEntry deleteLayoutUtilityPageEntry(
			LayoutUtilityPageEntry layoutUtilityPageEntry)
		throws PortalException {

		// Layout page template

		layoutUtilityPageEntryPersistence.remove(layoutUtilityPageEntry);

		// Resources

		_resourceLocalService.deleteResource(
			layoutUtilityPageEntry.getCompanyId(),
			LayoutUtilityPageEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			layoutUtilityPageEntry.getLayoutUtilityPageEntryId());

		// Layout

		Layout layout = _layoutLocalService.fetchLayout(
			layoutUtilityPageEntry.getPlid());

		LayoutSet layoutSet = _layoutSetLocalService.fetchLayoutSet(
			layoutUtilityPageEntry.getGroupId(), false);

		if ((layout != null) && (layoutSet != null)) {
			_layoutLocalService.deleteLayout(layout);
		}

		// Portlet file entry

		if (layoutUtilityPageEntry.getPreviewFileEntryId() > 0) {
			_portletFileRepository.deletePortletFileEntry(
				layoutUtilityPageEntry.getPreviewFileEntryId());
		}

		return layoutUtilityPageEntry;
	}

	@Override
	public LayoutUtilityPageEntry deleteLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId)
		throws PortalException {

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			layoutUtilityPageEntryLocalService.fetchLayoutUtilityPageEntry(
				layoutUtilityPageEntryId);

		return layoutUtilityPageEntryLocalService.deleteLayoutUtilityPageEntry(
			layoutUtilityPageEntry);
	}

	@Override
	public LayoutUtilityPageEntry deleteLayoutUtilityPageEntry(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return layoutUtilityPageEntryLocalService.deleteLayoutUtilityPageEntry(
			getLayoutUtilityPageEntryByExternalReferenceCode(
				externalReferenceCode, groupId));
	}

	@Override
	public LayoutUtilityPageEntry fetchDefaultLayoutUtilityPageEntry(
		long groupId, String type) {

		return layoutUtilityPageEntryPersistence.fetchByG_D_T_First(
			groupId, true, type, null);
	}

	@Override
	public LayoutUtilityPageEntry fetchLayoutUtilityPageEntry(
		long groupId, String name, String type) {

		return layoutUtilityPageEntryPersistence.fetchByG_N_T(
			groupId, name, type);
	}

	@Override
	public LayoutUtilityPageEntry fetchLayoutUtilityPageEntryByPlid(long plid) {
		return layoutUtilityPageEntryPersistence.fetchByPlid(plid);
	}

	@Override
	public LayoutUtilityPageEntry getDefaultLayoutUtilityPageEntry(
			long groupId, String type)
		throws PortalException {

		return layoutUtilityPageEntryPersistence.findByG_D_T_First(
			groupId, true, type, null);
	}

	@Override
	public List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		long groupId) {

		return layoutUtilityPageEntryPersistence.findByGroupId(groupId);
	}

	@Override
	public List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		long groupId, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return layoutUtilityPageEntryPersistence.findByGroupId(
			groupId, start, end, orderByComparator);
	}

	@Override
	public List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		long groupId, String type, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return layoutUtilityPageEntryPersistence.findByG_T(
			groupId, type, start, end, orderByComparator);
	}

	@Override
	public List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		long groupId, String keyword, String[] types, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return layoutUtilityPageEntryPersistence.findByG_LikeN_T(
			groupId,
			_customSQL.keywords(keyword, false, WildcardMode.SURROUND)[0],
			types, start, end, orderByComparator);
	}

	@Override
	public List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		long groupId, String[] types, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator) {

		return layoutUtilityPageEntryPersistence.findByG_T(
			groupId, types, start, end, orderByComparator);
	}

	@Override
	public int getLayoutUtilityPageEntriesCount(long groupId) {
		return layoutUtilityPageEntryPersistence.countByGroupId(groupId);
	}

	@Override
	public int getLayoutUtilityPageEntriesCount(
		long groupId, String keyword, String[] types) {

		return layoutUtilityPageEntryPersistence.filterCountByG_LikeN_T(
			groupId, keyword, types);
	}

	@Override
	public int getLayoutUtilityPageEntriesCount(long groupId, String[] types) {
		return layoutUtilityPageEntryPersistence.filterCountByG_T(
			groupId, types);
	}

	@Override
	public LayoutUtilityPageEntry setDefaultLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId)
		throws PortalException {

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			layoutUtilityPageEntryPersistence.findByPrimaryKey(
				layoutUtilityPageEntryId);

		Layout layout = _layoutLocalService.getLayout(
			layoutUtilityPageEntry.getPlid());

		if (!layout.isPublished()) {
			throw new DefaultLayoutUtilityPageEntryException();
		}

		LayoutUtilityPageEntry defaultLayoutUtilityPageEntry =
			layoutUtilityPageEntryPersistence.fetchByG_D_T_First(
				layoutUtilityPageEntry.getGroupId(), true,
				layoutUtilityPageEntry.getType(), null);

		if (defaultLayoutUtilityPageEntry != null) {
			defaultLayoutUtilityPageEntry.setDefaultLayoutUtilityPageEntry(
				false);

			layoutUtilityPageEntryPersistence.update(
				defaultLayoutUtilityPageEntry);
		}

		layoutUtilityPageEntry.setDefaultLayoutUtilityPageEntry(true);

		return layoutUtilityPageEntryPersistence.update(layoutUtilityPageEntry);
	}

	@Override
	public LayoutUtilityPageEntry updateLayoutUtilityPageEntry(
		long layoutUtilityPageEntryId, long previewFileEntryId) {

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			layoutUtilityPageEntryPersistence.fetchByPrimaryKey(
				layoutUtilityPageEntryId);

		layoutUtilityPageEntry.setModifiedDate(new Date());
		layoutUtilityPageEntry.setPreviewFileEntryId(previewFileEntryId);

		return layoutUtilityPageEntryPersistence.update(layoutUtilityPageEntry);
	}

	@Override
	public LayoutUtilityPageEntry updateLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId, String name)
		throws PortalException {

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			layoutUtilityPageEntryPersistence.fetchByPrimaryKey(
				layoutUtilityPageEntryId);

		_validateName(
			layoutUtilityPageEntry.getGroupId(), layoutUtilityPageEntryId, name,
			layoutUtilityPageEntry.getType());

		layoutUtilityPageEntry.setName(name);

		layoutUtilityPageEntry = layoutUtilityPageEntryPersistence.update(
			layoutUtilityPageEntry);

		Map<Locale, String> titleMap = Collections.singletonMap(
			LocaleUtil.getSiteDefault(), name);

		Layout draftLayout = _layoutLocalService.fetchDraftLayout(
			layoutUtilityPageEntry.getPlid());

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			serviceContext = new ServiceContext();
		}

		serviceContext.setAttribute(
			"layout.instanceable.allowed", Boolean.TRUE);

		_layoutLocalService.updateLayout(
			draftLayout.getGroupId(), draftLayout.isPrivateLayout(),
			draftLayout.getLayoutId(), draftLayout.getParentLayoutId(),
			titleMap, titleMap, draftLayout.getDescriptionMap(),
			draftLayout.getKeywordsMap(), draftLayout.getRobotsMap(),
			draftLayout.getType(), draftLayout.isHidden(),
			draftLayout.getFriendlyURLMap(), draftLayout.getIconImage(), null,
			draftLayout.getStyleBookEntryId(),
			draftLayout.getFaviconFileEntryId(),
			draftLayout.getMasterLayoutPlid(), serviceContext);

		Layout layout = _layoutLocalService.getLayout(
			layoutUtilityPageEntry.getPlid());

		_layoutLocalService.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getParentLayoutId(), titleMap, titleMap,
			layout.getDescriptionMap(), layout.getKeywordsMap(),
			layout.getRobotsMap(), layout.getType(), layout.isHidden(),
			layout.getFriendlyURLMap(), layout.getIconImage(), null,
			layout.getStyleBookEntryId(), layout.getFaviconFileEntryId(),
			layout.getMasterLayoutPlid(), serviceContext);

		return layoutUtilityPageEntry;
	}

	private Layout _addLayout(
			long userId, long groupId, String name, long masterLayoutPlid,
			boolean published, ServiceContext serviceContext)
		throws PortalException {

		Map<Locale, String> titleMap = Collections.singletonMap(
			LocaleUtil.getSiteDefault(), name);

		UnicodeProperties typeSettingsUnicodeProperties =
			new UnicodeProperties();

		if (masterLayoutPlid > 0) {
			typeSettingsUnicodeProperties.setProperty(
				"lfr-theme:regular:show-footer", Boolean.FALSE.toString());
			typeSettingsUnicodeProperties.setProperty(
				"lfr-theme:regular:show-header", Boolean.FALSE.toString());
			typeSettingsUnicodeProperties.setProperty(
				"lfr-theme:regular:show-header-search",
				Boolean.FALSE.toString());
			typeSettingsUnicodeProperties.setProperty(
				"lfr-theme:regular:wrap-widget-page-content",
				Boolean.FALSE.toString());
		}

		if (published) {
			typeSettingsUnicodeProperties.put(
				LayoutTypeSettingsConstants.KEY_PUBLISHED, "true");
		}

		String typeSettings = typeSettingsUnicodeProperties.toString();

		serviceContext.setAttribute(
			"layout.instanceable.allowed", Boolean.TRUE);

		Layout layout = _layoutLocalService.addLayout(
			null, userId, groupId, false, 0, 0, 0, titleMap, titleMap, null,
			null, null, LayoutConstants.TYPE_UTILITY, typeSettings, true, true,
			new HashMap<>(), masterLayoutPlid, serviceContext);

		Layout draftLayout = layout.fetchDraftLayout();

		if (masterLayoutPlid > 0) {
			LayoutSet layoutSet = _layoutSetLocalService.getLayoutSet(
				groupId, false);

			String themeId = layoutSet.getThemeId();

			String colorSchemeId = _getColorSchemeId(
				layout.getCompanyId(), themeId);

			_layoutLocalService.updateLookAndFeel(
				groupId, false, draftLayout.getLayoutId(), themeId,
				colorSchemeId, StringPool.BLANK);

			layout = _layoutLocalService.updateLookAndFeel(
				groupId, false, layout.getLayoutId(), themeId, colorSchemeId,
				StringPool.BLANK);
		}

		if (published) {
			return layout;
		}

		return _layoutLocalService.updateStatus(
			userId, layout.getPlid(), WorkflowConstants.STATUS_DRAFT,
			serviceContext);
	}

	private long _copyPreviewFileEntryId(
			long layoutUtilityPageEntryId, long previewFileEntryId,
			ServiceContext serviceContext)
		throws Exception {

		if (previewFileEntryId <= 0) {
			return previewFileEntryId;
		}

		FileEntry portletFileEntry = _portletFileRepository.getPortletFileEntry(
			previewFileEntryId);

		Folder folder = portletFileEntry.getFolder();

		String fileName =
			layoutUtilityPageEntryId + "_preview." +
				portletFileEntry.getExtension();

		FileEntry fileEntry = _portletFileRepository.addPortletFileEntry(
			portletFileEntry.getGroupId(), serviceContext.getUserId(),
			LayoutUtilityPageEntry.class.getName(), layoutUtilityPageEntryId,
			LayoutAdminPortletKeys.GROUP_PAGES, folder.getFolderId(),
			_file.getBytes(portletFileEntry.getContentStream()), fileName,
			portletFileEntry.getMimeType(), false);

		return fileEntry.getFileEntryId();
	}

	private String _getColorSchemeId(long companyId, String themeId) {
		ColorScheme colorScheme = _themeLocalService.getColorScheme(
			companyId, themeId, StringPool.BLANK);

		return colorScheme.getColorSchemeId();
	}

	private String _getExternalReferenceCode(long groupId, String name) {
		String externalReferenceCode = StringUtil.toLowerCase(name.trim());

		externalReferenceCode = StringUtil.replace(
			externalReferenceCode, CharPool.SPACE, CharPool.DASH);

		int count = 0;

		while (true) {
			LayoutUtilityPageEntry layoutUtilityPageEntry =
				layoutUtilityPageEntryPersistence.fetchByERC_G(
					externalReferenceCode, groupId);

			if (layoutUtilityPageEntry == null) {
				return externalReferenceCode;
			}

			externalReferenceCode =
				externalReferenceCode + CharPool.DASH + count++;
		}
	}

	private String _getUniqueCopyName(
		long groupId, String sourceName, String type) {

		String copy = _language.get(LocaleUtil.getSiteDefault(), "copy");

		String name = StringUtil.appendParentheticalSuffix(sourceName, copy);

		for (int i = 1;; i++) {
			LayoutUtilityPageEntry layoutUtilityPageEntry =
				layoutUtilityPageEntryPersistence.fetchByG_N_T(
					groupId, name, type);

			if (layoutUtilityPageEntry == null) {
				break;
			}

			name = StringUtil.appendParentheticalSuffix(
				sourceName, copy + StringPool.SPACE + i);
		}

		return name;
	}

	private void _validateName(
			long groupId, long layoutUtilityPageEntryId, String name,
			String type)
		throws PortalException {

		if (Validator.isNull(name)) {
			throw new LayoutUtilityPageEntryNameException.MustNotBeNull(
				groupId);
		}

		int nameMaxLength = ModelHintsUtil.getMaxLength(
			LayoutUtilityPageEntry.class.getName(), "name");

		if (name.length() > nameMaxLength) {
			throw new LayoutUtilityPageEntryNameException.
				MustNotExceedMaximumSize(nameMaxLength);
		}

		Character character = LayoutValidator.getBlacklistCharacter(name);

		if (character != null) {
			throw new LayoutUtilityPageEntryNameException.
				MustNotContainInvalidCharacters(character);
		}

		LayoutUtilityPageEntry duplicatedLayoutUtilityPageEntry =
			layoutUtilityPageEntryPersistence.fetchByG_N_T(groupId, name, type);

		if ((duplicatedLayoutUtilityPageEntry != null) &&
			(duplicatedLayoutUtilityPageEntry.getLayoutUtilityPageEntryId() !=
				layoutUtilityPageEntryId)) {

			throw new LayoutUtilityPageEntryNameException.MustNotBeDuplicate(
				groupId, name);
		}
	}

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private File _file;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference
	private PortletFileRepository _portletFileRepository;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private ThemeLocalService _themeLocalService;

	@Reference
	private UserLocalService _userLocalService;

}