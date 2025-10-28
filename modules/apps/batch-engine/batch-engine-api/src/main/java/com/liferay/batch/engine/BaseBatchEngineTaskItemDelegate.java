/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine;

import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.odata.entity.EntityModel;

import jakarta.ws.rs.core.UriInfo;

import java.io.Serializable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Ivica Cardic
 * @author Igor Beslic
 */
public abstract class BaseBatchEngineTaskItemDelegate<T>
	implements BatchEngineTaskItemDelegate<T> {

	@Override
	public void create(
			Collection<T> items, Map<String, Serializable> parameters)
		throws Exception {

		for (T currentItem : items) {
			importItemUnsafeBiConsumer.accept(
				currentItem, item -> createItem(item, parameters));
		}
	}

	public T createItem(T item, Map<String, Serializable> parameters)
		throws Exception {

		return null;
	}

	@Override
	public void delete(
			Collection<T> items, Map<String, Serializable> parameters)
		throws Exception {

		for (T currentItem : items) {
			importItemUnsafeBiConsumer.accept(
				currentItem,
				item -> {
					deleteItem(item, parameters);

					return item;
				});
		}
	}

	public void deleteItem(T item, Map<String, Serializable> parameters)
		throws Exception {
	}

	@Override
	public Set<String> getAvailableCreateStrategies() {
		return _availableCreateStrategies;
	}

	@Override
	public Set<String> getAvailableUpdateStrategies() {
		return _availableUpdateStrategies;
	}

	@Override
	public EntityModel getEntityModel(Map<String, List<String>> multivaluedMap)
		throws Exception {

		return null;
	}

	@Override
	public boolean hasCreateStrategy(String createStrategy) {
		return _availableCreateStrategies.contains(createStrategy);
	}

	@Override
	public boolean hasUpdateStrategy(String updateStrategy) {
		return _availableUpdateStrategies.contains(updateStrategy);
	}

	@Override
	public void setContextCompany(Company contextCompany) {
		this.contextCompany = contextCompany;
	}

	@Override
	public void setContextUriInfo(UriInfo uriInfo) {
		this.uriInfo = uriInfo;
	}

	@Override
	public void setContextUser(User contextUser) {
		this.contextUser = contextUser;
	}

	@Override
	public void setImportItemUnsafeBiConsumer(
		UnsafeBiConsumer<T, UnsafeFunction<T, T, Exception>, Exception>
			unsafeBiConsumer) {

		importItemUnsafeBiConsumer = unsafeBiConsumer;
	}

	@Override
	public void setLanguageId(String languageId) {
		this.languageId = languageId;
	}

	@Override
	public void update(
			Collection<T> items, Map<String, Serializable> parameters)
		throws Exception {

		for (T item : items) {
			updateItem(item, parameters);
		}
	}

	public void updateItem(T item, Map<String, Serializable> parameters)
		throws Exception {
	}

	protected Company contextCompany;
	protected User contextUser;
	protected UnsafeBiConsumer<T, UnsafeFunction<T, T, Exception>, Exception>
		importItemUnsafeBiConsumer;
	protected String languageId;
	protected UriInfo uriInfo;

	private final Set<String> _availableCreateStrategies =
		Collections.unmodifiableSet(SetUtil.fromArray("INSERT"));
	private final Set<String> _availableUpdateStrategies =
		Collections.unmodifiableSet(SetUtil.fromArray("UPDATE"));

}