/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesToFieldsConverter;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.journal.util.JournalConverter;
import com.liferay.journal.util.JournalHelper;
import com.liferay.journal.web.internal.util.JournalArticleUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=/journal/save_as_draft_article"
	},
	service = MVCActionCommand.class
)
public class SaveAsDraftArticleMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			JournalArticle article = _processAction(actionRequest);

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put(
					"articleId", article.getArticleId()
				).put(
					"classPK", article.getResourcePrimKey()
				).put(
					"friendlyUrlMap", article.getFriendlyURLMap()
				).put(
					"previewURL",
					() -> {
						AssetRendererFactory<JournalArticle>
							assetRendererFactory =
								AssetRendererFactoryRegistryUtil.
									getAssetRendererFactoryByClass(
										JournalArticle.class);

						AssetRenderer<JournalArticle> assetRenderer =
							assetRendererFactory.getAssetRenderer(
								article, AssetRendererFactory.TYPE_LATEST);

						String viewContentURL = StringPool.BLANK;

						try {
							viewContentURL = assetRenderer.getURLViewInContext(
								_portal.getLiferayPortletRequest(actionRequest),
								_portal.getLiferayPortletResponse(
									actionResponse),
								null);
						}
						catch (Exception exception) {
							if (_log.isDebugEnabled()) {
								_log.debug(exception);
							}
						}

						return HttpComponentsUtil.addParameter(
							viewContentURL, "p_l_mode", Constants.PREVIEW);
					}
				).put(
					"version", article.getVersion()
				));
		}
		catch (Exception exception) {
			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put("error", exception.getMessage()));
		}
	}

	private JournalArticle _processAction(ActionRequest actionRequest)
		throws Exception {

		String actionName = StringPool.BLANK;

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		if (cmd.equals(Constants.ADD)) {
			actionName = "/journal/add_article";
		}
		else if (cmd.equals(Constants.UPDATE)) {
			actionName = "/journal/update_article";
		}

		return JournalArticleUtil.addOrUpdateArticle(
			actionName, _ddmFormValuesFactory, _ddmFormValuesToFieldsConverter,
			_ddmStructureLocalService, _journalArticleService,
			_journalConverter, _journalHelper, _localization, _portal,
			actionRequest);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SaveAsDraftArticleMVCActionCommand.class);

	@Reference
	private DDMFormValuesFactory _ddmFormValuesFactory;

	@Reference
	private DDMFormValuesToFieldsConverter _ddmFormValuesToFieldsConverter;

	@Reference
	private DDMStructureLocalService _ddmStructureLocalService;

	@Reference
	private JournalArticleService _journalArticleService;

	@Reference
	private JournalConverter _journalConverter;

	@Reference
	private JournalHelper _journalHelper;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

}