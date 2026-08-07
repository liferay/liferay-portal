/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.dto.v1_0;

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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "A reusable, company-scoped specification attribute template; specifications group products by attribute, are optionally bound to one or more list type definitions that constrain their values, and can be displayed on the storefront and used as search facets.",
	value = "Specification"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A reusable, company-scoped specification attribute template; specifications group products by attribute, are optionally bound to one or more list type definitions that constrain their values, and can be displayed on the storefront and used as search facets.",
	requiredProperties = {"key", "title"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Specification")
public class Specification implements Serializable {

	public static Specification toDTO(String json) {
		return ObjectMapperUtil.readValue(Specification.class, json);
	}

	public static Specification unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Specification.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Localized administrative description shown in the admin UI; map keys are locale codes and values are the translated strings.",
		example = "{en_US=Material composition, hr_HR=Sastav materijala, hu_HU=Anyagosszetetel}"
	)
	@Valid
	public Map<String, String> getDescription() {
		if (_descriptionSupplier != null) {
			description = _descriptionSupplier.get();

			_descriptionSupplier = null;
		}

		return description;
	}

	public void setDescription(Map<String, String> description) {
		this.description = description;

		_descriptionSupplier = null;
	}

	@JsonIgnore
	public void setDescription(
		UnsafeSupplier<Map<String, String>, Exception>
			descriptionUnsafeSupplier) {

		_descriptionSupplier = () -> {
			try {
				return descriptionUnsafeSupplier.get();
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
		description = "Localized administrative description shown in the admin UI; map keys are locale codes and values are the translated strings."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> description;

	@JsonIgnore
	private Supplier<Map<String, String>> _descriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Idempotency key for create and update; must be unique per specification within the company.",
		example = "AB-34098-789-N"
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
		description = "Idempotency key for create and update; must be unique per specification within the company."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether the specification is exposed as a search facet in product catalog searches; indexed and filterable through the visible field on the search results.",
		example = "true"
	)
	public Boolean getFacetable() {
		if (_facetableSupplier != null) {
			facetable = _facetableSupplier.get();

			_facetableSupplier = null;
		}

		return facetable;
	}

	public void setFacetable(Boolean facetable) {
		this.facetable = facetable;

		_facetableSupplier = null;
	}

	@JsonIgnore
	public void setFacetable(
		UnsafeSupplier<Boolean, Exception> facetableUnsafeSupplier) {

		_facetableSupplier = () -> {
			try {
				return facetableUnsafeSupplier.get();
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
		description = "Whether the specification is exposed as a search facet in product catalog searches; indexed and filterable through the visible field on the search results."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean facetable;

	@JsonIgnore
	private Supplier<Boolean> _facetableSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Primary key; read-only and assigned by the service on create.",
		example = "31130"
	)
	public Long getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(Long id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		_idSupplier = () -> {
			try {
				return idUnsafeSupplier.get();
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
		description = "Primary key; read-only and assigned by the service on create."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Machine identifier used for lookups and integrations; normalized to a friendly URL form (underscores converted to dashes) and must be unique per company; required.",
		example = "specification-key"
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

	@GraphQLField(
		description = "Machine identifier used for lookups and integrations; normalized to a friendly URL form (underscores converted to dashes) and must be unique per company; required."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotEmpty
	protected String key;

	@JsonIgnore
	private Supplier<String> _keySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference codes of the list type definitions that constrain the allowed values of this specification; they take precedence over `listTypeDefinitionIds`. A code that does not resolve fails the request, except during an import, where it creates an empty list type definition to be completed later.",
		example = "[AB-34098-789-N, AB-34098-789-O]"
	)
	public String[] getListTypeDefinitionExternalReferenceCodes() {
		if (_listTypeDefinitionExternalReferenceCodesSupplier != null) {
			listTypeDefinitionExternalReferenceCodes =
				_listTypeDefinitionExternalReferenceCodesSupplier.get();

			_listTypeDefinitionExternalReferenceCodesSupplier = null;
		}

		return listTypeDefinitionExternalReferenceCodes;
	}

	public void setListTypeDefinitionExternalReferenceCodes(
		String[] listTypeDefinitionExternalReferenceCodes) {

		this.listTypeDefinitionExternalReferenceCodes =
			listTypeDefinitionExternalReferenceCodes;

		_listTypeDefinitionExternalReferenceCodesSupplier = null;
	}

	@JsonIgnore
	public void setListTypeDefinitionExternalReferenceCodes(
		UnsafeSupplier<String[], Exception>
			listTypeDefinitionExternalReferenceCodesUnsafeSupplier) {

		_listTypeDefinitionExternalReferenceCodesSupplier = () -> {
			try {
				return listTypeDefinitionExternalReferenceCodesUnsafeSupplier.
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
		description = "External reference codes of the list type definitions that constrain the allowed values of this specification; they take precedence over `listTypeDefinitionIds`. A code that does not resolve fails the request, except during an import, where it creates an empty list type definition to be completed later."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] listTypeDefinitionExternalReferenceCodes;

	@JsonIgnore
	private Supplier<String[]>
		_listTypeDefinitionExternalReferenceCodesSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		deprecated = true,
		description = "Deprecated; returns the first ID from `listTypeDefinitionIds` in iteration order, which is not stable; preserved only for backward compatibility with older clients; use `listTypeDefinitionIds` instead.",
		example = "31130"
	)
	public Long getListTypeDefinitionId() {
		if (_listTypeDefinitionIdSupplier != null) {
			listTypeDefinitionId = _listTypeDefinitionIdSupplier.get();

			_listTypeDefinitionIdSupplier = null;
		}

		return listTypeDefinitionId;
	}

	public void setListTypeDefinitionId(Long listTypeDefinitionId) {
		this.listTypeDefinitionId = listTypeDefinitionId;

		_listTypeDefinitionIdSupplier = null;
	}

	@JsonIgnore
	public void setListTypeDefinitionId(
		UnsafeSupplier<Long, Exception> listTypeDefinitionIdUnsafeSupplier) {

		_listTypeDefinitionIdSupplier = () -> {
			try {
				return listTypeDefinitionIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@Deprecated
	@GraphQLField(
		description = "Deprecated; returns the first ID from `listTypeDefinitionIds` in iteration order, which is not stable; preserved only for backward compatibility with older clients; use `listTypeDefinitionIds` instead."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long listTypeDefinitionId;

	@JsonIgnore
	private Supplier<Long> _listTypeDefinitionIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "References to the list type definitions that constrain the allowed values of this specification.",
		example = "[30130, 30131]"
	)
	public Long[] getListTypeDefinitionIds() {
		if (_listTypeDefinitionIdsSupplier != null) {
			listTypeDefinitionIds = _listTypeDefinitionIdsSupplier.get();

			_listTypeDefinitionIdsSupplier = null;
		}

		return listTypeDefinitionIds;
	}

	public void setListTypeDefinitionIds(Long[] listTypeDefinitionIds) {
		this.listTypeDefinitionIds = listTypeDefinitionIds;

		_listTypeDefinitionIdsSupplier = null;
	}

	@JsonIgnore
	public void setListTypeDefinitionIds(
		UnsafeSupplier<Long[], Exception> listTypeDefinitionIdsUnsafeSupplier) {

		_listTypeDefinitionIdsSupplier = () -> {
			try {
				return listTypeDefinitionIdsUnsafeSupplier.get();
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
		description = "References to the list type definitions that constrain the allowed values of this specification."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long[] listTypeDefinitionIds;

	@JsonIgnore
	private Supplier<Long[]> _listTypeDefinitionIdsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public OptionCategory getOptionCategory() {
		if (_optionCategorySupplier != null) {
			optionCategory = _optionCategorySupplier.get();

			_optionCategorySupplier = null;
		}

		return optionCategory;
	}

	public void setOptionCategory(OptionCategory optionCategory) {
		this.optionCategory = optionCategory;

		_optionCategorySupplier = null;
	}

	@JsonIgnore
	public void setOptionCategory(
		UnsafeSupplier<OptionCategory, Exception>
			optionCategoryUnsafeSupplier) {

		_optionCategorySupplier = () -> {
			try {
				return optionCategoryUnsafeSupplier.get();
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
	protected OptionCategory optionCategory;

	@JsonIgnore
	private Supplier<OptionCategory> _optionCategorySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Display order used when listing specifications in the admin UI; lower values appear first.",
		example = "1.2"
	)
	public Double getPriority() {
		if (_prioritySupplier != null) {
			priority = _prioritySupplier.get();

			_prioritySupplier = null;
		}

		return priority;
	}

	public void setPriority(Double priority) {
		this.priority = priority;

		_prioritySupplier = null;
	}

	@JsonIgnore
	public void setPriority(
		UnsafeSupplier<Double, Exception> priorityUnsafeSupplier) {

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
		description = "Display order used when listing specifications in the admin UI; lower values appear first."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Double priority;

	@JsonIgnore
	private Supplier<Double> _prioritySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Localized display label shown to administrators and (when visible) to shoppers; map keys are locale codes and values are the translated strings; required -- the title map must hold a non-null value for the site default locale.",
		example = "{en_US=Material, hr_HR=Materijal, hu_HU=Anyag}"
	)
	@Valid
	public Map<String, String> getTitle() {
		if (_titleSupplier != null) {
			title = _titleSupplier.get();

			_titleSupplier = null;
		}

		return title;
	}

	public void setTitle(Map<String, String> title) {
		this.title = title;

		_titleSupplier = null;
	}

	@JsonIgnore
	public void setTitle(
		UnsafeSupplier<Map<String, String>, Exception> titleUnsafeSupplier) {

		_titleSupplier = () -> {
			try {
				return titleUnsafeSupplier.get();
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
		description = "Localized display label shown to administrators and (when visible) to shoppers; map keys are locale codes and values are the translated strings; required -- the title map must hold a non-null value for the site default locale."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotNull
	protected Map<String, String> title;

	@JsonIgnore
	private Supplier<Map<String, String>> _titleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether the specification is shown to shoppers on the storefront and exposed through search facets.",
		example = "true"
	)
	public Boolean getVisible() {
		if (_visibleSupplier != null) {
			visible = _visibleSupplier.get();

			_visibleSupplier = null;
		}

		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;

		_visibleSupplier = null;
	}

	@JsonIgnore
	public void setVisible(
		UnsafeSupplier<Boolean, Exception> visibleUnsafeSupplier) {

		_visibleSupplier = () -> {
			try {
				return visibleUnsafeSupplier.get();
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
		description = "Whether the specification is shown to shoppers on the storefront and exposed through search facets."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean visible;

	@JsonIgnore
	private Supplier<Boolean> _visibleSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Specification)) {
			return false;
		}

		Specification specification = (Specification)object;

		return Objects.equals(toString(), specification.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Map<String, String> description = getDescription();

		if (description != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append(_toJSON(description));
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

		Boolean facetable = getFacetable();

		if (facetable != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"facetable\": ");

			sb.append(facetable);
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
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

		String[] listTypeDefinitionExternalReferenceCodes =
			getListTypeDefinitionExternalReferenceCodes();

		if (listTypeDefinitionExternalReferenceCodes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"listTypeDefinitionExternalReferenceCodes\": ");

			sb.append("[");

			for (int i = 0; i < listTypeDefinitionExternalReferenceCodes.length;
				 i++) {

				sb.append("\"");

				sb.append(_escape(listTypeDefinitionExternalReferenceCodes[i]));

				sb.append("\"");

				if ((i + 1) < listTypeDefinitionExternalReferenceCodes.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Long listTypeDefinitionId = getListTypeDefinitionId();

		if (listTypeDefinitionId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"listTypeDefinitionId\": ");

			sb.append(listTypeDefinitionId);
		}

		Long[] listTypeDefinitionIds = getListTypeDefinitionIds();

		if (listTypeDefinitionIds != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"listTypeDefinitionIds\": ");

			sb.append("[");

			for (int i = 0; i < listTypeDefinitionIds.length; i++) {
				sb.append(listTypeDefinitionIds[i]);

				if ((i + 1) < listTypeDefinitionIds.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		OptionCategory optionCategory = getOptionCategory();

		if (optionCategory != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"optionCategory\": ");

			sb.append(String.valueOf(optionCategory));
		}

		Double priority = getPriority();

		if (priority != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(priority);
		}

		Map<String, String> title = getTitle();

		if (title != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append(_toJSON(title));
		}

		Boolean visible = getVisible();

		if (visible != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"visible\": ");

			sb.append(visible);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.catalog.dto.v1_0.Specification",
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
// LIFERAY-REST-BUILDER-HASH:209765726