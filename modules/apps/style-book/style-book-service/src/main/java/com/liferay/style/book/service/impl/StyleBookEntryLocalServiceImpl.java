/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service.impl;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.style.book.exception.DuplicateStyleBookEntryKeyException;
import com.liferay.style.book.exception.StyleBookEntryNameException;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.base.StyleBookEntryLocalServiceBaseImpl;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 * @see    StyleBookEntryLocalServiceBaseImpl
 */
@Component(
	property = "model.class.name=com.liferay.style.book.model.StyleBookEntry",
	service = AopService.class
)
public class StyleBookEntryLocalServiceImpl
	extends StyleBookEntryLocalServiceBaseImpl {

	@Override
	public StyleBookEntry addStyleBookEntry(
			String externalReferenceCode, long userId, long groupId,
			boolean defaultStyleBookEntry, String frontendTokensValues,
			String name, String styleBookEntryKey, String themeId,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		long companyId = user.getCompanyId();

		if (serviceContext != null) {
			companyId = serviceContext.getCompanyId();
		}
		else {
			serviceContext = new ServiceContext();
		}

		_validate(name);

		if (Validator.isNull(styleBookEntryKey)) {
			styleBookEntryKey = generateStyleBookEntryKey(groupId, name);
		}
		else {
			styleBookEntryKey = _getStyleBookEntryKey(styleBookEntryKey);
		}

		_validateStyleBookEntryKey(groupId, styleBookEntryKey);

		StyleBookEntry styleBookEntry = create();

		String uuid = serviceContext.getUuid();

		if (Validator.isNotNull(uuid)) {
			styleBookEntry.setUuid(uuid);
		}

		styleBookEntry.setExternalReferenceCode(externalReferenceCode);
		styleBookEntry.setGroupId(groupId);
		styleBookEntry.setCompanyId(companyId);
		styleBookEntry.setUserId(user.getUserId());
		styleBookEntry.setUserName(user.getFullName());
		styleBookEntry.setCreateDate(serviceContext.getCreateDate(new Date()));
		styleBookEntry.setDefaultStyleBookEntry(defaultStyleBookEntry);
		styleBookEntry.setFrontendTokensValues(frontendTokensValues);
		styleBookEntry.setName(name);
		styleBookEntry.setStyleBookEntryKey(styleBookEntryKey);

		if (FeatureFlagManagerUtil.isEnabled("LPD-30204")) {
			styleBookEntry.setThemeId(themeId);
		}
		else {
			LayoutSet publicLayoutSet = _layoutSetLocalService.getLayoutSet(
				groupId, false);

			styleBookEntry.setThemeId(publicLayoutSet.getThemeId());
		}

		return publishDraft(styleBookEntry);
	}

	@Override
	public StyleBookEntry copyStyleBookEntry(
			long userId, long groupId, long sourceStyleBookEntryId,
			ServiceContext serviceContext)
		throws PortalException {

		StyleBookEntry sourceStyleBookEntry = getStyleBookEntry(
			sourceStyleBookEntryId);

		String name = _getUniqueCopyName(sourceStyleBookEntry);

		StyleBookEntry targetStyleBookEntry = addStyleBookEntry(
			null, userId, groupId, false,
			sourceStyleBookEntry.getFrontendTokensValues(), name,
			StringPool.BLANK, sourceStyleBookEntry.getThemeId(),
			serviceContext);

		long previewFileEntryId = _copyStyleBookEntryPreviewFileEntry(
			userId, groupId, sourceStyleBookEntry, targetStyleBookEntry);

		StyleBookEntry draftStyleBookEntry = fetchDraft(sourceStyleBookEntry);

		if (draftStyleBookEntry != null) {
			StyleBookEntry copyDraftStyleBookEntry = getDraft(
				targetStyleBookEntry);

			copyDraftStyleBookEntry.setFrontendTokensValues(
				draftStyleBookEntry.getFrontendTokensValues());

			updateDraft(copyDraftStyleBookEntry);
		}

		return updatePreviewFileEntryId(
			targetStyleBookEntry.getStyleBookEntryId(), previewFileEntryId);
	}

	@Override
	public void deleteStyleBookEntries(long groupId) throws PortalException {
		for (StyleBookEntry styleBookEntry :
				styleBookEntryPersistence.findByGroupId_Head(groupId, true)) {

			deleteStyleBookEntry(styleBookEntry);
		}
	}

	@Override
	public StyleBookEntry deleteStyleBookEntry(long styleBookEntryId)
		throws PortalException {

		return deleteStyleBookEntry(getStyleBookEntry(styleBookEntryId));
	}

	@Override
	public StyleBookEntry deleteStyleBookEntry(
			String externalReferenceCode, long groupId)
		throws PortalException {

		StyleBookEntry styleBookEntry =
			styleBookEntryPersistence.fetchByERC_G_Head(
				externalReferenceCode, groupId, true);

		return deleteStyleBookEntry(styleBookEntry);
	}

	@Override
	public StyleBookEntry deleteStyleBookEntry(StyleBookEntry styleBookEntry)
		throws PortalException {

		if (styleBookEntry.getPreviewFileEntryId() > 0) {
			PortletFileRepositoryUtil.deletePortletFileEntry(
				styleBookEntry.getPreviewFileEntryId());
		}

		return delete(styleBookEntry);
	}

	@Override
	public StyleBookEntry fetchDefaultStyleBookEntry(
		long groupId, String themeId) {

		if (FeatureFlagManagerUtil.isEnabled("LPD-30204")) {
			return styleBookEntryPersistence.fetchByG_D_T_First(
				groupId, true, themeId, null);
		}

		return styleBookEntryPersistence.fetchByG_D_Head_First(
			groupId, true, true, null);
	}

	@Override
	public StyleBookEntry fetchStyleBookEntry(
		long groupId, String styleBookEntryKey) {

		return styleBookEntryPersistence.fetchByG_SBEK_First(
			groupId, _getStyleBookEntryKey(styleBookEntryKey), null);
	}

	@Override
	public StyleBookEntry fetchStyleBookEntryByUuidAndGroupId(
		String uuid, long groupId) {

		StyleBookEntry styleBookEntry =
			styleBookEntryPersistence.fetchByUUID_G_Head(uuid, groupId, true);

		if (styleBookEntry != null) {
			return styleBookEntry;
		}

		return styleBookEntryPersistence.fetchByUUID_G_Head(
			uuid, groupId, false);
	}

	@Override
	public String generateStyleBookEntryKey(long groupId, String name) {
		String styleBookEntryKey = _getStyleBookEntryKey(name);

		styleBookEntryKey = StringUtil.replace(
			styleBookEntryKey, CharPool.SPACE, CharPool.DASH);

		String curStyleBookEntryKey = styleBookEntryKey;

		int count = 0;

		while (true) {
			StyleBookEntry styleBookEntry = fetchStyleBookEntry(
				groupId, curStyleBookEntryKey);

			if (styleBookEntry == null) {
				return curStyleBookEntryKey;
			}

			curStyleBookEntryKey = styleBookEntryKey + CharPool.DASH + count++;
		}
	}

	@Override
	public List<StyleBookEntry> getStyleBookEntries(
		long groupId, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator) {

		return styleBookEntryPersistence.findByGroupId_Head(
			groupId, true, start, end, orderByComparator);
	}

	@Override
	public List<StyleBookEntry> getStyleBookEntries(
		long groupId, String themeId) {

		return styleBookEntryPersistence.findByG_T(groupId, themeId);
	}

	@Override
	public List<StyleBookEntry> getStyleBookEntries(
		long groupId, String name, int start, int end,
		OrderByComparator<StyleBookEntry> orderByComparator) {

		return styleBookEntryPersistence.findByG_LikeN_Head(
			groupId, _customSQL.keywords(name, false, WildcardMode.SURROUND)[0],
			true, start, end, orderByComparator);
	}

	@Override
	public List<StyleBookEntry> getStyleBookEntriesByUuidAndCompanyId(
		String uuid, long companyId) {

		return styleBookEntryPersistence.findByUuid_C(uuid, companyId);
	}

	@Override
	public int getStyleBookEntriesCount(long groupId) {
		return styleBookEntryPersistence.countByGroupId_Head(groupId, true);
	}

	@Override
	public int getStyleBookEntriesCount(long groupId, String name) {
		return styleBookEntryPersistence.countByG_LikeN_Head(
			groupId, _customSQL.keywords(name, false, WildcardMode.SURROUND)[0],
			true);
	}

	@Override
	public StyleBookEntry updateDefaultStyleBookEntry(
			long styleBookEntryId, boolean defaultStyleBookEntry)
		throws PortalException {

		StyleBookEntry styleBookEntry = fetchStyleBookEntry(styleBookEntryId);

		if (styleBookEntry == null) {
			return null;
		}

		StyleBookEntry oldDefaultStyleBookEntry = null;

		if (FeatureFlagManagerUtil.isEnabled("LPD-30204")) {
			oldDefaultStyleBookEntry =
				styleBookEntryPersistence.fetchByG_D_T_First(
					styleBookEntry.getGroupId(), true,
					styleBookEntry.getThemeId(), null);
		}
		else {
			oldDefaultStyleBookEntry =
				styleBookEntryPersistence.fetchByG_D_First(
					styleBookEntry.getGroupId(), true, null);
		}

		if (defaultStyleBookEntry && (oldDefaultStyleBookEntry != null) &&
			(oldDefaultStyleBookEntry.getStyleBookEntryId() !=
				styleBookEntryId)) {

			oldDefaultStyleBookEntry.setDefaultStyleBookEntry(false);

			StyleBookEntry oldDefaultDraftStyleBookEntry = fetchDraft(
				oldDefaultStyleBookEntry);

			if (oldDefaultDraftStyleBookEntry != null) {
				oldDefaultDraftStyleBookEntry.setDefaultStyleBookEntry(
					defaultStyleBookEntry);

				updateDraft(oldDefaultDraftStyleBookEntry);
			}

			styleBookEntryPersistence.update(oldDefaultStyleBookEntry);
		}

		styleBookEntry.setModifiedDate(new Date());
		styleBookEntry.setDefaultStyleBookEntry(defaultStyleBookEntry);

		StyleBookEntry draftStyleBookEntry = fetchDraft(styleBookEntry);

		if (draftStyleBookEntry != null) {
			draftStyleBookEntry.setDefaultStyleBookEntry(defaultStyleBookEntry);

			updateDraft(draftStyleBookEntry);
		}

		return styleBookEntryPersistence.update(styleBookEntry);
	}

	@Override
	public StyleBookEntry updateFrontendTokensValues(
			long styleBookEntryId, String frontendTokensValues)
		throws PortalException {

		StyleBookEntry styleBookEntry =
			styleBookEntryPersistence.findByPrimaryKey(styleBookEntryId);

		styleBookEntry.setModifiedDate(new Date());
		styleBookEntry.setFrontendTokensValues(frontendTokensValues);

		StyleBookEntry draftStyleBookEntry = fetchDraft(styleBookEntry);

		if (draftStyleBookEntry != null) {
			draftStyleBookEntry.setModifiedDate(new Date());
			draftStyleBookEntry.setFrontendTokensValues(frontendTokensValues);

			updateDraft(draftStyleBookEntry);
		}

		return styleBookEntryPersistence.update(styleBookEntry);
	}

	@Override
	public StyleBookEntry updateName(long styleBookEntryId, String name)
		throws PortalException {

		StyleBookEntry styleBookEntry =
			styleBookEntryPersistence.findByPrimaryKey(styleBookEntryId);

		_validate(name);

		styleBookEntry.setModifiedDate(new Date());
		styleBookEntry.setName(name);

		StyleBookEntry draftStyleBookEntry = fetchDraft(styleBookEntry);

		if (draftStyleBookEntry != null) {
			draftStyleBookEntry.setModifiedDate(new Date());
			draftStyleBookEntry.setName(name);

			updateDraft(draftStyleBookEntry);
		}

		return styleBookEntryPersistence.update(styleBookEntry);
	}

	@Override
	public StyleBookEntry updatePreviewFileEntryId(
			long styleBookEntryId, long previewFileEntryId)
		throws PortalException {

		StyleBookEntry styleBookEntry =
			styleBookEntryPersistence.findByPrimaryKey(styleBookEntryId);

		styleBookEntry.setModifiedDate(new Date());
		styleBookEntry.setPreviewFileEntryId(previewFileEntryId);

		StyleBookEntry draftStyleBookEntry = fetchDraft(styleBookEntry);

		if (draftStyleBookEntry != null) {
			draftStyleBookEntry.setModifiedDate(new Date());
			draftStyleBookEntry.setPreviewFileEntryId(previewFileEntryId);

			updateDraft(draftStyleBookEntry);
		}

		return styleBookEntryPersistence.update(styleBookEntry);
	}

	@Override
	public StyleBookEntry updateStyleBookEntry(
			long userId, long styleBookEntryId, boolean defaultStylebookEntry,
			String frontendTokensValues, String name, String styleBookEntryKey,
			long previewFileEntryId)
		throws PortalException {

		StyleBookEntry styleBookEntry =
			styleBookEntryPersistence.findByPrimaryKey(styleBookEntryId);

		_validate(name);

		if (Validator.isNull(styleBookEntryKey)) {
			styleBookEntryKey = generateStyleBookEntryKey(
				styleBookEntry.getGroupId(), name);
		}
		else {
			styleBookEntryKey = _getStyleBookEntryKey(styleBookEntryKey);
		}

		if (!StringUtil.equals(
				_getStyleBookEntryKey(styleBookEntry.getStyleBookEntryKey()),
				styleBookEntryKey)) {

			_validateStyleBookEntryKey(
				styleBookEntry.getGroupId(), styleBookEntryKey);
		}

		styleBookEntry.setUserId(userId);
		styleBookEntry.setDefaultStyleBookEntry(defaultStylebookEntry);
		styleBookEntry.setFrontendTokensValues(frontendTokensValues);
		styleBookEntry.setName(name);
		styleBookEntry.setPreviewFileEntryId(previewFileEntryId);
		styleBookEntry.setStyleBookEntryKey(styleBookEntryKey);

		return styleBookEntryPersistence.update(styleBookEntry);
	}

	@Override
	public StyleBookEntry updateStyleBookEntry(
			long styleBookEntryId, String frontendTokensValues, String name)
		throws PortalException {

		StyleBookEntry styleBookEntry =
			styleBookEntryPersistence.findByPrimaryKey(styleBookEntryId);

		_validate(name);

		styleBookEntry.setModifiedDate(new Date());
		styleBookEntry.setFrontendTokensValues(frontendTokensValues);
		styleBookEntry.setName(name);

		StyleBookEntry draftStyleBookEntry = fetchDraft(styleBookEntry);

		if (draftStyleBookEntry != null) {
			draftStyleBookEntry.setModifiedDate(new Date());
			draftStyleBookEntry.setFrontendTokensValues(frontendTokensValues);
			draftStyleBookEntry.setName(name);

			updateDraft(draftStyleBookEntry);
		}

		return styleBookEntryPersistence.update(styleBookEntry);
	}

	private long _copyStyleBookEntryPreviewFileEntry(
			long userId, long groupId, StyleBookEntry sourceStyleBookEntry,
			StyleBookEntry copyStyleBookEntry)
		throws PortalException {

		if (sourceStyleBookEntry.getPreviewFileEntryId() == 0) {
			return 0;
		}

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(
			sourceStyleBookEntry.getPreviewFileEntryId());

		Repository repository =
			PortletFileRepositoryUtil.fetchPortletRepository(
				groupId, StyleBookPortletKeys.STYLE_BOOK);

		if (repository == null) {
			ServiceContext addPortletRepositoryServiceContext =
				new ServiceContext();

			addPortletRepositoryServiceContext.setAddGroupPermissions(true);
			addPortletRepositoryServiceContext.setAddGuestPermissions(true);

			repository = PortletFileRepositoryUtil.addPortletRepository(
				groupId, StyleBookPortletKeys.STYLE_BOOK,
				addPortletRepositoryServiceContext);
		}

		String fileName =
			copyStyleBookEntry.getStyleBookEntryId() + "_preview." +
				fileEntry.getExtension();

		fileEntry = PortletFileRepositoryUtil.addPortletFileEntry(
			null, groupId, userId, StyleBookEntry.class.getName(),
			copyStyleBookEntry.getStyleBookEntryId(),
			StyleBookPortletKeys.STYLE_BOOK, repository.getDlFolderId(),
			fileEntry.getContentStream(), fileName, fileEntry.getMimeType(),
			false);

		return fileEntry.getFileEntryId();
	}

	private String _getStyleBookEntryKey(String styleBookEntryKey) {
		if (styleBookEntryKey != null) {
			styleBookEntryKey = styleBookEntryKey.trim();

			return StringUtil.toLowerCase(styleBookEntryKey);
		}

		return StringPool.BLANK;
	}

	private String _getUniqueCopyName(StyleBookEntry styleBookEntry) {
		String copy = _language.get(LocaleUtil.getSiteDefault(), "copy");

		String name = StringUtil.appendParentheticalSuffix(
			styleBookEntry.getName(), copy);

		for (int i = 1;; i++) {
			StyleBookEntry existingStyleBookEntry =
				styleBookEntryPersistence.fetchByG_LikeN_First(
					styleBookEntry.getGroupId(), name, null);

			if (existingStyleBookEntry == null) {
				break;
			}

			name = StringUtil.appendParentheticalSuffix(
				styleBookEntry.getName(), copy + StringPool.SPACE + i);
		}

		return name;
	}

	private void _validate(String name) throws PortalException {
		if (Validator.isNull(name)) {
			throw new StyleBookEntryNameException("Name must not be null");
		}

		if (name.contains(StringPool.PERIOD) ||
			name.contains(StringPool.SLASH)) {

			throw new StyleBookEntryNameException(
				"Name contains invalid characters");
		}

		int nameMaxLength = ModelHintsUtil.getMaxLength(
			StyleBookEntry.class.getName(), "name");

		if (name.length() > nameMaxLength) {
			throw new StyleBookEntryNameException(
				"Maximum length of name exceeded");
		}
	}

	private void _validateStyleBookEntryKey(
			long groupId, String styleBookEntryKey)
		throws PortalException {

		styleBookEntryKey = _getStyleBookEntryKey(styleBookEntryKey);

		StyleBookEntry styleBookEntry = fetchStyleBookEntry(
			groupId, styleBookEntryKey);

		if (styleBookEntry != null) {
			throw new DuplicateStyleBookEntryKeyException();
		}
	}

	@Reference
	private CustomSQL _customSQL;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private Language _language;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

	@Reference
	private UserLocalService _userLocalService;

}