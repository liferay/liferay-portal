/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.social.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.DocumentType;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;
import com.liferay.portal.util.JavaFieldsParser;
import com.liferay.social.kernel.model.SocialAchievement;
import com.liferay.social.kernel.model.SocialActivityCounterConstants;
import com.liferay.social.kernel.model.SocialActivityCounterDefinition;
import com.liferay.social.kernel.model.SocialActivityDefinition;
import com.liferay.social.kernel.model.SocialActivityProcessor;
import com.liferay.social.kernel.util.SocialConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Zsolt Berentey
 */
public class SocialConfigurationImpl implements SocialConfiguration {

	@Override
	public List<String> getActivityCounterNames() {
		return getActivityCounterNames(
			SocialActivityCounterConstants.TYPE_ALL, false);
	}

	@Override
	public List<String> getActivityCounterNames(boolean transientCounter) {
		return getActivityCounterNames(
			SocialActivityCounterConstants.TYPE_ALL, transientCounter);
	}

	@Override
	public List<String> getActivityCounterNames(int ownerType) {
		return getActivityCounterNames(ownerType, false);
	}

	@Override
	public List<String> getActivityCounterNames(
		int ownerType, boolean transientCounter) {

		Set<String> activityCounterNames = new LinkedHashSet<>();

		for (Map<Integer, SocialActivityDefinition> activityDefinitions :
				_activityDefinitions.values()) {

			for (SocialActivityDefinition activityDefinition :
					activityDefinitions.values()) {

				for (SocialActivityCounterDefinition activityCounterDefinition :
						activityDefinition.getActivityCounterDefinitions()) {

					if ((activityCounterDefinition.isTransient() !=
							transientCounter) ||
						((ownerType !=
							SocialActivityCounterConstants.TYPE_ALL) &&
						 (ownerType !=
							 activityCounterDefinition.getOwnerType()))) {

						continue;
					}

					activityCounterNames.add(
						activityCounterDefinition.getName());
				}
			}
		}

		return new ArrayList<>(activityCounterNames);
	}

	@Override
	public SocialActivityDefinition getActivityDefinition(
		String modelName, int activityType) {

		Map<Integer, SocialActivityDefinition> activityDefinitions =
			_activityDefinitions.get(modelName);

		if (activityDefinitions == null) {
			return null;
		}

		return activityDefinitions.get(activityType);
	}

	@Override
	public List<SocialActivityDefinition> getActivityDefinitions(
		String modelName) {

		Map<Integer, SocialActivityDefinition> activityDefinitions =
			_activityDefinitions.get(modelName);

		if (activityDefinitions == null) {
			return Collections.emptyList();
		}

		return ListUtil.fromCollection(activityDefinitions.values());
	}

	@Override
	public String[] getActivityModelNames() {
		Set<String> activityModelNames = _activityDefinitions.keySet();

		return activityModelNames.toArray(new String[0]);
	}

	@Override
	public List<Object> read(ClassLoader classLoader, String[] xmls)
		throws Exception {

		List<Object> objects = new ArrayList<>();

		for (String xml : xmls) {
			_read(classLoader, xml, objects);
		}

		return objects;
	}

	@Override
	public void removeActivityDefinition(
		SocialActivityDefinition activityDefinition) {

		Map<Integer, SocialActivityDefinition> activityDefinitions =
			_activityDefinitions.get(activityDefinition.getModelName());

		if (activityDefinitions != null) {
			activityDefinitions.remove(activityDefinition.getActivityType());
		}
	}

	private void _read(
			ClassLoader classLoader, String xml, List<Object> objects)
		throws Exception {

		if (xml == null) {
			return;
		}

		xml = JavaFieldsParser.parse(classLoader, xml);

		Document document = UnsecureSAXReaderUtil.read(xml);

		DocumentType documentType = document.getDocumentType();

		String publicId = documentType.getPublicId();

		if (!publicId.equals("-//Liferay//DTD Social 6.1.0//EN") &&
			!publicId.equals("-//Liferay//DTD Social 6.2.0//EN") &&
			!publicId.equals("-//Liferay//DTD Social 7.0.0//EN") &&
			!publicId.equals("-//Liferay//DTD Social 7.1.0//EN") &&
			!publicId.equals("-//Liferay//DTD Social 7.2.0//EN") &&
			!publicId.equals("-//Liferay//DTD Social 7.3.0//EN") &&
			!publicId.equals("-//Liferay//DTD Social 7.4.0//EN")) {

			throw new DocumentException(
				"Unsupported document type " + publicId);
		}

		Element rootElement = document.getRootElement();

		List<Element> activityElements = rootElement.elements("activity");

		for (Element activityElement : activityElements) {
			_readActivity(classLoader, objects, activityElement);
		}
	}

