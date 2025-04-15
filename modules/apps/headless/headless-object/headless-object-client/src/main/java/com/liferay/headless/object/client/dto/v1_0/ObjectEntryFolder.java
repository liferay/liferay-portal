/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.object.client.dto.v1_0;

import com.liferay.headless.object.client.function.UnsafeSupplier;
import com.liferay.headless.object.client.serdes.v1_0.ObjectEntryFolderSerDes;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Alicia García
 * @generated
 */
@Generated("")
public class ObjectEntryFolder implements Cloneable, Serializable {

	public static ObjectEntryFolder toDTO(String json) {
		return ObjectEntryFolderSerDes.toDTO(json);
	}

	public Map<String, Map<String, String>> getActions() {
		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;
	}

	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		try {
			actions = actionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Map<String, String>> actions;

	public Creator getCreator() {
		return creator;
	}

	public void setCreator(Creator creator) {
		this.creator = creator;
	}

	public void setCreator(
		UnsafeSupplier<Creator, Exception> creatorUnsafeSupplier) {

		try {
			creator = creatorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Creator creator;

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public void setDateCreated(
		UnsafeSupplier<Date, Exception> dateCreatedUnsafeSupplier) {

		try {
			dateCreated = dateCreatedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateCreated;

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public void setDateModified(
		UnsafeSupplier<Date, Exception> dateModifiedUnsafeSupplier) {

		try {
			dateModified = dateModifiedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateModified;

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setLabel(
		UnsafeSupplier<String, Exception> labelUnsafeSupplier) {

		try {
			label = labelUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String label;

	public Map<String, String> getLabel_i18n() {
		return label_i18n;
	}

	public void setLabel_i18n(Map<String, String> label_i18n) {
		this.label_i18n = label_i18n;
	}

	public void setLabel_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			label_i18nUnsafeSupplier) {

		try {
			label_i18n = label_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> label_i18n;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String name;

	public Integer getNumberOfObjectEntries() {
		return numberOfObjectEntries;
	}

	public void setNumberOfObjectEntries(Integer numberOfObjectEntries) {
		this.numberOfObjectEntries = numberOfObjectEntries;
	}

	public void setNumberOfObjectEntries(
		UnsafeSupplier<Integer, Exception>
			numberOfObjectEntriesUnsafeSupplier) {

		try {
			numberOfObjectEntries = numberOfObjectEntriesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer numberOfObjectEntries;

	public Integer getNumberOfObjectEntryFolders() {
		return numberOfObjectEntryFolders;
	}

	public void setNumberOfObjectEntryFolders(
		Integer numberOfObjectEntryFolders) {

		this.numberOfObjectEntryFolders = numberOfObjectEntryFolders;
	}

	public void setNumberOfObjectEntryFolders(
		UnsafeSupplier<Integer, Exception>
			numberOfObjectEntryFoldersUnsafeSupplier) {

		try {
			numberOfObjectEntryFolders =
				numberOfObjectEntryFoldersUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer numberOfObjectEntryFolders;

	public ParentObjectEntryFolderBrief getParentObjectEntryFolderBrief() {
		return parentObjectEntryFolderBrief;
	}

	public void setParentObjectEntryFolderBrief(
		ParentObjectEntryFolderBrief parentObjectEntryFolderBrief) {

		this.parentObjectEntryFolderBrief = parentObjectEntryFolderBrief;
	}

	public void setParentObjectEntryFolderBrief(
		UnsafeSupplier<ParentObjectEntryFolderBrief, Exception>
			parentObjectEntryFolderBriefUnsafeSupplier) {

		try {
			parentObjectEntryFolderBrief =
				parentObjectEntryFolderBriefUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ParentObjectEntryFolderBrief parentObjectEntryFolderBrief;

	public String getParentObjectEntryFolderExternalReferenceCode() {
		return parentObjectEntryFolderExternalReferenceCode;
	}

	public void setParentObjectEntryFolderExternalReferenceCode(
		String parentObjectEntryFolderExternalReferenceCode) {

		this.parentObjectEntryFolderExternalReferenceCode =
			parentObjectEntryFolderExternalReferenceCode;
	}

	public void setParentObjectEntryFolderExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			parentObjectEntryFolderExternalReferenceCodeUnsafeSupplier) {

		try {
			parentObjectEntryFolderExternalReferenceCode =
				parentObjectEntryFolderExternalReferenceCodeUnsafeSupplier.
					get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String parentObjectEntryFolderExternalReferenceCode;

	public Long getParentObjectEntryFolderId() {
		return parentObjectEntryFolderId;
	}

	public void setParentObjectEntryFolderId(Long parentObjectEntryFolderId) {
		this.parentObjectEntryFolderId = parentObjectEntryFolderId;
	}

	public void setParentObjectEntryFolderId(
		UnsafeSupplier<Long, Exception>
			parentObjectEntryFolderIdUnsafeSupplier) {

		try {
			parentObjectEntryFolderId =
				parentObjectEntryFolderIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long parentObjectEntryFolderId;

	public String getScopeKey() {
		return scopeKey;
	}

	public void setScopeKey(String scopeKey) {
		this.scopeKey = scopeKey;
	}

	public void setScopeKey(
		UnsafeSupplier<String, Exception> scopeKeyUnsafeSupplier) {

		try {
			scopeKey = scopeKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String scopeKey;

	public ViewableBy getViewableBy() {
		return viewableBy;
	}

	public String getViewableByAsString() {
		if (viewableBy == null) {
			return null;
		}

		return viewableBy.toString();
	}

	public void setViewableBy(ViewableBy viewableBy) {
		this.viewableBy = viewableBy;
	}

	public void setViewableBy(
		UnsafeSupplier<ViewableBy, Exception> viewableByUnsafeSupplier) {

		try {
			viewableBy = viewableByUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ViewableBy viewableBy;

	@Override
	public ObjectEntryFolder clone() throws CloneNotSupportedException {
		return (ObjectEntryFolder)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectEntryFolder)) {
			return false;
		}

		ObjectEntryFolder objectEntryFolder = (ObjectEntryFolder)object;

		return Objects.equals(toString(), objectEntryFolder.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ObjectEntryFolderSerDes.toJSON(this);
	}

	public static enum ViewableBy {

		ANYONE("Anyone"), MEMBERS("Members"), OWNER("Owner");

		public static ViewableBy create(String value) {
			for (ViewableBy viewableBy : values()) {
				if (Objects.equals(viewableBy.getValue(), value) ||
					Objects.equals(viewableBy.name(), value)) {

					return viewableBy;
				}
			}

			return null;
		}

		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private ViewableBy(String value) {
			_value = value;
		}

		private final String _value;

	}

}