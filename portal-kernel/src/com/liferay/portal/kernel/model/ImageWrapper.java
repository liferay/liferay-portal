/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * <p>
 * This class is a wrapper for {@link Image}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see Image
 * @generated
 */
public class ImageWrapper
	extends BaseModelWrapper<Image> implements Image, ModelWrapper<Image> {

	public ImageWrapper(Image image) {
		super(image);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("imageId", getImageId());
		attributes.put("companyId", getCompanyId());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("type", getType());
		attributes.put("height", getHeight());
		attributes.put("width", getWidth());
		attributes.put("size", getSize());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long ctCollectionId = (Long)attributes.get("ctCollectionId");

		if (ctCollectionId != null) {
			setCtCollectionId(ctCollectionId);
		}

		Long imageId = (Long)attributes.get("imageId");

		if (imageId != null) {
			setImageId(imageId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		String type = (String)attributes.get("type");

		if (type != null) {
			setType(type);
		}

		Integer height = (Integer)attributes.get("height");

		if (height != null) {
			setHeight(height);
		}

		Integer width = (Integer)attributes.get("width");

		if (width != null) {
			setWidth(width);
		}

		Integer size = (Integer)attributes.get("size");

		if (size != null) {
			setSize(size);
		}
	}

	@Override
	public Image cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this image.
	 *
	 * @return the company ID of this image
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the ct collection ID of this image.
	 *
	 * @return the ct collection ID of this image
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	/**
	 * Returns the height of this image.
	 *
	 * @return the height of this image
	 */
	@Override
	public int getHeight() {
		return model.getHeight();
	}

	/**
	 * Returns the image ID of this image.
	 *
	 * @return the image ID of this image
	 */
	@Override
	public long getImageId() {
		return model.getImageId();
	}

	/**
	 * Returns the modified date of this image.
	 *
	 * @return the modified date of this image
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this image.
	 *
	 * @return the mvcc version of this image
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the primary key of this image.
	 *
	 * @return the primary key of this image
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the size of this image.
	 *
	 * @return the size of this image
	 */
	@Override
	public int getSize() {
		return model.getSize();
	}

	@Override
	public byte[] getTextObj() {
		return model.getTextObj();
	}

	/**
	 * Returns the type of this image.
	 *
	 * @return the type of this image
	 */
	@Override
	public String getType() {
		return model.getType();
	}

	/**
	 * Returns the width of this image.
	 *
	 * @return the width of this image
	 */
	@Override
	public int getWidth() {
		return model.getWidth();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the company ID of this image.
	 *
	 * @param companyId the company ID of this image
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the ct collection ID of this image.
	 *
	 * @param ctCollectionId the ct collection ID of this image
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the height of this image.
	 *
	 * @param height the height of this image
	 */
	@Override
	public void setHeight(int height) {
		model.setHeight(height);
	}

	/**
	 * Sets the image ID of this image.
	 *
	 * @param imageId the image ID of this image
	 */
	@Override
	public void setImageId(long imageId) {
		model.setImageId(imageId);
	}

	/**
	 * Sets the modified date of this image.
	 *
	 * @param modifiedDate the modified date of this image
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this image.
	 *
	 * @param mvccVersion the mvcc version of this image
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the primary key of this image.
	 *
	 * @param primaryKey the primary key of this image
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the size of this image.
	 *
	 * @param size the size of this image
	 */
	@Override
	public void setSize(int size) {
		model.setSize(size);
	}

	@Override
	public void setTextObj(byte[] textObj) {
		model.setTextObj(textObj);
	}

	/**
	 * Sets the type of this image.
	 *
	 * @param type the type of this image
	 */
	@Override
	public void setType(String type) {
		model.setType(type);
	}

	/**
	 * Sets the width of this image.
	 *
	 * @param width the width of this image
	 */
	@Override
	public void setWidth(int width) {
		model.setWidth(width);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	public Map<String, Function<Image, Object>> getAttributeGetterFunctions() {
		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<Image, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	protected ImageWrapper wrap(Image image) {
		return new ImageWrapper(image);
	}

}