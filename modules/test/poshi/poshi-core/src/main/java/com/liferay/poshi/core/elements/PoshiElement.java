/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.core.elements;

import com.liferay.poshi.core.PoshiContext;
import com.liferay.poshi.core.PoshiProperties;
import com.liferay.poshi.core.script.PoshiScriptParserException;
import com.liferay.poshi.core.script.PoshiScriptParserUtil;
import com.liferay.poshi.core.util.Dom4JUtil;
import com.liferay.poshi.core.util.NaturalOrderStringComparator;
import com.liferay.poshi.core.util.RegexUtil;
import com.liferay.poshi.core.util.StringPool;
import com.liferay.poshi.core.util.StringUtil;
import com.liferay.poshi.core.util.Validator;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Attribute;
import org.dom4j.CDATA;
import org.dom4j.Comment;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.tree.DefaultElement;

/**
 * @author Kenji Heigel
 */
public abstract class PoshiElement
	extends DefaultElement implements PoshiNode<Element, PoshiElement> {

	public PoshiElement(String name) {
		super(name);
	}

	@Override
	public void add(Attribute attribute) {
		if (attribute instanceof PoshiElementAttribute) {
			super.add(attribute);

			return;
		}

		super.add(new PoshiElementAttribute(attribute));
	}

	@Override
	public void add(CDATA cdata) {
		if (cdata instanceof PoshiCDATA) {
			super.add(cdata);

			return;
		}

		super.add(new PoshiCDATA(cdata));
	}

	public abstract PoshiElement clone(
			PoshiElement parentPoshiElement, String poshiScript)
		throws PoshiScriptParserException;

	@Override
	public PoshiElement clone(String poshiScript)
		throws PoshiScriptParserException {

		return clone(null, poshiScript);
	}

	public String getPoshiLogDescriptor() {
		return getPoshiScript();
	}

	@Override
	public String getPoshiScript() {
		return _poshiScript;
	}

	public int getPoshiScriptLineNumber(Boolean includeAnnotation) {
		int poshiScriptLineNumber = PoshiNode.super.getPoshiScriptLineNumber();

		if (!includeAnnotation) {
			poshiScriptLineNumber += StringUtil.count(
				getBlockName(getPoshiScript()), "\n");
		}

		return poshiScriptLineNumber;
	}

	public boolean isPoshiScriptComment(String poshiScript) {
		Matcher matcher = _poshiScriptCommentPattern.matcher(poshiScript);

		return matcher.find();
	}

	public boolean isValidPoshiScript() throws PoshiScriptParserException {
		for (PoshiElementAttribute poshiElementAttribute :
				toPoshiElementAttributes(attributeList())) {

			poshiElementAttribute.validatePoshiScript();
		}

		String originalPoshiScript = getPoshiScript();
		String generatedPoshiScript = toPoshiScript();

		originalPoshiScript = originalPoshiScript.replaceAll("\\s+", "");

		generatedPoshiScript = generatedPoshiScript.replaceAll("\\s+", "");

		if (((elements().size() == 0) &&
			 !originalPoshiScript.equals(generatedPoshiScript)) ||
			(originalPoshiScript.length() != generatedPoshiScript.length())) {

			return false;
		}

		return true;
	}

	@Override
	public boolean remove(Attribute attribute) {
		if (attribute instanceof PoshiElementAttribute) {
			return super.remove(attribute);
		}

		for (PoshiElementAttribute poshiElementAttribute :
				toPoshiElementAttributes(attributes())) {

			if (poshiElementAttribute == attribute) {
				return super.remove(poshiElementAttribute);
			}
		}

		return false;
	}

	@Override
	public boolean remove(CDATA cdata) {
		if (cdata instanceof PoshiCDATA) {
			return super.remove(cdata);
		}

		for (PoshiNode<?, ?> poshiNode : toPoshiNodes(content())) {
			if (poshiNode == cdata) {
				return super.remove(poshiNode);
			}
		}

		return false;
	}

	public void setFilePathURL(URL filePathURL) {
	}

	@Override
	public void setPoshiScript(String poshiScript) {
		_poshiScript = poshiScript;
	}

	public List<PoshiElement> toPoshiElements(List<?> list) {
		if (list == null) {
			return null;
		}

		List<PoshiElement> poshiElements = new ArrayList<>(list.size());

		for (Object object : list) {
			poshiElements.add((PoshiElement)object);
		}

		return poshiElements;
	}

	@Override
	public String toPoshiScript() {
		StringBuilder sb = new StringBuilder();

		for (Node node : Dom4JUtil.toNodeList(content())) {
			if (node instanceof PoshiComment) {
				PoshiComment poshiComment = (PoshiComment)node;

				sb.append(poshiComment.toPoshiScript());
			}
			else if (node instanceof PoshiElement) {
				PoshiElement poshiElement = (PoshiElement)node;

				sb.append(poshiElement.toPoshiScript());
			}
		}

		return sb.toString();
	}

	@Override
	public void validatePoshiScript() throws PoshiScriptParserException {
		if (!isValidPoshiScript() && !isValidPoshiXML()) {
			throw new PoshiScriptParserException(
				PoshiScriptParserException.TRANSLATION_LOSS_MESSAGE, this);
		}
	}

	protected PoshiElement(String name, Element element) {
		super(name);

		if (!isElementType(name, element)) {
			throw new RuntimeException(
				"Element does not match expected Poshi element name\n" +
					element.toString());
		}

		_addAttributes(element);
		_addNodes(element);
	}

	protected PoshiElement(String name, Element element, URL url) {
		this(name, element);

		setFilePathURL(url);
	}

	protected PoshiElement(
		String name, List<Attribute> attributes, List<Node> nodes) {

		super(name);

		if (attributes != null) {
			for (Attribute attribute : attributes) {
				add(attribute);
			}
		}

		if (nodes != null) {
			for (Node node : nodes) {
				add(node);
			}
		}
	}

	protected PoshiElement(
			String name, PoshiElement parentPoshiElement, String poshiScript)
		throws PoshiScriptParserException {

		this(name, parentPoshiElement, poshiScript, null);
	}

	protected PoshiElement(
			String name, PoshiElement parentPoshiElement, String poshiScript,
			URL url)
		throws PoshiScriptParserException {

		super(name);

		setFilePathURL(url);

		setParent(parentPoshiElement);

		setPoshiScript(poshiScript);

		try {
			parsePoshiScript(poshiScript.trim());

			if (poshiProperties.testPoshiScriptValidation &&
				!PoshiNodeFactory.validationInitialized.contains(
					getFilePathURL())) {

				PoshiNodeFactory.validationInitialized.add(getFilePathURL());

				validatePoshiScript();
			}
		}
		catch (PoshiScriptParserException poshiScriptParserException) {
		}

		detach();
	}

	protected String createPoshiScriptBlock(List<PoshiNode<?, ?>> poshiNodes) {
		StringBuilder sb = new StringBuilder();

		String pad = getPad();

		sb.append("\n");
		sb.append(pad);

		String blockName = getBlockName();

		sb.append(blockName);

		PoshiProperties poshiProperties = PoshiProperties.getPoshiProperties();

		if (poshiProperties.generateCommandSignature &&
			(blockName.startsWith("function") ||
			 blockName.startsWith("macro"))) {

			sb.append("(");

			if (Validator.isNotNull(attributeValue("arguments"))) {
				sb.append(attributeValue("arguments"));
			}

			sb.append(")");
		}

		sb.append(" {");

		PoshiNode<?, ?> previousPoshiNode = null;

		Collections.sort(poshiNodes, new CommandComparator());
		Collections.sort(poshiNodes, new PropertyComparator());

		for (Iterator<PoshiNode<?, ?>> iterator = poshiNodes.iterator();
			 iterator.hasNext();) {

			PoshiNode<?, ?> poshiNode = iterator.next();

			if (poshiNode instanceof DescriptionPoshiElement) {
				continue;
			}

			String poshiScriptSnippet = poshiNode.toPoshiScript();

			if (((previousPoshiNode == null) ||
				 ((previousPoshiNode instanceof PropertyPoshiElement) &&
				  (poshiNode instanceof PropertyPoshiElement)) ||
				 ((previousPoshiNode instanceof InlinePoshiComment) &&
				  (poshiNode instanceof InlinePoshiComment))) &&
				poshiScriptSnippet.startsWith("\n\n")) {

				poshiScriptSnippet = poshiScriptSnippet.replaceFirst(
					"\n\n", "\n");
			}

			String padPoshiScriptSnippet = padPoshiScriptSnippet(
				poshiScriptSnippet);

			padPoshiScriptSnippet = _removeEmptyLineBetweenVariables(
				padPoshiScriptSnippet, poshiNode, previousPoshiNode);

			if (padPoshiScriptSnippet.startsWith("\n\n") &&
				StringUtil.endsWith(
					sb.toString(), StringPool.OPEN_CURLY_BRACE)) {

				padPoshiScriptSnippet = padPoshiScriptSnippet.replaceFirst(
					"\n\n", "\n");
			}

			if ((previousPoshiNode == null) &&
				(poshiNode instanceof InlinePoshiComment)) {

				padPoshiScriptSnippet = "\n" + padPoshiScriptSnippet;
			}

			if (StringUtil.endsWith(sb.toString(), "\ndefinition {") &&
				!padPoshiScriptSnippet.startsWith("\n\n")) {

				sb.append("\n");
			}

			sb.append(padPoshiScriptSnippet);

			previousPoshiNode = poshiNode;
		}

		if (previousPoshiNode instanceof InlinePoshiComment) {
			sb.append("\n");
		}

		sb.append("\n");

		if (blockName.equals("definition")) {
			sb.append("\n");
		}

		sb.append(pad);
		sb.append("}");

		return sb.toString();
	}

	protected String createPoshiScriptSnippet(String content) {
		StringBuilder sb = new StringBuilder();

		String pad = getPad();

		sb.append("\n");
		sb.append(pad);
		sb.append(getBlockName());
		sb.append(" {");

		if (content.startsWith("\n\n")) {
			content = content.replaceFirst("\n\n", "\n");
		}

		content = content.replaceAll("\n", "\n" + pad);

		sb.append(content.replaceAll("\n\t\n", "\n\n"));

		sb.append("\n");
		sb.append(pad);
		sb.append("}");

		return sb.toString();
	}

	protected String doubleQuoteContent(String content) {
		return "\"" + content + "\"";
	}

	protected String getBlockContent(String poshiScriptBlock) {
		String blockName = getBlockName(poshiScriptBlock);

		int index = blockName.length();

		String blockContent = poshiScriptBlock.substring(index);

		return getBracedContent(blockContent);
	}

	protected abstract String getBlockName();

	protected String getBlockName(String poshiScriptBlock) {
		StringBuilder sb = new StringBuilder();

		for (char c : poshiScriptBlock.toCharArray()) {
			if ((c == '{') &&
				PoshiScriptParserUtil.isBalancedPoshiScript(sb.toString())) {

				String blockName = sb.toString();

				return blockName.trim();
			}

			sb.append(c);
		}

		throw new RuntimeException(
			"Unable to get Poshi script block name from:\n" + poshiScriptBlock);
	}

	protected String getBracedContent(String poshiScript) {
		return RegexUtil.getGroup(poshiScript, ".*?\\{(.*)\\}", 1);
	}

	protected String getBracketedContent(String poshiScript) {
		return RegexUtil.getGroup(poshiScript, ".*?\\[(.*)\\]", 1);
	}

	protected String getClassCommandName(String classCommand) {
		classCommand = classCommand.trim();

		if (classCommand.contains("(")) {
			int index = classCommand.indexOf("(");

			classCommand = classCommand.substring(0, index);
		}

		return classCommand;
	}

	protected String getClassName(String classCommand) {
		String classCommandName = getClassCommandName(classCommand);

		int index = classCommandName.length();

		if (classCommandName.contains(".")) {
			index = classCommandName.lastIndexOf(".");
		}
		else if (classCommandName.contains("#")) {
			index = classCommandName.lastIndexOf("#");
		}

		return classCommandName.substring(0, index);
	}

	protected String getCommandName(String classCommand) {
		String classCommandName = getClassCommandName(classCommand);

		if (classCommandName.contains(".")) {
			int index = classCommandName.lastIndexOf(".");

			return classCommandName.substring(index + 1);
		}

		return classCommandName;
	}

	protected Pattern getConditionPattern() {
		return null;
	}

	protected String getDoubleQuotedContent(String poshiScript) {
		return RegexUtil.getGroup(poshiScript, ".*?\"(.*)\"", 1);
	}

	protected String getNameFromAssignment(String assignment) {
		String name = assignment.split("=")[0];

		name = name.trim();
		name = StringUtil.replace(name, "@", "");
		name = StringUtil.replace(name, "property ", "");

		return StringUtil.replace(name, "var ", "");
	}

	protected List<String> getNestedConditions(
		String poshiScript, String operator) {

		List<String> nestedConditions = new ArrayList<>();

		Set<Integer> tokenIndices = new TreeSet<>();

		int index = poshiScript.indexOf(operator);

		while (index >= 0) {
			tokenIndices.add(index);

			index = poshiScript.indexOf(operator, index + 1);
		}

		StringBuilder sb = new StringBuilder();

		char[] chars = poshiScript.toCharArray();

		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];

			if (tokenIndices.contains(i) &&
				PoshiScriptParserUtil.isBalancedPoshiScript(sb.toString())) {

				nestedConditions.add(sb.toString());

				sb.setLength(0);

				i++;

				continue;
			}

			if (i == (chars.length - 1)) {
				sb.append(c);

				if (PoshiScriptParserUtil.isBalancedPoshiScript(
						sb.toString()) &&
					!nestedConditions.isEmpty()) {

					String nestedCondition = sb.toString();

					if (!nestedCondition.contains("maxIterations")) {
						nestedConditions.add(nestedCondition);
					}
				}
			}

			sb.append(c);
		}

		return nestedConditions;
	}

	protected String getPad() {
		return "\t";
	}

	protected String getParentheticalContent(String poshiScript) {
		return RegexUtil.getGroup(poshiScript, ".*?\\((.*)\\)", 1);
	}

	protected List<PoshiNode<?, ?>> getPoshiNodes() {
		return toPoshiNodes(content());
	}

	protected String getPoshiScriptEscapedContent(String poshiScript) {
		poshiScript = poshiScript.trim();

		return poshiScript.substring(3, poshiScript.length() - 3);
	}

	protected String getPoshiScriptKeyword() {
		PoshiElement poshiParentElement = (PoshiElement)getParent();

		return poshiParentElement.getPoshiScriptKeyword();
	}

	protected List<String> getPoshiScriptSnippets(
		String poshiScriptBlockContent) {

		return getPoshiScriptSnippets(poshiScriptBlockContent, true);
	}

	protected List<String> getPoshiScriptSnippets(
		String poshiScriptBlockContent, boolean splitElseBlocks) {

		List<String> poshiScriptSnippets = new ArrayList<>();

		StringBuilder sb = new StringBuilder();

		int index = 0;
		boolean skipBalanceCheck = false;
		Stack<Integer> storedIndices = new Stack<>();

		for (char c : poshiScriptBlockContent.toCharArray()) {
			sb.append(c);

			String poshiScriptSnippet = sb.toString();

			String trimmedPoshiScriptSnippet = poshiScriptSnippet.trim();

			if (trimmedPoshiScriptSnippet.startsWith("//")) {
				if (c == '\n') {
					poshiScriptSnippet = poshiScriptSnippet.substring(
						0, poshiScriptSnippet.length() - 1);

					poshiScriptSnippets.add(poshiScriptSnippet);

					sb.setLength(0);

					sb.append(c);
				}

				continue;
			}

			if (trimmedPoshiScriptSnippet.startsWith("/*")) {
				if (trimmedPoshiScriptSnippet.endsWith("*/")) {
					poshiScriptSnippets.add(poshiScriptSnippet);

					sb.setLength(0);
				}

				continue;
			}

			index++;

			if (c == '\'') {
				if (storedIndices.isEmpty()) {
					storedIndices.push(index);
				}
				else if (storedIndices.peek() == (index - 1)) {
					storedIndices.push(index);
				}

				if ((storedIndices.size() == 3) ||
					(storedIndices.size() == 6)) {

					skipBalanceCheck = !skipBalanceCheck;
				}

				if (storedIndices.size() > 6) {
					throw new RuntimeException(
						"Invalid multiline string: \n" + poshiScriptSnippet);
				}
			}
			else {
				storedIndices.clear();
			}

			if (trimmedPoshiScriptSnippet.startsWith("return") && (c != ';')) {
				continue;
			}

			if (skipBalanceCheck || ((c != '}') && (c != ';'))) {
				continue;
			}

			if ((_isVarPoshiScriptSnippet(trimmedPoshiScriptSnippet) &&
				 (c != ';')) ||
				!PoshiScriptParserUtil.isBalancedPoshiScript(
					poshiScriptSnippet)) {

				continue;
			}

			if (splitElseBlocks &&
				(isValidPoshiScriptBlock(
					ElseIfPoshiElement.blockNamePattern, poshiScriptSnippet) ||
				 isValidPoshiScriptBlock(
					 ElsePoshiElement.blockNamePattern, poshiScriptSnippet))) {

				int lastIndex = poshiScriptSnippets.size() - 1;

				String lastPoshiScriptSnippet = poshiScriptSnippets.get(
					lastIndex);

				poshiScriptSnippets.set(
					lastIndex, lastPoshiScriptSnippet + poshiScriptSnippet);

				sb.setLength(0);

				continue;
			}

			poshiScriptSnippets.add(poshiScriptSnippet);

			sb.setLength(0);
		}

		String poshiScriptSnippet = sb.toString();

		poshiScriptSnippet = poshiScriptSnippet.trim();

		if (!poshiScriptSnippet.isEmpty()) {
			poshiScriptSnippets.add(poshiScriptSnippet);
		}

		return poshiScriptSnippets;
	}

	protected String getSingleQuotedContent(String poshiScript) {
		return RegexUtil.getGroup(poshiScript, ".*?\'(.*)\'", 1);
	}

	protected Pattern getStatementPattern() {
		return null;
	}

	protected String getValueFromAssignment(String assignment) {
		assignment = assignment.trim();

		int start = assignment.indexOf("=");

		int end = assignment.length();

		if (assignment.endsWith(";")) {
			end = end - 1;
		}

		String value = assignment.substring(start + 1, end);

		return value.trim();
	}

	protected final boolean isConditionElementType(
		PoshiElement parentPoshiElement, String poshiScript) {

		if (!isConditionValidInParent(parentPoshiElement)) {
			return false;
		}

		poshiScript = poshiScript.trim();

		Pattern conditionPattern = getConditionPattern();

		if (conditionPattern == null) {
			throw new RuntimeException(
				"Condition pattern has not been defined");
		}

		Matcher matcher = conditionPattern.matcher(poshiScript);

		return matcher.find();
	}

	protected boolean isConditionValidInParent(
		PoshiElement parentPoshiElement) {

		if (parentPoshiElement instanceof AndPoshiElement ||
			parentPoshiElement instanceof ElseIfPoshiElement ||
			parentPoshiElement instanceof IfPoshiElement ||
			parentPoshiElement instanceof NotPoshiElement ||
			parentPoshiElement instanceof OrPoshiElement) {

			return true;
		}

		return false;
	}

	protected boolean isElementType(String name, Element element) {
		return name.equals(element.getName());
	}

	protected boolean isNestedCondition(String poshiScript) {
		Matcher matcher = _nestedConditionPattern.matcher(poshiScript);

		if (matcher.find()) {
			List<String> nestedConditions = getNestedConditions(
				poshiScript, matcher.group(0));

			if (nestedConditions.size() > 1) {
				return true;
			}
		}

		return false;
	}

	protected boolean isQuotedContent(String content) {
		if (content.matches(NONQUOTED_REGEX)) {
			if (content.contains("{")) {
				return !isSingleVariable(content);
			}

			return false;
		}

		return true;
	}

	protected boolean isSingleVariable(String content) {
		boolean singleVariable = false;

		Stack<Character> stack = new Stack<>();

		for (int i = 0; i < content.length(); i++) {
			char c = content.charAt(i);

			if (i > 0) {
				char previousChar = content.charAt(i - 1);

				if ((previousChar == '$') && (c == '{')) {
					stack.push(c);
				}
			}

			if (!stack.isEmpty() && (c == '}')) {
				stack.pop();

				if (i == (content.length() - 1)) {
					singleVariable = true;
				}

				break;
			}
		}

		return singleVariable;
	}

	protected boolean isValidFunctionFileName(String poshiScriptInvocation) {
		poshiScriptInvocation = poshiScriptInvocation.trim();

		Set<String> functionFileNames = PoshiContext.getFunctionFileNames();

		if (functionFileNames.contains(getClassName(poshiScriptInvocation)) ||
			functionFileNames.contains(
				getClassCommandName(poshiScriptInvocation))) {

			return true;
		}

		return false;
	}

	protected boolean isValidMacroFileName(String poshiScriptInvocation) {
		poshiScriptInvocation = poshiScriptInvocation.trim();

		if (poshiScriptInvocation.startsWith("var ")) {
			poshiScriptInvocation = getValueFromAssignment(
				poshiScriptInvocation);
		}

		Set<String> macroFileNames = PoshiContext.getMacroFileNames();

		if (macroFileNames.contains(getClassName(poshiScriptInvocation)) ||
			macroFileNames.contains(
				getClassCommandName(poshiScriptInvocation))) {

			return true;
		}

		return false;
	}

	protected boolean isValidPoshiScriptBlock(
		Pattern poshiScriptBlockNamePattern, String poshiScript) {

		poshiScript = poshiScript.trim();

		Matcher poshiScriptBlockMatcher = poshiScriptBlockPattern.matcher(
			poshiScript);

		if (poshiScriptBlockMatcher.find()) {
			Matcher poshiScriptBlockNameMatcher =
				poshiScriptBlockNamePattern.matcher(getBlockName(poshiScript));

			if (poshiScriptBlockNameMatcher.find()) {
				return true;
			}
		}

		return false;
	}

	protected boolean isValidPoshiScriptStatement(
		Pattern poshiScriptStatementPattern, String poshiScript) {

		poshiScript = poshiScript.trim();

		Matcher poshiScriptStatementMatcher =
			poshiScriptStatementPattern.matcher(poshiScript);

		return poshiScriptStatementMatcher.find();
	}

	protected boolean isVarAssignedToMacroInvocation(String poshiScript) {
		poshiScript = poshiScript.trim();

		if (!poshiScript.startsWith("var")) {
			return false;
		}

		String value = getValueFromAssignment(poshiScript);

		if (isValidPoshiScriptStatement(
				varInvocationAssignmentStatementPattern, poshiScript) &&
			isValidMacroFileName(value)) {

			return true;
		}

		return false;
	}

	protected String padPoshiScriptSnippet(String poshiScriptSnippet) {
		if (!poshiScriptSnippet.contains("'''") &&
			!poshiScriptSnippet.contains("/*") &&
			!poshiScriptSnippet.contains("*/")) {

			poshiScriptSnippet = StringUtil.replace(
				poshiScriptSnippet, "\n", "\n" + getPad());

			poshiScriptSnippet = StringUtil.replace(
				poshiScriptSnippet, "\n\t\n", "\n\n");

			return StringUtil.replace(poshiScriptSnippet, "\n\n\n", "\n\n");
		}

		Stack<String> stack = new Stack<>();
		StringBuilder sb = new StringBuilder();

		if (poshiScriptSnippet.startsWith("\n\n")) {
			poshiScriptSnippet = poshiScriptSnippet.replaceFirst("\n\n", "\n");
		}

		for (String line : poshiScriptSnippet.split("\n")) {
			String trimmedLine = line.trim();

			sb.append("\n");

			String stackPeek = "";

			if (stack.isEmpty()) {
				if (!trimmedLine.isEmpty()) {
					line = getPad() + line;
				}
			}
			else {
				stackPeek = stack.peek();
			}

			sb.append(line);

			if (trimmedLine.startsWith("/*") && !stack.contains("/*")) {
				stack.push("/*");
			}

			if ((StringUtil.count(trimmedLine, "'''") % 2) == 1) {
				if (stackPeek.equals("'''")) {
					stack.pop();
				}
				else {
					stack.push("'''");
				}
			}

			if (!trimmedLine.endsWith("*/") || !stackPeek.equals("/*")) {
				continue;
			}

			stack.pop();
		}

		return sb.toString();
	}

	protected String singleQuoteContent(String content) {
		return "'" + content + "'";
	}

	protected List<PoshiElementAttribute> toPoshiElementAttributes(
		List<?> list) {

		if (list == null) {
			return null;
		}

		List<PoshiElementAttribute> poshiElementAttributes = new ArrayList<>(
			list.size());

		for (Object object : list) {
			poshiElementAttributes.add((PoshiElementAttribute)object);
		}

		return poshiElementAttributes;
	}

	protected List<PoshiNode<?, ?>> toPoshiNodes(List<?> list) {
		if (list == null) {
			return null;
		}

		List<PoshiNode<?, ?>> poshiNodes = new ArrayList<>(list.size());

		for (Object object : list) {
			poshiNodes.add((PoshiNode)object);
		}

		return poshiNodes;
	}

	protected void validateSemicolon(String poshiScript)
		throws PoshiScriptParserException {

		int index = 0;

		Pattern statementPattern = getStatementPattern();

		poshiScript = poshiScript.trim();

		Matcher matcher = statementPattern.matcher(poshiScript);

		if (matcher.find()) {
			if (matcher.end() == poshiScript.length()) {
				index = matcher.end();
			}
			else {
				StringBuilder sb = new StringBuilder();

				sb.append(matcher.group());

				for (int i = matcher.end(); i < poshiScript.length(); i++) {
					sb.append(poshiScript.charAt(i));

					String balanced = sb.toString();

					matcher = statementPattern.matcher(balanced);

					if (PoshiScriptParserUtil.isBalancedPoshiScript(balanced) &&
						matcher.find()) {

						index = i + 1;

						break;
					}
				}
			}
		}

		String errorMessage = "Missing semicolon";

		for (int i = index; i < poshiScript.length(); i++) {
			char c = poshiScript.charAt(i);

			if (Character.isWhitespace(c)) {
				continue;
			}

			if (c != ';') {
				throw new PoshiScriptParserException(
					errorMessage, poshiScript, (PoshiElement)getParent());
			}
		}

		if (!poshiScript.endsWith(";")) {
			throw new PoshiScriptParserException(
				errorMessage, poshiScript, (PoshiElement)getParent());
		}
	}

	protected static final String ASSIGNMENT_REGEX = "[\\s]*=[\\s]*";

	protected static final String BLOCK_NAME_ANNOTATION_REGEX = "(@.*=.*|)";

	protected static final String BLOCK_NAME_PARAMETER_REGEX =
		"[\\s]*\\(.*?\\)$";

	protected static final String INVOCATION_REGEX;

	protected static final String INVOCATION_START_REGEX = "[\\s]*[\\w\\.]*";

	protected static final String NONQUOTED_REGEX = "(\\$\\{.*\\}|\\d+)";

	protected static final String PARAMETER_REGEX = "\\((.*?|.*)\\)";

	protected static final String QUOTED_REGEX = "\".*\"";

	protected static final String STATEMENT_END_REGEX = ";$";

	protected static final String VAR_NAME_REGEX =
		"((static[\\s]*|)var|)([\\s]*[A-Z][\\w]*|)[\\s]*[\\w]*";

	protected static final String VAR_STATEMENT_END_REGEX = "(;|)$";

	protected static final Pattern poshiScriptAnnotationPattern =
		Pattern.compile(
			"@(?<name>[\\w-]*)[\\s]*?(\n|=[\\s](?<value>.*?)(?=[ \t]*\\n))",
			Pattern.DOTALL);
	protected static final Pattern poshiScriptBlockPattern = Pattern.compile(
		"^[^{]*\\{[\\s\\S]*\\}$");
	protected static final Pattern varInvocationAssignmentStatementPattern;

	static {
		INVOCATION_REGEX = INVOCATION_START_REGEX + PARAMETER_REGEX;

		varInvocationAssignmentStatementPattern = Pattern.compile(
			"^" + VAR_NAME_REGEX + ASSIGNMENT_REGEX + INVOCATION_REGEX +
				VAR_STATEMENT_END_REGEX,
			Pattern.DOTALL | Pattern.MULTILINE);
	}

	private void _addAttributes(Element element) {
		for (Attribute attribute :
				Dom4JUtil.toAttributeList(element.attributes())) {

			add(new PoshiElementAttribute((Attribute)attribute.clone()));
		}
	}

	private void _addNodes(Element element) {
		for (Node node : Dom4JUtil.toNodeList(element.content())) {
			if (node instanceof Comment || node instanceof Element) {
				add(PoshiNodeFactory.newPoshiNode(node));
			}

			if (!(node instanceof CDATA)) {
				continue;
			}

			add(new PoshiCDATA((CDATA)node.clone()));
		}
	}

	private boolean _isVarPoshiScriptSnippet(String poshiScriptSnippet) {
		poshiScriptSnippet = StringUtil.trimLeading(poshiScriptSnippet);

		if (poshiScriptSnippet.startsWith("static") ||
			poshiScriptSnippet.startsWith("var")) {

			Matcher matcher = _varNamePattern.matcher(poshiScriptSnippet);

			return matcher.find();
		}

		return false;
	}

	private String _removeEmptyLineBetweenVariables(
		String poshiScriptSnippet, PoshiNode<?, ?> poshiNode,
		PoshiNode<?, ?> previousPoshiNode) {

		if ((previousPoshiNode == null) ||
			!_isVarPoshiScriptSnippet(previousPoshiNode.toPoshiScript()) ||
			!_isVarPoshiScriptSnippet(poshiNode.toPoshiScript())) {

			return poshiScriptSnippet;
		}

		String previousVariableName = StringPool.BLANK;
		PoshiElement previousPoshiElement = (PoshiElement)previousPoshiNode;

		if (previousPoshiNode instanceof VarPoshiElement) {
			previousVariableName = previousPoshiElement.attributeValue("name");
		}
		else if (previousPoshiNode instanceof ExecutePoshiElement) {
			Element element = previousPoshiElement.element("return");

			if (element != null) {
				previousVariableName = element.attributeValue("name");
			}
		}

		if (Validator.isNotNull(previousVariableName) &&
			!poshiScriptSnippet.contains("${" + previousVariableName + "}")) {

			return poshiScriptSnippet.replaceFirst("\n\n", "\n");
		}

		return poshiScriptSnippet;
	}

	private static final Pattern _nestedConditionPattern = Pattern.compile(
		"(\\|{2}|\\&{2})");
	private static final Pattern _poshiScriptCommentPattern = Pattern.compile(
		"^[\\s]*(\\/\\/.*?(\\n|$)|\\/\\*.*?\\*\\/)", Pattern.DOTALL);
	private static final Pattern _varNamePattern = Pattern.compile(
		VAR_NAME_REGEX);

	private String _poshiScript;

	private class CommandComparator implements Comparator<PoshiNode> {

		@Override
		public int compare(PoshiNode poshiNode1, PoshiNode poshiNode2) {
			if ((poshiNode1 instanceof CommandPoshiElement) &&
				(poshiNode2 instanceof CommandPoshiElement)) {

				CommandPoshiElement commandPoshiElement1 =
					(CommandPoshiElement)poshiNode1;
				CommandPoshiElement commandPoshiElement2 =
					(CommandPoshiElement)poshiNode2;

				String poshiScriptKeyword1 =
					commandPoshiElement1.getPoshiScriptKeyword();
				String poshiScriptKeyword2 =
					commandPoshiElement2.getPoshiScriptKeyword();

				String blockName1 = commandPoshiElement1.getBlockName();
				String blockName2 = commandPoshiElement2.getBlockName();
				NaturalOrderStringComparator naturalOrderStringComparator =
					new NaturalOrderStringComparator();

				if ((blockName1.startsWith(poshiScriptKeyword1) &&
					 blockName2.startsWith(poshiScriptKeyword2)) ||
					(!blockName1.startsWith(poshiScriptKeyword1) &&
					 !blockName2.startsWith(poshiScriptKeyword2))) {

					return naturalOrderStringComparator.compare(
						blockName1, blockName2);
				}

				if (blockName1.startsWith(poshiScriptKeyword1) &&
					!blockName2.startsWith(poshiScriptKeyword2)) {

					return 1;
				}

				if (blockName2.startsWith(poshiScriptKeyword2) &&
					!blockName1.startsWith(poshiScriptKeyword1)) {

					return -1;
				}

				return naturalOrderStringComparator.compare(
					blockName1, blockName2);
			}

			return 0;
		}

	}

	private class PropertyComparator implements Comparator<PoshiNode> {

		@Override
		public int compare(PoshiNode poshiNode1, PoshiNode poshiNode2) {
			if ((poshiNode1 instanceof PropertyPoshiElement) &&
				(poshiNode2 instanceof PropertyPoshiElement)) {

				PropertyPoshiElement propertyPoshiElement1 =
					(PropertyPoshiElement)poshiNode1;
				PropertyPoshiElement propertyPoshiElement2 =
					(PropertyPoshiElement)poshiNode2;

				String propertyName1 = propertyPoshiElement1.attributeValue(
					"name");
				String propertyName2 = propertyPoshiElement2.attributeValue(
					"name");

				return propertyName1.compareTo(propertyName2);
			}

			return 0;
		}

	}

}