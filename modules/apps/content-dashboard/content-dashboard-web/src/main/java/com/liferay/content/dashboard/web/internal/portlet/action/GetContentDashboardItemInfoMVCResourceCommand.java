/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.portlet.action;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.content.dashboard.item.ContentDashboardItem;
import com.liferay.content.dashboard.item.ContentDashboardItemFactory;
import com.liferay.content.dashboard.item.ContentDashboardItemVersion;
import com.liferay.content.dashboard.item.VersionableContentDashboardItem;
import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.content.dashboard.item.type.ContentDashboardItemSubtype;
import com.liferay.content.dashboard.web.internal.constants.ContentDashboardPortletKeys;
import com.liferay.content.dashboard.web.internal.item.ContentDashboardItemFactoryRegistry;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentDashboardPortletKeys.CONTENT_DASHBOARD_ADMIN,
		"mvc.command.name=/content_dashboard/get_content_dashboard_item_info"
	},
	service = MVCResourceCommand.class
)
public class GetContentDashboardItemInfoMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			resourceRequest);
		Locale locale = _portal.getLocale(resourceRequest);
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			String className = ParamUtil.getString(
				resourceRequest, "className");

			ContentDashboardItemFactory<?> contentDashboardItemFactory =
				_contentDashboardItemFactoryRegistry.
					getContentDashboardItemFactory(className);

			if (contentDashboardItemFactory == null) {
				JSONPortletResponseUtil.writeJSON(
					resourceRequest, resourceResponse,
					_jsonFactory.createJSONArray());

				return;
			}

			long classPK = GetterUtil.getLong(
				ParamUtil.getLong(resourceRequest, "classPK"));

			ContentDashboardItem<?> contentDashboardItem =
				contentDashboardItemFactory.create(classPK);

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"className", _getClassName(contentDashboardItem)
				).put(
					"classPK", _getClassPK(contentDashboardItem)
				).put(
					"createDate",
					_toString(contentDashboardItem.getCreateDate())
				).put(
					"description", contentDashboardItem.getDescription(locale)
				).put(
					"downloadURL",
					_getDownloadURL(contentDashboardItem, httpServletRequest)
				).put(
					"fetchSharingButtonURL",
					_getFetchSharingButtonURL(
						contentDashboardItem, httpServletRequest)
				).put(
					"fetchSharingCollaboratorsURL",
					_getFetchSharingCollaboratorsURL(
						contentDashboardItem, httpServletRequest)
				).put(
					"getItemVersionsURL",
					() -> {
						if (!(contentDashboardItem instanceof
								VersionableContentDashboardItem)) {

							return null;
						}

						VersionableContentDashboardItem
							versionableContentDashboardItem =
								(VersionableContentDashboardItem)
									contentDashboardItem;

						if (!versionableContentDashboardItem.
								isShowContentDashboardItemVersions(
									httpServletRequest)) {

							return null;
						}

						return ResourceURLBuilder.createResourceURL(
							resourceResponse
						).setParameter(
							"className", className
						).setParameter(
							"classPK", classPK
						).setResourceID(
							"/content_dashboard" +
								"/get_content_dashboard_item_versions"
						).buildString();
					}
				).put(
					"languageTag", locale.toLanguageTag()
				).put(
					"latestVersions",
					_getLatestContentDashboardItemVersionsJSONArray(
						contentDashboardItem, locale)
				).put(
					"modifiedDate",
					_toString(contentDashboardItem.getModifiedDate())
				).put(
					"preview",
					_getPreviewJSONObject(
						contentDashboardItem, httpServletRequest)
				).put(
					"showItemVersions",
					() -> {
						if (!(contentDashboardItem instanceof
								VersionableContentDashboardItem)) {

							return false;
						}

						VersionableContentDashboardItem
							versionableContentDashboardItem =
								(VersionableContentDashboardItem)
									contentDashboardItem;

						return versionableContentDashboardItem.
							isShowContentDashboardItemVersions(
								httpServletRequest);
					}
				).put(
					"specificFields",
					_getSpecificFieldsJSONArray(contentDashboardItem, locale)
				).put(
					"subscribe",
					_getSubscribeJSONObject(
						contentDashboardItem, httpServletRequest)
				).put(
					"subType",
					() -> {
						ContentDashboardItemSubtype<?>
							contentDashboardItemSubtype =
								contentDashboardItem.
									getContentDashboardItemSubtype();

						if (contentDashboardItemSubtype == null) {
							return StringPool.BLANK;
						}

						return contentDashboardItemSubtype.getLabel(locale);
					}
				).put(
					"tags", _getAssetTagsJSONArray(contentDashboardItem)
				).put(
					"title", contentDashboardItem.getTitle(locale)
				).put(
					"type", contentDashboardItem.getTypeLabel(locale)
				).put(
					"user",
					_getUserJSONObject(contentDashboardItem, themeDisplay)
				).put(
					"viewURLs",
					_getViewURLsJSONArray(
						contentDashboardItem, httpServletRequest)
				).put(
					"vocabularies",
					_getAssetVocabulariesJSONObject(
						contentDashboardItem, locale)
				));
		}
		catch (Exception exception) {
			if (_log.isInfoEnabled()) {
				_log.info(exception);
			}

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"error",
					ResourceBundleUtil.getString(
						ResourceBundleUtil.getBundle(locale, getClass()),
						"an-unexpected-error-occurred")));
		}
	}

	private JSONArray _getAssetTagsJSONArray(
		ContentDashboardItem contentDashboardItem) {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		List<AssetTag> assetTags = contentDashboardItem.getAssetTags();

		for (AssetTag assetTag : assetTags) {
			jsonArray.put(assetTag.getName());
		}

		return jsonArray;
	}

	private JSONObject _getAssetVocabulariesJSONObject(
		ContentDashboardItem contentDashboardItem, Locale locale) {

		Map<Long, Map<String, Object>> assetVocabularyMaps = new HashMap<>();

		List<AssetCategory> assetCategories =
			contentDashboardItem.getAssetCategories();

		for (AssetCategory assetCategory : assetCategories) {
			assetVocabularyMaps.computeIfAbsent(
				assetCategory.getVocabularyId(),
				vocabularyId -> _getAssetVocabularyMap(
					_assetVocabularyLocalService.fetchAssetVocabulary(
						vocabularyId),
					locale));

			Map<String, Object> assetVocabularyMap = assetVocabularyMaps.get(
				assetCategory.getVocabularyId());

			List<String> assetCategoryTitles =
				(List<String>)assetVocabularyMap.get("categories");

			assetCategoryTitles.add(assetCategory.getTitle(locale));
		}

		return _jsonFactory.createJSONObject(assetVocabularyMaps);
	}

	private Map<String, Object> _getAssetVocabularyMap(
		AssetVocabulary assetVocabulary, Locale locale) {

		return HashMapBuilder.<String, Object>put(
			"categories", ListUtil.fromArray()
		).put(
			"groupName",
			() -> {
				Group group = _groupLocalService.fetchGroup(
					assetVocabulary.getGroupId());

				if (group == null) {
					return StringPool.BLANK;
				}

				try {
					return group.getDescriptiveName(locale);
				}
				catch (PortalException portalException) {
					_log.error(portalException);

					return group.getName(locale);
				}
			}
		).put(
			"isPublic",
			assetVocabulary.getVisibilityType() ==
				AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC
		).put(
			"vocabularyName", assetVocabulary.getTitle(locale)
		).build();
	}

	private String _getClassName(ContentDashboardItem<?> contentDashboardItem) {
		InfoItemReference infoItemReference =
			contentDashboardItem.getInfoItemReference();

		return infoItemReference.getClassName();
	}

	private long _getClassPK(ContentDashboardItem<?> contentDashboardItem) {
		InfoItemReference infoItemReference =
			contentDashboardItem.getInfoItemReference();

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier)) {
			return 0;
		}

		ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
			(ClassPKInfoItemIdentifier)
				infoItemReference.getInfoItemIdentifier();

		return classPKInfoItemIdentifier.getClassPK();
	}

	private String _getDownloadURL(
		ContentDashboardItem contentDashboardItem,
		HttpServletRequest httpServletRequest) {

		List<ContentDashboardItemAction> contentDashboardItemActions =
			contentDashboardItem.getContentDashboardItemActions(
				httpServletRequest, ContentDashboardItemAction.Type.DOWNLOAD);

		if (ListUtil.isEmpty(contentDashboardItemActions)) {
			return null;
		}

		ContentDashboardItemAction contentDashboardItemAction =
			contentDashboardItemActions.get(0);

		return contentDashboardItemAction.getURL();
	}

	private String _getFetchSharingButtonURL(
		ContentDashboardItem contentDashboardItem,
		HttpServletRequest httpServletRequest) {

		List<ContentDashboardItemAction> contentDashboardItemActions =
			contentDashboardItem.getContentDashboardItemActions(
				httpServletRequest,
				ContentDashboardItemAction.Type.SHARING_BUTTON);

		if (ListUtil.isEmpty(contentDashboardItemActions)) {
			return null;
		}

		ContentDashboardItemAction contentDashboardItemAction =
			contentDashboardItemActions.get(0);

		return contentDashboardItemAction.getURL();
	}

	private String _getFetchSharingCollaboratorsURL(
		ContentDashboardItem contentDashboardItem,
		HttpServletRequest httpServletRequest) {

		List<ContentDashboardItemAction> contentDashboardItemActions =
			contentDashboardItem.getContentDashboardItemActions(
				httpServletRequest,
				ContentDashboardItemAction.Type.SHARING_COLLABORATORS);

		if (ListUtil.isEmpty(contentDashboardItemActions)) {
			return null;
		}

		ContentDashboardItemAction contentDashboardItemAction =
			contentDashboardItemActions.get(0);

		return contentDashboardItemAction.getURL();
	}

	private JSONArray _getLatestContentDashboardItemVersionsJSONArray(
		ContentDashboardItem contentDashboardItem, Locale locale) {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		List<ContentDashboardItemVersion> latestContentDashboardItemVersions =
			contentDashboardItem.getLatestContentDashboardItemVersions(locale);

		for (ContentDashboardItemVersion contentDashboardItemVersion :
				latestContentDashboardItemVersions) {

			jsonArray.put(contentDashboardItemVersion.toJSONObject());
		}

		return jsonArray;
	}

	private String _getPreviewImageURL(
		ContentDashboardItem contentDashboardItem,
		HttpServletRequest httpServletRequest) {

		List<ContentDashboardItemAction> contentDashboardItemActions =
			contentDashboardItem.getContentDashboardItemActions(
				httpServletRequest,
				ContentDashboardItemAction.Type.PREVIEW_IMAGE);

		if (ListUtil.isEmpty(contentDashboardItemActions)) {
			return null;
		}

		ContentDashboardItemAction contentDashboardItemAction =
			contentDashboardItemActions.get(0);

		return contentDashboardItemAction.getURL();
	}

	private JSONObject _getPreviewJSONObject(
		ContentDashboardItem contentDashboardItem,
		HttpServletRequest httpServletRequest) {

		return JSONUtil.put(
			"imageURL",
			_getPreviewImageURL(contentDashboardItem, httpServletRequest)
		).put(
			"url", _getPreviewURL(contentDashboardItem, httpServletRequest)
		);
	}

	private String _getPreviewURL(
		ContentDashboardItem contentDashboardItem,
		HttpServletRequest httpServletRequest) {

		List<ContentDashboardItemAction> contentDashboardItemActions =
			contentDashboardItem.getContentDashboardItemActions(
				httpServletRequest, ContentDashboardItemAction.Type.PREVIEW);

		if (ListUtil.isEmpty(contentDashboardItemActions)) {
			return null;
		}

		ContentDashboardItemAction contentDashboardItemAction =
			contentDashboardItemActions.get(0);

		return contentDashboardItemAction.getURL();
	}

	private JSONArray _getSpecificFieldsJSONArray(
		ContentDashboardItem contentDashboardItem, Locale locale) {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		List<ContentDashboardItem.SpecificInformation<?>>
			specificInformationList =
				contentDashboardItem.getSpecificInformationList(locale);

		for (ContentDashboardItem.SpecificInformation specificInformation :
				specificInformationList) {

			jsonArray.put(specificInformation.toJSONObject(_language, locale));
		}

		return jsonArray;
	}

	private JSONObject _getSubscribeContentDashboardItemActionJSONObject(
		ContentDashboardItem contentDashboardItem,
		HttpServletRequest httpServletRequest) {

		List<ContentDashboardItemAction> contentDashboardItemActions =
			contentDashboardItem.getContentDashboardItemActions(
				httpServletRequest, ContentDashboardItemAction.Type.SUBSCRIBE);

		if (ListUtil.isEmpty(contentDashboardItemActions)) {
			return null;
		}

		ContentDashboardItemAction contentDashboardItemAction =
			contentDashboardItemActions.get(0);

		return JSONUtil.put(
			"disabled", contentDashboardItemAction.isDisabled()
		).put(
			"icon", contentDashboardItemAction.getIcon()
		).put(
			"label",
			contentDashboardItemAction.getLabel(
				_portal.getLocale(httpServletRequest))
		).put(
			"url", contentDashboardItemAction.getURL()
		);
	}

	private JSONObject _getSubscribeJSONObject(
		ContentDashboardItem contentDashboardItem,
		HttpServletRequest httpServletRequest) {

		JSONObject jsonObject =
			_getSubscribeContentDashboardItemActionJSONObject(
				contentDashboardItem, httpServletRequest);

		if (jsonObject != null) {
			return jsonObject;
		}

		return _getUnSubscribeContentDashboardItemActionJSONObject(
			contentDashboardItem, httpServletRequest);
	}

	private JSONObject _getUnSubscribeContentDashboardItemActionJSONObject(
		ContentDashboardItem contentDashboardItem,
		HttpServletRequest httpServletRequest) {

		List<ContentDashboardItemAction> contentDashboardItemActions =
			contentDashboardItem.getContentDashboardItemActions(
				httpServletRequest,
				ContentDashboardItemAction.Type.UNSUBSCRIBE);

		if (ListUtil.isEmpty(contentDashboardItemActions)) {
			return null;
		}

		ContentDashboardItemAction contentDashboardItemAction =
			contentDashboardItemActions.get(0);

		return JSONUtil.put(
			"disabled", contentDashboardItemAction.isDisabled()
		).put(
			"icon", contentDashboardItemAction.getIcon()
		).put(
			"label",
			contentDashboardItemAction.getLabel(
				_portal.getLocale(httpServletRequest))
		).put(
			"url", contentDashboardItemAction.getURL()
		);
	}

	private JSONObject _getUserJSONObject(
		ContentDashboardItem contentDashboardItem, ThemeDisplay themeDisplay) {

		return JSONUtil.put(
			"name", contentDashboardItem.getUserName()
		).put(
			"url",
			() -> {
				User user = _userLocalService.fetchUser(
					contentDashboardItem.getUserId());

				if ((user == null) || (user.getPortraitId() <= 0)) {
					return null;
				}

				try {
					return user.getPortraitURL(themeDisplay);
				}
				catch (PortalException portalException) {
					_log.error(portalException);
				}

				return null;
			}
		).put(
			"userId", contentDashboardItem.getUserId()
		);
	}

	private JSONArray _getViewURLsJSONArray(
		ContentDashboardItem contentDashboardItem,
		HttpServletRequest httpServletRequest) {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		List<ContentDashboardItemAction> contentDashboardItemActions =
			contentDashboardItem.getContentDashboardItemActions(
				httpServletRequest, ContentDashboardItemAction.Type.VIEW);

		List<Locale> locales = contentDashboardItem.getAvailableLocales();

		if (ListUtil.isEmpty(contentDashboardItemActions)) {
			for (Locale locale : locales) {
				jsonArray.put(
					JSONUtil.put(
						"default",
						Objects.equals(
							locale, contentDashboardItem.getDefaultLocale())
					).put(
						"languageId", LocaleUtil.toBCP47LanguageId(locale)
					));
			}

			return jsonArray;
		}

		ContentDashboardItemAction contentDashboardItemAction =
			contentDashboardItemActions.get(0);

		for (Locale locale : locales) {
			jsonArray.put(
				JSONUtil.put(
					"default",
					Objects.equals(
						locale, contentDashboardItem.getDefaultLocale())
				).put(
					"languageId", LocaleUtil.toBCP47LanguageId(locale)
				).put(
					"viewURL", contentDashboardItemAction.getURL(locale)
				));
		}

		return jsonArray;
	}

	private String _toString(Date date) {
		Instant instant = date.toInstant();

		ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

		LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();

		return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetContentDashboardItemInfoMVCResourceCommand.class);

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private ContentDashboardItemFactoryRegistry
		_contentDashboardItemFactoryRegistry;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}