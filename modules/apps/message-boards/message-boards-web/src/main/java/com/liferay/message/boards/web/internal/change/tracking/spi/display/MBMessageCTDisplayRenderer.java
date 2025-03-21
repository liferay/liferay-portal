/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.context.DisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.LinkTag;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.util.MBUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = CTDisplayRenderer.class)
public class MBMessageCTDisplayRenderer
	extends BaseCTDisplayRenderer<MBMessage> {

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest, MBMessage mbMessage)
		throws PortalException {

		Group group = _groupLocalService.getGroup(mbMessage.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, group, MBPortletKeys.MESSAGE_BOARDS, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/message_boards/edit_message"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"messageId", mbMessage.getMessageId()
		).buildString();
	}

	@Override
	public Class<MBMessage> getModelClass() {
		return MBMessage.class;
	}

	@Override
	public String getTitle(Locale locale, MBMessage mbMessage) {
		return mbMessage.getSubject();
	}

	@Override
	public boolean isHideable(MBMessage mbMessage) {
		return mbMessage.isDiscussion();
	}

	@Override
	public String renderPreview(DisplayContext<MBMessage> displayContext) {
		MBMessage mbMessage = displayContext.getModel();

		if (mbMessage.isFormatBBCode()) {
			HttpServletRequest httpServletRequest =
				displayContext.getHttpServletRequest();

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			return MBUtil.getBBCodeHTML(
				mbMessage.getBody(false), themeDisplay.getPathThemeImages());
		}

		return mbMessage.getBody(false);
	}

	@Override
	public boolean showPreviewDiff() {
		return true;
	}

	@Override
	protected void buildDisplay(DisplayBuilder<MBMessage> displayBuilder)
		throws PortalException {

		MBMessage mbMessage = displayBuilder.getModel();

		displayBuilder.display(
			"subject", mbMessage.getSubject()
		).display(
			"created-by",
			() -> {
				String userName = mbMessage.getUserName();

				if (Validator.isNotNull(userName)) {
					return userName;
				}

				return null;
			}
		).display(
			"create-date", mbMessage.getCreateDate()
		).display(
			"last-modified", mbMessage.getModifiedDate()
		).display(
			"answer[noun]", mbMessage.isAnswer()
		).display(
			"number-of-attachments", mbMessage.getAttachmentsFileEntriesCount()
		).display(
			"attachments", _getAttachments(displayBuilder, mbMessage), false
		);
	}

	private String _getAttachments(
			DisplayBuilder<MBMessage> displayBuilder, MBMessage mbMessage)
		throws PortalException {

		if (mbMessage.getAttachmentsFileEntriesCount() == 0) {
			return null;
		}

		DisplayContext<MBMessage> displayContext =
			displayBuilder.getDisplayContext();

		HttpServletRequest httpServletRequest =
			displayContext.getHttpServletRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		StringBundler sb = new StringBundler(
			(3 * mbMessage.getAttachmentsFileEntriesCount()) + 2);

		sb.append("<ul>");

		for (FileEntry fileEntry : mbMessage.getAttachmentsFileEntries()) {
			sb.append("<li>");

			LinkTag linkTag = new LinkTag();

			linkTag.setDisplayType("primary");
			linkTag.setHref(
				_portletFileRepository.getDownloadPortletFileEntryURL(
					themeDisplay, fileEntry, StringPool.BLANK));
			linkTag.setIcon(fileEntry.getIconCssClass());
			linkTag.setLabel(
				StringBundler.concat(
					fileEntry.getTitle(), " (",
					_language.formatStorageSize(
						fileEntry.getSize(), httpServletRequest.getLocale()),
					")"));
			linkTag.setSmall(true);

			try {
				sb.append(
					linkTag.doTagAsString(
						httpServletRequest,
						displayContext.getHttpServletResponse()));
			}
			catch (JspException jspException) {
				ReflectionUtil.throwException(jspException);
			}

			sb.append("</li>");
		}

		sb.append("</ul>");

		return sb.toString();
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private PortletFileRepository _portletFileRepository;

}