	private void _readAchievement(
			ClassLoader classLoader, List<Object> objects,
			SocialActivityDefinition activityDefinition,
			Element achievementElement)
		throws Exception {

		String achievementClassName = GetterUtil.getString(
			achievementElement.elementText("achievement-class"));

		SocialAchievement achievement =
			(SocialAchievement)ProxyFactory.newInstance(
				classLoader, SocialAchievement.class, achievementClassName);

		achievement.setName(
			GetterUtil.getString(achievementElement.elementText("name")));
		achievement.setIcon(
			GetterUtil.getString(achievementElement.elementText("icon")));

		List<Element> propertyElements = achievementElement.elements(
			"property");

		for (Element propertyElement : propertyElements) {
			_readAchievementProperty(achievement, propertyElement);
		}

		achievement.initialize(activityDefinition);

		List<SocialAchievement> achievements =
			activityDefinition.getAchievements();

		achievements.add(achievement);

		Tuple tuple = new Tuple(activityDefinition, achievement);

		objects.add(tuple);
	}

	private void _readAchievementProperty(
		SocialAchievement achievement, Element propertyElement) {

		String name = GetterUtil.getString(propertyElement.elementText("name"));
		String value = GetterUtil.getString(
			propertyElement.elementText("value"));

		achievement.setProperty(name, value);
	}

	private void _readActivity(
			ClassLoader classLoader, Element activityElement,
			SocialActivityDefinition activityDefinition)
		throws Exception {

		activityDefinition.setLogActivity(
			GetterUtil.getBoolean(activityElement.elementText("log-activity")));

		boolean countersEnabled = GetterUtil.getBoolean(
			activityElement.elementText("counters-enabled"), true);

		activityDefinition.setCountersEnabled(countersEnabled);

		if (!countersEnabled) {
			return;
		}

		activityDefinition.setLanguageKey(
			GetterUtil.getString(activityElement.elementText("language-key")));

		String processorClassName = GetterUtil.getString(
			activityElement.elementText("processor-class"));

		if (Validator.isNotNull(processorClassName)) {
			SocialActivityProcessor activityProcessor =
				(SocialActivityProcessor)ProxyFactory.newInstance(
					classLoader, SocialActivityProcessor.class,
					processorClassName);

			activityDefinition.setActivityProcessor(activityProcessor);
		}

		_readActivityContribution(activityElement, activityDefinition);
		_readActivityParticipation(activityElement, activityDefinition);
	}

	private void _readActivity(
			ClassLoader classLoader, List<Object> objects,
			Element activityElement)
		throws Exception {

		String modelName = GetterUtil.getString(
			activityElement.elementText("model-name"));

		Map<Integer, SocialActivityDefinition> activityDefinitions =
			_activityDefinitions.get(modelName);

		if (activityDefinitions == null) {
			activityDefinitions = new HashMap<>();

			_activityDefinitions.put(modelName, activityDefinitions);
		}

		int activityType = GetterUtil.getInteger(
			activityElement.elementText("activity-type"));

		SocialActivityDefinition activityDefinition = activityDefinitions.get(
			activityType);

		if (activityDefinition == null) {
			activityDefinition = new SocialActivityDefinition();

			activityDefinition.setModelName(modelName);
			activityDefinition.setActivityType(activityType);

			_readActivity(classLoader, activityElement, activityDefinition);

			activityDefinitions.put(activityType, activityDefinition);

			objects.add(activityDefinition);
		}

		List<Element> counterElements = activityElement.elements("counter");

		for (Element counterElement : counterElements) {
			_readCounter(objects, activityDefinition, counterElement);
		}

		List<Element> achievementElements = activityElement.elements(
			"achievement");

		for (Element achievementElement : achievementElements) {
			_readAchievement(
				classLoader, objects, activityDefinition, achievementElement);
		}
	}

