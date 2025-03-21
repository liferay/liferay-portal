/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.segments;

import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalServiceUtil;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.CopyLayoutThreadLocal;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.segments.constants.SegmentsExperimentConstants;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.service.SegmentsExperienceServiceUtil;
import com.liferay.segments.service.SegmentsExperimentLocalServiceUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Eduardo García
 * @author David Arques
 */
public class SegmentsExperienceUtil {

	public static void copySegmentsExperienceData(
			CommentManager commentManager, long groupId, Layout layout,
			PortletRegistry portletRegistry,
			SegmentsExperience sourceSegmentsExperience,
			SegmentsExperience targetSegmentsExperience,
			Function<String, ServiceContext> serviceContextFunction,
			long userId)
		throws PortalException {

		boolean copyLayout = CopyLayoutThreadLocal.isCopyLayout();

		try {
			CopyLayoutThreadLocal.setCopyLayout(true);

			_copyLayoutData(
				commentManager, groupId, layout, portletRegistry,
				sourceSegmentsExperience, targetSegmentsExperience,
				serviceContextFunction, userId);
		}
		catch (Throwable throwable) {
			throw new PortalException(throwable);
		}
		finally {
			CopyLayoutThreadLocal.setCopyLayout(copyLayout);
		}
	}

	public static Map<String, Object> getAvailableSegmentsExperiences(
			HttpServletRequest httpServletRequest)
		throws Exception {

		Map<String, Object> availableSegmentsExperiences = new HashMap<>();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout draftLayout = themeDisplay.getLayout();
		List<SegmentsExperience> segmentsExperiences =
			SegmentsExperienceServiceUtil.getSegmentsExperiences(
				themeDisplay.getScopeGroupId(), themeDisplay.getPlid(), true);

		for (SegmentsExperience segmentsExperience : segmentsExperiences) {
			availableSegmentsExperiences.put(
				String.valueOf(segmentsExperience.getSegmentsExperienceId()),
				HashMapBuilder.<String, Object>put(
					"hasLockedSegmentsExperiment",
					segmentsExperience.hasSegmentsExperiment()
				).put(
					"name", segmentsExperience.getName(themeDisplay.getLocale())
				).put(
					"priority", segmentsExperience.getPriority()
				).put(
					"segmentsEntryId",
					String.valueOf(segmentsExperience.getSegmentsEntryId())
				).put(
					"segmentsExperienceId",
					String.valueOf(segmentsExperience.getSegmentsExperienceId())
				).put(
					"segmentsExperimentStatus",
					getSegmentsExperimentStatus(
						themeDisplay,
						segmentsExperience.getSegmentsExperienceKey())
				).put(
					"segmentsExperimentURL",
					_getSegmentsExperimentURL(
						themeDisplay,
						PortalUtil.getLayoutFullURL(
							LayoutLocalServiceUtil.getLayout(
								draftLayout.getClassPK()),
							themeDisplay),
						segmentsExperience.getSegmentsExperienceId())
				).build());
		}

		return availableSegmentsExperiences;
	}

	public static JSONObject getSegmentsExperienceJSONObject(
		SegmentsExperience segmentsExperience) {

		return JSONUtil.put(
			"active", segmentsExperience.isActive()
		).put(
			"name", segmentsExperience.getNameCurrentValue()
		).put(
			"priority", segmentsExperience.getPriority()
		).put(
			"segmentsEntryId", segmentsExperience.getSegmentsEntryId()
		).put(
			"segmentsExperienceId", segmentsExperience.getSegmentsExperienceId()
		);
	}

	public static Map<String, Object> getSegmentsExperimentStatus(
		ThemeDisplay themeDisplay, String segmentsExperienceKey) {

		SegmentsExperiment segmentsExperiment =
			SegmentsExperimentLocalServiceUtil.fetchSegmentsExperiment(
				themeDisplay.getScopeGroupId(), segmentsExperienceKey,
				themeDisplay.getPlid());

		if (segmentsExperiment == null) {
			return null;
		}

		SegmentsExperimentConstants.Status status =
			SegmentsExperimentConstants.Status.valueOf(
				segmentsExperiment.getStatus());

		return HashMapBuilder.<String, Object>put(
			"label",
			LanguageUtil.get(themeDisplay.getLocale(), status.getLabel())
		).put(
			"value", status.getValue()
		).build();
	}

