/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.model.listener;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.fragment.listener.FragmentEntryLinkListener;
import com.liferay.fragment.listener.FragmentEntryLinkListenerRegistry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.content.page.editor.web.internal.exception.NoninstanceablePortletException;
import com.liferay.layout.manager.ContentManager;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.layout.util.CheckNoninstanceablePortletThreadLocal;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.display.template.PortletDisplayTemplate;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(service = ModelListener.class)
public class FragmentEntryLinkModelListener
	extends BaseModelListener<FragmentEntryLink> {

	@Override
	public void onAfterCreate(FragmentEntryLink fragmentEntryLink)
		throws ModelListenerException {

		_contentManager.updateLayoutClassedModelUsage(fragmentEntryLink);

		_updateDDMTemplateLink(fragmentEntryLink);
	}

	@Override
	public void onAfterRemove(FragmentEntryLink fragmentEntryLink)
		throws ModelListenerException {

		_layoutClassedModelUsageLocalService.deleteLayoutClassedModelUsages(
			String.valueOf(fragmentEntryLink.getFragmentEntryLinkId()),
			_portal.getClassNameId(FragmentEntryLink.class.getName()),
			fragmentEntryLink.getPlid());

		try {
			_deleteDDMTemplateLinks(fragmentEntryLink);

			_commentManager.deleteDiscussion(
				FragmentEntryLink.class.getName(),
				fragmentEntryLink.getFragmentEntryLinkId());

			for (FragmentEntryLinkListener fragmentEntryLinkListener :
					_fragmentEntryLinkListenerRegistry.
						getFragmentEntryLinkListeners()) {

				fragmentEntryLinkListener.onDeleteFragmentEntryLink(
					fragmentEntryLink);
			}
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}
	}

	@Override
	public void onAfterUpdate(
			FragmentEntryLink originalFragmentEntryLink,
			FragmentEntryLink fragmentEntryLink)
		throws ModelListenerException {

		_contentManager.updateLayoutClassedModelUsage(fragmentEntryLink);

		_updateDDMTemplateLink(fragmentEntryLink);
	}

	@Override
	public void onBeforeCreate(FragmentEntryLink fragmentEntryLink)
		throws ModelListenerException {

		if (!CheckNoninstanceablePortletThreadLocal.
				isCheckNoninstanceablePortlet()) {

			return;
		}

		_checkNoninstanceablePortletUsed(fragmentEntryLink);
	}

	@Override
	public void onBeforeUpdate(
			FragmentEntryLink originalFragmentEntryLink,
			FragmentEntryLink fragmentEntryLink)
		throws ModelListenerException {

		if (!CheckNoninstanceablePortletThreadLocal.
				isCheckNoninstanceablePortlet() ||
			Objects.equals(
				originalFragmentEntryLink.getHtml(),
				fragmentEntryLink.getHtml())) {

			return;
		}

		_checkNoninstanceablePortletUsed(fragmentEntryLink);
	}

	private void _checkNoninstanceablePortletUsed(
			FragmentEntryLink fragmentEntryLink)
		throws ModelListenerException {

		List<String> portletNames = TransformUtil.transform(
			_portletRegistry.getFragmentEntryLinkPortletIds(
				null, fragmentEntryLink),
			portletId -> {
				Portlet portlet = _portletLocalService.getPortletById(
					fragmentEntryLink.getCompanyId(), portletId);

				if (portlet.isInstanceable()) {
					return null;
				}

				return PortletIdCodec.decodePortletName(portletId);
			});

		if (ListUtil.isEmpty(portletNames)) {
			return;
		}

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.
				getFragmentEntryLinksBySegmentsExperienceId(
					fragmentEntryLink.getGroupId(),
					fragmentEntryLink.getSegmentsExperienceId(),
					fragmentEntryLink.getPlid(), false);

		for (FragmentEntryLink curFragmentEntryLink : fragmentEntryLinks) {
			if (curFragmentEntryLink.getFragmentEntryLinkId() ==
					fragmentEntryLink.getFragmentEntryLinkId()) {

				continue;
			}

			for (String portletId :
					_portletRegistry.getFragmentEntryLinkPortletIds(
						null, curFragmentEntryLink)) {

				String portletName = PortletIdCodec.decodePortletName(
					portletId);

				if (portletNames.contains(portletName)) {
					throw new ModelListenerException(
						new NoninstanceablePortletException(portletName));
				}
			}
		}
	}

	private void _deleteDDMTemplateLinks(FragmentEntryLink fragmentEntryLink)
		throws PortalException {

		_ddmTemplateLinkLocalService.deleteTemplateLink(
			_portal.getClassNameId(FragmentEntryLink.class.getName()),
			fragmentEntryLink.getFragmentEntryLinkId());

		JSONObject jsonObject = fragmentEntryLink.getEditableValuesJSONObject();

		Iterator<String> keysIterator = jsonObject.keys();

		while (keysIterator.hasNext()) {
			String key = keysIterator.next();

			JSONObject editableProcessorJSONObject = jsonObject.getJSONObject(
				key);

			if (editableProcessorJSONObject == null) {
				continue;
			}

			Iterator<String> editableKeysIterator =
				editableProcessorJSONObject.keys();

			while (editableKeysIterator.hasNext()) {
				String editableKey = editableKeysIterator.next();

				String compositeClassName =
					ResourceActionsUtil.getCompositeModelName(
						FragmentEntryLink.class.getName(), editableKey);

				ClassName className = _classNameLocalService.fetchClassName(
					compositeClassName);

				if (className == null) {
					continue;
				}

				_ddmTemplateLinkLocalService.deleteTemplateLink(
					className.getClassNameId(),
					fragmentEntryLink.getFragmentEntryLinkId());
			}
		}
	}

	private void _updateDDMTemplateLink(FragmentEntryLink fragmentEntryLink) {
		_ddmTemplateLinkLocalService.deleteTemplateLink(
			_portal.getClassNameId(FragmentEntryLink.class.getName()),
			fragmentEntryLink.getFragmentEntryLinkId());

		JSONObject jsonObject = fragmentEntryLink.getEditableValuesJSONObject();

		Iterator<String> keysIterator = jsonObject.keys();

		while (keysIterator.hasNext()) {
			String key = keysIterator.next();

			JSONObject editableProcessorJSONObject = jsonObject.getJSONObject(
				key);

			if (editableProcessorJSONObject == null) {
				continue;
			}

			Iterator<String> editableKeysIterator =
				editableProcessorJSONObject.keys();

			while (editableKeysIterator.hasNext()) {
				String editableKey = editableKeysIterator.next();

				JSONObject editableJSONObject =
					editableProcessorJSONObject.getJSONObject(editableKey);

				if (editableJSONObject == null) {
					continue;
				}

				String fieldId = editableJSONObject.getString("fieldId");

				String mappedField = editableJSONObject.getString(
					"mappedField", fieldId);

				if (Validator.isNull(mappedField)) {
					continue;
				}

				_updateDDMTemplateLink(
					fragmentEntryLink, editableKey, mappedField);
			}
		}
	}

	private void _updateDDMTemplateLink(
		FragmentEntryLink fragmentEntryLink, String editableKey,
		String mappedField) {

		if (!mappedField.startsWith(
				PortletDisplayTemplate.DISPLAY_STYLE_PREFIX)) {

			return;
		}

		String ddmTemplateKey = mappedField.substring(
			PortletDisplayTemplate.DISPLAY_STYLE_PREFIX.length());

		DDMTemplate ddmTemplate = _ddmTemplateLocalService.fetchTemplate(
			fragmentEntryLink.getGroupId(),
			_portal.getClassNameId(DDMStructure.class.getName()),
			ddmTemplateKey);

		if (ddmTemplate == null) {
			return;
		}

		String compositeClassName = ResourceActionsUtil.getCompositeModelName(
			FragmentEntryLink.class.getName(), editableKey);

		_ddmTemplateLinkLocalService.updateTemplateLink(
			_portal.getClassNameId(compositeClassName),
			fragmentEntryLink.getFragmentEntryLinkId(),
			ddmTemplate.getTemplateId());
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CommentManager _commentManager;

	@Reference
	private ContentManager _contentManager;

	@Reference
	private DDMTemplateLinkLocalService _ddmTemplateLinkLocalService;

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private FragmentEntryLinkListenerRegistry
		_fragmentEntryLinkListenerRegistry;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private PortletRegistry _portletRegistry;

}