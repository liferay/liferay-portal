/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.query.field;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.query.field.FieldQueryBuilder;
import com.liferay.portal.search.query.field.FieldQueryBuilderFactory;
import com.liferay.portal.search.query.field.QueryPreProcessConfiguration;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author André de Oliveira
 * @author Rodrigo Paulino
 */
@Component(
	property = {
		"description.fields=content|description",
		"title.fields=name|objectEntryTitle|title"
	},
	service = FieldQueryBuilderFactory.class
)
public class FieldQueryBuilderFactoryImpl implements FieldQueryBuilderFactory {

	@Override
	public FieldQueryBuilder getQueryBuilder(String fieldName) {
		if (queryPreProcessConfiguration.isSubstringSearchAlways(fieldName)) {
			return substringFieldQueryBuilder;
		}

		for (String descriptionFieldName : _descriptionFieldNames) {
			if (fieldName.startsWith(descriptionFieldName)) {
				return descriptionFieldQueryBuilder;
			}
		}

		for (String titleFieldName : _titleFieldNames) {
			if (fieldName.startsWith(titleFieldName)) {
				return titleFieldQueryBuilder;
			}
		}

		return null;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_descriptionFieldNames = _getFields(properties, "description.fields");
		_titleFieldNames = _getFields(properties, "title.fields");
	}

	@Reference(target = "(query.builder.type=description)")
	protected FieldQueryBuilder descriptionFieldQueryBuilder;

	@Reference
	protected QueryPreProcessConfiguration queryPreProcessConfiguration;

	@Reference(target = "(query.builder.type=substring)")
	protected FieldQueryBuilder substringFieldQueryBuilder;

	@Reference(target = "(query.builder.type=title)")
	protected FieldQueryBuilder titleFieldQueryBuilder;

	private Collection<String> _getFields(
		Map<String, Object> properties, String key) {

		String[] values = StringUtil.split(
			GetterUtil.getString(properties.get(key)), CharPool.PIPE);

		return new HashSet<>(Arrays.asList(values));
	}

	private volatile Collection<String> _descriptionFieldNames = Arrays.asList(
		"content", "description");
	private volatile Collection<String> _titleFieldNames = new HashSet<>(
		Arrays.asList("name", "objectEntryTitle", "title"));

}