	private static void _copyLayoutData(
			CommentManager commentManager, long groupId, Layout layout,
			PortletRegistry portletRegistry,
			SegmentsExperience sourceSegmentsExperience,
			SegmentsExperience targetSegmentsExperience,
			Function<String, ServiceContext> serviceContextFunction,
			long userId)
		throws PortalException {

		LayoutStructure layoutStructure =
			LayoutStructureUtil.getLayoutStructure(
				groupId, layout.getPlid(),
				sourceSegmentsExperience.getSegmentsExperienceId());

		JSONObject dataJSONObject = _updateLayoutDataJSONObject(
			commentManager, groupId, layout, layoutStructure, portletRegistry,
			sourceSegmentsExperience, serviceContextFunction,
			targetSegmentsExperience, userId);

		LayoutPageTemplateStructureLocalServiceUtil.
			updateLayoutPageTemplateStructureData(
				groupId, layout.getPlid(),
				targetSegmentsExperience.getSegmentsExperienceId(),
				dataJSONObject.toString());
	}

	private static void _copyPortletPreferences(
		FragmentEntryLink fragmentEntryLink,
		FragmentEntryLink newFragmentEntryLink, long plid,
		PortletRegistry portletRegistry) {

		for (String portletId :
				portletRegistry.getFragmentEntryLinkPortletIds(
					fragmentEntryLink)) {

			_getNewPortletPreferences(
				fragmentEntryLink.getNamespace(),
				newFragmentEntryLink.getNamespace(), plid, portletId);
		}
	}

	private static String _getNewEditableValues(
		JSONObject editableValuesJSONObject, String namespace,
		String newNamespace, long plid) {

		String instanceId = editableValuesJSONObject.getString("instanceId");
		String portletId = editableValuesJSONObject.getString("portletId");

		if (Validator.isNull(instanceId) || Validator.isNull(portletId)) {
			return editableValuesJSONObject.toString();
		}

		PortletPreferences portletPreferences = _getNewPortletPreferences(
			namespace, newNamespace, plid,
			PortletIdCodec.encode(portletId, instanceId));

		if (portletPreferences == null) {
			return editableValuesJSONObject.toString();
		}

		JSONObject newEditableValuesJSONObject = editableValuesJSONObject.put(
			"instanceId",
			PortletIdCodec.decodeInstanceId(portletPreferences.getPortletId()));

		return newEditableValuesJSONObject.toString();
	}

	private static String _getNewPortletId(
		String namespace, String newNamespace, String portletId) {

		if (!portletId.contains(namespace)) {
			return PortletIdCodec.encode(
				PortletIdCodec.decodePortletName(portletId), newNamespace);
		}

		return StringUtil.replace(portletId, namespace, newNamespace);
	}

	private static PortletPreferences _getNewPortletPreferences(
		String namespace, String newNamespace, long plid, String portletId) {

		PortletPreferences portletPreferences =
			PortletPreferencesLocalServiceUtil.fetchPortletPreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, plid, portletId);

		if (portletPreferences == null) {
			return null;
		}

		Portlet portlet = PortletLocalServiceUtil.getPortletById(portletId);

		if ((portlet == null) || portlet.isUndeployedPortlet()) {
			return null;
		}

		String newPortletId = _getNewPortletId(
			namespace, newNamespace, portletId);

		PortletPreferences existingPortletPreferences =
			PortletPreferencesLocalServiceUtil.fetchPortletPreferences(
				portletPreferences.getOwnerId(),
				portletPreferences.getOwnerType(), plid, newPortletId);

		jakarta.portlet.PortletPreferences jxPortletPreferences =
			PortletPreferenceValueLocalServiceUtil.getPreferences(
				portletPreferences);

		if (existingPortletPreferences == null) {
			return PortletPreferencesLocalServiceUtil.addPortletPreferences(
				portletPreferences.getCompanyId(),
				portletPreferences.getOwnerId(),
				portletPreferences.getOwnerType(), plid, newPortletId, portlet,
				PortletPreferencesFactoryUtil.toXML(jxPortletPreferences));
		}

