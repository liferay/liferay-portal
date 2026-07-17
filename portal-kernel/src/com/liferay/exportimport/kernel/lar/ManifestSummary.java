/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.lar;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LongWrapper;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Máté Thurzó
 * @author Zsolt Berentey
 * @author Daniel Kocsis
 */
public class ManifestSummary implements Serializable {

	public static String getManifestSummaryKey(
		StagedModelType stagedModelType) {

		return getManifestSummaryKey(
			stagedModelType.getClassName(),
			stagedModelType.getReferrerClassName());
	}

	public void addAssetTitle(String className, String assetTitle) {
		if (Validator.isNotNull(assetTitle)) {
			_stagedModelAssetTitles.put(className, assetTitle);
		}
	}

	public void addDataPortlet(
		Portlet portlet, String[] configurationPortletOptions) {

		String rootPortletId = portlet.getRootPortletId();

		if (!_configurationPortletOptions.containsKey(rootPortletId)) {
			_dataPortlets.add(portlet);

			_configurationPortletOptions.put(
				rootPortletId, configurationPortletOptions);
		}
	}

	public void addLayoutPortlet(
		Portlet portlet, String[] configurationPortletOptions) {

		String rootPortletId = portlet.getRootPortletId();

		if (!_configurationPortletOptions.containsKey(rootPortletId)) {
			_layoutPortlets.add(portlet);

			_configurationPortletOptions.put(
				rootPortletId, configurationPortletOptions);
		}
	}

	public void addModelAdditionCount(
		StagedModelType stagedModelType, long count) {

		addModelAdditionCount(getManifestSummaryKey(stagedModelType), count);
	}

	public void addModelAdditionCount(String manifestSummaryKey, long count) {
		LongWrapper modelAdditionCounter = _modelAdditionCounters.get(
			manifestSummaryKey);

		if (modelAdditionCounter == null) {
			modelAdditionCounter = new LongWrapper();

			_modelAdditionCounters.put(
				manifestSummaryKey, modelAdditionCounter);
		}

		modelAdditionCounter.setValue(count);

		_manifestSummaryKeys.add(manifestSummaryKey);
	}

	public void addModelDeletionCount(
		StagedModelType stagedModelType, long count) {

		addModelDeletionCount(getManifestSummaryKey(stagedModelType), count);
	}

	public void addModelDeletionCount(String manifestSummaryKey, long count) {
		LongWrapper modelDeletionCounter = _modelDeletionCounters.get(
			manifestSummaryKey);

		if (modelDeletionCounter == null) {
			modelDeletionCounter = new LongWrapper();

			_modelDeletionCounters.put(
				manifestSummaryKey, modelDeletionCounter);
		}

		modelDeletionCounter.setValue(count);

		_manifestSummaryKeys.add(manifestSummaryKey);
	}

	@Override
	public Object clone() {
		ManifestSummary manifestSummary = new ManifestSummary();

		manifestSummary._configurationPortletOptions = new HashMap<>(
			manifestSummary._configurationPortletOptions);
		manifestSummary._dataPortlets = new ArrayList<>(_dataPortlets);
		manifestSummary._layoutPortlets = new ArrayList<>(_layoutPortlets);

		if (_exportDate != null) {
			manifestSummary.setExportDate(new Date(_exportDate.getTime()));
		}

		manifestSummary._manifestSummaryKeys = new HashSet<>(
			_manifestSummaryKeys);
		manifestSummary._modelAdditionCounters = new HashMap<>(
			_modelAdditionCounters);
		manifestSummary._modelDeletionCounters = new HashMap<>(
			_modelDeletionCounters);
		manifestSummary._stagedModelAssetTitles = new HashMap<>(
			_stagedModelAssetTitles);

		return manifestSummary;
	}

