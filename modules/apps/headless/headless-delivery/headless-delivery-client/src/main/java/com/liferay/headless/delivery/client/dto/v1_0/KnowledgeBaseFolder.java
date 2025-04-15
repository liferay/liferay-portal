/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.KnowledgeBaseFolderSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class KnowledgeBaseFolder implements Cloneable, Serializable {

	public static KnowledgeBaseFolder toDTO(String json) {
		return KnowledgeBaseFolderSerDes.toDTO(json);
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

	public com.liferay.headless.delivery.client.custom.field.CustomField[]
		getCustomFields() {

		return customFields;
	}

	public void setCustomFields(
		com.liferay.headless.delivery.client.custom.field.CustomField[]
			customFields) {

		this.customFields = customFields;
	}

	public void setCustomFields(
		UnsafeSupplier
			<com.liferay.headless.delivery.client.custom.field.CustomField[],
			 Exception> customFieldsUnsafeSupplier) {

		try {
			customFields = customFieldsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected com.liferay.headless.delivery.client.custom.field.CustomField[]
		customFields;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		try {
			description = descriptionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String description;

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

	public Integer getNumberOfKnowledgeBaseArticles() {
		return numberOfKnowledgeBaseArticles;
	}

	public void setNumberOfKnowledgeBaseArticles(
		Integer numberOfKnowledgeBaseArticles) {

		this.numberOfKnowledgeBaseArticles = numberOfKnowledgeBaseArticles;
	}

	public void setNumberOfKnowledgeBaseArticles(
		UnsafeSupplier<Integer, Exception>
			numberOfKnowledgeBaseArticlesUnsafeSupplier) {

		try {
			numberOfKnowledgeBaseArticles =
				numberOfKnowledgeBaseArticlesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer numberOfKnowledgeBaseArticles;

	public Integer getNumberOfKnowledgeBaseFolders() {
		return numberOfKnowledgeBaseFolders;
	}

	public void setNumberOfKnowledgeBaseFolders(
		Integer numberOfKnowledgeBaseFolders) {

		this.numberOfKnowledgeBaseFolders = numberOfKnowledgeBaseFolders;
	}

	public void setNumberOfKnowledgeBaseFolders(
		UnsafeSupplier<Integer, Exception>
			numberOfKnowledgeBaseFoldersUnsafeSupplier) {

		try {
			numberOfKnowledgeBaseFolders =
				numberOfKnowledgeBaseFoldersUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer numberOfKnowledgeBaseFolders;

	public ParentKnowledgeBaseFolder getParentKnowledgeBaseFolder() {
		return parentKnowledgeBaseFolder;
	}

	public void setParentKnowledgeBaseFolder(
		ParentKnowledgeBaseFolder parentKnowledgeBaseFolder) {

		this.parentKnowledgeBaseFolder = parentKnowledgeBaseFolder;
	}

	public void setParentKnowledgeBaseFolder(
		UnsafeSupplier<ParentKnowledgeBaseFolder, Exception>
			parentKnowledgeBaseFolderUnsafeSupplier) {

		try {
			parentKnowledgeBaseFolder =
				parentKnowledgeBaseFolderUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ParentKnowledgeBaseFolder parentKnowledgeBaseFolder;

	public Long getParentKnowledgeBaseFolderId() {
		return parentKnowledgeBaseFolderId;
	}

	public void setParentKnowledgeBaseFolderId(
		Long parentKnowledgeBaseFolderId) {

		this.parentKnowledgeBaseFolderId = parentKnowledgeBaseFolderId;
	}

	public void setParentKnowledgeBaseFolderId(
		UnsafeSupplier<Long, Exception>
			parentKnowledgeBaseFolderIdUnsafeSupplier) {

		try {
			parentKnowledgeBaseFolderId =
				parentKnowledgeBaseFolderIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long parentKnowledgeBaseFolderId;

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public void setSiteId(
		UnsafeSupplier<Long, Exception> siteIdUnsafeSupplier) {

		try {
			siteId = siteIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long siteId;

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
	public KnowledgeBaseFolder clone() throws CloneNotSupportedException {
		return (KnowledgeBaseFolder)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof KnowledgeBaseFolder)) {
			return false;
		}

		KnowledgeBaseFolder knowledgeBaseFolder = (KnowledgeBaseFolder)object;

		return Objects.equals(toString(), knowledgeBaseFolder.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return KnowledgeBaseFolderSerDes.toJSON(this);
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