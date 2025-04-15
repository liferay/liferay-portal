/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.dto.v1_0;

import com.liferay.search.experiences.rest.client.function.UnsafeSupplier;
import com.liferay.search.experiences.rest.client.serdes.v1_0.HighlightConfigurationSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class HighlightConfiguration implements Cloneable, Serializable {

	public static HighlightConfiguration toDTO(String json) {
		return HighlightConfigurationSerDes.toDTO(json);
	}

	public Map<String, HighlightField> getFields() {
		return fields;
	}

	public void setFields(Map<String, HighlightField> fields) {
		this.fields = fields;
	}

	public void setFields(
		UnsafeSupplier<Map<String, HighlightField>, Exception>
			fieldsUnsafeSupplier) {

		try {
			fields = fieldsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, HighlightField> fields;

	public Integer getFragment_size() {
		return fragment_size;
	}

	public void setFragment_size(Integer fragment_size) {
		this.fragment_size = fragment_size;
	}

	public void setFragment_size(
		UnsafeSupplier<Integer, Exception> fragment_sizeUnsafeSupplier) {

		try {
			fragment_size = fragment_sizeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer fragment_size;

	public Integer getNumber_of_fragments() {
		return number_of_fragments;
	}

	public void setNumber_of_fragments(Integer number_of_fragments) {
		this.number_of_fragments = number_of_fragments;
	}

	public void setNumber_of_fragments(
		UnsafeSupplier<Integer, Exception> number_of_fragmentsUnsafeSupplier) {

		try {
			number_of_fragments = number_of_fragmentsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer number_of_fragments;

	public String[] getPost_tags() {
		return post_tags;
	}

	public void setPost_tags(String[] post_tags) {
		this.post_tags = post_tags;
	}

	public void setPost_tags(
		UnsafeSupplier<String[], Exception> post_tagsUnsafeSupplier) {

		try {
			post_tags = post_tagsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] post_tags;

	public String[] getPre_tags() {
		return pre_tags;
	}

	public void setPre_tags(String[] pre_tags) {
		this.pre_tags = pre_tags;
	}

	public void setPre_tags(
		UnsafeSupplier<String[], Exception> pre_tagsUnsafeSupplier) {

		try {
			pre_tags = pre_tagsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] pre_tags;

	public Boolean getRequire_field_match() {
		return require_field_match;
	}

	public void setRequire_field_match(Boolean require_field_match) {
		this.require_field_match = require_field_match;
	}

	public void setRequire_field_match(
		UnsafeSupplier<Boolean, Exception> require_field_matchUnsafeSupplier) {

		try {
			require_field_match = require_field_matchUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean require_field_match;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setType(UnsafeSupplier<String, Exception> typeUnsafeSupplier) {
		try {
			type = typeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String type;

	@Override
	public HighlightConfiguration clone() throws CloneNotSupportedException {
		return (HighlightConfiguration)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof HighlightConfiguration)) {
			return false;
		}

		HighlightConfiguration highlightConfiguration =
			(HighlightConfiguration)object;

		return Objects.equals(toString(), highlightConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return HighlightConfigurationSerDes.toJSON(this);
	}

}