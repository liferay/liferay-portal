/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.portlet.action;

import com.liferay.asset.kernel.exception.AssetCategoryException;
import com.liferay.asset.kernel.exception.AssetTagException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portlet.LiferayPortletUtil;
import com.liferay.portlet.PortletURLImplWrapper;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.service.TrashEntryLocalService;
import com.liferay.trash.service.TrashEntryService;
import com.liferay.wiki.configuration.WikiGroupServiceConfiguration;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.exception.DuplicatePageException;
import com.liferay.wiki.exception.NoSuchNodeException;
import com.liferay.wiki.exception.NoSuchPageException;
import com.liferay.wiki.exception.PageContentException;
import com.liferay.wiki.exception.PageTitleException;
import com.liferay.wiki.exception.PageVersionException;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.model.WikiPageResource;
import com.liferay.wiki.service.WikiPageLocalService;
import com.liferay.wiki.service.WikiPageResourceLocalService;
import com.liferay.wiki.service.WikiPageService;
import com.liferay.wiki.web.internal.util.WikiWebComponentProvider;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.filter.ActionResponseWrapper;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 * @author Roberto Díaz
 */
@Component(
	property = {
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI,
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_ADMIN,
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI_DISPLAY,
		"mvc.command.name=/wiki/edit_page"
	},
	service = MVCActionCommand.class
)
public class EditPageMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		WikiPage page = null;

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				page = _updatePage(actionRequest);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deletePage(actionRequest, false);
			}
			else if (cmd.equals(Constants.MOVE_TO_TRASH)) {
				_deletePage(actionRequest, true);
			}
			else if (cmd.equals(Constants.RESTORE)) {
				_restorePage(actionRequest);
			}
			else if (cmd.equals(Constants.REVERT)) {
				_revertPage(actionRequest);
			}
			else if (cmd.equals(Constants.SUBSCRIBE)) {
				_subscribePage(actionRequest);
			}
			else if (cmd.equals(Constants.UNSUBSCRIBE)) {
				_unsubscribePage(actionRequest);
			}

			if (Validator.isNotNull(cmd)) {
				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				if (page != null) {
					int workflowAction = ParamUtil.getInteger(
						actionRequest, "workflowAction",
						WorkflowConstants.ACTION_PUBLISH);

					if (workflowAction == WorkflowConstants.ACTION_SAVE_DRAFT) {
						redirect = _getSaveAndContinueRedirect(
							actionRequest, actionResponse, page, redirect);
					}
					else if (redirect.endsWith("title=")) {
						redirect += page.getTitle();
					}
				}

				sendRedirect(actionRequest, actionResponse, redirect);
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchNodeException ||
				exception instanceof NoSuchPageException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());
			}
			else if (exception instanceof DuplicatePageException ||
					 exception instanceof PageContentException ||
					 exception instanceof PageTitleException ||
					 exception instanceof PageVersionException ||
					 exception instanceof SanitizerException) {

				SessionErrors.add(actionRequest, exception.getClass());
			}
			else if (exception instanceof AssetCategoryException ||
					 exception instanceof AssetTagException) {

				SessionErrors.add(
					actionRequest, exception.getClass(), exception);
			}
			else {
				Throwable throwable = exception.getCause();

				if (throwable instanceof SanitizerException) {
					SessionErrors.add(actionRequest, SanitizerException.class);
				}
				else {
					throw exception;
				}
			}
		}
	}

	private void _deletePage(ActionRequest actionRequest, boolean moveToTrash)
		throws Exception {

		long nodeId = ParamUtil.getLong(actionRequest, "nodeId");
		String title = ParamUtil.getString(actionRequest, "title");
		double version = ParamUtil.getDouble(actionRequest, "version");

		String[] deletePageTitles = null;

		if (Validator.isNotNull(title)) {
			deletePageTitles = new String[] {title};
		}
		else {
			deletePageTitles = ParamUtil.getStringValues(
				actionRequest, "rowIdsWikiPage");
		}

		List<TrashedModel> trashedModels = new ArrayList<>();

		for (String deletePageTitle : deletePageTitles) {
			if (moveToTrash) {
				WikiPage trashedWikiPage = null;

				if (version > 0) {
					trashedWikiPage = _wikiPageService.movePageToTrash(
						nodeId, deletePageTitle, version);
				}
				else {
					trashedWikiPage = _wikiPageService.movePageToTrash(
						nodeId, deletePageTitle);
				}

				trashedModels.add(trashedWikiPage);
			}
			else {
				if (version > 0) {
					_wikiPageService.discardDraft(
						nodeId, deletePageTitle, version);
				}
				else {
					_wikiPageService.deletePage(nodeId, deletePageTitle);
				}
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

	private String _getSaveAndContinueRedirect(
			ActionRequest actionRequest, ActionResponse actionResponse,
			WikiPage page, String redirect)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		while (actionResponse instanceof ActionResponseWrapper) {
			ActionResponseWrapper actionResponseWrapper =
				(ActionResponseWrapper)actionResponse;

			actionResponse = actionResponseWrapper.getResponse();
		}

		LiferayPortletURL liferayPortletURL = new PortletURLImplWrapper(
			LiferayPortletUtil.getLiferayPortletResponse(actionResponse),
			themeDisplay.getPlid(), PortletRequest.RENDER_PHASE);

		liferayPortletURL.setParameter(
			"mvcRenderCommandName", "/wiki/edit_page");
		liferayPortletURL.setParameter(Constants.CMD, Constants.UPDATE, false);
		liferayPortletURL.setParameter("redirect", redirect, false);
		liferayPortletURL.setParameter(
			"groupId", String.valueOf(layout.getGroupId()), false);
		liferayPortletURL.setParameter(
			"nodeId", String.valueOf(page.getNodeId()), false);
		liferayPortletURL.setParameter("title", page.getTitle(), false);
		liferayPortletURL.setWindowState(actionRequest.getWindowState());

		return liferayPortletURL.toString();
	}

	private void _restorePage(ActionRequest actionRequest) throws Exception {
		long[] restoreEntryIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "restoreTrashEntryIds"), 0L);

		for (long restoreEntryId : restoreEntryIds) {
			long overridePageResourcePrimKey = 0;

			TrashEntry trashEntry = _trashEntryLocalService.getTrashEntry(
				restoreEntryId);

			WikiPageResource pageResource =
				_wikiPageResourceLocalService.getPageResource(
					trashEntry.getClassPK());

			String title = _trashHelper.getOriginalTitle(
				pageResource.getTitle());

			WikiWebComponentProvider wikiWebComponentProvider =
				WikiWebComponentProvider.getWikiWebComponentProvider();

			WikiGroupServiceConfiguration wikiGroupServiceConfiguration =
				wikiWebComponentProvider.getWikiGroupServiceConfiguration();

			if (title.equals(wikiGroupServiceConfiguration.frontPageName())) {
				WikiPage overridePage = _wikiPageLocalService.fetchPage(
					pageResource.getNodeId(),
					wikiGroupServiceConfiguration.frontPageName());

				if (overridePage != null) {
					overridePageResourcePrimKey =
						overridePage.getResourcePrimKey();
				}
			}

			_trashEntryService.restoreEntry(
				restoreEntryId, overridePageResourcePrimKey, null);
		}
	}

	private void _revertPage(ActionRequest actionRequest) throws Exception {
		long nodeId = ParamUtil.getLong(actionRequest, "nodeId");
		String title = ParamUtil.getString(actionRequest, "title");
		double version = ParamUtil.getDouble(actionRequest, "version");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			WikiPage.class.getName(), actionRequest);

		_wikiPageService.revertPage(nodeId, title, version, serviceContext);
	}

	private void _subscribePage(ActionRequest actionRequest) throws Exception {
		long nodeId = ParamUtil.getLong(actionRequest, "nodeId");
		String title = ParamUtil.getString(actionRequest, "title");

		_wikiPageService.subscribePage(nodeId, title);
	}

	private void _unsubscribePage(ActionRequest actionRequest)
		throws Exception {

		long nodeId = ParamUtil.getLong(actionRequest, "nodeId");
		String title = ParamUtil.getString(actionRequest, "title");

		_wikiPageService.unsubscribePage(nodeId, title);
	}

	private WikiPage _updatePage(ActionRequest actionRequest) throws Exception {
		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		long nodeId = ParamUtil.getLong(actionRequest, "nodeId");
		String title = ParamUtil.getString(actionRequest, "title");

		String content = ParamUtil.getString(actionRequest, "content");
		String summary = ParamUtil.getString(actionRequest, "summary");
		boolean minorEdit = ParamUtil.getBoolean(actionRequest, "minorEdit");
		String format = ParamUtil.getString(actionRequest, "format");
		String parentTitle = ParamUtil.getString(actionRequest, "parentTitle");
		String redirectTitle = null;

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			WikiPage.class.getName(), actionRequest);

		WikiPage page = null;

		addAttachments(actionRequest);

		if (cmd.equals(Constants.UPDATE)) {
			double version = ParamUtil.getDouble(actionRequest, "version");

			page = _wikiPageService.updatePage(
				nodeId, title, version, content, summary, minorEdit, format,
				parentTitle, redirectTitle, serviceContext);
		}
		else {
			page = _wikiPageService.addPage(
				nodeId, title, content, summary, minorEdit, format, parentTitle,
				redirectTitle, serviceContext);

			boolean copyPageAttachments = ParamUtil.getBoolean(
				actionRequest, "copyPageAttachments");

			if (copyPageAttachments) {
				long templateNodeId = ParamUtil.getLong(
					actionRequest, "templateNodeId");
				String templateTitle = ParamUtil.getString(
					actionRequest, "templateTitle");

				_wikiPageService.copyPageAttachments(
					templateNodeId, templateTitle, page.getNodeId(),
					page.getTitle());
			}
		}

		return page;
	}

	@Reference
	private TrashEntryLocalService _trashEntryLocalService;

	@Reference
	private TrashEntryService _trashEntryService;

	@Reference
	private TrashHelper _trashHelper;

	@Reference
	private WikiPageLocalService _wikiPageLocalService;

	@Reference
	private WikiPageResourceLocalService _wikiPageResourceLocalService;

	@Reference
	private WikiPageService _wikiPageService;

}