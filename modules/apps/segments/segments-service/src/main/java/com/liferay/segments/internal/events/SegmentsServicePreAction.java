/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.events;

import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.SegmentsEntryRetriever;
import com.liferay.segments.configuration.provider.SegmentsConfigurationProvider;
import com.liferay.segments.constants.SegmentsWebKeys;
import com.liferay.segments.context.RequestContextMapper;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.processor.SegmentsExperienceRequestProcessorRegistry;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	configurationPid = "com.liferay.segments.configuration.SegmentsConfiguration",
	property = "key=servlet.service.events.pre", service = LifecycleAction.class
)
public class SegmentsServicePreAction extends Action {

	@Override
	public void run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws ActionException {

		try {
			if (!_segmentsConfigurationProvider.isSegmentationEnabled(
					_portal.getCompanyId(httpServletRequest))) {

				return;
			}

			_run(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new ActionException(exception);
		}
	}

	private long[] _getSegmentsExperienceIds(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, long groupId, long userId,
		long plid) {

		try {
			long[] segmentsExperienceIds =
				_segmentsExperienceRequestProcessorRegistry.
					getSegmentsExperienceIds(
						httpServletRequest, httpServletResponse, groupId, plid);

			Set<Long> segmentsExperienceIdsSegmentsEntryIds = new HashSet<>();

			for (long segmentsExperienceId : segmentsExperienceIds) {
				SegmentsExperience segmentsExperience =
					_segmentsExperienceLocalService.fetchSegmentsExperience(
						segmentsExperienceId);

				if (segmentsExperience != null) {
					segmentsExperienceIdsSegmentsEntryIds.add(
						segmentsExperience.getSegmentsEntryId());
				}
				else if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to get segments experience " +
							segmentsExperienceId);
				}
			}

			long[] cachedSegmentsEntryIds =
				(long[])httpServletRequest.getAttribute(
					SegmentsWebKeys.SEGMENTS_ENTRY_IDS);

			long[] segmentsEntryIds = null;

			if (cachedSegmentsEntryIds != null) {
				segmentsEntryIds = cachedSegmentsEntryIds;
			}
			else {
				segmentsEntryIds = _segmentsEntryRetriever.getSegmentsEntryIds(
					groupId, userId,
					_requestContextMapper.map(httpServletRequest),
					ArrayUtil.toArray(
						segmentsExperienceIdsSegmentsEntryIds.toArray(
							new Long[0])));
			}

			httpServletRequest.setAttribute(
				SegmentsWebKeys.SEGMENTS_ENTRY_IDS, segmentsEntryIds);

			return _segmentsExperienceRequestProcessorRegistry.
				getSegmentsExperienceIds(
					httpServletRequest, httpServletResponse, groupId, plid,
					segmentsEntryIds);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}

		return new long[] {
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				plid)
		};
	}

	private void _run(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		String portletNamespace = _portal.getPortletNamespace(
			ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET);

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				ParamUtil.getLong(
					httpServletRequest,
					portletNamespace + "segmentsExperienceId"));

		if (permissionChecker.isGroupAdmin(themeDisplay.getScopeGroupId()) &&
			Objects.equals(
				ParamUtil.getString(
					httpServletRequest, "p_l_mode", Constants.VIEW),
				Constants.EDIT) &&
			(segmentsExperience != null)) {

			httpServletRequest.setAttribute(
				SegmentsWebKeys.SEGMENTS_EXPERIENCE_IDS,
				new long[] {segmentsExperience.getSegmentsExperienceId()});

			return;
		}

		if (!themeDisplay.isLifecycleRender()) {
			return;
		}

		Layout layout = themeDisplay.getLayout();

		if ((layout == null) || layout.isTypeControlPanel() ||
			(!layout.isTypeAssetDisplay() && !layout.isTypeContent())) {

			return;
		}

		httpServletRequest.setAttribute(
			SegmentsWebKeys.SEGMENTS_EXPERIENCE_IDS,
			_getSegmentsExperienceIds(
				httpServletRequest, httpServletResponse, layout.getGroupId(),
				themeDisplay.getUserId(), layout.getPlid()));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SegmentsServicePreAction.class);

	@Reference
	private Portal _portal;

	@Reference
	private RequestContextMapper _requestContextMapper;

	@Reference
	private SegmentsConfigurationProvider _segmentsConfigurationProvider;

	@Reference
	private volatile SegmentsEntryRetriever _segmentsEntryRetriever;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private SegmentsExperienceRequestProcessorRegistry
		_segmentsExperienceRequestProcessorRegistry;

}