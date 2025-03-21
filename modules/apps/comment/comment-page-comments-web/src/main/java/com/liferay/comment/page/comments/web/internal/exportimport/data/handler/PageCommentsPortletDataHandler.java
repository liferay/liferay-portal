/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.comment.page.comments.web.internal.exportimport.data.handler;

import com.liferay.comment.page.comments.web.internal.constants.PageCommentsPortletKeys;
import com.liferay.exportimport.kernel.lar.BasePortletDataHandler;
import com.liferay.exportimport.kernel.lar.DataLevel;
import com.liferay.exportimport.kernel.lar.ExportImportProcessCallbackRegistry;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerBoolean;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerControl;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.comment.DiscussionStagingHandler;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.xml.Element;

import jakarta.portlet.PortletPreferences;

import java.util.List;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gergely Mathe
 */
@Component(
	property = "jakarta.portlet.name=" + PageCommentsPortletKeys.PAGE_COMMENTS,
	service = PortletDataHandler.class
)
public class PageCommentsPortletDataHandler extends BasePortletDataHandler {

	public static final String NAMESPACE = "comment";

	public static final String SCHEMA_VERSION = "4.0.0";

	@Override
	public StagedModelType[] getDeletionSystemEventStagedModelTypes() {
		DiscussionStagingHandler discussionStagingHandler =
			_commentManager.getDiscussionStagingHandler();

		if (discussionStagingHandler == null) {
			return new StagedModelType[0];
		}

		Class<? extends StagedModel> clazz =
			discussionStagingHandler.getStagedModelClass();

		StagedModelType stagedModelType = new StagedModelType(
			clazz.getName(), StagedModelType.REFERRER_CLASS_NAME_ANY);

		return new StagedModelType[] {stagedModelType};
	}

	@Override
	public PortletDataHandlerControl[] getExportControls() {
		DiscussionStagingHandler discussionStagingHandler =
			_commentManager.getDiscussionStagingHandler();

		if (discussionStagingHandler == null) {
			return new PortletDataHandlerControl[0];
		}

		PortletDataHandlerBoolean portletDataHandlerBoolean =
			new PortletDataHandlerBoolean(
				NAMESPACE, "comment", true, false, null,
				discussionStagingHandler.getClassName(),
				StagedModelType.REFERRER_CLASS_NAME_ANY);

		return new PortletDataHandlerControl[] {portletDataHandlerBoolean};
	}

	@Override
	public PortletDataHandlerControl[] getImportControls() {
		return getExportControls();
	}

	@Override
	public String getSchemaVersion() {
		return SCHEMA_VERSION;
	}

	@Activate
	protected void activate() {
		setDataAlwaysStaged(true);
		setDataLevel(DataLevel.PORTLET_INSTANCE);
		setPublishToLiveByDefault(true);
	}

	@Override
	protected PortletPreferences doDeleteData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		if (portletDataContext.addPrimaryKey(
				PageCommentsPortletDataHandler.class, "deleteData")) {

			return portletPreferences;
		}

		_commentManager.deleteGroupComments(
			portletDataContext.getScopeGroupId());

		return portletPreferences;
	}

	@Override
	protected String doExportData(
			PortletDataContext portletDataContext, String portletId,
			PortletPreferences portletPreferences)
		throws Exception {

		DiscussionStagingHandler discussionStagingHandler =
			_commentManager.getDiscussionStagingHandler();

		if (discussionStagingHandler == null) {
			return StringPool.BLANK;
		}

		Element rootElement = addExportDataRootElement(portletDataContext);

		rootElement.addAttribute(
			"group-id", String.valueOf(portletDataContext.getScopeGroupId()));

		if (!portletDataContext.getBooleanParameter(NAMESPACE, "comment")) {
			return getExportDataRootElementString(rootElement);
		}

		ActionableDynamicQuery actionableDynamicQuery =
			discussionStagingHandler.getCommentExportActionableDynamicQuery(
				portletDataContext);

		actionableDynamicQuery.performActions();

		return getExportDataRootElementString(rootElement);
	}

	@Override
	protected PortletPreferences doImportData(
		PortletDataContext portletDataContext, String portletId,
		PortletPreferences portletPreferences, String data) {

		_exportImportProcessCallbackRegistry.registerCallback(
			portletDataContext.getExportImportProcessId(),
			new ImportCommentsCallable(_commentManager, portletDataContext));

		return null;
	}

	@Override
	protected void doPrepareManifestSummary(
			PortletDataContext portletDataContext,
			PortletPreferences portletPreferences)
		throws Exception {

		DiscussionStagingHandler discussionStagingHandler =
			_commentManager.getDiscussionStagingHandler();

		if (discussionStagingHandler == null) {
			return;
		}

		ActionableDynamicQuery actionableDynamicQuery =
			discussionStagingHandler.getCommentExportActionableDynamicQuery(
				portletDataContext);

		actionableDynamicQuery.performCount();
	}

	@Reference
	private CommentManager _commentManager;

	@Reference
	private ExportImportProcessCallbackRegistry
		_exportImportProcessCallbackRegistry;

	private static class ImportCommentsCallable implements Callable<Void> {

		public ImportCommentsCallable(
			CommentManager commentManager,
			PortletDataContext portletDataContext) {

			_commentManager = commentManager;
			_portletDataContext = portletDataContext;
		}

		@Override
		public Void call() throws PortalException {
			DiscussionStagingHandler discussionStagingHandler =
				_commentManager.getDiscussionStagingHandler();

			if (discussionStagingHandler == null) {
				return null;
			}

			_portletDataContext.importPortletPermissions(
				discussionStagingHandler.getResourceName());

			if (!_portletDataContext.getBooleanParameter(
					NAMESPACE, "comment")) {

				return null;
			}

			Element messagesElement =
				_portletDataContext.getImportDataGroupElement(
					discussionStagingHandler.getStagedModelClass());

			List<Element> messageElements = messagesElement.elements();

			for (Element messageElement : messageElements) {
				StagedModelDataHandlerUtil.importStagedModel(
					_portletDataContext, messageElement);
			}

			return null;
		}

		private final CommentManager _commentManager;
		private final PortletDataContext _portletDataContext;

	}

}