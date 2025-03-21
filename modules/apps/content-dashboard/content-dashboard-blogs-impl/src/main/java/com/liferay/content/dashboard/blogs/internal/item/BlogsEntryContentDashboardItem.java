/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.blogs.internal.item;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.content.dashboard.item.ContentDashboardItem;
import com.liferay.content.dashboard.item.ContentDashboardItemVersion;
import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.content.dashboard.item.action.ContentDashboardItemActionProviderRegistry;
import com.liferay.content.dashboard.item.action.exception.ContentDashboardItemActionException;
import com.liferay.content.dashboard.item.action.provider.ContentDashboardItemActionProvider;
import com.liferay.content.dashboard.item.type.ContentDashboardItemSubtype;
import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemReference;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Cristina González
 */
public class BlogsEntryContentDashboardItem
	implements ContentDashboardItem<BlogsEntry> {

	public BlogsEntryContentDashboardItem(
		List<AssetCategory> assetCategories, List<AssetTag> assetTags,
		BlogsEntry blogsEntry,
		ContentDashboardItemActionProviderRegistry
			contentDashboardItemActionProviderRegistry,
		Group group, Language language, Portal portal) {

		if (ListUtil.isEmpty(assetCategories)) {
			_assetCategories = Collections.emptyList();
		}
		else {
			_assetCategories = Collections.unmodifiableList(assetCategories);
		}

		if (ListUtil.isEmpty(assetTags)) {
			_assetTags = Collections.emptyList();
		}
		else {
			_assetTags = Collections.unmodifiableList(assetTags);
		}

		_blogsEntry = blogsEntry;
		_contentDashboardItemActionProviderRegistry =
			contentDashboardItemActionProviderRegistry;
		_group = group;
		_language = language;
		_portal = portal;
	}

	@Override
	public List<AssetCategory> getAssetCategories() {
		return _assetCategories;
	}

	@Override
	public List<AssetCategory> getAssetCategories(long assetVocabularyId) {
		return ListUtil.filter(
			_assetCategories,
			assetCategory ->
				assetCategory.getVocabularyId() == assetVocabularyId);
	}

	@Override
	public List<AssetTag> getAssetTags() {
		return _assetTags;
	}

	@Override
	public List<Locale> getAvailableLocales() {
		return Collections.emptyList();
	}

	@Override
	public List<ContentDashboardItemAction> getContentDashboardItemActions(
		HttpServletRequest httpServletRequest,
		ContentDashboardItemAction.Type... types) {

		return TransformUtil.transform(
			_contentDashboardItemActionProviderRegistry.
				getContentDashboardItemActionProviders(
					BlogsEntry.class.getName(), types),
			contentDashboardItemActionProvider -> {
				try {
					return contentDashboardItemActionProvider.
						getContentDashboardItemAction(
							_blogsEntry, httpServletRequest);
				}
				catch (ContentDashboardItemActionException
							contentDashboardItemActionException) {

					_log.error(contentDashboardItemActionException);
				}

				return null;
			});
	}

	@Override
	public ContentDashboardItemSubtype getContentDashboardItemSubtype() {
		return null;
	}

	@Override
	public Date getCreateDate() {
		return _blogsEntry.getCreateDate();
	}

	@Override
	public ContentDashboardItemAction getDefaultContentDashboardItemAction(
		HttpServletRequest httpServletRequest) {

		long userId = _portal.getUserId(httpServletRequest);

		Locale locale = _portal.getLocale(httpServletRequest);

		ContentDashboardItemVersion version =
			_getLastContentDashboardItemVersion(locale);

		if ((getUserId() == userId) &&
			Objects.equals(
				version.getLabel(),
				_language.get(
					locale,
					WorkflowConstants.getStatusLabel(
						WorkflowConstants.STATUS_DRAFT)))) {

			ContentDashboardItemActionProvider
				contentDashboardItemActionProvider =
					_contentDashboardItemActionProviderRegistry.
						getContentDashboardItemActionProvider(
							BlogsEntry.class.getName(),
							ContentDashboardItemAction.Type.EDIT);

			if (contentDashboardItemActionProvider == null) {
				return null;
			}

			ContentDashboardItemAction contentDashboardItemAction =
				_toContentDashboardItemAction(
					contentDashboardItemActionProvider, httpServletRequest);

			if (contentDashboardItemAction == null) {
				return null;
			}

			return contentDashboardItemAction;
		}

		ContentDashboardItemActionProvider
			viewContentDashboardItemActionProvider =
				_contentDashboardItemActionProviderRegistry.
					getContentDashboardItemActionProvider(
						BlogsEntry.class.getName(),
						ContentDashboardItemAction.Type.VIEW);

		if (viewContentDashboardItemActionProvider == null) {
			return _getContentDashboardItemAction(httpServletRequest);
		}

		ContentDashboardItemAction viewContentDashboardItemAction =
			_toContentDashboardItemAction(
				viewContentDashboardItemActionProvider, httpServletRequest);

		if (viewContentDashboardItemAction == null) {
			return _getContentDashboardItemAction(httpServletRequest);
		}

		return viewContentDashboardItemAction;
	}

	@Override
	public Locale getDefaultLocale() {
		try {
			return _portal.getSiteDefaultLocale(_blogsEntry.getGroupId());
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return LocaleUtil.getDefault();
		}
	}

	@Override
	public String getDescription(Locale locale) {
		return _blogsEntry.getSubtitle();
	}

	@Override
	public long getId() {
		return _blogsEntry.getEntryId();
	}

	@Override
	public InfoItemReference getInfoItemReference() {
		return new InfoItemReference(
			BlogsEntry.class.getName(), _blogsEntry.getEntryId());
	}

	@Override
	public List<ContentDashboardItemVersion>
		getLatestContentDashboardItemVersions(Locale locale) {

		return Collections.singletonList(
			new ContentDashboardItemVersion(
				null, null, _blogsEntry.getCreateDate(),
				_language.get(
					locale,
					WorkflowConstants.getStatusLabel(_blogsEntry.getStatus())),
				null, WorkflowConstants.getStatusStyle(_blogsEntry.getStatus()),
				_blogsEntry.getStatusByUserName(), "1.0"));
	}

	@Override
	public Date getModifiedDate() {
		return _blogsEntry.getModifiedDate();
	}

	@Override
	public String getScopeName(Locale locale) {
		if (_group == null) {
			return StringPool.BLANK;
		}

		try {
			String descriptiveName = _group.getDescriptiveName(locale);

			if (descriptiveName != null) {
				return descriptiveName;
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return _group.getName(locale);
	}

	@Override
	public List<SpecificInformation<?>> getSpecificInformationList(
		Locale locale) {

		return Collections.singletonList(
			new SpecificInformation<>(
				"display-date", SpecificInformation.Type.DATE,
				_blogsEntry.getDisplayDate()));
	}

	@Override
	public String getTitle(Locale locale) {
		return _blogsEntry.getTitle();
	}

	@Override
	public String getTypeLabel(Locale locale) {
		InfoItemClassDetails infoItemClassDetails = new InfoItemClassDetails(
			BlogsEntry.class.getName());

		return infoItemClassDetails.getLabel(locale);
	}

	@Override
	public long getUserId() {
		return _blogsEntry.getUserId();
	}

	@Override
	public String getUserName() {
		return _blogsEntry.getUserName();
	}

	@Override
	public boolean isViewable(HttpServletRequest httpServletRequest) {
		ContentDashboardItemActionProvider contentDashboardItemActionProvider =
			_contentDashboardItemActionProviderRegistry.
				getContentDashboardItemActionProvider(
					BlogsEntry.class.getName(),
					ContentDashboardItemAction.Type.VIEW);

		if (contentDashboardItemActionProvider == null) {
			return false;
		}

		return contentDashboardItemActionProvider.isShow(
			_blogsEntry, httpServletRequest);
	}

	private ContentDashboardItemAction _getContentDashboardItemAction(
		HttpServletRequest httpServletRequest) {

		ContentDashboardItemActionProvider
			editContentDashboardItemActionProvider =
				_contentDashboardItemActionProviderRegistry.
					getContentDashboardItemActionProvider(
						BlogsEntry.class.getName(),
						ContentDashboardItemAction.Type.EDIT);

		if (editContentDashboardItemActionProvider == null) {
			return null;
		}

		ContentDashboardItemAction contentDashboardItemAction =
			_toContentDashboardItemAction(
				editContentDashboardItemActionProvider, httpServletRequest);

		if (contentDashboardItemAction == null) {
			return null;
		}

		return contentDashboardItemAction;
	}

	private ContentDashboardItemVersion _getLastContentDashboardItemVersion(
		Locale locale) {

		List<ContentDashboardItemVersion> contentDashboardItemVersions =
			getLatestContentDashboardItemVersions(locale);

		return contentDashboardItemVersions.get(
			contentDashboardItemVersions.size() - 1);
	}

	private ContentDashboardItemAction _toContentDashboardItemAction(
		ContentDashboardItemActionProvider contentDashboardItemActionProvider,
		HttpServletRequest httpServletRequest) {

		try {
			return contentDashboardItemActionProvider.
				getContentDashboardItemAction(_blogsEntry, httpServletRequest);
		}
		catch (ContentDashboardItemActionException
					contentDashboardItemActionException) {

			_log.error(contentDashboardItemActionException);

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BlogsEntryContentDashboardItem.class);

	private final List<AssetCategory> _assetCategories;
	private final List<AssetTag> _assetTags;
	private final BlogsEntry _blogsEntry;
	private final ContentDashboardItemActionProviderRegistry
		_contentDashboardItemActionProviderRegistry;
	private final Group _group;
	private final Language _language;
	private final Portal _portal;

}