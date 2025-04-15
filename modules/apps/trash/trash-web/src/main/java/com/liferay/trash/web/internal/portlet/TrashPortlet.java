/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.web.internal.portlet;

import com.liferay.portal.kernel.exception.TrashPermissionException;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.adapter.ModelAdapterUtil;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.constants.TrashEntryConstants;
import com.liferay.trash.constants.TrashPortletKeys;
import com.liferay.trash.exception.RestoreEntryException;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.service.TrashEntryLocalService;
import com.liferay.trash.service.TrashEntryService;
import com.liferay.trash.web.internal.constants.TrashWebKeys;
import com.liferay.trash.web.internal.util.TrashUndoUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides the Recycle Bin implementation of the <code>Portlet</code> interface
 * (in <code>jakarta.portlet</code>). If the Recycle Bin is enabled, this portlet
 * moves assets into the Recycle Bin instead of deleting them directly. The site
 * administrator is able to browse the list of removed asset entries, restore
 * selected entries, and empty the Recycle Bin.
 *
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"com.liferay.portlet.css-class-wrapper=portlet-trash",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.icon=/icons/trash.png",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.scopeable=true",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Trash",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + TrashPortletKeys.TRASH,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class TrashPortlet extends MVCPortlet {

	public void deleteEntries(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long trashEntryId = ParamUtil.getLong(actionRequest, "trashEntryId");

		if (trashEntryId > 0) {
			_trashEntryService.deleteEntry(trashEntryId);

			return;
		}

		long[] deleteEntryIds = ParamUtil.getLongValues(
			actionRequest, "rowIds");

		if (deleteEntryIds.length > 0) {
			for (long deleteEntryId : deleteEntryIds) {
				_trashEntryService.deleteEntry(deleteEntryId);
			}

			return;
		}

		String className = ParamUtil.getString(actionRequest, "className");
		long classPK = ParamUtil.getLong(actionRequest, "classPK");

		if (Validator.isNotNull(className) && (classPK > 0)) {
			_trashEntryService.deleteEntry(className, classPK);
		}

		sendRedirect(actionRequest, actionResponse);
	}

	public void emptyTrash(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long groupId = ParamUtil.getLong(
			actionRequest, "groupId", themeDisplay.getScopeGroupId());

		_trashEntryService.deleteEntries(groupId);
	}

	public void moveEntry(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long containerModelId = ParamUtil.getLong(
			actionRequest, "containerModelId");
		String className = ParamUtil.getString(actionRequest, "className");
		long classPK = ParamUtil.getLong(actionRequest, "classPK");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			className, actionRequest);

		_trashEntryService.moveEntry(
			className, classPK, containerModelId, serviceContext);

		TrashUndoUtil.addRestoreData(actionRequest, className, classPK);

		hideDefaultSuccessMessage(actionRequest);

		sendRedirect(actionRequest, actionResponse);
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(TrashWebKeys.TRASH_HELPER, _trashHelper);

		super.render(renderRequest, renderResponse);
	}

	public void restoreEntries(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		List<ObjectValuePair<String, Long>> entries = new ArrayList<>();

		long trashEntryId = ParamUtil.getLong(actionRequest, "trashEntryId");

		if (trashEntryId > 0) {
			_checkEntry(actionRequest, actionResponse);

			TrashEntry entry = _trashEntryService.restoreEntry(trashEntryId);

			entries.add(
				new ObjectValuePair<>(
					entry.getClassName(), entry.getClassPK()));
		}
		else {
			long[] restoreEntryIds = ParamUtil.getLongValues(
				actionRequest, "rowIds");

			for (long restoreEntryId : restoreEntryIds) {
				try {
					TrashEntry entry = _trashEntryService.restoreEntry(
						restoreEntryId);

					entries.add(
						new ObjectValuePair<>(
							entry.getClassName(), entry.getClassPK()));
				}
				catch (RestoreEntryException restoreEntryException) {
					if (restoreEntryException.getType() !=
							RestoreEntryException.NOT_RESTORABLE) {

						throw restoreEntryException;
					}
				}
			}
		}

		TrashUndoUtil.addRestoreData(actionRequest, entries);

		if (!entries.isEmpty()) {
			hideDefaultSuccessMessage(actionRequest);
		}

		sendRedirect(actionRequest, actionResponse);
	}

	public void restoreEntry(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		if (cmd.equals(Constants.RENAME)) {
			_checkEntry(actionRequest, actionResponse);

			restoreRename(actionRequest, actionResponse);
		}
		else if (cmd.equals(Constants.OVERRIDE)) {
			restoreOverride(actionRequest, actionResponse);
		}
	}

	public void restoreOverride(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long trashEntryId = ParamUtil.getLong(actionRequest, "trashEntryId");

		long duplicateEntryId = ParamUtil.getLong(
			actionRequest, "duplicateEntryId");

		TrashEntry entry = _trashEntryService.restoreEntry(
			trashEntryId, duplicateEntryId, null);

		TrashUndoUtil.addRestoreData(
			actionRequest, entry.getClassName(), entry.getClassPK());

		hideDefaultSuccessMessage(actionRequest);

		sendRedirect(actionRequest, actionResponse);
	}

	public void restoreRename(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long trashEntryId = ParamUtil.getLong(actionRequest, "trashEntryId");

		String newName = ParamUtil.getString(actionRequest, "newName");

		if (Validator.isNull(newName)) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			String oldName = ParamUtil.getString(actionRequest, "oldName");

			newName = _trashHelper.getNewName(themeDisplay, null, 0, oldName);
		}

		TrashEntry entry = _trashEntryService.restoreEntry(
			trashEntryId, 0, newName);

		TrashUndoUtil.addRestoreData(
			actionRequest, entry.getClassName(), entry.getClassPK());

		hideDefaultSuccessMessage(actionRequest);

		sendRedirect(actionRequest, actionResponse);
	}

	@Override
	protected boolean isSessionErrorException(Throwable throwable) {
		if (throwable instanceof RestoreEntryException ||
			throwable instanceof TrashPermissionException) {

			return true;
		}

		return false;
	}

	private void _checkEntry(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long trashEntryId = ParamUtil.getLong(actionRequest, "trashEntryId");

		String newName = ParamUtil.getString(actionRequest, "newName");

		TrashEntry entry = _trashEntryLocalService.fetchTrashEntry(
			trashEntryId);

		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			entry.getClassName());

		try {
			trashHandler.checkRestorableEntry(
				ModelAdapterUtil.adapt(
					com.liferay.trash.kernel.model.TrashEntry.class, entry),
				TrashEntryConstants.DEFAULT_CONTAINER_ID, newName);
		}
		catch (RestoreEntryException restoreEntryException) {
			actionRequest.setAttribute(
				WebKeys.REDIRECT,
				PortletURLBuilder.createRenderURL(
					_portal.getLiferayPortletResponse(actionResponse)
				).setMVCPath(
					"/restore_entry.jsp"
				).setRedirect(
					ParamUtil.getString(actionRequest, "redirect")
				).setParameter(
					"duplicateEntryId",
					restoreEntryException.getDuplicateEntryId()
				).setParameter(
					"oldName", restoreEntryException.getOldName()
				).setParameter(
					"overridable", restoreEntryException.isOverridable()
				).setParameter(
					"trashEntryId", restoreEntryException.getTrashEntryId()
				).buildString());

			hideDefaultErrorMessage(actionRequest);

			sendRedirect(actionRequest, actionResponse);

			throw new RestoreEntryException(
				restoreEntryException.getType(),
				restoreEntryException.getCause());
		}
	}

	@Reference
	private Portal _portal;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.trash.web)(&(release.schema.version>=1.0.0)(!(release.schema.version>=2.0.0))))"
	)
	private Release _release;

	@Reference
	private TrashEntryLocalService _trashEntryLocalService;

	@Reference
	private TrashEntryService _trashEntryService;

	@Reference
	private TrashHelper _trashHelper;

}