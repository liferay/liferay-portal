/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mentions.web.internal.notifications;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.mentions.constants.MentionsPortletKeys;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.BaseModelUserNotificationHandler;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera
 * @author Sergio González
 */
@Component(
	property = "jakarta.portlet.name=" + MentionsPortletKeys.MENTIONS,
	service = UserNotificationHandler.class
)
public class MentionsUserNotificationHandler
	extends BaseModelUserNotificationHandler {

	public MentionsUserNotificationHandler() {
		setPortletId(MentionsPortletKeys.MENTIONS);
	}

	@Override
	public boolean isApplicable(
		UserNotificationEvent userNotificationEvent,
		ServiceContext serviceContext) {

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				userNotificationEvent.getPayload());

			String discussionClassName = jsonObject.getString(
				"discussionClassName");

			if (Validator.isNull(discussionClassName)) {
				return true;
			}

			long discussionClassPK = jsonObject.getLong("discussionClassPK");

			if (_isInTrash(discussionClassName, discussionClassPK)) {
				return false;
			}

			AssetRenderer<?> assetRenderer = getAssetRenderer(
				discussionClassName, discussionClassPK);

			if ((assetRenderer == null) ||
				!assetRenderer.hasViewPermission(
					PermissionThreadLocal.getPermissionChecker())) {

				return false;
			}

			return true;
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return false;
		}
	}

	@Override
	protected AssetRenderer<?> getAssetRenderer(JSONObject jsonObject) {
		String discussionClassName = jsonObject.getString(
			"discussionClassName");

		if (Validator.isNotNull(discussionClassName)) {
			return getAssetRenderer(
				discussionClassName, jsonObject.getLong("discussionClassPK"));
		}

		MBMessage mbMessage = _mbMessageLocalService.fetchMBMessage(
			jsonObject.getLong("classPK"));

		if ((mbMessage != null) && mbMessage.isDiscussion()) {
			return getAssetRenderer(
				mbMessage.getClassName(), mbMessage.getClassPK());
		}

		return getAssetRenderer(
			jsonObject.getString("className"), jsonObject.getLong("classPK"));
	}

	@Override
	protected String getBodyContent(JSONObject jsonObject) {
		if (_isCMSDiscussion(jsonObject)) {
			return StringPool.BLANK;
		}

		return super.getBodyContent(jsonObject);
	}

	@Override
	protected String getTitle(
		JSONObject jsonObject, AssetRenderer<?> assetRenderer,
		UserNotificationEvent userNotificationEvent,
		ServiceContext serviceContext) {

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", serviceContext.getLocale(), getClass());

		if (_isCMSDiscussion(jsonObject)) {
			String userGroupName = jsonObject.getString("userGroupName");

			if (Validator.isNotNull(userGroupName)) {
				return _language.format(
					resourceBundle, "x-mentioned-x-in-a-comment-on-x",
					new String[] {
						HtmlUtil.escape(
							_portal.getUserName(
								jsonObject.getLong("userId"),
								StringPool.BLANK)),
						HtmlUtil.escape(userGroupName),
						HtmlUtil.escape(
							assetRenderer.getTitle(serviceContext.getLocale()))
					},
					false);
			}

			return _language.format(
				resourceBundle, "x-mentioned-you-in-a-comment-on-x",
				new String[] {
					HtmlUtil.escape(
						_portal.getUserName(
							jsonObject.getLong("userId"), StringPool.BLANK)),
					HtmlUtil.escape(
						assetRenderer.getTitle(serviceContext.getLocale()))
				},
				false);
		}

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				assetRenderer.getClassName());

		String typeName = assetRendererFactory.getTypeName(
			serviceContext.getLocale());

		MBMessage mbMessage = _mbMessageLocalService.fetchMBMessage(
			jsonObject.getLong("classPK"));

		if ((mbMessage != null) && mbMessage.isDiscussion()) {
			return _language.format(
				resourceBundle, "x-mentioned-you-in-a-comment-in-a-x",
				new String[] {
					HtmlUtil.escape(
						_portal.getUserName(
							jsonObject.getLong("userId"), StringPool.BLANK)),
					StringUtil.toLowerCase(HtmlUtil.escape(typeName))
				},
				false);
		}

		return _language.format(
			resourceBundle, "x-mentioned-you-in-a-x",
			new String[] {
				HtmlUtil.escape(
					_portal.getUserName(
						jsonObject.getLong("userId"), StringPool.BLANK)),
				StringUtil.toLowerCase(HtmlUtil.escape(typeName))
			},
			false);
	}

	private boolean _isCMSDiscussion(JSONObject jsonObject) {
		if (!FeatureFlagManagerUtil.isEnabled(
				CompanyThreadLocal.getCompanyId(), "LPD-104086")) {

			return false;
		}

		String discussionClassName = jsonObject.getString(
			"discussionClassName");

		if (Validator.isNull(discussionClassName)) {
			MBMessage mbMessage = _mbMessageLocalService.fetchMBMessage(
				jsonObject.getLong("classPK"));

			if ((mbMessage == null) || !mbMessage.isDiscussion()) {
				return false;
			}

			discussionClassName = mbMessage.getClassName();
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				CompanyThreadLocal.getCompanyId(), discussionClassName);

		if ((objectDefinition != null) && objectDefinition.isCMS()) {
			return true;
		}

		return false;
	}

	private boolean _isInTrash(String className, long classPK)
		throws PortalException {

		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			className);

		if (trashHandler == null) {
			return false;
		}

		return trashHandler.isInTrash(classPK);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MentionsUserNotificationHandler.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private MBMessageLocalService _mbMessageLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private Portal _portal;

}