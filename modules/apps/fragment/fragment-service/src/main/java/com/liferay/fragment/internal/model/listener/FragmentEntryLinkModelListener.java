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
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.layout.util.InfoFieldUtil;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.HtmlUtil;
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

	private String _escapeTextEditableValues(
		JSONObject editableValuesJSONObject, List<InfoField<?>> infoFields) {

		for (String fragmentEntryProcessorKey :
				_FRAGMENT_ENTRY_PROCESSOR_KEYS) {

			JSONObject editableFragmentEntryProcessorJSONObject =
				editableValuesJSONObject.getJSONObject(
					fragmentEntryProcessorKey);

			if (editableFragmentEntryProcessorJSONObject == null) {
				continue;
			}

			for (InfoField<?> infoField : infoFields) {
				if (!Objects.equals(
						infoField.getInfoFieldType(),
						TextInfoFieldType.INSTANCE)) {

					continue;
				}

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
					defaultValue = HtmlUtil.unescape(defaultValue);

					editableValueJSONObject.put(
						"defaultValue", HtmlUtil.escape(defaultValue));
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

						value = HtmlUtil.unescape(value);

						editableValueJSONObject.put(
							valueKey, HtmlUtil.escape(value));
					}
				}
			}
		}

		return editableValuesJSONObject.toString();
	}

	private void _processInfoFields(FragmentEntryLink fragmentEntryLink) {
		Layout layout = _layoutLocalService.fetchLayout(
			fragmentEntryLink.getPlid());

		if (layout == null) {
			return;
		}

		List<InfoField<?>> infoFields = new ArrayList<>();

		try {
			try (AutoCloseable autoCloseable =
					_layoutServiceContextHelper.getServiceContextAutoCloseable(
						layout)) {

				InfoFieldUtil.forEachInfoField(
					fragmentEntryLink, _fragmentRendererController,
					(fieldObjectValuePair, infoField, unsafeSupplier) ->
						infoFields.add(infoField));
			}
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}

		if (infoFields.isEmpty()) {
			return;
		}

		fragmentEntryLink.setEditableValues(
			_escapeTextEditableValues(
				fragmentEntryLink.getEditableValuesJSONObject(), infoFields));
	}

	private static final String[] _FRAGMENT_ENTRY_PROCESSOR_KEYS = {
		FragmentEntryProcessorConstants.KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR
	};

	@Reference
	private FragmentEntryProcessorHelper _fragmentEntryProcessorHelper;

	@Reference
	private FragmentRendererController _fragmentRendererController;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutServiceContextHelper _layoutServiceContextHelper;

}