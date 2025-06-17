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
	description = "A page specification of a content page. A content page will contain 1 page specification for its draft layout and 1 page specification for its published layout.",
	value = "ContentPageSpecification"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ContentPageSpecification")
public class ContentPageSpecification
	extends PageSpecification implements Serializable {

	public static ContentPageSpecification toDTO(String json) {
		return ObjectMapperUtil.readValue(ContentPageSpecification.class, json);
	}

	public static ContentPageSpecification unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			ContentPageSpecification.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The draft content page specification external reference code or null if it is a draft content page specification."
	)
	public String getDraftContentPageSpecificationExternalReferenceCode() {
		if (_draftContentPageSpecificationExternalReferenceCodeSupplier !=
				null) {

			draftContentPageSpecificationExternalReferenceCode =
				_draftContentPageSpecificationExternalReferenceCodeSupplier.
					get();

			_draftContentPageSpecificationExternalReferenceCodeSupplier = null;
		}

		return draftContentPageSpecificationExternalReferenceCode;
	}

	public void setDraftContentPageSpecificationExternalReferenceCode(
		String draftContentPageSpecificationExternalReferenceCode) {

		this.draftContentPageSpecificationExternalReferenceCode =
			draftContentPageSpecificationExternalReferenceCode;

		_draftContentPageSpecificationExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setDraftContentPageSpecificationExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			draftContentPageSpecificationExternalReferenceCodeUnsafeSupplier) {

		_draftContentPageSpecificationExternalReferenceCodeSupplier = () -> {
			try {
				return draftContentPageSpecificationExternalReferenceCodeUnsafeSupplier.
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
		description = "The draft content page specification external reference code or null if it is a draft content page specification."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String draftContentPageSpecificationExternalReferenceCode;

	@JsonIgnore
	private Supplier<String>
		_draftContentPageSpecificationExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public PageExperience[] getPageExperiences() {
		if (_pageExperiencesSupplier != null) {
			pageExperiences = _pageExperiencesSupplier.get();

			_pageExperiencesSupplier = null;
		}

		return pageExperiences;
	}

	public void setPageExperiences(PageExperience[] pageExperiences) {
		this.pageExperiences = pageExperiences;

		_pageExperiencesSupplier = null;
	}

	@JsonIgnore
	public void setPageExperiences(
		UnsafeSupplier<PageExperience[], Exception>
			pageExperiencesUnsafeSupplier) {

		_pageExperiencesSupplier = () -> {
			try {
				return pageExperiencesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected PageExperience[] pageExperiences;

	@JsonIgnore
	private Supplier<PageExperience[]> _pageExperiencesSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ContentPageSpecification)) {
			return false;
		}

		ContentPageSpecification contentPageSpecification =
			(ContentPageSpecification)object;

		return Objects.equals(toString(), contentPageSpecification.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String draftContentPageSpecificationExternalReferenceCode =
			getDraftContentPageSpecificationExternalReferenceCode();

		if (draftContentPageSpecificationExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append(
				"\"draftContentPageSpecificationExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(draftContentPageSpecificationExternalReferenceCode));

			sb.append("\"");
		}

		PageExperience[] pageExperiences = getPageExperiences();

		if (pageExperiences != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageExperiences\": ");

			sb.append("[");

			for (int i = 0; i < pageExperiences.length; i++) {
				sb.append(String.valueOf(pageExperiences[i]));

				if ((i + 1) < pageExperiences.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

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

		Settings settings = getSettings();

		if (settings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"settings\": ");

			sb.append(String.valueOf(settings));
		}

		String siteTemplatePageSpecificationExternalReferenceCode =
			getSiteTemplatePageSpecificationExternalReferenceCode();

		if (siteTemplatePageSpecificationExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append(
				"\"siteTemplatePageSpecificationExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(siteTemplatePageSpecificationExternalReferenceCode));

			sb.append("\"");
		}

		Status status = getStatus();

		if (status != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");

			sb.append(status);

			sb.append("\"");
		}

		Type type = getType();

		if (type != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(type);

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.ContentPageSpecification",
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