	private void _readActivityContribution(
		Element activityElement, SocialActivityDefinition activityDefinition) {

		Element contributionValueElement = activityElement.element(
			"contribution-value");
		Element contributionLimitElement = activityElement.element(
			"contribution-limit");

		if ((contributionValueElement == null) &&
			(contributionLimitElement == null)) {

			return;
		}

		SocialActivityCounterDefinition activityCounterDefinition =
			new SocialActivityCounterDefinition();

		activityCounterDefinition.setName(
			SocialActivityCounterConstants.NAME_CONTRIBUTION);
		activityCounterDefinition.setOwnerType(
			SocialActivityCounterConstants.TYPE_CREATOR);

		int increment = 0;

		if (contributionValueElement != null) {
			increment = GetterUtil.getInteger(
				contributionValueElement.getText());
		}

		activityCounterDefinition.setIncrement(increment);

		if (contributionLimitElement != null) {
			String limitEnabled = contributionLimitElement.attributeValue(
				"enabled");

			if (Validator.isNotNull(limitEnabled)) {
				activityCounterDefinition.setLimitEnabled(
					GetterUtil.getBoolean(limitEnabled));
			}

			String limitPeriod = contributionLimitElement.attributeValue(
				"period");

			if (Validator.isNotNull(limitPeriod)) {
				activityCounterDefinition.setLimitPeriod(limitPeriod);
			}

			activityCounterDefinition.setLimitValue(
				GetterUtil.getInteger(contributionLimitElement.getText()));
		}

		activityDefinition.addCounter(activityCounterDefinition);

		SocialActivityCounterDefinition popularityActivityCounterDefinition =
			new SocialActivityCounterDefinition();

		popularityActivityCounterDefinition.setName(
			SocialActivityCounterConstants.NAME_POPULARITY);
		popularityActivityCounterDefinition.setOwnerType(
			SocialActivityCounterConstants.TYPE_ASSET);
		popularityActivityCounterDefinition.setIncrement(
			activityCounterDefinition.getIncrement());
		popularityActivityCounterDefinition.setLimitEnabled(
			activityCounterDefinition.isLimitEnabled());
		popularityActivityCounterDefinition.setLimitPeriod(
			activityCounterDefinition.getLimitPeriod());
		popularityActivityCounterDefinition.setLimitValue(
			activityCounterDefinition.getLimitValue());

		activityDefinition.addCounter(popularityActivityCounterDefinition);
	}

	private void _readActivityParticipation(
		Element activityElement, SocialActivityDefinition activityDefinition) {

		Element participationValueElement = activityElement.element(
			"participation-value");
		Element participationLimitElement = activityElement.element(
			"participation-limit");

		if ((participationValueElement == null) &&
			(participationLimitElement == null)) {

			return;
		}

		SocialActivityCounterDefinition activityCounterDefinition =
			new SocialActivityCounterDefinition();

		activityCounterDefinition.setName(
			SocialActivityCounterConstants.NAME_PARTICIPATION);
		activityCounterDefinition.setOwnerType(
			SocialActivityCounterConstants.TYPE_ACTOR);

		int increment = 0;

		if (participationValueElement != null) {
			increment = GetterUtil.getInteger(
				participationValueElement.getText());
		}

		activityCounterDefinition.setIncrement(increment);

		if (participationLimitElement != null) {
			String limitEnabled = participationLimitElement.attributeValue(
				"enabled");

			if (Validator.isNotNull(limitEnabled)) {
				activityCounterDefinition.setLimitEnabled(
					GetterUtil.getBoolean(limitEnabled));
			}

			String limitPeriod = participationLimitElement.attributeValue(
				"period");

			if (Validator.isNotNull(limitPeriod)) {
				activityCounterDefinition.setLimitPeriod(limitPeriod);
			}

			activityCounterDefinition.setLimitValue(
				GetterUtil.getInteger(participationLimitElement.getText()));
		}

		activityDefinition.addCounter(activityCounterDefinition);
	}

	private void _readCounter(
		List<Object> objects, SocialActivityDefinition activityDefinition,
		Element counterElement) {

		SocialActivityCounterDefinition activityCounterDefinition =
			new SocialActivityCounterDefinition();

		activityCounterDefinition.setIncrement(
			GetterUtil.getInteger(counterElement.elementText("increment"), 1));
		activityCounterDefinition.setEnabled(
			GetterUtil.getBoolean(counterElement.elementText("enabled"), true));
		activityCounterDefinition.setName(
			GetterUtil.getString(counterElement.elementText("name")));

		String ownerType = GetterUtil.getString(
			counterElement.elementText("owner-type"));

		activityCounterDefinition.setOwnerType(ownerType);

		if (activityCounterDefinition.getOwnerType() == 0) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Invalid owner type ", ownerType, " for model ",
						activityDefinition.getModelName()));
			}

			return;
		}

		activityDefinition.addCounter(activityCounterDefinition);

		Tuple tuple = new Tuple(activityDefinition, activityCounterDefinition);

		objects.add(tuple);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SocialConfigurationImpl.class);

	private final Map<String, Map<Integer, SocialActivityDefinition>>
		_activityDefinitions = new HashMap<>();

}