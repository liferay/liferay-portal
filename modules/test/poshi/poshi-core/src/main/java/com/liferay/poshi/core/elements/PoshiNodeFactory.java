/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.core.elements;

import com.liferay.poshi.core.script.PoshiScriptParserException;
import com.liferay.poshi.core.script.PoshiScriptParserUtil;
import com.liferay.poshi.core.util.Dom4JUtil;
import com.liferay.poshi.core.util.FileUtil;

import java.io.IOException;

import java.lang.reflect.Modifier;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Comment;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;

import org.reflections.Reflections;

/**
 * @author Kenji Heigel
 */
public abstract class PoshiNodeFactory {

	public static PoshiNode<?, ?> newPoshiNode(Node node) {
		PoshiNode<?, ?> newPoshiNode = null;

		if (node instanceof Comment) {
			newPoshiNode = _newPoshiComment((Comment)node);
		}

		if (node instanceof Element) {
			newPoshiNode = _newPoshiElement((Element)node);
		}

		if (newPoshiNode != null) {
			return newPoshiNode;
		}

		String nodeContent;

		try {
			nodeContent = Dom4JUtil.format(node);
		}
		catch (IOException ioException) {
			nodeContent = node.toString();
		}

		throw new RuntimeException("Unknown node\n" + nodeContent);
	}

	public static PoshiNode<?, ?> newPoshiNode(
			PoshiNode<?, ?> parentPoshiNode, String poshiScript)
		throws PoshiScriptParserException {

		PoshiNode<?, ?> newPoshiNode = _newPoshiComment(poshiScript);

		if (newPoshiNode != null) {
			return newPoshiNode;
		}

		newPoshiNode = _newPoshiElement(
			(PoshiElement)parentPoshiNode, poshiScript);

		if (newPoshiNode != null) {
			return newPoshiNode;
		}

		throw new PoshiScriptParserException(
			"Invalid Poshi Script syntax", poshiScript, parentPoshiNode);
	}

	public static PoshiNode<?, ?> newPoshiNode(String content, URL url)
		throws PoshiScriptParserException {

		try {
			content = content.trim();

			if (content.startsWith("<definition")) {
				Document document = Dom4JUtil.parse(content);

				return _definitionPoshiElement.clone(
					document.getRootElement(), url);
			}

			if (PoshiScriptParserUtil.isBalancedPoshiScript(
					content, url, true)) {

				return _definitionPoshiElement.clone(content, url);
			}
		}
		catch (DocumentException documentException) {
			throw new RuntimeException(
				"Unable to parse Poshi XML file: " + url.getFile(),
				documentException.getCause());
		}
		catch (PoshiScriptParserException poshiScriptParserException) {
			throw poshiScriptParserException;
		}

		return null;
	}

	public static PoshiNode<?, ?> newPoshiNodeFromFile(URL url)
		throws PoshiScriptParserException {

		try {
			String content = FileUtil.read(url);

			return newPoshiNode(content, url);
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to read file: " + url.getFile(),
				ioException.getCause());
		}
	}

	protected static final Set<URL> validationInitialized =
		Collections.synchronizedSet(new HashSet<>());

	private static DefinitionPoshiElement _getDefinitionPoshiElement() {
		for (PoshiElement poshiElement : _poshiElements) {
			if (poshiElement instanceof DefinitionPoshiElement) {
				return (DefinitionPoshiElement)poshiElement;
			}
		}

		return new DefinitionPoshiElement();
	}

	private static PoshiComment _newPoshiComment(Comment comment) {
		for (PoshiComment poshiComment : _poshiComments) {
			PoshiComment newPoshiComment = poshiComment.clone(comment);

			if (newPoshiComment != null) {
				return newPoshiComment;
			}
		}

		return null;
	}

	private static PoshiComment _newPoshiComment(String poshiScript)
		throws PoshiScriptParserException {

		for (PoshiComment poshiComment : _poshiComments) {
			PoshiComment newPoshiComment = poshiComment.clone(poshiScript);

			if (newPoshiComment != null) {
				return newPoshiComment;
			}
		}

		return null;
	}

	private static PoshiElement _newPoshiElement(Element element) {
		for (PoshiElement poshiElement : _poshiElements) {
			PoshiElement newPoshiElement = poshiElement.clone(element);

			if (newPoshiElement != null) {
				return newPoshiElement;
			}
		}

		return null;
	}

	private static PoshiElement _newPoshiElement(
			PoshiElement parentPoshiElement, String poshiScript)
		throws PoshiScriptParserException {

		for (PoshiElement poshiElement : _poshiElements) {
			PoshiElement newPoshiElement = poshiElement.clone(
				parentPoshiElement, poshiScript);

			if (newPoshiElement != null) {
				return newPoshiElement;
			}
		}

		return null;
	}

	private static final DefinitionPoshiElement _definitionPoshiElement;
	private static final List<PoshiComment> _poshiComments = new ArrayList<>();
	private static final List<PoshiElement> _poshiElements = new ArrayList<>();

	static {
		try {
			Reflections reflections = new Reflections(
				"com.liferay.poshi.core.elements");

			List<Class<?>> poshiElementClasses = new ArrayList<>(
				reflections.getSubTypesOf(PoshiNode.class));

			Collections.sort(
				poshiElementClasses,
				new Comparator<Class<?>>() {

					@Override
					public int compare(Class<?> class1, Class<?> class2) {
						String className1 = class1.getName();
						String className2 = class2.getName();

						return className1.compareTo(className2);
					}

				});

			for (Class<?> clazz : poshiElementClasses) {
				if (Modifier.isAbstract(clazz.getModifiers()) ||
					!(PoshiComment.class.isAssignableFrom(clazz) ||
					  PoshiElement.class.isAssignableFrom(clazz))) {

					continue;
				}

				PoshiNode<?, ?> poshiNode =
					(PoshiNode<?, ?>)clazz.newInstance();

				if (poshiNode instanceof PoshiComment) {
					_poshiComments.add((PoshiComment)poshiNode);

					continue;
				}

				if (!(poshiNode instanceof PoshiElement)) {
					continue;
				}

				_poshiElements.add((PoshiElement)poshiNode);
			}

			Collections.sort(
				_poshiElements,
				new Comparator<PoshiElement>() {

					@Override
					public int compare(
						PoshiElement poshiElement1,
						PoshiElement poshiElement2) {

						Class<?> poshiElementClass1 = poshiElement1.getClass();
						Class<?> poshiElementClass2 = poshiElement2.getClass();

						String classSimpleName1 =
							poshiElementClass1.getSimpleName();
						String classSimpleName2 =
							poshiElementClass2.getSimpleName();

						if (classSimpleName1.equals("AndPoshiElement") ||
							(classSimpleName1.equals("OrPoshiElement") &&
							 !classSimpleName2.equals("AndPoshiElement"))) {

							return -1;
						}

						return classSimpleName1.compareTo(classSimpleName2);
					}

				});

			_definitionPoshiElement = _getDefinitionPoshiElement();
		}
		catch (IllegalAccessException | InstantiationException exception) {
			throw new RuntimeException(exception);
		}
	}

}