	public long getAllModelDeletionCounts() {
		long modelDeletionCount = -1;

		for (String manifestSummaryKey : _manifestSummaryKeys) {
			long manifestSummaryKeyModelDeletionCount = getModelDeletionCount(
				manifestSummaryKey);

			if (manifestSummaryKeyModelDeletionCount == -1) {
				continue;
			}

			if (modelDeletionCount == -1) {
				modelDeletionCount = manifestSummaryKeyModelDeletionCount;
			}
			else {
				modelDeletionCount += manifestSummaryKeyModelDeletionCount;
			}
		}

		return modelDeletionCount;
	}

	public String[] getConfigurationPortletOptions(String rootPortletId) {
		return _configurationPortletOptions.get(rootPortletId);
	}

	public List<Portlet> getDataPortlets() {
		return _dataPortlets;
	}

	public Date getExportDate() {
		return _exportDate;
	}

	public List<Portlet> getLayoutPortlets() {
		return _layoutPortlets;
	}

	public Collection<String> getManifestSummaryKeys() {
		return _manifestSummaryKeys;
	}

	public long getModelAdditionCount(StagedModel stagedModel) {
		return getModelAdditionCount(stagedModel.getStagedModelType());
	}

	public long getModelAdditionCount(StagedModelType stagedModelType) {
		return getModelAdditionCount(
			stagedModelType.getClassName(),
			stagedModelType.getReferrerClassName());
	}

	public long getModelAdditionCount(String manifestSummaryKey) {
		LongWrapper modelAdditionCounter = _modelAdditionCounters.get(
			manifestSummaryKey);

		if (modelAdditionCounter == null) {
			return -1;
		}

		return modelAdditionCounter.getValue();
	}

	public Map<String, LongWrapper> getModelAdditionCounters() {
		return _modelAdditionCounters;
	}

	public long getModelDeletionCount(StagedModel stagedModel) {
		return getModelDeletionCount(stagedModel.getStagedModelType());
	}

	public long getModelDeletionCount(StagedModelType stagedModelType) {
		return getModelDeletionCount(
			stagedModelType.getClassName(),
			stagedModelType.getReferrerClassName());
	}

	public long getModelDeletionCount(StagedModelType[] stagedModelTypes) {
		if (ArrayUtil.isEmpty(stagedModelTypes)) {
			return 0;
		}

		long modelDeletionCount = -1;

		for (StagedModelType stagedModelType : stagedModelTypes) {
			long stagedModelTypeModelDeletionCount = getModelDeletionCount(
				stagedModelType);

			if (stagedModelTypeModelDeletionCount == -1) {
				continue;
			}

			if (modelDeletionCount == -1) {
				modelDeletionCount = stagedModelTypeModelDeletionCount;
			}
			else {
				modelDeletionCount += stagedModelTypeModelDeletionCount;
			}
		}

		return modelDeletionCount;
	}

	public long getModelDeletionCount(String manifestSummaryKey) {
		LongWrapper modelDeletionCounter = _modelDeletionCounters.get(
			manifestSummaryKey);

		if (modelDeletionCounter == null) {
			return -1;
		}

		return modelDeletionCounter.getValue();
	}

	public Map<String, LongWrapper> getModelDeletionCounters() {
		return _modelDeletionCounters;
	}

	public String getStagedModelAssetTitle(String manifestSummaryKey) {
		String stagedModelAssetTitle = _stagedModelAssetTitles.get(
			manifestSummaryKey);

		if (stagedModelAssetTitle == null) {
			return StringPool.BLANK;
		}

		return stagedModelAssetTitle;
	}

	public Map<String, String> getStagedModelAssetTitles() {
		return _stagedModelAssetTitles;
	}

	public void incrementModelAdditionCount(StagedModelType stagedModelType) {
		String manifestSummaryKey = getManifestSummaryKey(stagedModelType);

		LongWrapper modelAdditionCounter = _modelAdditionCounters.get(
			manifestSummaryKey);

		if (modelAdditionCounter == null) {
			_modelAdditionCounters.put(manifestSummaryKey, new LongWrapper(1));

			_manifestSummaryKeys.add(manifestSummaryKey);

			return;
		}

		modelAdditionCounter.increment();
	}

