/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.rules.engine.drools.internal;

import com.liferay.portal.kernel.resource.ResourceRetriever;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.rules.engine.Fact;
import com.liferay.portal.rules.engine.Query;
import com.liferay.portal.rules.engine.QueryType;
import com.liferay.portal.rules.engine.RulesEngine;
import com.liferay.portal.rules.engine.RulesEngineException;
import com.liferay.portal.rules.engine.RulesLanguage;
import com.liferay.portal.rules.engine.RulesResourceRetriever;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.command.Command;
import org.drools.command.CommandFactory;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;

import org.mvel2.MVELRuntime;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Michael C. Han
 * @author Vihang Pathak
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"rules.engine.default.language=DRL",
		"rules.engine.language.mapping.DROOLS_BRL=BRL",
		"rules.engine.language.mapping.DROOLS_CHANGE_SET=CHANGE_SET",
		"rules.engine.language.mapping.DROOLS_DECISION_TABLE=DTABLE",
		"rules.engine.language.mapping.DROOLS_DOMAIN_SPECIFIC=DSL",
		"rules.engine.language.mapping.DROOLS_DOMAIN_SPECIFIC_RULE=DSLR",
		"rules.engine.language.mapping.DROOLS_PKG=PKG",
		"rules.engine.language.mapping.DROOLS_RULE_FLOW=DRF",
		"rules.engine.language.mapping.DROOLS_RULE_LANGUAGE=DRL",
		"rules.engine.language.mapping.DROOLS_XML_LANGUAGE=XDRL"
	},
	service = RulesEngine.class
)
public class RulesEngineImpl implements RulesEngine {

	@Override
	public void add(
			String domainName, RulesResourceRetriever rulesResourceRetriever)
		throws RulesEngineException {

		KnowledgeBase knowledgeBase = _knowledgeBaseMap.get(domainName);

		if (knowledgeBase == null) {
			knowledgeBase = _createKnowledgeBase(rulesResourceRetriever);

			_knowledgeBaseMap.put(domainName, knowledgeBase);
		}
	}

	@Override
	public boolean containsRuleDomain(String domainName) {
		return _knowledgeBaseMap.containsKey(domainName);
	}

	@Override
	public void execute(
			RulesResourceRetriever rulesResourceRetriever, List<Fact<?>> facts)
		throws RulesEngineException {

		KnowledgeBase knowledgeBase = _createKnowledgeBase(
			rulesResourceRetriever);

		execute(facts, knowledgeBase);
	}

	@Override
	public Map<String, ?> execute(
			RulesResourceRetriever rulesResourceRetriever, List<Fact<?>> facts,
			Query query)
		throws RulesEngineException {

		KnowledgeBase knowledgeBase = _createKnowledgeBase(
			rulesResourceRetriever);

		return execute(facts, knowledgeBase, query);
	}

	@Override
	public void execute(String domainName, List<Fact<?>> facts)
		throws RulesEngineException {

		KnowledgeBase knowledgeBase = _knowledgeBaseMap.get(domainName);

		if (knowledgeBase == null) {
			throw new RulesEngineException(
				"No rules found for domain " + domainName);
		}

		execute(facts, knowledgeBase);
	}

	@Override
	public Map<String, ?> execute(
			String domainName, List<Fact<?>> facts, Query query)
		throws RulesEngineException {

		KnowledgeBase knowledgeBase = _knowledgeBaseMap.get(domainName);

		if (knowledgeBase == null) {
			throw new RulesEngineException(
				"No rules found for domain " + domainName);
		}

		return execute(facts, knowledgeBase, query);
	}

	@Override
	public void remove(String domainName) {
		_knowledgeBaseMap.remove(domainName);
	}

	public void setDefaultRulesLanguage(String defaultRulesLanguage) {
		_defaultResourceType = ResourceType.getResourceType(
			defaultRulesLanguage);
	}

	public void setRulesLanguageMapping(Map<String, String> rulesLanguageMap) {
		for (Map.Entry<String, String> entry : rulesLanguageMap.entrySet()) {
			RulesLanguage rulesLanguage = RulesLanguage.valueOf(entry.getKey());

			_resourceTypeMap.put(
				rulesLanguage, ResourceType.getResourceType(entry.getValue()));
		}
	}

	@Override
	public void update(
			String domainName, RulesResourceRetriever rulesResourceRetriever)
		throws RulesEngineException {

		remove(domainName);

		add(domainName, rulesResourceRetriever);
	}

	@Activate
	protected void activate(ComponentContext componentContext) {
		Dictionary<String, Object> properties =
			componentContext.getProperties();

		setDefaultRulesLanguage(
			GetterUtil.getString(
				properties.get("rules.engine.default.language")));

		setRulesLanguageMapping(_getRulesLanguageMap(properties));
	}

	@Deactivate
	protected void deactivate() {
		_defaultResourceType = null;

		_resourceTypeMap = null;

		MVELRuntime.resetDebugger();
	}

	protected void execute(List<Fact<?>> facts, KnowledgeBase knowledgeBase) {
		execute(facts, knowledgeBase, null);
	}

