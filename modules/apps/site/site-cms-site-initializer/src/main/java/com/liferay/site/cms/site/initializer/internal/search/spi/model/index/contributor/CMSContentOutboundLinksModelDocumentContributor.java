/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.search.spi.model.index.contributor;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.bag.ObjectFieldBag;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentContributor;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.site.cms.site.initializer.internal.search.links.OutboundLinksUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jürgen Kappler
 */
@Component(service = DocumentContributor.class)
public class CMSContentOutboundLinksModelDocumentContributor
	implements DocumentContributor<ObjectEntry> {

	@Override
	public void contribute(
		Document document, BaseModel<ObjectEntry> baseModel) {

		if (!(baseModel instanceof ObjectEntry)) {
			return;
		}

		ObjectEntry objectEntry = (ObjectEntry)baseModel;

		try {
			if (!FeatureFlagManagerUtil.isEnabled(
					objectEntry.getCompanyId(), "LPD-82226")) {

				return;
			}

			ObjectDefinition objectDefinition =
				objectEntry.getObjectDefinition();

			if (!objectDefinition.isCMS()) {
				return;
			}

			Set<String> outboundLinks = new LinkedHashSet<>();

			Map<String, Serializable> indexedValues =
				objectEntry.getIndexedValues();

			ObjectFieldBag objectFieldBag =
				objectDefinition.getObjectFieldBag();

			for (ObjectField objectField :
					objectFieldBag.getIndexedObjectFields()) {

				String businessType = objectField.getBusinessType();

				if (Objects.equals(
						businessType,
						ObjectFieldConstants.BUSINESS_TYPE_RELATIONSHIP)) {

					long objectEntryId = GetterUtil.getLong(
						indexedValues.get(objectField.getName()));

					if (objectEntryId != 0) {
						outboundLinks.add(
							_getToken(
								"objectEntryId",
								String.valueOf(objectEntryId)));
					}
				}
				else if (Objects.equals(
							businessType,
							ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT)) {

					for (String content :
							_getContents(indexedValues, objectField)) {

						for (String externalReferenceCode :
								OutboundLinksUtil.
									getObjectEntryExternalReferenceCodes(
										content)) {

							outboundLinks.add(
								_getToken(
									"objectEntryERC", externalReferenceCode));
						}
					}
				}
			}

			if (!outboundLinks.isEmpty()) {
				document.addKeyword(
					"outboundLinks", outboundLinks.toArray(new String[0]));
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to contribute outbound links for object entry " +
						objectEntry.getObjectEntryId(),
					exception);
			}
		}
	}

	private List<String> _getContents(
		Map<String, Serializable> indexedValues, ObjectField objectField) {

		if (objectField.isLocalized()) {
			Object localizedValues = indexedValues.get(
				objectField.getI18nObjectFieldName());

			if (localizedValues instanceof Map) {
				Map<?, ?> localizedValuesMap = (Map<?, ?>)localizedValues;

				return TransformUtil.transform(
					localizedValuesMap.values(),
					localizedValue -> {
						if (localizedValue == null) {
							return null;
						}

						return String.valueOf(localizedValue);
					});
			}
		}

		Object content = indexedValues.get(objectField.getName());

		if (content == null) {
			return Collections.emptyList();
		}

		return Collections.singletonList(String.valueOf(content));
	}

	private String _getToken(String prefix, String value) {
		return StringBundler.concat(prefix, StringPool.UNDERLINE, value);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CMSContentOutboundLinksModelDocumentContributor.class);

}