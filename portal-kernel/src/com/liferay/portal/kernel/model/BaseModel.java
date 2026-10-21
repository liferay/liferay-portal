/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.template.TemplateRestricted;

import java.io.Serializable;

import java.util.Collections;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * The base interface for all model classes. This interface should never need to
 * be used directly.
 *
 * @author Brian Wing Shun Chan
 * @see    com.liferay.portal.kernel.model.impl.BaseModelImpl
 */
public interface BaseModel<T>
	extends ClassedModel, Cloneable, Comparable<T>, Serializable {

	/**
	 * Creates a shallow clone of this model instance.
	 *
	 * @return the shallow clone of this model instance
	 */
	public Object clone();

	public T cloneWithOriginalValues();

	public default void copyCacheFields(T source) {
	}

	@TemplateRestricted
	public default Map<String, Function<T, Object>>
		getAttributeGetterFunctions() {

		return Collections.emptyMap();
	}

	public default Map<String, BiConsumer<T, Object>>
		getAttributeSetterBiConsumers() {

		return Collections.emptyMap();
	}

	/**
	 * Returns the expando bridge for this model instance.
	 *
	 * @return the expando bridge for this model instance
	 */
	@Override
	public ExpandoBridge getExpandoBridge();

	@TemplateRestricted
	public Map<String, Object> getModelAttributes();

	/**
	 * Returns the primary key of this model instance.
	 *
	 * @return the primary key of this model instance
	 */
	@Override
	public Serializable getPrimaryKeyObj();

	/**
	 * Returns <code>true</code> if this model instance was retrieved from the
	 * entity cache.
	 *
	 * @return <code>true</code> if this model instance was retrieved from the
	 *         entity cache; <code>false</code> otherwise
	 * @see    #setCachedModel(boolean)
	 */
	public boolean isCachedModel();

	/**
	 * Returns <code>true</code> if this model's entity cache is enabled.
	 *
	 * @return     <code>true</code> if this model's entity cache is enabled;
	 *             <code>false</code> otherwise
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public boolean isEntityCacheEnabled();

	/**
	 * Returns <code>true</code> if this model instance is escaped.
	 *
	 * @return <code>true</code> if this model instance is escaped;
	 *         <code>false</code> otherwise
	 */
	public boolean isEscapedModel();

	/**
	 * Returns <code>true</code> if this model's finder cache is enabled.
	 *
	 * @return     <code>true</code> if this model's finder cache is enabled;
	 *             <code>false</code> otherwise
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public boolean isFinderCacheEnabled();

	/**
	 * Returns <code>true</code> if this model instance does not yet exist in
	 * the database.
	 *
	 * @return <code>true</code> if this model instance does not yet exist in
	 *         the database; <code>false</code> otherwise
	 */
	public boolean isNew();

	/**
	 * Reset all original fields to current values.
	 */
	@TemplateRestricted
	public void resetOriginalValues();

	/**
	 * Sets whether this model instance was retrieved from the entity cache.
	 *
	 * @param cachedModel whether this model instance was retrieved from the
	 *        entity cache
	 * @see   com.liferay.portal.kernel.dao.orm.EntityCache
	 */
	@TemplateRestricted
	public void setCachedModel(boolean cachedModel);

	@TemplateRestricted
	public void setExpandoBridgeAttributes(BaseModel<?> baseModel);

	@TemplateRestricted
	public void setExpandoBridgeAttributes(ExpandoBridge expandoBridge);

	/**
	 * Sets the expando bridge attributes for this model instance to the
	 * attributes stored in the service context.
	 *
	 * @param serviceContext the service context to be applied
	 * @see   ServiceContext#getExpandoBridgeAttributes()
	 */
	@TemplateRestricted
	public void setExpandoBridgeAttributes(ServiceContext serviceContext);

	@TemplateRestricted
	public void setModelAttributes(Map<String, Object> attributes);

	/**
	 * Sets whether this model instance does not yet exist in the database.
	 *
	 * @param n whether this model instance does not yet exist in the database
	 */
	@TemplateRestricted
	public void setNew(boolean n);

	/**
	 * Sets the primary key of this model instance.
	 *
	 * @param primaryKeyObj the primary key of this model instance
	 */
	@Override
	@TemplateRestricted
	public void setPrimaryKeyObj(Serializable primaryKeyObj);

	/**
	 * Returns a cache model object for this entity used by entity cache.
	 *
	 * @return the cache model object
	 */
	@TemplateRestricted
	public CacheModel<T> toCacheModel();

	/**
	 * Returns a copy of this entity as an escaped model instance by wrapping it
	 * with an {@link com.liferay.portal.kernel.bean.AutoEscapeBeanHandler}.
	 *
	 * @return the escaped model instance
	 * @see    com.liferay.portal.kernel.bean.AutoEscapeBeanHandler
	 */
	public T toEscapedModel();

	public T toUnescapedModel();

}