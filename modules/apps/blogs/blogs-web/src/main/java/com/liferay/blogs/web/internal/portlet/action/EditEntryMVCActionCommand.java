/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.portlet.action;

import com.liferay.asset.display.page.portlet.AssetDisplayPageEntryFormProcessor;
import com.liferay.asset.kernel.exception.AssetCategoryException;
import com.liferay.asset.kernel.exception.AssetTagException;
import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.blogs.exception.EntryContentException;
import com.liferay.blogs.exception.EntryCoverImageCropException;
import com.liferay.blogs.exception.EntryDescriptionException;
import com.liferay.blogs.exception.EntryDisplayDateException;
import com.liferay.blogs.exception.EntrySmallImageNameException;
import com.liferay.blogs.exception.EntrySmallImageScaleException;
import com.liferay.blogs.exception.EntryTitleException;
import com.liferay.blogs.exception.EntryUrlTitleException;
import com.liferay.blogs.exception.NoSuchEntryException;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.blogs.service.BlogsEntryService;
import com.liferay.blogs.web.internal.helper.BlogsEntryImageSelectorHelper;
import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.friendly.url.exception.DuplicateFriendlyURLEntryException;
import com.liferay.friendly.url.exception.FriendlyURLCategoryException;
import com.liferay.friendly.url.exception.FriendlyURLLocalizationUrlTitleException;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTTransactionException;
import com.liferay.portal.kernel.editor.constants.EditorConstants;
import com.liferay.portal.kernel.exception.ImageResolutionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.taglib.ui.ImageSelector;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.upload.LiferayFileItemException;
import com.liferay.portal.kernel.upload.UploadException;
import com.liferay.portal.kernel.upload.UploadRequestSizeException;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.service.TrashEntryService;
import com.liferay.upload.AttachmentContentUpdater;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Wilson S. Man
 * @author Thiago Moreira
 * @author Juan Fernández
 * @author Zsolt Berentey
 * @author Levente Hudák
 */
