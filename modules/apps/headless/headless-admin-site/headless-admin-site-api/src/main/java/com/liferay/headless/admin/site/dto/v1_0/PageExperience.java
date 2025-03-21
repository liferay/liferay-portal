/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "A customized experience for a given page specification.",
	value = "PageExperience"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PageExperience")
public class PageExperience implements Serializable {

	public static PageExperience toDTO(String json) {
		return ObjectMapperUtil.readValue(PageExperience.class, json);
	}

	public static PageExperience unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(PageExperience.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The experience's external reference code, unique per site."
	)
	public String getExternalReferenceCode() {
		if (_externalReferenceCodeSupplier != null) {
			externalReferenceCode = _externalReferenceCodeSupplier.get();

			_externalReferenceCodeSupplier = null;
		}

		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;

		_externalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		_externalReferenceCodeSupplier = () -> {
			try {
				return externalReferenceCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The experience's external reference code, unique per site."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The experience's key."
	)
	public String getKey() {
		if (_keySupplier != null) {
			key = _keySupplier.get();

			_keySupplier = null;
		}

		return key;
	}

	public void setKey(String key) {
		this.key = key;

		_keySupplier = null;
	}

	@JsonIgnore
	public void setKey(UnsafeSupplier<String, Exception> keyUnsafeSupplier) {
		_keySupplier = () -> {
			try {
				return keyUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The experience's key.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String key;

	@JsonIgnore
	private Supplier<String> _keySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The localized experience's names."
	)
	@Valid
	public Map<String, String> getName_i18n() {
		if (_name_i18nSupplier != null) {
			name_i18n = _name_i18nSupplier.get();

			_name_i18nSupplier = null;
		}

		return name_i18n;
	}

	public void setName_i18n(Map<String, String> name_i18n) {
		this.name_i18n = name_i18n;

		_name_i18nSupplier = null;
	}

	@JsonIgnore
	public void setName_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			name_i18nUnsafeSupplier) {

		_name_i18nSupplier = () -> {
			try {
				return name_i18nUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The localized experience's names.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> name_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _name_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page elements in the experience."
	)
	@Valid
	public PageElement[] getPageElements() {
		if (_pageElementsSupplier != null) {
			pageElements = _pageElementsSupplier.get();

			_pageElementsSupplier = null;
		}

		return pageElements;
	}

	public void setPageElements(PageElement[] pageElements) {
		this.pageElements = pageElements;

		_pageElementsSupplier = null;
	}

	@JsonIgnore
	public void setPageElements(
		UnsafeSupplier<PageElement[], Exception> pageElementsUnsafeSupplier) {

		_pageElementsSupplier = () -> {
			try {
				return pageElementsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The page elements in the experience.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected PageElement[] pageElements;

	@JsonIgnore
	private Supplier<PageElement[]> _pageElementsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page rules in the experience."
	)
	@Valid
	public PageRule[] getPageRules() {
		if (_pageRulesSupplier != null) {
			pageRules = _pageRulesSupplier.get();

			_pageRulesSupplier = null;
		}

		return pageRules;
	}

	public void setPageRules(PageRule[] pageRules) {
		this.pageRules = pageRules;

		_pageRulesSupplier = null;
	}

	@JsonIgnore
	public void setPageRules(
		UnsafeSupplier<PageRule[], Exception> pageRulesUnsafeSupplier) {

		_pageRulesSupplier = () -> {
			try {
				return pageRulesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The page rules in the experience.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected PageRule[] pageRules;

	@JsonIgnore
	private Supplier<PageRule[]> _pageRulesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page specification's external reference code."
	)
	public String getPageSpecificationExternalReferenceCode() {
		if (_pageSpecificationExternalReferenceCodeSupplier != null) {
			pageSpecificationExternalReferenceCode =
				_pageSpecificationExternalReferenceCodeSupplier.get();

			_pageSpecificationExternalReferenceCodeSupplier = null;
		}

		return pageSpecificationExternalReferenceCode;
	}

	public void setPageSpecificationExternalReferenceCode(
		String pageSpecificationExternalReferenceCode) {

		this.pageSpecificationExternalReferenceCode =
			pageSpecificationExternalReferenceCode;

		_pageSpecificationExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setPageSpecificationExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			pageSpecificationExternalReferenceCodeUnsafeSupplier) {

		_pageSpecificationExternalReferenceCodeSupplier = () -> {
			try {
				return pageSpecificationExternalReferenceCodeUnsafeSupplier.
					get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The page specification's external reference code."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String pageSpecificationExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _pageSpecificationExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "the experience's priority. It must be a unique value within the page specification. The default experience will always be assigned priority 0. A priority higher than 0 will result in an experience being active and a priority lower than 0 will result in an experience being inactive."
	)
	public Integer getPriority() {
		if (_prioritySupplier != null) {
			priority = _prioritySupplier.get();

			_prioritySupplier = null;
		}

		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;

		_prioritySupplier = null;
	}

	@JsonIgnore
	public void setPriority(
		UnsafeSupplier<Integer, Exception> priorityUnsafeSupplier) {

		_prioritySupplier = () -> {
			try {
				return priorityUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "the experience's priority. It must be a unique value within the page specification. The default experience will always be assigned priority 0. A priority higher than 0 will result in an experience being active and a priority lower than 0 will result in an experience being inactive."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer priority;

	@JsonIgnore
	private Supplier<Integer> _prioritySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The segment's external reference code."
	)
	public String getSegmentExternalReferenceCode() {
		if (_segmentExternalReferenceCodeSupplier != null) {
			segmentExternalReferenceCode =
				_segmentExternalReferenceCodeSupplier.get();

			_segmentExternalReferenceCodeSupplier = null;
		}

		return segmentExternalReferenceCode;
	}

	public void setSegmentExternalReferenceCode(
		String segmentExternalReferenceCode) {

		this.segmentExternalReferenceCode = segmentExternalReferenceCode;

		_segmentExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setSegmentExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			segmentExternalReferenceCodeUnsafeSupplier) {

		_segmentExternalReferenceCodeSupplier = () -> {
			try {
				return segmentExternalReferenceCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The segment's external reference code.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String segmentExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _segmentExternalReferenceCodeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PageExperience)) {
			return false;
		}

		PageExperience pageExperience = (PageExperience)object;

		return Objects.equals(toString(), pageExperience.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String externalReferenceCode = getExternalReferenceCode();

		if (externalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(externalReferenceCode));

			sb.append("\"");
		}

		String key = getKey();

		if (key != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(key));

			sb.append("\"");
		}

		Map<String, String> name_i18n = getName_i18n();

		if (name_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(name_i18n));
		}

		PageElement[] pageElements = getPageElements();

		if (pageElements != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageElements\": ");

			sb.append("[");

			for (int i = 0; i < pageElements.length; i++) {
				sb.append(String.valueOf(pageElements[i]));

				if ((i + 1) < pageElements.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		PageRule[] pageRules = getPageRules();

		if (pageRules != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageRules\": ");

			sb.append("[");

			for (int i = 0; i < pageRules.length; i++) {
				sb.append(String.valueOf(pageRules[i]));

				if ((i + 1) < pageRules.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String pageSpecificationExternalReferenceCode =
			getPageSpecificationExternalReferenceCode();

		if (pageSpecificationExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageSpecificationExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(pageSpecificationExternalReferenceCode));

			sb.append("\"");
		}

		Integer priority = getPriority();

		if (priority != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(priority);
		}

		String segmentExternalReferenceCode = getSegmentExternalReferenceCode();

		if (segmentExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"segmentExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(segmentExternalReferenceCode));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.PageExperience",
		name = "x-class-name"
	)
	public String xClassName;

	private static String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[])value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof Map) {
						sb.append(_toJSON((Map<String, ?>)valueArray[i]));
					}
					else if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>)value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}