	protected Map<String, ?> execute(
		List<Fact<?>> facts, KnowledgeBase knowledgeBase, Query query) {

		QueryType queryType = null;

		if (query != null) {
			queryType = query.getQueryType();
		}

		if ((query != null) && queryType.equals(QueryType.CUSTOM) &&
			Validator.isNull(query.getQueryName())) {

			throw new IllegalArgumentException(
				"Cannot execute a custom query without a query string");
		}

		StatelessKnowledgeSession statelessKnowledgeSession =
			knowledgeBase.newStatelessKnowledgeSession();

		List<Command<?>> commands = new ArrayList<>();

		List<String> identifiers = new ArrayList<>(facts.size());

		for (Fact<?> fact : facts) {
			String identifier = fact.getIdentifier();

			Command<?> command = CommandFactory.newInsert(
				fact.getFactObject(), identifier);

			commands.add(command);

			identifiers.add(identifier);
		}

		if ((query != null) && queryType.equals(QueryType.CUSTOM)) {
			Command<?> command = CommandFactory.newQuery(
				query.getIdentifier(), query.getQueryName(),
				query.getArguments());

			commands.add(command);
		}

		ExecutionResults executionResults = statelessKnowledgeSession.execute(
			CommandFactory.newBatchExecution(commands));

		return _processQueryResults(query, identifiers, executionResults);
	}

	private ResourceType _convertRulesLanguage(String rulesLanguage) {
		if (Validator.isNull(rulesLanguage)) {
			return _defaultResourceType;
		}

		ResourceType resourceType = _resourceTypeMap.get(
			RulesLanguage.valueOf(rulesLanguage));

		if (resourceType == null) {
			throw new IllegalArgumentException(
				rulesLanguage + " not supported by the Drools");
		}

		return resourceType;
	}

	private KnowledgeBase _createKnowledgeBase(RulesResourceRetriever retriever)
		throws RulesEngineException {

		try {
			KnowledgeBuilderConfiguration knowledgeBuilderConfiguration =
				KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();

			KnowledgeBuilder knowledgeBuilder =
				KnowledgeBuilderFactory.newKnowledgeBuilder(
					knowledgeBuilderConfiguration);

			ResourceType resourceType = _convertRulesLanguage(
				retriever.getRulesLanguage());

			Set<ResourceRetriever> resourceRetrievers =
				retriever.getResourceRetrievers();

			for (ResourceRetriever resourceRetriever : resourceRetrievers) {
				Resource resource = ResourceFactory.newInputStreamResource(
					resourceRetriever.getInputStream());

				knowledgeBuilder.add(resource, resourceType);
			}

			if (knowledgeBuilder.hasErrors()) {
				KnowledgeBuilderErrors knowledgeBuilderErrors =
					knowledgeBuilder.getErrors();

				throw new RulesEngineException(
					knowledgeBuilderErrors.toString());
			}

			KnowledgeBaseConfiguration knowledgeBaseConfiguration =
				KnowledgeBaseFactory.newKnowledgeBaseConfiguration();

			KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase(
				knowledgeBaseConfiguration);

			knowledgeBase.addKnowledgePackages(
				knowledgeBuilder.getKnowledgePackages());

			return knowledgeBase;
		}
		catch (Exception exception) {
			throw new RulesEngineException(
				"Unable to create knowledge base", exception);
		}
	}

	private Map<String, String> _getRulesLanguageMap(
		Dictionary<String, Object> properties) {

		Map<String, String> rulesLanguageMap = new HashMap<>();

		Enumeration<String> enumeration = properties.keys();

		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();

			if (!key.startsWith("rules.engine.language.mapping")) {
				continue;
			}

			String language = StringUtil.extractLast(
				key, "rules.engine.language.mapping.");

			rulesLanguageMap.put(
				language, GetterUtil.getString(properties.get(key)));
		}

		return rulesLanguageMap;
	}

	private Map<String, ?> _processQueryResults(
		Query query, List<String> identifiers,
		ExecutionResults executionResults) {

		if (query == null) {
			return Collections.emptyMap();
		}

		Map<String, Object> returnValues = new HashMap<>();

		QueryType queryType = query.getQueryType();

		if (queryType.equals(QueryType.STANDARD)) {
			for (String identifier : identifiers) {
				Object fact = executionResults.getValue(identifier);

				returnValues.put(identifier, fact);
			}

			return returnValues;
		}

		QueryResults queryResults = (QueryResults)executionResults.getValue(
			query.getIdentifier());

		String[] queryResultsIdentifiers = queryResults.getIdentifiers();

		for (QueryResultsRow queryResultsRow : queryResults) {
			for (String identifier : queryResultsIdentifiers) {
				Object returnValue = queryResultsRow.get(identifier);

				if (returnValue == null) {
					continue;
				}

				returnValues.put(identifier, returnValue);
			}
		}

		return returnValues;
	}

	private ResourceType _defaultResourceType;
	private final Map<String, KnowledgeBase> _knowledgeBaseMap =
		new ConcurrentHashMap<>();
	private Map<RulesLanguage, ResourceType> _resourceTypeMap =
		new ConcurrentHashMap<>();

}