	public void incrementModelDeletionCount(StagedModelType stagedModelType) {
		String manifestSummaryKey = getManifestSummaryKey(stagedModelType);

		LongWrapper modelDeletionCounter = _modelDeletionCounters.get(
			manifestSummaryKey);

		if (modelDeletionCounter == null) {
			_modelDeletionCounters.put(manifestSummaryKey, new LongWrapper(1));

			_manifestSummaryKeys.add(manifestSummaryKey);

			return;
		}

		modelDeletionCounter.increment();
	}

	public void resetCounters() {
		_modelAdditionCounters.clear();
		_modelDeletionCounters.clear();

		_manifestSummaryKeys.clear();
	}

	public void setExportDate(Date exportDate) {
		_exportDate = exportDate;
	}

	@Override
	public String toString() {
		return StringBundler.concat(
			"{modelAdditionCounters=", MapUtil.toString(_modelAdditionCounters),
			", modelDeletionCounters=",
			MapUtil.toString(_modelDeletionCounters), "}");
	}

	protected static String getManifestSummaryKey(
		String modelName, String referrerModelName) {

		if (Validator.isNull(referrerModelName)) {
			return modelName;
		}

		return StringBundler.concat(
			modelName, StringPool.POUND, referrerModelName);
	}

	protected long getModelAdditionCount(
		String className, String referrerClassName) {

		if (Validator.isNull(referrerClassName) ||
			(!referrerClassName.equals(
				StagedModelType.REFERRER_CLASS_NAME_ALL) &&
			 !referrerClassName.equals(
				 StagedModelType.REFERRER_CLASS_NAME_ANY))) {

			return getModelAdditionCount(
				getManifestSummaryKey(className, referrerClassName));
		}

		long modelAdditionCount = -1;

		for (String key : _modelAdditionCounters.keySet()) {
			if (!key.startsWith(className.concat(StringPool.POUND)) &&
				(!key.equals(className) ||
				 !referrerClassName.equals(
					 StagedModelType.REFERRER_CLASS_NAME_ALL))) {

				continue;
			}

			long count = getModelAdditionCount(key);

			if (count >= 0) {
				if (modelAdditionCount < 0) {
					modelAdditionCount = count;
				}
				else {
					modelAdditionCount += count;
				}
			}
		}

		return modelAdditionCount;
	}

	protected long getModelDeletionCount(
		String className, String referrerClassName) {

		if (Validator.isNull(referrerClassName) ||
			(!referrerClassName.equals(
				StagedModelType.REFERRER_CLASS_NAME_ALL) &&
			 !referrerClassName.equals(
				 StagedModelType.REFERRER_CLASS_NAME_ANY))) {

			return getModelDeletionCount(
				getManifestSummaryKey(className, referrerClassName));
		}

		long modelDeletionCount = -1;

		for (String key : _modelDeletionCounters.keySet()) {
			if (!key.startsWith(className.concat(StringPool.POUND)) &&
				(!key.equals(className) ||
				 !referrerClassName.equals(
					 StagedModelType.REFERRER_CLASS_NAME_ALL))) {

				continue;
			}

			long count = getModelDeletionCount(key);

			if (count >= 0) {
				if (modelDeletionCount < 0) {
					modelDeletionCount = count;
				}
				else {
					modelDeletionCount += count;
				}
			}
		}

		return modelDeletionCount;
	}

	private Map<String, String[]> _configurationPortletOptions =
		new HashMap<>();
	private List<Portlet> _dataPortlets = new ArrayList<>();
	private Date _exportDate;
	private List<Portlet> _layoutPortlets = new ArrayList<>();
	private Set<String> _manifestSummaryKeys = new HashSet<>();
	private Map<String, LongWrapper> _modelAdditionCounters = new HashMap<>();
	private Map<String, LongWrapper> _modelDeletionCounters = new HashMap<>();
	private Map<String, String> _stagedModelAssetTitles = new HashMap<>();

}