@Component(
	property = {
		"jakarta.portlet.name=" + BlogsPortletKeys.BLOGS,
		"jakarta.portlet.name=" + BlogsPortletKeys.BLOGS_ADMIN,
		"jakarta.portlet.name=" + BlogsPortletKeys.BLOGS_AGGREGATOR,
		"mvc.command.name=/blogs/edit_entry"
	},
	service = MVCActionCommand.class
)
public class EditEntryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void addSuccessMessage(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		String portletResource = ParamUtil.getString(
			actionRequest, "portletResource");
		int workflowAction = ParamUtil.getInteger(
			actionRequest, "workflowAction",
			WorkflowConstants.ACTION_SAVE_DRAFT);

		if (Validator.isNotNull(portletResource) &&
			(workflowAction != WorkflowConstants.ACTION_SAVE_DRAFT)) {

			MultiSessionMessages.add(
				actionRequest, portletResource + "requestProcessed");
		}
		else {
			super.addSuccessMessage(actionRequest, actionResponse);
		}
	}

	@Override
	protected void doProcessAction(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			BlogsEntry entry = null;

			UploadException uploadException =
				(UploadException)actionRequest.getAttribute(
					WebKeys.UPLOAD_EXCEPTION);

			if (uploadException != null) {
				Throwable throwable = uploadException.getCause();

				if (uploadException.isExceededFileSizeLimit()) {
					throw new FileSizeException(throwable);
				}

				if (uploadException.isExceededLiferayFileItemSizeLimit()) {
					throw new LiferayFileItemException(throwable);
				}

				if (uploadException.isExceededUploadRequestSizeLimit()) {
					throw new UploadRequestSizeException(throwable);
				}

				throw new PortalException(throwable);
			}
			else if (cmd.equals(Constants.ADD) ||
					 cmd.equals(Constants.UPDATE)) {

				Callable<BlogsEntry> updateEntryCallable =
					new UpdateEntryCallable(actionRequest);

				entry = TransactionInvokerUtil.invoke(
					_transactionConfig, updateEntryCallable);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteEntries(actionRequest, false);
			}
			else if (cmd.equals(Constants.MOVE_TO_TRASH)) {
				_deleteEntries(actionRequest, true);
			}
			else if (cmd.equals(Constants.RESTORE)) {
				_restoreTrashEntries(actionRequest);
			}
			else if (cmd.equals(Constants.SUBSCRIBE)) {
				_subscribe(actionRequest);
			}
			else if (cmd.equals(Constants.UNSUBSCRIBE)) {
				_unsubscribe(actionRequest);
			}

			boolean ajax = ParamUtil.getBoolean(actionRequest, "ajax");

			if (ajax) {
				JSONPortletResponseUtil.writeJSON(
					actionRequest, actionResponse,
					JSONUtil.put(
						"attributeDataImageId",
						EditorConstants.ATTRIBUTE_DATA_IMAGE_ID
					).put(
						"content", entry.getContent()
					).put(
						"coverImageFileEntryId",
						entry.getCoverImageFileEntryId()
					).put(
						"entryId", entry.getEntryId()
					).put(
						"urlTitle", entry.getUrlTitle()
					));

				return;
			}

			int workflowAction = ParamUtil.getInteger(
				actionRequest, "workflowAction",
				WorkflowConstants.ACTION_SAVE_DRAFT);

			String redirect = ParamUtil.getString(actionRequest, "redirect");

			if ((entry != null) &&
				(workflowAction == WorkflowConstants.ACTION_SAVE_DRAFT)) {

				_sendDraftRedirect(actionRequest, actionResponse, entry);
			}
			else if ((Validator.isNotNull(redirect) &&
					  cmd.equals(Constants.UPDATE)) ||
					 ((entry == null) &&
					  (workflowAction ==
						  WorkflowConstants.ACTION_SAVE_DRAFT))) {

				_sendUpdateRedirect(actionRequest, actionResponse);
			}
			else if (Validator.isNotNull(redirect) &&
					 cmd.equals(Constants.ADD) && (entry != null)) {

				_sendAddRedirect(
					actionRequest, actionResponse, entry.getEntryId());
			}
		}
		catch (AssetCategoryException | AssetTagException exception) {
			SessionErrors.add(actionRequest, exception.getClass(), exception);

			actionResponse.setRenderParameter(
				"mvcRenderCommandName", "/blogs/edit_entry");

			hideDefaultSuccessMessage(actionRequest);
		}
		catch (CTTransactionException ctTransactionException) {
			throw ctTransactionException;
		}
		catch (DuplicateFriendlyURLEntryException | EntryContentException |
			   EntryCoverImageCropException | EntryDescriptionException |
			   EntryDisplayDateException | EntrySmallImageNameException |
			   EntrySmallImageScaleException | EntryTitleException |
			   EntryUrlTitleException | FileSizeException |
			   FriendlyURLCategoryException |
			   FriendlyURLLocalizationUrlTitleException |
			   ImageResolutionException | LiferayFileItemException |
			   SanitizerException | UploadRequestSizeException exception) {

			SessionErrors.add(actionRequest, exception.getClass());

			actionResponse.setRenderParameter(
				"mvcRenderCommandName", "/blogs/edit_entry");

			hideDefaultSuccessMessage(actionRequest);
		}
		catch (NoSuchEntryException | PrincipalException exception) {
			SessionErrors.add(actionRequest, exception.getClass());

			actionResponse.setRenderParameter("mvcPath", "/blogs/error.jsp");

			hideDefaultSuccessMessage(actionRequest);
		}
		catch (Throwable throwable) {
			_log.error(throwable, throwable);

			SessionErrors.add(actionRequest, throwable.getClass());

			actionResponse.setRenderParameter("mvcPath", "/blogs/error.jsp");

			hideDefaultSuccessMessage(actionRequest);
		}
	}

	private void _deleteEntries(
			ActionRequest actionRequest, boolean moveToTrash)
		throws Exception {

		List<TrashedModel> trashedModels = new ArrayList<>();

		BulkSelection<BlogsEntry> blogsEntryBulkSelection =
			_blogsEntryBulkSelectionFactory.create(
				_getParameterMap(actionRequest));

		blogsEntryBulkSelection.forEach(
			blogsEntry -> _deleteEntry(blogsEntry, moveToTrash, trashedModels));

		if (moveToTrash && !trashedModels.isEmpty()) {
			addDeleteSuccessData(
				actionRequest,
				HashMapBuilder.<String, Object>put(
					"trashedModels", trashedModels
				).build());
		}
	}

	private void _deleteEntry(
		BlogsEntry entry, boolean moveToTrash,
		List<TrashedModel> trashedModels) {

		try {
			if (moveToTrash) {
				trashedModels.add(
					_blogsEntryService.moveEntryToTrash(entry.getEntryId()));
			}
			else {
				_blogsEntryService.deleteEntry(entry.getEntryId());
			}
		}
		catch (PortalException portalException) {
			ReflectionUtil.throwException(portalException);
		}
	}

	private Map<String, String[]> _getParameterMap(ActionRequest actionRequest)
		throws Exception {

		return HashMapBuilder.create(
			actionRequest.getParameterMap()
		).put(
			"groupId",
			new String[] {
				String.valueOf(_portal.getScopeGroupId(actionRequest))
			}
		).build();
	}

	private String _getSaveAndContinueRedirect(
		ActionRequest actionRequest, ActionResponse actionResponse,
		BlogsEntry entry, String redirect, String portletResource) {

		PortletConfig portletConfig = (PortletConfig)actionRequest.getAttribute(
			JavaConstants.JAVAX_PORTLET_CONFIG);

		return PortletURLBuilder.createRenderURL(
			_portal.getLiferayPortletResponse(actionResponse),
			portletConfig.getPortletName()
		).setMVCRenderCommandName(
			"/blogs/edit_entry"
		).setCMD(
			Constants.UPDATE
		).setRedirect(
			redirect
		).setPortletResource(
			portletResource
		).setParameter(
			"groupId", String.valueOf(entry.getGroupId()), false
		).setParameter(
			"entryId", String.valueOf(entry.getEntryId()), false
		).setWindowState(
			actionRequest.getWindowState()
		).buildString();
	}

	private void _restoreTrashEntries(ActionRequest actionRequest)
		throws Exception {

		long[] restoreTrashEntryIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "restoreTrashEntryIds"), 0L);

		for (long restoreTrashEntryId : restoreTrashEntryIds) {
			_trashEntryService.restoreEntry(restoreTrashEntryId);
		}
	}

	private void _sendAddRedirect(
			ActionRequest actionRequest, ActionResponse actionResponse,
			long entryId)
		throws Exception {

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		String portletResource = HttpComponentsUtil.getParameter(
			redirect, "portletResource", false);

		if (Validator.isNotNull(portletResource)) {
			String namespace = _portal.getPortletNamespace(portletResource);

			redirect = HttpComponentsUtil.addParameter(
				redirect, namespace + "className", BlogsEntry.class.getName());
			redirect = HttpComponentsUtil.addParameter(
				redirect, namespace + "classPK", entryId);
		}

		sendRedirect(
			actionRequest, actionResponse, _portal.escapeRedirect(redirect));
	}

	private void _sendDraftRedirect(
			ActionRequest actionRequest, ActionResponse actionResponse,
			BlogsEntry entry)
		throws Exception {

		sendRedirect(
			actionRequest, actionResponse,
			_getSaveAndContinueRedirect(
				actionRequest, actionResponse, entry,
				ParamUtil.getString(actionRequest, "redirect"),
				ParamUtil.getString(actionRequest, "portletResource")));
	}

	private void _sendUpdateRedirect(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		sendRedirect(
			actionRequest, actionResponse,
			_portal.escapeRedirect(
				HttpComponentsUtil.setParameter(
					ParamUtil.getString(actionRequest, "redirect"),
					actionResponse.getNamespace() + "redirectToLastFriendlyURL",
					false)));
	}

	private void _subscribe(ActionRequest actionRequest) throws Exception {
		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_blogsEntryService.subscribe(themeDisplay.getScopeGroupId());
	}

	private void _unsubscribe(ActionRequest actionRequest) throws Exception {
		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_blogsEntryService.unsubscribe(themeDisplay.getScopeGroupId());
	}

	private String _updateContent(
			BlogsEntry entry, String content, ThemeDisplay themeDisplay)
		throws Exception {

		return _attachmentContentUpdater.updateContent(
			content, ContentTypes.TEXT_HTML,
			tempFileEntry -> _blogsEntryLocalService.addAttachmentFileEntry(
				null, themeDisplay.getUserId(), entry.getGroupId(),
				tempFileEntry.getTitle(), tempFileEntry.getMimeType(),
				tempFileEntry.getContentStream()));
	}

	private BlogsEntry _updateEntry(ActionRequest actionRequest)
		throws Exception {

		String description = StringPool.BLANK;

		boolean customAbstract = ParamUtil.getBoolean(
			actionRequest, "customAbstract");

		if (customAbstract) {
			description = ParamUtil.getString(actionRequest, "description");

			if (Validator.isNull(description)) {
				throw new EntryDescriptionException();
			}
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String content = ParamUtil.getString(actionRequest, "content");
		long entryId = ParamUtil.getLong(actionRequest, "entryId");
		String subtitle = ParamUtil.getString(actionRequest, "subtitle");
		String title = ParamUtil.getString(actionRequest, "title");
		String urlTitle = ParamUtil.getString(actionRequest, "urlTitle");

		int displayDateMonth = ParamUtil.getInteger(
			actionRequest, "displayDateMonth");
		int displayDateDay = ParamUtil.getInteger(
			actionRequest, "displayDateDay");
		int displayDateYear = ParamUtil.getInteger(
			actionRequest, "displayDateYear");
		int displayDateHour = ParamUtil.getInteger(
			actionRequest, "displayDateHour");
		int displayDateMinute = ParamUtil.getInteger(
			actionRequest, "displayDateMinute");
		int displayDateAmPm = ParamUtil.getInteger(
			actionRequest, "displayDateAmPm");

		if (displayDateAmPm == Calendar.PM) {
			displayDateHour += 12;
		}

		boolean allowPingbacks = ParamUtil.getBoolean(
			actionRequest, "allowPingbacks");
		boolean allowTrackbacks = ParamUtil.getBoolean(
			actionRequest, "allowTrackbacks");
		String[] trackbacks = StringUtil.split(
			ParamUtil.getString(actionRequest, "trackbacks"));

		long coverImageFileEntryId = ParamUtil.getLong(
			actionRequest, "coverImageFileEntryId");
		String coverImageURL = ParamUtil.getString(
			actionRequest, "coverImageURL");
		String coverImageFileEntryCropRegion = ParamUtil.getString(
			actionRequest, "coverImageFileEntryCropRegion");

		String coverImageCaption = ParamUtil.getString(
			actionRequest, "coverImageCaption");

		long oldCoverImageFileEntryId = 0;
		String oldCoverImageURL = StringPool.BLANK;
		long oldSmallImageFileEntryId = 0;
		long oldSmallImageId = 0;
		String oldSmallImageURL = StringPool.BLANK;

		if (entryId != 0) {
			BlogsEntry entry = _blogsEntryLocalService.getEntry(entryId);

			oldCoverImageFileEntryId = entry.getCoverImageFileEntryId();
			oldCoverImageURL = entry.getCoverImageURL();
			oldSmallImageFileEntryId = entry.getSmallImageFileEntryId();
			oldSmallImageId = entry.getSmallImageId();
			oldSmallImageURL = entry.getSmallImageURL();
		}

		BlogsEntryImageSelectorHelper blogsEntryCoverImageSelectorHelper =
			new BlogsEntryImageSelectorHelper(
				0, coverImageFileEntryId, oldCoverImageFileEntryId,
				coverImageFileEntryCropRegion, coverImageURL, oldCoverImageURL);

		ImageSelector coverImageImageSelector =
			blogsEntryCoverImageSelectorHelper.getImageSelector();

		long smallImageFileEntryId = ParamUtil.getLong(
			actionRequest, "smallImageFileEntryId");
		String smallImageURL = ParamUtil.getString(
			actionRequest, "smallImageURL");

		BlogsEntryImageSelectorHelper blogsEntrySmallImageSelectorHelper =
			new BlogsEntryImageSelectorHelper(
				oldSmallImageId, smallImageFileEntryId,
				oldSmallImageFileEntryId, StringPool.BLANK, smallImageURL,
				oldSmallImageURL);

		ImageSelector smallImageImageSelector =
			blogsEntrySmallImageSelectorHelper.getImageSelector();

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			BlogsEntry.class.getName(), actionRequest);

		serviceContext.setAttribute(
			"friendlyURLAssetCategoryIds",
			ParamUtil.getLongValues(
				actionRequest, "friendlyURLAssetCategoryIds"));
		serviceContext.setAttribute(
			"updateAutoTags",
			ParamUtil.getBoolean(actionRequest, "updateAutoTags"));

		BlogsEntry entry = null;

		if (entryId <= 0) {

			// Add entry

			entry = _blogsEntryService.addEntry(
				null, title, subtitle, urlTitle, description, content,
				displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, allowPingbacks,
				allowTrackbacks, trackbacks, coverImageCaption,
				coverImageImageSelector, smallImageImageSelector,
				serviceContext);

			String updatedContent = _updateContent(
				entry, content, themeDisplay);

			if (!content.equals(updatedContent)) {
				entry.setContent(updatedContent);

				entry = _blogsEntryLocalService.updateBlogsEntry(entry);
			}
		}
		else {

			// Update entry

			boolean sendEmailEntryUpdated = ParamUtil.getBoolean(
				actionRequest, "sendEmailEntryUpdated");

			serviceContext.setAttribute(
				"sendEmailEntryUpdated", sendEmailEntryUpdated);

			String emailEntryUpdatedComment = ParamUtil.getString(
				actionRequest, "emailEntryUpdatedComment");

			serviceContext.setAttribute(
				"emailEntryUpdatedComment", emailEntryUpdatedComment);

			entry = _blogsEntryLocalService.getEntry(entryId);

			content = _updateContent(entry, content, themeDisplay);

			entry = _blogsEntryService.updateEntry(
				entryId, title, subtitle, urlTitle, description, content,
				displayDateMonth, displayDateDay, displayDateYear,
				displayDateHour, displayDateMinute, allowPingbacks,
				allowTrackbacks, trackbacks, coverImageCaption,
				coverImageImageSelector, smallImageImageSelector,
				serviceContext);
		}

		if (blogsEntryCoverImageSelectorHelper.isFileEntryTempFile()) {
			_blogsEntryLocalService.addOriginalImageFileEntry(
				themeDisplay.getUserId(), entry.getGroupId(),
				entry.getEntryId(), coverImageImageSelector);

			_portletFileRepository.deletePortletFileEntry(
				coverImageFileEntryId);
		}

		if (blogsEntrySmallImageSelectorHelper.isFileEntryTempFile()) {
			_blogsEntryLocalService.addOriginalImageFileEntry(
				themeDisplay.getUserId(), entry.getGroupId(),
				entry.getEntryId(), smallImageImageSelector);

			_portletFileRepository.deletePortletFileEntry(
				smallImageFileEntryId);
		}

		_assetDisplayPageEntryFormProcessor.process(
			BlogsEntry.class.getName(), entry.getEntryId(), actionRequest);

		return entry;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditEntryMVCActionCommand.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private AssetDisplayPageEntryFormProcessor
		_assetDisplayPageEntryFormProcessor;

	@Reference
	private AttachmentContentUpdater _attachmentContentUpdater;

	@Reference(target = "(model.class.name=com.liferay.blogs.model.BlogsEntry)")
	private BulkSelectionFactory<BlogsEntry> _blogsEntryBulkSelectionFactory;

	@Reference
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Reference
	private BlogsEntryService _blogsEntryService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletFileRepository _portletFileRepository;

	@Reference
	private TrashEntryService _trashEntryService;

	private class UpdateEntryCallable implements Callable<BlogsEntry> {

		@Override
		public BlogsEntry call() throws Exception {
			return _updateEntry(_actionRequest);
		}

		private UpdateEntryCallable(ActionRequest actionRequest) {
			_actionRequest = actionRequest;
		}

		private final ActionRequest _actionRequest;

	}

}