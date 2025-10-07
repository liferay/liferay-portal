/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.editable;

import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.entry.processor.editable.mapper.EditableElementMapper;
import com.liferay.fragment.entry.processor.editable.parser.EditableElementParser;
import com.liferay.fragment.entry.processor.helper.FragmentEntryProcessorHelper;
import com.liferay.fragment.entry.processor.util.AnalyticsAttributesUtil;
import com.liferay.fragment.entry.processor.util.EditableFragmentEntryProcessorUtil;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.DocumentFragmentEntryProcessor;
import com.liferay.fragment.processor.FragmentEntryProcessorContext;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Collector;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "fragment.entry.processor.priority:Integer=2",
	service = DocumentFragmentEntryProcessor.class
)
public class EditableDocumentFragmentEntryProcessor
	implements DocumentFragmentEntryProcessor {

	@Override
	public void processFragmentEntryLinkHTML(
			FragmentEntryLink fragmentEntryLink, Document document,
			FragmentEntryProcessorContext fragmentEntryProcessorContext)
		throws PortalException {

		JSONObject jsonObject = fragmentEntryLink.getEditableValuesJSONObject();

		if (jsonObject.length() == 0) {
			jsonObject.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				_getDefaultEditableValuesJSONObject(document));
		}

		JSONObject editableValuesJSONObject = jsonObject.getJSONObject(
			FragmentEntryProcessorConstants.
				KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR);

		if (editableValuesJSONObject == null) {
			return;
		}

		Map<InfoItemReference, InfoItemFieldValues> infoDisplaysFieldValues =
			new HashMap<>();

		Elements elements = Collector.collect(
			new Evaluator() {

				@Override
				public boolean matches(Element root, Element element) {
					if (element.hasAttr("data-lfr-editable-id") ||
						Objects.equals(element.normalName(), "lfr-editable")) {

						return true;
					}

					return false;
				}

			},
			document.body());

		for (Element element : elements) {
			EditableElementParser editableElementParser =
				_getEditableElementParser(element);

			if (editableElementParser == null) {
				continue;
			}

			String id = EditableFragmentEntryProcessorUtil.getElementId(
				element);

			if (!editableValuesJSONObject.has(id)) {
				continue;
			}

			JSONObject editableValueJSONObject =
				editableValuesJSONObject.getJSONObject(id);

			String value = null;

			JSONObject mappedValueConfigJSONObject =
				_jsonFactory.createJSONObject();

			if (_fragmentEntryProcessorHelper.isMapped(
					editableValueJSONObject) ||
				_fragmentEntryProcessorHelper.isMappedCollection(
					editableValueJSONObject) ||
				_fragmentEntryProcessorHelper.isMappedDisplayPage(
					editableValueJSONObject)) {

				Object fieldValue = _fragmentEntryProcessorHelper.getFieldValue(
					editableValueJSONObject, infoDisplaysFieldValues,
					fragmentEntryProcessorContext);

				if (fieldValue != null) {
					String fieldId = editableValueJSONObject.getString(
						"collectionFieldId");

					if (_fragmentEntryProcessorHelper.isMappedDisplayPage(
							editableValueJSONObject)) {

						fieldId = editableValueJSONObject.getString(
							"mappedField");
					}
					else if (_fragmentEntryProcessorHelper.isMapped(
								editableValueJSONObject)) {

						fieldId = editableValueJSONObject.getString("fieldId");
					}

					mappedValueConfigJSONObject =
						editableElementParser.getFieldTemplateConfigJSONObject(
							fieldId, fragmentEntryProcessorContext.getLocale(),
							fieldValue);

					value = editableElementParser.parseFieldValue(fieldValue);
				}
				else {
					value = editableValueJSONObject.getString("defaultValue");
				}
			}
			else {
				value = _fragmentEntryProcessorHelper.getEditableValue(
					editableValueJSONObject,
					fragmentEntryProcessorContext.getLocale());
			}

			JSONObject configJSONObject = JSONUtil.merge(
				editableValueJSONObject.getJSONObject("config"),
				mappedValueConfigJSONObject);

			JSONObject localizedJSONObject = configJSONObject.getJSONObject(
				LocaleUtil.toLanguageId(
					fragmentEntryProcessorContext.getLocale()));

			String mapperType = configJSONObject.getString(
				"mapperType", element.attr("type"));

			if ((localizedJSONObject != null) &&
				(localizedJSONObject.length() > 0)) {

				configJSONObject = localizedJSONObject;
			}

			if (value != null) {
				editableElementParser.replace(element, value, configJSONObject);
			}

			if (!fragmentEntryProcessorContext.isEditMode()) {
				if (Validator.isNull(mapperType)) {
					mapperType = element.attr("data-lfr-editable-type");
				}

				EditableElementMapper editableElementMapper =
					_editableElementMapperServiceTrackerMap.getService(
						mapperType);

				if (editableElementMapper != null) {
					editableElementMapper.map(
						element, configJSONObject,
						fragmentEntryProcessorContext);
				}
			}

			if ((fragmentEntryProcessorContext.isPreviewMode() ||
				 fragmentEntryProcessorContext.isViewMode()) &&
				Objects.equals(element.tagName(), "lfr-editable")) {

				element.removeAttr("id");
				element.removeAttr("type");

				String tagName = element.attr("view-tag-name");

				if (!Objects.equals(tagName, "span")) {
					tagName = "div";
				}

				element.tagName(tagName);

				element.removeAttr("view-tag-name");
			}

			if (FeatureFlagManagerUtil.isEnabled(
					fragmentEntryLink.getCompanyId(), "LPD-39437") &&
				fragmentEntryProcessorContext.isViewMode()) {

				AnalyticsAttributesUtil.addAnalyticsAttributes(
					editableValueJSONObject, element,
					fragmentEntryProcessorContext,
					_fragmentEntryProcessorHelper, infoDisplaysFieldValues,
					_infoItemServiceRegistry);
			}
		}

		if ((fragmentEntryProcessorContext.getPreviewClassNameId() > 0) &&
			(fragmentEntryProcessorContext.getPreviewClassPK() > 0)) {

			InfoItemReference infoItemReference = new InfoItemReference(
				_portal.getClassName(
					fragmentEntryProcessorContext.getPreviewClassNameId()),
				new ClassPKInfoItemIdentifier(
					fragmentEntryProcessorContext.getPreviewClassPK()));

			if (infoDisplaysFieldValues.containsKey(infoItemReference)) {
				Element previewElement = new Element("div");

				previewElement.attr("style", "border: 1px solid #0B5FFF");

				Element bodyElement = document.body();

				previewElement.html(bodyElement.html());
			}
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_editableElementMapperServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, EditableElementMapper.class, "type");
		_editableElementParserServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, EditableElementParser.class, "type");
	}

	@Deactivate
	protected void deactivate() {
		_editableElementMapperServiceTrackerMap.close();
		_editableElementParserServiceTrackerMap.close();
	}

	private JSONObject _getDefaultEditableValuesJSONObject(Document document) {
		JSONObject defaultEditableValuesJSONObject =
			_jsonFactory.createJSONObject();

		for (Element element :
				document.select("lfr-editable,*[data-lfr-editable-id]")) {

			EditableElementParser editableElementParser =
				_getEditableElementParser(element);

			if (editableElementParser == null) {
				continue;
			}

			JSONObject defaultValueJSONObject = JSONUtil.put(
				"config", editableElementParser.getAttributes(element)
			).put(
				"defaultValue", editableElementParser.getValue(element)
			);

			defaultEditableValuesJSONObject.put(
				EditableFragmentEntryProcessorUtil.getElementId(element),
				defaultValueJSONObject);
		}

		return defaultEditableValuesJSONObject;
	}

	private EditableElementParser _getEditableElementParser(Element element) {
		String type = EditableFragmentEntryProcessorUtil.getElementType(
			element);

		return _editableElementParserServiceTrackerMap.getService(type);
	}

	private ServiceTrackerMap<String, EditableElementMapper>
		_editableElementMapperServiceTrackerMap;
	private ServiceTrackerMap<String, EditableElementParser>
		_editableElementParserServiceTrackerMap;

	@Reference
	private FragmentEntryProcessorHelper _fragmentEntryProcessorHelper;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

}