		return PortletPreferencesLocalServiceUtil.updatePreferences(
			existingPortletPreferences.getOwnerId(),
			existingPortletPreferences.getOwnerType(),
			existingPortletPreferences.getPlid(),
			existingPortletPreferences.getPortletId(), jxPortletPreferences);
	}

	private static String _getSegmentsExperimentURL(
		ThemeDisplay themeDisplay, String layoutFullURL,
		long segmentsExperienceId) {

		HttpComponentsUtil.addParameter(
			layoutFullURL, "p_l_back_url", themeDisplay.getURLCurrent());

		return HttpComponentsUtil.addParameter(
			layoutFullURL, "segmentsExperienceId", segmentsExperienceId);
	}

	private static JSONObject _updateLayoutDataJSONObject(
			CommentManager commentManager, long groupId, Layout layout,
			LayoutStructure layoutStructure, PortletRegistry portletRegistry,
			SegmentsExperience sourceSegmentsExperience,
			Function<String, ServiceContext> serviceContextFunction,
			SegmentsExperience targetSegmentsExperience, long userId)
		throws PortalException {

		List<FragmentEntryLink> fragmentEntryLinks =
			FragmentEntryLinkLocalServiceUtil.
				getFragmentEntryLinksBySegmentsExperienceId(
					groupId, sourceSegmentsExperience.getSegmentsExperienceId(),
					layout.getPlid());

		for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
			if (fragmentEntryLink.isDeleted()) {
				continue;
			}

			FragmentStyledLayoutStructureItem
				fragmentStyledLayoutStructureItem =
					(FragmentStyledLayoutStructureItem)
						layoutStructure.
							getLayoutStructureItemByFragmentEntryLinkId(
								fragmentEntryLink.getFragmentEntryLinkId());

			if (fragmentStyledLayoutStructureItem == null) {
				continue;
			}

			FragmentEntryLink newFragmentEntryLink =
				(FragmentEntryLink)fragmentEntryLink.clone();

			newFragmentEntryLink.setUuid(PortalUUIDUtil.generate());
			newFragmentEntryLink.setExternalReferenceCode(null);
			newFragmentEntryLink.setFragmentEntryLinkId(
				CounterLocalServiceUtil.increment());
			newFragmentEntryLink.setCreateDate(new Date());
			newFragmentEntryLink.setModifiedDate(new Date());
			newFragmentEntryLink.setOriginalFragmentEntryLinkId(0);
			newFragmentEntryLink.setSegmentsExperienceId(
				targetSegmentsExperience.getSegmentsExperienceId());

			String newNamespace = StringUtil.randomId();

			JSONObject editableValuesJSONObject =
				JSONFactoryUtil.createJSONObject(
					fragmentEntryLink.getEditableValues());

			SegmentsExperiment segmentsExperiment =
				SegmentsExperimentLocalServiceUtil.fetchSegmentsExperiment(
					groupId,
					sourceSegmentsExperience.getSegmentsExperienceKey(),
					layout.getPlid());

			if (Validator.isNull(
					editableValuesJSONObject.getString("instanceId")) &&
				Validator.isNull(
					editableValuesJSONObject.getString("portletId")) &&
				(segmentsExperiment != null)) {

				newNamespace = fragmentEntryLink.getNamespace();
			}

			newFragmentEntryLink.setEditableValues(
				_getNewEditableValues(
					editableValuesJSONObject, fragmentEntryLink.getNamespace(),
					newNamespace, layout.getPlid()));

			newFragmentEntryLink.setNamespace(newNamespace);
			newFragmentEntryLink.setLastPropagationDate(new Date());

			newFragmentEntryLink =
				FragmentEntryLinkLocalServiceUtil.addFragmentEntryLink(
					newFragmentEntryLink);

			fragmentStyledLayoutStructureItem.setFragmentEntryLinkId(
				newFragmentEntryLink.getFragmentEntryLinkId());

			commentManager.copyDiscussion(
				userId, groupId, FragmentEntryLink.class.getName(),
				fragmentEntryLink.getFragmentEntryLinkId(),
				newFragmentEntryLink.getFragmentEntryLinkId(),
				serviceContextFunction);

			_copyPortletPreferences(
				fragmentEntryLink, newFragmentEntryLink, layout.getPlid(),
				portletRegistry);
		}

		return layoutStructure.toJSONObject();
	}

}