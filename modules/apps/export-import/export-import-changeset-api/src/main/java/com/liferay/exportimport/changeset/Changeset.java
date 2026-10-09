/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.changeset;

import com.liferay.exportimport.kernel.lar.ExportImportClassedModelUtil;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Máté Thurzó
 */
public class Changeset implements Serializable {

	public static Builder create() {
		return new Builder(new Changeset());
	}

	public static RawBuilder createRaw() {
		return new RawBuilder(new Changeset());
	}

	public static RawBuilder createRaw(String uuid) {
		return new RawBuilder(new Changeset(uuid));
	}

	public List<StagedModel> getStagedModels() {
		return _stagedModels;
	}

	public String getUuid() {
		return _uuid;
	}

	public static class Builder {

		public Builder(Changeset changeset) {
			_changeset = changeset;
		}

		public Builder addModel(
			Supplier<ClassedModel> supplier,
			Function<ClassedModel, StagedModel> adapterFunction) {

			_changeset._stagedModels.add(adapterFunction.apply(supplier.get()));

			return this;
		}

		public Builder addMultipleStagedModel(
			Supplier<Collection<? extends StagedModel>> supplier) {

			Collection<? extends StagedModel> stagedModels = supplier.get();

			stagedModels.forEach(
				stagedModel -> {
					if (stagedModel != null) {
						_changeset._stagedModels.add(stagedModel);
					}
				});

			return this;
		}

		public Builder addStagedModel(Supplier<StagedModel> supplier) {
			_changeset._stagedModels.add(supplier.get());

			return this;
		}

		@SuppressWarnings("unchecked")
		public <T extends StagedModel> Builder addStagedModelHierarchy(
			Supplier<T> supplier,
			Function<T, Collection<?>> hierarchyFunction) {

			Function<StagedModel, Collection<?>> function =
				(Function<StagedModel, Collection<?>>)hierarchyFunction;

			StagedModel stagedModel = supplier.get();

			_collectChildrenStagedModels(
				_changeset._stagedModels, stagedModel, function);

			return this;
		}

		public Changeset build() {
			return _changeset;
		}

		private final Changeset _changeset;

	}

	public static class RawBuilder {

		public RawBuilder(Changeset changeset) {
			_changeset = changeset;
		}

		public RawBuilder addMultipleStagedModel(
			Collection<? extends StagedModel> stagedModels) {

			stagedModels.forEach(
				stagedModel -> {
					if (stagedModel != null) {
						_changeset._stagedModels.add(stagedModel);
					}
				});

			return this;
		}

		public RawBuilder addStagedModel(StagedModel stagedModel) {
			_changeset._stagedModels.add(stagedModel);

			return this;
		}

		public Changeset build() {
			return _changeset;
		}

		private final Changeset _changeset;

	}

	private static void _collectChildrenStagedModels(
		List<StagedModel> childrenStagedModels, StagedModel parentStagedModel,
		Function<StagedModel, Collection<?>> hierarchyFunction) {

		String parentClassName = ExportImportClassedModelUtil.getClassName(
			parentStagedModel);

		Queue<StagedModel> queue = new LinkedList<>();

		queue.add(parentStagedModel);

		StagedModel stagedModel = null;

		while ((stagedModel = queue.poll()) != null) {
			String stagedModelClassName = stagedModel.getModelClassName();

			if (!stagedModelClassName.equals(parentClassName)) {
				continue;
			}

			for (Object object : hierarchyFunction.apply(stagedModel)) {
				StagedModel childStagedModel = (StagedModel)object;

				childrenStagedModels.add(childStagedModel);

				queue.add(childStagedModel);
			}
		}
	}

	private Changeset() {
		this(PortalUUIDUtil.generate());
	}

	private Changeset(String uuid) {
		_uuid = uuid;
	}

	private final List<StagedModel> _stagedModels = new ArrayList<>();
	private final String _uuid;

}