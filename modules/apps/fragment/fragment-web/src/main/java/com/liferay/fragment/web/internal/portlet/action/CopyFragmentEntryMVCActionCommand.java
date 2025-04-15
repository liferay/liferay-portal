/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet.action;

import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentComposition;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentCompositionService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.fragment.service.FragmentEntryService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.net.URL;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FragmentPortletKeys.FRAGMENT,
		"mvc.command.name=/fragment/copy_fragment_entry"
	},
	service = MVCActionCommand.class
)
public class CopyFragmentEntryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		hideDefaultSuccessMessage(actionRequest);

		long[] fragmentEntryIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "fragmentEntryIds"), 0L);

		long fragmentCollectionId = ParamUtil.getLong(
			actionRequest, "fragmentCollectionId");

		if (_isShowSuccessMessage(fragmentEntryIds, fragmentCollectionId)) {
			MultiSessionMessages.add(actionRequest, "fragmentEntryCopied");
		}

		sendRedirect(
			actionRequest, actionResponse,
			PortletURLBuilder.createRenderURL(
				_portal.getLiferayPortletResponse(actionResponse)
			).setParameter(
				"fragmentCollectionId",
				() -> {
					ServiceContext serviceContext =
						ServiceContextFactory.getInstance(actionRequest);

					ThemeDisplay themeDisplay =
						(ThemeDisplay)actionRequest.getAttribute(
							WebKeys.THEME_DISPLAY);

					for (long fragmentEntryId : fragmentEntryIds) {
						_fragmentEntryService.copyFragmentEntry(
							themeDisplay.getScopeGroupId(), fragmentEntryId,
							fragmentCollectionId, serviceContext);
					}

					String[] contributedEntryKeys = StringUtil.split(
						ParamUtil.getString(
							actionRequest, "contributedEntryKeys"));

					for (String contributedEntryKey : contributedEntryKeys) {
						FragmentComposition fragmentComposition =
							_fragmentCollectionContributorRegistry.
								getFragmentComposition(contributedEntryKey);

						FragmentEntry fragmentEntry =
							_fragmentCollectionContributorRegistry.
								getFragmentEntry(contributedEntryKey);

						if (fragmentComposition != null) {
							_addFragmentComposition(
								fragmentCollectionId, fragmentComposition,
								serviceContext, themeDisplay);
						}
						else if (fragmentEntry != null) {
							_addFragmentEntry(
								fragmentCollectionId, fragmentEntry,
								serviceContext, themeDisplay);
						}
					}

					return fragmentCollectionId;
				}
			).buildString());
	}

	private void _addFragmentComposition(
			long fragmentCollectionId, FragmentComposition fragmentComposition,
			ServiceContext serviceContext, ThemeDisplay themeDisplay)
		throws PortalException {

		long previewFileEntryId = 0;

		String imagePreviewURL = fragmentComposition.getImagePreviewURL(
			themeDisplay);

		if (Validator.isNotNull(imagePreviewURL)) {
			previewFileEntryId = _getPreviewFileEntryId(
				themeDisplay.getUserId(), themeDisplay.getScopeGroupId(),
				fragmentCollectionId,
				themeDisplay.getPortalURL() + imagePreviewURL);
		}

		_fragmentCompositionService.addFragmentComposition(
			null, themeDisplay.getScopeGroupId(), fragmentCollectionId,
			StringPool.BLANK,
			StringBundler.concat(
				fragmentComposition.getName(), StringPool.SPACE,
				StringPool.OPEN_PARENTHESIS,
				_language.get(LocaleUtil.getSiteDefault(), "copy"),
				StringPool.CLOSE_PARENTHESIS),
			null, fragmentComposition.getData(), previewFileEntryId,
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	private void _addFragmentEntry(
			long fragmentCollectionId, FragmentEntry fragmentEntry,
			ServiceContext serviceContext, ThemeDisplay themeDisplay)
		throws PortalException {

		long previewFileEntryId = 0;

		String imagePreviewURL = fragmentEntry.getImagePreviewURL(themeDisplay);

		if (Validator.isNotNull(imagePreviewURL)) {
			previewFileEntryId = _getPreviewFileEntryId(
				themeDisplay.getUserId(), themeDisplay.getScopeGroupId(),
				fragmentCollectionId,
				themeDisplay.getPortalURL() + imagePreviewURL);
		}

		_fragmentEntryService.addFragmentEntry(
			null, themeDisplay.getScopeGroupId(), fragmentCollectionId,
			StringPool.BLANK,
			StringBundler.concat(
				fragmentEntry.getName(), StringPool.SPACE,
				StringPool.OPEN_PARENTHESIS,
				_language.get(LocaleUtil.getSiteDefault(), "copy"),
				StringPool.CLOSE_PARENTHESIS),
			fragmentEntry.getCss(), fragmentEntry.getHtml(),
			fragmentEntry.getJs(), fragmentEntry.isCacheable(),
			fragmentEntry.getConfiguration(), fragmentEntry.getIcon(),
			previewFileEntryId, false, fragmentEntry.isReadOnly(),
			fragmentEntry.getType(), fragmentEntry.getTypeOptions(),
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	private long _getPreviewFileEntryId(
		long userId, long groupId, long fragmentCollectionId,
		String imagePreviewURL) {

		try {
			byte[] bytes = URLUtil.toByteArray(new URL(imagePreviewURL));

			String shortFileName = FileUtil.getShortFileName(imagePreviewURL);

			Repository repository =
				PortletFileRepositoryUtil.fetchPortletRepository(
					groupId, FragmentPortletKeys.FRAGMENT);

			if (repository == null) {
				ServiceContext serviceContext = new ServiceContext();

				serviceContext.setAddGroupPermissions(true);
				serviceContext.setAddGuestPermissions(true);

				repository = PortletFileRepositoryUtil.addPortletRepository(
					groupId, FragmentPortletKeys.FRAGMENT, serviceContext);
			}

			FileEntry fileEntry = PortletFileRepositoryUtil.addPortletFileEntry(
				groupId, userId, FragmentCollection.class.getName(),
				fragmentCollectionId, FragmentPortletKeys.FRAGMENT,
				repository.getDlFolderId(), bytes, shortFileName,
				MimeTypesUtil.getContentType(shortFileName), false);

			return fileEntry.getFileEntryId();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return 0;
	}

	private boolean _isShowSuccessMessage(
		long[] fragmentEntryIds, long fragmentCollectionId) {

		if (ArrayUtil.isEmpty(fragmentEntryIds)) {
			return false;
		}

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.fetchFragmentEntry(fragmentEntryIds[0]);

		if ((fragmentEntry == null) ||
			(fragmentEntry.getFragmentCollectionId() != fragmentCollectionId)) {

			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CopyFragmentEntryMVCActionCommand.class);

	@Reference
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Reference
	private FragmentCompositionService _fragmentCompositionService;

	@Reference
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Reference
	private FragmentEntryService _fragmentEntryService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}