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
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.BaseModelUserNotificationHandler;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;

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
	protected AssetRenderer<?> getAssetRenderer(JSONObject jsonObject) {
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
		MBMessage mbMessage = _mbMessageLocalService.fetchMBMessage(
			jsonObject.getLong("classPK"));

		if (_isCMSDiscussion(mbMessage)) {
			return StringPool.BLANK;
		}

		return super.getBodyContent(jsonObject);
	}

	@Override
	protected String getTitle(
		JSONObject jsonObject, AssetRenderer<?> assetRenderer,
		UserNotificationEvent userNotificationEvent,
		ServiceContext serviceContext) {

		MBMessage mbMessage = _mbMessageLocalService.fetchMBMessage(
			jsonObject.getLong("classPK"));

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", serviceContext.getLocale(), getClass());

		if (_isCMSDiscussion(mbMessage)) {
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

	private boolean _isCMSDiscussion(MBMessage mbMessage) {
		if ((mbMessage == null) || !mbMessage.isDiscussion() ||
			!FeatureFlagManagerUtil.isEnabled(
				mbMessage.getCompanyId(), "LPD-104086")) {

			return false;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				mbMessage.getCompanyId(), mbMessage.getClassName());

		if ((objectDefinition != null) && objectDefinition.isCMS()) {
			return true;
		}

		return false;
	}

	@Reference
	private Language _language;

	@Reference
	private MBMessageLocalService _mbMessageLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private Portal _portal;

}