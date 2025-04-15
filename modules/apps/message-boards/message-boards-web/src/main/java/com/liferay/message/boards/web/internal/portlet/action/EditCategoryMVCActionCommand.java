/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.portlet.action;

import com.liferay.captcha.configuration.CaptchaConfiguration;
import com.liferay.captcha.util.CaptchaUtil;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.exception.CategoryNameException;
import com.liferay.message.boards.exception.MailingListEmailAddressException;
import com.liferay.message.boards.exception.MailingListInServerNameException;
import com.liferay.message.boards.exception.MailingListInUserNameException;
import com.liferay.message.boards.exception.MailingListOutEmailAddressException;
import com.liferay.message.boards.exception.MailingListOutServerNameException;
import com.liferay.message.boards.exception.MailingListOutUserNameException;
import com.liferay.message.boards.exception.NoSuchCategoryException;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.service.MBCategoryService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.captcha.CaptchaConfigurationException;
import com.liferay.portal.kernel.captcha.CaptchaException;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.trash.service.TrashEntryService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Daniel Sanz
 */
@Component(
	property = {
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS,
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS_ADMIN,
		"mvc.command.name=/message_boards/edit_category"
	},
	service = MVCActionCommand.class
)
public class EditCategoryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				_updateCategory(actionRequest);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteCategories(actionRequest, false);
			}
			else if (cmd.equals(Constants.MOVE_TO_TRASH)) {
				_deleteCategories(actionRequest, true);
			}
			else if (cmd.equals(Constants.RESTORE)) {
				_restoreTrashEntries(actionRequest);
			}
			else if (cmd.equals(Constants.SUBSCRIBE)) {
				_subscribeCategory(actionRequest);
			}
			else if (cmd.equals(Constants.UNSUBSCRIBE)) {
				_unsubscribeCategory(actionRequest);
			}
		}
		catch (NoSuchCategoryException | PrincipalException exception) {
			SessionErrors.add(actionRequest, exception.getClass());

			actionResponse.setRenderParameter(
				"mvcPath", "/message_boards/error.jsp");
		}
		catch (CaptchaException | CategoryNameException |
			   MailingListEmailAddressException |
			   MailingListInServerNameException |
			   MailingListInUserNameException |
			   MailingListOutEmailAddressException |
			   MailingListOutServerNameException |
			   MailingListOutUserNameException exception) {

			SessionErrors.add(actionRequest, exception.getClass());
		}
	}

	protected CaptchaConfiguration getCaptchaConfiguration(
			ActionRequest actionRequest)
		throws CaptchaConfigurationException {

		try {
			return _configurationProvider.getCompanyConfiguration(
				CaptchaConfiguration.class,
				_portal.getCompanyId(actionRequest));
		}
		catch (Exception exception) {
			throw new CaptchaConfigurationException(exception);
		}
	}

	private void _deleteCategories(
			ActionRequest actionRequest, boolean moveToTrash)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long[] deleteCategoryIds = null;

		long categoryId = ParamUtil.getLong(actionRequest, "mbCategoryId");

		if (categoryId > 0) {
			deleteCategoryIds = new long[] {categoryId};
		}
		else {
			deleteCategoryIds = StringUtil.split(
				ParamUtil.getString(actionRequest, "deleteCategoryIds"), 0L);
		}

		List<TrashedModel> trashedModels = new ArrayList<>();

		for (long deleteCategoryId : deleteCategoryIds) {
			if (moveToTrash) {
				MBCategory category = _mbCategoryService.moveCategoryToTrash(
					deleteCategoryId);

				trashedModels.add(category);
			}
			else {
				_mbCategoryService.deleteCategory(
					themeDisplay.getScopeGroupId(), deleteCategoryId);
			}
		}

		if (moveToTrash && !trashedModels.isEmpty()) {
			addDeleteSuccessData(
				actionRequest,
				HashMapBuilder.<String, Object>put(
					"trashedModels", trashedModels
				).build());
		}
	}

	private void _restoreTrashEntries(ActionRequest actionRequest)
		throws Exception {

		long[] restoreTrashEntryIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "restoreTrashEntryIds"), 0L);

		for (long restoreTrashEntryId : restoreTrashEntryIds) {
			_trashEntryService.restoreEntry(restoreTrashEntryId);
		}
	}

	private void _subscribeCategory(ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long categoryId = ParamUtil.getLong(actionRequest, "mbCategoryId");

		_mbCategoryService.subscribeCategory(
			themeDisplay.getScopeGroupId(), categoryId);
	}

	private void _unsubscribeCategory(ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long categoryId = ParamUtil.getLong(actionRequest, "mbCategoryId");

		_mbCategoryService.unsubscribeCategory(
			themeDisplay.getScopeGroupId(), categoryId);
	}

	private void _updateCategory(ActionRequest actionRequest) throws Exception {
		long categoryId = ParamUtil.getLong(actionRequest, "mbCategoryId");

		long parentCategoryId = ParamUtil.getLong(
			actionRequest, "parentCategoryId");
		String name = ParamUtil.getString(actionRequest, "name");
		String description = ParamUtil.getString(actionRequest, "description");
		String displayStyle = ParamUtil.getString(
			actionRequest, "displayStyle");

		String emailAddress = ParamUtil.getString(
			actionRequest, "emailAddress");
		String inProtocol = ParamUtil.getString(actionRequest, "inProtocol");
		String inServerName = ParamUtil.getString(
			actionRequest, "inServerName");
		int inServerPort = ParamUtil.getInteger(actionRequest, "inServerPort");
		boolean inUseSSL = ParamUtil.getBoolean(actionRequest, "inUseSSL");
		String inUserName = ParamUtil.getString(actionRequest, "inUserName");
		String inPassword = ParamUtil.getString(actionRequest, "inPassword");
		int inReadInterval = ParamUtil.getInteger(
			actionRequest, "inReadInterval");
		String outEmailAddress = ParamUtil.getString(
			actionRequest, "outEmailAddress");
		boolean outCustom = ParamUtil.getBoolean(actionRequest, "outCustom");
		String outServerName = ParamUtil.getString(
			actionRequest, "outServerName");
		int outServerPort = ParamUtil.getInteger(
			actionRequest, "outServerPort");
		boolean outUseSSL = ParamUtil.getBoolean(actionRequest, "outUseSSL");
		String outUserName = ParamUtil.getString(actionRequest, "outUserName");
		String outPassword = ParamUtil.getString(actionRequest, "outPassword");
		boolean allowAnonymous = ParamUtil.getBoolean(
			actionRequest, "allowAnonymous");
		boolean mailingListActive = ParamUtil.getBoolean(
			actionRequest, "mailingListActive");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			MBCategory.class.getName(), actionRequest);

		if (categoryId <= 0) {
			CaptchaConfiguration captchaConfiguration = getCaptchaConfiguration(
				actionRequest);

			if (captchaConfiguration.messageBoardsEditMessageCaptchaEnabled()) {
				CaptchaUtil.check(actionRequest);
			}

			// Add category

			_mbCategoryService.addCategory(
				null, parentCategoryId, name, description, displayStyle,
				emailAddress, inProtocol, inServerName, inServerPort, inUseSSL,
				inUserName, inPassword, inReadInterval, outEmailAddress,
				outCustom, outServerName, outServerPort, outUseSSL, outUserName,
				outPassword, allowAnonymous, mailingListActive, serviceContext);
		}
		else {

			// Update category

			boolean mergeWithParentCategory = ParamUtil.getBoolean(
				actionRequest, "mergeWithParentCategory");

			_mbCategoryService.updateCategory(
				categoryId, parentCategoryId, name, description, displayStyle,
				emailAddress, inProtocol, inServerName, inServerPort, inUseSSL,
				inUserName, inPassword, inReadInterval, outEmailAddress,
				outCustom, outServerName, outServerPort, outUseSSL, outUserName,
				outPassword, allowAnonymous, mailingListActive,
				mergeWithParentCategory, serviceContext);
		}
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private MBCategoryService _mbCategoryService;

	@Reference
	private Portal _portal;

	@Reference
	private TrashEntryService _trashEntryService;

}