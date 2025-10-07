/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.model.listener;

import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.entry.processor.helper.FragmentEntryProcessorHelper;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRendererController;
import com.liferay.info.field.InfoField;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.layout.util.InfoFieldUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Javier Moral
 */
@Component(service = ModelListener.class)
public class FragmentEntryLinkModelListener
	extends BaseModelListener<FragmentEntryLink> {

	@Override
	public void onBeforeCreate(FragmentEntryLink fragmentEntryLink)
		throws ModelListenerException {

		_processInfoFields(fragmentEntryLink);
	}

	@Override
	public void onBeforeUpdate(
			FragmentEntryLink originalFragmentEntryLink,
			FragmentEntryLink fragmentEntryLink)
		throws ModelListenerException {

		if (Objects.equals(
				originalFragmentEntryLink.getEditableValues(),
				fragmentEntryLink.getEditableValues())) {

			return;
		}

		_processInfoFields(fragmentEntryLink);
	}

	private void _processInfoFields(FragmentEntryLink fragmentEntryLink) {
		List<ObjectValuePair<InfoField<?>, String>> infoFieldObjectValuePairs =
			new ArrayList<>();

		try {
			InfoFieldUtil.forEachInfoField(
				fragmentEntryLink, _fragmentRendererController,
				(infoField, type, unsafeSupplier) ->
					infoFieldObjectValuePairs.add(
						new ObjectValuePair<>(infoField, type)));

			if (infoFieldObjectValuePairs.isEmpty()) {
				return;
			}

			fragmentEntryLink.setEditableValues(
				_sanitizeEditableValues(
					fragmentEntryLink.getEditableValuesJSONObject(),
					fragmentEntryLink, infoFieldObjectValuePairs));
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private String _sanitizeEditableValue(
			FragmentEntryLink fragmentEntryLink, String type, String value)
		throws SanitizerException {

		if (Objects.equals(type, "action") || Objects.equals(type, "link") ||
			Objects.equals(type, "text")) {

			value = HtmlUtil.unescape(value);
			value = HtmlUtil.escape(value);
		}
		else {
			value = SanitizerUtil.sanitize(
				fragmentEntryLink.getCompanyId(),
				fragmentEntryLink.getGroupId(), fragmentEntryLink.getUserId(),
				FragmentEntryLink.class.getName(),
				fragmentEntryLink.getFragmentEntryLinkId(),
				ContentTypes.TEXT_HTML, value);
		}

		return value;
	}

	private String _sanitizeEditableValues(
			JSONObject editableValuesJSONObject,
			FragmentEntryLink fragmentEntryLink,
			List<ObjectValuePair<InfoField<?>, String>>
				infoFieldObjectValuePairs)
		throws SanitizerException {

		for (String fragmentEntryProcessorKey :
				_FRAGMENT_ENTRY_PROCESSOR_KEYS) {

			JSONObject editableFragmentEntryProcessorJSONObject =
				editableValuesJSONObject.getJSONObject(
					fragmentEntryProcessorKey);

			if (editableFragmentEntryProcessorJSONObject == null) {
				continue;
			}

			for (ObjectValuePair<InfoField<?>, String>
					infoFieldObjectValuePair : infoFieldObjectValuePairs) {

				String type = infoFieldObjectValuePair.getValue();

				if (!(Objects.equals(type, "action") ||
					  Objects.equals(type, "link") ||
					  Objects.equals(type, "text") ||
					  Objects.equals(type, "rich-text"))) {

					continue;
				}

				InfoField<?> infoField = infoFieldObjectValuePair.getKey();

				InfoLocalizedValue<String> labelInfoLocalizedValue =
					infoField.getLabelInfoLocalizedValue();

				JSONObject editableValueJSONObject =
					editableFragmentEntryProcessorJSONObject.getJSONObject(
						labelInfoLocalizedValue.getValue());

				if (editableValueJSONObject == null) {
					continue;
				}

				String defaultValue = editableValueJSONObject.getString(
					"defaultValue");

				if (Validator.isNotNull(defaultValue)) {
					editableValueJSONObject.put(
						"defaultValue",
						_sanitizeEditableValue(
							fragmentEntryLink, type, defaultValue));
				}

				if (!_fragmentEntryProcessorHelper.isMapped(
						editableValueJSONObject) &&
					!_fragmentEntryProcessorHelper.isMappedCollection(
						editableValueJSONObject) &&
					!_fragmentEntryProcessorHelper.isMappedDisplayPage(
						editableValueJSONObject)) {

					Iterator<String> valueKeysIterator =
						editableValueJSONObject.keys();

					while (valueKeysIterator.hasNext()) {
						String valueKey = valueKeysIterator.next();

						if (valueKey.equals("config") ||
							valueKey.equals("defaultValue")) {

							continue;
						}

						String value = editableValueJSONObject.getString(
							valueKey);

						editableValueJSONObject.put(
							valueKey,
							_sanitizeEditableValue(
								fragmentEntryLink, type, value));
					}
				}
			}
		}

		return editableValuesJSONObject.toString();
	}

	private static final String[] _FRAGMENT_ENTRY_PROCESSOR_KEYS = {
		FragmentEntryProcessorConstants.KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR
	};

	@Reference
	private FragmentEntryProcessorHelper _fragmentEntryProcessorHelper;

	@Reference
	private FragmentRendererController _fragmentRendererController;

}